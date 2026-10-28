package com.jigar.me.ui.view.ab

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/*
 * The "toy" abacus frame of the Ab kit (mirrors `AbacusToyFrame.swift`): a chunky frame body with rivets, a pale panel with
 * coloured place-value rods, a glossy beam with a golden star on the unit rod, and a hanging shelf of coloured tags under
 * the frame. The bead geometry is exactly the classic one, so beads, drag handling and the tour overlays are unchanged.
 */

enum class AbacusFrameStyle {
    /** The original frame, with the answer bar and label strip drawn by the abacus. */
    Classic,

    /** The toy frame: its own answer bar and tags instead of the label strip. */
    Toy,
}

/** Place-value colours for the integer rods, ones first (the same hues the Free Mode labels always used). */
object AbacusPlaceValue {
    val colors: List<Long> = listOf(0xF57C00, 0xA4B42B, 0x388E3C, 0x00796B, 0x0097A7, 0x1976D2, 0x303F9F)
    val values = listOf("1", "10", "100", "1000", "10000", "100000", "1000000")
    val names = listOf("Ones", "Tens", "Hundreds", "Thousands", "Ten\nThousands", "Lakhs", "Ten\nLakhs")
}

internal fun toyHex(hex: Long): Color = Color(
    red = ((hex shr 16) and 0xFF) / 255f,
    green = ((hex shr 8) and 0xFF) / 255f,
    blue = (hex and 0xFF) / 255f,
)

object AbacusToyPalette {
    /**
     * Top and bottom of the panel the beads sit on: a whisper of the frame colour on white by day (so it suits every bead
     * theme instead of clashing), midnight by night.
     */
    fun panel(base: Color, isDark: Boolean): Pair<Color, Color> {
        if (isDark) return toyHex(0x1D1A4C) to toyHex(0x14123A)
        return Color.White.abBlended(base, 0.06f) to Color.White.abBlended(base, 0.15f)
    }
}

/** Everything the frame drawing needs to know about the abacus geometry. */
data class AbacusToyGeometry(
    val numberOfColumns: Int,
    val totalWidth: Dp,
    val totalHeight: Dp,
    val lineWidth: Dp,
    val beadWidth: Dp,
    val beadHeight: Dp,
    val beamHeight: Dp,
    val extraSpace: Dp,
    val columnSpaces: Dp,
    val decimalGap: Dp,
    val corner: Dp,
) {
    val cellWidth: Dp get() = beadWidth + columnSpaces * 2

    /** Centre x of a rod. */
    fun rodX(i: Int): Dp = lineWidth + cellWidth * (i + 0.5f) + (if (numberOfColumns > 7 && i > 6) decimalGap else 0.dp)

    val beamTop: Dp get() = lineWidth + extraSpace + beadHeight * 2

    /** Outer corner radius of the frame body, and of the pale panel inside it (the tour's spotlight uses the same radii). */
    val frameCorner: Dp get() = maxOf(corner, lineWidth * 0.9f)
    val panelCorner: Dp get() = maxOf(frameCorner - lineWidth * 0.6f, 6.dp)

    /** Rods that carry a dot on the beam: the last rod and every third rod before it (the classic frame's rule). */
    fun hasBeamDot(i: Int): Boolean = (numberOfColumns - 1 - i) % 3 == 0

    /**
     * Which place value an integer rod holds: 0 = ones; null for the decimal rods. A small abacus (Level screens, the
     * Settings preview) has only integer rods and its last rod is the ones rod.
     */
    fun place(i: Int): Int? {
        if (numberOfColumns > 7) return if (i <= 6) 6 - i else null
        return numberOfColumns - 1 - i
    }
}

private fun roundRectPath(rect: Rect, radius: Float): Path = Path().apply {
    addRoundRect(RoundRect(rect, CornerRadius(radius, radius)))
}

/** A soft drop shadow built from a few widening translucent layers (works on every API level). */
internal fun DrawScope.softShadow(rect: Rect, radius: Float, blur: Float, dy: Float, alpha: Float) {
    val steps = 6
    for (k in steps downTo 1) {
        val grow = blur * k / steps
        val r = Rect(rect.left - grow, rect.top - grow + dy, rect.right + grow, rect.bottom + grow + dy)
        drawPath(roundRectPath(r, radius + grow), Color.Black.copy(alpha = alpha / steps * 0.55f))
    }
}

