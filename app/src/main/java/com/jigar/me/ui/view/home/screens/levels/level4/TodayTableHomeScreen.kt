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
                ScreenTopBar(title = "$tableNumber Times Table", onBack = onBackClick)
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
            title = "Learn & Drill",
            subtitle = "Study the table, then tackle 10 questions",
            accent = Color(0xFF1976D2),
            onClick = onDrill
        )
        AbActivityRow(
            emoji = "🃏",
            title = "Flashcard Quiz",
            subtitle = "Flip cards — Got it or Try Again",
            accent = Color(0xFF00897B),
            onClick = onFlashcard
        )
        AbActivityRow(
            emoji = "✏️",
            title = "Fill the Blanks",
            subtitle = "Pick the missing answer from 4 choices",
            accent = Color(0xFF7B1FA2),
            onClick = onFillBlank
        )
    }
}
