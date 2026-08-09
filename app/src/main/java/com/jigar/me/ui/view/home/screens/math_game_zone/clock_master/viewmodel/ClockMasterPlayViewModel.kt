package com.jigar.me.ui.view.home.screens.math_game_zone.clock_master.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.clock_master.components.ClockMasterConfig
import com.jigar.me.ui.view.home.screens.math_game_zone.clock_master.components.ClockMasterUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.clock_master.components.ClockQuestion
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

const val KEY_CLOCK_MASTER_DIFF = "clock_master_diff"

@HiltViewModel
class ClockMasterPlayViewModel @Inject constructor(
    private val prefManager: AppPreferencesHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val difficulty: CommonDifficulty4 =
        savedStateHandle.get<String>(KEY_CLOCK_MASTER_DIFF)?.let { CommonDifficulty4.fromName(it) }
            ?: CommonDifficulty4.easy
    val config: ClockMasterConfig = ClockMasterConfig.forDifficulty(difficulty)

    private val _uiState = MutableStateFlow(ClockMasterUiState(timeLeft = config.sessionSeconds))
    val uiState: StateFlow<ClockMasterUiState> = _uiState

    private var timerJob: Job? = null
    private var pendingJob: Job? = null
    private var answering = false

    private val bestKey get() = "clockMasterBest_${difficulty.name}"
    val bestScore: Int get() = prefManager.getCustomParamInt(bestKey, 0)

    fun start() {
        stop()
        answering = false
        _uiState.value = ClockMasterUiState(timeLeft = config.sessionSeconds)
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

    private fun format(hour: Int, minute: Int): String = "%d:%02d".format(hour, minute)

    private fun makeQuestion(): ClockQuestion {
        val hour = Random.nextInt(1, 13)
        val steps = 60 / config.minuteStep
        val minute = Random.nextInt(0, steps) * config.minuteStep
        val correct = format(hour, minute)

        // Wrong options: swapped hands, off-by-one hour, off-by-one step —
        // the classic clock-reading mistakes.
        val distractors = mutableSetOf<String>()
        val swappedHour = if (minute / 5 == 0) 12 else minute / 5
        distractors.add(format(swappedHour, (hour % 12) * 5))
        distractors.add(format(if (hour == 12) 1 else hour + 1, minute))
        distractors.add(format(hour, (minute + config.minuteStep) % 60))
        distractors.add(format(if (hour == 1) 12 else hour - 1, minute))
        distractors.remove(correct)
        // Top up: collisions (e.g. 12:00 swapped-hands == correct) can leave
        // fewer than 3 distractors — the 2x2 grid must always show 4 options.
        var guard = 0
        while (distractors.size < 3 && guard < 100) {
            val extra = format(Random.nextInt(1, 13), Random.nextInt(0, steps) * config.minuteStep)
            if (extra != correct) distractors.add(extra)
            guard++
        }

        val options = (distractors.shuffled(Random).take(3) + correct).shuffled(Random)
        return ClockQuestion(
            hour = hour, minute = minute,
            options = options, correctIndex = options.indexOf(correct)
        )
    }

    private fun nextQuestion() {
        _uiState.update {
            it.copy(
                question = makeQuestion(),
                questionNumber = it.questionNumber + 1,
                selectedIndex = null,
                lastAnswerWrong = false,
                lastAnswerRight = false
            )
        }
        answering = false
    }

    fun answer(index: Int) {
        val s = _uiState.value
        val q = s.question ?: return
        if (s.isGameOver || answering) return
        answering = true
        if (index == q.correctIndex) {
            AudioPlayerManager.playSoundDing()
            _uiState.update {
                it.copy(
                    score = it.score + 15 * it.multiplier,
                    streak = it.streak + 1,
                    correctCount = it.correctCount + 1,
                    selectedIndex = index,
                    lastAnswerRight = true
                )
            }
            pendingJob = viewModelScope.launch { delay(450); if (!_uiState.value.isGameOver) nextQuestion() }
        } else {
            AudioPlayerManager.playSoundWrongSoft()
            _uiState.update {
                it.copy(streak = 0, wrongCount = it.wrongCount + 1, selectedIndex = index, lastAnswerWrong = true)
            }
            // Highlight the right time for a beat before moving on.
            pendingJob = viewModelScope.launch { delay(1200); if (!_uiState.value.isGameOver) nextQuestion() }
        }
    }

    private fun endGame() {
        stop()
        _uiState.update { it.copy(isGameOver = true) }
        val finalScore = _uiState.value.score
        if (finalScore > bestScore) prefManager.setCustomParamInt(bestKey, finalScore)
    }
}
