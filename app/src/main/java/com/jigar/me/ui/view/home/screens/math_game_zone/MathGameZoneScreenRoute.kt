package com.jigar.me.ui.view.home.screens.math_game_zone

import androidx.compose.runtime.Composable
import com.jigar.me.ui.view.jetpack.fragments.game_zone.GameCategoryType
import com.jigar.me.ui.view.jetpack.fragments.game_zone.MathGameZoneScreen

@Composable
fun MathGameZoneScreenRoute(
    onBackClick: () -> Unit,
    onNavigateToNumberSequencePuzzle: () -> Unit,
    onNavigateToSudoku: () -> Unit,
    onNavigateToMathPyramid: () -> Unit,
    onNavigateToTargetNumber: () -> Unit,
) {
    MathGameZoneScreen(
        gameType = { type ->
            when (type) {
                GameCategoryType.NUMBER_SEQUENCE_PUZZLE -> onNavigateToNumberSequencePuzzle()
                GameCategoryType.SUDOKU -> onNavigateToSudoku()
                GameCategoryType.MATH_PYRAMID -> onNavigateToMathPyramid()
                GameCategoryType.TARGET_NUMBER -> onNavigateToTargetNumber()
            }
        },
        onBackClick = onBackClick
    )
}
