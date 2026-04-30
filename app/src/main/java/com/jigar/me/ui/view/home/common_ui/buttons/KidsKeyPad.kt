package com.jigar.me.ui.view.home.common_ui.buttons

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens10
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens2
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens6
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.AppDimens.ShadowOffset
import com.jigar.me.ui.view.home.theme.AppDimens.ShadowOffsetText
import com.jigar.me.ui.view.home.theme.AppDimens.keyPadHeight
import com.jigar.me.ui.view.home.theme.AppDimens.keyPadWidth
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.getButtonColors

@Composable
fun KidsKeyPad(
    text: String? = null,
    icon: Painter? = null,
    type: ButtonType,
    width: Dp = keyPadWidth,
    height: Dp = keyPadHeight,
    isSmall: Boolean = false,
    onClick: () -> Unit
) {
    require(text != null || icon != null) {
        "KidsPadKey needs either text or icon"
    }

    val colors = getButtonColors(type)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        label = ""
    )

    Box(
        modifier = Modifier
            .size(width = width, height = height)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .drawBehind {
                drawRoundRect(
                    color = colors.base,
                    size = size,
                    cornerRadius = CornerRadius(100f, 100f),
                    topLeft = Offset(0f, Dimens2.toPx()) // same as KidsActionButton
                )
            }
            .clip(RoundedCornerShape(100.dp))
            .background(colors.gradient)
            .clickable(
                interactionSource = interaction,
                indication = null
            ) {
                onClick()
            }
            .padding(
                horizontal = if (isSmall) Dimens8 else Dimens10,
                vertical = if (isSmall) Dimens4 else Dimens6
            ),
        contentAlignment = Alignment.Center
    ) {

        // ---- CONTENT ----

        if (text != null) {

            // shadow text
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.scaled().copy(
                    fontSize = (keyPadHeight.value * 0.6).sp
                ),
                color = Color.Black.copy(alpha = 0.35f),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.offset(ShadowOffsetText, ShadowOffsetText)
            )

            // main text
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.scaled().copy(
                    fontSize = (keyPadHeight.value * 0.6).sp
                ),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        if (icon != null) {

            // shadow icon
            Icon(
                painter = icon,
                contentDescription = null,
                tint = Color.Black.copy(alpha = 0.35f),
                modifier = Modifier
                    .size(keyPadHeight / 2)
                    .offset(ShadowOffset, ShadowOffset)
            )

            // main icon
            Icon(
                painter = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(keyPadHeight / 2)
            )
        }
    }
}