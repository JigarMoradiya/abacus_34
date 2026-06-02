package com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.viewmodels

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jigar.me.R
import com.jigar.me.data.local.data.ExamProvider.AbacusFormulaType
import com.jigar.me.data.local.data.ExamProvider.FormulaStep
import com.jigar.me.data.local.data.ExamProvider.detectFormulaSteps
import com.jigar.me.data.local.data.RodMovement
import com.jigar.me.data.model.data.QuestionDataRequest
import com.jigar.me.data.model.data.SubmitAllExamDataRequest
import com.jigar.me.data.model.dbtable.abacus_all_data.SetProgress
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.api.SubmitAllExamUseCase
import com.jigar.me.ui.jetpack.core.StatefulViewModelAbacus
import com.jigar.me.ui.jetpack.core.repository.abacus_data.AbacusDataRepository
import com.jigar.me.ui.jetpack.utils.TextToSpeechManager
import com.jigar.me.ui.view.base.abacus_base.AbacusTheme
import com.jigar.me.ui.view.base.abacus_base.utils.MathUtils
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class AbacusDoPracticeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ttsManager: TextToSpeechManager,
    prefs: AppPreferencesHelper,
    private val abacusDataRepository: AbacusDataRepository,
    private val submitAllExamUseCase: SubmitAllExamUseCase,
    savedStateHandle: SavedStateHandle
) : StatefulViewModelAbacus<AbacusDoPracticeUiState>(prefs = prefs,numberOfColumns = 13) {

    override val TAG = "AbacusDoPracticeViewModel"

    private val saveResults: Boolean = savedStateHandle.get<Boolean>("saveResults") ?: true
    override fun getInitialState() = AbacusDoPracticeUiState(saveResults = saveResults)
    val setId: String? = savedStateHandle["setId"]
    private var timerJob: Job? = null // for timer of set
    init {
        // Direction hint default from prefs
        showDirectionHints = prefs.getCustomParamBoolean(AppConstants.Settings.Setting_direction, true)
        // current abacus theme color model
        updateState_ {
            copy(currentColorPresetModel = AbacusTheme.colorPreset(selectedTheme))
        }

        initialLoad()
    }

    private fun initialLoad() = viewModelScope.launch {
        setId?.let {
            val abacusList = abacusDataRepository.getAbacus(setId).first()
            val setDetail = abacusDataRepository.getSetDetail(setId).first()
            val setProgress = abacusDataRepository.getSetProgress(setId).first()
            if (abacusList.isEmpty()) return@launch

            val restoredIndex = setProgress
                ?.takeIf { !it.is_set_completed }
                ?.latest_abacus_id
                ?.let { id -> abacusList.indexOfFirst { it.id == id } }
                ?.takeIf { it >= 0 }
                ?: 0

            val restoredTime = setDetail
                ?.takeIf { it.show_time_setting }
                ?.let {
                    if (setProgress?.is_set_completed == false)
                        setProgress.total_time_taken.toLong()
                    else 0L
                }

            val currentAbacus = abacusList[restoredIndex]
            val isStepByStep = setDetail?.answer_setting == AppConstants.apiParams.answerStepByStep
            val isFinalAnswer = setDetail?.answer_setting == AppConstants.apiParams.answerFinalAnswer
            val isFormalExam = setDetail?.answer_setting == AppConstants.apiParams.answerFormalExam
            val abacusType = currentAbacus.findCurrentAbacusType()
            updateState_ {
                copy(
                    setDetail = setDetail,
                    setProgress = setProgress,
                    abacus = abacusList,
                    currentIndexOfAbacus = restoredIndex,
                    currentAbacus = currentAbacus,
                    currentAbacusType = abacusType,
                    currentAbacusFormula =
                        if (isDisplayHelpMessage && isStepByStep && abacusType == AppConstants.extras_Comman.AbacusTypeAdditionSubtraction)
                            detectFormulaSteps(initial = 0, steps = currentAbacus.question.sumToIntList())
                        else emptyList(),
                    currentSetTime = restoredTime,
                    isStepByStep = isStepByStep,
                    isFinalAnswer = isFinalAnswer,
                    isShowSubmitAnswer = isFormalExam,
                    isNextButtonEnable = isFormalExam,
                    isLoading = false,
                )
            }


            if (restoredTime != null) startSetTimer()

            preSetAbacus() // pre set data for division
            speakQue() // speak question
            handleMatch() // find direction and check abacus is completed or not
            observeOperationIndex() // observer for currentIndexOfOperation
        }
    }
    private fun preSetAbacus() {
        // set dividend on abacus
        if (state().currentAbacusType == AppConstants.extras_Comman.AbacusTypeDivision){
            abacusCalc.setAbacusValueFromString((state().currentAbacus?.dividend?:"0").toString(), true)
        }
    }

    // observer of currentIndexOfOperation for formula find of Multiplication and Division
    private fun observeOperationIndex() {
        viewModelScope.launch {
            uiState.map { it.currentIndexOfOperation }
                .distinctUntilChanged()
                .collect { newIndex ->
                    if (isDisplayHelpMessage && state().isStepByStep){
                        state().currentAbacus?.let{ currentAbacus ->
                            val currentOperationIndex = state().currentIndexOfOperation
                            if (currentOperationIndex > -1){
                                if (state().currentAbacusType == AppConstants.extras_Comman.AbacusTypeMultiplication){
                                    val leftInt = abacusCalc.totalValuePair.first.toIntOrNull() ?: 0
                                    val newValue = currentAbacus.eachStepProduct[currentOperationIndex]
                                    val ques = leftInt.toString()+"+"+(newValue - leftInt)
                                    val formulaList = detectFormulaSteps(initial = 0, steps = ques.sumToIntList())
                                    if (currentAbacus.num2.isNotNullOrEmpty()){
                                        val tableOf = currentAbacus.num2[state().currentIndexNum2]
                                        val index = currentAbacus.num1[state().currentIndexNum1]
                                        val formattedResult = String.format(Locale.US, "%02d", tableOf * index)
                                        val mulTable = "$index x $tableOf = $formattedResult"
                                        val multiplicationFormula = FormulaStep(0, mulTable, AbacusFormulaType.Multiplication.description)
                                        updateState_ {
                                            copy(currentAbacusFormula = listOf(multiplicationFormula) + formulaList)
                                        }
                                    }
                                }else if (state().currentAbacusType == AppConstants.extras_Comman.AbacusTypeDivision){
                                    val leftInt = abacusCalc.totalValuePair.first.toIntOrNull() ?: 0
                                    val toValue = currentAbacus.eachStepQuotient[currentOperationIndex] - leftInt
                                    val que1 = "$leftInt+$toValue"
                                    val formulaList1 = detectFormulaSteps(initial = 0, steps= que1.sumToIntList())

                                    val newDividend = currentAbacus.dividend
                                    val toValue2 = currentAbacus.eachStepRemainder[currentOperationIndex] - newDividend
                                    val que2 = "$newDividend+$toValue2"
                                    val formulaList2 = detectFormulaSteps(initial = 0, steps= que2.sumToIntList())

                                    val tableOf = currentAbacus.divisor
                                    val index = currentAbacus.eachStepQuotientDigitsForMultiplicationTable[currentOperationIndex]
                                    val formattedResult = String.format(Locale.US, "%02d", tableOf * index)
                                    val mulTable = "$index x $tableOf = $formattedResult"
                                    val multiplicationFormula = FormulaStep(0, mulTable, AbacusFormulaType.Multiplication.description)

                                    updateState_ {
                                        copy(currentAbacusFormula = listOf(multiplicationFormula) + formulaList1 + formulaList2)
                                    }
                                }
                            }else{
                                updateState_ {
                                    copy(currentAbacusFormula = arrayListOf())
                                }
                            }
                        }
                    }
                }
        }
    }
    // start timer for set
    fun startSetTimer() {
        if (timerJob != null) return
        state().currentSetTime ?: return

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
                when (currentAbacusType) {
                    AppConstants.extras_Comman.AbacusTypeNumber -> {
                        val questionWord = context.convertNumberToWords(currentAbacus.question.toInt())
                        ttsManager.speak(questionWord)
                    }
                    AppConstants.extras_Comman.AbacusTypeAdditionSubtraction -> {
                        val questionWord = context.convertNumberToWords(currentAbacus.operationStepsStringsArray[currentIndexOfOperation].toInt())
                        ttsManager.speak(questionWord)
                    }
                    AppConstants.extras_Comman.AbacusTypeMultiplication -> {
                        val num1Int = currentAbacus.num1.joinToString("").toIntOrNull() ?: 0
                        val num2Int = currentAbacus.num2.joinToString("").toIntOrNull() ?: 0
                        val num1Str = context.convertNumberToWords(num1Int)
                        val num2Str = context.convertNumberToWords(num2Int)
                        val questionWord = String.format(context.getString(R.string.speak_multiply_by),num1Str,num2Str)
                        ttsManager.speak(questionWord)
                    }
                    AppConstants.extras_Comman.AbacusTypeDivision -> {
                        val dividend = context.convertNumberToWords(currentAbacus.dividend)
                        val divisor = context.convertNumberToWords(currentAbacus.divisor)
                        val questionWord = String.format(context.getString(R.string.speak_divide_by),dividend,divisor)
                        ttsManager.speak(questionWord)
                    }
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
                    }else if (state().currentAbacusType == AppConstants.extras_Comman.AbacusTypeMultiplication){
                        if (leftInt == currentAbacus.eachStepProduct[currentOperationIndex] && rightInt == 0 && currentAbacus.finalAnswer.toString() == leftInt.toString()){
                            isAbacusDone = true
                        }
                    }else if (state().currentAbacusType == AppConstants.extras_Comman.AbacusTypeDivision){
                        if (currentOperationIndex < currentAbacus.eachStepQuotient.size &&
                            currentOperationIndex < currentAbacus.eachStepRemainder.size &&
                            leftInt == currentAbacus.eachStepQuotient[currentOperationIndex] &&
                            rightInt == currentAbacus.eachStepRemainder[currentOperationIndex]
                        ){
                            if (leftInt == currentAbacus.quotient && rightInt == currentAbacus.remainder) {
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
                        copy(isNextButtonEnable = true,isSumComplete = true, currentIndexOfOperation = -1,currentIndexNum1 = -1,currentIndexNum2 = -1)
                    }
                } else {
                    // check next direction if abacus is not done
                    updateState_ {
                        copy(isNextButtonEnable = false)
                    }
                    // show direction if enable setting and step by step mode
                    if (state().isStepByStep){
                        when (state().currentAbacusType) {
                            AppConstants.extras_Comman.AbacusTypeDivision -> {
                                if (currentOperationIndex < currentAbacus.eachStepQuotient.size &&
                                    currentOperationIndex < currentAbacus.eachStepRemainder.size &&
                                    leftInt == currentAbacus.eachStepQuotient[currentOperationIndex] &&
                                    rightInt == currentAbacus.eachStepRemainder[currentOperationIndex]
                                ) {
                                    currentOperationIndex += 1
                                    // Skip duplicate steps (like 300 → 300)
                                    while (
                                        currentOperationIndex < currentAbacus.eachStepQuotient.size - 1 &&
                                        currentOperationIndex > 0 &&
                                        currentAbacus.eachStepQuotient[currentOperationIndex] ==
                                        currentAbacus.eachStepQuotient[currentOperationIndex - 1]
                                    ) {
                                        currentOperationIndex++
                                    }

                                    updateState_ {
                                        copy(currentIndexOfOperation = currentOperationIndex)
                                    }
                                }
                            }
                            AppConstants.extras_Comman.AbacusTypeAdditionSubtraction -> {
                                if (leftInt.toString() == currentAbacus.operationNumbersArray[currentOperationIndex].toString()) {
                                    currentOperationIndex += 1 // current step completed
                                    updateState_ {
                                        copy(currentIndexOfOperation = currentOperationIndex)
                                    }
                                    // speak question when go to next step
                                    speakQue()
                                }
                            }
                            AppConstants.extras_Comman.AbacusTypeMultiplication -> {
                                val requiredValue = currentAbacus.eachStepProduct[currentOperationIndex]
                                if (leftInt == requiredValue && rightInt == 0) {
                                    currentOperationIndex += 1
                                    var currentIndexNum1 = state().currentIndexNum1
                                    var currentIndexNum2 = state().currentIndexNum2
                                    do {
                                        if (currentIndexNum1 == currentAbacus.num1.size - 1) {
                                            currentIndexNum1 = 0
                                            currentIndexNum2 += 1
                                        } else {
                                            currentIndexNum1 += 1
                                        }

                                        if (currentIndexNum2 >= currentAbacus.num2.size) {
                                            break
                                        }
                                    } while (
                                        currentOperationIndex < currentAbacus.eachStepProduct.size && (currentAbacus.num1[currentIndexNum1] == 0 || currentAbacus.num2[currentIndexNum2] == 0)
                                    )
                                    updateState_ {
                                        copy(currentIndexNum1 = currentIndexNum1, currentIndexNum2 = currentIndexNum2, currentIndexOfOperation = currentOperationIndex)
                                    }
                                }
                            }
                        }
                        if (showDirectionHints){
                            if (state().currentAbacusType == AppConstants.extras_Comman.AbacusTypeDivision){
                                val newValue = currentAbacus.eachStepQuotient[currentOperationIndex]
                                val rods = max(leftInt.toString().length, newValue.toString().length)
                                val left = MathUtils.calculateRodMovements(from = leftInt, to = newValue, rods = rods, isForRightRods = false)
                                var rightList : List<RodMovement> = arrayListOf()
                                if (rightInt > 0) {
                                    val newValueRemainder = currentAbacus.eachStepRemainder.getOrNull(currentOperationIndex)
                                    if (newValueRemainder != null) {
                                        rightList = MathUtils.calculateRodMovements(from = rightInt, to = newValueRemainder, rods = 6, isForRightRods = true)
                                    }
                                } else {
                                    if (!state().isSumComplete) {
                                        val newValueRemainder = currentAbacus.eachStepRemainder.getOrNull(currentOperationIndex)
                                        if (newValueRemainder != null) {
                                            rightList = MathUtils.calculateRodMovements(from = 0, to = newValueRemainder, rods = 6, isForRightRods = true)
                                        }
                                    }
                                }
                                updateRodMovements(left + rightList)
                            }else{
                                val right = MathUtils.calculateRodMovements(from = rightInt, to = 0, rods = 6, isForRightRods = true)
                                when (state().currentAbacusType) {
                                    AppConstants.extras_Comman.AbacusTypeNumber -> {
                                        val rods = max(leftInt.toString().length, currentAbacus.question.length)
                                        val left = MathUtils.calculateRodMovements(from = leftInt, to = currentAbacus.question.toInt(), rods = rods, isForRightRods = false)
                                        updateRodMovements(left + right)
                                    }
                                    AppConstants.extras_Comman.AbacusTypeAdditionSubtraction -> {
                                        val newValue = currentAbacus.operationNumbersArray[currentOperationIndex]
                                        val rods = max(leftInt.toString().length, newValue.toString().length)
                                        val left = MathUtils.calculateRodMovements(from = leftInt, to = newValue, rods = rods, isForRightRods = false)
                                        updateRodMovements(left + right)
                                    }
                                    AppConstants.extras_Comman.AbacusTypeMultiplication -> {
                                        val newValue = currentAbacus.eachStepProduct[currentOperationIndex]
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
        }
    }

    // reset abacus click
    fun resetAbacus() {
        preSetAbacus() // reset pre set abacus data for division
        updateState_ {
            copy(isNextButtonEnable = false, isSumComplete = false, currentIndexOfOperation = 0, currentIndexNum1 = 0, currentIndexNum2 = 0)
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
                // current abacus's answer update
                val currentIndex = state().currentIndexOfAbacus
                state().currentAbacus?.let {
                    val leftInt = abacusCalc.totalValuePair.first.toIntOrNull() ?: 0
                    val rightInt = abacusCalc.totalValuePair.second.toIntOrNull() ?: 0
                    val userAnswer = if (rightInt == 0 || state().currentAbacusType == AppConstants.extras_Comman.AbacusTypeDivision){
                        "$leftInt"
                    }else{
                        val toStr = rightInt.toString().padStart(6, '0')
                        val fractionalTrimmed = toStr.trimEnd('0')
                        "$leftInt.$fractionalTrimmed"
                    }
                    // update user answer in current list
                    updateState_ {
                        copy(
                            abacus = abacus.mapIndexed { i, item ->
                                if (i == currentIndex) item.copy(userAnswer = userAnswer)
                                else item
                            }
                        )
                    }
                    // update answer in database
                    if (state().currentIndexOfAbacus != abacusList.lastIndex){
                        viewModelScope.launch {
                            abacusDataRepository.updateUserAnswer(it.id,userAnswer)
                        }
                    }
                }
                if (state().currentIndexOfAbacus == abacusList.lastIndex){ // submit all data on server on last record
                    stopSetTimer() // stop timer once set complete
                    val submitExamRequest = SubmitAllExamDataRequest()
                    submitExamRequest.apply {
                        set_id = setId
                        type = setDetail?.answer_setting
                        if (setDetail?.show_time_setting == true){
                            total_time_taken = (state().currentSetTime?:0L).toInt()
                        }
                        no_of_questions = abacusList.size
                        val questionsList : ArrayList<QuestionDataRequest> = arrayListOf()
                        var rightAnswerCount = 0
                        state().abacus.map {
                            val question = it.question
                            val isRightAnswer = (it.finalAnswer.toString() == it.userAnswer)
                            if (isRightAnswer){
                                rightAnswerCount++
                            }
                            questionsList.add(QuestionDataRequest(question,it.userAnswer,isRightAnswer))
                        }
                        no_of_right_answers = rightAnswerCount
                        questions = questionsList

                        val setProgress = state().setProgress
                        val retryCounts: Int = if (setProgress == null){
                            1
                        }else if(setProgress.is_set_completed){
                            setProgress.retry_count + 1
                        }else{
                            setProgress.retry_count
                        }
                        retry_count = retryCounts

                        is_set_completed = true
                        if (!saveResults) {
                            setProgress?.let {
                                it.is_set_completed = true
                                it.latest_abacus_id = null
                                if (setDetail?.show_time_setting == true) it.total_time_taken = (state().currentSetTime ?: 0L).toInt()
                                updateProgress(it)
                            }
                            updateState_ { copy(submitExerciseRequest = submitExamRequest, isShowCompletePopup = true) }
                        } else {
                            submitExamApi(submitExamRequest)
                        }
                    }
                }else{ // update user answer only and go next abacus
                    changeAbacus()
                }
            }else{
                if (state().currentIndexOfAbacus == abacusList.lastIndex){ // set complete send on server on last record
                    val submitExamRequest = SubmitAllExamDataRequest()
                    submitExamRequest.apply {
                        stopSetTimer() // stop timer once set complete
                        val setProgress = state().setProgress
                        val retryCounts: Int = if (setProgress == null){
                            1
                        }else if(setProgress.is_set_completed){
                            setProgress.retry_count + 1
                        }else{
                            setProgress.retry_count
                        }
                        retry_count = retryCounts
                        is_set_completed = true
                        if (setDetail?.show_time_setting == true){
                            total_time_taken = (state().currentSetTime?:0L).toInt()
                        }
                        abacus_id = null
                        set_id = setId
                        type = setDetail?.answer_setting
                        if (!saveResults) {
                            setProgress?.let {
                                it.is_set_completed = true
                                it.latest_abacus_id = null
                                if (setDetail?.show_time_setting == true) it.total_time_taken = (state().currentSetTime ?: 0L).toInt()
                                updateProgress(it)
                            }
                            updateState_ { copy(submitExerciseRequest = submitExamRequest, isShowCompletePopup = true) }
                        } else {
                            submitExamApi(submitExamRequest)
                        }
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
            val nextAbacus = abacusList.getOrNull(nextIndex)
            nextAbacus?.let {
                val abacusType = it.findCurrentAbacusType()
                updateState_ {
                    copy(setProgress = setProgress,isNextButtonEnable = false, currentIndexOfAbacus = nextIndex, currentAbacus = it,
                        currentAbacusType = abacusType, isSumComplete = false, currentIndexOfOperation = 0,
                        currentIndexNum1 = 0, currentIndexNum2 = 0,
                        currentAbacusFormula = if (isDisplayHelpMessage && isStepByStep && abacusType == AppConstants.extras_Comman.AbacusTypeAdditionSubtraction)
                            detectFormulaSteps(initial = 0, steps = it.question.sumToIntList()) else emptyList()
                    )
                }
            }

            speakQue() // speak question when change
            preSetAbacus() // pre set abacus data for next division question

            // submit progress on server on every 5th abacus
            if (nextIndex > 0 && (nextIndex % 5 == 0)){
                val submitExamRequest = SubmitAllExamDataRequest()
                submitExamRequest.apply {
                    retry_count = retryCounts
                    if (state().isShowSubmitAnswer == false && saveResults){
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
        abacusDataRepository.insertSetProgress(listOf(progress))
    }
    fun submitExamApi(submitExamRequest : SubmitAllExamDataRequest) = viewModelScope.launch {

        submitAllExamUseCase(
            params = submitExamRequest,
            onStart = {
                if (submitExamRequest.is_set_completed == true || state().isShowSubmitAnswer == true){
                    updateState_ {
                        copy(isLoading = true,submitExerciseRequest = submitExamRequest)
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

    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }
}