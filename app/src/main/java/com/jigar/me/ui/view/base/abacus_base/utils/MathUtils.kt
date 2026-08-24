package com.jigar.me.ui.view.base.abacus_base.utils

import com.jigar.me.data.local.data.Movement
import com.jigar.me.data.local.data.RodMovement
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.tan
import kotlin.text.iterator

object MathUtils{
    fun formatQuestionSpace(question: String): String {
        return question
            .replace("x", " x ")
            .replace("×", " x ")
            .replace("/", " ÷ ")
            .replace("÷", " ÷ ")
            .replace("+", " + ")
            .replace("-", " - ")
    }
    fun formatQuestion(question: String): String {
        return question
            .replace("x", "*")
            .replace("×", "*")
            .replace("÷", "/")
    }
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
            val realIndex = if (isForRightRods) (6 + rods) - i else 6 - i

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

    fun calculateStringExpression(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0
            fun nextChar() {
                ch = if (++pos < str.length) str[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.code)) x += parseTerm() // addition
                    else if (eat('-'.code)) x -= parseTerm() // subtraction
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.code)) x *= parseFactor() // multiplication
                    else if (eat('/'.code)) x /= parseFactor() // division
                    else return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor() // unary plus
                if (eat('-'.code)) return -parseFactor() // unary minus
                var x: Double
                val startPos = pos
                if (eat('('.code)) { // parentheses
                    x = parseExpression()
                    eat(')'.code)
                } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) { // numbers
                    while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                    x = str.substring(startPos, pos).toDouble()
                } else if (ch >= 'a'.code && ch <= 'z'.code) { // functions
                    while (ch >= 'a'.code && ch <= 'z'.code) nextChar()
                    val func = str.substring(startPos, pos)
                    x = parseFactor()
                    x =
                        if (func == "sqrt") Math.sqrt(x) else if (func == "sin") sin(
                            Math.toRadians(
                                x
                            )
                        ) else if (func == "cos") cos(
                            Math.toRadians(x)
                        ) else if (func == "tan") tan(Math.toRadians(x)) else throw RuntimeException(
                            "Unknown function: $func"
                        )
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }
                if (eat('^'.code)) x = x.pow(parseFactor()) // exponentiation
                return x
            }
        }.parse()
    }

    fun splitQuestionIntoLines(question: String): List<String> {
        val result = mutableListOf<String>()
        var temp = ""

        for (char in question) {
            if (
                char == '+' ||
                char == '-' ||
                char == 'x' ||
                char == '×' ||
                char == '÷' ||
                char == '/'
            ) {
                if (temp.isNotEmpty()) {
                    result.add(temp)
                }
                temp = char.toString()
            } else {
                temp += char
            }
        }

        if (temp.isNotEmpty()) {
            result.add(temp)
        }

        return result
    }
}