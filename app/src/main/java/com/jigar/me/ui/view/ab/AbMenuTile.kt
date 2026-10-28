package com.jigar.me.ui.view.ab

import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max

/**
 * A Home menu button: a big glossy round badge in the activity's colour with a custom-drawn icon, sparkles and a ribbon
 * carrying the name. Replaces the server-supplied picture.
 *
 * [size] is the square the tile may fill. [title] / [subtitle] are the already-localised texts (the English defaults of
 * [kind] are used when omitted). The iOS tile is not tappable by itself (its caller wraps it in a button); pass [onClick]
 * to get the kit press feel and click sound, or leave it null and make the tile clickable from outside.
 */
@Composable
fun AbMenuTile(
    kind: AbMenuKind,
    size: Dp,
    modifier: Modifier = Modifier,
    title: String = kind.title,
    subtitle: String = kind.subtitle,
    onClick: (() -> Unit)? = null,
) {
    val colors = LocalAbColors.current
    val density = LocalDensity.current
    val tone = kind.tones
    val sv = size.value
    val d = size * 0.86f                              // badge diameter
    val edge = max(5f, sv * 0.03f).dp
    val showSubtitle = sv >= 170f
    val edgePx = with(density) { edge.toPx() }
    val ringPx = with(density) { max(3f, sv * 0.022f).dp.toPx() }
    val isDark = colors.isDark

    Box(
        modifier
            .size(size)
            .then(if (onClick != null) Modifier.abSquash(onClick = onClick) else Modifier)
            .clearAndSetSemantics { contentDescription = title },
    ) {
        // Badge
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .padding(top = size * 0.01f)
                .size(d),
        ) {
            Canvas(Modifier.matchParentSize()) { drawBadge(tone, isDark, edgePx, ringPx) }

            // The icon, with the soft drop shadow SwiftUI's `.shadow(.black 0.22, radius size*0.02, y size*0.015)` gives it.
            // The shadow is the icon's silhouette, blurred and faded; the blur needs API 31, below that no shadow is drawn
            // (a hard, unblurred copy would look wrong).
            val shadowDy = with(density) { (size * 0.015f).toPx() }
            Box(
                Modifier
                    .align(Alignment.Center)
                    .offset(y = -(d * (if (showSubtitle) 0.07f else 0.06f)))
                    .size(d * 0.70f),
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    AbMenuIconSilhouette(
                        kind,
                        Modifier
                            .matchParentSize()
                            .graphicsLayer { translationY = shadowDy; alpha = 0.22f }
                            .blur(size * 0.01f, BlurredEdgeTreatment.Unbounded),
                    )
                }
                AbMenuIcon(kind, Modifier.matchParentSize())
            }
        }

        // Ribbon
        MenuRibbon(
            tone = tone,
            title = title,
            subtitle = subtitle,
            size = size,
            showSubtitle = showSubtitle,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

/** The badge: 3D edge, radial-gradient face, confetti, white ring and a soft gloss on the upper left. Fills a d x d box. */
private fun DrawScope.drawBadge(tone: AbMenuTones, isDark: Boolean, edgePx: Float, ringPx: Float) {
    val dPx = size.minDimension
    val r = dPx / 2f
    val centre = Offset(r, r)
    val disc = Path().apply { addOval(Rect(0f, 0f, dPx, dPx)) }

    // 3D edge under the badge
    drawCircle(color = tone.deep.abDarkened(0.34f), radius = r, center = Offset(r, r + edgePx))
    // face
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(tone.light, tone.deep),
            center = Offset(0.36f * dPx, 0.2f * dPx),
            radius = dPx * 0.95f,
        ),
        radius = r,
        center = centre,
    )
    clipPath(disc) { drawBadgeConfetti() }
    // white ring, drawn inside the badge (SwiftUI strokeBorder)
    drawCircle(
        color = Color.White.copy(alpha = if (isDark) 0.75f else 0.95f),
        radius = r - ringPx / 2f,
        center = centre,
        style = Stroke(width = ringPx),
    )
    // soft gloss on the upper left (clipped to the badge)
    val gw = dPx * 0.62f
    val gh = dPx * 0.34f
    val gcx = dPx / 2f - dPx * 0.14f
    val gcy = dPx / 2f - dPx * 0.32f
    clipPath(disc) {
        drawOval(
            brush = Brush.verticalGradient(
                colors = listOf(Color.White.copy(alpha = 0.42f), Color.White.copy(alpha = 0f)),
                startY = gcy - gh / 2f,
                endY = gcy + gh / 2f,
            ),
            topLeft = Offset(gcx - gw / 2f, gcy - gh / 2f),
            size = Size(gw, gh),
        )
    }
}

