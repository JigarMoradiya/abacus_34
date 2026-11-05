package com.jigar.me.ui.view.ab

import com.jigar.me.ui.view.ab.abReadable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

/**
 * A coloured card with a glossy gradient face, a faint rod-and-beads backdrop, a white rim and a 3D edge. Fills whatever
 * size the caller gives it (it is also used with no content as a plain background: `AbHeroPanel(accent, Modifier.fillMaxSize(), padding = 0.dp)`);
 * the edge adds its own height at the bottom. Mirrors `AbHeroPanel` in `AbPanels.swift`.
 */
@Composable
fun AbHeroPanel(
    accent: Color,
    modifier: Modifier = Modifier,
    padding: Dp = 20.dp,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val tone = accent.abReadable() // a very light accent would not hold white text
    val edgeH = 6.dp * s
    val shape = RoundedCornerShape(AbShapes.lg * s)
    val edgeColor = tone.abDarkened(0.32f)
    Box(
        modifier
            .padding(bottom = edgeH)
            .drawBehind {
                val r = min((AbShapes.lg * s).toPx(), size.minDimension / 2f)
                drawRoundRect(edgeColor, topLeft = Offset(0f, edgeH.toPx()), size = size, cornerRadius = CornerRadius(r, r))
            },
    ) {
        // face: gradient, faint rods and beads, glossy highlight
        Box(Modifier.matchParentSize().clip(shape)) {
            Canvas(Modifier.matchParentSize()) {
                drawRect(Brush.verticalGradient(listOf(tone.abBlended(Color.White, 0.2f), tone, tone.abDarkened(0.12f))))
            }
            AbRodBackdrop(Modifier.matchParentSize().alpha(0.7f))
            Canvas(Modifier.matchParentSize()) {
                val ew = size.width * 1.1f
                val eh = size.height * 0.55f
                val cx = size.width * 0.5f - size.width * 0.25f
                val cy = size.height * 0.5f - size.height * 0.32f
                drawOval(
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.30f), Color.White.copy(alpha = 0f)),
                        startY = cy - eh / 2f, endY = cy + eh / 2f,
                    ),
                    topLeft = Offset(cx - ew / 2f, cy - eh / 2f), size = Size(ew, eh),
                )
            }
        }
        Box(Modifier.matchParentSize().border(2.dp, Color.White.copy(alpha = if (colors.isDark) 0.2f else 0.45f), shape))
        Box(Modifier.padding(padding * s), content = content)
    }
}

/**
 * A calm surface for the abacus board or a big number: white in light, the elevated navy in dark, outlined and softly
 * shadowed. Mirrors `AbGlassPanel`.
 */
@Composable
fun AbGlassPanel(
    modifier: Modifier = Modifier,
    padding: Dp = 0.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val shape = RoundedCornerShape(AbShapes.lg * s)
    val shadowTint = colors.outline.copy(alpha = if (colors.isDark) 0.3f else 0.9f)
    Box(
        modifier
            .shadow(12.dp * s, shape, clip = false, ambientColor = shadowTint, spotColor = shadowTint)
            .background(colors.bgSecondary.copy(alpha = if (colors.isDark) 0.92f else 0.88f), shape)
            .border(2.dp, colors.outline, shape)
            .padding(padding * s),
        content = content,
    )
}

/** The translucent text box inside a hero panel. Mirrors `AbInsetBox`. */
@Composable
fun AbInsetBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val s = LocalAbScale.current.factor
    val shape = RoundedCornerShape(AbShapes.md * s)
    Box(
        modifier
            .background(Color.White.copy(alpha = 0.18f), shape)
            .border(1.5.dp, Color.White.copy(alpha = 0.22f), shape)
            .padding(12.dp * s),
        content = content,
    )
}

/**
 * A pill button for use on a hero panel. [filled] is the main action (white face, accent text); otherwise a see-through
 * white pill with white text. Same press feel as every kit button. Mirrors `AbPanelButton`.
 */
@Composable
fun AbPanelButton(
    label: String,
    accent: Color,
    modifier: Modifier = Modifier,
    filled: Boolean = true,
    enabled: Boolean = true,
    sound: AbSound? = AbSound.Button,
    onClick: () -> Unit,
) {
    val s = LocalAbScale.current.factor
    // the see-through button is a dark glass: white text on a pale face is hard to read on the bright panels
    val face = if (filled) Color.White else Color.Black.copy(alpha = 0.28f)
    val edge = if (filled) Color(0xFFC9CEE6) else Color.Black.copy(alpha = 0.40f)
    Box(
        modifier
            .alpha(if (enabled) 1f else 0.55f)
            .abSquash(
                face = face, edge = edge, stroke = null, cornerRadius = AbShapes.pill, edgeHeight = 5.dp * s,
                enabled = enabled, sound = sound, onClick = onClick,
            )
            .heightIn(min = 40.dp * s)
            .padding(horizontal = 20.dp * s),
        contentAlignment = Alignment.Center,
    ) {
        AbMinScaleText(
            text = label,
            style = AbTypography.body(s).copy(fontWeight = FontWeight.ExtraBold),
            color = if (filled) accent.abDarkened(0.2f).abReadable(4.0f) else Color.White,
            minScale = 0.7f,
            maxLines = 2,
        )
    }
}
