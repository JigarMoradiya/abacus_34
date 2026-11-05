package com.jigar.me.ui.view.home.screens.abacus_practice.set_list.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.ui.view.ab.AbMotion
import com.jigar.me.ui.view.ab.AbShrinkText
import com.jigar.me.ui.view.ab.AbSound
import com.jigar.me.ui.view.ab.abBlended
import com.jigar.me.ui.view.ab.abChunkyFace
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abFixedStyle
import com.jigar.me.ui.view.ab.abShadow
import com.jigar.me.utils.AppConstants

/**
 * One set of a page as a round numbered stage button, coloured by how the set is answered (step by step, final answer,
 * formal exam), with a clock for timed sets and small dots for progress. [size] is the diameter of the button.
 */
@Composable
fun SetItem(
    set: Set,
    size: Dp,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    isLocked: Boolean = false
) {
    val dens = LocalDensity.current.density
    val tint = setTint(set.answer_setting)
    val face = if (isLocked) tint.abBlended(Color.Gray, 0.55f) else tint
    val edge = size * 0.08f
    val digits = set.getSetTitle().filter { it.isDigit() }

    Box(Modifier.size(size, size + edge)) {
        Box(
            Modifier
                .setBubblePress(face.abDarkened(0.32f), size, edge, enabled = !isLocked, onClick = onClick, onLongClick = onLongClick)
                .size(size)
                .background(Brush.verticalGradient(listOf(face.abBlended(Color.White, 0.3f), face)), CircleShape)
                .border(2.5.dp, Color.White.copy(alpha = 0.85f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            AbShrinkText(
                text = digits.ifEmpty { set.getSetTitle().take(2) },
                style = abFixedStyle(size.value * 0.46f, FontWeight.ExtraBold).copy(
                    shadow = abShadow(dens, Color.Black.copy(alpha = 0.25f), dy = 1.5f)
                ),
                color = Color.White,
                minScale = 0.5f,
                maxLines = 1,
            )
            if (set.show_time_setting) {
                Box(
                    Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = -size * 0.04f, y = size * 0.04f)
                        .size(size * 0.36f)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(size * 0.26f),
                        tint = face.abDarkened(0.35f)
                    )
                }
            }
            if (isLocked) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    modifier = Modifier.offset(y = size * 0.3f).size(size * 0.36f),
                    tint = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // Progress badge (only for unlocked sets)
        if (!isLocked && (set.is_completed_set || set.is_running_set)) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = size * 0.1f, y = -size * 0.04f)
                    .shadow(1.5.dp, CircleShape)
                    .background(Color.White, CircleShape)
                    .padding(horizontal = 5.dp, vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                if (set.is_completed_set) {
                    Box(
                        Modifier
                            .size(size * 0.17f)
                            .background(colorResource(R.color.green_A400), CircleShape)
                    )
                }
                if (set.is_running_set) {
                    BlinkingDot(color = colorResource(R.color.red_A400), size = size * 0.17f)
                }
            }
        }
    }
}

/**
 * The press feel of a kit stage button (the click cue as the press begins, the bouncy squash, the 3D edge under the
 * face) plus a long press, which the debug build uses to open the list of the set's abacus.
 */
private fun Modifier.setBubblePress(
    edge: Color,
    size: Dp,
    edgeHeight: Dp,
    enabled: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val pressedRaw by interaction.collectIsPressedAsState()
    val pressed = pressedRaw && enabled
    LaunchedEffect(interaction, enabled) {
        interaction.interactions.collect { if (it is PressInteraction.Press && enabled) AbSound.Button.play() }
    }
    val squash by animateFloatAsState(
        targetValue = if (pressed) AbMotion.PRESS_SCALE else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium),
        label = "setBubbleSquash",
    )
    this
        .combinedClickable(
            interactionSource = interaction, indication = null, enabled = enabled,
            onClick = onClick, onLongClick = onLongClick,
        )
        .graphicsLayer { scaleX = squash; scaleY = squash }
        .abChunkyFace(Color.Transparent, edge, null, size, edgeHeight, pressed)
}

@Composable
private fun setTint(answerSetting: String): Color {
    return when (answerSetting) {
        AppConstants.apiParams.answerStepByStep ->
            colorResource(R.color.step_by_step_answer)

        AppConstants.apiParams.answerFinalAnswer ->
            colorResource(R.color.final_answer)

        AppConstants.apiParams.answerFormalExam ->
            colorResource(R.color.formal_exam)

        else ->
            Color.Gray
    }
}

@Composable
fun BlinkingDot(
    color: Color,
    size: Dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "blink")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blinkAlpha"
    )

    Box(
        modifier = Modifier
            .size(size)
            .background(color.copy(alpha = alpha), CircleShape)
    )
}
