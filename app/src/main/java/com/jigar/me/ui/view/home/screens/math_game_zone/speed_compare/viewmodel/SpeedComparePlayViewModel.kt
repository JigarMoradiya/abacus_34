package com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.components.Comparator
import com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.components.SpeedCompareConfig
import com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.components.SpeedCompareGenerator
import com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.components.SpeedCompareUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

const val KEY_SPEED_DIFF = "speed_diff"

@HiltViewModel
class SpeedComparePlayViewModel @Inject constructor(
    private val prefManager: AppPreferencesHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val difficulty: CommonDifficulty4 =
        savedStateHandle.get<String>(KEY_SPEED_DIFF)?.let { CommonDifficulty4.fromName(it) }
            ?: CommonDifficulty4.easy
    val config: SpeedCompareConfig = SpeedCompareConfig.forDifficulty(difficulty)

    private val _uiState = MutableStateFlow(SpeedCompareUiState(round = SpeedCompareGenerator.makeRound(config)))
    val uiState: StateFlow<SpeedCompareUiState> = _uiState

    private var timerJob: Job? = null

    val multiplier: Int
        get() = when (_uiState.value.streak) {
            in 0..2 -> 1; in 3..5 -> 2; in 6..9 -> 3; else -> 4
        }

    private val bestScoreKey get() = "speedCompareBest_${difficulty.name}"
    val bestScore: Int get() = prefManager.getCustomParamInt(bestScoreKey, 0)

    fun start() {
        stop()
        _uiState.value = SpeedCompareUiState(
            round = SpeedCompareGenerator.makeRound(config),
            timeLeft = config.timerSeconds ?: 0
        )
        if (config.timerSeconds != null) {
            timerJob = viewModelScope.launch {
                while (isActive && !_uiState.value.isGameOver) {
                    delay(1000)
                    val left = _uiState.value.timeLeft - 1
                    _uiState.update { it.copy(timeLeft = left) }
                    if (left <= 0) endGame()
                }
            }
        }
    }

    fun stop() { timerJob?.cancel(); timerJob = null }

    override fun onCleared() { stop() }

    fun answer(comparator: Comparator) {
        val s = _uiState.value
        if (s.revealed || s.isGameOver) return
        val correct = comparator == s.round.truth
        val newStreak = if (correct) s.streak + 1 else 0
        val mult = when (newStreak) { in 0..2 -> 1; in 3..5 -> 2; in 6..9 -> 3; else -> 4 }

        if (correct) {
            AudioPlayerManager.playSoundDing()
            _uiState.update {
                it.copy(
                    chosen = comparator, revealed = true, lastCorrect = true,
                    roundsPlayed = it.roundsPlayed + 1,
                    streak = newStreak, score = it.score + 10 * mult,
                    correctCount = it.correctCount + 1
                )
            }
        } else {
            AudioPlayerManager.playSoundWrongSoft()
            _uiState.update {
                it.copy(
                    chosen = comparator, revealed = true, lastCorrect = false,
                    roundsPlayed = it.roundsPlayed + 1,
                    streak = 0, wrongCount = it.wrongCount + 1,
                    score = if (difficulty != CommonDifficulty4.easy) maxOf(0, it.score - 5) else it.score
                )
            }
        }

        // Hold the tipped scale so the kid sees the answer, then advance.
        viewModelScope.launch {
            delay(950)
            if (_uiState.value.isGameOver) return@launch
            val goal = config.roundsGoal
            if (goal != null && _uiState.value.roundsPlayed >= goal) {
                endGame()
            } else {
                _uiState.update {
                    it.copy(round = SpeedCompareGenerator.makeRound(config), revealed = false, chosen = null)
                }
            }
        }
    }

    private fun endGame() {
        stop()
        val finalScore = _uiState.value.score
        if (finalScore > bestScore) prefManager.setCustomParamInt(bestScoreKey, finalScore)
        _uiState.update { it.copy(isGameOver = true) }
    }
}
