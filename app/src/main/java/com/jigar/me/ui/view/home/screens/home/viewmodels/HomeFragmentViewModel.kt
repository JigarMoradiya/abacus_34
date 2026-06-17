package com.jigar.me.ui.view.home.screens.home.viewmodels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.core.StatefulViewModel
import com.jigar.me.ui.jetpack.core.domain.ConsumableCommand
import com.jigar.me.ui.jetpack.core.repository.abacus_data.AbacusDataRepository
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.AppReviewManager
import com.jigar.me.utils.StreakManager
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

        // No streak dialog queued up to show this session — check the review
        // gate immediately. If a milestone dialog IS queued, defer to
        // dismissStreakMilestone() so the two never show at once.
        if (result.milestoneAwarded == null) maybeShowReviewGate()
    }

    fun dismissStreakMilestone() {
        updateState_ { copy(streakMilestoneAwarded = null) }
        maybeShowReviewGate()
    }

    private fun maybeShowReviewGate() {
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