package com.jigar.me.utils

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.DisplayMetrics
import androidx.core.content.ContextCompat
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusContent

object ViewUtils {
    fun convertDpToPixel(dp: Float, context: Context): Int {
        return (dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }
    fun calculateStringExpression(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0
            fun nextChar() {
                ch = if (++pos < str.length) str[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.code)) x += parseTerm() // addition
                    else if (eat('-'.code)) x -= parseTerm() // subtraction
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.code)) x *= parseFactor() // multiplication
                    else if (eat('/'.code)) x /= parseFactor() // division
                    else return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor() // unary plus
                if (eat('-'.code)) return -parseFactor() // unary minus
                var x: Double
                val startPos = pos
                if (eat('('.code)) { // parentheses
                    x = parseExpression()
                    eat(')'.code)
                } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) { // numbers
                    while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                    x = str.substring(startPos, pos).toDouble()
                } else if (ch >= 'a'.code && ch <= 'z'.code) { // functions
                    while (ch >= 'a'.code && ch <= 'z'.code) nextChar()
                    val func = str.substring(startPos, pos)
                    x = parseFactor()
                    x =
                        if (func == "sqrt") Math.sqrt(x) else if (func == "sin") Math.sin(
                            Math.toRadians(
                                x
                            )
                        ) else if (func == "cos") Math.cos(
                            Math.toRadians(x)
                        ) else if (func == "tan") Math.tan(Math.toRadians(x)) else throw RuntimeException(
                            "Unknown function: $func"
                        )
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }
                if (eat('^'.code)) x = Math.pow(x, parseFactor()) // exponentiation
                return x
            }
        }.parse()
    }

    fun getTable(context: Context, tableOf: Int, highlightedPosition: Int,themeContent : AbacusContent? = null,isDefaultColor : Int? = null,selectedColor : Int? = null): SpannableString {
        var table = ""
        var start = 0
        var end = 0
        for (i in 0..10) {
            var str = (i * tableOf).toString() + ""
            if (str.length == 1) {
                str = "0$str"
            }
            var currentRow = "$i x $tableOf = $str"
            if (i < 10) {
                currentRow += "\n"
            }
            table += currentRow
            if (i > 0 && highlightedPosition == i) { //no need to highlight 0 position. as in sum 0 is always getting skipped. Also 0 means no highlight.
                start = table.indexOf(currentRow)
                end = start + currentRow.length
            }
        }
        val spannableString = SpannableString(table)
        val color = if (themeContent != null){
            if (themeContent.equals(AppConstants.Settings.theam_Poligon_Silver) || themeContent.equals(AppConstants.Settings.theam_Poligon_Brown)){
                ContextCompat.getColor(context,R.color.black)
            }else{
                CommonUtils.mixTwoColors(ContextCompat.getColor(context,themeContent.dividerColor1), ContextCompat.getColor(context,themeContent.resetBtnColor8), 0.20f)
            }
        }else{
            if (selectedColor != null){
                ContextCompat.getColor(context, selectedColor)
            }else{
                ContextCompat.getColor(context, R.color.purple_A700)
            }

        }
        spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, isDefaultColor ?: R.color.gray_dark99)),0,table.length,Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        if (end > 0) {
            spannableString.setSpan(ForegroundColorSpan(color),start,end,Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(StyleSpan(android.graphics.Typeface.BOLD),start,end,Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        }
        return spannableString
    }

}