package com.jigar.me.ui.view.jetpack.fragments.reports.viewmodels

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.jigar.me.R
import com.jigar.me.data.model.data.AllExamData
import com.jigar.me.data.model.data.FetchReportHistoryRequest
import com.jigar.me.data.model.data.toSubmitRequest
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.jetpack.api.GetReportHistoryUseCase
import com.jigar.me.ui.view.jetpack.core.StatefulViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportHistoryViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: AppPreferencesHelper,
    private val getReportHistory: GetReportHistoryUseCase,
) : StatefulViewModel<ReportHistoryUiState>() {

    override val TAG = "ReportHistoryViewModel"

    val list = arrayListOf<ReportFilterItem>().apply {
        add(ReportFilterItem(label = context.getString(R.string.select_report_type), apiValue = null))
        add(ReportFilterItem(label = context.getString(R.string.all_report), apiValue = null))
        add(ReportFilterItem(label = context.getString(R.string.exam), apiValue = AppConstants.EXAM.type_Exam))
        add(ReportFilterItem(label = context.getString(R.string.exercise), apiValue = AppConstants.EXAM.type_Exercise))
        add(ReportFilterItem(label = context.getString(R.string.custom_challenge_mode), apiValue = AppConstants.EXAM.type_CCM))
        add(ReportFilterItem(label = context.getString(R.string.practice_set), apiValue = AppConstants.apiParams.answerFormalExam))
    }

    // get selected theme
    val dateFilterList = buildDateFilterList(context)
    override fun getInitialState() = ReportHistoryUiState(selectedTheme = prefs.getCustomParam(AppConstants.Settings.Theam, AppConstants.Settings.theam_Default),filterList = list, selectedFilter = list[1], dateFilterList = dateFilterList, selectedDateFilter = dateFilterList.first())

    init {
        callApi()
    }

    fun onFilterClick() {
        updateState_ { copy(isDropdownExpanded = true) }
    }

    fun onFilterDismiss() {
        updateState_ { copy(isDropdownExpanded = false) }
    }

    // report type filter change
    fun onFilterSelected(item: ReportFilterItem) {
        // same logic as XML
        if (item.label != context.getString(R.string.select_report_type)) {
            updateState_ {
                copy(selectedFilter = item, isDropdownExpanded = false, from = 0, list = emptyList())
            }
            callApi()
        }
    }

    fun onDateFilterClick() {
        updateState_ { copy(isDateDropdownExpanded = true) }
    }

    fun onDateFilterDismiss() {
        updateState_ { copy(isDateDropdownExpanded = false) }
    }

    fun onDateFilterSelected(item: ReportDateFilterItem) {
        val (fromDate, toDate) = DateTimeUtils.getDateRange(item.type)

        updateState_ {
            copy(selectedDateFilter = item, from_date = fromDate, to_date = toDate,
                from = 0, isDateDropdownExpanded = false, list = emptyList())
        }

        callApi()
    }

    fun loadNextPage() {
        if (state().isPagingLoader) return
        if (state().list.size >= state().totalRecord) return

        updateState_ {
            copy(isPagingLoader = true, from = list.size)
        }

        callApi()
    }


    private fun callApi() = viewModelScope.launch {
        val from = state().from
        val type = state().selectedFilter?.apiValue
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
                    copy(
                        totalRecord = data.totalRecord,
                        list = list + data.list,
                        isPagingLoader = false
                    )
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