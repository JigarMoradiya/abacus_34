package com.jigar.me.ui.view.ab

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.pow

/**
 * Design tokens for the "Ab" UI kit (the kids-game restyle of every non-Game-Zone screen). Mirrors the iOS kit
 * (`AbTokens.swift`) value for value. Colours come from the theme (`LocalAbColors`), never from statics, so the in-app
 * Light/Dark toggle repaints live. Kept separate from `KidsActionButton` / `getButtonColors`, which the Math Game Zone
 * still uses.
 */
enum class AbThemeMode {
    Light, Dark;

    companion object {
        fun from(key: String?): AbThemeMode = entries.firstOrNull { it.name.equals(key, ignoreCase = true) } ?: Light
    }
}

private fun rgb(hex: Long): Color = Color(
    red = ((hex shr 16) and 0xFF) / 255f,
    green = ((hex shr 8) and 0xFF) / 255f,
    blue = (hex and 0xFF) / 255f,
)

@Immutable
data class AbColors(
    val isDark: Boolean,
    val bgPrimary: Color, val bgSecondary: Color, val bgElevated: Color, val surfaceTint: Color,
    val textPrimary: Color, val textSecondary: Color,
    /** Brand indigo-blue (primary actions) and sky blue (secondary actions), each with its 3D edge colour. */
    val brandPrimary: Color, val brandPrimaryDark: Color, val brandSecondary: Color, val brandSecondaryDark: Color,
    val accentPink: Color, val accentGreen: Color, val accentOrange: Color, val accentPurple: Color, val accentYellow: Color,
    val danger: Color, val outline: Color, val overlay: Color,
    /** The place-value rainbow on the Free Mode rods, ones first: orange, lime, green, dark teal, teal, blue, indigo. */
    val placeValue: List<Color>,
) {
    val overlayScrim: Color get() = overlay.copy(alpha = if (isDark) 0.62f else 0.5f)

    companion object {
        val Light = AbColors(
            isDark = false,
            bgPrimary = rgb(0xF7F2FF), bgSecondary = rgb(0xFFFFFF), bgElevated = rgb(0xFFFFFF), surfaceTint = rgb(0xECE6FF),
            textPrimary = rgb(0x1E2470), textSecondary = rgb(0x5F64A0),
            brandPrimary = rgb(0x3F5CE0), brandPrimaryDark = rgb(0x2A3FB0), brandSecondary = rgb(0x42A5F5), brandSecondaryDark = rgb(0x1E7FD0),
            accentPink = rgb(0xF5538A), accentGreen = rgb(0x2FC48D), accentOrange = rgb(0xFB8C00), accentPurple = rgb(0x7E5CF0), accentYellow = rgb(0xFFC53D),
            danger = rgb(0xFF5C5C), outline = rgb(0xDDD5F5), overlay = rgb(0x1E2470),
            placeValue = listOf(0xF57C00L, 0xAFB42BL, 0x388E3CL, 0x00796BL, 0x0097A7L, 0x1976D2L, 0x303F9FL).map(::rgb),
        )

        val Dark = AbColors(
            isDark = true,
            bgPrimary = rgb(0x14123A), bgSecondary = rgb(0x221F5C), bgElevated = rgb(0x2B2870), surfaceTint = rgb(0x35307F),
            textPrimary = rgb(0xF4F1FF), textSecondary = rgb(0xBDB8F0),
            brandPrimary = rgb(0x6B86FF), brandPrimaryDark = rgb(0x3F51D9), brandSecondary = rgb(0x5BB8FF), brandSecondaryDark = rgb(0x1E7FD0),
            accentPink = rgb(0xFF74A3), accentGreen = rgb(0x4CE0A6), accentOrange = rgb(0xFFA033), accentPurple = rgb(0xA58CFF), accentYellow = rgb(0xFFD166),
            danger = rgb(0xFF7B7B), outline = rgb(0x4A4190), overlay = rgb(0x000000),
            placeValue = listOf(0xFF9A3CL, 0xCDD43AL, 0x5BBF60L, 0x2BB5A0L, 0x2CC0D3L, 0x4A9BF0L, 0x7C8CF0L).map(::rgb),
        )
    }
}

object AbShapes {
    val sm = 12.dp
    val md = 18.dp
    val lg = 26.dp
    /** Big enough that a RoundedCornerShape becomes a true capsule at any height. */
    val pill = 999.dp
    val buttonEdge = 6.dp
}

object AbMotion {
    const val FAST = 120
    const val NORMAL = 220
    const val SLOW = 360
    const val CELEBRATE = 900
    const val PRESS_SCALE = 0.94f
}

/** Sinks toward black by [amount] (sRGB multiply): the 3D edge under a face when no `*Dark` token exists. */
fun Color.abDarkened(amount: Float = 0.22f): Color =
    Color(red * (1 - amount), green * (1 - amount), blue * (1 - amount), 1f)

private fun toLinear(c: Float): Float = if (c <= 0.04045f) c / 12.92f else ((c + 0.055f) / 1.055f).pow(2.4f)
private fun toSrgb(c: Float): Float = if (c <= 0.0031308f) c * 12.92f else 1.055f * c.pow(1f / 2.4f) - 0.055f

/** Linear-light blend toward [other] (`fraction` 0 = this, 1 = other), full opacity. Same maths as the iOS kit. */
fun Color.abBlended(other: Color, fraction: Float): Color {
    val t = fraction.coerceIn(0f, 1f)
    fun mix(a: Float, b: Float): Float {
        val la = toLinear(a)
        val lb = toLinear(b)
        return toSrgb(la + (lb - la) * t)
    }
    return Color(mix(red, other.red), mix(green, other.green), mix(blue, other.blue), 1f)
}

/** Relative luminance (0 black ... 1 white) of the colour as drawn in sRGB. */
val Color.abLuminance: Float
    get() = 0.2126f * toLinear(red) + 0.7152f * toLinear(green) + 0.0722f * toLinear(blue)

/**
 * Text colour that reads on top of this colour: white when the contrast against white is good, otherwise a very dark
 * shade of this same colour (so it still feels part of the same family, not a flat black).
 */
val Color.abReadableInk: Color
    get() = if (1.05f / (abLuminance + 0.05f) >= 3.0f) Color.White else abDarkened(0.82f)
