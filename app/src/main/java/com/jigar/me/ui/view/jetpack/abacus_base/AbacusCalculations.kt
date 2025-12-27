package com.jigar.me.ui.view.jetpack.abacus_base

import androidx.compose.runtime.*

class AbacusCalculations(private val numberOfColumns: Int) {

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
        abacusState = MutableList(abacusState.size) {
            mutableListOf(true, false, false, true, true, true, true)
        }

        displayValue = "0"
        totalValuePair = "0" to "0"

        stateVersion++   // ⭐ trigger recomposition
    }

    // -------------------------------------------------------
    // SET FROM VALUE
    // -------------------------------------------------------
    fun setAbacusValueFromString(value: String,isQuestionForDivision : Boolean = false) {
        isDivisionQuestion = isQuestionForDivision
        val padded = value.padStart(abacusState.size, '0')  // 🔥 ensure full length

        val newState = MutableList(abacusState.size) {
            mutableListOf(true, false, false, true, true, true, true)
        }

        for (i in padded.indices) {
            val digitChar = padded[i]
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
        if (numberOfColumns == 13){
            val padded = raw.padStart(13, '0')

            val firstPart = padded.substring(0, 7)
            val secondPart = padded.substring(7, 13)

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
            displayValue = raw
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
