package com.jigar.me.ui.view.home.screens.math_game_zone.missing_operator.components

import kotlin.random.Random

object MissingOperatorGenerator {

    fun makeRound(config: MissingOperatorConfig): MissingOpRound {
        // Retry until the answer is UNIQUE among the shown operators
        // (e.g. 2 ? 2 = 4 is ambiguous: + and × both work).
        repeat(200) {
            val op = config.operators.random()
            val pair = operands(op, config) ?: return@repeat
            val (a, b) = pair
            val result = op.apply(a, b) ?: return@repeat
            val matches = config.operators.count { it.apply(a, b) == result }
            if (matches == 1) {
                return MissingOpRound(a, b, result, op, config.operators)
            }
        }
        val a = 3; val b = 4
        return MissingOpRound(a, b, a + b, MathOperator.PLUS, config.operators)
    }

    private fun operands(op: MathOperator, config: MissingOperatorConfig): Pair<Int, Int>? {
        val m = config.maxOperand
        return when (op) {
            MathOperator.PLUS -> Pair(Random.nextInt(1, m + 1), Random.nextInt(1, m + 1))
            MathOperator.MINUS -> {
                val a = Random.nextInt(1, m + 1)
                val b = Random.nextInt(1, a + 1)
                Pair(a, b)
            }
            MathOperator.TIMES -> {
                val hi = minOf(m, 12)
                Pair(Random.nextInt(2, hi + 1), Random.nextInt(2, hi + 1))
            }
            MathOperator.DIVIDE -> {
                val hi = maxOf(2, minOf(m, 12))
                val b = Random.nextInt(2, hi + 1)
                val q = Random.nextInt(2, hi + 1)
                Pair(b * q, b)   // a / b = q, clean
            }
        }
    }
}
