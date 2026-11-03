package com.jigar.me

import com.google.gson.Gson
import com.jigar.me.utils.TtsLocale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Locale

class TtsLocaleTest {

    @Test fun theOldDefaultWithAnUnderscoreWasAnEmptyLocale() {
        // the reason the default voice never worked: this is what the old code built
        assertEquals("", Locale.forLanguageTag("en_IN").language)
        assertEquals("en", TtsLocale.defaultFor("en").language)
        assertEquals("IN", TtsLocale.defaultFor("en").country)
    }

    @Test fun savedTagsAreReadInEveryFormTheAppEverWroteOrWillWrite() {
        assertEquals("hi-IN", TtsLocale.parse("hi-IN")?.toLanguageTag())
        assertEquals("hi-IN", TtsLocale.parse("hi_IN")?.toLanguageTag())
        assertEquals("hi-IN", TtsLocale.parse("\"hi_IN\"")?.toLanguageTag())     // what Gson wrote in old versions
        assertEquals("fr", TtsLocale.parse(" fr ")?.toLanguageTag())
    }

    @Test fun emptyOrJunkIsNotALanguage() {
        assertNull(TtsLocale.parse(null)); assertNull(TtsLocale.parse("")); assertNull(TtsLocale.parse("\"\"")); assertNull(TtsLocale.parse("12"))
    }

    @Test fun gsonReadsTheNewTagWrongWhichIsWhyItIsNotUsedAnyMore() {
        val viaGson = Gson().fromJson("hi-IN", Locale::class.java)
        assertEquals("hi-in", viaGson.language)      // the whole text became the "language": not a real language
        assertNotNull(TtsLocale.parse("hi-IN"))
    }

    @Test fun everyAppLanguageHasAVoiceDefault() {
        for (tag in listOf("fr", "es", "pt", "tr", "ru", "ja", "th", "ar", "en")) assertEquals(tag, TtsLocale.defaultFor(tag).language)
    }

    @Test fun aSavedChoiceWinsOverTheAppLanguageDefault() {
        assertEquals("de", TtsLocale.current("de-DE", "fr").language)
        assertEquals("fr", TtsLocale.current("", "fr").language)
    }
}
