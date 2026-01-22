package com.jigar.me.ui.view.jetpack.fragments.purchase.viewmodels

import com.jigar.me.data.model.DisplayPurchaseData
import com.jigar.me.data.model.data.PlanAssignFromAdminData
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails

data class PurchaseUiState(
    val error: Int? = null,
    val selectedIndex: Int = 0,

    val yearPlanAssignFromAdmin: PlanAssignFromAdminData? = null,
    val allPlanAssignFromAdmin: PlanAssignFromAdminData? = null,

    // UI lists
    val benefitList: List<String> = emptyList(),
    val displayItemList: List<DisplayPurchaseData> = emptyList(),
    val planListAssignFromAdmin: List<PlanAssignFromAdminData> = emptyList(),

    // SKU data
    val inAppSkuDetailsList: List<InAppSkuDetails> = emptyList(),
    val sortedSkuList: List<InAppSkuDetails> = emptyList(),
    val oldPurchasedSkuList: List<InAppSkuDetails> = emptyList(),

    // Discounts
    val discountPer: Int = 0,
    val discountPerLifetime: Int = 0,

    // Original (before discount)
    val original1YearData: InAppSkuDetails? = null,
    val originalLifetimeData: InAppSkuDetails? = null,
    val original1MonthData: InAppSkuDetails? = null,

    // UI flags
    val isOldSubscriptionThere: Boolean = false,
    val showOldSubscriptionPopup: Boolean = false,
    val showSubmitButton: Boolean = true,
)