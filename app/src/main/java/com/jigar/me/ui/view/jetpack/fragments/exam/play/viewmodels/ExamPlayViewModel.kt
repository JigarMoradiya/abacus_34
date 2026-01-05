package com.jigar.me.ui.view.jetpack.fragments.exam.play.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.jigar.me.data.local.data.ExamProvider
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.jetpack.core.StatefulViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.PlaySound
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExamPlayViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: AppPreferencesHelper,
) : StatefulViewModel<ExamPlayUiState>() {

    override val TAG = "ExamPlayViewModel"
    private var timerJob: Job? = null
    override fun getInitialState() = ExamPlayUiState()

    init {
        generateExam()
    }

    fun generateExam() {
        val examForList = arrayListOf<String>().apply {
            if (prefs.getCustomParamBoolean(AppConstants.EXAM.isAdditionSelected, true))
                add(AppConstants.EXAM.isAdditionSelected)
            if (prefs.getCustomParamBoolean(AppConstants.EXAM.isSubtractionSelected, false))
                add(AppConstants.EXAM.isSubtractionSelected)
            if (prefs.getCustomParamBoolean(AppConstants.EXAM.isMultiplicationSelected, false))
                add(AppConstants.EXAM.isMultiplicationSelected)
            if (prefs.getCustomParamBoolean(AppConstants.EXAM.isDivisionSelected, false))
                add(AppConstants.EXAM.isDivisionSelected)
        }

        val selectedDifficulty = prefs.getCustomParam(AppConstants.EXAM.examDifficulty, AppConstants.EXAM.examDifficultyBeginner)
        val listExam = ExamProvider.generateExamPaperNew(selectedDifficulty,examForList)
        Log.e("jigarExamPlay","listExam = "+ Gson().toJson(listExam))
        updateState_ {
            copy(selectedExam = examForList,examPaper = listExam, currentIndex = 0,totalWrong = 0, elapsedSeconds = 0, isShowCompletePopup = false)
        }
        startTimer()
    }


    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                updateState_ { copy(elapsedSeconds = elapsedSeconds + 1) }
            }
        }
    }

    // option click
    fun onOptionSelected(
        selectedAnswer: String,
        correctAnswer: String
    ) {
        PlaySound.playTap(context)
        val updatedExamPaper = state().examPaper.toMutableList().apply {
            val current = this[state().currentIndex]
            this[state().currentIndex] = current.copy(
                userAnswer = selectedAnswer
            )
        }

        updateState_ {
            copy(examPaper = updatedExamPaper,
                totalWrong = if (selectedAnswer != correctAnswer) totalWrong + 1 else totalWrong,
                currentIndex = currentIndex + 1
            )
        }

        if (state().currentIndex >= state().examPaper.size) {
            completeExam()
        }
    }

    private fun completeExam() {
        timerJob?.cancel()
        updateState_ { copy(isShowCompletePopup = true) }
    }

    fun onLeaveExam() {
        timerJob?.cancel()
        updateState_ { copy(isLeaveExam = true) }
    }

    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }
}