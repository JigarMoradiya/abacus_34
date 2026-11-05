package com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.components

import androidx.lifecycle.ViewModel
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CrossMathHomeViewModel @Inject constructor(
    private val prefManager: AppPreferencesHelper
) : ViewModel() {

    val progress = CrossMathProgress(prefManager)

    private val _selectedDifficulty = MutableStateFlow(
        CommonDifficulty4.fromName(prefManager.getCustomParam("crossMathDiff", CommonDifficulty4.easy.name))
    )
    val selectedDifficulty: StateFlow<CommonDifficulty4> = _selectedDifficulty

    // Bumped after each play session so the roadmap re-reads star data.
    private val _progressVersion = MutableStateFlow(0)
    val progressVersion: StateFlow<Int> = _progressVersion

    fun selectDifficulty(difficulty: CommonDifficulty4) {
        _selectedDifficulty.value = difficulty
        prefManager.setCustomParam("crossMathDiff", difficulty.name)
    }

    fun refreshProgress() { _progressVersion.value++ }
}
