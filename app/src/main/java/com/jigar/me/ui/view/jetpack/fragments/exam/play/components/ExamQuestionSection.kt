package com.jigar.me.ui.view.jetpack.fragments.exam.play.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.fragments.exam.play.viewmodels.ExamPlayUiState
import com.jigar.me.ui.view.jetpack.fragments.exam.play.viewmodels.ExamPlayViewModel

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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ---------- Question ----------
        Text(
            text = "${question.que} = ?",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        // ---------- Blink text ----------
        Text(
            text = stringResource(R.string.TapCorrectAns),
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = colorResource(R.color.back_icon_bg),
            modifier = Modifier
                .padding(top = 8.dp)
                .alpha(blinkAlpha)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ---------- Options ----------
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ExamOptionButton(
                    value = question.option1,
                    bgColor = Color(0xFF4CAF50),
                ) {
                    viewModel.onOptionSelected(it, question.answer)
                }

                ExamOptionButton(
                    value = question.option2,
                    bgColor = Color(0xFFF44336),
                ) {
                    viewModel.onOptionSelected(it, question.answer)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ExamOptionButton(
                    value = question.option3,
                    bgColor = Color(0xFFFF9800),
                ) {
                    viewModel.onOptionSelected(it, question.answer)
                }

                ExamOptionButton(
                    value = question.option4,
                    bgColor = Color(0xFF2196F3),
                ) {
                    viewModel.onOptionSelected(it, question.answer)
                }
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