package com.jigar.me.ui.view.home.screens.activities.exam.play.viewmodels

import com.jigar.me.data.model.data.SubmitAllExamDataRequest
import com.jigar.me.ui.view.home.screens.activities.exam.play.exam_generator.ExamMathQuestion
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
    val isLeavePage: Boolean = false,
    val isShowNoInternet: Boolean = false,
    val noInternetMessage : String = "",
    val submitExamRequest : SubmitAllExamDataRequest? = null,
    val saveResults: Boolean = true,
    val resultSaved: Boolean = false,
)