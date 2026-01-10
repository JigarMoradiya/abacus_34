package com.jigar.me.ui.view.jetpack.fragments.exercise.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.jigar.me.BuildConfig
import com.jigar.me.R
import com.jigar.me.data.model.data.QuestionDataRequest
import com.jigar.me.data.model.data.SubmitAllExamDataRequest
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusTheme
import com.jigar.me.ui.view.jetpack.core.StatefulViewModelAbacus
import com.jigar.me.ui.view.jetpack.exam_base.SubmitAllExamUseCase
import com.jigar.me.ui.view.jetpack.fragments.exercise.exercise_generator.ExerciseGenerator
import com.jigar.me.ui.view.jetpack.fragments.exercise.exercise_generator.GridItemModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.PlaySound
import com.jigar.me.utils.extensions.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    prefs: AppPreferencesHelper,
    private val submitAllExamUseCase: SubmitAllExamUseCase,
) : StatefulViewModelAbacus<ExerciseUiState>(prefs = prefs,numberOfColumns = 7) {

    override val TAG = "ExerciseViewModel"

    override fun getInitialState() = ExerciseUiState(isAbacusOnLeftHand = isAbacusOnLeftHand,currentColorPresetModel = AbacusTheme.colorPreset(selectedTheme))
    private var timerJob: Job? = null

    fun generateExercise() {
        val item = state().selectedItems[state().currentPage]
        item?.let{
            val exerciseList = when (state().currentPage) {
                1 -> {
                    ExerciseGenerator.generateMultiplicationExercise(item)
                }
                2 -> {
                    ExerciseGenerator.generateDivisionExercise(item)
                }
                else -> {
                    ExerciseGenerator.generateAddSubQuestions(item)
                }
            }
            abacusCalc.resetAbacusData()

            updateState_ {
                copy(exerciseQuestionList = exerciseList, isExerciseStarted = true, currentQueIndex = 0,elapsedSeconds = item.maxTime,
                    isShowCompletePopup = false, totalCorrect = 0) // this param use when re generate exercise
            }
            timerJob?.cancel()
            startTimer()
        }
    }

    fun closeExercise() {
        abacusCalc.resetAbacusData()
        updateState_ {
            copy(isExerciseStarted = false, isShowCompletePopup = false,isLeavePage = false)
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (isActive && state().elapsedSeconds > 0) {
                delay(1000)
                updateState_ {
                    copy(elapsedSeconds = elapsedSeconds - 1)
                }
            }

            // ⏰ Timer finished
            onTimerFinished()
        }
    }

    private fun onTimerFinished() {
        timerJob?.cancel()
        completeExercise()
    }


    /** Called when pager page changes */
    fun onPageChanged(page: Int) {
        updateState_ {
            // if already selected → do nothing
            if (selectedItems.containsKey(page)) {
                copy(currentPage = page)
            } else {
                // 🔥 select first grid item by default
                val firstItem = exercises.getOrNull(page)?.gridItems?.firstOrNull()
                if (firstItem != null) {
                    copy(
                        currentPage = page,
                        selectedItems = selectedItems + (page to firstItem)
                    )
                } else {
                    copy(currentPage = page)
                }
            }
        }
    }

    /** User taps a grid item */
    fun onGridItemSelected(page: Int, item: GridItemModel) {
        updateState_ {
            copy(
                selectedItems = selectedItems + (page to item)
            )
        }
    }


    fun addKeyboardValue(value: String) {
        updateState_ {
            if (answerDigits.size >= 7) return@updateState_ this
            val newList = answerDigits.toMutableList()
            if (value == "0") {
                if (newList.isNotEmpty()) newList.add(value)
            } else {
                newList.add(value)
            }
            PlaySound.playClick(context)
            copy(answerDigits = newList)
        }
        updateAbacusFromKeyboard()
    }

    fun eraseKeyboardValue() {
        updateState_ {
            if (answerDigits.isEmpty()) return@updateState_ this
            PlaySound.playClear(context)
            copy(answerDigits = answerDigits.dropLast(1))
        }
        updateAbacusFromKeyboard()
    }

    fun clearKeyboard() {
        updateState_ {
            PlaySound.playBeadReset(context)
            copy(answerDigits = emptyList())
        }
        updateAbacusFromKeyboard()
    }

    private fun updateAbacusFromKeyboard() {
        if (!state().answerDigits.isEmpty()){
            val text = state().answerDigits.joinToString("")
            abacusCalc.setAbacusValueFromString(text, false)
        }else{
            abacusCalc.resetAbacusData()
        }
    }

    fun handleMatch() {
        val leftString = abacusCalc.totalValuePair.first
        val answerDigits = leftString
            .filter { it.isDigit() }
            .map { it.toString() }
        updateState_ {
            copy(answerDigits = answerDigits,answerText = leftString)
        }
    }

    fun nextQuestion() {
        val questionList = state().exerciseQuestionList.toMutableList()
        val isCorrectAnswer = state().answerText.toInt() == questionList[state().currentQueIndex].answer
        val updatedQuestionList = questionList.apply {
            val current = this[state().currentQueIndex]
            this[state().currentQueIndex] = current.copy(userAnswer = state().answerText.toInt(),isCorrect = isCorrectAnswer)
        }

        if (state().currentQueIndex >= state().exerciseQuestionList.lastIndex) {
            updateState_ {
                copy(exerciseQuestionList = updatedQuestionList,totalCorrect = if (isCorrectAnswer) totalCorrect + 1 else totalCorrect)
            }
            timerJob?.cancel()
            completeExercise()
        }else{
            abacusCalc.resetAbacusData()
            updateState_ {
                copy(currentQueIndex = currentQueIndex + 1, exerciseQuestionList = updatedQuestionList,totalCorrect = if (isCorrectAnswer) totalCorrect + 1 else totalCorrect)
            }
        }
    }
    fun completeExercise(isFirstAttempt : Boolean = true) {
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
            val currentPage = state().currentPage
            val selectedPage = state().exercises[currentPage]
            val selectedItem = state().selectedItems[currentPage]
            val questionList = state().exerciseQuestionList

            type = AppConstants.EXAM.type_Exercise
            category = selectedPage.title
            label = (selectedItem?.selectedItemDescriptionApi(currentPage) ?: "")
            val maxTime = (selectedItem?.maxTime?:0)
            allowed_max_time = maxTime
            total_time_taken = maxTime - state().elapsedSeconds
            no_of_questions = questionList.size
            no_of_right_answers = state().totalCorrect
            val questionsList : ArrayList<Any> = arrayListOf()
            questionList.map {
                questionsList.add(QuestionDataRequest(it.que,if (it.userAnswer == null){null}else{it.userAnswer.toString()},it.isCorrect))
            }
            questions = questionsList
        }
        if (BuildConfig.DEBUG){
            updateState_ {
                copy(submitExamRequest = submitExamRequest,isShowCompletePopup = true)
            }
        }else{
            submitExamApi(submitExamRequest)
        }
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
    fun onLeaveExercise() {
        if (!state().isShowCompletePopup){
            timerJob?.cancel()
            updateState_ { copy(isLeavePage = true) }
        }
    }
    fun resumeExercise() {
        updateState_ { copy(isLeavePage = false) }
        startTimer()
    }

    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }
}