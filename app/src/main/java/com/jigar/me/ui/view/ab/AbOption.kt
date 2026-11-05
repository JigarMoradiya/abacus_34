package com.jigar.me.ui.view.ab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

enum class AbOptionState { Idle, Correct, Wrong }

/**
 * Answer choice for quizzes and drills: a chunky tile that is neutral until answered, then turns green with a check
 * (correct, with a pop) or red with a cross (wrong, with a shake).
 *
 * @param width the tile width; null = fill the width it is given.
 * @param height the tile height; null = 56 dp scaled by the kit factor. The text size follows the height.
 * @param sound the cue played when the press begins; null = silent, for a screen whose view model plays its own click.
 */
@Composable
fun AbOptionButton(
    text: String,
    modifier: Modifier = Modifier,
    state: AbOptionState = AbOptionState.Idle,
    width: Dp? = null,
    height: Dp? = null,
    sound: AbSound? = AbSound.Button,
    onClick: () -> Unit,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val h = height ?: (56.dp * s)
    val radius = minOf(AbShapes.md * s, h * 0.35f)
    val face = when (state) {
        AbOptionState.Idle -> colors.bgSecondary
        AbOptionState.Correct -> colors.accentGreen
        AbOptionState.Wrong -> colors.danger
    }
    val edge = when (state) {
        AbOptionState.Idle -> colors.outline
        AbOptionState.Correct -> colors.accentGreen.abDarkened(0.3f)
        AbOptionState.Wrong -> colors.danger.abDarkened(0.3f)
    }
    val ink = if (state == AbOptionState.Idle) colors.textPrimary else Color.White

    // pop on correct (the iOS kit's AbMotion.spring)
    val pop by animateFloatAsState(
        targetValue = if (state == AbOptionState.Correct) 1.06f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium),
        label = "abOptionPop",
    )

    // shake on wrong: 0 -> 1 in 0.4 s drives 8 dp * sin(4 pi t), two full swings that end at rest
    val shakes = remember { Animatable(0f) }
    var previous by remember { mutableStateOf(state) }
    LaunchedEffect(state) {
        val wasWrong = previous == AbOptionState.Wrong
        previous = state
        if (state == AbOptionState.Wrong && !wasWrong) {
            shakes.snapTo(0f)
            shakes.animateTo(1f, tween(400, easing = LinearEasing))
        } else if (state != AbOptionState.Wrong) {
            shakes.snapTo(0f)
        }
    }

    // the badge keeps its last symbol while it animates out
    var badge by remember { mutableStateOf(AbOptionState.Correct) }
    if (state != AbOptionState.Idle) badge = state

    Box(
        modifier
            .graphicsLayer {
                translationX = 8.dp.toPx() * sin(shakes.value * PI.toFloat() * 4f)
                scaleX = pop
                scaleY = pop
            }
            .abSquash(
                face = face, edge = edge,
                stroke = if (state == AbOptionState.Idle) colors.outline else null,
                cornerRadius = radius, edgeHeight = 5.dp * s,
                sound = sound, onClick = onClick,
            )
            .then(if (width != null) Modifier.width(width) else Modifier.fillMaxWidth())
            .height(h),
        contentAlignment = Alignment.Center,
    ) {
        AbShrinkText(
            text = text,
            style = AbTypography.fixed(h.value * 0.46f, FontWeight.ExtraBold),
            color = ink,
            modifier = Modifier.padding(horizontal = 10.dp * s),
            minScale = 0.5f,
            maxLines = 1,
            textAlign = TextAlign.Center,
        )
        AnimatedVisibility(
            visible = state != AbOptionState.Idle,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = h * 0.1f, y = -(h * 0.1f)),
            enter = scaleIn(spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium)) + fadeIn(),
            exit = scaleOut() + fadeOut(),
        ) {
            val correct = badge == AbOptionState.Correct
            val mark = (if (correct) colors.accentGreen else colors.danger).abDarkened(0.25f)
            Box(Modifier.size(h * 0.34f).background(Color.White, CircleShape), contentAlignment = Alignment.Center) {
                Icon(
                    if (correct) Icons.Rounded.Check else Icons.Rounded.Close, null,
                    Modifier.size(h * 0.24f), tint = mark,
                )
            }
        }
    }
}
