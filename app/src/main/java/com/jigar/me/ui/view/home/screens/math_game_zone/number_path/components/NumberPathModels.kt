package com.jigar.me.ui.view.home.screens.math_game_zone.number_path.components

import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4

enum class PathCellType { WALL, START, GOAL, OP }

data class NumberPathCell(
    val type: PathCellType,
    val op: Char? = null,       // '+', '-', '*', '/'
    val operand: Int? = null
) {
    val displayText: String
        get() = when (op) {
            '*' -> "×$operand"
            '/' -> "÷$operand"
            null -> ""
            else -> "$op$operand"
        }
}

data class NumberPathLevel(
    val rows: Int,
    val cols: Int,
    val cells: List<NumberPathCell>,
    val startIndex: Int,
    val goalIndex: Int,
    val startValue: Int,
    val target: Int,
    val canonicalPath: List<Int>   // flat indexes incl. start and goal (hints)
) {
    companion object {
        // "<rows>x<cols>|<row>|...!<startVal>!<target>!<pathCSV>" — tokens:
        // "." wall, "S" start, "G" goal, "+3"/"-2"/"*2"/"/4" op tiles.
        fun parse(encoded: String): NumberPathLevel {
            val seg = encoded.split("!")
            val parts = seg[0].split("|")
            val (r, c) = parts[0].split("x").map { it.toInt() }
            var startIndex = 0
            var goalIndex = 0
            val cells = MutableList(r * c) { NumberPathCell(PathCellType.WALL) }
            for (row in 0 until r) {
                val tokens = parts[row + 1].split(" ")
                for (col in 0 until c) {
                    val i = row * c + col
                    val t = tokens[col]
                    cells[i] = when {
                        t == "." -> NumberPathCell(PathCellType.WALL)
                        t == "S" -> { startIndex = i; NumberPathCell(PathCellType.START) }
                        t == "G" -> { goalIndex = i; NumberPathCell(PathCellType.GOAL) }
                        else -> NumberPathCell(PathCellType.OP, op = t[0], operand = t.substring(1).toInt())
                    }
                }
            }
            return NumberPathLevel(
                rows = r, cols = c, cells = cells,
                startIndex = startIndex, goalIndex = goalIndex,
                startValue = seg[1].toInt(), target = seg[2].toInt(),
                canonicalPath = seg[3].split(",").map { it.toInt() }
            )
        }
    }
}

data class NumberPathUiState(
    val rows: Int = 0,
    val cols: Int = 0,
    val cells: List<NumberPathCell> = emptyList(),
    val startValue: Int = 0,
    val target: Int = 0,
    val trail: List<Int> = emptyList(),    // visited cells, starts with START
    val totals: List<Int> = emptyList(),   // running total after each trail cell
    val hintIndex: Int? = null,            // cell pulsing as the hinted next step
    val hintsLeft: Int = 3,
    val hintsUsed: Int = 0,
    val elapsed: Int = 0,
    val goalBounceCount: Int = 0,          // increments on wrong goal arrival
    val blockedBounceCount: Int = 0,       // increments on impossible op step
    val justSolved: Boolean = false,
    val isGameOver: Boolean = false,
    val starsEarned: Int = 0
) {
    val currentTotal: Int get() = totals.lastOrNull() ?: startValue
    val currentCell: Int get() = trail.lastOrNull() ?: 0
}

object NumberPathConfig {
    const val LEVEL_COUNT = 50
    const val HINTS_PER_LEVEL = 3

    fun parSecondsPerStep(difficulty: CommonDifficulty4): Int = when (difficulty) {
        CommonDifficulty4.easy -> 6
        CommonDifficulty4.medium -> 7
        CommonDifficulty4.hard -> 8
        else -> 9
    }

    fun parSeconds(difficulty: CommonDifficulty4, pathSteps: Int): Int =
        pathSteps * parSecondsPerStep(difficulty)

    fun stars(hintsUsed: Int, elapsed: Int, par: Int): Int = when {
        hintsUsed == 0 && elapsed <= par -> 3
        hintsUsed <= 1 && elapsed <= 2 * par -> 2
        else -> 1
    }
}
