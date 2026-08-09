package com.jigar.me.ui.view.home.screens.math_game_zone.place_value.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.place_value.components.PlaceValueConfig
import com.jigar.me.ui.view.home.screens.math_game_zone.place_value.components.PlaceValueQuestion
import com.jigar.me.ui.view.home.screens.math_game_zone.place_value.components.PlaceValueUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.random.Random
import javax.inject.Inject

const val KEY_PLACE_VALUE_DIFF = "place_value_diff"

@HiltViewModel
class PlaceValuePlayViewModel @Inject constructor(
    private val prefManager: AppPreferencesHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val difficulty: CommonDifficulty4 =
        savedStateHandle.get<String>(KEY_PLACE_VALUE_DIFF)?.let { CommonDifficulty4.fromName(it) }
            ?: CommonDifficulty4.easy
    val config: PlaceValueConfig = PlaceValueConfig.forDifficulty(difficulty)

    private val _uiState = MutableStateFlow(PlaceValueUiState(timeLeft = config.sessionSeconds))
    val uiState: StateFlow<PlaceValueUiState> = _uiState

    private var timerJob: Job? = null
    private var pendingJob: Job? = null
    private var checking = false

    private val bestKey get() = "placeValueBest_${difficulty.name}"
    val bestScore: Int get() = prefManager.getCustomParamInt(bestKey, 0)

    fun start() {
        stop()
        checking = false
        _uiState.value = PlaceValueUiState(timeLeft = config.sessionSeconds)
        nextQuestion()
        timerJob = viewModelScope.launch {
            while (isActive && !_uiState.value.isGameOver) {
                delay(1000)
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

    private fun makeQuestion(): PlaceValueQuestion {
        val digits = config.digits
        val lo = 10.0.pow(digits - 1).toInt()
        val hi = 10.0.pow(digits).toInt() - 1
        var target: Int
        do {
            target = Random.nextInt(lo, hi + 1)
            // Push zero-trap numbers to appear often: retry until at least one
            // zero digit half the time (except 2-digit easy mode).
        } while (digits >= 3 && Random.nextBoolean() && !target.toString().drop(1).contains('0'))

        val parts = mutableListOf<Int>()
        var rest = target
        var place = lo
        while (place >= 1) {
            val d = rest / place
            if (d > 0) parts.add(d * place)
            rest %= place
            place /= 10
        }
        return PlaceValueQuestion(target = target, digits = digits, expandedParts = parts)
    }

    private fun nextQuestion() {
        val q = makeQuestion()
        _uiState.update {
            it.copy(
                question = q,
                questionNumber = it.questionNumber + 1,
                entered = List(q.digits) { null },
                activeSlot = 0,
                lastAnswerWrong = false,
                lastAnswerRight = false
            )
        }
        checking = false
    }

    fun tapSlot(index: Int) {
        val s = _uiState.value
        if (s.isGameOver || checking) return
        if (index !in (s.question?.let { 0 until it.digits } ?: return)) return
        _uiState.update { it.copy(activeSlot = index) }
    }

    fun tapDigit(digit: Int) {
        val s = _uiState.value
        val q = s.question ?: return
        if (s.isGameOver || checking) return
        AudioPlayerManager.playSoundTilePlace()
        val entered = s.entered.toMutableList()
        entered[s.activeSlot] = digit
        // Auto-advance to the next empty slot.
        val next = (s.activeSlot + 1 until q.digits).firstOrNull { entered[it] == null }
            ?: entered.indexOfFirst { it == null }
        _uiState.update {
            it.copy(entered = entered, activeSlot = if (next >= 0) next else it.activeSlot)
        }
        if (entered.none { it == null }) checkAnswer(entered.map { it!! })
    }

    fun backspace() {
        val s = _uiState.value
        if (s.isGameOver || checking) return
        val entered = s.entered.toMutableList()
        // Clear the active slot, or the last filled one if active is empty.
        val target = if (entered[s.activeSlot] != null) s.activeSlot
        else entered.indexOfLast { it != null }
        if (target < 0) return
        entered[target] = null
        _uiState.update { it.copy(entered = entered, activeSlot = target) }
    }

    private fun checkAnswer(digits: List<Int>) {
        val s = _uiState.value
        val q = s.question ?: return
        checking = true
        val built = digits.fold(0) { acc, d -> acc * 10 + d }
        if (built == q.target) {
            AudioPlayerManager.playSoundDing()
            _uiState.update {
                it.copy(
                    score = it.score + 20 * it.multiplier,
                    streak = it.streak + 1,
                    correctCount = it.correctCount + 1,
                    lastAnswerRight = true
                )
            }
            pendingJob = viewModelScope.launch { delay(500); if (!_uiState.value.isGameOver) nextQuestion() }
        } else {
            AudioPlayerManager.playSoundWrongSoft()
            _uiState.update {
                it.copy(streak = 0, wrongCount = it.wrongCount + 1, lastAnswerWrong = true)
            }
            // Show the right answer for a beat, then a fresh number.
            pendingJob = viewModelScope.launch { delay(1400); if (!_uiState.value.isGameOver) nextQuestion() }
        }
    }

    private fun endGame() {
        stop()
        _uiState.update { it.copy(isGameOver = true) }
        val finalScore = _uiState.value.score
        if (finalScore > bestScore) prefManager.setCustomParamInt(bestKey, finalScore)
    }
}
