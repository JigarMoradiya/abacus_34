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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
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
import com.jigar.me.ui.view.jetpack.abacus_base.components.withcanvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.jetpack.fragments.abacus_free_mode.viewmodel.AbacusFreeModeViewModel
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.components.subitems.AbacusFormulaItem
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.components.AddSubAbacusItem
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.components.NumberAbacusItem
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.components.subitems.SetTimer
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.viewmodels.AbacusDoPracticeViewModel
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText
import com.jigar.me.ui.view.jetpack.fragments.common.Loader
import com.jigar.me.ui.view.jetpack.fragments.common.dialogs.CustomPopupView
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

                MaterialTheme {
                    Column(modifier = Modifier.Companion.fillMaxSize()) {
                        Row(
                            modifier = Modifier.Companion.fillMaxWidth(),
                            verticalAlignment = Alignment.Companion.CenterVertically
                        ) {
                            BackButtonWithText(title = "Abacus No : ${(uiState.currentIndexOfAbacus + 1)}", onBackClick = { onBack() })
                            Spacer(modifier = Modifier.Companion.weight(1f))
                            SetTimer(uiState)
                        }

                        Spacer(Modifier.Companion.weight(1f))

                        Row(verticalAlignment = Alignment.CenterVertically){
                            AbacusWithDecimalCanvas(
                                selectedTheme = viewModel.selectedTheme,
                                screenType = AppConstants.AbacusScreen.screenTypeAbacusPractice,
                                isBeadSoundOn = viewModel.isBeadSoundEnabled,
                                isDisplayCurrentAbacusInput = viewModel.isDisplayCurrentAbacusInput,
                                abacusData = viewModel.abacusCalc,
                                numberOfColumns = AbacusFreeModeViewModel.Companion.COLUMNS,
                                rodMovement = viewModel.rodMovements,
                                showDirectionHint = viewModel.showDirectionHints,
                                abacusType = uiState.setDetail?.answer_setting,
                                isNextButtonEnable = uiState.isNextButtonEnable,
                                onRodMovementChange = { viewModel.updateRodMovements(it) },
                                onShowDirectionHintsChange = { viewModel.updateShowDirectionHints(it) },
                                onShowHighlighterChange = {},
                                onReset = {
                                    viewModel.resetAbacus()
                                },onNext = {
                                    viewModel.goToNextAbacus()
                                }
                            )

                            uiState.currentAbacus?.let {
                                if (uiState.currentAbacusType == AppConstants.extras_Comman.AbacusTypeNumber) {
                                    NumberAbacusItem(it,modifier = Modifier.weight(1f))
                                }else if (uiState.currentAbacusType == AppConstants.extras_Comman.AbacusTypeAdditionSubtraction) {
                                    Spacer(Modifier.Companion.weight(1f))
                                    AbacusFormulaItem(uiState)
                                    AddSubAbacusItem(uiState)
                                }
                            }
                        }

                        Spacer(Modifier.Companion.weight(1f))
                    }
                }

                // loader
                if (uiState.isLoading) {
                    Loader()
                }
                AnimatedVisibility(
                    visible = uiState.isShowCompletePopup,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
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

    private fun onBack() {
        findNavController().popBackStack()
    }
    override fun onStop() {
        super.onStop()
        viewModel.pauseSetTimer()
        viewModel.persistSetTime()
    }
}