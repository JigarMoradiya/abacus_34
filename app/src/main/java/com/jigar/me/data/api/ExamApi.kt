package com.jigar.me.data.api

import com.jigar.me.data.model.MainAPIResponse
import com.jigar.me.data.model.data.SubmitAllExamDataRequest
import com.jigar.me.utils.AppConstants
import retrofit2.http.*

interface ExamApi {
    @POST("adhoc-question-result")
    suspend fun submitAllExam(@Body request : SubmitAllExamDataRequest): MainAPIResponse

    @GET("adhoc-question-result")
    suspend fun getAllExam(@Query("type") type : String? = null,@Query("from_date") from_date : String? = null,@Query("to_date") to_date : String? = null,
                           @Query("from") from : Int = 0,@Query("rows") rows : Int = AppConstants.PAGINATION_RECORDS): MainAPIResponse

    @GET("statistics")
    suspend fun getStatistics(): MainAPIResponse
}