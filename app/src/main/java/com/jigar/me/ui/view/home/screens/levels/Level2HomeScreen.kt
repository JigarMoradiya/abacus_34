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
fun Level2HomeScreen(onBackClick: () -> Unit, onNavigateToLesson: (Int) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize()) {
            BackButtonWithText(title = "Add & Subtract", onBackClick = onBackClick)

            BoxWithConstraints(
                modifier         = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                val cols       = 3
                val rows       = 2
                val spacing    = AppDimens.Dimens12
                val hPad       = AppDimens.Dimens16
                val vPad       = AppDimens.Dimens12
                val shadowRoom = AppDimens.Dimens8

                val cellW = (maxWidth - hPad * 2 - spacing * (cols - 1)) / cols
                val cellH = (maxHeight - vPad * 2 - spacing * (rows - 1) - shadowRoom) / rows

                Column(
                    modifier            = Modifier.width(maxWidth - hPad * 2).padding(vertical = vPad),
                    verticalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        level2Lessons.take(cols).forEach { lesson ->
                            L2LessonCard(lesson, cellW, cellH) { onNavigateToLesson(lesson.id) }
                        }
                    }
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally)
                    ) {
                        level2Lessons.drop(cols).forEach { lesson ->
                            L2LessonCard(lesson, cellW, cellH) { onNavigateToLesson(lesson.id) }
                        }
                    }
                    Spacer(modifier = Modifier.height(shadowRoom))
                }
            }
        }
    }
}

@Composable
private fun L2LessonCard(lesson: Level2LessonData, cellW: Dp, cellH: Dp, onClick: () -> Unit) {
    val shape = RoundedCornerShape(AppDimens.Dimens20)
    Box(
        modifier = Modifier
            .size(width = cellW, height = cellH)
            .shadow(AppDimens.Dimens6, shape,
                ambientColor = lesson.endColor.copy(alpha = 0.4f),
                spotColor    = lesson.endColor.copy(alpha = 0.4f))
            .background(Brush.linearGradient(listOf(lesson.startColor, lesson.endColor)))
            .clickable(remember { MutableInteractionSource() }, null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier            = Modifier.padding(AppDimens.Dimens12)
        ) {
            Text(text = lesson.emoji, style = MaterialTheme.typography.displaySmall.scaled())
            Spacer(Modifier.height(AppDimens.Dimens8))
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
            Spacer(Modifier.height(AppDimens.Dimens6))
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
