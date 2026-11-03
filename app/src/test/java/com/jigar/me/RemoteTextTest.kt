package com.jigar.me

import com.jigar.me.utils.RemoteText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteTextTest {
    @Test fun languageKeyIsTheBaseKeyWithTheTag() = assertEquals("paywall_social_proof_fr", RemoteText.key("paywall_social_proof", "fr"))

    @Test fun everyAppLanguageButEnglishHasAKey() {
        val keys = RemoteText.languageKeys("videoList")
        assertTrue("videoList_ru" in keys && "videoList_ja" in keys && "videoList_th" in keys)
        assertFalse("videoList_en" in keys)
    }
}
