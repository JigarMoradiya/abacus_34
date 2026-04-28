package com.jigar.me.ui.view.home.screens.math_game_zone.math_pyramid.play.components

import com.jigar.me.ui.view.home.common.enums.CommonDifficulty4
import kotlin.random.Random

object NumberPyramidGenerator {

    private data class Coord(val r: Int, val c: Int)

    data class Result(val puzzle: List<List<Int?>>, val full: List<List<Int>>)

    fun generate(
        levels: Int = 4,
        difficulty: CommonDifficulty4 = CommonDifficulty4.easy,
        numberRange: IntRange = 1..20
    ): Result {
        // Build bottom row
        val bottom = List(levels) { Random.Default.nextInt(numberRange.first, numberRange.last + 1) }
        val fullMutable: MutableList<List<Int>> = mutableListOf(bottom)

        var current = bottom
        while (current.size > 1) {
            val upper = (0 until current.size - 1).map { i -> current[i] + current[i + 1] }
            fullMutable.add(0, upper)
            current = upper
        }

        val full = fullMutable.toList()
        val puzzle: MutableList<MutableList<Int?>> = full.map { it.map { v -> v as Int? }.toMutableList() }.toMutableList()
        val totalBoxes = (1..levels).sum()

        val emptyCount = when (levels) {
            2 -> when (difficulty) {
                CommonDifficulty4.easy -> 1
                else -> 1
            }
            3 -> when (difficulty) {
                CommonDifficulty4.easy -> 2
                CommonDifficulty4.medium -> 3
                CommonDifficulty4.hard, CommonDifficulty4.veryHard -> 4
            }
            4 -> when (difficulty) {
                CommonDifficulty4.easy -> maxOf(1, totalBoxes / 2 - 1)
                CommonDifficulty4.medium -> maxOf(1, totalBoxes / 2)
                CommonDifficulty4.hard -> maxOf(1, totalBoxes / 2 + 1)
                CommonDifficulty4.veryHard -> maxOf(1, totalBoxes - (2..3).random())
            }
            5 -> when (difficulty) {
                CommonDifficulty4.easy -> maxOf(1, totalBoxes / 2 + 1)
                CommonDifficulty4.medium -> maxOf(1, totalBoxes / 2 + 2)
                CommonDifficulty4.hard -> maxOf(1, totalBoxes / 2 + 3)
                CommonDifficulty4.veryHard -> maxOf(1, totalBoxes - (3..4).random())
            }
            else -> when (difficulty) {
                CommonDifficulty4.easy -> maxOf(1, totalBoxes / 2 + 1)
                CommonDifficulty4.medium -> maxOf(1, totalBoxes / 2 + 2)
                CommonDifficulty4.hard -> maxOf(1, totalBoxes / 2 + 3)
                CommonDifficulty4.veryHard -> maxOf(1, totalBoxes - (4..5).random())
            }
        }

        val allCoords = mutableListOf<Coord>()
        for (r in 0 until levels) {
            for (c in 0 until full[r].size) {
                allCoords.add(Coord(r, c))
            }
        }

        val shuffled = allCoords.shuffled().toMutableList()
        val empties = mutableSetOf<Coord>()
        while (empties.size < minOf(emptyCount, allCoords.size) && shuffled.isNotEmpty()) {
            empties.add(shuffled.removeAt(shuffled.lastIndex))
        }

        empties.forEach { coord ->
            puzzle[coord.r][coord.c] = null
        }

        // safety: ensure not all are empty
        val visibleCount = totalBoxes - empties.size
        if (visibleCount == 0 && allCoords.isNotEmpty()) {
            val pick = allCoords.random()
            puzzle[pick.r][pick.c] = full[pick.r][pick.c]
        }

        return Result(puzzle.map { it.toList() }, full)
    }
}