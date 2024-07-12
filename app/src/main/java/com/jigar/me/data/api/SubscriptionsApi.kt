package com.jigar.me.data.api

import com.jigar.me.data.model.MainAPIResponse
import com.jigar.me.data.model.data.CancelReactivePlanRequest
import com.jigar.me.data.model.data.PurchasePlanCreateRequest
import com.jigar.me.data.model.data.PurchaseSuccessRequest
import retrofit2.http.*

interface SubscriptionsApi {
    @GET("plans")
    suspend fun plans(): MainAPIResponse

    @POST("plans/create-payment-intent")
    suspend fun purchasePlanCreate(@Body request : PurchasePlanCreateRequest): MainAPIResponse

    @PUT("payment-status")
    suspend fun purchaseSuccess(@Body request : PurchaseSuccessRequest): MainAPIResponse

    @PUT("plans/{planId}")
    suspend fun cancelReactivePlan(
        @Path("planId") planId : String? = null,
        @Body request : CancelReactivePlanRequest
    ): MainAPIResponse

    @GET("account-detail")
    suspend fun accountDetail(): MainAPIResponse
}