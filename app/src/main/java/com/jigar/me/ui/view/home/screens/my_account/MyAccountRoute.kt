package com.jigar.me.ui.view.home.screens.my_account

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.screens.my_account.components.MyAccountScreen
import com.jigar.me.ui.view.home.screens.my_account.viewmodels.MyAccountViewModel
import com.jigar.me.ui.view.login.LoginDashboardActivity
import com.jigar.me.ui.view.other.ContactUsActivity
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.openURL

@Composable
fun MyAccountRoute(
    onBackClick: () -> Unit,
    onNavigateToFAQs: () -> Unit,
    onNavigateToPurchase: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToReportHistory: () -> Unit,
    onNavigateToWhatsLearning: () -> Unit,
) {
    val viewModel: MyAccountViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        BackButtonWithText(
            title = stringResource(R.string.my_account),
            onBackClick = onBackClick
        )
        MyAccountScreen(uiState) { tag ->
            when (tag) {
                "faqs" -> onNavigateToFAQs()
                "subscription" -> onNavigateToPurchase()
                "setting" -> onNavigateToSettings()
                "report_history" -> onNavigateToReportHistory()
                "about_app" -> onNavigateToWhatsLearning()
                "rate_us_on_the_play_store" -> {
                    context.openURL("https://play.google.com/store/apps/details?id=${context.packageName}")
                }
                "need_help" -> {
                    ContactUsActivity.getInstance(context, AppConstants.extras_Comman.typeNeedHelp)
                }
                "privacy_policy" -> {
                    uiState.privacyPolicyUrl?.let { context.openURL(it) }
                }
                "logout" -> viewModel.logoutOpenClose(true)
            }
        }
    }

    AnimatedVisibility(
        visible = uiState.isShowLogoutPopup,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        CustomPopupView(
            title = stringResource(R.string.logout_alert),
            description = stringResource(R.string.logout_alert_msg),
            positiveButtonText = stringResource(R.string.yes_i_m_sure),
            negativeButtonText = stringResource(R.string.no),
            icon = R.drawable.ic_alert,
            widthMultiplier = 0.5f,
            onPositiveTapped = {
                viewModel.makeLogout()
                LoginDashboardActivity.getInstance(context)
            },
            onNegativeTapped = { viewModel.logoutOpenClose(false) }
        )
    }
}
