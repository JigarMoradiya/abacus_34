package com.jigar.me.ui.view.home.screens.activities.exam.play.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.base.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.base.abacus_base.utils.MathUtils
import com.jigar.me.ui.view.home.common_ui.buttons.KidsOptionButton
import com.jigar.me.ui.view.home.screens.activities.exam.play.exam_generator.ExamGenerator
import com.jigar.me.ui.view.home.screens.activities.exam.play.exam_generator.MainQuestionType
import com.jigar.me.ui.view.home.screens.activities.exam.play.exam_generator.QuestionParts
import com.jigar.me.ui.view.home.screens.activities.exam.play.viewmodels.ExamPlayUiState
import com.jigar.me.ui.view.home.screens.activities.exam.play.viewmodels.ExamPlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.examOptionHeight
import com.jigar.me.ui.view.home.theme.AppDimens.examOptionWidth
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.utils.AppConstants

@Composable
fun ExamQuestionSection(
    uiState: ExamPlayUiState,
    viewModel: ExamPlayViewModel,
    modifier: Modifier = Modifier
) {
    val blinkAlpha by rememberBlinkAlpha()

    // Safety check
    if (uiState.examPaper.isEmpty() || uiState.currentIndex >= uiState.examPaper.size) {
        return
    }

    val question = uiState.examPaper[uiState.currentIndex]
    var parts : QuestionParts? = null
    if (question.queType != MainQuestionType.question.name) {
        parts = ExamGenerator.questionPartsFromSpace(question.que, question.index ?: 0)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimens.Dimens12),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ---------- Question ----------
        if (question.queType == MainQuestionType.question.name){
            Text(
                text = "${question.que} = ?",
                fontSize = 36.sp.scaled(),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        }else{
            val left = parts?.left
            val number = parts?.number
            val right = parts?.right
            Row(
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (!left.isNullOrEmpty()) {
                    Text(
                        text = left,
                        fontSize = 36.sp.scaled(),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = AppDimens.Dimens12)
                    )
                }

                if (question.queType == MainQuestionType.abacus.name){
                    val numberOfColumns = number.toString().length
                    val abacusCalc = AbacusCalculations(numberOfColumns = numberOfColumns)
                    abacusCalc.setAbacusValueFromString(number.toString())
                    AbacusWithDecimalCanvas(
                        selectedTheme = viewModel.selectedTheme,
                        screenType = AppConstants.AbacusScreen.screenTypeExam,
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
                }else{
                    Column(
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "?",
                            fontSize = 36.sp.scaled(),
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )

                        Box(
                            modifier = Modifier
                                .width(AppDimens.Dimens60)
                                .height(AppDimens.Dimens4)
                                .background(Color.Red)
                        )
                    }
                }


                if (!right.isNullOrEmpty()) {
                    Text(
                        text = right,
                        fontSize = 36.sp.scaled(),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = AppDimens.Dimens12)
                    )
                }

                if (question.queType == MainQuestionType.missingNumber.name) {

                    val newQue = MathUtils.formatQuestion(question.que)
                    val correctAnswer = MathUtils.calculateStringExpression(newQue).toInt()

                    Text(
                        text = "= $correctAnswer",
                        fontSize = 36.sp.scaled(),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = AppDimens.Dimens12)
                    )

                } else {

                    Text(
                        text = "= ?",
                        fontSize = 36.sp.scaled(),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = AppDimens.Dimens12)
                    )
                }
            }
        }


        // ---------- Blink text ----------
        Text(
            text = stringResource(R.string.TapCorrectAns),
            style = MaterialTheme.typography.bodyMedium.scaled(),
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0B5960),
            modifier = Modifier
                .padding(top = AppDimens.Dimens16)
                .alpha(blinkAlpha)
        )

        Spacer(modifier = Modifier.height(AppDimens.Dimens16))

        // ---------- Options ----------
        Column(
            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens10),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10)) {
                KidsOptionButton(
                    text = question.option1.toString(),
                    type = ButtonType.TEAL,
                    fontSize = examOptionHeight.value.sp * 0.6,
                    onClick = {
                        viewModel.onOptionSelected(question.option1, question.answer)
                    },
                    modifier = Modifier.width(examOptionWidth).height(examOptionHeight)
                )
                KidsOptionButton(
                    text = question.option2.toString(),
                    type = ButtonType.TEAL,
                    fontSize = examOptionHeight.value.sp * 0.6,
                    onClick = {
                        viewModel.onOptionSelected(question.option2, question.answer)
                    },
                    modifier = Modifier.width(examOptionWidth).height(examOptionHeight)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10)) {
                KidsOptionButton(
                    text = question.option3.toString(),
                    type = ButtonType.TEAL,
                    fontSize = examOptionHeight.value.sp * 0.6,
                    onClick = {
                        viewModel.onOptionSelected(question.option3, question.answer)
                    },
                    modifier = Modifier.width(examOptionWidth).height(examOptionHeight)
                )
                KidsOptionButton(
                    text = question.option4.toString(),
                    type = ButtonType.TEAL,
                    fontSize = examOptionHeight.value.sp * 0.6,
                    onClick = {
                        viewModel.onOptionSelected(question.option4, question.answer)
                    },
                    modifier = Modifier.width(examOptionWidth).height(examOptionHeight)
                )
            }
        }
    }
}


@Composable
fun rememberBlinkAlpha(): State<Float> {
    val infiniteTransition = rememberInfiniteTransition(label = "blink")

    return infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blinkAlpha"
    )
}