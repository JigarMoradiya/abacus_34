package com.jigar.me.ui.view.home.screens.math_game_zone.missing_operator.components

import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4

enum class MathOperator(val symbol: String) {
    PLUS("+"), MINUS("−"), TIMES("×"), DIVIDE("÷");

    // Returns null when the operation isn't a clean non-negative integer.
    fun apply(a: Int, b: Int): Int? = when (this) {
        PLUS -> a + b
        MINUS -> if (a >= b) a - b else null
        TIMES -> a * b
        DIVIDE -> if (b != 0 && a % b == 0) a / b else null
    }
}

data class MissingOpRound(
    val left: Int,
    val right: Int,
    val result: Int,
    val answer: MathOperator,
    val options: List<MathOperator>
)

data class MissingOperatorConfig(
    val operators: List<MathOperator>,
    val maxOperand: Int,
    val timerSeconds: Int?,
    val roundsGoal: Int?
) {
    companion object {
        fun forDifficulty(difficulty: CommonDifficulty4): MissingOperatorConfig = when (difficulty) {
            CommonDifficulty4.easy ->
                MissingOperatorConfig(listOf(MathOperator.PLUS, MathOperator.MINUS), 9, null, 15)
            CommonDifficulty4.medium ->
                MissingOperatorConfig(listOf(MathOperator.PLUS, MathOperator.MINUS, MathOperator.TIMES), 9, 90, null)
            CommonDifficulty4.hard ->
                MissingOperatorConfig(MathOperator.entries, 12, 60, null)
            CommonDifficulty4.veryHard ->
                MissingOperatorConfig(MathOperator.entries, 20, 60, null)
        }
    }
}

data class MissingOperatorUiState(
    val round: MissingOpRound,
    val revealed: Boolean = false,
    val chosen: MathOperator? = null,
    val lastCorrect: Boolean = false,
    val score: Int = 0,
    val streak: Int = 0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val roundsPlayed: Int = 0,
    val timeLeft: Int = 0,
    val isGameOver: Boolean = false
)
