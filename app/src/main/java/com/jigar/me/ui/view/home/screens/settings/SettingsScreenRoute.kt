package com.jigar.me.ui.view.home.screens.settings

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText
import com.jigar.me.ui.view.jetpack.fragments.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.jetpack.fragments.other.setting.components.SettingsScreen
import com.jigar.me.ui.view.jetpack.fragments.other.setting.components.voice.VoiceSettingBottomSheetFragment
import com.jigar.me.ui.view.jetpack.fragments.other.setting.viewmodels.SettingViewModel

@Composable
fun SettingsScreenRoute(
    homeActivityViewModel: HomeActivityViewModel,
    onBackClick: () -> Unit,
) {
    val activity = LocalContext.current as ComponentActivity
    val viewModel: SettingViewModel = hiltViewModel(viewModelStoreOwner = activity)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        BackButtonWithText(
            title = stringResource(R.string.txt_setting_title),
            onBackClick = onBackClick
        )

        SettingsScreen(
            viewModel = viewModel,
            uiState = uiState,
            onMusicVolumeChange = homeActivityViewModel::updateMusicVolume,
            onVoiceClick = {
                val fragmentActivity = activity as FragmentActivity
                VoiceSettingBottomSheetFragment
                    .newInstance()
                    .show(fragmentActivity.supportFragmentManager, "VoiceSettingBottomSheet")
            }
        )
    }
}
