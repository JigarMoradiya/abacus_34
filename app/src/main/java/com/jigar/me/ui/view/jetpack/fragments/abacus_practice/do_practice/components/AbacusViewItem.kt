package com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import com.jigar.me.ui.view.jetpack.abacus_base.components.withcanvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.jetpack.fragments.abacus_free_mode.viewmodel.AbacusFreeModeViewModel
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.viewmodels.AbacusDoPracticeUiState
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.viewmodels.AbacusDoPracticeViewModel
import com.jigar.me.utils.AppConstants


@Composable
fun AbacusViewItem(viewModel : AbacusDoPracticeViewModel, uiState: AbacusDoPracticeUiState) {
    Box{
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
    }
}