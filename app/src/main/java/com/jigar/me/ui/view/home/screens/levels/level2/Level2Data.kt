package com.jigar.me.ui.view.home.screens.levels.level2

import androidx.compose.ui.graphics.Color

internal data class Level2LessonData(
    val id: Int,
    val emoji: String,
    val title: String,
    val subtitle: String,
    val startColor: Color,
    val endColor: Color,
)

internal val level2Lessons = listOf(
    Level2LessonData(1, "➕", "Add to 5",         "Add two numbers — result up to 5",  Color(0xFFAD1457), Color(0xFFEC407A)),
    Level2LessonData(2, "🔢", "Add to 9",         "Add numbers using the heaven bead", Color(0xFFE65100), Color(0xFFFF9800)),
    Level2LessonData(3, "➖", "Subtract from 5",  "Take away from small numbers",      Color(0xFF0D47A1), Color(0xFF29B6F6)),
    Level2LessonData(4, "🔟", "Subtract from 9",  "Take away from bigger numbers",     Color(0xFF004D40), Color(0xFF26A69A)),
    Level2LessonData(5, "🔀", "Add & Subtract",   "Mix of adding and subtracting",     Color(0xFF4A148C), Color(0xFFAB47BC)),
)
