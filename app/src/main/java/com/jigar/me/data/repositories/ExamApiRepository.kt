package com.jigar.me.data.repositories

import com.jigar.me.data.api.ExamApi
import com.jigar.me.data.api.connections.SafeApiCall
import com.jigar.me.data.model.data.SubmitAllExamDataRequest
import com.jigar.me.utils.AppConstants
import javax.inject.Inject

class ExamApiRepository @Inject constructor(
    private val api: ExamApi
) : SafeApiCall {
    suspend fun submitAllExam(request: SubmitAllExamDataRequest) = safeApiCall {
        api.submitAllExam(request)
    }
    suspend fun getAllExam(type : String? = null,from_date : String? = null, to_date : String? = null, from : Int = 0) = safeApiCall {
        api.getAllExam(type,from_date,to_date,from)
    }
    suspend fun getStatistics() = safeApiCall {
        api.getStatistics()
    }
}