package com.jigar.me

import com.jigar.me.utils.PurchaseRestore
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** The launch restore runs once per RevenueCat customer, and again whenever the customer is a different one. */
class PurchaseRestoreTest {
    @Test fun `a new install has nothing saved, so it restores`() =
        assertTrue(PurchaseRestore.needsRestore(restoredFor = "", userId = "\$RCAnonymousID:abc"))

    @Test fun `the same customer is not restored again`() =
        assertFalse(PurchaseRestore.needsRestore(restoredFor = "\$RCAnonymousID:abc", userId = "\$RCAnonymousID:abc"))

    @Test fun `a login makes a new customer, so it restores once more`() =
        assertTrue(PurchaseRestore.needsRestore(restoredFor = "\$RCAnonymousID:abc", userId = "user-42"))

    @Test fun `a logout gives a new anonymous customer, so it restores once more`() =
        assertTrue(PurchaseRestore.needsRestore(restoredFor = "user-42", userId = "\$RCAnonymousID:def"))
}
