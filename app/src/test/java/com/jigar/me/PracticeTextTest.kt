package com.jigar.me

import com.jigar.me.ui.view.home.screens.abacus_practice.PracticePatterns
import com.jigar.me.ui.view.home.screens.abacus_practice.PracticeText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PracticeTextTest {
    /** `pattern <TAB> example` for every page title and description of the server data (tools/l10n/practice_patterns.py --write). */
    private val fixture = javaClass.classLoader!!.getResourceAsStream("practice_patterns.txt")!!.bufferedReader().readLines()
        .filter { it.isNotBlank() }.map { it.split('\t') }

    @Test fun everyKnownServerTextHasATranslation() {
        assertTrue(fixture.size > 100)
        for ((pattern, example) in fixture) {
            val match = PracticeText.match(example)
            assertNotNull("no translation for \"$example\"", match)
            assertEquals(pattern, PracticeText.patternOf(example))
            assertEquals(Regex("\\d+").findAll(example).map { it.value }.toList(), match!!.numbers)
        }
    }

    @Test fun numbersAreCollectedInOrder() {
        assertEquals(listOf("12"), PracticeText.match("Mental Arithmetic 12")!!.numbers)
        assertEquals(listOf("3", "2", "1", "5"), PracticeText.match("Multiplication 3 (2D x 1D) (Level 5)")!!.numbers)
    }

    @Test fun caseAndSpacingOfTheServerDoNotMatter() {
        assertEquals(PracticeText.match("Revision 4 (Little Friend of +5)")!!.res, PracticeText.match("Revision 4 (Little friend of +5) ")!!.res)
        assertEquals(PracticeText.match("All Formula of -2,-3")!!.res, PracticeText.match("All  Formula of -2,-3")!!.res)
        assertEquals(PracticeText.match("Divide By Single Digit")!!.res, PracticeText.match("Divide by Single Digit")!!.res)
    }

    @Test fun signsOnlyAndUnknownTextStayAsTheServerSentThem() {
        assertNull(PracticeText.match("+5"))
        assertNull(PracticeText.match("-3,-4"))
        assertNull(PracticeText.match("A page title nobody has seen"))
        assertNull(PracticeText.match(null))
        assertNull(PracticeText.match("  "))
    }

    @Test fun everyPatternIsStoredNormalized() {
        for (key in PracticePatterns.map.keys) assertEquals(key, PracticeText.patternOf(key))
    }
}
