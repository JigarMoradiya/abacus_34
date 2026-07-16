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
            }
        },
        onBackClick = onBackClick
    )
}