/** The frame body, panel, rods and beam. Drawn behind the beads. */
@Composable
fun AbacusToyFrameBack(
    g: AbacusToyGeometry,
    top: Color,
    base: Color,
    bottom: Color,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAbColors.current
    val isDark = colors.isDark
    Canvas(modifier.width(g.totalWidth).height(g.totalHeight)) {
        val w = size.width
        val h = size.height
        val t = g.lineWidth.toPx()
        val r = g.frameCorner.toPx()
        val frameRect = Rect(0f, 0f, w, h)

        // drop shadow behind the body: one shape only, so no second rounded rectangle can show as a crescent at the corners
        softShadow(frameRect, r, blur = t * 0.2f, dy = t * 0.16f, alpha = 0.28f)

        // frame body: one clean gradient, a soft sheen along the top bar and a single outline
        val outer = roundRectPath(frameRect, r)
        drawPath(outer, Brush.verticalGradient(listOf(top.abBlended(Color.White, 0.2f), base, base.abDarkened(0.18f)), startY = 0f, endY = h))
        clipPath(outer) {
            drawRect(
                Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.28f), Color.White.copy(alpha = 0f)), startY = 0f, endY = t * 1.4f),
                size = Size(w, t * 1.4f),
            )
        }
        // The outline sits fully INSIDE the frame, so it is the same weight on the sides and at the corners.
        val ow = max(1.5f.dp.toPx(), t * 0.06f)
        drawPath(
            roundRectPath(Rect(ow / 2, ow / 2, w - ow / 2, h - ow / 2), max(r - ow / 2, 1f)),
            base.abDarkened(0.5f),
            style = Stroke(width = ow, join = StrokeJoin.Round),
        )

        // panel
        val inset = t
        val panelRect = Rect(inset, inset, w - inset, h - inset)
        val panel = roundRectPath(panelRect, g.panelCorner.toPx())
        val tint = AbacusToyPalette.panel(base, isDark)
        drawPath(panel, Brush.verticalGradient(listOf(tint.first, tint.second), startY = inset, endY = h - inset))
        // inner shadow: dark along the top / left, like a window cut into the frame
        clipPath(panel) {
            translate(t * 0.05f, t * 0.09f) {
                drawPath(panel, base.abDarkened(0.5f).copy(alpha = 0.38f), style = Stroke(width = t * 0.2f))
            }
        }
        drawPath(panel, base.abDarkened(0.5f).copy(alpha = 0.6f), style = Stroke(width = max(1.5f.dp.toPx(), t * 0.05f)))

        // rods
        val rw = max(4.dp.toPx(), g.beamHeight.toPx() * 0.62f)
        val rodTop = inset + 2.dp.toPx()
        val rodBottom = h - inset - 2.dp.toPx()
        for (i in 0 until g.numberOfColumns) {
            val x = g.rodX(i).toPx()
            val place = g.place(i)
            val rodColor = place?.let { toyHex(AbacusPlaceValue.colors[it]) } ?: base.abBlended(toyHex(0x9AA3C7), 0.5f)
            val rect = Rect(x - rw / 2, rodTop, x + rw / 2, rodBottom)
            drawPath(
                roundRectPath(rect, rw / 2),
                Brush.horizontalGradient(listOf(rodColor.abBlended(Color.White, 0.35f), rodColor, rodColor.abDarkened(0.2f)), startX = rect.left, endX = rect.right),
            )
            drawPath(
                roundRectPath(Rect(rect.left + rw * 0.18f, rect.top + 2.dp.toPx(), rect.left + rw * 0.38f, rect.bottom - 2.dp.toPx()), rw * 0.1f),
                Color.White.copy(alpha = if (place == null) 0.15f else 0.32f),
            )
        }

        // beam
        val beamH = g.beamHeight.toPx()
        val beamInset = inset - t * 0.25f
        val beamRect = Rect(beamInset, g.beamTop.toPx(), w - beamInset, g.beamTop.toPx() + beamH)
        val beam = roundRectPath(beamRect, beamH / 2)
        translate(0f, beamH * 0.14f) { drawPath(beam, base.abDarkened(0.5f)) }
        drawPath(beam, Brush.verticalGradient(listOf(top.abBlended(Color.White, 0.35f), base, base.abDarkened(0.15f)), startY = beamRect.top, endY = beamRect.bottom))
        drawPath(beam, base.abDarkened(0.5f), style = Stroke(width = max(1.5f.dp.toPx(), beamH * 0.07f)))

        // beam markers: a golden star on the ones rod, white dots every third rod counted from the last one
        for (i in 0 until g.numberOfColumns) {
            val x = g.rodX(i).toPx()
            val y = beamRect.center.y
            if (g.place(i) == 0) {
                val sr = beamH * 0.92f
                val star = Path()
                for (k in 0 until 10) {
                    val a = k * PI / 5 - PI / 2
                    val rr = if (k % 2 == 0) sr else sr * 0.45f
                    val px = x + rr * cos(a).toFloat()
                    val py = y + rr * sin(a).toFloat()
                    if (k == 0) star.moveTo(px, py) else star.lineTo(px, py)
                }
                star.close()
                drawPath(star, Brush.verticalGradient(listOf(toyHex(0xFFE680), toyHex(0xFFB800)), startY = y - sr, endY = y + sr))
                drawPath(star, toyHex(0xB07A00), style = Stroke(width = max(1.2f.dp.toPx(), sr * 0.1f), join = StrokeJoin.Round))
            } else if (g.hasBeamDot(i)) {
                drawCircle(Color.White.copy(alpha = 0.9f), radius = beamH * 0.2f, center = Offset(x, y))
            }
        }

        // rivets in the four corners, tucked in so they clear the rounded edge, and grips on the posts
        val rr = t * 0.17f
        val ri = t * 0.62f
        for ((cx, cy) in listOf(ri to ri, w - ri to ri, ri to h - ri, w - ri to h - ri)) {
            drawCircle(base.abDarkened(0.35f), radius = rr, center = Offset(cx, cy))
            drawCircle(Color.White.copy(alpha = 0.55f), radius = rr * 0.75f, center = Offset(cx - rr * 0.12f, cy - rr * 0.12f))
        }
        for (sx in listOf(t / 2, w - t / 2)) {
            for (k in -1..1) {
                val y = h / 2 + k * t * 0.55f
                drawPath(
                    roundRectPath(Rect(sx - t * 0.14f, y - t * 0.12f, sx + t * 0.14f, y + t * 0.12f), t * 0.1f),
                    Color.White.copy(alpha = 0.32f),
                )
            }
        }
    }
}

