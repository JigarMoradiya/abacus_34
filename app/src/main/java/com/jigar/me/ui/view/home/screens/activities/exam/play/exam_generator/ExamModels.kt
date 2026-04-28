package com.jigar.me.ui.view.home.screens.activities.exam.play.exam_generator

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.gson.Gson
import com.jigar.me.data.model.data.QuestionDataRequest
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorDARKGreen
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorGreen
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorOrange
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorRed

enum class ExamQuestionType {
    Number, Addition, Subtraction, Multiplication, Division
}
enum class MainQuestionType {
    question,abacus,missingNumber
}
data class ExamMathQuestion(
    val questionType: String,
    val que: String,
    val answer: Int,
    val option1: Int,
    val option2: Int,
    val option3: Int,
    val option4: Int,
    var userAnswer: Int? = null,
    var isCorrect: Boolean? = null,
    val queType: String = "question",
    var index: Int? = null,
)

data class QuestionParts(
    val left: String,
    val number: Int?,
    val right: String
)

data class QuestionResult(
    val que: String, val userAnswer: String, val statusQue: QuestionStatus,
    var que_type: String? = null,
    var index: Int? = null
)

data class QuestionStatus(
    val color: Color, val symbol: ImageVector
)

data class ExamResultUi(
    val title: String,
    val titleColor: Color,
    val rating: Float
)

fun ExamMathQuestion.toQuestionDataRequest(): QuestionDataRequest {
    return QuestionDataRequest(
        que = que.replace(" ",""),
        user_answer = userAnswer?.toString(),
        is_correct = userAnswer?.let { it == answer },
        que_type = queType,
        image = null, // keep null unless required later,
        index = index
    )
}

fun List<QuestionDataRequest>.toQuestionResultList(): List<QuestionResult> {
    return map { it.toQuestionResult() }
}

fun QuestionDataRequest.toQuestionResult(): QuestionResult {
    val userAns = if (user_answer.isNullOrEmpty()){"0"}else{ user_answer }
    return QuestionResult(que = que?:"", userAnswer = (userAns?:"0"), statusQue = toQuestionStatus(),que_type,index)
}
fun QuestionDataRequest.toQuestionStatus(): QuestionStatus {
    return when (is_correct) {
        null if user_answer.isNullOrEmpty() -> {
            QuestionStatus(color = ColorOrange, symbol = Icons.Default.RemoveCircle)
        }
        true -> {
            QuestionStatus(color = ColorDARKGreen, symbol = Icons.Default.CheckCircle)
        }
        else -> {
            QuestionStatus(color = ColorRed, symbol = Icons.Default.Cancel)
        }
    }
}


