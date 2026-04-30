package com.jigar.me.ui.view.home.screens.math_game_zone.target_number.components

enum class TargetOperation(val symbol: String) {
    ADD("+"), SUBTRACT("−"), MULTIPLY("×"), DIVIDE("÷")
}

data class TargetSettings(
    val ops: List<TargetOperation>,
    val count: Int,
    val range: IntRange
)

data class TargetPuzzle(
    val target: Int,
    val numbers: List<Int>,
    val allowedOps: List<TargetOperation>,
    val steps: List<String>
)

data class TargetUiState(
    val target: Int = 0,
    val numbers: List<Int> = emptyList(),
    val originalNumbers: List<Int> = emptyList(),
    val allowedOps: List<TargetOperation> = TargetOperation.entries,
    val currentExpression: String = "",
    val steps: List<String> = emptyList(),
    val isSolved: Boolean = false,
    val isSolvedCorrect: Boolean? = null,
    val message: String? = null,
    val solutionSteps: List<String> = emptyList(),
    val shownHintIndex: Int = 0,
    val hintUsed: Int = 0,
    val hintLimit: Int = 0,
    var selectedNumberIndex: Int? = null
)