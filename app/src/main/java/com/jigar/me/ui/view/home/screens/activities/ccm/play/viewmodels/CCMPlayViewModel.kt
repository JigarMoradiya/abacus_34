package com.jigar.me.ui.view.home.screens.activities.ccm.play.viewmodels

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.jigar.me.R
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.data.model.data.*
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.api.SubmitAllExamUseCase
import com.jigar.me.ui.jetpack.core.StatefulViewModelAbacus
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.TextToSpeechManager
import com.jigar.me.utils.*
import com.jigar.me.utils.extensions.convertNumberToWords
import com.jigar.me.utils.extensions.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class CCMPlayViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: AppPreferencesHelper,
    private val ttsManager: TextToSpeechManager,
    private val submitAllExamUseCase: SubmitAllExamUseCase,
) : StatefulViewModelAbacus<CCMPlayUiState>(prefs = prefs,numberOfColumns = 7) {

    override val TAG = "CCMPlayViewModel"
    private var questionJob: Job? = null

    override fun getInitialState() = CCMPlayUiState(
        isAbacusOnLeftHand = isAbacusOnLeftHand,
        totalQuestion = prefs.getCustomParamInt(AppConstants.CCM.totalQuestion, 10),
        questionGap = prefs.getCustomParamInt(AppConstants.CCM.questionGap, 3),
        questionMinLength = prefs.getCustomParamInt(AppConstants.CCM.questionMinLength, 2),
        questionMaxLength = prefs.getCustomParamInt(AppConstants.CCM.questionMaxLength, 3),
        isQuestionSpeak = prefs.getCustomParamBoolean(AppConstants.CCM.isQuestionSpeak, true),
        isQuestionShowNumber = prefs.getCustomParamBoolean(AppConstants.CCM.isQuestionShowNumber, true),
        isQuestionShowWord = prefs.getCustomParamBoolean(AppConstants.CCM.isQuestionShowWord, true),)

    init {
        generateChallenge()
    }

    fun generateChallenge() {
        val challengeData = DataProvider.generateChallengeModeQuestion(state().totalQuestion,state().questionMinLength,state().questionMaxLength)
        val challengeQuestionList = challengeData.questions
        updateState_ {
            copy(customChallengeData = challengeData, questionList = challengeQuestionList, currentIndex = 0, isQuestionPhase = true)
        }
        setChallengeNumber()
    }

    fun setChallengeNumber() {
        val state = state()
        val index = state.currentIndex
        val list = state.questionList

        if (index < list.size) {
            val item = list[index]
            val number = item.question
            val sign = item.sign

            // 🔹 Number text
            val numberPrefix = when (sign) {
                "-" -> "-"
                "+" -> "+"
                else -> ""
            }

            val numberText =
                if (state.isQuestionShowNumber)
                    numberPrefix + number
                else
                    ""

            // 🔹 Word text
            val wordPrefix = when (sign) {
                "-" -> context.getString(R.string.minus) + " "
                "+" -> context.getString(R.string.plus) + " "
                else -> ""
            }

            val wordText = if (state.isQuestionShowWord)
                wordPrefix + context.convertNumberToWords(number)
            else
                ""
            updateState_ {
                copy(
                    currentNumberText = numberText,
                    currentWordText = wordText,
                    isQuestionPhase = true
                )
            }

            // 🔊 Speak or auto-move
            if (state.isQuestionSpeak) {
                speakText(wordText, AppConstants.AbacusScreen.screenTypeCCM)
            } else {
                scheduleNext()
            }

        } else {
            onQuestionsCompleted()
        }
    }

    private fun scheduleNext() {
        questionJob?.cancel()

        questionJob = viewModelScope.launch {
            delay(state().questionGap * 1000L)
            updateState_ { copy(currentIndex = currentIndex + 1) }
            setChallengeNumber()
        }
    }

    private fun onQuestionsCompleted() {
        speakText(context.getString(R.string.set_your_answer), "complete")
        updateState_ {
            copy(isQuestionPhase = false, currentNumberText = "", currentWordText = "")
        }
    }

    private fun speakText(text : String,utteranceId : String) {
        ttsManager.speak(text = text, utteranceId = utteranceId) { utteranceId ->
            if (utteranceId == AppConstants.AbacusScreen.screenTypeCCM) {
                scheduleNext()
            }
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
            AudioPlayerManager.playSoundBtnClick()
            copy(answerDigits = newList)
        }
        updateAbacusFromKeyboard()
    }

    fun eraseKeyboardValue() {
        updateState_ {
            if (answerDigits.isEmpty()) return@updateState_ this
            AudioPlayerManager.playSoundBtnBack()
            copy(answerDigits = answerDigits.dropLast(1))
        }

        updateAbacusFromKeyboard()
    }

    fun clearKeyboard() {
        updateState_ {
            AudioPlayerManager.playAbacusReset()
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

    fun submitAnswer(isFirstAttempt : Boolean = true) {
        if (context.isNetworkAvailable){
            generateRequest()
        }else{
            updateState_ {
                copy(isShowNoInternet = true, noInternetMessage = if (isFirstAttempt) context.getString(R.string.no_internet) else context.getString(R.string.still_no_internet))
            }
        }
    }

    fun generateRequest() = with(state()){
        val userAnswer = abacusCalc.totalValuePair.first
        val isAnswerTrue = userAnswer == (customChallengeData?.answer?:"").toString()
        updateState_ {
            copy(isAnswerTrue = isAnswerTrue)
        }
        val submitExamRequest = SubmitAllExamDataRequest()
        submitExamRequest.apply {
            type = AppConstants.EXAM.type_CCM
            total_set_of_question = 1
            total_numbers_in_set = questionList.size
            gap_between_two_question = questionGap
            question_min_length = questionMinLength
            question_max_length = questionMaxLength
            is_question_speak = isQuestionSpeak
            is_question_show_in_number = isQuestionShowNumber
            is_question_show_in_word = isQuestionShowWord
            no_of_right_answers = if (isAnswerTrue){1}else{0}

            val questionsList : ArrayList<QuestionDataRequest> = arrayListOf()
            questionsList.add(QuestionDataRequest(customChallengeData?.fullQuestion,userAnswer,isAnswerTrue))
            questions = questionsList
            submitExamApi(submitExamRequest)
        }
    }

    fun submitExamApi(submitExamRequest : SubmitAllExamDataRequest) = viewModelScope.launch {
//        updateState_ {
//            copy(isLoading = true)
//        }
//        delay(5000)
//        updateState_ {
//            copy(isLoading = false, isShowCompletePopup = true)
//        }
        submitAllExamUseCase(
            params = submitExamRequest,
            onStart = {
                updateState_ {
                    copy(isLoading = true, isShowNoInternet = false)
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

    fun dismissCompletePopup() {
        updateState_ { copy(isShowCompletePopup = false) }
    }


    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }

}