/** Text that shrinks (down to [minScale]) instead of wrapping or clipping when it is wider than its box. */
@Composable
private fun ToyFitText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    minScale: Float = 0.5f,
    shadow: Boolean = false,
) {
    var scale by remember(text, style.fontSize) { mutableFloatStateOf(1f) }
    androidx.compose.material3.Text(
        text = text,
        // measured with no height limit, so a line box shorter than the font's own height never clips the glyphs
        modifier = modifier.wrapContentHeight(unbounded = true),
        color = color,
        style = style.copy(
            fontSize = style.fontSize * scale,
            lineHeight = style.lineHeight * scale,
            shadow = if (shadow) Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 1f), 0f) else null,
        ),
        maxLines = 1,
        softWrap = false,
        textAlign = TextAlign.Center,
        onTextLayout = { if (it.didOverflowWidth && scale > minScale) scale = max(minScale, scale * 0.9f) },
    )
}

/** The chunky coloured tag shape (a darker bottom edge, a lighter face, a white rim). */
private fun Modifier.toyTagFace(color: Color, tagH: Dp): Modifier = drawBehind {
    val h = size.height
    val corner = CornerRadius(tagH.toPx() * 0.4f)
    drawRoundRect(color.abDarkened(0.35f), topLeft = Offset(0f, tagH.toPx() * 0.07f), size = size, cornerRadius = corner)
    drawRoundRect(Brush.verticalGradient(listOf(color.abBlended(Color.White, 0.22f), color), startY = 0f, endY = h), size = size, cornerRadius = corner)
    val sw = 1.5f.dp.toPx()
    drawRoundRect(
        Color.White.copy(alpha = 0.85f),
        topLeft = Offset(sw / 2, sw / 2),
        size = Size(size.width - sw, size.height - sw),
        cornerRadius = CornerRadius(max(corner.x - sw / 2, 0f)),
        style = Stroke(width = sw),
    )
}

