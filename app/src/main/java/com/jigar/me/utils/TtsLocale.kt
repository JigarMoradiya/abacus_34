package com.jigar.me.utils

import com.jigar.me.data.pref.AppPreferencesHelper
import java.util.Locale

/**
 * The language of the spoken voice (text to speech).
 *
 * Two old problems are fixed here:
 * - `Locale.forLanguageTag("en_IN")` (the old default, with an underscore) is an EMPTY locale in Java, so the default voice was never
 *   really set and the phone's own language was used.
 * - The choice was saved as "hi-IN" but read back with Gson, which expects "hi_IN": a chosen voice language silently became English.
 */
object TtsLocale {

    /** Reads a saved voice language: "hi-IN", "hi_IN" or the old Gson form "\"hi_IN\"". Null when empty or not a language. */
    fun parse(saved: String?): Locale? {
        val tag = saved.orEmpty().trim().trim('"').replace('_', '-')
        if (tag.isEmpty()) return null
        val locale = Locale.forLanguageTag(tag)
        return if (locale.language.isNullOrEmpty()) null else locale
    }

    /** The usual voice for an app language when the user has not picked one. English keeps the Indian English voice. */
    fun defaultFor(appLanguageTag: String): Locale = Locale.forLanguageTag(
        when (appLanguageTag) {
            "fr" -> "fr-FR"
            "es" -> "es-ES"
            "pt" -> "pt-BR"
            "tr" -> "tr-TR"
            "ru" -> "ru-RU"
            "ja" -> "ja-JP"
            "th" -> "th-TH"
            "ar" -> "ar"
            else -> AppPreferencesHelper.DEFAULT_TTS_LANGUAGE_VALUE.replace('_', '-')
        }
    )

    /** What to use now: the saved choice, else the default for the app language. */
    fun current(saved: String?, appLanguageTag: String): Locale = parse(saved) ?: defaultFor(appLanguageTag)
}
