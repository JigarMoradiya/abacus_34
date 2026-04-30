package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SudokuDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SudokuHomeUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SudokuSize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SudokuHomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_UI_STATE = "sudoku_ui_state"
    }

    private val _uiState = MutableStateFlow(
        savedStateHandle.get<SudokuHomeUiState>(KEY_UI_STATE) ?: SudokuHomeUiState()
    )

    val uiState: StateFlow<SudokuHomeUiState> = _uiState

    private fun updateState(reducer: SudokuHomeUiState.() -> SudokuHomeUiState) {
        val newState = _uiState.value.reducer()
        _uiState.value = newState
        savedStateHandle[KEY_UI_STATE] = newState // persist on config change & process death
    }

    fun selectSize(size: SudokuSize) {
        if (_uiState.value.selectedSize != size){
            AudioPlayerManager.playSoundBtnClick()
            updateState { copy(selectedSize = size) }
        }
    }

    fun selectDifficulty(diff: SudokuDifficulty4) {
        if (_uiState.value.selectedDifficulty != diff){
            AudioPlayerManager.playSoundBtnClick()
            updateState { copy(selectedDifficulty = diff) }
        }
    }

    fun setDataGameStart(size: SudokuSize, diff: SudokuDifficulty4, isNew: Boolean) {
        updateState { copy(selectedSizeFinal = size, selectedDifficultyFinal = diff,isNewGame = isNew) }
    }

    fun openResumePopup() {
        updateState {copy(showResumePopup = true) }
    }

    fun closeResumePopup() {
        updateState { copy(showResumePopup = false) }
    }
}