package com.jigar.me.ui.view.home.screens.math_game_zone.number_sequence_puzzle.viewmodels

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorYellowOrange
import com.jigar.me.utils.AppConstants
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
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

    private val _uiState = MutableStateFlow(NumberSequencePuzzleUiState())
    val uiState: StateFlow<NumberSequencePuzzleUiState> = _uiState.asStateFlow()


    private var gridSize = 3

    private val yellowShades = listOf(
        Color(0xFFF8C8DC),
        Color(0xFFF8BBD0),
        Color(0xFFF48FB1),
        Color(0xFFCE5B82),
        Color(0xFFFF538E),
        Color(0xFFE91E63).copy(alpha = 0.5f),
        Color(0xFFE91E63).copy(alpha = 0.45f),
        Color(0xFFE91E63).copy(alpha = 0.40f),
        Color(0xFFE91E63).copy(alpha = 0.35f),
        Color(0xFFE91E63).copy(alpha = 0.30f),
        Color(0xFFE91E63).copy(alpha = 0.25f),
        Color(0xFFE91E63).copy(alpha = 0.20f),
    )

    private val orangeShades = listOf(
        Color(0xFFFFBF00),
        Color(0xFFFBCEB1),
        Color(0xFFFFAC1C),
        Color(0xFFECB28D),
        ColorYellowOrange,
        Color(0xFFFF951E),
        Color(0xFFFAD5A5),
        Color(0xFFFFD580),
        Color(0xFFFFDEAD),
        Color(0xFFF4BB44),
        Color(0xFFFFA500),
        Color(0xFFFAC898),
        Color(0xFFBD9956),
        Color(0xFFF59816),
        Color(0xFFD5871A),
        Color(0xFFE0613A),
        Color(0xFFFF825B),
        Color(0xFFFFA284),
        Color(0xFFFF9F85),
    )

    private val blueShades = listOf(
        Color(0xFFBBDEFB),
        Color(0xFF90CAF9),
        Color(0xFFB3E5FC),
        Color(0xFF81D4FA),
        Color(0xFF80D8FF),
        Color(0xFF1FA0DA),
        Color(0xFF66AAEE),
        Color(0xFF3CC1FF),
        Color(0xFF22A6EF),
        Color(0xFF7084FF),
        Color(0xFF919FFF),
        Color(0xFF9FA9E7),
        Color(0xFF6E7EE8),
        Color(0xFF038FC5),
        Color(0xFF73DBFF),
        Color(0xFF87CFEE),
        Color(0xFF00B6FA).copy(alpha = 0.30f),
        Color(0xFF00B6FA).copy(alpha = 0.40f),
        Color(0xFF00B6FA).copy(alpha = 0.50f),
        Color(0xFF2196F3).copy(alpha = 0.30f),
        Color(0xFF2196F3).copy(alpha = 0.40f),
        Color(0xFF2196F3).copy(alpha = 0.50f),
        Color(0xFF1B3BE5).copy(alpha = 0.15f),
        Color(0xFF1B3BE5).copy(alpha = 0.20f),
        Color(0xFF1B3BE5).copy(alpha = 0.25f),
        Color(0xFF1B3BE5).copy(alpha = 0.30f),
        Color(0xFF1B3BE5).copy(alpha = 0.35f),
        Color(0xFF1B3BE5).copy(alpha = 0.40f),
        Color(0xFF1B3BE5).copy(alpha = 0.45f),
        Color(0xFF1B3BE5).copy(alpha = 0.50f),
        Color(0xFF1B3BE5).copy(alpha = 0.55f),
    )


    fun initialize(size: Int) {
        gridSize = size
        _uiState.value = NumberSequencePuzzleUiState(
            tiles = generateSolvableGrid(gridSize),
            tileColors = generateTileColors(gridSize),
            soundOn = prefManager.getCustomParamBoolean(
                AppConstants.Settings.Setting_NumberPuzzleVolume,
                true
            )
        )
    }

    private fun generateTileColors(size: Int): List<List<Color>> {
        return List(size) {
            List(size) {
                randomTileColor(size)
            }
        }
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
                tileColors = generateTileColors(gridSize),
                moveCount = 0,
                isSolved = false
            )
        }
    }

    fun onTileMove(row: Int, col: Int) {
        if (_uiState.value.isSolved) return
        val (newTiles, moved) = moveTile(_uiState.value.tiles, row, col)
        if (moved) {
            val isSolve = checkSolved(newTiles)
            val playWin = isSolve && _uiState.value.soundOn
            val playSwap = !isSolve && _uiState.value.soundOn

            if (playSwap) AudioPlayerManager.playSoundSwip()
            if (playWin) AudioPlayerManager.playSoundWin()

            if (isSolve) {
                com.jigar.me.ui.view.home.screens.math_game_zone.common.ZoneSolveCounter.increment(
                    prefManager, com.jigar.me.ui.view.home.screens.math_game_zone.common.ZoneSolveCounter.NUMBER_SEQUENCE,
                    "grid$gridSize"
                )
                com.jigar.me.utils.WeeklySummaryManager.record(prefManager, 1)
            }
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

    fun randomTileColor(gridSize: Int): Color {
        return when (gridSize) {
            3 -> yellowShades.random()
            4 -> orangeShades.random()
            else -> blueShades.random()
        }
    }

}
