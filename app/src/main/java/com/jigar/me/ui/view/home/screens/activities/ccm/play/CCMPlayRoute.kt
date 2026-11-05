package com.jigar.me.ui.view.home.screens.activities.ccm.play

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
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
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbProgressBar
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.base.abacus_base.AbacusTheme
import com.jigar.me.ui.view.home.common_ui.Loader
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.common_ui.dialogs.PopupTheme
import com.jigar.me.ui.view.home.common_ui.sheets.ReviewGateHost
import com.jigar.me.ui.view.home.common_ui.sheets.rememberReviewGateController
import com.jigar.me.ui.view.ab.AbFitOnPhone
import com.jigar.me.ui.view.home.screens.activities.ActivityTopBar
import com.jigar.me.ui.view.home.screens.activities.ccm.play.components.AnswerSection
import com.jigar.me.ui.view.home.screens.activities.ccm.play.components.CCMCompleteBottomSheetCompose
import com.jigar.me.ui.view.home.screens.activities.ccm.play.components.QuestionSection
import com.jigar.me.ui.view.home.screens.activities.ccm.play.viewmodels.CCMPlayViewModel
import com.jigar.me.ui.view.home.screens.activities.exercise.components.UseWhichHandTextUi
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType

@Composable
fun CCMPlayRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: CCMPlayViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val prefs = LocalPreferencesHelper.current
    val reviewGate = rememberReviewGateController()

    LaunchedEffect(viewModel.abacusCalc.stateVersion) {
        viewModel.handleMatch()
    }

    AbTheme {
        val isTablet = LocalAbScale.current.isTablet
        val s = LocalAbScale.current.factor
        Box(modifier = Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Meadow, Modifier.fillMaxSize())

            Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
                if (uiState.isQuestionPhase) {
                    QuestionSection(uiState = uiState)
                } else {
                    // the abacus block shrinks to the room between the header and the instruction line (on a phone)
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(
                                start = DeviceInfo.screenHorizontalPadding(),
                                end = AppDimens.Dimens16,
                                // the header is about this tall on a phone (top gap + back button + its edge): the abacus and its
                                // answer bar start below it, never touching it
                                top = if (isTablet) 0.dp else DeviceInfo.screenTopPadding() + 44.dp + 14.dp,
                                bottom = if (isTablet) 0.dp else 8.dp,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        AbFitOnPhone(stretchWidth = true) {
                            AnswerSection(uiState = uiState, viewModel = viewModel)
                        }
                    }
                }

                ActivityTopBar(
                    title = stringResource(R.string.custom_challenge_mode),
                    onBack = onBackClick,
                ) {
                    // how far through the challenge the child is
                    if (uiState.isQuestionPhase && uiState.questionList.isNotEmpty()) {
                        AbProgressBar(
                            filled = minOf(uiState.currentIndex + 1, uiState.questionList.size),
                            total = uiState.questionList.size,
                            modifier = Modifier.widthIn(max = 220.dp * s),
                        )
                    }
                }
            }
        }

        if (uiState.isLoading) {
            Loader()
        }

        if (uiState.isShowCompletePopup) {
            CCMCompleteBottomSheetCompose(
                uiState = uiState,
                onContinue = {
                    viewModel.dismissCompletePopup()
                    viewModel.generateChallenge()
                },
                onClose = {
                    viewModel.dismissCompletePopup()
                    if (uiState.isAnswerTrue) reviewGate.attempt(prefs) { onBackClick() } else onBackClick()
                }
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
                onPositiveTapped = { viewModel.submitAnswer(false) },
                onNegativeTapped = onBackClick
            )
        }

        ReviewGateHost(reviewGate, prefs)
    }
}
