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
    onNavigateToNumberPath: () -> Unit,
    onNavigateToTrueFalse: () -> Unit,
    onNavigateToPlaceValue: () -> Unit,
    onNavigateToClockMaster: () -> Unit,
    onNavigateToMathBingo: () -> Unit,
    onNavigateToKakuro: () -> Unit,
    onNavigateToTrophyRoom: () -> Unit,
) {
    MathGameZoneScreen(
        onTrophyRoom = {
            AudioPlayerManager.playSoundBtnClick()
            onNavigateToTrophyRoom()
        },
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
                GameCategoryType.NUMBER_PATH -> onNavigateToNumberPath()
                GameCategoryType.TRUE_FALSE -> onNavigateToTrueFalse()
                GameCategoryType.PLACE_VALUE -> onNavigateToPlaceValue()
                GameCategoryType.CLOCK_MASTER -> onNavigateToClockMaster()
                GameCategoryType.MATH_BINGO -> onNavigateToMathBingo()
                GameCategoryType.KAKURO -> onNavigateToKakuro()
            }
        },
        onBackClick = onBackClick
    )
}
