package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.generator

import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SudokuSize

// ---------- Solver & Generator ----------
typealias Board = MutableList<MutableList<Int>>

object SudokuBoxRules {
    fun boxSize(size: SudokuSize): Pair<Int, Int> {
        return when (size) {
            SudokuSize.FOUR -> 2 to 2
            SudokuSize.SIX  -> 2 to 3
            SudokuSize.NINE -> 3 to 3
        }
    }
}