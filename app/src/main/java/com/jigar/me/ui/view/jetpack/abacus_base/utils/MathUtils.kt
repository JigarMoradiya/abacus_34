package com.jigar.me.ui.view.jetpack.abacus_base.utils

import com.jigar.me.data.local.data.Movement
import com.jigar.me.data.local.data.RodMovement

object MathUtils{
    // question string convert into list
    fun extractNumbersAndSigns(expression: String): List<String> {
        val result = mutableListOf<String>()
        var buffer = ""

        expression.forEach { char ->
            if (char == '+' || char == '-') {
                if (buffer.isNotEmpty()) {
                    result.add(buffer.removePrefix("+"))
                    buffer = ""
                }
            }
            buffer += char
        }

        if (buffer.isNotEmpty()) {
            result.add(buffer.removePrefix("+"))
        }

        return result
    }
    fun evaluateAtEachStep(expression: String): List<Int> {
        val parts = extractNumbersAndSigns(expression)
        val result = mutableListOf<Int>()
        var currentTotal = 0

        for (part in parts) {
            currentTotal += part.toInt()
            result.add(currentTotal)
        }

        return result
    }

    fun evaluateExpression(expression: String): Int {
        val parts = extractNumbersAndSigns(expression)
        return parts.sumOf { it.toInt() }
    }

    fun calculateEachStepProduct(n1: List<Int>, n2: List<Int>): List<Int> {
        val result = mutableListOf<Int>()
        for (b in n2) {
            for (a in n1) {
                val mul = a * b
                if (mul != 0) {
                    result.add((result.lastOrNull() ?: 0) + mul)
                }
            }
        }
        return result
    }

    fun convertStringToArrayOfArrays(input: String): List<List<Int>> {
        val result = mutableListOf<List<Int>>()
        val currentArray = mutableListOf<Int>()

        input.forEach { char ->
            when {
                char.isDigit() -> {
                    currentArray.add(char.digitToInt())
                }

                char == '×' || char == '*' || char.equals('x', true) || char == '/' || char == '÷' -> {
                    if (currentArray.isNotEmpty()) {
                        result.add(currentArray.toList())
                        currentArray.clear()
                    }
                }
            }
        }

        if (currentArray.isNotEmpty()) {
            result.add(currentArray.toList())
        }

        return result
    }



    fun calculateRodMovements(from: Int, to: Int, rods: Int, isForRightRods: Boolean = false): List<RodMovement> {

        val fromStr = from.toString().padStart(rods, '0')
        val toStr = to.toString().padStart(rods, '0')

        val fromDigits = fromStr.map { it.digitToInt() }
        val toDigits = toStr.map { it.digitToInt() }

        val result = mutableListOf<RodMovement>()

        (0 until rods).forEach { i ->
            val rodIndex = rods - 1 - i   // rightmost is least significant
            val realIndex = if (isForRightRods) 12 - i else 6 - i

            val f = fromDigits[rodIndex]
            val t = toDigits[rodIndex]

            val upperDown = t >= 5 && f < 5
            val upperUp = t < 5 && f >= 5

            val fLower = f % 5
            val tLower = t % 5

            val lowerUp = if (tLower > fLower) tLower - fLower else 0
            val lowerDown = if (tLower < fLower) fLower - tLower else 0

            val lowerOldValue = if (f >= 5) f % 5 else f

            result.add(
                RodMovement(
                    rodIndex = realIndex,
                    movement = Movement(
                        upperUp = upperUp,
                        upperDown = upperDown,
                        lowerUp = lowerUp,
                        lowerDown = lowerDown,
                        lowerOldValue = lowerOldValue
                    )
                )
            )
        }

        return result
    }
}