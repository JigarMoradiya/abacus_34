package com.jigar.me.utils

import com.revenuecat.purchases.CustomerInfo

object RevenueCatHelper {
    private var cachedInfo: CustomerInfo? = null

    fun update(info: CustomerInfo) {
        cachedInfo = info
    }

    val isSubscribed: Boolean
        get() = cachedInfo?.entitlements?.get("premium")?.isActive == true
}
