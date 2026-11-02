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
    val rcPackage: Package,
    /** The store order (transaction) id of this plan's purchase, from RevenueCat's records; null when not bought. */
    val orderId: String? = null
) {
    fun isLifeTimeOffer() = sku.contains("all")
}

/** A plan price the way every plan card shows it: the store currency, no decimals ("₹1,200"). */
fun formatPlanPrice(amountMicros: Long, currencyCode: String): String {
    return try {
        val format = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.getDefault())
        format.currency = java.util.Currency.getInstance(currencyCode)
        format.maximumFractionDigits = 0
        format.format(amountMicros / 1_000_000.0)
    } catch (e: Exception) {
        amountMicros.div(1_000_000).toString()
    }
}

data class PurchaseUiState(
    val error: Int? = null,
    val isLoading: Boolean = true,
    val selectedIndex: Int = 0,
    val isPurchasing: Boolean = false,
    val purchaseSuccess: Boolean = false,

    val yearPlanAssignFromAdmin: PlanAssignFromAdminData? = null,
    val allPlanAssignFromAdmin: PlanAssignFromAdminData? = null,
    val planListAssignFromAdmin: List<PlanAssignFromAdminData> = emptyList(),

    // Plan data (from RC)
    val sortedPlanList: List<RcPlanItem> = emptyList(),
    // The RevenueCat offering these plans came from (an Experiments price variant is a different offering)
    val offeringId: String = "",

    // The regular plans by type (the monthly one feeds the "save vs monthly" maths)
    val original1YearData: RcPlanItem? = null,
    val originalLifetimeData: RcPlanItem? = null,
    val original1MonthData: RcPlanItem? = null,
    val original1WeekData: RcPlanItem? = null,

    // Removed: the store provides the discount (Google Play offers read through RevenueCat, see storeIntroOffer()).
    // // Discounts
    // val discountPer: Int = 0,
    // val discountPerLifetime: Int = 0,
    //
    // // Timed Home offer (Remote Config `home_offer`): when it ends, so the Purchase
    // // page can show the same live countdown as the Home card. Null = not active for
    // // that specific plan.
    // val homeOfferYearlyEndMillis: Long? = null,
    // val homeOfferLifetimeEndMillis: Long? = null,

    // UI flags
    val showSubmitButton: Boolean = false,
    val isOldSubscriptionThere: Boolean = false,
    val showOldSubscriptionPopup: Boolean = false,

    // Benefit list
    val benefitList: List<String> = emptyList(),

    // Social proof line (Remote Config, hidden when blank)
    val socialProofText: String = "",
)
