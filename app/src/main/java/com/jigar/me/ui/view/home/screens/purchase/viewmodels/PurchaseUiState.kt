package com.jigar.me.ui.view.home.screens.purchase.viewmodels

import com.jigar.me.data.model.data.PlanAssignFromAdminData
import com.revenuecat.purchases.Package

data class RcPlanItem(
    val sku: String,
    val price: String?,
    val price_amount_micros: Long?,
    val type: String?,
    val isPurchase: Boolean,
    val billingPeriod: String?,
    val purchaseTime: Long,
    val rcPackage: Package
) {
    fun isLifeTimeOffer() = sku.contains("all")
}

data class PurchaseUiState(
    val error: Int? = null,
    val selectedIndex: Int = 0,
    val isPurchasing: Boolean = false,
    val purchaseSuccess: Boolean = false,

    val yearPlanAssignFromAdmin: PlanAssignFromAdminData? = null,
    val allPlanAssignFromAdmin: PlanAssignFromAdminData? = null,
    val planListAssignFromAdmin: List<PlanAssignFromAdminData> = emptyList(),

    // Plan data (from RC)
    val sortedPlanList: List<RcPlanItem> = emptyList(),

    // Originals for discount display
    val original1YearData: RcPlanItem? = null,
    val originalLifetimeData: RcPlanItem? = null,
    val original1MonthData: RcPlanItem? = null,

    // Discounts
    val discountPer: Int = 0,
    val discountPerLifetime: Int = 0,

    // UI flags
    val showSubmitButton: Boolean = false,
    val isOldSubscriptionThere: Boolean = false,
    val showOldSubscriptionPopup: Boolean = false,

    // Benefit list
    val benefitList: List<String> = emptyList(),
)
