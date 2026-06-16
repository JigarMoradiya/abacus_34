package com.jigar.me.ui.view.home.screens.levels.level1

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.common_ui.LevelHomeCard
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun Level1HomeScreen(onBackClick: () -> Unit, onNavigateToLesson: (Int) -> Unit) {
    val prefs = LocalPreferencesHelper.current

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            BackButtonWithText(title = "Bead Basics", onBackClick = onBackClick)

            BoxWithConstraints(
                modifier         = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                val cols    = 3
                val rows    = 2
                val spacing = AppDimens.Dimens12
                val hPad    = AppDimens.Dimens16
                val vPad    = AppDimens.Dimens12

                val cellW = (maxWidth  - hPad * 2 - spacing * (cols - 1)) / cols
                val cellH = (maxHeight - spacing * (rows - 1) - vPad) / rows

                Column(
                    modifier            = Modifier.fillMaxSize().padding(horizontal = hPad).padding(bottom = vPad),
                    verticalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterVertically),
                ) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing),
                    ) {
                        level1Lessons.take(cols).forEach { lesson ->
                            LevelHomeCard(
                                emoji      = lesson.emoji,
                                title      = lesson.title,
                                label      = "Lesson ${lesson.id}",
                                startColor = lesson.startColor,
                                endColor   = lesson.endColor,
                                width      = cellW,
                                height     = cellH,
                                stars      = prefs.getCustomParamInt("l1_quiz_stars_${lesson.id}", 0),
                                onClick    = { AudioPlayerManager.playSoundBtnClick(); onNavigateToLesson(lesson.id) },
                            )
                        }
                    }
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
                    ) {
                        level1Lessons.drop(cols).forEach { lesson ->
                            LevelHomeCard(
                                emoji      = lesson.emoji,
                                title      = lesson.title,
                                label      = "Lesson ${lesson.id}",
                                startColor = lesson.startColor,
                                endColor   = lesson.endColor,
                                width      = cellW,
                                height     = cellH,
                                stars      = prefs.getCustomParamInt("l1_quiz_stars_${lesson.id}", 0),
                                onClick    = { AudioPlayerManager.playSoundBtnClick(); onNavigateToLesson(lesson.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}
