package com.jigar.me.ui.view.home.screens.math_game_zone.clock_master.components

import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4

// One round: an analog clock shows hour:minute, kid picks the right digital
// time out of four options.
data class ClockQuestion(
    val hour: Int,          // 1..12
    val minute: Int,        // 0..59 (granularity by difficulty)
    val options: List<String>,
    val correctIndex: Int
) {
    val correctText: String get() = options[correctIndex]
}

data class ClockMasterConfig(
    val minuteStep: Int,     // 60=only o'clock, 30, 15, 5
    val sessionSeconds: Int
) {
    companion object {
        fun forDifficulty(difficulty: CommonDifficulty4): ClockMasterConfig = when (difficulty) {
            CommonDifficulty4.easy -> ClockMasterConfig(60, 60)      // o'clock only
            CommonDifficulty4.medium -> ClockMasterConfig(30, 60)    // half hours
            CommonDifficulty4.hard -> ClockMasterConfig(15, 60)      // quarter hours
            else -> ClockMasterConfig(5, 60)                          // 5-minute steps
        }
    }
}

data class ClockMasterUiState(
    val question: ClockQuestion? = null,
    val questionNumber: Int = 0,
    val score: Int = 0,
    val streak: Int = 0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val timeLeft: Int = 60,
    val selectedIndex: Int? = null,      // the tapped option (for feedback color)
    val lastAnswerWrong: Boolean = false,
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
    val starCount: Int
        get() = when {
            correctCount >= 12 && accuracy >= 0.9f -> 3
            correctCount >= 7 && accuracy >= 0.75f -> 2
            else -> 1
        }
}
