package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class SudokuHomeUiState(
    val selectedSize: SudokuSize = SudokuSize.FOUR,
    val selectedDifficulty: SudokuDifficulty4 = SudokuDifficulty4.EASY,
    var showResumePopup: Boolean = false,

    val selectedSizeFinal: SudokuSize = SudokuSize.FOUR,
    val selectedDifficultyFinal: SudokuDifficulty4 = SudokuDifficulty4.EASY,
    var isNewGame: Boolean = true
) : Parcelable

// ---------- Models & Enums ----------
@Parcelize
enum class SudokuDifficulty4(val displayName: String) : Parcelable {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard"),
    VERY_HARD("Very Hard")
}
@Parcelize
enum class SudokuSize(val grid: Int) : Parcelable {
    FOUR(4),
    SIX(6),
    NINE(9);

    val displayName: String
        get() = "$grid × $grid"
}


data class SudokuPuzzle(
    val size: SudokuSize,
    val startBoard: List<List<Int>>,
    val solution: List<List<Int>>,
    val difficulty: SudokuDifficulty4
)

data class SavedSudokuGame(
    val puzzle: SudokuPuzzle,
    val board: List<List<Int>>,
    val hintUsed: Int,
    val hintLimit: Int,
    val isSolved: Boolean,
    val selectedR: Int?,
    val selectedC: Int?,
    val savedAt: Long
)

