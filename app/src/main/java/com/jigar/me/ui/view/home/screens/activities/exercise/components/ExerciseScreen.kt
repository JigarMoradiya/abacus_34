package com.jigar.me.ui.view.home.screens.activities.exercise.components

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimary
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.screens.activities.exercise.components.pager.ExercisePagerScreen
import com.jigar.me.ui.view.home.screens.activities.exercise.viewmodels.ExerciseUiState
import com.jigar.me.ui.view.home.screens.activities.exercise.viewmodels.ExerciseViewModel
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.exerciseWidth

@Composable
fun ExerciseScreen(
    uiState: ExerciseUiState, viewModel: ExerciseViewModel, onBackClick: () -> Unit) {
    val curve = if (uiState.isAbacusOnLeftHand) 0.dp else AppDimens.Dimens32
    val nonCurve = if (uiState.isAbacusOnLeftHand) AppDimens.Dimens32 else 0.dp
    val shadowElevation = if (uiState.isAbacusOnLeftHand) AppDimens.Dimens4 else AppDimens.Dimens4
    Surface(
        modifier = Modifier
            .width(exerciseWidth)
            .fillMaxHeight(), shape = RoundedCornerShape(
            topStart = nonCurve, bottomStart = nonCurve, topEnd = curve, bottomEnd = curve
        ), color = Color.White, tonalElevation = 0.dp, shadowElevation = shadowElevation
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val modifier = if (uiState.isAbacusOnLeftHand) {
                Modifier.fillMaxSize()
            }else{
                Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            }

            Column(modifier = modifier) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isVisible = !uiState.isAbacusOnLeftHand
                    val alpha = if (isVisible) 1f else 0f

                    BackButtonWithText(
                        title = stringResource(R.string.title_exercises),
                        modifier = Modifier
                            .alpha(alpha)
                            .weight(1f),
                        enabled = isVisible,
                        onBackClick = {
                            onBackClick()
                        }
                    )

                    // show que no when exercise start
                    if (uiState.isExerciseStarted) {
                        Text(
                            text = "Q${uiState.currentQueIndex + 1}", style = MaterialTheme.typography.titleMedium.scaled().copy(
                                color = ColorPrimary, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))
                            )
                        )
                        Spacer(Modifier.width(Dimens16))
                    }
                }
                if (uiState.isExerciseStarted) {
                    ExerciseQuestions(uiState,viewModel)
                } else {
                    ExercisePagerScreen(uiState, viewModel) { item, index ->
                        viewModel.generateExercise()
                    }
                }
            }
        }
    }

}
