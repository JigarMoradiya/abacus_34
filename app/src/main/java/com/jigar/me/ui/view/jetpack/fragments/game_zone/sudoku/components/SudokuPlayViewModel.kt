package com.jigar.me.ui.view.jetpack.fragments.game_zone.sudoku.components

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SudokuPlayViewModel(initialPuzzle: SudokuPuzzle? = null, private val context: Context? = null) : ViewModel() {
    var puzzle by mutableStateOf(initialPuzzle ?: SudokuGenerator.generatePuzzle(SudokuSize.SIX, SudokuDifficulty4.EASY))
        private set

    var board by mutableStateOf(puzzle.startBoard.map { it.toMutableList() }.toMutableList())
        private set

    var selected: Pair<Int, Int>? by mutableStateOf(null)
    var message by mutableStateOf<String?>(null)
    var isSolved by mutableStateOf(false)
    var hintUsed by mutableIntStateOf(0)
    var hintLimit by mutableIntStateOf( defaultHintLimit(puzzle.size,puzzle.difficulty) )
    var showCandidates by mutableStateOf(false)

    init {
        // If a saved game exists and no initialPuzzle provided, load it
        if (initialPuzzle == null && context != null) {
            val saved = SudokuStorage.load(context)
            if (saved != null) {
                puzzle = saved.puzzle
                board = saved.board.map { it.toMutableList() }.toMutableList()
                hintUsed = saved.hintUsed
                hintLimit = saved.hintLimit
                isSolved = saved.isSolved
                if (saved.selectedR != null && saved.selectedC != null) selected = saved.selectedR to saved.selectedC
            }
        }
    }

    companion object Companion {
        private fun defaultHintLimit(size: SudokuSize, diff: SudokuDifficulty4): Int {
            return when (size) {

                SudokuSize.FOUR -> 1

                SudokuSize.SIX -> when (diff) {
                    SudokuDifficulty4.EASY      -> 1
                    SudokuDifficulty4.MEDIUM    -> 1
                    SudokuDifficulty4.HARD      -> 2
                    SudokuDifficulty4.VERY_HARD -> 2
                }

                SudokuSize.NINE -> when (diff) {
                    SudokuDifficulty4.EASY      -> 1
                    SudokuDifficulty4.MEDIUM    -> 2
                    SudokuDifficulty4.HARD      -> 3
                    SudokuDifficulty4.VERY_HARD -> 4
                }
            }
        }

    }

    fun selectCell(r: Int, c: Int) {
        if (isSolved) return
        if (puzzle.startBoard[r][c] != 0) {
            selected = null
        } else {
            selected = if (selected == r to c) null else r to c
            message = null
        }
    }

    fun enter(number: Int) {
        val sel = selected ?: return
        if (isSolved) return
        val (r, c) = sel
        if (puzzle.startBoard[r][c] != 0) return

        if (SudokuSolver.isValid(board.map { it.toList() }, puzzle.size, r, c, number)) {
            val newBoard = board.map { it.toMutableList() }.toMutableList()
            newBoard[r][c] = number
            board = newBoard
            saveProgress()
            message = null
            checkSolved()
        } else {
            val newBoard = board.map { it.toMutableList() }.toMutableList()
            newBoard[r][c] = number
            board = newBoard
            saveProgress()
            message = null // keep silent like Swift version; UI shows conflicts
        }
    }

    fun eraseSelected() {
        val sel = selected ?: return
        val (r, c) = sel
        if (puzzle.startBoard[r][c] == 0) {
            val newBoard = board.map { it.toMutableList() }.toMutableList()
            newBoard[r][c] = 0
            board = newBoard
            saveProgress()
        }
    }

    fun toggleCandidates() {
        if (selected == null) { message = "Select a cell first"; return }
        showCandidates = !showCandidates
    }

    fun candidatesForSelected(): List<Int> {
        val s = selected ?: return emptyList()
        return SudokuSolver.candidatesFor(board.map { it.toList() }, puzzle.size, s.first, s.second)
    }

    fun revealOneNumber() {
        val s = selected ?: run { message = "Select a cell first"; return }
        if (hintUsed >= hintLimit) { message = "No more hints available"; return }
        if (board[s.first][s.second] == 0) {
            val newBoard = board.map { it.toMutableList() }.toMutableList()
            newBoard[s.first][s.second] = puzzle.solution[s.first][s.second]
            board = newBoard

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

    fun newPuzzle(size: SudokuSize, diff: SudokuDifficulty4) {
        viewModelScope.launch(Dispatchers.Default) {
            val newP = SudokuGenerator.generatePuzzle(size, diff)
            launch(Dispatchers.Main) {
                puzzle = newP
                board = newP.startBoard.map { it.toMutableList() }.toMutableList()
                selected = null
                message = null
                isSolved = false
                hintUsed = 0
                hintLimit = defaultHintLimit(size,diff)
                showCandidates = false
                saveProgress()
            }
        }
    }

    private fun checkSolved() {
        val flat = board.flatten()
        if (!flat.contains(0)) {
            if (board.map { it.toList() } == puzzle.solution) {
                isSolved = true
                context?.let { SudokuStorage.clear(it) }
                message = "Solved"
            } else {
                isSolved = false
                message = "Some numbers are placed incorrectly."
            }
        }
    }

    fun saveProgress() {
        context?.let {
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
            SudokuStorage.save(it, save)
        }
    }
}