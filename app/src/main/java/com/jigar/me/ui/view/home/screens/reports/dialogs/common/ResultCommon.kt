package com.jigar.me.ui.view.home.screens.reports.dialogs.common

import android.content.Context
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.base.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.base.abacus_base.utils.MathUtils
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimaryLight
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.screens.activities.exam.play.exam_generator.ExamGenerator
import com.jigar.me.ui.view.home.screens.activities.exam.play.exam_generator.ExamResultUi
import com.jigar.me.ui.view.home.screens.activities.exam.play.exam_generator.MainQuestionType
import com.jigar.me.ui.view.home.screens.activities.exam.play.exam_generator.QuestionResult
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants


@Composable
fun QuestionExamColumnItemHorizontal(item: QuestionResult, selectedTheme: String) {
    val parts = remember(item.que, item.index, item.que_type) {
        when (item.que_type) {
            MainQuestionType.abacus.name,
            MainQuestionType.missingNumber.name ->
                ExamGenerator.questionPartsFromSign(item.que, item.index ?: 0)

            else -> null
        }
    }
    val abacusCalc = remember(parts?.number) {
        if (parts?.number != null) {
            val numberOfColumns = parts.number.toString().length
            AbacusCalculations(numberOfColumns).apply {
                setAbacusValueFromString(parts.number.toString())
            }
        } else null
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppDimens.Dimens10),
        colors = CardDefaults.cardColors(containerColor = ColorPrimaryLight),
        elevation = CardDefaults.cardElevation(AppDimens.Dimens2)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(
                    horizontal = AppDimens.Dimens8,
                    vertical = AppDimens.Dimens4
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            when (item.que_type) {
                MainQuestionType.abacus.name -> {
                    parts?.let {
                        if (abacusCalc != null){
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Text(
                                    text = buildAnnotatedString {
                                        // Question left part
                                        withStyle(
                                            style = SpanStyle(
                                                color = Color.DarkGray,
                                                fontWeight = FontWeight.SemiBold,
                                                fontFamily = FontFamily(Font(R.font.font_semibold))
                                            )
                                        ) {
                                            append(MathUtils.formatQuestionSpace(parts.left))
                                        }
                                    },
                                    style = MaterialTheme.typography.bodyMedium.scaled(),
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )

                                val numberOfColumns = parts.number.toString().length
                                AbacusWithDecimalCanvas(
                                    selectedTheme = selectedTheme,
                                    screenType = AppConstants.AbacusScreen.screenTypeExamResult,
                                    abacusData = abacusCalc,
                                    numberOfColumns = numberOfColumns,
                                    rodMovement = emptyList(),
                                    showDirectionHint = false,
                                    isBeadSoundOn = false,
                                    isDisplayCurrentAbacusInput = false,
                                    onRodMovementChange = { },
                                    onShowDirectionHintsChange = { },
                                    onShowHighlighterChange = { },
                                    onReset = {},
                                    onNext = {},
                                )

                                Text(
                                    text = buildAnnotatedString {
                                        // Question right part
                                        withStyle(
                                            style = SpanStyle(
                                                color = Color.DarkGray,
                                                fontWeight = FontWeight.SemiBold,
                                                fontFamily = FontFamily(Font(R.font.font_semibold))
                                            )
                                        ) {
                                            append(MathUtils.formatQuestionSpace(parts.right))
                                            append(" = ")
                                        }

                                        // Answer
                                        withStyle(
                                            style = SpanStyle(
                                                color = item.statusQue.color,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontFamily = FontFamily(Font(R.font.font_bold))
                                            )
                                        ) {
                                            append(item.userAnswer)
                                        }
                                    },
                                    style = MaterialTheme.typography.bodyMedium.scaled(),
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
                MainQuestionType.missingNumber.name -> {
                    parts?.let {
                        val newQue = MathUtils.formatQuestion(item.que)
                        val correctAnswer = MathUtils.calculateStringExpression(newQue).toInt()
                        Text(
                            modifier = Modifier.weight(1f),
                            text = buildAnnotatedString {
                                // Question left part
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.DarkGray,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = FontFamily(Font(R.font.font_semibold))
                                    )
                                ) {
                                    append(MathUtils.formatQuestionSpace(parts.left))
                                }

                                // Answer part
                                withStyle(
                                    style = SpanStyle(
                                        color = item.statusQue.color,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily(Font(R.font.font_bold)),
                                        textDecoration = TextDecoration.Underline
                                    )
                                ) {
                                    append(item.userAnswer)
                                }

                                // Question right part
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.DarkGray,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = FontFamily(Font(R.font.font_semibold))
                                    )
                                ) {
                                    append(MathUtils.formatQuestionSpace(parts.right))
                                    append(" = ")
                                }

                                // Answer
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.DarkGray,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = FontFamily(Font(R.font.font_semibold))
                                    )
                                ) {
                                    append(correctAnswer.toString())
                                }
                            },
                            style = MaterialTheme.typography.bodyMedium.scaled(),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                else -> { // MainQuestionType.question.name
                    Text(
                        modifier = Modifier.weight(1f),   // 👈 takes remaining space
                        text = buildAnnotatedString {
                            // Question part
                            withStyle(
                                style = SpanStyle(
                                    color = Color.DarkGray,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily(Font(R.font.font_semibold))
                                )
                            ) {
                                append(MathUtils.formatQuestionSpace(item.que))
                                append(" = ")
                            }

                            // Answer part
                            withStyle(
                                style = SpanStyle(
                                    color = item.statusQue.color,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily(Font(R.font.font_bold))
                                )
                            ) {
                                append(item.userAnswer)
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium.scaled(),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.width(AppDimens.Dimens6))

            // 🔹 Correct / Wrong icon
            Icon(
                imageVector = item.statusQue.symbol,
                contentDescription = null,
                tint = item.statusQue.color,
                modifier = Modifier.size(AppDimens.Dimens16)
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
            modifier = Modifier.size(AppDimens.Dimens32)
        )
        Spacer(Modifier.height(AppDimens.Dimens4))
        Text(value,
            style = MaterialTheme.typography.titleLarge.scaled().copy(color = color, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily(Font(R.font.font_extra_bold))))
        Text(stringResource(label),
            style = MaterialTheme.typography.titleSmall.scaled().copy(color = color.copy(alpha = 0.8f), fontWeight = FontWeight.Medium, fontFamily = FontFamily(Font(R.font.font_medium))))
    }
}
