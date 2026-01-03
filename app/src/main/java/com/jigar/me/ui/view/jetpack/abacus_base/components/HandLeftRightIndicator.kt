package com.jigar.me.ui.view.jetpack.abacus_base.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jigar.me.R


@Composable
fun HandLeftRightIndicator(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    travelDp: Dp = 12.dp
) {
    if (!isVisible) return

    val transition = rememberInfiniteTransition(label = "hand_lr")

    val offsetX by transition.animateFloat(
        initialValue = 0f,
        targetValue = with(LocalDensity.current) { travelDp.toPx() },
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "handOffsetX"
    )

    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "handScale"
    )

    Image(
        painter = painterResource(R.drawable.ic_hand_point_right), //  👈 pointing hand
        contentDescription = null,
        modifier = modifier
            .offset(x = offsetX.dp)
            .scale(scale)
            .size(46.dp)
    )
}