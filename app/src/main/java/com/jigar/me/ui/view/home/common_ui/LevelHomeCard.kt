package com.jigar.me.ui.view.home.common_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun LevelHomeCard(
    emoji:      String,
    title:      String,
    label:      String,
    startColor: Color,
    endColor:   Color,
    width:      Dp,
    height:     Dp,
    hasBorder:  Boolean = false,
    isLocked:   Boolean = false,
    stars:      Int     = 0,
    onClick:    () -> Unit,
) {
    val isTablet = DeviceInfo.isTablet
    val shape    = RoundedCornerShape(AppDimens.Dimens20)

    Box(
        modifier = Modifier
            .size(width = width, height = height)
            .shadow(AppDimens.Dimens6, shape,
                ambientColor = endColor.copy(alpha = 0.4f),
                spotColor    = endColor.copy(alpha = 0.4f))
            .background(Brush.linearGradient(listOf(startColor, endColor)), shape)
            .then(if (hasBorder) Modifier.border(AppDimens.Dimens1, Color.White.copy(0.25f), shape) else Modifier)
            .clickable(remember { MutableInteractionSource() }, null) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier            = Modifier.padding(AppDimens.Dimens8),
        ) {
            Text(
                text  = emoji,
                style = if (isTablet)
                    MaterialTheme.typography.displayLarge.scaled().copy(fontSize = 60.sp.scaled())
                else
                    MaterialTheme.typography.displaySmall.scaled()
            )
            Spacer(Modifier.height(if (isTablet) AppDimens.Dimens8 else AppDimens.Dimens6))
            Text(
                text       = title,
                style      = if (isTablet) MaterialTheme.typography.headlineLarge.scaled() else MaterialTheme.typography.titleMedium.scaled(),
                color      = Color.White,
                fontWeight = FontWeight.Black,
                textAlign  = TextAlign.Center,
                maxLines   = 2,
            )
            Spacer(Modifier.height(AppDimens.Dimens4))
            Box(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.22f), RoundedCornerShape(AppDimens.Dimens100))
                    .padding(horizontal = AppDimens.Dimens8, vertical = AppDimens.Dimens3)
            ) {
                Text(
                    text       = label,
                    style      = if (isTablet) MaterialTheme.typography.labelLarge.scaled() else MaterialTheme.typography.labelSmall.scaled(),
                    color      = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    textAlign  = TextAlign.Center,
                )
            }
            if (stars > 0) {
                Spacer(Modifier.height(AppDimens.Dimens4))
                Text(
                    text  = "★".repeat(stars) + "☆".repeat(3 - stars),
                    color = Color.White,
                    style = if (isTablet) MaterialTheme.typography.labelLarge.scaled()
                            else          MaterialTheme.typography.labelMedium.scaled(),
                )
            }
        }

        if (isLocked) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(AppDimens.Dimens6)
                    .size(AppDimens.Dimens24)
                    .background(Color.Black.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(AppDimens.Dimens14)
                )
            }
        }
    }
}