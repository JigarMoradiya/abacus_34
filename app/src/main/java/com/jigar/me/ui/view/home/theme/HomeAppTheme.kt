package com.jigar.me.ui.view.home.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import androidx.compose.ui.text.style.TextDirection

val PrimaryBlue = Color(0xFF182480)

private val LightColors = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    secondary = Color(0xFF2132B6),

    background = Color.White,
    surface = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

val customFonts = FontFamily(
    Font(R.font.font_regular, FontWeight.Normal),
    Font(R.font.font_medium, FontWeight.Medium),
    Font(R.font.font_semibold, FontWeight.SemiBold),
    Font(R.font.font_bold, FontWeight.Bold),
    Font(R.font.font_extra_bold, FontWeight.ExtraBold),
)

val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = customFonts, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 38.sp, textDirection = TextDirection.ContentOrLtr),
    displayMedium = TextStyle(fontFamily = customFonts, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 34.sp, textDirection = TextDirection.ContentOrLtr),
    displaySmall = TextStyle(fontFamily = customFonts, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 30.sp, textDirection = TextDirection.ContentOrLtr),

    headlineLarge = TextStyle(fontFamily = customFonts, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp, textDirection = TextDirection.ContentOrLtr),
    headlineMedium = TextStyle(fontFamily = customFonts, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 26.sp, textDirection = TextDirection.ContentOrLtr),
    headlineSmall = TextStyle(fontFamily = customFonts, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp, textDirection = TextDirection.ContentOrLtr),

    titleLarge = TextStyle(fontFamily = customFonts, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp, textDirection = TextDirection.ContentOrLtr),
    titleMedium = TextStyle(fontFamily = customFonts, fontWeight = FontWeight.Medium, fontSize = 18.sp, lineHeight = 24.sp, textDirection = TextDirection.ContentOrLtr),
    titleSmall = TextStyle(fontFamily = customFonts, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp, textDirection = TextDirection.ContentOrLtr),

    bodyLarge = TextStyle(fontFamily = customFonts, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 22.sp, textDirection = TextDirection.ContentOrLtr),
    bodyMedium = TextStyle(fontFamily = customFonts, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp, textDirection = TextDirection.ContentOrLtr),
    bodySmall = TextStyle(fontFamily = customFonts, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 18.sp, textDirection = TextDirection.ContentOrLtr),

    labelLarge = TextStyle(fontFamily = customFonts, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp, textDirection = TextDirection.ContentOrLtr),
    labelMedium = TextStyle(fontFamily = customFonts, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp, textDirection = TextDirection.ContentOrLtr),
    labelSmall = TextStyle(fontFamily = customFonts, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 14.sp, textDirection = TextDirection.ContentOrLtr)
)

@Composable
fun MyApplicationTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
    ) {
        // see AbTheme: numbers and math fragments read left to right in an Arabic screen
        androidx.compose.material3.ProvideTextStyle(
            androidx.compose.ui.text.TextStyle(textDirection = androidx.compose.ui.text.style.TextDirection.ContentOrLtr),
            content,
        )
    }
}
