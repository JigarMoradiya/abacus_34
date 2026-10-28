package com.jigar.me.ui.view.home.screens.levels.level1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jigar.me.ui.view.ab.AbLevelCard
import com.jigar.me.ui.view.ab.AbTopBar
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.screens.levels.LevelScreenRoot
import com.jigar.me.ui.view.home.screens.levels.levelForeground
import com.jigar.me.ui.view.home.screens.levels.levelTopBarPadding
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun Level1HomeScreen(onBackClick: () -> Unit, onNavigateToLesson: (Int) -> Unit) {
    val prefs = LocalPreferencesHelper.current

    LevelScreenRoot(AbWorld.Meadow) {
        val colors = LocalAbColors.current
        // One accent per lesson, taken from the theme so it follows light/dark.
        val accents = listOf(colors.brandSecondary, colors.accentOrange, colors.accentGreen, colors.accentPurple, colors.accentPink)

        Column(modifier = Modifier.levelForeground()) {
            AbTopBar(title = "Bead Basics", onBack = onBackClick, modifier = Modifier.levelTopBarPadding())

            BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
                val cols    = 3
                val rows    = 2
                val spacing = AppDimens.Dimens12
                val hPad    = AppDimens.Dimens16
                val vPad    = AppDimens.Dimens12

                val cellW = (maxWidth - hPad * 2 - spacing * (cols - 1)) / cols
                val cellH = (maxHeight - spacing * (rows - 1) - vPad * 2) / rows

                @Composable
                fun lessonCard(lesson: Level1LessonData) {
                    AbLevelCard(
                        emoji   = lesson.emoji,
                        title   = lesson.title,
                        label   = "Lesson ${lesson.id}",
                        number  = lesson.id,
                        accent  = accents[(lesson.id - 1) % accents.size],
                        width   = cellW,
                        height  = cellH,
                        stars   = prefs.getCustomParamInt("l1_quiz_stars_${lesson.id}", 0),
                        onClick = { onNavigateToLesson(lesson.id) },
                    )
                }

                Column(
                    modifier            = Modifier.fillMaxSize().padding(horizontal = hPad).padding(vertical = vPad),
                    verticalArrangement = Arrangement.spacedBy(spacing),
                ) {
                    // Row 1: lessons 1, 2, 3
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing),
                    ) {
                        level1Lessons.take(cols).forEach { lessonCard(it) }
                    }
                    // Row 2: lessons 4, 5, centred
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
                    ) {
                        level1Lessons.drop(cols).forEach { lessonCard(it) }
                    }
                }
            }
        }
    }
}
