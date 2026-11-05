package com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jigar.me.ui.view.ab.AbChalkText
import com.jigar.me.ui.view.ab.AbChalkTone
import com.jigar.me.ui.view.ab.AbQuestionBoard
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.subitems.AbacusAnswerUI
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.viewmodels.AbacusDoPracticeUiState


/** The addition / subtraction sum written on the chalkboard: one line per step, the step being worked on lit. */
@Composable
fun AddSubAbacusItem(uiState: AbacusDoPracticeUiState) {
    val currentAbacus = uiState.currentAbacus
    currentAbacus?.let { abacus ->
        AbQuestionBoard {
            // The sum is written like a school sum: every number ends on the right, so ones sit under ones, tens under tens
            // The column is only as wide as its widest number (the dash does not stretch it), so the whole sum sits in the middle of the board
            Column(modifier = Modifier.width(IntrinsicSize.Max), horizontalAlignment = Alignment.End) {
                abacus.operationStepsStringsArray.forEachIndexed { index, step ->
                    val tone = if (!uiState.isStepByStep) {
                        AbChalkTone.Normal
                    } else if (index == uiState.currentIndexOfOperation && !uiState.isSumComplete) {
                        AbChalkTone.Active
                    } else {
                        AbChalkTone.Dim
                    }
                    AbChalkText(text = step, tone = tone, pill = true, tabular = true)
                }

                AbacusAnswerUI(uiState, trailing = true)
            }
        }
    }
}
