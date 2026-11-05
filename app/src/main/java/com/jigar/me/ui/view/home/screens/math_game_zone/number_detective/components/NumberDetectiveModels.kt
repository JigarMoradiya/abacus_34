package com.jigar.me.ui.view.home.screens.math_game_zone.number_detective.components

import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4

// One round: a secret number hiding in [minRange, maxRange]. The kid guesses
// and gets a Too High / Too Low nudge until they crack it or run out of tries.
data class NumberDetectiveQuestion(
    val secret: Int,
    val minRange: Int,
    val maxRange: Int
)

data class NumberDetectiveConfig(
    val minRange: Int,
    val maxRange: Int,
    val maxTries: Int,
    val sessionSeconds: Int
) {
    companion object {
        fun forDifficulty(difficulty: CommonDifficulty4): NumberDetectiveConfig = when (difficulty) {
            CommonDifficulty4.easy -> NumberDetectiveConfig(1, 20, 7, 90)
            CommonDifficulty4.medium -> NumberDetectiveConfig(1, 50, 8, 90)
            CommonDifficulty4.hard -> NumberDetectiveConfig(1, 100, 9, 90)
            else -> NumberDetectiveConfig(1, 500, 10, 90)
        }
    }
}

data class NumberDetectiveUiState(
    val question: NumberDetectiveQuestion? = null,
    val questionNumber: Int = 0,
    val enteredDigits: String = "",
    val triesUsed: Int = 0,
    val maxTries: Int = 7,
    val guesses: List<Int> = emptyList(),
    // -1 = last guess was too HIGH (secret is lower), +1 = too LOW (secret is
    // higher), 0 = no active nudge (fresh round, just solved, or revealed).
    val feedbackDirection: Int = 0,
    val score: Int = 0,
    val streak: Int = 0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val timeLeft: Int = 90,
    val lastAnswerWrong: Boolean = false,
    val lastAnswerRight: Boolean = false,
    val isGameOver: Boolean = false,
    // Set after a strong run - the result overlay invites the kid up a tier.
    val suggestedDifficulty: String? = null
) {
    val triesLeft: Int get() = (maxTries - triesUsed).coerceAtLeast(0)
    val multiplier: Int
        get() = when (streak) {
            in 0..2 -> 1
            in 3..5 -> 2
            else -> 3
        }
    val accuracy: Float
        get() = if (correctCount + wrongCount == 0) 0f else correctCount.toFloat() / (correctCount + wrongCount)
    val starCount: Int
        get() = when {
            correctCount >= 8 && accuracy >= 0.85f -> 3
            correctCount >= 5 && accuracy >= 0.6f -> 2
            else -> 1
        }
}
