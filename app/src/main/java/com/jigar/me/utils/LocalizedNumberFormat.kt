package com.jigar.me.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Numbers for display. Owner decision: the digits are ALWAYS 0-9 (also in Arabic and Thai), so sums, the abacus and the answers look the
 * same in every language. Only the separators follow the language: a comma as the decimal mark in French, Spanish, Portuguese, Russian
 * and Turkish, a point in English, Japanese and Thai.
 *
 * This is for showing numbers. Keypad input, parsing and what is sent to the server stay in the plain "12.5" form.
 */
object LocalizedNumberFormat {

    /** The same language with Western digits forced (`-u-nu-latn`). */
    fun latinDigits(locale: Locale): Locale = Locale.Builder().setLocale(locale).setUnicodeLocaleKeyword("nu", "latn").build()

    fun decimalSeparator(locale: Locale = Locale.getDefault()): Char = DecimalFormatSymbols.getInstance(latinDigits(locale)).decimalSeparator

    /** Takes plain text with a point ("12.5", "0.05") and returns it with the language's decimal mark ("12,5"). Digits stay 0-9. */
    fun display(plain: String, locale: Locale = Locale.getDefault()): String {
        val mark = decimalSeparator(locale)
        return if (mark == '.') plain else plain.replace('.', mark)
    }

    /** A number with up to [maxFractionDigits] decimals, no thousands grouping, digits 0-9 and the language's decimal mark. */
    fun format(value: Double, maxFractionDigits: Int = 6, locale: Locale = Locale.getDefault()): String {
        val format = DecimalFormat("0", DecimalFormatSymbols.getInstance(latinDigits(locale)))
        format.maximumFractionDigits = maxFractionDigits
        format.isGroupingUsed = false
        return format.format(value)
    }
}
