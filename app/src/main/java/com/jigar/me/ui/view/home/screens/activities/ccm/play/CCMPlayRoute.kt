package com.jigar.me.ui.view.home.screens.activities.ccm.play

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.screens.activities.ccm.play.components.AnswerSection
import com.jigar.me.ui.view.home.screens.activities.ccm.play.components.CCMCompleteBottomSheetCompose
import com.jigar.me.ui.view.home.screens.activities.ccm.play.components.QuestionSection
import com.jigar.me.ui.view.home.screens.activities.ccm.play.viewmodels.CCMPlayViewModel
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.Loader
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.sheets.ReviewGateHost
import com.jigar.me.ui.view.home.common_ui.sheets.rememberReviewGateController
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.common_ui.dialogs.PopupTheme
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

    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
       Row(verticalAlignment = Alignment.CenterVertically){
           BackButtonWithText(
               title = stringResource(R.string.custom_challenge_mode),
               onBackClick = onBackClick
           )

           if (!uiState.isQuestionPhase) {
               Spacer(modifier = Modifier.weight(1f))
               Text(
                   text = stringResource(R.string.set_your_answer),
                   style = MaterialTheme.typography.titleLarge.scaled().copy(color = Color.DarkGray, fontWeight = FontWeight.SemiBold,fontFamily = FontFamily(Font(R.font.font_semibold))),
               )
               Spacer(modifier = Modifier.weight(1f))
           }
       }
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
                if (uiState.isAnswerTrue) reviewGate.attempt(prefs) { onBackClick() } else onBackClick()
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
            theme = PopupTheme.CONFIRM,
            accent = ButtonType.RED,
            widthMultiplier = 0.7f,
            onPositiveTapped = { viewModel.submitAnswer(false) },
            onNegativeTapped = onBackClick
        )
    }

    ReviewGateHost(reviewGate, prefs)
}
