package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel

import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.generator.SudokuGenerator
import javax.inject.Inject

class SudokuRepository @Inject constructor() {

    fun generatePuzzle(size: SudokuSize, difficulty: SudokuDifficulty4): SudokuPuzzle {
        return SudokuGenerator.generatePuzzle(size, difficulty)
    }
}
