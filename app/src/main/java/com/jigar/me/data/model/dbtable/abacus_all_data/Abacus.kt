package com.jigar.me.data.model.dbtable.abacus_all_data

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


    val finalAnswer: Int
        get() {
            return when {
                question.contains("+") || question.contains("-") -> {
                    MathUtils.evaluateExpression(question)
                }

                question.contains("×") || question.contains("*") || question.contains("x", ignoreCase = true) -> {
                    eachStepProduct.lastOrNull() ?: 0
                }

                question.contains("/") || question.contains("÷") -> {
                    0
                }

                else -> { // number
                    MathUtils.evaluateExpression(question)
                }
            }
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

}

