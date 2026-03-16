package com.jigar.me.data.model.dbtable.abacus_all_data

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jigar.me.ui.view.jetpack.abacus_base.utils.MathUtils
import com.jigar.me.utils.AppConstants
import kotlin.math.pow

@Entity(tableName = AppConstants.DBParam.table_abacus)
data class Abacus(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val set_id: String,
    val question: String,
    val hint: String? = null,
    val created_at: String? = null,
    var userAnswer: String? = null
){
    // find abacus type from current abacus question
    fun findCurrentAbacusType() : String {
        return when {
            question.contains("+") || question.contains("-") -> AppConstants.extras_Comman.AbacusTypeAdditionSubtraction
            question.contains("*", true) -> AppConstants.extras_Comman.AbacusTypeMultiplication
            question.contains("/", true) -> AppConstants.extras_Comman.AbacusTypeDivision
            else -> AppConstants.extras_Comman.AbacusTypeNumber
        }
    }

    // user for addition and subtraction
    val operationStepsStringsArray: List<String>
        get() = MathUtils.extractNumbersAndSigns(question)

    val operationNumbersArray: List<Int>
        get() = MathUtils.evaluateAtEachStep(question)


    val finalAnswer: Int get(){
        return MathUtils.calculateStringExpression(question).toInt()
    }

    val eachStepProduct: List<Int>
        get() = MathUtils.calculateEachStepProduct(exponentNum1, exponentNum2)


    // user for multiplication
    val num1: List<Int>
        get() = MathUtils.convertStringToArrayOfArrays(question).getOrNull(0).orEmpty()

    val num2: List<Int>
        get() = if (
            question.contains("×") ||
            question.contains("*") ||
            question.contains("x", ignoreCase = true)
        ) {
            MathUtils.convertStringToArrayOfArrays(question).getOrNull(1).orEmpty()
        } else {
            MathUtils.convertStringToArrayOfArrays(question).getOrNull(0).orEmpty()
        }

    val exponentNum1: List<Int>
        get() = raiseToExponents(num1)

    val exponentNum2: List<Int>
        get() = raiseToExponents(num2)

    fun raiseToExponents(input: List<Int>): List<Int> {
        val size = input.size
        return input.mapIndexed { index, value ->
            value * 10.0.pow((size - 1 - index).toDouble()).toInt()
        }
    }

    // for division
    val dividend: Int
        get() = if (question.contains("/") || question.contains("÷")) {
            MathUtils.convertStringToArrayOfArrays(question)[0]
                .joinToString("") { it.toString() }
                .toIntOrNull() ?: 0
        } else {
            0
        }

    val dividendArray: List<Int>
        get() = MathUtils.convertStringToArrayOfArrays(question)[0]

    val divisor: Int
        get() = MathUtils.convertStringToArrayOfArrays(question)[1]
            .joinToString("") { it.toString() }
            .toIntOrNull() ?: 0

    val displayDividendArray: List<Int>
        get() = convertToDisplayDividendArray(dividendArray, divisor)

    val quotient: Int
        get() = if (divisor != 0) dividend / divisor else 0

    val remainder: Int
        get() = if (divisor != 0) dividend % divisor else 0

    val eachStepQuotient: List<Int>
        get() {
            val tempArray = raiseToExponents(
                MathUtils.convertStringToArrayOfArrays(quotient.toString())[0]
            ).toMutableList()

            for (i in 1 until tempArray.size) {
                tempArray[i] += tempArray[i - 1]
            }
            return tempArray
        }

    val eachStepQuotientDigitsForMultiplicationTable: List<Int>
        get() = MathUtils.convertStringToArrayOfArrays(quotient.toString())[0]

    val eachStepRemainder: List<Int>
        get() {
            val tempArray = mutableListOf<Int>()
            eachStepQuotient.forEach {
                tempArray.add(dividend - it * divisor)
            }
            return tempArray
        }

    fun removeAdjacentDuplicates(input: List<Int>): List<Int> {
        if (input.isEmpty()) return emptyList()

        val result = mutableListOf(input[0])
        for (i in 1 until input.size) {
            if (input[i] != input[i - 1]) {
                result.add(input[i])
            }
        }
        return result
    }

    fun convertToDisplayDividendArray(originalDividendArray: List<Int>, divisor: Int): List<Int> {

        val newArray = mutableListOf<Int>()
        var index = 0

        while (index < originalDividendArray.size) {

            if (originalDividendArray[index] < divisor && originalDividendArray[index] != 0) {

                var tempSum = 0
                while (tempSum < divisor) {
                    tempSum = tempSum * 10 + originalDividendArray[index]
                    index++

                    if (index == originalDividendArray.size) {
                        break
                    }
                }
                newArray.add(tempSum)

            } else if (originalDividendArray[index] == 0) {
                newArray.add(originalDividendArray[index])
                index++
            } else {
                newArray.add(originalDividendArray[index])
                index++
            }
        }

        return newArray
    }

}

