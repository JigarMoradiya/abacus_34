package com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.viewmodels

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.local.data.ExamProvider.detectFormulaSteps
import com.jigar.me.data.model.data.SubmitAllExamDataRequest
import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus
import com.jigar.me.data.model.dbtable.abacus_all_data.SetProgress
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusTheme
import com.jigar.me.ui.view.jetpack.abacus_base.utils.MathUtils
import com.jigar.me.ui.view.jetpack.core.StatefulViewModelAbacus
import com.jigar.me.ui.view.jetpack.core.repository.abacus_data.AbacusDataRepository
import com.jigar.me.ui.view.jetpack.exam_base.SubmitAllExamUseCase
import com.jigar.me.ui.view.jetpack.utils.TextToSpeechManager
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.convertNumberToWords
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import com.jigar.me.utils.extensions.sumToIntList
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class AbacusDoPracticeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    ttsManager: TextToSpeechManager,
    prefs: AppPreferencesHelper,
    private val abacusDataRepository: AbacusDataRepository,
    private val submitAllExamUseCase: SubmitAllExamUseCase,
    savedStateHandle: SavedStateHandle
) : StatefulViewModelAbacus<AbacusDoPracticeUiState>(ttsManager = ttsManager, prefs = prefs) {

    override val TAG = "AbacusDoPracticeViewModel"

    override fun getInitialState() = AbacusDoPracticeUiState()
    val setId: String? = savedStateHandle["setId"]
    private var timerJob: Job? = null // for timer of set

    init {
        // Direction hint default from prefs
        showDirectionHints = prefs.getCustomParamBoolean(AppConstants.Settings.Setting_direction, true)
        // current abacus theme color model
        updateState_ {
            copy(currentColorPresetModel = AbacusTheme.colorPreset(selectedTheme))
        }

        loadAbacus()
    }

    private fun loadAbacus() = viewModelScope.launch {
        setId?.let {
            combine(
                abacusDataRepository.getAbacus(it),
                abacusDataRepository.getSetDetail(it),
                abacusDataRepository.getSetProgress(it)
            ){ abacusList, setDetail, setProgress ->
                if (abacusList.isNotNullOrEmpty()){
                    // find current abacus all data
                    val restoredIndex = setProgress
                        ?.takeIf { !it.is_set_completed }
                        ?.latest_abacus_id
                        ?.let { latestId ->
                            abacusList.indexOfFirst { it.id == latestId }
                        }
                        ?.takeIf { it >= 0 }

                    // old time restore if there
                    val restoredTime: Long? =
                        setDetail
                            ?.takeIf { it.show_time_setting }
                            ?.let {
                                if (setProgress?.is_set_completed == false)
                                    setProgress.total_time_taken.toLong()
                                else
                                    0L
                            }

                    // current abacus all data
                    val currentIndex = restoredIndex ?: state().currentIndexOfAbacus
                    val currentAbacus = abacusList.getOrNull(currentIndex)
                        ?: abacusList.first()

                    val isStepByStep =  setDetail?.answer_setting == AppConstants.apiParams.answerStepByStep
                    val isFinalAnswer =  setDetail?.answer_setting == AppConstants.apiParams.answerFinalAnswer
                    updateState_ {
                        copy(
                            setDetail = setDetail,
                            setProgress = setProgress,
                            abacus = abacusList,
                            currentIndexOfAbacus = currentIndex,
                            currentAbacus = currentAbacus,
                            currentAbacusFormula = if (isDisplayHelpMessage && isStepByStep) detectFormulaSteps(initial = 0, steps = currentAbacus.question.sumToIntList()) else emptyList(),
                            currentAbacusType = findCurrentAbacusType(currentAbacus),
                            currentSetTime = restoredTime,
                            isStepByStep = isStepByStep,
                            isFinalAnswer = isFinalAnswer,
                            isShowSubmitAnswer = setDetail?.answer_setting == AppConstants.apiParams.answerFormalExam,
                            isNextButtonEnable = setDetail?.answer_setting == AppConstants.apiParams.answerFormalExam,
                            isLoading = false
                        )
                    }

                    // start timer if set has timer on
                    if (state().currentSetTime != null) {
                        startSetTimer()
                    }

                    speakQue()
                    handleMatch()
                }else{
                    // TODO navigation up or empty ui
                }
            }.catch { onFailure(it) }.collect()
        }
    }

    // start timer for set
    fun startSetTimer() {
        state().currentSetTime ?: return
        if (timerJob != null) return

        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1_000)
                updateState_ {
                    copy(currentSetTime = (currentSetTime ?: 0L) + 1)
                }
            }
        }
    }
    fun pauseSetTimer() {
        timerJob?.cancel()
        timerJob = null
    }
    fun stopSetTimer() {
        timerJob?.cancel()
        timerJob = null
    }
    fun persistSetTime() {
        val time = state().currentSetTime ?: return
        val progress = state().setProgress ?: return

        // update progress on database
        progress.total_time_taken = time.toInt()
        updateProgress(progress)
    }



    // speak question
    fun speakQue() = with(state()){
        if (isStepByStep && isAbacusQuestionSpeak){
            currentAbacus?.let{ currentAbacus ->
                if (currentAbacusType == AppConstants.extras_Comman.AbacusTypeNumber){
                    val questionWord = context.convertNumberToWords(currentAbacus.question.toInt())
                    speakOut(questionWord)
                }else if (currentAbacusType == AppConstants.extras_Comman.AbacusTypeAdditionSubtraction){
                    val questionWord = context.convertNumberToWords(currentAbacus.operationStepsStringsArray[currentIndexOfOperation].toInt())
                    speakOut(questionWord)
                }
            }
        }
    }

    // bead value matching logic
    fun handleMatch() {
        val leftInt = abacusCalc.totalValuePair.first.toIntOrNull() ?: 0
        val rightInt = abacusCalc.totalValuePair.second.toIntOrNull() ?: 0
        var currentOperationIndex = state().currentIndexOfOperation
        if (currentOperationIndex > -1){
            state().currentAbacus?.let{ currentAbacus ->
                // check answer is match
                var isAbacusDone = false
                if (state().currentAbacusType == AppConstants.extras_Comman.AbacusTypeNumber){
                    if (rightInt == 0 && currentAbacus.question == leftInt.toString()){
                        isAbacusDone = true
                    }
                }else if (state().isStepByStep){
                    if (state().currentAbacusType == AppConstants.extras_Comman.AbacusTypeAdditionSubtraction){
                        val isLastStep = currentOperationIndex == currentAbacus.operationStepsStringsArray.lastIndex
                        if (isLastStep){
                            if (rightInt == 0 && currentAbacus.finalAnswer.toString() == leftInt.toString()){
                                isAbacusDone = true
                            }
                        }
                    }
                }else if (state().isFinalAnswer){
                    if (rightInt == 0 && currentAbacus.finalAnswer.toString() == leftInt.toString()){
                        isAbacusDone = true
                    }
                }
                if (isAbacusDone) {
                    // remove direction if abacus is done
                    updateRodMovements(arrayListOf())
                    updateState_ {
                        copy(isNextButtonEnable = true,isSumComplete = true, currentIndexOfOperation = -1)
                    }
                } else {
                    // check next direction if abacus is not done
                    updateState_ {
                        copy(isNextButtonEnable = false)
                    }
                    // show direction if enable setting and step by step mode
                    if (showDirectionHints && state().isStepByStep){
                        if (state().currentAbacusType == AppConstants.extras_Comman.AbacusTypeDivision){

                        }else{
                            val right = MathUtils.calculateRodMovements(from = rightInt, to = 0, rods = 6, isForRightRods = true)
                            if (state().currentAbacusType == AppConstants.extras_Comman.AbacusTypeNumber){
                                val rods = max(leftInt.toString().length, currentAbacus.question.length)
                                val left = MathUtils.calculateRodMovements(from = leftInt, to = currentAbacus.question.toInt(), rods = rods, isForRightRods = false)
                                updateRodMovements(left + right)
                            }else if (state().currentAbacusType == AppConstants.extras_Comman.AbacusTypeAdditionSubtraction){
                                if (leftInt.toString() == currentAbacus.operationNumbersArray[currentOperationIndex].toString()){
                                    currentOperationIndex = currentOperationIndex + 1 // current step completed
                                    updateState_ {
                                        copy(currentIndexOfOperation = currentOperationIndex)
                                    }
                                    // speak question when go to next step
                                    speakQue()
                                }
                                val newValue = currentAbacus.operationNumbersArray[currentOperationIndex]
                                val rods = max(leftInt.toString().length, newValue.toString().length)
                                val left = MathUtils.calculateRodMovements(from = leftInt, to = newValue, rods = rods, isForRightRods = false)
                                updateRodMovements(left + right)
                            }
                        }
                    }
                }
            }
        }
    }

    // reset abacus click
    fun resetAbacus() {
        updateState_ {
            copy(isNextButtonEnable = false, isSumComplete = false, currentIndexOfOperation = 0)
        }
        // speak question again when reset abacus
        speakQue()
    }
    // next abacus click
    fun goToNextAbacus() {
        val abacusList = state().abacus
        setId?.let { setId ->
            val setDetail = state().setDetail
            if (state().isShowSubmitAnswer == true){ // formal exam
                if (state().currentIndexOfAbacus == abacusList.lastIndex){ // submit all data on server on last record
                    stopSetTimer() // stop timer once set complete
                }else{ // update user answer only and go next abacus
                    val currentIndex = state().currentIndexOfAbacus
                    viewModelScope.launch {
                        state().currentAbacus?.let {
                            val leftInt = abacusCalc.totalValuePair.first.toIntOrNull() ?: 0
                            val rightInt = abacusCalc.totalValuePair.second.toIntOrNull() ?: 0
                            val userAnswer = if (rightInt == 0){
                                "$leftInt"
                            }else{
                                val toStr = rightInt.toString().padStart(6, '0')
                                val fractionalTrimmed = toStr.trimEnd('0')
                                "$leftInt.$fractionalTrimmed"
                            }
                            abacusDataRepository.updateUserAnswer(it.id,userAnswer)
                            // update user answer in current list
                            updateState_ {
                                copy(
                                    abacus = abacus.mapIndexed { i, item ->
                                        if (i == currentIndex) item.copy(userAnswer = userAnswer)
                                        else item
                                    }
                                )
                            }
                            changeAbacus()
                        }
                    }
                }
            }else{
                if (state().currentIndexOfAbacus == abacusList.lastIndex){ // set complete send on server on last record
                    val submitExamRequest = SubmitAllExamDataRequest()
                    submitExamRequest.apply {
                        val setProgress = state().setProgress
                        stopSetTimer() // stop timer once set complete
                        val retryCounts: Int = if (setProgress == null){
                            1
                        }else if(setProgress.is_set_completed){
                            setProgress.retry_count +1
                        }else{
                            setProgress.retry_count
                        }
                        if (setDetail?.show_time_setting == true){
                            total_time_taken = (state().currentSetTime?:0L).toInt()
                        }
                        retry_count = retryCounts
                        is_set_completed = true
                        abacus_id = null
                        set_id = setId
                        type = setDetail?.answer_setting
                        submitExamApi(submitExamRequest)
                    }
                }else{ // update abacus index on database
                    changeAbacus()
                }
            }
        }
    }
    fun changeAbacus()  {
        setId?.let { setId ->

            val abacusList = state().abacus
            val setDetail = state().setDetail
            var setProgress = state().setProgress
            abacusCalc.resetAbacusData() // reset abacus
            // go next abacus
            val nextIndex = state().currentIndexOfAbacus + 1

            // update progress in database and every 5th abacus send progress on server
            var retryCounts: Int
            if (setProgress == null){
                retryCounts = 1
                setProgress = SetProgress(setId,abacusList[nextIndex].id,false)
            }else if(setProgress.is_set_completed){
                retryCounts =  setProgress.retry_count +1
                setProgress = SetProgress(setId,abacusList[nextIndex].id,false)
            }else{
                retryCounts = setProgress.retry_count
                setProgress.latest_abacus_id = abacusList[nextIndex].id
            }
            setProgress.retry_count = retryCounts
            if (setDetail?.show_time_setting == true){
                setProgress.total_time_taken = (state().currentSetTime?:0L).toInt()
            }

            // update ui state
            updateState_ {
                val nextAbacus = abacusList.getOrNull(nextIndex)
                copy(setProgress = setProgress,isNextButtonEnable = false, currentIndexOfAbacus = nextIndex, currentAbacus = nextAbacus,
                    currentAbacusType = findCurrentAbacusType(nextAbacus), isSumComplete = false, currentIndexOfOperation = 0)
            }

            // submit progress on server on every 5th abacus
            if (nextIndex > 0 && (nextIndex % 5 == 0)){
                val submitExamRequest = SubmitAllExamDataRequest()
                submitExamRequest.apply {
                    retry_count = retryCounts
                    if (state().isShowSubmitAnswer == false){
                        if (setDetail?.show_time_setting == true){
                            total_time_taken = (state().currentSetTime?:0L).toInt()
                        }
                        is_set_completed = false
                        abacus_id = abacusList[nextIndex].id
                        set_id = setId
                        type = setDetail?.answer_setting
                        submitExamApi(submitExamRequest)
                    }
                }
            }
            // update progress on database
            updateProgress(setProgress)
        }
    }
    fun updateProgress(progress: SetProgress) = viewModelScope.launch {
        // update progress in database
        abacusDataRepository.insertSetProgress(listOf(progress))
    }
    fun submitExamApi(submitExamRequest : SubmitAllExamDataRequest) = viewModelScope.launch {
        submitAllExamUseCase(
            params = submitExamRequest,
            onStart = {
                if (submitExamRequest.is_set_completed == true || state().isShowSubmitAnswer == true){
                    updateState_ {
                        copy(isLoading = true)
                    }
                }
            },
            onEachEmit = {},
            onCompletion = {
                if (submitExamRequest.is_set_completed == true || state().isShowSubmitAnswer == true){
                    val setProgress = state().setProgress
                    setProgress?.let{
                        it.is_set_completed = true
                        it.latest_abacus_id = null
                        if (state().setDetail?.show_time_setting == true){
                            it.total_time_taken = (state().currentSetTime?:0L).toInt()
                        }
                        updateProgress(setProgress)
                    }
                    updateState_ {
                        copy(isLoading = false, isShowCompletePopup = true)
                    }
                }
            },
            onError = {
                if (submitExamRequest.is_set_completed == true || state().isShowSubmitAnswer == true){
                    updateState_ {
                        copy(isLoading = false)
                    }
                }
                onFailure(it)
            }
        ).catch {}.collect()
    }
    // find abacus type from current abacus question
    fun findCurrentAbacusType(currentAbacus : Abacus?) : String {
        if (currentAbacus == null){
            return ""
        }
        return when {
            currentAbacus.question.contains("+") || currentAbacus.question.contains("-") -> AppConstants.extras_Comman.AbacusTypeAdditionSubtraction
            currentAbacus.question.contains("*", true) -> AppConstants.extras_Comman.AbacusTypeMultiplication
            currentAbacus.question.contains("/", true) -> AppConstants.extras_Comman.AbacusTypeDivision
            else -> AppConstants.extras_Comman.AbacusTypeNumber
        }
    }

    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }
}