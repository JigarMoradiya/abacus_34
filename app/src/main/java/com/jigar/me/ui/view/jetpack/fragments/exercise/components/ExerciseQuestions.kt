package com.jigar.me.ui.view.jetpack.fragments.exercise.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.core.presentation.components.SecondaryButton
import com.jigar.me.ui.view.jetpack.core.presentation.theme.ColorGreen
import com.jigar.me.ui.view.jetpack.fragments.custom_challenge.play.components.IconKeyButton
import com.jigar.me.ui.view.jetpack.fragments.custom_challenge.play.components.KeyButton
import com.jigar.me.ui.view.jetpack.fragments.custom_challenge.play.components.KeypadRow
import com.jigar.me.ui.view.jetpack.fragments.exercise.components.pager.ExercisePagerScreen
import com.jigar.me.ui.view.jetpack.fragments.exercise.viewmodels.ExerciseUiState
import com.jigar.me.ui.view.jetpack.fragments.exercise.viewmodels.ExerciseViewModel

@Composable
fun ExerciseQuestions(
    uiState: ExerciseUiState, viewModel: ExerciseViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Column(
                modifier = Modifier.weight(1f)
                    .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
            ) {
                // show question for multiplication and division
                if (uiState.currentPage != 0 && uiState.currentQueIndex <= uiState.exerciseQuestionList.lastIndex) {
                    val que = uiState.exerciseQuestionList[uiState.currentQueIndex].que+" = ?"
                    Text(
                        que,
                        style = MaterialTheme.typography.headlineSmall.copy(color = Color.Black, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold))),
                    )
                    Spacer(Modifier.height(dimensionResource(R.dimen.activity_padding16)))
                }

                KeypadRow(listOf("1", "2", "3")) { viewModel.addKeyboardValue(it) }
                Spacer(Modifier.height(dimensionResource(R.dimen.activity_padding6)))

                KeypadRow(listOf("4", "5", "6")) { viewModel.addKeyboardValue(it) }
                Spacer(Modifier.height(dimensionResource(R.dimen.activity_padding6)))

                KeypadRow(listOf("7", "8", "9")) { viewModel.addKeyboardValue(it) }
                Spacer(Modifier.height(dimensionResource(R.dimen.activity_padding6)))

                Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.activity_padding4))) {
                    KeyButton("C") { viewModel.clearKeyboard() }
                    KeypadRow(listOf("0")) { viewModel.addKeyboardValue(it) }
                    IconKeyButton(Icons.AutoMirrored.Filled.Backspace) {
                        viewModel.eraseKeyboardValue()
                    }
                }
            }
            // show question for addition - subtraction
            if (uiState.currentPage == 0 && uiState.currentQueIndex <= uiState.exerciseQuestionList.lastIndex) {
                Column(
                    horizontalAlignment = Alignment.End, modifier = Modifier.padding(end = dimensionResource(R.dimen.activity_padding16))
                ) {
                    uiState.exerciseQuestionList[uiState.currentQueIndex].que.split(Regex("(?=[+-])")).forEach {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold))),
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.activity_padding16)), verticalAlignment = Alignment.CenterVertically
        ) {

            Text("Answer:",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold))))

            Spacer(Modifier.width(dimensionResource(R.dimen.activity_padding8)))

            Text(
                text = uiState.answerText,
                style = MaterialTheme.typography.bodyLarge.copy(color = ColorGreen, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))),
            )

            Spacer(Modifier.weight(1f))

            SecondaryButton(text = stringResource(R.string.next), color = ColorGreen, onClick = {
                viewModel.nextQuestion()
            })
        }
        Spacer(Modifier.height(dimensionResource(R.dimen.activity_padding12)))
    }
}
