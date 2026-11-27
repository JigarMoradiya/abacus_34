package com.jigar.me

import com.jigar.me.utils.NumberWords
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The reference file `number_words_golden.tsv` is Android's own ICU spell-out of 621 numbers per language (language, number, words),
 * taken from a real phone (Android 16) with soft hyphens and zero-width spaces removed.
 *
 * German, Indonesian and Vietnamese rows come from the ICU spell-out of macOS (same CLDR rules, generated 2026-11-25), not from a phone;
 * Hindi and Chinese rows the same way (2026-11-27).
 *
 * Differences from ICU on purpose:
 * - Indonesian: ICU says "negatif" for a negative number; people (and maths books) say "minus", so the reference rows were changed to "minus".
 * - Japanese: ICU writes 1000 inside a bigger number as 千 (二万千). Correct is 二万一千, so those numbers are skipped here and checked by hand below.
 * - Chinese (macOS ICU, 2026-11-27): ICU fills a gap with 〇 (一百〇五); written Chinese uses 零 (一百零五), so the rows say 零. ICU also drops the
 *   零 after 亿 (一亿二百四十八万); school books write 一亿零二百四十八万, so those 6 rows were changed too.
 */
class NumberWordsTest {

    private data class Row(val lang: String, val n: Long, val words: String)

    private val rows: List<Row> by lazy {
        val text = javaClass.classLoader!!.getResourceAsStream("number_words_golden.tsv")!!.bufferedReader(Charsets.UTF_8).readText()
        text.lines().filter { it.isNotBlank() }.map { val p = it.split('\t'); Row(p[0], p[1].toLong(), p[2]) }
    }

    /** True for Japanese numbers where ICU leaves out the 一 before 千 inside a bigger number. */
    private fun icuQuirk(r: Row): Boolean {
        if (r.lang != "ja") return false
        val a = Math.abs(r.n)
        if (a < 10_000) return false
        return listOf(a / 100_000_000, (a / 10_000) % 10_000, a % 10_000).any { it / 1000 == 1L }
    }

    @Test fun matchesTheReferenceOfARealPhone() {
        val bad = ArrayList<String>()
        for (r in rows) {
            if (icuQuirk(r)) continue
            val ours = NumberWords.spell(r.n, r.lang)
            if (ours != r.words) bad += "${r.lang} ${r.n}: ours='$ours' ICU='${r.words}'"
        }
        assertTrue("${bad.size} differ:\n" + bad.take(40).joinToString("\n"), bad.isEmpty())
    }

    @Test fun referenceFileIsComplete() {
        assertEquals(12 * 621, rows.size)
        assertEquals(NumberWords.languages - "ar", rows.map { it.lang }.toSet())   // Arabic has no phone reference: see ArabicNumberWordsTest
    }

    @Test fun japaneseInsideBiggerNumbers() {
        assertEquals("千", NumberWords.spell(1000, "ja"))
        assertEquals("二万一千", NumberWords.spell(21000, "ja"))
        assertEquals("一万一千", NumberWords.spell(11000, "ja"))
        assertEquals("一千万", NumberWords.spell(10_000_000, "ja"))
        assertEquals("一億", NumberWords.spell(100_000_000, "ja"))
    }

    @Test fun unknownLanguageIsNull() {
        assertNull(NumberWords.spell(5, "en"))
        assertNull(NumberWords.spell(5, "sw"))
        assertNull(NumberWords.spell(5, "xx"))
    }

    @Test fun nothingBlankOrWithDigits() {
        for (lang in NumberWords.languages) for (n in listOf(0L, 1, 9, 10, 99, 100, 999, 1000, 123456, 2_147_483_647L, -7)) {
            val s = NumberWords.spell(n, lang)!!
            assertTrue("$lang $n", s.isNotBlank() && s.none { it.isDigit() })
        }
    }
}
