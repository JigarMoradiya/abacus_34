package com.jigar.me.ui.view.home.common_ui.animations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.home.theme.colorList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

data class ConfettiItem(
    val id: Int,
    val x: Float,
    val drift: Float,
    val size: Float,
    val color: Color,
    val anim: Animatable<Float, AnimationVector1D>
)
@Composable
fun ConfettiRainEffect() {

    val density = LocalDensity.current
    val confettiList = remember { mutableStateListOf<ConfettiItem>() }

    var start by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(true) }



    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {

        val heightPx = with(density) { maxHeight.toPx() }

        // 🚀 start AFTER UI renders
        LaunchedEffect(Unit) {
            delay(100) // 🔥 allow popup to render first
            start = true
        }

        if (start) {
            LaunchedEffect(Unit) {

                var idCounter = 0
                val startTime = System.currentTimeMillis()

                while (System.currentTimeMillis() - startTime < 2000) {

                    repeat(3) { // 🔥 reduced load but still dense

                        val anim = Animatable(-100f)

                        val item = ConfettiItem(
                            id = idCounter++,
                            x = Random.nextFloat(),
                            drift = Random.nextFloat() * 40f - 20f,
                            size = Random.nextFloat() * 10f + 6f,
                            color = colorList.random(),
                            anim = anim
                        )

                        confettiList.add(item)

                        launch {
                            anim.animateTo(
                                targetValue = heightPx + 200f,
                                animationSpec = tween(
                                    durationMillis = Random.nextInt(600, 900), // ⚡ fast
                                    easing = LinearEasing
                                )
                            )
                            confettiList.remove(item)
                        }
                    }

                    delay(12) // ⚡ balanced spawn
                }

                visible = false
            }
        }

        confettiList.forEach { item ->

            val progress = item.anim.value / heightPx

            val offsetX = (item.x * maxWidth.value) + (item.drift * progress)
            val offsetY = with(density) { item.anim.value.toDp() }

            Box(
                modifier = Modifier
                    .offset(x = offsetX.dp, y = offsetY)
                    .size(item.size.dp)
                    .graphicsLayer {
                        rotationZ = progress * 360f
                    }
                    .background(item.color, RoundedCornerShape(2.dp))
            )
        }

        if (!visible && confettiList.isEmpty()) return@BoxWithConstraints
    }
}