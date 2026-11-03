package com.jigar.me.utils

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import android.content.Context
import android.content.res.Configuration
import androidx.core.os.LocaleListCompat
import com.jigar.me.BuildConfig
import com.jigar.me.R
import java.util.Locale

/**
 * The languages the app can be shown in, which ones the picker offers, and how the choice is applied.
 *
 * - The choice is stored by AppCompat (`autoStoreLocales` in the manifest) and applied to every activity, also on Android 7-12.
 * - Which languages are offered besides English comes from the Remote Config key `enabled_languages` (see [parseEnabled]), so a
 *   language can be turned on or off without a new release. Debug builds offer all of them.
 * - "Follow the phone" means no app locale is set: the phone's language is used when we have it, otherwise English.
 */
object LanguageManager {

    /** @param tag language tag, also the resource folder (values-fr); @param nameRes the language written in itself; @param wave 0 = source, 1 / 2 = release wave. */
    data class Language(val tag: String, @StringRes val nameRes: Int, val wave: Int)

    /** Arabic is right to left. It is switched on only when the right-to-left pass (module M21) is finished and checked: until then it is
     *  never offered and an Arabic phone gets English. Turn this on together with `apply.py --with-ar`. */
    const val RTL_READY = false
    private fun isOffered(tag: String) = tag != "ar" || RTL_READY

    val all: List<Language> = listOf(
        Language("en", R.string.language_name_en, 0),
        Language("fr", R.string.language_name_fr, 1),
        Language("es", R.string.language_name_es, 1),
        Language("pt", R.string.language_name_pt, 1),   // one folder for Brazilian and European Portuguese (Brazilian wording)
        Language("tr", R.string.language_name_tr, 1),
        Language("ru", R.string.language_name_ru, 1),
        Language("ja", R.string.language_name_ja, 2),
        Language("th", R.string.language_name_th, 2),
        Language("ar", R.string.language_name_ar, 2),
    )

    /** "fr, es,xx,,RU" -> the tags of [all] that are listed (unknown ones ignored), English always included, in the order of [all]. */
    fun parseEnabled(raw: String?, includeAll: Boolean = false): List<String> {
        if (includeAll) return all.map { it.tag }
        val wanted = raw.orEmpty().split(',', ';', ' ').map { it.trim().lowercase(Locale.ROOT) }.filter { it.isNotEmpty() }.toSet()
        return all.filter { it.tag == "en" || it.tag in wanted }.map { it.tag }
    }

    /** The languages the picker offers right now. Debug builds offer all of them (for testing without Remote Config). */
    fun enabled(rawRemoteConfig: String?): List<Language> {
        val tags = parseEnabled(rawRemoteConfig, includeAll = BuildConfig.DEBUG).toSet()
        return all.filter { it.tag in tags && isOffered(it.tag) }
    }

    /** The tag of the language the UI uses now: the chosen app language, else the phone's when we have it, else English. */
    fun currentTag(): String {
        val chosen = AppCompatDelegate.getApplicationLocales()
        val language = if (!chosen.isEmpty) chosen[0]?.language else Locale.getDefault().language
        return all.firstOrNull { it.tag == language && isOffered(it.tag) }?.tag ?: "en"
    }

    /** The app language whose words a voice of [voice] speaks: the voice's own language when the app has it, else English
     *  (an Indian English or Hindi voice gets English words, as before). */
    fun tagForVoice(voice: Locale): String = all.firstOrNull { it.tag == voice.language && isOffered(it.tag) }?.tag ?: "en"

    /** True when the user has not picked a language in the app (the phone's language is followed). */
    fun isFollowingPhone(): Boolean = AppCompatDelegate.getApplicationLocales().isEmpty

    /** Applies a language, or null to follow the phone again. Recreates the activity, so call it from the main thread. */
    fun set(tag: String?) {
        val list = if (tag == null) LocaleListCompat.getEmptyLocaleList() else LocaleListCompat.forLanguageTags(tag)
        AppCompatDelegate.setApplicationLocales(list)
    }
}

/**
 * This context with its strings in language [tag] ("en" = the default strings).
 * Needed because the application context (the one view models hold) does not follow the language picked inside the app on Android 12
 * and older, only the activity does.
 */
fun Context.withLanguage(tag: String): Context {
    val config = Configuration(resources.configuration)
    config.setLocale(Locale.forLanguageTag(tag))
    return createConfigurationContext(config)
}

/** This context in the language the app shows now (the chosen language, else the phone's when we have it, else English). */
fun Context.inAppLanguage(): Context = withLanguage(LanguageManager.currentTag())
