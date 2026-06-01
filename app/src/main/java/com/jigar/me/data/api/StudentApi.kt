package com.jigar.me.data.api

import com.jigar.me.data.model.MainAPIResponse
import com.jigar.me.data.model.data.FetchAbacusDataRequest
import com.jigar.me.data.model.data.LoginRequest
import com.jigar.me.data.model.data.PurchasedPlanCheckRequest
import com.jigar.me.data.model.data.SignupV2Request
import com.jigar.me.data.model.data.SocialLoginRequest
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
    @POST("student/handle-existing-plan")
    suspend fun handleExistingPurchase(@Body request : PurchasedPlanCheckRequest): MainAPIResponse
    @GET("student/app-reviews-list")
    suspend fun appReviewsList(): MainAPIResponse
    @POST("student/change-plan")
    suspend fun changePlan(@Body request : PurchasedPlanCheckRequest): MainAPIResponse
    @POST("student/get-abacus-set-pages-categories-levels-data")
    suspend fun getAbacusData(@Body request : FetchAbacusDataRequest): MainAPIResponse
    @POST("student/get-abacus-set-pages-categories-levels-data-public")
    suspend fun getAbacusDataPublic(@Body request : FetchAbacusDataRequest): MainAPIResponse
    @Multipart
    @POST("app-reviews")
    suspend fun submitReview(
        @Part("plan_id") plan_id : RequestBody,
        @Part("description") description : RequestBody,
        @Part image_1: MultipartBody.Part?
    ): MainAPIResponse

}