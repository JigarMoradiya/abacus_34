package com.jigar.me

import com.jigar.me.utils.StoreOneTimeOffers
import com.jigar.me.utils.StoreOneTimeOffers.Facts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StoreOneTimeOffersTest {
    private val now = 1_000_000L
    private fun offer(price: Long, full: Long?, percent: Int? = null, amount: Long? = null, start: Long? = null, end: Long? = null,
                      preorder: Boolean = false, rental: Boolean = false, token: String = "t$price") =
        Facts(price, full, percent, amount, "INR", "id$price", token, start, end, preorder, rental)

    @Test fun theBasePurchaseOptionIsNotADiscount() = assertNull(StoreOneTimeOffers.choose(listOf(offer(4_750_000_000, null)), now))

    @Test fun aDiscountOfferIsFoundAndTheCheapestWins() {
        val list = listOf(offer(4_750_000_000, null), offer(3_800_000_000, 4_750_000_000, percent = 20), offer(3_325_000_000, 4_750_000_000, percent = 30))
        assertEquals("t3325000000", StoreOneTimeOffers.choose(list, now)!!.token)
    }

    @Test fun aPercentageOfferShowsThePercentageFromPlay() {
        val o = StoreOneTimeOffers.toOffer(offer(3_325_000_000, 4_750_000_000, percent = 30))
        assertEquals(30, o.percentOff)
        assertNull(o.amountOffText)
    }

    @Test fun anAbsoluteOfferShowsTheAmountAndAWorkedOutPercent() {
        val o = StoreOneTimeOffers.toOffer(offer(4_250_000_000, 4_750_000_000, amount = 500_000_000))
        assertEquals("₹500", o.amountOffText)
        assertEquals(11, o.percentOff)   // 500 of 4750 = 10.5 %
    }

    @Test fun anOfferOutsideItsTimeWindowIsIgnored() {
        assertNull(StoreOneTimeOffers.choose(listOf(offer(3_000_000_000, 4_750_000_000, percent = 37, start = now + 1)), now))
        assertNull(StoreOneTimeOffers.choose(listOf(offer(3_000_000_000, 4_750_000_000, percent = 37, end = now)), now))
        assertTrue(StoreOneTimeOffers.choose(listOf(offer(3_000_000_000, 4_750_000_000, percent = 37, start = now - 5, end = now + 5)), now) != null)
    }

    @Test fun aPreorderOrARentalIsNotADiscount() {
        assertNull(StoreOneTimeOffers.choose(listOf(offer(3_000_000_000, 4_750_000_000, preorder = true)), now))
        assertNull(StoreOneTimeOffers.choose(listOf(offer(3_000_000_000, 4_750_000_000, rental = true)), now))
    }

    @Test fun aPriceNotBelowTheFullPriceIsIgnored() {
        assertNull(StoreOneTimeOffers.choose(listOf(offer(4_750_000_000, 4_750_000_000, percent = 0)), now))
        assertNull(StoreOneTimeOffers.choose(listOf(offer(0, 4_750_000_000)), now))
    }

    @Test fun theSwitchIsOnUnlessItSaysOff() {
        assertTrue(StoreOneTimeOffers.enabled(null)); assertTrue(StoreOneTimeOffers.enabled("")); assertTrue(StoreOneTimeOffers.enabled("on")); assertTrue(StoreOneTimeOffers.enabled("true"))
        assertFalse(StoreOneTimeOffers.enabled("off")); assertFalse(StoreOneTimeOffers.enabled(" False ")); assertFalse(StoreOneTimeOffers.enabled("0"))
    }

    /** The real Play data of the diwali-2026 offer: a start time only. Play's ValidTimeWindow reports the missing end as 0, which must mean "no end". */
    @Test fun anOfferWithAStartAndNoEndRunsFromItsStart() {
        val start = 1_793_752_200_000L
        val diwali = offer(3_610_000_000, 3_800_000_000, percent = 5, start = start, end = 0L)
        val list = listOf(offer(3_800_000_000, null), diwali)
        val chosen = StoreOneTimeOffers.choose(list, start + 60_000L)
        assertEquals("t3610000000", chosen!!.token)
        val o = StoreOneTimeOffers.toOffer(chosen)
        assertEquals(5, o.percentOff)
        assertEquals("₹3,610", o.offerPriceText)
        assertEquals("₹3,800", o.fullPriceText)
        assertNull(o.endMillis)
        assertNull(StoreOneTimeOffers.choose(list, start - 1L))   // before its start it is not shown
    }
}
