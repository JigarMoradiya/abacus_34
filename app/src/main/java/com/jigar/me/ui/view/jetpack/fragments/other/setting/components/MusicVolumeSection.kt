package com.jigar.me.ui.view.jetpack.fragments.other.setting.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.utils.ui.slider.SingleSlider

@Composable
fun MusicVolumeSection(
    modifier: Modifier = Modifier,
    volume: Int,
    onVolumeChange: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF3EFFF)
        ), border = BorderStroke(1.dp, Color(0xFF9C27B0)), // purple stroke
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.activity_padding16))
                .padding(top = dimensionResource(R.dimen.activity_padding16))
                .padding(bottom = dimensionResource(R.dimen.activity_padding8))
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.VolumeUp, contentDescription = null, tint = Color.Black
                )

                Text(
                    text = stringResource(R.string.background_music_volume),
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.activity_padding8)),
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black, fontWeight = FontWeight.Medium, fontFamily = FontFamily(Font(R.font.font_medium))),
                )
            }

            SingleSlider(
                isShowText = false, value = volume, range = 0f..100f, onValueChange = onVolumeChange, modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.activity_padding8))
            )
        }
    }

}


