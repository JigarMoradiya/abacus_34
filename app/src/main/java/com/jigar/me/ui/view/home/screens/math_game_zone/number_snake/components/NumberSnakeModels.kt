package com.jigar.me.ui.view.home.screens.math_game_zone.number_snake.components

import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4

// A grid where numbers 1..size*size form an unbroken trail: every number k
// sits orthogonally next to k+1. Some cells are given as clues; the rest
// come from a tray of the missing numbers.
data class NumberSnakePuzzle(
    val size: Int,
    val given: List<Int>,     // size*size, 0 = blank
    val solution: List<Int>   // size*size, values 1..size*size
)

data class NumberSnakeConfig(
    val size: Int,
    val density: Float,     // fraction of cells given as clues
    val parSeconds: Int,    // "good" solve time — drives stars + speed bonus
    val hintLimit: Int
) {
    companion object {
        fun forDifficulty(difficulty: CommonDifficulty4): NumberSnakeConfig = when (difficulty) {
            CommonDifficulty4.easy -> NumberSnakeConfig(4, 0.55f, 60, 3)
            CommonDifficulty4.medium -> NumberSnakeConfig(5, 0.45f, 100, 3)
            CommonDifficulty4.hard -> NumberSnakeConfig(5, 0.32f, 130, 2)
            else -> NumberSnakeConfig(6, 0.28f, 170, 2)
        }

        // hintsUsed==0 && within par -> 3; hintsUsed<=1 && within 2x par -> 2; else 1.
        fun stars(hintsUsed: Int, elapsedSeconds: Int, parSeconds: Int): Int = when {
            hintsUsed == 0 && elapsedSeconds <= parSeconds -> 3
            hintsUsed <= 1 && elapsedSeconds <= parSeconds * 2 -> 2
            else -> 1
        }
    }
}

data class NumberSnakeUiState(
    val size: Int = 4,
    val grid: List<Int> = List(16) { 0 },       // current filled state, 0 = empty
    val given: List<Int> = List(16) { 0 },      // 0 = blank cell, else fixed clue
    val solution: List<Int> = List(16) { 0 },
    val tray: List<Int> = emptyList(),          // numbers not yet placed
    val selectedIndex: Int? = null,
    val hintLocked: Set<Int> = emptySet(),
    val hintsLeft: Int = 3,
    val hintsUsed: Int = 0,
    val wrongFlash: Boolean = false,
    val score: Int = 0,
    val elapsed: Int = 0,
    val lastSpeedBonus: Int = 0,
    val justSolved: Boolean = false,
    val isGameOver: Boolean = false
)
