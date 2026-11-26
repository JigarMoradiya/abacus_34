package com.jigar.me

import com.jigar.me.utils.PurchaseRestore
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** The launch restore runs once per RevenueCat customer, and again whenever the customer is a different one; a free phone looks once a week. */
class PurchaseRestoreTest {
    @Test fun `a new install has nothing saved, so it restores`() =
        assertTrue(PurchaseRestore.needsRestore(restoredFor = "", userId = "\$RCAnonymousID:abc"))

    @Test fun `the same customer is not restored again`() =
        assertFalse(PurchaseRestore.needsRestore(restoredFor = "\$RCAnonymousID:abc", userId = "\$RCAnonymousID:abc"))

    @Test fun `a login makes a new customer, so it restores once more`() =
        assertTrue(PurchaseRestore.needsRestore(restoredFor = "\$RCAnonymousID:abc", userId = "user-42"))

    @Test fun `a logout gives a new anonymous customer, so it restores once more`() =
        assertTrue(PurchaseRestore.needsRestore(restoredFor = "user-42", userId = "\$RCAnonymousID:def"))

    private val day = 24L * 60 * 60 * 1000
    private val now = 1_800_000_000_000L

    @Test fun `a free phone looked at yesterday waits`() =
        assertFalse(PurchaseRestore.needsWeeklyCheck(checkedAt = now - day, now = now, hasPremium = false))

    @Test fun `a free phone looked at six days ago still waits`() =
        assertFalse(PurchaseRestore.needsWeeklyCheck(checkedAt = now - 6 * day, now = now, hasPremium = false))

    @Test fun `a free phone looked at seven days ago looks again`() =
        assertTrue(PurchaseRestore.needsWeeklyCheck(checkedAt = now - 7 * day, now = now, hasPremium = false))

    @Test fun `a free phone never looked at looks`() =
        assertTrue(PurchaseRestore.needsWeeklyCheck(checkedAt = 0L, now = now, hasPremium = false))

    @Test fun `a saved time in the future (the clock went back) does not block the look`() =
        assertTrue(PurchaseRestore.needsWeeklyCheck(checkedAt = now + 30 * day, now = now, hasPremium = false))

    @Test fun `a phone with Premium never looks`() {
        assertFalse(PurchaseRestore.needsWeeklyCheck(checkedAt = now - 30 * day, now = now, hasPremium = true))
        assertFalse(PurchaseRestore.needsWeeklyCheck(checkedAt = 0L, now = now, hasPremium = true))
    }
}
