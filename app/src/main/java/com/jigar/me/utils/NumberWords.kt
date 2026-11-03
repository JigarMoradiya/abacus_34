package com.jigar.me.utils

/**
 * A whole number written in words, for the languages whose spelling is known here ([languages]).
 *
 * English is not in this file: it keeps the app's own Indian-style words (lakh, crore) from the resource strings, see
 * `Context.convertNumberToWords`. Arabic is added with the right-to-left pass.
 *
 * Why our own and not Android's `RuleBasedNumberFormat`: that class is not part of the public SDK (it is a hidden class that happens to be
 * on the phone), it differs between phones and Android versions, and it cannot run in unit tests. The words here are checked against
 * the spell-out data of a real phone (`NumberWordsTest` + `number_words_golden.tsv`); the few deliberate differences are listed in the test.
 *
 * Pure Kotlin on purpose (no Android classes), so it is unit tested on the computer.
 */
object NumberWords {

    val languages = setOf("fr", "es", "pt", "tr", "ru", "ja", "th")

    fun isSupported(tag: String) = tag in languages

    /** [n] in words in language [lang] ("fr", "es", ...), or null when that language is not supported here. */
    fun spell(n: Long, lang: String): String? {
        if (lang !in languages) return null
        if (n == 0L) return ZERO.getValue(lang)
        val body = when (lang) {
            "fr" -> fr(abs(n))
            "es" -> es(abs(n))
            "pt" -> pt(abs(n))
            "tr" -> tr(abs(n))
            "ru" -> ru(abs(n))
            "ja" -> ja(abs(n))
            else -> th(abs(n))
        }
        if (n > 0) return body
        return MINUS.getValue(lang) + (if (lang == "ja" || lang == "th") "" else " ") + body
    }

    private fun abs(n: Long) = if (n < 0) -n else n

    private val ZERO = mapOf("fr" to "zéro", "es" to "cero", "pt" to "zero", "tr" to "sıfır", "ru" to "ноль", "ja" to "〇", "th" to "ศูนย์")
    private val MINUS = mapOf("fr" to "moins", "es" to "menos", "pt" to "menos", "tr" to "eksi", "ru" to "минус", "ja" to "マイナス", "th" to "ลบ")

    private fun join(vararg parts: String) = parts.filter { it.isNotEmpty() }.joinToString(" ")

    // ---------------------------------------------------------------- French
    private val FR_ONES = arrayOf("zéro", "un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf", "dix", "onze", "douze", "treize", "quatorze", "quinze", "seize")
    private val FR_TENS = arrayOf("", "", "vingt", "trente", "quarante", "cinquante", "soixante")
    private val FR_SCALES = arrayOf("", "mille", "million", "milliard")

    private fun fr(n: Long): String {
        val g = groups(n)
        val parts = ArrayList<String>()
        for (i in g.indices.reversed()) {
            val v = g[i]
            if (v == 0) continue
            parts += when (i) {
                0 -> frBelow1000(v, plural = true)                       // "deux cents", "quatre-vingts": the s only at the very end
                1 -> if (v == 1) "mille" else frBelow1000(v, plural = false) + " mille"
                else -> frBelow1000(v, plural = false) + " " + FR_SCALES[i] + (if (v > 1) "s" else "")
            }
        }
        return parts.joinToString(" ")
    }

    private fun frBelow1000(v: Int, plural: Boolean): String {
        val h = v / 100
        val r = v % 100
        val hundreds = when {
            h == 0 -> ""
            h == 1 -> "cent"
            else -> FR_ONES[h] + " cent" + (if (r == 0 && plural) "s" else "")
        }
        return join(hundreds, if (r == 0) "" else frBelow100(r, plural))
    }

    private fun frTeen(u: Int) = if (u < 17) FR_ONES[u] else "dix-" + FR_ONES[u - 10]   // 10..19

