package com.jigar.me.ui.view.home.screens.today_table

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens10
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens20
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
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
                    TablePanel(tableNumber = tableNumber)
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
private fun TablePanel(tableNumber: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .padding(top = Dimens12, start = Dimens12, end = Dimens12, bottom = Dimens12),
            contentAlignment = Alignment.TopCenter
        ) {
            // White card with internal spacer to make room for banner
            Card(
                shape = RoundedCornerShape(Dimens16),
                elevation = CardDefaults.cardElevation(AppDimens.Dimens4),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Spacer(Modifier.height(ToolbarIconSize * 0.55f))
                    (1..10).forEach { i ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (i % 2 != 0) PrimaryBlue.copy(alpha = 0.06f) else Color.Transparent
                                )
                                .padding(vertical = Dimens8, horizontal = Dimens16),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$tableNumber × $i",
                                style = MaterialTheme.typography.bodySmall.scaled(),
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black.copy(alpha = 0.75f)
                            )
                            Text(
                                text = " = ",
                                style = MaterialTheme.typography.bodySmall.scaled(),
                                color = Color.Gray
                            )
                            Text(
                                text = "${tableNumber * i}",
                                style = MaterialTheme.typography.bodySmall.scaled(),
                                fontWeight = FontWeight.Black,
                                color = PrimaryBlue
                            )
                        }
                    }
                }
            }
            // Banner overflows above card (offset moves it up beyond card's top edge)
            Box(
                modifier = Modifier
                    .offset(y = -(ToolbarIconSize * 0.22f))
                    .shadow(Dimens4, RoundedCornerShape(Dimens8))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(PrimaryBlue, PrimaryBlue.copy(alpha = 0.85f))
                        ),
                        shape = RoundedCornerShape(Dimens8)
                    )
                    .padding(horizontal = Dimens20, vertical = Dimens4)
            ) {
                Text(
                    text = "×$tableNumber",
                    style = MaterialTheme.typography.bodyMedium.scaled(),
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
        Spacer(Modifier.height(Dimens12))
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
