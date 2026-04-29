package com.jigar.me.ui.view.home.common_ui.buttons

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens2
import com.jigar.me.ui.view.home.theme.AppDimens.ShadowOffsetText
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.getButtonColors

@Composable
fun KidsOptionButton(
    text: String,
    type: ButtonType,
    fontSize: TextUnit,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Center,           // 👈 new
    contentAlignment: Alignment = Alignment.Center     // 👈 new
) {
    val colors = getButtonColors(type)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = Spring.StiffnessMedium
        ),
        label = ""
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .drawBehind {
                drawRoundRect(
                    color = colors.base,
                    size = size,
                    cornerRadius = CornerRadius(100f, 100f),
                    topLeft = Offset(0f, Dimens2.toPx())
                )
            }
            .clip(RoundedCornerShape(50))
            .background(brush = colors.gradient)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            }
            .padding(horizontal = Dimens12),
        contentAlignment = contentAlignment // 👈 dynamic
    ) {

        Box(
            modifier = Modifier.fillMaxWidth() // 👈 needed for textAlign
        ) {

            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = fontSize),
                fontWeight = FontWeight.ExtraBold,
                textAlign = textAlign, // 👈 dynamic
                color = Color.Black.copy(alpha = if (type == ButtonType.OPTIONS) 0.15f else 0.25f),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(ShadowOffsetText, ShadowOffsetText)
            )

            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = fontSize),
                fontWeight = FontWeight.ExtraBold,
                textAlign = textAlign, // 👈 dynamic
                color = if (type == ButtonType.OPTIONS) Color.Black else Color.White,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}