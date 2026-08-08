package com.jigar.me.ui.view.home.screens.math_game_zone

import androidx.compose.runtime.Composable
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager

@Composable
fun MathGameZoneScreenRoute(
    onBackClick: () -> Unit,
    onNavigateToNumberSequencePuzzle: () -> Unit,
    onNavigateToSudoku: () -> Unit,
    onNavigateToMathPyramid: () -> Unit,
    onNavigateToTargetNumber: () -> Unit,
    onNavigateToBalloonPop: () -> Unit,
    onNavigateToSpeedCompare: () -> Unit,
    onNavigateToMissingOperator: () -> Unit,
    onNavigateToMagicSquare: () -> Unit,
    onNavigateToCalcudoku: () -> Unit,
    onNavigateToMerge2048: () -> Unit,
    onNavigateToEquationMatch: () -> Unit,
    onNavigateToCrossMath: () -> Unit,
) {
    MathGameZoneScreen(
        gameType = { type ->
            AudioPlayerManager.playSoundBtnClick()
            when (type) {
                GameCategoryType.NUMBER_SEQUENCE_PUZZLE -> onNavigateToNumberSequencePuzzle()
                GameCategoryType.SUDOKU -> onNavigateToSudoku()
                GameCategoryType.MATH_PYRAMID -> onNavigateToMathPyramid()
                GameCategoryType.TARGET_NUMBER -> onNavigateToTargetNumber()
                GameCategoryType.BALLOON_POP -> onNavigateToBalloonPop()
                GameCategoryType.SPEED_COMPARE -> onNavigateToSpeedCompare()
                GameCategoryType.MISSING_OPERATOR -> onNavigateToMissingOperator()
                GameCategoryType.MAGIC_SQUARE -> onNavigateToMagicSquare()
                GameCategoryType.CALCUDOKU -> onNavigateToCalcudoku()
                GameCategoryType.MERGE_2048 -> onNavigateToMerge2048()
                GameCategoryType.EQUATION_MATCH -> onNavigateToEquationMatch()
                GameCategoryType.CROSS_MATH -> onNavigateToCrossMath()
            }
        },
        onBackClick = onBackClick
    )
}
