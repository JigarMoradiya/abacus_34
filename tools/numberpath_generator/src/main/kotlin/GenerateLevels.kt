// Number Path level generator (dev tool — never ships in the app).
//
// Generates 4 tiers (easy/medium/hard/veryHard) x 50 levels x 3 variants and
// emits them as source files for both platforms (Android NumberPathLevels.kt,
// iOS NumberPathLevels.swift) so the two apps ship identical maze data.
//
// Level string format (mirrored by NumberPathModels on each platform):
//   "<rows>x<cols>|<row>|<row>|...!<startVal>!<target>!<canonicalPathCSV>"
// Row tokens (space separated):
//   .        wall / absent cell
//   S        start cell (its number is the <startVal> segment)
//   G        goal cell (win when the running total equals <target> here)
//   +3 -2 *2 /4   operation tiles applied when stepped on
// canonicalPathCSV = flat cell indexes of the intended solution path,
// including S and G — used by the in-game hint.
//
// Run from the repo root:
//   gradlew.bat -p tools\numberpath_generator run [--args="<seed>"]

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Random

const val WALL = "."
const val START = "S"
const val GOAL = "G"

data class Tier(
    val name: String,
    val ops: String,              // subset of "+-*/"
    val addMax: Int,              // max operand for + and -
    val mulFactors: List<Int>,    // allowed x and / factors
    val maxTotal: Int,
    val rows: Int,
    val cols: Int
) {
    // Number of operation tiles on the canonical path (ramps within the tier).
    fun opsCount(i: Int): Int = when (name) {
        "easy" -> 3 + i / 17          // 3..5
        "medium" -> 4 + i / 17        // 4..6
        "hard" -> 5 + i / 17          // 5..7
        else -> 7 + i / 17            // 7..9
    }

    fun decoys(i: Int): Int = when (name) {
        "easy" -> 1
        "medium" -> 2
        "hard" -> 2 + i / 25          // 2..3
        else -> 3 + i / 25            // 3..4
    }

    // Hard/veryHard decoy branches may end next to the goal — trap paths that
    // reach G with the wrong total.
    val allowTraps: Boolean get() = name == "hard" || name == "veryHard"
}

val TIERS = listOf(
    Tier("easy", "+-", 5, emptyList(), 20, 4, 6),
    Tier("medium", "+-*", 9, listOf(2, 3), 60, 5, 7),
    Tier("hard", "+-*/", 12, listOf(2, 3), 100, 5, 8),
    Tier("veryHard", "+-*/", 15, listOf(2, 3, 4), 200, 5, 9)
)

val DIRS = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

fun neighbors(r: Int, c: Int, rows: Int, cols: Int): List<Pair<Int, Int>> =
    DIRS.map { (dr, dc) -> r + dr to c + dc }
        .filter { (nr, nc) -> nr in 0 until rows && nc in 0 until cols }

// Self-avoiding random walk of exactly `length` cells starting at `start`.
fun carveWalk(
    start: Pair<Int, Int>, length: Int, rows: Int, cols: Int,
    blocked: Set<Pair<Int, Int>>, rng: Random
): List<Pair<Int, Int>>? {
    val path = ArrayList<Pair<Int, Int>>()
    path.add(start)
    fun dfs(): Boolean {
        if (path.size == length) return true
        val (r, c) = path.last()
        val options = neighbors(r, c, rows, cols)
            .filter { it !in blocked && it !in path }
            .shuffled(rng)
        for (next in options) {
            path.add(next)
            if (dfs()) return true
            path.removeAt(path.size - 1)
        }
        return false
    }
    return if (dfs()) path else null
}

// Applies `op` to `total`, or null if the result is invalid for the tier.
fun apply(total: Int, op: Char, operand: Int, t: Tier): Int? {
    val out = when (op) {
        '+' -> total + operand
        '-' -> total - operand
        '*' -> total * operand
        '/' -> if (operand != 0 && total % operand == 0) total / operand else return null
        else -> return null
    }
    return if (out in 1..t.maxTotal) out else null
}

data class OpTile(val op: Char, val operand: Int) {
    fun token(): String = "$op$operand"
}

fun randomOp(total: Int, t: Tier, rng: Random): Pair<OpTile, Int>? {
    for (attempt in 0 until 60) {
        val op = t.ops[rng.nextInt(t.ops.length)]
        val operand = when (op) {
            '*', '/' -> t.mulFactors[rng.nextInt(t.mulFactors.size)]
            else -> 1 + rng.nextInt(t.addMax)
        }
        val next = apply(total, op, operand, t) ?: continue
        if (next == total) continue
        return OpTile(op, operand) to next
    }
    return null
}

