package com.jigar.me

import com.jigar.me.utils.RemoteText
import org.junit.Assert.assertEquals
import org.junit.Test

class RemoteTextTest {
    private val json = """{"en":"Join us","fr":"Rejoignez-nous","ja":"","th":null}"""

    @Test fun plainTextIsTheSameInEveryLanguage() {
        assertEquals("Join us", RemoteText.pick("Join us", "fr"))
        assertEquals("", RemoteText.pick("", "fr"))
    }

    @Test fun jsonGivesTheTextOfTheLanguage() {
        assertEquals("Rejoignez-nous", RemoteText.pick(json, "fr"))
        assertEquals("Join us", RemoteText.pick(json, "en"))
    }

    @Test fun aMissingBlankOrNullLanguageFallsBackToEnglish() {
        assertEquals("Join us", RemoteText.pick(json, "es"))
        assertEquals("Join us", RemoteText.pick(json, "ja"))
        assertEquals("Join us", RemoteText.pick(json, "th"))
    }

    @Test fun jsonWithoutEnglishShowsNothingForAMissingLanguage() = assertEquals("", RemoteText.pick("""{"fr":"Bonjour"}""", "es"))

    @Test fun aDamagedJsonShowsNothing() = assertEquals("", RemoteText.pick("""{"en":"Join us""", "en"))

    @Test fun theJsonKeyWinsAndTheOlderPlainKeyIsTheFallback() {
        assertEquals("Rejoignez-nous", RemoteText.pickFirst(listOf(json, "Old line"), "fr"))
        assertEquals("Old line", RemoteText.pickFirst(listOf("", "Old line"), "fr"))
        assertEquals("Old line", RemoteText.pickFirst(listOf("""{"en":"Join us""", "Old line"), "fr"))
        assertEquals("Old line", RemoteText.pickFirst(listOf("""{"fr":"Bonjour"}""", "Old line"), "es"))
        assertEquals("", RemoteText.pickFirst(listOf("", ""), "fr"))
    }
}
