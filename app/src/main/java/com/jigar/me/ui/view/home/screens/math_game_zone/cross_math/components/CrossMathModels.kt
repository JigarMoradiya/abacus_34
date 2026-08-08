package com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.components

import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4

enum class CrossCellType { ABSENT, GIVEN_NUM, BLANK, OP, EQUALS, BLANK_OP }

fun opDisplay(op: Char?): String = when (op) {
    '*' -> "×"
    '/' -> "÷"
    null -> ""
    else -> op.toString()
}

data class CrossMathCell(
    val type: CrossCellType,
    val given: Int? = null,      // GIVEN_NUM value
    val solution: Int? = null,   // BLANK canonical value
    val symbol: Char? = null,    // '+', '-', '*', '/' or '='
    val solutionOp: Char? = null // BLANK_OP canonical operator
) {
    val displaySymbol: String get() = if (symbol == '=') "=" else opDisplay(symbol)
}

// One equation "a op b = c": five flat-grid indexes [a, op, b, =, c].
// The operator is read from cells[1] at runtime (it may be a blank the
// player fills from the sign palette).
data class CrossRun(val cells: List<Int>)

data class CrossMathLevel(
    val rows: Int,
    val cols: Int,
    val cells: List<CrossMathCell>,
    val runs: List<CrossRun>,
    val tray: List<Int>
) {
    val blankCount: Int
        get() = cells.count { it.type == CrossCellType.BLANK || it.type == CrossCellType.BLANK_OP }

    companion object {
        // Format: "<rows>x<cols>|<row>|<row>|...!<trayCSV>" — tokens per row are
        // space separated: "." absent, "12" given, "[7]" blank(sol 7), "+ - * /", "=".
        fun parse(encoded: String): CrossMathLevel {
            val (head, trayCsv) = encoded.split("!", limit = 2)
            val parts = head.split("|")
            val (r, c) = parts[0].split("x").map { it.toInt() }
            val cells = MutableList(r * c) { CrossMathCell(CrossCellType.ABSENT) }
            for (row in 0 until r) {
                val tokens = parts[row + 1].split(" ")
                for (col in 0 until c) {
                    val i = row * c + col
                    val t = tokens[col]
                    cells[i] = when {
                        t == "." -> CrossMathCell(CrossCellType.ABSENT)
                        t == "=" -> CrossMathCell(CrossCellType.EQUALS, symbol = '=')
                        t.length == 1 && t[0] in "+-*/" -> CrossMathCell(CrossCellType.OP, symbol = t[0])
                        t.length == 3 && t[0] == '[' && t[1] in "+-*/" ->
                            CrossMathCell(CrossCellType.BLANK_OP, solutionOp = t[1])
                        t.startsWith("[") -> CrossMathCell(CrossCellType.BLANK, solution = t.trim('[', ']').toInt())
                        else -> CrossMathCell(CrossCellType.GIVEN_NUM, given = t.toInt())
                    }
                }
            }
            val tray = if (trayCsv.isEmpty()) emptyList() else trayCsv.split(",").map { it.toInt() }
            return CrossMathLevel(r, c, cells, findRuns(r, c, cells), tray)
        }

        private fun findRuns(rows: Int, cols: Int, cells: List<CrossMathCell>): List<CrossRun> {
            fun isNum(i: Int) = cells[i].type == CrossCellType.GIVEN_NUM || cells[i].type == CrossCellType.BLANK
            fun isOp(i: Int) = cells[i].type == CrossCellType.OP || cells[i].type == CrossCellType.BLANK_OP
            fun isEq(i: Int) = cells[i].type == CrossCellType.EQUALS
            val runs = mutableListOf<CrossRun>()
            for (r in 0 until rows) for (c in 0 until cols) {
                val i = r * cols + c
                if (c + 4 < cols && isNum(i) && isOp(i + 1) && isNum(i + 2) && isEq(i + 3) && isNum(i + 4)) {
                    runs.add(CrossRun(listOf(i, i + 1, i + 2, i + 3, i + 4)))
                }
                if (r + 4 < rows) {
                    val d = cols
                    if (isNum(i) && isOp(i + d) && isNum(i + 2 * d) && isEq(i + 3 * d) && isNum(i + 4 * d)) {
                        runs.add(CrossRun(listOf(i, i + d, i + 2 * d, i + 3 * d, i + 4 * d)))
                    }
                }
            }
            return runs
        }
    }
}

data class TrayTile(val id: Int, val value: Int, val used: Boolean = false)

// A single reversible action for the undo stack. Number moves carry a
// trayId; sign moves carry the operator char instead (trayId = -1).
data class CrossMove(
    val cellIndex: Int,
    val trayId: Int,
    val isPlace: Boolean,
    val op: Char? = null
)

data class CrossMathUiState(
    val rows: Int = 0,
    val cols: Int = 0,
    val cells: List<CrossMathCell> = emptyList(),
    val placed: Map<Int, Int> = emptyMap(),     // blank cellIndex -> tray tile id
    val placedOps: Map<Int, Char> = emptyMap(), // BLANK_OP cellIndex -> chosen sign
    val tray: List<TrayTile> = emptyList(),
    val selectedCell: Int? = null,
    val wrongCells: Set<Int> = emptySet(),      // cells of complete-but-wrong runs
    val solvedCells: Set<Int> = emptySet(),     // cells of satisfied runs
    val hintLocked: Set<Int> = emptySet(),
    val hintsLeft: Int = 3,
    val hintsUsed: Int = 0,
    val elapsed: Int = 0,
    val undoStack: List<CrossMove> = emptyList(),
    val justSolved: Boolean = false,
    val isGameOver: Boolean = false,
    val starsEarned: Int = 0
)

object CrossMathConfig {
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
