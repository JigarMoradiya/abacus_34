package com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku.components

import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4

enum class CageOp(val symbol: String) {
    GIVEN(""), PLUS("+"), MINUS("−"), TIMES("×"), DIVIDE("÷")
}

data class Cage(val cells: List<Int>, val op: CageOp, val target: Int) {
    val clueIndex: Int get() = cells.min()
    val clueText: String get() = "$target${op.symbol}"
}

data class CalcudokuPuzzle(
    val size: Int,
    val solution: List<Int>,   // size*size, values 1..size
    val cages: List<Cage>,
    val cageId: List<Int>      // per-cell cage index
)

data class CalcudokuConfig(
    val size: Int,
    val allowedOps: List<CageOp>,
    val parSeconds: Int          // "good" solve time — drives stars + speed bonus
) {
    companion object {
        // One puzzle per session; count up and record the best (fastest) time.
        fun forDifficulty(difficulty: CommonDifficulty4): CalcudokuConfig = when (difficulty) {
            CommonDifficulty4.easy ->
                CalcudokuConfig(3, listOf(CageOp.PLUS), 45)
            CommonDifficulty4.medium ->
                CalcudokuConfig(4, listOf(CageOp.PLUS, CageOp.MINUS), 90)
            CommonDifficulty4.hard ->
                CalcudokuConfig(4, listOf(CageOp.PLUS, CageOp.MINUS, CageOp.TIMES), 110)
            CommonDifficulty4.veryHard ->
                CalcudokuConfig(5, listOf(CageOp.PLUS, CageOp.MINUS, CageOp.TIMES, CageOp.DIVIDE), 150)
        }
    }
}

data class CalcudokuUiState(
    val size: Int = 3,
    val grid: List<Int> = List(9) { 0 },
    val fixed: List<Boolean> = List(9) { false },
    val cageId: List<Int> = List(9) { 0 },
    val cages: List<Cage> = emptyList(),
    val selectedIndex: Int? = null,
    val puzzlesSolved: Int = 0,
    val score: Int = 0,
    val streak: Int = 0,
    val timeLeft: Int = 0,
    val elapsed: Int = 0,             // counts up in goal (Easy) mode, for the speed bonus
    val lastSpeedBonus: Int = 0,
    val justSolved: Boolean = false,
    val isGameOver: Boolean = false
)
