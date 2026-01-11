package com.jigar.me.ui.view.jetpack.fragments.reports.viewmodels

import com.jigar.me.data.model.data.AllExamData
import com.jigar.me.data.model.data.SubmitAllExamDataRequest


data class ReportHistoryUiState(
    val error: Int? = null,
    val isLoading: Boolean = false,

    val from: Int = 0,
    val type: String? = null,
    val from_date: String? = null,
    val to_date: String? = null,

    val totalRecord: Int = 0,
    val list: List<AllExamData> = arrayListOf(),

    val exerciseExamRequest : SubmitAllExamDataRequest? = null,
    val isShowExerciseExamPopup: Boolean = false,
)

