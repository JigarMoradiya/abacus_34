package com.jigar.me.ui.view.home.screens.activities.exercise.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.jigar.me.ui.view.home.screens.activities.exercise.components.pager.ExercisePagerScreen
import com.jigar.me.ui.view.home.screens.activities.exercise.viewmodels.ExerciseUiState
import com.jigar.me.ui.view.home.screens.activities.exercise.viewmodels.ExerciseViewModel
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText

@Composable
fun ExerciseScreen(
    uiState: ExerciseUiState, viewModel: ExerciseViewModel, onBackClick: () -> Unit) {
    val curve = if (uiState.isAbacusOnLeftHand) 0.dp else 32.dp
    val nonCurve = if (uiState.isAbacusOnLeftHand) 32.dp else 0.dp
    Surface(
        modifier = Modifier.width(dimensionResource(R.dimen.exercise_width)), shape = RoundedCornerShape(
            topStart = nonCurve, bottomStart = nonCurve, topEnd = curve, bottomEnd = curve
        ), color = Color.White, tonalElevation = 0.dp, shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                val alpha = if (uiState.isAbacusOnLeftHand){ 0f } else { 1f }
                BackButtonWithText(title = stringResource(R.string.title_exercises),  modifier = Modifier.alpha(alpha).weight(1f),onBackClick = {
                    onBackClick()
                })

                // show que no when exercise start
                if (uiState.isExerciseStarted) {
                    Text(
                        text = "Q${uiState.currentQueIndex + 1}", style = MaterialTheme.typography.titleMedium.copy(
                            color = ColorPrimary, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))
                        )
                    )
                    Spacer(Modifier.width(dimensionResource(R.dimen.activity_padding16)))
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
