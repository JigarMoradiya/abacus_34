package com.jigar.me.ui.view.jetpack.utils

import android.content.Context
import android.speech.tts.TextToSpeech
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
    private var pendingText: String? = null

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ready = true
                applySettings()

                pendingText?.let {
                    speak(it)
                    pendingText = null
                }
            }
        }
    }

    fun speak(text: String) {
        Log.e("jigarTTS","text = "+text)
        if (!ready) {
            pendingText = text
            return
        }
        Log.e("jigarTTS","text text = "+text)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun stop() {
        tts?.stop()
    }

    fun applySettings() {
        val currentLanguageJson =
            prefs.getCustomParam(AppPreferencesHelper.KEY_DEFAULT_TTS_LANGUAGE, "")

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

