package com.jigar.me.ui.view.jetpack.fragments.setting.components.voice

import android.app.Dialog
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jigar.me.ui.view.jetpack.fragments.setting.viewmodels.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VoiceSettingBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModel: SettingViewModel by activityViewModels()

    private lateinit var tts: TextToSpeech

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                    ?: return@setOnShowListener

            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
            behavior.skipCollapsed = true
        }

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        tts = TextToSpeech(requireContext()) {
            viewModel.loadVoiceSettings(tts)
        }

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                VoiceSettingSheetContent(
                    uiState = uiState,
                    onLanguageSelect = { viewModel.onLanguageSelected(tts, it) },
                    onPitchChange = { viewModel.updateState_ { copy(pitch = it) } },
                    onSpeedChange = { viewModel.updateState_ { copy(speed = it) } },
                    onTest = { viewModel.testVoice(tts) },
                    onSave = {
                        viewModel.saveVoiceSettings()
                        dismiss()
                    },
                    onCancel = { dismiss() },
                    onVoiceSelect = {}
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (tts.isSpeaking) tts.stop()
        tts.shutdown()
    }

    companion object {
        fun newInstance() = VoiceSettingBottomSheetFragment()
    }
}
