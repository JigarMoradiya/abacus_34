package com.jigar.me

import com.jigar.me.utils.NumberWords
import org.junit.Assert.assertEquals
import org.junit.Test

/** Modern Standard Arabic cardinals (masculine, nominative), spot checks written by hand. */
class ArabicNumberWordsTest {
    private fun w(n: Long) = NumberWords.spell(n, "ar")

    @Test fun smallNumbers() {
        assertEquals("صفر", w(0)); assertEquals("واحد", w(1)); assertEquals("اثنان", w(2)); assertEquals("عشرة", w(10))
        assertEquals("أحد عشر", w(11)); assertEquals("اثنا عشر", w(12)); assertEquals("تسعة عشر", w(19))
    }

    @Test fun tensAreJoinedWithAnd() {
        assertEquals("عشرون", w(20)); assertEquals("واحد وعشرون", w(21)); assertEquals("خمسة وأربعون", w(45)); assertEquals("تسعة وتسعون", w(99))
    }

    @Test fun hundreds() {
        assertEquals("مئة", w(100)); assertEquals("مئة وواحد", w(101)); assertEquals("مئتان", w(200)); assertEquals("ثلاثمئة وخمسة وعشرون", w(325))
        assertEquals("تسعمئة وتسعة وتسعون", w(999))
    }

    @Test fun thousandsAndMillions() {
        assertEquals("ألف", w(1000)); assertEquals("ألف وواحد", w(1001)); assertEquals("ألفان", w(2000)); assertEquals("ثلاثة آلاف", w(3000))
        assertEquals("أحد عشر ألفًا", w(11000)); assertEquals("مئتا ألف", w(200000)); assertEquals("مليون", w(1_000_000))
        assertEquals("مليونان وخمسمئة ألف", w(2_500_000)); assertEquals("ثلاثة ملايين", w(3_000_000))
    }

    @Test fun negativeNumbersAreSaid() = assertEquals("سالب خمسة", w(-5))
}
