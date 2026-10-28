package com.jigar.me.ui.view.home.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SettingsVoice
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbShrinkText
import com.jigar.me.ui.view.ab.AbTonePalette
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abSquash

/**
 * Voice settings (Android only): a card in the style of the volume card, with a chunky button that opens the voice dialog.
 * Its control row has the same height as the volume slider, so the two cards sit level side by side.
 */
@Composable
fun VoiceSection(
    modifier: Modifier = Modifier, onVoiceChange: () -> Unit
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val palette = AbTonePalette.of(AbTone.Secondary, colors)
    AbGlassPanel(modifier, padding = 12.dp) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp * s)) {
            SettingsCardTitle(icon = Icons.Rounded.SettingsVoice, title = stringResource(R.string.change_voice_setting))
            Box(Modifier.fillMaxWidth().height(38.dp * s), contentAlignment = Alignment.Center) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .abSquash(
                            face = palette.face, edge = palette.edge, cornerRadius = AbShapes.pill,
                            edgeHeight = 3.5.dp * s, onClick = onVoiceChange,
                        )
                        .heightIn(min = 32.dp * s)
                        .padding(horizontal = 12.dp * s),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AbShrinkText(
                        text = stringResource(R.string.customize_pronunciation_voice),
                        style = AbTypography.label(s),
                        color = Color.White,
                        minScale = 0.6f,
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
