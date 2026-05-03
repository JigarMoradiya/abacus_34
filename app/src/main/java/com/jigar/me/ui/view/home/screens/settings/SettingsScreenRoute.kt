package com.jigar.me.ui.view.home.screens.settings

import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.home.screens.settings.components.SettingsScreen
import com.jigar.me.ui.view.home.screens.settings.components.voice.VoiceSettingSheetContent
import com.jigar.me.ui.view.home.screens.settings.viewmodels.SettingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenRoute(
    homeActivityViewModel: HomeActivityViewModel,
    onBackClick: () -> Unit,
) {
    val activity = LocalContext.current as ComponentActivity
    val viewModel: SettingViewModel = hiltViewModel(viewModelStoreOwner = activity)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showVoiceSheet by remember {
        mutableStateOf(false)
    }

    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        BackButtonWithText(
            title = stringResource(R.string.txt_setting_title),
            onBackClick = onBackClick
        )

        SettingsScreen(
            viewModel = viewModel,
            uiState = uiState,
            onMusicVolumeChange = homeActivityViewModel::updateMusicVolume,
            onVoiceClick = {
                showVoiceSheet = true
            }
        )

        if (showVoiceSheet) {

            val context = LocalContext.current
            var tts by remember { mutableStateOf<TextToSpeech?>(null) }

            LaunchedEffect(Unit) {
                tts = TextToSpeech(context) {
                    viewModel.loadVoiceSettings(tts!!)
                }
            }

            DisposableEffect(Unit) {
                onDispose {
                    tts?.let {
                        if (it.isSpeaking) {
                            it.stop()
                        }
                        it.shutdown()
                    }
                }
            }

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            val sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )

            ModalBottomSheet(
                onDismissRequest = {
                    showVoiceSheet = false
                },
                sheetState = sheetState,
                dragHandle = null
            ) {
                VoiceSettingSheetContent(
                    uiState = uiState,

                    onLanguageSelect = {
                        tts?.let { engine ->
                            viewModel.onLanguageSelected(engine, it)
                        }
                    },

                    onPitchChange = {
                        viewModel.updateState_ {
                            copy(pitch = it)
                        }
                    },

                    onSpeedChange = {
                        viewModel.updateState_ {
                            copy(speed = it)
                        }
                    },

                    onTest = {
                        tts?.let { engine ->
                            viewModel.testVoice(engine)
                        }
                    },

                    onSave = {
                        viewModel.saveVoiceSettings()
                        showVoiceSheet = false
                    },

                    onCancel = {
                        showVoiceSheet = false
                    },

                    onVoiceSelect = {
                        viewModel.onVoiceSelected(it)
                    }
                )
            }
        }
    }
}
