package com.jigar.me.ui.view.jetpack.fragments.exam.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.core.components.HorizontalCheckbox
import com.jigar.me.ui.view.jetpack.core.components.HorizontalRadio
import com.jigar.me.ui.view.jetpack.fragments.exam.home.viewmodels.ExamHomeUiState
import com.jigar.me.ui.view.jetpack.fragments.exam.home.viewmodels.ExamHomeViewModel
import com.jigar.me.utils.AppConstants
import java.util.Locale

@Composable
fun ExamHomeScreen(uiState: ExamHomeUiState, viewModel: ExamHomeViewModel, onStartClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        val gradientBrush = Brush.linearGradient(colors = listOf(Color(0xFF9C27B0), Color(0xFFE991FF)))

        Icon(
            imageVector = Icons.Default.AccessTimeFilled,
            contentDescription = AppConstants.HomeClicks.Menu_CCM,
            modifier = Modifier
                .size(48.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            brush = gradientBrush,
                            size = size,
                            blendMode = BlendMode.SrcAtop
                        )
                    }
                }
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.activity_padding4)))

        Text(
            text = stringResource(R.string.child_level),
            modifier = Modifier,
            style = MaterialTheme.typography.titleLarge.copy(color = Color.Black, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily(Font(R.font.font_extra_bold))),
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.activity_padding16)))

        Text(
            text = stringResource(R.string.select_exam_types),
            modifier = Modifier,
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))),
        )
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.activity_padding16), Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalCheckbox(
                text = stringResource(R.string.Addition), checked = uiState.isAdditionSelected, type = AppConstants.EXAM.isAdditionSelected, onCheckedChange = viewModel::updateValues
            )
            HorizontalCheckbox(
                text = stringResource(R.string.Subtraction), checked = uiState.isSubtractionSelected, type = AppConstants.EXAM.isSubtractionSelected, onCheckedChange = viewModel::updateValues
            )
            HorizontalCheckbox(
                text = stringResource(R.string.Multiplication), checked = uiState.isMultiplicationSelected, type = AppConstants.EXAM.isMultiplicationSelected, onCheckedChange = viewModel::updateValues
            )
            HorizontalCheckbox(
                text = stringResource(R.string.Division), checked = uiState.isDivisionSelected, type = AppConstants.EXAM.isDivisionSelected, onCheckedChange = viewModel::updateValues
            )
        }
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.activity_padding16)))

        Text(
            text = stringResource(R.string.select_exam_difficulty),
            modifier = Modifier,
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.activity_padding16),
                Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalRadio(
                text = stringResource(R.string.beginner),
                selected = uiState.selectedDifficulty == AppConstants.EXAM.examDifficultyBeginner,
                type = AppConstants.EXAM.examDifficultyBeginner,
                onSelected = viewModel::updateRadioValue
            )

            HorizontalRadio(
                text = stringResource(R.string.intermediate),
                selected = uiState.selectedDifficulty == AppConstants.EXAM.examDifficultyIntermediate,
                type = AppConstants.EXAM.examDifficultyIntermediate,
                onSelected = viewModel::updateRadioValue
            )

            HorizontalRadio(
                text = stringResource(R.string.expert),
                selected = uiState.selectedDifficulty == AppConstants.EXAM.examDifficultyExpert,
                type = AppConstants.EXAM.examDifficultyExpert,
                onSelected = viewModel::updateRadioValue
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.activity_padding12)))

        Surface(
            onClick = { onStartClick() },
            shape = RoundedCornerShape(50.dp),
            color = colorResource(R.color.colorPrimary),
            tonalElevation = 0.dp,
            shadowElevation = dimensionResource(R.dimen.activity_padding8)
        ) {
            Text(
                text = stringResource(R.string.let_s_start).uppercase(Locale.getDefault()), style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))), modifier = Modifier.padding(
                    horizontal = dimensionResource(R.dimen.activity_padding16), vertical = dimensionResource(R.dimen.activity_padding10)
                )
            )
        }
    }
}
