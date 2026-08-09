package com.jigar.me.ui.view.home.screens.math_game_zone.kakuro.components

import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4

enum class KakuroCellType { WALL, BLANK, CLUE }

data class KakuroCell(
    val type: KakuroCellType,
    val acrossSum: Int? = null,   // CLUE: sum of the blank run to the right
    val downSum: Int? = null,     // CLUE: sum of the blank run below
    val solution: Int? = null     // BLANK: canonical digit (hints)
)

// A run of blank cells governed by one clue sum.
data class KakuroRun(val cells: List<Int>, val sum: Int)

data class KakuroLevel(
    val rows: Int,
    val cols: Int,
    val cells: List<KakuroCell>,
    val runs: List<KakuroRun>
) {
    val blankCount: Int get() = cells.count { it.type == KakuroCellType.BLANK }

    companion object {
        // "<rows>x<cols>|<row>|...!<solutionCSV>" — tokens: "." wall, "_" blank,
        // "A<sum>", "D<sum>", "B<a>:<d>" clue cells.
        fun parse(encoded: String): KakuroLevel {
            val seg = encoded.split("!")
            val parts = seg[0].split("|")
            val (r, c) = parts[0].split("x").map { it.toInt() }
            val cells = MutableList(r * c) { KakuroCell(KakuroCellType.WALL) }
            for (row in 0 until r) {
                val tokens = parts[row + 1].split(" ")
                for (col in 0 until c) {
                    val i = row * c + col
                    val t = tokens[col]
                    cells[i] = when {
                        t == "." -> KakuroCell(KakuroCellType.WALL)
                        t == "_" -> KakuroCell(KakuroCellType.BLANK)
                        t.startsWith("B") -> {
                            val (aSum, dSum) = t.substring(1).split(":").map { it.toInt() }
                            KakuroCell(KakuroCellType.CLUE, acrossSum = aSum, downSum = dSum)
                        }
                        t.startsWith("A") -> KakuroCell(KakuroCellType.CLUE, acrossSum = t.substring(1).toInt())
                        else -> KakuroCell(KakuroCellType.CLUE, downSum = t.substring(1).toInt())
                    }
                }
            }
            // Attach canonical solutions to blanks (row-major order).
            val solutions = seg[1].split(",").map { it.toInt() }
            var si = 0
            for (i in cells.indices) {
                if (cells[i].type == KakuroCellType.BLANK) {
                    cells[i] = cells[i].copy(solution = solutions[si]); si++
                }
            }
            // Derive runs from the clue cells.
            val runs = mutableListOf<KakuroRun>()
            for (row in 0 until r) for (col in 0 until c) {
                val i = row * c + col
                val cell = cells[i]
                if (cell.type != KakuroCellType.CLUE) continue
                cell.acrossSum?.let { sum ->
                    val run = mutableListOf<Int>()
                    var cc = col + 1
                    while (cc < c && cells[row * c + cc].type == KakuroCellType.BLANK) { run.add(row * c + cc); cc++ }
                    if (run.isNotEmpty()) runs.add(KakuroRun(run, sum))
                }
                cell.downSum?.let { sum ->
                    val run = mutableListOf<Int>()
                    var rr = row + 1
                    while (rr < r && cells[rr * c + col].type == KakuroCellType.BLANK) { run.add(rr * c + col); rr++ }
                    if (run.isNotEmpty()) runs.add(KakuroRun(run, sum))
                }
            }
            return KakuroLevel(r, c, cells, runs)
        }
    }
}

data class KakuroUiState(
    val rows: Int = 0,
    val cols: Int = 0,
    val cells: List<KakuroCell> = emptyList(),
    val placed: Map<Int, Int> = emptyMap(),     // blank cellIndex -> digit
    val selectedCell: Int? = null,
    val wrongCells: Set<Int> = emptySet(),
    val solvedCells: Set<Int> = emptySet(),
    val hintLocked: Set<Int> = emptySet(),
    val hintsLeft: Int = 3,
    val hintsUsed: Int = 0,
    val elapsed: Int = 0,
    val justSolved: Boolean = false,
    val isGameOver: Boolean = false,
    val starsEarned: Int = 0
)

object KakuroConfig {
    const val LEVEL_COUNT = 50
    const val HINTS_PER_LEVEL = 3

    fun parSecondsPerBlank(difficulty: CommonDifficulty4): Int = when (difficulty) {
        CommonDifficulty4.easy -> 20
        CommonDifficulty4.medium -> 25
        CommonDifficulty4.hard -> 30
        else -> 35
    }

    fun parSeconds(difficulty: CommonDifficulty4, blanks: Int): Int =
        blanks * parSecondsPerBlank(difficulty)

    fun stars(hintsUsed: Int, elapsed: Int, par: Int): Int = when {
        hintsUsed == 0 && elapsed <= par -> 3
        hintsUsed <= 1 && elapsed <= 2 * par -> 2
        else -> 1
    }
}
