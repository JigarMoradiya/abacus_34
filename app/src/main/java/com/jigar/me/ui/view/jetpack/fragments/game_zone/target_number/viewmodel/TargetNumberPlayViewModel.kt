package com.jigar.me.ui.view.jetpack.fragments.game_zone.target_number.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.jigar.me.ui.view.jetpack.fragments.common.enums.CommonDifficulty4
import com.jigar.me.ui.view.jetpack.fragments.game_zone.target_number.components.TargetOperation
import com.jigar.me.ui.view.jetpack.fragments.game_zone.target_number.components.TargetUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val KEY_UI_STATE = "target_ui_state_json"
private const val KEY_LEVEL = "target_level"
private const val KEY_DIFF = "target_diff"

@HiltViewModel
class TargetNumberPlayViewModel @Inject constructor(
    private val repo: TargetRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val gson = Gson()

    // keep level/difficulty in savedStateHandle (defaults)
    val level: Int = savedStateHandle.get<Int>(KEY_LEVEL) ?: 1
    val difficulty: CommonDifficulty4 = savedStateHandle.get<String>(KEY_DIFF)
        ?.let { CommonDifficulty4.valueOf(it) } ?: CommonDifficulty4.medium

    private val _uiState = MutableStateFlow(TargetUiState())
    val uiState: StateFlow<TargetUiState> = _uiState

    init {
        val json = savedStateHandle.get<String>(KEY_UI_STATE)

        if (!json.isNullOrBlank()) {
            val restored = try {
                gson.fromJson(json, TargetUiState::class.java)
            } catch (_: Throwable) {
                null
            }

            if (restored != null) {
                _uiState.value = restored
            } else {
                generateNewPuzzle(level, difficulty)
            }
        } else {
            generateNewPuzzle(level, difficulty)
        }
    }


    fun generateNewPuzzle(level: Int, difficulty: CommonDifficulty4) {
        viewModelScope.launch {
            val puzzle = repo.generatePuzzle(level, difficulty)
            val newState = TargetUiState(
                target = puzzle.target,
                numbers = puzzle.numbers,
                originalNumbers = puzzle.numbers,
                allowedOps = puzzle.allowedOps,
                currentExpression = "",
                steps = emptyList(),
                isSolved = false,
                isSolvedCorrect = null,
                message = null,
                solutionSteps = puzzle.steps,
                shownHintIndex = 0,
                hintUsed = 0,
                hintLimit = if (level == 1) 1 else if (difficulty == CommonDifficulty4.hard || difficulty == CommonDifficulty4.veryHard) 2 else 1
            )
            updateState(newState)
        }
    }


    // To implement the same behavior as Swift version we need ephemeral selection/operation fields:
    private var firstOperand: Int? = null
    private var selectedIndex: Int? = null
    private var selectedOp: TargetOperation? = null

    fun tapNumber(index: Int) {
        val s = uiState.value
        if (s.isSolved) return

        val num = s.numbers.getOrNull(index) ?: return

        // 1) First selection
        if (selectedIndex == null) {
            selectedIndex = index
            firstOperand = num

            updateState(
                s.copy(
                    currentExpression = "$num",
                    message = null,
                    selectedNumberIndex = index  // highlight
                )
            )
            return
        }

        // 2) Same number tapped again
        if (selectedIndex == index) {
            updateState(
                s.copy(message = "You already selected this number. Choose another.")
            )
            return
        }

        // 3) Need operation first
        val op = selectedOp
        val a = firstOperand
        if (op == null || a == null) {
            updateState(
                s.copy(message = "Select an operation first!")
            )
            return
        }

        val b = num
        val result = perform(op, a, b)
        val step = "($a ${op.symbol} $b) = $result"

        val newNumbers = s.numbers.toMutableList().also {
            val i1 = maxOf(selectedIndex!!, index)
            val i2 = minOf(selectedIndex!!, index)
            it.removeAt(i1)
            it.removeAt(i2)
            it.add(result)
        }

        selectedIndex = null
        firstOperand = null
        selectedOp = null

        updateState(
            s.copy(
                numbers = newNumbers,
                steps = s.steps + step,
                currentExpression = step,
                message = null,
                selectedNumberIndex = null  // remove highlight
            )
        )

        checkSolved(uiState.value)
    }


    private fun updateState(newState: TargetUiState) {
        _uiState.value = newState
        savedStateHandle["target_ui_state"] = gson.toJson(newState)
    }


    fun tapOperation(op: TargetOperation) {
        val s = uiState.value
        if (firstOperand == null) {
            _uiState.value = s.copy(message = "Select a number first!")
            savedStateHandle[KEY_UI_STATE] = gson.toJson(_uiState.value)
            return
        }
        selectedOp = op
        _uiState.value = s.copy(currentExpression = "${firstOperand!!} ${op.symbol}", message = null)
        savedStateHandle[KEY_UI_STATE] = gson.toJson(_uiState.value)
    }

    private fun perform(op: TargetOperation, a: Int, b: Int): Int {
        return when (op) {
            TargetOperation.ADD -> a + b
            TargetOperation.SUBTRACT -> a - b
            TargetOperation.MULTIPLY -> a * b
            TargetOperation.DIVIDE -> if (b != 0) a / b else a
        }
    }

    fun resetPuzzle() {
        val s = uiState.value
        firstOperand = null; selectedIndex = null; selectedOp = null
        val newState = s.copy(
            numbers = s.originalNumbers,
            steps = emptyList(),
            currentExpression = "",
            isSolved = false,
            isSolvedCorrect = null,
            message = null,
            shownHintIndex = 0,
            hintUsed = 0
        )
        updateState(newState)
    }

    fun showHint() {
        val s = uiState.value
        if (s.hintUsed >= s.hintLimit) {
            _uiState.value = s.copy(message = "No more hints available.")
            savedStateHandle[KEY_UI_STATE] = gson.toJson(_uiState.value)
            return
        }
        val idx = s.shownHintIndex
        if (idx < s.solutionSteps.size) {
            val hint = s.solutionSteps[idx]
            val newState = s.copy(
                shownHintIndex = idx + 1,
                hintUsed = s.hintUsed + 1,
                message = "Hint: $hint"
            )
            updateState(newState)
        } else {
            _uiState.value = s.copy(message = "All hints already shown")
            savedStateHandle[KEY_UI_STATE] = gson.toJson(_uiState.value)
        }
    }

    private fun checkSolved(s: TargetUiState) {
        if (s.numbers.size == 1) {
            val result = s.numbers[0]
            if (result == s.target) {
                val next = s.copy(isSolved = true, isSolvedCorrect = true, message = "✅ Perfect! You reached ${s.target}")
                updateState(next)
            } else {
                val next = s.copy(isSolved = true, isSolvedCorrect = false, message = "❌ Final result $result, target was ${s.target}")
                updateState(next)
            }
        }
    }

    // optional: expose a method to clear persisted UI state
    fun clearPersistedState() {
        savedStateHandle.remove<String>(KEY_UI_STATE)
    }
}

class TargetNumberPlayViewModelFake : ViewModel() {

    // Fake MutableStateFlow
    private val _uiState = MutableStateFlow(
        TargetUiState(
            target = 42,
            numbers = listOf(8, 4, 6),
            allowedOps = listOf(
                TargetOperation.ADD,
                TargetOperation.SUBTRACT,
                TargetOperation.MULTIPLY
            ),
            currentExpression = "(8 + 4)",
            steps = listOf("8 + 4 = 12"),
            message = "Try next step",
            isSolved = false,
            isSolvedCorrect = null,
            hintUsed = 0,
            hintLimit = 2
        )
    )
    val uiState: StateFlow<TargetUiState> = _uiState

    // no-op calls for preview
    fun tapNumber(i: Int) {}
    fun tapOperation(op: TargetOperation) {}
    fun showHint() {}
    fun resetPuzzle() {}
    fun generateNewPuzzle(level: Int, diff: CommonDifficulty4) {}
}
