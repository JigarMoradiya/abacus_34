package com.jigar.me.ui.view.home.screens.levels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun Level1HomeScreen(onBackClick: () -> Unit, onNavigateToLesson: (Int) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize()) {
            BackButtonWithText(title = "Bead Basics", onBackClick = onBackClick)

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                val cols        = 3
                val rows        = 2
                val spacing     = AppDimens.Dimens12
                val hPad        = AppDimens.Dimens16
                val vPad        = AppDimens.Dimens12  // top AND bottom padding
                val shadowRoom  = AppDimens.Dimens8   // Spacer after last row so shadow isn't cut

                // cellW × cellH fills available space minus padding and gaps
                val cellW = (maxWidth - hPad * 2 - spacing * (cols - 1)) / cols
                val cellH = (maxHeight - vPad * 2 - spacing * (rows - 1) - shadowRoom) / rows

                // Regular Column+Row (NOT LazyGrid) — avoids lazy-layout shadow clipping
                Column(
                    modifier = Modifier
                        .width(maxWidth - hPad * 2)
                        .padding(vertical = vPad),
                    verticalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    // Row 1 — all 3 lessons, left-aligned
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        level1Lessons.take(cols).forEach { lesson ->
                            LessonCard(lesson, cellW, cellH) { onNavigateToLesson(lesson.id) }
                        }
                    }
                    // Row 2 — remaining 2 lessons, centered horizontally
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally)
                    ) {
                        level1Lessons.drop(cols).forEach { lesson ->
                            LessonCard(lesson, cellW, cellH) { onNavigateToLesson(lesson.id) }
                        }
                    }
                    // Transparent space so last-row card shadows render without clipping
                    Spacer(modifier = Modifier.height(shadowRoom))
                }
            }
        }
    }
}

@Composable
private fun LessonCard(lesson: Level1LessonData, cellW: Dp, cellH: Dp, onClick: () -> Unit) {
    val shape = RoundedCornerShape(AppDimens.Dimens20)
    Box(
        modifier = Modifier
            .size(width = cellW, height = cellH)
            .shadow(
                elevation    = AppDimens.Dimens6,
                shape        = shape,
                ambientColor = lesson.endColor.copy(alpha = 0.4f),
                spotColor    = lesson.endColor.copy(alpha = 0.4f)
            )
            .background(Brush.linearGradient(listOf(lesson.startColor, lesson.endColor)))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier            = Modifier.padding(AppDimens.Dimens12)
        ) {
            Text(
                text  = lesson.emoji,
                style = MaterialTheme.typography.displaySmall.scaled()
            )
            Spacer(modifier = Modifier.height(AppDimens.Dimens8))
            Box(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(AppDimens.Dimens100))
                    .padding(horizontal = AppDimens.Dimens10, vertical = AppDimens.Dimens4)
            ) {
                Text(
                    text       = "Lesson ${lesson.id}",
                    style      = MaterialTheme.typography.labelSmall.scaled(),
                    color      = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(AppDimens.Dimens6))
            Text(
                text       = lesson.title,
                style      = MaterialTheme.typography.titleMedium.scaled(),
                color      = Color.White,
                fontWeight = FontWeight.Black,
                textAlign  = TextAlign.Center
            )
        }
    }
}
