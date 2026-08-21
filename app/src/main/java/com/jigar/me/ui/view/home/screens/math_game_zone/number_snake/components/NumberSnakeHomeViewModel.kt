package com.jigar.me.ui.view.home.screens.math_game_zone.number_snake.components

import androidx.lifecycle.ViewModel
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NumberSnakeHomeViewModel @Inject constructor(
    private val prefManager: AppPreferencesHelper
) : ViewModel() {

    private val _selectedDifficulty = MutableStateFlow(CommonDifficulty4.easy)
    val selectedDifficulty: StateFlow<CommonDifficulty4> = _selectedDifficulty

    fun selectDifficulty(difficulty: CommonDifficulty4) {
        _selectedDifficulty.value = difficulty
    }

    fun bestTime(difficulty: CommonDifficulty4): Int =
        prefManager.getCustomParamInt("numberSnakeBestTime_${difficulty.name}", 0)
}
