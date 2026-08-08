package com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.components.CrossCellType
import com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.components.CrossMathConfig
import com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.components.CrossMathLevel
import com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.components.CrossMathLevels
import com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.components.CrossMathProgress
import com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.components.CrossMathUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.components.CrossMove
import com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.components.CrossRun
import com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.components.TrayTile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

const val KEY_CROSS_MATH_DIFF = "cross_math_diff"
const val KEY_CROSS_MATH_LEVEL = "cross_math_level"

@HiltViewModel
class CrossMathPlayViewModel @Inject constructor(
    private val prefManager: AppPreferencesHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val difficulty: CommonDifficulty4 =
        savedStateHandle.get<String>(KEY_CROSS_MATH_DIFF)?.let { CommonDifficulty4.fromName(it) }
            ?: CommonDifficulty4.easy
    val level: Int = (savedStateHandle.get<Int>(KEY_CROSS_MATH_LEVEL) ?: 1)
        .coerceIn(1, CrossMathConfig.LEVEL_COUNT)

    private val progress = CrossMathProgress(prefManager)
    private lateinit var puzzle: CrossMathLevel

    private val _uiState = MutableStateFlow(CrossMathUiState())
    val uiState: StateFlow<CrossMathUiState> = _uiState

    private var timerJob: Job? = null

    val parSeconds: Int get() = CrossMathConfig.parSeconds(difficulty, puzzle.blankCount)
    val hasNextLevel: Boolean get() = level < CrossMathConfig.LEVEL_COUNT
    val hasOpBlanks: Boolean
        get() = _uiState.value.cells.any { it.type == CrossCellType.BLANK_OP }

    // Rotates through the level's variants so a replay shows different numbers.
    private val playsKey get() = "crossMathPlays_${difficulty.name}_$level"

    fun start() {
        stop()
        val variants = CrossMathLevels.forDifficulty(difficulty)[level - 1]
        val plays = prefManager.getCustomParamInt(playsKey, 0)
        puzzle = CrossMathLevel.parse(variants[plays % variants.size])
        prefManager.setCustomParamInt(playsKey, plays + 1)
        _uiState.value = CrossMathUiState(
            rows = puzzle.rows, cols = puzzle.cols, cells = puzzle.cells,
            tray = puzzle.tray.mapIndexed { i, v -> TrayTile(id = i, value = v) },
            hintsLeft = CrossMathConfig.HINTS_PER_LEVEL
        )
        timerJob = viewModelScope.launch {
            while (isActive && !_uiState.value.isGameOver) {
                delay(1000)
                _uiState.update { it.copy(elapsed = it.elapsed + 1) }
            }
        }
    }

    fun stop() { timerJob?.cancel(); timerJob = null }
    override fun onCleared() { stop() }

    private fun isFillable(type: CrossCellType) =
        type == CrossCellType.BLANK || type == CrossCellType.BLANK_OP

    fun tapCell(index: Int) {
        val s = _uiState.value
        if (s.justSolved || s.isGameOver) return
        val type = s.cells.getOrNull(index)?.type ?: return
        if (!isFillable(type)) return
        if (index in s.hintLocked) return
        when {
            type == CrossCellType.BLANK && s.placed[index] != null ->
                removeNumber(index, s.placed.getValue(index))
            type == CrossCellType.BLANK_OP && s.placedOps[index] != null ->
                removeOp(index, s.placedOps.getValue(index))
            else -> _uiState.update { it.copy(selectedCell = index) }
        }
    }

    fun tapTray(id: Int) {
        val s = _uiState.value
        if (s.justSolved || s.isGameOver) return
        val tile = s.tray.firstOrNull { it.id == id } ?: return
        if (tile.used) return
        val target = s.selectedCell?.takeIf {
            s.cells[it].type == CrossCellType.BLANK && s.placed[it] == null && it !in s.hintLocked
        } ?: firstEmpty(s, CrossCellType.BLANK) ?: return
        AudioPlayerManager.playSoundTilePlace()
        _uiState.update {
            it.copy(
                placed = it.placed + (target to id),
                tray = it.tray.map { t -> if (t.id == id) t.copy(used = true) else t },
                selectedCell = target,
                undoStack = it.undoStack + CrossMove(target, id, isPlace = true)
            )
        }
        evaluateRuns()
        advanceSelectionIfCorrect(target)
        checkWin()
    }

    // Sign palette: place one of + - * / into the selected (or first empty) sign box.
    fun tapSign(op: Char) {
        val s = _uiState.value
        if (s.justSolved || s.isGameOver) return
        val target = s.selectedCell?.takeIf {
            s.cells[it].type == CrossCellType.BLANK_OP && s.placedOps[it] == null && it !in s.hintLocked
        } ?: firstEmpty(s, CrossCellType.BLANK_OP) ?: return
        AudioPlayerManager.playSoundTilePlace()
        _uiState.update {
            it.copy(
                placedOps = it.placedOps + (target to op),
                selectedCell = target,
                undoStack = it.undoStack + CrossMove(target, -1, isPlace = true, op = op)
            )
        }
        evaluateRuns()
        advanceSelectionIfCorrect(target)
        checkWin()
    }

    // After a placement: if it completed an equation WRONG, keep the kid on
    // that box so the mistake is easy to fix; otherwise move to the next one.
    private fun advanceSelectionIfCorrect(target: Int) {
        _uiState.update {
            if (target in it.wrongCells) it.copy(selectedCell = target)
            else it.copy(selectedCell = nextEmpty(it, after = target))
        }
    }

    // Eraser tool: clears the selected filled box (number or sign).
    fun erase() {
        val s = _uiState.value
        if (s.justSolved || s.isGameOver) return
        val i = s.selectedCell ?: return
        if (i in s.hintLocked) return
        when {
            s.placed[i] != null -> removeNumber(i, s.placed.getValue(i))
            s.placedOps[i] != null -> removeOp(i, s.placedOps.getValue(i))
        }
    }

    private fun removeNumber(index: Int, trayId: Int) {
        _uiState.update {
            it.copy(
                placed = it.placed - index,
                tray = it.tray.map { t -> if (t.id == trayId) t.copy(used = false) else t },
                selectedCell = index,
                undoStack = it.undoStack + CrossMove(index, trayId, isPlace = false)
            )
        }
        evaluateRuns()
    }

    private fun removeOp(index: Int, op: Char) {
        _uiState.update {
            it.copy(
                placedOps = it.placedOps - index,
                selectedCell = index,
                undoStack = it.undoStack + CrossMove(index, -1, isPlace = false, op = op)
            )
        }
        evaluateRuns()
    }

    fun undo() {
        val s = _uiState.value
        if (s.justSolved || s.isGameOver) return
        val move = s.undoStack.lastOrNull() ?: return
        _uiState.update {
            val stack = it.undoStack.dropLast(1)
            if (move.op != null) {
                if (move.isPlace) it.copy(placedOps = it.placedOps - move.cellIndex, selectedCell = move.cellIndex, undoStack = stack)
                else it.copy(placedOps = it.placedOps + (move.cellIndex to move.op), undoStack = stack)
            } else if (move.isPlace) {
                it.copy(
                    placed = it.placed - move.cellIndex,
                    tray = it.tray.map { t -> if (t.id == move.trayId) t.copy(used = false) else t },
                    selectedCell = move.cellIndex,
                    undoStack = stack
                )
            } else {
                it.copy(
                    placed = it.placed + (move.cellIndex to move.trayId),
                    tray = it.tray.map { t -> if (t.id == move.trayId) t.copy(used = true) else t },
                    undoStack = stack
                )
            }
        }
        evaluateRuns()
    }

    // Clears the kid's placements; hint-filled cells, hints and the timer stay.
    fun restart() {
        val s = _uiState.value
        if (s.justSolved || s.isGameOver) return
        val keepIds = s.placed.filterKeys { it in s.hintLocked }.values.toSet()
        _uiState.update {
            it.copy(
                placed = it.placed.filterKeys { k -> k in it.hintLocked },
                placedOps = it.placedOps.filterKeys { k -> k in it.hintLocked },
                tray = it.tray.map { t -> t.copy(used = t.id in keepIds) },
                selectedCell = null,
                wrongCells = emptySet(),
                solvedCells = emptySet(),
                undoStack = emptyList()
            )
        }
        evaluateRuns()
    }

    // Fills one correct cell (number first, then signs). Works even when the
    // kid has placed tray tiles in "wrong" spots: if no empty blank can take
    // its canonical value from the tray, we swap a misplaced tile back first.
    fun useHint() {
        val s = _uiState.value
        if (s.justSolved || s.isGameOver || s.hintsLeft <= 0) return

        val emptyBlanks = indexesOf(s, CrossCellType.BLANK).filter { s.placed[it] == null }
        val freeTiles = s.tray.filter { !it.used }

        var target: Int? = null
        var tile: TrayTile? = null
        for (i in emptyBlanks) {
            val sol = s.cells[i].solution ?: continue
            val t = freeTiles.firstOrNull { it.value == sol }
            if (t != null) { target = i; tile = t; break }
        }

        var working = s
        if (target == null) {
            // Maybe an empty SIGN box can be hinted instead.
            val emptyOp = indexesOf(s, CrossCellType.BLANK_OP).firstOrNull { s.placedOps[it] == null }
            if (emptyOp != null) {
                val sol = s.cells[emptyOp].solutionOp ?: return
                AudioPlayerManager.playSoundSparkle()
                _uiState.value = s.copy(
                    placedOps = s.placedOps + (emptyOp to sol),
                    hintLocked = s.hintLocked + emptyOp,
                    hintsLeft = s.hintsLeft - 1,
                    hintsUsed = s.hintsUsed + 1,
                    selectedCell = null,
                    undoStack = emptyList()
                )
                evaluateRuns()
                checkWin()
                return
            }
            // Every empty blank's value is stuck inside a misplaced cell.
            val misplaced = indexesOf(s, CrossCellType.BLANK).firstOrNull { i ->
                val trayId = s.placed[i] ?: return@firstOrNull false
                i !in s.hintLocked && s.tray.first { it.id == trayId }.value != s.cells[i].solution
            } ?: return
            val freedId = s.placed[misplaced]!!
            working = s.copy(
                placed = s.placed - misplaced,
                tray = s.tray.map { t -> if (t.id == freedId) t.copy(used = false) else t }
            )
            target = misplaced
            tile = working.tray.firstOrNull { !it.used && it.value == working.cells[misplaced].solution }
                ?: return
        }

        val cellIndex = target
        val trayTile = tile ?: return
        AudioPlayerManager.playSoundSparkle()
        _uiState.value = working.copy(
            placed = working.placed + (cellIndex to trayTile.id),
            tray = working.tray.map { t -> if (t.id == trayTile.id) t.copy(used = true) else t },
            hintLocked = working.hintLocked + cellIndex,
            hintsLeft = working.hintsLeft - 1,
            hintsUsed = working.hintsUsed + 1,
            selectedCell = null,
            // Hints are deliberate moves — they clear the undo history so undo
            // can never pull a hint-locked tile back out via an older move.
            undoStack = emptyList()
        )
        evaluateRuns()
        checkWin()
    }

    // ── evaluation ──────────────────────────────────────────────────────

    private fun valueAt(s: CrossMathUiState, index: Int): Int? {
        val cell = s.cells[index]
        return when (cell.type) {
            CrossCellType.GIVEN_NUM -> cell.given
            CrossCellType.BLANK -> s.placed[index]?.let { id -> s.tray.first { it.id == id }.value }
            else -> null
        }
    }

    private fun opAt(s: CrossMathUiState, index: Int): Char? {
        val cell = s.cells[index]
        return when (cell.type) {
            CrossCellType.OP -> cell.symbol
            CrossCellType.BLANK_OP -> s.placedOps[index]
            else -> null
        }
    }

    private fun runComplete(s: CrossMathUiState, run: CrossRun): Boolean =
        valueAt(s, run.cells[0]) != null && opAt(s, run.cells[1]) != null &&
            valueAt(s, run.cells[2]) != null && valueAt(s, run.cells[4]) != null

    private fun runSatisfied(s: CrossMathUiState, run: CrossRun): Boolean {
        val a = valueAt(s, run.cells[0]) ?: return false
        val op = opAt(s, run.cells[1]) ?: return false
        val b = valueAt(s, run.cells[2]) ?: return false
        val c = valueAt(s, run.cells[4]) ?: return false
        return when (op) {
            '+' -> a + b == c
            '-' -> a - b == c
            '*' -> a * b == c
            '/' -> b != 0 && a % b == 0 && a / b == c
            else -> false
        }
    }

    private fun evaluateRuns() {
        val s = _uiState.value
        val wrong = mutableSetOf<Int>()
        val solved = mutableSetOf<Int>()
        for (run in puzzle.runs) {
            // Fully prefilled equations are correct from the start — they
            // stay neutral; only lines the player completes get highlighted.
            if (run.cells.none { isFillable(s.cells[it].type) }) continue
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
        val stars = CrossMathConfig.stars(s.hintsUsed, s.elapsed, parSeconds)
        progress.setStars(difficulty, level, stars)
        _uiState.update { it.copy(isGameOver = true, starsEarned = stars) }
    }

    // ── helpers ─────────────────────────────────────────────────────────

    private fun indexesOf(s: CrossMathUiState, type: CrossCellType): List<Int> =
        s.cells.indices.filter { s.cells[it].type == type }

    private fun isEmptyFillable(s: CrossMathUiState, i: Int): Boolean = when (s.cells[i].type) {
        CrossCellType.BLANK -> s.placed[i] == null && i !in s.hintLocked
        CrossCellType.BLANK_OP -> s.placedOps[i] == null && i !in s.hintLocked
        else -> false
    }

    private fun firstEmpty(s: CrossMathUiState, type: CrossCellType): Int? =
        indexesOf(s, type).firstOrNull { isEmptyFillable(s, it) }

    private fun nextEmpty(s: CrossMathUiState, after: Int): Int? {
        val empties = s.cells.indices.filter { it != after && isEmptyFillable(s, it) }
        return empties.firstOrNull { it > after } ?: empties.firstOrNull()
    }
}
