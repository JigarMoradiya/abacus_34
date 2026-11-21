package com.jigar.me.ui.view.home.common_ui.sheets

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ThumbDown
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens20
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.jigar.me.R

// One-time review ask shown on day 2 of usage. Same "Candy Pop" visual system as
// ReviewGateBottomSheet, with two big thumbs instead of the smiley + Yes/No buttons.
// Thumb up opens the Play Store listing directly (no review popup of our own);
// thumb down opens the feedback page (ReviewFeedbackPage) where the parent says what
// went wrong; tapping outside only closes it.
@Composable
fun Day2ReviewGateBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onLike: () -> Unit,
    onDislike: () -> Unit,
) {
    val cardAccent = Brush.horizontalGradient(listOf(Color(0xFFFF9F43), Color(0xFFFF7A45)))
    val accentBase = Color(0xFFC77000)
    var chosen by remember { mutableIntStateOf(0) }   // 1 = thumb up, -1 = thumb down
    var hasResolved by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    // a sheet that is shown again (the feedback page was left with back) starts fresh
    LaunchedEffect(visible) { if (visible) { hasResolved = false; chosen = 0 } }

    fun choose(like: Boolean) {
        if (hasResolved) return
        hasResolved = true
        chosen = if (like) 1 else -1
        scope.launch {
            delay(250)
            if (like) onLike() else onDislike()
        }
    }

    KidsBottomSheet(
        visible = visible,
        onDismiss = {
            // A thumb tap may already have a delayed resolution in flight (see
            // choose) -- let that resolve on its own instead of also firing
            // the separate dismiss path, or both onLike/onDislike could
            // run for one interaction.
            if (!hasResolved) {
                hasResolved = true
                onDismiss()
            }
        },
        widthFraction = 0.62f,
        containerBackground = cardAccent,
        handleColor = Color.White.copy(alpha = 0.5f),
        borderColor = Color.White.copy(alpha = 0.3f),
        showHandle = false,
        decoration = {
            BoxWithConstraints(modifier = Modifier.matchParentSize()) {
                val w = maxWidth
                val h = maxHeight
                Box(
                    Modifier
                        .size(26.dp)
                        .offset(x = w * 0.12f - 13.dp, y = h * 0.15f - 13.dp)
                        .background(Color.White.copy(alpha = 0.18f), CircleShape)
                )
                Box(
                    Modifier
                        .size(16.dp)
                        .offset(x = w * 0.88f - 8.dp, y = h * 0.2f - 8.dp)
                        .background(Color.White.copy(alpha = 0.12f), CircleShape)
                )
                Box(
                    Modifier
                        .size(20.dp)
                        .offset(x = w * 0.85f - 10.dp, y = h * 0.82f - 10.dp)
                        .background(Color.White.copy(alpha = 0.14f), CircleShape)
                )
                Box(
                    Modifier
                        .size(14.dp)
                        .offset(x = w * 0.1f - 7.dp, y = h * 0.85f - 7.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape)
                )
                Text(
                    text = "✨",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.45f),
                    modifier = Modifier.offset(x = w * 0.75f - 7.dp, y = h * 0.1f - 8.dp)
                )
            }
        }
    ) {
        Spacer(modifier = Modifier.height(Dimens12))

        Box(
            modifier = Modifier
                .size(96.dp)
                .drawBehind {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.85f),
                        radius = size.minDimension / 2f - 1.5.dp.toPx(),
                        style = Stroke(
                            width = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(elevation = 8.dp, shape = CircleShape)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val emojiStyle = MaterialTheme.typography.displayLarge.scaled()
                Text(
                    text = "🌱",
                    style = emojiStyle.copy(fontSize = emojiStyle.fontSize * 1.35f)
                )
            }
            Text(
                text = "✨",
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = 2.dp)
            )
            Text(
                text = "✨",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-2).dp, y = (-4).dp)
            )
        }

        Spacer(modifier = Modifier.height(Dimens12))

        Text(
            text = stringResource(R.string.review_like_title),
            style = MaterialTheme.typography.titleMedium.scaled().copy(
                shadow = Shadow(
                    color = accentBase.copy(alpha = 0.85f),
                    offset = Offset(1.5f, 1.5f),
                    blurRadius = 0f
                )
            ),
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens8))

        Text(
            text = stringResource(R.string.review_like_message),
            style = MaterialTheme.typography.bodyMedium.scaled(),
            color = Color.White.copy(alpha = 0.92f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens20)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens16, vertical = Dimens12),
            horizontalArrangement = Arrangement.spacedBy(36.dp, alignment = Alignment.CenterHorizontally),
            verticalAlignment = Alignment.Top
        ) {
            ThumbChoice(
                icon = Icons.Rounded.ThumbUp,
                iconColor = Color(0xFF1FA650),
                label = stringResource(R.string.review_like_yes),
                selected = chosen == 1,
                onClick = { choose(true) },
            )
            ThumbChoice(
                icon = Icons.Rounded.ThumbDown,
                iconColor = Color(0xFFE5484D),
                label = stringResource(R.string.review_like_no),
                selected = chosen == -1,
                onClick = { choose(false) },
            )
        }

        Spacer(modifier = Modifier.height(Dimens16))
    }
}

// A big round thumb button: a white face on a darker 3D edge, the label under it. It bounces up when chosen.
@Composable
private fun ThumbChoice(icon: ImageVector, iconColor: Color, label: String, selected: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "thumbScale"
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = onClick)
    ) {
        Box(modifier = Modifier.size(width = 72.dp, height = 76.dp)) {
            Box(Modifier.align(Alignment.BottomCenter).size(72.dp).background(Color(0x33000000), CircleShape))
            Box(
                Modifier.align(Alignment.TopCenter).size(72.dp).background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = label, tint = iconColor, modifier = Modifier.size(38.dp))
            }
        }
        Spacer(Modifier.height(Dimens8))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.scaled(),
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
    }
}
