package com.jigar.me.ui.view.home.screens.activities.exam.play

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.Loader
import com.jigar.me.ui.view.home.common_ui.buttons.KidsLabel
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.screens.activities.exam.play.components.ExamProgressBar
import com.jigar.me.ui.view.home.screens.activities.exam.play.components.ExamQuestionSection
import com.jigar.me.ui.view.home.screens.activities.exam.play.viewmodels.ExamPlayViewModel
import com.jigar.me.ui.view.home.screens.reports.dialogs.ExerciseExamCompleteResultDialog
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.extensions.secToTimeFormat

@Composable
fun ExamPlayRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: ExamPlayViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler { viewModel.onLeaveExam() }

    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Box {
            Row {
                BackButtonWithText(
                    title = stringResource(R.string.math_exam),
                    onBackClick = { viewModel.onLeaveExam() },
                    modifier = Modifier.weight(1f)
                )
            }

            ExamProgressBar(
                progress = uiState.currentIndex + 1,
                max = uiState.examPaper.size,
                modifier = Modifier
                    .width(AppDimens.Dimens240)
                    .padding(horizontal = AppDimens.Dimens16)
                    .align(alignment = Alignment.Center)
            )


            KidsLabel(
                text = "Time : "+uiState.elapsedSeconds.secToTimeFormat(),modifier = Modifier.align(alignment = Alignment.CenterEnd)
            )
        }
        Box(modifier = Modifier.fillMaxSize()) {
            ExamQuestionSection(
                uiState = uiState,
                viewModel = viewModel,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    if (uiState.isLoading) {
        Loader()
    }

    AnimatedVisibility(
        visible = uiState.isShowCompletePopup,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        uiState.submitExamRequest?.let {
            ExerciseExamCompleteResultDialog(
                selectedTheme = viewModel.selectedTheme,
                it,
                onClose = onBackClick,
                onGiveAgain = { viewModel.reGenerateExam() }
            )
        }
    }

    AnimatedVisibility(
        visible = uiState.isLeavePage,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        CustomPopupView(
            title = stringResource(R.string.leave_exam_alert),
            description = stringResource(R.string.leave_exam_msg),
            positiveButtonText = stringResource(R.string.yes_i_m_sure),
            negativeButtonText = stringResource(R.string.no_please_continue),
            icon = R.drawable.ic_alert,
            widthMultiplier = 0.5f,
            onPositiveTapped = onBackClick,
            onNegativeTapped = { viewModel.resumeExam() }
        )
    }

    AnimatedVisibility(
        visible = uiState.isShowNoInternet,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        CustomPopupView(
            title = stringResource(R.string.no_internet_working),
            description = uiState.noInternetMessage,
            notes = stringResource(R.string.no_internet_close_notes),
            positiveButtonText = stringResource(R.string.continue_working_internet),
            negativeButtonText = stringResource(R.string.no_working_internet),
            icon = R.drawable.ic_alert_sad_emoji,
            widthMultiplier = 0.7f,
            onPositiveTapped = { viewModel.completeExam(false) },
            onNegativeTapped = onBackClick
        )
    }
}
