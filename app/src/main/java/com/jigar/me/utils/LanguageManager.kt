package com.jigar.me.utils

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import android.content.Context
import android.content.res.Configuration
import androidx.core.os.LocaleListCompat
import com.jigar.me.BuildConfig
import com.jigar.me.MyApplication
import com.jigar.me.R
import java.util.Locale

/**
 * The languages the app can be shown in, which ones the picker offers, and how the choice is applied.
 *
 * - The choice is stored by AppCompat (`autoStoreLocales` in the manifest) and applied to every activity, also on Android 7-12.
 * - Which languages are offered besides English comes from the Remote Config key `enabled_languages` (see [parseEnabled]), so a
 *   language can be turned on or off without a new release. Debug builds offer all of them.
 * - English is the default whatever the phone's language is ([useEnglishUntilChosen]); another language only after the person picks it.
 */
object LanguageManager {

    /** @param tag language tag, also the resource folder (values-fr); @param nameRes the language written in itself; @param wave 0 = source, 1 / 2 = release wave. */
    data class Language(val tag: String, @StringRes val nameRes: Int, val wave: Int)

    /** Arabic is right to left. It is switched on only when the right-to-left pass (module M21) is finished and checked: until then it is
     *  never offered and an Arabic phone gets English. Turn this on together with `apply.py --with-ar`. */
    const val RTL_READY = true
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

    /** The tag of the language [chosenLanguage] (what the person picked, null = nothing picked) gives: the phone's language is NOT used,
     *  English is the default. A tag the app does not offer (yet) is English too. */
    fun tagFor(chosenLanguage: String?): String = all.firstOrNull { it.tag == chosenLanguage && isOffered(it.tag) }?.tag ?: "en"

    /** The tag of the language the UI uses now: the language picked in the app (or in the phone's per-app language setting), else English.
     *  AppCompat can only tell the picked language while an activity exists (on Android 13+ it asks the system through the activity), so the
     *  pick is also kept in our own preference file: background work (notifications) reads it from there. */
    fun currentTag(): String {
        val chosen = AppCompatDelegate.getApplicationLocales()
        return tagFor(if (!chosen.isEmpty) chosen[0]?.language else savedTag())
    }

    private const val PREFS = "app_language"
    private const val KEY_TAG = "tag"
    private fun savedTag(): String? = runCatching {
        MyApplication.getInstance().getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_TAG, null)
    }.getOrNull()
    private fun saveTag(tag: String) {
        runCatching { MyApplication.getInstance().getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_TAG, tag).apply() }
    }

    /** English is the default language, whatever the phone's language is: until a language is picked the app is set to English.
     *  Call from an activity right after `super.onCreate`; a language picked later (in the app or in the phone's per-app language
     *  setting) is kept and remembered for background work. */
    fun startUp() {
        val chosen = AppCompatDelegate.getApplicationLocales()
        if (chosen.isEmpty) set(savedTag() ?: "en") else chosen[0]?.language?.let { saveTag(it) }
    }

    /** The app language whose words a voice of [voice] speaks: the voice's own language when the app has it, else English
     *  (an Indian English or Hindi voice gets English words, as before). */
    fun tagForVoice(voice: Locale): String = all.firstOrNull { it.tag == voice.language && isOffered(it.tag) }?.tag ?: "en"

    /** Applies a language. Recreates the activity, so call it from the main thread. */
    fun set(tag: String) {
        saveTag(tag)
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
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

/** This context in the language the app shows now (the chosen language, else English). */
fun Context.inAppLanguage(): Context = withLanguage(LanguageManager.currentTag())
