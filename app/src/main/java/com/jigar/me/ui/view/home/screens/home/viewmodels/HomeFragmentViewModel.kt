package com.jigar.me.ui.view.home.screens.home.viewmodels

import kotlinx.coroutines.flow.first
import com.jigar.me.utils.AppAnalytics
import com.jigar.me.utils.FreemiumWindow
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.BuildConfig
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.core.StatefulViewModel
import com.jigar.me.ui.jetpack.core.domain.ConsumableCommand
import com.jigar.me.ui.jetpack.core.repository.abacus_data.AbacusDataRepository
import com.jigar.me.ui.view.home.screens.math_game_zone.common.AbDebugFlags
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.AppReviewManager
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.HomeOfferManager
import com.jigar.me.utils.StoreOneTimeOffers
import com.revenuecat.purchases.models.GoogleStoreProduct
import com.jigar.me.R
import com.jigar.me.utils.RevenueCatHelper
import com.jigar.me.utils.StreakManager
import com.jigar.me.utils.WeeklySummaryManager
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.awaitOfferings
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    private val prefs: AppPreferencesHelper,
    private val abacusDataRepository: AbacusDataRepository,
    @ApplicationContext private val context: Context,
) : StatefulViewModel<HomeUiState>() {

    override val TAG = "HomeFragmentViewModel"

    override fun getInitialState() = HomeUiState()

    init {
        viewModelScope.launch { loadHomeMenu() }
        viewModelScope.launch { checkStreak() }
        handleNotificationPermission()
        initHomeOffer()
        initStoreOffer()
        observePremium()
        checkBillingIssue()
    }

    // Time-limited Home offer (Remote Config `home_offer`). `enabled` + `product` is
    // the only thing that decides whether this shows -- no dependency on the
    // permanent discount_per(_lifetime) fields or the display_plan whitelist.
    private fun initHomeOffer() {
        val cfg = HomeOfferManager.config(prefs)
        val targetsLifetime = cfg.targetsLifetime
        // (a campaign with "trigger": "dismiss" starts when a parent closes a paywall instead)
        if (!cfg.startsOnDismiss) HomeOfferManager.startIfNeeded(prefs, forceRestart = BuildConfig.DEBUG)
        if (!HomeOfferManager.isTimedOfferActive(prefs)) return

        // (the card is hidden for premium users by observePremium() below)
        viewModelScope.launch {
            val packages = runCatching { Purchases.sharedInstance.awaitOfferings().current?.availablePackages }
                .getOrNull() ?: emptyList()
            val baseId = if (targetsLifetime) AppConstants.Products.lifetime else AppConstants.Products.year
            val offerId = if (targetsLifetime) AppConstants.Products.lifetimeOffer else AppConstants.Products.yearOffer
            val base  = packages.firstOrNull { AppConstants.Products.matches(it.product.id, baseId) }
            val offer = packages.firstOrNull { AppConstants.Products.matches(it.product.id, offerId) }
            // The .offer package must actually exist in the store offering -- can't
            // show a discount for a product that isn't real. This card only lives on Home: the paywall and the
            // subscription page are not changed by it (they show the regular products and the store's own offers).
            if (offer == null && !AbDebugFlags.homeOfferPreviewEnabled) return@launch
            // Re-validate the window is still running now that offerings have loaded --
            // it may have expired during the await -- so an already-expired offer is
            // never published (HomeOfferCard would self-correct within a frame anyway,
            // but there's no reason to show it even briefly).
            if (!HomeOfferManager.isTimedOfferActive(prefs)) return@launch
            val startedAt = HomeOfferManager.startedAt(prefs) ?: return@launch
            val discountPercent = if (targetsLifetime)
                HomeOfferManager.effectiveLifetimeDiscountPer(prefs, base?.product?.price?.amountMicros, offer?.product?.price?.amountMicros)
            else
                HomeOfferManager.effectiveYearDiscountPer(prefs, base?.product?.price?.amountMicros, offer?.product?.price?.amountMicros)
            val offerUi = HomeOfferUi(
                title = cfg.name?.takeIf { it.isNotBlank() },
                startedAtMillis = startedAt,
                durationMin = cfg.duration_min ?: 0,
                discountPercent = discountPercent,
                targetsLifetime = targetsLifetime,
            )
            updateState_ { copy(homeOffer = offerUi) }
        }
    }

    fun onHomeOfferExpired() = updateState_ { copy(homeOffer = null) }

    // A Google Play discount offer on the lifetime plan (set in the Play Console) also shows on Home, as the same banner, with its
    // "30% OFF" or "₹500 OFF" (whichever the console offer is) and a countdown only when it ends within a day. Never together with the
    // timed Home offer (`home_offer`): while that one runs, it has the banner.
    private fun initStoreOffer() {
        if (HomeOfferManager.isTimedOfferActive(prefs)) return
        if (!StoreOneTimeOffers.enabled(prefs.getCustomParam(AppConstants.RemoteConfig.storeOneTimeOffers, ""))) return
        viewModelScope.launch {
            val offer = if (AbDebugFlags.homeStoreOfferPreviewEnabled) AbDebugFlags.sampleStoreOffer() else {
                val packages = runCatching { Purchases.sharedInstance.awaitOfferings().current?.availablePackages }.getOrNull() ?: return@launch
                val lifetime = packages.firstOrNull { it.product.id == AppConstants.Products.lifetime } ?: return@launch
                StoreOneTimeOffers.read((lifetime.product as? GoogleStoreProduct)?.productDetails) ?: return@launch
            }
            val end = offer.countdownEnd()
            updateState_ {
                copy(
                    homeOffer = HomeOfferUi(
                        title = context.getString(R.string.home_offer_store_title), startedAtMillis = 0L, durationMin = 0,
                        discountPercent = offer.percentOff, targetsLifetime = true,
                        storeBadge = offer.badgeText(context), endMillis = end, hasTimer = end != null,
                    )
                )
            }
        }
    }

    // Hide "Upgrade" and the Home offer card for premium users, reacting to RC updates (login, restore, purchase).
    private fun observePremium() {
        viewModelScope.launch {
            RevenueCatHelper.customerInfoFlow.collect {
                updateState_ { copy(isPremium = CommonUtils.checkPurchaseForExerciseExamCCM(prefs)) }
            }
        }
    }

    private fun handleNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val alreadyAsked = prefs.getCustomParamBoolean(AppConstants.Notifications.permissionAsked, false)
        if (!alreadyAsked) {
            // First ever open — show system dialog, mark asked
            prefs.setCustomParamBoolean(AppConstants.Notifications.permissionAsked, true)
            updateState_ { copy(checkNotificationPermission = ConsumableCommand.unit()) }
        } else {
            // Already asked — if still denied, show reminder sheet once per day
            maybeShowNotificationSheet()
        }
    }

    private fun maybeShowNotificationSheet() {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) return
        val today      = StreakManager.today()
        val lastShown  = prefs.getCustomParam(AppConstants.Notifications.sheetLastShownDate, "")
        if (lastShown == today) return
        prefs.setCustomParam(AppConstants.Notifications.sheetLastShownDate, today)
        updateState_ { copy(isShowNotificationSettingPopup = true) }
    }

    fun weeklyStats(): WeeklySummaryManager.WeeklyStats = WeeklySummaryManager.readStats(prefs)

    private suspend fun checkStreak() = withContext(Dispatchers.IO) {
        val result = StreakManager.checkAndUpdate(prefs)
        updateState_ {
            copy(
                currentStreak          = result.data.currentStreak,
                longestStreak          = result.data.longestStreak,
                streakShields          = result.data.shields,
                streakMilestoneAwarded = result.milestoneAwarded
            )
        }
        StreakManager.scheduleNotification(context)
        WeeklySummaryManager.scheduleNotification(context)
        // Freemium time limit: refresh the cached state, keep the day-5 / day-7 notifications in step, tag analytics
        val subscribed = CommonUtils.checkPurchaseForExerciseExamCCM(prefs)
        FreemiumWindow.refresh(prefs)
        FreemiumWindow.syncNotifications(context, prefs, subscribed)
        FreemiumWindow.noteStateForAnalytics(prefs)
        FreemiumWindow.syncUserSegments(prefs, subscribed)

        // No streak dialog queued up to show this session — check the review
        // gate immediately. If a milestone dialog IS queued, defer to
        // dismissStreakMilestone() so the two never show at once.
        if (result.milestoneAwarded == null) {
            maybeShowReviewGate()
            maybeShowParentReminder()
        }
    }

    // Day 3 / day 7 of a new install's free week: the grown-up question, then the plans. Never stacks on a review sheet
    // (it simply waits for the next launch), and each reminder is stamped as shown the moment it is taken.
    private fun maybeShowParentReminder() {
        if (state().showDay2Review || state().showReviewGate || state().streakMilestoneAwarded != null) return
        val subscribed = CommonUtils.checkPurchaseForExerciseExamCCM(prefs)
        val reminderDay = FreemiumWindow.takeDueReminderDay(prefs, subscribed)
        if (reminderDay != null) {
            AppAnalytics.log(AppAnalytics.FREEMIUM_REMINDER_SHOWN, "day" to reminderDay)
            updateState_ { copy(showParentReminder = true, reminderIsReturning = false) }
            return
        }
        // no free-week reminder today: a returning-user prompt, if one is due on the Remote Config schedule
        val returningDay = FreemiumWindow.takeReturningPrompt(prefs, subscribed) ?: return
        AppAnalytics.log(AppAnalytics.RETURNING_PAYWALL_SHOWN, "day" to returningDay)
        updateState_ { copy(showParentReminder = true, reminderIsReturning = true) }
    }

    fun dismissParentReminder() = updateState_ { copy(showParentReminder = false) }

    // A failed renewal: tell the parent once a day; the store page opens only after the grown-up question (HomeScreen).
    // RevenueCat's customer info arrives a moment after launch, so wait for the first one.
    private fun checkBillingIssue() {
        viewModelScope.launch {
            val debug = BuildConfig.DEBUG && FreemiumWindow.debugBillingIssue
            if (!debug) RevenueCatHelper.customerInfoFlow.first { it != null }
            if (!debug && !RevenueCatHelper.hasBillingIssue) return@launch
            val today = com.jigar.me.utils.ReturningPaywallLogic.dayString(System.currentTimeMillis())
            if (!debug && prefs.getCustomParam(AppConstants.Freemium.billingIssueDay, "") == today) return@launch
            prefs.setCustomParam(AppConstants.Freemium.billingIssueDay, today)
            AppAnalytics.log(AppAnalytics.BILLING_ISSUE_SHOWN)
            val url = RevenueCatHelper.managementUrl ?: "https://play.google.com/store/account/subscriptions"
            updateState_ { copy(billingIssueUrl = url) }
        }
    }

    fun dismissBillingIssue() = updateState_ { copy(billingIssueUrl = null) }

    fun dismissStreakMilestone() {
        updateState_ { copy(streakMilestoneAwarded = null) }
        maybeShowReviewGate()
    }

    // Day-2 emotional review ask takes priority (once-ever); falls through to
    // the recurring milestone gate only when day 2 isn't eligible (they can
    // never both be eligible the same day since the milestone gate's earliest
    // day is 3), so the two overlays never stack.
    private fun maybeShowReviewGate() {
        if (AppReviewManager.shouldShowDay2Review(prefs)) {
            AppReviewManager.onDay2ReviewShown(prefs)
            updateState_ { copy(showDay2Review = true) }
            return
        }
        if (!AppReviewManager.shouldShowReviewGate(prefs)) return
        AppReviewManager.onGateShown(prefs)
        updateState_ { copy(showReviewGate = true) }
    }

    fun onReviewGateNegative() {
        AppReviewManager.onGateNegative(prefs)
        updateState_ { copy(showReviewGate = false) }
    }

    fun onReviewGatePositive(activity: android.app.Activity) {
        updateState_ { copy(showReviewGate = false) }
        viewModelScope.launch { AppReviewManager.onGatePositive(prefs, activity) }
    }

    fun onDay2ReviewFiveStars() {
        updateState_ { copy(showDay2Review = false) }
        AppReviewManager.onDay2ReviewFiveStars(prefs)
    }

    fun onDay2ReviewLowRating() {
        updateState_ { copy(showDay2Review = false) }
        AppReviewManager.onDay2ReviewLowRating(prefs)
    }

    fun dismissDay2Review() {
        updateState_ { copy(showDay2Review = false) }
        // Swiping away / tapping outside without picking a star must still stamp
        // the flag -- otherwise it re-qualifies and re-shows on every next launch.
        AppReviewManager.onDay2ReviewLowRating(prefs)
    }

    fun closeConflictPopup() {
        updateState_ { copy(isShowPurchasedConflictPopup = false) }
    }
    fun showHideNotificationSettingPopup(isShowNotificationPopup: Boolean) {
        updateState_ {
            copy(isShowNotificationSettingPopup = isShowNotificationPopup)
        }
    }

    fun hideFreeTrialPopup() {
        updateState_ {
            copy(isShowFreeTrialPopup = false)
        }
    }

    private suspend fun loadHomeMenu() {
        try {
            val displayMenuList = getDisplayMenuList()
            abacusDataRepository.getLevels(displayMenuList).collect {
                updateState_ {
                    copy(menuLevels = it)
                }
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    private fun getDisplayMenuList(): List<String> {
        val menuListStr = prefs.getCustomParam(AppConstants.RemoteConfig.displayMenuList, "")
        return if (menuListStr.isNotEmpty() && menuListStr.length > 5) {
            val type = object : TypeToken<ArrayList<String>>() {}.type
            Gson().fromJson(menuListStr, type)
        } else {
            listOf(
                AppConstants.HomeClicks.Menu_Abacus_Free_Mode,
                AppConstants.HomeClicks.Menu_Practice_Abacus,
                AppConstants.HomeClicks.Menu_Abacus_Exercise,
                AppConstants.HomeClicks.Menu_Exam,
                AppConstants.HomeClicks.Menu_CCM,
                AppConstants.HomeClicks.Menu_Math_Game,
                AppConstants.HomeClicks.Menu_Purchase_Store,
                AppConstants.HomeClicks.Menu_Settings,
                AppConstants.HomeClicks.Menu_My_Account,
                AppConstants.HomeClicks.Menu_Video_Tutorial
            )
        }
    }


    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }

}