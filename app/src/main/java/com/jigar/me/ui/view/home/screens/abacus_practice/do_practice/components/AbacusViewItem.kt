package com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.ab.AbacusFrameStyle
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.viewmodels.AbacusDoPracticeUiState
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.viewmodels.AbacusDoPracticeViewModel
import com.jigar.me.utils.AppConstants


@Composable
fun AbacusViewItem(viewModel : AbacusDoPracticeViewModel, uiState: AbacusDoPracticeUiState) {
    // the toy frame's answer bar hangs about 70 dp (x the kit scale) above the frame: leave room for it
    Box(Modifier.padding(top = 70.dp * LocalAbScale.current.factor)) {
        AbacusWithDecimalCanvas(
            selectedTheme = viewModel.selectedTheme,
            screenType = AppConstants.AbacusScreen.screenTypeAbacusPractice,
            isBeadSoundOn = viewModel.isBeadSoundEnabled,
            isDisplayCurrentAbacusInput = viewModel.isDisplayCurrentAbacusInput,
            abacusData = viewModel.abacusCalc,
            numberOfColumns = viewModel.currentNumberOfColumns,
            rodMovement = viewModel.rodMovements,
            showDirectionHint = viewModel.showDirectionHints,
            abacusType = uiState.setDetail?.answer_setting,
            questionType = uiState.currentAbacusType,
            frameStyle = AbacusFrameStyle.Toy,
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
