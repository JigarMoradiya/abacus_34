package com.jigar.me

import com.jigar.me.data.pref.PreferencesHelper
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.HomeOfferManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeOfferManagerTest {

    // Map-backed stand-in for SharedPreferences so the window/date math is testable
    // with an injected clock and no Android runtime.
    private class FakePrefs : PreferencesHelper {
        private val strings = mutableMapOf<String, String>()
        private val ints = mutableMapOf<String, Int>()
        private val bools = mutableMapOf<String, Boolean>()
        private val floats = mutableMapOf<String, Float>()
        private var loggedIn = false
        private var token: String? = null
        private var loginData: String? = null

        override fun isUserLoggedIn() = loggedIn
        override fun setUserLoggedIn(value: Boolean) { loggedIn = value }
        override fun getAccessToken() = token
        override fun setAccessToken(accessToken: String?) { token = accessToken }
        override fun getLoginData() = loginData
        override fun setLoginData(data: String?) { loginData = data }
        override fun getCustomParam(paramName: String, defaultValue: String) = strings[paramName] ?: defaultValue
        override fun setCustomParam(paramName: String, paramValue: String) { strings[paramName] = paramValue }
        override fun getCustomParamInt(paramName: String, defaultValue: Int) = ints[paramName] ?: defaultValue
        override fun setCustomParamInt(paramName: String, paramValue: Int) { ints[paramName] = paramValue }
        override fun getCustomParamBoolean(paramName: String, defaultValue: Boolean) = bools[paramName] ?: defaultValue
        override fun setCustomParamBoolean(paramName: String, paramValue: Boolean) { bools[paramName] = paramValue }
        override fun getCustomParamFloat(paramName: String, defaultValue: Float) = floats[paramName] ?: defaultValue
        override fun setCustomParamFloat(paramName: String, paramValue: Float) { floats[paramName] = paramValue }
    }

    private val t0 = 1_700_000_000_000L
    private val tenMin = 10 * 60_000L

    private fun prefsWith(json: String?) = FakePrefs().also { p ->
        if (json != null) p.setCustomParam(AppConstants.RemoteConfig.homeOffer, json)
    }

    @Test
    fun `missing, empty and malformed config all read as disabled`() {
        assertFalse(HomeOfferManager.config(prefsWith(null)).isEnabled)
        assertFalse(HomeOfferManager.config(prefsWith("")).isEnabled)
        assertFalse(HomeOfferManager.config(prefsWith("not json")).isEnabled)
        assertFalse(HomeOfferManager.config(prefsWith("""{"enabled":true}""")).isEnabled) // no duration
        assertFalse(HomeOfferManager.config(prefsWith("""{"enabled":true,"duration_min":0}""")).isEnabled)
        assertFalse(HomeOfferManager.config(prefsWith("""{"duration_min":10}""")).isEnabled)     // no flag
    }

    @Test
    fun `valid config parses and ignores unknown fields`() {
        val cfg = HomeOfferManager.config(prefsWith("""{"enabled":true,"duration_min":10,"name":"Diwali","future":1}"""))
        assertTrue(cfg.isEnabled)
        assertEquals(10, cfg.duration_min)
        assertEquals("Diwali", cfg.name)
    }

    @Test
    fun `window starts on first sight and counts down by wall clock`() {
        val p = prefsWith("""{"enabled":true,"duration_min":10,"name":"Diwali"}""")
        assertFalse(HomeOfferManager.isTimedOfferActive(p, t0))            // not started yet
        HomeOfferManager.startIfNeeded(p, t0)
        assertEquals(tenMin, HomeOfferManager.remainingMillis(p, t0))
        assertEquals(tenMin - 90_000L, HomeOfferManager.remainingMillis(p, t0 + 90_000L))
        assertTrue(HomeOfferManager.isTimedOfferActive(p, t0 + tenMin - 1))
        assertFalse(HomeOfferManager.isTimedOfferActive(p, t0 + tenMin))
        // Expired stays expired while the flag is on and the name is unchanged.
        HomeOfferManager.startIfNeeded(p, t0 + tenMin + 5_000L)
        assertFalse(HomeOfferManager.isTimedOfferActive(p, t0 + tenMin + 5_000L))
    }

    @Test
    fun `clock moved backwards cannot extend the window past its duration`() {
        val p = prefsWith("""{"enabled":true,"duration_min":10}""")
        HomeOfferManager.startIfNeeded(p, t0)
        assertEquals(tenMin, HomeOfferManager.remainingMillis(p, t0 - 60 * 60_000L))
    }

    @Test
    fun `renaming the campaign restarts the window`() {
        val p = prefsWith("""{"enabled":true,"duration_min":10,"name":"Diwali"}""")
        HomeOfferManager.startIfNeeded(p, t0)
        val later = t0 + tenMin + 60_000L
        assertFalse(HomeOfferManager.isTimedOfferActive(p, later))
        p.setCustomParam(AppConstants.RemoteConfig.homeOffer, """{"enabled":true,"duration_min":10,"name":"New Year"}""")
        HomeOfferManager.startIfNeeded(p, later)
        assertEquals(tenMin, HomeOfferManager.remainingMillis(p, later))
    }

    @Test
    fun `clearStart resets so OFF then ON is a fresh campaign`() {
        val p = prefsWith("""{"enabled":true,"duration_min":10,"name":"Diwali"}""")
        HomeOfferManager.startIfNeeded(p, t0)
        HomeOfferManager.clearStart(p)
        assertNull(HomeOfferManager.startedAt(p))
        HomeOfferManager.startIfNeeded(p, t0 + 5 * tenMin)
        assertEquals(tenMin, HomeOfferManager.remainingMillis(p, t0 + 5 * tenMin))
    }

    @Test
    fun `forceRestart always gives a fresh window, even mid-campaign or after expiry`() {
        val p = prefsWith("""{"enabled":true,"duration_min":10,"name":"Diwali"}""")
        HomeOfferManager.startIfNeeded(p, t0)
        // Mid-campaign: forceRestart still resets the clock (unlike the default,
        // which would leave an unexpired window alone).
        HomeOfferManager.startIfNeeded(p, t0 + 60_000L, forceRestart = true)
        assertEquals(tenMin, HomeOfferManager.remainingMillis(p, t0 + 60_000L))
        // Expired: forceRestart revives it instead of leaving it expired (the
        // default behaviour for an expired, same-named campaign is covered by
        // "window starts on first sight and counts down by wall clock" above).
        val expiredAt = t0 + 60_000L + tenMin + 5_000L
        HomeOfferManager.startIfNeeded(p, expiredAt, forceRestart = true)
        assertEquals(tenMin, HomeOfferManager.remainingMillis(p, expiredAt))
    }

    @Test
    fun `yearly discount is active under permanent discount or timed offer`() {
        val p = prefsWith("""{"enabled":true,"duration_min":10}""")
        assertFalse(HomeOfferManager.isYearlyDiscountActive(p, t0))
        p.setCustomParamInt(AppConstants.RemoteConfig.discountPer, 20)
        assertTrue(HomeOfferManager.isYearlyDiscountActive(p, t0))
        p.setCustomParamInt(AppConstants.RemoteConfig.discountPer, 0)
        HomeOfferManager.startIfNeeded(p, t0)
        assertTrue(HomeOfferManager.isYearlyDiscountActive(p, t0 + 1_000L))
        assertFalse(HomeOfferManager.isYearlyDiscountActive(p, t0 + tenMin))
    }

    @Test
    fun `discount percent comes from real prices`() {
        assertEquals(25, HomeOfferManager.discountPercent(4_000_000L, 3_000_000L))
        assertEquals(33, HomeOfferManager.discountPercent(3_000_000L, 2_000_000L))
        assertEquals(0, HomeOfferManager.discountPercent(3_000_000L, 3_000_000L))   // no saving
        assertEquals(0, HomeOfferManager.discountPercent(2_000_000L, 3_000_000L))   // offer dearer
        assertEquals(0, HomeOfferManager.discountPercent(null, 3_000_000L))
        assertEquals(0, HomeOfferManager.discountPercent(0L, 0L))
    }

    @Test
    fun `product field defaults to yearly when missing or unrecognized`() {
        assertFalse(HomeOfferManager.config(prefsWith("""{"enabled":true,"duration_min":10}""")).targetsLifetime)
        assertFalse(HomeOfferManager.config(prefsWith("""{"enabled":true,"duration_min":10,"product":"yearly"}""")).targetsLifetime)
        assertFalse(HomeOfferManager.config(prefsWith("""{"enabled":true,"duration_min":10,"product":"bogus"}""")).targetsLifetime)
        assertTrue(HomeOfferManager.config(prefsWith("""{"enabled":true,"duration_min":10,"product":"lifetime"}""")).targetsLifetime)
        assertTrue(HomeOfferManager.config(prefsWith("""{"enabled":true,"duration_min":10,"product":"LIFETIME"}""")).targetsLifetime)
    }

    @Test
    fun `timed offer only activates the discount for its targeted product`() {
        val p = prefsWith("""{"enabled":true,"duration_min":10,"product":"lifetime"}""")
        HomeOfferManager.startIfNeeded(p, t0)
        // Targets lifetime -> yearly must stay exactly as if the offer didn't exist.
        assertFalse(HomeOfferManager.isYearlyDiscountActive(p, t0 + 1_000L))
        assertTrue(HomeOfferManager.isLifetimeDiscountActive(p, t0 + 1_000L))
        assertEquals(0, HomeOfferManager.effectiveYearDiscountPer(p, 4_000_000L, 3_000_000L, t0 + 1_000L))
        assertEquals(25, HomeOfferManager.effectiveLifetimeDiscountPer(p, 4_000_000L, 3_000_000L, t0 + 1_000L))
    }

    @Test
    fun `timed offer targeting yearly leaves lifetime untouched`() {
        val p = prefsWith("""{"enabled":true,"duration_min":10,"product":"yearly"}""")
        HomeOfferManager.startIfNeeded(p, t0)
        assertTrue(HomeOfferManager.isYearlyDiscountActive(p, t0 + 1_000L))
        assertFalse(HomeOfferManager.isLifetimeDiscountActive(p, t0 + 1_000L))
        assertEquals(0, HomeOfferManager.effectiveLifetimeDiscountPer(p, 4_000_000L, 3_000_000L, t0 + 1_000L))
    }

    @Test
    fun `timed offer % is always price-derived even if the permanent field is nonzero`() {
        val p = prefsWith("""{"enabled":true,"duration_min":10,"product":"lifetime"}""")
        HomeOfferManager.startIfNeeded(p, t0)
        // A permanent discount_per_lifetime left set (e.g. from earlier testing) must
        // NOT leak into the timed offer's displayed %% -- it's always the real
        // base-vs-offer price calculation while the timed offer is active for lifetime.
        p.setCustomParamInt(AppConstants.RemoteConfig.discountPerLifeTime, 40)
        assertEquals(25, HomeOfferManager.effectiveLifetimeDiscountPer(p, 4_000_000L, 3_000_000L, t0 + 1_000L))
    }

    @Test
    fun `permanent discounts still win over an inactive or differently-targeted timed offer`() {
        val p = prefsWith("""{"enabled":true,"duration_min":10,"product":"yearly"}""")
        HomeOfferManager.startIfNeeded(p, t0)
        p.setCustomParamInt(AppConstants.RemoteConfig.discountPerLifeTime, 15)
        // Permanent lifetime discount is on even though the timed offer targets yearly.
        assertTrue(HomeOfferManager.isLifetimeDiscountActive(p, t0 + 1_000L))
        assertEquals(15, HomeOfferManager.effectiveLifetimeDiscountPer(p, 4_000_000L, 3_000_000L, t0 + 1_000L))
    }

    @Test
    fun `targetProductIds is empty when inactive and the right pair when active`() {
        val p = prefsWith(null)
        assertEquals(emptySet<String>(), HomeOfferManager.targetProductIds(p, t0))

        val yearly = prefsWith("""{"enabled":true,"duration_min":10,"product":"yearly"}""")
        HomeOfferManager.startIfNeeded(yearly, t0)
        assertEquals(setOf(AppConstants.Products.year, AppConstants.Products.yearOffer), HomeOfferManager.targetProductIds(yearly, t0 + 1_000L))

        val lifetime = prefsWith("""{"enabled":true,"duration_min":10,"product":"lifetime"}""")
        HomeOfferManager.startIfNeeded(lifetime, t0)
        assertEquals(setOf(AppConstants.Products.lifetime, AppConstants.Products.lifetimeOffer), HomeOfferManager.targetProductIds(lifetime, t0 + 1_000L))

        // Expired -> empty again, regardless of product.
        assertEquals(emptySet<String>(), HomeOfferManager.targetProductIds(yearly, t0 + tenMin))
    }
}
