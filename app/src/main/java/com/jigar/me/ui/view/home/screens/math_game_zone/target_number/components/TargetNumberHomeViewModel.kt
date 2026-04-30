package com.jigar.me.ui.view.home.screens.math_game_zone.target_number.components

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize

@Parcelize
data class TargetNumberUiState(
    val selectedLevel: Int = 2,
    val selectedDifficulty: CommonDifficulty4 = CommonDifficulty4.easy
) : Parcelable

@HiltViewModel
class TargetNumberViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_UI_STATE = "pyramid_ui_state"
    }

    private val _uiState = MutableStateFlow(
        savedStateHandle.get<TargetNumberUiState>(KEY_UI_STATE) ?: TargetNumberUiState()
    )

    val uiState: StateFlow<TargetNumberUiState> = _uiState

    private fun updateState(reducer: TargetNumberUiState.() -> TargetNumberUiState) {
        val newState = _uiState.value.reducer()
        _uiState.value = newState
        savedStateHandle[KEY_UI_STATE] = newState // persist on config change & process death
    }

    fun selectLevel(level: Int) {
        if (uiState.value.selectedLevel != level){
            AudioPlayerManager.playSoundBtnClick()
            updateState { copy(selectedLevel = level) }
        }
    }

    fun selectDifficulty(diff: CommonDifficulty4) {
        if (uiState.value.selectedDifficulty != diff){
            AudioPlayerManager.playSoundBtnClick()
            updateState { copy(selectedDifficulty = diff) }
        }
    }
}