/**
 * Free Mode only: what each rod's upper (heaven) bead is worth (5, 50, 500 ...), as small tags that sit on the frame's
 * top rail, in the same style as the value tags under the frame.
 */
@Composable
fun AbacusToyHeavenTags(g: AbacusToyGeometry, stripHeight: Dp, modifier: Modifier = Modifier) {
    val cw = g.cellWidth
    val rw = maxOf(4.dp, g.beamHeight * 0.62f)
    val tagH = minOf(cw * 0.46f, stripHeight * 1.45f) * 0.86f
    val stemH = 4.dp
    Box(modifier.width(g.totalWidth).height(tagH + stemH)) {
        for (i in 0 until min(7, g.numberOfColumns)) {
            val p = g.place(i) ?: continue
            val color = toyHex(AbacusPlaceValue.colors[p])
            Column(
                Modifier.width(cw).offset(x = g.rodX(i) - cw / 2),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    Modifier.width(cw * 0.86f).height(tagH).toyTagFace(color, tagH).padding(horizontal = cw * 0.05f),
                    contentAlignment = Alignment.Center,
                ) {
                    ToyFitText("5" + "0".repeat(p), AbTypography.fixed(tagH.value * 0.5f, FontWeight.ExtraBold), Color.White, minScale = 0.4f, shadow = true)
                }
                Box(
                    Modifier.width(rw).height(stemH)
                        .background(Brush.horizontalGradient(listOf(color.abBlended(Color.White, 0.3f), color)), CircleShape),
                )
            }
        }
    }
}

/**
 * The labels under the frame: each rod grows a short coloured stem that ends in a tag. With [showsValues] (Free Mode, where
 * the place values are the lesson) the tag carries the place value and the place name is written beneath; otherwise (the
 * sum screens) the tag IS the place name ("Ones", "Tens" ...) in the same chunky style. Decimal rods get quiet outlined
 * tags so they read as "the other group".
 */
