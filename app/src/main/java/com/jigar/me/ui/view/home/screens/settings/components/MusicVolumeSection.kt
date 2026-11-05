package com.jigar.me.ui.view.home.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AB_SF_TO_VECTOR
import com.jigar.me.ui.view.ab.AbSlider
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import kotlin.math.roundToInt

/** Background music volume: a title row and a chunky slider whose thumb shows the percentage. */
@Composable
fun MusicVolumeSection(
    modifier: Modifier = Modifier,
    volume: Int,
    onVolumeChange: (Int) -> Unit
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    AbGlassPanel(modifier, padding = 12.dp) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp * s)) {
            SettingsCardTitle(icon = Icons.Rounded.MusicNote, title = stringResource(R.string.background_music_volume))
            AbSlider(
                value = volume.toDouble(),
                onValueChange = { onVolumeChange(it.roundToInt()) },
                range = 0.0..100.0,
                step = 1.0,
                accent = colors.accentOrange,
                valueText = { it.roundToInt().toString() },
            )
        }
    }
}

/** The orange icon and the title at the top of a settings card (the volume and the voice cards). */
@Composable
internal fun SettingsCardTitle(icon: ImageVector, title: String) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp * s)) {
        Box(Modifier.width(26.dp * s), contentAlignment = Alignment.Center) {
            Icon(icon, null, Modifier.size((15f * AB_SF_TO_VECTOR).dp * s), tint = colors.accentOrange)
        }
        Text(title, style = AbTypography.body(s).copy(fontWeight = FontWeight.SemiBold), color = colors.textPrimary)
    }
}
