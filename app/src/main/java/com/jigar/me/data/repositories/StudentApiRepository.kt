package com.jigar.me.data.repositories

import androidx.annotation.Keep
import com.jigar.me.data.api.StudentApi
import com.jigar.me.data.api.connections.SafeApiCall
import com.jigar.me.data.model.data.FetchAbacusDataRequest
import com.jigar.me.data.model.data.LoginRequest
import com.jigar.me.data.model.data.SignupV2Request
import com.jigar.me.data.model.data.SocialLoginRequest
import com.jigar.me.data.model.data.VerifyEmailRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class StudentApiRepository @Inject constructor(
    private val api: StudentApi
) : SafeApiCall {

    suspend fun signup(request : SignupV2Request) = safeApiCall {
        api.signup(request)
    }
    suspend fun verification(request : VerifyEmailRequest) = safeApiCall {
        api.verification(request)
    }
    suspend fun login(request : LoginRequest) = safeApiCall {
        api.login(request)
    }
    suspend fun socialLogin(request : SocialLoginRequest) = safeApiCall {
        api.socialLogin(request)
    }
    suspend fun getAbacusData(request : FetchAbacusDataRequest) = safeApiCall {
        api.getAbacusData(request)
    }
    suspend fun getAbacusDataPublic(request : FetchAbacusDataRequest) = safeApiCall {
        api.getAbacusDataPublic(request)
    }
    suspend fun appReviewsList() = safeApiCall {
        api.appReviewsList()
    }
    suspend fun submitReview(
        planId: RequestBody,
        description: RequestBody,
        image1: MultipartBody.Part?,
    ) = safeApiCall {
        api.submitReview(planId, description, image1)
    }
}

sealed class Result<out R> {
    @Keep
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}