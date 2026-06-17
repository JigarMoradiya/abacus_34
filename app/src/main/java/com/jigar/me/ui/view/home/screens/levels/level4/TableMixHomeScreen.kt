package com.jigar.me.ui.view.home.screens.levels.level4

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens20
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.AppDimens.ToolbarIconSize

@Composable
fun TableMixHomeScreen(
    tables: List<Int>,
    onNavigateToDrill: () -> Unit,
    onNavigateToFlashcard: () -> Unit,
    onNavigateToFillBlank: () -> Unit,
    onBackClick: () -> Unit,
) {
    val group = MIX_GROUPS.find { it.tables == tables } ?: MIX_GROUPS.first()

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            BackButtonWithText(title = "${group.label} Mix", onBackClick = onBackClick)

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

                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(Color.Gray.copy(alpha = 0.2f))
                )

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

@Composable
private fun MixInfoPanel(group: MixGroup) {
    val isTablet = DeviceInfo.isTablet
    val shape = RoundedCornerShape(Dimens20)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens16)
            .shadow(elevation = 8.dp, shape = shape)
            .background(Brush.linearGradient(listOf(group.startColor, group.endColor)), shape)
            .padding(Dimens20),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens8)
    ) {
        Text(
            text = group.emoji,
            style = if (isTablet) MaterialTheme.typography.displaySmall.scaled()
                    else MaterialTheme.typography.headlineLarge.scaled()
        )
        Text(
            text = group.label,
            style = if (isTablet) MaterialTheme.typography.headlineMedium.scaled()
                    else MaterialTheme.typography.headlineSmall.scaled(),
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Text(
            text = "${group.tables.size} tables mixed",
            style = if (isTablet) MaterialTheme.typography.bodyLarge.scaled()
                    else MaterialTheme.typography.bodyMedium.scaled(),
            color = Color.White.copy(alpha = 0.85f)
        )
        Spacer(Modifier.height(AppDimens.Dimens4))
        // Table number chips — up to 5 per row
        group.tables.chunked(5).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4),
                verticalAlignment = Alignment.CenterVertically
            ) {
                row.forEach { n ->
                    Surface(
                        color = Color.White.copy(alpha = 0.22f),
                        shape = RoundedCornerShape(AppDimens.Dimens6)
                    ) {
                        Text(
                            text = "×$n",
                            style = MaterialTheme.typography.labelSmall.scaled(),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = AppDimens.Dimens6, vertical = AppDimens.Dimens4)
                        )
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
        MixActivityCard(
            icon = "🎯",
            title = "Mix Drill",
            subtitle = "10 questions from mixed tables",
            gradient = listOf(Color(0xFF1565C0), Color(0xFF1976D2)),
            onClick = onDrill
        )
        MixActivityCard(
            icon = "🃏",
            title = "Mix Flashcards",
            subtitle = "Flip cards from mixed tables",
            gradient = listOf(Color(0xFF00695C), Color(0xFF00897B)),
            onClick = onFlashcard
        )
        MixActivityCard(
            icon = "✏️",
            title = "Mix Fill the Blanks",
            subtitle = "Fill missing answers from mixed tables",
            gradient = listOf(Color(0xFF6A1B9A), Color(0xFF7B1FA2)),
            onClick = onFillBlank
        )
    }
}

@Composable
private fun MixActivityCard(
    icon: String,
    title: String,
    subtitle: String,
    gradient: List<Color>,
    onClick: () -> Unit,
) {
    val isTablet = DeviceInfo.isTablet
    Card(
        onClick = { AudioPlayerManager.playSoundBtnClick(); onClick() },
        shape = RoundedCornerShape(Dimens16),
        elevation = CardDefaults.cardElevation(AppDimens.Dimens4),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(gradient))
                .padding(Dimens16),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens16)
        ) {
            Text(
                text = icon,
                style = if (isTablet) MaterialTheme.typography.headlineMedium.scaled()
                        else MaterialTheme.typography.headlineSmall.scaled(),
                modifier = Modifier
                    .size(ToolbarIconSize)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(Dimens12))
                    .wrapContentSize(Alignment.Center)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = if (isTablet) MaterialTheme.typography.titleLarge.scaled()
                            else MaterialTheme.typography.titleMedium.scaled(),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    style = if (isTablet) MaterialTheme.typography.bodyMedium.scaled()
                            else MaterialTheme.typography.bodySmall.scaled(),
                    color = Color.White.copy(alpha = 0.88f)
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}
