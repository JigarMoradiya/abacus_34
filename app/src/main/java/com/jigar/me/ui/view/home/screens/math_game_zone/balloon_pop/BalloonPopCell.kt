package com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop.components.MakeTenBalloon
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

private const val BALLOON_W = 94f
private const val BALLOON_H = 106f
private const val ROPE_H = 50f

@Composable
fun BalloonPopCell(
    balloon: MakeTenBalloon,
    containerWidthPx: Float,
    containerHeightPx: Float,
    isShaking: Boolean,
    onTap: () -> Unit,
    onExitTop: () -> Unit
) {
    val density = LocalDensity.current

    // Rise/wobble computed from the clock every frame — never a cancellable
    // animation. (On iOS a withAnimation on position detached the text/burst.)
    var elapsed by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(balloon.id) {
        val start = withFrameNanos { it }
        while (true) {
            withFrameNanos { now -> elapsed = (now - start) / 1_000_000_000f }
        }
    }
    LaunchedEffect(balloon.id) {
        delay((balloon.speed * 1000).toLong())
        onExitTop()
    }

    // Wrong-tap shake
    val shakeX = remember { Animatable(0f) }
    LaunchedEffect(isShaking) {
        if (isShaking) {
            repeat(5) {
                shakeX.animateTo(-8f, tween(60))
                shakeX.animateTo(8f, tween(60))
            }
            shakeX.animateTo(0f, tween(60))
        } else {
            shakeX.snapTo(0f)
        }
    }

    val riseProgress = (elapsed / balloon.speed).coerceIn(0f, 1f)
    val yFraction = 1.06f - riseProgress * 1.36f      // 1.06 (below edge) → -0.30 (above top)
    val wobbleAngle = if (balloon.isPopping) 0f else sin(elapsed * 3f / balloon.wobbleSpeed) * 4f

    val cellWidthPx = with(density) { BALLOON_W.dp.toPx() }
    val bodyCenterPx = with(density) { (BALLOON_H / 2f).dp.toPx() }
    val cx = balloon.xPosition * containerWidthPx
    val cy = yFraction * containerHeightPx
    val offX = (cx - cellWidthPx / 2f + shakeX.value).roundToInt()
    val offY = (cy - bodyCenterPx).roundToInt()

    Box(
        modifier = Modifier
            .offset { IntOffset(offX, offY) }
            .width(BALLOON_W.dp)
            .height((BALLOON_H + ROPE_H).dp)
            .graphicsLayer { rotationZ = wobbleAngle }
    ) {
        // Balloon body + rope (hidden instantly when popping — real balloons don't shrink)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer { alpha = if (balloon.isPopping) 0f else 1f }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(BALLOON_W.dp, BALLOON_H.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTap() }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawOval(
                        brush = Brush.verticalGradient(listOf(Color.White, balloon.color)),
                        topLeft = Offset.Zero,
                        size = size
                    )
                }
                Text(
                    text = "${balloon.value}",
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                    fontSize = 30.sp.scaled()
                )
            }
            Canvas(modifier = Modifier.size(20.dp, ROPE_H.dp)) {
                val path = Path()
                val amplitude = size.width * 0.5f
                val seg = size.height / 4f
                path.moveTo(size.width / 2f, 0f)
                for (i in 0 until 4) {
                    val y1 = i * seg
                    val y2 = y1 + seg
                    val dir = if (i % 2 == 0) 1f else -1f
                    path.quadraticBezierTo(
                        size.width / 2f + amplitude * dir, (y1 + y2) / 2f,
                        size.width / 2f, y2
                    )
                }
                drawPath(path, color = Color.Black, style = Stroke(width = 2.dp.toPx()))
            }
        }

        if (balloon.isPopping) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = (BALLOON_H / 2f - 100f).dp)
            ) {
                BalloonBurst(color = balloon.color)
            }
        }
    }
}

// Particle-dissolve pop matching the reference GIF: the balloon becomes ~180
// tiny dots of its own colour that hold the balloon silhouette, then the dot
// cloud expands, drifts upward and fades. Clock-driven so it can't be skipped.
@Composable
private fun BalloonBurst(color: Color) {
    data class Dot(
        val ux: Float, val uy: Float, val size: Float,
        val shade: Float, val driftX: Float, val driftY: Float
    )

    val dots = remember {
        List(180) {
            val angle = Random.nextFloat() * (2f * Math.PI.toFloat())
            val radius = sqrt(Random.nextFloat())
            Dot(
                ux = cos(angle) * radius,
                uy = sin(angle) * radius,
                size = Random.nextFloat() * (6.5f - 3.0f) + 3.0f,
                shade = Random.nextFloat() * (1.0f - 0.55f) + 0.55f,
                driftX = Random.nextFloat() * 44f - 22f,
                driftY = Random.nextFloat() * 38f - 28f
            )
        }
    }

    var progress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (progress < 1f) {
            withFrameNanos { now ->
                progress = ((now - start) / 1_000_000_000f / 0.6f).coerceIn(0f, 1f)
            }
        }
    }

    val eased = 1f - (1f - progress) * (1f - progress)
    val expansion = 1f + 0.85f * eased
    val alpha = 1f - progress * progress

    Canvas(modifier = Modifier.size(200.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val bodyHalfX = 40.dp.toPx()
        val bodyHalfY = 45.dp.toPx()
        dots.forEach { dot ->
            val x = center.x + dot.ux * bodyHalfX * expansion + dot.driftX.dp.toPx() * eased
            val y = center.y + dot.uy * bodyHalfY * expansion + dot.driftY.dp.toPx() * eased
            drawCircle(
                color = color.copy(alpha = (dot.shade * alpha).coerceIn(0f, 1f)),
                radius = dot.size.dp.toPx() / 2f,
                center = Offset(x, y)
            )
        }
    }
}
