package com.jigar.me.ui.view.ab

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A round glossy badge with a symbol: the hero of a setup screen. [size] is the badge diameter (a 3D edge hangs a little
 * below it, so the view is a bit taller than [size]).
 */
@Composable
fun AbHeroBadge(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    accent: Color? = null,
    size: Dp = 64.dp,
) {
    val colors = LocalAbColors.current
    val tone = accent ?: colors.brandPrimary
    val edge = tone.abDarkened(0.3f)
    val lit = tone.abBlended(Color.White, 0.25f)
    val rim = Color.White.copy(alpha = if (colors.isDark) 0.75f else 0.95f)
    Box(modifier.padding(bottom = size * 0.06f).size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val d = this.size.minDimension
            val r = d / 2f
            val centre = Offset(this.size.width / 2f, this.size.height / 2f)
            drawCircle(edge, r, centre + Offset(0f, d * 0.06f))
            // glossy face: the light comes from the upper left
            drawCircle(Brush.radialGradient(listOf(lit, tone), center = Offset(d * 0.35f, d * 0.2f), radius = d), r, centre)
            val ring = maxOf(2.5.dp.toPx(), d * 0.045f)
            drawCircle(rim, r - ring / 2f, centre, style = Stroke(ring))
        }
        val glyph = size * 0.55f
        // hard 0-blur drop shadow of the symbol
        Icon(icon, null, Modifier.size(glyph).offset(y = size * 0.02f), tint = Color.Black.copy(alpha = 0.2f))
        Icon(icon, null, Modifier.size(glyph), tint = Color.White)
    }
}

/** "Select exam types": small and calm above a row of cards. */
@Composable
fun AbSectionLabel(text: String, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    Text(text, modifier, style = AbTypography.label(s), color = colors.textSecondary)
}

/**
 * A selectable card: a big [symbol], an [icon] or a row of [stars] over a short title. Selected = filled with its
 * [accent]. A locked card is dimmed and carries a little padlock badge on its top-right corner (it stays tappable: the
 * caller decides what a tap on it does, usually the paywall).
 *
 * @param width the card width (the caller lays a row of equal cards out).
 * @param stars 1..3 = that many gold-style stars lit out of three (a difficulty).
 */
@Composable
fun AbSelectCard(
    title: String,
    accent: Color,
    isSelected: Boolean,
    width: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    symbol: String? = null,
    icon: ImageVector? = null,
    stars: Int? = null,
    isLocked: Boolean = false,
    /** A shorter card (the Exam home): smaller icon row and less vertical padding. */
    compact: Boolean = false,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val face = if (isSelected) accent else colors.bgSecondary
    val edge = if (isSelected) accent.abDarkened(0.3f) else colors.outline
    val ink = if (isSelected) Color.White else colors.textPrimary
    val shape = RoundedCornerShape(AbShapes.md * s)

    Box(modifier, contentAlignment = Alignment.TopEnd) {
        Column(
            Modifier
                .alpha(if (isLocked) 0.65f else 1f)
                .abSquash(
                    face = face, edge = edge, stroke = null,
                    cornerRadius = AbShapes.md * s, edgeHeight = 4.dp * s,
                    sound = AbSound.Button, onClick = onClick,
                )
                .width(width)
                .border(2.dp, if (isSelected) Color.White.copy(alpha = 0.6f) else colors.outline, shape)
                .padding(vertical = (if (compact) 4f else 8f).dp * s),
            verticalArrangement = Arrangement.spacedBy(2.dp * s),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (symbol != null) {
                Box(Modifier.height((if (compact) 24f else 32f).dp * s), contentAlignment = Alignment.Center) {
                    Text(symbol, style = AbTypography.fixed(28f * s, FontWeight.ExtraBold), color = if (isSelected) Color.White else accent, maxLines = 1)
                }
            }
            if (icon != null) {
                Box(Modifier.height((if (compact) 24f else 32f).dp * s), contentAlignment = Alignment.Center) {
                    Icon(icon, null, Modifier.size((20f * AB_SF_TO_VECTOR).dp * s), tint = if (isSelected) Color.White else accent)
                }
            }
            if (stars != null) {
                // the star vector has its own padding, which stands in for the 2 pt gap between the stars of the iOS card
                Row(Modifier.height((if (compact) 24f else 32f).dp * s), verticalAlignment = Alignment.CenterVertically) {
                    for (i in 0 until 3) {
                        Icon(
                            Icons.Rounded.Star, null, Modifier.size((14f * AB_SF_TO_VECTOR).dp * s),
                            tint = if (isSelected) Color.White else if (i < stars) accent else colors.outline,
                        )
                    }
                }
            }
            Box(Modifier.fillMaxWidth().height(16.dp * s), contentAlignment = Alignment.Center) {
                AbShrinkText(
                    text = title,
                    style = AbTypography.fixed(13f * s, FontWeight.ExtraBold),
                    color = ink,
                    minScale = 0.6f,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                )
            }
        }

        if (isLocked) {
            // hard-coded orange in the iOS kit too
            Box(
                Modifier
                    .offset(x = 5.dp * s, y = (-5).dp * s)
                    .size(20.dp * s)
                    .background(Color(0xFFFF9800), CircleShape)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.Lock, null, Modifier.size((9f * AB_SF_TO_VECTOR).dp * s), tint = Color.White)
            }
        }
    }
}
