package com.jigar.me.ui.view.jetpack.abacus_base.viewmodel


import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.jigar.me.data.local.data.RodMovement
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusCalculations
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import dagger.hilt.android.qualifiers.ApplicationContext

open class BaseAbacusViewModel(
    numberOfColumns: Int, @ApplicationContext private val context: Context, private val prefs: AppPreferencesHelper
) : ViewModel() {

    // ------------ TTS CORE ------------
    private var textToSpeech: TextToSpeech? = null
    private var pendingSpeechText: String? = null
    private var ttsReady = false

    init {
        initTextToSpeech()
    }

    // Text To Speech
    private fun initTextToSpeech() {
        if (textToSpeech != null) return   // prevent double init
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true
                textToSpeech?.let {
                    CommonUtils.applySpeechSettings(prefs, it)
                }
                // 🔥 SPEAK PENDING TEXT
                pendingSpeechText?.let {
                    textToSpeech?.speak(it, TextToSpeech.QUEUE_FLUSH, null, null)
                    pendingSpeechText = null
                }
            }
        }
    }
    fun speakOut(text: String) {
        if (!ttsReady) {
            pendingSpeechText = text
            initTextToSpeech()
            return
        }

        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }
    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }

        // Shared abacus core state
    val abacusCalc: AbacusCalculations = AbacusCalculations(numberOfColumns = numberOfColumns)

    // get selected theme
    val selectedTheme : String
        get() = prefs.getCustomParam(AppConstants.Settings.Theam, AppConstants.Settings.theam_Default)

    // sound setting
    val isBeadSoundEnabled: Boolean
        get() = prefs.getCustomParamBoolean(AppConstants.Settings.Setting_sound, true)

    // sound setting
    val isDisplayCurrentAbacusInput: Boolean
        get() = prefs.getCustomParamBoolean(AppConstants.Settings.Setting_display_abacus_number, true)

    val isDisplayHelpMessage: Boolean
        get() = prefs.getCustomParamBoolean(AppConstants.Settings.Setting_display_help_message, true)

    var rodMovements by mutableStateOf(listOf<RodMovement>())
        protected set

    var showDirectionHints by mutableStateOf(false)
        protected set

    fun updateRodMovements(list: List<RodMovement>) {
        rodMovements = list
    }

    fun updateShowDirectionHints(show: Boolean) {
        showDirectionHints = show
    }
}
