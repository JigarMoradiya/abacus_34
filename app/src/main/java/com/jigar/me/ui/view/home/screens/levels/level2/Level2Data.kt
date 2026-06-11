package com.jigar.me.ui.view.home.screens.levels.level2

import androidx.compose.ui.graphics.Color

enum class Level2ChapterType { DIRECT, FORMULA_REF, FORMULA }

internal data class Level2ChapterData(
    val id: Int,
    val emoji: String,
    val title: String,
    val subtitle: String,
    val startColor: Color,
    val endColor: Color,
    val type: Level2ChapterType,
    val columns: Int = 1,
)

// Alias so existing sub-screens compile without change
internal val level2Lessons get() = level2Chapters

internal val level2Chapters = listOf(
    Level2ChapterData(1,  "⬆️",  "Earth Add",      "Push earth beads — result 1 to 4",        Color(0xFF006064), Color(0xFF00BCD4), Level2ChapterType.DIRECT),
    Level2ChapterData(2,  "🌟",  "Heaven Add",      "Add with heaven bead — direct only",      Color(0xFF1565C0), Color(0xFF42A5F5), Level2ChapterType.DIRECT),
    Level2ChapterData(3,  "⬇️",  "Earth Subtract", "Pull earth beads — no formula needed",    Color(0xFFBF360C), Color(0xFFFF7043), Level2ChapterType.DIRECT),
    Level2ChapterData(4,  "🔽",  "Heaven Subtract","Subtract — heaven bead stays set",        Color(0xFF880E4F), Color(0xFFEC407A), Level2ChapterType.DIRECT),
    Level2ChapterData(5,  "📋",  "Meet Formulas",  "Why, when & how to use bead formulas",    Color(0xFF4A148C), Color(0xFFAB47BC), Level2ChapterType.FORMULA_REF),
    Level2ChapterData(6,  "🤝",  "Small Friend +", "+1=−4+5  +2=−3+5  +3=−2+5  +4=−1+5",    Color(0xFF1B5E20), Color(0xFF4CAF50), Level2ChapterType.FORMULA),
    Level2ChapterData(7,  "🤝",  "Small Friend −", "−1=−5+4  −2=−5+3  −3=−5+2  −4=−5+1",    Color(0xFF004D40), Color(0xFF26A69A), Level2ChapterType.FORMULA),
    Level2ChapterData(8,  "🔢",  "Big Friend +",   "+1=+10−9  …  +9=+10−1 (carry to tens)",  Color(0xFF1A237E), Color(0xFF5C6BC0), Level2ChapterType.FORMULA, columns = 2),
    Level2ChapterData(9,  "🔢",  "Big Friend −",   "−1=−10+9  …  −9=−10+1 (borrow tens)",    Color(0xFF33691E), Color(0xFF8BC34A), Level2ChapterType.FORMULA, columns = 2),
    Level2ChapterData(10, "👨‍👩‍👧‍👦", "Family",         "−5+10 and +5−10 combined",                Color(0xFFE65100), Color(0xFFFFB300), Level2ChapterType.FORMULA, columns = 2),
)
