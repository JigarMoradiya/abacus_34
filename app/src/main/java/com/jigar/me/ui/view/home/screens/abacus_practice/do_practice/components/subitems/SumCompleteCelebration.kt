package com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.subitems

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class CelebrationPiece(
    val id: Int,
    val emoji: String,
    val angleRad: Double,
    val distance: Float,
    val anim: Animatable<Float, AnimationVector1D>
)

private val celebrationEmojis = listOf("⭐", "✨", "🎉", "🌟")

// Brief radial sparkle burst shown when a single abacus sum is completed
// (step-by-step or final-answer mode). Only ever calls suspend delay()
// inside its own LaunchedEffect coroutines — never blocks the UI thread —
// and self-clears, so it never traps the user or stalls input.
@Composable
fun SumCompleteCelebration() {
    val pieces = remember { mutableStateListOf<CelebrationPiece>() }

    LaunchedEffect(Unit) {
        val count = 8
        repeat(count) { i ->
            val angle = Math.toRadians((360.0 / count) * i + Random.nextFloat() * 15f)
            val anim = Animatable(0f)
            val piece = CelebrationPiece(
                id = i,
                emoji = celebrationEmojis.random(),
                angleRad = angle,
                distance = 60f + Random.nextFloat() * 30f,
                anim = anim
            )
            pieces.add(piece)
            launch {
                anim.animateTo(1f, tween(650, easing = LinearOutSlowInEasing))
                pieces.remove(piece)
            }
        }
    }

    if (pieces.isEmpty()) return

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        pieces.forEach { piece ->
            val progress = piece.anim.value
            val dx = (cos(piece.angleRad) * piece.distance * progress).toFloat()
            val dy = (sin(piece.angleRad) * piece.distance * progress).toFloat()
            Text(
                text = piece.emoji,
                fontSize = 22.sp,
                modifier = Modifier
                    .offset(x = dx.dp, y = dy.dp)
                    .graphicsLayer {
                        alpha = 1f - progress
                        scaleX = 1f + progress * 0.4f
                        scaleY = 1f + progress * 0.4f
                    }
            )
        }
    }
}
