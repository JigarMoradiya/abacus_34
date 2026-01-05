package com.jigar.me.ui.view.jetpack.fragments.exam.play.viewmodels

import com.jigar.me.data.local.data.ExamPaper
import com.jigar.me.utils.AppConstants

data class ExamPlayUiState(
    val error: Int? = null,
    val selectedExam: ArrayList<String> = arrayListOf(),

    val examPaper: List<ExamPaper> = arrayListOf(),
    val isShowCompletePopup: Boolean = false,
    val isLeaveExam: Boolean = false,
    val currentIndex: Int = 0,
    val totalWrong: Int = 0,
    val elapsedSeconds: Int = 0,
)