    private fun frBelow100(r: Int, plural: Boolean): String = when {
        r < 17 -> FR_ONES[r]
        r < 20 -> "dix-" + FR_ONES[r - 10]
        r < 70 -> {
            val u = r % 10
            FR_TENS[r / 10] + when (u) { 0 -> ""; 1 -> "-et-un"; else -> "-" + FR_ONES[u] }
        }
        r < 80 -> {
            val u = r - 60                                                // 70 = soixante-dix, 71 = soixante-et-onze
            "soixante" + when (u) { 10 -> "-dix"; 11 -> "-et-onze"; else -> "-" + frTeen(u) }
        }
        else -> {
            val u = r - 80                                                // 80 = quatre-vingts, 91 = quatre-vingt-onze
            if (u == 0) "quatre-vingt" + (if (plural) "s" else "") else "quatre-vingt-" + frTeen(u)
        }
    }

    // ---------------------------------------------------------------- Spanish
    private val ES_ONES = arrayOf(
        "cero", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve", "diez", "once", "doce", "trece", "catorce", "quince",
        "dieciséis", "diecisiete", "dieciocho", "diecinueve", "veinte", "veintiuno", "veintidós", "veintitrés", "veinticuatro", "veinticinco",
        "veintiséis", "veintisiete", "veintiocho", "veintinueve",
    )
    private val ES_TENS = arrayOf("", "", "", "treinta", "cuarenta", "cincuenta", "sesenta", "setenta", "ochenta", "noventa")
    private val ES_HUND = arrayOf("", "ciento", "doscientos", "trescientos", "cuatrocientos", "quinientos", "seiscientos", "setecientos", "ochocientos", "novecientos")

    private fun es(n: Long): String {
        val millions = (n / 1_000_000).toInt()      // up to 2147: 1.000.000.000 is "mil millones"
        val rest = (n % 1_000_000).toInt()
        return join(
            when { millions == 0 -> ""; millions == 1 -> "un millón"; else -> esBelowMillion(millions, true) + " millones" },
            if (rest == 0) "" else esBelowMillion(rest, false),
        )
    }

    /** [apocope]: "uno" becomes "un" before mil / millones ("veintiún mil", "treinta y un millones"). */
    private fun esBelowMillion(v: Int, apocope: Boolean): String {
        val t = v / 1000
        val r = v % 1000
        return join(
            when { t == 0 -> ""; t == 1 -> "mil"; else -> esBelow1000(t, true) + " mil" },
            if (r == 0) "" else esBelow1000(r, apocope),
        )
    }

    private fun esBelow1000(v: Int, apocope: Boolean): String {
        val h = v / 100
        val r = v % 100
        return join(
            when { h == 0 -> ""; h == 1 && r == 0 -> "cien"; else -> ES_HUND[h] },
            if (r == 0) "" else esBelow100(r, apocope),
        )
    }

    private fun esBelow100(r: Int, apocope: Boolean): String {
        if (r < 30) return when { apocope && r == 1 -> "un"; apocope && r == 21 -> "veintiún"; else -> ES_ONES[r] }
        val u = r % 10
        return ES_TENS[r / 10] + if (u == 0) "" else " y " + (if (apocope && u == 1) "un" else ES_ONES[u])
    }

    // ---------------------------------------------------------------- Portuguese (Brazilian wording)
    private val PT_ONES = arrayOf(
        "zero", "um", "dois", "três", "quatro", "cinco", "seis", "sete", "oito", "nove", "dez", "onze", "doze", "treze", "catorze", "quinze",
        "dezesseis", "dezessete", "dezoito", "dezenove",
    )
    private val PT_TENS = arrayOf("", "", "vinte", "trinta", "quarenta", "cinquenta", "sessenta", "setenta", "oitenta", "noventa")
    private val PT_HUND = arrayOf("", "cento", "duzentos", "trezentos", "quatrocentos", "quinhentos", "seiscentos", "setecentos", "oitocentos", "novecentos")

    private fun ptBelow1000(v: Int): String {
        val h = v / 100
        val r = v % 100
        val hundreds = when { h == 0 -> ""; h == 1 && r == 0 -> "cem"; else -> PT_HUND[h] }
        val rest = when {
            r == 0 -> ""
            r < 20 -> PT_ONES[r]
            else -> PT_TENS[r / 10] + if (r % 10 == 0) "" else " e " + PT_ONES[r % 10]
        }
        return if (hundreds.isNotEmpty() && rest.isNotEmpty()) "$hundreds e $rest" else hundreds + rest
    }

