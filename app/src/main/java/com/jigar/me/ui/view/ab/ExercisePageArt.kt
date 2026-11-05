package com.jigar.me.ui.view.ab

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

/*
 * The pictures of the exercise pager: a row of chunky symbol stickers at the top of each page and faint symbols scattered
 * behind it. Page 0 is addition and subtraction, page 1 multiplication, page 2 division. Drawing only.
 */

enum class ExerciseSymbol { Plus, Minus, Times, Divide }

/**
 * Draws [symbol] with shapes (crisp, and the same whatever the font), centred in a square of [d] pixels at the origin.
 * All bars are added to ONE path, so the crossing of a plus is filled once (a translucent colour does not double up).
 */
private fun DrawScope.drawExerciseSymbol(symbol: ExerciseSymbol, color: Color, d: Float) {
    val c = d / 2f
    val t = d * 0.17f      // stroke thickness
    val l = d * 0.6f       // arm length
    val path = Path()
    fun bar(w: Float, h: Float) {
        path.addRoundRect(RoundRect(Rect(c - w / 2f, c - h / 2f, c + w / 2f, c + h / 2f), CornerRadius(min(w, h) / 2f)))
    }
    when (symbol) {
        ExerciseSymbol.Plus, ExerciseSymbol.Times -> { bar(l, t); bar(t, l) }
        ExerciseSymbol.Minus -> bar(l, t)
        ExerciseSymbol.Divide -> {
            bar(l, t)
            val dot = t * 1.15f / 2f
            path.addOval(Rect(Offset(c, c - l * 0.34f), dot))
            path.addOval(Rect(Offset(c, c + l * 0.34f), dot))
        }
    }
    if (symbol == ExerciseSymbol.Times) {
        rotate(45f, Offset(c, c)) { drawPath(path, color) }
    } else {
        drawPath(path, color)
    }
}

/** One symbol drawn with shapes, centred in a square of [size]. */
@Composable
fun ExerciseSymbolShape(symbol: ExerciseSymbol, color: Color, size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) { drawExerciseSymbol(symbol, color, this.size.minDimension) }
}

/** A round 3D sticker with a white symbol on it. Its height is [size] x 1.08: the extra 8% is the 3D edge underneath. */
@Composable
fun ExerciseSticker(symbol: ExerciseSymbol, tone: Color, size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size, size * 1.08f)) {
        val d = this.size.width
        val r = d / 2f
        val centre = Offset(r, r)
        // 3D edge, face, white ring (drawn inside the circle, like SwiftUI strokeBorder)
        drawCircle(color = tone.abDarkened(0.3f), radius = r, center = Offset(r, r + d * 0.08f))
        drawCircle(
            brush = Brush.verticalGradient(listOf(tone.abBlended(Color.White, 0.3f), tone), startY = 0f, endY = d),
            radius = r,
            center = centre,
        )
        val ring = max(2.dp.toPx(), d * 0.05f)
        drawCircle(color = Color.White.copy(alpha = 0.85f), radius = r - ring / 2f, center = centre, style = Stroke(width = ring))
        // the white symbol, with a hard (zero blur) shadow just under it
        translate(0f, d * 0.03f) { drawExerciseSymbol(symbol, Color.Black.copy(alpha = 0.16f), d) }
        drawExerciseSymbol(symbol, Color.White, d)
    }
}

/** A small abacus bead (a flat hexagon, like the beads on the abacus). Its height is [width] x 0.62 x 1.12 (3D edge included). */
@Composable
fun ExerciseBead(tone: Color, width: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(width, width * 0.62f * 1.12f)) {
        val w = this.size.width
        val h = w * 0.62f
        val drop = h * 0.12f
        drawPath(abBeadPath(Rect(0f, drop, w, drop + h)), tone.abDarkened(0.28f))
        drawPath(
            abBeadPath(Rect(0f, 0f, w, h)),
            Brush.verticalGradient(listOf(tone.abBlended(Color.White, 0.35f), tone), startY = 0f, endY = h),
        )
    }
}

