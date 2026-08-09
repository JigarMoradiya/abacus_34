package com.jigar.me.ui.view.home.screens.math_game_zone.math_bingo.components

import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4

// A 4x4 card of answers. Each round "calls" an equation whose answer is an
// unmarked cell; mark a full row/column/diagonal for BINGO!
const val BINGO_SIZE = 4

data class BingoCall(
    val text: String,     // "7 × 8"
    val answer: Int
)

data class MathBingoConfig(
    val ops: String,
    val addMax: Int,
    val mulMax: Int,
    val sessionSeconds: Int
) {
    companion object {
        fun forDifficulty(difficulty: CommonDifficulty4): MathBingoConfig = when (difficulty) {
            CommonDifficulty4.easy -> MathBingoConfig("+-", 10, 0, 90)
            CommonDifficulty4.medium -> MathBingoConfig("+-*", 20, 9, 90)
            CommonDifficulty4.hard -> MathBingoConfig("+-*/", 50, 12, 90)
            else -> MathBingoConfig("+-*/", 99, 15, 90)
        }
    }
}

data class MathBingoUiState(
    val card: List<Int> = emptyList(),        // 16 answers
    val marked: List<Boolean> = emptyList(),
    val bingoLines: Set<Int> = emptySet(),    // completed line ids (for glow)
    val call: BingoCall? = null,
    val callNumber: Int = 0,
    val cardNumber: Int = 0,                  // increments per fresh card
    val score: Int = 0,
    val bingoCount: Int = 0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val timeLeft: Int = 90,
    val wrongTapIndex: Int? = null,           // shakes that cell
    val wrongTapCount: Int = 0,               // bumps so repeat taps re-shake
    val justBingo: Boolean = false,           // triggers the BINGO banner
    val isGameOver: Boolean = false
) {
    val accuracy: Float
        get() = if (correctCount + wrongCount == 0) 0f else correctCount.toFloat() / (correctCount + wrongCount)
    val starCount: Int
        get() = when {
            bingoCount >= 3 && accuracy >= 0.85f -> 3
            bingoCount >= 2 -> 2
            else -> 1
        }
}
