package com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.components

import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4

enum class Comparator(val symbol: String) {
    LESS("<"), EQUAL("="), GREATER(">")
}

data class CompareOperand(
    val display: String,   // "7" or "7+5"
    val value: Int         // 7 or 12
)

data class CompareRound(
    val left: CompareOperand,
    val right: CompareOperand
) {
    val truth: Comparator
        get() = when {
            left.value < right.value -> Comparator.LESS
            left.value > right.value -> Comparator.GREATER
            else -> Comparator.EQUAL
        }
}

data class SpeedCompareConfig(
    val allowSums: Boolean,    // false = single numbers only (Easy)
    val bothSums: Boolean,     // both sides are sums (Hard / Very Hard)
    val singleMax: Int,        // max value for a single-number operand
    val addendMax: Int,        // max value per addend in a sum
    val timerSeconds: Int?,    // null → no timer (round-goal mode)
    val roundsGoal: Int?,      // Easy only
    val equalChance: Double    // how often left == right in value
) {
    companion object {
        fun forDifficulty(difficulty: CommonDifficulty4): SpeedCompareConfig = when (difficulty) {
            CommonDifficulty4.easy ->
                SpeedCompareConfig(false, false, 9, 9, null, 15, 0.25)
            CommonDifficulty4.medium ->
                SpeedCompareConfig(true, false, 20, 9, 90, null, 0.25)
            CommonDifficulty4.hard ->
                SpeedCompareConfig(true, true, 50, 20, 60, null, 0.20)
            CommonDifficulty4.veryHard ->
                SpeedCompareConfig(true, true, 99, 50, 60, null, 0.20)
        }
    }
}

data class SpeedCompareUiState(
    val round: CompareRound,
    val revealed: Boolean = false,
    val chosen: Comparator? = null,
    val lastCorrect: Boolean = false,
    val score: Int = 0,
    val streak: Int = 0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val roundsPlayed: Int = 0,
    val timeLeft: Int = 0,
    val isGameOver: Boolean = false
)
