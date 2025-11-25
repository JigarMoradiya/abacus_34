package com.jigar.me.ui.view.jetpack.fragments.game_zone

import androidx.compose.ui.res.stringResource
import com.jigar.me.R


enum class GameCategoryType {
    NUMBER_SEQUENCE_PUZZLE,
    SUDOKU,
    MATH_PYRAMID,
    TARGET_NUMBER
}

data class GameCategoryData(
    val type: GameCategoryType,
    val title: String,
    val desc: String
)