    private fun pt(n: Long): String {
        // (size, singular, plural); thousands are "mil" in both
        val scales = listOf(Triple(1_000_000_000L, "bilhão", "bilhões"), Triple(1_000_000L, "milhão", "milhões"), Triple(1_000L, "mil", "mil"))
        var rem = n
        val sb = StringBuilder()
        for ((size, one, many) in scales) {
            val count = (rem / size).toInt()
            val restValue = rem                      // this part and everything below it
            rem %= size
            if (count == 0) continue
            val text = when {
                size == 1_000L -> if (count == 1) "mil" else ptBelow1000(count) + " mil"
                else -> ptBelow1000(count) + " " + (if (count == 1) one else many)
            }
            appendPt(sb, text, restValue)
        }
        if (rem > 0) appendPt(sb, ptBelow1000(rem.toInt()), rem)
        return sb.toString()
    }

    /** The parts are joined with "e" when what is left is under 100 or a round hundred ("mil e um", "dois milhões e cem mil"), else with a space. */
    private fun appendPt(sb: StringBuilder, text: String, restValue: Long) {
        if (sb.isNotEmpty()) sb.append(if (restValue < 100 || restValue % 100 == 0L) " e " else " ")
        sb.append(text)
    }

    // ---------------------------------------------------------------- Turkish
    private val TR_ONES = arrayOf("sıfır", "bir", "iki", "üç", "dört", "beş", "altı", "yedi", "sekiz", "dokuz")
    private val TR_TENS = arrayOf("", "on", "yirmi", "otuz", "kırk", "elli", "altmış", "yetmiş", "seksen", "doksan")

    private fun trBelow1000(v: Int): String {
        val h = v / 100
        val t = (v / 10) % 10
        val u = v % 10
        return join(
            when { h == 0 -> ""; h == 1 -> "yüz"; else -> TR_ONES[h] + " yüz" },
            TR_TENS[t],
            if (u == 0) "" else TR_ONES[u],
        )
    }

    private fun tr(n: Long): String {
        val scales = listOf(1_000_000_000L to "milyar", 1_000_000L to "milyon", 1_000L to "bin")
        var rem = n
        val parts = ArrayList<String>()
        for ((size, name) in scales) {
            val count = (rem / size).toInt()
            rem %= size
            if (count == 0) continue
            parts += if (size == 1_000L && count == 1) "bin" else trBelow1000(count) + " " + name   // "bin", but "bir milyon"
        }
        if (rem > 0) parts += trBelow1000(rem.toInt())
        return parts.joinToString(" ")
    }

    // ---------------------------------------------------------------- Russian
    private val RU_ONES = arrayOf(
        "ноль", "один", "два", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять", "десять", "одиннадцать", "двенадцать", "тринадцать",
        "четырнадцать", "пятнадцать", "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать",
    )
    private val RU_TENS = arrayOf("", "", "двадцать", "тридцать", "сорок", "пятьдесят", "шестьдесят", "семьдесят", "восемьдесят", "девяносто")
    private val RU_HUND = arrayOf("", "сто", "двести", "триста", "четыреста", "пятьсот", "шестьсот", "семьсот", "восемьсот", "девятьсот")

    private fun ruOnes(x: Int, feminine: Boolean) = when { feminine && x == 1 -> "одна"; feminine && x == 2 -> "две"; else -> RU_ONES[x] }

    private fun ruBelow1000(v: Int, feminine: Boolean): String {
        val h = v / 100
        val r = v % 100
        return join(
            RU_HUND[h],
            when {
                r == 0 -> ""
                r < 20 -> ruOnes(r, feminine)
                else -> join(RU_TENS[r / 10], if (r % 10 == 0) "" else ruOnes(r % 10, feminine))
            },
        )
    }

    /** Russian counts take three forms: 1 тысяча, 2-4 тысячи, 5-20 тысяч (and 21 тысяча, 22 тысячи ...; 11-14 are always the "many" form). */
    private fun ruForm(v: Int, one: String, few: String, many: String): String {
        val last2 = v % 100
        val last = v % 10
        return when { last2 in 11..14 -> many; last == 1 -> one; last in 2..4 -> few; else -> many }
    }

