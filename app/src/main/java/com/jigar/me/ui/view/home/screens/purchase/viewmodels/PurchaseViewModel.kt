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
import com.jigar.me.utils.StoreOfferPurchaser
import com.jigar.me.utils.StoreOneTimeOffer
import com.jigar.me.utils.StoreOneTimeOffers
import com.revenuecat.purchases.awaitSyncPurchases
import com.revenuecat.purchases.models.GoogleStoreProduct
import com.jigar.me.utils.RevenueCatHelper
import com.jigar.me.utils.AppAnalytics
import com.revenuecat.purchases.PurchasesErrorCode
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PackageType
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.PurchasesException
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.awaitOfferings
import com.revenuecat.purchases.awaitCustomerInfo
import com.revenuecat.purchases.awaitPurchase
import com.revenuecat.purchases.awaitRestore
import com.revenuecat.purchases.models.StoreTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.jigar.me.utils.RemoteText

@HiltViewModel
class PurchaseViewModel @Inject constructor(
    private val prefManager: AppPreferencesHelper,
    private val abacusRepository: AbacusRepository,
) : StatefulViewModel<PurchaseUiState>() {

    override val TAG = "PurchaseViewModel"

    override fun getInitialState() = PurchaseUiState(
        benefitList = listOf(R.string.purchase_benefit_1, R.string.purchase_benefit_2, R.string.purchase_benefit_3, R.string.purchase_benefit_4)
    )

    private var lastOfferingId = ""
    private var offerExpiryJob: kotlinx.coroutines.Job? = null

    /** When the timed offer's window ends, load the plans again so the regular plan comes back (the countdown pills tick on their own). */
    private fun scheduleOfferExpiry(now: Long) {
        offerExpiryJob?.cancel()
        val end = HomeOfferManager.endMillis(prefManager, now) ?: return
        offerExpiryJob = viewModelScope.launch {
            kotlinx.coroutines.delay((end - System.currentTimeMillis()).coerceAtLeast(0L) + 500L)
            loadData()
        }
    }
    fun isUserLoggedIn(): Boolean = prefManager.isUserLoggedIn()
    fun privacyPolicyUrl(): String = prefManager.getCustomParam(com.jigar.me.utils.AppConstants.RemoteConfig.privacyPolicyUrl, "")
    fun isUserSubscribed(): Boolean = CommonUtils.checkPurchaseForExerciseExamCCM(prefManager)

    fun loadData() {
        // Normal times: only the REGULAR products are shown and any discount comes from the store (a Google Play offer on
        // the base plan, read through RevenueCat: see storeIntroOffer()). The one exception is the timed Home offer
        // (Remote Config `home_offer`, enabled + a duration): while it runs, the ".offer" product of the plan it targets is
        // shown INSTEAD of the regular one, with its % off, the original price and a countdown.
        // Pinned to one instant so the plan choice and the % cannot disagree if the window ends while the plans load.
        val decisionNow = System.currentTimeMillis()
        val timedOfferRunning = HomeOfferManager.isTimedOfferActive(prefManager, decisionNow)
        val yearlyDiscountActive = timedOfferRunning && !HomeOfferManager.config(prefManager).targetsLifetime
        val lifetimeDiscountActive = timedOfferRunning && HomeOfferManager.config(prefManager).targetsLifetime

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
                // Removed: the store provides the discount
                // discountPer = prefManager.getCustomParamInt(AppConstants.RemoteConfig.discountPer, 0),
                // discountPerLifetime = discountLifetime,
                planListAssignFromAdmin = adminPlanList,
                yearPlanAssignFromAdmin = yearPlanFromAdmin,
                allPlanAssignFromAdmin = allPlanFromAdmin,
                socialProofText = RemoteText.read(prefManager, AppConstants.RemoteConfig.paywallSocialProof),
                isLoading = true,
                error = null,
            )
        }

        val displayPlanJson = prefManager.getCustomParam(AppConstants.RemoteConfig.displayPlanList, "")
        val displayPlanIds: Set<String> = if (displayPlanJson.length > 5) {
            // a typo in the Remote Config list must not crash the paywall: a list that cannot be read shows every plan
            runCatching {
                val type = object : TypeToken<List<Map<String, Any>>>() {}.type
                val list: List<Map<String, Any>> = Gson().fromJson(displayPlanJson, type)
                // an id of a legacy ".offer" product whitelists the regular product behind it
                list.mapNotNull { it["id"] as? String }.map { AppConstants.Products.withoutOffer(it) }.toSet()
            }.getOrDefault(emptySet())
        } else emptySet()

        viewModelScope.launch {
            try {
                val offerings = Purchases.sharedInstance.awaitOfferings()
                // Only the REGULAR products are shown, except the ".offer" product of the timed Home offer while it runs: any
                // other legacy ".offer" product is dropped even if the RevenueCat offering still contains it.
                // The timed offer is self-contained (enabled + product decide everything): its base / .offer pair is shown
                // whatever display_plan says, so the Home card never promises a plan the paywall then hides.
                val timedOfferIds = HomeOfferManager.targetProductIds(prefManager, decisionNow)
                fun isTimedOfferId(productId: String) = timedOfferIds.any { id -> AppConstants.Products.matches(productId, id) }
                val allPackages = (offerings.current?.availablePackages ?: emptyList())
                    .filter { !AppConstants.Products.isOffer(it.product.id) || isTimedOfferId(it.product.id) }
                lastOfferingId = offerings.current?.identifier ?: ""
                var packages = if (displayPlanIds.isEmpty()) allPackages
                               else allPackages.filter { pkg ->
                                   displayPlanIds.any { id -> pkg.product.id == id || pkg.product.id.startsWith("$id:") } ||
                                       isTimedOfferId(pkg.product.id)
                               }
                // The % off while the timed offer runs comes from the real base vs .offer store prices, so it is what the store
                // charges (also per country); 0 when it does not run for that plan.
                fun micros(id: String) = allPackages.firstOrNull { AppConstants.Products.matches(it.product.id, id) }?.product?.price?.amountMicros
                updateState_ {
                    copy(
                        discountPer = if (yearlyDiscountActive) HomeOfferManager.discountPercent(micros(AppConstants.Products.year), micros(AppConstants.Products.yearOffer)) else 0,
                        discountPerLifetime = if (lifetimeDiscountActive) HomeOfferManager.discountPercent(micros(AppConstants.Products.lifetime), micros(AppConstants.Products.lifetimeOffer)) else 0,
                        homeOfferYearlyEndMillis = HomeOfferManager.yearlyEndMillis(prefManager, decisionNow),
                        homeOfferLifetimeEndMillis = HomeOfferManager.lifetimeEndMillis(prefManager, decisionNow),
                    )
                }
                scheduleOfferExpiry(decisionNow)
                // A Google Play discount offer on the regular lifetime plan (Play Console: Purchase options and offers > Add offer > Discount).
                // Never together with the timed ".offer" plan: while that one replaces the lifetime plan there is nothing to read here.
                val lifetimePackage = allPackages.firstOrNull { it.product.id == AppConstants.Products.lifetime }
                val storeOffer = if (!lifetimeDiscountActive && StoreOneTimeOffers.enabled(prefManager.getCustomParam(AppConstants.RemoteConfig.storeOneTimeOffers, "")))
                    StoreOneTimeOffers.read((lifetimePackage?.product as? GoogleStoreProduct)?.productDetails) else null
                updateState_ { copy(lifetimeStoreOffer = storeOffer) }
                val customerInfo = Purchases.sharedInstance.awaitCustomerInfo()
                RevenueCatHelper.update(customerInfo)
                val premiumEntitlement = customerInfo.entitlements["premium"]
                val activeProdId: String? = if (premiumEntitlement?.isActive == true) premiumEntitlement?.productIdentifier else null
                // "com.abacus.puzzle.onetime" is a legacy pre-RevenueCat lifetime purchase --
                // it's not part of any current RC offering, so it can never match a Package
                // and the paywall would show NO card as Purchased even though the entitlement
                // is active. Remap it to today's lifetime SKU for matching purposes only.
                // Android-only: iOS never sold this SKU, so it has no such legacy product.
                // A subscriber of an old ".offer" product (sold before the store-side discounts) keeps their access and
                // sees the REGULAR plan behind it as Subscribed / Purchased, so ids from the customer are matched
                // without ".offer" (the date lookup below still uses the original id).
                val activeProdIdForMatching: String? = if (activeProdId == "com.abacus.puzzle.onetime") {
                    AppConstants.Products.lifetime
                } else {
                    activeProdId?.let { AppConstants.Products.withoutOffer(it) }
                }
                val allPurchasedIds = customerInfo.allPurchasedProductIds
                val allPurchasedMatchIds = allPurchasedIds.map { AppConstants.Products.withoutOffer(it) }

                // Always show the user's active plan even if not in Firebase config
                val effectivePurchasedId = activeProdIdForMatching
                    ?: allPurchasedMatchIds.firstOrNull { id -> allPackages.any { pkg -> pkg.product.id == id || pkg.product.id.startsWith("$id:") } }
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

                val activeSubIds = customerInfo.activeSubscriptions.map { AppConstants.Products.withoutOffer(it) }
                fun toPlanItem(pkg: Package): RcPlanItem {
                    val matchesActive = activeProdIdForMatching != null && (
                        pkg.product.id == activeProdIdForMatching ||
                        pkg.product.id.startsWith("$activeProdIdForMatching:") ||
                        pkg.product.id.endsWith(":$activeProdIdForMatching")
                    )
                    // For subscriptions use activeSubscriptions (expired subs are excluded).
                    // For lifetime (INAPP) use allPurchasedProductIds since they never expire.
                    val matchesAllPurchased = if (pkg.packageType == PackageType.LIFETIME) {
                        allPurchasedMatchIds.any { id ->
                            pkg.product.id == id || pkg.product.id.startsWith("$id:") || pkg.product.id.endsWith(":$id")
                        }
                    } else {
                        activeSubIds.any { id ->
                            pkg.product.id == id || pkg.product.id.startsWith("$id:") || pkg.product.id.endsWith(":$id")
                        }
                    }
                    val purchasedIdForDate = allPurchasedIds
                        .firstOrNull { rawId ->
                            val id = AppConstants.Products.withoutOffer(rawId)
                            pkg.product.id == id || pkg.product.id.startsWith("$id:") || pkg.product.id.endsWith(":$id")
                        }
                        // This package can stand in for a legacy purchase under a different
                        // SKU (e.g. "com.abacus.puzzle.onetime" remapped to today's lifetime
                        // plan above) -- allPurchasedIds has the LEGACY id, not this package's
                        // id, so fall back to the real underlying id for the date lookup.
                        ?: activeProdId?.takeIf { pkg.product.id == activeProdIdForMatching && activeProdId != activeProdIdForMatching }
                    val purchaseTime = purchasedIdForDate
                        ?.let { id -> customerInfo.getPurchaseDateForProductId(id)?.time ?: 0L }
                        ?: 0L
                    // the store order (transaction) id of this plan's purchase: the latest lifetime transaction, or the subscription's
                    fun idMatches(rawId: String): Boolean {
                        val id = AppConstants.Products.withoutOffer(rawId)
                        return pkg.product.id == id || pkg.product.id.startsWith("$id:") || pkg.product.id.endsWith(":$id")
                    }
                    val orderId: String? = if (pkg.packageType == PackageType.LIFETIME) {
                        customerInfo.nonSubscriptionTransactions
                            .filter { idMatches(it.productIdentifier) }
                            .maxByOrNull { it.purchaseDate }?.storeTransactionId
                    } else {
                        customerInfo.subscriptionsByProductIdentifier.entries
                            .firstOrNull { idMatches(it.key) }?.value?.storeTransactionId
                    }
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
                        rcPackage = pkg,
                        orderId = orderId
                    )
                }
                val allPlans = packages.map { toPlanItem(it) }.sortedBy { it.price_amount_micros }

                arrangeData(allPlans, yearlyDiscountActive, lifetimeDiscountActive, yearPlanFromAdmin, allPlanFromAdmin)
                updateState_ { copy(isLoading = false) }

            } catch (e: Exception) {
                updateState_ { copy(error = R.string.something_went_wrong, isLoading = false) }
            }
        }
    }

    fun makePurchase(activity: Activity, surface: String = "purchase_screen", context: String = "full") {
        val selected = state().sortedPlanList.getOrNull(state().selectedIndex) ?: return
        if (state().isPurchasing) return   // a second tap while the store sheet is opening
        viewModelScope.launch {
            updateState_ { copy(isPurchasing = true) }
            val analytics = arrayOf("surface" to surface, "context" to context, "product_id" to selected.sku)
            AppAnalytics.log(AppAnalytics.PURCHASE_STARTED, *analytics)
            // the lifetime plan WITH a Play discount offer: RevenueCat would charge the full price, so Google Play is called directly
            val storeOffer = state().lifetimeStoreOffer?.takeIf { selected.sku == AppConstants.Products.lifetime }
            if (storeOffer != null) {
                buyWithStoreOffer(activity, selected, storeOffer, analytics)
                return@launch
            }
            try {
                val result = Purchases.sharedInstance.awaitPurchase(
                    PurchaseParams.Builder(activity, selected.rcPackage).build()
                )
                RevenueCatHelper.update(result.customerInfo)
                if (result.customerInfo.entitlements["premium"]?.isActive == true) {
                    AppAnalytics.log(
                        AppAnalytics.PURCHASE_COMPLETED, *analytics,
                        "price" to (selected.price_amount_micros?.div(1_000_000.0) ?: 0.0),
                        "currency" to selected.rcPackage.product.price.currencyCode
                    )
                    submitToServer(result.storeTransaction, selected)
                    updateState_ { copy(isPurchasing = false, purchaseSuccess = true) }
                    loadData()
                } else {
                    // not active yet: a pending (slow payment method) purchase, or an entitlement that has not synced
                    AppAnalytics.log(AppAnalytics.PURCHASE_NOT_ACTIVE, *analytics)
                    updateState_ { copy(isPurchasing = false, message = R.string.purchase_pending) }
                }
            } catch (e: PurchasesException) {
                if (e.error.code == PurchasesErrorCode.PurchaseCancelledError) {
                    AppAnalytics.log(AppAnalytics.PURCHASE_CANCELLED, *analytics)
                } else {
                    AppAnalytics.log(AppAnalytics.PURCHASE_FAILED, *analytics, "error_code" to e.error.code.code)
                }
                // say what happened (a pending payment, purchases blocked on this device, already owned, no network)
                val message = when (e.error.code) {
                    PurchasesErrorCode.PurchaseCancelledError -> null
                    PurchasesErrorCode.PaymentPendingError -> R.string.purchase_pending
                    PurchasesErrorCode.PurchaseNotAllowedError -> R.string.purchase_not_allowed
                    PurchasesErrorCode.ProductAlreadyPurchasedError -> R.string.purchase_already_owned
                    PurchasesErrorCode.NetworkError -> R.string.purchase_network
                    else -> R.string.something_went_wrong
                }
                updateState_ { copy(isPurchasing = false, message = message) }
                // already owned: the purchase exists on the store but not on this account yet, so restore it now
                if (e.error.code == PurchasesErrorCode.ProductAlreadyPurchasedError) {
                    runCatching { RevenueCatHelper.update(Purchases.sharedInstance.awaitRestore()) }
                    loadData()
                }
            }
        }
    }

    fun clearMessage() = updateState_ { copy(message = null) }

    /** Buys the lifetime plan at its Google Play offer price, then lets RevenueCat grant the access (`syncPurchases`). */
    private suspend fun buyWithStoreOffer(
        activity: Activity, selected: RcPlanItem, offer: StoreOneTimeOffer, analytics: Array<Pair<String, String>>,
    ) {
        val extra = arrayOf(*analytics, "store_offer" to (offer.offerId ?: "yes"))
        when (val result = StoreOfferPurchaser(activity).purchase(activity, selected.sku)) {
            is StoreOfferPurchaser.Result.Purchased -> {
                val info = runCatching { Purchases.sharedInstance.awaitSyncPurchases() }.getOrNull()
                    ?: runCatching { Purchases.sharedInstance.awaitCustomerInfo() }.getOrNull()
                if (info != null) RevenueCatHelper.update(info)
                if (info?.entitlements["premium"]?.isActive == true) {
                    AppAnalytics.log(AppAnalytics.PURCHASE_COMPLETED, *extra, "price" to offer.priceMicros / 1_000_000.0, "currency" to offer.currencyCode)
                    submitToServer(result.purchase.orderId, result.purchase.purchaseTime, selected, offer.priceMicros / 1_000_000.0, offer.currencyCode)
                    updateState_ { copy(isPurchasing = false, purchaseSuccess = true) }
                    loadData()
                } else {
                    AppAnalytics.log(AppAnalytics.PURCHASE_NOT_ACTIVE, *extra)
                    updateState_ { copy(isPurchasing = false, message = R.string.purchase_pending) }
                }
            }
            StoreOfferPurchaser.Result.Pending -> {
                AppAnalytics.log(AppAnalytics.PURCHASE_NOT_ACTIVE, *extra)
                updateState_ { copy(isPurchasing = false, message = R.string.purchase_pending) }
            }
            StoreOfferPurchaser.Result.Cancelled -> {
                AppAnalytics.log(AppAnalytics.PURCHASE_CANCELLED, *extra)
                updateState_ { copy(isPurchasing = false) }
            }
            StoreOfferPurchaser.Result.AlreadyOwned -> {
                updateState_ { copy(isPurchasing = false, message = R.string.purchase_already_owned) }
                runCatching { RevenueCatHelper.update(Purchases.sharedInstance.awaitRestore()) }
                loadData()
            }
            StoreOfferPurchaser.Result.OfferGone -> {
                // nothing was charged: the card is drawn again without the offer
                AppAnalytics.log(AppAnalytics.PURCHASE_FAILED, *extra, "error_code" to "offer_gone")
                updateState_ { copy(isPurchasing = false, message = R.string.paywall_offer_ended) }
                loadData()
            }
            is StoreOfferPurchaser.Result.Failed -> {
                AppAnalytics.log(AppAnalytics.PURCHASE_FAILED, *extra, "error_code" to result.code)
                updateState_ { copy(isPurchasing = false, message = if (result.code == 2 || result.code == -1) R.string.purchase_network else R.string.something_went_wrong) }
            }
        }
    }

    private fun submitToServer(transaction: StoreTransaction?, plan: RcPlanItem) =
        submitToServer(
            transaction?.orderId, transaction?.purchaseTime, plan,
            plan.price_amount_micros?.div(1_000_000.0) ?: 0.0, plan.rcPackage.product.price.currencyCode ?: ""
        )

    private fun submitToServer(orderId: String?, purchaseTime: Long?, plan: RcPlanItem, price: Double, currency: String) {
        viewModelScope.launch {
            val request = PurchasedPlanCheckRequest(
                arrayListOf(
                    GooglePurchasedPlanRequest(
                        google_plan_id = plan.sku,
                        google_order_id = orderId ?: "",
                        is_lifetime_plan = plan.sku.contains("all"),
                        is_all_feature = true,
                        start_date = purchaseTime ?: 0L,
                        end_date = 0L,
                        purchase_price = price,
                        purchase_currency = currency,
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
        allAdmin: PlanAssignFromAdminData?
    ) {
        val skuList = plans.toMutableList()

        val originalYear = plans.find { AppConstants.Products.matches(it.sku, AppConstants.Products.year) }
            ?: plans.find { it.sku.contains("1year") }
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
            // Monthly, Yearly and Weekly are separate Google Play subscriptions and no replacement mode is configured, so a second
            // subscription bought on top of an active one would bill BOTH: a subscriber is only offered Lifetime (like iOS).
            val monthlyPurchased = plans.any { it.sku.contains("1month") && it.isPurchase }
            if (monthlyPurchased) { remove("week"); remove("1year") }   // was: remove("week") only, "keep yearly for upgrade"
            val weeklyPurchased = plans.any { it.sku.contains("week") && it.isPurchase }
            if (weeklyPurchased) { remove("1month"); remove("1year") }

            // Yearly: while the timed Home offer targets yearly the ".offer" plan REPLACES the regular one; otherwise the
            // regular one stays and the ".offer" one is dropped. A buyer sees the regular plan as bought.
            val yearPurchased = plans.find { it.sku.contains("1year") && it.isPurchase }
            if (yearPurchased != null) {
                removeExact(AppConstants.Products.yearOffer)
                remove("week"); remove("1month"); showSubmit = false
            } else {
                val hasYearOffer = skuList.any { AppConstants.Products.matches(it.sku, AppConstants.Products.yearOffer) }
                if (yearlyDiscountActive && hasYearOffer) removeExact(AppConstants.Products.year)
                else removeExact(AppConstants.Products.yearOffer)
            }

            // Lifetime: the same for the timed offer that targets lifetime.
            val lifetimePurchased = plans.find { it.sku.contains("all") && it.isPurchase }
            if (lifetimePurchased != null) {
                removeExact(AppConstants.Products.lifetimeOffer)
                remove("week"); remove("1month"); remove("1year"); showSubmit = false
            } else {
                val hasLifetimeOffer = skuList.any { it.sku == AppConstants.Products.lifetimeOffer }
                if (lifetimeDiscountActive && hasLifetimeOffer) removeExact(AppConstants.Products.lifetime)
                else removeExact(AppConstants.Products.lifetimeOffer)
            }
        }

        // what can still be bought, the YEARLY plan first (best value; for a yearly subscriber that is Lifetime), else the plan
        // already bought when nothing is left to buy
        val buyable = skuList.withIndex().filter { !it.value.isPurchase }
        val purchasedIndex = buyable.firstOrNull { it.value.sku.contains("1year") }?.index
            ?: buyable.firstOrNull()?.index
            ?: skuList.indexOfFirst { it.isPurchase }.takeIf { it >= 0 } ?: 0

        updateState_ {
            copy(
                sortedPlanList = skuList,
                offeringId = lastOfferingId,
                selectedIndex = purchasedIndex,
                original1YearData = originalYear,
                originalLifetimeData = originalLifetime,
                original1MonthData = originalMonth,
                original1WeekData = originalWeek,
                showSubmitButton = showSubmit
            )
        }
    }

    private fun formatPrice(amountMicros: Long, currencyCode: String): String = formatPlanPrice(amountMicros, currencyCode)

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
