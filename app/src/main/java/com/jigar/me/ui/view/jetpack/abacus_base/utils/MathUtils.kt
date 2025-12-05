package com.jigar.me.ui.view.jetpack.abacus_base.utils

import com.jigar.me.data.local.data.Movement
import com.jigar.me.data.local.data.RodMovement

class MathUtils {
    fun calculateRodMovements(from: Int, to: Int, rods: Int, isForRightRods: Boolean = false): List<RodMovement> {

        val fromStr = from.toString().padStart(rods, '0')
        val toStr = to.toString().padStart(rods, '0')

        val fromDigits = fromStr.map { it.digitToInt() }
        val toDigits = toStr.map { it.digitToInt() }

        val result = mutableListOf<RodMovement>()

        (0 until rods).forEach { i ->
            val rodIndex = rods - 1 - i   // rightmost is least significant
            val realIndex = if (isForRightRods) 12 - i else 6 - i

            val f = fromDigits[rodIndex]
            val t = toDigits[rodIndex]

            val upperDown = t >= 5 && f < 5
            val upperUp = t < 5 && f >= 5

            val fLower = f % 5
            val tLower = t % 5

            val lowerUp = if (tLower > fLower) tLower - fLower else 0
            val lowerDown = if (tLower < fLower) fLower - tLower else 0

            val lowerOldValue = if (f >= 5) f % 5 else f

            result.add(
                RodMovement(
                    rodIndex = realIndex,
                    movement = Movement(
                        upperUp = upperUp,
                        upperDown = upperDown,
                        lowerUp = lowerUp,
                        lowerDown = lowerDown,
                        lowerOldValue = lowerOldValue
                    )
                )
            )
        }

        return result
    }
}