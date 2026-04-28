package com.jigar.me.ui.view.home.common_ui

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens10
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.AppDimens.ShadowOffset
import com.jigar.me.ui.view.home.theme.AppDimens.ToolbarIconSize
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.getButtonColors
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled


@Composable
fun BackButtonWithTextNew(
    title: String,
    modifier: Modifier = Modifier,
    size: Dp = ToolbarIconSize,
    onBackClick: () -> Unit
) {
    val headerColors = getButtonColors(ButtonType.BLUE)
    Row(
        modifier = modifier.fillMaxWidth().padding(top = DeviceInfo.screenTopPadding(), bottom = Dimens8, start = DeviceInfo.screenHorizontalPadding(), end = Dimens16),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier.wrapContentSize()
        ) {

            // BACK CAPSULE (BEHIND)
            Box(
                modifier = Modifier
                    .padding(start = (size / 2) + Dimens12) // ⭐ push to allow circle overlap
                    .height(size * 0.8f)
                    .shadow(
                        elevation = Dimens10,
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            bottomStart = 0.dp,
                            topEnd = 100.dp,
                            bottomEnd = 100.dp
                        ),
                        clip = false
                    )
                    .background(
                        brush = headerColors.gradient,
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            bottomStart = 0.dp,
                            topEnd = 100.dp,
                            bottomEnd = 100.dp
                        )
                    )
                    .padding(start = size / 2, end = Dimens16) // ⭐ space for circle
                    .align(Alignment.CenterStart)
            ) {

                // TEXT inside capsule
                Box(
                    modifier = Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge.scaled(),
                        fontWeight = FontWeight.Black
                    )
                }
            }

            val interactionSource = remember { MutableInteractionSource() }

            Box(
                modifier = Modifier
                    .size(size)
                    .shadow(Dimens8, CircleShape, clip = false)
                    .background(headerColors.base, CircleShape)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current
                    ) {
                        AudioPlayerManager.playSoundBtnBack()
                        onBackClick()
                    }
                    .align(Alignment.CenterStart),
                contentAlignment = Alignment.Center
            ) {
                Box {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null,
                        tint = Color.Black.copy(alpha = 0.35f),
                        modifier = Modifier.size(24.dp.scaled())
                            .offset(ShadowOffset, ShadowOffset), // shadow layer
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp.scaled())
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}