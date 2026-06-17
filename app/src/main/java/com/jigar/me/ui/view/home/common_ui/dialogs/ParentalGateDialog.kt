package com.jigar.me.ui.view.home.common_ui.dialogs

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.theme.AppDimens
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Parental gate overlay — required before login, purchase, or external URL.
 * Apple Kids category guideline: must present a math challenge an adult can solve.
 * Uses 2-digit numbers so kids cannot easily guess the answer.
 *
 * Correct answer → [onPassed]. Cancel → [onCancelled]. Wrong → shake + regenerate.
 */
@Composable
fun ParentalGateDialog(
    onPassed: () -> Unit,
    onCancelled: () -> Unit,
) {
    var num1 by remember { mutableStateOf(Random.nextInt(11, 50)) }
    var num2 by remember { mutableStateOf(Random.nextInt(11, 50)) }
    var options by remember { mutableStateOf(generateOptions(num1 + num2)) }
    val shakeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    fun regenerate() {
        num1 = Random.nextInt(11, 50)
        num2 = Random.nextInt(11, 50)
        options = generateOptions(num1 + num2)
    }

    fun handleAnswer(selected: Int) {
        if (selected == num1 + num2) {
            onPassed()
        } else {
            scope.launch {
                repeat(4) { i ->
                    shakeOffset.animateTo(
                        targetValue = if (i % 2 == 0) 14f else -14f,
                        animationSpec = tween(durationMillis = 80)
                    )
                }
                shakeOffset.animateTo(0f, animationSpec = tween(80))
                delay(150)
                regenerate()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 380.dp)
                .padding(horizontal = AppDimens.Dimens24)
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(AppDimens.Dimens24))
                .clip(RoundedCornerShape(AppDimens.Dimens24))
                .background(Color.White)
                .padding(AppDimens.Dimens24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
        ) {
            // Lock icon circle
            Box(
                modifier = Modifier
                    .size(AppDimens.Dimens56)
                    .clip(CircleShape)
                    .background(Color(0xFFEFF6FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🔒", style = MaterialTheme.typography.headlineSmall.scaled())
            }

            Text(
                text = "For Parents",
                style = MaterialTheme.typography.titleLarge.scaled(),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C1C1C)
            )

            Text(
                text = "Solve this to continue",
                style = MaterialTheme.typography.bodyMedium.scaled(),
                color = Color(0xFF666666)
            )

            // Question with shake animation
            Text(
                text = "$num1 + $num2 = ?",
                style = MaterialTheme.typography.headlineMedium.scaled(),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0074D5),
                modifier = Modifier.graphicsLayer { translationX = shakeOffset.value }
            )

            // 3 answer buttons
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
                options.forEach { option ->
                    Box(
                        modifier = Modifier
                            .size(AppDimens.Dimens56)
                            .clip(RoundedCornerShape(AppDimens.Dimens12))
                            .background(Color(0xFFEFF6FF))
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { handleAnswer(option) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$option",
                            style = MaterialTheme.typography.titleMedium.scaled(),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0074D5)
                        )
                    }
                }
            }

            Text(
                text = "Cancel",
                style = MaterialTheme.typography.bodyMedium.scaled(),
                color = Color(0xFF999999),
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onCancelled() }
                    .padding(AppDimens.Dimens8)
            )
        }
    }
}

private fun generateOptions(correct: Int): List<Int> {
    val set = mutableSetOf(correct)
    while (set.size < 3) {
        val delta = (1..6).random() * if (Math.random() > 0.5) 1 else -1
        val wrong = correct + delta
        if (wrong > 0 && wrong != correct) set.add(wrong)
    }
    return set.shuffled()
}
