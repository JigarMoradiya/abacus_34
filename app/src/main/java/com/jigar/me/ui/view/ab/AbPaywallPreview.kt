package com.jigar.me.ui.view.ab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jigar.me.ui.view.home.common_ui.dialogs.PaywallContext
import com.jigar.me.ui.view.home.common_ui.dialogs.PaywallKitContent
import com.jigar.me.ui.view.home.common_ui.sheets.KidsBottomSheet
import com.jigar.me.ui.view.home.screens.purchase.components.PurchaseScreen
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.PurchaseUiState
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.RcPlanItem
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PackageType
import com.revenuecat.purchases.PresentedOfferingContext
import com.revenuecat.purchases.models.Period
import com.revenuecat.purchases.models.Price
import com.revenuecat.purchases.models.PricingPhase
import com.revenuecat.purchases.models.RecurrenceMode
import com.revenuecat.purchases.models.TestStoreProduct

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
        benefitList = listOf(
            "⭐ <strong>Get unlimited access</strong> to all Abacus Levels, Exercises, Exams and Custom Challenge Modes Module.",
            "🧮 Practice Addition, Subtraction, Multiplication, Division, with <strong>smart bead directions</strong> and <strong>formula on every steps.</strong>",
            "🎯 Prepare for math competitions, UCMAS and abacus exams with <strong>real exam-style practice.</strong>",
            "📊 <strong>Track your child's</strong> progress, speed and accuracy with detailed reports.",
        ),
        socialProofText = "Join thousands of students learning with Vedaavi Abacus",
    )
}

@Composable
fun AbPaywallPreview() {
    val state = sampleState()
    Box(Modifier.fillMaxSize()) {
        AbBackground(AbWorld.Meadow, Modifier.fillMaxSize())
        KidsBottomSheet(visible = true, onDismiss = {}, widthFraction = 0.85f, showHandle = false) {
            PaywallKitContent(
                headline = "You've reached Level 3!", subtitle = "Upgrade to keep learning", socialProof = state.socialProofText,
                uiState = state, onClose = {}, onSeeBenefits = {}, onRetry = {}, onSubscribe = {},
                privacyUrl = "https://example.com/privacy",
            )
        }
    }
}

@Composable
fun AbPurchasePreview() {
    Box(Modifier.fillMaxSize()) {
        AbBackground(AbWorld.Sunset, Modifier.fillMaxSize())
        PurchaseScreen(
            uiState = sampleState(), onPlanSelected = {}, onShowOldSubClick = {}, oldSubPopupCloseClick = {},
            onSubscribe = {}, onClose = {}, privacyUrl = "https://example.com/privacy",
        )
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
        PurchaseScreen(
            uiState = sampleState().copy(sortedPlanList = listOf(bought, lifetime), selectedIndex = 1, original1YearData = bought, original1MonthData = null),
            onPlanSelected = {}, onShowOldSubClick = {}, oldSubPopupCloseClick = {},
            onSubscribe = {}, onClose = {}, privacyUrl = "https://example.com/privacy",
        )
    }
}

/** Debug preview of the first-launch tutorial with sample plans on its last page: `ab_screen=onboarding1..6`. */
@Composable
fun AbOnboardingPreview(page: Int) {
    com.jigar.me.ui.view.home.screens.onboarding.OnboardingTutorialScreen(
        selectedTheme = com.jigar.me.utils.AppConstants.Settings.theam_Default,
        onFinish = {},
        startPage = page - 1,
    ) { left, _ ->
        com.jigar.me.ui.view.home.common_ui.dialogs.PaywallKitEmbeddedLayout(left = left) {
            // a fresh install: the yearly plan first with the store's intro price (lifetime is hidden on this page)
            val fresh = sampleState()
            com.jigar.me.ui.view.home.common_ui.dialogs.PaywallKitPlans(
                uiState = fresh, onRetry = {}, onSubscribe = {},
                privacyUrl = "https://example.com/privacy", firstLaunch = true,
            )
        }
    }
}
