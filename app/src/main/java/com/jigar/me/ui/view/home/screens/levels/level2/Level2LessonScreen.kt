package com.jigar.me.ui.view.home.screens.levels.level2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.home.screens.levels.levelDp
import com.jigar.me.ui.view.ab.AbLevelCard
import com.jigar.me.ui.view.ab.AbTopBar
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.home.screens.levels.LevelScreenRoot
import com.jigar.me.ui.view.home.screens.levels.levelForeground
import com.jigar.me.ui.view.home.screens.levels.levelTopBarPadding
import com.jigar.me.ui.view.home.theme.AppDimens

private data class L2PhaseData(
    val emoji: String,
    val title: String,
    val description: String,
    val accent: Color,
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
        PhaseAction(L2PhaseData("📚", "Learn",    "Understand the concept step by step", lesson.endColor))  { onNavigateToLearn(lessonId) },
        PhaseAction(L2PhaseData("✏️", "Practice", "Solve equations on your abacus",      Color(0xFF4CAF50))) { onNavigateToPractice(lessonId) },
        PhaseAction(L2PhaseData("⭐", "Quiz",     "Test your math skills & earn stars!", Color(0xFFAB47BC))) { onNavigateToQuiz(lessonId) },
    )

    LevelScreenRoot(AbWorld.Sunset) {
        Column(modifier = Modifier.levelForeground()) {
            AbTopBar(
                title    = "Lesson ${lesson.id}  ·  ${lesson.title}",
                onBack   = onBackClick,
                modifier = Modifier.levelTopBarPadding(),
            )

            BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
                val spacing = AppDimens.Dimens16
                val hPad    = AppDimens.Dimens20
                val cardW   = (maxWidth - hPad * 2 - spacing * (phaseActions.size - 1)) / phaseActions.size
                val cardH   = minOf(maxHeight * 0.82f, levelDp(360f))

                Row(
                    modifier              = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    phaseActions.forEach { pa ->
                        AbLevelCard(
                            emoji     = pa.phase.emoji,
                            title     = pa.phase.title,
                            label     = pa.phase.title,
                            accent    = pa.phase.accent,
                            width     = cardW,
                            height    = cardH,
                            showStars = false,
                            subtitle  = pa.phase.description,
                            onClick   = pa.onClick,
                        )
                    }
                }
            }
        }
    }
}
