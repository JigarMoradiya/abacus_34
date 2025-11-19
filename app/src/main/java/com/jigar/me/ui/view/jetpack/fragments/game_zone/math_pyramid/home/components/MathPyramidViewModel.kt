package com.jigar.me.ui.view.jetpack.fragments.game_zone.math_pyramid.home.components

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.jigar.me.ui.view.jetpack.fragments.common.enums.CommonDifficulty4
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MathPyramidViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // KEY for persistence
    companion object {
        private const val KEY_UI_STATE = "pyramid_ui_state"
    }

    // Load previous state (if process died)
    private val _uiState = MutableStateFlow(
        savedStateHandle.get<MathPyramidUiState>(KEY_UI_STATE) ?: MathPyramidUiState()
    )

    val uiState: StateFlow<MathPyramidUiState> = _uiState

    private fun updateState(reducer: MathPyramidUiState.() -> MathPyramidUiState) {
        val newState = _uiState.value.reducer()
        _uiState.value = newState
        savedStateHandle[KEY_UI_STATE] = newState   // persist for process death
    }

    fun selectLevel(level: Int) {
        updateState { copy(selectedLevel = level) }
    }

    fun selectDifficulty(diff: CommonDifficulty4) {
        updateState { copy(selectedDifficulty = diff) }
    }
}
