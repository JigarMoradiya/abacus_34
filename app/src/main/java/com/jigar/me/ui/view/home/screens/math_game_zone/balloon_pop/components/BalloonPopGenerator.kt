package com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop.components

import kotlin.math.abs
import kotlin.random.Random

object BalloonPopGenerator {

    fun newPartner(targetSum: Int, excluding: Int?): Int {
        if (targetSum <= 2) return 1
        var partner: Int
        do {
            partner = Random.nextInt(1, targetSum)   // 1..targetSum-1
        } while (partner == excluding)
        return partner
    }

    fun makeBalloon(
        id: Long,
        partner: Int,
        config: BalloonPopConfig,
        forceCorrect: Boolean = false
    ): MakeTenBalloon {
        val complement = config.targetSum - partner
        val isCorrect = forceCorrect || Random.nextFloat() < config.correctChance
        val value = if (isCorrect) complement else distractor(complement, config)
        return MakeTenBalloon(
            id = id,
            value = value,
            xPosition = safeX(),
            speed = Random.nextFloat() * (config.riseMax - config.riseMin) + config.riseMin,
            wobbleSpeed = Random.nextFloat() * (1.6f - 0.8f) + 0.8f,
            colorSeed = Random.nextInt(0, 8)
        )
    }

    // On harder modes half the distractors sit near the complement (±1/±2)
    // so kids must actually compute, not just pattern-match.
    private fun distractor(complement: Int, config: BalloonPopConfig): Int {
        val upper = maxOf(9, config.targetSum - 1)
        val nearMiss = config.targetSum >= 10 && Random.nextBoolean()
        var value: Int
        do {
            value = if (nearMiss) {
                complement + listOf(-2, -1, 1, 2).random()
            } else {
                Random.nextInt(1, upper + 1)
            }
        } while (value == complement || value < 1 || value > upper)
        return value
    }

    private var lastX: Float = 0f
    private fun safeX(): Float {
        var x: Float
        do {
            x = Random.nextFloat() * (0.9f - 0.1f) + 0.1f
        } while (abs(x - lastX) < 0.15f)
        lastX = x
        return x
    }
}
