package com.jigar.me.ui.view.home.screens.activities.exam.play

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.Loader
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.sheets.ReviewGateHost
import com.jigar.me.ui.view.home.common_ui.sheets.rememberReviewGateController
import com.jigar.me.ui.view.home.common_ui.buttons.KidsLabel
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.common_ui.Loader
import com.jigar.me.ui.view.home.screens.abacus_practice.level_list.components.ProgressCandyRow
import com.jigar.me.ui.view.login.screens.login_home.viewmodels.LoginHomeViewModel
import com.jigar.me.utils.extensions.toastL
import com.jigar.me.ui.view.home.screens.activities.exam.play.components.ExamQuestionSection
import com.jigar.me.ui.view.home.screens.activities.exam.play.components.rememberBlinkAlpha
import com.jigar.me.ui.view.home.screens.activities.exam.play.viewmodels.ExamPlayViewModel
import com.jigar.me.ui.view.home.screens.reports.dialogs.ExerciseExamCompleteResultDialog
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.extensions.secToTimeFormat

@Composable
fun ExamPlayRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: ExamPlayViewModel = hiltViewModel()
    val loginViewModel: LoginHomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loginUiState by loginViewModel.uiState.collectAsStateWithLifecycle()
    val blinkAlpha by rememberBlinkAlpha()
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

    Column(modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.safeDrawing)) {
        Box {
            Row {
                BackButtonWithText(
                    title = stringResource(R.string.math_exam), onBackClick = { viewModel.onLeaveExam() }, modifier = Modifier.weight(1f)
                )
            }

            Column(
                modifier = Modifier
                    .width(AppDimens.Dimens300)
                    .padding(horizontal = AppDimens.Dimens16)
                    .padding(top = DeviceInfo.screenTopPadding())
                    .align(alignment = Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
            ) {
                // ---------- Question Progress Bar ----------
                ProgressCandyRow(
                    completed = uiState.currentIndex + 1,
                    total = uiState.examPaper.size,
                    colors = listOf(
                        Color(0xFF46BBC5),
                        Color(0xFF107D86),
                        Color(0xFF0B5960)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // ---------- Blink text ----------
                Text(
                    text = stringResource(R.string.TapCorrectAns), style = MaterialTheme.typography.bodyMedium.scaled(), fontWeight = FontWeight.ExtraBold, color = Color(0xFF0B5960), modifier = Modifier
                        .padding(top = AppDimens.Dimens4)
                        .alpha(blinkAlpha)
                )
            }


            KidsLabel(
                text = "Time : " + uiState.elapsedSeconds.secToTimeFormat(), modifier = Modifier.align(alignment = Alignment.CenterEnd)
            )
        }
        Box(modifier = Modifier.fillMaxSize()) {
            ExamQuestionSection(
                uiState = uiState, viewModel = viewModel, modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    if (uiState.isLoading || loginUiState.isLoading) {
        Loader()
    }

    AnimatedVisibility(
        visible = uiState.isShowCompletePopup, enter = fadeIn(), exit = fadeOut()
    ) {
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

    AnimatedVisibility(
        visible = uiState.isLeavePage, enter = fadeIn(), exit = fadeOut()
    ) {
        CustomPopupView(
            title = stringResource(R.string.leave_exam_alert),
            description = stringResource(R.string.leave_exam_msg),
            positiveButtonText = stringResource(R.string.yes_i_m_sure),
            negativeButtonText = stringResource(R.string.no_please_continue),
            icon = R.drawable.ic_alert,
            widthMultiplier = 0.5f,
            onPositiveTapped = onBackClick,
            onNegativeTapped = { viewModel.resumeExam() })
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
            onPositiveTapped = { viewModel.completeExam(false) },
            onNegativeTapped = onBackClick
        )
    }
}
