package com.jigar.me.ui.view.home.screens.math_game_zone.number_path.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.number_path.components.NumberPathConfig
import com.jigar.me.ui.view.home.screens.math_game_zone.number_path.components.NumberPathLevel
import com.jigar.me.ui.view.home.screens.math_game_zone.number_path.components.NumberPathLevels
import com.jigar.me.ui.view.home.screens.math_game_zone.number_path.components.NumberPathProgress
import com.jigar.me.ui.view.home.screens.math_game_zone.number_path.components.NumberPathUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.number_path.components.PathCellType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import javax.inject.Inject

const val KEY_NUMBER_PATH_DIFF = "number_path_diff"
const val KEY_NUMBER_PATH_LEVEL = "number_path_level"

@HiltViewModel
class NumberPathPlayViewModel @Inject constructor(
    private val prefManager: AppPreferencesHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val difficulty: CommonDifficulty4 =
        savedStateHandle.get<String>(KEY_NUMBER_PATH_DIFF)?.let { CommonDifficulty4.fromName(it) }
            ?: CommonDifficulty4.easy
    val level: Int = (savedStateHandle.get<Int>(KEY_NUMBER_PATH_LEVEL) ?: 1)
        .coerceIn(1, NumberPathConfig.LEVEL_COUNT)

    private val progress = NumberPathProgress(prefManager)
    private lateinit var puzzle: NumberPathLevel

    private val _uiState = MutableStateFlow(NumberPathUiState())
    val uiState: StateFlow<NumberPathUiState> = _uiState

    private var timerJob: Job? = null

    // App in background - freeze the clock so interruptions never cost
    // stars or time. Set from the screen via lifecycle events.
    private var timerPaused = false
    fun setTimerPaused(paused: Boolean) { timerPaused = paused }

    // The kid's chosen companion walks the maze.
    val buddy: String
        get() = com.jigar.me.ui.view.home.screens.math_game_zone.common.ZoneBuddy.current(prefManager)

    val parSeconds: Int get() = NumberPathConfig.parSeconds(difficulty, puzzle.canonicalPath.size)
    val hasNextLevel: Boolean get() = level < NumberPathConfig.LEVEL_COUNT
    val goalIndex: Int get() = puzzle.goalIndex

    // Rotates through the level's variants so a replay shows a different maze.
    private val playsKey get() = "numberPathPlays_${difficulty.name}_$level"

    fun start() {
        stop()
        val variants = NumberPathLevels.forDifficulty(difficulty)[level - 1]
        val plays = prefManager.getCustomParamInt(playsKey, 0)
        puzzle = NumberPathLevel.parse(variants[plays % variants.size])
        prefManager.setCustomParamInt(playsKey, plays + 1)
        _uiState.value = NumberPathUiState(
            rows = puzzle.rows, cols = puzzle.cols, cells = puzzle.cells,
            startValue = puzzle.startValue, target = puzzle.target,
            trail = listOf(puzzle.startIndex),
            totals = listOf(puzzle.startValue),
            hintsLeft = NumberPathConfig.HINTS_PER_LEVEL
        )
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

    private fun adjacent(a: Int, b: Int): Boolean {
        val r1 = a / puzzle.cols; val c1 = a % puzzle.cols
        val r2 = b / puzzle.cols; val c2 = b % puzzle.cols
        return abs(r1 - r2) + abs(c1 - c2) == 1
    }

    private fun applyOp(total: Int, op: Char, operand: Int): Int? {
        val out = when (op) {
            '+' -> total + operand
            '-' -> total - operand
            '*' -> total * operand
            '/' -> if (operand != 0 && total % operand == 0) total / operand else return null
            else -> return null
        }
        return if (out in 1..999) out else null
    }

    fun tapCell(index: Int) {
        val s = _uiState.value
        if (s.justSolved || s.isGameOver) return
        val cell = s.cells.getOrNull(index) ?: return
        if (cell.type == PathCellType.WALL) return

        // Tapping the previous cell walks back one step (same as undo).
        if (s.trail.size > 1 && index == s.trail[s.trail.size - 2]) { undo(); return }

        if (index in s.trail) return
        if (!adjacent(s.currentCell, index)) return

        when (cell.type) {
            PathCellType.GOAL -> {
                if (s.currentTotal == s.target) {
                    AudioPlayerManager.playSoundSparkle()
                    // Freeze the clock now — the 1.1s celebration must not
                    // tick elapsed past a star threshold.
                    stop()
                    _uiState.update {
                        it.copy(trail = it.trail + index, totals = it.totals + it.currentTotal,
                            hintIndex = null, justSolved = true)
                    }
                    viewModelScope.launch {
                        delay(1100)
                        if (_uiState.value.isGameOver) return@launch
                        endGame()
                    }
                } else {
                    // Wrong total — the goal bounces the buddy back.
                    AudioPlayerManager.playSoundWrongSoft()
                    _uiState.update { it.copy(goalBounceCount = it.goalBounceCount + 1) }
                }
            }
            PathCellType.OP -> {
                val next = applyOp(s.currentTotal, cell.op!!, cell.operand!!)
                if (next == null) {
                    // Impossible step (e.g. ÷2 on an odd number) — tile refuses.
                    AudioPlayerManager.playSoundWrongSoft()
                    _uiState.update { it.copy(blockedBounceCount = it.blockedBounceCount + 1) }
                } else {
                    AudioPlayerManager.playSoundTilePlace()
                    _uiState.update {
                        it.copy(trail = it.trail + index, totals = it.totals + next, hintIndex = null)
                    }
                }
            }
            else -> {}
        }
    }

    fun undo() {
        val s = _uiState.value
        if (s.justSolved || s.isGameOver || s.trail.size <= 1) return
        AudioPlayerManager.playSoundTilePlace()
        _uiState.update {
            it.copy(trail = it.trail.dropLast(1), totals = it.totals.dropLast(1), hintIndex = null)
        }
    }

    // Walks the buddy back to the start; timer and hints keep running.
    fun restart() {
        val s = _uiState.value
        if (s.justSolved || s.isGameOver) return
        _uiState.update {
            it.copy(trail = listOf(puzzle.startIndex), totals = listOf(puzzle.startValue), hintIndex = null)
        }
    }

    // Pulses the next tile of the intended path. If the kid wandered off it,
    // the hint pulses the correct branch cell — showing where to return to.
    fun useHint() {
        val s = _uiState.value
        if (s.justSolved || s.isGameOver || s.hintsLeft <= 0) return
        val canonical = puzzle.canonicalPath
        var common = 0
        while (common < s.trail.size && common < canonical.size && s.trail[common] == canonical[common]) common++
        val target = if (common < canonical.size) canonical[common] else return
        AudioPlayerManager.playSoundSparkle()
        _uiState.update {
            it.copy(hintIndex = target, hintsLeft = it.hintsLeft - 1, hintsUsed = it.hintsUsed + 1)
        }
    }

    private fun endGame() {
        stop()
        val s = _uiState.value
        val stars = NumberPathConfig.stars(s.hintsUsed, s.elapsed, parSeconds)
        progress.setStars(difficulty, level, stars)
        com.jigar.me.utils.WeeklySummaryManager.record(prefManager, 1)
        _uiState.update { it.copy(isGameOver = true, starsEarned = stars) }
    }
}
