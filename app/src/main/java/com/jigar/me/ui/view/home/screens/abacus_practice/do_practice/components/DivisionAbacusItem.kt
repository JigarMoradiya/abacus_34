package com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.ab.AbChalkDash
import com.jigar.me.ui.view.ab.AbChalkText
import com.jigar.me.ui.view.ab.AbChalkTone
import com.jigar.me.ui.view.ab.AbQuestionBoard
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.subitems.AbacusAnswerUI
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.viewmodels.AbacusDoPracticeUiState
import com.jigar.me.ui.view.home.theme.AppDimens

/** The division sum written on the chalkboard: dividend, divisor, the remainders so far and the quotient. */
@Composable
fun DivisionAbacusItem(
    uiState: AbacusDoPracticeUiState
) {
    val abacus = uiState.currentAbacus ?: return
    val plain = !uiState.isStepByStep || uiState.isSumComplete

    AbQuestionBoard {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            /* ───────────────────────
             * Dividend (displayDividendArray)
             * ─────────────────────── */
            Row {
                abacus.displayDividendArray.forEachIndexed { index, value ->
                    AbChalkText(
                        text = value.toString(),
                        tone = if (plain) AbChalkTone.Normal
                        else if (uiState.currentIndexOfOperation == index) AbChalkTone.Active
                        else AbChalkTone.Dim
                    )
                }
            }

            /* ───────────────────────
             * ÷ Divisor
             * ─────────────────────── */
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AbChalkText(text = "÷", tone = if (plain) AbChalkTone.Normal else AbChalkTone.Active)
                AbChalkText(text = abacus.divisor.toString(), tone = if (plain) AbChalkTone.Normal else AbChalkTone.Active)
            }

            if (uiState.isStepByStep) {
                /* ───────────────────────
                 * Step-wise Remainders
                 * ─────────────────────── */
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val lastIndex =
                        if (uiState.isSumComplete)
                            abacus.eachStepRemainder.size - 1
                        else
                            uiState.currentIndexOfOperation.coerceAtLeast(0)

                    for (index in 0 until lastIndex) {

                        if (abacus.eachStepRemainder[index] != abacus.eachStepRemainder[index + 1]) {

                            AbChalkDash(Modifier.padding(top = 5.dp))

                            AbChalkText(
                                text = abacus.eachStepRemainder[index].toString(),
                                tone = if (plain) AbChalkTone.Normal else AbChalkTone.Active,
                                modifier = Modifier
                                    .padding(top = 5.dp)
                                    .alpha(
                                        if (uiState.isSumComplete)
                                            1f
                                        else if (index <= uiState.currentIndexOfOperation - 1)
                                            1f
                                        else
                                            0f
                                    )
                            )
                        }
                    }
                }
            }

            AbacusAnswerUI(uiState)
        }
    }
}
