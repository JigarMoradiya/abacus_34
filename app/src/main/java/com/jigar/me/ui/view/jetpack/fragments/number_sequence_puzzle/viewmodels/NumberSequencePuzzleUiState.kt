package com.jigar.me.ui.view.jetpack.fragments.number_sequence_puzzle.viewmodels

data class NumberSequencePuzzleUiState(
    val tiles: List<List<Int?>> = emptyList(),
    val moveCount: Int = 0,
    val isSolved: Boolean = false,
    val soundOn: Boolean = true
)
