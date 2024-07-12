package com.jigar.me.data.repositories

import androidx.annotation.Keep
import com.jigar.me.data.api.StudentApi
import com.jigar.me.data.api.connections.SafeApiCall
import com.jigar.me.data.model.data.ChangePasswordRequest
import com.jigar.me.data.model.data.ContactUsRequest
import com.jigar.me.data.model.data.ForgotPasswordRequest
import com.jigar.me.data.model.data.LoginRequest
import com.jigar.me.data.model.data.PurchasedPlanCheckRequest
import com.jigar.me.data.model.data.ResendOTPRequest
import com.jigar.me.data.model.data.ResetPasswordRequest
import com.jigar.me.data.model.data.SignupV2Request
import com.jigar.me.data.model.data.SocialLoginRequest
import com.jigar.me.data.model.data.UpdateProfileRequest
import com.jigar.me.data.model.data.VerifyEmailRequest
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
    suspend fun handleExistingPurchase(request : PurchasedPlanCheckRequest) = safeApiCall {
        api.handleExistingPurchase(request)
    }
    suspend fun changePlan(request : PurchasedPlanCheckRequest) = safeApiCall {
        api.changePlan(request)
    }
    suspend fun forgotPassword(request : ForgotPasswordRequest) = safeApiCall {
        api.forgotPassword(request)
    }
    suspend fun resendOTP(request : ResendOTPRequest) = safeApiCall {
        api.resendOTP(request)
    }
    
    suspend fun resetPassword(request : ResetPasswordRequest) = safeApiCall {
        api.resetPassword(request)
    }
    suspend fun changePassword(request : ChangePasswordRequest) = safeApiCall {
        api.changePassword(request)
    }
    suspend fun updateProfile(request : UpdateProfileRequest) = safeApiCall {
        api.updateProfile(request)
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