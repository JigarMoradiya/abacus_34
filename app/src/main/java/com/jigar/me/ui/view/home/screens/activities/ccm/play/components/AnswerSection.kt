package com.jigar.me.ui.view.home.screens.activities.ccm.play.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.*
import com.jigar.me.R
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.screens.activities.ccm.play.viewmodels.CCMPlayUiState
import com.jigar.me.ui.view.home.screens.activities.ccm.play.viewmodels.CCMPlayViewModel
import com.jigar.me.utils.AppConstants

@Composable
fun AnswerSection(
    uiState: CCMPlayUiState, viewModel: CCMPlayViewModel
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (uiState.isAbacusOnLeftHand) {
            CCMAbacusRow(viewModel)
            Spacer(Modifier.weight(1f))
            Text(
                text = stringResource(R.string.or),
                style = MaterialTheme.typography.titleSmall.copy(color = Color.Black, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))),
            )
            Spacer(Modifier.weight(1f))
            CCMKeyboardSection(uiState = uiState, viewModel = viewModel)
            Spacer(Modifier.weight(1f))
        } else {
            Spacer(Modifier.weight(1f))
            CCMKeyboardSection(uiState = uiState, viewModel = viewModel)
            Spacer(Modifier.weight(1f))
            Text(
                text = stringResource(R.string.or),
                style = MaterialTheme.typography.titleSmall.copy(color = Color.Black, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))),
            )
            Spacer(Modifier.weight(1f))
            CCMAbacusRow(viewModel)
        }

    }
}

@Composable
fun CCMAbacusRow(viewModel: CCMPlayViewModel) {
    Box {
        AbacusWithDecimalCanvas(
            selectedTheme = viewModel.selectedTheme,
            screenType = AppConstants.AbacusScreen.screenTypeCCM,
            isBeadSoundOn = viewModel.isBeadSoundEnabled,
            isDisplayCurrentAbacusInput = viewModel.isDisplayCurrentAbacusInput,
            abacusData = viewModel.abacusCalc,
            numberOfColumns = 7,
            rodMovement = viewModel.rodMovements,
            showDirectionHint = viewModel.showDirectionHints,
            isNextButtonEnable = true,
            onRodMovementChange = { viewModel.updateRodMovements(it) },
            onShowDirectionHintsChange = { viewModel.updateShowDirectionHints(it) },
            onShowHighlighterChange = {},
            onReset = {},
            onNext = {
                viewModel.submitAnswer()
            })
    }
}
