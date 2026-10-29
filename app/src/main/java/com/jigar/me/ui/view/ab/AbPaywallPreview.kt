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
import com.revenuecat.purchases.models.Price
import com.revenuecat.purchases.models.TestStoreProduct

/** Debug previews of the two paywalls with sample plans (the real offerings need the store): `ab_screen=paywall|purchase`. */
private fun samplePlan(sku: String, formatted: String, micros: Long, type: PackageType, purchased: Boolean = false): RcPlanItem {
    val product = TestStoreProduct(sku, "Plan", "Sample plan", Price(formatted, micros, "INR"), null)
    return RcPlanItem(
        sku = sku, price = formatted, price_amount_micros = micros, type = "subs", isPurchase = purchased,
        billingPeriod = null, purchaseTime = 0L,
        rcPackage = Package(sku, type, product, PresentedOfferingContext("preview")),
    )
}

private fun sampleState(selected: Int = 0): PurchaseUiState {
    // the India store numbers of the plan page: monthly, the yearly timed offer (from 1,200, 30% off) and lifetime
    val month = samplePlan("com.abacus.puzzle.1month", "₹199", 199_000_000, PackageType.MONTHLY)
    val year = samplePlan("com.abacus.puzzle.1year.offer", "₹840", 840_000_000, PackageType.ANNUAL)
    val lifetime = samplePlan("com.abacus.all", "₹4,750", 4_750_000_000, PackageType.LIFETIME)
    return PurchaseUiState(
        isLoading = false,
        selectedIndex = selected,
        sortedPlanList = listOf(month, year, lifetime),
        original1YearData = samplePlan("com.abacus.puzzle.1year", "₹1,200", 1_200_000_000, PackageType.ANNUAL),
        originalLifetimeData = samplePlan("com.abacus.all", "₹4,750", 4_750_000_000, PackageType.LIFETIME),
        original1MonthData = month,
        discountPer = 30,
        discountPerLifetime = 0,
        homeOfferYearlyEndMillis = System.currentTimeMillis() + 565_000L,
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
                uiState = state, onClose = {}, onSeeBenefits = {}, onRetry = {}, onSubscribe = {}, onOfferExpired = {},
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
            onSubscribe = {}, onClose = {},
        )
    }
}
