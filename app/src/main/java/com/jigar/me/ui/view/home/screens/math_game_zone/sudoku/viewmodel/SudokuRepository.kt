package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.viewmodel

import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.components.SudokuDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.components.SudokuGenerator
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.components.SudokuPuzzle
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.components.SudokuSize
import javax.inject.Inject

class SudokuRepository @Inject constructor() {

    /**
     * Generate puzzle.
     */
    fun generatePuzzle(size: SudokuSize, difficulty: SudokuDifficulty4): SudokuPuzzle {
        return SudokuGenerator.generatePuzzle(size, difficulty)
    }
}
