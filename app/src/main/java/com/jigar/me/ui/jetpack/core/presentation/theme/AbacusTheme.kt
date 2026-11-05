package com.jigar.me.ui.jetpack.core.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun AbacusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = lightColorScheme(
        primary = ColorPrimary,
        onPrimary = White,

        secondary = ColorAccent,
        onSecondary = White,

        background = White,
        onBackground = Black,

        surface = White,
        onSurface = Black,

        surfaceVariant = White,
        onSurfaceVariant = Black
    )

    MaterialTheme(colorScheme = colorScheme, content = content)
}
