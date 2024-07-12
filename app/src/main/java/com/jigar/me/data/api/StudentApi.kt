package com.jigar.me.data.api

import com.jigar.me.data.model.MainAPIResponse
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
import retrofit2.http.*

interface StudentApi {
    @POST("signup")
    suspend fun signup(@Body request : SignupV2Request): MainAPIResponse
    @POST("verification")
    suspend fun verification(@Body request : VerifyEmailRequest): MainAPIResponse
    @POST("google-login-signup")
    suspend fun socialLogin(@Body request : SocialLoginRequest): MainAPIResponse
    @POST("login")
    suspend fun login(@Body request : LoginRequest): MainAPIResponse
    @POST("forgot-password")
    suspend fun forgotPassword(@Body request : ForgotPasswordRequest): MainAPIResponse
    @POST("resend-otp")
    suspend fun resendOTP(@Body request : ResendOTPRequest): MainAPIResponse
    @POST("reset-password")
    suspend fun resetPassword(@Body request : ResetPasswordRequest): MainAPIResponse
    @POST("change-password")
    suspend fun changePassword(@Body request : ChangePasswordRequest): MainAPIResponse

    @PUT("profile")
    suspend fun updateProfile(@Body request : UpdateProfileRequest): MainAPIResponse
    @POST("handle-existing-plan")
    suspend fun handleExistingPurchase(@Body request : PurchasedPlanCheckRequest): MainAPIResponse
    @POST("change-plan")
    suspend fun changePlan(@Body request : PurchasedPlanCheckRequest): MainAPIResponse

}