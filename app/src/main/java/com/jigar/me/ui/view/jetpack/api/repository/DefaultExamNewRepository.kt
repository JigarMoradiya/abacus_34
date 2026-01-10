package com.jigar.me.ui.view.jetpack.api.repository

import com.google.gson.Gson
import com.jigar.me.data.api.ExamApi
import com.jigar.me.data.api.connections.SafeApiCall
import com.jigar.me.data.local.db.abacus_all_data.AbacusAllDataDB
import com.jigar.me.data.model.MainAPIResponse
import com.jigar.me.data.model.data.Statistics
import com.jigar.me.data.model.data.SubmitAllExamDataRequest
import com.jigar.me.ui.view.jetpack.core.miscs.emitFlow
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Resource
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import javax.inject.Inject

class DefaultExamNewRepository @Inject constructor(private val remote: ExamApi,private val abacusAllDataDB: AbacusAllDataDB) : ExamNewRepository, SafeApiCall {

    // network directory
    override fun submitExamData(params: SubmitAllExamDataRequest): Flow<Unit> = emitFlow {
        val result = submitExamDataApi(params)
        when (result) {
            is Resource.Success -> {
                val response = result.value
                if (response.status == AppConstants.APIStatus.SUCCESS){
                    return@emitFlow submitExamDataSuccess(response,params)
                } else {
                    val errorMsg = response.error?.message
                        ?: response.message
                        ?: "Unknown server response"
                    throw Exception(errorMsg)
                }
            }

            is Resource.Failure -> throw when {
                result.isNetworkError -> IOException("Network error occurred")
                else -> Exception("API error: ${result.errorBody ?: "Unknown error"}, code: ${result.errorCode}")
            }

            else -> throw Exception("Unexpected response type")
        }
    }

    private suspend fun submitExamDataApi(request: SubmitAllExamDataRequest) = safeApiCall {
        remote.submitAllExam(request)
    }

    private suspend fun submitExamDataSuccess(response: MainAPIResponse, request: SubmitAllExamDataRequest) {
        if (response.status == AppConstants.APIStatus.SUCCESS){
            if (request.type == AppConstants.apiParams.answerFormalExam){
                request.set_id?.let{
                    abacusAllDataDB.removeUserAnswer(it)
                }
            }
        }
    }

    override fun getStatistics(): Flow<Statistics> = emitFlow {
        val result = getStatisticsApi()
        when (result) {
            is Resource.Success -> {
                val response = result.value
                if (response.status == AppConstants.APIStatus.SUCCESS){
                    val statistics =  Gson().fromJson(response.data, Statistics::class.java)
                    return@emitFlow statistics
                } else {
                    val errorMsg = response.error?.message
                        ?: response.message
                        ?: "Unknown server response"
                    throw Exception(errorMsg)
                }
            }

            is Resource.Failure -> throw when {
                result.isNetworkError -> IOException("Network error occurred")
                else -> Exception("API error: ${result.errorBody ?: "Unknown error"}, code: ${result.errorCode}")
            }

            else -> throw Exception("Unexpected response type")
        }
    }

    private suspend fun getStatisticsApi() = safeApiCall {
        remote.getStatistics()
    }
}
