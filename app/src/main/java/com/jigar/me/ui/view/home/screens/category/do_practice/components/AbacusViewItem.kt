package com.jigar.me.ui.view.home.screens.category.do_practice.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.screens.category.do_practice.viewmodels.AbacusDoPracticeUiState
import com.jigar.me.ui.view.home.screens.category.do_practice.viewmodels.AbacusDoPracticeViewModel
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
            numberOfColumns = 13,
            rodMovement = viewModel.rodMovements,
            showDirectionHint = viewModel.showDirectionHints,
            abacusType = uiState.setDetail?.answer_setting,
            questionType = uiState.currentAbacusType,
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