package com.jigar.me.ui.view.jetpack.fragments.game_zone.target_number.viewmodel

import com.jigar.me.ui.view.jetpack.fragments.common.enums.CommonDifficulty4
import com.jigar.me.ui.view.jetpack.fragments.game_zone.target_number.components.TargetOperation
import com.jigar.me.ui.view.jetpack.fragments.game_zone.target_number.components.TargetPuzzle
import com.jigar.me.ui.view.jetpack.fragments.game_zone.target_number.components.TargetSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

class TargetRepository @Inject constructor() {

    suspend fun generatePuzzle(level: Int, difficulty: CommonDifficulty4): TargetPuzzle =
        withContext(Dispatchers.Default) {
            var puzzle: TargetPuzzle? = null
            while (puzzle == null) {
                val settings = settingsFor(level, difficulty)

                val nums = List(settings.count) {
                    Random.nextInt(settings.range.first, settings.range.last + 1)
                }
//                val nums = listOf(14,9,3,2,1)
                // 9 + 3, 14 + 2
                val solution = TargetSolver.findSolution(nums, settings.ops)

                if (solution != null) {
                    // Return from the entire generate() function
                    puzzle = TargetPuzzle(
                        target = solution.target,
                        numbers = nums.shuffled(),
                        allowedOps = settings.ops,
                        steps = solution.steps
                    )
                }
            }

            puzzle
        }

    private fun settingsFor(level: Int, diff: CommonDifficulty4): TargetSettings {
        return when (level) {
            1 -> TargetSettings(
                ops = listOf(TargetOperation.ADD, TargetOperation.SUBTRACT),
                count = when (diff) {
                    CommonDifficulty4.easy -> 3
                    CommonDifficulty4.medium -> 3
                    CommonDifficulty4.hard -> 4
                    CommonDifficulty4.veryHard -> 4
                },
                range = when (diff) {
                    CommonDifficulty4.easy -> 1..9
                    CommonDifficulty4.medium -> 1..20
                    CommonDifficulty4.hard -> 2..30
                    CommonDifficulty4.veryHard -> 2..40
                }
            )

            2 -> TargetSettings(
                ops = listOf(TargetOperation.ADD, TargetOperation.SUBTRACT, TargetOperation.MULTIPLY),
                count = when (diff) {
                    CommonDifficulty4.easy -> 3
                    CommonDifficulty4.medium -> 4
                    CommonDifficulty4.hard -> 4
                    CommonDifficulty4.veryHard -> 5
                },
                range = when (diff) {
                    CommonDifficulty4.easy -> 1..9
                    CommonDifficulty4.medium -> 1..12
                    CommonDifficulty4.hard -> 1..12
                    CommonDifficulty4.veryHard -> 1..15
                }
            )

            else -> {
                val ops = when (diff) {
                    CommonDifficulty4.easy -> listOf(TargetOperation.ADD, TargetOperation.SUBTRACT)
                    CommonDifficulty4.hard, CommonDifficulty4.veryHard ->
                        listOf(TargetOperation.ADD, TargetOperation.SUBTRACT, TargetOperation.MULTIPLY, TargetOperation.DIVIDE)
                    else -> listOf(TargetOperation.ADD, TargetOperation.SUBTRACT, TargetOperation.MULTIPLY)
                }

                TargetSettings(
                    ops = ops,
                    count = when (diff) {
                        CommonDifficulty4.easy -> 4
                        CommonDifficulty4.medium -> 5
                        CommonDifficulty4.hard -> 4
                        CommonDifficulty4.veryHard -> 5
                    },
                    range = when (diff) {
                        CommonDifficulty4.easy -> 1..50
                        CommonDifficulty4.medium -> 1..15
                        CommonDifficulty4.hard -> 1..15
                        CommonDifficulty4.veryHard -> 1..20
                    }
                )
            }
        }
    }
}

object TargetSolver {
    data class Node(val numbers: List<Int>, val steps: List<String>)
    data class Solution(val target: Int, val steps: List<String>)

    fun findSolution(nums: List<Int>, ops: List<TargetOperation>): Solution? {
        val nodes = dfs(nums, ops)
        val valid = nodes.filter { it.numbers.size == 1 && it.numbers[0] > 0 }
        if (valid.isEmpty()) return null
        val chosen = valid.random()
        return Solution(chosen.numbers[0], chosen.steps)
//        return Solution(5, listOf("(3 − 2) = 1","(9 − 1) = 8","(1 + 8) = 9","(14 − 9) = 5"))
    }

    private fun dfs(nums: List<Int>, ops: List<TargetOperation>): List<Node> {
        if (nums.size == 1) return listOf(Node(nums, emptyList()))
        val results = mutableListOf<Node>()
        for (i in nums.indices) for (j in i + 1 until nums.size) {
            val a = nums[i]; val b = nums[j]
            val rest = nums.toMutableList().also {
                // remove j then i (index j > i)
                it.removeAt(j); it.removeAt(i)
            }
            for (op in ops) {
                val res = applyOp(op, a, b) ?: continue
                val newNums = rest + res
                for (child in dfs(newNums, ops)) {
                    val step = "($a ${op.symbol} $b) = $res"
                    val updated = listOf(step) + child.steps
                    results.add(Node(child.numbers, updated))
                }
            }
        }
        return results
    }

    private fun applyOp(op: TargetOperation, a: Int, b: Int): Int? {
        return when (op) {
            TargetOperation.ADD -> a + b
            TargetOperation.SUBTRACT -> {
                val r = a - b
                if (r > 0) r else null
            }
            TargetOperation.MULTIPLY -> a * b
            TargetOperation.DIVIDE -> if (b != 0 && a % b == 0 && a / b > 0) a / b else null
        }
    }
}