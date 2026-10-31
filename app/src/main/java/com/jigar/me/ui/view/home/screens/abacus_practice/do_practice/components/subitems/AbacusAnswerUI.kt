package com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.subitems

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.ab.AbChalkAnswer
import com.jigar.me.ui.view.ab.AbChalkDash
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.viewmodels.AbacusDoPracticeUiState

/**
 * The foot of every question board: the dashed "equals" line and the answer under it. The answer shows once the sum is
 * complete; a formal exam never shows it (its board keeps the "?").
 */
@Composable
fun AbacusAnswerUI(
    uiState: AbacusDoPracticeUiState,
    /** Right-aligned sums (addition / subtraction): the answer ends where the digits above it end. */
    trailing: Boolean = false,
) {
    val abacus = uiState.currentAbacus ?: return

    AbChalkDash(Modifier.padding(top = 5.dp))
    AbChalkAnswer(
        text = abacus.finalAnswer.toString(),
        revealed = uiState.isSumComplete && uiState.isShowSubmitAnswer != true,
        trailing = trailing
    )
}
