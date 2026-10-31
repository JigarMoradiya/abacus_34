package com.jigar.me.ui.view.home.screens.math_game_zone.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.BuildConfig
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbTonePalette
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abBlended
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay
import kotlin.math.floor
import kotlin.math.sin

// The end-of-game card every Math Game Zone game shares (Android twin of iOS Main/Games/GameResultKit.swift): a glossy accent
// panel with a bouncing emoji badge, a title, stars that pop in one by one, a score line, stat chips and the buttons, with
// confetti for a good result. View-level only: the games keep their own scrim, sounds, record-keeping and button actions and
// just hand this card their numbers.

/** Debug builds only: lets the lead render a game's real result overlay (`--ez ab_show_result true`). Always false in release. */
object AbDebugFlags {
    @Volatile var showResult: Boolean = false
    val showResultEnabled: Boolean get() = BuildConfig.DEBUG && showResult
}

@Composable
fun GameResultCard(
    title: String,
    emoji: String,
    accent: ButtonType = ButtonType.ORANGE,
    stars: Int,
    score: String? = null,
    /** A wider card, for a row of three buttons (Next Level, Play Again, Home). */
    wide: Boolean = false,
    stats: @Composable () -> Unit = {},
    buttons: @Composable () -> Unit = {},
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val face = AbTonePalette.of(accent.abTone(), colors).face.abReadable(3f)
    val screenW = LocalConfiguration.current.screenWidthDp.dp
    val cardW = if (wide) minOf(screenW * 0.76f, 780.dp * s) else minOf(screenW * 0.55f, 560.dp * s)

    val scale = remember { Animatable(0.6f) }
    LaunchedEffect(Unit) { scale.animateTo(1f, spring(dampingRatio = 0.62f, stiffness = Spring.StiffnessMediumLow)) }

    // confetti falls over the whole screen but is only DRAWN around the card, so it never changes the card's own size
    var confettiT by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(stars) {
        if (stars >= 2) {
            val start = withFrameNanosCompat()
            while (true) { confettiT = (withFrameNanosCompat() - start) / 1_000_000_000f }
        }
    }
    val screenH = LocalConfiguration.current.screenHeightDp.dp

    Box(
        Modifier
            .width(cardW)
            .graphicsLayer { scaleX = scale.value; scaleY = scale.value; alpha = ((scale.value - 0.6f) / 0.4f).coerceIn(0f, 1f) }
            .drawWithContent {
                drawContent()
                if (stars >= 2) {
                    val w = screenW.toPx(); val h = screenH.toPx()
                    translate(-(w - size.width) / 2f, -(h - size.height) / 2f) { drawConfetti(confettiT, w, h) }
                }
            },
        contentAlignment = Alignment.TopCenter,
    ) {
        val shape = RoundedCornerShape(AbShapes.lg * s)
        val drop = 8.dp * s
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.dp * s),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = drop)
                .shadow(18.dp, shape, clip = false, ambientColor = face, spotColor = face)
                .drawBehind {
                    drawRoundRect(face.abDarkened(0.32f), Offset(0f, drop.toPx()), size, CornerRadius(AbShapes.lg.toPx() * s))
                }
                .background(Brush.verticalGradient(listOf(face.abBlended(Color.White, 0.2f), face.abDarkened(0.08f))), shape)
                .drawBehind {
                    drawRoundRect(
                        Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.28f), Color.Transparent), endY = size.height * 0.5f),
                        cornerRadius = CornerRadius(AbShapes.lg.toPx() * s),
                    )
                }
                .border(3.dp, Color.White.copy(alpha = 0.5f), shape)
                .padding(horizontal = 26.dp * s, vertical = 12.dp * s),
        ) {
            Spacer(Modifier.height(22.dp * s))
            Text(
                title,
                style = AbTypography.fixed(28f * s, FontWeight.Black).copy(
                    shadow = androidx.compose.ui.graphics.Shadow(face.abDarkened(0.45f), Offset(0f, 5f), 0f),
                ),
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            GameResultStars(count = stars, size = 30.dp * s)
            if (score != null) {
                Text(
                    score,
                    style = AbTypography.fixed(22f * s, FontWeight.Black).copy(
                        shadow = androidx.compose.ui.graphics.Shadow(Color.Black.copy(alpha = 0.22f), Offset(0f, 4f), 0f),
                    ),
                    color = Color.White,
                    maxLines = 1,
                )
            }
            stats()
            Box(Modifier.padding(top = 4.dp * s)) { buttons() }
        }
        GameResultBadge(emoji = emoji, face = face, modifier = Modifier.offset(y = (-22).dp * s))
    }
}

