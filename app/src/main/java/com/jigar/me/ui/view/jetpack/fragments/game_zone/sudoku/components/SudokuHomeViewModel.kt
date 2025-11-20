package com.jigar.me.ui.view.jetpack.fragments.game_zone.sudoku.components

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SudokuHomeViewModel @Inject constructor(
    context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(SudokuHomeUiState())
    val uiState = _uiState.asStateFlow()

    fun openResumePopup() {
        _uiState.update { it.copy(showResumePopup = true) }
    }

    fun closeResumePopup() {
        _uiState.update { it.copy(showResumePopup = false) }
    }
}
