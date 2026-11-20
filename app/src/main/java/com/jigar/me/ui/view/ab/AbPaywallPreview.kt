package com.jigar.me.ui.view.ab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jigar.me.ui.view.home.common_ui.dialogs.PaywallContext
import com.jigar.me.ui.view.home.common_ui.dialogs.PaywallKitContent
import com.jigar.me.ui.view.home.common_ui.sheets.KidsBottomSheet
import com.jigar.me.ui.view.home.screens.purchase.components.PurchaseScreen
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.PurchaseUiState
import com.jigar.me.utils.StoreOneTimeOffer
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.RcPlanItem
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PackageType
import com.revenuecat.purchases.PresentedOfferingContext
import com.revenuecat.purchases.models.Period
import com.revenuecat.purchases.models.Price
import com.revenuecat.purchases.models.PricingPhase
import com.revenuecat.purchases.models.RecurrenceMode
import com.revenuecat.purchases.models.TestStoreProduct
import com.jigar.me.R

/**
 * Debug previews of the two paywalls with sample plans (the real offerings need the store): `ab_screen=paywall|purchase`.
 * [intro] is a paid intro price the store offers for the first year (a Google Play offer on the base plan): formatted + micros.
 */
private fun samplePlan(
    sku: String, formatted: String, micros: Long, type: PackageType, purchased: Boolean = false, intro: Pair<String, Long>? = null,
    purchasedAt: Long = 0L, orderId: String? = null,
): RcPlanItem {
    val period = when {
        sku.contains("1year") -> Period(1, Period.Unit.YEAR, "P1Y")
        sku.contains("1month") -> Period(1, Period.Unit.MONTH, "P1M")
        else -> null
    }
    val introPhase = intro?.let { PricingPhase(Period(1, Period.Unit.YEAR, "P1Y"), RecurrenceMode.FINITE_RECURRING, 1, Price(it.first, it.second, "INR")) }
    val product = TestStoreProduct(sku, "Plan", "Sample plan", "Sample plan", Price(formatted, micros, "INR"), period, null, introPhase)
    return RcPlanItem(
        sku = sku, price = formatted, price_amount_micros = micros, type = "subs", isPurchase = purchased,
        billingPeriod = if (sku.contains("1year")) "P1Y" else null, purchaseTime = purchasedAt,
        rcPackage = Package(sku, type, product, PresentedOfferingContext("preview")),
        orderId = orderId,
    )
}

/** The timed Home offer (`ab_screen=paywalloffer` / `purchaseoffer`): the ".offer" yearly plan is shown INSTEAD of the regular one, 10 minutes left. */
private fun sampleOfferState(): PurchaseUiState {
    val base = sampleState(withIntro = false)
    val yearOffer = samplePlan("com.abacus.puzzle.1year.offer", "₹840", 840_000_000, PackageType.ANNUAL)
    return base.copy(
        sortedPlanList = listOf(base.sortedPlanList[0], yearOffer, base.sortedPlanList[2]),
        selectedIndex = 1,
        discountPer = 30,
        homeOfferYearlyEndMillis = System.currentTimeMillis() + 10 * 60_000L,
    )
}

/**
 * The "Biggest offer" campaign (`ab_screen=paywallbiggest` / `purchasebiggest` / `onboardingbiggest`), with the India Android store numbers:
 * monthly 199, yearly 1,299 (struck through) replaced by an ".offer" plan (499 here, only an example: the real price is the store price of
 * the `.offer` product), lifetime 3,800; monthly and lifetime are shown disabled. The `...life` routes run it on LIFETIME instead (home_offer
 * product "lifetime"): lifetime 3,800 struck through and replaced by an example ".offer" 999, monthly and yearly disabled.
 */
