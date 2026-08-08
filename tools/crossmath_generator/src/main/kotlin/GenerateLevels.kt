// Cross Math level generator (dev tool — never ships in the app).
//
// Generates 3 tiers (easy/medium/hard) x 50 fixed levels and emits them as
// source files for both platforms (Android CrossMathLevels.kt, iOS
// CrossMathLevels.swift) so the two apps ship identical level data.
//
// Level string format (mirrored by CrossMathModels on each platform):
//   "<rows>x<cols>|<row>|<row>|...!<trayCSV>"
// Row tokens (space separated):
//   .        absent cell
//   12       given number
//   [7]      blank number cell whose canonical solution is 7
//   + - * /  given operator cell
//   =        given equals cell
// Every equation is a straight 5-cell run  a op b = c  (horizontal or
// vertical); runs intersect only at number cells (even offsets).
//
// Run from the repo root:
//   gradlew.bat -p tools\crossmath_generator run [--args="<seed>"]

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Collections
import java.util.Random
import java.util.TreeSet

data class GridRun(val r: Int, val c: Int, val horizontal: Boolean) {
    fun cells(): List<IntArray> = List(5) { i ->
        intArrayOf(if (horizontal) r else r + i, if (horizontal) c + i else c)
    }
}

data class Template(val name: String, val rows: Int, val cols: Int, val runs: List<GridRun>) {
    init {
        val seen = HashMap<Long, String>()
        for (run in runs) {
            run.cells().forEachIndexed { off, cell ->
                val (r, c) = cell[0] to cell[1]
                check(r in 0 until rows && c in 0 until cols) { "$name: cell out of bounds $r,$c" }
                val kind = if (off % 2 == 0) "num" else if (off == 1) "op" else "eq"
                val prev = seen[key(r, c)]
                check(prev == null || (prev == "num" && kind == "num")) { "$name: bad overlap at $r,$c" }
                seen[key(r, c)] = kind
            }
        }
    }

    fun numberCells(): List<Long> {
        val out = TreeSet<Long>()
        for (run in runs) {
            val cells = run.cells()
            for (off in 0..4 step 2) out.add(key(cells[off][0], cells[off][1]))
        }
        return out.toList()
    }

    fun intersections(): Set<Long> {
        val count = HashMap<Long, Int>()
        for (run in runs) {
            val cells = run.cells()
            for (off in 0..4 step 2) count.merge(key(cells[off][0], cells[off][1]), 1, Int::plus)
        }
        return count.filterValues { it > 1 }.keys
    }
}

fun key(r: Int, c: Int): Long = r * 1000L + c
fun keyR(k: Long): Int = (k / 1000).toInt()
fun keyC(k: Long): Int = (k % 1000).toInt()

fun h(r: Int, c: Int) = GridRun(r, c, true)
fun v(r: Int, c: Int) = GridRun(r, c, false)

// ── shape expansion: every template also appears mirrored left-right,
// top-bottom, and both — distinct silhouettes multiply for free, and the
// math/intersection rules survive mirroring untouched.
fun flipH(t: Template): Template = Template(t.name + "_fh", t.rows, t.cols, t.runs.map { run ->
    if (run.horizontal) GridRun(run.r, t.cols - 1 - (run.c + 4), true)
    else GridRun(run.r, t.cols - 1 - run.c, false)
})

fun flipV(t: Template): Template = Template(t.name + "_fv", t.rows, t.cols, t.runs.map { run ->
    if (run.horizontal) GridRun(t.rows - 1 - run.r, run.c, true)
    else GridRun(t.rows - 1 - (run.r + 4), run.c, false)
})

fun expand(base: List<Template>): List<Template> {
    val out = LinkedHashMap<String, Template>()
    for (t in base) for (variant in listOf(t, flipH(t), flipV(t), flipV(flipH(t)))) {
        val sig = variant.runs.map { "${it.r},${it.c},${it.horizontal}" }.sorted().joinToString(";")
        out.putIfAbsent(sig, variant)
    }
    return out.values.toList()
}

