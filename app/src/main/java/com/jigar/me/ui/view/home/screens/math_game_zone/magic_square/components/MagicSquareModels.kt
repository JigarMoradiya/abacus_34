package com.jigar.me.ui.view.home.screens.math_game_zone.magic_square.components

import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4

data class MagicSquareConfig(
    val emptyCount: Int,          // how many cells the kid must fill
    val requireDiagonals: Boolean, // Hard / Very Hard also need both diagonals
    val timerSeconds: Int?,       // null → no timer (puzzle-goal mode)
    val puzzlesGoal: Int?         // Easy only
) {
    val target = 15               // 3×3 magic square with 1–9

    companion object {
        fun forDifficulty(difficulty: CommonDifficulty4): MagicSquareConfig = when (difficulty) {
            CommonDifficulty4.easy ->
                MagicSquareConfig(emptyCount = 3, requireDiagonals = false, timerSeconds = null, puzzlesGoal = 5)
            CommonDifficulty4.medium ->
                MagicSquareConfig(emptyCount = 4, requireDiagonals = false, timerSeconds = 90, puzzlesGoal = null)
            CommonDifficulty4.hard ->
                MagicSquareConfig(emptyCount = 5, requireDiagonals = true, timerSeconds = 60, puzzlesGoal = null)
            CommonDifficulty4.veryHard ->
                MagicSquareConfig(emptyCount = 6, requireDiagonals = true, timerSeconds = 60, puzzlesGoal = null)
        }
    }
}

data class MagicSquareUiState(
    val grid: List<Int> = List(9) { 0 },       // 0 = empty
    val fixed: List<Boolean> = List(9) { true },
    val palette: List<Int> = emptyList(),
    val selectedIndex: Int? = null,
    val puzzlesSolved: Int = 0,
    val score: Int = 0,
    val streak: Int = 0,
    val timeLeft: Int = 0,
    val justSolved: Boolean = false,
    val isGameOver: Boolean = false
)
