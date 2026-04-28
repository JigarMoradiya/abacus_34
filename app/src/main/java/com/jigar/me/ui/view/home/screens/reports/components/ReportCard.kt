package com.jigar.me.ui.view.home.screens.reports.components

import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.jigar.me.R
import com.jigar.me.data.model.data.AllExamData
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorGreen
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimary
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants

@Composable
fun ReportCard(
    item: AllExamData,
    onCheckResultTapped: (AllExamData) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens2),
        shape = RoundedCornerShape(AppDimens.Dimens12),
        elevation = CardDefaults.cardElevation(
            defaultElevation = AppDimens.Dimens2
        ), colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimens.Dimens12)
        ) {
            ReportHeaderRow(item, onCheckResultTapped)

            when (item.type) {
                AppConstants.EXAM.type_CCM ->
                    ReportCCMSection(item)

                AppConstants.EXAM.type_Exercise,
                AppConstants.EXAM.type_Exam,
                AppConstants.apiParams.answerFormalExam ->
                    ReportExerciseExamSection(item, onCheckResultTapped)
            }
        }
    }
}

@Composable
private fun ReportHeaderRow(
    item: AllExamData,
    onCheckResultTapped: (AllExamData) -> Unit
) {
    val context = LocalContext.current

    val (title, badgeColor) = remember(item.type) {
        when (item.type) {
            AppConstants.apiParams.answerFormalExam ->
                "Practice set of Formal Exam" to Color(ContextCompat.getColor(context, R.color.report_set_btn_bg))
            AppConstants.EXAM.type_CCM ->
                "Custom Challenge Mode" to Color(ContextCompat.getColor(context, R.color.report_ccm_btn_bg))
            AppConstants.EXAM.type_Exercise ->
                "Exercise" to Color(ContextCompat.getColor(context, R.color.report_exercise_btn_bg))
            AppConstants.EXAM.type_Exam ->
                "Exam" to Color(ContextCompat.getColor(context, R.color.report_exam_btn_bg))
            else -> "" to Color.Black
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.font_bold))
            ),
            modifier = Modifier
                .background(badgeColor, RoundedCornerShape(AppDimens.Dimens20))
                .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6)
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = item.dateTimeFormat()?:"",
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.font_semibold))
            ),
            color = Color.Black
        )
    }
}


@Composable
private fun ReportExerciseExamSection(
    item: AllExamData,
    onCheckResultTapped: (AllExamData) -> Unit
) {
    Spacer(Modifier.height(AppDimens.Dimens6))

    // Check Result button row
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row {
                val title = when (item.type) {
                    AppConstants.EXAM.type_Exercise -> {
                        stringResource(R.string.type_of_exercise)
                    }
                    AppConstants.EXAM.type_Exam -> {
                        stringResource(R.string.level_of_exam)
                    }
                    AppConstants.apiParams.answerFormalExam -> {
                        stringResource(R.string.level_of_set)
                    }
                    else -> {
                        ""
                    }
                }
                val titleValue = when (item.type) {
                    AppConstants.EXAM.type_Exercise -> {
                        item.category
                    }
                    AppConstants.EXAM.type_Exam -> {
                        item.level
                    }
                    AppConstants.apiParams.answerFormalExam -> {
                        item.level
                    }
                    else -> {
                        ""
                    }
                }
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.font_bold))
                    ),
                )
                Text(
                    " $titleValue",
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = FontFamily(Font(R.font.font_medium))
                )
            }

            if (item.type != AppConstants.apiParams.answerFormalExam){
                val title2 = when (item.type) {
                    AppConstants.EXAM.type_Exercise -> {
                        stringResource(R.string.exercise_in)
                    }
                    AppConstants.EXAM.type_Exam -> {
                        stringResource(R.string.type_of_questions)
                    }
                    else -> {
                        ""
                    }
                }
                val titleValue2 = when (item.type) {
                    AppConstants.EXAM.type_Exercise -> {
                        item.label
                    }
                    AppConstants.EXAM.type_Exam -> {
                        item.sub_type?.replace(",", ", ")
                    }
                    else -> {
                        ""
                    }
                }
                Row {
                    Text(
                        text = title2,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold))
                        ),
                    )
                    Text(" $titleValue2",
                        style = MaterialTheme.typography.labelMedium,
                        fontFamily = FontFamily(Font(R.font.font_medium))
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = stringResource(R.string.checkResult),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,fontFamily = FontFamily(Font(R.font.font_bold))
            ),
            modifier = Modifier
                .background(ColorPrimary, RoundedCornerShape(AppDimens.Dimens20))
                .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6)
                .clickable{
                    onCheckResultTapped(item)
                }
        )
    }

    Spacer(Modifier.height(AppDimens.Dimens12))

    // Stats Row (exact replacement of RelativeLayouts)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StatItemSmall(
            icon = R.mipmap.ic_question_total,
            value = (item.no_of_questions ?: 0).toString(),
            label = R.string.TotalQue,
            color = Color(ContextCompat.getColor(LocalContext.current, R.color.colorBlueDark))
        )

        if (item.type == AppConstants.EXAM.type_Exercise) {
            StatItemSmall(
                icon = R.mipmap.ic_max_time,
                value = item.maxTimeFormat(),
                label = R.string.maximum_time,
                color = Color(ContextCompat.getColor(LocalContext.current, R.color.colorPinkDark))
            )
        }

        StatItemSmall(
            icon = R.mipmap.ic_time,
            value = item.totalTimeFormat(),
            label = R.string.TotalTakeTime,
            color = Color(ContextCompat.getColor(LocalContext.current, R.color.colorPinkDark))
        )

        StatItemSmall(
            icon = R.mipmap.ic_question_right,
            value = (item.no_of_right_answers ?: 0).toString(),
            label = R.string.RightQue,
            color = Color(ContextCompat.getColor(LocalContext.current, R.color.colorGreenDark))
        )

        StatItemSmall(
            icon = R.mipmap.ic_question_wrong,
            value = ((item.no_of_questions ?: 0) - (item.no_of_right_answers ?: 0)).toString(),
            label = R.string.WrongQue,
            color = Color(ContextCompat.getColor(LocalContext.current, R.color.colorRedDark))
        )
    }
}

