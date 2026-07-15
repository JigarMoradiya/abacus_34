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
        val discountPer = prefManager.getCustomParamInt(AppConstants.RemoteConfig.discountPer, 0)
        val discountLifetime = prefManager.getCustomParamInt(AppConstants.RemoteConfig.discountPerLifeTime, 0)

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
                discountPer = discountPer,
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
                var packages = if (displayPlanIds.isEmpty()) allPackages
                               else allPackages.filter { pkg ->
                                   displayPlanIds.any { id -> pkg.product.id == id || pkg.product.id.startsWith("$id:") }
                               }
                val customerInfo = Purchases.sharedInstance.awaitCustomerInfo()
                RevenueCatHelper.update(customerInfo)
                val premiumEntitlement = customerInfo.entitlements["premium"]
                val activeProdId: String? = if (premiumEntitlement?.isActive == true) premiumEntitlement?.productIdentifier else null
                val allPurchasedIds = customerInfo.allPurchasedProductIds


                // Always show the user's active plan even if not in Firebase config
                val effectivePurchasedId = activeProdId
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
                val allPlans = packages.map { pkg ->
                    val matchesActive = activeProdId != null && (
                        pkg.product.id == activeProdId ||
                        pkg.product.id.startsWith("$activeProdId:") ||
                        pkg.product.id.endsWith(":$activeProdId")
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
                    val purchaseTime = allPurchasedIds
                        .firstOrNull { id -> pkg.product.id == id || pkg.product.id.startsWith("$id:") || pkg.product.id.endsWith(":$id") }
                        ?.let { id -> customerInfo.getPurchaseDateForProductId(id)?.time ?: 0L }
                        ?: 0L
                    RcPlanItem(
                        sku = pkg.product.id,
                        price = formatPrice(pkg.product.price.amountMicros, pkg.product.price.currencyCode),
                        price_amount_micros = pkg.product.price.amountMicros,
                        type = if (pkg.packageType == PackageType.LIFETIME) "inapp" else "subs",
                        isPurchase = matchesActive || matchesAllPurchased,
                        billingPeriod = billingPeriodFor(pkg.packageType),
                        purchaseTime = purchaseTime,
                        rcPackage = pkg
                    )
                }.sortedBy { it.price_amount_micros }

                arrangeData(allPlans, discountPer, discountLifetime, yearPlanFromAdmin, allPlanFromAdmin)

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
        discountPer: Int,
        discountPerLifetime: Int,
        yearAdmin: PlanAssignFromAdminData?,
        allAdmin: PlanAssignFromAdminData?
    ) {
        val skuList = plans.toMutableList()

        val originalYear = plans.find { it.sku == "com.abacus.puzzle.1year" }
        val originalLifetime = plans.find { it.sku == "com.abacus.all" }
        val originalMonth = plans.find { it.sku.contains("1month") && !it.sku.contains("trial") }
        val originalWeek = plans.find { it.sku.contains("week") }
        var showSubmit = true

        fun remove(contains: String) = skuList.removeAll { it.sku.contains(contains) }
        fun removeExact(sku: String) = skuList.removeAll { it.sku == sku }

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
                val hasYearOffer = skuList.any { it.sku == "com.abacus.puzzle.1year.offer" }
                if (discountPer > 0 && hasYearOffer) removeExact("com.abacus.puzzle.1year")
                else removeExact("com.abacus.puzzle.1year.offer")
            }

            val lifetimePurchased = plans.find { it.sku.contains("all") && it.isPurchase }
            if (lifetimePurchased != null) {
                if (lifetimePurchased.sku == "com.abacus.all") removeExact("com.abacus.all.offer")
                else removeExact("com.abacus.all")
                remove("week"); remove("1month"); remove("1year"); showSubmit = false
            } else {
                val hasLifetimeOffer = skuList.any { it.sku == "com.abacus.all.offer" }
                if (discountPerLifetime > 0 && hasLifetimeOffer) removeExact("com.abacus.all")
                else removeExact("com.abacus.all.offer")
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