/** Tiny shapes scattered across a badge so the colour is not a flat disc: dots (kind 0) and little pluses (kind 1). */
private fun DrawScope.drawBadgeConfetti() {
    val w = size.width
    val h = size.height
    class Piece(val x: Float, val y: Float, val r: Float, val kind: Int)
    val pieces = listOf(
        Piece(0.18f, 0.30f, 0.020f, 0), Piece(0.80f, 0.22f, 0.024f, 1), Piece(0.88f, 0.52f, 0.016f, 0), Piece(0.12f, 0.58f, 0.018f, 1),
        Piece(0.70f, 0.82f, 0.020f, 0), Piece(0.30f, 0.86f, 0.016f, 1), Piece(0.52f, 0.10f, 0.016f, 0),
    )
    for (p in pieces) {
        val r = minOf(w, h) * p.r
        val cx = p.x * w
        val cy = p.y * h
        if (p.kind == 0) {
            drawCircle(color = Color.White.copy(alpha = 0.35f), radius = r, center = Offset(cx, cy))
        } else {
            val corner = CornerRadius(r * 0.3f)
            // one path with both bars, filled once, so the crossing is not drawn twice
            val plus = Path().apply {
                addRoundRect(RoundRect(Rect(cx - r * 0.3f, cy - r, cx + r * 0.3f, cy + r), corner))
                addRoundRect(RoundRect(Rect(cx - r, cy - r * 0.3f, cx + r, cy + r * 0.3f), corner))
            }
            drawPath(plus, Color.White.copy(alpha = 0.4f))
        }
    }
}

/** The name plate at the bottom of the tile (title, plus the subtitle on large tiles). */
@Composable
private fun MenuRibbon(
    tone: AbMenuTones,
    title: String,
    subtitle: String,
    size: Dp,
    showSubtitle: Boolean,
    modifier: Modifier,
) {
    val density = LocalDensity.current
    val sv = size.value
    val w = size * 0.94f
    val h = size * (if (showSubtitle) 0.24f else 0.15f)
    val titleSize = sv * (if (showSubtitle) 0.1f else 0.085f)
    val subtitleSize = sv * 0.064f
    val corner = with(density) { (size * 0.07f).toPx() }
    val edgeDy = with(density) { (size * 0.018f).toPx() }
    val borderPx = with(density) { max(2f, sv * 0.014f).dp.toPx() }
    val textMaxPx = with(density) { (w - size * 0.10f).toPx() }
    val titleShadow = Shadow(Color.Black.copy(alpha = 0.3f), Offset(0f, 1.5f * density.density), 0f)

    Box(
        modifier
            .size(w, h)
            .drawBehind {
                val radius = CornerRadius(corner)
                // 3D edge, face gradient, white border (inside the plate, like SwiftUI strokeBorder)
                drawRoundRect(tone.deep.abDarkened(0.38f), topLeft = Offset(0f, edgeDy), size = this.size, cornerRadius = radius)
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(tone.deep.abBlended(Color.White, 0.12f), tone.deep.abDarkened(0.1f)),
                        startY = 0f,
                        endY = this.size.height,
                    ),
                    size = this.size,
                    cornerRadius = radius,
                )
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.9f),
                    topLeft = Offset(borderPx / 2f, borderPx / 2f),
                    size = Size(this.size.width - borderPx, this.size.height - borderPx),
                    cornerRadius = CornerRadius(max(0f, corner - borderPx / 2f)),
                    style = Stroke(width = borderPx),
                )
            }
            .padding(horizontal = size * 0.05f, vertical = size * 0.018f),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TileLine(title, titleSize, FontWeight.ExtraBold, Color.White, titleShadow, textMaxPx)
            if (showSubtitle) {
                TileLine(subtitle, subtitleSize, FontWeight.SemiBold, Color.White.copy(alpha = 0.92f), null, textMaxPx)
            }
        }
    }
}

/**
 * One line of ribbon text at exactly [sizeDp] (it never grows with the user's font scale: it sits in a fixed tile). It
 * shrinks to at most 60% to fit [maxWidthPx] (SwiftUI's `minimumScaleFactor(0.6)`), then truncates. Its line box is about
 * its own height (1.02 x), so title and subtitle sit close; the box does not change when the text shrinks.
 */
@Composable
private fun TileLine(
    text: String,
    sizeDp: Float,
    weight: FontWeight,
    color: Color,
    shadow: Shadow?,
    maxWidthPx: Float,
) {
    val density = LocalDensity.current
    val measurer = rememberTextMeasurer()
    val sizeSp = with(density) { sizeDp.dp.toSp().value }
    val base = AbTypography.fixed(sizeSp, weight).copy(
        color = color,
        lineHeight = (sizeSp * 1.02f).sp,
        shadow = shadow,
    )
    val natural = measurer.measure(text, base, maxLines = 1, softWrap = false).size.width
    val scale = if (natural > maxWidthPx && natural > 0) max(0.6f, maxWidthPx / natural) else 1f
    // An exact-height box, with the text measured without a height limit and centred in it, so the font's own tall line
    // box can never push the next line down (the subtitle was falling out of the ribbon).
    Box(Modifier.height((sizeDp * 1.02f).dp), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = base.copy(fontSize = (sizeSp * scale).sp),
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.wrapContentHeight(unbounded = true),
        )
    }
}
