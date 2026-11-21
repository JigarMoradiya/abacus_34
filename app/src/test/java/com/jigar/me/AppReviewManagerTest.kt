package com.jigar.me

import com.jigar.me.utils.AppReviewManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppReviewManagerTest {
    private val hour = 60L * 60 * 1000

    @Test
    fun `no review ask on the first day`() {
        val installed = 1_000_000_000_000L
        assertFalse(AppReviewManager.isPastFirstDay(installed, installed))                  // just installed
        assertFalse(AppReviewManager.isPastFirstDay(installed, installed + 10 * 60_000L))   // 10 minutes later (a midnight in between does not matter)
        assertFalse(AppReviewManager.isPastFirstDay(installed, installed + 19 * hour))
    }

    @Test
    fun `the ask is allowed from the next day`() {
        val installed = 1_000_000_000_000L
        assertTrue(AppReviewManager.isPastFirstDay(installed, installed + 20 * hour))
        assertTrue(AppReviewManager.isPastFirstDay(installed, installed + 48 * hour))
    }

    @Test
    fun `an install time that cannot be read does not block`() {
        assertTrue(AppReviewManager.isPastFirstDay(0L, 5L))
    }
}
