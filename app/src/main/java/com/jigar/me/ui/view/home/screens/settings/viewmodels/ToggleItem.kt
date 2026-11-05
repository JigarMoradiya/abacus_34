package com.jigar.me.ui.view.home.screens.settings.viewmodels

import androidx.compose.ui.graphics.vector.ImageVector

data class ToggleItem(
    val icon: ImageVector,
    val label: String,
    val isOn: Boolean,
    val onToggle: (Boolean) -> Unit
)
