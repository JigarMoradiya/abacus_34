package com.jigar.me.ui.view.jetpack.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.google.gson.Gson
import com.jigar.me.data.pref.AppPreferencesHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject

class TextToSpeechManager @Inject constructor(
    @ApplicationContext context: Context,
    private val prefs: AppPreferencesHelper
) {

    private var tts: TextToSpeech? = null
    private var ready = false
    private var pending: Pair<String, String?>? = null

    // 🔹 Optional callback (NOT always used)
    private val utteranceCallbacks =
        mutableMapOf<String, (String?) -> Unit>()



    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ready = true
                applySettings()
                setupListener()
                pending?.let { (text, id) ->
                    speak(text, id)
                    pending = null
                }
            }
        }
    }

    private fun setupListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {

            override fun onStart(utteranceId: String?) = Unit

            override fun onDone(utteranceId: String?) {
                utteranceId?.let { id ->
                    utteranceCallbacks[id]?.invoke(id)
                    utteranceCallbacks.remove(id)
                }
            }

            override fun onError(utteranceId: String?) = Unit
        })
    }


    fun speak(text: String, utteranceId: String? = null, onDone: ((String?) -> Unit)? = null) {
        if (!ready) {
            pending = text to utteranceId
            utteranceId?.let { id ->
                onDone?.let { utteranceCallbacks[id] = it }
            }
            return
        }

        utteranceId?.let { id ->
            onDone?.let { utteranceCallbacks[id] = it }
        }

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }


    fun stop() {
        tts?.stop()
    }

    fun applySettings() {
        val currentLanguageJson = prefs.getCustomParam(AppPreferencesHelper.KEY_DEFAULT_TTS_LANGUAGE, "")

        val locale = if (currentLanguageJson.isEmpty()) {
            Locale.forLanguageTag(AppPreferencesHelper.DEFAULT_TTS_LANGUAGE_VALUE)
        } else {
            Gson().fromJson(currentLanguageJson, Locale::class.java)
        }
        val speed = prefs.getDefaultTTSSpeed()
        val pitch = prefs.getDefaultTTSPitch()

        // Set language
        val langResult = tts?.setLanguage(locale)

        if (langResult == TextToSpeech.LANG_MISSING_DATA ||
            langResult == TextToSpeech.LANG_NOT_SUPPORTED
        ) {
            Log.e("TTS", "Language not supported: $locale")
            tts?.language = Locale.forLanguageTag(AppPreferencesHelper.DEFAULT_TTS_LANGUAGE_VALUE)
            return
        }

        // set voice
        val requestedVoiceName = prefs.getCustomParam(AppPreferencesHelper.KEY_DEFAULT_TTS_VOICE, AppPreferencesHelper.DEFAULT_TTS_VOICE_VALUE)
        val matchingVoice = tts?.voices?.firstOrNull { it.name == requestedVoiceName }

        if (matchingVoice != null) {
            tts?.voice = matchingVoice
        } else {
            Log.w("TTS", "Requested voice not found, using default")
        }

        tts?.setSpeechRate(speed/10f)
        tts?.setPitch(pitch/10f)
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}

