package com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.components

import kotlin.random.Random

object SpeedCompareGenerator {

    fun makeRound(config: SpeedCompareConfig): CompareRound {
        val left = makeOperand(config, forceSum = config.bothSums)
        // Force an equal-value right side sometimes, so "=" stays meaningful.
        if (Random.nextDouble() < config.equalChance) {
            val right = operandWithValue(left.value, config, avoidDisplay = left.display)
            return CompareRound(left, right)
        }
        var right = makeOperand(config, forceSum = config.bothSums)
        var guard = 0
        while (right.value == left.value && guard < 8) {
            right = makeOperand(config, forceSum = config.bothSums)
            guard++
        }
        return CompareRound(left, right)
    }

    private fun makeOperand(config: SpeedCompareConfig, forceSum: Boolean): CompareOperand {
        val useSum = forceSum || (config.allowSums && Random.nextBoolean())
        return if (useSum) {
            val a = Random.nextInt(1, config.addendMax + 1)
            val b = Random.nextInt(1, config.addendMax + 1)
            CompareOperand("$a+$b", a + b)
        } else {
            val n = Random.nextInt(1, config.singleMax + 1)
            CompareOperand("$n", n)
        }
    }

    // Build an operand equal to target, preferring a different display string.
    private fun operandWithValue(target: Int, config: SpeedCompareConfig, avoidDisplay: String): CompareOperand {
        if (config.allowSums && target >= 2) {
            repeat(10) {
                val a = Random.nextInt(1, minOf(config.addendMax, target - 1) + 1)
                val b = target - a
                if (b in 1..config.addendMax) {
                    val display = "$a+$b"
                    if (display != avoidDisplay) return CompareOperand(display, target)
                }
            }
        }
        return CompareOperand("$target", target)
    }
}
