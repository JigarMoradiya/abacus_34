package com.jigar.me.ui.view.home.screens.math_game_zone.place_value.components

import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4

// One round: build `target` from its expanded form, e.g. 402 → "400 + 2"
// (zero places are omitted — the classic place-value trap!).
data class PlaceValueQuestion(
    val target: Int,
    val digits: Int,               // slot count, e.g. 3 for 402
    val expandedParts: List<Int>   // [400, 2]
)

data class PlaceValueConfig(
    val digits: Int,
    val sessionSeconds: Int
) {
    companion object {
        fun forDifficulty(difficulty: CommonDifficulty4): PlaceValueConfig = when (difficulty) {
            CommonDifficulty4.easy -> PlaceValueConfig(2, 90)
            CommonDifficulty4.medium -> PlaceValueConfig(3, 90)
            CommonDifficulty4.hard -> PlaceValueConfig(4, 90)
            else -> PlaceValueConfig(5, 90)
        }
    }
}

data class PlaceValueUiState(
    val question: PlaceValueQuestion? = null,
    val questionNumber: Int = 0,
    val entered: List<Int?> = emptyList(),   // one slot per digit, left to right
    val activeSlot: Int = 0,
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
            correctCount >= 10 && accuracy >= 0.9f -> 3
            correctCount >= 6 && accuracy >= 0.75f -> 2
            else -> 1
        }
}
