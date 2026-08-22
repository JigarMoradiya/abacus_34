package com.jigar.me.ui.view.home.screens.math_game_zone.number_snake.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.number_snake.components.NumberSnakeConfig
import com.jigar.me.ui.view.home.screens.math_game_zone.number_snake.components.NumberSnakeGenerator
import com.jigar.me.ui.view.home.screens.math_game_zone.number_snake.components.NumberSnakeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

const val KEY_NUMBER_SNAKE_DIFF = "number_snake_diff"

@HiltViewModel
class NumberSnakePlayViewModel @Inject constructor(
    private val prefManager: AppPreferencesHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val difficulty: CommonDifficulty4 =
        savedStateHandle.get<String>(KEY_NUMBER_SNAKE_DIFF)?.let { CommonDifficulty4.fromName(it) }
            ?: CommonDifficulty4.easy
    val config: NumberSnakeConfig = NumberSnakeConfig.forDifficulty(difficulty)

    private val _uiState = MutableStateFlow(NumberSnakeUiState())
    val uiState: StateFlow<NumberSnakeUiState> = _uiState

    private var timerJob: Job? = null
    private var timerPaused = false
    fun setTimerPaused(paused: Boolean) { timerPaused = paused }

    private val bestScoreKey get() = "numberSnakeBest_${difficulty.name}"
    val bestScore: Int get() = prefManager.getCustomParamInt(bestScoreKey, 0)
    private val bestTimeKey get() = "numberSnakeBestTime_${difficulty.name}"
    val bestTime: Int get() = prefManager.getCustomParamInt(bestTimeKey, 0)

    fun start() {
        stop()
        _uiState.value = NumberSnakeUiState()
        loadPuzzle()
        timerJob = viewModelScope.launch {
            while (isActive && !_uiState.value.isGameOver) {
                delay(1000)
                if (timerPaused) continue
                _uiState.update { it.copy(elapsed = it.elapsed + 1) }
            }
        }
    }

    fun stop() { timerJob?.cancel(); timerJob = null }
    override fun onCleared() { stop() }

    private fun loadPuzzle() {
        val p = NumberSnakeGenerator.make(config)
        val n = p.size
        val total = n * n
        _uiState.update {
            it.copy(
                size = n,
                grid = p.given,
                given = p.given,
                solution = p.solution,
                tray = trayFor(p.given, total).shuffled(),
                selectedIndex = null,
                hintLocked = emptySet(),
                hintsLeft = config.hintLimit,
                hintsUsed = 0,
                wrongFlash = false,
                justSolved = false
            )
        }
    }

    private fun trayFor(given: List<Int>, total: Int): List<Int> {
        val used = given.filter { it != 0 }.toHashSet()
        return (1..total).filterNot { it in used }
    }

    fun tapCell(index: Int) {
        val s = _uiState.value
        if (s.given[index] != 0 || s.justSolved || index in s.hintLocked) return
        _uiState.update { it.copy(selectedIndex = index, wrongFlash = false) }
    }

    fun tapTrayNumber(number: Int) {
        val s = _uiState.value
        if (s.justSolved) return
        val i = s.selectedIndex ?: s.grid.indices.firstOrNull { s.given[it] == 0 && s.grid[it] == 0 && it !in s.hintLocked } ?: return
        if (s.given[i] != 0 || i in s.hintLocked) return
        AudioPlayerManager.playSoundTilePlace()

        val newGrid = s.grid.toMutableList()
        val newTray = s.tray.toMutableList()
        // Bumping out whatever was in this cell before (if any) back to the tray.
        val previous = newGrid[i]
        if (previous != 0) newTray.add(previous)
        newGrid[i] = number
        newTray.remove(number)

        val nextSelected = nearestEmptyIndex(i, s.size, newGrid)
        _uiState.update {
            it.copy(grid = newGrid, tray = newTray, selectedIndex = nextSelected, wrongFlash = false)
        }
        checkWin()
    }

    // After placing a number, jump the selection to the physically closest
    // blank cell instead of the next one in row-major order — jumping across
    // the grid to a far-away cell every tap made the flow feel random.
    private fun nearestEmptyIndex(from: Int, size: Int, grid: List<Int>): Int? {
        val fr = from / size; val fc = from % size
        return grid.indices
            .filter { grid[it] == 0 }
            .minByOrNull { idx ->
                val r = idx / size; val c = idx % size
                (r - fr) * (r - fr) + (c - fc) * (c - fc)
            }
    }

    fun clearCell(index: Int) {
        val s = _uiState.value
        if (s.given[index] != 0 || s.justSolved || index in s.hintLocked) return
        val value = s.grid[index]
        if (value == 0) return
        val newGrid = s.grid.toMutableList().also { it[index] = 0 }
        val newTray = s.tray.toMutableList().also { it.add(value) }
        _uiState.update { it.copy(grid = newGrid, tray = newTray, wrongFlash = false) }
    }

    fun useHint() {
        val s = _uiState.value
        if (s.justSolved || s.hintsLeft <= 0) return
        val emptyIndex = s.grid.indices.firstOrNull { s.grid[it] == 0 } ?: return
        val correct = s.solution[emptyIndex]
        AudioPlayerManager.playSoundSparkle()

        val newGrid = s.grid.toMutableList().also { it[emptyIndex] = correct }
        val newTray = s.tray.toMutableList().also { it.remove(correct) }
        _uiState.update {
            it.copy(
                grid = newGrid, tray = newTray,
                hintLocked = it.hintLocked + emptyIndex,
                hintsLeft = it.hintsLeft - 1,
                hintsUsed = it.hintsUsed + 1,
                selectedIndex = null
            )
        }
        checkWin()
    }

    private fun checkWin() {
        val s = _uiState.value
        if (s.grid.contains(0)) return
        if (s.grid != s.solution) {
            AudioPlayerManager.playSoundWrongSoft()
            _uiState.update { it.copy(wrongFlash = true) }
            return
        }

        AudioPlayerManager.playSoundSparkle()
        _uiState.update { it.copy(score = it.score + 60, justSolved = true, wrongFlash = false) }
        viewModelScope.launch {
            delay(1100)
            if (!_uiState.value.isGameOver) endGame()
        }
    }

    private fun endGame() {
        stop()
        val bonus = maxOf(0, config.parSeconds - _uiState.value.elapsed) * 3
        _uiState.update { it.copy(score = it.score + bonus, lastSpeedBonus = bonus, isGameOver = true) }
        val finalScore = _uiState.value.score
        if (finalScore > bestScore) prefManager.setCustomParamInt(bestScoreKey, finalScore)
        val time = _uiState.value.elapsed
        if (time > 0 && (bestTime == 0 || time < bestTime)) prefManager.setCustomParamInt(bestTimeKey, time)
        com.jigar.me.utils.WeeklySummaryManager.record(prefManager, 1)
    }
}
