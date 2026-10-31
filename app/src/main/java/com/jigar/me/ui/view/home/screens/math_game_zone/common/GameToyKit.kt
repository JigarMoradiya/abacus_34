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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.ab.AbColors
import com.jigar.me.ui.view.ab.abChunkyFace
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abReadable
import kotlinx.coroutines.delay
import kotlin.math.abs

// The "toy box" look and motion for the Math Game Zone's content (Android twin of iOS Main/Games/GameToyKit.swift): chunky candy
// tiles that pop in, bounce when they change, wobble when something is wrong and breathe while they wait to be tapped.
// View-level only, no game logic.

// ── Motion ────────────────────────────────────────────────────────────────────────────────────────────────────────────

/** Springs in from small when it first appears. */
fun Modifier.abPopIn(delayMs: Int = 0): Modifier = composed {
    val scale = remember { Animatable(0.4f) }
    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        scale.animateTo(1f, spring(dampingRatio = 0.58f, stiffness = Spring.StiffnessMediumLow))
    }
    graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
        alpha = ((scale.value - 0.4f) / 0.6f).coerceIn(0f, 1f)
    }
}

/** Shakes side to side each time [whenTrue] turns true (a wrong answer, a clash). */
fun Modifier.abWobble(whenTrue: Boolean): Modifier = composed {
    val x = remember { Animatable(0f) }
    val density = LocalDensity.current.density
    LaunchedEffect(whenTrue) {
        if (whenTrue) {
            repeat(4) { i -> x.animateTo(if (i % 2 == 0) 9f else -9f, tween(55)) }
            x.animateTo(0f, tween(55))
        }
    }
    graphicsLayer { translationX = x.value * density }
}

/** Pops big and settles back each time [value] changes (a digit placed, a score going up). The first value does not pop. */
fun Modifier.abBounce(value: Any?, size: Float = 1.3f): Modifier = composed {
    val scale = remember { Animatable(1f) }
    var first by remember { mutableStateOf(true) }
    LaunchedEffect(value) {
        if (first) {
            first = false
        } else {
            scale.animateTo(size, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessHigh))
            scale.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium))
        }
    }
    graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
    }
}

/** A slow idle pulse, so something that wants a tap looks alive. */
fun Modifier.abBreathe(amount: Float = 0.035f, periodMs: Int = 1600): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "breathe")
    val pulse by transition.animateFloat(
        initialValue = 1f - amount,
        targetValue = 1f + amount,
        animationSpec = infiniteRepeatable(tween(periodMs / 2, easing = LinearEasing), RepeatMode.Reverse),
        label = "breathe",
    )
    graphicsLayer {
        scaleX = pulse
        scaleY = pulse
    }
}

// ── Look ──────────────────────────────────────────────────────────────────────────────────────────────────────────────

/** A chunky candy tile: a bright face with a glossy top, a soft rim and the kit's 3D bottom edge. */
fun Modifier.abToyTile(face: Color, corner: Dp = 10.dp, edgeHeight: Dp = 3.dp): Modifier =
    this
        .abChunkyFace(face, face.abDarkened(0.3f), null, corner, edgeHeight)
        .drawWithContent {
            drawContent()
            val r = CornerRadius(corner.toPx(), corner.toPx())
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.32f), Color.Transparent), endY = size.height * 0.5f),
                cornerRadius = r,
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.45f),
                cornerRadius = r,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx()),
            )
        }

/** The toy tray a board sits in: a thick rim in the game's colour on a soft white base, with a 3D edge. */
fun Modifier.abToyTray(accent: Color, inset: Dp = 10.dp): Modifier = composed {
    val shape = RoundedCornerShape(22.dp)
    val drop = 6.dp
    this
        .padding(bottom = drop)
        .drawBehind {
            drawRoundRect(accent.abDarkened(0.3f), Offset(0f, drop.toPx()), size, CornerRadius(22.dp.toPx()))
        }
        .shadow(12.dp, shape, clip = false)
        .background(Color.White.copy(alpha = 0.94f), shape)
        // the rim is drawn BEHIND the content (Modifier.border paints over it and cut off tiles that scale or hop past the edge)
        .drawBehind {
            val w = 5.dp.toPx()
            drawRoundRect(accent, Offset(w / 2, w / 2), Size(size.width - w, size.height - w), CornerRadius(22.dp.toPx() - w / 2), style = androidx.compose.ui.graphics.drawscope.Stroke(w))
        }
        .padding(inset)
}

/** A bright, readable colour for the n-th item (digit keys, tiles): the place-value rainbow, repeating. */
fun AbColors.toyColor(n: Int): Color {
    val list = placeValue
    return if (list.isEmpty()) brandPrimary else list[abs(n) % list.size].abReadable(3f)
}
