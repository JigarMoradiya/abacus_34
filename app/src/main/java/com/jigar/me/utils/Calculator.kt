package com.jigar.me.utils

import com.fathzer.soft.javaluator.DoubleEvaluator
import com.fathzer.soft.javaluator.Function
import com.fathzer.soft.javaluator.Parameters
import java.util.*
import kotlin.math.sqrt


class Calculator {
    // The function has one argument and its name is "sqrt"
    val sqrt: Function = Function("sqrt", 1)
    val factorial: Function = Function("!", 1)
    val cuberoot: Function = Function("crt", 1)
    val combination: Function = Function("comb", 2)
    val permutation: Function = Function("permu", 2)
    var params: Parameters? = null
    var evaluator: DoubleEvaluator
    private val previousSum = 0.0
    private var currentSum = 0.0

    //Used to show display to user
    val currentDisplay = ""

    //private String expressionUsedForParsing ="";
    private val isRadians = false
    private fun getFactorial(n: Int): Int {
        if (n == 0 || n == 1) return 1
        val result: Int = getFactorial(n - 1) * n
        return result
    }

    fun addFunctions() {
        params = DoubleEvaluator.getDefaultParameters()
        params!!.add(sqrt)
        params!!.add(factorial)
        params!!.add(cuberoot)
        params!!.add(combination)
        params!!.add(permutation)
    }

    fun getResult(currentDisplay: String, expressionUsedForParsing: String): String {
        //Tries to parse the information as it is entered, if the parser can't handle it, the word error is shown on screen
        var currentDisplay = currentDisplay
        var expressionUsedForParsing = expressionUsedForParsing
        try {
//            println("Displayed Output $expressionUsedForParsing")
            expressionUsedForParsing = expressionUsedForParsing.replace("%", "/100")
            expressionUsedForParsing = expressionUsedForParsing.replace("x", "*")
//            println("Displayed Output ==$expressionUsedForParsing")
//            println("fixExpression " + fixExpression(expressionUsedForParsing))
            currentSum = evaluator.evaluate(fixExpression(expressionUsedForParsing))
//            println("currentSum $currentSum")
            currentSum = convertToRadians(currentSum)
//            println("convertToRadians $currentSum")
            currentDisplay = currentSum.toString()
            //previousSum = currentSum;
        } catch (e: Exception) {
            currentDisplay = ""
        }
        return currentDisplay
    }

    fun convertToRadians(sum: Double): Double {
        var newSum = sum
        if (isRadians) newSum = Math.toRadians(sum)
        return newSum
    }

    //Handles fixing the expression before parsing. Adding parens, making sure parens can multiply with each other,
    fun fixExpression(exp: String): String {
        var openParens = 0
        var closeParens = 0
        val openP = '('
        val closeP = ')'
        var expr = exp
        for (i in exp.indices) {
            if (exp[i] == openP) openParens++ else if (exp[i] == closeP) closeParens++
        }
        while (openParens > 0) {
            expr += closeP
            openParens--
        }
        while (closeParens > 0) {
            expr = openP.toString() + expr
            closeParens--
        }
        expr = multiplicationForParens(expr)
        return expr
    }

    //Used to fix multiplication between parentheses
    fun multiplicationForParens(s: String): String {
        var fixed = ""
        for (position in s.indices) {
            fixed += s[position]
            if (position == s.length - 1) continue
            if (s[position] == ')' && s[position + 1] == '(') fixed += '*'
            if (s[position] == '(' && s[position + 1] == ')') fixed += '1'
        }
        return fixed
    }

    init {
        addFunctions()
        //Adds the functions to the evaluator
        evaluator = object : DoubleEvaluator(params) {
            override fun evaluate(
                function: Function,
                arguments: Iterator<Double>,
                evaluationContext: Any?
            ): Double {
                return when (function) {
                    sqrt -> sqrt((arguments.next() as Double?)!!)
                    cuberoot -> {
                        Math.cbrt((arguments.next() as Double?)!!)
                    }
                    combination -> {
                        var numberInputs: Double
                        val saveValue = ArrayList<Double>()
                        while (arguments.hasNext()) {
                            numberInputs = arguments.next()
                            saveValue.add(numberInputs)
                        }
                        val firstArgument = saveValue[0]
                        val secondArgument = saveValue[1]
                        val denominator = getFactorial(firstArgument.toInt()).toDouble()
                        val nominator =
                            (getFactorial(secondArgument.toInt()) * getFactorial((firstArgument - secondArgument) as Int)).toDouble()
                        denominator / nominator
                    }
                    permutation -> {
                        var numberInputs: Double
                        val saveValue = ArrayList<Double>()
                        while (arguments.hasNext()) {
                            numberInputs = arguments.next()
                            saveValue.add(numberInputs)
                        }
                        val firstArgument = saveValue[0]
                        val secondArgument = saveValue[1]
                        val denominator = getFactorial(firstArgument.toInt()).toDouble()
                        val nominator =
                            getFactorial((firstArgument - secondArgument).toInt()).toDouble()
                        denominator / nominator
                    }
                    factorial -> {
                        var result = 1.0
                        val num = arguments.next()
                        var i = 2
                        while (i <= num) {
                            result *= i
                            i++
                        }
                        result
                    }
                    else -> super.evaluate(
                        function,
                        arguments,
                        evaluationContext
                    )
                }
            }
        }
    }
}