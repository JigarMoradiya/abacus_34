package com.jigar.me.ui.view.home.screens.math_game_zone.true_false.components

import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4

// One flashed statement, e.g. "7 × 8 = 54" with isTrue = false.
data class TrueFalseQuestion(
    val text: String,
    val isTrue: Boolean,
    val correctAnswer: Int   // shown briefly after a wrong tap
)

data class TrueFalseConfig(
    val ops: String,          // subset of "+-*/"
    val addMax: Int,          // operand cap for + and -
    val mulMax: Int,          // table cap for * and /
    val sessionSeconds: Int
) {
    companion object {
        fun forDifficulty(difficulty: CommonDifficulty4): TrueFalseConfig = when (difficulty) {
            CommonDifficulty4.easy -> TrueFalseConfig("+-", 10, 0, 60)
            CommonDifficulty4.medium -> TrueFalseConfig("+-*", 20, 9, 60)
            CommonDifficulty4.hard -> TrueFalseConfig("+-*/", 50, 12, 60)
            else -> TrueFalseConfig("+-*/", 99, 15, 60)
        }
    }
}

data class TrueFalseUiState(
    val question: TrueFalseQuestion? = null,
    val questionNumber: Int = 0,        // increments per question — drives card animation
    val score: Int = 0,
    val streak: Int = 0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val timeLeft: Int = 60,
    val lastAnswerWrong: Boolean = false,   // flashes the correct answer
    val lastAnswerRight: Boolean = false,
    val isGameOver: Boolean = false
) {
    val multiplier: Int
        get() = when (streak) {
            in 0..2 -> 1
            in 3..5 -> 2
            else -> 3
        }
    val accuracy: Float
        get() = if (correctCount + wrongCount == 0) 0f else correctCount.toFloat() / (correctCount + wrongCount)

    // 3 stars = sharp AND fast, 2 = solid, 1 = keep practicing.
    val starCount: Int
        get() = when {
            correctCount >= 20 && accuracy >= 0.9f -> 3
            correctCount >= 12 && accuracy >= 0.75f -> 2
            else -> 1
        }
}
