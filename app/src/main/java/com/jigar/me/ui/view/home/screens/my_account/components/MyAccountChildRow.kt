package com.jigar.me.ui.view.home.screens.my_account.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.screens.my_account.viewmodels.MyAccountMenu
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens10
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens14
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens20
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8


@Composable
fun MyAccountChildRow(
    item: MyAccountMenu,
    onClick: (String) -> Unit
) {

    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        label = ""
    )

    Box(
        modifier = Modifier
            .padding(horizontal = Dimens10, vertical = Dimens4) // ↓ reduced
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(Dimens12)) // ↓ reduced
            .background(Color.White)
            .clickable(
                interactionSource = interaction,
                indication = null
            ) {
                AudioPlayerManager.playSoundBtnClick()
                onClick(item.tag)
            }
            .padding(horizontal = Dimens10, vertical = Dimens8), // ↓ reduced
        contentAlignment = Alignment.Center
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            item.menuIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = Color(0xFFFB8C00),
                    modifier = Modifier.size(Dimens20) // ↓ slightly smaller
                )
            }

            Spacer(modifier = Modifier.width(Dimens8)) // ↓ reduced

            Text(
                text = item.menuTitle,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium.scaled().copy( // ↓ smaller
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.font_semibold)),
                    color = Color(0xFF4E342E)
                )
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFFBDBDBD),
                modifier = Modifier.size(Dimens14) // ↓ smaller
            )
        }
    }
}