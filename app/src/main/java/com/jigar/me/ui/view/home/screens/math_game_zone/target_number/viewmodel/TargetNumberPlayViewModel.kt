package com.jigar.me.ui.view.home.screens.math_game_zone.target_number.viewmodel

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.target_number.components.TargetOperation
import com.jigar.me.ui.view.home.screens.math_game_zone.target_number.components.TargetUiState
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val KEY_UI_STATE = "target_ui_state_json"
private const val KEY_LEVEL = "target_level"
private const val KEY_DIFF = "target_diff"

@HiltViewModel
class TargetNumberPlayViewModel @Inject constructor(
    private val repo: TargetRepository,
    private val savedStateHandle: SavedStateHandle,
    private val app : Application
) : ViewModel() {

    private val gson = Gson()

    // LEVEL + DIFFICULTY (if passed via navigation stored in SavedStateHandle)
    val level: Int = savedStateHandle.get(KEY_LEVEL) ?: 1
    val difficulty: CommonDifficulty4 =
        savedStateHandle.get<String>(KEY_DIFF)?.let { CommonDifficulty4.valueOf(it) }
            ?: CommonDifficulty4.medium

    private val _uiState = MutableStateFlow(TargetUiState())
    val uiState: StateFlow<TargetUiState> = _uiState

    // Temporary working values (not part of UI State)
    private var firstOperand: Int? = null
    private var selectedIndex: Int? = null
    private var selectedOp: TargetOperation? = null

    init {
        startGenerating()
    }

    private fun startGenerating() {
        val json = savedStateHandle.get<String>(KEY_UI_STATE)
        if (!json.isNullOrBlank()) {
            val restored = try {
                gson.fromJson(json, TargetUiState::class.java)
            } catch (_: Throwable) {
                null
            }

            if (restored != null) {
                _uiState.value = restored
                return
            }
        }
        generateNewPuzzle()
    }

    // ---------------------------------------------------------
    // GENERATE NEW PUZZLE
    // ---------------------------------------------------------

    fun generateNewPuzzle() {
        viewModelScope.launch {
            val puzzle = repo.generatePuzzle(level, difficulty)

            val state = TargetUiState(
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
//                hintLimit = 10,
                hintLimit = when {
                    level == 1 -> 1
                    difficulty == CommonDifficulty4.hard || difficulty == CommonDifficulty4.veryHard -> 2
                    else -> 1
                },
                selectedNumberIndex = null
            )

            firstOperand = null
            selectedIndex = null
            selectedOp = null

            updateState(state)
        }
    }

    // ---------------------------------------------------------
    // UPDATE STATE + PERSIST
    // ---------------------------------------------------------

    private fun updateState(newState: TargetUiState) {
        _uiState.value = newState
        savedStateHandle[KEY_UI_STATE] = gson.toJson(newState)
    }

    // ---------------------------------------------------------
    // TAP NUMBER
    // ---------------------------------------------------------

    fun tapNumber(index: Int) {
        val s = uiState.value
        if (s.isSolved) return

        val num = s.numbers.getOrNull(index) ?: return

        // FIRST SELECTION
        if (selectedIndex == null) {
            selectedIndex = index
            firstOperand = num
            AudioPlayerManager.playSoundBtnClick()
            updateState(
                s.copy(
                    currentExpression = "$num",
                    message = null,
                    selectedNumberIndex = index
                )
            )
            return
        }

        // SAME NUMBER PRESSED AGAIN
        if (selectedIndex == index) {
            AudioPlayerManager.playSoundAnsWrong()
            updateState(s.copy(message = "You already selected this number. Choose another."))
            return
        }

        // REQUIRE OPERATION BEFORE SECOND NUMBER
        val op = selectedOp
        val a = firstOperand
        if (op == null || a == null) {
            AudioPlayerManager.playSoundAnsWrong()
            updateState(s.copy(message = "Select an operation first!"))
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

        // RESET OPERANDS
        selectedIndex = null
        firstOperand = null
        selectedOp = null

        AudioPlayerManager.playSoundBtnClick()
        val newState = s.copy(
            numbers = newNumbers,
            steps = s.steps + step,
            currentExpression = step,
            message = null,
            selectedNumberIndex = null
        )

        updateState(newState)
        checkSolved(newState)
    }

    // ---------------------------------------------------------
    // TAP OPERATION
    // ---------------------------------------------------------

    fun tapOperation(op: TargetOperation) {
        val s = uiState.value

        if (firstOperand == null) {
            AudioPlayerManager.playSoundAnsWrong()
            updateState(s.copy(message = "Select a number first!"))
            return
        }

        selectedOp = op
        AudioPlayerManager.playSoundBtnClick()
        updateState(s.copy(currentExpression = "${firstOperand!!} ${op.symbol}", message = null))
    }

    // ---------------------------------------------------------
    // OPERATION PERFORM
    // ---------------------------------------------------------

    private fun perform(op: TargetOperation, a: Int, b: Int): Int {
        return when (op) {
            TargetOperation.ADD -> a + b
            TargetOperation.SUBTRACT -> a - b
            TargetOperation.MULTIPLY -> a * b
            TargetOperation.DIVIDE -> if (b != 0) a / b else a
        }
    }

    // ---------------------------------------------------------
    // RESET
    // ---------------------------------------------------------

    fun resetPuzzle() {
        val s = uiState.value

        firstOperand = null
        selectedIndex = null
        selectedOp = null
        AudioPlayerManager.playSoundHintClick()
        updateState(
            s.copy(
                numbers = s.originalNumbers,
                steps = emptyList(),
                currentExpression = "",
                isSolved = false,
                isSolvedCorrect = null,
                message = null,
                shownHintIndex = 0,
                hintUsed = 0,
                selectedNumberIndex = null
            )
        )
    }

    // ---------------------------------------------------------
    // HINT LOGIC (SMART + CANONICAL FALLBACK)
    // ---------------------------------------------------------

    fun showHint() {
        val s = uiState.value

        // Limit
        if (s.hintUsed >= s.hintLimit) {
            AudioPlayerManager.playSoundAnsWrong()
            updateState(s.copy(message = "No more hints available."))
            return
        }

        // Dynamic Hint from current numbers
        val dynamicHint = findDynamicHint(s.numbers, s.target, s.allowedOps)
//        Log.e("TargetNumberPlayViewModel","dynamicHint = "+ Gson().toJson(dynamicHint))
//        Log.e("TargetNumberPlayViewModel","solutionSteps = "+ Gson().toJson(s.solutionSteps))

        if (dynamicHint != null) {
            AudioPlayerManager.playSoundHintClick()
            updateState(
                s.copy(
                    hintUsed = s.hintUsed + 1,
                    message = "Hint: $dynamicHint"
                )
            )
            return
        }

        // Fallback → Use canonical solution steps
        val next = s.steps.size

        if (next >= s.solutionSteps.size) {
            AudioPlayerManager.playSoundAnsWrong()
            updateState(s.copy(message = "All hints already shown"))
            return
        }

        val fallbackHint = s.solutionSteps[next]

        AudioPlayerManager.playSoundHintClick()
        updateState(
            s.copy(
                hintUsed = s.hintUsed + 1,
                shownHintIndex = next + 1,
                message = "Hint: $fallbackHint"
            )
        )
    }

    // ---------------------------------------------------------
    // SMART HINT ENGINE
    // ---------------------------------------------------------

    private fun applyOperation(op: TargetOperation, a: Int, b: Int): Int? {
        return when (op) {
            TargetOperation.ADD -> a + b
            TargetOperation.SUBTRACT -> (a - b).takeIf { it > 0 }
            TargetOperation.MULTIPLY -> a * b
            TargetOperation.DIVIDE ->
                if (b != 0 && a % b == 0 && a / b > 0) a / b else null
        }
    }

    // try both orders when applying op
    private fun tryBothOrders(op: TargetOperation, a: Int, b: Int): Sequence<Int> = sequence {
        applyOperation(op, a, b)?.let { yield(it) }
        applyOperation(op, b, a)?.let { yield(it) }
    }

    // recursive solver (tries both orders for non-commutative ops)
    private fun canSolve(nums: List<Int>, target: Int, ops: List<TargetOperation>): Boolean {
        if (nums.size == 1) return nums[0] == target

        for (i in nums.indices) {
            for (j in i + 1 until nums.size) {
                val a = nums[i]
                val b = nums[j]

                val rest = nums.toMutableList()
                // remove larger index first
                rest.removeAt(j)
                rest.removeAt(i)

                for (op in ops) {
                    // try a op b and b op a (applyOperation already enforces validity)
                    for (r in tryBothOrders(op, a, b)) {
                        val next = rest + r
                        if (canSolve(next, target, ops)) return true
                    }
                }
            }
        }
        return false
    }

    private fun findDynamicHint(numbers: List<Int>, target: Int, ops: List<TargetOperation>): String? {
        for (i in numbers.indices) {
            for (j in i + 1 until numbers.size) {
                val a = numbers[i]
                val b = numbers[j]

                val rest = numbers.toMutableList()
                rest.removeAt(j)
                rest.removeAt(i)

                for (op in ops) {
                    // try both orders: (a op b) and (b op a)
                    for (r in tryBothOrders(op, a, b)) {
                        val next = rest + r
                        if (canSolve(next, target, ops)) {
                            // Prefer returning the form that produced `r`
                            // determine which order produced r (try to match visually)
                            val text1 = "($a ${op.symbol} $b) = $r"
                            val text2 = "($b ${op.symbol} $a) = $r"
                            // if applyOperation(a,b) yields r then return text1, else text2
                            val producedByAB = applyOperation(op, a, b) == r
                            return if (producedByAB) text1 else text2
                        }
                    }
                }
            }
        }
        return null
    }


    // ---------------------------------------------------------
    // CHECK SOLVED
    // ---------------------------------------------------------

    private fun checkSolved(s: TargetUiState) {
        if (s.numbers.size != 1) return

        val result = s.numbers[0]

        if (result == s.target) {
            AudioPlayerManager.playSoundWin()
            updateState(
                s.copy(
                    isSolved = true,
                    isSolvedCorrect = true,
                    message = "🎉 Perfect! You reached ${s.target}"
                )
            )
        } else {
            AudioPlayerManager.playSoundAnsWrong()
            updateState(
                s.copy(
                    isSolved = true,
                    isSolvedCorrect = false,
//                    message = "❌ $result ≠ ${s.target}"
                    message = "❌ Final result $result, target was ${s.target}"
                )
            )
        }
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
