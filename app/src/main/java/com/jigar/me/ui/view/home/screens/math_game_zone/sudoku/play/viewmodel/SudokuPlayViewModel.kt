package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.generator.SudokuBoxRules
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.generator.SudokuSolver
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.generator.SudokuStorage
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SudokuPlayViewModel @Inject constructor(
    private val app: Application,
    private val repository: SudokuRepository,
    savedStateHandle: SavedStateHandle,
    private val prefManager: com.jigar.me.data.pref.AppPreferencesHelper
) : ViewModel() {

    // Read args via SavedStateHandle
    val size: SudokuSize =
        SudokuSize.valueOf(savedStateHandle["size"] ?: SudokuSize.SIX.name)

    val difficulty: SudokuDifficulty4 =
        SudokuDifficulty4.valueOf(savedStateHandle["difficulty"] ?: SudokuDifficulty4.EASY.name)

    private val isNewPuzzle: Boolean =
        savedStateHandle["isNewPuzzle"] ?: true

    // Loading state (very important!)
    var isLoading by mutableStateOf(false)
        private set

    // Puzzle state
    var puzzle by mutableStateOf(SudokuPuzzle(size, emptyList(), emptyList(), difficulty))
        private set

    // Safe empty board (prevents crash)
    var board by mutableStateOf(
        MutableList(size.grid) { MutableList(size.grid) { 0 } }
    )

    var selected: Pair<Int, Int>? by mutableStateOf(null)
    var message: String? by mutableStateOf(null)
    var isSolved by mutableStateOf(false)

    var hintUsed by mutableIntStateOf(0)
    var hintLimit by mutableIntStateOf(1)
    var showCandidates by mutableStateOf(false)

    init {
        loadGame()
    }

    private fun loadGame() {
        // Try load saved game
        val saved = if (!isNewPuzzle) SudokuStorage.load(app) else null

        if (saved != null) {
            applySavedGame(saved)
            return
        }

        // Else generate new puzzle
        loadNewPuzzle(size, difficulty)
    }

    fun loadNewPuzzle(size: SudokuSize, diff: SudokuDifficulty4) {

        isLoading = true

        viewModelScope.launch(Dispatchers.Default) {

            // Repository handles puzzle generation
            val newPuzzle = repository.generatePuzzle(size, diff)

            withContext(Dispatchers.Main) {
                puzzle = newPuzzle
                board = newPuzzle.startBoard.map { it.toMutableList() }.toMutableList()
                selected = null
                message = null
                isSolved = false
                hintUsed = 0
                hintLimit = defaultHintLimit(size, diff)
                showCandidates = false
                isLoading = false

                saveProgress()
            }
        }
    }


    private fun applySavedGame(saved: SavedSudokuGame) {
        puzzle = saved.puzzle
        board = saved.board.map { it.toMutableList() }.toMutableList()
        hintUsed = saved.hintUsed
        hintLimit = saved.hintLimit
        isSolved = saved.isSolved

        selected = saved.selectedR?.let { r ->
            saved.selectedC?.let { c -> r to c }
        }
    }

    private fun defaultHintLimit(size: SudokuSize, diff: SudokuDifficulty4) = when (size) {
        SudokuSize.FOUR -> 1
        SudokuSize.SIX -> when (diff) {
            SudokuDifficulty4.EASY -> 1
            SudokuDifficulty4.MEDIUM -> 1
            SudokuDifficulty4.HARD -> 2
            SudokuDifficulty4.VERY_HARD -> 2
        }
        SudokuSize.NINE -> when (diff) {
            SudokuDifficulty4.EASY -> 1
            SudokuDifficulty4.MEDIUM -> 2
            SudokuDifficulty4.HARD -> 3
            SudokuDifficulty4.VERY_HARD -> 4
        }
    }

    // GAME LOGIC (unchanged)
    fun selectCell(r: Int, c: Int) {
        if (isSolved) return
        selected = if (puzzle.startBoard[r][c] == 0) {
            if (selected == r to c) null else r to c
        } else null
        message = null
    }

    fun enter(number: Int) {

        val sel = selected ?: return
        if (isSolved) return

        val (r, c) = sel
        if (puzzle.startBoard[r][c] != 0) return

        // ✅ ALWAYS SET VALUE
        board = board.map { it.toMutableList() }
            .toMutableList()
            .also { it[r][c] = number }

//        val valid = SudokuSolver.isValid(board, puzzle.size, r, c, number)

        AudioPlayerManager.playSoundTilePlace()
        saveProgress()
        checkSolved()
    }

    fun eraseSelected() {
        val sel = selected ?: return
        val (r, c) = sel
        if (puzzle.startBoard[r][c] == 0) {
            board = board.map { it.toMutableList() }.toMutableList().also { it[r][c] = 0 }
            AudioPlayerManager.playSoundBtnBack()
            saveProgress()
        }
    }

    fun toggleCandidates() {
        if (selected == null) {
            AudioPlayerManager.playSoundWrongSoft()
            message = "Select a cell first"
            return
        }
        showCandidates = !showCandidates
    }

    fun candidatesForSelected(): List<Int> {
        val s = selected ?: return emptyList()
        return SudokuSolver.candidatesFor(board, puzzle.size, s.first, s.second)
    }

    fun revealOneNumber() {
        val s = selected ?: run {
            AudioPlayerManager.playSoundWrongSoft()
            message = "Select a cell first"
            return
        }
        if (hintUsed >= hintLimit) {
            AudioPlayerManager.playSoundWrongSoft()
            message = "No more hints available"
            return
        }
        if (board[s.first][s.second] == 0) {
            board = board.map { it.toMutableList() }.toMutableList()
                .also { it[s.first][s.second] = puzzle.solution[s.first][s.second] }
            hintUsed++
            saveProgress()
            checkSolved()
        }
    }

    fun resetPuzzle() {
        board = puzzle.startBoard.map { it.toMutableList() }.toMutableList()
        selected = null
        message = null
        isSolved = false
        hintUsed = 0
        saveProgress()
    }

    private fun checkSolved() {

        val n = puzzle.size.grid

        if (board.any { row -> row.any { it == 0 } }) return

        for (r in 0 until n) {
            for (c in 0 until n) {

                val value = board[r][c]

                if (!isValidIgnoringSelf(r, c, value)) {
                    message = "Please check all numbers, something is wrong 🤔"
                    return
                }
            }
        }

        isSolved = true
        SudokuStorage.clear(app)
        AudioPlayerManager.playSoundWin()
        com.jigar.me.ui.view.home.screens.math_game_zone.common.ZoneSolveCounter.increment(
            prefManager, com.jigar.me.ui.view.home.screens.math_game_zone.common.ZoneSolveCounter.SUDOKU
        )
    }

    private fun isValidIgnoringSelf(row: Int, col: Int, num: Int): Boolean {

        val n = puzzle.size.grid

        for (i in 0 until n) {
            if (i != col && board[row][i] == num) return false
            if (i != row && board[i][col] == num) return false
        }

        val (br, bc) = SudokuBoxRules.boxSize(puzzle.size)
        val sr = (row / br) * br
        val sc = (col / bc) * bc

        for (r in 0 until br) {
            for (c in 0 until bc) {
                val rr = sr + r
                val cc = sc + c
                if (!(rr == row && cc == col) && board[rr][cc] == num) return false
            }
        }

        return true
    }

    fun saveProgress() {
        val save = SavedSudokuGame(
            puzzle = puzzle,
            board = board.map { it.toList() },
            hintUsed = hintUsed,
            hintLimit = hintLimit,
            isSolved = isSolved,
            selectedR = selected?.first,
            selectedC = selected?.second,
            savedAt = System.currentTimeMillis()
        )
        SudokuStorage.save(app, save)
    }
}