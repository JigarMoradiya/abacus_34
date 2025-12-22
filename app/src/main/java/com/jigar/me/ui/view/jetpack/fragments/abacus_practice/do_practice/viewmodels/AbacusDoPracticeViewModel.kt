package com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.viewmodels

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.model.data.SubmitAllExamDataRequest
import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus
import com.jigar.me.data.model.dbtable.abacus_all_data.SetProgress
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.jetpack.abacus_base.utils.MathUtils
import com.jigar.me.ui.view.jetpack.core.StatefulViewModelAbacus
import com.jigar.me.ui.view.jetpack.core.repository.abacus_data.AbacusDataRepository
import com.jigar.me.ui.view.jetpack.exam_base.SubmitAllExamUseCase
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class AbacusDoPracticeViewModel @Inject constructor(
    @ApplicationContext context: Context,
    prefs: AppPreferencesHelper,
    private val abacusDataRepository: AbacusDataRepository,
    private val submitAllExamUseCase: SubmitAllExamUseCase,
    savedStateHandle: SavedStateHandle
) : StatefulViewModelAbacus<AbacusDoPracticeUiState>(context = context, prefs = prefs) {

    override val TAG = "AbacusDoPracticeViewModel"

    override fun getInitialState() = AbacusDoPracticeUiState()
    val setId: String? = savedStateHandle["setId"]

    init {
        // Direction hint default from prefs
        showDirectionHints = prefs.getCustomParamBoolean(AppConstants.Settings.Setting_direction, true)
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

                    val restoredTime = if (
                        setDetail?.show_time_setting == true &&
                        setProgress?.is_set_completed == false
                    ) {
                        setProgress.total_time_taken.toLong()
                    } else {
                        null
                    }

                    // current abacus all data
                    val currentIndex = restoredIndex ?: state().currentIndexOfAbacus
                    val currentAbacus = abacusList.getOrNull(currentIndex)
                        ?: abacusList.first()



                    updateState_ {
                        copy(
                            setDetail = setDetail,
                            setProgress = setProgress,
                            abacus = abacusList,
                            currentIndexOfAbacus = currentIndex,
                            currentAbacus = currentAbacus,
                            currentAbacusType = findCurrentAbacusType(currentAbacus),
                            currentSetTime = restoredTime,
                            isStepByStep = setDetail?.answer_setting == AppConstants.apiParams.answerSettingStepByStep,
                            isShowSubmitAnswer = setDetail?.answer_setting == AppConstants.apiParams.answerFormalAnswer,
                            isNextButtonEnable = setDetail?.answer_setting == AppConstants.apiParams.answerFormalAnswer,
                            isLoading = false
                        )
                    }
                    handleMatch()
                }else{
                    // TODO
                }
            }.catch { onFailure(it) }.collect()
        }
    }

    // --------- Matching logic (guided mode) ----------
    fun handleMatch() {
        val leftInt = abacusCalc.totalValuePair.first.toIntOrNull() ?: 0
        val rightInt = abacusCalc.totalValuePair.second.toIntOrNull() ?: 0

        state().currentAbacus?.let{ currentAbacus ->
            // check answer is match
            var isAbacusDone = false
            if (state().currentAbacusType == AppConstants.extras_Comman.AbacusTypeNumber){
                if (rightInt == 0 && currentAbacus.question == leftInt.toString()){
                    isAbacusDone = true
                }
            }
            if (isAbacusDone) {
                // remove direction if abacus is done
                updateRodMovements(arrayListOf())
                updateState_ {
                    copy(isNextButtonEnable = true)
                }
            } else {
                // check next direction if abacus is not done
                updateState_ {
                    copy(isNextButtonEnable = false)
                }
                // show direction if enable setting and step by step mode
                if (showDirectionHints && state().isStepByStep == true){
                    if (state().currentAbacusType == AppConstants.extras_Comman.AbacusTypeNumber){
                        val rods = max(leftInt.toString().length, currentAbacus.question.length)
                        val left = MathUtils.calculateRodMovements(from = leftInt, to = currentAbacus.question.toInt(), rods = rods, isForRightRods = false)
                        val right = MathUtils.calculateRodMovements(from = rightInt, to = 0, rods = 6, isForRightRods = true)
                        updateRodMovements(left + right)
                    }
                }
            }
        }
    }

    // next abacus click
    fun goToNextAbacus() {
        val abacusList = state().abacus
        setId?.let { setId ->
            val submitExamRequest = SubmitAllExamDataRequest()
            with(submitExamRequest) {
                val setDetail = state().setDetail
                var setProgress = state().setProgress
                if (state().currentIndexOfAbacus == abacusList.lastIndex){
                    // complete set TODO
                    if (state().isShowSubmitAnswer == true){

                    }else{
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
                }else{
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
                        copy(setProgress = setProgress,isNextButtonEnable = false, currentIndexOfAbacus = nextIndex, currentAbacus = nextAbacus, currentAbacusType = findCurrentAbacusType(nextAbacus))
                    }
                    // find direction for new question
                    handleMatch()

                    // submit progress on server on every 5th abacus
                    if (nextIndex > 0 && (nextIndex % 5 == 0)){
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
                    // update progress on database
                    updateProgress(setProgress)
                }
            }
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
            onEachEmit = {

            },
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