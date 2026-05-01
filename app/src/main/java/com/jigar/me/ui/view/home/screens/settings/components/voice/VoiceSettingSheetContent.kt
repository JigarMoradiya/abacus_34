package com.jigar.me.ui.view.home.screens.settings.components.voice

import com.jigar.me.ui.view.home.theme.AppDimens

import android.speech.tts.Voice
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.jetpack.utils.ui.slider.SingleSlider
import com.jigar.me.ui.view.home.screens.settings.viewmodels.SettingUiState
import java.util.Locale

@Composable
fun VoiceSettingSheetContent(
    uiState: SettingUiState,
    onLanguageSelect: (Locale) -> Unit,
    onVoiceSelect: (Voice) -> Unit,
    onPitchChange: (Int) -> Unit,
    onSpeedChange: (Int) -> Unit,
    onTest: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppDimens.Dimens16)
    ) {

        // ─────────────────── Handle Bar ───────────────────
        Spacer(Modifier.height(AppDimens.Dimens12))
        Box(
            modifier = Modifier
                .width(AppDimens.Dimens40)
                .height(AppDimens.Dimens4)
                .background(Color.DarkGray, RoundedCornerShape(AppDimens.Dimens2))
                .align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(AppDimens.Dimens16))

        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
            Column(Modifier.weight(1f)) {
                // ─────────────────── Languages ───────────────────
                Text(
                    text = "Languages (${uiState.languages.size})",
                    style = MaterialTheme.typography.bodyLarge.scaled().copy(color = Color.Black, fontWeight = FontWeight.Medium, fontFamily = FontFamily(Font(R.font.font_medium))),
                )

                Spacer(Modifier.height(AppDimens.Dimens6))
                SimpleDropdownField(
                    text = uiState.selectedLanguage?.displayName ?: "",
                    items = uiState.languages,
                    itemLabel = { it.displayName },
                    onSelect = onLanguageSelect
                )
            }
//            Column(Modifier.weight(1f)) {
//                // ─────────────────── Voices ───────────────────
//
//                Text(
//                    text = "Voices (${uiState.voices.size})",
//                    style = MaterialTheme.typography.bodyLarge.scaled().copy(color = Color.Black, fontWeight = FontWeight.Medium, fontFamily = FontFamily(Font(R.font.font_medium))),
//                )
//
//                Spacer(Modifier.height(AppDimens.Dimens6))
//                SimpleDropdownField(
//                    text = uiState.selectedVoice?.name ?: "",
//                    items = uiState.voices,
//                    itemLabel = { "${it.name} (${it.locale.country})" },
//                    onSelect = onVoiceSelect
//                )
//            }
        }

        Spacer(Modifier.height(AppDimens.Dimens16))

        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
            // ─────────────────── Pitch ───────────────────
            Column(Modifier.weight(1f)) {
                Text(text = stringResource(R.string.pitch),style = MaterialTheme.typography.bodyLarge.scaled().copy(color = Color.Black, fontWeight = FontWeight.Medium, fontFamily = FontFamily(Font(R.font.font_medium))),)
                SingleSlider(
                    isShowText = true, value = uiState.pitch, range = 0f..20f, onValueChange = onPitchChange, modifier = Modifier.padding(horizontal = AppDimens.Dimens8)
                )
            }
            // ─────────────────── Speed ───────────────────
            Column(Modifier.weight(1f)) {
                Text(text = stringResource(R.string.voice_speed),style = MaterialTheme.typography.bodyLarge.scaled().copy(color = Color.Black, fontWeight = FontWeight.Medium, fontFamily = FontFamily(Font(R.font.font_medium))),)
                SingleSlider(
                    isShowText = true, value = uiState.speed, range = 0f..20f, onValueChange = onSpeedChange, modifier = Modifier.padding(horizontal = AppDimens.Dimens8)
                )
            }
        }

        // ─────────────────── Buttons ───────────────────
        Spacer(Modifier.height(AppDimens.Dimens16))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {

            TextButton(onClick = onCancel) {
                Text(text = stringResource(R.string.txtCancel))
            }

            Row {
                Button(
                    onClick = onTest,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(stringResource(R.string.check_voice))
                }

                Spacer(Modifier.width(AppDimens.Dimens12))

                Button(onClick = onSave) {
                    Text(stringResource(R.string.update))
                }
            }
        }

        Spacer(Modifier.height(AppDimens.Dimens16))
    }
}
