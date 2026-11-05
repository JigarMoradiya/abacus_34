package com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.subitems

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.runtime.Composable
import com.jigar.me.ui.view.ab.AbChip
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.viewmodels.AbacusDoPracticeUiState
import com.jigar.me.utils.DateTimeUtils

/** The running time of a timed set: a purple kit chip with a clock. */
@Composable
fun SetTimer(uiState: AbacusDoPracticeUiState) {
    uiState.currentSetTime?.let { seconds ->
        AbChip(
            text = DateTimeUtils.displayDurationHourMinSec(seconds),
            tone = AbTone.Purple,
            icon = Icons.Outlined.Timer
        )
    }
}
