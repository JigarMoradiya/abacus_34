package com.jigar.me.ui.view.login.screens.splash.viewmodels

import android.content.Context
import android.os.Build
import androidx.lifecycle.viewModelScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.core.StatefulViewModel
import com.jigar.me.ui.jetpack.core.domain.ConsumableCommand
import com.jigar.me.ui.view.login.data.PostLoginHandler
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
import com.jigar.me.utils.extensions.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: AppPreferencesHelper,
    private val postLoginHandler: PostLoginHandler,
) : StatefulViewModel<SplashUiState>() {

    override val TAG = "SplashViewModel"

    override fun getInitialState() = SplashUiState()

    private val remoteConfig: FirebaseRemoteConfig by lazy {
        FirebaseRemoteConfig.getInstance().apply {
            val settings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(5)
                .build()
            setConfigSettingsAsync(settings)
        }
    }

    fun fetchRemoteConfigAndContinue() {
        viewModelScope.launch {
            try {
                remoteConfig.fetchAndActivate().await()
                handleVersionCheck()
            } catch (_: Exception) {
                // Ignore — splash should still continue silently if remote config fails
                routeAfterRemoteConfig()
            }
        }
    }

    private fun handleVersionCheck() {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val appVersion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION") pInfo.versionCode.toLong()
            }
            val remoteVersion = remoteConfig.getLong(AppConstants.RemoteConfig.versionCode)
            if (remoteVersion > appVersion) {
                updateState_ { copy(isLoading = false, showAppUpdatePopup = true) }
            } else {
                persistRemoteConfig(remoteVersion)
                routeAfterRemoteConfig()
            }
        } catch (_: Exception) {
            routeAfterRemoteConfig()
        }
    }

    private fun persistRemoteConfig(remoteVersion: Long) {
        val video = remoteConfig.getString(AppConstants.RemoteConfig.videoList)
        val displayPlan = remoteConfig.getString(AppConstants.RemoteConfig.displayPlanList)
        val displayMenu = remoteConfig.getString(AppConstants.RemoteConfig.displayMenuList)
        val privacyPolicyUrl = remoteConfig.getString(AppConstants.RemoteConfig.privacyPolicyUrl)
        val supportEmail = remoteConfig.getString(AppConstants.RemoteConfig.supportEmail)
        val newVersionNotes = remoteConfig.getString(AppConstants.RemoteConfig.newVersionNotes)
        val bulkLogin = remoteConfig.getString(AppConstants.RemoteConfig.bulkLogin)
        val discountPer = remoteConfig.getLong(AppConstants.RemoteConfig.discountPer)
        val discountPerLifeTime = remoteConfig.getLong(AppConstants.RemoteConfig.discountPerLifeTime)
        val manualFreeTrialDays = remoteConfig.getLong(AppConstants.RemoteConfig.manualFreeTrialDays)

        with(prefs) {
            setCustomParam(AppConstants.RemoteConfig.privacyPolicyUrl, privacyPolicyUrl)
            setCustomParam(AppConstants.RemoteConfig.supportEmail, supportEmail)
            setCustomParam(AppConstants.RemoteConfig.newVersionNotes, newVersionNotes)
            setCustomParam(AppConstants.RemoteConfig.bulkLogin, bulkLogin)
            setCustomParamInt(AppConstants.RemoteConfig.versionCode, remoteVersion.toInt())
            setCustomParamInt(AppConstants.RemoteConfig.discountPer, discountPer.toInt())
            setCustomParamInt(AppConstants.RemoteConfig.discountPerLifeTime, discountPerLifeTime.toInt())
            setCustomParamInt(AppConstants.RemoteConfig.manualFreeTrialDays, manualFreeTrialDays.toInt())
            setCustomParam(AppConstants.RemoteConfig.videoList, if (video.length > 5) video else "")
            setCustomParam(AppConstants.RemoteConfig.displayPlanList, displayPlan)
            setCustomParam(AppConstants.RemoteConfig.displayMenuList, displayMenu)
        }
    }

    private fun routeAfterRemoteConfig() {
        if (!context.isNetworkAvailable) {
            updateState_ { copy(isLoading = false, showNoInternetPopup = true) }
            return
        }
        val hasLocalData = prefs.getCustomParam(Constants.last_sync_time, "").isNotEmpty()
        viewModelScope.launch {
            val outcome = if (prefs.isUserLoggedIn() && !prefs.getAccessToken().isNullOrEmpty()) {
                postLoginHandler.fetchAbacusDataForAuth()
            } else {
                postLoginHandler.fetchPublicAbacusData()
            }
            when (outcome) {
                is PostLoginHandler.Outcome.NavigateHome -> {
                    updateState_ { copy(isLoading = false, navigateToHome = ConsumableCommand(Unit)) }
                }
                is PostLoginHandler.Outcome.Failure -> {
                    if (hasLocalData) {
                        // Has cached data — navigate home, use cache
                        updateState_ { copy(isLoading = false, navigateToHome = ConsumableCommand(Unit)) }
                    } else {
                        // No cached data — must show error, user needs to retry
                        updateState_ { copy(isLoading = false, showErrorPopup = true, errorPopupMessage = outcome.message) }
                    }
                }
            }
        }
    }

    fun retryAfterError() {
        updateState_ { copy(isLoading = true, showErrorPopup = false, showNoInternetPopup = false, errorPopupMessage = null) }
        fetchRemoteConfigAndContinue()
    }

    fun onUpdateConfirmed() {
        updateState_ { copy(showAppUpdatePopup = false, finishActivity = ConsumableCommand(Unit)) }
    }

    fun onUpdateDeclined() {
        updateState_ { copy(showAppUpdatePopup = false, finishActivity = ConsumableCommand(Unit)) }
    }

    fun consumeError() {
        updateState_ { copy(errorMessage = null) }
    }

    override fun onFailure(throwable: Throwable) {
        updateState_ { copy(errorMessage = throwable.localizedMessage) }
    }
}
