package com.jigar.me.ui.view.jetpack.fragments.game_zone.number_sequence_puzzle.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.PlaySound
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class NumberSequencePuzzleViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val prefManager: AppPreferencesHelper
) : ViewModel() {

    val _uiState = MutableStateFlow(NumberSequencePuzzleUiState())
    val uiState: StateFlow<NumberSequencePuzzleUiState> = _uiState.asStateFlow()

    private var gridSize = 3

    fun initialize(size: Int) {
        gridSize = size
        _uiState.value = NumberSequencePuzzleUiState(
            tiles = generateSolvableGrid(gridSize),
            soundOn = prefManager.getCustomParamBoolean(
                AppConstants.Settings.Setting_NumberPuzzleVolume,
                true
            )
        )
    }

    fun toggleSound() {
        val newVal = !_uiState.value.soundOn
        prefManager.setCustomParamBoolean(AppConstants.Settings.Setting_NumberPuzzleVolume, newVal)
        _uiState.update { it.copy(soundOn = newVal) }
    }

    fun restartGame() {
        _uiState.update {
            it.copy(
                tiles = generateSolvableGrid(gridSize),
                moveCount = 0,
                isSolved = false
            )
        }
    }

    fun onTileMove(row: Int, col: Int) {
        val (newTiles, moved) = moveTile(_uiState.value.tiles, row, col)
        if (moved) {
            val isSolve = checkSolved(newTiles)
            val playWin = isSolve && _uiState.value.soundOn
            val playSwap = !isSolve && _uiState.value.soundOn

            if (playSwap) PlaySound.playSwip(context)
            if (playWin) PlaySound.playWin(context)

            _uiState.update {
                it.copy(
                    tiles = newTiles,
                    moveCount = it.moveCount + 1,
                    isSolved = isSolve
                )
            }
        }
    }

    fun closePopup() {
        _uiState.update { it.copy(isSolved = false) }
    }

    fun playAgain() {
        restartGame()
    }
}
