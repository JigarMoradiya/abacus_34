package com.jigar.me.ui.view.jetpack.fragments.activities.exam.home.viewmodels

import com.jigar.me.utils.AppConstants


data class ExamHomeUiState(
    val error: Int? = null,
    val isAdditionSelected: Boolean = false,
    val isSubtractionSelected: Boolean = false,
    val isMultiplicationSelected: Boolean = false,
    val isDivisionSelected: Boolean = false,
    val selectedDifficulty: String = AppConstants.EXAM.examDifficultyBeginner,
)