package com.jigar.me.ui.view.home.screens.math_game_zone.true_false.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.true_false.components.TrueFalseConfig
import com.jigar.me.ui.view.home.screens.math_game_zone.true_false.components.TrueFalseQuestion
import com.jigar.me.ui.view.home.screens.math_game_zone.true_false.components.TrueFalseUiState
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

const val KEY_TRUE_FALSE_DIFF = "true_false_diff"

@HiltViewModel
class TrueFalsePlayViewModel @Inject constructor(
    private val prefManager: AppPreferencesHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val difficulty: CommonDifficulty4 =
        savedStateHandle.get<String>(KEY_TRUE_FALSE_DIFF)?.let { CommonDifficulty4.fromName(it) }
            ?: CommonDifficulty4.easy
    val config: TrueFalseConfig = TrueFalseConfig.forDifficulty(difficulty)

    private val _uiState = MutableStateFlow(TrueFalseUiState(timeLeft = config.sessionSeconds))
    val uiState: StateFlow<TrueFalseUiState> = _uiState

    private var timerJob: Job? = null
    private var pendingJob: Job? = null
    private var answering = false

    private val bestKey get() = "trueFalseBest_${difficulty.name}"
    val bestScore: Int get() = prefManager.getCustomParamInt(bestKey, 0)

    fun start() {
        stop()
        answering = false
        _uiState.value = TrueFalseUiState(timeLeft = config.sessionSeconds)
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

    private fun makeQuestion(): TrueFalseQuestion {
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
        val isTrue = Random.nextBoolean()
        val shown = if (isTrue) answer else {
            // Near-miss lie: off by 1..3, never negative, never accidentally right.
            var lie: Int
            do { lie = answer + (1 + Random.nextInt(3)) * (if (Random.nextBoolean()) 1 else -1) } while (lie == answer || lie < 0)
            lie
        }
        return TrueFalseQuestion(text = "$a $symbol $b = $shown", isTrue = isTrue, correctAnswer = answer)
    }

    private fun nextQuestion() {
        _uiState.update {
            it.copy(
                question = makeQuestion(),
                questionNumber = it.questionNumber + 1,
                lastAnswerWrong = false,
                lastAnswerRight = false
            )
        }
        answering = false
    }

    // Kid smashed TRUE or FALSE.
    fun answer(saidTrue: Boolean) {
        val s = _uiState.value
        val q = s.question ?: return
        if (s.isGameOver || answering) return
        answering = true
        val right = saidTrue == q.isTrue
        if (right) {
            AudioPlayerManager.playSoundDing()
            _uiState.update {
                it.copy(
                    score = it.score + 10 * it.multiplier,
                    streak = it.streak + 1,
                    correctCount = it.correctCount + 1,
                    lastAnswerRight = true
                )
            }
            pendingJob = viewModelScope.launch { delay(350); if (!_uiState.value.isGameOver) nextQuestion() }
        } else {
            AudioPlayerManager.playSoundWrongSoft()
            _uiState.update {
                it.copy(streak = 0, wrongCount = it.wrongCount + 1, lastAnswerWrong = true)
            }
            // Show the correct answer for a beat before moving on.
            pendingJob = viewModelScope.launch { delay(1100); if (!_uiState.value.isGameOver) nextQuestion() }
        }
    }

    private fun endGame() {
        stop()
        _uiState.update { it.copy(isGameOver = true) }
        val finalScore = _uiState.value.score
        if (finalScore > bestScore) prefManager.setCustomParamInt(bestKey, finalScore)
    }
}
