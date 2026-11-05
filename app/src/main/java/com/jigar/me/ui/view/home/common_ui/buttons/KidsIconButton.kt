package com.jigar.me.ui.view.home.common_ui.buttons

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens2
import com.jigar.me.ui.view.home.theme.AppDimens.KidsIconSize
import com.jigar.me.ui.view.home.theme.AppDimens.ShadowOffset
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.getButtonColors

@Composable
fun KidsIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    type: ButtonType,
    size: Dp = KidsIconSize,
    isPlayBackSound : Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = getButtonColors(type)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = 0.5f, // 🎮 bouncy feel
            stiffness = Spring.StiffnessMedium
        ),
        label = ""
    )

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .drawBehind {

                // Bottom shadow (depth)
                drawCircle(
                    color = colors.base,
                    radius = size.toPx() / 2,
                    center = Offset(
                        size.toPx() / 2,
                        size.toPx() / 2 + Dimens2.toPx()
                    )
                )

                // Top shine (bubble highlight)
                drawCircle(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    ),
                    radius = size.toPx() / 2,
                    center = Offset(size.toPx() / 2, size.toPx() / 2)
                )
            }
            .clip(CircleShape)
            .background(colors.gradient)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                if (isPlayBackSound){
                    AudioPlayerManager.playSoundBtnBack()
                }else{
                    AudioPlayerManager.playSoundBtnClick()
                }
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {

        Box {

            // Stronger shadow (cartoon depth)
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Black.copy(alpha = 0.35f),
                modifier = Modifier
                    .size(size/(if (size == KidsIconSize) 1.5f else 1.7f))
                    .offset(ShadowOffset, ShadowOffset)
            )

            // Main icon
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(size/(if (size == KidsIconSize) 1.5f else 1.7f))
            )
        }
    }
}