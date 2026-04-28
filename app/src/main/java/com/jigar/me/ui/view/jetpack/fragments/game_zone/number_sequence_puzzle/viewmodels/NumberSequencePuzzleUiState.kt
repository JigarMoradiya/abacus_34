package com.jigar.me.ui.view.jetpack.fragments.game_zone.number_sequence_puzzle.viewmodels

import androidx.compose.ui.graphics.Color

data class NumberSequencePuzzleUiState(
    val tiles: List<List<Int?>> = emptyList(),
    val tileColors: List<List<Color>> = emptyList(),
    val moveCount: Int = 0,
    val isSolved: Boolean = false,
    val soundOn: Boolean = true
)
