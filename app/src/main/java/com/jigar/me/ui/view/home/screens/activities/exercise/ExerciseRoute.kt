package com.jigar.me.ui.view.home.screens.activities.exercise

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.home.screens.activities.exercise.components.ExerciseAbacusRow
import com.jigar.me.ui.view.home.screens.activities.exercise.components.ExerciseScreen
import com.jigar.me.ui.view.home.screens.activities.exercise.viewmodels.ExerciseViewModel
import com.jigar.me.ui.view.home.common_ui.Loader
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.sheets.ReviewGateHost
import com.jigar.me.ui.view.home.common_ui.sheets.rememberReviewGateController
import com.jigar.me.ui.view.home.common_ui.buttons.KidsLabel
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumLoginBottomSheet
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumPaywallBottomSheet
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.home.screens.reports.dialogs.ExerciseExamCompleteResultDialog
import com.jigar.me.ui.view.login.screens.login_home.viewmodels.LoginHomeViewModel
import com.jigar.me.utils.extensions.toastL
import com.jigar.me.ui.view.home.theme.AppDimens.ToolbarIconSize
import com.jigar.me.ui.view.home.theme.AppDimens.exerciseWidth
import com.jigar.me.utils.extensions.secToCountDown

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

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isAbacusOnLeftHand) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f).windowInsetsPadding(WindowInsets.safeDrawing)) {
                    ExerciseAbacusRow(uiState, viewModel, Modifier) { handleBack() }
                }
                ExerciseScreen(uiState, viewModel, isLoggedIn = isLoggedIn,
                    onShowLogin = { showLogin = true }, onShowPaywall = { showPaywall = true }) { handleBack() }
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ExerciseScreen(uiState, viewModel, isLoggedIn = isLoggedIn,
                    onShowLogin = { showLogin = true }, onShowPaywall = { showPaywall = true }) { handleBack() }
                ExerciseAbacusRow(uiState, viewModel, Modifier.fillMaxSize()) { handleBack() }
            }
        }
        if (uiState.isExerciseStarted) {
            val alignment = if (uiState.isAbacusOnLeftHand) Alignment.TopEnd else Alignment.TopStart
            val paddingStart = if (uiState.isAbacusOnLeftHand) 0.dp else exerciseWidth
            val paddingEnd = if (uiState.isAbacusOnLeftHand) exerciseWidth else 0.dp
            Row(
                modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
                    .height(ToolbarIconSize)
                    .align(alignment)
                    .padding(
                        start = paddingStart,
                        end = paddingEnd,
                        top = AppDimens.Dimens12
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val innerPaddingStart = if (uiState.isAbacusOnLeftHand) 0.dp else AppDimens.Dimens24
                val innerPaddingEnd = if (uiState.isAbacusOnLeftHand) AppDimens.Dimens24 else 0.dp

                KidsLabel(
                    text = "Time : "+uiState.elapsedSeconds.secToCountDown(),modifier = Modifier.padding(start = innerPaddingStart, end = innerPaddingEnd)
                )
            }
        }
    }

    if (uiState.isLoading) {
        Loader()
    }

    if (showLogin) {
        FreemiumLoginBottomSheet(
            showContinueWithoutSaving = true,
            subtitle = stringResource(R.string.exercise_login_subtitle),
            onLoginSuccess = { showLogin = false; isPurchase = homeActivityViewModel.isPurchasedForModule(); viewModel.generateExercise() },
            onContinueWithoutSaving = { showLogin = false; viewModel.generateExerciseWithoutSaving() },
            onDismiss = { showLogin = false }
        )
    }

    if (showPaywall) {
        FreemiumPaywallBottomSheet(
            onSubscriptionActivated = { isPurchase = true; showPaywall = false },
            onDismiss = { showPaywall = false }
        )
    }

    if (loginUiState.isLoading) { Loader() }

    AnimatedVisibility(
        visible = uiState.isShowCompletePopup, enter = fadeIn(), exit = fadeOut()
    ) {
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

    AnimatedVisibility(
        visible = uiState.isLeavePage, enter = fadeIn(), exit = fadeOut()
    ) {
        CustomPopupView(
            title = stringResource(R.string.leave_exercise_alert),
            description = stringResource(R.string.leave_exercise_msg),
            positiveButtonText = stringResource(R.string.yes_i_m_sure),
            negativeButtonText = stringResource(R.string.no_please_continue),
            icon = R.drawable.ic_alert,
            widthMultiplier = 0.5f,
            onPositiveTapped = { viewModel.closeExercise() },
            onNegativeTapped = { viewModel.resumeExercise() }
        )
    }

    AnimatedVisibility(
        visible = uiState.isShowNoInternet, enter = fadeIn(), exit = fadeOut()
    ) {
        CustomPopupView(
            title = stringResource(R.string.no_internet_working),
            description = uiState.noInternetMessage,
            notes = stringResource(R.string.no_internet_close_notes),
            positiveButtonText = stringResource(R.string.continue_working_internet),
            negativeButtonText = stringResource(R.string.no_working_internet),
            icon = R.drawable.ic_alert_sad_emoji,
            widthMultiplier = 0.7f,
            onPositiveTapped = { viewModel.completeExercise(false) },
            onNegativeTapped = { onBackClick() }
        )
    }
}