    private fun ru(n: Long): String {
        var rem = n
        val parts = ArrayList<String>()
        val billions = (rem / 1_000_000_000L).toInt(); rem %= 1_000_000_000L
        val millions = (rem / 1_000_000L).toInt(); rem %= 1_000_000L
        val thousands = (rem / 1_000L).toInt(); rem %= 1_000L
        if (billions > 0) parts += ruBelow1000(billions, false) + " " + ruForm(billions, "миллиард", "миллиарда", "миллиардов")
        if (millions > 0) parts += ruBelow1000(millions, false) + " " + ruForm(millions, "миллион", "миллиона", "миллионов")
        if (thousands > 0) parts += ruBelow1000(thousands, true) + " " + ruForm(thousands, "тысяча", "тысячи", "тысяч")
        if (rem > 0) parts += ruBelow1000(rem.toInt(), false)
        return parts.joinToString(" ")
    }

    // ---------------------------------------------------------------- Japanese (kanji numerals, no spaces)
    private const val JA_DIGITS = "〇一二三四五六七八九"

    /** [keepOneThousand]: inside a bigger number 1000 is written 一千 (二万一千), only a plain 1000 is 千. */
    private fun jaBelow10000(v: Int, keepOneThousand: Boolean): String {
        val d3 = v / 1000
        val d2 = (v / 100) % 10
        val d1 = (v / 10) % 10
        val d0 = v % 10
        val sb = StringBuilder()
        if (d3 > 0) sb.append(if (d3 == 1 && !keepOneThousand) "千" else "" + JA_DIGITS[d3] + "千")
        if (d2 > 0) sb.append(if (d2 == 1) "百" else "" + JA_DIGITS[d2] + "百")
        if (d1 > 0) sb.append(if (d1 == 1) "十" else "" + JA_DIGITS[d1] + "十")
        if (d0 > 0) sb.append(JA_DIGITS[d0])
        return sb.toString()
    }

    private fun ja(n: Long): String {
        val oku = (n / 100_000_000L).toInt()
        val man = ((n / 10_000L) % 10_000L).toInt()
        val rest = (n % 10_000L).toInt()
        val big = n >= 10_000L
        return buildString {
            if (oku > 0) append(jaBelow10000(oku, big)).append("億")
            if (man > 0) append(jaBelow10000(man, big)).append("万")
            if (rest > 0) append(jaBelow10000(rest, big))
        }
    }

    // ---------------------------------------------------------------- Thai (no spaces between words)
    private val TH_DIGITS = arrayOf("ศูนย์", "หนึ่ง", "สอง", "สาม", "สี่", "ห้า", "หก", "เจ็ด", "แปด", "เก้า")
    private val TH_UNITS = arrayOf("", "สิบ", "ร้อย", "พัน", "หมื่น", "แสน")

    private fun thBelowMillion(v: Int): String {
        val sb = StringBuilder()
        val tensDigit = (v / 10) % 10
        for (p in 5 downTo 0) {
            var pow = 1
            repeat(p) { pow *= 10 }
            val d = (v / pow) % 10
            if (d == 0) continue
            when (p) {
                0 -> sb.append(if (d == 1 && tensDigit != 0) "เอ็ด" else TH_DIGITS[d])      // 11 = สิบเอ็ด, 21 = ยี่สิบเอ็ด
                1 -> sb.append(when (d) { 1 -> "สิบ"; 2 -> "ยี่สิบ"; else -> TH_DIGITS[d] + "สิบ" })
                else -> sb.append(TH_DIGITS[d]).append(TH_UNITS[p])
            }
        }
        return sb.toString()
    }

    private fun th(n: Long): String {
        val millions = n / 1_000_000L
        val rest = (n % 1_000_000L).toInt()
        return (if (millions > 0) th(millions) + "ล้าน" else "") + (if (rest > 0) thBelowMillion(rest) else "")
    }

    // ---------------------------------------------------------------- shared
    /** 1234567 -> [567, 234, 1]: groups of three digits, lowest first. */
    private fun groups(n: Long): List<Int> {
        val out = ArrayList<Int>()
        var x = n
        while (x > 0) { out += (x % 1000).toInt(); x /= 1000 }
        return out
    }
}
