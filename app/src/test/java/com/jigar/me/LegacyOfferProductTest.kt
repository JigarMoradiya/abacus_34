package com.jigar.me

import com.jigar.me.utils.AppConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** An old buyer of a retired ".offer" product must still land on the regular plan that replaced it. */
class LegacyOfferProductTest {
    private val products = AppConstants.Products

    @Test fun offerIdsAreRecognised() {
        assertTrue(products.isOffer("com.abacus.puzzle.1year.offer"))
        assertTrue(products.isOffer("com.abacus.puzzle.1year.offer:1year-offer"))
        assertTrue(products.isOffer("com.abacus.all.offer"))
        assertFalse(products.isOffer("com.abacus.puzzle.1year"))
        assertFalse(products.isOffer("com.abacus.puzzle.1year:1year"))
        assertFalse(products.isOffer("com.abacus.all"))
    }

    @Test fun offerIdsMapToTheRegularProduct() {
        assertEquals("com.abacus.puzzle.1year", products.withoutOffer("com.abacus.puzzle.1year.offer"))
        assertEquals("com.abacus.puzzle.1year", products.withoutOffer("com.abacus.puzzle.1year.offer:1year-offer"))
        assertEquals("com.abacus.all", products.withoutOffer("com.abacus.all.offer"))
    }

    @Test fun regularIdsAreUnchanged() {
        assertEquals("com.abacus.puzzle.1year", products.withoutOffer("com.abacus.puzzle.1year"))
        assertEquals("com.abacus.puzzle.1year:1year", products.withoutOffer("com.abacus.puzzle.1year:1year"))
        assertEquals("com.abacus.all", products.withoutOffer("com.abacus.all"))
        assertEquals("com.abacus.puzzle.1month", products.withoutOffer("com.abacus.puzzle.1month"))
    }

    @Test fun oldOfferBuyerMatchesTheRegularPackage() {
        // the same comparison PurchaseViewModel.toPlanItem makes
        fun matches(packageId: String, customerId: String): Boolean {
            val id = products.withoutOffer(customerId)
            return packageId == id || packageId.startsWith("$id:") || packageId.endsWith(":$id")
        }
        assertTrue(matches("com.abacus.puzzle.1year:1year", "com.abacus.puzzle.1year.offer"))
        assertTrue(matches("com.abacus.puzzle.1year:1year", "com.abacus.puzzle.1year.offer:1year-offer"))
        assertTrue(matches("com.abacus.all", "com.abacus.all.offer"))
        assertFalse(matches("com.abacus.puzzle.1month:1month", "com.abacus.puzzle.1year.offer"))
        assertFalse(matches("com.abacus.all", "com.abacus.puzzle.1year.offer"))
    }
}
