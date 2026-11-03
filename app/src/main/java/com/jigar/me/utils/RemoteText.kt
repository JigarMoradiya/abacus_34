package com.jigar.me.utils

import com.jigar.me.data.pref.AppPreferencesHelper

/**
 * Server text that exists once per language. The Remote Config value of a key such as `paywall_social_proof` is the English text;
 * the same key with the language tag at the end (`paywall_social_proof_fr`, `_es`, `_pt`, `_tr`, `_ru`, `_ja`, `_th`) holds the
 * translation. A language without its own value shows the English one. The splash screen saves every value; the screens read
 * through [read], so the text follows the language the app is in now.
 */
object RemoteText {
    /** The keys whose text is shown to the parent and can be translated in the Remote Config console. */
    val translatedKeys: List<String> = listOf(
        AppConstants.RemoteConfig.videoList,
        AppConstants.RemoteConfig.paywallSocialProof,
        AppConstants.RemoteConfig.newVersionNotes,
        AppConstants.RemoteConfig.bulkLogin,
    )

    fun key(base: String, tag: String): String = "${base}_$tag"

    /** The per-language keys of [base] (every app language but English). */
    fun languageKeys(base: String): List<String> = LanguageManager.all.filter { it.tag != "en" }.map { key(base, it.tag) }

    /** The saved text of [base] in language [tag] (default: the language of the app now), else the English one. */
    fun read(prefs: AppPreferencesHelper, base: String, tag: String = LanguageManager.currentTag()): String {
        val own = if (tag == "en") "" else prefs.getCustomParam(key(base, tag), "")
        return own.ifBlank { prefs.getCustomParam(base, "") }
    }
}