class Maze(val rows: Int, val cols: Int) {
    val grid = Array(rows) { Array(cols) { WALL } }
    var start = 0 to 0
    var goal = 0 to 0
    var startVal = 0
    var target = 0
    var canonical: List<Pair<Int, Int>> = emptyList()

    fun idx(cell: Pair<Int, Int>) = cell.first * cols + cell.second

    fun encode(): String {
        val sb = StringBuilder("${rows}x$cols")
        for (row in grid) sb.append('|').append(row.joinToString(" "))
        sb.append('!').append(startVal)
        sb.append('!').append(target)
        sb.append('!').append(canonical.joinToString(",") { idx(it).toString() })
        return sb.toString()
    }
}

fun buildLevel(t: Tier, idx: Int, rng: Random): String {
    for (attempt in 0 until 800) {
        val maze = Maze(t.rows, t.cols)
        val ops = t.opsCount(idx)
        val pathCells = ops + 2  // S + ops + G

        val start = rng.nextInt(t.rows) to rng.nextInt(t.cols)
        val path = carveWalk(start, pathCells, t.rows, t.cols, emptySet(), rng) ?: continue

        // Assign the start value and the op chain along the canonical path.
        var total = 1 + rng.nextInt(t.addMax + 3)
        maze.startVal = total
        val tiles = ArrayList<OpTile>()
        var ok = true
        for (step in 1 until pathCells - 1) {
            val pick = randomOp(total, t, rng)
            if (pick == null) { ok = false; break }
            tiles.add(pick.first)
            total = pick.second
        }
        if (!ok) continue
        if (total == maze.startVal) continue
        maze.target = total
        maze.start = path.first()
        maze.goal = path.last()
        maze.canonical = path

        maze.grid[path.first().first][path.first().second] = START
        maze.grid[path.last().first][path.last().second] = GOAL
        for (step in 1 until pathCells - 1) {
            val (r, c) = path[step]
            maze.grid[r][c] = tiles[step - 1].token()
        }

        // Decoy branches: dead ends off the canonical path (Hard/VeryHard may
        // end next to the goal, creating wrong-total trap paths).
        val used = HashSet(path)
        var decoysAdded = 0
        for (d in 0 until t.decoys(idx) * 3) {
            if (decoysAdded >= t.decoys(idx)) break
            val branchFrom = path[1 + rng.nextInt(pathCells - 2)]
            val branchLen = 2 + rng.nextInt(3)
            val free = neighbors(branchFrom.first, branchFrom.second, t.rows, t.cols)
                .filter { it !in used }
            if (free.isEmpty()) continue
            val first = free[rng.nextInt(free.size)]
            val branch = carveWalk(first, branchLen, t.rows, t.cols, used, rng) ?: continue
            var bTotal = 1 + rng.nextInt(t.addMax)
            for ((r, c) in branch) {
                val pick = randomOp(bTotal, t, rng) ?: OpTile('+', 1) to (bTotal + 1)
                maze.grid[r][c] = pick.first.token()
                bTotal = pick.second
            }
            used.addAll(branch)
            decoysAdded++
        }
        if (decoysAdded == 0) continue

        // Verify: enumerate every simple path S -> G; require at least one
        // winner (total == target) and not too many (puzzle stays a puzzle).
        val winners = countWinners(maze, t)
        if (winners < 1 || winners > 4) continue

        return maze.encode()
    }
    error("could not build ${t.name} level ${idx + 1}")
}

// DFS over all simple paths from S to G, counting those whose total == target.
fun countWinners(maze: Maze, t: Tier): Int {
    var winners = 0
    var explored = 0
    val visited = HashSet<Pair<Int, Int>>()

    fun value(cell: Pair<Int, Int>, total: Int): Int? {
        val tok = maze.grid[cell.first][cell.second]
        if (tok == START || tok == GOAL) return total
        val op = tok[0]
        val operand = tok.substring(1).toIntOrNull() ?: return null
        return apply(total, op, operand, t)
    }

    fun dfs(cell: Pair<Int, Int>, total: Int) {
        if (explored > 50000) return
        explored++
        if (cell == maze.goal) {
            if (total == maze.target) winners++
            return
        }
        for (next in neighbors(cell.first, cell.second, maze.rows, maze.cols)) {
            if (next in visited) continue
            val tok = maze.grid[next.first][next.second]
            if (tok == WALL || tok == START) continue
            val nextTotal = if (tok == GOAL) total else value(next, total) ?: continue
            visited.add(next)
            dfs(next, nextTotal)
            visited.remove(next)
        }
    }

    visited.add(maze.start)
    dfs(maze.start, maze.startVal)
    return winners
}

