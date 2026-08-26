package com.jigar.me.ui.view.base.abacus_base

import android.util.Log
import androidx.compose.runtime.*

class AbacusCalculations(numberOfColumns: Int) {

    companion object {
        // The integer part of the abacus is always exactly the first 7
        // columns (ones/tens/hundreds/.../ten-lakhs) regardless of total
        // column count — named here since it was previously a bare `7`
        // repeated at many call sites, which made the 7-rod feature's bugs
        // easy to miss.
        const val INTEGER_RODS = 7
    }

    var numberOfColumns = numberOfColumns
        private set

    // How many columns sit right of the integer part (0 when there's no
    // decimal/remainder side at all, e.g. 7-rod mode outside Division).
    val rightRodCount: Int
        get() = (numberOfColumns - INTEGER_RODS).coerceAtLeast(0)

    // ⭐ Fires whenever any bead changes
    var stateVersion by mutableIntStateOf(0)
        private set

    // ⭐ MAIN STATE (reactive like SwiftUI @State)
    var abacusState by mutableStateOf(
        MutableList(numberOfColumns) {
            mutableListOf(true, false, false, true, true, true, true)
        }
    )
        private set

    var displayValue by mutableStateOf("0")
        private set

    var isDivisionQuestion by mutableStateOf(false)
        private set

    var totalValuePair by mutableStateOf("0" to "0")
        private set

    // -------------------------------------------------------
    // RESET
    // -------------------------------------------------------
    fun resetAbacusData() {
        abacusState = MutableList(numberOfColumns) {
            mutableListOf(true, false, false, true, true, true, true)
        }

        displayValue = "0"
        isDivisionQuestion = false
        totalValuePair = "0" to "0"

        stateVersion++   // ⭐ trigger recomposition
    }

    // -------------------------------------------------------
    // RESIZE (used to switch between 7-rod / 13-rod abacus)
    // -------------------------------------------------------
    fun resizeColumns(newColumns: Int) {
        if (newColumns == numberOfColumns) return
        numberOfColumns = newColumns
        resetAbacusData()
    }

    // -------------------------------------------------------
    // SET FROM VALUE
    // -------------------------------------------------------
    fun setAbacusValueFromString(value: String, isQuestionForDivision: Boolean = false) {

        isDivisionQuestion = isQuestionForDivision
        val padded = value.padStart(abacusState.size, '0')
        val newState = MutableList(abacusState.size) {
            mutableListOf(true, false, false, true, true, true, true)
        }

        for (i in 0 until abacusState.size) {
            val digitChar = padded.getOrNull(i) ?: '0'
            val digit = digitChar - '0'
            newState[i] = digitToColumn(digit)
        }

        abacusState = newState
        recalcTotal()
        stateVersion++
    }


    private fun digitToColumn(d: Int): MutableList<Boolean> = when (d) {
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

        if (index == 0) return col[0]
        if (index == 1) return false

        val fi = lowerFalseIndex(columnIndex) ?: return false
        return index < fi
    }

    // -------------------------------------------------------
    // CAN MOVE UP (Swift identical)
    // -------------------------------------------------------
    fun canMoveUp(index: Int, columnIndex: Int): Boolean {
        val col = abacusState[columnIndex]

        if (index == 0) return false
        if (index == 1) return col[1]

        val fi = lowerFalseIndex(columnIndex) ?: return false
        return index > fi
    }

    // -------------------------------------------------------
    // MOVE DOWN
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

        abacusState = abacusState.toMutableList().also { it[columnIndex] = col }

        recalcTotal()
        stateVersion++   // ⭐ ALWAYS bump version
    }

    // -------------------------------------------------------
    // MOVE UP
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

        abacusState = abacusState.toMutableList().also { it[columnIndex] = col }

        recalcTotal()
        stateVersion++   // ⭐ ALWAYS bump version
    }

    // -------------------------------------------------------
    // TOTAL VALUE (iOS matched)
    // -------------------------------------------------------
    private fun recalcTotal() {
        val raw = calculateAbacusString()
        if (numberOfColumns > INTEGER_RODS){
            val padded = raw.padStart(numberOfColumns, '0')

            val firstPart = padded.substring(0, INTEGER_RODS)
            val secondPart = padded.substring(INTEGER_RODS, numberOfColumns)

            totalValuePair = firstPart to secondPart

            val intPartTrimmed = firstPart.trimStart('0').ifEmpty { "0" }
            val fractionalTrimmed = secondPart.trimEnd('0')

            displayValue = if (fractionalTrimmed.isNotEmpty())
                if (isDivisionQuestion){
                    "$intPartTrimmed < ${secondPart.trimStart('0')}"
                }else{
                    "$intPartTrimmed.$fractionalTrimmed"
                }
            else intPartTrimmed
        }else{
            raw.trimStart('0').ifEmpty { "0" }
            val intPartTrimmed = raw.trimStart('0').ifEmpty { "0" }
            displayValue = intPartTrimmed
            totalValuePair = intPartTrimmed to "0"
        }
    }

    // -------------------------------------------------------
    // BUILD STRING LIKE iOS
    // -------------------------------------------------------
    fun calculateAbacusString(): String {
        val builder = StringBuilder()

        for (column in abacusState) {
            var digit = 0

            if (column[1]) digit += 5

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
