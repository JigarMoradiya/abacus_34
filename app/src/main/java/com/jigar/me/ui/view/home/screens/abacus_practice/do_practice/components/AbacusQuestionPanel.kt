package com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.ab.AbQuestionPanel
import com.jigar.me.ui.view.ab.LocalAbQuestionTextScale
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.subitems.AbacusFormulaItem
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.viewmodels.AbacusDoPracticeUiState
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.viewmodels.AbacusDoPracticeViewModel
import com.jigar.me.utils.AppConstants

/** SwiftUI `responsiveDimenstion`: a phone size grown by the iPad multipliers (1.5 / 1.7) on the two tablet classes. */
internal fun practiceResponsive(baseSize: Float): Float = baseSize * when {
    DeviceInfo.isLargeTablet -> 1.7f
    DeviceInfo.isTablet -> 1.5f
    else -> 1f
}

/**
 * The question side of the play screen: the chalkboard with the sum and, in step by step sets, the hint card, sized to the
 * room that is left beside the abacus. A 13-rod abacus leaves a narrow column, so the board keeps its natural size (it
 * only shrinks when a crowded hint needs the height) and only the hint may grow; a 7-rod abacus leaves much more room, so
 * both may grow (by 1.3 at most, as the height is the same).
 */
@Composable
fun AbacusQuestionPanel(
    viewModel: AbacusDoPracticeViewModel,
    uiState: AbacusDoPracticeUiState,
    topInset: Dp,
    bottomInset: Dp,
    modifier: Modifier = Modifier,
) {
    val abacus = uiState.currentAbacus ?: return
    val abacusOnLeft = viewModel.isAbacusOnLeftHand
    val isRoomy = viewModel.currentNumberOfColumns <= 9
    val maxScale = if (isRoomy) 1.3f else 1f

    if (uiState.currentAbacusType == AppConstants.extras_Comman.AbacusTypeNumber) {
        AbQuestionPanel(
            showsHint = false,
            modifier = modifier,
            boardMax = maxScale,
            hintMax = maxScale,
            sideBySide = isRoomy,
            packTrailing = abacusOnLeft,
            topInset = topInset,
            bottomInset = bottomInset,
            hint = {},
            board = { NumberAbacusItem(abacus) },
        )
    } else {
        // Step by step sets show the hint card with the question, so there the question text is smaller (the board
        // follows the text). Other sets are left as they are.
        val showsHint = viewModel.isDisplayHelpMessage && uiState.isStepByStep
        AbQuestionPanel(
            showsHint = showsHint,
            modifier = modifier,
            hintLeading = abacusOnLeft,
            boardMax = maxScale,
            hintMax = maxScale,
            sideBySide = isRoomy,
            packTrailing = abacusOnLeft,
            topInset = topInset,
            bottomInset = bottomInset,
            resetKey = uiState.currentIndexOfAbacus,
            hint = { AbacusFormulaItem(uiState) },
            board = {
                CompositionLocalProvider(LocalAbQuestionTextScale provides if (showsHint) 0.8f else 1f) {
                    when (uiState.currentAbacusType) {
                        AppConstants.extras_Comman.AbacusTypeAdditionSubtraction -> AddSubAbacusItem(uiState)
                        AppConstants.extras_Comman.AbacusTypeMultiplication -> MultiplicationAbacusItem(uiState)
                        AppConstants.extras_Comman.AbacusTypeDivision -> DivisionAbacusItem(uiState)
                    }
                }
            },
        )
    }
}
