package com.jigar.me.ui.view.home.screens.levels.level2

import androidx.compose.ui.graphics.Color
import androidx.annotation.StringRes
import com.jigar.me.R

enum class Level2ChapterType { DIRECT, FORMULA_REF, FORMULA }

internal data class Level2ChapterData(
    val id: Int,
    val emoji: String,
    @StringRes val titleRes: Int,
    val endColor: Color,
    val type: Level2ChapterType,
    val columns: Int = 1,
)

// Alias so existing sub-screens compile without change
internal val level2Lessons get() = level2Chapters

internal val level2Chapters = listOf(
    Level2ChapterData(1, "⬆️", R.string.l2_chapter1_title, Color(0xFF00BCD4), Level2ChapterType.DIRECT),
    Level2ChapterData(2, "🌟", R.string.l2_chapter2_title, Color(0xFF42A5F5), Level2ChapterType.DIRECT),
    Level2ChapterData(3, "⬇️", R.string.l2_chapter3_title, Color(0xFFFF7043), Level2ChapterType.DIRECT),
    Level2ChapterData(4, "🔽", R.string.l2_chapter4_title, Color(0xFFEC407A), Level2ChapterType.DIRECT),
    Level2ChapterData(5, "📋", R.string.l2_chapter5_title, Color(0xFFAB47BC), Level2ChapterType.FORMULA_REF),
    Level2ChapterData(6, "🤝", R.string.l2_chapter6_title, Color(0xFF4CAF50), Level2ChapterType.FORMULA),
    Level2ChapterData(7, "🤝", R.string.l2_chapter7_title, Color(0xFF26A69A), Level2ChapterType.FORMULA),
    Level2ChapterData(8, "🔢", R.string.l2_chapter8_title, Color(0xFF5C6BC0), Level2ChapterType.FORMULA, columns = 2),
    Level2ChapterData(9, "🔢", R.string.l2_chapter9_title, Color(0xFF8BC34A), Level2ChapterType.FORMULA, columns = 2),
    Level2ChapterData(10, "👨‍👩‍👧‍👦", R.string.l2_chapter10_title, Color(0xFFFFB300), Level2ChapterType.FORMULA, columns = 2),
)
