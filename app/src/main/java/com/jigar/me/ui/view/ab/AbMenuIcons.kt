package com.jigar.me.ui.view.ab

import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import com.jigar.me.R
import com.jigar.me.utils.AppConstants
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/** 0xRRGGBB, sRGB (the kit-wide `Color(abHex:)` of the iOS app). */
internal fun abIconHex(hex: Int): Color = Color(
    red = ((hex shr 16) and 0xFF) / 255f,
    green = ((hex shr 8) and 0xFF) / 255f,
    blue = (hex and 0xFF) / 255f,
)

/** Light and deep tone of the badge behind a menu icon. */
data class AbMenuTones(val light: Color, val deep: Color)

/**
 * The eight Home menus that have a hand-drawn icon. Titles and subtitles are the English defaults (the caller passes the
 * localised strings to [AbMenuTile]); the tones are the badge colours.
 */
enum class AbMenuKind(
    val title: String,
    val subtitle: String,
    lightHex: Int,
    deepHex: Int,
) {
    FreeMode("Abacus Free Mode", "Perfect for beginner", 0xFFC46B, 0xF28A1E),
    Practice("Abacus Practice", "Learn math in easy way", 0x8FDB8F, 0x35A853),
    Exercise("Abacus Exercise", "Boost speed and accuracy", 0x7CC4FF, 0x2A7FE0),
    Games("Math Game Zone", "Unlock your mind's fun", 0xFF9DC0, 0xE23A7B),
    Exam("Math Exam", "Be ready, be confident", 0xC9A0F5, 0x8A4FD8),
    Challenge("Custom Challenge", "Your rules, your challenge!", 0xFFE27A, 0xF2A91C),
    Store("Purchase Store", "Unlock new math adventures!", 0xFF9C9C, 0xE04545),
    Video("Video Tutorial", "Learn abacus visually", 0x7EE3E8, 0x14A5B8);

    val light: Color = abIconHex(lightHex)
    val deep: Color = abIconHex(deepHex)
    val tones: AbMenuTones get() = AbMenuTones(light, deep)

    companion object {
        /** Maps the server's menu name to an icon; null for a menu we have no drawing for (it falls back to the server image). */
        fun fromMenuName(menuName: String): AbMenuKind? = when (menuName) {
            AppConstants.HomeClicks.Menu_Abacus_Free_Mode -> FreeMode
            AppConstants.HomeClicks.Menu_Practice_Abacus -> Practice
            AppConstants.HomeClicks.Menu_Abacus_Exercise -> Exercise
            AppConstants.HomeClicks.Menu_Math_Game -> Games
            AppConstants.HomeClicks.Menu_Exam -> Exam
            AppConstants.HomeClicks.Menu_CCM -> Challenge
            AppConstants.HomeClicks.Menu_Purchase_Store -> Store
            AppConstants.HomeClicks.Menu_Video_Tutorial -> Video
            else -> null
        }
    }
}

/** Draws one menu icon filling a square frame. */
@Composable
fun AbMenuIcon(kind: AbMenuKind, modifier: Modifier = Modifier) =
    AbMenuIconCanvas(kind, modifier, silhouette = false)

/**
 * The same drawing as a flat black silhouette (no gloss, no inner colour). [AbMenuTile] blurs and fades it under the real
 * icon to get the soft drop shadow that SwiftUI's `.shadow` gives the iOS tile.
 */
@Composable
internal fun AbMenuIconSilhouette(kind: AbMenuKind, modifier: Modifier = Modifier) =
    AbMenuIconCanvas(kind, modifier, silhouette = true)

