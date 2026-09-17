package com.jigar.me.ui.view.home.screens.purchase.viewmodels

import android.app.Activity
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.R
import com.jigar.me.data.model.data.GooglePurchasedPlanRequest
import com.jigar.me.data.model.data.PlanAssignFromAdminData
import com.jigar.me.data.model.data.PurchasedPlanCheckRequest
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.core.StatefulViewModel
import com.jigar.me.ui.view.home.screens.home.repository.AbacusRepository
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Constants
import com.jigar.me.utils.HomeOfferManager
import com.jigar.me.utils.RevenueCatHelper
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PackageType
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.PurchasesException
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.awaitOfferings
import com.revenuecat.purchases.awaitCustomerInfo
import com.revenuecat.purchases.awaitPurchase
import com.revenuecat.purchases.models.StoreTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PurchaseViewModel @Inject constructor(
    private val prefManager: AppPreferencesHelper,
    private val abacusRepository: AbacusRepository,
) : StatefulViewModel<PurchaseUiState>() {

    override val TAG = "PurchaseViewModel"

    override fun getInitialState() = PurchaseUiState(
        benefitList = listOf(
            "⭐ <strong>Get unlimited access</strong> to all Abacus Levels, Exercises, Exams and Custom Challenge Modes Module.",
            "🧮 Practice Addition, Subtraction, Multiplication, Division, with <strong>smart bead directions</strong> and <strong>formula on every steps.</strong>",
            "🎯 Prepare for math competitions, UCMAS and abacus exams with <strong>real exam-style practice.</strong>",
            "📊 <strong>Track your child's</strong> progress, speed and accuracy with detailed reports."
        )
    )

    fun isUserLoggedIn(): Boolean = prefManager.isUserLoggedIn()
    fun isUserSubscribed(): Boolean = CommonUtils.checkPurchaseForExerciseExamCCM(prefManager)

    fun loadData() {
        val discountLifetime = prefManager.getCustomParamInt(AppConstants.RemoteConfig.discountPerLifeTime, 0)
        // The timed Home offer (Remote Config `home_offer`) makes the yearly .offer plan
        // active exactly like the permanent discount does. Inactive => unchanged behaviour.
        // Pinned to one instant so the SKU choice below and the % badge computed after
        // awaitOfferings() can't disagree if the window happens to expire mid-load.
        val decisionNow = System.currentTimeMillis()
        val yearlyDiscountActive = HomeOfferManager.isYearlyDiscountActive(prefManager, decisionNow)
        val lifetimeDiscountActive = HomeOfferManager.isLifetimeDiscountActive(prefManager, decisionNow)

        val adminPlanList = prefManager.getCustomParam(Constants.PLAN_ASSIGN_FROM_ADMIN_DATA, "")
            .takeIf { it.isNotEmpty() }
            ?.let { Gson().fromJson<List<PlanAssignFromAdminData>>(it, object : TypeToken<List<PlanAssignFromAdminData>>() {}.type) }
            ?: emptyList()

        val yearPlanFromAdmin = adminPlanList.find {
            it.google_order_id == null && it.google_plan_id?.contains("1year") == true
        }
        val allPlanFromAdmin = adminPlanList.find {
            it.google_order_id == null && it.google_plan_id?.contains("all") == true
        }

        updateState_ {
            copy(
                // Fallback floor: the permanent Remote Config value, set synchronously so
                // the paywall never loses its discount badge if awaitOfferings() below
                // fails (e.g. offline). The async block overwrites this with the timed-
                // offer-aware value once offerings load; until then this is exactly the
                // pre-existing behaviour.
                discountPer = prefManager.getCustomParamInt(AppConstants.RemoteConfig.discountPer, 0),
                discountPerLifetime = discountLifetime,
                planListAssignFromAdmin = adminPlanList,
                yearPlanAssignFromAdmin = yearPlanFromAdmin,
                allPlanAssignFromAdmin = allPlanFromAdmin,
                socialProofText = prefManager.getCustomParam(AppConstants.RemoteConfig.paywallSocialProof, ""),
            )
        }

        val displayPlanJson = prefManager.getCustomParam(AppConstants.RemoteConfig.displayPlanList, "")
        val displayPlanIds: Set<String> = if (displayPlanJson.length > 5) {
            val type = object : TypeToken<List<Map<String, Any>>>() {}.type
            val list: List<Map<String, Any>> = Gson().fromJson(displayPlanJson, type)
            list.mapNotNull { it["id"] as? String }.toSet()
        } else emptySet()

        viewModelScope.launch {
            try {
                val offerings = Purchases.sharedInstance.awaitOfferings()
                val allPackages = offerings.current?.availablePackages ?: emptyList()
                // The timed Home offer is fully self-contained (enabled + product decide
                // everything), so its target base/.offer pair is always shown regardless
                // of display_plan -- otherwise the Home card could promise a discount the
                // paywall then filters back out.
                val timedOfferIds = HomeOfferManager.targetProductIds(prefManager, decisionNow)
                var packages = if (displayPlanIds.isEmpty()) allPackages
                               else allPackages.filter { pkg ->
                                   displayPlanIds.any { id -> pkg.product.id == id || pkg.product.id.startsWith("$id:") } ||
                                       timedOfferIds.any { id -> AppConstants.Products.matches(pkg.product.id, id) }
                               }
                // % shown on the yearly .offer: the permanent Remote Config value wins;
                // during the timed Home offer it's derived from the real base vs .offer
                // store prices so it always matches what the store actually charges.
                // (Written once here, not synchronously above, so it never flickers to
                // the wrong value while offerings are loading.)
                val effectiveDiscountPer = HomeOfferManager.effectiveYearDiscountPer(
                    prefManager,
                    allPackages.firstOrNull { AppConstants.Products.matches(it.product.id, AppConstants.Products.year) }?.product?.price?.amountMicros,
                    allPackages.firstOrNull { AppConstants.Products.matches(it.product.id, AppConstants.Products.yearOffer) }?.product?.price?.amountMicros,
                    decisionNow
                )
                // Same formula, mirrored for the lifetime plan.
                val effectiveDiscountPerLifetime = HomeOfferManager.effectiveLifetimeDiscountPer(
                    prefManager,
                    allPackages.firstOrNull { it.product.id == AppConstants.Products.lifetime }?.product?.price?.amountMicros,
                    allPackages.firstOrNull { it.product.id == AppConstants.Products.lifetimeOffer }?.product?.price?.amountMicros,
                    decisionNow
                )
                updateState_ {
                    copy(
                        discountPer = effectiveDiscountPer,
                        discountPerLifetime = effectiveDiscountPerLifetime,
                        homeOfferYearlyEndMillis = HomeOfferManager.yearlyEndMillis(prefManager, decisionNow),
                        homeOfferLifetimeEndMillis = HomeOfferManager.lifetimeEndMillis(prefManager, decisionNow)
                    )
                }
                val customerInfo = Purchases.sharedInstance.awaitCustomerInfo()
                RevenueCatHelper.update(customerInfo)
                val premiumEntitlement = customerInfo.entitlements["premium"]
                val activeProdId: String? = if (premiumEntitlement?.isActive == true) premiumEntitlement?.productIdentifier else null
                // "com.abacus.puzzle.onetime" is a legacy pre-RevenueCat lifetime purchase --
                // it's not part of any current RC offering, so it can never match a Package
                // and the paywall would show NO card as Purchased even though the entitlement
                // is active. Remap it to today's lifetime SKU for matching purposes only.
                // Android-only: iOS never sold this SKU, so it has no such legacy product.
                val activeProdIdForMatching: String? = if (activeProdId == "com.abacus.puzzle.onetime") {
                    AppConstants.Products.lifetime
                } else {
                    activeProdId
                }
                val allPurchasedIds = customerInfo.allPurchasedProductIds

                // Always show the user's active plan even if not in Firebase config
                val effectivePurchasedId = activeProdIdForMatching
                    ?: allPurchasedIds.firstOrNull { id -> allPackages.any { pkg -> pkg.product.id == id || pkg.product.id.startsWith("$id:") } }
                if (effectivePurchasedId != null) {
                    val alreadyInList = packages.any { pkg ->
                        pkg.product.id == effectivePurchasedId || pkg.product.id.startsWith("$effectivePurchasedId:")
                    }
                    if (!alreadyInList) {
                        val activePkg = allPackages.firstOrNull { pkg ->
                            pkg.product.id == effectivePurchasedId || pkg.product.id.startsWith("$effectivePurchasedId:")
                        }
                        if (activePkg != null) packages = listOf(activePkg) + packages
                    }
                }

                val activeSubIds = customerInfo.activeSubscriptions
                fun toPlanItem(pkg: Package): RcPlanItem {
                    val matchesActive = activeProdIdForMatching != null && (
                        pkg.product.id == activeProdIdForMatching ||
                        pkg.product.id.startsWith("$activeProdIdForMatching:") ||
                        pkg.product.id.endsWith(":$activeProdIdForMatching")
                    )
                    // For subscriptions use activeSubscriptions (expired subs are excluded).
                    // For lifetime (INAPP) use allPurchasedProductIds since they never expire.
                    val matchesAllPurchased = if (pkg.packageType == PackageType.LIFETIME) {
                        allPurchasedIds.any { id ->
                            pkg.product.id == id || pkg.product.id.startsWith("$id:") || pkg.product.id.endsWith(":$id")
                        }
                    } else {
                        activeSubIds.any { id ->
                            pkg.product.id == id || pkg.product.id.startsWith("$id:") || pkg.product.id.endsWith(":$id")
                        }
                    }
                    val purchasedIdForDate = allPurchasedIds
                        .firstOrNull { id -> pkg.product.id == id || pkg.product.id.startsWith("$id:") || pkg.product.id.endsWith(":$id") }
                        // This package can stand in for a legacy purchase under a different
                        // SKU (e.g. "com.abacus.puzzle.onetime" remapped to today's lifetime
                        // plan above) -- allPurchasedIds has the LEGACY id, not this package's
                        // id, so fall back to the real underlying id for the date lookup.
                        ?: activeProdId?.takeIf { pkg.product.id == activeProdIdForMatching && activeProdId != activeProdIdForMatching }
                    val purchaseTime = purchasedIdForDate
                        ?.let { id -> customerInfo.getPurchaseDateForProductId(id)?.time ?: 0L }
                        ?: 0L
                    return RcPlanItem(
                        sku = pkg.product.id,
                        price = formatPrice(pkg.product.price.amountMicros, pkg.product.price.currencyCode),
                        price_amount_micros = pkg.product.price.amountMicros,
                        type = if (pkg.packageType == PackageType.LIFETIME) "inapp" else "subs",
                        // When there's a live entitlement, only the SKU actually backing it
                        // counts as "purchased" -- an upgrade (e.g. monthly -> yearly) leaves
                        // the old SKU sitting in allPurchasedProductIds/activeSubscriptions
                        // forever (Play doesn't retroactively clear purchase history), so
                        // OR-ing the two let the old, cheaper plan win the "Purchased" badge.
                        // Only fall back to the broader purchase-history match when there is
                        // no active entitlement at all (e.g. it expired).
                        isPurchase = if (activeProdIdForMatching != null) matchesActive else matchesAllPurchased,
                        billingPeriod = billingPeriodFor(pkg.packageType),
                        purchaseTime = purchaseTime,
                        rcPackage = pkg
                    )
                }
                val allPlans = packages.map { toPlanItem(it) }.sortedBy { it.price_amount_micros }
                // Strike-through anchor from the UNFILTERED offering so it survives a
                // display_plan whitelist that omits the base yearly (matches iOS).
                val originalYearFromOffering = allPackages
                    .firstOrNull { AppConstants.Products.matches(it.product.id, AppConstants.Products.year) }
                    ?.let { toPlanItem(it) }

                arrangeData(allPlans, yearlyDiscountActive, lifetimeDiscountActive, yearPlanFromAdmin, allPlanFromAdmin, originalYearFromOffering)

            } catch (e: Exception) {
                updateState_ { copy(error = R.string.something_went_wrong) }
            }
        }
    }

    fun makePurchase(activity: Activity) {
        val selected = state().sortedPlanList.getOrNull(state().selectedIndex) ?: return
        viewModelScope.launch {
            updateState_ { copy(isPurchasing = true) }
            try {
                val result = Purchases.sharedInstance.awaitPurchase(
                    PurchaseParams.Builder(activity, selected.rcPackage).build()
                )
                RevenueCatHelper.update(result.customerInfo)
                if (result.customerInfo.entitlements["premium"]?.isActive == true) {
                    submitToServer(result.storeTransaction, selected)
                    updateState_ { copy(isPurchasing = false, purchaseSuccess = true) }
                    loadData()
                } else {
                    updateState_ { copy(isPurchasing = false) }
                }
            } catch (e: PurchasesException) {
                updateState_ { copy(isPurchasing = false, error = R.string.something_went_wrong) }
            }
        }
    }

    private fun submitToServer(transaction: StoreTransaction?, plan: RcPlanItem) {
        viewModelScope.launch {
            val request = PurchasedPlanCheckRequest(
                arrayListOf(
                    GooglePurchasedPlanRequest(
                        google_plan_id = plan.sku,
                        google_order_id = transaction?.orderId ?: "",
                        is_lifetime_plan = plan.sku.contains("all"),
                        is_all_feature = true,
                        start_date = transaction?.purchaseTime ?: 0L,
                        end_date = 0L,
                        purchase_price = plan.price_amount_micros?.div(1_000_000.0) ?: 0.0,
                        purchase_currency = plan.rcPackage.product.price.currencyCode ?: "",
                        no_of_renewals = 0
                    )
                )
            )
            // Fire and forget — no error handling needed
            runCatching { abacusRepository.devicePurchaseVerify(request).collect {} }
        }
    }

    private fun arrangeData(
        plans: List<RcPlanItem>,
        yearlyDiscountActive: Boolean,
        lifetimeDiscountActive: Boolean,
        yearAdmin: PlanAssignFromAdminData?,
        allAdmin: PlanAssignFromAdminData?,
        originalYearFromOffering: RcPlanItem?
    ) {
        val skuList = plans.toMutableList()

        val originalYear = originalYearFromOffering ?: plans.find { AppConstants.Products.matches(it.sku, AppConstants.Products.year) }
        val originalLifetime = plans.find { it.sku == "com.abacus.all" }
        val originalMonth = plans.find { it.sku.contains("1month") && !it.sku.contains("trial") }
        val originalWeek = plans.find { it.sku.contains("week") }
        var showSubmit = true

        fun remove(contains: String) = skuList.removeAll { it.sku.contains(contains) }
        // Tolerant of the "<sku>:<basePlanId>" suffix Google Play Billing subscriptions
        // (yearly) carry -- see AppConstants.Products.matches.
        fun removeExact(sku: String) = skuList.removeAll { AppConstants.Products.matches(it.sku, sku) }

        if (allAdmin != null) {
            remove("week"); remove("1month"); remove("1year")
            removeExact("com.abacus.all"); showSubmit = false
        } else if (yearAdmin != null) {
            remove("week"); remove("1month")
            removeExact("com.abacus.puzzle.1year"); remove("all")
            showSubmit = false
        } else {
            // Monthly purchased: hide weekly (downgrade), keep yearly/lifetime for upgrade
            val monthlyPurchased = plans.any { it.sku.contains("1month") && it.isPurchase }
            if (monthlyPurchased) remove("week")

            val yearPurchased = plans.find { it.sku.contains("1year") && it.isPurchase }
            if (yearPurchased != null) {
                if (yearPurchased.sku == "com.abacus.puzzle.1year") removeExact("com.abacus.puzzle.1year.offer")
                else removeExact("com.abacus.puzzle.1year")
                remove("week"); remove("1month"); showSubmit = false
            } else {
                val hasYearOffer = skuList.any { AppConstants.Products.matches(it.sku, AppConstants.Products.yearOffer) }
                if (yearlyDiscountActive && hasYearOffer) removeExact(AppConstants.Products.year)
                else removeExact(AppConstants.Products.yearOffer)
            }

            val lifetimePurchased = plans.find { it.sku.contains("all") && it.isPurchase }
            if (lifetimePurchased != null) {
                if (lifetimePurchased.sku == "com.abacus.all") removeExact("com.abacus.all.offer")
                else removeExact("com.abacus.all")
                remove("week"); remove("1month"); remove("1year"); showSubmit = false
            } else {
                val hasLifetimeOffer = skuList.any { it.sku == AppConstants.Products.lifetimeOffer }
                if (lifetimeDiscountActive && hasLifetimeOffer) removeExact(AppConstants.Products.lifetime)
                else removeExact(AppConstants.Products.lifetimeOffer)
            }
        }

        val purchasedIndex = skuList.indexOfFirst { it.isPurchase }.takeIf { it >= 0 } ?: 0

        updateState_ {
            copy(
                sortedPlanList = skuList,
                selectedIndex = purchasedIndex,
                original1YearData = originalYear,
                originalLifetimeData = originalLifetime,
                original1MonthData = originalMonth,
                original1WeekData = originalWeek,
                showSubmitButton = showSubmit
            )
        }
    }

    private fun formatPrice(amountMicros: Long, currencyCode: String): String {
        return try {
            val format = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.getDefault())
            format.currency = java.util.Currency.getInstance(currencyCode)
            format.maximumFractionDigits = 0
            format.format(amountMicros / 1_000_000.0)
        } catch (e: Exception) {
            amountMicros.div(1_000_000).toString()
        }
    }

    private fun billingPeriodFor(type: PackageType): String? = when (type) {
        PackageType.WEEKLY -> "P1W"
        PackageType.MONTHLY -> "P1M"
        PackageType.THREE_MONTH -> "P3M"
        PackageType.ANNUAL -> "P1Y"
        PackageType.LIFETIME -> null
        else -> null
    }

    fun onPlanSelected(index: Int) = updateState_ { copy(selectedIndex = index) }
    fun onShowOldSubClick() = updateState_ { copy(showOldSubscriptionPopup = true) }
    fun oldSubPopupClose() = updateState_ { copy(showOldSubscriptionPopup = false) }

    override fun onFailure(throwable: Throwable) {
        updateState_ { copy(error = R.string.something_went_wrong) }
    }
}
