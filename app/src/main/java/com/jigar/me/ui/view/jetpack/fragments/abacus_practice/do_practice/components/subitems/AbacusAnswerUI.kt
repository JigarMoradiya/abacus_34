package com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.components.subitems

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
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
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.viewmodels.AbacusDoPracticeUiState
import com.jigar.me.utils.AppConstants
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
            modifier = Modifier.padding(top = dimensionResource(R.dimen.activity_padding4)),
            thickness = 2.dp,
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
                .padding(bottom = dimensionResource(R.dimen.activity_padding4))
                .alpha(alphaValue)
        )
    }
}
