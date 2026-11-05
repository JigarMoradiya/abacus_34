package com.jigar.me

import com.jigar.me.ui.view.home.common_ui.dialogs.storeIntroOffer
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.RcPlanItem
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PackageType
import com.revenuecat.purchases.PresentedOfferingContext
import com.revenuecat.purchases.models.Period
import com.revenuecat.purchases.models.Price
import com.revenuecat.purchases.models.PricingPhase
import com.revenuecat.purchases.models.RecurrenceMode
import com.revenuecat.purchases.models.TestStoreProduct
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/** The store-native discount of a plan card: the paid intro phase of the product's default option (a Google Play offer). */
class StoreIntroOfferTest {

    private fun plan(
        sku: String, regularMicros: Long, period: Period?, type: PackageType = PackageType.ANNUAL,
        free: PricingPhase? = null, intro: PricingPhase? = null,
    ): RcPlanItem {
        val product = TestStoreProduct(sku, "Plan", "Plan", "Plan", Price("x", regularMicros, "INR"), period, free, intro)
        return RcPlanItem(
            sku = sku, price = "x", price_amount_micros = regularMicros, type = if (type == PackageType.LIFETIME) "inapp" else "subs",
            isPurchase = false, billingPeriod = null, purchaseTime = 0L,
            rcPackage = Package(sku, type, product, PresentedOfferingContext("test")),
        )
    }

    private fun phase(unit: Period.Unit, value: Int, cycles: Int, micros: Long) =
        PricingPhase(Period(value, unit, "P"), RecurrenceMode.FINITE_RECURRING, cycles, Price("p", micros, "INR"))

    private val year = Period(1, Period.Unit.YEAR, "P1Y")

    @Test fun regularPlanHasNoIntroOffer() {
        assertNull(plan("com.abacus.puzzle.1year", 1_200_000_000, year).storeIntroOffer())
    }

    @Test fun firstYearIntroPriceGivesPercentOffAndPeriod() {
        val offer = plan("com.abacus.puzzle.1year", 1_200_000_000, year, intro = phase(Period.Unit.YEAR, 1, 1, 840_000_000)).storeIntroOffer()
        assertNotNull(offer)
        assertEquals(30, offer!!.percentOff)
        assertEquals(Period.Unit.YEAR, offer.periodUnit)
        assertEquals(1, offer.periodCount)
    }

    @Test fun threeMonthIntroUsesPeriodTimesCycles() {
        val offer = plan("com.abacus.puzzle.1year", 1_200_000_000, year, intro = phase(Period.Unit.MONTH, 1, 3, 300_000_000)).storeIntroOffer()
        assertNotNull(offer)
        assertEquals(75, offer!!.percentOff)
        assertEquals(Period.Unit.MONTH, offer.periodUnit)
        assertEquals(3, offer.periodCount)
    }

    @Test fun introAsPricyAsTheRegularPriceIsNotADiscount() {
        assertNull(plan("com.abacus.puzzle.1year", 1_200_000_000, year, intro = phase(Period.Unit.YEAR, 1, 1, 1_200_000_000)).storeIntroOffer())
    }

    @Test fun lifetimeNeverShowsAnIntroOffer() {
        assertNull(plan("com.abacus.all", 4_750_000_000, null, PackageType.LIFETIME).storeIntroOffer())
    }

    @Test fun freeTrialKeepsTheTrialLineInsteadOfTheIntroOffer() {
        val trial = phase(Period.Unit.DAY, 7, 1, 0)
        assertNull(plan("com.abacus.puzzle.1year", 1_200_000_000, year, free = trial, intro = phase(Period.Unit.YEAR, 1, 1, 840_000_000)).storeIntroOffer())
    }
}
