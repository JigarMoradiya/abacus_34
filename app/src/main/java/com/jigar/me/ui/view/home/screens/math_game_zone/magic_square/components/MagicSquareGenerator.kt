package com.jigar.me.ui.view.home.screens.math_game_zone.magic_square.components

object MagicSquareGenerator {

    // The canonical 3×3 magic square (every line sums to 15).
    private val base = listOf(2, 7, 6,
                              9, 5, 1,
                              4, 3, 8)

    data class Puzzle(
        val solution: List<Int>,   // 9 values, a valid magic square
        val fixed: List<Boolean>,  // true = pre-filled, false = blank for the kid
        val palette: List<Int>     // the removed numbers, shuffled
    )

    fun make(config: MagicSquareConfig): Puzzle {
        var sq = base
        // Random symmetry (rotations + optional mirror) → 8 possible layouts.
        repeat((0..3).random()) { sq = rotate90(sq) }
        if ((0..1).random() == 1) sq = mirror(sq)

        val shuffledIndices = (0..8).shuffled()
        val isBlank = BooleanArray(9)
        for (k in 0 until minOf(config.emptyCount, 9)) {
            isBlank[shuffledIndices[k]] = true
        }
        val fixed = (0..8).map { !isBlank[it] }
        val palette = (0..8).filter { isBlank[it] }.map { sq[it] }.shuffled()
        return Puzzle(solution = sq, fixed = fixed, palette = palette)
    }

    private fun rotate90(g: List<Int>): List<Int> {
        // new[r][c] = old[2-c][r]
        val out = IntArray(9)
        for (r in 0 until 3) {
            for (c in 0 until 3) {
                out[r * 3 + c] = g[(2 - c) * 3 + r]
            }
        }
        return out.toList()
    }

    private fun mirror(g: List<Int>): List<Int> {
        val out = IntArray(9)
        for (r in 0 until 3) {
            for (c in 0 until 3) {
                out[r * 3 + c] = g[r * 3 + (2 - c)]
            }
        }
        return out.toList()
    }
}
