package com.jigar.me.ui.view.jetpack.fragments.exercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.core.presentation.theme.AbacusTheme
import com.jigar.me.ui.view.jetpack.fragments.common.Loader
import com.jigar.me.ui.view.jetpack.fragments.common.dialogs.CustomPopupView
import com.jigar.me.ui.view.jetpack.fragments.exercise.components.ExerciseAbacusRow
import com.jigar.me.ui.view.jetpack.fragments.results.components.ExerciseCompleteResultDialog
import com.jigar.me.ui.view.jetpack.fragments.exercise.components.ExerciseScreen
import com.jigar.me.ui.view.jetpack.fragments.exercise.viewmodels.ExerciseViewModel
import com.jigar.me.utils.extensions.secToCountDown
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExerciseFragment : Fragment() {
    private val viewModel: ExerciseViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (uiState.isAbacusOnLeftHand){
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ExerciseAbacusRow(uiState, viewModel,Modifier.weight(1f)){
                                    onBackClick()
                                }
                                ExerciseScreen(uiState, viewModel){
                                    onBackClick()
                                }
                            }
                        }else{
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ExerciseScreen(uiState, viewModel){
                                    onBackClick()
                                }
                                ExerciseAbacusRow(uiState, viewModel, Modifier.fillMaxSize()){
                                    onBackClick()
                                }
                            }
                        }
                        // show timer when exercise start
                        if (uiState.isExerciseStarted) {
                            val alignment = if (uiState.isAbacusOnLeftHand) Alignment.TopEnd else Alignment.TopStart
                            val paddingStart  = if (uiState.isAbacusOnLeftHand) 0.dp else dimensionResource(R.dimen.exercise_width)
                            val paddingEnd  = if (uiState.isAbacusOnLeftHand) dimensionResource(R.dimen.exercise_width) else 0.dp
                            Row(
                                modifier = Modifier
                                    .height(dimensionResource(R.dimen.menu_icons_bg))
                                    .align(alignment)
                                    .padding(start = paddingStart, end = paddingEnd,
                                        top = dimensionResource(R.dimen.activity_padding12)),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                val paddingStart  = if (uiState.isAbacusOnLeftHand) 0.dp else dimensionResource(R.dimen.activity_padding24)
                                val paddingEnd  = if (uiState.isAbacusOnLeftHand) dimensionResource(R.dimen.activity_padding24) else 0.dp

                                Text(
                                    modifier = Modifier.padding(start = paddingStart, end = paddingEnd), text = "Time : ${uiState.elapsedSeconds.secToCountDown()}", style = MaterialTheme.typography.bodyLarge.copy(
                                        color = Color.Black, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))
                                    )
                                )
                            }
                        }

                    }

                    // loader
                    if (uiState.isLoading) {
                        Loader()
                    }

                    // exam complete popup
                    AnimatedVisibility(
                        visible = uiState.isShowCompletePopup, enter = fadeIn(), exit = fadeOut()
                    ) {
                        uiState.submitExamRequest?.let {
                            ExerciseCompleteResultDialog(it, onClose = {
                                viewModel.closeExercise()
                            }, onGiveAgain = {
                                viewModel.generateExercise()
                            })
                        }
                    }

                    // leave exam popup
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
                            onPositiveTapped = {
                                viewModel.closeExercise()
                            },
                            onNegativeTapped = {
                                viewModel.resumeExercise()
                            })
                    }

                    // no internet popup
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
                            onPositiveTapped = {
                                viewModel.completeExercise(false)
                            },
                            onNegativeTapped = {
                                onBack()
                            })
                    }
                }

            }
        }
    }

    fun onBackClick() {
        if (viewModel.uiState.value.isExerciseStarted){
            viewModel.onLeaveExercise()
        } else {
            onBack()
        }
    }

    fun onBack() {
        findNavController().popBackStack()
    }
}