// ── shape templates (all intersections land on even offsets) ───────────────
val EASY_2 = expand(listOf(
    Template("plus", 5, 5, listOf(h(2, 0), v(0, 2))),
    Template("L_tl", 5, 5, listOf(h(0, 0), v(0, 0))),
    Template("L_tr", 5, 5, listOf(h(0, 0), v(0, 4))),
    Template("L_bl", 5, 5, listOf(h(4, 0), v(0, 0))),
    Template("L_br", 5, 5, listOf(h(4, 0), v(0, 4))),
    Template("T_top", 5, 5, listOf(h(0, 0), v(0, 2))),
    Template("T_bot", 5, 5, listOf(h(4, 0), v(0, 2))),
    Template("mid_left", 5, 5, listOf(h(2, 0), v(0, 0)))
))
val EASY_3 = expand(listOf(
    Template("U", 5, 5, listOf(h(4, 0), v(0, 0), v(0, 4))),
    Template("arch", 5, 5, listOf(h(0, 0), v(0, 0), v(0, 4))),
    Template("S5", 5, 5, listOf(h(0, 0), v(0, 4), h(4, 0))),
    Template("Z5", 5, 5, listOf(h(0, 0), v(0, 0), h(4, 0)))
))
val MEDIUM = expand(listOf(
    Template("H_shape", 5, 5, listOf(v(0, 0), v(0, 4), h(2, 0))),
    Template("ring", 5, 5, listOf(h(0, 0), h(4, 0), v(0, 0), v(0, 4))),
    Template("stair7", 5, 7, listOf(h(0, 0), v(0, 4), h(4, 2))),
    Template("hook7", 5, 7, listOf(h(0, 2), v(0, 2), h(4, 2))),
    Template("fork7", 5, 7, listOf(h(0, 0), v(0, 0), v(0, 4), h(4, 0))),
    Template("comb9", 5, 9, listOf(h(0, 0), h(0, 4), v(0, 2))),
    Template("windmill7", 5, 7, listOf(h(0, 2), v(0, 2), h(4, 0))),
    Template("T9", 5, 9, listOf(h(0, 0), h(0, 4), v(0, 4))),
    Template("rungs5", 5, 5, listOf(h(0, 0), h(4, 0), v(0, 2))),
    Template("zigzag9", 5, 9, listOf(h(0, 0), v(0, 4), h(4, 4))),
    Template("fork5", 5, 5, listOf(v(0, 0), h(2, 0), h(4, 0)))
))
val HARD = expand(listOf(
    Template("snake9", 5, 9, listOf(h(0, 0), v(0, 4), h(4, 4), v(0, 8))),
    Template("ring9", 5, 9, listOf(h(0, 0), h(4, 0), v(0, 0), v(0, 4), h(4, 4))),
    Template("ladder9", 5, 9, listOf(h(0, 0), h(0, 4), h(4, 0), h(4, 4), v(0, 0), v(0, 8))),
    Template("zig7", 7, 7, listOf(v(0, 2), h(2, 0), v(2, 4), h(6, 2))),
    Template("tower7", 7, 7, listOf(h(0, 0), v(0, 2), h(4, 2), v(2, 6), h(6, 2))),
    Template("grid9", 5, 9, listOf(h(0, 0), h(0, 4), v(0, 2), v(0, 6), h(4, 2))),
    Template("bigH9", 5, 9, listOf(v(0, 0), v(0, 8), h(2, 0), h(2, 4))),
    Template("window7", 5, 7, listOf(v(0, 2), v(0, 6), h(0, 2), h(4, 2))),
    Template("E5", 5, 5, listOf(h(0, 0), h(2, 0), h(4, 0), v(0, 0))),
    Template("doubleU9", 5, 9, listOf(v(0, 2), v(0, 6), h(0, 0), h(0, 4), h(4, 0), h(4, 4)))
))
// Long, dense shapes like the classic cross-math reference boards.
val VERY_HARD = expand(listOf(
    // The reference board: 3 verticals off the top row, 2 ladders, a side arm.
    Template("reference7", 7, 7, listOf(
        h(0, 0), v(0, 0), v(0, 2), v(0, 4), h(2, 2), h(4, 0), v(2, 6), h(6, 2)
    )),
    Template("ladder9x7", 5, 9, listOf(
        h(0, 0), h(0, 4), v(0, 0), v(0, 4), v(0, 8), h(4, 0), h(4, 4)
    )),
    Template("snake11", 7, 11, listOf(
        h(0, 0), v(0, 4), h(4, 4), v(2, 8), h(6, 6)
    )),
    Template("doublecross9", 5, 9, listOf(
        v(0, 2), v(0, 6), h(2, 0), h(2, 4), h(4, 0), h(4, 4)
    )),
    Template("arch9", 5, 9, listOf(
        h(0, 0), h(0, 4), v(0, 4), v(0, 8), h(4, 4)
    )),
    Template("castle9", 7, 9, listOf(
        v(0, 0), v(0, 4), h(0, 0), h(4, 0), h(4, 4), v(2, 8), h(6, 4)
    ))
))

