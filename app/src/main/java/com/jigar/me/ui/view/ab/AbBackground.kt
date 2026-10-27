package com.jigar.me.ui.view.ab

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sin

/** One world per learning level (Level 1 = meadow, 2 = sunset, 3 = lagoon, 4 = twilight). */
enum class AbWorld {
    Meadow, Sunset, Lagoon, Twilight;

    data class Palette(val skyTop: Color, val skyBottom: Color, val hillFar: Color, val hillNear: Color, val glow: Color)

    fun palette(isDark: Boolean): Palette {
        fun p(a: Long, b: Long, c: Long, d: Long, e: Long) = Palette(hex(a), hex(b), hex(c), hex(d), hex(e))
        return when (this) {
            Meadow -> if (isDark) p(0x0D0B2E, 0x2B2468, 0x2A3B7A, 0x1F2C66, 0xFFF4C2) else p(0x7EC8FF, 0xFFF1DC, 0xA9E3B4, 0x7FD18F, 0xFFE08A)
            Sunset -> if (isDark) p(0x1A0F3A, 0x4A2060, 0x4A2A7A, 0x371F63, 0xFFE3B0) else p(0xFFA9C7, 0xFFE9C9, 0xDDB0F2, 0xBE90E8, 0xFFD27A)
            Lagoon -> if (isDark) p(0x06232E, 0x0E4650, 0x145C63, 0x0E4850, 0xE8FFF8) else p(0x6FE0D4, 0xE6FBFF, 0x8BDDCF, 0x54C4B4, 0xFFF3A8)
            Twilight -> if (isDark) p(0x120C3A, 0x37206E, 0x3A2A80, 0x2C1F68, 0xFFF0C8) else p(0xB9A6FF, 0xFBE8FF, 0xCDB8FF, 0xA98CFA, 0xFFE6A0)
        }
    }

    private fun hex(v: Long) = Color(((v shr 16) and 0xFF) / 255f, ((v shr 8) and 0xFF) / 255f, (v and 0xFF) / 255f)
}

/**
 * Screen background of restyled screens: a quiet painted world. Sky, a few clouds (day) or stars (night), two rows of
 * rolling hills and some large faint beads. Deliberately pale, low-contrast and completely still, so it never competes
 * with the content in front: the foreground is always the first thing the eye lands on.
 */
@Composable
fun AbBackground(world: AbWorld = AbWorld.Meadow, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    val dark = colors.isDark
    val raw = world.palette(dark)
    // Wash every world colour toward the plain screen colour so the scene stays a whisper.
    val soften = if (dark) 0.45f else 0.62f
    val base = colors.bgPrimary
    val skyTop = raw.skyTop.abBlended(base, soften)
    val skyBottom = raw.skyBottom.abBlended(base, soften * 0.7f)
    val hillFar = raw.hillFar.abBlended(base, soften)
    val hillNear = raw.hillNear.abBlended(base, soften * 0.9f)
    val beadColors = colors.placeValue

    Canvas(modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        drawRect(Brush.verticalGradient(listOf(skyTop, skyBottom)))

        if (dark) {
            // A sprinkle of dim, still stars.
            for (i in 0 until 28) {
                val x = rnd(i, 1.0) * w
                val y = rnd(i, 2.0) * h * 0.65f
                val r = 0.7f + rnd(i, 3.0) * 1.2f
                drawCircle(Color.White.copy(alpha = 0.22f + rnd(i, 4.0) * 0.2f), r * density, Offset(x, y))
            }
        } else {
            // Three soft clouds.
            listOf(Triple(0.12f, 0.16f, 0.9f), Triple(0.52f, 0.30f, 0.7f), Triple(0.78f, 0.13f, 0.8f)).forEach { (cx, cy, cs) ->
                drawCloud(Offset(cx * w, cy * h), cs * min(w, h) / 420f)
            }
        }

        // Large faint beads, scattered (the abacus motif, barely there).
        data class B(val x: Float, val y: Float, val s: Float, val ci: Int, val rot: Float)
        listOf(B(0.07f, 0.52f, 0.09f, 0, -8f), B(0.34f, 0.60f, 0.075f, 4, 5f), B(0.64f, 0.56f, 0.085f, 1, -4f), B(0.93f, 0.62f, 0.08f, 6, 7f)).forEach { b ->
            val bw = min(w, h) * b.s * 1.6f
            val bh = bw * 0.62f
            translate(b.x * w, b.y * h) {
                rotate(b.rot, Offset.Zero) {
                    drawPath(abBeadPath(Rect(-bw / 2, -bh / 2, bw / 2, bh / 2)), beadColors[b.ci % beadColors.size].copy(alpha = if (dark) 0.08f else 0.10f))
                }
            }
        }

        // Hills: far row, then near row.
        drawPath(hill(w, h, h * 0.82f, h * 0.03f, 0.0075f, 0.8f), hillFar)
        drawPath(hill(w, h, h * 0.90f, h * 0.035f, 0.0058f, 3.1f), hillNear)

        // Final wash: pulls the whole scene back another step.
        drawRect((if (dark) Color.Black else Color.White).copy(alpha = if (dark) 0.12f else 0.22f))
    }
}

private fun hill(w: Float, h: Float, base: Float, amp: Float, freq: Float, phase: Float): Path = Path().apply {
    moveTo(0f, h)
    var x = 0f
    while (x <= w + 8f) { lineTo(x, base + amp * sin(x * freq + phase)); x += 8f }
    lineTo(w, h)
    close()
}

private fun DrawScope.drawCloud(c: Offset, scale: Float) {
    listOf(floatArrayOf(0f, 0f, 120f, 44f), floatArrayOf(-38f, 8f, 70f, 34f), floatArrayOf(40f, 10f, 76f, 34f), floatArrayOf(6f, -14f, 64f, 40f)).forEach { p ->
        drawOval(
            Color.White.copy(alpha = 0.55f),
            topLeft = Offset(c.x + p[0] * scale - p[2] * scale / 2f, c.y + p[1] * scale - p[3] * scale / 2f),
            size = Size(p[2] * scale, p[3] * scale),
        )
    }
}

/** Deterministic 0..1 noise for static scatter (stars). */
private fun rnd(i: Int, k: Double): Float {
    val v = sin(i * 12.9898 + k * 78.233) * 43758.5453
    return (v - floor(v)).toFloat()
}
