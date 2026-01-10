package com.jigar.me.ui.view.jetpack.fragments.exercise.exercise_generator

import kotlin.math.pow

object ExerciseGenerator {

    /** Addition + Subtraction */
    fun generateAddSubQuestions(model: GridItemModel): List<ExerciseQuestionList> {
        val questions = mutableListOf<ExerciseQuestionList>()

        val minVal = 10.0.pow((model.digit - 1).toDouble()).toInt()
        val maxVal = 10.0.pow(model.digit.toDouble()).toInt() - 1
        val numberRange = if (model.digit == 1) 1..9 else minVal..maxVal

        val maxMinusAllowed = if (model.question == 5) 1
        else if (model.question == 10) 3
        else model.question / 2

        while (questions.size < model.question) {
            var expression = ""
            var result = 0
            var minusCount = 0

            repeat(model.line) { i ->
                if (i == 0) {
                    val first = numberRange.random()
                    expression = "$first"
                    result = first
                } else {
                    val useMinus = minusCount < maxMinusAllowed && listOf(true, false).random()

                    if (useMinus && result > 1) {
                        val safeMax = minOf(result - 1, numberRange.last)
                        val num = (1..safeMax).random()
                        expression += "-$num"
                        result -= num
                        minusCount++
                    } else {
                        val num = numberRange.random()
                        expression += "+$num"
                        result += num
                    }
                }
            }

            if (result > 0) {
                questions.add(ExerciseQuestionList(expression, result))
            }
        }

        return questions
    }

    /** Multiplication */
    fun generateMultiplicationExercise(
        model: GridItemModel
    ): List<ExerciseQuestionList> {
        val list = mutableListOf<ExerciseQuestionList>()

        repeat(model.question) {
            val que1 = when (model.digit) {
                3 -> (2..99).random()
                4 -> (2..999).random()
                5 -> listOf(
                    (2..9999), (99..999), (999..5999)
                ).random().random()

                6 -> listOf(
                    (2..99999), (99..9999), (49..999), (999..4999)
                ).random().random()

                7 -> listOf(
                    (9999..999999), (99..99999), (999..999999), (99..9999), (9999..49999), (999..4999)
                ).random().random()

                else -> (2..99).random()
            }

            val que2 = when (model.digit) {
                3 -> (maxOf(2, 100 / que1)..maxOf(2, 999 / que1)).random()
                4 -> (maxOf(2, 1000 / que1)..maxOf(2, 9999 / que1)).random()
                5 -> (maxOf(2, 10000 / que1)..maxOf(2, 99999 / que1)).random()
                6 -> (maxOf(2, 100000 / que1)..maxOf(2, 999999 / que1)).random()
                7 -> (maxOf(2, 1000000 / que1)..maxOf(2, 9999999 / que1)).random()
                else -> (2..9).random()
            }

            val invert = model.question > 5 && listOf(true, false).random()
            val q = if (invert) "$que2 x $que1" else "$que1 x $que2"
            list.add(ExerciseQuestionList(q, que1 * que2))
        }

        return list
    }

    /** Division */
    fun generateDivisionExercise(model: GridItemModel): List<ExerciseQuestionList> {
        val list = mutableListOf<ExerciseQuestionList>()

        repeat(model.question) { index ->
            val que1 = when (model.digit) {
                3 -> (2..9).random()
                4 -> if (index < 2) (2..19).random() else (2..99).random()
                5 -> if (index < 2) (2..19).random() else (2..299).random()
                else -> if (index < 5) (2..299).random() else (300..999).random()
            }

            val que2 = when (model.digit) {
                3 -> (100 / que1..999 / que1).random()
                4 -> (1000 / que1..9999 / que1).random()
                5 -> (10000 / que1..99999 / que1).random()
                else -> (100000 / que1..999999 / que1).random()
            }

            val invert = que2 > que1
            val dividend = que1 * que2
            val question = if (invert) "$dividend ÷ $que1" else "$dividend ÷ $que2"
            val answer = dividend / if (invert) que1 else que2

            list.add(ExerciseQuestionList(question, answer))
        }

        return list.shuffled()
    }
}
