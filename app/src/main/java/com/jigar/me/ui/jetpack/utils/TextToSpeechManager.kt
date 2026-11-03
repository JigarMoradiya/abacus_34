package com.jigar.me.ui.jetpack.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.annotation.StringRes
import com.jigar.me.R
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.utils.LanguageManager
import com.jigar.me.utils.TtsLocale
import com.jigar.me.utils.extensions.convertNumberToWords
import com.jigar.me.utils.withLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TextToSpeechManager @Inject constructor(
    @ApplicationContext private val context: Context,
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
                Handler(Looper.getMainLooper()).post {
                    utteranceId?.let { id ->
                        utteranceCallbacks[id]?.invoke(id)
                        utteranceCallbacks.remove(id)
                    }
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


    // ---- what is said: the words follow the language of the voice, so a French voice never reads English words

    /** The app language whose words the voice speaks now: the voice's language when the app has it, else English. */
    fun speechTag(): String =
        LanguageManager.tagForVoice(TtsLocale.current(prefs.getCustomParam(AppPreferencesHelper.KEY_DEFAULT_TTS_LANGUAGE, ""), LanguageManager.currentTag()))

    /** [n] in words in the voice's language. */
    fun words(n: Int): String = context.convertNumberToWords(n, speechTag())

    /** A string of the app in the voice's language, e.g. `phrase(R.string.speak_multiply_by, a, b)`. */
    fun phrase(@StringRes id: Int, vararg args: Any): String = context.withLanguage(speechTag()).getString(id, *args)

    /** "eighteen times seven equals one hundred twenty six" in the voice's language. */
    fun tableLine(table: Int, i: Int): String = phrase(R.string.speak_times_equals, words(table), words(i), words(table * i))

    fun applySettings() {
        if (!ready) return   // the engine is still starting: its start-up callback applies the settings


        // the voice the user picked (read in every form it was ever saved), else the usual voice of the app language
        val locale = TtsLocale.current(prefs.getCustomParam(AppPreferencesHelper.KEY_DEFAULT_TTS_LANGUAGE, ""), LanguageManager.currentTag())
        val speed = prefs.getDefaultTTSSpeed()
        val pitch = prefs.getDefaultTTSPitch()

        // Set language
        val langResult = tts?.setLanguage(locale)

        if (langResult == TextToSpeech.LANG_MISSING_DATA ||
            langResult == TextToSpeech.LANG_NOT_SUPPORTED
        ) {
            Log.e("TTS", "Language not supported: $locale")
            tts?.language = TtsLocale.defaultFor("en")
            return
        }

        // set voice
//        val requestedVoiceName = prefs.getCustomParam(AppPreferencesHelper.KEY_DEFAULT_TTS_VOICE, AppPreferencesHelper.DEFAULT_TTS_VOICE_VALUE)
//        tts?.voices
//            ?.firstOrNull { it.name == requestedVoiceName }
//            ?.let { tts?.voice = it }

        tts?.setSpeechRate(speed/10f)
        tts?.setPitch(pitch/10f)
    }

    fun stop() {
        tts?.stop()
        utteranceCallbacks.clear()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}

