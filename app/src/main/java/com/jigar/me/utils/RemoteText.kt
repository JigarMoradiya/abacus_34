package com.jigar.me.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.data.pref.AppPreferencesHelper

/**
 * Server text that exists once per language, in ONE Remote Config value (the apps already live read the older plain-text key, so the JSON
 * has a key of its own, for example `paywall_social_proof_languages`). The value is either plain text (shown in every language) or a JSON
 * object keyed by language tag: `{"en":"...","fr":"...","ja":"..."}`. A language without its own entry shows the `en` one; a damaged
 * JSON, or an object without `en` and without the language, gives nothing, and [read] then goes on with the next key it was given
 * (the older plain-text key). The splash screen saves the values; the screens read through [read], so the text follows the language
 * the app is in now.
 */
object RemoteText {
    private val byTagType = object : TypeToken<Map<String, String?>>() {}.type

    /** The text in [raw] for language [tag] (default: the language of the app now). */
    fun pick(raw: String, tag: String = LanguageManager.currentTag()): String {
        if (!raw.trimStart().startsWith("{")) return raw
        val byTag: Map<String, String?> = runCatching { Gson().fromJson<Map<String, String?>>(raw, byTagType) }.getOrNull() ?: return ""
        return byTag[tag]?.takeIf { it.isNotBlank() } ?: byTag["en"].orEmpty()
    }

    /** The first of [raws] (most wanted first) that has a text for language [tag]; empty when none has. */
    fun pickFirst(raws: List<String>, tag: String = LanguageManager.currentTag()): String =
        raws.firstNotNullOfOrNull { pick(it, tag).takeIf { text -> text.isNotBlank() } }.orEmpty()

    /** The text of the first of the saved values [bases] (most wanted first) that has one in the language of the app now. */
    fun read(prefs: AppPreferencesHelper, vararg bases: String): String =
        pickFirst(bases.map { prefs.getCustomParam(it, "") })
}
