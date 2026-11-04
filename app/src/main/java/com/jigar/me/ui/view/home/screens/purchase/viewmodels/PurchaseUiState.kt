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

/** A plan price the way every plan card shows it: the store currency, no decimals for a whole amount ("₹1,200") and the cents otherwise ("$4.99": never rounded). */
fun formatPlanPrice(amountMicros: Long, currencyCode: String): String {
    return try {
        val format = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.getDefault())
        format.currency = java.util.Currency.getInstance(currencyCode)
        val whole = amountMicros % 1_000_000L == 0L
        format.minimumFractionDigits = if (whole) 0 else format.currency!!.defaultFractionDigits.coerceAtLeast(0)
        format.maximumFractionDigits = if (whole) 0 else format.currency!!.defaultFractionDigits.coerceAtLeast(0)
        format.format(amountMicros / 1_000_000.0)
    } catch (e: Exception) {
        amountMicros.div(1_000_000).toString()
    }
}

data class PurchaseUiState(
    val error: Int? = null,
    /** A one-time message about a purchase that failed, is waiting for approval or was already owned (shown as a Toast, then cleared). */
    val message: Int? = null,
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

    // The timed Home offer (Remote Config `home_offer`), only while it runs: the % off of the ".offer" plan that is shown
    // INSTEAD of the regular one (worked out from the two store prices), and when the window ends so the plan card can show
    // the same live countdown as the Home card. 0 / null = no timed offer for that plan (store offers are in storeIntroOffer()).
    val discountPer: Int = 0,
    val discountPerLifetime: Int = 0,
    val homeOfferYearlyEndMillis: Long? = null,
    val homeOfferLifetimeEndMillis: Long? = null,

    // UI flags
    val showSubmitButton: Boolean = false,
    val isOldSubscriptionThere: Boolean = false,
    val showOldSubscriptionPopup: Boolean = false,

    // Benefit list
    val benefitList: List<String> = emptyList(),

    // Social proof line (Remote Config, hidden when blank)
    val socialProofText: String = "",
)
