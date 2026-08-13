package com.jigar.me.ui.view.home.screens.math_game_zone.magic_square.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.magic_square.components.MagicSquareConfig
import com.jigar.me.ui.view.home.screens.math_game_zone.magic_square.components.MagicSquareGenerator
import com.jigar.me.ui.view.home.screens.math_game_zone.magic_square.components.MagicSquareUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

const val KEY_MAGIC_SQUARE_DIFF = "magic_square_diff"

@HiltViewModel
class MagicSquarePlayViewModel @Inject constructor(
    private val prefManager: AppPreferencesHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val difficulty: CommonDifficulty4 =
        savedStateHandle.get<String>(KEY_MAGIC_SQUARE_DIFF)?.let { CommonDifficulty4.fromName(it) }
            ?: CommonDifficulty4.easy
    val config: MagicSquareConfig = MagicSquareConfig.forDifficulty(difficulty)
    val target: Int get() = config.target

    private val _uiState = MutableStateFlow(MagicSquareUiState())
    val uiState: StateFlow<MagicSquareUiState> = _uiState

    private var timerJob: Job? = null

    val multiplier: Int
        get() = when (_uiState.value.streak) { in 0..2 -> 1; in 3..5 -> 2; else -> 3 }

    private val bestScoreKey get() = "magicSquareBest_${difficulty.name}"
    val bestScore: Int get() = prefManager.getCustomParamInt(bestScoreKey, 0)

    fun start() {
        stop()
        _uiState.value = MagicSquareUiState(timeLeft = config.timerSeconds ?: 0)
        loadPuzzle()
        if (config.timerSeconds != null) {
            timerJob = viewModelScope.launch {
                while (isActive && !_uiState.value.isGameOver) {
                    delay(1000)
                    val left = _uiState.value.timeLeft - 1
                    _uiState.update { it.copy(timeLeft = left) }
                    if (left <= 0) endGame()
                }
            }
        }
    }

    fun stop() { timerJob?.cancel(); timerJob = null }
    override fun onCleared() { stop() }

    private fun loadPuzzle() {
        val p = MagicSquareGenerator.make(config)
        _uiState.update {
            it.copy(
                fixed = p.fixed,
                grid = (0..8).map { i -> if (p.fixed[i]) p.solution[i] else 0 },
                palette = p.palette,
                selectedIndex = null,
                justSolved = false
            )
        }
    }

    // Tap a cell: pick a placed number back up, or select an empty cell.
    fun tapCell(index: Int) {
        val s = _uiState.value
        if (s.fixed[index] || s.justSolved) return
        if (s.grid[index] != 0) {
            val newGrid = s.grid.toMutableList().also { it[index] = 0 }
            _uiState.update {
                it.copy(grid = newGrid, palette = it.palette + s.grid[index], selectedIndex = index)
            }
        } else {
            _uiState.update { it.copy(selectedIndex = index) }
        }
    }

    // Tap a palette number: place it in the selected cell (or first empty).
    fun placeNumber(n: Int) {
        val s = _uiState.value
        if (s.justSolved) return
        val i = s.selectedIndex ?: firstEmptyEditable(s) ?: return
        if (s.fixed[i]) return

        val newGrid = s.grid.toMutableList()
        val newPalette = s.palette.toMutableList()
        if (newGrid[i] != 0) newPalette.add(newGrid[i])
        newGrid[i] = n
        val pi = newPalette.indexOf(n)
        if (pi >= 0) newPalette.removeAt(pi)
        AudioPlayerManager.playSoundTilePlace()
        _uiState.update { it.copy(grid = newGrid, palette = newPalette, selectedIndex = null) }
        checkWin()
    }

    private fun firstEmptyEditable(s: MagicSquareUiState): Int? =
        (0..8).firstOrNull { s.grid[it] == 0 && !s.fixed[it] }

    fun rowSum(g: List<Int>, r: Int): Int = (0..2).sumOf { g[r * 3 + it] }
    fun colSum(g: List<Int>, c: Int): Int = (0..2).sumOf { g[it * 3 + c] }
    fun rowComplete(g: List<Int>, r: Int): Boolean =
        (0..2).all { g[r * 3 + it] != 0 } && rowSum(g, r) == target
    fun colComplete(g: List<Int>, c: Int): Boolean =
        (0..2).all { g[it * 3 + c] != 0 } && colSum(g, c) == target

    private fun linesSatisfied(g: List<Int>): Boolean {
        for (i in 0..2) if (rowSum(g, i) != target || colSum(g, i) != target) return false
        if (config.requireDiagonals) {
            if (g[0] + g[4] + g[8] != target) return false
            if (g[2] + g[4] + g[6] != target) return false
        }
        return true
    }

    private fun checkWin() {
        val s = _uiState.value
        if (s.grid.contains(0) || !linesSatisfied(s.grid)) return
        AudioPlayerManager.playSoundSparkle()
        val mult = multiplier
        _uiState.update {
            it.copy(streak = it.streak + 1, score = it.score + 50 * mult,
                puzzlesSolved = it.puzzlesSolved + 1, justSolved = true)
        }
        viewModelScope.launch {
            delay(1100)
            if (_uiState.value.isGameOver) return@launch
            val goal = config.puzzlesGoal
            if (goal != null && _uiState.value.puzzlesSolved >= goal) {
                endGame()
            } else {
                loadPuzzle()
            }
        }
    }

    private fun endGame() {
        stop()
        val finalScore = _uiState.value.score
        if (finalScore > bestScore) prefManager.setCustomParamInt(bestScoreKey, finalScore)
        com.jigar.me.utils.WeeklySummaryManager.record(prefManager, _uiState.value.puzzlesSolved)
        _uiState.update { it.copy(isGameOver = true) }
    }
}
