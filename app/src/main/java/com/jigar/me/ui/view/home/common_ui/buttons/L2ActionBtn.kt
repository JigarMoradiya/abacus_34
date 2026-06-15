package com.jigar.me.ui.view.home.common_ui.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun L2ActionBtn(label: String, filled: Boolean, startColor: Color = Color(0xFF1B5E20), onClick: () -> Unit) {
    val shape = RoundedCornerShape(AppDimens.Dimens100)
    Box(
        modifier = Modifier
            .shadow(AppDimens.Dimens4, shape)
            .background(if (filled) Color.White else Color.White.copy(0.25f), shape)
            .clickable(remember { MutableInteractionSource() }, null) { AudioPlayerManager.playSoundBtnClick(); onClick() }
            .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens12),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = label,
            style      = MaterialTheme.typography.labelLarge.scaled(),
            color      = if (filled) startColor else Color.White,
            fontWeight = FontWeight.Black
        )
    }
}