@Composable
private fun AbMenuIconCanvas(kind: AbMenuKind, modifier: Modifier, silhouette: Boolean) {
    val typeface = rememberIconTypeface()
    Canvas(modifier.aspectRatio(1f)) {
        val pen = IconPen(this, min(size.width, size.height) / 100f, typeface, silhouette)
        when (kind) {
            AbMenuKind.FreeMode -> pen.abacus()
            AbMenuKind.Practice -> pen.notebook()
            AbMenuKind.Exercise -> pen.stopwatch()
            AbMenuKind.Games -> pen.controller()
            AbMenuKind.Exam -> pen.examPaper()
            AbMenuKind.Challenge -> pen.sliders()
            AbMenuKind.Store -> pen.shoppingBag()
            AbMenuKind.Video -> pen.television()
        }
    }
}

/** Baloo 2 ExtraBold as an Android Typeface, for the few glyphs drawn inside the icons ("2+3", "A+", the four operators). */
@Composable
internal fun rememberIconTypeface(): Typeface? {
    val context = LocalContext.current
    return remember(context) { runCatching { ResourcesCompat.getFont(context, R.font.baloo2_extrabold) }.getOrNull() }
}

private val TwinkleFill = abIconHex(0xFFF3A0)
private val TwinkleRim = abIconHex(0xE0A800)

/**
 * The little drawing toolkit. Everything is authored in a 100 x 100 box: [u] is the pixel size of one of those units.
 * [silhouette] redraws the same shapes as one black shape (used for the tile's soft drop shadow).
 *
 * Rounded rectangles use circular corners. SwiftUI's `.continuous` (squircle) corners differ by a fraction of a pixel at
 * these radii; the rest of the Android kit makes the same trade.
 */
