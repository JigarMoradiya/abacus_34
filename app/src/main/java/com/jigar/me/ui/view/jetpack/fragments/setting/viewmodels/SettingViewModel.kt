package com.jigar.me.ui.view.jetpack.fragments.setting.viewmodels

import android.speech.tts.TextToSpeech
import androidx.fragment.app.FragmentActivity
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.VoiceControllerSetting
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.VoiceControllerSettingInterface
import com.jigar.me.ui.view.jetpack.core.StatefulViewModelAbacus
import com.jigar.me.ui.view.jetpack.utils.TextToSpeechManager
import com.jigar.me.utils.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    ttsManager: TextToSpeechManager,
    private val prefs: AppPreferencesHelper,
) : StatefulViewModelAbacus<SettingUiState>(ttsManager = ttsManager, prefs = prefs,numberOfColumns = 3) {

    override val TAG = "SettingViewModel"

    override fun getInitialState() = SettingUiState(
        displayNumber = prefs.getCustomParamBoolean(
            AppConstants.Settings.Setting_display_abacus_number, true
        ),
        displayHint = prefs.getCustomParamBoolean(
            AppConstants.Settings.Setting_display_help_message, true
        ),
        displayDirection = prefs.getCustomParamBoolean(
            AppConstants.Settings.Setting_direction, true
        ),
        leftHanded = prefs.getCustomParamBoolean(
            AppConstants.Settings.Setting_left_hand, true
        ),
        sumSound = prefs.getCustomParamBoolean(
            AppConstants.Settings.Setting__hint_sound, true
        ),
        beadSound = prefs.getCustomParamBoolean(
            AppConstants.Settings.Setting_sound, true
        ),
        musicVolume = prefs.getCustomParamInt(
            AppConstants.Settings.Setting_bg_music_volume, AppConstants.Settings.Setting_bg_music_volume_default
        )
    )

    fun loadVoiceSettings(tts: TextToSpeech) {

        val languages = tts.availableLanguages?.toList()?.sortedBy { it.displayName } ?: emptyList()

        val savedLangTag = prefs.getCustomParam(
            AppPreferencesHelper.KEY_DEFAULT_TTS_LANGUAGE,
            AppPreferencesHelper.DEFAULT_TTS_LANGUAGE_VALUE
        )

        val selectedLang = languages.firstOrNull {
            it.toLanguageTag().equals(savedLangTag, true)
        } ?: languages.firstOrNull()

        val voices = tts.voices
            ?.filter { it.locale == selectedLang }
            ?.toList()
            ?: emptyList()

        val savedVoiceName = prefs.getCustomParam(
            AppPreferencesHelper.KEY_DEFAULT_TTS_VOICE,
            AppPreferencesHelper.DEFAULT_TTS_VOICE_VALUE
        )

        val selectedVoice = voices.firstOrNull { it.name == savedVoiceName } ?: voices.firstOrNull()

        updateState_ {
            copy(
                languages = languages,
                voices = voices,
                selectedLanguage = selectedLang,
                selectedVoice = selectedVoice,
                pitch = (prefs.getDefaultTTSPitch() * 10).toInt(),
                speed = (prefs.getDefaultTTSSpeed() * 10).toInt()
            )
        }
    }

    fun onLanguageSelected(tts: TextToSpeech, locale: Locale) {
        val voices = tts.voices
            ?.filter { it.locale == locale }
            ?.toList()
            ?: emptyList()

        updateState_ {
            copy(
                selectedLanguage = locale,
                voices = voices,
                selectedVoice = voices.firstOrNull()
            )
        }
    }

    fun testVoice(tts: TextToSpeech) {
        val state = state()
        tts.voice = state.selectedVoice
        tts.setPitch(state.pitch / 10f)
        tts.setSpeechRate(state.speed / 10f)
        tts.speak("Welcome to Abacus Child", TextToSpeech.QUEUE_FLUSH, null, "test")
    }

    fun saveVoiceSettings() {
        val state = state()

        prefs.setCustomParam(
            AppPreferencesHelper.KEY_DEFAULT_TTS_LANGUAGE,
            state.selectedLanguage?.toLanguageTag() ?: ""
        )

        prefs.setCustomParam(
            AppPreferencesHelper.KEY_DEFAULT_TTS_VOICE,
            state.selectedVoice?.name ?: ""
        )
        prefs.setCustomParamFloat(AppPreferencesHelper.KEY_DEFAULT_TTS_PITCH, state.pitch.toFloat())
        prefs.setCustomParamFloat(AppPreferencesHelper.KEY_DEFAULT_TTS_SPEECH, state.speed.toFloat())
        updateSpeechSettings()
    }

    /* -------------------------
    * Theme
    * ------------------------- */
    fun selectTheme(theme: String) {
        prefs.setCustomParam(AppConstants.Settings.Theam, theme)
        updateState_ {
            copy(previewKey = previewKey + 1)
        }
    }

    /* -------------------------
     * Music Volume
     * ------------------------- */
    fun updateMusicVolume(value: Int) {
        prefs.setCustomParamInt(
            AppConstants.Settings.Setting_bg_music_volume, value
        )
        updateState_ { copy(musicVolume = value) }
    }

    /* -------------------------
     * Toggles
     * ------------------------- */
    fun toggleDisplayNumber(value: Boolean) {
        prefs.setCustomParamBoolean(
            AppConstants.Settings.Setting_display_abacus_number, value
        )
        updateState_ { copy(displayNumber = value) }
    }

    fun toggleHintMessage(value: Boolean) {
        prefs.setCustomParamBoolean(
            AppConstants.Settings.Setting_display_help_message, value
        )
        updateState_ { copy(displayHint = value) }
    }

    fun toggleDirection(value: Boolean) {
        prefs.setCustomParamBoolean(
            AppConstants.Settings.Setting_direction, value
        )
        updateShowDirectionHints(value)
        updateState_ { copy(displayDirection = value) }
    }

    fun toggleLeftHand(value: Boolean) {
        prefs.setCustomParamBoolean(
            AppConstants.Settings.Setting_left_hand, value
        )
        updateState_ { copy(leftHanded = value) }
    }

    fun toggleSumSound(value: Boolean) {
        prefs.setCustomParamBoolean(
            AppConstants.Settings.Setting__hint_sound, value
        )
        updateState_ { copy(sumSound = value) }
    }

    fun toggleBeadSound(value: Boolean) {
        prefs.setCustomParamBoolean(
            AppConstants.Settings.Setting_sound, value
        )
        updateState_ { copy(beadSound = value) }
    }


    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }
}