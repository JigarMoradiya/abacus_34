package com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku.components.Cage
import com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku.components.CageOp
import com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku.components.CalcudokuConfig
import com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku.components.CalcudokuGenerator
import com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku.components.CalcudokuUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import javax.inject.Inject

const val KEY_CALCUDOKU_DIFF = "calcudoku_diff"

@HiltViewModel
class CalcudokuPlayViewModel @Inject constructor(
    private val prefManager: AppPreferencesHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val difficulty: CommonDifficulty4 =
        savedStateHandle.get<String>(KEY_CALCUDOKU_DIFF)?.let { CommonDifficulty4.fromName(it) }
            ?: CommonDifficulty4.easy
    val config: CalcudokuConfig = CalcudokuConfig.forDifficulty(difficulty)

    private val _uiState = MutableStateFlow(
        CalcudokuUiState(
            size = config.size,
            grid = List(config.size * config.size) { 0 },
            fixed = List(config.size * config.size) { false },
            cageId = List(config.size * config.size) { 0 }
        )
    )
    val uiState: StateFlow<CalcudokuUiState> = _uiState

    private var timerJob: Job? = null

    val multiplier: Int
        get() = when (_uiState.value.streak) { in 0..2 -> 1; in 3..5 -> 2; else -> 3 }

    private val bestScoreKey get() = "calcudokuBest_${difficulty.name}"
    val bestScore: Int get() = prefManager.getCustomParamInt(bestScoreKey, 0)

    // Fastest full-session completion, in seconds (0 = no record yet).
    private val bestTimeKey get() = "calcudokuBestTime_${difficulty.name}"
    val bestTime: Int get() = prefManager.getCustomParamInt(bestTimeKey, 0)

    fun start() {
        stop()
        _uiState.value = CalcudokuUiState(
            size = config.size,
            grid = List(config.size * config.size) { 0 },
            fixed = List(config.size * config.size) { false },
            cageId = List(config.size * config.size) { 0 }
        )
        loadPuzzle()
        timerJob = viewModelScope.launch {
            while (isActive && !_uiState.value.isGameOver) {
                delay(1000)
                _uiState.update { it.copy(elapsed = it.elapsed + 1) }   // count up; best time is the record
            }
        }
    }

    fun stop() { timerJob?.cancel(); timerJob = null }
    override fun onCleared() { stop() }

    private fun loadPuzzle() {
        val p = CalcudokuGenerator.make(config)
        val n = p.size
        val fixed = BooleanArray(n * n)
        val grid = IntArray(n * n)
        for (cage in p.cages) if (cage.op == CageOp.GIVEN) {
            val i = cage.cells[0]
            grid[i] = p.solution[i]
            fixed[i] = true
        }
        _uiState.update {
            it.copy(
                size = n, cages = p.cages, cageId = p.cageId,
                grid = grid.toList(), fixed = fixed.toList(),
                selectedIndex = null, justSolved = false
            )
        }
    }

    fun tapCell(index: Int) {
        val s = _uiState.value
        if (s.fixed[index] || s.justSolved) return
        _uiState.update { it.copy(selectedIndex = index) }
    }

    fun placeNumber(n: Int) {
        val s = _uiState.value
        if (s.justSolved) return
        val i = s.selectedIndex ?: firstEmptyEditable(s) ?: return
        if (s.fixed[i]) return
        val newGrid = s.grid.toMutableList().also { it[i] = n }
        AudioPlayerManager.playSoundTilePlace()
        _uiState.update { it.copy(grid = newGrid) }
        checkWin()
    }

    fun erase() {
        val s = _uiState.value
        val i = s.selectedIndex ?: return
        if (s.justSolved || s.fixed[i]) return
        val newGrid = s.grid.toMutableList().also { it[i] = 0 }
        _uiState.update { it.copy(grid = newGrid) }
    }

    // Clear every cell the kid filled in (keep the given clue cells).
    fun reset() {
        val s = _uiState.value
        if (s.justSolved) return
        val newGrid = s.grid.mapIndexed { i, v -> if (s.fixed[i]) v else 0 }
        _uiState.update { it.copy(grid = newGrid, selectedIndex = null) }
    }

    private fun firstEmptyEditable(s: CalcudokuUiState): Int? =
        s.grid.indices.firstOrNull { s.grid[it] == 0 && !s.fixed[it] }

    private fun cageSatisfied(cage: Cage, grid: List<Int>): Boolean {
        val vals = cage.cells.map { grid[it] }
        if (vals.contains(0)) return false
        return when (cage.op) {
            CageOp.GIVEN -> vals[0] == cage.target
            CageOp.PLUS -> vals.sum() == cage.target
            CageOp.TIMES -> vals.reduce { a, b -> a * b } == cage.target
            CageOp.MINUS -> abs(vals[0] - vals[1]) == cage.target
            CageOp.DIVIDE -> {
                val hi = max(vals[0], vals[1]); val lo = min(vals[0], vals[1])
                lo != 0 && hi % lo == 0 && hi / lo == cage.target
            }
        }
    }

    private fun latinValid(grid: List<Int>, size: Int): Boolean {
        for (r in 0 until size) {
            val seen = BooleanArray(size + 1)
            for (c in 0 until size) {
                val v = grid[r * size + c]
                if (v < 1 || v > size || seen[v]) return false
                seen[v] = true
            }
        }
        for (c in 0 until size) {
            val seen = BooleanArray(size + 1)
            for (r in 0 until size) {
                val v = grid[r * size + c]
                if (v < 1 || v > size || seen[v]) return false
                seen[v] = true
            }
        }
        return true
    }

    private fun checkWin() {
        val s = _uiState.value
        if (s.grid.contains(0) || !latinValid(s.grid, s.size)) return
        if (s.cages.any { !cageSatisfied(it, s.grid) }) return

        AudioPlayerManager.playSoundSparkle()
        _uiState.update {
            it.copy(score = it.score + 60, puzzlesSolved = it.puzzlesSolved + 1, justSolved = true)
        }
        // One puzzle per session — end after solving it.
        viewModelScope.launch {
            delay(1100)
            if (_uiState.value.isGameOver) return@launch
            endGame()
        }
    }

    private fun endGame() {
        stop()
        // Reward a fast solve, and record the best (fastest) time.
        val bonus = maxOf(0, config.parSeconds - _uiState.value.elapsed) * 3
        _uiState.update { it.copy(score = it.score + bonus, lastSpeedBonus = bonus, isGameOver = true) }
        val finalScore = _uiState.value.score
        if (finalScore > bestScore) prefManager.setCustomParamInt(bestScoreKey, finalScore)
        val time = _uiState.value.elapsed
        if (time > 0 && (bestTime == 0 || time < bestTime)) prefManager.setCustomParamInt(bestTimeKey, time)
        com.jigar.me.utils.WeeklySummaryManager.record(prefManager, 1)
    }
}
