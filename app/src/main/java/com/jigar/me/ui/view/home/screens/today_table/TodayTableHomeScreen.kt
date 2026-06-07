package com.jigar.me.ui.view.home.screens.today_table

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
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.ToolbarIconSize
import com.jigar.me.ui.view.home.theme.PrimaryBlue

@Composable
fun TodayTableHomeScreen(
    tableNumber: Int,
    onNavigateToDrill: () -> Unit,
    onNavigateToFlashcard: () -> Unit,
    onNavigateToFillBlank: () -> Unit,
    onBackClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize()) {
            BackButtonWithText(title = "$tableNumber Times Table", onBackClick = onBackClick)
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                // Left panel — 35% — scrollable table with overflow banner
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.35f)
                ) {
                    TableDisplayCard(tableNumber = tableNumber)
                }
                // Vertical divider
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(Color.Gray.copy(alpha = 0.2f))
                )
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
        ActivityCard(
            icon = "🎯",
            title = "Learn & Drill",
            subtitle = "Study the table, then tackle 10 questions",
            gradient = listOf(Color(0xFF1565C0), Color(0xFF1976D2)),
            onClick = onDrill
        )
        ActivityCard(
            icon = "🃏",
            title = "Flashcard Quiz",
            subtitle = "Flip cards — Got it or Try Again",
            gradient = listOf(Color(0xFF6A1B9A), Color(0xFF7B1FA2)),
            onClick = onFlashcard
        )
        ActivityCard(
            icon = "✏️",
            title = "Fill the Blanks",
            subtitle = "Pick the missing answer from 4 choices",
            gradient = listOf(Color(0xFF00695C), Color(0xFF00897B)),
            onClick = onFillBlank
        )
    }
}

@Composable
private fun ActivityCard(
    icon: String,
    title: String,
    subtitle: String,
    gradient: List<Color>,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
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
                style = MaterialTheme.typography.headlineSmall.scaled(),
                modifier = Modifier
                    .size(ToolbarIconSize)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(Dimens12))
                    .wrapContentSize(Alignment.Center)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.scaled(),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.scaled(),
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
