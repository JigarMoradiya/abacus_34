package com.jigar.me

import com.jigar.me.data.pref.PreferencesHelper
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.BiggestOffer
import com.jigar.me.utils.HomeOfferManager
import com.jigar.me.utils.parseEndAt
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
    fun `timed offer is active from its start until its window is over`() {
        val p = prefsWith("""{"enabled":true,"duration_min":10}""")
        assertFalse(HomeOfferManager.isTimedOfferActive(p, t0))                    // not started yet
        HomeOfferManager.startIfNeeded(p, t0)
        assertTrue(HomeOfferManager.isTimedOfferActive(p, t0 + 1_000L))
        assertFalse(HomeOfferManager.isTimedOfferActive(p, t0 + tenMin))
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
        // Targets lifetime -> yearly must stay exactly as if the offer didn't exist (no countdown on the yearly plan).
        assertNull(HomeOfferManager.yearlyEndMillis(p, t0 + 1_000L))
        assertEquals(t0 + tenMin, HomeOfferManager.lifetimeEndMillis(p, t0 + 1_000L))
    }

    @Test
    fun `timed offer targeting yearly leaves lifetime untouched`() {
        val p = prefsWith("""{"enabled":true,"duration_min":10,"product":"yearly"}""")
        HomeOfferManager.startIfNeeded(p, t0)
        assertEquals(t0 + tenMin, HomeOfferManager.yearlyEndMillis(p, t0 + 1_000L))
        assertNull(HomeOfferManager.lifetimeEndMillis(p, t0 + 1_000L))
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

    // ---- fixed calendar end (end_at) ----

    private val endAt = "2026-12-15T18:29:59Z"
    private val endMs = parseEndAt("2026-12-15T18:29:59Z")!!

    @Test
    fun `end_at reads Z and offset forms, with or without fraction, and rejects the rest`() {
        assertEquals(1_797_359_399_000L, parseEndAt("2026-12-15T18:29:59Z"))
        assertEquals(parseEndAt("2026-12-15T18:29:59Z"), parseEndAt("2026-12-15T23:59:59+05:30"))   // the same instant in India time
        assertEquals(parseEndAt("2026-12-15T18:29:59Z")!! + 123L, parseEndAt("2026-12-15T18:29:59.123Z"))
        assertNull(parseEndAt(null)); assertNull(parseEndAt("")); assertNull(parseEndAt("15 Dec 2026")); assertNull(parseEndAt("2026-13-45T00:00:00Z"))
    }

    @Test
    fun `end_at alone enables the offer and a bad end_at without a duration does not`() {
        assertTrue(HomeOfferManager.config(prefsWith("""{"enabled":true,"end_at":"$endAt"}""")).isEnabled)
        assertFalse(HomeOfferManager.config(prefsWith("""{"enabled":true,"end_at":"soon"}""")).isEnabled)
        assertFalse(HomeOfferManager.config(prefsWith("""{"enabled":false,"end_at":"$endAt","duration_min":10}""")).isEnabled)
    }

    @Test
    fun `with end_at every phone shares one end, no per-device start is needed and nothing runs after it`() {
        val p = prefsWith("""{"enabled":true,"duration_min":10,"end_at":"$endAt"}""")   // duration_min is only the fallback of old app versions
        assertTrue(HomeOfferManager.isTimedOfferActive(p, endMs - 5 * 24 * 3_600_000L))          // days before the end, never started on this device
        assertEquals(5 * 24 * 3_600_000L, HomeOfferManager.remainingMillis(p, endMs - 5 * 24 * 3_600_000L))   // NOT clamped to the 10 minutes
        assertEquals(endMs, HomeOfferManager.endMillis(p, endMs - 1_000L))
        assertFalse(HomeOfferManager.isTimedOfferActive(p, endMs))
        assertFalse(HomeOfferManager.isTimedOfferActive(p, endMs + 1L))
        assertNull(HomeOfferManager.endMillis(p, endMs + 1L))
    }

    @Test
    fun `without end_at the per-device window works exactly as before`() {
        val p = prefsWith("""{"enabled":true,"duration_min":10}""")
        assertFalse(HomeOfferManager.isTimedOfferActive(p, t0))            // not started on this device
        HomeOfferManager.startIfNeeded(p, t0)
        assertEquals(tenMin, HomeOfferManager.remainingMillis(p, t0))
        assertEquals(t0 + tenMin, HomeOfferManager.endMillis(p, t0 + 1_000L))
        assertFalse(HomeOfferManager.isTimedOfferActive(p, t0 + tenMin))
    }

    // ---- "Biggest offer" campaign ----

    @Test
    fun `the campaign is on only for a running offer that says biggest`() {
        val ok = prefsWith("""{"enabled":true,"end_at":"$endAt","campaign":"biggest"}""")
        assertTrue(HomeOfferManager.isBiggestCampaignActive(ok, endMs - 1_000L))
        assertFalse(HomeOfferManager.isBiggestCampaignActive(ok, endMs + 1_000L))                                   // over
        assertTrue(HomeOfferManager.isBiggestCampaignActive(prefsWith("""{"enabled":true,"end_at":"$endAt","campaign":"BIGGEST"}"""), endMs - 1_000L))
        assertFalse(HomeOfferManager.isBiggestCampaignActive(prefsWith("""{"enabled":true,"end_at":"$endAt"}"""), endMs - 1_000L))              // a plain timed offer
        assertFalse(HomeOfferManager.isBiggestCampaignActive(prefsWith("""{"enabled":true,"end_at":"$endAt","campaign":"other"}"""), endMs - 1_000L))
        // the campaign runs on the plan `product` names: lifetime is a campaign too (the screens read targetsLifetime to pick the plan)
        val lifetimePrefs = prefsWith("""{"enabled":true,"end_at":"$endAt","campaign":"biggest","product":"lifetime"}""")
        assertTrue(HomeOfferManager.isBiggestCampaignActive(lifetimePrefs, endMs - 1_000L))
        assertTrue(HomeOfferManager.config(lifetimePrefs).targetsLifetime)
        assertFalse(HomeOfferManager.config(ok).targetsLifetime)
        assertFalse(HomeOfferManager.isBiggestCampaignActive(prefsWith("""{"enabled":false,"end_at":"$endAt","campaign":"biggest"}"""), endMs - 1_000L))
    }

    @Test
    fun `the others are locked only when the yearly offer can really be bought and is cheaper`() {
        // Android India numbers: yearly 1,299, offer 499
        assertTrue(BiggestOffer.isLocked(true, true, 499_000_000, 1_299_000_000))
        assertFalse(BiggestOffer.isLocked(false, true, 499_000_000, 1_299_000_000))   // campaign not requested
        assertFalse(BiggestOffer.isLocked(true, false, 499_000_000, 1_299_000_000))   // offer not on the list (not sold in this country, owned, removed)
        assertFalse(BiggestOffer.isLocked(true, true, null, 1_299_000_000))           // price unknown
        assertFalse(BiggestOffer.isLocked(true, true, 499_000_000, null))
        assertFalse(BiggestOffer.isLocked(true, true, 1_299_000_000, 1_299_000_000))  // same price: not an offer
        assertFalse(BiggestOffer.isLocked(true, true, 1_500_000_000, 1_299_000_000))  // dearer than the regular plan
        assertFalse(BiggestOffer.isLocked(true, true, 0L, 1_299_000_000))             // a 0 price is a missing price, never a free plan
        assertTrue(BiggestOffer.isLocked(true, true, 999_000_000, 3_800_000_000))     // lifetime: offer price against the regular LIFETIME price
        assertFalse(BiggestOffer.isLocked(true, true, 3_800_000_000, 3_800_000_000))
        assertEquals(62, HomeOfferManager.discountPercent(1_299_000_000, 499_000_000))   // what the Android card says: ~62% OFF
        assertEquals(72, HomeOfferManager.discountPercent(1_799_000_000, 499_000_000))   // iOS: ~72% OFF
    }

    // ---- the four configs the owner publishes (exactly as written in the Firebase console) ----

    private val campaignLifetimeJson = """{"enabled":true,"name":"Biggest Offer","product":"lifetime","duration_min":10080,"end_at":"2026-12-15T18:29:59Z","campaign":"biggest"}"""
    private val campaignYearlyJson = """{"enabled":true,"name":"Biggest Offer","product":"yearly","duration_min":10080,"end_at":"2026-12-15T18:29:59Z","campaign":"biggest"}"""
    private val plainLifetimeJson = """{"enabled": true, "duration_min": 120, "name": "Special Offer", "product": "lifetime"}"""
    private val plainYearlyJson = """{"enabled": true, "duration_min": 120, "name": "Special Offer", "product": "yearly"}"""
    private val day = 24 * 3600_000L

    @Test
    fun `campaign configs with end_at run to the shared end date on the plan they name`() {
        for ((json, lifetime) in listOf(campaignLifetimeJson to true, campaignYearlyJson to false)) {
            val p = prefsWith(json)
            val cfg = HomeOfferManager.config(p)
            assertTrue(cfg.isEnabled)
            assertTrue(cfg.isBiggestCampaign)
            assertEquals(lifetime, cfg.targetsLifetime)
            assertEquals(endMs, cfg.endAtMillis)
            // no per-device start is needed: the window is open for every phone, and it is NOT cut to duration_min (7 days) 20 days before the end
            assertEquals(20 * day, HomeOfferManager.remainingMillis(p, endMs - 20 * day))
            assertTrue(HomeOfferManager.isTimedOfferActive(p, endMs - 20 * day))
            assertTrue(HomeOfferManager.isBiggestCampaignActive(p, endMs - 20 * day))
            assertEquals(endMs, HomeOfferManager.endMillis(p, endMs - 20 * day))
            // the countdown and the discount belong to the plan `product` names and to no other
            assertEquals(if (lifetime) endMs else null, HomeOfferManager.lifetimeEndMillis(p, endMs - day))
            assertEquals(if (lifetime) null else endMs, HomeOfferManager.yearlyEndMillis(p, endMs - day))
            // over: everything is back to the regular plans
            assertFalse(HomeOfferManager.isTimedOfferActive(p, endMs + 1_000L))
            assertFalse(HomeOfferManager.isBiggestCampaignActive(p, endMs + 1_000L))
            assertNull(HomeOfferManager.endMillis(p, endMs + 1_000L))
        }
    }

    @Test
    fun `plain timed configs keep the per-device window and never turn on the campaign`() {
        for ((json, lifetime) in listOf(plainLifetimeJson to true, plainYearlyJson to false)) {
            val p = prefsWith(json)
            val cfg = HomeOfferManager.config(p)
            assertTrue(cfg.isEnabled)
            assertFalse(cfg.isBiggestCampaign)
            assertNull(cfg.endAtMillis)
            assertEquals(lifetime, cfg.targetsLifetime)
            assertFalse(HomeOfferManager.isTimedOfferActive(p, t0))               // not started: the window opens on the first Home visit
            HomeOfferManager.startIfNeeded(p, t0)
            val twoHours = 120 * 60_000L
            assertEquals(twoHours, HomeOfferManager.remainingMillis(p, t0))
            assertTrue(HomeOfferManager.isTimedOfferActive(p, t0 + twoHours - 60_000L))
            assertFalse(HomeOfferManager.isTimedOfferActive(p, t0 + twoHours + 60_000L))
            assertEquals(twoHours, HomeOfferManager.remainingMillis(p, t0 - day))  // a clock moved back cannot stretch it
            assertFalse(HomeOfferManager.isBiggestCampaignActive(p, t0 + 60_000L))   // so nothing is ever locked or badged
            assertEquals(if (lifetime) t0 + twoHours else null, HomeOfferManager.lifetimeEndMillis(p, t0 + 60_000L))
            assertEquals(if (lifetime) null else t0 + twoHours, HomeOfferManager.yearlyEndMillis(p, t0 + 60_000L))
        }
    }

    // ---- no_end: an offer with no end date ----

    @Test
    fun `no_end runs until enabled is switched off, for every phone, with no countdown`() {
        for ((json, lifetime) in listOf(
            """{"enabled": true, "duration_min": 0, "name": "Special Offer", "product": "lifetime", "no_end": true}""" to true,
            """{"enabled": true, "name": "Special Offer", "product": "yearly", "no_end": true}""" to false,
        )) {
            val p = prefsWith(json)
            val cfg = HomeOfferManager.config(p)
            assertTrue(cfg.isEnabled)
            assertTrue(cfg.isOpenEnded)
            assertEquals(lifetime, cfg.targetsLifetime)
            // on from the first moment (no per-device start) and still on ten years later
            for (now in listOf(t0, t0 + 3650 * day)) {
                assertTrue(HomeOfferManager.isTimedOfferActive(p, now))
                assertNull(HomeOfferManager.endMillis(p, now))                  // nothing to count down to: no pill on any screen
                assertNull(HomeOfferManager.yearlyEndMillis(p, now))
                assertNull(HomeOfferManager.lifetimeEndMillis(p, now))
            }
        }
    }

    @Test
    fun `no_end beats end_at, duration_min and the dismiss trigger, and enabled false still switches it off`() {
        val past = prefsWith("""{"enabled":true,"no_end":true,"end_at":"2020-01-01T00:00:00Z","duration_min":10,"trigger":"dismiss"}""")
        assertTrue(HomeOfferManager.isTimedOfferActive(past, t0))               // the old end date is ignored
        assertNull(HomeOfferManager.config(past).sharedEndMillis)
        assertFalse(HomeOfferManager.config(past).startsOnDismiss)              // it does not wait for a paywall to be closed
        assertFalse(HomeOfferManager.isTimedOfferActive(prefsWith("""{"enabled":false,"no_end":true}"""), t0))
        assertFalse(HomeOfferManager.isTimedOfferActive(prefsWith("""{"enabled":true,"no_end":false,"duration_min":0}"""), t0))   // 0 minutes without no_end is still OFF
        assertFalse(HomeOfferManager.isTimedOfferActive(prefsWith("""{"enabled":true,"duration_min":0}"""), t0))
    }

    @Test
    fun `no_end works with the Biggest offer campaign too`() {
        val p = prefsWith("""{"enabled":true,"name":"Biggest Offer","product":"lifetime","campaign":"biggest","no_end":true}""")
        assertTrue(HomeOfferManager.isBiggestCampaignActive(p, t0 + 3650 * day))
        assertNull(HomeOfferManager.endMillis(p, t0))
    }
}
