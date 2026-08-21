package com.jigar.me.ui.view.home.screens.math_game_zone.number_detective.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.number_detective.components.NumberDetectiveConfig
import com.jigar.me.ui.view.home.screens.math_game_zone.number_detective.components.NumberDetectiveQuestion
import com.jigar.me.ui.view.home.screens.math_game_zone.number_detective.components.NumberDetectiveUiState
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

const val KEY_NUMBER_DETECTIVE_DIFF = "number_detective_diff"

@HiltViewModel
class NumberDetectivePlayViewModel @Inject constructor(
    private val prefManager: AppPreferencesHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val difficulty: CommonDifficulty4 =
        savedStateHandle.get<String>(KEY_NUMBER_DETECTIVE_DIFF)?.let { CommonDifficulty4.fromName(it) }
            ?: CommonDifficulty4.easy
    val config: NumberDetectiveConfig = NumberDetectiveConfig.forDifficulty(difficulty)

    private val _uiState = MutableStateFlow(NumberDetectiveUiState(timeLeft = config.sessionSeconds, maxTries = config.maxTries))
    val uiState: StateFlow<NumberDetectiveUiState> = _uiState

    private var timerJob: Job? = null
    private var timerPaused = false
    fun setTimerPaused(paused: Boolean) { timerPaused = paused }
    private var pendingJob: Job? = null
    private var checking = false

    private val bestKey get() = "numberDetectiveBest_${difficulty.name}"
    val bestScore: Int get() = prefManager.getCustomParamInt(bestKey, 0)

    fun start() {
        stop()
        checking = false
        _uiState.value = NumberDetectiveUiState(timeLeft = config.sessionSeconds, maxTries = config.maxTries)
        nextQuestion()
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

    private fun nextQuestion() {
        val secret = Random.nextInt(config.minRange, config.maxRange + 1)
        _uiState.update {
            it.copy(
                question = NumberDetectiveQuestion(secret, config.minRange, config.maxRange),
                questionNumber = it.questionNumber + 1,
                enteredDigits = "",
                triesUsed = 0,
                guesses = emptyList(),
                feedbackDirection = 0,
                lastAnswerWrong = false,
                lastAnswerRight = false
            )
        }
        checking = false
    }

    fun tapDigit(digit: Int) {
        val s = _uiState.value
        if (s.isGameOver || checking) return
        val maxLen = config.maxRange.toString().length
        if (s.enteredDigits.length >= maxLen) return
        AudioPlayerManager.playSoundTilePlace()
        _uiState.update { it.copy(enteredDigits = it.enteredDigits + digit) }
    }

    fun backspace() {
        val s = _uiState.value
        if (s.isGameOver || checking || s.enteredDigits.isEmpty()) return
        _uiState.update { it.copy(enteredDigits = it.enteredDigits.dropLast(1)) }
    }

    fun submitGuess() {
        val s = _uiState.value
        val q = s.question ?: return
        val guess = s.enteredDigits.toIntOrNull() ?: return
        if (s.isGameOver || checking) return
        checking = true

        val triesUsed = s.triesUsed + 1
        val guesses = s.guesses + guess

        if (guess == q.secret) {
            AudioPlayerManager.playSoundDing()
            val bonus = (s.maxTries - triesUsed + 1).coerceAtLeast(1) * 10
            _uiState.update {
                it.copy(
                    score = it.score + bonus * it.multiplier,
                    streak = it.streak + 1,
                    correctCount = it.correctCount + 1,
                    triesUsed = triesUsed,
                    guesses = guesses,
                    feedbackDirection = 0,
                    lastAnswerRight = true
                )
            }
            pendingJob = viewModelScope.launch { delay(900); if (!_uiState.value.isGameOver) nextQuestion() }
        } else if (triesUsed >= s.maxTries) {
            AudioPlayerManager.playSoundWrongSoft()
            _uiState.update {
                it.copy(
                    streak = 0,
                    wrongCount = it.wrongCount + 1,
                    triesUsed = triesUsed,
                    guesses = guesses,
                    feedbackDirection = 0,
                    lastAnswerWrong = true
                )
            }
            pendingJob = viewModelScope.launch { delay(1600); if (!_uiState.value.isGameOver) nextQuestion() }
        } else {
            AudioPlayerManager.playSoundWrongSoft()
            _uiState.update {
                it.copy(
                    triesUsed = triesUsed,
                    guesses = guesses,
                    enteredDigits = "",
                    feedbackDirection = if (guess < q.secret) 1 else -1
                )
            }
            checking = false
        }
    }

    // After 2 strong sessions in a row, invite the kid up a tier.
    private fun maybeSuggestNextDifficulty() {
        val next = when (difficulty) {
            CommonDifficulty4.easy -> CommonDifficulty4.medium
            CommonDifficulty4.medium -> CommonDifficulty4.hard
            CommonDifficulty4.hard -> CommonDifficulty4.veryHard
            else -> return
        }
        val s = _uiState.value
        val total = s.correctCount + s.wrongCount
        val accuracy = if (total == 0) 0f else s.correctCount.toFloat() / total
        val strong = accuracy >= 0.85f && s.correctCount >= 5
        val key = "numberDetectiveStrongRuns_${difficulty.name}"
        val runs = if (strong) prefManager.getCustomParamInt(key, 0) + 1 else 0
        prefManager.setCustomParamInt(key, runs)
        if (runs >= 2) {
            prefManager.setCustomParamInt(key, 0)
            _uiState.update { it.copy(suggestedDifficulty = next.displayName) }
        }
    }

    private fun endGame() {
        stop()
        val correctCount = _uiState.value.correctCount
        _uiState.update { it.copy(isGameOver = true) }
        val finalScore = _uiState.value.score
        if (finalScore > bestScore) prefManager.setCustomParamInt(bestKey, finalScore)
        com.jigar.me.utils.WeeklySummaryManager.record(prefManager, correctCount)
        maybeSuggestNextDifficulty()
    }
}