private fun sampleCampaignState(onLifetime: Boolean = false): PurchaseUiState {
    val month = samplePlan("com.abacus.puzzle.1month", "₹199", 199_000_000, PackageType.MONTHLY)
    val year = samplePlan("com.abacus.puzzle.1year", "₹1,299", 1_299_000_000, PackageType.ANNUAL)
    val yearOffer = samplePlan("com.abacus.puzzle.1year.offer", "₹499", 499_000_000, PackageType.ANNUAL)
    val lifetime = samplePlan("com.abacus.all", "₹3,800", 3_800_000_000, PackageType.LIFETIME)
    val lifetimeOffer = samplePlan("com.abacus.all.offer", "₹999", 999_000_000, PackageType.LIFETIME)
    val endMillis = System.currentTimeMillis() + (5 * 24 + 3) * 3600_000L
    if (onLifetime) return PurchaseUiState(
        isLoading = false,
        selectedIndex = 2,
        sortedPlanList = listOf(month, year, lifetimeOffer),
        original1YearData = year,
        originalLifetimeData = lifetime,
        original1MonthData = month,
        benefitList = listOf(R.string.purchase_benefit_1, R.string.purchase_benefit_2, R.string.purchase_benefit_3, R.string.purchase_benefit_4),
        socialProofText = "Join thousands of students learning with Vedaavi Abacus",
        discountPerLifetime = com.jigar.me.utils.HomeOfferManager.discountPercent(3_800_000_000, 999_000_000),
        homeOfferLifetimeEndMillis = endMillis,
        campaignLock = true,
        campaignOnLifetime = true,
        disabledSkus = setOf(month.sku, year.sku),
    )
    return PurchaseUiState(
        isLoading = false,
        selectedIndex = 1,
        sortedPlanList = listOf(month, yearOffer, lifetime),
        original1YearData = year,
        originalLifetimeData = lifetime,
        original1MonthData = month,
        benefitList = listOf(R.string.purchase_benefit_1, R.string.purchase_benefit_2, R.string.purchase_benefit_3, R.string.purchase_benefit_4),
        socialProofText = "Join thousands of students learning with Vedaavi Abacus",
        discountPer = com.jigar.me.utils.HomeOfferManager.discountPercent(1_299_000_000, 499_000_000),
        homeOfferYearlyEndMillis = endMillis,
        campaignLock = true,
        disabledSkus = setOf(month.sku, lifetime.sku),
    )
}

/** A Google Play discount offer on the lifetime plan (`ab_screen=paywallstore` / `purchasestore`; `:2` = an absolute amount off). */
private fun sampleStoreOfferState(absolute: Boolean): PurchaseUiState {
    val base = sampleState(withIntro = false)
    return base.copy(
        lifetimeStoreOffer = StoreOneTimeOffer(
            offerPriceText = if (absolute) "₹4,250" else "₹3,325", fullPriceText = "₹4,750",
            percentOff = if (absolute) 11 else 30, amountOffText = if (absolute) "₹500" else null,
            offerToken = "sample", offerId = "sample", endMillis = null, priceMicros = if (absolute) 4_250_000_000 else 3_325_000_000, currencyCode = "INR",
        ),
    )
}

private fun sampleState(selected: Int = 0, withIntro: Boolean = true): PurchaseUiState {
    // the India store numbers of the plan page: monthly, the regular yearly (1,200; with a store intro price of 840 for the
    // first year = 30% off) and lifetime
    val month = samplePlan("com.abacus.puzzle.1month", "₹199", 199_000_000, PackageType.MONTHLY)
    val year = samplePlan("com.abacus.puzzle.1year", "₹1,200", 1_200_000_000, PackageType.ANNUAL, intro = if (withIntro) "₹840" to 840_000_000L else null)
    val lifetime = samplePlan("com.abacus.all", "₹4,750", 4_750_000_000, PackageType.LIFETIME)
    return PurchaseUiState(
        isLoading = false,
        selectedIndex = selected,
        sortedPlanList = listOf(month, year, lifetime),
        original1YearData = year,
        originalLifetimeData = lifetime,
        original1MonthData = month,
        benefitList = listOf(R.string.purchase_benefit_1, R.string.purchase_benefit_2, R.string.purchase_benefit_3, R.string.purchase_benefit_4),
        socialProofText = "Join thousands of students learning with Vedaavi Abacus",
    )
}

