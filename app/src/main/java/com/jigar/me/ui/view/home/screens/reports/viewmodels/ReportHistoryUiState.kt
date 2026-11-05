package com.jigar.me.ui.view.home.screens.reports.viewmodels

import android.content.Context
import com.jigar.me.R
import com.jigar.me.data.model.data.AllExamData
import com.jigar.me.data.model.data.SubmitAllExamDataRequest


data class ReportHistoryUiState(
    val error: Int? = null,
    val isLoading: Boolean = false,
    val isPagingLoader: Boolean = false,
    val selectedTheme: String = "",

    // Report filter
    val filterList: List<ReportFilterItem> = emptyList(),
    val isDropdownExpanded: Boolean = false,
    val selectedFilter: ReportFilterItem? = null,

    // Date filter
    val dateFilterList: List<ReportDateFilterItem> = emptyList(),
    val selectedDateFilter: ReportDateFilterItem? = null,
    val isDateDropdownExpanded: Boolean = false,

    val from: Int = 0,
    val type: String? = null,
    val from_date: String? = null,
    val to_date: String? = null,

    val totalRecord: Int = 0,
    val list: List<AllExamData> = arrayListOf(),

    val exerciseExamRequest : SubmitAllExamDataRequest? = null,
    val isShowExerciseExamPopup: Boolean = false,
)

data class ReportFilterItem(
    val label: String,   // UI text
    val apiValue: String? // API value (null for default)
)

data class ReportDateFilterItem(
    val label: String,        // UI text
    val type: DateFilterType  // Logic type
)

enum class DateFilterType {
    LAST_30_DAYS,
    THIS_MONTH,
    LAST_MONTH,
    LAST_3_MONTH,
    LAST_6_MONTH,
    LAST_1_YEAR
}

fun buildDateFilterList(context: Context): List<ReportDateFilterItem> {
    return listOf(
        ReportDateFilterItem(
            label = context.getString(R.string.last_30_days),
            type = DateFilterType.LAST_30_DAYS
        ),
        ReportDateFilterItem(
            label = context.getString(R.string.this_month),
            type = DateFilterType.THIS_MONTH
        ),
        ReportDateFilterItem(
            label = context.getString(R.string.last_month),
            type = DateFilterType.LAST_MONTH
        ),
        ReportDateFilterItem(
            label = context.getString(R.string.last_3_month),
            type = DateFilterType.LAST_3_MONTH
        ),
        ReportDateFilterItem(
            label = context.getString(R.string.last_6_month),
            type = DateFilterType.LAST_6_MONTH
        ),
        ReportDateFilterItem(
            label = context.getString(R.string.last_1_year),
            type = DateFilterType.LAST_1_YEAR
        )
    )
}
