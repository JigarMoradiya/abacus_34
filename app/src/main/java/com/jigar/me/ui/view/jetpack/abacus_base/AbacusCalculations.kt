package com.jigar.me.ui.view.jetpack.abacus_base

import androidx.compose.runtime.*

class AbacusCalculations(numberOfColumns: Int) {

    // ⭐ MAIN STATE (reactive like SwiftUI @State)
    var abacusState by mutableStateOf(
        MutableList(numberOfColumns) {
            mutableListOf(true, false, false, true, true, true, true)
        }
    )
        private set

    var displayValue by mutableStateOf("0")
        private set
    var totalValuePair by mutableStateOf("0" to "0")
        private set

    // -------------------------------------------------------
    // RESET
    // -------------------------------------------------------
    fun resetAbacusData() {
        abacusState = MutableList(abacusState.size) {
            mutableListOf(true, false, false, true, true, true, true)
        }
        displayValue = "0"
    }

    // -------------------------------------------------------
    // SET FROM VALUE (iOS equivalent)
    // -------------------------------------------------------
    fun setAbacusValue(totalValueInput: Int) {
        displayValue = totalValueInput.toString()
        var temp = totalValueInput
        var idx = abacusState.size - 1

        val newState = MutableList(abacusState.size) {
            mutableListOf(true, false, false, true, true, true, true)
        }

        while (temp != 0 && idx >= 0) {
            val digit = temp % 10
            newState[idx] = digitToColumn(digit)
            temp /= 10
            idx--
        }

        abacusState = newState
    }

    private fun digitToColumn(d: Int): MutableList<Boolean> {
        return when (d) {
            0 -> mutableListOf(true, false, false, true, true, true, true)
            1 -> mutableListOf(true, false, true, false, true, true, true)
            2 -> mutableListOf(true, false, true, true, false, true, true)
            3 -> mutableListOf(true, false, true, true, true, false, true)
            4 -> mutableListOf(true, false, true, true, true, true, false)
            5 -> mutableListOf(false, true, false, true, true, true, true)
            6 -> mutableListOf(false, true, true, false, true, true, true)
            7 -> mutableListOf(false, true, true, true, false, true, true)
            8 -> mutableListOf(false, true, true, true, true, false, true)
            9 -> mutableListOf(false, true, true, true, true, true, false)
            else -> mutableListOf(true, false, false, true, true, true, true)
        }
    }

    // -------------------------------------------------------
    // FIND LOWER FALSE INDEX (2..6)
    // -------------------------------------------------------
    private fun lowerFalseIndex(columnIndex: Int): Int? {
        val col = abacusState[columnIndex]
        for (i in 2..6) {
            if (!col[i]) return i
        }
        return null
    }

    // -------------------------------------------------------
    // CAN MOVE DOWN (Swift identical)
    // -------------------------------------------------------
    fun canMoveDown(index: Int, columnIndex: Int): Boolean {
        val col = abacusState[columnIndex]

        if (index == 0) return col[0]     // only if 0 is true
        if (index == 1) return false      // 1 never moves down

        val fi = lowerFalseIndex(columnIndex) ?: return false
        return index < fi
    }

    // -------------------------------------------------------
    // CAN MOVE UP (Swift identical)
    // -------------------------------------------------------
    fun canMoveUp(index: Int, columnIndex: Int): Boolean {
        val col = abacusState[columnIndex]

        if (index == 0) return false
        if (index == 1) return col[1]     // bead 1 moves up only if true

        val fi = lowerFalseIndex(columnIndex) ?: return false
        return index > fi
    }

    // -------------------------------------------------------
    // MOVE DOWN (Swift identical)
    // -------------------------------------------------------
    fun moveBeadDown(index: Int, columnIndex: Int) {
        val col = abacusState[columnIndex].toMutableList()

        if (index == 0) {
            if (col[0]) {
                col[0] = false
                col[1] = true
            }
        } else if (index in 2..6) {
            val fi = lowerFalseIndex(columnIndex) ?: return
            if (index < fi) {
                for (i in index + 1..fi) col[i] = true
                col[index] = false
            }
        }

        // ⭐ Replace whole column to trigger recomposition
        abacusState = abacusState.toMutableList().also { it[columnIndex] = col }
        recalcTotal()
    }

    // -------------------------------------------------------
    // MOVE UP (Swift identical)
    // -------------------------------------------------------
    fun moveBeadUp(index: Int, columnIndex: Int) {
        val col = abacusState[columnIndex].toMutableList()

        if (index == 1) {
            if (col[1]) {
                col[1] = false
                col[0] = true
            }
        } else if (index in 2..6) {
            val fi = lowerFalseIndex(columnIndex) ?: return
            if (index > fi) {
                for (i in fi until index) col[i] = true
                col[index] = false
            }
        }

        // ⭐ Replace whole column
        abacusState = abacusState.toMutableList().also { it[columnIndex] = col }
        recalcTotal()
    }

    // -------------------------------------------------------
    // TOTAL VALUE (iOS matched)
    // -------------------------------------------------------
    private fun recalcTotal() {
        displayValue = "0"
        val raw = calculateAbacusString()
        val padded = raw.padStart(13, '0')             // safety
        val firstPart = padded.substring(0, 7)         // first 7 chars
        val secondPart = padded.substring(7, 13)       // last 6 chars

        totalValuePair = firstPart to secondPart

        // ---- Format for display, same as iOS ----
        val intPartTrimmed = firstPart.trimStart('0').ifEmpty { "0" }
        val fractionalTrimmed = secondPart.trimEnd('0')  // remove trailing zeros

        displayValue =
            if (fractionalTrimmed.isNotEmpty())
                "$intPartTrimmed.$fractionalTrimmed"
            else
                intPartTrimmed
    }

    // -------------------------------------------------------
    // BUILD STRING LIKE iOS calculateAbacusString()
    // -------------------------------------------------------
    fun calculateAbacusString(): String {
        val builder = StringBuilder()

        for (column in abacusState) {
            var digit = 0

            // 1️⃣ Heaven bead – same as Swift:
            // if column[1] == true → heaven is down → add 5
            if (column[1]) {
                digit += 5
            }

            // 2️⃣ Lower beads – COUNT TRUE beads from index 2 upwards,
            // stopping when first false is found (exactly like the while loop in Swift)
            var lower = 0
            var i = 2
            while (i <= 6 && column[i]) {
                lower++
                i++
            }

            digit += lower
            builder.append(digit)
        }

        return builder.toString()
    }

}