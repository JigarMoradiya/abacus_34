package com.jigar.me.ui.view.home.screens.math_game_zone.kakuro.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.kakuro.components.KakuroCellType
import com.jigar.me.ui.view.home.screens.math_game_zone.kakuro.components.KakuroConfig
import com.jigar.me.ui.view.home.screens.math_game_zone.kakuro.components.KakuroLevel
import com.jigar.me.ui.view.home.screens.math_game_zone.kakuro.components.KakuroLevels
import com.jigar.me.ui.view.home.screens.math_game_zone.kakuro.components.KakuroProgress
import com.jigar.me.ui.view.home.screens.math_game_zone.kakuro.components.KakuroRun
import com.jigar.me.ui.view.home.screens.math_game_zone.kakuro.components.KakuroUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

const val KEY_KAKURO_DIFF = "kakuro_diff"
const val KEY_KAKURO_LEVEL = "kakuro_level"

@HiltViewModel
class KakuroPlayViewModel @Inject constructor(
    private val prefManager: AppPreferencesHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val difficulty: CommonDifficulty4 =
        savedStateHandle.get<String>(KEY_KAKURO_DIFF)?.let { CommonDifficulty4.fromName(it) }
            ?: CommonDifficulty4.easy
    val level: Int = (savedStateHandle.get<Int>(KEY_KAKURO_LEVEL) ?: 1)
        .coerceIn(1, KakuroConfig.LEVEL_COUNT)

    private val progress = KakuroProgress(prefManager)
    private lateinit var puzzle: KakuroLevel

    private val _uiState = MutableStateFlow(KakuroUiState())
    val uiState: StateFlow<KakuroUiState> = _uiState

    private var timerJob: Job? = null

    // App in background - freeze the clock so interruptions never cost
    // stars or time. Set from the screen via lifecycle events.
    private var timerPaused = false
    fun setTimerPaused(paused: Boolean) { timerPaused = paused }

    val parSeconds: Int get() = KakuroConfig.parSeconds(difficulty, puzzle.blankCount)
    val hasNextLevel: Boolean get() = level < KakuroConfig.LEVEL_COUNT

    // Rotates through the level's variants so a replay shows different numbers.
    private val playsKey get() = "kakuroPlays_${difficulty.name}_$level"

    fun start() {
        stop()
        val variants = KakuroLevels.forDifficulty(difficulty)[level - 1]
        val plays = prefManager.getCustomParamInt(playsKey, 0)
        puzzle = KakuroLevel.parse(variants[plays % variants.size])
        prefManager.setCustomParamInt(playsKey, plays + 1)
        _uiState.value = KakuroUiState(
            rows = puzzle.rows, cols = puzzle.cols, cells = puzzle.cells,
            hintsLeft = KakuroConfig.HINTS_PER_LEVEL
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

    fun tapCell(index: Int) {
        val s = _uiState.value
        if (s.justSolved || s.isGameOver) return
        if (s.cells.getOrNull(index)?.type != KakuroCellType.BLANK) return
        if (index in s.hintLocked) return
        _uiState.update { it.copy(selectedCell = index) }
    }

    fun tapDigit(digit: Int) {
        val s = _uiState.value
        if (s.justSolved || s.isGameOver) return
        val target = s.selectedCell?.takeIf {
            s.cells[it].type == KakuroCellType.BLANK && it !in s.hintLocked
        } ?: firstEmptyBlank(s) ?: return
        AudioPlayerManager.playSoundTilePlace()
        _uiState.update {
            it.copy(
                placed = it.placed + (target to digit),
                selectedCell = target
            )
        }
        evaluateRuns()
        advanceSelectionIfCorrect(target)
        checkWin()
    }

    // Keeps the kid on a box that just completed an equation wrong.
    private fun advanceSelectionIfCorrect(target: Int) {
        _uiState.update {
            if (target in it.wrongCells) it.copy(selectedCell = target)
            else it.copy(selectedCell = nextEmptyBlank(it, after = target))
        }
    }

    // Eraser tool: clears the selected filled box.
    fun erase() {
        val s = _uiState.value
        if (s.justSolved || s.isGameOver) return
        val i = s.selectedCell ?: return
        if (i in s.hintLocked || s.placed[i] == null) return
        _uiState.update { it.copy(placed = it.placed - i) }
        evaluateRuns()
    }

    val canErase: Boolean
        get() {
            val s = _uiState.value
            val i = s.selectedCell ?: return false
            return s.placed[i] != null && i !in s.hintLocked
        }

    // Clears the kid's digits; hint-filled cells, hints and the timer stay.
    fun restart() {
        val s = _uiState.value
        if (s.justSolved || s.isGameOver) return
        _uiState.update {
            it.copy(
                placed = it.placed.filterKeys { k -> k in it.hintLocked },
                selectedCell = null,
                wrongCells = emptySet(),
                solvedCells = emptySet()
            )
        }
        evaluateRuns()
    }

    // Fills one correct cell with the canonical digit and locks it.
    fun useHint() {
        val s = _uiState.value
        if (s.justSolved || s.isGameOver || s.hintsLeft <= 0) return
        // Prefer an empty blank; else fix a wrong one.
        val target = s.cells.indices.firstOrNull {
            s.cells[it].type == KakuroCellType.BLANK && s.placed[it] == null
        } ?: s.cells.indices.firstOrNull {
            s.cells[it].type == KakuroCellType.BLANK && it !in s.hintLocked &&
                s.placed[it] != s.cells[it].solution
        } ?: return
        val solution = s.cells[target].solution ?: return
        AudioPlayerManager.playSoundSparkle()
        _uiState.update {
            it.copy(
                placed = it.placed + (target to solution),
                hintLocked = it.hintLocked + target,
                hintsLeft = it.hintsLeft - 1,
                hintsUsed = it.hintsUsed + 1,
                selectedCell = null
            )
        }
        evaluateRuns()
        checkWin()
    }

    // ── evaluation ──────────────────────────────────────────────────────

    private fun runComplete(s: KakuroUiState, run: KakuroRun): Boolean =
        run.cells.all { s.placed[it] != null }

    private fun runSatisfied(s: KakuroUiState, run: KakuroRun): Boolean {
        val digits = run.cells.map { s.placed[it] ?: return false }
        return digits.sum() == run.sum && digits.toSet().size == digits.size
    }

    private fun evaluateRuns() {
        val s = _uiState.value
        val wrong = mutableSetOf<Int>()
        val solved = mutableSetOf<Int>()
        for (run in puzzle.runs) {
            if (!runComplete(s, run)) continue
            if (runSatisfied(s, run)) solved.addAll(run.cells) else wrong.addAll(run.cells)
        }
        val newWrong = wrong.isNotEmpty() && wrong != s.wrongCells
        val newSolved = solved.isNotEmpty() && solved != s.solvedCells && wrong.isEmpty()
        if (newWrong) AudioPlayerManager.playSoundWrongSoft()
        else if (newSolved) AudioPlayerManager.playSoundSparkle()
        _uiState.update { it.copy(wrongCells = wrong, solvedCells = solved) }
    }

    private fun checkWin() {
        val s = _uiState.value
        if (puzzle.runs.any { !runSatisfied(s, it) }) return
        // Freeze the clock now — the 1.1s celebration must not tick elapsed
        // past a star threshold.
        stop()
        _uiState.update { it.copy(justSolved = true, selectedCell = null) }
        viewModelScope.launch {
            delay(1100)
            if (_uiState.value.isGameOver) return@launch
            endGame()
        }
    }

    private fun endGame() {
        stop()
        val s = _uiState.value
        val stars = KakuroConfig.stars(s.hintsUsed, s.elapsed, parSeconds)
        progress.setStars(difficulty, level, stars)
        _uiState.update { it.copy(isGameOver = true, starsEarned = stars) }
    }

    // ── helpers ─────────────────────────────────────────────────────────

    private fun firstEmptyBlank(s: KakuroUiState): Int? =
        s.cells.indices.firstOrNull {
            s.cells[it].type == KakuroCellType.BLANK && s.placed[it] == null && it !in s.hintLocked
        }

    private fun nextEmptyBlank(s: KakuroUiState, after: Int): Int? {
        val empties = s.cells.indices.filter {
            it != after && s.cells[it].type == KakuroCellType.BLANK &&
                s.placed[it] == null && it !in s.hintLocked
        }
        return empties.firstOrNull { it > after } ?: empties.firstOrNull()
    }
}
