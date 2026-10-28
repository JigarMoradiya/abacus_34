package com.jigar.me.ui.view.home.screens.activities.exercise.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbChip
import com.jigar.me.ui.view.ab.AbColors
import com.jigar.me.ui.view.ab.AbFitToSpace
import com.jigar.me.ui.view.ab.AbShrinkText
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.home.screens.activities.ccm.play.components.KeypadRow
import com.jigar.me.ui.view.home.screens.activities.exercise.viewmodels.ExerciseUiState
import com.jigar.me.ui.view.home.screens.activities.exercise.viewmodels.ExerciseViewModel
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens10
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8

@Composable
fun ExerciseQuestions(
    uiState: ExerciseUiState, viewModel: ExerciseViewModel
) {
    val colors = LocalAbColors.current
    val scale = LocalAbScale.current
    val s = scale.factor
    val page = uiState.currentPage
    val currentQuestion = uiState.exerciseQuestionList.getOrNull(uiState.currentQueIndex)
    // Wide enough for a signed seven-digit line, narrower than the room beside the keypad so the keypad stays centred.
    // Multiplication and division write the whole sum above the keypad, so their side column is only the "Q" chip: it
    // takes no width and the keypad column gets the full panel.
    val questionColumnWidth = if (page == 0) 18.dp * s * 4.4f else 0.dp
    // room kept free under the question lines for the Answer / Next row
    val answerRowReserve = 46.dp * s + Dimens16 * 2 + Dimens8

    Box(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxHeight()) {
            Spacer(Modifier.weight(1f))

            // Keypad buttons, centred vertically
            Column(
                Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens8, Alignment.CenterVertically),
            ) {
                // show question for multiplication and division
                if (page != 0 && currentQuestion != null) {
                    AbShrinkText(
                        text = currentQuestion.que + " = ?",
                        style = AbTypography.fixed(28f * s, FontWeight.ExtraBold),
                        color = colors.textPrimary,
                        modifier = Modifier.padding(horizontal = Dimens12).padding(bottom = 30.dp),
                        minScale = 0.6f,
                        maxLines = 1,
                    )
                }
                val onKey: (String) -> Unit = {
                    when (it) {
                        "C" -> viewModel.clearKeyboard()
                        "⌫" -> viewModel.eraseKeyboardValue()
                        else -> viewModel.addKeyboardValue(it)
                    }
                }
                KeypadRow(listOf("1", "2", "3"), onKey)
                KeypadRow(listOf("4", "5", "6"), onKey)
                KeypadRow(listOf("7", "8", "9"), onKey)
                KeypadRow(listOf("C", "0", "⌫"), onKey)
            }

            Spacer(Modifier.weight(1f))

            // Question display (right side)
            Column(
                Modifier.padding(end = Dimens16).width(questionColumnWidth).fillMaxHeight(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(Dimens10),
            ) {
                Box(Modifier.padding(top = Dimens10).height(48.dp * s), contentAlignment = Alignment.CenterEnd) {
                    AbChip(
                        text = "Q ${uiState.currentQueIndex + 1} / ${maxOf(uiState.exerciseQuestionList.size, 1)}",
                        tone = AbTone.Purple,
                        modifier = Modifier.wrapContentWidth(Alignment.End, unbounded = true),
                        icon = Icons.AutoMirrored.Filled.Help,
                    )
                }

                // show question for addition - subtraction
                if (page == 0 && currentQuestion != null) {
                    // Ten lines of seven digits are taller than the room above the answer row, so the lines shrink to
                    // fit it (never grow), stay right-aligned and stay clear of the answer row.
                    Box(Modifier.weight(1f).fillMaxWidth().padding(bottom = answerRowReserve)) {
                        AbFitToSpace(Modifier.fillMaxSize(), alignment = Alignment.TopEnd) {
                            Column(horizontalAlignment = Alignment.End) {
                                currentQuestion.que.split(Regex("(?=[+-])")).forEach {
                                    Text(it, style = AbTypography.fixed(20f * s, FontWeight.ExtraBold), color = colors.textPrimary, maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Answer and Next, along the bottom
        Column(Modifier.fillMaxSize().padding(Dimens16)) {
            Spacer(Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // the panel's left edge is rounded on the side the abacus is on, so the answer steps in a little there
                val answerStart = (if (scale.isTablet) Dimens16 else 0.dp) + (if (uiState.isAbacusOnLeftHand) Dimens16 else 0.dp)
                Row(
                    Modifier.padding(start = answerStart),
                    horizontalArrangement = Arrangement.spacedBy(Dimens4),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Answer:", style = AbTypography.fixed(15f * s, FontWeight.Bold), color = colors.textPrimary)
                    Text(
                        text = uiState.answerText.ifEmpty { "0" },
                        style = AbTypography.fixed(20f * s, FontWeight.ExtraBold),
                        color = AbColors.Light.accentGreen.abDarkened(0.12f),
                    )
                }

                Spacer(Modifier.weight(1f))

                AbButton(
                    text = stringResource(R.string.next),
                    tone = AbTone.Warning,
                    size = AbSize.Small,
                    icon = Icons.Default.ChevronRight,
                    iconAtEnd = true,
                    onClick = { viewModel.nextQuestion() },
                )
            }
        }
    }
}
