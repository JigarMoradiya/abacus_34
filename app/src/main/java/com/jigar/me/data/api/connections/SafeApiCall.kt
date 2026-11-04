package com.jigar.me.data.api.connections


import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jigar.me.MyApplication
import com.jigar.me.R
import com.jigar.me.data.model.MainAPIResponse
import com.jigar.me.utils.ApiErrors
import com.jigar.me.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

interface SafeApiCall {
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                Resource.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        val data = throwable.response()?.errorBody()?.string()
                        val errorResponse = runCatching { Gson().fromJson(data, MainAPIResponse::class.java) }.getOrNull()
                        Log.e("error_safe_api_call","response = "+errorResponse)
                        val status = errorResponse?.statusCode?.takeIf { it != 0 } ?: throwable.code()
                        val code = errorResponse?.error?.error_code
                        // the text is ours (translated), the server's sentence only helps to find the case
                        Resource.Failure(
                            false,
                            errorCode = status,
                            errorBody = ApiErrors.text(status, code, errorResponse?.error?.message ?: errorResponse?.message),
                            errorType = code
                        )
                    }
                    else -> {
                        Log.e("error_safe_api_call", throwable.toString())
                        Resource.Failure(true, null, ApiErrors.text(null, ApiErrors.CODE_NETWORK, null), ApiErrors.CODE_NETWORK)
                    }
                }
            }
        }
    }
}