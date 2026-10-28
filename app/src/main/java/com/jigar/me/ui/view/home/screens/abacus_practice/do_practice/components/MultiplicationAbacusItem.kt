package com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jigar.me.ui.view.ab.AbChalkText
import com.jigar.me.ui.view.ab.AbChalkTone
import com.jigar.me.ui.view.ab.AbQuestionBoard
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.subitems.AbacusAnswerUI
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.viewmodels.AbacusDoPracticeUiState
import com.jigar.me.ui.view.home.theme.AppDimens

/** The multiplication sum written on the chalkboard: the digit being multiplied (and the one it is multiplied by) lit. */
@Composable
fun MultiplicationAbacusItem(uiState: AbacusDoPracticeUiState) {
    val currentAbacus = uiState.currentAbacus
    currentAbacus?.let { abacus ->
        val plain = !uiState.isStepByStep || uiState.isSumComplete
        AbQuestionBoard {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row {
                    abacus.num1.forEachIndexed { index, digit ->
                        AbChalkText(
                            text = digit.toString(),
                            tone = if (plain) AbChalkTone.Normal
                            else if (index == uiState.currentIndexNum1) AbChalkTone.Active
                            else AbChalkTone.Dim
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    AbChalkText(
                        text = "x",
                        tone = if (plain) AbChalkTone.Normal else AbChalkTone.Active,
                        modifier = Modifier.padding(end = AppDimens.Dimens4)
                    )

                    abacus.num2.forEachIndexed { index, digit ->
                        AbChalkText(
                            text = digit.toString(),
                            tone = if (plain) AbChalkTone.Normal
                            else if (index == uiState.currentIndexNum2) AbChalkTone.Active
                            else AbChalkTone.Dim
                        )
                    }
                }

                AbacusAnswerUI(uiState)
            }
        }
    }
}