@Composable
private fun ReportCCMSection(item: AllExamData) {
    Spacer(Modifier.height(AppDimens.Dimens6))

    Row {
        Text(
            text = stringResource(R.string.question)+" ",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.font_bold))
            ),
        )
        AndroidView(
            factory = { context ->
                TextView(context).apply {
                    textSize = 14f
                    setTextColor(Color.Black.toArgb())
                }
            },
            update = {
                it.text = HtmlCompat.fromHtml(
                    item.fullQuestion() + " = <b><font color='#4CAF50'>" +
                            item.correctAns() + "</font></b>",
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )
            }
        )
    }

    Row {
        Text(
            text = if (item.isAnswerCorrect())
                stringResource(R.string.great_your_answer_is_correct)
            else
                stringResource(R.string.sorry_your_answer_is_incorrect),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (item.isAnswerCorrect()) ColorGreen else Color.Red,
                fontFamily = FontFamily(Font(R.font.font_bold))
            ),
        )

        Spacer(Modifier.weight(1f))

        if (!item.isAnswerCorrect()){
            Text(
                text = "Your answer is : ${item.userAns()}",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Red,
                    fontFamily = FontFamily(Font(R.font.font_bold))
                ),
            )
        }
    }
    Spacer(Modifier.height(AppDimens.Dimens4))
    Row {
        Text(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            textAlign = TextAlign.Start,
            text = stringResource(R.string.is_question_speak),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.font_bold))
            ),
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = stringResource(R.string.is_question_show_in_number),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.font_bold))
            ),
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            textAlign = TextAlign.End,
            text = stringResource(R.string.is_question_show_in_word),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.font_bold))
            ),
        )
    }

    Row {
        Text(
            text = if (item.is_question_speak == true) stringResource(R.string.yes) else stringResource(R.string.no),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.font_medium))
            ),
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = if (item.is_question_show_in_number == true) stringResource(R.string.yes) else stringResource(R.string.no),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.font_medium))
            ),
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = if (item.is_question_show_in_word == true) stringResource(R.string.yes) else stringResource(R.string.no),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.font_medium))
            ),
        )
    }
    Spacer(Modifier.height(AppDimens.Dimens4))
    Row {
        Text(
            text = stringResource(R.string.gap_between_each_question_number),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.font_bold))
            ),
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = stringResource(R.string.question_min_max_length),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.font_bold))
            ),
        )
    }

    Row {
        Text(
            text = (item.gap_between_two_question?:0).toString()+" sec",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.font_medium))
            ),
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = (item.question_min_length?:0).toString()+" - "+(item.question_max_length?:0),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.font_medium))
            ),
        )
    }

}


@Composable
fun StatItemSmall(
    icon: Int,
    value: String,
    label: Int,
    color: Color
) {
    Row {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(AppDimens.Dimens24).padding(top = AppDimens.Dimens2)
        )
        Spacer(Modifier.width(AppDimens.Dimens4))
        Column {
            Text(value,
                style = MaterialTheme.typography.titleSmall.copy(color = color, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))))
            Text(stringResource(label),
                style = MaterialTheme.typography.bodySmall.copy(color = color.copy(alpha = 0.8f), fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold))))
        }
    }

}
