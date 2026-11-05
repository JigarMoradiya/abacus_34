package com.jigar.me.ui.view.home.common_ui.buttons

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens2
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.AppDimens.ShadowOffsetText
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.getButtonColors


@Composable
fun KidsNumberButton(
    num: Int,
    isSelected: Boolean,
    minWidth: Dp = AppDimens.Dimens56,
    minHeight: Dp = AppDimens.Dimens56,
    onClick: () -> Unit
) {

    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        label = ""
    )

    val colors = getButtonColors(if (isSelected) ButtonType.POSITIVE else ButtonType.BLUE)

    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
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
            .clip(RoundedCornerShape(100.dp))
            .background(colors.gradient)
            .clickable(
                interactionSource = interaction,
                indication = null
            ) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = num.toString(),
            fontSize = (minHeight.value * 0.55).sp.scaled(),
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
            color = Color.Black.copy(alpha = 0.35f),
            modifier = Modifier.offset(ShadowOffsetText, ShadowOffsetText).padding(horizontal = Dimens16)
        )

        Text(
            text = num.toString(),
            fontSize = (minHeight.value * 0.55).sp.scaled(),
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
            color = Color.White,
            modifier = Modifier.padding(horizontal = Dimens16)
        )
    }
}