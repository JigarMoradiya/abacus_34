package com.jigar.me.ui.view.home.screens.activities.exam.play.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.jigar.me.BuildConfig
import com.jigar.me.R
import com.jigar.me.data.model.data.QuestionDataRequest
import com.jigar.me.data.model.data.SubmitAllExamDataRequest
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.api.SubmitAllExamUseCase
import com.jigar.me.ui.jetpack.core.StatefulViewModelAbacus
import com.jigar.me.ui.view.home.screens.activities.exam.play.exam_generator.ExamGenerator
import com.jigar.me.ui.view.home.screens.activities.exam.play.exam_generator.toQuestionDataRequest
import com.jigar.me.utils.AppConstants
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.utils.extensions.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExamPlayViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: AppPreferencesHelper,
    private val submitAllExamUseCase: SubmitAllExamUseCase
) : StatefulViewModelAbacus<ExamPlayUiState>(prefs = prefs,numberOfColumns = 3) {

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
        val listExam = ExamGenerator.generateExamPaperNew(selectedDifficulty,examForList)
        updateState_ {
            copy(selectedDifficulty = selectedDifficulty, selectedExam = examForList,examPaper = listExam, currentIndex = 0,totalCorrect = 0, elapsedSeconds = 0, isShowCompletePopup = false)
        }
        timerJob?.cancel()
        startTimer()
    }

    fun reGenerateExam(){
        val listExam = ExamGenerator.generateExamPaperNew(state().selectedDifficulty,state().selectedExam)
        updateState_ {
            copy(examPaper = listExam, currentIndex = 0,totalCorrect = 0, elapsedSeconds = 0, isShowCompletePopup = false)
        }
        timerJob?.cancel()
        startTimer()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                updateState_ { copy(elapsedSeconds = elapsedSeconds + 1) }
            }
        }
    }

    // option click
    fun onOptionSelected(selectedAnswer: Int, correctAnswer: Int) {
        AudioPlayerManager.playSoundBtnClick()
        val isCorrectAnswer = selectedAnswer == correctAnswer
        val updatedExamPaper = state().examPaper.toMutableList().apply {
            val current = this[state().currentIndex]
            this[state().currentIndex] = current.copy(userAnswer = selectedAnswer, isCorrect = isCorrectAnswer)
        }
        if (state().currentIndex >= state().examPaper.lastIndex) {
            updateState_ {
                copy(examPaper = updatedExamPaper, totalCorrect = if (isCorrectAnswer) totalCorrect + 1 else totalCorrect)
            }
            timerJob?.cancel()
            completeExam()
        }else{
            updateState_ {
                copy(examPaper = updatedExamPaper,
                    totalCorrect = if (isCorrectAnswer) totalCorrect + 1 else totalCorrect,
                    currentIndex = currentIndex + 1
                )
            }
        }
    }

    fun completeExam(isFirstAttempt : Boolean = true) {
        if (context.isNetworkAvailable){
            generateRequest()
        }else{
            updateState_ {
                copy(isShowNoInternet = true, noInternetMessage = if (isFirstAttempt) context.getString(R.string.no_internet) else context.getString(R.string.still_no_internet))
            }
        }
    }

    private fun generateRequest() {
        val submitExamRequest = SubmitAllExamDataRequest()
        with(submitExamRequest){
            type = AppConstants.EXAM.type_Exam
            level = state().selectedDifficulty
            theme = theme
            sub_type = state().selectedExam.joinToString(separator = ",")
            total_time_taken = state().elapsedSeconds
            no_of_questions = state().examPaper.size
            no_of_right_answers = state().totalCorrect
            val questionsList : ArrayList<QuestionDataRequest> = arrayListOf()
            state().examPaper.forEach {
                questionsList.add(it.toQuestionDataRequest())
            }
            questions = questionsList
        }
//        if (BuildConfig.DEBUG){
//            Log.e("jigarExamPlay","submitExamRequest = "+ Gson().toJson(submitExamRequest))
//            updateState_ {
//                copy(submitExamRequest = submitExamRequest,isShowCompletePopup = true)
//            }
//        }else{
            submitExamApi(submitExamRequest)
//        }
    }

    fun submitExamApi(submitExamRequest : SubmitAllExamDataRequest) = viewModelScope.launch {
        submitAllExamUseCase(
            params = submitExamRequest,
            onStart = {
                updateState_ {
                    copy(isLoading = true,isShowNoInternet = false,submitExamRequest = submitExamRequest)
                }
            },
            onEachEmit = {},
            onCompletion = {
                updateState_ {
                    copy(isLoading = false, isShowCompletePopup = true)
                }
            },
            onError = {
                updateState_ {
                    copy(isLoading = false, isShowCompletePopup = true)
                }
                onFailure(it)
            }
        ).catch {}.collect()
    }

    fun onLeaveExam() {
        if (!state().isShowCompletePopup){
            timerJob?.cancel()
            updateState_ { copy(isLeavePage = true) }
        }
    }
    fun resumeExam() {
        updateState_ { copy(isLeavePage = false) }
        startTimer()
    }
    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }
}