internal class IconPen(
    val ctx: DrawScope,
    val u: Float,
    private val typeface: Typeface? = null,
    private val silhouette: Boolean = false,
) {
    /** Short form of [abIconHex] so the drawings stay readable. */
    fun h(hex: Int): Color = abIconHex(hex)

    private fun tone(c: Color): Color = if (silhouette) Color.Black.copy(alpha = c.alpha) else c

    // MARK: Shapes

    fun rect(x: Float, y: Float, w: Float, h: Float): Rect = Rect(x * u, y * u, (x + w) * u, (y + h) * u)

    fun rr(x: Float, y: Float, w: Float, h: Float, r: Float): Path =
        Path().apply { addRoundRect(RoundRect(rect(x, y, w, h), CornerRadius(r * u))) }

    fun oval(cx: Float, cy: Float, rx: Float, ry: Float): Path =
        Path().apply { addOval(rect(cx - rx, cy - ry, rx * 2f, ry * 2f)) }

    fun circle(cx: Float, cy: Float, r: Float): Path = oval(cx, cy, r, r)

    fun poly(pts: List<Pair<Float, Float>>): Path = Path().apply {
        pts.forEachIndexed { i, p ->
            if (i == 0) moveTo(p.first * u, p.second * u) else lineTo(p.first * u, p.second * u)
        }
        close()
    }

    fun poly(vararg pts: Pair<Float, Float>): Path = poly(pts.asList())

    fun sparkle(cx: Float, cy: Float, r: Float): Path {
        val k = 0.28f
        return poly(
            listOf(
                cx to (cy - r), (cx + r * k) to (cy - r * k), (cx + r) to cy, (cx + r * k) to (cy + r * k),
                cx to (cy + r), (cx - r * k) to (cy + r * k), (cx - r) to cy, (cx - r * k) to (cy - r * k),
            ),
        )
    }

    // MARK: Painting

    /** Soft ground shadow under an object. */
    fun groundShadow(cx: Float, cy: Float, rx: Float) {
        flat(oval(cx, cy, rx, rx * 0.22f), Color.Black.copy(alpha = 0.16f))
    }

    /** A filled shape with a vertical gradient, a darker outline and a glossy highlight: the "sticker" look. */
    fun sticker(path: Path, base: Color, lw: Float = 2.6f, outline: Color? = null, gloss: Boolean = true) {
        if (silhouette) {
            ctx.drawPath(path, Color.Black)
            ctx.drawPath(path, Color.Black, style = Stroke(width = lw * u, join = StrokeJoin.Round))
            return
        }
        val b = path.getBounds()
        ctx.drawPath(
            path,
            Brush.linearGradient(
                colors = listOf(base.abBlended(Color.White, 0.32f), base),
                start = Offset(b.center.x, b.top),
                end = Offset(b.center.x, b.bottom),
            ),
        )
        if (gloss) {
            ctx.clipPath(path) {
                drawOval(
                    Color.White.copy(alpha = 0.28f),
                    topLeft = Offset(b.left - b.width * 0.1f, b.top - b.height * 0.55f),
                    size = Size(b.width * 1.2f, b.height * 0.95f),
                )
            }
        }
        ctx.drawPath(path, outline ?: base.abDarkened(0.42f), style = Stroke(width = lw * u, join = StrokeJoin.Round))
    }

    fun flat(path: Path, c: Color) {
        ctx.drawPath(path, tone(c))
    }

    /** Strokes [path] with a width given in drawing units (the Swift code writes `lineWidth: 1.6 * u`). */
    fun strokePath(path: Path, c: Color, lw: Float, cap: StrokeCap = StrokeCap.Butt, join: StrokeJoin = StrokeJoin.Miter) {
        ctx.drawPath(path, tone(c), style = Stroke(width = lw * u, cap = cap, join = join))
    }

    /** Fills [path] with a top-to-bottom gradient running from `y0` to `y1` (drawing units). */
    fun fillVertical(path: Path, top: Color, bottom: Color, y0: Float, y1: Float) {
        if (silhouette) {
            ctx.drawPath(path, Color.Black)
            return
        }
        ctx.drawPath(path, Brush.verticalGradient(listOf(top, bottom), startY = y0 * u, endY = y1 * u))
    }

    /** Runs [body] clipped to [path] (an inner highlight, so a silhouette has nothing to add). */
    fun clipped(path: Path, body: DrawScope.() -> Unit) {
        if (silhouette) return
        ctx.clipPath(path, block = body)
    }

    fun line(x1: Float, y1: Float, x2: Float, y2: Float, c: Color, lw: Float) {
        ctx.drawLine(
            color = tone(c),
            start = Offset(x1 * u, y1 * u),
            end = Offset(x2 * u, y2 * u),
            strokeWidth = lw * u,
            cap = StrokeCap.Round,
        )
    }

    fun twinkle(cx: Float, cy: Float, r: Float, c: Color = TwinkleFill) {
        sticker(sparkle(cx, cy, r), c, lw = 1.4f, outline = TwinkleRim, gloss = false)
    }

    /**
     * Text centred on (cx, cy) in drawing units, [sizeU] units tall. Horizontally centred on its advance width (like
     * SwiftUI), vertically on the ink of the glyphs, so a digit or an operator sits in the middle of its badge whatever
     * the font's own line box is.
     */
    fun text(s: String, cx: Float, cy: Float, sizeU: Float, ink: Color) {
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            this.typeface = this@IconPen.typeface ?: Typeface.DEFAULT_BOLD
            textSize = sizeU * u
            this.color = tone(ink).toArgb()
        }
        val bounds = android.graphics.Rect()
        paint.getTextBounds(s, 0, s.length, bounds)
        val x = cx * u - paint.measureText(s) / 2f
        val y = cy * u - bounds.exactCenterY()
        ctx.drawContext.canvas.nativeCanvas.drawText(s, x, y, paint)
    }

    /** Draws [body] rotated about a point (drawing units). */
    fun rotated(degrees: Float, aboutX: Float, aboutY: Float, body: (IconPen) -> Unit) {
        ctx.rotate(degrees, Offset(aboutX * u, aboutY * u)) { body(this@IconPen) }
    }

    // MARK: Icons

    /**
     * Free Mode: a real abacus. Each rod has room for two beads on the top deck but only one bead, and room for five on
     * the bottom deck but only four, so one slot on each deck is always empty. A bead "counts" when it touches the beam.
     */
    fun abacus() {
        groundShadow(50f, 90f, 38f)
        val wood = h(0x8A4B1F)
        // frame and cream panel
        sticker(rr(8f, 14f, 84f, 72f, 11f), wood, lw = 3f)
        flat(rr(16f, 22f, 68f, 56f, 6f), h(0xFFF6E0))
        strokePath(rr(16f, 22f, 68f, 56f, 6f), wood.copy(alpha = 0.45f), 1.6f)
        // The panel (22...78) holds 2 upper slots, the beam and 5 lower slots.
        val top = 22f
        val bottom = 78f
        val beamH = 6f
        val bh = (bottom - top - beamH) / 7f
        val beamY = top + 2f * bh
        val lowerTop = beamY + beamH
        // rods
        val rods = listOf(29f, 43f, 57f, 71f)
        for (x in rods) line(x, top, x, bottom, h(0xC79A5E), 2.6f)
        // beam
        sticker(rr(16f, beamY, 68f, beamH, 2.4f), h(0xB8702E), lw = 1.8f)
        // beads
        val colors = listOf(0xEF5350, 0x42A5F5, 0x4CC764, 0xAB47BC)
        val upperCounts = listOf(true, false, true, false) // upper bead down against the beam = 5
        val lowerCounts = listOf(2, 4, 0, 1)               // lower beads pushed up against the beam
        val bw = 12.6f
        rods.forEachIndexed { i, x ->
            val c = h(colors[i])
            // one upper bead, in a deck that has room for two
            val uy = if (upperCounts[i]) beamY - bh else top
            sticker(rr(x - bw / 2f, uy, bw, bh - 0.4f, 3.4f), c, lw = 1.5f)
            // four lower beads, in a deck that has room for five
            for (j in 0 until 4) {
                val y = if (j < lowerCounts[i]) lowerTop + j * bh else bottom - (4 - j) * bh
                sticker(rr(x - bw / 2f, y, bw, bh - 0.4f, 3.4f), c, lw = 1.5f)
            }
        }
        twinkle(90f, 12f, 8f)
        twinkle(10f, 88f, 5.5f)
    }

    /** Practice: a notebook with a sum on it and a pencil. */
    fun notebook() {
        groundShadow(48f, 90f, 34f)
        // cover + pages
        sticker(rr(18f, 14f, 56f, 72f, 8f), h(0x2F8BEA), lw = 3f)
        flat(rr(26f, 20f, 42f, 60f, 4f), h(0xFFFFFF))
        strokePath(rr(26f, 20f, 42f, 60f, 4f), h(0xA9C9F0), 1.2f)
        for (y in listOf(40f, 50f, 60f, 70f)) line(30f, y, 64f, y, h(0xC8DDF5), 1.4f)
        // spiral rings
        for (y in listOf(26f, 40f, 54f, 68f)) {
            sticker(rr(14f, y, 10f, 5f, 2.5f), h(0xCFD8DC), lw = 1.2f, gloss = false)
        }
        // the sum
        text("2+3", 47f, 31f, 15f, h(0x1E5FB8))
        text("=5", 47f, 46f, 15f, h(0x2FB35A))
        // pencil
        rotated(38f, 70f, 62f) { p ->
            p.sticker(p.rr(64f, 22f, 12f, 52f, 3f), h(0xFFC83D), lw = 2f)
            p.sticker(p.rr(64f, 16f, 12f, 9f, 3f), h(0xFF7B9C), lw = 2f)
            p.flat(p.rr(64f, 24f, 12f, 3f, 0f), h(0xC9CFD6))
            p.sticker(p.poly(64f to 74f, 76f to 74f, 70f to 90f), h(0xF2D2A0), lw = 2f)
            p.flat(p.poly(67.5f to 84f, 72.5f to 84f, 70f to 90f), h(0x3A3A4A))
        }
        twinkle(84f, 20f, 7f)
    }

    /** Exercise: a stopwatch with a speed bolt. */
    fun stopwatch() {
        groundShadow(50f, 90f, 30f)
        val steel = h(0x8EA2B5)
        // crown button
        sticker(rr(42f, 10f, 16f, 9f, 3f), steel, lw = 2f)
        sticker(rr(45f, 17f, 10f, 8f, 2f), steel, lw = 1.6f, gloss = false)
        rotated(40f, 50f, 54f) { p ->
            p.sticker(p.rr(70f, 21f, 10f, 7f, 2.5f), steel, lw = 1.8f)
        }
        // body
        sticker(circle(50f, 56f, 33f), h(0x2A7FE0), lw = 3f)
        flat(circle(50f, 56f, 25.5f), h(0xFFFFFF))
        strokePath(circle(50f, 56f, 25.5f), h(0xBFD9F7), 1.6f)
        // ticks
        for (i in 0 until 12) {
            val a = i * Math.PI.toFloat() / 6f
            val r1 = 21f
            val r2 = if (i % 3 == 0) 17f else 19f
            line(
                50f + r1 * sin(a), 56f - r1 * cos(a), 50f + r2 * sin(a), 56f - r2 * cos(a),
                h(0x2A7FE0), if (i % 3 == 0) 2.2f else 1.4f,
            )
        }
        // time wedge + hand: from 12 o'clock clockwise by 80 degrees (SwiftUI -90 to -10 with clockwise: false)
        val wedge = Path().apply {
            moveTo(50f * u, 56f * u)
            lineTo(50f * u, 33f * u)
            arcTo(Rect(Offset(50f * u, 56f * u), 23f * u), -90f, 80f, false)
            close()
        }
        flat(wedge, h(0xFF8A65).copy(alpha = 0.55f))
        line(50f, 56f, 64f, 42f, h(0xE23A3A), 3.2f)
        sticker(circle(50f, 56f, 4f), h(0xE23A3A), lw = 1.4f, gloss = false)
        // speed bolt
        sticker(
            poly(78f to 66f, 68f to 80f, 75f to 80f, 70f to 94f, 86f to 75f, 78f to 75f, 84f to 66f),
            h(0xFFD23F), lw = 2f,
        )
        twinkle(14f, 24f, 6f)
    }

    /**
     * Math Game Zone: a game controller whose face buttons are the four operations. Parts that overlap are merged into
     * one outline (a path union) before they are stroked, so no inner seams show; parts that sit on top of others are
     * drawn afterwards without a stroke where they would otherwise cross.
     */
    fun controller() {
        groundShadow(50f, 84f, 40f)
        val ink = h(0x4A2FA0)

        // bumpers sit behind the body, so draw them first and let the body cover their lower edge
        sticker(rr(15f, 22f, 22f, 13f, 6f), h(0x8E6CF0), lw = 2f, outline = ink)
        sticker(rr(63f, 22f, 22f, 13f, 6f), h(0x8E6CF0), lw = 2f, outline = ink)

        // body: the slab and both grips as ONE shape
        val slab = rr(7f, 31f, 86f, 40f, 20f)
        val body = Path.combine(
            PathOperation.Union,
            Path.combine(PathOperation.Union, slab, Path().apply { addOval(rect(8f, 50f, 30f, 36f)) }),
            Path().apply { addOval(rect(62f, 50f, 30f, 36f)) },
        )
        sticker(body, h(0xFFFFFF), lw = 3f, outline = ink)
        // a soft shade along the underside of the body, clipped to it
        clipped(body) {
            drawRect(
                h(0xC9BEE8).copy(alpha = 0.55f),
                topLeft = Offset(0f, 66f * u),
                size = Size(100f * u, 30f * u),
            )
        }

        // d-pad: a plus drawn as one merged shape
        val plus = Path.combine(PathOperation.Union, rr(25.5f, 40f, 11f, 30f, 3.5f), rr(16f, 49.5f, 30f, 11f, 3.5f))
        sticker(plus, h(0x4A2FA0), lw = 1.8f, outline = h(0x2E1B70), gloss = false)
        flat(circle(31f, 55f, 3.2f), h(0x6B4FD0))

        // face buttons: + (top), - (right), x (bottom), / (left)
        class FaceButton(val x: Float, val y: Float, val color: Int, val glyph: String)
        val buttons = listOf(
            FaceButton(71f, 41f, 0xFFB800, "+"),
            FaceButton(82f, 52f, 0x2FB35A, "−"),
            FaceButton(71f, 63f, 0x2A7FE0, "×"),
            FaceButton(60f, 52f, 0xE23A3A, "÷"),
        )
        for (b in buttons) {
            sticker(circle(b.x, b.y, 6.8f), h(b.color), lw = 1.8f)
            text(b.glyph, b.x, b.y, 10.5f, Color.White)
        }
        // centre buttons
        flat(circle(48f, 50f, 1.9f), h(0xC9BEE8))
        flat(circle(52f, 50f, 1.9f), h(0xC9BEE8))
        twinkle(90f, 16f, 7.5f)
        twinkle(10f, 20f, 5.5f)
    }

    /** Exam: a test paper with a big A+ and a gold star. */
    fun examPaper() {
        groundShadow(50f, 90f, 30f)
        val paper = Path().apply {
            moveTo(24f * u, 14f * u)
            lineTo(62f * u, 14f * u)
            lineTo(78f * u, 30f * u)
            lineTo(78f * u, 86f * u)
            quadraticTo(78f * u, 90f * u, 72f * u, 90f * u)
            lineTo(30f * u, 90f * u)
            quadraticTo(24f * u, 90f * u, 24f * u, 84f * u)
            close()
        }
        val violet = h(0x6D3AB5)
        sticker(paper, h(0xFFFFFF), lw = 3f, outline = violet)
        // folded corner
        sticker(poly(62f to 14f, 62f to 28f, 78f to 30f), h(0xDCCBF5), lw = 2f, outline = violet, gloss = false)
        // text lines
        listOf(40f, 50f, 60f).forEachIndexed { i, y ->
            line(32f, y, if (i == 2) 52f else 66f, y, h(0xD9CCF0), 2.4f)
        }
        // grade stamp
        sticker(circle(52f, 72f, 13f), h(0xE23A3A), lw = 2.2f, outline = h(0x9E1F1F))
        text("A+", 52f, 72f, 14f, Color.White)
        // gold star sticker
        sticker(poly(starPoints(72f, 26f, 13f, 5.6f)), h(0xFFC83D), lw = 2f, outline = h(0xC98A00))
        twinkle(14f, 30f, 6f)
    }

    /** Custom Challenge: a panel of sliders, each knob a different colour. */
    fun sliders() {
        groundShadow(50f, 88f, 34f)
        sticker(rr(14f, 16f, 72f, 68f, 14f), h(0xF2A91C), lw = 3f)
        flat(rr(21f, 23f, 58f, 54f, 8f), h(0xFFF4D2))
        val xs = listOf(34f, 50f, 66f)
        val ys = listOf(58f, 36f, 50f)
        val cs = listOf(0xEF5350, 0x35A853, 0x2A7FE0)
        xs.forEachIndexed { i, x ->
            sticker(rr(x - 3f, 28f, 6f, 44f, 3f), h(0xD9C9A0), lw = 1.4f, outline = h(0xB59A58), gloss = false)
            // filled part of the track
            flat(rr(x - 3f, ys[i], 6f, 72f - ys[i], 3f), h(cs[i]).copy(alpha = 0.85f))
            sticker(circle(x, ys[i], 8.2f), h(cs[i]), lw = 2f)
            flat(circle(x - 2f, ys[i] - 2.4f, 2.2f), Color.White.copy(alpha = 0.8f))
        }
        twinkle(84f, 14f, 7f)
        twinkle(14f, 80f, 5f)
    }

    /** Store: a shopping bag with a diamond. */
    fun shoppingBag() {
        groundShadow(50f, 90f, 32f)
        // handles
        val handle = Path().apply {
            moveTo(36f * u, 40f * u)
            cubicTo(36f * u, 12f * u, 64f * u, 12f * u, 64f * u, 40f * u)
        }
        strokePath(handle, h(0x9E2B2B), 6.4f, cap = StrokeCap.Round)
        strokePath(handle, h(0xFFD27A), 3.4f, cap = StrokeCap.Round)
        // bag
        val bag = Path().apply {
            moveTo(20f * u, 36f * u)
            lineTo(80f * u, 36f * u)
            lineTo(85f * u, 84f * u)
            quadraticTo(85f * u, 90f * u, 79f * u, 90f * u)
            lineTo(21f * u, 90f * u)
            quadraticTo(15f * u, 90f * u, 15f * u, 84f * u)
            close()
        }
        sticker(bag, h(0xE04545), lw = 3f)
        flat(rr(15f, 36f, 70f, 7f, 0f), h(0xB83030).copy(alpha = 0.5f))
        // diamond (the outline points, nudged one unit left and one down)
        val gem = poly(
            listOf(50f to 50f, 66f to 50f, 72f to 59f, 58f to 80f, 44f to 59f, 36f to 50f)
                .map { (x, y) -> (x - 1f) to (y + 1f) },
        )
        sticker(gem, h(0x5CE1E6), lw = 2.2f, outline = h(0x138A99))
        val facet = Color.White.copy(alpha = 0.6f)
        line(35f, 60f, 71f, 60f, facet, 1.4f)
        line(49f, 51f, 43f, 60f, facet, 1.4f)
        line(57f, 51f, 63f, 60f, facet, 1.4f)
        twinkle(82f, 18f, 7f)
        twinkle(14f, 40f, 5f)
    }

    /** Video Tutorial: a TV with a big play button. */
    fun television() {
        groundShadow(50f, 90f, 30f)
        // antennas
        line(40f, 22f, 30f, 8f, h(0x4B5A6B), 3f)
        line(60f, 22f, 70f, 8f, h(0x4B5A6B), 3f)
        flat(circle(30f, 8f, 3f), h(0xFFC83D))
        flat(circle(70f, 8f, 3f), h(0xFFC83D))
        // feet
        sticker(rr(24f, 78f, 12f, 9f, 3f), h(0x4B5A6B), lw = 1.8f, gloss = false)
        sticker(rr(64f, 78f, 12f, 9f, 3f), h(0x4B5A6B), lw = 1.8f, gloss = false)
        // set
        sticker(rr(10f, 22f, 80f, 58f, 13f), h(0x14A5B8), lw = 3f)
        sticker(rr(17f, 29f, 66f, 44f, 8f), h(0x0F3F5C), lw = 2f, outline = h(0x0A2B3F), gloss = false)
        // screen glow + play
        val glass = rr(19f, 31f, 62f, 40f, 6f)
        fillVertical(glass, h(0x4DD6F0), h(0x1B8FD8), 31f, 71f)
        sticker(poly(43f to 40f, 43f to 62f, 63f to 51f), h(0xFFFFFF), lw = 2f, outline = h(0x0F6C9E))
        // glossy streak on glass
        clipped(glass) {
            drawPath(poly(19f to 31f, 46f to 31f, 30f to 71f, 19f to 71f), Color.White.copy(alpha = 0.14f))
        }
        twinkle(90f, 40f, 6f)
        twinkle(8f, 56f, 5f)
    }

    fun starPoints(cx: Float, cy: Float, outer: Float, inner: Float): List<Pair<Float, Float>> =
        (0 until 10).map { i ->
            val a = i * Math.PI.toFloat() / 5f - Math.PI.toFloat() / 2f
            val r = if (i % 2 == 0) outer else inner
            (cx + r * cos(a)) to (cy + r * sin(a))
        }
}
