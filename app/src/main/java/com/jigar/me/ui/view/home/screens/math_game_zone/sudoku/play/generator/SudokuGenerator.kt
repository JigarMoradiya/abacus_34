package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.generator

import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SudokuDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SudokuPuzzle
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SudokuSize
import kotlin.random.Random

object SudokuGenerator {

    fun generatePuzzle(size: SudokuSize, difficulty: SudokuDifficulty4): SudokuPuzzle {

        val solved = generateSolvedBoard(size)

        val puzzle = solved.map { it.toMutableList() }.toMutableList()

        val (minRemove, maxRemove) = removalCountRange(size, difficulty)
        val removals = maxRemove // 👈 important

        val n = size.grid
        val indices = (0 until n * n).shuffled()

        for (i in 0 until removals) {
            val idx = indices[i]
            val r = idx / n
            val c = idx % n
            puzzle[r][c] = 0
        }

        return SudokuPuzzle(
            size,
            puzzle.map { it.toList() },
            solved.map { it.toList() }, // 👈 keep solution
            difficulty
        )
    }

    private fun generateSolvedBoard(size: SudokuSize): List<List<Int>> {

        val n = size.grid
        val board = MutableList(n) { MutableList(n) { 0 } }

        fun fill(): Boolean {
            for (r in 0 until n) {
                for (c in 0 until n) {
                    if (board[r][c] == 0) {

                        val nums = (1..n).shuffled()

                        for (num in nums) {
                            if (isValid(board, size, r, c, num)) {
                                board[r][c] = num
                                if (fill()) return true
                                board[r][c] = 0
                            }
                        }
                        return false
                    }
                }
            }
            return true
        }

        fill()
        return board.map { it.toList() }
    }

    private fun isValid(
        board: List<List<Int>>,
        size: SudokuSize,
        row: Int,
        col: Int,
        num: Int
    ): Boolean {

        val n = size.grid

        for (i in 0 until n) {
            if (board[row][i] == num) return false
            if (board[i][col] == num) return false
        }

        val (br, bc) = SudokuBoxRules.boxSize(size)
        val sr = (row / br) * br
        val sc = (col / bc) * bc

        for (r in 0 until br) {
            for (c in 0 until bc) {
                if (board[sr + r][sc + c] == num) return false
            }
        }

        return true
    }

    private fun removalCountRange(
        size: SudokuSize,
        difficulty: SudokuDifficulty4
    ): Pair<Int, Int> {

        return when (size) {

            SudokuSize.FOUR -> when (difficulty) {
                SudokuDifficulty4.EASY -> 4 to 6
                SudokuDifficulty4.MEDIUM -> 6 to 8
                SudokuDifficulty4.HARD -> 8 to 10
                SudokuDifficulty4.VERY_HARD -> 10 to 12
            }

            SudokuSize.SIX -> when (difficulty) {
                SudokuDifficulty4.EASY -> 8 to 12
                SudokuDifficulty4.MEDIUM -> 12 to 16
                SudokuDifficulty4.HARD -> 16 to 20
                SudokuDifficulty4.VERY_HARD -> 20 to 24
            }

            SudokuSize.NINE -> when (difficulty) {
                SudokuDifficulty4.EASY -> 20 to 28
                SudokuDifficulty4.MEDIUM -> 28 to 34
                SudokuDifficulty4.HARD -> 34 to 42
                SudokuDifficulty4.VERY_HARD -> 42 to 50
            }
        }
    }
}