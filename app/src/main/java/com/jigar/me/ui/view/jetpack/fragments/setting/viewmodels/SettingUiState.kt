package com.jigar.me.ui.view.jetpack.fragments.setting.viewmodels

data class SettingUiState(
    val error: Int? = null,
    val previewKey: Int = 0,

    val displayNumber: Boolean = true,
    val displayHint: Boolean = true,
    val displayDirection: Boolean = true,
    val leftHanded: Boolean = true,
    val sumSound: Boolean = true,
    val beadSound: Boolean = true,

    val musicVolume: Int = 10
)