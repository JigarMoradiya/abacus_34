package com.jigar.me.ui.view.jetpack.fragments.custom_challenge.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.core.components.VerticalCheckbox
import com.jigar.me.ui.view.jetpack.fragments.custom_challenge.home.viewmodels.CCMHomeUiState
import com.jigar.me.ui.view.jetpack.fragments.custom_challenge.home.viewmodels.CCMHomeViewModel
import com.jigar.me.ui.view.jetpack.utils.ui.slider.RangeSlider
import com.jigar.me.ui.view.jetpack.utils.ui.slider.SingleSlider
import com.jigar.me.utils.AppConstants
import java.util.Locale

@Composable
fun CCMHomeScreen(uiState: CCMHomeUiState, viewModel: CCMHomeViewModel, onStartClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.create_custom_challenge_as_per_your_kid_s_ability),
            modifier = Modifier,
            style = MaterialTheme.typography.titleLarge.copy(color = Color.Black, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily(Font(R.font.font_extra_bold))),
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.activity_padding8)))
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.number_of_questions),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold))),
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.activity_padding8)))
            Row(modifier = Modifier.weight(1f)) {
                SingleSlider(
                    isShowText = true, value = uiState.totalQuestion, range = 5f..20f, step = 5, onValueChange = {
                        viewModel.updateValues(AppConstants.CCM.totalQuestion, it)
                    }, modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = dimensionResource(R.dimen.activity_padding8))
                )
                Spacer(Modifier.weight(1f))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.gap_between_two_question_sec),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold))),
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.activity_padding8)))
            Row(modifier = Modifier.weight(1f)) {
                SingleSlider(
                    isShowText = true, value = uiState.questionGap, range = 1f..10f, onValueChange = {
                        viewModel.updateValues(AppConstants.CCM.questionGap, it)
                    }, modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = dimensionResource(R.dimen.activity_padding8))
                )
                Spacer(Modifier.weight(1f))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.question_min_max_length),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold))),
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.activity_padding8)))
            Row(modifier = Modifier.weight(1f)) {
                RangeSlider(
                    startValue = uiState.questionMinLength, endValue = uiState.questionMaxLength, range = 1f..6f, step = 1, onValueChange = { start, end ->
                        viewModel.updateValues(AppConstants.CCM.questionMinLength, start)
                        viewModel.updateValues(AppConstants.CCM.questionMaxLength, end)
                    }, modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = dimensionResource(R.dimen.activity_padding8))
                )

                Spacer(Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.activity_padding8)))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.activity_padding16), Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically
        ) {
            VerticalCheckbox(
                text = stringResource(R.string.question_speak_voice), checked = uiState.isQuestionSpeak, type = AppConstants.CCM.isQuestionSpeak, onCheckedChange = viewModel::updateValues
            )
            VerticalCheckbox(
                text = stringResource(R.string.question_show_in_number), checked = uiState.isQuestionShowNumber, type = AppConstants.CCM.isQuestionShowNumber, onCheckedChange = viewModel::updateValues
            )
            VerticalCheckbox(
                text = stringResource(R.string.question_show_in_word), checked = uiState.isQuestionShowWord, type = AppConstants.CCM.isQuestionShowWord, onCheckedChange = viewModel::updateValues
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
