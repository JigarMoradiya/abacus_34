package com.jigar.me.ui.view.home.screens.settings.components.voice

import android.speech.tts.Voice
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.SettingsVoice
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbDialog
import com.jigar.me.ui.view.ab.AbMenuPicker
import com.jigar.me.ui.view.ab.AbSettingRow
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbSlider
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.home.screens.settings.viewmodels.SettingUiState
import java.util.Locale
import kotlin.math.roundToInt

/** The voice settings (Android only), as a kit dialog: language, pitch, speed and the buttons. */
@Composable
fun VoiceSettingDialog(
    uiState: SettingUiState,
    onLanguageSelect: (Locale) -> Unit,
    onVoiceSelect: (Voice) -> Unit,
    onPitchChange: (Int) -> Unit,
    onSpeedChange: (Int) -> Unit,
    onTest: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val s = LocalAbScale.current.factor
    AbDialog(
        title = stringResource(R.string.change_voice_setting),
        icon = Icons.Rounded.SettingsVoice,
        onDismiss = onCancel,
    ) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp * s)) {
            // ─────────────────── Languages ───────────────────
            AbSettingRow(label = "Languages (${uiState.languages.size})", icon = Icons.Rounded.Language) {
                AbMenuPicker(
                    items = uiState.languages,
                    selectedTitle = uiState.selectedLanguage?.displayName ?: "",
                    title = { it.displayName },
                    onSelect = onLanguageSelect,
                    minWidth = 150.dp * s,
                )
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp * s)) {
                // ─────────────────── Pitch ───────────────────
                VoiceSliderPlate(
                    label = stringResource(R.string.pitch),
                    value = uiState.pitch,
                    onValueChange = onPitchChange,
                    modifier = Modifier.weight(1f),
                )
                // ─────────────────── Speed ───────────────────
                VoiceSliderPlate(
                    label = stringResource(R.string.voice_speed),
                    value = uiState.speed,
                    onValueChange = onSpeedChange,
                    modifier = Modifier.weight(1f),
                )
            }

            // ─────────────────── Buttons ───────────────────
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp * s)
            ) {
                AbButton(
                    text = stringResource(R.string.txtCancel), tone = AbTone.Ghost, size = AbSize.Small,
                    icon = Icons.Rounded.Close, onClick = onCancel,
                )
                Spacer(Modifier.weight(1f))
                AbButton(
                    text = stringResource(R.string.check_voice), tone = AbTone.Warning, size = AbSize.Small,
                    icon = Icons.Rounded.RecordVoiceOver, onClick = onTest,
                )
                AbButton(
                    text = stringResource(R.string.update), tone = AbTone.Success, size = AbSize.Small,
                    icon = Icons.Rounded.Update, onClick = onSave,
                )
            }
        }
    }
}

/** A label over a chunky 0 - 20 slider, on the same soft plate as a setting row. */
@Composable
private fun VoiceSliderPlate(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    Column(
        modifier
            .background(
                colors.surfaceTint.copy(alpha = if (colors.isDark) 0.35f else 0.55f),
                RoundedCornerShape(AbShapes.md * s),
            )
            .padding(horizontal = 14.dp * s, vertical = 8.dp * s),
        verticalArrangement = Arrangement.spacedBy(2.dp * s),
    ) {
        Text(label, style = AbTypography.body(s).copy(fontWeight = FontWeight.SemiBold), color = colors.textPrimary)
        AbSlider(
            value = value.toDouble(),
            onValueChange = { onValueChange(it.roundToInt()) },
            range = 0.0..20.0,
            step = 1.0,
        )
    }
}
