package com.jigar.me.ui.view.home.screens.levels.level2

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

private data class L2PhaseData(
    val emoji: String,
    val title: String,
    val description: String,
    val startColor: Color,
    val endColor: Color,
)

@Composable
fun Level2LessonScreen(
    lessonId: Int,
    onBackClick: () -> Unit,
    onNavigateToLearn: (Int) -> Unit = {},
    onNavigateToPractice: (Int) -> Unit = {},
    onNavigateToQuiz: (Int) -> Unit = {},
) {
    val lesson = level2Lessons.find { it.id == lessonId } ?: return

    data class PhaseAction(val phase: L2PhaseData, val onClick: () -> Unit)
    val phaseActions = listOf(
        PhaseAction(L2PhaseData("📚", "Learn",    "Understand the concept step by step",  lesson.startColor, lesson.endColor))   { onNavigateToLearn(lessonId) },
        PhaseAction(L2PhaseData("✏️", "Practice", "Solve equations on your abacus",        Color(0xFF1B5E20),  Color(0xFF4CAF50))) { onNavigateToPractice(lessonId) },
        PhaseAction(L2PhaseData("⭐", "Quiz",     "Test your math skills & earn stars!",   Color(0xFF4A148C),  Color(0xFFAB47BC))) { onNavigateToQuiz(lessonId) },
    )

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize()) {
            BackButtonWithText(
                title       = "Lesson ${lesson.id}  ·  ${lesson.title}",
                onBackClick = onBackClick
            )
            BoxWithConstraints(
                modifier         = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val spacing    = AppDimens.Dimens16
                val hPad       = AppDimens.Dimens20
                val shadowRoom = AppDimens.Dimens10
                val cardW      = (maxWidth - hPad * 2 - spacing * (phaseActions.size - 1)) / phaseActions.size
                val cardH      = maxHeight * 0.80f

                Column(
                    modifier            = Modifier.width(maxWidth - hPad * 2),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(spacing),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        phaseActions.forEach { pa ->
                            L2PhaseCard(pa.phase, cardW, cardH, pa.onClick)
                        }
                    }
                    Spacer(Modifier.height(shadowRoom))
                }
            }
        }
    }
}

@Composable
private fun L2PhaseCard(phase: L2PhaseData, cardW: Dp, cardH: Dp, onClick: () -> Unit) {
    val shape = RoundedCornerShape(AppDimens.Dimens24)
    Box(
        modifier = Modifier
            .width(cardW).height(cardH)
            .shadow(AppDimens.Dimens8, shape,
                ambientColor = phase.endColor.copy(alpha = 0.4f),
                spotColor    = phase.endColor.copy(alpha = 0.4f))
            .background(Brush.linearGradient(listOf(phase.startColor, phase.endColor)))
            .clickable(remember { MutableInteractionSource() }, null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier            = Modifier.padding(AppDimens.Dimens16)
        ) {
            Text(text = phase.emoji, style = MaterialTheme.typography.displaySmall.scaled())
            Spacer(Modifier.height(AppDimens.Dimens12))
            Text(
                text       = phase.title,
                style      = MaterialTheme.typography.headlineMedium.scaled(),
                color      = Color.White,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(AppDimens.Dimens8))
            Text(
                text      = phase.description,
                style     = MaterialTheme.typography.bodyMedium.scaled(),
                color     = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
        }
    }
}
