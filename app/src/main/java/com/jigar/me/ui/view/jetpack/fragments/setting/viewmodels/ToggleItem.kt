package com.jigar.me.ui.view.jetpack.fragments.setting.viewmodels

import androidx.compose.ui.graphics.vector.ImageVector

data class ToggleItem(
    val icon: ImageVector,
    val label: String,
    val isOn: Boolean,
    val onToggle: (Boolean) -> Unit
)
