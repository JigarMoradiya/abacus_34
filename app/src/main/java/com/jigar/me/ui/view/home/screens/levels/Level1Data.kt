package com.jigar.me.ui.view.home.screens.levels

import androidx.compose.ui.graphics.Color

internal data class Level1LessonData(
    val id: Int,
    val emoji: String,
    val title: String,
    val subtitle: String,
    val startColor: Color,
    val endColor: Color,
)

internal val level1Lessons = listOf(
    Level1LessonData(1, "🎯", "Meet the Abacus",   "What beads are & how to tap them",  Color(0xFF0D47A1), Color(0xFF42A5F5)),
    Level1LessonData(2, "🔢", "Numbers 1 to 4",    "Move lower beads — count up to 4!", Color(0xFFBF360C), Color(0xFFFF7043)),
    Level1LessonData(3, "✋", "Number 5 & Beyond", "Upper bead, counting 5 through 9",  Color(0xFF00695C), Color(0xFF26C6DA)),
    Level1LessonData(4, "🏡", "Place Value",        "Discover tens and ones columns",    Color(0xFF1B5E20), Color(0xFF4CAF50)),
    Level1LessonData(5, "💯", "Numbers to 99",      "Read any two-digit number!",         Color(0xFF4A148C), Color(0xFFAB47BC)),
)
