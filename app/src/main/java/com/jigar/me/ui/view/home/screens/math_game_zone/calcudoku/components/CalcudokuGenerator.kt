package com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku.components

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object CalcudokuGenerator {

    fun make(config: CalcudokuConfig): CalcudokuPuzzle {
        val n = config.size
        val sol = latinSquare(n)
        val parts = partition(n)

        val cages = ArrayList<Cage>()
        val cageId = IntArray(n * n)
        parts.forEachIndexed { i, cells ->
            cages.add(makeCage(cells, sol, config.allowedOps))
            for (cell in cells) cageId[cell] = i
        }
        return CalcudokuPuzzle(n, sol, cages, cageId.toList())
    }

    // Latin square: cyclic base with shuffled rows, columns and symbols.
    private fun latinSquare(n: Int): List<Int> {
        val rows = ArrayList<IntArray>()
        for (r in 0 until n) {
            val row = IntArray(n) { c -> (r + c) % n + 1 }
            rows.add(row)
        }
        rows.shuffle()

        val colOrder = (0 until n).toMutableList().also { it.shuffle() }

        val symMap = IntArray(n + 1)
        val syms = (1..n).toMutableList().also { it.shuffle() }
        for (v in 1..n) symMap[v] = syms[v - 1]

        val flat = ArrayList<Int>()
        for (row in rows) {
            for (c in colOrder) flat.add(symMap[row[c]])
        }
        return flat
    }

    // Grow orthogonally-connected cages, mostly size 2.
    private fun partition(n: Int): List<List<Int>> {
        val total = n * n
        val cageOf = IntArray(total) { -1 }
        val cages = ArrayList<MutableList<Int>>()

        val order = (0 until total).toMutableList().also { it.shuffle() }
        for (start in order) {
            if (cageOf[start] != -1) continue
            val roll = (0 until 10).random()
            val targetSize = if (roll < 2) 1 else if (roll < 8) 2 else 3

            val cage = mutableListOf(start)
            cageOf[start] = cages.size
            while (cage.size < targetSize) {
                val candidates = ArrayList<Int>()
                for (cell in cage) {
                    val r = cell / n; val c = cell % n
                    for (nb in listOf(intArrayOf(r - 1, c), intArrayOf(r + 1, c), intArrayOf(r, c - 1), intArrayOf(r, c + 1))) {
                        val nr = nb[0]; val nc = nb[1]
                        if (nr in 0 until n && nc in 0 until n) {
                            val idx = nr * n + nc
                            if (cageOf[idx] == -1) candidates.add(idx)
                        }
                    }
                }
                if (candidates.isEmpty()) break
                val pick = candidates.random()
                cageOf[pick] = cages.size
                cage.add(pick)
            }
            cages.add(cage)
        }
        return cages
    }

    private fun makeCage(cells: List<Int>, solution: List<Int>, allowed: List<CageOp>): Cage {
        val values = cells.map { solution[it] }
        if (cells.size == 1) return Cage(cells, CageOp.GIVEN, values[0])

        val candidates = ArrayList<CageOp>()
        for (op in allowed) {
            when (op) {
                CageOp.PLUS -> candidates.add(CageOp.PLUS)
                CageOp.TIMES -> candidates.add(CageOp.TIMES)
                CageOp.MINUS -> if (cells.size == 2) candidates.add(CageOp.MINUS)
                CageOp.DIVIDE -> if (cells.size == 2) {
                    val hi = max(values[0], values[1]); val lo = min(values[0], values[1])
                    if (lo != 0 && hi % lo == 0) candidates.add(CageOp.DIVIDE)
                }
                CageOp.GIVEN -> {}
            }
        }
        if (candidates.isEmpty()) candidates.add(CageOp.PLUS)

        val op = candidates.random()
        val target = when (op) {
            CageOp.PLUS -> values.sum()
            CageOp.TIMES -> values.reduce { a, b -> a * b }
            CageOp.MINUS -> abs(values[0] - values[1])
            CageOp.DIVIDE -> max(values[0], values[1]) / min(values[0], values[1])
            CageOp.GIVEN -> values[0]
        }
        return Cage(cells, op, target)
    }
}
