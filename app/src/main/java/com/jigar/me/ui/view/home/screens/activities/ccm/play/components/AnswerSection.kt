package com.jigar.me.ui.view.home.screens.activities.ccm.play.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.ab.AbacusFrameStyle
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.base.abacus_base.AbacusTheme
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.screens.activities.ccm.play.viewmodels.CCMPlayUiState
import com.jigar.me.ui.view.home.screens.activities.ccm.play.viewmodels.CCMPlayViewModel
import com.jigar.me.ui.view.home.screens.activities.exercise.components.ExerciseHintBadges
import com.jigar.me.ui.view.home.screens.activities.exercise.components.UseWhichHandTextUi
import com.jigar.me.ui.view.home.screens.activities.rememberToyBarRoom
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants

/**
 * The answer screen, laid out like Exercise: on a phone the hint about the two ways to answer sits between the abacus
 * and the keypad; on a tablet it sits at the bottom, under the abacus.
 */
@Composable
fun AnswerSection(
    uiState: CCMPlayUiState, viewModel: CCMPlayViewModel
) {
    val isTablet = LocalAbScale.current.isTablet
    val highlight = AbacusTheme.colorPreset(viewModel.selectedTheme).buttonColor
    val onLeft = uiState.isAbacusOnLeftHand

    if (isTablet) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
            if (onLeft) {
                CCMAbacusColumn(viewModel, highlight, fromLeft = true)
                Spacer(Modifier.weight(2f))
                CCMKeyboardSection(uiState = uiState, viewModel = viewModel)
                Spacer(Modifier.weight(1f))
            } else {
                Spacer(Modifier.weight(1f))
                CCMKeyboardSection(uiState = uiState, viewModel = viewModel)
                Spacer(Modifier.weight(2f))
                CCMAbacusColumn(viewModel, highlight, fromLeft = false)
            }
        }
    } else {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            if (onLeft) {
                CCMAbacusRow(viewModel, fromLeft = true)
                CCMInstruction(highlight, Modifier.weight(1f))
                CCMKeyboardSection(uiState = uiState, viewModel = viewModel)
                Spacer(Modifier.weight(0.35f))
            } else {
                Spacer(Modifier.weight(0.35f))
                CCMKeyboardSection(uiState = uiState, viewModel = viewModel)
                CCMInstruction(highlight, Modifier.weight(1f))
                CCMAbacusRow(viewModel, fromLeft = false)
            }
        }
    }
}

/** On a phone: the two ways to answer drawn above the hint, between the abacus and the keypad. */
@Composable
private fun CCMInstruction(highlight: Color, modifier: Modifier = Modifier) {
    Column(
        modifier.padding(horizontal = AppDimens.Dimens12),
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ExerciseHintBadges(34.dp * LocalAbScale.current.factor)
        UseWhichHandTextUi(highlightColor = highlight)
    }
}

/** On a tablet: the abacus in the middle of the screen's height, the hint at the bottom under it. */
@Composable
private fun CCMAbacusColumn(viewModel: CCMPlayViewModel, highlight: Color, fromLeft: Boolean) {
    val s = LocalAbScale.current.factor
    Column(Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.weight(1f))
        CCMAbacusRow(viewModel, fromLeft)
        Spacer(Modifier.weight(1f))
        Row(
            Modifier.padding(bottom = AppDimens.Dimens16),
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ExerciseHintBadges(30.dp * s)
            UseWhichHandTextUi(highlightColor = highlight)
        }
    }
}

@Composable
fun CCMAbacusRow(viewModel: CCMPlayViewModel, fromLeft: Boolean = true) {
    // the toy answer bar hangs above the frame, so the box leaves room for it; the whole abacus is shown at 95%, from the
    // edge it sits against, so it keeps clear of the header
    Box(
        Modifier
            .padding(top = rememberToyBarRoom(AppConstants.AbacusScreen.screenTypeCCM))
            .graphicsLayer {
                scaleX = 0.95f
                scaleY = 0.95f
                transformOrigin = TransformOrigin(if (fromLeft) 0f else 1f, 0.5f)
            }
    ) {
        AbacusWithDecimalCanvas(
            selectedTheme = viewModel.selectedTheme,
            screenType = AppConstants.AbacusScreen.screenTypeCCM,
            isBeadSoundOn = viewModel.isBeadSoundEnabled,
            isDisplayCurrentAbacusInput = viewModel.isDisplayCurrentAbacusInput,
            abacusData = viewModel.abacusCalc,
            numberOfColumns = 7,
            rodMovement = viewModel.rodMovements,
            showDirectionHint = viewModel.showDirectionHints,
            frameStyle = AbacusFrameStyle.Toy,
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