// Re-parse and re-verify the encoded string exactly as the apps will.
fun verify(encoded: String, t: Tier, idx: Int) {
    val seg = encoded.split("!")
    check(seg.size == 4) { "${t.name} ${idx + 1}: segment count" }
    val parts = seg[0].split("|")
    val dims = parts[0].split("x")
    val rows = dims[0].toInt(); val cols = dims[1].toInt()
    check(parts.size - 1 == rows) { "${t.name} ${idx + 1}: rows" }
    val maze = Maze(rows, cols)
    for (r in 0 until rows) {
        val toks = parts[r + 1].split(" ")
        check(toks.size == cols) { "${t.name} ${idx + 1}: cols row $r" }
        for (c in 0 until cols) {
            maze.grid[r][c] = toks[c]
            if (toks[c] == START) maze.start = r to c
            if (toks[c] == GOAL) maze.goal = r to c
        }
    }
    maze.startVal = seg[1].toInt()
    maze.target = seg[2].toInt()
    val pathIdx = seg[3].split(",").map { it.toInt() }
    maze.canonical = pathIdx.map { it / cols to it % cols }
    check(maze.canonical.first() == maze.start) { "${t.name} ${idx + 1}: path start" }
    check(maze.canonical.last() == maze.goal) { "${t.name} ${idx + 1}: path goal" }

    // Walk the canonical path — it must land exactly on the target.
    var total = maze.startVal
    for (step in 1 until maze.canonical.size - 1) {
        val (r, c) = maze.canonical[step]
        val tok = maze.grid[r][c]
        val next = apply(total, tok[0], tok.substring(1).toInt(), t)
        checkNotNull(next) { "${t.name} ${idx + 1}: canonical step invalid at $r,$c" }
        total = next
    }
    check(total == maze.target) { "${t.name} ${idx + 1}: canonical total $total != ${maze.target}" }
    // Canonical steps must be orthogonally adjacent.
    for (s in 1 until maze.canonical.size) {
        val (r1, c1) = maze.canonical[s - 1]; val (r2, c2) = maze.canonical[s]
        check(Math.abs(r1 - r2) + Math.abs(c1 - c2) == 1) { "${t.name} ${idx + 1}: path not adjacent" }
    }
}

fun main(args: Array<String>) {
    val seed = if (args.isNotEmpty()) args[0].toLong() else 20260809L
    val variantsPerLevel = 3

    val all = HashMap<String, List<List<String>>>()
    var salt = 1
    for (t in TIERS) {
        val rng = Random(seed * 31 + salt++)
        val levels = ArrayList<List<String>>()
        for (i in 0 until 50) {
            val variants = ArrayList<String>()
            for (v in 0 until variantsPerLevel) {
                val lv = buildLevel(t, i, rng)
                verify(lv, t, i)
                variants.add(lv)
            }
            levels.add(variants)
        }
        all[t.name] = levels
        println("${t.name}: 50 levels x $variantsPerLevel variants ok")
    }

    val header = "GENERATED by tools/numberpath_generator (seed $seed) - do not hand-edit"
    val androidOut = Paths.get(
        "..", "..", "app", "src", "main", "java", "com", "jigar", "me",
        "ui", "view", "home", "screens", "math_game_zone", "number_path", "components", "NumberPathLevels.kt"
    ).toAbsolutePath().normalize()
    val iosOut = Paths.get(
        "..", "..", "..", "iOS_Abacus", "Abacus", "Main", "Games",
        "Number Path", "Components", "NumberPathLevels.swift"
    ).toAbsolutePath().normalize()

    val kt = StringBuilder()
    kt.append("package com.jigar.me.ui.view.home.screens.math_game_zone.number_path.components\n\n")
    kt.append("import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4\n\n")
    kt.append("// ").append(header).append("\n")
    kt.append("// Each level holds $variantsPerLevel variants; replays rotate through them.\n")
    kt.append("object NumberPathLevels {\n")
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
    sw.append("enum NumberPathLevels {\n")
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
