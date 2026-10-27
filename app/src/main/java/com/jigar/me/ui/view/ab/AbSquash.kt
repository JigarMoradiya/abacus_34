package com.jigar.me.ui.view.ab

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import kotlin.math.min

/** The click cue of a control. Follows the existing app helpers (`AudioPlayerManager`). */
enum class AbSound {
    Button, Back;

    fun play() = when (this) {
        Button -> AudioPlayerManager.playSoundBtnClick()
        Back -> AudioPlayerManager.playSoundBtnBack()
    }
}

/**
 * Chunky face with a darker 3D bottom edge: the face sinks onto the edge while [pressed]. Reusable by non-interactive
 * views so a display-only pill looks identical to a button.
 */
fun Modifier.abChunkyFace(
    face: Color,
    edge: Color?,
    stroke: Color?,
    cornerRadius: Dp,
    edgeHeight: Dp,
    pressed: Boolean = false,
): Modifier = composed {
    val s = LocalAbScale.current.factor
    val shape = RoundedCornerShape(cornerRadius)
    val drop = if (edge != null) edgeHeight else 0.dp
    this
        .padding(bottom = drop)
        .drawBehind {
            if (edge != null) {
                val r = min(cornerRadius.toPx(), size.minDimension / 2f)
                drawRoundRect(color = edge, topLeft = Offset(0f, edgeHeight.toPx()), size = size, cornerRadius = CornerRadius(r, r))
            }
        }
        .offset { IntOffset(0, if (pressed) drop.roundToPx() else 0) }
        .background(face, shape)
        .then(if (stroke != null) Modifier.border(2.dp * s, stroke, shape) else Modifier)
}

/**
 * The press feel shared by every tappable Ab component: a [abChunkyFace] plus a bouncy squash, with the click cue played
 * the moment the press BEGINS (not on release, which is too late on a control that navigates away).
 * [sound] null = silent by design (the owner of the control plays its own sound).
 */
fun Modifier.abSquash(
    face: Color = Color.Transparent,
    edge: Color? = null,
    stroke: Color? = null,
    cornerRadius: Dp = AbShapes.md,
    edgeHeight: Dp = AbShapes.buttonEdge,
    enabled: Boolean = true,
    sound: AbSound? = AbSound.Button,
    onClick: () -> Unit,
): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val pressedRaw by interaction.collectIsPressedAsState()
    val pressed = pressedRaw && enabled
    LaunchedEffect(interaction, enabled, sound) {
        interaction.interactions.collect { if (it is PressInteraction.Press && enabled) sound?.play() }
    }
    val squash by animateFloatAsState(
        targetValue = if (pressed) AbMotion.PRESS_SCALE else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium),
        label = "abSquash",
    )
    this
        .clickable(interactionSource = interaction, indication = null, enabled = enabled, onClick = onClick)
        .graphicsLayer { scaleX = squash; scaleY = squash }
        .abChunkyFace(face, edge, stroke, cornerRadius, edgeHeight, pressed)
}
