package com.jigar.me.ui.view.jetpack.fragments.activities.custom_challenge.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.core.presentation.theme.AbacusTheme
import com.jigar.me.ui.view.jetpack.fragments.activities.custom_challenge.play.components.AnswerSection
import com.jigar.me.ui.view.jetpack.fragments.activities.custom_challenge.play.components.CCMCompleteBottomSheetCompose
import com.jigar.me.ui.view.jetpack.fragments.activities.custom_challenge.play.components.QuestionSection
import com.jigar.me.ui.view.jetpack.fragments.activities.custom_challenge.play.viewmodels.CCMPlayViewModel
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText
import com.jigar.me.ui.view.jetpack.fragments.common.Loader
import com.jigar.me.ui.view.jetpack.fragments.common.dialogs.CustomPopupView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CCMPlayFragment : Fragment() {
    private val viewModel: CCMPlayViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                // HANDLE ABACUS MOVEMENT
                LaunchedEffect(viewModel.abacusCalc.stateVersion) {
                    viewModel.handleMatch()
                }
                AbacusTheme {
                    Column(modifier = Modifier.fillMaxSize()) {
                        BackButtonWithText(title = stringResource(R.string.custom_challenge_mode), onBackClick = { onBack() })
                        if (uiState.isQuestionPhase) {
                            QuestionSection(uiState = uiState)
                        } else {
                            Spacer(Modifier.weight(1f))
                            AnswerSection(uiState = uiState,viewModel)
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
                // loader
                if (uiState.isLoading) {
                    Loader()
                }

                // set complete popup
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
                            findNavController().popBackStack()
                        }
                    )
                }

                // no internet popup
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
                        onPositiveTapped = {
                            viewModel.submitAnswer(false)
                        },
                        onNegativeTapped = {
                            onBack()
                        }
                    )
                }

            }
        }
    }

    fun onBack(){
        findNavController().popBackStack()
    }
}
