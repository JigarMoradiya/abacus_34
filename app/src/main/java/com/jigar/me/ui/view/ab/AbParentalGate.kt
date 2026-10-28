package com.jigar.me.ui.view.ab

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * The "For Parents" maths gate of the restyled screens: a lock badge, a sum and three chunky answers. A wrong answer
 * shakes the sum and draws a new one (same rules as the Game Zone's gate). Compose it as the last child of a full-screen
 * box; it fills that box, dims it, and starts its own entrance. Tapping the scrim, Cancel or Android's back calls
 * [onClose]; the right answer calls [onSuccess].
 *
 * The three texts are fixed English in the iOS kit; they are parameters here so the Android caller can localise them.
 */
@Composable
fun AbParentalGate(
    onSuccess: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "For Parents",
    subtitle: String = "Solve this to continue",
    cancelText: String = "Cancel",
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val tone = colors.accentPurple

    var num1 by remember { mutableIntStateOf(Random.nextInt(11, 50)) }
    var num2 by remember { mutableIntStateOf(Random.nextInt(11, 50)) }
    var options by remember { mutableStateOf(makeOptions(num1 + num2)) }
    var isShaking by remember { mutableStateOf(false) }
    val shake = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    fun regenerate() {
        num1 = Random.nextInt(11, 50)
        num2 = Random.nextInt(11, 50)
        options = makeOptions(num1 + num2)
    }

    // 4 swings of +-14 (80 ms each, after an 80 ms beat), back to centre, then a new sum 150 ms after the return starts.
    fun shakeSum() {
        if (isShaking) return
        isShaking = true
        scope.launch {
            delay(80)
            for (i in 0 until 4) {
                shake.animateTo(if (i % 2 == 0) 14f else -14f, tween(80, easing = LinearEasing))
            }
            launch { shake.animateTo(0f, tween(80, easing = LinearEasing)) }
            delay(150)
            regenerate()
            isShaking = false
        }
    }

    fun handleAnswer(selected: Int) {
        if (selected == num1 + num2) onSuccess() else shakeSum()
    }

    AbPopupLayer(onScrimTap = onClose) { layerWidth, _, shown ->
        AbPopupCard(
            width = minOf(layerWidth * 0.5f, 380.dp * s),
            shown = shown,
            tone = tone,
            lightBorderAlpha = 0.5f,
            padding = 20.dp * s,
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp * s),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // lock badge
            Box(Modifier.padding(bottom = 4.dp * s).size(58.dp * s), contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxSize()) {
                    val d = size.minDimension
                    val centre = Offset(size.width / 2f, size.height / 2f)
                    drawCircle(tone.abDarkened(0.3f), d / 2f, centre + Offset(0f, 4.dp.toPx() * s))
                    drawCircle(
                        Brush.verticalGradient(listOf(tone.abBlended(Color.White, 0.25f), tone)),
                        d / 2f, centre,
                    )
                    val ring = 3.dp.toPx()
                    drawCircle(Color.White.copy(alpha = 0.9f), d / 2f - ring / 2f, centre, style = Stroke(ring))
                }
                Icon(Icons.Rounded.Lock, null, Modifier.size((24f * AB_SF_TO_VECTOR).dp * s), tint = Color.White)
            }

            // the title and its line below sit close: Baloo's line box leaves a wide empty band between two lines
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy((-6).dp * s)) {
                Text(title, style = AbTypography.title(s), color = colors.textPrimary)
                Text(subtitle, style = AbTypography.body(s).copy(fontWeight = FontWeight.SemiBold), color = colors.textSecondary)
            }

            // the sum, which shakes on a wrong answer
            Box(
                Modifier
                    .padding(vertical = 2.dp * s)
                    .height(40.dp * s)
                    .offset { IntOffset(shake.value.dp.roundToPx(), 0) },
                contentAlignment = Alignment.Center,
            ) {
                Text("$num1 + $num2 = ?", style = AbTypography.fixed(30f * s, FontWeight.ExtraBold), color = colors.brandPrimary, maxLines = 1)
            }

            Row(Modifier.padding(bottom = 4.dp * s), horizontalArrangement = Arrangement.spacedBy(12.dp * s)) {
                options.forEach { option ->
                    key(option) {
                        Box(
                            Modifier
                                .abSquash(
                                    face = tone, edge = tone.abDarkened(0.3f), stroke = null,
                                    cornerRadius = AbShapes.md * s, edgeHeight = 4.dp * s,
                                    sound = AbSound.Button, onClick = { handleAnswer(option) },
                                )
                                .size(64.dp * s, 52.dp * s),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("$option", style = AbTypography.fixed(22f * s, FontWeight.ExtraBold), color = Color.White, maxLines = 1)
                        }
                    }
                }
            }

            Text(
                cancelText,
                modifier = Modifier
                    .abSquash(cornerRadius = AbShapes.sm * s, sound = AbSound.Back, onClick = onClose)
                    .padding(horizontal = 16.dp * s, vertical = 4.dp * s),
                style = AbTypography.label(s),
                color = colors.textSecondary,
            )
        }
    }
}

/** The right answer plus two near misses (1 to 6 off, never zero or negative), in random order. */
private fun makeOptions(correct: Int): List<Int> {
    val set = linkedSetOf(correct)
    while (set.size < 3) {
        val delta = Random.nextInt(1, 7) * (if (Random.nextBoolean()) 1 else -1)
        val wrong = correct + delta
        if (wrong > 0) set.add(wrong)
    }
    return set.shuffled()
}
