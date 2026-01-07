package com.jigar.me.ui.view.jetpack.fragments.exam.play.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.core.presentation.components.PrimaryButton
import com.jigar.me.ui.view.jetpack.core.presentation.theme.ColorPrimaryLight
import com.jigar.me.ui.view.jetpack.fragments.exam.play.exam_generator.ExamResultUi
import com.jigar.me.ui.view.jetpack.fragments.exam.play.exam_generator.QuestionResult
import com.jigar.me.ui.view.jetpack.fragments.exam.play.exam_generator.toQuestionResultList
import com.jigar.me.ui.view.jetpack.fragments.exam.play.viewmodels.ExamPlayUiState
import com.jigar.me.utils.extensions.secToTimeFormat

@Composable
fun ExamCompleteResultDialog(
    uiState: ExamPlayUiState,
    onClose: () -> Unit,
    onGiveAgain: () -> Unit,
) {
    val context = LocalContext.current
    val questionResults: List<QuestionResult> = uiState.examPaper.toQuestionResultList()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onClose() }) {

        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(dimensionResource(R.dimen.activity_padding16)), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(6.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                // Header
                Row(modifier = Modifier.fillMaxWidth()
                    .padding(start = dimensionResource(R.dimen.activity_padding16)).padding(end = dimensionResource(R.dimen.activity_padding12)),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.result_of_exam),
                        style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily(Font(R.font.font_extra_bold))),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onClose,
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.DarkGray,modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(dimensionResource(R.dimen.activity_padding4)))
                        Text(
                            stringResource(R.string.close), style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold)))
                        )
                    }
                }

                HorizontalDivider(
                    Modifier, color = Color.Black.copy(alpha = 0.1f), thickness = 1.dp
                )

                Row{
                    Column(modifier = Modifier.weight(1f).fillMaxHeight(),horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(
                                icon = R.mipmap.ic_time,
                                value = uiState.elapsedSeconds.secToTimeFormat(),
                                label = R.string.TotalTime,
                                color = Color(0xFFD81B60)
                            )
                            StatItem(
                                icon = R.mipmap.ic_question_right,
                                value = uiState.totalCorrect.toString()+" / "+uiState.examPaper.size.toString(),
                                label = R.string.RightQue,
                                color = Color(0xFF2E7D32)
                            )
                        }
                        Spacer(Modifier.height(dimensionResource(R.dimen.activity_padding16)))
                        val result = buildExamResult(
                            context = context,
                            totalRight = uiState.examPaper.count { it.userAnswer == it.answer },
                            totalQuestion = uiState.examPaper.size
                        )
                        Text(
                            text = result.title,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleSmall.copy(color = result.titleColor, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily(Font(R.font.font_medium))))

                        Spacer(Modifier.height(dimensionResource(R.dimen.activity_padding12)))

                        PrimaryButton(text = stringResource(R.string.give_exam_again), onClick = onGiveAgain)
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.activity_padding8)),
                        contentPadding = PaddingValues(dimensionResource(R.dimen.activity_padding12))
                    ) {
                        items(
                            items = questionResults,
                            key = { item -> "${item.que}_${item.hashCode()}" }
                        ) { item ->
                            QuestionExamColumnItemHorizontal(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionExamColumnItemHorizontal(item: QuestionResult) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = ColorPrimaryLight),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.activity_padding8),
                    vertical = dimensionResource(R.dimen.activity_padding4)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                modifier = Modifier.weight(1f),   // 👈 takes remaining space
                text = buildAnnotatedString {
                    // Question part
                    withStyle(
                        style = SpanStyle(
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily(Font(R.font.font_semibold))
                        )
                    ) {
                        append(
                            item.que
                                .replace("x", " x ")
                                .replace("×", " x ")
                                .replace("/", " ÷ ")
                                .replace("÷", " ÷ ")
                        )
                        append(" = ")
                    }

                    // Answer part
                    withStyle(
                        style = SpanStyle(
                            color = item.statusQue.color,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold))
                        )
                    ) {
                        append(item.userAnswer.toString())
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // 🔹 Correct / Wrong icon
            Icon(
                imageVector = item.statusQue.symbol,
                contentDescription = null,
                tint = item.statusQue.color,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

fun buildExamResult(
    context: Context,
    totalRight: Int,
    totalQuestion: Int
): ExamResultUi {

    val percentage = (totalRight * 100f) / totalQuestion

    val (text, color) = when {
        percentage == 100f -> context.getString(R.string.unstoppable_great_job) to Color(0xFF2E7D32)
        percentage >= 90f -> context.getString(R.string.you_are_brilliant) to Color(0xFF1565C0)
        percentage >= 80f -> context.getString(R.string.you_are_on_the_right_track) to Color(0xFF6A1B9A)
        percentage >= 70f -> context.getString(R.string.good_job) to Color(0xFF827717)
        percentage >= 60f -> context.getString(R.string.not_bad) to Color(0xFFEF6C00)
        else -> context.getString(R.string.better_luck_for_next_time) to Color(0xFFC62828)
    }

    return ExamResultUi(
        title = text,
        titleColor = color,
        rating = (totalRight * 5f) / totalQuestion
    )
}

@Composable
fun StatItem(
    icon: Int,
    value: String,
    label: Int,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.height(dimensionResource(R.dimen.activity_padding4)))
        Text(value,
            style = MaterialTheme.typography.titleLarge.copy(color = color, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily(Font(R.font.font_extra_bold))))
        Text(stringResource(label),
            style = MaterialTheme.typography.titleSmall.copy(color = color.copy(alpha = 0.8f), fontWeight = FontWeight.Medium, fontFamily = FontFamily(Font(R.font.font_medium))))
    }
}
