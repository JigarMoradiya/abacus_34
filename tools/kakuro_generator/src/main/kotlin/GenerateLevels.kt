// Kakuro Kids level generator (dev tool — never ships in the app).
//
// Generates 4 tiers x 50 levels x 3 variants of kid-sized kakuro puzzles and
// emits identical level data for both platforms (KakuroLevels.kt / .swift).
//
// Level string format (mirrored by KakuroModels on each platform):
//   "<rows>x<cols>|<row>|<row>|...!<solutionCSV>"
// Row tokens (space separated):
//   .          wall
//   _          blank cell the kid fills with 1..9
//   A<sum>     clue cell: sum of the blank run to its RIGHT
//   D<sum>     clue cell: sum of the blank run BELOW it
//   B<a>:<d>   clue cell with both an across and a down sum
// solutionCSV = the digits of all blanks in row-major order (used by hints).
// Classic kakuro rule: no digit repeats inside a single run.
//
// Run from the repo root:
//   gradlew.bat -p tools\kakuro_generator run [--args="<seed>"]

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Random

// A run of blank cells: starts at (r,c), `len` cells, across or down.
// The clue cell sits immediately before the run.
data class KRun(val r: Int, val c: Int, val len: Int, val across: Boolean) {
    fun cells(): List<Pair<Int, Int>> =
        (0 until len).map { if (across) r to c + it else r + it to c }

    fun clueCell(): Pair<Int, Int> = if (across) r to c - 1 else r - 1 to c
}

data class Template(val name: String, val rows: Int, val cols: Int, val runs: List<KRun>) {
    init {
        val blankSet = HashSet<Pair<Int, Int>>()
        val clueSet = HashSet<Pair<Int, Int>>()
        for (run in runs) {
            check(run.len in 2..4) { "$name: run len ${run.len}" }
            for (cell in run.cells()) {
                check(cell.first in 0 until rows && cell.second in 0 until cols) { "$name: blank OOB $cell" }
                blankSet.add(cell)
            }
            val clue = run.clueCell()
            check(clue.first in 0 until rows && clue.second in 0 until cols) { "$name: clue OOB $clue" }
            clueSet.add(clue)
        }
        check(blankSet.none { it in clueSet }) { "$name: clue overlaps blank" }
    }

    val blanks: List<Pair<Int, Int>> get() = runs.flatMap { it.cells() }.distinct().sortedWith(compareBy({ it.first }, { it.second }))
}

fun a(r: Int, c: Int, len: Int) = KRun(r, c, len, true)
fun d(r: Int, c: Int, len: Int) = KRun(r, c, len, false)

// Kid-sized shapes; every blank belongs to at least one run, crossings share blanks.
val EASY = listOf(
    Template("pairL", 3, 4, listOf(a(1, 1, 2), d(1, 1, 2))),
    Template("pairT", 3, 4, listOf(a(1, 1, 3), d(1, 2, 2))),
    Template("corner", 4, 4, listOf(a(1, 1, 2), d(1, 2, 3))),
    Template("hookE", 4, 4, listOf(a(1, 1, 3), d(1, 1, 3)))
)
val MEDIUM = listOf(
    Template("windowM", 5, 5, listOf(a(1, 1, 3), a(3, 1, 3), d(1, 1, 3), d(1, 3, 3))),
    Template("stairsM", 5, 5, listOf(a(1, 1, 2), a(2, 1, 3), d(1, 1, 2), d(1, 2, 3))),
    Template("crossM", 5, 5, listOf(a(2, 1, 3), d(1, 2, 3), a(4, 2, 2), d(2, 3, 3))),
    Template("blockM", 4, 5, listOf(a(1, 1, 3), a(2, 1, 3), d(1, 1, 2), d(1, 2, 2), d(1, 3, 2)))
)
val HARD = listOf(
    Template("windowH", 5, 6, listOf(a(1, 1, 4), a(3, 1, 4), d(1, 1, 3), d(1, 4, 3))),
    Template("gridH", 5, 6, listOf(a(1, 1, 3), a(2, 1, 4), a(4, 2, 3), d(1, 1, 2), d(1, 2, 4), d(2, 4, 3))),
    Template("towersH", 6, 5, listOf(a(1, 1, 3), a(3, 1, 2), a(5, 1, 3), d(1, 1, 3), d(1, 3, 3), d(3, 2, 3))),
    Template("snakeH", 6, 6, listOf(a(1, 1, 3), d(1, 3, 3), a(3, 3, 2), d(3, 4, 3), a(5, 2, 3)))
)
val VERY_HARD = listOf(
    Template("bigWindow", 6, 7, listOf(a(1, 1, 4), a(3, 1, 4), a(5, 1, 4), d(1, 1, 3), d(1, 2, 3), d(1, 4, 3), d(3, 3, 3))),
    Template("mazeV", 6, 7, listOf(a(1, 1, 4), a(2, 1, 3), a(4, 2, 4), a(5, 4, 2), d(1, 1, 2), d(1, 3, 4), d(2, 5, 4), d(4, 2, 2))),
    Template("fortV", 7, 6, listOf(a(1, 1, 4), a(3, 1, 3), a(5, 1, 4), d(1, 1, 3), d(1, 3, 3), d(3, 4, 3)))
)