// ── tier configuration ──────────────────────────────────────────────────────
data class Tier(
    val name: String, val ops: String, val min: Int, val maxOperand: Int, val maxResult: Int,
    val mulHi: Int = 9,   // multiplication operands stay in 2..mulHi
    val divHi: Int = 12   // division divisor/quotient stay in 2..divHi
) {
    // Extra tray numbers that fit nowhere (near-miss values) — real difficulty,
    // because leftovers can no longer be brute-forced into the last blanks.
    val decoys: Int get() = if (name == "veryHard") 2 else 0

    // Blanked-out operator signs (kid picks from a +−×÷ palette in the app).
    fun opBlanks(i: Int): Int = when (name) {
        "hard" -> 1 + i / 25       // 1..2
        "veryHard" -> 2 + i / 25   // 2..3
        else -> 0
    }

    fun blanks(i: Int): Int = when (name) {
        "easy" -> 2 + i / 17      // 2..4
        "medium" -> 3 + i / 17    // 3..5
        "hard" -> 4 + i / 13      // 4..7
        else -> 6 + i / 17        // 6..8 (veryHard)
    }

    fun templates(i: Int): List<Template> = when (name) {
        "easy" -> if (i < 30) EASY_2 else EASY_3
        "medium" -> MEDIUM
        "hard" -> HARD
        else -> VERY_HARD
    }
}

val TIERS = listOf(
    Tier("easy", "+-", 1, 10, 20),
    Tier("medium", "+-*", 1, 15, 60),
    Tier("hard", "+-*/", 1, 75, 150, mulHi = 12, divHi = 12),
    Tier("veryHard", "+-*/", 1, 100, 200, mulHi = 14, divHi = 14)
)

// ── equation constraints ────────────────────────────────────────────────────
fun opOk(a: Int, op: Char, b: Int, c: Int, t: Tier): Boolean {
    if (a < t.min || b < t.min || c < t.min) return false
    if (b > t.maxOperand || c > t.maxResult) return false
    return when (op) {
        '+' -> a + b == c && a <= t.maxOperand
        '-' -> a - b == c && a <= t.maxResult
        '*' -> a * b == c && a in 2..t.mulHi && b in 2..t.mulHi
        '/' -> b in 2..t.divHi && a % b == 0 && a / b == c && a <= t.maxResult
        else -> false
    }
}

data class Candidate(val a: Int, val op: Char, val b: Int, val c: Int)

fun candidatesForRun(fixed: Map<Long, Int>, cells: List<IntArray>, t: Tier, rng: Random): List<Candidate> {
    val a0 = fixed[key(cells[0][0], cells[0][1])]
    val b0 = fixed[key(cells[2][0], cells[2][1])]
    val c0 = fixed[key(cells[4][0], cells[4][1])]
    val ops = ArrayList(t.ops.toList())
    Collections.shuffle(ops, rng)
    val out = ArrayList<Candidate>()
    for (op in ops) {
        for (tries in 0 until 300) {
            val a: Int; val b: Int; val c: Int
            when (op) {
                '*' -> {
                    a = a0 ?: (2 + rng.nextInt(t.mulHi - 1))
                    b = b0 ?: (2 + rng.nextInt(t.mulHi - 1))
                    c = a * b
                }
                '/' -> {
                    b = b0 ?: (2 + rng.nextInt(t.divHi - 1))
                    c = c0 ?: (2 + rng.nextInt(t.divHi - 1))
                    a = a0 ?: (b * c)
                }
                else -> {
                    a = a0 ?: (t.min + rng.nextInt(t.maxOperand - t.min + 1))
                    b = b0 ?: (t.min + rng.nextInt(t.maxOperand - t.min + 1))
                    c = if (op == '+') a + b else a - b
                }
            }
            if (c0 != null && c != c0) continue
            if (op == '/' && a0 != null && b * c != a0) continue
            if (opOk(a, op, b, c, t)) { out.add(Candidate(a, op, b, c)); break }
        }
    }
    Collections.shuffle(out, rng)
    return out
}

