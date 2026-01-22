package com.jigar.me.ui.view.jetpack.fragments.other.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.core.presentation.theme.AbacusTheme
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText
import com.jigar.me.ui.view.jetpack.fragments.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.jetpack.fragments.other.setting.components.SettingsScreen
import com.jigar.me.ui.view.jetpack.fragments.other.setting.components.voice.VoiceSettingBottomSheetFragment
import com.jigar.me.ui.view.jetpack.fragments.other.setting.viewmodels.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragmentNew : Fragment() {
    private val viewModel: SettingViewModel by activityViewModels()
    private val homeActivityViewModel: HomeActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                AbacusTheme {
                    Column(modifier = Modifier.fillMaxSize()) {
                        BackButtonWithText(title = stringResource(R.string.txt_setting_title), onBackClick = {findNavController().popBackStack()})

                        SettingsScreen(
                            viewModel = viewModel,
                            uiState = uiState,
                            onMusicVolumeChange = homeActivityViewModel::updateMusicVolume,
                            onVoiceClick = {
                                VoiceSettingBottomSheetFragment
                                    .newInstance()
                                    .show(childFragmentManager, "VoiceSettingBottomSheet")
                            }
                        )
                    }
                }

            }
        }
    }

}
