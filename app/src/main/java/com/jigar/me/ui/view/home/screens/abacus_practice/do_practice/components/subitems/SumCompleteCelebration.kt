package com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.subitems

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.home.theme.colorList
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class ConfettiPiece(
    val id: Int,
    val angleRad: Double,
    val distance: Float,
    val size: Float,
    val spin: Float,
    val color: androidx.compose.ui.graphics.Color,
    val anim: Animatable<Float, AnimationVector1D>
)

// Party-popper style paper-piece burst shown when a single abacus sum is
// completed (step-by-step or final-answer mode). Purely decorative and
// self-clearing — only ever suspends inside its own LaunchedEffect
// coroutines, never blocks the UI thread or traps input.
@Composable
fun SumCompleteCelebration() {
    val pieces = remember { mutableStateListOf<ConfettiPiece>() }

    LaunchedEffect(Unit) {
        val count = 16
        repeat(count) { i ->
            val angle = Math.toRadians((360.0 / count) * i + Random.nextFloat() * 12f)
            val anim = Animatable(0f)
            val piece = ConfettiPiece(
                id = i,
                angleRad = angle,
                distance = 70f + Random.nextFloat() * 50f,
                size = 7f + Random.nextFloat() * 6f,
                spin = (Random.nextFloat() * 540f - 270f),
                color = colorList.random(),
                anim = anim
            )
            pieces.add(piece)
            launch {
                anim.animateTo(1f, tween(750, easing = FastOutSlowInEasing))
                pieces.remove(piece)
            }
        }
    }

    if (pieces.isEmpty()) return

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        pieces.forEach { piece ->
            val progress = piece.anim.value
            // Radial burst outward, with a little gravity pulling pieces
            // down as they fly out — like real popper confetti.
            val dx = (cos(piece.angleRad) * piece.distance * progress).toFloat()
            val dy = (sin(piece.angleRad) * piece.distance * progress).toFloat() +
                (progress * progress * 40f)
            Box(
                modifier = Modifier
                    .offset(x = dx.dp, y = dy.dp)
                    .size(piece.size.dp)
                    .graphicsLayer {
                        alpha = 1f - progress
                        rotationZ = piece.spin * progress
                    }
                    .background(piece.color, RoundedCornerShape(1.dp))
            )
        }
    }
}
