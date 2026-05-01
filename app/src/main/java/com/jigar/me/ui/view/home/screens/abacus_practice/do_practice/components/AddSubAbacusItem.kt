package com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.subitems.AbacusAnswerUI
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.viewmodels.AbacusDoPracticeUiState
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
                .width(AppDimens.Dimens80)
                .padding(end = AppDimens.Dimens4)
                .border(
                    width = AppDimens.Dimens4,
                    color = colorPreset.columnColors.mixWith(Color.White,0.7f),
                    shape = RoundedCornerShape(AppDimens.Dimens10)
                ).padding(top = AppDimens.Dimens8)
        ) {

            abacus.operationStepsStringsArray.forEachIndexed { index, step ->
                Text(
                    text = step,
                    style = MaterialTheme.typography.titleLarge.scaled(),
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

