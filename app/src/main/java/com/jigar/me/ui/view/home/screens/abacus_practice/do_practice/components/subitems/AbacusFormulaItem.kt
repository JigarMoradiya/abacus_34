package com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.subitems

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.ab.AbHintCard
import com.jigar.me.ui.view.ab.AbHintKind
import com.jigar.me.ui.view.ab.AbHintTag
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abFixedStyle
import com.jigar.me.ui.view.ab.hintInk
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.viewmodels.AbacusDoPracticeUiState
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.isNotNullOrEmpty

/**
 * The hint card (a sticky note) with the formula, or the multiplication table row, that goes with the step being worked
 * on. Shows nothing while there is no hint or once the sum is complete; the panel keeps the card's place meanwhile.
 */
@Composable
fun AbacusFormulaItem(uiState: AbacusDoPracticeUiState) {
    if (uiState.isSumComplete) return
    val colors = LocalAbColors.current
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
        // DimensionsUtils.formulaHintTextSize: 12 on a phone, 20 / 24 on the two tablet classes
        val hintSize = (if (DeviceInfo.isLargeTablet) 24f else if (DeviceInfo.isTablet) 20f else 12f) * 1.3f
        AbHintCard {
            distinctList.forEach { item ->
                item.formulaUsed?.let { formulaHint ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        // Formula Type
                        AbHintTag(title = item.formulaType, kind = AbHintKind.fromFormulaType(item.formulaType))

                        // Formula Hint (with Carry)
                        Text(
                            text = buildString {
                                if (item.isCarryFormula) append("Carry: ")
                                append(formulaHint)
                            },
                            style = abFixedStyle(hintSize, FontWeight.ExtraBold),
                            color = colors.hintInk
                        )
                    }
                }
            }
        }
    }
}
