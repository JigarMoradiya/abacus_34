
package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.generator

import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SudokuSize

object SudokuSolver {
    fun isValid(board: List<List<Int>>, size: SudokuSize, row: Int, col: Int, number: Int): Boolean {
        val n = size.grid
        for (i in 0 until n) {
            if (board[row][i] == number) return false
            if (board[i][col] == number) return false
        }
        val (boxR, boxC) = SudokuBoxRules.boxSize(size)
        val br = (row / boxR) * boxR
        val bc = (col / boxC) * boxC
        for (r in 0 until boxR) for (c in 0 until boxC)
            if (board[br + r][bc + c] == number) return false
        return true
    }

    fun solve(board: Board, size: SudokuSize): Boolean {
        val n = size.grid
        for (r in 0 until n) {
            for (c in 0 until n) {
                if (board[r][c] == 0) {
                    for (num in 1..n) {
                        if (isValid(board, size, r, c, num)) {
                            board[r][c] = num
                            if (solve(board, size)) return true
                            board[r][c] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    // Count solutions up to maxCount (early exit)
    fun solutionCount(initial: List<List<Int>>, size: SudokuSize, maxCount: Int = 2): Int {
        val board = initial.map { it.toMutableList() }.toMutableList()
        var counter = 0
        fun backtrack(): Boolean {
            val n = size.grid
            var row = -1
            var col = -1
            loop@ for (r in 0 until n) {
                for (c in 0 until n) {
                    if (board[r][c] == 0) { row = r; col = c; break@loop }
                }
            }
            if (row == -1) {
                counter++
                return counter >= maxCount
            }
            for (num in 1..n) {
                if (isValid(board, size, row, col, num)) {
                    board[row][col] = num
                    if (backtrack()) return true
                    board[row][col] = 0
                }
            }
            return false
        }
        backtrack()
        return counter
    }

    fun candidatesFor(board: List<List<Int>>, size: SudokuSize, row: Int, col: Int): List<Int> {
        if (board[row][col] != 0) return emptyList()
        return (1..size.grid).filter { isValid(board, size, row, col, it) }
    }
}