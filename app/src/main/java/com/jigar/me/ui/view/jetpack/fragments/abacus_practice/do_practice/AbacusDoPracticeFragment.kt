package com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.core.presentation.theme.AbacusTheme
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.components.AbacusViewItem
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.components.AddSubAbacusItem
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.components.DivisionAbacusItem
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.components.MultiplicationAbacusItem
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.components.NumberAbacusItem
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.components.subitems.AbacusFormulaItem
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.components.subitems.SetTimer
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.components.subitems.UseWhichHandTextUi
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.viewmodels.AbacusDoPracticeViewModel
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText
import com.jigar.me.ui.view.jetpack.fragments.common.Loader
import com.jigar.me.ui.view.jetpack.fragments.common.dialogs.CustomPopupView
import com.jigar.me.ui.view.jetpack.fragments.reports.dialogs.ExerciseExamCompleteResultDialog
import com.jigar.me.utils.AppConstants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AbacusDoPracticeFragment : Fragment() {
    private val viewModel: AbacusDoPracticeViewModel by viewModels()

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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BackButtonWithText(title = "Abacus No : ${(uiState.currentIndexOfAbacus + 1)}", onBackClick = { onBack() })
                            Spacer(modifier = Modifier.weight(1f))
                            SetTimer(uiState)
                        }

                        Spacer(Modifier.weight(1f))

                        Row(verticalAlignment = Alignment.CenterVertically){
                            if (viewModel.isAbacusOnLeftHand){
                                AbacusViewItem(viewModel,uiState)
                                uiState.currentAbacus?.let {
                                    when (uiState.currentAbacusType) {
                                        AppConstants.extras_Comman.AbacusTypeNumber -> {
                                            NumberAbacusItem(it,modifier = Modifier.weight(1f))
                                        }
                                        AppConstants.extras_Comman.AbacusTypeAdditionSubtraction -> {
                                            Spacer(Modifier.weight(1f))
                                            AbacusFormulaItem(uiState)
                                            AddSubAbacusItem(uiState)
                                        }
                                        AppConstants.extras_Comman.AbacusTypeMultiplication -> {
                                            Spacer(Modifier.weight(1f))
                                            AbacusFormulaItem(uiState)
                                            MultiplicationAbacusItem(uiState)
                                        }
                                        AppConstants.extras_Comman.AbacusTypeDivision -> {
                                            Spacer(Modifier.weight(1f))
                                            AbacusFormulaItem(uiState)
                                            DivisionAbacusItem(uiState)
                                        }
                                    }
                                }
                            }else{
                                uiState.currentAbacus?.let {
                                    when (uiState.currentAbacusType) {
                                        AppConstants.extras_Comman.AbacusTypeNumber -> {
                                            NumberAbacusItem(it,modifier = Modifier.weight(1f))
                                        }
                                        AppConstants.extras_Comman.AbacusTypeAdditionSubtraction -> {
                                            AddSubAbacusItem(uiState)
                                            AbacusFormulaItem(uiState)
                                            Spacer(Modifier.weight(1f))
                                        }
                                        AppConstants.extras_Comman.AbacusTypeMultiplication -> {
                                            MultiplicationAbacusItem(uiState)
                                            AbacusFormulaItem(uiState)
                                            Spacer(Modifier.weight(1f))
                                        }
                                        AppConstants.extras_Comman.AbacusTypeDivision -> {
                                            DivisionAbacusItem(uiState)
                                            AbacusFormulaItem(uiState)
                                            Spacer(Modifier.weight(1f))
                                        }
                                    }
                                }
                                AbacusViewItem(viewModel,uiState)
                                Spacer(Modifier.width(dimensionResource(R.dimen.activity_padding4)))
                            }
                        }

                        Spacer(Modifier.weight(1f))
                    }
                }
                // use left or right hand text
                UseWhichHandTextUi(uiState,viewModel)

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
                    if (uiState.isShowSubmitAnswer == true){
                        uiState.submitExerciseRequest?.let {
                            ExerciseExamCompleteResultDialog(selectedTheme = viewModel.selectedTheme,
                                it, onClose = {
                                    onBack()
                                }, onGiveAgain = {
                                    onBack()
                                },
                            )
                        }
                    }else{
                        CustomPopupView(
                            title = stringResource(R.string.congratulations),
                            description = stringResource(R.string.txt_set_completed_msg),
                            positiveButtonText = stringResource(R.string.ok_thanks),
                            negativeButtonText = stringResource(R.string.close),
                            icon = R.drawable.ic_alert_complete_page,
                            widthMultiplier = 0.5f,
                            onPositiveTapped = {
                                onBack()
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

    private fun onBack() {
        findNavController().popBackStack()
    }
    override fun onStop() {
        super.onStop()
        viewModel.pauseSetTimer()
        viewModel.persistSetTime()
    }
}