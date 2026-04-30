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
import com.jigar.me.ui.view.home.common_ui.buttons.KidsLabel
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.home.screens.reports.dialogs.ExerciseExamCompleteResultDialog
import com.jigar.me.ui.view.home.theme.AppDimens.ToolbarIconSize
import com.jigar.me.ui.view.home.theme.AppDimens.exerciseWidth
import com.jigar.me.utils.extensions.secToCountDown

@Composable
fun ExerciseRoute(
    homeActivityViewModel: HomeActivityViewModel,
    onBackClick: () -> Unit,
    onNavigateToPurchase: () -> Unit,
) {
    val viewModel: ExerciseViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val purchasedSKU by homeActivityViewModel.purchasedSku.collectAsStateWithLifecycle()
    val isPurchase = homeActivityViewModel.isPurchasedForModule(purchasedSKU)
    viewModel.setIsPurchased(isPurchase)

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

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        if (uiState.isAbacusOnLeftHand) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ExerciseAbacusRow(uiState, viewModel, Modifier.weight(1f)) { handleBack() }
                ExerciseScreen(uiState, viewModel) { handleBack() }
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ExerciseScreen(uiState, viewModel) { handleBack() }
                ExerciseAbacusRow(uiState, viewModel, Modifier.fillMaxSize()) { handleBack() }
            }
        }
        if (uiState.isExerciseStarted) {
            val alignment = if (uiState.isAbacusOnLeftHand) Alignment.TopEnd else Alignment.TopStart
            val paddingStart = if (uiState.isAbacusOnLeftHand) 0.dp else exerciseWidth
            val paddingEnd = if (uiState.isAbacusOnLeftHand) exerciseWidth else 0.dp
            Row(
                modifier = Modifier
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

    uiState.navigateToPurchase?.consume {
        onNavigateToPurchase()
    }

    AnimatedVisibility(
        visible = uiState.isShowCompletePopup, enter = fadeIn(), exit = fadeOut()
    ) {
        uiState.submitExerciseRequest?.let {
            ExerciseExamCompleteResultDialog(
                selectedTheme = viewModel.selectedTheme,
                it,
                onClose = { viewModel.closeExercise() },
                onGiveAgain = { viewModel.generateExercise() },
            )
        }
    }

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
