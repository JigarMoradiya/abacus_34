package com.jigar.me.ui.view.home.screens.home

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.Loader
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.common_ui.dialogs.FreeTrialDialog
import com.jigar.me.ui.view.home.screens.home.components.HomeHeaderLeft
import com.jigar.me.ui.view.home.screens.home.components.HomeHeaderRight
import com.jigar.me.ui.view.home.screens.home.components.HomeMenuScreen
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeFragmentViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens100
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens50
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens80
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
import com.jigar.me.utils.checkPermissions

@Composable
fun HomeScreen(
    onNavigateToLevelCategory: (levelId: String) -> Unit,
    onNavigateToAbacusFreeMode: () -> Unit,
    onNavigateToMathGameZone: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToMyAccount: () -> Unit,
    onNavigateToExercise: () -> Unit,
    onNavigateToExamHome: () -> Unit,
    onNavigateToCCMHome: () -> Unit,
    onNavigateToPurchase: () -> Unit,
    onNavigateToYoutubeVideo: () -> Unit,
    onNavigateToWhatsLearning: () -> Unit,
) {
    val viewModel: HomeFragmentViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val resumeActivityResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { /* no-op */ }

    val requestMultiplePermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.filter { !it.value }.also {
            if (it.isNotEmpty()) {
                viewModel.showHideNotificationSettingPopup(true)
            }
        }
    }

    fun onMenuClick(level: String, id: String? = null) {
        when (level) {
            AppConstants.HomeClicks.Menu_Practice_Abacus -> {
                id?.let { levelId -> onNavigateToLevelCategory(levelId) }
            }
            AppConstants.HomeClicks.Menu_Abacus_Free_Mode -> onNavigateToAbacusFreeMode()
            AppConstants.HomeClicks.Menu_Math_Game -> onNavigateToMathGameZone()
            AppConstants.HomeClicks.Menu_Settings -> onNavigateToSettings()
            AppConstants.HomeClicks.Menu_My_Account -> onNavigateToMyAccount()
            AppConstants.HomeClicks.Menu_Abacus_Exercise -> onNavigateToExercise()
            AppConstants.HomeClicks.Menu_Exam -> onNavigateToExamHome()
            AppConstants.HomeClicks.Menu_CCM -> onNavigateToCCMHome()
            AppConstants.HomeClicks.Menu_Purchase_Store -> onNavigateToPurchase()
            AppConstants.HomeClicks.Menu_Video_Tutorial -> onNavigateToYoutubeVideo()
        }
    }

    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            HomeHeaderLeft {
                onMenuClick(AppConstants.HomeClicks.Menu_My_Account)
            }
            Spacer(Modifier.weight(1f))
            HomeHeaderRight {
                onMenuClick(it)
            }
        }
        if (DeviceInfo.isTablet){
            Spacer(Modifier.height(Dimens50))
        }
        HomeMenuScreen(
            uiState = uiState,
            onMenuClick = {
                AudioPlayerManager.playSoundBtnClick()
                onMenuClick(it.name, it.id)
            },
            modifier = Modifier.weight(1f).padding(vertical = AppDimens.Dimens16)
        )
        if (DeviceInfo.isTablet){
            Spacer(Modifier.height(Dimens80))
        }
    }

    if (uiState.isLoading == true) {
        Loader()
    }

    AnimatedVisibility(
        visible = uiState.isShowPurchasedConflictPopup,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        val title = if (uiState.purchasedConflictPopupType ==
            AppConstants.APIStatus.ERROR_CODE_THIS_STUDENT_IS_ASSOCIATED_WITH_OTHER_ORDER
        ) {
            stringResource(R.string.your_login_is_associated_with_other_purchases)
        } else {
            stringResource(R.string.your_device_purchases_is_associated_with_other_login)
        }
        CustomPopupView(
            title = title,
            description = stringResource(R.string.want_to_move_purchase_with_this_login),
            positiveButtonText = stringResource(R.string.yes_i_want_to_move),
            negativeButtonText = stringResource(R.string.no_move_later),
            notes = stringResource(R.string.no_move_later_msg),
            widthMultiplier = 0.8f,
            onPositiveTapped = { viewModel.changePurchase() },
            onNegativeTapped = { viewModel.closeConflictPopup() }
        )
    }

    uiState.checkNotificationPermission?.consume {
        context.checkPermissions(Constants.NOTIFICATION_PERMISSION, requestMultiplePermissions)
    }

    AnimatedVisibility(
        visible = uiState.isShowNotificationSettingPopup,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        CustomPopupView(
            title = stringResource(R.string.permission_alert),
            description = stringResource(R.string.notification_permission_msg),
            positiveButtonText = stringResource(R.string.okay),
            negativeButtonText = stringResource(R.string.give_later),
            widthMultiplier = 0.7f,
            onPositiveTapped = {
                viewModel.showHideNotificationSettingPopup(false)
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", context.packageName, null)
                resumeActivityResultLauncher.launch(intent)
            },
            onNegativeTapped = {
                viewModel.showHideNotificationSettingPopup(false)
            }
        )
    }

    AnimatedVisibility(
        visible = uiState.isShowFreeTrialPopup,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        uiState.freeTrialParam?.let { freeTrialParam ->
            FreeTrialDialog(
                remainingDays = freeTrialParam.remainingDays,
                discountPer = freeTrialParam.discountPer,
                discountPerLifetime = freeTrialParam.discountPerLifeTime,
                manualFreeTrialDays = freeTrialParam.manualFreeTrialDays,
                onYes = {
                    viewModel.hideFreeTrialPopup()
                    if (freeTrialParam.remainingDays >= 7) {
                        onNavigateToWhatsLearning()
                    } else {
                        onNavigateToPurchase()
                    }
                },
                onNo = { viewModel.hideFreeTrialPopup() },
                onDismiss = { viewModel.hideFreeTrialPopup() }
            )
        }
    }
}
