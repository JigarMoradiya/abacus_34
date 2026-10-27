package com.jigar.me.ui.view.ab

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

enum class AbTone { Primary, Secondary, Success, Warning, Pink, Purple, Danger, Ghost }
enum class AbSize { Compact, Small, Medium, Large }

data class AbTonePalette(val face: Color, val edge: Color?, val stroke: Color?, val content: Color) {
    companion object {
        fun of(tone: AbTone, c: AbColors): AbTonePalette = when (tone) {
            AbTone.Primary -> AbTonePalette(c.brandPrimary, c.brandPrimaryDark, null, Color.White)
            AbTone.Secondary -> AbTonePalette(c.brandSecondary, c.brandSecondaryDark, null, Color.White)
            AbTone.Success -> AbTonePalette(c.accentGreen, c.accentGreen.abDarkened(0.28f), null, Color.White)
            AbTone.Warning -> AbTonePalette(c.accentOrange, c.accentOrange.abDarkened(0.28f), null, Color.White)
            AbTone.Pink -> AbTonePalette(c.accentPink, c.accentPink.abDarkened(0.28f), null, Color.White)
            AbTone.Purple -> AbTonePalette(c.accentPurple, c.accentPurple.abDarkened(0.28f), null, Color.White)
            AbTone.Danger -> AbTonePalette(c.danger, c.danger.abDarkened(0.28f), null, Color.White)
            AbTone.Ghost -> AbTonePalette(Color.Transparent, null, c.outline, c.textPrimary)
        }

        /**
         * Soft on-palette disabled look (no alpha, which turns colours muddy on the dark background). The label keeps the
         * secondary text colour so it stays readable on the pale face.
         */
        fun disabled(tone: AbTone, c: AbColors): AbTonePalette {
            val base = of(tone, c)
            if (tone == AbTone.Ghost) return AbTonePalette(base.face, null, c.outline, c.textSecondary)
            val face = base.face.abBlended(c.surfaceTint, 0.62f)
            return AbTonePalette(
                face = face,
                // the edge is the face a touch darker, so it reads as the same material
                edge = base.edge?.let { face.abDarkened(if (c.isDark) 0.3f else 0.1f) },
                stroke = base.stroke,
                content = c.textSecondary,
            )
        }
    }
}

/**
 * Chunky pill with a darker 3D bottom edge; press drops the face onto the edge and squashes to 0.94.
 * Replaces `KidsActionButton` on non-Game-Zone screens.
 */
@Composable
fun AbButton(
    text: String,
    modifier: Modifier = Modifier,
    tone: AbTone = AbTone.Primary,
    size: AbSize = AbSize.Medium,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    painterIcon: Painter? = null,
    iconAtEnd: Boolean = false,
    fillWidth: Boolean = false,
    sound: AbSound? = AbSound.Button,
    onClick: () -> Unit,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val palette = if (enabled) AbTonePalette.of(tone, colors) else AbTonePalette.disabled(tone, colors)
    val style = when (size) {
        AbSize.Compact, AbSize.Small -> AbTypography.label(s)
        AbSize.Medium -> AbTypography.body(s).copy(fontWeight = FontWeight.Bold)
        AbSize.Large -> AbTypography.headline(s)
    }
    val minHeight = when (size) { AbSize.Compact -> 32; AbSize.Small -> 40; AbSize.Medium -> 52; AbSize.Large -> 64 }
    val hPad = when (size) { AbSize.Compact -> 12; AbSize.Small -> 16; AbSize.Medium -> 22; AbSize.Large -> 28 }
    val iconPx = when (size) { AbSize.Compact -> 13; AbSize.Small -> 16; else -> 22 }
    val edge = (if (size == AbSize.Compact) 3.5f else AbShapes.buttonEdge.value).dp * s

    Row(
        modifier = modifier
            .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier)
            .abSquash(
                face = palette.face, edge = palette.edge, stroke = palette.stroke,
                cornerRadius = AbShapes.pill, edgeHeight = edge, enabled = enabled, sound = sound, onClick = onClick,
            )
            .heightIn(min = minHeight.dp * s)
            .padding(horizontal = hPad.dp * s),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        @Composable fun glyph() {
            val modifierIcon = Modifier.size(iconPx.dp * s)
            when {
                painterIcon != null -> Icon(painterIcon, null, modifierIcon, tint = Color.Unspecified)
                icon != null -> Icon(icon, null, modifierIcon, tint = palette.content)
            }
        }
        val hasIcon = icon != null || painterIcon != null
        if (hasIcon && !iconAtEnd) { glyph(); Spacer(Modifier.width((if (size == AbSize.Compact) 5 else 8).dp * s)) }
        Text(text, style = style, color = palette.content, maxLines = 1, overflow = TextOverflow.Clip, textAlign = TextAlign.Center)
        if (hasIcon && iconAtEnd) { Spacer(Modifier.width((if (size == AbSize.Compact) 5 else 8).dp * s)); glyph() }
    }
}

/** Round chunky icon button (same 3D edge + squash as [AbButton]). */
@Composable
fun AbIconButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tone: AbTone = AbTone.Secondary,
    enabled: Boolean = true,
    sound: AbSound? = AbSound.Button,
    onClick: () -> Unit,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val palette = if (enabled) AbTonePalette.of(tone, colors) else AbTonePalette.disabled(tone, colors)
    Box(
        modifier = modifier
            .abSquash(palette.face, palette.edge, palette.stroke, AbShapes.pill, AbShapes.buttonEdge * s, enabled, sound, onClick)
            .size(48.dp * s),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, null, Modifier.size(22.dp * s), tint = palette.content)
    }
}

/** A heavy rounded chevron, drawn (not an icon font) so its weight matches the kit's chunky buttons. */
@Composable
fun AbChevron(color: Color, modifier: Modifier = Modifier, pointingLeft: Boolean = true) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            if (pointingLeft) { moveTo(w * 0.62f, h * 0.18f); lineTo(w * 0.32f, h * 0.5f); lineTo(w * 0.62f, h * 0.82f) }
            else { moveTo(w * 0.38f, h * 0.18f); lineTo(w * 0.68f, h * 0.5f); lineTo(w * 0.38f, h * 0.82f) }
        }
        drawPath(path, color, style = Stroke(width = w * 0.17f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/** THE back control of restyled screens: chevron in a chunky circle, plays the app's back sound. */
@Composable
fun AbBackButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val palette = AbTonePalette.of(AbTone.Primary, colors)
    Box(
        modifier = modifier
            .abSquash(palette.face, palette.edge, null, AbShapes.pill, AbShapes.buttonEdge * s, true, AbSound.Back, onClick)
            .size(48.dp * s),
        contentAlignment = Alignment.Center,
    ) {
        AbChevron(palette.content, Modifier.size(26.dp * s))
    }
}
