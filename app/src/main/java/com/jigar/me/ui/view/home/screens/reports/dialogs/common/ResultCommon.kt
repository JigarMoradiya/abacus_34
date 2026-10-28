package com.jigar.me.ui.view.home.screens.reports.dialogs.common

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AB_SF_TO_VECTOR
import com.jigar.me.ui.view.ab.AbColors
import com.jigar.me.ui.view.ab.AbHeroBadge
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbShrinkText
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.base.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.base.abacus_base.utils.MathUtils
import com.jigar.me.ui.view.home.screens.activities.exam.play.exam_generator.ExamGenerator
import com.jigar.me.ui.view.home.screens.activities.exam.play.exam_generator.ExamResultUi
import com.jigar.me.ui.view.home.screens.activities.exam.play.exam_generator.MainQuestionType
import com.jigar.me.ui.view.home.screens.activities.exam.play.exam_generator.QuestionResult
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants

/** One question of the result list: the question with the child's answer, and a tick or cross, on a plate edged in that status colour. */
@Composable
fun QuestionExamColumnItemHorizontal(item: QuestionResult, selectedTheme: String) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
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
    // right = green, wrong = red, not answered = orange, from the kit so both modes stay readable
    val status = when (item.statusQue.symbol) {
        Icons.Default.CheckCircle -> colors.accentGreen
        Icons.Default.RemoveCircle -> colors.accentOrange
        else -> colors.danger
    }
    val plain = SpanStyle(color = colors.textPrimary, fontWeight = FontWeight.SemiBold)
    val answer = SpanStyle(color = status, fontWeight = FontWeight.Bold)
    val textStyle = AbTypography.body(s)
    val shape = RoundedCornerShape(AbShapes.sm * s)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surfaceTint.copy(alpha = if (colors.isDark) 0.5f else 0.7f), shape)
            .border(2.dp, status.copy(alpha = 0.55f), shape)
            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)
    ) {

        when (item.que_type) {
            MainQuestionType.abacus.name -> {
                if (parts != null && abacusCalc != null) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)
                    ) {
                        Text(
                            text = buildAnnotatedString { withStyle(plain) { append(MathUtils.formatQuestionSpace(parts.left)) } },
                            style = textStyle
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
                                withStyle(plain) {
                                    append(MathUtils.formatQuestionSpace(parts.right))
                                    append(" = ")
                                }
                                withStyle(answer) { append(item.userAnswer) }
                            },
                            style = textStyle
                        )
                    }
                }
            }
            MainQuestionType.missingNumber.name -> {
                if (parts != null) {
                    val newQue = MathUtils.formatQuestion(item.que)
                    val correctAnswer = MathUtils.calculateStringExpression(newQue).toInt()
                    Text(
                        modifier = Modifier.weight(1f),
                        text = buildAnnotatedString {
                            withStyle(plain) { append(MathUtils.formatQuestionSpace(parts.left)) }
                            withStyle(answer.copy(textDecoration = TextDecoration.Underline)) { append(item.userAnswer) }
                            withStyle(plain) {
                                append(MathUtils.formatQuestionSpace(parts.right))
                                append(" = ")
                            }
                            withStyle(SpanStyle(color = colors.textPrimary, fontWeight = FontWeight.Bold)) { append(correctAnswer.toString()) }
                        },
                        style = textStyle
                    )
                }
            }
            else -> { // MainQuestionType.question.name
                Text(
                    modifier = Modifier.weight(1f),
                    text = buildAnnotatedString {
                        withStyle(plain) {
                            append(MathUtils.formatQuestionSpace(item.que))
                            append(" = ")
                        }
                        withStyle(answer) { append(item.userAnswer) }
                    },
                    style = textStyle
                )
            }
        }

        // Correct / Wrong icon
        Icon(
            imageVector = item.statusQue.symbol,
            contentDescription = null,
            tint = status,
            modifier = Modifier.size((20f * AB_SF_TO_VECTOR).dp * s)
        )
    }
}

/** The verdict under the score: a line to cheer the child on, in a colour that grows warmer with the result. */
fun buildExamResult(
    context: Context,
    totalRight: Int,
    totalQuestion: Int
): ExamResultUi {

    val percentage = (totalRight * 100f) / totalQuestion

    val (text, color) = when {
        percentage == 100f -> context.getString(R.string.unstoppable_great_job) to AbColors.Light.accentGreen
        percentage >= 90f -> context.getString(R.string.you_are_brilliant) to AbColors.Light.brandPrimary
        percentage >= 80f -> context.getString(R.string.you_are_on_the_right_track) to AbColors.Light.accentPurple
        percentage >= 70f -> context.getString(R.string.good_job) to AbColors.Light.brandSecondary
        percentage >= 60f -> context.getString(R.string.not_bad) to AbColors.Light.accentOrange
        else -> context.getString(R.string.better_luck_for_next_time) to AbColors.Light.danger
    }

    return ExamResultUi(
        title = text,
        titleColor = color,
        rating = (totalRight * 5f) / totalQuestion
    )
}

/** A big figure of the result (the time, the score): a glossy round badge, the value and its label. */
@Composable
fun StatItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp * s)) {
        AbHeroBadge(icon, accent = color, size = 44.dp * s)

        Box(Modifier.height(30.dp * s), contentAlignment = Alignment.Center) {
            AbShrinkText(
                text = value,
                style = AbTypography.fixed(26f * s, FontWeight.ExtraBold),
                color = colors.textPrimary,
                minScale = 0.6f,
                maxLines = 1
            )
        }

        Text(label, style = AbTypography.label(s), color = colors.textSecondary)
    }
}
