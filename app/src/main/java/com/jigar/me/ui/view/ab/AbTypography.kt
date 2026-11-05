package com.jigar.me.ui.view.ab

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.jigar.me.R

/**
 * Baloo 2 type ramp for the Ab kit. The font ships as four static weights (res/font/baloo2_*.ttf, Latin subset of the
 * variable font the iOS app uses), so every API level picks the right weight.
 *
 * Baloo's own line box is tall, which spreads stacked lines apart. Every style here uses an explicit line height with
 * the extra space trimmed evenly above and below, so lines sit tight without clipping glyphs.
 */
object AbTypography {
    val Baloo = FontFamily(
        Font(R.font.baloo2_medium, FontWeight.Normal),
        Font(R.font.baloo2_medium, FontWeight.Medium),
        Font(R.font.baloo2_semibold, FontWeight.SemiBold),
        Font(R.font.baloo2_bold, FontWeight.Bold),
        Font(R.font.baloo2_extrabold, FontWeight.ExtraBold),
        Font(R.font.baloo2_extrabold, FontWeight.Black),
    )

    private val tightLine = LineHeightStyle(alignment = LineHeightStyle.Alignment.Center, trim = LineHeightStyle.Trim.Both)

    /** Exact size, no scale factor: for a glyph inside a fixed-size tile (keypad key, bead number). */
    fun fixed(size: Float, weight: FontWeight = FontWeight.ExtraBold, tabular: Boolean = false): TextStyle = TextStyle(
        fontFamily = Baloo,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = (size * 1.12f).sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
        lineHeightStyle = tightLine,
        fontFeatureSettings = if (tabular) "tnum" else null,
    )

    private fun scaled(size: Float, weight: FontWeight, s: Float, tabular: Boolean = false) = fixed(size * s, weight, tabular)

    fun display(s: Float = 1f) = scaled(40f, FontWeight.ExtraBold, s)
    fun title(s: Float = 1f) = scaled(28f, FontWeight.Bold, s)
    fun headline(s: Float = 1f) = scaled(22f, FontWeight.Bold, s)
    fun body(s: Float = 1f) = scaled(17f, FontWeight.Medium, s)
    fun label(s: Float = 1f) = scaled(14f, FontWeight.SemiBold, s)
    fun number(s: Float = 1f) = scaled(34f, FontWeight.ExtraBold, s, tabular = true)
    fun numberLarge(s: Float = 1f) = scaled(48f, FontWeight.ExtraBold, s, tabular = true)

    /**
     * Text with `<b>bold</b>` runs in one kit font: the same size throughout, heavier where bold (message text from the
     * shared strings carries this markup). Every other tag is dropped.
     */
    fun markup(
        source: String,
        regular: FontWeight = FontWeight.SemiBold,
        bold: FontWeight = FontWeight.ExtraBold,
    ): AnnotatedString = buildAnnotatedString {
        var rest = stripTags(source)
        while (rest.isNotEmpty()) {
            val open = rest.indexOf("<b>")
            val close = rest.indexOf("</b>")
            if (open >= 0 && close > open) {
                withStyle(SpanStyle(fontWeight = regular)) { append(rest.substring(0, open)) }
                withStyle(SpanStyle(fontWeight = bold)) { append(rest.substring(open + 3, close)) }
                rest = rest.substring(close + 4)
            } else {
                withStyle(SpanStyle(fontWeight = regular)) { append(rest) }
                break
            }
        }
    }

    private fun stripTags(markup: String): String {
        var out = markup.replace("<br>", "\n").replace("<br/>", "\n").replace("\\n", "\n")
        while (true) {
            val open = out.indexOf("<font")
            if (open < 0) break
            val close = out.indexOf('>', open)
            if (close < 0) break
            out = out.removeRange(open, close + 1)
        }
        return out.replace("</font>", "")
    }
}
