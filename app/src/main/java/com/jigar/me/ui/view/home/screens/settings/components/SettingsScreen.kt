package com.jigar.me.ui.view.home.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.PanToolAlt
import androidx.compose.material.icons.outlined.Speaker
import androidx.compose.material.icons.outlined.Swipe
import androidx.compose.material.icons.outlined.ViewColumn
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.ab.AbThemeController
import com.jigar.me.ui.view.ab.AbThemeMode
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
            .padding(
                start = DeviceInfo.screenHorizontalPadding(),
                end = AppDimens.Dimens16,
                top = AppDimens.Dimens12,
                bottom = AppDimens.Dimens16
            ),
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
    ) {

        ThemeSection(
            viewModel = viewModel,
            selectedTheme = viewModel.selectedTheme,
            onThemeSelected = viewModel::selectTheme
        )

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
        ) {
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

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
        ) {
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
                    ),
                    // In-app Light/Dark choice for the restyled screens (never follows the device).
                    ToggleItem(
                        Icons.Outlined.DarkMode,
                        "Dark Mode",
                        AbThemeController.mode == AbThemeMode.Dark
                    ) { AbThemeController.set(if (it) AbThemeMode.Dark else AbThemeMode.Light) }
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
                    ),
                    ToggleItem(Icons.Outlined.ViewColumn,
                        stringResource(R.string.txt_setting_7_rods_mode),
                        uiState.is7RodsMode,
                        viewModel::toggle7RodsMode
                    )
                )
            )
        }
    }
}
