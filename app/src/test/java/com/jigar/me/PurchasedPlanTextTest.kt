package com.jigar.me

import com.jigar.me.utils.CommonUtils
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** The purchased plan card shows the purchase date, and the store order id beside the Subscribed / Purchased pill. */
class PurchasedPlanTextTest {
    private val bought = 1_730_000_000_000L

    @Test fun lifetimeShowsPurchaseDate() {
        val text = CommonUtils.getPurchaseTime("inapp", bought, null)
        assertNotNull(text)
        assertTrue(text!!.contains("Purchased ON"))
        assertFalse(text.contains("Order ID"))   // the order id has its own small label beside the pill
    }

    @Test fun subscriptionShowsBothDates() {
        val text = CommonUtils.getPurchaseTime("subs", bought, "P1Y")!!
        assertTrue(text.contains("Subscribed ON"))
        assertTrue(text.contains("Expire ON"))
    }

    @Test fun noPurchaseTimeMeansNoText() {
        assertNull(CommonUtils.getPurchaseTime("inapp", 0L, null))
    }

}
