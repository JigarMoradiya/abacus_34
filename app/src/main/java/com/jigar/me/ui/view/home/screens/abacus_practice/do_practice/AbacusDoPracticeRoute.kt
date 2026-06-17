package com.jigar.me.ui.view.home.screens.abacus_practice.do_practice

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo.screenHorizontalPadding
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.AbacusViewItem
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.AddSubAbacusItem
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.DivisionAbacusItem
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.MultiplicationAbacusItem
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.NumberAbacusItem
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.subitems.AbacusFormulaItem
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.subitems.SetTimer
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.subitems.UseWhichHandTextUi
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.viewmodels.AbacusDoPracticeViewModel
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.Loader
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.sheets.ReviewGateHost
import com.jigar.me.ui.view.home.common_ui.sheets.rememberReviewGateController
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.screens.reports.dialogs.ExerciseExamCompleteResultDialog
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.login.screens.login_home.viewmodels.LoginHomeViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.toastL

@Composable
fun AbacusDoPracticeRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: AbacusDoPracticeViewModel = hiltViewModel()
    val loginViewModel: LoginHomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loginUiState by loginViewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val prefs = LocalPreferencesHelper.current
    val reviewGate = rememberReviewGateController()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.pauseSetTimer()
                viewModel.persistSetTime()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(viewModel.abacusCalc.stateVersion) {
        viewModel.handleMatch()
    }

    // After Google login succeeds, submit the pending result
    loginUiState.navigateToHome?.consume {
        uiState.submitExerciseRequest?.let { viewModel.submitExamApi(it) }
    }

    loginUiState.errorMessage?.let { msg ->
        LaunchedEffect(msg) {
            context.toastL(msg)
            loginViewModel.consumeError()
        }
    }

    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButtonWithText(
                title = "Abacus No : ${(uiState.currentIndexOfAbacus + 1)}",
                onBackClick = onBackClick,
                modifier = Modifier.weight(1f))
            SetTimer(uiState)
        }

        Spacer(Modifier.weight(1f))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = screenHorizontalPadding())) {
            if (viewModel.isAbacusOnLeftHand) {
                AbacusViewItem(viewModel, uiState)
                uiState.currentAbacus?.let {
                    when (uiState.currentAbacusType) {
                        AppConstants.extras_Comman.AbacusTypeNumber -> {
                            NumberAbacusItem(it, modifier = Modifier.weight(1f))
                        }
                        AppConstants.extras_Comman.AbacusTypeAdditionSubtraction -> {
                            Spacer(Modifier.weight(1f))
                            AbacusFormulaItem(uiState)
                            AddSubAbacusItem(uiState)
                        }
                        AppConstants.extras_Comman.AbacusTypeMultiplication -> {
                            Spacer(Modifier.weight(1f))
                            AbacusFormulaItem(uiState)
                            MultiplicationAbacusItem(uiState)
                        }
                        AppConstants.extras_Comman.AbacusTypeDivision -> {
                            Spacer(Modifier.weight(1f))
                            AbacusFormulaItem(uiState)
                            DivisionAbacusItem(uiState)
                        }
                    }
                }
            } else {
                uiState.currentAbacus?.let {
                    when (uiState.currentAbacusType) {
                        AppConstants.extras_Comman.AbacusTypeNumber -> {
                            NumberAbacusItem(it, modifier = Modifier.weight(1f))
                        }
                        AppConstants.extras_Comman.AbacusTypeAdditionSubtraction -> {
                            AddSubAbacusItem(uiState)
                            AbacusFormulaItem(uiState)
                            Spacer(Modifier.weight(1f))
                        }
                        AppConstants.extras_Comman.AbacusTypeMultiplication -> {
                            MultiplicationAbacusItem(uiState)
                            AbacusFormulaItem(uiState)
                            Spacer(Modifier.weight(1f))
                        }
                        AppConstants.extras_Comman.AbacusTypeDivision -> {
                            DivisionAbacusItem(uiState)
                            AbacusFormulaItem(uiState)
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
                AbacusViewItem(viewModel, uiState)
                Spacer(Modifier.width(AppDimens.Dimens4))
            }
        }

        Spacer(Modifier.weight(1f))
    }

    UseWhichHandTextUi(uiState, viewModel)

    if (uiState.isLoading || loginUiState.isLoading) {
        Loader()
    }

    AnimatedVisibility(
        visible = uiState.isShowCompletePopup,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        if (uiState.isShowSubmitAnswer == true) {
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
                        if (pct >= 0.8f) reviewGate.attempt(prefs) { onBackClick() } else onBackClick()
                    },
                    onGiveAgain = onBackClick,
                )
            }
        } else {
            CustomPopupView(
                title = stringResource(R.string.congratulations),
                description = stringResource(R.string.txt_set_completed_msg),
                positiveButtonText = stringResource(R.string.ok_thanks),
                negativeButtonText = stringResource(R.string.close),
                icon = R.drawable.ic_alert_complete_page,
                widthMultiplier = 0.5f,
                onPositiveTapped = onBackClick,
                onNegativeTapped = onBackClick
            )
        }
    }

    ReviewGateHost(reviewGate, prefs)
}
