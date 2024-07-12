package com.jigar.me.data.api.connections


import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jigar.me.MyApplication
import com.jigar.me.R
import com.jigar.me.data.model.MainAPIResponse
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
                        val gson = GsonBuilder().setPrettyPrinting().create()
                        val data = throwable.response()?.errorBody()?.string().toString()
                        val errorResponse = gson.fromJson(data, MainAPIResponse::class.java)
                        Log.e("error_safe_api_call","response = "+errorResponse)
                        Resource.Failure(
                            false,
                            errorCode = errorResponse.statusCode,
                            errorBody = errorResponse.error?.message,
                            errorType = errorResponse.error?.error_code
                        )
                    }
                    else -> {
                        Log.e("error_safe_api_call", throwable.toString())
                        Resource.Failure(true, null, MyApplication.getInstance().getString(R.string.no_internet))
                    }
                }
            }
        }
    }
}