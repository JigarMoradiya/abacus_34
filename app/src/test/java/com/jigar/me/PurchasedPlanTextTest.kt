package com.jigar.me

import com.jigar.me.utils.CommonUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** The purchased plan card shows the purchase date, and for a subscription the end of its period. */
class PurchasedPlanTextTest {
    private val bought = 1_730_000_000_000L

    @Test fun lifetimeHasOnlyThePurchaseDate() {
        val dates = CommonUtils.purchaseDates("inapp", bought, null)
        assertNotNull(dates)
        assertEquals(bought, dates!!.first.time)
        assertNull(dates.second)
    }

    @Test fun subscriptionHasBothDates() {
        val dates = CommonUtils.purchaseDates("subs", bought, "P1Y")!!
        assertEquals(bought, dates.first.time)
        assertTrue(dates.second!!.time > dates.first.time)
    }

    @Test fun noPurchaseTimeMeansNoDates() {
        assertNull(CommonUtils.purchaseDates("inapp", 0L, null))
    }
}
