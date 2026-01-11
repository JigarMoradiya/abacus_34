package com.jigar.me.ui.view.jetpack.fragments.reports.viewmodels

import androidx.lifecycle.viewModelScope
import com.jigar.me.data.model.data.AllExamData
import com.jigar.me.data.model.data.FetchReportHistoryRequest
import com.jigar.me.data.model.data.toSubmitRequest
import com.jigar.me.ui.view.jetpack.api.GetReportHistoryUseCase
import com.jigar.me.ui.view.jetpack.core.StatefulViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportHistoryViewModel @Inject constructor(
    private val getReportHistory: GetReportHistoryUseCase,
) : StatefulViewModel<ReportHistoryUiState>() {

    override val TAG = "ReportHistoryViewModel"

    override fun getInitialState() = ReportHistoryUiState()

    init {
        getReportHistory()
    }

    private fun getReportHistory() = viewModelScope.launch {
        val from = state().from
        val type = state().type
        val fromDate = state().from_date
        val toDate = state().to_date
        getReportHistory(
            params = FetchReportHistoryRequest(type,fromDate,toDate,from),
            onStart = {
                if (from == 0){
                    updateState_ {
                        copy(isLoading = true)
                    }
                }
            },
            onEachEmit = { data ->
                if (from == 0){
                    updateState_ {
                        copy(isLoading = false)
                    }
                }
                updateState_ {
                    copy(totalRecord = data.totalRecord,list = data.list)
                }
            },
            onError = {
                if (from == 0){
                    updateState_ {
                        copy(isLoading = false)
                    }
                }
                onFailure(it)
            }
        ).catch {}.collect()
    }

    fun showExerciseExamDialog(it: AllExamData) {
        updateState_ {
            copy(exerciseExamRequest = it.toSubmitRequest(),isShowExerciseExamPopup = true)
        }
    }

    fun closeExercise() {
        updateState_ {
            copy(exerciseExamRequest = null,isShowExerciseExamPopup = false)
        }
    }

    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }


}