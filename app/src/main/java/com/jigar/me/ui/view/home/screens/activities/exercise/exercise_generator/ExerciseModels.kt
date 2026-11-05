package com.jigar.me.ui.view.home.screens.activities.exercise.exercise_generator

import java.util.UUID


data class Exercise(
    val title: String,
    val gridItems: List<GridItemModel>
)

data class GridItemModel(
    val id: String = UUID.randomUUID().toString(),
    val digit: Int,
    val line: Int,
    val question: Int,
    val maxTime: Int
){
    fun selectedItemDescription(
        currentPage: Int
    ): String {
        val timeInMinutes = maxTime / 60

        return when (currentPage) {
            0 -> {
                "$digit Digits, $line Lines,\n$question Questions in $timeInMinutes Minute"
            }

            1 -> {
                "The answer is $digit Digits,\n$question Questions in $timeInMinutes Minute"
            }

            else -> {
                "The dividend is MAX $digit Digits,\n$question Questions in $timeInMinutes Minute"
            }
        }
    }

    fun selectedItemDescriptionApi(currentPage: Int): String {
        return when (currentPage) {
            0 -> {
                "$digit Digits, $line Lines, $question Questions"
            }

            1 -> {
                "The answer is $digit Digits, $question Questions "
            }

            else -> {
                "The dividend is MAX $digit Digits, $question Questions"
            }
        }
    }

}

data class ExerciseQuestionList(
    val que: String,
    val answer: Int,
    var userAnswer: Int? = null,
    var isCorrect: Boolean? = null,
)

val exercisesViewPageData = listOf(
    Exercise(
        title = "Addition & Subtraction",
        gridItems = listOf(
            GridItemModel(digit = 1, line = 5, question = 5, maxTime = 180),
            GridItemModel(digit = 1, line = 10, question = 10, maxTime = 300),
            GridItemModel(digit = 2, line = 5, question = 5, maxTime = 240),
            GridItemModel(digit = 2, line = 10, question = 10, maxTime = 480),
            GridItemModel(digit = 3, line = 5, question = 5, maxTime = 240),
            GridItemModel(digit = 3, line = 10, question = 10, maxTime = 480),
            GridItemModel(digit = 4, line = 5, question = 5, maxTime = 300),
            GridItemModel(digit = 4, line = 10, question = 10, maxTime = 600),
            GridItemModel(digit = 5, line = 5, question = 5, maxTime = 300),
            GridItemModel(digit = 5, line = 10, question = 10, maxTime = 600),
            GridItemModel(digit = 6, line = 5, question = 5, maxTime = 300),
            GridItemModel(digit = 6, line = 10, question = 10, maxTime = 600)
        )
    ),

    Exercise(
        title = "Multiplication",
        gridItems = listOf(
            GridItemModel(digit = 3, line = 0, question = 5, maxTime = 180),
            GridItemModel(digit = 3, line = 0, question = 10, maxTime = 300),
            GridItemModel(digit = 4, line = 0, question = 5, maxTime = 180),
            GridItemModel(digit = 4, line = 0, question = 10, maxTime = 300),
            GridItemModel(digit = 5, line = 0, question = 5, maxTime = 180),
            GridItemModel(digit = 5, line = 0, question = 10, maxTime = 300),
            GridItemModel(digit = 6, line = 0, question = 5, maxTime = 180),
            GridItemModel(digit = 6, line = 0, question = 10, maxTime = 300),
            GridItemModel(digit = 7, line = 0, question = 5, maxTime = 180),
            GridItemModel(digit = 7, line = 0, question = 10, maxTime = 300)
        )
    ),

    Exercise(
        title = "Division",
        gridItems = listOf(
            GridItemModel(digit = 3, line = 0, question = 5, maxTime = 240),
            GridItemModel(digit = 3, line = 0, question = 5, maxTime = 120),
            GridItemModel(digit = 4, line = 0, question = 5, maxTime = 180),
            GridItemModel(digit = 4, line = 0, question = 5, maxTime = 120),
            GridItemModel(digit = 4, line = 0, question = 10, maxTime = 360),
            GridItemModel(digit = 4, line = 0, question = 10, maxTime = 240),
            GridItemModel(digit = 5, line = 0, question = 5, maxTime = 180),
            GridItemModel(digit = 5, line = 0, question = 5, maxTime = 120),
            GridItemModel(digit = 5, line = 0, question = 10, maxTime = 360),
            GridItemModel(digit = 5, line = 0, question = 10, maxTime = 240),
            GridItemModel(digit = 6, line = 0, question = 10, maxTime = 360),
            GridItemModel(digit = 6, line = 0, question = 10, maxTime = 240)
        )
    )
)