/** The picture at the top of an exercise page. */
@Composable
fun ExercisePageArt(page: Int, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val d = 31.dp * s
    val w = 14.dp * s
    Row(
        modifier.clearAndSetSemantics { },
        horizontalArrangement = Arrangement.spacedBy(10.dp * s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (page) {
            0 -> {
                ExerciseBeadColumn(listOf(colors.accentYellow, colors.accentGreen), w, tilt = -10f)
                ExerciseSticker(ExerciseSymbol.Plus, colors.accentGreen, d, Modifier.rotate(-8f))
                ExerciseSticker(ExerciseSymbol.Minus, colors.accentOrange, d, Modifier.rotate(8f))
                ExerciseBeadColumn(listOf(colors.accentOrange, colors.accentPink), w, tilt = 10f)
            }
            1 -> {
                ExerciseBeadColumn(listOf(colors.accentPink, colors.accentPurple), w, tilt = -10f)
                ExerciseSticker(ExerciseSymbol.Times, colors.accentPurple, d * 1.15f)
                ExerciseBeadColumn(listOf(colors.accentPurple, colors.accentYellow), w, tilt = 10f)
            }
            else -> {
                ExerciseBeadColumn(listOf(colors.accentYellow, colors.accentPink), w, tilt = -10f)
                ExerciseSticker(ExerciseSymbol.Divide, colors.accentPink, d * 1.15f)
                ExerciseBeadColumn(listOf(colors.accentPink, colors.accentGreen), w, tilt = 10f)
            }
        }
    }
}

/** A small column of beads, tilted about its own centre (rotation does not change the layout). */
@Composable
private fun ExerciseBeadColumn(tones: List<Color>, width: Dp, tilt: Float) {
    val s = LocalAbScale.current.factor
    Column(
        Modifier.rotate(tilt),
        verticalArrangement = Arrangement.spacedBy(3.dp * s),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        tones.forEach { ExerciseBead(it, width) }
    }
}

private class DecorPiece(
    val symbol: ExerciseSymbol,
    val tone: Int,                // index into the page's colours
    val x: Float, val y: Float,   // position as a fraction of the page
    val size: Float,
    val tilt: Float,
)

private fun decorPieces(page: Int): List<DecorPiece> = when (page) {
    0 -> listOf(
        DecorPiece(ExerciseSymbol.Plus, 0, 0.10f, 0.10f, 34f, -12f),
        DecorPiece(ExerciseSymbol.Minus, 1, 0.91f, 0.08f, 30f, 10f),
        DecorPiece(ExerciseSymbol.Minus, 1, 0.06f, 0.46f, 24f, 14f),
        DecorPiece(ExerciseSymbol.Plus, 2, 0.95f, 0.50f, 28f, -8f),
        DecorPiece(ExerciseSymbol.Plus, 1, 0.11f, 0.84f, 30f, 12f),
        DecorPiece(ExerciseSymbol.Minus, 0, 0.90f, 0.86f, 26f, -10f),
    )
    1 -> listOf(
        DecorPiece(ExerciseSymbol.Times, 0, 0.10f, 0.10f, 34f, -10f),
        DecorPiece(ExerciseSymbol.Times, 1, 0.91f, 0.08f, 28f, 12f),
        DecorPiece(ExerciseSymbol.Times, 1, 0.06f, 0.46f, 24f, 8f),
        DecorPiece(ExerciseSymbol.Times, 2, 0.95f, 0.50f, 30f, -14f),
        DecorPiece(ExerciseSymbol.Times, 2, 0.11f, 0.84f, 30f, 10f),
        DecorPiece(ExerciseSymbol.Times, 0, 0.90f, 0.86f, 26f, -6f),
    )
    else -> listOf(
        DecorPiece(ExerciseSymbol.Divide, 0, 0.10f, 0.10f, 34f, -12f),
        DecorPiece(ExerciseSymbol.Divide, 1, 0.91f, 0.08f, 28f, 10f),
        DecorPiece(ExerciseSymbol.Divide, 1, 0.06f, 0.46f, 24f, 14f),
        DecorPiece(ExerciseSymbol.Divide, 2, 0.95f, 0.50f, 30f, -8f),
        DecorPiece(ExerciseSymbol.Divide, 2, 0.11f, 0.84f, 30f, 12f),
        DecorPiece(ExerciseSymbol.Divide, 0, 0.90f, 0.86f, 26f, -10f),
    )
}

/** Faint symbols scattered behind a page, in the page's colours (light, so they never compete with the writing). Fills its parent. */
@Composable
fun ExercisePageDecor(page: Int, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val pieces = decorPieces(page)
    val tones = when (page) {
        0 -> listOf(colors.accentGreen, colors.accentOrange, colors.accentPink)
        1 -> listOf(colors.accentPurple, colors.accentPink, colors.accentOrange)
        else -> listOf(colors.accentPink, colors.accentPurple, colors.accentGreen)
    }
    val opacity = if (colors.isDark) 0.16f else 0.13f
    Canvas(modifier.fillMaxSize().clearAndSetSemantics { }) {
        for (p in pieces) {
            val d = (p.size * s).dp.toPx()
            // the symbol is centred on its (x, y) spot, then tilted about that centre
            translate(this.size.width * p.x, this.size.height * p.y) {
                rotate(p.tilt, Offset.Zero) {
                    translate(-d / 2f, -d / 2f) { drawExerciseSymbol(p.symbol, tones[p.tone].copy(alpha = opacity), d) }
                }
            }
        }
    }
}

/**
 * The scene behind a running exercise: a soft colour wash in two corners and a squared-notebook grid that is visible only
 * near the edges and fades to nothing behind the question and the keypad, so nothing competes with them. A plain composable
 * that fills its parent: the caller clips it to the panel's rounded shape.
 */
@Composable
fun ExerciseQuestionDecor(page: Int, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val dark = colors.isDark
    val tone = when (page) {
        0 -> colors.accentGreen
        1 -> colors.accentPurple
        else -> colors.accentPink
    }
    val yellow = colors.accentYellow
    Box(modifier.fillMaxSize().clearAndSetSemantics { }) {
        // two soft colour washes: the page colour from the top-left corner, yellow from the bottom-right
        Canvas(Modifier.fillMaxSize()) {
            val reach = max(this.size.width, this.size.height)
            drawRect(
                Brush.radialGradient(
                    listOf(tone.copy(alpha = if (dark) 0.16f else 0.12f), tone.copy(alpha = 0f)),
                    center = Offset.Zero,
                    radius = reach * 0.7f,
                ),
            )
            drawRect(
                Brush.radialGradient(
                    listOf(yellow.copy(alpha = if (dark) 0.12f else 0.10f), yellow.copy(alpha = 0f)),
                    center = Offset(this.size.width, this.size.height),
                    radius = reach * 0.6f,
                ),
            )
        }
        // the grid, masked by a radial fade (clear in the middle, solid at the edges): drawn in its own layer so the mask
        // only touches the grid and not the washes
        Canvas(Modifier.fillMaxSize().graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }) {
            val w = this.size.width
            val h = this.size.height
            val reach = max(w, h)
            val step = (26f * s).dp.toPx()
            val grid = Path()
            var x = step / 2f
            while (x < w) { grid.moveTo(x, 0f); grid.lineTo(x, h); x += step }
            var y = step / 2f
            while (y < h) { grid.moveTo(0f, y); grid.lineTo(w, y); y += step }
            drawPath(grid, tone.copy(alpha = if (dark) 0.22f else 0.16f), style = Stroke(width = 1.dp.toPx()))
            drawRect(
                Brush.radialGradient(
                    0f to Color.Transparent,
                    0.45f to Color.Transparent,
                    1f to Color.Black,
                    center = Offset(w / 2f, h / 2f),
                    radius = reach * 0.62f,
                ),
                blendMode = BlendMode.DstIn,
            )
        }
    }
}
