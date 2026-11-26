package com.jigar.me.utils

import com.google.gson.Gson
import com.jigar.me.data.model.data.PlanAssignFromAdminData
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** The plan a RevenueCat promotional grant stands for. This app only ever grants Monthly, Yearly or Lifetime. */
enum class GrantKind { MONTHLY, YEARLY, LIFETIME }

/** A RevenueCat promotional grant (dashboard > Customers > Grant Entitlement `premium`): its plan and dates. */
data class GrantedPlan(val kind: GrantKind, val since: java.util.Date?, val expires: java.util.Date?)

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

    // A failed renewal (an expired card, no funds) sits in the store's grace period or billing retry: the parent has to fix the
    // payment method or the subscription ends. RevenueCat reports it as `billingIssueDetectedAt`.
    val hasBillingIssue: Boolean
        get() = cachedInfo?.entitlements?.get("premium")?.billingIssueDetectedAt != null

    val managementUrl: String?
        get() = cachedInfo?.managementURL?.toString()

    val allPurchasedProductIds: Set<String>
        get() = cachedInfo?.allPurchasedProductIds ?: emptySet()

    // True when user has a lifetime "all" plan purchase in RC history (handles pre-RC migration purchases)
    val isLifetimePurchased: Boolean
        get() = allPurchasedProductIds.any { it.contains("all") || it.contains("onetime") }

    /**
     * The plan a grant stands for, from its product id `rc_promo_<entitlement>_<duration>` ("rc_promo_premium_monthly" ...). Other durations
     * (a day, 3 days, a week, 2 / 3 / 6 months) stand for no plan: they unlock Premium and nothing more.
     */
    fun grantKindOf(productId: String): GrantKind? {
        val id = productId.lowercase()
        return when {
            "lifetime" in id -> GrantKind.LIFETIME
            "yearly" in id -> GrantKind.YEARLY
            "monthly" in id -> GrantKind.MONTHLY
            else -> null
        }
    }

    /** The grant behind the active `premium` entitlement of [info], or null when premium comes from a purchase (or there is none). */
    fun grantedPlanOf(info: CustomerInfo?): GrantedPlan? {
        val premium = info?.entitlements?.get("premium") ?: return null
        if (!premium.isActive || premium.store != Store.PROMOTIONAL) return null
        val kind = grantKindOf(premium.productIdentifier) ?: return null
        // RevenueCat gives a Lifetime grant an end date about 200 years away: a lifetime plan has no end date
        return GrantedPlan(kind, premium.latestPurchaseDate, if (kind == GrantKind.LIFETIME) null else premium.expirationDate)
    }

    val grantedPlan: GrantedPlan?
        get() = grantedPlanOf(cachedInfo)

    /** What a screen has to notice about the premium entitlement (a grant that arrives, ends or is revoked changes it). */
    fun premiumSignature(info: CustomerInfo?): String {
        val premium = info?.entitlements?.get("premium")
        return "${premium?.isActive}|${premium?.productIdentifier}|${premium?.store}"
    }

    /** The data an admin-assigned plan carries, built from a grant, so every plan card that shows an admin-assigned plan shows the grant too. */
    fun assignedDataOf(grant: GrantedPlan): PlanAssignFromAdminData {
        val format = java.text.SimpleDateFormat(DateTimeUtils.yyyy_MM_dd_T_HH_mm_ss_sssz, java.util.Locale.US)
            .apply { timeZone = java.util.TimeZone.getTimeZone("UTC") }
        return PlanAssignFromAdminData(
            google_plan_id = when (grant.kind) {
                GrantKind.MONTHLY -> "com.abacus.puzzle.1month"
                GrantKind.YEARLY -> "com.abacus.puzzle.1year"
                GrantKind.LIFETIME -> "all"
            },
            start_date = grant.since?.let { format.format(it) },
            end_date = grant.expires?.let { format.format(it) },
            purchased_from = "REVENUECAT_GRANT",
        )
    }
}
