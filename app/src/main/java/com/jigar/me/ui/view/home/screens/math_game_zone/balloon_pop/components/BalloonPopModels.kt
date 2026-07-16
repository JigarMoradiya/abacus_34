package com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop.components

import androidx.compose.ui.graphics.Color
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4

// Stable 8-colour balloon palette — matches the iOS Balloon Pop exactly.
val balloonPopPalette: List<Color> = listOf(
    Color(0xFFFF6B6B), Color(0xFF4ECDC4), Color(0xFFFFC107), Color(0xFF9B59B6),
    Color(0xFF0074D5), Color(0xFFFF8400), Color(0xFF2ECC71), Color(0xFFE91E63),
)

data class MakeTenBalloon(
    val id: Long,
    val value: Int,
    val xPosition: Float,     // 0.1..0.9 fraction of field width
    val speed: Float,         // seconds to rise across the field
    val wobbleSpeed: Float,
    val colorSeed: Int,       // stable colour pick — random-per-frame flickers
    val isPopping: Boolean = false
) {
    val color: Color get() = balloonPopPalette[colorSeed % balloonPopPalette.size]
}

data class BalloonPopConfig(
    val targetSum: Int,
    val riseMin: Float,
    val riseMax: Float,
    val spawnInterval: Float,
    val timerSeconds: Int?,    // null → no timer (round ends at popsGoal)
    val popsGoal: Int?,        // Easy only
    val correctChance: Float   // fraction of spawned balloons that are the complement
) {
    val popsPerTarget = 5      // partner rotates after this many correct pops

    companion object {
        // riseDuration = full screen traversal. Values verified on iOS via screenshots.
        fun forDifficulty(difficulty: CommonDifficulty4): BalloonPopConfig = when (difficulty) {
            CommonDifficulty4.easy ->
                BalloonPopConfig(5, 9.0f, 12.0f, 1.4f, null, 15, 0.55f)
            CommonDifficulty4.medium ->
                BalloonPopConfig(10, 7.0f, 9.5f, 1.1f, 90, null, 0.45f)
            CommonDifficulty4.hard ->
                BalloonPopConfig(10, 5.5f, 7.5f, 0.9f, 60, null, 0.40f)
            CommonDifficulty4.veryHard ->
                BalloonPopConfig(20, 4.5f, 6.0f, 0.8f, 60, null, 0.35f)
        }
    }
}

data class BalloonPopUiState(
    val balloons: List<MakeTenBalloon> = emptyList(),
    val partner: Int = 1,
    val score: Int = 0,
    val streak: Int = 0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val missedCount: Int = 0,
    val timeLeft: Int = 0,
    val isGameOver: Boolean = false,
    val shakeBalloonId: Long? = null,
    val showNewTargetBanner: Boolean = false
)
