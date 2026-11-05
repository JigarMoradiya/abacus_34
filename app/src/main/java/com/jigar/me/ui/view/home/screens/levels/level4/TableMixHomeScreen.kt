package com.jigar.me.ui.view.home.screens.levels.level4

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.ab.AbActivityRow
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbHeroPanel
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.abFixedStyle
import com.jigar.me.ui.view.home.screens.levels.level3.components.ScreenTopBar
import com.jigar.me.ui.view.home.screens.levels.level3.components.abEmojiStyle
import com.jigar.me.ui.view.home.screens.levels.level3.components.abFont
import com.jigar.me.ui.view.home.screens.levels.level3.components.abResponsive
import com.jigar.me.ui.view.home.screens.levels.level3.components.screenLeftEdge
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens3
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens6
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8

@Composable
fun TableMixHomeScreen(
    tables: List<Int>,
    onNavigateToDrill: () -> Unit,
    onNavigateToFlashcard: () -> Unit,
    onNavigateToFillBlank: () -> Unit,
    onBackClick: () -> Unit,
) {
    val group = MIX_GROUPS.find { it.tables == tables } ?: MIX_GROUPS.first()

    AbTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Twilight)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .screenLeftEdge()
            ) {
                ScreenTopBar(title = "${group.label} Mix", onBack = onBackClick)

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    // Left panel 35% — mix info card
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.35f),
                        contentAlignment = Alignment.Center
                    ) {
                        MixInfoPanel(group = group)
                    }

                    // Right panel 65% — activity cards
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.65f),
                        contentAlignment = Alignment.Center
                    ) {
                        MixActivitiesPanel(
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
private fun MixInfoPanel(group: MixGroup) {
    Box(Modifier.fillMaxWidth().padding(Dimens16)) {
        AbHeroPanel(group.endColor, Modifier.matchParentSize(), padding = 0.dp)
        Column(
            modifier = Modifier.fillMaxWidth().padding(Dimens16),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens8)
        ) {
            Text(
                text = group.emoji,
                style = abEmojiStyle(abResponsive(36f))
            )
            // the title and its line below sit close: Baloo's line box leaves a wide empty band between two lines
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy((-6).dp)) {
                Text(
                    text = group.label,
                    style = abFixedStyle(abResponsive(17f), FontWeight.Black),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "${group.tables.size} tables mixed",
                    style = abFont(11f),
                    color = Color.White.copy(alpha = 0.88f)
                )
            }
            Spacer(Modifier.height(Dimens8))
            // Table number chips — up to 5 per row
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens4),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                group.tables.chunked(5).forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimens4),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row.forEach { n ->
                            Text(
                                text = "×$n",
                                style = abFixedStyle(abResponsive(9f), FontWeight.Bold),
                                color = Color.White,
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.22f), RoundedCornerShape(Dimens4))
                                    .padding(horizontal = Dimens6, vertical = Dimens3)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MixActivitiesPanel(
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
            title = "Mix Drill",
            subtitle = "10 questions from mixed tables",
            accent = Color(0xFF1976D2),
            onClick = onDrill
        )
        AbActivityRow(
            emoji = "🃏",
            title = "Mix Flashcards",
            subtitle = "Flip cards from mixed tables",
            accent = Color(0xFF00897B),
            onClick = onFlashcard
        )
        AbActivityRow(
            emoji = "✏️",
            title = "Mix Fill the Blanks",
            subtitle = "Fill missing answers from mixed tables",
            accent = Color(0xFF7B1FA2),
            onClick = onFillBlank
        )
    }
}
