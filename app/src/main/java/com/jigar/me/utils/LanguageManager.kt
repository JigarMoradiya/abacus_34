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

    /** @param tag language tag, also the resource folder (values-fr; Indonesian is `id` here but its folder is `values-in`, see [resourceQualifier]);
     *  @param nameRes the language written in itself; @param wave 0 = source, 1 / 2 = first release waves, 3 = the second round (de id vi), 4 = zh ko hi. */
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
        Language("de", R.string.language_name_de, 3),
        Language("id", R.string.language_name_id, 3),   // the resource folder is values-in: Android still calls Indonesian "in"
        Language("vi", R.string.language_name_vi, 3),
        Language("hi", R.string.language_name_hi, 4),
        Language("zh", R.string.language_name_zh, 4),   // Simplified Chinese (mainland); one folder values-zh
    )

    /** Android (and Java before 17) report Indonesian as the old code "in", whatever tag it was set with: read it as "id". */
    fun normalizeTag(tag: String?): String? = if (tag == "in") "id" else tag

    /** The folder qualifier of the strings of language [tag] (values-<this>): the same tag, except Indonesian which is "in". */
    fun resourceQualifier(tag: String): String = if (tag == "id") "in" else tag

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
    fun tagFor(chosenLanguage: String?): String = normalizeTag(chosenLanguage).let { chosen -> all.firstOrNull { it.tag == chosen && isOffered(it.tag) }?.tag ?: "en" }

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
     *  setting) is kept and remembered for background work. [rawEnabled] is the saved Remote Config `enabled_languages`: a language
     *  that is not in it any more (removed, or the list cleared) goes back to English. */
    fun startUp(rawEnabled: String?) {
        val chosen = AppCompatDelegate.getApplicationLocales()
        if (chosen.isEmpty) set(savedTag() ?: "en") else chosen[0]?.language?.let { saveTag(normalizeTag(it) ?: it) }
        resetIfNotEnabled(rawEnabled)
    }

    /** True when [tag] may stay the language of the app: English always, another language while `enabled_languages` lists it (and always in debug builds). */
    fun isStillEnabled(tag: String, rawEnabled: String?, includeAll: Boolean = BuildConfig.DEBUG): Boolean =
        tag == "en" || (isOffered(tag) && tag in parseEnabled(rawEnabled, includeAll))

    /** English again when the language in use is no longer enabled: the console removed its tag or cleared `enabled_languages`.
     *  From the main thread (it applies a language); the picker, notifications and the voice words follow. */
    fun resetIfNotEnabled(rawEnabled: String?) {
        if (!isStillEnabled(currentTag(), rawEnabled)) set("en")
    }

    /** The app language whose words a voice of [voice] speaks: the voice's own language when the app has it, else English
     *  (an Indian English or Hindi voice gets English words, as before). */
    fun tagForVoice(voice: Locale, appTag: String? = null): String = normalizeTag(voice.language).let { lang ->
        // a Hindi voice kept English words before Hindi was an app language (Indian parents often pick it for the accent): it still does,
        // unless the app itself is in Hindi
        if (lang == "hi" && appTag != "hi") "en" else all.firstOrNull { it.tag == lang && isOffered(it.tag) }?.tag ?: "en"
    }

    /** Applies a language, from the main thread. HomeActivity handles the change itself (its configChanges include locale and layoutDirection),
     *  so the screen is not created again; the screens read their strings again. */
    fun set(tag: String) {
        saveTag(tag)
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
        onLanguageChanged?.invoke()
    }

    /** Set by FcmTokenManager: the app language is one of the push details sent to our server. */
    var onLanguageChanged: (() -> Unit)? = null
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
