package com.jigar.me.ui.view.home.common_ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.jigar.me.ui.jetpack.utils.TextToSpeechManager

val LocalTextToSpeechManager = staticCompositionLocalOf<TextToSpeechManager> {
    error("TextToSpeechManager not provided")
}
