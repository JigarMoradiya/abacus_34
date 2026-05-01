package com.jigar.me.ui.view.home.screens.category.do_practice.components

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.screens.category.do_practice.components.subitems.AbacusAnswerUI
import com.jigar.me.ui.view.home.screens.category.do_practice.viewmodels.AbacusDoPracticeUiState
import com.jigar.me.utils.extensions.mixWith

@Composable
fun DivisionAbacusItem(
    uiState: AbacusDoPracticeUiState
) {
    val colorPreset = uiState.currentColorPresetModel
    val abacus = uiState.currentAbacus ?: return

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp),
        modifier = Modifier
            .width(AppDimens.Dimens80)
            .padding(end = AppDimens.Dimens4)
            .border(
                width = AppDimens.Dimens4,
                color = colorPreset.columnColors.mixWith(Color.White, 0.7f),
                shape = RoundedCornerShape(AppDimens.Dimens10)
            )
            .padding(top = AppDimens.Dimens8)
    ) {

        /* ───────────────────────
         * Dividend (displayDividendArray)
         * ─────────────────────── */
        Row(horizontalArrangement = Arrangement.Center) {
            abacus.displayDividendArray.forEachIndexed { index, value ->
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.titleLarge.scaled(),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.font_bold)),
                    color =
                        if (!uiState.isStepByStep)
                            Color.Black
                        else if (uiState.currentIndexOfOperation == index)
                            colorPreset.buttonColor
                        else
                            Color.Gray.mixWith(Color.White,0.2f)
                )
            }
        }

        /* ───────────────────────
         * ÷ Divisor
         * ─────────────────────── */
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "÷",
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

            Text(
                text = abacus.divisor.toString(),
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
        }

        if (uiState.isStepByStep){
            /* ───────────────────────
             * Step-wise Remainders
             * ─────────────────────── */
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                val lastIndex =
                    if (uiState.isSumComplete)
                        abacus.eachStepRemainder.size - 1
                    else
                        uiState.currentIndexOfOperation.coerceAtLeast(0)

                for (index in 0 until lastIndex) {

                    if (abacus.eachStepRemainder[index] != abacus.eachStepRemainder[index + 1]) {

                        HorizontalDivider(
                            modifier = Modifier.padding(top = AppDimens.Dimens4),
                            thickness = AppDimens.Dimens2,
                            color = colorPreset.columnColors.mixWith(Color.White, 0.5f)
                        )

                        Text(
                            text = abacus.eachStepRemainder[index].toString(),
                            modifier = Modifier
                                .padding(top = AppDimens.Dimens4)
                                .alpha(
                                    if (uiState.isSumComplete)
                                        1f
                                    else if (index <= uiState.currentIndexOfOperation - 1)
                                        1f
                                    else
                                        0f
                                ),
                            style = MaterialTheme.typography.titleLarge.scaled(),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            color =
                                if (uiState.isSumComplete)
                                    Color.Gray.mixWith(Color.White, 0.2f)
                                else
                                    colorPreset.buttonColor
                        )
                    }
                }
            }
        }

        AbacusAnswerUI(uiState)
    }
}
