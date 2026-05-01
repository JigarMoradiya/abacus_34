package com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
fun MultiplicationAbacusItem(uiState: AbacusDoPracticeUiState) {

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
            Row(
                horizontalArrangement = Arrangement.Center) {
                abacus.num1.forEachIndexed { index, digit ->
                    Text(
                        text = digit.toString(),
                        style = MaterialTheme.typography.titleLarge.scaled(),
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.font_bold)),
                        color = if (!uiState.isStepByStep)
                            Color.Black
                        else  if (index == uiState.currentIndexNum1)
                            colorPreset.buttonColor
                        else
                            Color.Gray.mixWith(Color.White,0.2f)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "x",
                    modifier = Modifier.padding(end = AppDimens.Dimens4),
                    style = MaterialTheme.typography.titleLarge.scaled(),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.font_bold)),
                    color = if (!uiState.isStepByStep)
                        Color.Black
                    else if (uiState.isSumComplete)
                        Color.Gray.mixWith(Color.White,0.2f)
                    else
                        colorPreset.buttonColor
                )

                abacus.num2.forEachIndexed { index, digit ->
                    Text(
                        text = digit.toString(),
                        style = MaterialTheme.typography.titleLarge.scaled(),
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.font_bold)),
                        color = if (!uiState.isStepByStep)
                            Color.Black
                        else if (index == uiState.currentIndexNum2)
                            colorPreset.buttonColor
                        else
                            Color.Gray.mixWith(Color.White,0.2f)
                    )
                }
            }

            AbacusAnswerUI(uiState)
        }
    }
}