@Composable
fun AbPaywallPreview(timedOffer: Boolean = false, storeOffer: Int = 0, campaign: Boolean = false, campaignLifetime: Boolean = false) {
    val state = if (campaign) sampleCampaignState(campaignLifetime) else if (timedOffer) sampleOfferState() else if (storeOffer > 0) sampleStoreOfferState(storeOffer == 2) else sampleState()
    Box(Modifier.fillMaxSize()) {
        AbBackground(AbWorld.Meadow, Modifier.fillMaxSize())
        KidsBottomSheet(visible = true, onDismiss = {}, widthFraction = 0.85f, showHandle = false) {
            PaywallKitContent(
                headline = androidx.compose.ui.res.stringResource(com.jigar.me.ui.view.home.common_ui.dialogs.PaywallContext.LEVEL3.headlineRes), subtitle = androidx.compose.ui.res.stringResource(com.jigar.me.ui.view.home.common_ui.dialogs.PaywallContext.LEVEL3.subtitleRes), socialProof = state.socialProofText,
                uiState = state, onClose = {}, onSeeBenefits = {}, onRetry = {}, onSubscribe = {},
                privacyUrl = "https://example.com/privacy",
            )
        }
    }
}

@Composable
fun AbPurchasePreview(timedOffer: Boolean = false, storeOffer: Int = 0, campaign: Boolean = false, campaignLifetime: Boolean = false) {
    Box(Modifier.fillMaxSize()) {
        AbBackground(AbWorld.Sunset, Modifier.fillMaxSize())
        // inside the safe area, like the real route (PurchaseScreenRoute)
        Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            PurchaseScreen(
                uiState = if (campaign) sampleCampaignState(campaignLifetime) else if (timedOffer) sampleOfferState() else if (storeOffer > 0) sampleStoreOfferState(storeOffer == 2) else sampleState(), onPlanSelected = {}, onShowOldSubClick = {}, oldSubPopupCloseClick = {},
                onSubscribe = {}, onClose = {}, privacyUrl = "https://example.com/privacy",
            )
        }
    }
}

/**
 * Debug preview of the full purchase screen for a customer who already has the yearly plan (`ab_screen=purchasebought`):
 * the yearly card shows its purchase date and order id, monthly and weekly are gone, and Lifetime is preselected so it can still be bought.
 */
@Composable
fun AbPurchaseBoughtPreview() {
    val bought = samplePlan("com.abacus.puzzle.1year", "₹1,200", 1_200_000_000, PackageType.ANNUAL, purchased = true,
        purchasedAt = System.currentTimeMillis() - 40L * 24 * 3600 * 1000, orderId = "GPA.3399-1234-5678-90123")
    val lifetime = samplePlan("com.abacus.all", "₹4,750", 4_750_000_000, PackageType.LIFETIME)
    Box(Modifier.fillMaxSize()) {
        AbBackground(AbWorld.Sunset, Modifier.fillMaxSize())
        Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            PurchaseScreen(
                uiState = sampleState().copy(sortedPlanList = listOf(bought, lifetime), selectedIndex = 1, original1YearData = bought, original1MonthData = null),
                onPlanSelected = {}, onShowOldSubClick = {}, oldSubPopupCloseClick = {},
                onSubscribe = {}, onClose = {}, privacyUrl = "https://example.com/privacy",
            )
        }
    }
}

/** Debug preview of the first-launch tutorial with sample plans on its last page: `ab_screen=onboarding1..6`. */
@Composable
fun AbOnboardingPreview(page: Int, campaign: Boolean = false, campaignLifetime: Boolean = false) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper.current
    com.jigar.me.ui.view.home.screens.onboarding.OnboardingTutorialScreen(
        selectedTheme = com.jigar.me.utils.AppConstants.Settings.theam_Default,
        // finishing (Continue free for now, a skip on the last page) opens the real app, like the first launch does; it used to do nothing here
        onFinish = {
            com.jigar.me.ui.view.home.screens.onboarding.OnboardingGate.markSeen(prefs)
            context.startActivity(
                android.content.Intent(context, com.jigar.me.ui.view.home.HomeActivity::class.java)
                    .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        },
        startPage = page - 1,
    ) { left, footer, _ ->
        com.jigar.me.ui.view.home.common_ui.dialogs.PaywallKitEmbeddedLayout(left = left, footer = footer) {
            // a fresh install: the yearly plan first with the store's intro price (lifetime is hidden on this page)
            val fresh = if (campaign) sampleCampaignState(campaignLifetime) else sampleState()
            com.jigar.me.ui.view.home.common_ui.dialogs.PaywallKitPlans(
                uiState = fresh, onRetry = {}, onSubscribe = {},
                privacyUrl = "https://example.com/privacy", firstLaunch = true,
            )
        }
    }
}
