package com.jigar.me.ui.view.jetpack.fragments.setting.viewmodels

import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.jetpack.core.StatefulViewModelAbacus
import com.jigar.me.ui.view.jetpack.utils.TextToSpeechManager
import com.jigar.me.utils.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
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