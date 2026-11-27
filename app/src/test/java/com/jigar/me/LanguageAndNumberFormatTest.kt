package com.jigar.me

import com.jigar.me.utils.LanguageManager
import com.jigar.me.utils.LocalizedNumberFormat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class LanguageAndNumberFormatTest {

    @Test fun missingListMeansEnglishOnly() {
        assertEquals(listOf("en"), LanguageManager.parseEnabled(null))
        assertEquals(listOf("en"), LanguageManager.parseEnabled(""))
    }

    @Test fun listedLanguagesAreOfferedInTheFixedOrderAndEnglishIsAlwaysThere() {
        assertEquals(listOf("en", "fr", "ru"), LanguageManager.parseEnabled("ru, fr"))
        assertEquals(listOf("en", "es", "ar"), LanguageManager.parseEnabled("AR;es"))
    }

    @Test fun unknownTagsAndJunkAreIgnored() {
        assertEquals(listOf("en", "tr"), LanguageManager.parseEnabled("xx,,tr, ,zz"))
    }

    @Test fun includeAllGivesEveryLanguage() {
        assertEquals(13, LanguageManager.parseEnabled("fr", includeAll = true).size)
    }

    @Test fun aLanguageStaysOnlyWhileItIsListed() {
        assertTrue(LanguageManager.isStillEnabled("en", "", includeAll = false))
        assertTrue(LanguageManager.isStillEnabled("fr", "fr,es", includeAll = false))
        assertTrue(LanguageManager.isStillEnabled("fr", " ES ; FR", includeAll = false))
        assertFalse(LanguageManager.isStillEnabled("fr", "es,ru", includeAll = false))
        assertFalse(LanguageManager.isStillEnabled("fr", "", includeAll = false))
        assertFalse(LanguageManager.isStillEnabled("fr", null, includeAll = false))
    }

    @Test fun debugBuildsNeverResetALanguage() = assertTrue(LanguageManager.isStillEnabled("fr", "", includeAll = true))

    @Test fun decimalMarkFollowsTheLanguage() {
        for (tag in listOf("fr", "es", "pt", "ru", "tr")) assertEquals("$tag", ',', LocalizedNumberFormat.decimalSeparator(Locale.forLanguageTag(tag)))
        for (tag in listOf("en", "ja", "th")) assertEquals("$tag", '.', LocalizedNumberFormat.decimalSeparator(Locale.forLanguageTag(tag)))
    }

    @Test fun digitsAreAlwaysWestern() {
        for (tag in listOf("ar", "ar-SA", "ar-EG", "th", "ja", "ru", "fr")) {
            val shown = LocalizedNumberFormat.format(1234.5, locale = Locale.forLanguageTag(tag))
            assertTrue("$tag -> $shown", shown.all { it in '0'..'9' || it == ',' || it == '.' || it == '٫' })
            assertTrue("$tag -> $shown", shown.startsWith("1234"))
        }
    }

    @Test fun displayKeepsTheDigitsAndOnlyChangesTheMark() {
        assertEquals("12,5", LocalizedNumberFormat.display("12.5", Locale.forLanguageTag("fr")))
        assertEquals("0,05", LocalizedNumberFormat.display("0.05", Locale.forLanguageTag("ru")))
        assertEquals("12.5", LocalizedNumberFormat.display("12.5", Locale.forLanguageTag("en")))
        assertEquals("456", LocalizedNumberFormat.display("456", Locale.forLanguageTag("tr")))
    }

    @Test fun formatHasNoGroupingAndTrimsZeros() {
        assertEquals("1000000", LocalizedNumberFormat.format(1000000.0, locale = Locale.forLanguageTag("fr")))
        assertEquals("2,5", LocalizedNumberFormat.format(2.5, locale = Locale.forLanguageTag("es")))
        assertEquals("3", LocalizedNumberFormat.format(3.0, locale = Locale.forLanguageTag("ja")))
    }

    @Test fun englishIsTheDefaultWhateverThePhoneSpeaks() {
        val before = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag("fr-FR"))
            assertEquals("en", LanguageManager.tagFor(null))   // nothing picked: English, not the phone's French
        } finally { Locale.setDefault(before) }
    }

    @Test fun aPickedLanguageIsUsedAndAnUnknownOneIsEnglish() {
        assertEquals("fr", LanguageManager.tagFor("fr"))
        assertEquals("ar", LanguageManager.tagFor("ar"))
        assertEquals("en", LanguageManager.tagFor("sw"))
        assertEquals("en", LanguageManager.tagFor("en"))
    }

    @Test fun theNewLanguagesAreOffered() {
        assertEquals("de", LanguageManager.tagFor("de"))
        assertEquals("vi", LanguageManager.tagFor("vi"))
        assertEquals("id", LanguageManager.tagFor("id"))
        assertEquals("hi", LanguageManager.tagFor("hi"))
    }

    @Test fun androidCallsIndonesianInSoTheOldCodeIsReadAsId() {
        // Android reports the language of "id" as the old code "in" (a saved or system tag); it must not fall back to English
        assertEquals("id", LanguageManager.tagFor("in"))
        assertEquals("id", LanguageManager.tagForVoice(Locale("in", "ID")))
        assertEquals("id", LanguageManager.normalizeTag("in"))
        assertEquals("in", LanguageManager.resourceQualifier("id"))      // the strings folder is values-in
        assertEquals("de", LanguageManager.resourceQualifier("de"))
    }

    @Test fun voiceLanguageDecidesTheSpokenWords() {
        assertEquals("fr", LanguageManager.tagForVoice(Locale.forLanguageTag("fr-CA")))
        assertEquals("pt", LanguageManager.tagForVoice(Locale.forLanguageTag("pt-BR")))
        assertEquals("en", LanguageManager.tagForVoice(Locale.forLanguageTag("en-IN")))
        assertEquals("en", LanguageManager.tagForVoice(Locale.forLanguageTag("hi-IN")))   // a Hindi voice gets English words, as before
        assertEquals("en", LanguageManager.tagForVoice(Locale.forLanguageTag("hi-IN"), "en"))   // ... while the app is in English
        assertEquals("hi", LanguageManager.tagForVoice(Locale.forLanguageTag("hi-IN"), "hi"))   // Hindi words once the app is in Hindi
        assertEquals("ar", LanguageManager.tagForVoice(Locale.forLanguageTag("ar")))      // Arabic words (the right-to-left pass is on)
        assertEquals("de", LanguageManager.tagForVoice(Locale.forLanguageTag("de-AT")))
        assertEquals("vi", LanguageManager.tagForVoice(Locale.forLanguageTag("vi-VN")))
    }
}
