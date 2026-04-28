package com.jigar.me.ui.view.home.screens.activities

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.home.screens.activities.ccm.play.components.AnswerSection
import com.jigar.me.ui.view.home.screens.activities.ccm.play.components.CCMCompleteBottomSheetCompose
import com.jigar.me.ui.view.home.screens.activities.ccm.play.components.QuestionSection
import com.jigar.me.ui.view.home.screens.activities.ccm.play.viewmodels.CCMPlayViewModel
import com.jigar.me.ui.view.home.common.BackButtonWithText
import com.jigar.me.ui.view.home.common.Loader
import com.jigar.me.ui.view.home.common.dialogs.CustomPopupView

@Composable
fun CCMPlayRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: CCMPlayViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.abacusCalc.stateVersion) {
        viewModel.handleMatch()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        BackButtonWithText(
            title = stringResource(R.string.custom_challenge_mode),
            onBackClick = onBackClick
        )
        if (uiState.isQuestionPhase) {
            QuestionSection(uiState = uiState)
        } else {
            Spacer(Modifier.weight(1f))
            AnswerSection(uiState = uiState, viewModel)
            Spacer(Modifier.weight(1f))
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
        CCMCompleteBottomSheetCompose(
            uiState = uiState,
            onContinue = {
                viewModel.dismissCompletePopup()
                viewModel.generateChallenge()
            },
            onClose = {
                viewModel.dismissCompletePopup()
                onBackClick()
            }
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
            onPositiveTapped = { viewModel.submitAnswer(false) },
            onNegativeTapped = onBackClick
        )
    }
}
