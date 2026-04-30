package com.jigar.me.ui.view.home.screens.math_game_zone


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