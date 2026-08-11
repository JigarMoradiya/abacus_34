package com.jigar.me.ui.view.home.screens.math_game_zone.math_bingo.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.math_bingo.components.BINGO_SIZE
import com.jigar.me.ui.view.home.screens.math_game_zone.math_bingo.components.BingoCall
import com.jigar.me.ui.view.home.screens.math_game_zone.math_bingo.components.MathBingoConfig
import com.jigar.me.ui.view.home.screens.math_game_zone.math_bingo.components.MathBingoUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random
import javax.inject.Inject

const val KEY_MATH_BINGO_DIFF = "math_bingo_diff"

@HiltViewModel
class MathBingoPlayViewModel @Inject constructor(
    private val prefManager: AppPreferencesHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val difficulty: CommonDifficulty4 =
        savedStateHandle.get<String>(KEY_MATH_BINGO_DIFF)?.let { CommonDifficulty4.fromName(it) }
            ?: CommonDifficulty4.easy
    val config: MathBingoConfig = MathBingoConfig.forDifficulty(difficulty)

    private val _uiState = MutableStateFlow(MathBingoUiState(timeLeft = config.sessionSeconds))
    val uiState: StateFlow<MathBingoUiState> = _uiState

    private var timerJob: Job? = null

    // App in background - freeze the clock so interruptions never cost
    // stars or time. Set from the screen via lifecycle events.
    private var timerPaused = false
    fun setTimerPaused(paused: Boolean) { timerPaused = paused }
    private var pendingJob: Job? = null
    // The equation behind every card cell, so any called cell has its call.
    private var cellCalls: List<BingoCall> = emptyList()
    private var calledIndex: Int = -1

    private val bestKey get() = "mathBingoBest_${difficulty.name}"
    val bestScore: Int get() = prefManager.getCustomParamInt(bestKey, 0)

    fun start() {
        stop()
        _uiState.value = MathBingoUiState(timeLeft = config.sessionSeconds)
        dealCard()
        timerJob = viewModelScope.launch {
            while (isActive && !_uiState.value.isGameOver) {
                delay(1000)
                if (timerPaused) continue
                _uiState.update { it.copy(timeLeft = it.timeLeft - 1) }
                if (_uiState.value.timeLeft <= 0) endGame()
            }
        }
    }

    fun stop() {
        timerJob?.cancel(); timerJob = null
        pendingJob?.cancel(); pendingJob = null
    }
    override fun onCleared() { stop() }

    private fun makeCall(): BingoCall {
        val op = config.ops[Random.nextInt(config.ops.length)]
        val a: Int; val b: Int; val answer: Int; val symbol: String
        when (op) {
            '+' -> { a = Random.nextInt(1, config.addMax + 1); b = Random.nextInt(1, config.addMax + 1); answer = a + b; symbol = "+" }
            '-' -> { val x = Random.nextInt(1, config.addMax + 1); val y = Random.nextInt(1, config.addMax + 1)
                a = maxOf(x, y); b = minOf(x, y); answer = a - b; symbol = "−" }
            '*' -> { a = Random.nextInt(2, config.mulMax + 1); b = Random.nextInt(2, config.mulMax + 1); answer = a * b; symbol = "×" }
            else -> { val q = Random.nextInt(2, config.mulMax + 1); b = Random.nextInt(2, config.mulMax + 1)
                a = q * b; answer = q; symbol = "÷" }
        }
        return BingoCall(text = "$a $symbol $b", answer = answer)
    }

    // Fresh card: 16 equations with DISTINCT answers become the cells.
    private fun dealCard() {
        val calls = mutableListOf<BingoCall>()
        val used = mutableSetOf<Int>()
        var guard = 0
        while (calls.size < BINGO_SIZE * BINGO_SIZE && guard < 4000) {
            val c = makeCall()
            if (used.add(c.answer)) calls.add(c)
            guard++
        }
        // Defensive: if the guard tripped before 16 distinct answers were
        // found, pad with unique fillers — lines() indexes 0..15 and a
        // short card would crash on the first completed line.
        var filler = 1
        while (calls.size < BINGO_SIZE * BINGO_SIZE) {
            if (used.add(filler)) calls.add(BingoCall("$filler + 0", filler))
            filler++
        }
        cellCalls = calls.shuffled(Random)
        _uiState.update {
            it.copy(
                card = cellCalls.map { c -> c.answer },
                marked = List(BINGO_SIZE * BINGO_SIZE) { false },
                bingoLines = emptySet(),
                cardNumber = it.cardNumber + 1,
                justBingo = false
            )
        }
        nextCall()
    }

    // Calls the equation of a random unmarked cell — the answer is always
    // findable on the card.
    private fun nextCall() {
        val s = _uiState.value
        val unmarked = s.marked.indices.filter { !s.marked[it] }
        if (unmarked.isEmpty()) return
        calledIndex = unmarked[Random.nextInt(unmarked.size)]
        _uiState.update {
            it.copy(call = cellCalls[calledIndex], callNumber = it.callNumber + 1, wrongTapIndex = null)
        }
    }

    private fun lines(): List<List<Int>> {
        val out = mutableListOf<List<Int>>()
        for (r in 0 until BINGO_SIZE) out.add((0 until BINGO_SIZE).map { r * BINGO_SIZE + it })
        for (c in 0 until BINGO_SIZE) out.add((0 until BINGO_SIZE).map { it * BINGO_SIZE + c })
        out.add((0 until BINGO_SIZE).map { it * BINGO_SIZE + it })
        out.add((0 until BINGO_SIZE).map { it * BINGO_SIZE + (BINGO_SIZE - 1 - it) })
        return out
    }

    fun tapCell(index: Int) {
        val s = _uiState.value
        val call = s.call ?: return
        if (s.isGameOver || s.justBingo) return
        if (s.marked.getOrNull(index) != false) return

        if (s.card[index] == call.answer) {
            AudioPlayerManager.playSoundDing()
            val marked = s.marked.toMutableList().also { it[index] = true }
            val newLines = lines().withIndex()
                .filter { (id, line) -> id !in s.bingoLines && line.all { marked[it] } }
                .map { it.index }
                .toSet()
            if (newLines.isNotEmpty()) {
                // BINGO! Banner + bonus, then a fresh card keeps the party going.
                // Marks pay 10 x the multiplier shown on the scoreboard (1+bingos).
                AudioPlayerManager.playSoundClap()
                _uiState.update {
                    it.copy(
                        marked = marked,
                        bingoLines = it.bingoLines + newLines,
                        score = it.score + 10 * (1 + it.bingoCount) + 50 * newLines.size,
                        correctCount = it.correctCount + 1,
                        bingoCount = it.bingoCount + newLines.size,
                        justBingo = true
                    )
                }
                pendingJob = viewModelScope.launch {
                    delay(1600)
                    if (!_uiState.value.isGameOver) dealCard()
                }
            } else {
                _uiState.update {
                    it.copy(marked = marked, score = it.score + 10 * (1 + it.bingoCount), correctCount = it.correctCount + 1)
                }
                nextCall()
            }
        } else {
            AudioPlayerManager.playSoundWrongSoft()
            _uiState.update {
                it.copy(wrongCount = it.wrongCount + 1, wrongTapIndex = index, wrongTapCount = it.wrongTapCount + 1)
            }
        }
    }

    private fun endGame() {
        stop()
        _uiState.update { it.copy(isGameOver = true) }
        val finalScore = _uiState.value.score
        if (finalScore > bestScore) prefManager.setCustomParamInt(bestKey, finalScore)
    }
}