// ── backtracking fill ───────────────────────────────────────────────────────
data class Filled(val values: Map<Long, Int>, val runOps: Map<GridRun, Char>)

fun fillTemplate(tpl: Template, t: Tier, rng: Random): Filled? =
    solve(ArrayList(tpl.runs), 0, HashMap(), HashMap(), t, rng)

fun solve(
    runs: MutableList<GridRun>, i: Int,
    fixed: Map<Long, Int>, ops: Map<GridRun, Char>,
    t: Tier, rng: Random
): Filled? {
    if (i == runs.size) return Filled(fixed, ops)
    // pick the remaining run with the most already-fixed number cells
    var best = i; var bestCount = -1
    for (k in i until runs.size) {
        var count = 0
        val cells = runs[k].cells()
        for (off in 0..4 step 2) if (fixed.containsKey(key(cells[off][0], cells[off][1]))) count++
        if (count > bestCount) { bestCount = count; best = k }
    }
    Collections.swap(runs, i, best)
    val run = runs[i]
    val cells = run.cells()
    val cands = candidatesForRun(fixed, cells, t, rng)
    for (ci in 0 until minOf(cands.size, 40)) {
        val cand = cands[ci]
        val nf = HashMap(fixed)
        nf[key(cells[0][0], cells[0][1])] = cand.a
        nf[key(cells[2][0], cells[2][1])] = cand.b
        nf[key(cells[4][0], cells[4][1])] = cand.c
        val no = HashMap(ops)
        no[run] = cand.op
        val res = solve(runs, i + 1, nf, no, t, rng)
        if (res != null) return res
    }
    return null
}

// ── level build + encode ────────────────────────────────────────────────────
fun buildLevel(t: Tier, idx: Int, rng: Random): String {
    for (attempt in 0 until 500) {
        val templates = t.templates(idx)
        val tpl = templates[(idx + attempt) % templates.size]
        val filled = fillTemplate(tpl, t, rng) ?: continue
        val pool = ArrayList(tpl.numberCells())
        val inter = tpl.intersections()
        val wantBlanks = minOf(t.blanks(idx), pool.size - 1)
        Collections.shuffle(pool, rng)
        if (t.name == "hard" || t.name == "veryHard") {
            // prefer intersection cells for blanks at the top tiers (stable sort)
            pool.sortWith(compareByDescending { it in inter })
        }
        val blanks: List<Long>
        if (t.name == "veryHard") {
            // Twist: one whole equation is blanked out (both numbers AND the
            // answer) — no anchor, it can only be solved via the crossing runs.
            val run = tpl.runs[rng.nextInt(tpl.runs.size)]
            val cells = run.cells()
            val mystery = arrayListOf(
                key(cells[0][0], cells[0][1]),
                key(cells[2][0], cells[2][1]),
                key(cells[4][0], cells[4][1])
            )
            for (cellKey in pool) {
                if (mystery.size >= wantBlanks) break
                if (cellKey !in mystery) mystery.add(cellKey)
            }
            blanks = mystery
        } else {
            blanks = pool.subList(0, wantBlanks)
        }
        val tray = ArrayList(blanks.map { filled.values.getValue(it) })
        // Decoys: values close to real answers so they look plausible but
        // never complete any equation.
        repeat(t.decoys) {
            var decoy: Int
            var guard = 0
            do {
                val base = tray[rng.nextInt(tray.size)]
                val delta = (1 + rng.nextInt(3)) * (if (rng.nextBoolean()) 1 else -1)
                decoy = (base + delta).coerceIn(1, t.maxResult)
                guard++
            } while (guard < 50 && wouldCompleteAnyRun(tpl, filled, blanks.toSet(), decoy))
            tray.add(decoy)
        }
        Collections.shuffle(tray, rng)
        // Blank some operator signs too (hard/veryHard) — the kid must pick
        // the right sign from the +−×÷ palette.
        val opCells = ArrayList<Long>()
        for (run in filled.runOps.keys) {
            val cells = run.cells()
            opCells.add(key(cells[1][0], cells[1][1]))
        }
        Collections.shuffle(opCells, rng)
        val blankOps = opCells.take(minOf(t.opBlanks(idx), opCells.size)).toSet()
        return encode(tpl, filled, blanks.toSet(), tray, blankOps)
    }
    error("could not build ${t.name} level ${idx + 1}")
}

