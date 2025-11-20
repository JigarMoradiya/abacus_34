package com.jigar.me.ui.view.jetpack.fragments.game_zone.sudoku.components

import kotlin.random.Random

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

object SudokuGenerator {
    private fun generateSolvedBoard(size: SudokuSize): List<List<Int>> {
        val n = size.grid
        val board = MutableList(n) { MutableList(n) { 0 } }
        fun fill(pos: Int): Boolean {
            if (pos == n * n) return true
            val r = pos / n
            val c = pos % n
            val numbers = (1..n).shuffled()
            for (num in numbers) {
                if (SudokuSolver.isValid(board, size, r, c, num)) {
                    board[r][c] = num
                    if (fill(pos + 1)) return true
                    board[r][c] = 0
                }
            }
            return false
        }
        fill(0)
        return board
    }

    private fun removalCountRange(size: SudokuSize, diff: SudokuDifficulty4): IntRange {
        return when (size) {
            SudokuSize.FOUR -> when (diff) {
                SudokuDifficulty4.EASY -> 4..6; SudokuDifficulty4.MEDIUM -> 6..8
                SudokuDifficulty4.HARD -> 8..10; SudokuDifficulty4.VERY_HARD -> 10..12
            }
            SudokuSize.SIX -> when (diff) {
                SudokuDifficulty4.EASY -> 12..16; SudokuDifficulty4.MEDIUM -> 16..20
                SudokuDifficulty4.HARD -> 20..24; SudokuDifficulty4.VERY_HARD -> 24..28
            }
            SudokuSize.NINE -> when (diff) {
                SudokuDifficulty4.EASY -> 30..36; SudokuDifficulty4.MEDIUM -> 36..46
                SudokuDifficulty4.HARD -> 46..54; SudokuDifficulty4.VERY_HARD -> 52..62
            }
        }
    }

    fun generatePuzzle(size: SudokuSize, difficulty: SudokuDifficulty4): SudokuPuzzle {
        val solved = generateSolvedBoard(size)
        val puzzle = solved.map { it.toMutableList() }.toMutableList()
        val removalRange = removalCountRange(size, difficulty)
        val removalsTarget = Random.nextInt(removalRange.first, removalRange.last + 1)
        val cellIndices = (0 until size.grid * size.grid).shuffled().toMutableList()
        var removed = 0
        var attempts = 0
        val maxAttempts = if (size == SudokuSize.NINE) 5000 else 2000
        while (removed < removalsTarget && cellIndices.isNotEmpty() && attempts < maxAttempts) {
            attempts++
            val idx = cellIndices.removeAt(0)
            val r = idx / size.grid
            val c = idx % size.grid
            val backup = puzzle[r][c]
            puzzle[r][c] = 0
            val count = SudokuSolver.solutionCount(puzzle.map { it.toList() }, size, 2)
            if (count == 1) {
                removed++
            } else {
                puzzle[r][c] = backup
            }
        }
        return SudokuPuzzle(size, puzzle.map { it.toList() }, solved.map { it.toList() }, difficulty)
    }
}