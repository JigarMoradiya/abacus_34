package com.jigar.me.ui.view.jetpack.api.repository

import com.google.gson.JsonObject
import com.jigar.me.data.model.data.FetchReportHistoryRequest
import com.jigar.me.data.model.data.FetchReportHistoryResponse
import com.jigar.me.data.model.data.Statistics
import com.jigar.me.data.model.data.SubmitAllExamDataRequest
import kotlinx.coroutines.flow.Flow

interface ExamNewRepository {
    fun submitExamData(params: SubmitAllExamDataRequest): Flow<Unit>
    fun getStatistics(): Flow<Statistics>
    fun getReportHistory(params : FetchReportHistoryRequest): Flow<FetchReportHistoryResponse>
}