data class Tier(val name: String, val templates: List<Template>)

val TIERS = listOf(
    Tier("easy", EASY),
    Tier("medium", MEDIUM),
    Tier("hard", HARD),
    Tier("veryHard", VERY_HARD)
)

// The template only defines WHERE the blanks are. The actual runs are the
// maximal contiguous blank segments (len >= 2) in the grid — exactly how the
// app will read them back — so sums always match what the kid sees.
data class DerivedRun(val cells: List<Pair<Int, Int>>, val across: Boolean) {
    fun clueCell(): Pair<Int, Int> {
        val first = cells.first()
        return if (across) first.first to first.second - 1 else first.first - 1 to first.second
    }
}

fun deriveRuns(tpl: Template): List<DerivedRun> {
    val blank = tpl.blanks.toHashSet()
    val runs = mutableListOf<DerivedRun>()
    for (r in 0 until tpl.rows) {
        var c = 0
        while (c < tpl.cols) {
            if (r to c in blank && (c == 0 || r to c - 1 !in blank)) {
                val cells = mutableListOf<Pair<Int, Int>>()
                var cc = c
                while (cc < tpl.cols && r to cc in blank) { cells.add(r to cc); cc++ }
                if (cells.size >= 2) runs.add(DerivedRun(cells, across = true))
                c = cc
            } else c++
        }
    }
    for (c in 0 until tpl.cols) {
        var r = 0
        while (r < tpl.rows) {
            if (r to c in blank && (r == 0 || r - 1 to c !in blank)) {
                val cells = mutableListOf<Pair<Int, Int>>()
                var rr = r
                while (rr < tpl.rows && rr to c in blank) { cells.add(rr to c); rr++ }
                if (cells.size >= 2) runs.add(DerivedRun(cells, across = false))
                r = rr
            } else r++
        }
    }
    // Every clue position must be a wall (in bounds and not a blank).
    for (run in runs) {
        val clue = run.clueCell()
        check(clue.first >= 0 && clue.second >= 0) { "${tpl.name}: clue out of bounds for run ${run.cells.first()}" }
        check(clue !in blank) { "${tpl.name}: clue cell collides with a blank at $clue" }
        check(run.cells.size <= 4) { "${tpl.name}: derived run longer than 4 (${run.cells.size})" }
    }
    return runs
}

// Backtracking fill: digits 1..9, unique within every derived run.
fun fill(blanks: List<Pair<Int, Int>>, runs: List<DerivedRun>, rng: Random): Map<Pair<Int, Int>, Int>? {
    val runsOf = HashMap<Pair<Int, Int>, MutableList<DerivedRun>>()
    for (run in runs) for (cell in run.cells) runsOf.getOrPut(cell) { mutableListOf() }.add(run)
    val values = HashMap<Pair<Int, Int>, Int>()

    fun ok(cell: Pair<Int, Int>, v: Int): Boolean {
        for (run in runsOf[cell] ?: emptyList<DerivedRun>()) {
            for (other in run.cells) {
                if (other != cell && values[other] == v) return false
            }
        }
        return true
    }

    fun dfs(i: Int): Boolean {
        if (i == blanks.size) return true
        val cell = blanks[i]
        for (v in (1..9).shuffled(rng)) {
            if (!ok(cell, v)) continue
            values[cell] = v
            if (dfs(i + 1)) return true
            values.remove(cell)
        }
        return false
    }

    return if (dfs(0)) values else null
}

