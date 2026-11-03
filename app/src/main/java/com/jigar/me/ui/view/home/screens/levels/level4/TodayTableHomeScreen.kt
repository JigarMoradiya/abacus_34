package com.jigar.me.ui.view.home.screens.levels.level4

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.jigar.me.ui.view.ab.AbActivityRow
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.home.screens.levels.level3.components.ScreenTopBar
import com.jigar.me.ui.view.home.screens.levels.level3.components.screenLeftEdge
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import androidx.compose.ui.res.stringResource
import com.jigar.me.R

@Composable
fun TodayTableHomeScreen(
    tableNumber: Int,
    onNavigateToDrill: () -> Unit,
    onNavigateToFlashcard: () -> Unit,
    onNavigateToFillBlank: () -> Unit,
    onBackClick: () -> Unit,
) {
    AbTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Twilight)
            Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).screenLeftEdge()) {
                ScreenTopBar(title = stringResource(R.string.l4_times_table, tableNumber), onBack = onBackClick)
                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    // Left panel — 35% — scrollable table with overflow banner
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.35f)
                    ) {
                        TableDisplayCard(tableNumber = tableNumber)
                    }
                    // Right panel — 65% — activities vertically centered
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.65f),
                        contentAlignment = Alignment.Center
                    ) {
                        ActivitiesPanel(
                            onDrill = onNavigateToDrill,
                            onFlashcard = onNavigateToFlashcard,
                            onFillBlank = onNavigateToFillBlank
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivitiesPanel(
    onDrill: () -> Unit,
    onFlashcard: () -> Unit,
    onFillBlank: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens12),
        verticalArrangement = Arrangement.spacedBy(Dimens12)
    ) {
        AbActivityRow(
            emoji = "🎯",
            title = stringResource(R.string.l4_learn_drill),
            subtitle = stringResource(R.string.l4_learn_drill_sub),
            accent = Color(0xFF1976D2),
            onClick = onDrill
        )
        AbActivityRow(
            emoji = "🃏",
            title = stringResource(R.string.l4_flashcard_quiz),
            subtitle = stringResource(R.string.l4_flashcard_quiz_sub),
            accent = Color(0xFF00897B),
            onClick = onFlashcard
        )
        AbActivityRow(
            emoji = "✏️",
            title = stringResource(R.string.l4_fill_blanks),
            subtitle = stringResource(R.string.l4_fill_blanks_sub),
            accent = Color(0xFF7B1FA2),
            onClick = onFillBlank
        )
    }
}
