package com.jigar.me.data.api

import com.jigar.me.data.model.MainAPIResponse
import com.jigar.me.data.model.data.ChangePasswordRequest
import com.jigar.me.data.model.data.ContactUsRequest
import com.jigar.me.data.model.data.FetchAbacusDataRequest
import com.jigar.me.data.model.data.ForgotPasswordRequest
import com.jigar.me.data.model.data.LoginRequest
import com.jigar.me.data.model.data.PurchasedPlanCheckRequest
import com.jigar.me.data.model.data.ResendOTPRequest
import com.jigar.me.data.model.data.ResetPasswordRequest
import com.jigar.me.data.model.data.SignupV2Request
import com.jigar.me.data.model.data.SocialLoginRequest
import com.jigar.me.data.model.data.UpdateProfileRequest
import com.jigar.me.data.model.data.VerifyEmailRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface StudentApi {
    @POST("student/signup")
    suspend fun signup(@Body request : SignupV2Request): MainAPIResponse
    @POST("student/verification")
    suspend fun verification(@Body request : VerifyEmailRequest): MainAPIResponse
    @POST("student/google-login-signup")
    suspend fun socialLogin(@Body request : SocialLoginRequest): MainAPIResponse
    @POST("student/login")
    suspend fun login(@Body request : LoginRequest): MainAPIResponse
    @POST("student/forgot-password")
    suspend fun forgotPassword(@Body request : ForgotPasswordRequest): MainAPIResponse
    @POST("student/resend-otp")
    suspend fun resendOTP(@Body request : ResendOTPRequest): MainAPIResponse
    @POST("student/reset-password")
    suspend fun resetPassword(@Body request : ResetPasswordRequest): MainAPIResponse
    @POST("student/change-password")
    suspend fun changePassword(@Body request : ChangePasswordRequest): MainAPIResponse
    @PUT("student/profile")
    suspend fun updateProfile(@Body request : UpdateProfileRequest): MainAPIResponse
    @POST("student/handle-existing-plan")
    suspend fun handleExistingPurchase(@Body request : PurchasedPlanCheckRequest): MainAPIResponse
    @GET("student/app-reviews-list")
    suspend fun appReviewsList(): MainAPIResponse
    @POST("student/change-plan")
    suspend fun changePlan(@Body request : PurchasedPlanCheckRequest): MainAPIResponse
    @POST("student/get-abacus-set-pages-categories-levels-data")
    suspend fun getAbacusData(@Body request : FetchAbacusDataRequest): MainAPIResponse
    @Multipart
    @POST("app-reviews")
    suspend fun submitReview(
        @Part("plan_id") plan_id : RequestBody,
        @Part("description") description : RequestBody,
        @Part image_1: MultipartBody.Part?
    ): MainAPIResponse

}