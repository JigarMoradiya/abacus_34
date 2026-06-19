package com.jigar.me.utils

import android.util.Log
import com.google.gson.Gson
import com.revenuecat.purchases.CustomerInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object RevenueCatHelper {
    private var cachedInfo: CustomerInfo? = null

    // Emits every time RC customer info updates (from any source: init, login, restore, listener)
    private val _customerInfoFlow = MutableStateFlow<CustomerInfo?>(null)
    val customerInfoFlow: StateFlow<CustomerInfo?> = _customerInfoFlow.asStateFlow()

    fun update(info: CustomerInfo) {
        cachedInfo = info
        _customerInfoFlow.value = info
    }

    val isSubscribed: Boolean
        get() = cachedInfo?.entitlements?.get("premium")?.isActive == true

    val allPurchasedProductIds: Set<String>
        get() = cachedInfo?.allPurchasedProductIds ?: emptySet()

    // True when user has a lifetime "all" plan purchase in RC history (handles pre-RC migration purchases)
    val isLifetimePurchased: Boolean
        get() = allPurchasedProductIds.any { it.contains("all") || it.contains("onetime") }
}
