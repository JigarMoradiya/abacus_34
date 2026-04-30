package com.jigar.me.ui.view.home.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.PanToolAlt
import androidx.compose.material.icons.outlined.Speaker
import androidx.compose.material.icons.outlined.Swipe
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.jigar.me.R
import com.jigar.me.ui.view.home.screens.settings.viewmodels.SettingUiState
import com.jigar.me.ui.view.home.screens.settings.viewmodels.SettingViewModel
import com.jigar.me.ui.view.home.screens.settings.viewmodels.ToggleItem
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun SettingsScreen(
    viewModel: SettingViewModel,
    uiState: SettingUiState,
    onMusicVolumeChange: (Int) -> Unit,
    onVoiceClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = AppDimens.Dimens16)
            .padding(start = AppDimens.Dimens16, end = AppDimens.Dimens16)
    ) {

        ThemeSection(
            viewModel = viewModel,
            selectedTheme = viewModel.selectedTheme,
            onThemeSelected = viewModel::selectTheme
        )

        Spacer(Modifier.height(AppDimens.Dimens12))

        Row(verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
            VoiceSection(
                modifier = Modifier.weight(1f),
                onVoiceChange = {
                    onVoiceClick()
                }
            )
            MusicVolumeSection(
                modifier = Modifier.weight(1f),
                volume = uiState.musicVolume,
                onVolumeChange = {
                    viewModel.updateMusicVolume(it)     // save
                    onMusicVolumeChange(it)              // play immediately
                }
            )
        }

        Spacer(Modifier.height(AppDimens.Dimens12))

        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
            ToggleSection(
                modifier = Modifier.weight(1f),
                items = listOf(
                    ToggleItem(Icons.Outlined.Visibility,
                        stringResource(R.string.txt_setting_display_abacus_number),
                        uiState.displayNumber,
                        viewModel::toggleDisplayNumber
                    ),
                    ToggleItem(Icons.AutoMirrored.Filled.HelpOutline,
                        stringResource(R.string.txt_setting_display_help_message),
                        uiState.displayHint,
                        viewModel::toggleHintMessage
                    ),
                    ToggleItem(
                        Icons.Outlined.Swipe,
                        stringResource(R.string.display_bead_direction),
                        uiState.displayDirection,
                        viewModel::toggleDirection
                    )
                )
            )

            ToggleSection(
                modifier = Modifier.weight(1f),
                items = listOf(
                    ToggleItem(Icons.Outlined.PanToolAlt,
                        stringResource(R.string.txt_setting_left_hand),
                        uiState.leftHanded,
                        viewModel::toggleLeftHand
                    ),
                    ToggleItem(Icons.Outlined.Speaker,
                        stringResource(R.string.txt_setting_hintsound),
                        uiState.sumSound,
                        viewModel::toggleSumSound
                    ),
                    ToggleItem(Icons.Outlined.MusicNote,
                        stringResource(R.string.txt_setting_sound),
                        uiState.beadSound,
                        viewModel::toggleBeadSound
                    )
                )
            )
        }
    }
}