fun buildLevel(tier: Tier, idx: Int, rng: Random): String {
    for (attempt in 0 until 300) {
        val tpl = tier.templates[(idx + attempt) % tier.templates.size]
        val runs = deriveRuns(tpl)
        val values = fill(tpl.blanks, runs, rng) ?: continue

        // Compose the grid: walls everywhere, then clues and blanks.
        val grid = Array(tpl.rows) { Array(tpl.cols) { "." } }
        for (cell in tpl.blanks) grid[cell.first][cell.second] = "_"
        // Clue cells may carry both an across and a down sum.
        val acrossAt = HashMap<Pair<Int, Int>, Int>()
        val downAt = HashMap<Pair<Int, Int>, Int>()
        for (run in runs) {
            val sum = run.cells.sumOf { values.getValue(it) }
            if (run.across) acrossAt[run.clueCell()] = sum else downAt[run.clueCell()] = sum
        }
        for (cell in (acrossAt.keys + downAt.keys)) {
            val aSum = acrossAt[cell]
            val dSum = downAt[cell]
            grid[cell.first][cell.second] = when {
                aSum != null && dSum != null -> "B$aSum:$dSum"
                aSum != null -> "A$aSum"
                else -> "D$dSum"
            }
        }

        val solutions = tpl.blanks.map { values.getValue(it) }
        val sb = StringBuilder("${tpl.rows}x${tpl.cols}")
        for (row in grid) sb.append('|').append(row.joinToString(" "))
        sb.append('!').append(solutions.joinToString(","))
        val encoded = sb.toString()
        verify(encoded, tier.name, idx)
        return encoded
    }
    error("could not build ${tier.name} level ${idx + 1}")
}

// Re-parse and re-verify exactly as the apps will.
fun verify(encoded: String, tier: String, idx: Int) {
    val seg = encoded.split("!")
    val parts = seg[0].split("|")
    val dims = parts[0].split("x")
    val rows = dims[0].toInt(); val cols = dims[1].toInt()
    check(parts.size - 1 == rows) { "$tier ${idx + 1}: rows" }
    val grid = Array(rows) { r -> parts[r + 1].split(" ").toTypedArray() }
    grid.forEachIndexed { r, row -> check(row.size == cols) { "$tier ${idx + 1}: cols row $r" } }
    val solutions = seg[1].split(",").map { it.toInt() }

    // Collect blanks in row-major order and map solutions onto them.
    val blankCells = mutableListOf<Pair<Int, Int>>()
    for (r in 0 until rows) for (c in 0 until cols) if (grid[r][c] == "_") blankCells.add(r to c)
    check(blankCells.size == solutions.size) { "$tier ${idx + 1}: solution count" }
    val value = blankCells.zip(solutions).toMap()

    // Every clue's run must sum correctly with unique digits.
    fun runFrom(r: Int, c: Int, dr: Int, dc: Int): List<Pair<Int, Int>> {
        val out = mutableListOf<Pair<Int, Int>>()
        var rr = r + dr; var cc = c + dc
        while (rr in 0 until rows && cc in 0 until cols && grid[rr][cc] == "_") {
            out.add(rr to cc); rr += dr; cc += dc
        }
        return out
    }
    for (r in 0 until rows) for (c in 0 until cols) {
        val tok = grid[r][c]
        if (tok.startsWith("A") || tok.startsWith("B")) {
            val sum = if (tok.startsWith("B")) tok.substring(1).split(":")[0].toInt() else tok.substring(1).toInt()
            val run = runFrom(r, c, 0, 1)
            check(run.size >= 2) { "$tier ${idx + 1}: across run too short at $r,$c" }
            check(run.sumOf { value.getValue(it) } == sum) { "$tier ${idx + 1}: across sum" }
            check(run.map { value.getValue(it) }.toSet().size == run.size) { "$tier ${idx + 1}: across dup" }
        }
        if (tok.startsWith("D") || tok.startsWith("B")) {
            val sum = if (tok.startsWith("B")) tok.substring(1).split(":")[1].toInt() else tok.substring(1).toInt()
            val run = runFrom(r, c, 1, 0)
            check(run.size >= 2) { "$tier ${idx + 1}: down run too short at $r,$c" }
            check(run.sumOf { value.getValue(it) } == sum) { "$tier ${idx + 1}: down sum" }
            check(run.map { value.getValue(it) }.toSet().size == run.size) { "$tier ${idx + 1}: down dup" }
        }
    }
}

