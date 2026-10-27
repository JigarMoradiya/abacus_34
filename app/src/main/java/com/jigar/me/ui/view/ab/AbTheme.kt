package com.jigar.me.ui.view.ab

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.data.pref.PreferencesHelper
import com.jigar.me.ui.view.home.theme.AppTypography

/**
 * Kit-wide size factor. Kit sizes are authored for a phone; tablets scale up (same 1 / 1.35 / 1.5 steps as the iOS kit,
 * and the same tablet classes as `AppDimens`).
 */
data class AbScale(val factor: Float, val isTablet: Boolean) {
    companion object {
        fun current(): AbScale = when {
            DeviceInfo.isLargeTablet -> AbScale(1.5f, true)
            DeviceInfo.isTablet -> AbScale(1.35f, true)
            else -> AbScale(1f, false)
        }
    }
}

val LocalAbColors = staticCompositionLocalOf { AbColors.Light }
val LocalAbScale = staticCompositionLocalOf { AbScale(1f, false) }

/**
 * True under [AbTheme]. Components shared with the Game Zone (login sheet, paywall sheet, bottom sheets, popups) draw the
 * kit look only when this is set, so the games keep their own look until they get their own pass.
 */
val LocalAbThemed = staticCompositionLocalOf { false }

/** Persists the in-app theme choice. Never consults the device's dark mode: default is Light. */
object AbThemeController {
    private const val KEY = "ab_theme_mode"

    var mode by mutableStateOf(AbThemeMode.Light)
        private set

    private var prefs: PreferencesHelper? = null

    /** Call once early (HomeActivity) with the app preferences. */
    fun init(preferences: PreferencesHelper) {
        prefs = preferences
        mode = AbThemeMode.from(preferences.getCustomParam(KEY, AbThemeMode.Light.name))
    }

    fun set(newMode: AbThemeMode) {
        prefs?.setCustomParam(KEY, newMode.name)
        mode = newMode
    }
}

/** Applies the kit theme from the in-app toggle. Use at the root of every non-Game-Zone screen tree. */
@Composable
fun AbTheme(content: @Composable () -> Unit) = AbTheme(AbThemeController.mode, content)

/** Pins one mode regardless of the in-app toggle (gallery previews, the Game Zone while it has no dark design). */
@Composable
fun AbTheme(forcing: AbThemeMode, content: @Composable () -> Unit) {
    val colors = if (forcing == AbThemeMode.Dark) AbColors.Dark else AbColors.Light
    // Stock Material widgets that still appear inside a kit screen (text selection handles, ripples) follow the same mode.
    val scheme = if (colors.isDark) {
        darkColorScheme(primary = colors.brandPrimary, onPrimary = androidx.compose.ui.graphics.Color.White,
            background = colors.bgPrimary, surface = colors.bgSecondary,
            onBackground = colors.textPrimary, onSurface = colors.textPrimary)
    } else {
        lightColorScheme(primary = colors.brandPrimary, onPrimary = androidx.compose.ui.graphics.Color.White,
            background = colors.bgPrimary, surface = colors.bgSecondary,
            onBackground = colors.textPrimary, onSurface = colors.textPrimary)
    }
    CompositionLocalProvider(
        LocalAbColors provides colors,
        LocalAbScale provides AbScale.current(),
        LocalAbThemed provides true,
    ) {
        MaterialTheme(colorScheme = scheme, typography = AppTypography, content = content)
    }
}
