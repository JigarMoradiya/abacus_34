package com.jigar.me.ui.view.home.screens.math_game_zone.number_snake.components

import kotlin.math.roundToInt

// Builds a fresh Hidato-style trail puzzle: a random Hamiltonian path over
// an n×n grid (every number 1..n*n sits next to its neighbor), then hides
// most cells behind a tray, keeping only `density`'s worth as given clues.
object NumberSnakeGenerator {

    fun make(config: NumberSnakeConfig): NumberSnakePuzzle {
        val n = config.size
        val total = n * n
        val path = randomHamiltonianPath(n)

        val solution = IntArray(total)
        path.forEachIndexed { idx, cell -> solution[cell.first * n + cell.second] = idx + 1 }

        val givenCount = (total * config.density).roundToInt().coerceIn(2, total)
        val middleNumbers = (2 until total).shuffled()
        val extraGivens = middleNumbers.take((givenCount - 2).coerceAtLeast(0))
        val givenSet = (extraGivens + listOf(1, total)).toHashSet()

        val given = IntArray(total) { i -> if (solution[i] in givenSet) solution[i] else 0 }

        return NumberSnakePuzzle(n, given.toList(), solution.toList())
    }

    private fun randomHamiltonianPath(n: Int): List<Pair<Int, Int>> {
        val total = n * n
        repeat(40) {
            val visited = Array(n) { BooleanArray(n) }
            val path = ArrayList<Pair<Int, Int>>(total)
            val start = (0 until n).random() to (0 until n).random()
            if (dfs(start, n, total, visited, path)) return path
        }
        // Guaranteed fallback: a boustrophedon zig-zag is always a valid path.
        return boustrophedon(n)
    }

    private fun dfs(
        cell: Pair<Int, Int>, n: Int, total: Int,
        visited: Array<BooleanArray>, path: ArrayList<Pair<Int, Int>>
    ): Boolean {
        visited[cell.first][cell.second] = true
        path.add(cell)
        if (path.size == total) return true
        val neighbors = neighborsOf(cell, n).filter { !visited[it.first][it.second] }.shuffled()
        for (next in neighbors) {
            if (dfs(next, n, total, visited, path)) return true
        }
        visited[cell.first][cell.second] = false
        path.removeAt(path.size - 1)
        return false
    }

    private fun neighborsOf(cell: Pair<Int, Int>, n: Int): List<Pair<Int, Int>> {
        val (r, c) = cell
        return listOf(r - 1 to c, r + 1 to c, r to c - 1, r to c + 1)
            .filter { it.first in 0 until n && it.second in 0 until n }
    }

    private fun boustrophedon(n: Int): List<Pair<Int, Int>> {
        val out = ArrayList<Pair<Int, Int>>(n * n)
        for (r in 0 until n) {
            val cols = if (r % 2 == 0) 0 until n else (n - 1) downTo 0
            for (c in cols) out.add(r to c)
        }
        return out
    }
}
