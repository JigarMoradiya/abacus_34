package com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.duel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.components.CompareRound
import com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.components.Comparator
import com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.components.SpeedCompareConfig
import com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.components.SpeedCompareGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

const val KEY_SPEED_DUEL_DIFF = "speed_duel_diff"

// Face-to-face duel: same comparison for both players, first correct answer
// takes the round. A wrong answer locks that player out of the round.
data class DuelUiState(
    val round: CompareRound? = null,
    val roundNumber: Int = 0,
    val p1Score: Int = 0,
    val p2Score: Int = 0,
    val roundWinner: Int? = null,
    val lockedPlayers: Set<Int> = emptySet(),
    val revealed: Boolean = false,
    val timeLeft: Int = 60,
    val isGameOver: Boolean = false
)

@HiltViewModel
class SpeedCompareDuelViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val difficulty: CommonDifficulty4 =
        savedStateHandle.get<String>(KEY_SPEED_DUEL_DIFF)?.let { CommonDifficulty4.fromName(it) }
            ?: CommonDifficulty4.easy
    private val config = SpeedCompareConfig.forDifficulty(difficulty)

    private val _uiState = MutableStateFlow(DuelUiState())
    val uiState: StateFlow<DuelUiState> = _uiState

    private var timerJob: Job? = null
    private var pendingJob: Job? = null

    // App in background - freeze the clock so interruptions never cost time.
    private var timerPaused = false
    fun setTimerPaused(paused: Boolean) { timerPaused = paused }

    fun start() {
        stop()
        _uiState.value = DuelUiState(timeLeft = DUEL_SECONDS)
        nextRound()
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

    private fun nextRound() {
        _uiState.update {
            it.copy(
                round = SpeedCompareGenerator.makeRound(config),
                roundNumber = it.roundNumber + 1,
                roundWinner = null,
                lockedPlayers = emptySet(),
                revealed = false
            )
        }
    }

    fun answer(player: Int, choice: Comparator) {
        val s = _uiState.value
        val round = s.round ?: return
        if (s.isGameOver || s.revealed || player in s.lockedPlayers) return

        if (choice == round.truth) {
            AudioPlayerManager.playSoundDing()
            _uiState.update {
                it.copy(
                    roundWinner = player,
                    revealed = true,
                    p1Score = it.p1Score + if (player == 1) 10 else 0,
                    p2Score = it.p2Score + if (player == 2) 10 else 0
                )
            }
            scheduleNextRound()
        } else {
            AudioPlayerManager.playSoundWrongSoft()
            val locked = s.lockedPlayers + player
            if (locked.size >= 2) {
                // Both missed — show the truth, nobody scores.
                _uiState.update { it.copy(lockedPlayers = locked, revealed = true) }
                scheduleNextRound()
            } else {
                _uiState.update { it.copy(lockedPlayers = locked) }
            }
        }
    }

    private fun scheduleNextRound() {
        pendingJob = viewModelScope.launch {
            delay(900)
            if (!_uiState.value.isGameOver) nextRound()
        }
    }

    private fun endGame() {
        stop()
        AudioPlayerManager.playSoundWin()
        _uiState.update { it.copy(isGameOver = true) }
    }

    companion object { private const val DUEL_SECONDS = 60 }
}
