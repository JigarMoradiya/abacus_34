package com.jigar.me.ui.view.jetpack.fragments.exam.play.viewmodels

import com.jigar.me.ui.view.jetpack.fragments.exam.play.exam_generator.ExamMathQuestion
import com.jigar.me.utils.AppConstants

data class ExamPlayUiState(
    val error: Int? = null,
    val isLoading: Boolean = false,
    val selectedExam: ArrayList<String> = arrayListOf(),
    val selectedDifficulty: String = AppConstants.EXAM.examDifficultyBeginner,

    val examPaper: List<ExamMathQuestion> = arrayListOf(),
    val questionsList: List<ExamMathQuestion> = arrayListOf(),

    val currentIndex: Int = 0,
    val totalCorrect: Int = 0,
    val elapsedSeconds: Int = 0,

    val isShowCompletePopup: Boolean = false,
    val isLeaveExam: Boolean = false,
    val isShowNoInternet: Boolean = false,
    val noInternetMessage : String = "",
)