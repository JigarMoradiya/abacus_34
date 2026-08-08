package com.jigar.me.ui.view.home.screens.math_game_zone


enum class GameCategoryType {
    NUMBER_SEQUENCE_PUZZLE,
    SUDOKU,
    MATH_PYRAMID,
    TARGET_NUMBER,
    BALLOON_POP,
    SPEED_COMPARE,
    MISSING_OPERATOR,
    MAGIC_SQUARE,
    CALCUDOKU,
    MERGE_2048,
    EQUATION_MATCH,
    CROSS_MATH
}

data class GameCategoryData(
    val type: GameCategoryType,
    val title: String,
    val desc: String
)