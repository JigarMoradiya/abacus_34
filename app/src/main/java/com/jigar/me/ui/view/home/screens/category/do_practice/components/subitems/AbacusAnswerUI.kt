package com.jigar.me.ui.view.home.screens.category.do_practice.components.subitems

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.home.screens.category.do_practice.viewmodels.AbacusDoPracticeUiState
import com.jigar.me.utils.extensions.mixWith

@Composable
fun AbacusAnswerUI(
    uiState: AbacusDoPracticeUiState
) {
    val colorPreset = uiState.currentColorPresetModel
    val abacus = uiState.currentAbacus ?: return

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {

        HorizontalDivider(
            modifier = Modifier.padding(top = AppDimens.Dimens4),
            thickness = AppDimens.Dimens2,
            color = colorPreset.columnColors.mixWith(Color.White, 0.7f)
        )

        val displayText = if (uiState.isShowSubmitAnswer == true) {
            "?"
        } else {
            abacus.finalAnswer.toString()
        }

        val alphaValue = if (uiState.isShowSubmitAnswer == true || uiState.isSumComplete) 1f else 0f
        val style = if (uiState.isShowSubmitAnswer == true) {
            MaterialTheme.typography.headlineLarge
        }else{
            MaterialTheme.typography.titleLarge
        }

        Text(
            text = displayText,
            style = style.copy(
                color = colorPreset.buttonColor,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily(Font(R.font.font_extra_bold))
            ),
            modifier = Modifier
                .padding(bottom = AppDimens.Dimens4)
                .alpha(alphaValue)
        )
    }
}
