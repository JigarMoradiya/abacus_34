package com.jigar.me.ui.view.home.screens.reports.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jigar.me.R

/*
 * A saved exercise result keeps the English category and label the app sent to the server ("Multiplication",
 * "The answer is 4 Digits, 5 Questions"), whatever language the app was in. These two functions show them in the app language.
 * Anything they do not know (a future label) is shown as it was saved.
 */

@Composable
fun exerciseCategoryText(category: String?): String = when (category) {
    "Addition & Subtraction" -> stringResource(R.string.exercise_title_add_sub)
    "Multiplication" -> stringResource(R.string.Multiplication)
    "Division" -> stringResource(R.string.Division)
    else -> category ?: ""
}

private val ADD_LABEL = Regex("""^(\d+) Digits, (\d+) Lines, (\d+) Questions\s*$""")
private val MUL_LABEL = Regex("""^The answer is (\d+) Digits, (\d+) Questions\s*$""")
private val DIV_LABEL = Regex("""^The dividend is MAX (\d+) Digits, (\d+) Questions\s*$""")

@Composable
fun exerciseLabelText(label: String?): String {
    val text = label ?: return ""
    ADD_LABEL.find(text)?.destructured?.let { (d, l, q) -> return stringResource(R.string.exercise_label_add, d.toInt(), l.toInt(), q.toInt()) }
    MUL_LABEL.find(text)?.destructured?.let { (d, q) -> return stringResource(R.string.exercise_label_mul, d.toInt(), q.toInt()) }
    DIV_LABEL.find(text)?.destructured?.let { (d, q) -> return stringResource(R.string.exercise_label_div, d.toInt(), q.toInt()) }
    return text
}