@Composable
private fun GameResultBadge(emoji: String, face: Color, modifier: Modifier = Modifier) {
    val s = LocalAbScale.current.factor
    val transition = rememberInfiniteTransition(label = "badge")
    val bob by transition.animateFloat(-3f, 3f, infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Reverse), label = "bob")
    val tilt by transition.animateFloat(-6f, 6f, infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse), label = "tilt")
    Box(
        modifier
            .size(58.dp * s)
            .graphicsLayer { translationY = bob * density * s; rotationZ = tilt }
            .drawBehind {
                drawCircle(face.abDarkened(0.3f), radius = size.minDimension / 2f, center = Offset(size.width / 2f, size.height / 2f + 4.dp.toPx() * s))
            }
            .background(Color.White, CircleShape)
            .border(3.dp, face.copy(alpha = 0.35f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(emoji, fontSize = (30f * s).sp)
    }
}

/** Three stars that pop in one after another; the earned ones are gold and sway, the rest are a soft ghost. */
@Composable
fun GameResultStars(count: Int, size: Dp, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(size * 0.2f), verticalAlignment = Alignment.Bottom) {
        for (i in 0 until 3) {
            GameResultStar(index = i, earned = i < count, size = size * (if (i == 1) 1.25f else 1f))
        }
    }
}

@Composable
private fun GameResultStar(index: Int, earned: Boolean, size: Dp) {
    val pop = remember { Animatable(0.01f) }
    LaunchedEffect(Unit) {
        delay(200L + index * 250L)
        pop.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMediumLow))
    }
    val transition = rememberInfiniteTransition(label = "star")
    val sway by transition.animateFloat(-5f, 5f, infiniteRepeatable(tween(1300 + index * 150, easing = LinearEasing), RepeatMode.Reverse), label = "sway")
    Box(
        Modifier
            .size(size)
            .graphicsLayer {
                scaleX = pop.value
                scaleY = pop.value
                rotationZ = (if (earned) sway else 0f) + (1f - pop.value.coerceAtMost(1f)) * -50f
            },
        contentAlignment = Alignment.Center,
    ) {
        if (earned) {
            Text("★", fontSize = (size.value * 1.15f).sp, color = Color(0xFFB45309), modifier = Modifier.offset(y = size * 0.07f))
            Text("★", fontSize = (size.value * 1.15f).sp, color = Color(0xFFFFC21A))
        } else {
            Text("★", fontSize = (size.value * 1.15f).sp, color = Color.White.copy(alpha = 0.32f))
        }
    }
}

/** One number on a result card: an emoji and a value on a white pill. */
@Composable
fun GameStatChip(emoji: String, value: String, color: Color = Color(0xFF1E2470), modifier: Modifier = Modifier) {
    val s = LocalAbScale.current.factor
    Row(
        modifier
            .shadow(3.dp, CircleShape)
            .background(Color.White, CircleShape)
            .padding(horizontal = 12.dp * s, vertical = 4.dp * s),
        horizontalArrangement = Arrangement.spacedBy(6.dp * s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(emoji, fontSize = (19f * s).sp)
        Text(value, style = AbTypography.fixed(19f * s, FontWeight.Black), color = color, maxLines = 1)
    }
}

private val ConfettiColors = listOf(
    Color(0xFFFF5C8A), Color(0xFFFFC53D), Color(0xFF2FC48D), Color(0xFF4D9FFF), Color(0xFFA58CFF), Color(0xFFFF8A3D),
)

private fun unit(x: Double): Double = x - floor(x)

/** Paper pieces that fall once over an [areaW] x [areaH] area; [t] is seconds since the burst started (they are gone after ~4 s). */
fun DrawScope.drawConfetti(t: Float, areaW: Float, areaH: Float) {
    val k = density   // iOS sizes are in points = dp, so one dp is `density` pixels
    for (i in 0 until 40) {
        val seed = (i + 1).toDouble()
        val x = (unit(sin(seed * 12.9898) * 43758.5453) * areaW).toFloat()
        val delaySec = (unit(sin(seed * 78.233) * 43758.5453) * 0.7).toFloat()
        val speed = (140 + unit(sin(seed * 39.425) * 43758.5453) * 170).toFloat() * k
        val sway = (sin(t * 3 + seed) * 18).toFloat() * k
        val y = (t - delaySec) * speed - 24f
        if (t > delaySec && y < areaH + 24f) {
            val w = ((7 + unit(seed * 3.7) * 7) * k).toFloat()
            translate(x + sway, y) {
                rotate((t * 220 + seed * 40).toFloat(), Offset.Zero) {
                    drawRoundRect(
                        ConfettiColors[i % ConfettiColors.size],
                        topLeft = Offset(-w / 2f, -w / 3f),
                        size = Size(w, w * 0.62f),
                        cornerRadius = CornerRadius(1.5f * density),
                    )
                }
            }
        }
    }
}

private suspend fun withFrameNanosCompat(): Long = androidx.compose.runtime.withFrameNanos { it }
