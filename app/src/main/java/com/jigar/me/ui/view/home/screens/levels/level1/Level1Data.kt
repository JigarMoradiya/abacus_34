package com.jigar.me.ui.view.home.screens.levels.level1

import androidx.compose.ui.graphics.Color
import androidx.annotation.StringRes
import com.jigar.me.R

internal data class Level1LessonData(
    val id: Int,
    val emoji: String,
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int,
    val endColor: Color,
)

internal val level1Lessons = listOf(
    Level1LessonData(1, "🎯", R.string.l1_lesson1_title, R.string.l1_lesson1_subtitle, Color(0xFF42A5F5)),
    Level1LessonData(2, "🔢", R.string.l1_lesson2_title, R.string.l1_lesson2_subtitle, Color(0xFFFF7043)),
    Level1LessonData(3, "✋", R.string.l1_lesson3_title, R.string.l1_lesson3_subtitle, Color(0xFF26C6DA)),
    Level1LessonData(4, "🏡", R.string.l1_lesson4_title, R.string.l1_lesson4_subtitle, Color(0xFF4CAF50)),
    Level1LessonData(5, "💯", R.string.l1_lesson5_title, R.string.l1_lesson5_subtitle, Color(0xFFAB47BC)),
)
