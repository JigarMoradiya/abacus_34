package com.jigar.me.ui.view.home.screens.math_game_zone.equation_match.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.equation_match.components.EqMatchCard
import com.jigar.me.ui.view.home.screens.math_game_zone.equation_match.components.EqMatchCardState
import com.jigar.me.ui.view.home.screens.math_game_zone.equation_match.components.EqOp
import com.jigar.me.ui.view.home.screens.math_game_zone.equation_match.components.EquationMatchConfig
import com.jigar.me.ui.view.home.screens.math_game_zone.equation_match.components.EquationMatchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random
import javax.inject.Inject

const val KEY_EQUATION_MATCH_DIFF = "equation_match_diff"

@HiltViewModel
class EquationMatchPlayViewModel @Inject constructor(
    private val prefManager: AppPreferencesHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val difficulty: CommonDifficulty4 =
        savedStateHandle.get<String>(KEY_EQUATION_MATCH_DIFF)?.let { CommonDifficulty4.fromName(it) }
            ?: CommonDifficulty4.easy
    val config: EquationMatchConfig = EquationMatchConfig.forDifficulty(difficulty)

    private val _uiState = MutableStateFlow(EquationMatchUiState())
    val uiState: StateFlow<EquationMatchUiState> = _uiState

    private var firstIndex: Int? = null
    private var isLocked = false
    private var nextId = 0

    private val bestKey get() = "equationMatchBest_${difficulty.name}"
    val bestScore: Int get() = prefManager.getCustomParamInt(bestKey, 0)

    val didWin: Boolean get() = _uiState.value.matchesFound == config.pairs

    fun start() {
        firstIndex = null
        isLocked = false
        nextId = 0
        _uiState.value = EquationMatchUiState(cards = generate())
    }

    // MARK: - Interaction

    fun tap(index: Int) {
        val st = _uiState.value
        if (isLocked || st.isGameOver || index !in st.cards.indices) return
        if (st.cards[index].state != EqMatchCardState.DOWN) return
        if (firstIndex == index) return

        setCard(index, EqMatchCardState.UP)
        AudioPlayerManager.playSoundCardFlip()

        val first = firstIndex
        if (first == null) { firstIndex = index; return }
        firstIndex = null

        val cards = _uiState.value.cards
        if (cards[first].pairId == cards[index].pairId) {
            // Match!
            setCard(first, EqMatchCardState.MATCHED)
            setCard(index, EqMatchCardState.MATCHED)
            _uiState.update {
                it.copy(
                    score = it.score + 100 * it.multiplier,
                    multiplier = minOf(4, it.multiplier + 1),
                    matchesFound = it.matchesFound + 1,
                    pulse = !it.pulse
                )
            }
            AudioPlayerManager.playSoundSparkle()
            if (_uiState.value.matchesFound == config.pairs) endGame()
        } else {
            // Mismatch — flip both back after a beat.
            _uiState.update { it.copy(mismatches = it.mismatches + 1, multiplier = 1) }
            isLocked = true
            AudioPlayerManager.playSoundWrongSoft()
            viewModelScope.launch {
                delay(800)
                setCard(first, EqMatchCardState.DOWN, onlyIfUp = true)
                setCard(index, EqMatchCardState.DOWN, onlyIfUp = true)
                isLocked = false
            }
        }
    }

    private fun setCard(index: Int, state: EqMatchCardState, onlyIfUp: Boolean = false) {
        _uiState.update { st ->
            st.copy(cards = st.cards.mapIndexed { i, c ->
                if (i == index && (!onlyIfUp || c.state == EqMatchCardState.UP)) c.copy(state = state) else c
            })
        }
    }

    private fun endGame() {
        viewModelScope.launch {
            delay(500)
            val score = _uiState.value.score
            if (score > bestScore) prefManager.setCustomParamInt(bestKey, score)
            com.jigar.me.utils.WeeklySummaryManager.record(prefManager, _uiState.value.matchesFound)
            _uiState.update { it.copy(isGameOver = true) }
        }
    }

    // Fewer mismatches → more stars.
    fun starCount(): Int {
        val m = _uiState.value.mismatches
        return when {
            m <= maxOf(2, config.pairs / 3) -> 3
            m <= config.pairs -> 2
            else -> 1
        }
    }

    // MARK: - Generator

    private fun generate(): List<EqMatchCard> {
        val used = ArrayList<Int>()
        val out = ArrayList<EqMatchCard>()
        var pairId = 0
        var safety = 0
        while (pairId < config.pairs && safety < 4000) {
            safety++
            val eq = makeEquation()
            if (used.contains(eq.second)) continue

            val second: String =
                if (Random.nextDouble() < config.eqEqChance) findEquation(eq.second, eq.first) ?: "${eq.second}"
                else "${eq.second}"

            used.add(eq.second)
            out.add(EqMatchCard(nextId++, pairId, eq.first))
            out.add(EqMatchCard(nextId++, pairId, second))
            pairId++
        }
        return out.shuffled()
    }

    private fun makeEquation(): Pair<String, Int> {
        return when (config.ops.random()) {
            EqOp.ADD -> {
                val a = Random.nextInt(1, config.maxOperand + 1); val b = Random.nextInt(1, config.maxOperand + 1)
                "$a+$b" to (a + b)
            }
            EqOp.SUB -> {
                val a = Random.nextInt(1, config.maxOperand + 1); val b = Random.nextInt(1, config.maxOperand + 1)
                val hi = maxOf(a, b); val lo = minOf(a, b)
                "$hi−$lo" to (hi - lo)
            }
            EqOp.MUL -> {
                val a = Random.nextInt(2, config.maxOperand + 1); val b = Random.nextInt(2, config.maxOperand + 1)
                "$a×$b" to (a * b)
            }
            EqOp.DIV -> {
                val b = Random.nextInt(2, config.maxOperand + 1); val q = Random.nextInt(2, config.maxOperand + 1)
                "${b * q}÷$b" to q
            }
        }
    }

    private fun findEquation(value: Int, exclude: String): String? {
        repeat(60) {
            val eq = makeEquation()
            if (eq.second == value && eq.first != exclude) return eq.first
        }
        return null
    }
}