// True when the candidate decoy, dropped into any single blank (all other
// cells at their canonical values), would satisfy that equation — such a
// value is not a safe decoy.
fun wouldCompleteAnyRun(tpl: Template, filled: Filled, blanks: Set<Long>, decoy: Int): Boolean {
    for ((run, op) in filled.runOps) {
        val cells = run.cells()
        val keys = listOf(
            key(cells[0][0], cells[0][1]),
            key(cells[2][0], cells[2][1]),
            key(cells[4][0], cells[4][1])
        )
        for (bi in 0..2) {
            if (keys[bi] !in blanks) continue
            val a = if (bi == 0) decoy else filled.values.getValue(keys[0])
            val b = if (bi == 1) decoy else filled.values.getValue(keys[1])
            val c = if (bi == 2) decoy else filled.values.getValue(keys[2])
            val ok = when (op) {
                '+' -> a + b == c
                '-' -> a - b == c
                '*' -> a * b == c
                '/' -> b != 0 && a % b == 0 && a / b == c
                else -> false
            }
            if (ok) return true
        }
    }
    return false
}

fun encode(tpl: Template, filled: Filled, blanks: Set<Long>, tray: List<Int>, blankOps: Set<Long> = emptySet()): String {
    val grid = Array(tpl.rows) { Array(tpl.cols) { "." } }
    for ((run, op) in filled.runOps) {
        val cells = run.cells()
        val opKey = key(cells[1][0], cells[1][1])
        grid[cells[1][0]][cells[1][1]] = if (opKey in blankOps) "[$op]" else op.toString()
        grid[cells[3][0]][cells[3][1]] = "="
    }
    for (cell in tpl.numberCells()) {
        val r = keyR(cell); val c = keyC(cell)
        val value = filled.values.getValue(cell)
        grid[r][c] = if (cell in blanks) "[$value]" else value.toString()
    }
    val sb = StringBuilder("${tpl.rows}x${tpl.cols}")
    for (row in grid) sb.append('|').append(row.joinToString(" "))
    sb.append('!').append(tray.joinToString(","))
    return sb.toString()
}

// ── verification (mirrors the app-side parser) ──────────────────────────────
fun verify(encoded: String, tier: String, idx: Int) {
    val main = encoded.split("!")
    val parts = main[0].split("|")
    val dims = parts[0].split("x")
    val rows = dims[0].toInt(); val cols = dims[1].toInt()
    check(parts.size - 1 == rows) { "$tier ${idx + 1}: row count" }
    val grid = Array(rows) { emptyArray<String>() }
    val solutions = HashMap<Long, Int>()
    var opBlankCount = 0
    for (r in 0 until rows) {
        grid[r] = parts[r + 1].split(" ").toTypedArray()
        check(grid[r].size == cols) { "$tier ${idx + 1}: col count row $r" }
        for (c in 0 until cols) {
            val tok = grid[r][c]
            if (tok.startsWith("[")) {
                val inner = tok.substring(1, tok.length - 1)
                if (inner.length == 1 && inner[0] in "+-*/") opBlankCount++
                else solutions[key(r, c)] = inner.toInt()
            }
        }
    }
    val tray = if (main[1].isEmpty()) emptyList() else main[1].split(",").map { it.toInt() }
    check(tray.size >= solutions.size) { "$tier ${idx + 1}: tray smaller than blanks" }
    // Tray must contain every solution value (extras are decoys).
    val remaining = ArrayList(tray)
    for (v in solutions.values) check(remaining.remove(v)) { "$tier ${idx + 1}: tray missing solution $v" }

    val runs = findRuns(rows, cols, grid)
    check(runs.isNotEmpty()) { "$tier ${idx + 1}: no runs" }
    check(satisfied(grid, runs, HashMap(solutions))) { "$tier ${idx + 1}: canonical solution invalid" }

    // ambiguity: count distinct valid assignments of tray values to blanks
    // (skipped for decoy/blank-sign tiers — the space gets too large).
    if (tray.size == solutions.size && opBlankCount == 0) {
        val blankCells = solutions.keys.sorted()
        val perms = HashSet<List<Int>>()
        permute(ArrayList(tray), 0, perms)
        var valid = 0
        for (perm in perms) {
            val fills = HashMap<Long, Int>()
            for (i in blankCells.indices) fills[blankCells[i]] = perm[i]
            if (satisfied(grid, runs, fills)) valid++
        }
        if (valid > 12) println("  warn: $tier level ${idx + 1} has $valid valid fills")
    }
}

