package com.jigar.me.ui.view.home.screens.activities.exercise.exercise_generator

import java.util.UUID
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import com.jigar.me.R


/** [title] is the identifier (also sent to the server as the exercise category): it stays the same English text in every language. [titleRes] is what is shown. */
data class Exercise(
    val title: String,
    @StringRes val titleRes: Int,
    val gridItems: List<GridItemModel>
)

data class GridItemModel(
    val id: String = UUID.randomUUID().toString(),
    val digit: Int,
    val line: Int,
    val question: Int,
    val maxTime: Int
){
    /** The words under the chosen card, in the app language ("3 Digits, 5 Lines, 5 Questions in 4 Minutes"). */
    @Composable
    fun selectedItemDescriptionText(currentPage: Int): String {
        val timeInMinutes = maxTime / 60
        return when (currentPage) {
            0 -> pluralStringResource(R.plurals.exercise_desc_add, timeInMinutes, digit, line, question, timeInMinutes)
            1 -> pluralStringResource(R.plurals.exercise_desc_mul, timeInMinutes, digit, question, timeInMinutes)
            else -> pluralStringResource(R.plurals.exercise_desc_div, timeInMinutes, digit, question, timeInMinutes)
        }
    }

    /** Sent to the server as the exercise label: English in every language (the report screen translates it for display). */
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
        title = "Addition & Subtraction", titleRes = R.string.exercise_title_add_sub,
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
        title = "Multiplication", titleRes = R.string.Multiplication,
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
        title = "Division", titleRes = R.string.Division,
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

