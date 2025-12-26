package com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.components.subitems.AbacusAnswerUI
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.viewmodels.AbacusDoPracticeUiState
import com.jigar.me.utils.extensions.mixWith


@Composable
fun AddSubAbacusItem(uiState: AbacusDoPracticeUiState) {

    val colorPreset = uiState.currentColorPresetModel
    val currentAbacus = uiState.currentAbacus
    currentAbacus?.let { abacus->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier
                .width(80.dp)
                .padding(end = dimensionResource(R.dimen.activity_padding4))
                .border(
                    width = 4.dp,
                    color = colorPreset.columnColors.mixWith(Color.White,0.7f),
                    shape = RoundedCornerShape(10.dp)
                ).padding(top = dimensionResource(R.dimen.activity_padding8))
        ) {

            abacus.operationStepsStringsArray.forEachIndexed { index, step ->
                Text(
                    text = step,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (!uiState.isStepByStep) {
                        Color.Black
                    } else {
                        if (index == uiState.currentIndexOfOperation)
                            colorPreset.buttonColor
                        else
                            Color.Gray.mixWith(Color.White,0.2f)
                    },
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.font_bold)))
            }

            AbacusAnswerUI(uiState)

        }
    }
}

