package com.jigar.me.ui.view.jetpack.abacus_base.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.jigar.me.data.local.data.RodMovement
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.jetpack.utils.TextToSpeechManager
import com.jigar.me.utils.AppConstants

open class BaseAbacusViewModel(
    numberOfColumns: Int,
    private val prefs: AppPreferencesHelper
) : ViewModel() {

    // Shared abacus core state
    val abacusCalc: AbacusCalculations = AbacusCalculations(numberOfColumns = numberOfColumns)

    // get selected theme
    val selectedTheme : String
        get() = prefs.getCustomParam(AppConstants.Settings.Theam, AppConstants.Settings.theam_Default)

    // sound setting
    val isBeadSoundEnabled: Boolean
        get() = prefs.getCustomParamBoolean(AppConstants.Settings.Setting_sound, true)

    // sound setting
    val isDisplayCurrentAbacusInput: Boolean
        get() = prefs.getCustomParamBoolean(AppConstants.Settings.Setting_display_abacus_number, true)

    val isDisplayHelpMessage: Boolean
        get() = prefs.getCustomParamBoolean(AppConstants.Settings.Setting_display_help_message, true)

    val isAbacusQuestionSpeak: Boolean
        get() = prefs.getCustomParamBoolean(AppConstants.Settings.Setting__hint_sound, true)

    val isAbacusOnLeftHand: Boolean
        get() = prefs.getCustomParamBoolean(AppConstants.Settings.Setting_left_hand, true)

    var rodMovements by mutableStateOf(listOf<RodMovement>())
        protected set

    var showDirectionHints by mutableStateOf(false)
        protected set

    fun updateRodMovements(list: List<RodMovement>) {
        rodMovements = list
    }

    fun updateShowDirectionHints(show: Boolean) {
        showDirectionHints = show
    }
}