fun main(args: Array<String>) {
    val seed = if (args.isNotEmpty()) args[0].toLong() else 20260810L
    val variantsPerLevel = 3

    val all = HashMap<String, List<List<String>>>()
    var salt = 1
    for (tier in TIERS) {
        val rng = Random(seed * 31 + salt++)
        val levels = ArrayList<List<String>>()
        for (i in 0 until 50) {
            val variants = ArrayList<String>()
            for (v in 0 until variantsPerLevel) variants.add(buildLevel(tier, i, rng))
            levels.add(variants)
        }
        all[tier.name] = levels
        println("${tier.name}: 50 levels x $variantsPerLevel variants ok")
    }

    val header = "GENERATED by tools/kakuro_generator (seed $seed) - do not hand-edit"
    val androidOut = Paths.get(
        "..", "..", "app", "src", "main", "java", "com", "jigar", "me",
        "ui", "view", "home", "screens", "math_game_zone", "kakuro", "components", "KakuroLevels.kt"
    ).toAbsolutePath().normalize()
    val iosOut = Paths.get(
        "..", "..", "..", "iOS_Abacus", "Abacus", "Main", "Games",
        "Kakuro", "Components", "KakuroLevels.swift"
    ).toAbsolutePath().normalize()

    val kt = StringBuilder()
    kt.append("package com.jigar.me.ui.view.home.screens.math_game_zone.kakuro.components\n\n")
    kt.append("import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4\n\n")
    kt.append("// ").append(header).append("\n")
    kt.append("// Each level holds $variantsPerLevel variants; replays rotate through them.\n")
    kt.append("object KakuroLevels {\n")
    for (tier in listOf("easy", "medium", "hard", "veryHard")) {
        kt.append("    val ").append(tier).append(" = listOf(\n")
        for (variants in all.getValue(tier)) {
            kt.append("        listOf(\n")
            for (lv in variants) kt.append("            \"").append(lv).append("\",\n")
            kt.append("        ),\n")
        }
        kt.append("    )\n\n")
    }
    kt.append("    fun forDifficulty(difficulty: CommonDifficulty4): List<List<String>> = when (difficulty) {\n")
    kt.append("        CommonDifficulty4.easy -> easy\n")
    kt.append("        CommonDifficulty4.medium -> medium\n")
    kt.append("        CommonDifficulty4.hard -> hard\n")
    kt.append("        else -> veryHard\n    }\n}\n")

    val sw = StringBuilder()
    sw.append("// ").append(header).append("\n")
    sw.append("// Each level holds $variantsPerLevel variants; replays rotate through them.\n\n")
    sw.append("enum KakuroLevels {\n")
    for (tier in listOf("easy", "medium", "hard", "veryHard")) {
        sw.append("    static let ").append(tier).append(": [[String]] = [\n")
        for (variants in all.getValue(tier)) {
            sw.append("        [\n")
            for (lv in variants) sw.append("            \"").append(lv).append("\",\n")
            sw.append("        ],\n")
        }
        sw.append("    ]\n\n")
    }
    sw.append("    static func forDifficulty(_ difficulty: CommonDifficulty4) -> [[String]] {\n")
    sw.append("        switch difficulty {\n        case .easy: return easy\n")
    sw.append("        case .medium: return medium\n        case .hard: return hard\n")
    sw.append("        default: return veryHard\n        }\n    }\n}\n")

    Files.createDirectories(androidOut.parent)
    Files.writeString(androidOut, kt.toString(), StandardCharsets.UTF_8)
    println("wrote $androidOut")
    Files.createDirectories(iosOut.parent)
    Files.writeString(iosOut, sw.toString(), StandardCharsets.UTF_8)
    println("wrote $iosOut")
}
