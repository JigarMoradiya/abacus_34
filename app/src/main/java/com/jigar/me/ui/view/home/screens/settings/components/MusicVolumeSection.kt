package com.jigar.me.ui.view.home.screens.settings.components

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorCoffee
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorLightYellow
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimary
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.jetpack.utils.ui.slider.SingleSlider

@Composable
fun MusicVolumeSection(
    modifier: Modifier = Modifier,
    volume: Int,
    onVolumeChange: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(AppDimens.Dimens16),
        colors = CardDefaults.cardColors(
            containerColor = ColorLightYellow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimens.Dimens3),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = AppDimens.Dimens6)
        ) {

            // 🟠 Title (same style as toggle sections)
            Text(
                text = stringResource(R.string.background_music_volume),
                modifier = Modifier.padding(horizontal = AppDimens.Dimens12),
                style = MaterialTheme.typography.bodyLarge.scaled().copy(
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily(Font(R.font.font_medium)),
                    color = ColorCoffee
                ),
            )

            // 🎚 Slider row (same feel as SettingsSwitchRow)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimens.Dimens10, vertical = AppDimens.Dimens4)
                    .clip(RoundedCornerShape(AppDimens.Dimens12))
                    .background(Color(0xFFE8F5E9))
                    .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.VolumeUp,
                    contentDescription = null,
                    tint = Color(0xFF43A047),
                    modifier = Modifier.size(AppDimens.Dimens24)
                )

                Spacer(modifier = Modifier.width(AppDimens.Dimens16))

                // 🔥 Slider takes remaining space
                SingleSlider(
                    isShowText = false,
                    value = volume,
                    range = 0f..100f,
                    onValueChange = onVolumeChange,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


