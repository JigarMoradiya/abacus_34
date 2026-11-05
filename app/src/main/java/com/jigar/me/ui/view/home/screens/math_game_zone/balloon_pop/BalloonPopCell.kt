package com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.jetpack.utils.ui.extensions.appScale
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.abBlended
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop.components.MakeTenBalloon
import com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop.components.balloonPopPalette
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
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
    // A little jelly squash so the balloon feels springy as it floats.
    val jelly = sin(elapsed * 4.2f / balloon.wobbleSpeed) * 0.025f

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
        Box(
            modifier = Modifier.graphicsLayer {
                alpha = if (balloon.isPopping) 0f else 1f
                scaleX = 1f + jelly
                scaleY = 1f - jelly
            }
        ) {
            BalloonPopCandyBalloon(
                color = balloon.color,
                text = "${balloon.value}",
                widthDp = BALLOON_W,
                fontSp = 30f * appScale(),
                elapsed = elapsed,
                isShaken = isShaking,
                bodyModifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onTap() }
            )
        }

        if (balloon.isPopping) {
            // The effects are drawn around the centre of this box. `size(200.dp)` is squeezed to the cell (94 x 156) by its parent, so
            // the centre is the cell's middle; lift it by half the rope to land on the balloon's body (twin of iOS `.offset(y: -25)`).
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = (-ROPE_H / 2f).dp)
            ) {
                BalloonBurst(color = balloon.color)
                BalloonPopStars(color = balloon.color)
            }
        }
    }
}

// ── Candy balloon ─────────────────────────────────────────────────────────────────────────────────────────────────────

/** The shades one balloon colour needs: a bright shine, a deeper edge and a dark ink for the face, string and number outline. */
private class BalloonPopTones(val base: Color) {
    val shine: Color = base.abBlended(Color.White, 0.45f)
    val deep: Color = base.abDarkened(0.28f)
    val ink: Color = base.abDarkened(0.6f)
}

/**
 * A cute glossy balloon (twin of iOS MakeTenCandyBalloon): radial candy shading, a shine, a knot, a little face, a sticker-style
 * number and a string that wiggles with [elapsed]. Drawn on the same 94 x 106 body + 20 x 50 rope frames as the plain balloon it
 * replaces ([widthDp] scales everything, 94 = the game's size). Used by the play field and the home preview. [bodyModifier] goes
 * on the body frame (the play field puts the tap handler there).
 */
