package com.jigar.me.ui.view.home.screens.settings.viewmodels

import android.speech.tts.Voice
import java.util.Locale

data class SettingUiState(
    val error: Int? = null,
    val previewKey: Int = 0,

    val displayNumber: Boolean = true,
    val displayHint: Boolean = true,
    val displayDirection: Boolean = true,
    val leftHanded: Boolean = true,
    val sumSound: Boolean = true,
    val beadSound: Boolean = true,
    val is7RodsMode: Boolean = false,

    val musicVolume: Int = 10,

    // Voice setting
    val languages: List<Locale> = emptyList(),
//    val voices: List<Voice> = emptyList(),
    val selectedLanguage: Locale? = null,
//    val selectedVoice: Voice? = null,
    val pitch: Int = 10,   // 0–20 (same as XML)
    val speed: Int = 9     // 0–20
)