package com.jigar.me.ui.view.jetpack.fragments.activities.exam.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import com.jigar.me.ui.view.jetpack.fragments.activities.exam.play.components.ExamHeader
import com.jigar.me.ui.view.jetpack.fragments.activities.exam.play.components.ExamQuestionSection
import com.jigar.me.ui.view.jetpack.fragments.activities.exam.play.viewmodels.ExamPlayViewModel
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText
import com.jigar.me.ui.view.jetpack.fragments.common.Loader
import com.jigar.me.ui.view.jetpack.fragments.common.dialogs.CustomPopupView
import com.jigar.me.ui.view.jetpack.fragments.results.components.ExamCompleteResultDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExamPlayFragment : Fragment() {
    private val viewModel: ExamPlayViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                AbacusTheme {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row {
                            BackButtonWithText(title = stringResource(R.string.math_exam), onBackClick = {
                                onBackClick()
                            })
                            Spacer(Modifier.weight(1f))
                            ExamHeader(uiState, elapsedSeconds = uiState.elapsedSeconds)
                        }
                        Box(modifier = Modifier.fillMaxSize()) {
                            ExamQuestionSection(
                                uiState = uiState,
                                viewModel = viewModel,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    // loader
                    if (uiState.isLoading) {
                        Loader()
                    }

                    // exam complete popup
                    AnimatedVisibility(
                        visible = uiState.isShowCompletePopup,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        uiState.submitExamRequest?.let{
                            ExamCompleteResultDialog(it, onClose = {
                                onBack()
                            }, onGiveAgain = {
                                viewModel.reGenerateExam()
                            })
                        }
                    }

                    // leave exam popup
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
                            onPositiveTapped = {
                                onBack()
                            },
                            onNegativeTapped = {
                                viewModel.resumeExam()
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
                                viewModel.completeExam(false)
                            },
                            onNegativeTapped = {
                                onBack()
                            }
                        )
                    }
                }

            }
        }
    }

    fun onBackClick() {
        viewModel.onLeaveExam()
    }

    fun onBack(){
        findNavController().popBackStack()
    }

}
