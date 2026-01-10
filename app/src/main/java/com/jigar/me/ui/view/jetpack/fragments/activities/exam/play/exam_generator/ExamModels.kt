package com.jigar.me.ui.view.jetpack.fragments.activities.exam.play.exam_generator

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.jigar.me.data.model.data.QuestionDataRequest
import com.jigar.me.ui.view.jetpack.core.presentation.theme.ColorGreen
import com.jigar.me.ui.view.jetpack.core.presentation.theme.ColorOrange
import com.jigar.me.ui.view.jetpack.core.presentation.theme.ColorRed

enum class ExamQuestionType {
    Number, Addition, Subtraction, Multiplication, Division
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
    val queType: String = "question"
)

data class QuestionResult(
    val que: String, val userAnswer: Int, val statusQue: QuestionStatus
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
        que_type = questionType,
        image = null // keep null unless required later
    )
}

fun List<QuestionDataRequest>.toQuestionResultList(): List<QuestionResult> {
    return map { it.toQuestionResult() }
}

fun QuestionDataRequest.toQuestionResult(): QuestionResult {
    return QuestionResult(que = que?:"", userAnswer = (user_answer?:"0").toInt(), statusQue = toQuestionStatus())
}
fun QuestionDataRequest.toQuestionStatus(): QuestionStatus {
    return when (is_correct) {
        null -> {
            QuestionStatus(color = ColorOrange, symbol = Icons.Default.RemoveCircle)
        }
        true -> {
            QuestionStatus(color = ColorGreen, symbol = Icons.Default.CheckCircle)
        }
        else -> {
            QuestionStatus(color = ColorRed, symbol = Icons.Default.Cancel)
        }
    }
}


