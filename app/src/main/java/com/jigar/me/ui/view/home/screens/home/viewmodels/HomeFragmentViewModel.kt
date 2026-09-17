package com.jigar.me.ui.view.home.screens.home.viewmodels

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
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.AppReviewManager
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.HomeOfferManager
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
    }

    // Time-limited Home offer (Remote Config `home_offer`). `enabled` + `product` is
    // the only thing that decides whether this shows -- no dependency on the
    // permanent discount_per(_lifetime) fields or the display_plan whitelist.
    private fun initHomeOffer() {
        val cfg = HomeOfferManager.config(prefs)
        val targetsLifetime = cfg.targetsLifetime
        HomeOfferManager.startIfNeeded(prefs, forceRestart = BuildConfig.DEBUG)
        if (!HomeOfferManager.isTimedOfferActive(prefs)) return

        // Hide the card for premium users, reacting to RC updates (login, restore, purchase).
        viewModelScope.launch {
            RevenueCatHelper.customerInfoFlow.collect {
                updateState_ { copy(isPremium = CommonUtils.checkPurchaseForExerciseExamCCM(prefs)) }
            }
        }
        viewModelScope.launch {
            val packages = runCatching { Purchases.sharedInstance.awaitOfferings().current?.availablePackages }
                .getOrNull() ?: emptyList()
            val baseId = if (targetsLifetime) AppConstants.Products.lifetime else AppConstants.Products.year
            val offerId = if (targetsLifetime) AppConstants.Products.lifetimeOffer else AppConstants.Products.yearOffer
            val base  = packages.firstOrNull { AppConstants.Products.matches(it.product.id, baseId) }
            val offer = packages.firstOrNull { AppConstants.Products.matches(it.product.id, offerId) }
            // The .offer package must actually exist in the store offering -- can't
            // show a discount for a product that isn't real. (The paywall itself is
            // made to always include this pair while the timed offer is active, so
            // there's no display_plan whitelist dependency to check here either.)
            if (offer == null) return@launch
            // Re-validate the window is still running now that offerings have loaded --
            // it may have expired during the await -- so an already-expired offer is
            // never published (HomeOfferCard would self-correct within a frame anyway,
            // but there's no reason to show it even briefly).
            if (!HomeOfferManager.isTimedOfferActive(prefs)) return@launch
            val startedAt = HomeOfferManager.startedAt(prefs) ?: return@launch
            val discountPercent = if (targetsLifetime)
                HomeOfferManager.effectiveLifetimeDiscountPer(prefs, base?.product?.price?.amountMicros, offer.product.price.amountMicros)
            else
                HomeOfferManager.effectiveYearDiscountPer(prefs, base?.product?.price?.amountMicros, offer.product.price.amountMicros)
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

        // No streak dialog queued up to show this session — check the review
        // gate immediately. If a milestone dialog IS queued, defer to
        // dismissStreakMilestone() so the two never show at once.
        if (result.milestoneAwarded == null) maybeShowReviewGate()
    }

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