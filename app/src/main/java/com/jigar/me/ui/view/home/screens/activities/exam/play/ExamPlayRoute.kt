package com.jigar.me.ui.view.home.screens.activities.exam.play

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbChip
import com.jigar.me.ui.view.ab.AbFitOnPhone
import com.jigar.me.ui.view.ab.AbProgressBar
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.home.common_ui.Loader
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.sheets.ReviewGateHost
import com.jigar.me.ui.view.home.common_ui.sheets.rememberReviewGateController
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.common_ui.dialogs.PopupTheme
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.screens.activities.ActivityTopBar
import com.jigar.me.ui.view.login.screens.login_home.viewmodels.LoginHomeViewModel
import com.jigar.me.utils.extensions.toastL
import com.jigar.me.ui.view.home.screens.activities.exam.play.components.ExamQuestionSection
import com.jigar.me.ui.view.home.screens.activities.exam.play.viewmodels.ExamPlayViewModel
import com.jigar.me.ui.view.home.screens.reports.dialogs.ExerciseExamCompleteResultDialog
import com.jigar.me.utils.extensions.secToTimeFormat

@Composable
fun ExamPlayRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: ExamPlayViewModel = hiltViewModel()
    val loginViewModel: LoginHomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loginUiState by loginViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val prefs = LocalPreferencesHelper.current
    val reviewGate = rememberReviewGateController()

    loginUiState.navigateToHome?.consume {
        uiState.submitExamRequest?.let { viewModel.submitExamApi(it) }
    }
    loginUiState.errorMessage?.let { msg ->
        LaunchedEffect(msg) {
            context.toastL(msg)
            loginViewModel.consumeError()
        }
    }

    BackHandler { viewModel.onLeaveExam() }

    AbTheme {
        val s = LocalAbScale.current.factor
        Box(modifier = Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Twilight, Modifier.fillMaxSize())
            Column(modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)) {
                ActivityTopBar(title = stringResource(R.string.math_exam), onBack = { viewModel.onLeaveExam() }) {
                    AbProgressBar(
                        filled = maxOf(0, uiState.currentIndex + 1),
                        total = maxOf(1, uiState.examPaper.size),
                        modifier = Modifier.widthIn(max = 220.dp * s),
                    )
                    AbChip(text = "Time : " + uiState.elapsedSeconds.secToTimeFormat(), tone = AbTone.Purple, icon = Icons.Filled.Schedule)
                }

                // the question sits in the middle of the room under the header (on a phone it shrinks to fit it)
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    AbFitOnPhone {
                        ExamQuestionSection(uiState = uiState, viewModel = viewModel)
                    }
                }
            }
        }

        if (uiState.isLoading || loginUiState.isLoading) {
            Loader()
        }

        // the result popup runs its own entrance, so it is composed plainly
        if (uiState.isShowCompletePopup) {
            uiState.submitExamRequest?.let {
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
                    onGiveAgain = { viewModel.reGenerateExam() }
                )
            }
        }

        ReviewGateHost(reviewGate, prefs)

        if (uiState.isLeavePage) {
            CustomPopupView(
                title = stringResource(R.string.leave_exam_alert),
                description = stringResource(R.string.leave_exam_msg),
                positiveButtonText = stringResource(R.string.yes_i_m_sure),
                negativeButtonText = stringResource(R.string.no_please_continue),
                icon = R.drawable.ic_alert,
                theme = PopupTheme.CONFIRM,
                accent = ButtonType.RED,
                widthMultiplier = 0.5f,
                onPositiveTapped = onBackClick,
                onNegativeTapped = { viewModel.resumeExam() })
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
                onPositiveTapped = { viewModel.completeExam(false) },
                onNegativeTapped = onBackClick
            )
        }
    }
}