@Composable
internal fun BalloonPopCandyBalloon(
    color: Color,
    text: String,
    widthDp: Float,
    fontSp: Float,
    elapsed: Float,
    isShaken: Boolean = false,
    bodyModifier: Modifier = Modifier
) {
    val tones = remember(color) { BalloonPopTones(color) }
    val k = widthDp / 94f
    val dens = LocalDensity.current.density
    val numberStyle = AbTypography.fixed(fontSp, FontWeight.Black)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = bodyModifier.size(widthDp.dp, (106f * k).dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val d = density
                val w = size.width
                val h = size.height
                val c = Offset(w / 2f, h / 2f)
                // body: radial candy shading, then a soft rim
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(tones.shine, tones.base, tones.deep),
                        center = Offset(w * 0.34f, h * 0.28f),
                        radius = 80f * k * d
                    ),
                    topLeft = Offset.Zero,
                    size = size
                )
                drawOval(Color.White.copy(alpha = 0.35f), topLeft = Offset.Zero, size = size, style = Stroke(width = 1.5f * d))
                // glossy shine, upper left
                val shinePivot = Offset(c.x - 26f * k * d, c.y - 24f * k * d)
                rotate(degrees = 24f, pivot = shinePivot) {
                    drawOval(
                        Color.White.copy(alpha = 0.6f),
                        topLeft = Offset(shinePivot.x - 8.5f * k * d, shinePivot.y - 14f * k * d),
                        size = Size(17f * k * d, 28f * k * d)
                    )
                }
                drawCircle(Color.White.copy(alpha = 0.75f), radius = 3f * k * d, center = Offset(c.x - 20f * k * d, c.y - 6f * k * d))
                // face: two shiny eyes, rosy cheeks and a smile (a small "oh" when the balloon was tapped by mistake)
                val faceY = c.y - 22f * k * d
                val eyeW = 6.5f * k * d
                val eyeH = 9f * k * d
                val eyeY = faceY - 6f * k * d
                for (side in 0 until 2) {
                    val ex = c.x + (if (side == 0) -1f else 1f) * 13.25f * k * d
                    drawRoundRect(
                        tones.ink,
                        topLeft = Offset(ex - eyeW / 2f, eyeY - eyeH / 2f),
                        size = Size(eyeW, eyeH),
                        cornerRadius = CornerRadius(eyeW / 2f, eyeW / 2f)
                    )
                    drawCircle(Color.White, radius = 1.3f * k * d, center = Offset(ex + 0.8f * k * d, eyeY - eyeH / 2f + 1.4f * k * d + 1.3f * k * d))
                    val cheekX = c.x + (if (side == 0) -1f else 1f) * 24.5f * k * d
                    drawOval(
                        Color.White.copy(alpha = 0.4f),
                        topLeft = Offset(cheekX - 4.5f * k * d, faceY + 5f * k * d - 3f * k * d),
                        size = Size(9f * k * d, 6f * k * d)
                    )
                }
                if (isShaken) {
                    drawOval(
                        tones.ink,
                        topLeft = Offset(c.x - 3.5f * k * d, faceY + 9f * k * d - 4f * k * d),
                        size = Size(7f * k * d, 8f * k * d)
                    )
                } else {
                    val smileTop = faceY + 8f * k * d - 3f * k * d
                    val smile = Path().apply {
                        moveTo(c.x - 7f * k * d, smileTop)
                        quadraticBezierTo(c.x, smileTop + 10.8f * k * d, c.x + 7f * k * d, smileTop)
                    }
                    drawPath(smile, tones.ink, style = Stroke(width = max(1.5f, 2f * k) * d, cap = StrokeCap.Round))
                }
                // the tied-off knot at the bottom: a small triangle, apex up
                val knot = Path().apply {
                    moveTo(c.x, h - 3f * k * d)
                    lineTo(c.x + 6f * k * d, h + 6f * k * d)
                    lineTo(c.x - 6f * k * d, h + 6f * k * d)
                    close()
                }
                drawPath(knot, tones.deep)
            }
            // sticker-style number: white with an ink outline on two diagonals
            Text(
                text,
                color = Color.White,
                style = numberStyle.copy(shadow = Shadow(tones.ink.copy(alpha = 0.9f), Offset(1.4f * dens, 1.4f * dens), 0f)),
                modifier = Modifier.offset(y = (12f * k).dp)
            )
            Text(
                text,
                color = Color.White,
                style = numberStyle.copy(shadow = Shadow(tones.ink.copy(alpha = 0.9f), Offset(-1.4f * dens, -1.4f * dens), 0f)),
                modifier = Modifier.offset(y = (12f * k).dp)
            )
        }
        // the string: the wave travels down it and grows toward the free end
        Canvas(modifier = Modifier.size((20f * k).dp, (50f * k).dp)) {
            val steps = 16
            val phase = elapsed * 4f
            val path = Path()
            path.moveTo(size.width / 2f, 0f)
            for (i in 1..steps) {
                val f = i / steps.toFloat()
                val sway = sin(f * 2.6f * PI.toFloat() - phase) * size.width * 0.5f * f
                path.lineTo(size.width / 2f + sway, f * size.height)
            }
            drawPath(
                path, tones.deep,
                style = Stroke(width = max(1.5f, 2.2f * k) * density, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}

// ── Pop effects ───────────────────────────────────────────────────────────────────────────────────────────────────────

/** Confetti and stars that fly out of a popped balloon, on top of the dissolving dot cloud. Clock-driven. */
@Composable
private fun BalloonPopStars(color: Color) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) { progress.animateTo(1f, tween(650, easing = LinearEasing)) }
    Canvas(modifier = Modifier.size(200.dp)) {
        val p = progress.value
        if (p < 1f) {
            val d = density
            val eased = 1f - (1f - p) * (1f - p)
            val ring = (30f + 150f * eased) * d
            drawCircle(
                Color.White.copy(alpha = 1f - p),
                radius = ring / 2f,
                center = center,
                style = Stroke(width = 7f * (1f - p) * d)
            )
            for (i in 0 until 12) {
                val angle = i / 12f * 2f * PI.toFloat() + 0.25f
                val reach = 52f + (i % 3) * 22f
                val dx = cos(angle) * reach * eased
                val dy = sin(angle) * reach * eased + 60f * p * p      // a little gravity
                val tint: Color = when (i % 3) {
                    0 -> Color.White
                    1 -> color
                    else -> balloonPopPalette[(i * 3) % balloonPopPalette.size]
                }.copy(alpha = 1f - p * p)
                val pos = Offset(center.x + dx * d, center.y + dy * d)
                val spin = p * 260f * (if (i % 2 == 0) 1f else -1f)
                val pieceScale = 1f - 0.45f * p
                rotate(degrees = spin, pivot = pos) {
                    if (i % 2 == 0) {
                        balloonPopStar(pos, (15f + (i % 3) * 3f) * 0.5f * d * pieceScale, tint)
                    } else {
                        val pw = 8f * d * pieceScale
                        val ph = 12f * d * pieceScale
                        drawRoundRect(
                            tint,
                            topLeft = Offset(pos.x - pw / 2f, pos.y - ph / 2f),
                            size = Size(pw, ph),
                            cornerRadius = CornerRadius(2f * d, 2f * d)
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.balloonPopStar(center: Offset, outer: Float, color: Color) {
    val inner = outer * 0.45f
    val path = Path()
    for (k in 0 until 10) {
        val r = if (k % 2 == 0) outer else inner
        val a = -PI / 2.0 + k * PI / 5.0
        val x = center.x + (cos(a) * r).toFloat()
        val y = center.y + (sin(a) * r).toFloat()
        if (k == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color)
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
