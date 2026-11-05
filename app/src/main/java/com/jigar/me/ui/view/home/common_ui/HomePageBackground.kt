package com.jigar.me.ui.view.home.common_ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


private data class BokehOrb(
    val cx: Float, val cy: Float,
    val radius: Float,
    val color: Color,
    val speed: Float, val phase: Float, val drift: Float
)

private data class FloatingSparkle(
    val cx: Float, val cy: Float,
    val size: Float,
    val speed: Float, val phase: Float
)

private val orbs = listOf(
    BokehOrb(0.10f, 0.20f, 240f, Color(0xFFA78BFA), 0.10f, 0.0f,  28f),
    BokehOrb(0.82f, 0.15f, 200f, Color(0xFF60A5FA), 0.08f, 1.5f,  22f),
    BokehOrb(0.55f, 0.80f, 220f, Color(0xFFF472B6), 0.11f, 0.8f,  26f),
    BokehOrb(0.18f, 0.75f, 175f, Color(0xFFFCD34D), 0.07f, 2.2f,  18f),
    BokehOrb(0.90f, 0.60f, 165f, Color(0xFF34D399), 0.12f, 1.0f,  20f),
    BokehOrb(0.48f, 0.38f, 185f, Color(0xFFC084FC), 0.09f, 3.1f,  15f),
)

private val sparkles = listOf(
    FloatingSparkle(0.05f, 0.10f, 0.028f, 0.40f, 0.0f),
    FloatingSparkle(0.93f, 0.08f, 0.022f, 0.35f, 1.2f),
    FloatingSparkle(0.28f, 0.92f, 0.030f, 0.45f, 0.5f),
    FloatingSparkle(0.72f, 0.88f, 0.020f, 0.38f, 2.0f),
    FloatingSparkle(0.96f, 0.42f, 0.024f, 0.42f, 1.7f),
    FloatingSparkle(0.07f, 0.58f, 0.018f, 0.30f, 3.0f),
    FloatingSparkle(0.50f, 0.04f, 0.026f, 0.50f, 0.3f),
    FloatingSparkle(0.76f, 0.52f, 0.016f, 0.55f, 2.5f),
    FloatingSparkle(0.38f, 0.15f, 0.020f, 0.36f, 1.1f),
    FloatingSparkle(0.62f, 0.65f, 0.022f, 0.44f, 0.7f),
)

@Composable
fun HomePageBackground(modifier: Modifier = Modifier) {
    var time by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        val startNanos = withFrameNanos { it }
        while (true) {
            withFrameNanos { frameNanos ->
                time = (frameNanos - startNanos) / 1_000_000_000f
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        // Base 3-stop diagonal gradient
        drawRect(
            brush = Brush.linearGradient(
                colorStops = arrayOf(
                    0.00f to Color(0xFFF7F2FF),
                    0.45f to Color(0xFFFDFCFF),
                    1.00f to Color(0xFFEFF6FF)
                ),
                start = Offset(0f, 0f),
                end   = Offset(size.width, size.height)
            )
        )

        drawOrbs(time)
        drawSparkles(time)
    }
}

private fun DrawScope.drawOrbs(time: Float) {
    for (orb in orbs) {
        val cx = orb.cx * size.width  + sin(time * orb.speed * PI.toFloat() + orb.phase) * orb.drift
        val cy = orb.cy * size.height + cos(time * orb.speed * PI.toFloat() + orb.phase + 1f) * orb.drift * 0.6f

        drawCircle(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0.00f to orb.color.copy(alpha = 0.28f),
                    0.60f to orb.color.copy(alpha = 0.10f),
                    1.00f to orb.color.copy(alpha = 0.00f)
                ),
                center = Offset(cx, cy),
                radius = orb.radius
            ),
            radius = orb.radius,
            center = Offset(cx, cy)
        )
    }
}

private fun DrawScope.drawSparkles(time: Float) {
    val color     = Color(0xFF6D28D9)
    val minDim    = min(size.width, size.height)
    val amplitude = size.height * 0.025f

    for (sp in sparkles) {
        val cx = sp.cx * size.width
        val cy = sp.cy * size.height + sin(time * sp.speed * PI.toFloat() + sp.phase) * amplitude
        val r  = sp.size * minDim
        val ir = r * 0.25f

        val path = Path()
        for (i in 0 until 8) {
            val angle  = i * PI.toFloat() / 4f - PI.toFloat() / 4f
            val radius = if (i % 2 == 0) r else ir
            val x = cx + radius * cos(angle)
            val y = cy + radius * sin(angle)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, color = color.copy(alpha = 0.11f))
    }
}

