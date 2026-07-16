package com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop.components.BalloonPopConfig
import com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop.components.BalloonPopGenerator
import com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop.components.BalloonPopUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

const val KEY_BALLOON_DIFF = "balloon_diff"

@HiltViewModel
class BalloonPopPlayViewModel @Inject constructor(
    private val prefManager: AppPreferencesHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val difficulty: CommonDifficulty4 =
        savedStateHandle.get<String>(KEY_BALLOON_DIFF)?.let { CommonDifficulty4.fromName(it) }
            ?: CommonDifficulty4.easy
    val config: BalloonPopConfig = BalloonPopConfig.forDifficulty(difficulty)

    private val _uiState = MutableStateFlow(BalloonPopUiState())
    val uiState: StateFlow<BalloonPopUiState> = _uiState

    private var nextId = 0L
    private var popsTowardRotation = 0
    private var spawnJob: Job? = null
    private var timerJob: Job? = null

    // Correctness is always derived from the CURRENT target, never stored on the
    // balloon — so rotating the target mid-flight can't leave stale flags.
    val complement: Int get() = config.targetSum - _uiState.value.partner

    // Combo multiplier — climbs with consecutive correct taps, resets on a wrong
    // tap. This is what makes the score (and best score) vary run-to-run.
    val multiplier: Int
        get() = when (_uiState.value.streak) {
            in 0..2 -> 1
            in 3..5 -> 2
            in 6..9 -> 3
            else -> 4
        }

    private val bestScoreKey get() = "balloonMakeTenBest_${difficulty.name}"
    val bestScore: Int get() = prefManager.getCustomParamInt(bestScoreKey, 0)

    fun start() {
        stop()
        val firstPartner = BalloonPopGenerator.newPartner(config.targetSum, null)
        popsTowardRotation = 0
        // First balloon appears immediately and is always poppable, so kids see
        // something to do right away.
        val first = BalloonPopGenerator.makeBalloon(nextId++, firstPartner, config, forceCorrect = true)
        _uiState.value = BalloonPopUiState(
            balloons = listOf(first),
            partner = firstPartner,
            timeLeft = config.timerSeconds ?: 0
        )

        spawnJob = viewModelScope.launch {
            while (isActive && !_uiState.value.isGameOver) {
                delay((config.spawnInterval * 1000).toLong())
                spawn()
            }
        }

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

    fun stop() {
        spawnJob?.cancel(); spawnJob = null
        timerJob?.cancel(); timerJob = null
    }

    override fun onCleared() {
        stop()
    }

    private fun spawn() {
        val s = _uiState.value
        if (s.isGameOver) return
        val b = BalloonPopGenerator.makeBalloon(nextId++, s.partner, config)
        _uiState.update { it.copy(balloons = it.balloons + b) }
    }

    fun tap(id: Long) {
        val s = _uiState.value
        if (s.isGameOver) return
        val balloon = s.balloons.firstOrNull { it.id == id } ?: return
        if (balloon.isPopping) return

        if (balloon.value == complement) {
            AudioPlayerManager.playSoundBtnClick()
            val newStreak = s.streak + 1
            val mult = when (newStreak) {
                in 0..2 -> 1; in 3..5 -> 2; in 6..9 -> 3; else -> 4
            }
            popsTowardRotation += 1
            _uiState.update {
                it.copy(
                    balloons = it.balloons.map { b -> if (b.id == id) b.copy(isPopping = true) else b },
                    streak = newStreak,
                    score = it.score + 10 * mult,
                    correctCount = it.correctCount + 1
                )
            }
            // keep the cell alive until the particle dissolve (0.6s) finishes
            viewModelScope.launch {
                delay(700)
                _uiState.update { it.copy(balloons = it.balloons.filterNot { b -> b.id == id }) }
            }

            val goal = config.popsGoal
            if (goal != null && _uiState.value.correctCount >= goal) {
                endGame()
            } else if (popsTowardRotation >= config.popsPerTarget) {
                rotateTarget()
            }
        } else {
            // Wrong balloons don't pop, so the same one stays tappable. Debounce
            // against its own shake window so a rapid double-tap (one mistake)
            // isn't counted as two wrongs / two streak resets.
            if (s.shakeBalloonId == id) return
            _uiState.update {
                it.copy(
                    wrongCount = it.wrongCount + 1,
                    streak = 0,                                    // combo breaks on all modes
                    score = if (difficulty != CommonDifficulty4.easy) maxOf(0, it.score - 5) else it.score,
                    shakeBalloonId = id
                )
            }
            viewModelScope.launch {
                delay(400)
                _uiState.update { if (it.shakeBalloonId == id) it.copy(shakeBalloonId = null) else it }
            }
        }
    }

    // Target change is a big moment for kids: pop EVERY balloon on screen as a
    // celebration/clean break, then announce the new target with a banner.
    private fun rotateTarget() {
        popsTowardRotation = 0
        val newPartner = BalloonPopGenerator.newPartner(config.targetSum, _uiState.value.partner)
        _uiState.update {
            it.copy(
                balloons = it.balloons.map { b -> b.copy(isPopping = true) },
                partner = newPartner,
                showNewTargetBanner = true
            )
        }
        viewModelScope.launch {
            delay(700)
            _uiState.update { it.copy(balloons = emptyList()) }
        }
        viewModelScope.launch {
            delay(1400)
            _uiState.update { it.copy(showNewTargetBanner = false) }
        }
    }

    fun balloonExitedTop(id: Long) {
        val s = _uiState.value
        if (s.isGameOver) return
        val balloon = s.balloons.firstOrNull { it.id == id } ?: return
        _uiState.update { it.copy(balloons = it.balloons.filterNot { b -> b.id == id }) }
        // A popping balloon was already tapped, or cleared by a target rotation —
        // it must NOT also count as missed.
        if (balloon.isPopping) return
        if (balloon.value == complement) {
            _uiState.update { it.copy(missedCount = it.missedCount + 1) }
        }
    }

    private fun endGame() {
        stop()
        val finalScore = _uiState.value.score
        if (finalScore > bestScore) prefManager.setCustomParamInt(bestScoreKey, finalScore)
        _uiState.update { it.copy(isGameOver = true, balloons = emptyList()) }
    }
}
