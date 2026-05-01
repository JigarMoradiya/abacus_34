package com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.subitems

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.viewmodels.AbacusDoPracticeUiState
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.isNotNullOrEmpty


@Composable
fun AbacusFormulaItem(uiState: AbacusDoPracticeUiState) {
    val colorPreset = uiState.currentColorPresetModel
    val distinctList = if (uiState.currentAbacusType == AppConstants.extras_Comman.AbacusTypeAdditionSubtraction){
        uiState.currentAbacusFormula
            .filter { !it.formulaUsed.isNullOrEmpty() && it.index == uiState.currentIndexOfOperation }
            .distinctBy { it.formulaUsed }
    }else if (uiState.currentAbacusType == AppConstants.extras_Comman.AbacusTypeMultiplication || uiState.currentAbacusType == AppConstants.extras_Comman.AbacusTypeDivision){
        uiState.currentAbacusFormula
            .filter { !it.formulaUsed.isNullOrEmpty()}
            .distinctBy { it.formulaUsed }
    }else{
        emptyList()
    }
    if (distinctList.isNotNullOrEmpty()){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
            modifier = Modifier
                .padding(end = AppDimens.Dimens4)
                .border(
                    width = AppDimens.Dimens1,
                    color = colorPreset.buttonColor.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(AppDimens.Dimens8)
                )
                .background(
                    color = colorPreset.columnColors.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(AppDimens.Dimens8)
                )
                .padding(AppDimens.Dimens8)
        ) {
            distinctList.forEach { item ->
                item.formulaUsed?.let { formulaHint ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Formula Type
                        Text(
                            text = item.formulaType,
                            style = MaterialTheme.typography.bodySmall.scaled(),
                            fontWeight = FontWeight.SemiBold,
                            color = colorPreset.buttonColor,
                            fontFamily = FontFamily(Font(R.font.font_semibold))
                        )

                        // Formula Hint (with Carry)
                        Text(
                            text = buildString {
                                if (item.isCarryFormula) append("Carry: ")
                                append(formulaHint)
                            },
                            style = MaterialTheme.typography.bodyMedium.scaled(),
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontFamily = FontFamily(Font(R.font.font_bold))
                        )
                    }
                }
            }
        }
    }
}

