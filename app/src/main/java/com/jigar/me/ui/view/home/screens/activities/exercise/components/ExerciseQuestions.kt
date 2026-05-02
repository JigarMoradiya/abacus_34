package com.jigar.me.ui.view.home.screens.activities.exercise.components

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorDARKGreen
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.common_ui.buttons.KidsKeyPad
import com.jigar.me.ui.view.home.screens.activities.ccm.play.components.KeypadRow
import com.jigar.me.ui.view.home.screens.activities.exercise.viewmodels.ExerciseUiState
import com.jigar.me.ui.view.home.screens.activities.exercise.viewmodels.ExerciseViewModel
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens32
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens6
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.AppDimens.keyPadHeight
import com.jigar.me.ui.view.home.theme.ButtonType

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
                        style = (if (DeviceInfo.isTablet)MaterialTheme.typography.displayMedium else MaterialTheme.typography.headlineLarge).scaled().copy(color = Color.Black, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold))),
                    )
                    Spacer(Modifier.height(if (DeviceInfo.isTablet)Dimens32 else Dimens16))
                }

                KeypadRow(listOf("1", "2", "3")) { viewModel.addKeyboardValue(it) }
                Spacer(Modifier.height(Dimens6))

                KeypadRow(listOf("4", "5", "6")) { viewModel.addKeyboardValue(it) }
                Spacer(Modifier.height(Dimens6))

                KeypadRow(listOf("7", "8", "9")) { viewModel.addKeyboardValue(it) }
                Spacer(Modifier.height(Dimens6))

                Row(horizontalArrangement = Arrangement.spacedBy(Dimens4)) {
                    KidsKeyPad(
                        text = "C",
                        type = ButtonType.RED,
                        width = keyPadHeight,
                        height = keyPadHeight,
                        onClick = { viewModel.clearKeyboard() }
                    )
                    KidsKeyPad(
                        text = "0",
                        type = ButtonType.GREEN,
                        width = keyPadHeight,
                        height = keyPadHeight,
                        onClick = { viewModel.addKeyboardValue("0") }
                    )
                    KidsKeyPad(
                        icon = painterResource(R.drawable.ic_backspace),
                        type = ButtonType.RED,
                        width = keyPadHeight,
                        height = keyPadHeight,
                        onClick = { viewModel.eraseKeyboardValue() }
                    )
                }
            }
            // show question for addition - subtraction
            if (uiState.currentPage == 0 && uiState.currentQueIndex <= uiState.exerciseQuestionList.lastIndex) {
                Column(
                    horizontalAlignment = Alignment.End, modifier = Modifier.padding(end = Dimens16)
                ) {
                    uiState.exerciseQuestionList[uiState.currentQueIndex].que.split(Regex("(?=[+-])")).forEach {
                        Text(
                            it,
                            style = (if (DeviceInfo.isTablet) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge).scaled().copy(color = Color.Black, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold))),
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens16), verticalAlignment = Alignment.CenterVertically
        ) {

            Text("Answer:",
                style = MaterialTheme.typography.bodyMedium.scaled().copy(color = Color.Black, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold))))

            Spacer(Modifier.width(Dimens8))

            Text(
                text = uiState.answerText,
                style = MaterialTheme.typography.bodyLarge.scaled().copy(color = ColorDARKGreen, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))),
            )

            Spacer(Modifier.weight(1f))

            KidsActionButton(
                text = stringResource(R.string.next),
                icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                type = ButtonType.ORANGE,
                isSmall = true,
                isIconStart = false,
                onClick = {
                    viewModel.nextQuestion()
                }
            )
        }
        Spacer(Modifier.height(Dimens12))
    }
}
