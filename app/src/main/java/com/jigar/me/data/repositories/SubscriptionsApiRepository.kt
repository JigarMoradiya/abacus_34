package com.jigar.me.data.repositories

import com.jigar.me.data.api.connections.SafeApiCall
import com.jigar.me.data.api.SubscriptionsApi
import com.jigar.me.data.model.data.CancelReactivePlanRequest
import com.jigar.me.data.model.data.PurchasePlanCreateRequest
import com.jigar.me.data.model.data.PurchaseSuccessRequest
import javax.inject.Inject

class SubscriptionsApiRepository @Inject constructor(
    private val api: SubscriptionsApi
) : SafeApiCall {
    suspend fun getPlansList() = safeApiCall {
        api.plans()
    }
    suspend fun purchasePlanCreate(request : PurchasePlanCreateRequest) = safeApiCall {
        api.purchasePlanCreate(request)
    }
    suspend fun purchaseSuccess(request : PurchaseSuccessRequest) = safeApiCall {
        api.purchaseSuccess(request)
    }
    suspend fun cancelReactivePlan(planId : String?, request : CancelReactivePlanRequest) = safeApiCall {
        api.cancelReactivePlan(planId, request)
    }
    suspend fun getAccountDetail() = safeApiCall {
        api.accountDetail()
    }
}