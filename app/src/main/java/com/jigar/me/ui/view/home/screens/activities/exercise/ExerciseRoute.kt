package com.jigar.me.ui.view.home.screens.activities.exercise

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.home.screens.activities.exercise.components.ExerciseScreen
import com.jigar.me.ui.view.home.screens.activities.exercise.viewmodels.ExerciseViewModel
import com.jigar.me.ui.view.home.common_ui.Loader
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.sheets.ReviewGateHost
import com.jigar.me.ui.view.home.common_ui.sheets.rememberReviewGateController
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.common_ui.dialogs.PopupTheme
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumLoginBottomSheet
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumPaywallBottomSheet
import com.jigar.me.ui.view.home.common_ui.dialogs.PaywallContext
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.home.screens.reports.dialogs.ExerciseExamCompleteResultDialog
import com.jigar.me.ui.view.login.screens.login_home.viewmodels.LoginHomeViewModel
import com.jigar.me.utils.extensions.toastL

@Composable
fun ExerciseRoute(
    homeActivityViewModel: HomeActivityViewModel,
    onBackClick: () -> Unit,
) {
    val viewModel: ExerciseViewModel = hiltViewModel()
    val loginViewModel: LoginHomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loginUiState by loginViewModel.uiState.collectAsStateWithLifecycle()
    var isPurchase by remember { mutableStateOf(homeActivityViewModel.isPurchasedForModule()) }
    LaunchedEffect(Unit) { homeActivityViewModel.isPurchasedFlow.collect { if (it) isPurchase = true } }
    val isLoggedIn = homeActivityViewModel.isUserLoggedIn()
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = LocalPreferencesHelper.current
    val reviewGate = rememberReviewGateController()
    viewModel.setIsPurchased(isPurchase)
    var showPaywall by remember { mutableStateOf(false) }

    loginUiState.navigateToHome?.consume {
        uiState.submitExerciseRequest?.let { viewModel.submitExamApi(it) }
    }
    loginUiState.errorMessage?.let { msg ->
        LaunchedEffect(msg) {
            context.toastL(msg)
            loginViewModel.consumeError()
        }
    }
    var showLogin by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.abacusCalc.stateVersion) {
        viewModel.handleMatch()
    }

    fun handleBack() {
        if (viewModel.uiState.value.isExerciseStarted) {
            viewModel.onLeaveExercise()
        } else {
            onBackClick()
        }
    }

    BackHandler { handleBack() }

    AbTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Sunset, Modifier.fillMaxSize())
            ExerciseScreen(uiState, viewModel, isLoggedIn = isLoggedIn,
                onShowLogin = { showLogin = true }, onShowPaywall = { showPaywall = true }) { handleBack() }
        }

        if (uiState.isLoading) {
            Loader()
        }

        if (showLogin) {
            FreemiumLoginBottomSheet(
                showContinueWithoutSaving = true,
                subtitle = stringResource(R.string.exercise_login_subtitle),
                onLoginSuccess = { showLogin = false; homeActivityViewModel.refreshPurchaseState(); isPurchase = homeActivityViewModel.isPurchasedForModule(); viewModel.generateExercise() },
                onContinueWithoutSaving = { showLogin = false; viewModel.generateExerciseWithoutSaving() },
                onDismiss = { showLogin = false }
            )
        }

        if (showPaywall) {
            FreemiumPaywallBottomSheet(
                paywallContext = PaywallContext.EXERCISE,
                onSubscriptionActivated = { isPurchase = true; showPaywall = false },
                onDismiss = { showPaywall = false }
            )
        }

        if (loginUiState.isLoading) { Loader() }

        // the result popup runs its own entrance, so it is composed plainly
        if (uiState.isShowCompletePopup) {
            uiState.submitExerciseRequest?.let {
                ExerciseExamCompleteResultDialog(
                    selectedTheme = viewModel.selectedTheme,
                    request = it,
                    saveResults = uiState.saveResults,
                    onLoginToSave = if (!uiState.saveResults && !uiState.resultSaved) {
                        { loginViewModel.signInWithGoogle(context) }
                    } else null,
                    onClose = {
                        val pct = (it.no_of_right_answers ?: 0).toFloat() / (it.no_of_questions ?: 1).toFloat()
                        if (pct >= 0.8f) reviewGate.attempt(prefs) { viewModel.closeExercise() } else viewModel.closeExercise()
                    },
                    onGiveAgain = { viewModel.generateExercise() },
                )
            }
        }

        ReviewGateHost(reviewGate, prefs)

        if (uiState.isLeavePage) {
            CustomPopupView(
                title = stringResource(R.string.leave_exercise_alert),
                description = stringResource(R.string.leave_exercise_msg),
                positiveButtonText = stringResource(R.string.yes_i_m_sure),
                negativeButtonText = stringResource(R.string.no_please_continue),
                icon = R.drawable.ic_alert,
                theme = PopupTheme.CONFIRM,
                accent = ButtonType.RED,
                widthMultiplier = 0.5f,
                onPositiveTapped = { viewModel.closeExercise() },
                onNegativeTapped = { viewModel.resumeExercise() }
            )
        }

        if (uiState.isShowNoInternet) {
            CustomPopupView(
                title = stringResource(R.string.no_internet_working),
                description = uiState.noInternetMessage,
                notes = stringResource(R.string.no_internet_close_notes),
                positiveButtonText = stringResource(R.string.continue_working_internet),
                negativeButtonText = stringResource(R.string.no_working_internet),
                icon = R.drawable.ic_alert_sad_emoji,
                theme = PopupTheme.CONFIRM,
                accent = ButtonType.RED,
                widthMultiplier = 0.7f,
                onPositiveTapped = { viewModel.completeExercise(false) },
                onNegativeTapped = { onBackClick() }
            )
        }
    }
}