@Composable
fun AbacusToyLabels(g: AbacusToyGeometry, stripHeight: Dp, modifier: Modifier = Modifier, showsValues: Boolean = true) {
    val colors = LocalAbColors.current
    val cw = g.cellWidth
    val rw = maxOf(4.dp, g.beamHeight * 0.62f)
    val tagH = minOf(cw * 0.46f, stripHeight * 1.45f)
    // The line between the frame and the tag is kept very short: it only has to show the tag hangs from the frame.
    val stemH = 4.dp
    val nameSize = maxOf(8f, stripHeight.value * 0.5f)
    // a little air between the value tag and the name under it
    val nameH = (nameSize * 1.8f + tagH.value * 0.3f).dp
    // A name card holds up to two lines ("Ten" / "Thousands"), set tight, so it is only a little taller than a value tag.
    val nameFont = tagH.value * 0.54f
    val cardH = (nameFont * 0.66f * 2 + tagH.value * 0.3f).dp
    val rowH = if (showsValues) tagH + nameH else cardH

    // The longest word of any name ("Thousands") sets the size of every name, so "Ten", "Lakhs" and "Thousands" all read alike.
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    fun sizeForLongestWord(size: Float, weight: FontWeight, available: Dp, floor: Float): Float {
        val wordPx = measurer.measure(AnnotatedString("Thousands"), AbTypography.fixed(size, weight)).size.width
        return size * (with(density) { available.toPx() } / wordPx * 0.97f).coerceIn(floor, 1f)
    }
    val cardFontShown = remember(nameFont, cw) { sizeForLongestWord(nameFont, FontWeight.ExtraBold, cw * 0.84f, 0.55f) }
    val nameSizeShown = remember(nameSize, cw) { sizeForLongestWord(nameSize, FontWeight.Bold, cw * 0.96f, 0.7f) }

    Box(modifier.width(g.totalWidth).height(stemH + rowH)) {
        for (i in 0 until g.numberOfColumns) {
            val p = g.place(i)
            // On the sum screens only the whole-number rods are named; the decimal rods get no label.
            if (!showsValues && p == null) continue
            val color = p?.let { toyHex(AbacusPlaceValue.colors[it]) } ?: toyHex(0x8E97C4)
            Column(
                Modifier.width(cw).offset(x = g.rodX(i) - cw / 2),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // The row sits a few points below the frame, so the line is drawn taller than its layout box and reaches up
                // to the frame: the tag hangs from the abacus, like the heaven tags above it.
                Box(Modifier.width(rw).height(stemH)) {
                    Box(
                        Modifier.width(rw).height(stemH + 5.dp).offset(y = (-3).dp)
                            .background(Brush.horizontalGradient(listOf(color.abBlended(Color.White, 0.3f), color)), CircleShape),
                    )
                }
                if (showsValues) {
                    val label = p?.let { AbacusPlaceValue.values[it] } ?: ("." + "0".repeat(max(0, i - 7)) + "1")
                    Box(
                        Modifier.width(cw * 0.86f).height(tagH).toyTagFaceOrOutline(p == null, color, tagH, colors.surfaceTint)
                            .padding(horizontal = cw * 0.05f),
                        contentAlignment = Alignment.Center,
                    ) {
                        ToyFitText(
                            label, AbTypography.fixed(tagH.value * (if (p == null) 0.42f else 0.5f), FontWeight.ExtraBold),
                            if (p == null) colors.textSecondary else Color.White, minScale = 0.4f, shadow = p != null,
                        )
                    }
                    // Names such as "Ten\nThousands" come with a line break; each line gets a tight box.
                    Column(
                        Modifier.width(cw).height(nameH).padding(horizontal = cw * 0.02f, vertical = 0.dp)
                            .padding(top = tagH * 0.3f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        val name = p?.let { AbacusPlaceValue.names[it] } ?: ""
                        for (line in name.split("\n")) {
                            Box(Modifier.height((nameSize * 0.68f).dp), contentAlignment = Alignment.Center) {
                                ToyFitText(
                                    line, AbTypography.fixed(nameSizeShown, FontWeight.Bold),
                                    if (colors.isDark) color.abBlended(Color.White, 0.45f) else color.abDarkened(0.15f), minScale = 0.7f,
                                )
                            }
                        }
                    }
                } else if (p != null) {
                    // The place name as a chunky card in the rod's colour, the same style as the value tags.
                    Column(
                        Modifier.width(cw * 0.92f).height(cardH).toyTagFace(color, tagH).padding(horizontal = cw * 0.04f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        for (line in AbacusPlaceValue.names[p].split("\n")) {
                            Box(Modifier.height((nameFont * 0.66f).dp), contentAlignment = Alignment.Center) {
                                ToyFitText(line, AbTypography.fixed(cardFontShown, FontWeight.ExtraBold), Color.White, minScale = 0.55f, shadow = true)
                            }
                        }
                    }
                }
            }
        }
    }
}

/** A value tag, or (for a decimal rod) a quiet tag with a dashed outline. */
private fun Modifier.toyTagFaceOrOutline(outlined: Boolean, color: Color, tagH: Dp, quietFill: Color): Modifier =
    if (!outlined) toyTagFace(color, tagH) else drawBehind {
        val corner = CornerRadius(tagH.toPx() * 0.4f)
        drawRoundRect(quietFill.copy(alpha = 0.7f), size = size, cornerRadius = corner)
        val sw = 1.5f.dp.toPx()
        drawRoundRect(
            color.copy(alpha = 0.7f),
            topLeft = Offset(sw / 2, sw / 2),
            size = Size(size.width - sw, size.height - sw),
            cornerRadius = CornerRadius(max(corner.x - sw / 2, 0f)),
            style = Stroke(width = sw, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx()))),
        )
    }