fun permute(arr: MutableList<Int>, k: Int, out: MutableSet<List<Int>>) {
    if (out.size > 20000) return
    if (k == arr.size) { out.add(ArrayList(arr)); return }
    for (i in k until arr.size) {
        Collections.swap(arr, k, i)
        permute(arr, k + 1, out)
        Collections.swap(arr, k, i)
    }
}

fun isOpTok(tok: String): Boolean =
    tok in listOf("+", "-", "*", "/") || (tok.length == 3 && tok[0] == '[' && tok[1] in "+-*/" && tok[2] == ']')

fun isNum(tok: String): Boolean = tok != "." && tok != "=" && !isOpTok(tok)

fun opOf(tok: String): Char = if (tok.startsWith("[")) tok[1] else tok[0]

fun findRuns(rows: Int, cols: Int, grid: Array<Array<String>>): List<LongArray> {
    val runs = ArrayList<LongArray>()
    for (r in 0 until rows) for (c in 0 until cols) {
        if (c + 4 < cols && isNum(grid[r][c]) && isOpTok(grid[r][c + 1]) && isNum(grid[r][c + 2]) &&
            grid[r][c + 3] == "=" && isNum(grid[r][c + 4])
        ) runs.add(longArrayOf(key(r, c), key(r, c + 1), key(r, c + 2), key(r, c + 3), key(r, c + 4)))
        if (r + 4 < rows && isNum(grid[r][c]) && isOpTok(grid[r + 1][c]) && isNum(grid[r + 2][c]) &&
            grid[r + 3][c] == "=" && isNum(grid[r + 4][c])
        ) runs.add(longArrayOf(key(r, c), key(r + 1, c), key(r + 2, c), key(r + 3, c), key(r + 4, c)))
    }
    return runs
}

fun satisfied(grid: Array<Array<String>>, runs: List<LongArray>, fills: Map<Long, Int>): Boolean {
    for (run in runs) {
        val a = valueAt(grid, run[0], fills) ?: return false
        val b = valueAt(grid, run[2], fills) ?: return false
        val c = valueAt(grid, run[4], fills) ?: return false
        val op = opOf(grid[keyR(run[1])][keyC(run[1])])
        val ok = when (op) {
            '+' -> a + b == c
            '-' -> a - b == c
            '*' -> a * b == c
            '/' -> b != 0 && a % b == 0 && a / b == c
            else -> false
        }
        if (!ok) return false
    }
    return true
}

fun valueAt(grid: Array<Array<String>>, cell: Long, fills: Map<Long, Int>): Int? {
    val tok = grid[keyR(cell)][keyC(cell)]
    return if (tok.startsWith("[")) fills[cell] else tok.toInt()
}

// ── emit ────────────────────────────────────────────────────────────────────
fun main(args: Array<String>) {
    val seed = if (args.isNotEmpty()) args[0].toLong() else 20260808L
    println("shape pool: easy=${EASY_2.size}+${EASY_3.size} medium=${MEDIUM.size} hard=${HARD.size} veryHard=${VERY_HARD.size}")

    // 3 variants per level so replaying the same level shows different numbers.
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
                verify(lv, t.name, i)
                variants.add(lv)
            }
            levels.add(variants)
        }
        all[t.name] = levels
        println("${t.name}: 50 levels x $variantsPerLevel variants ok")
    }

    val header = "GENERATED by tools/crossmath_generator (seed $seed) - do not hand-edit"
    val androidOut = Paths.get(
        "..", "..", "app", "src", "main", "java", "com", "jigar", "me",
        "ui", "view", "home", "screens", "math_game_zone", "cross_math", "components", "CrossMathLevels.kt"
    ).toAbsolutePath().normalize()
    val iosOut = Paths.get(
        "..", "..", "..", "iOS_Abacus", "Abacus", "Main", "Games",
        "Cross Math", "Components", "CrossMathLevels.swift"
    ).toAbsolutePath().normalize()

    val kt = StringBuilder()
    kt.append("package com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.components\n\n")
    kt.append("import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4\n\n")
    kt.append("// ").append(header).append("\n")
    kt.append("// Each level holds $variantsPerLevel variants; replays rotate through them.\n")
    kt.append("object CrossMathLevels {\n")
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
    sw.append("enum CrossMathLevels {\n")
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
