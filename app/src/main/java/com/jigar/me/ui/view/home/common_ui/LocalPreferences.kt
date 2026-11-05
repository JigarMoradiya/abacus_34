package com.jigar.me.ui.view.home.common_ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.jigar.me.data.pref.AppPreferencesHelper

val LocalPreferencesHelper = staticCompositionLocalOf<AppPreferencesHelper> {
    error("PreferencesHelper not provided")
}
