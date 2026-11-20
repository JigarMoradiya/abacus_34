package com.jigar.me

import com.jigar.me.utils.OfferNotificationLogic
import com.jigar.me.utils.OfferNotificationLogic.Kind
import com.jigar.me.utils.parseEndAt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.TimeZone

class OfferNotificationLogicTest {

    private val utc = TimeZone.getTimeZone("UTC")
    private fun at(iso: String) = parseEndAt(iso)!!
    private fun schedule(now: String, firstSeen: String, end: String?) =
        OfferNotificationLogic.schedule(at(now), at(firstSeen), end?.let { at(it) }, utc)
    private fun kinds(list: List<OfferNotificationLogic.Reminder>) = list.map { it.kind }

    @Test
    fun `a campaign with an end date gets the evening nudge and the 24 hour and 3 hour reminders`() {
        // seen on 20 Nov at 10:00, ends 15 Dec 18:29:59
        val list = schedule("2026-11-20T10:05:00Z", "2026-11-20T10:00:00Z", "2026-12-15T18:29:59Z")
        assertEquals(listOf(Kind.NUDGE_1, Kind.ENDS_24H, Kind.ENDS_3H), kinds(list))
        assertEquals(at("2026-11-21T18:00:00Z"), list[0].fireAtMillis)             // the evening after
        assertEquals(at("2026-12-14T18:29:59Z"), list[1].fireAtMillis)             // 24 h before the end
        assertEquals(at("2026-12-15T15:29:59Z"), list[2].fireAtMillis)             // 3 h before the end
    }

    @Test
    fun `a short two hour window gets only the 30 minute reminder`() {
        val list = schedule("2026-11-20T10:00:00Z", "2026-11-20T10:00:00Z", "2026-11-20T12:00:00Z")
        assertEquals(listOf(Kind.ENDS_30M), kinds(list))
        assertEquals(at("2026-11-20T11:30:00Z"), list[0].fireAtMillis)
    }

    @Test
    fun `nothing is ever shown at night`() {
        // a two hour window seen at 21:30: its 30 minute reminder would be 23:00, so it is dropped (20:30 is already past)
        assertTrue(schedule("2026-11-20T21:30:00Z", "2026-11-20T21:30:00Z", "2026-11-20T23:30:00Z").isEmpty())
        // an end at 02:00: the 24 hour reminder (02:00 the day before) moves to 08:00, the 3 hour one (23:00) to 20:30 the evening before
        val list = schedule("2026-12-01T09:00:00Z", "2026-12-01T09:00:00Z", "2026-12-10T02:00:00Z")
        assertEquals(at("2026-12-09T08:00:00Z"), list.first { it.kind == Kind.ENDS_24H }.fireAtMillis)
        assertEquals(at("2026-12-09T20:30:00Z"), list.first { it.kind == Kind.ENDS_3H }.fireAtMillis)
        for (r in list) {
            val hour = java.util.Calendar.getInstance(utc).apply { timeInMillis = r.fireAtMillis }.get(java.util.Calendar.HOUR_OF_DAY)
            assertTrue("fires at $hour:xx", hour in 8..20)
        }
    }

    @Test
    fun `an offer with no end gets a nudge the next evening and one a week later`() {
        val list = schedule("2026-11-20T10:05:00Z", "2026-11-20T10:00:00Z", null)
        assertEquals(listOf(Kind.NUDGE_1, Kind.NUDGE_7), kinds(list))
        assertEquals(at("2026-11-21T18:00:00Z"), list[0].fireAtMillis)
        assertEquals(at("2026-11-27T18:00:00Z"), list[1].fireAtMillis)
        // after the week there is nothing left to schedule: the offer never nags forever
        assertTrue(schedule("2026-12-05T10:00:00Z", "2026-11-20T10:00:00Z", null).isEmpty())
    }

    @Test
    fun `reminders that would be too close keep the more useful one`() {
        // the nudge (18:00 the next day) and the 24 hour reminder (20:30 that evening) are 2.5 h apart: the "ends soon" one stays
        val list = schedule("2026-12-01T10:00:00Z", "2026-12-01T10:00:00Z", "2026-12-03T21:30:00Z")
        assertEquals(Kind.ENDS_24H, list.first().kind)
        assertTrue(list.none { it.kind == Kind.NUDGE_1 })
        // two reminders are never closer than 4 hours
        for (i in 1 until list.size) assertTrue(list[i].fireAtMillis - list[i - 1].fireAtMillis >= OfferNotificationLogic.MIN_GAP)
    }

    @Test
    fun `an offer that is about to end or already over has nothing to schedule`() {
        assertTrue(schedule("2026-12-15T18:20:00Z", "2026-11-20T10:00:00Z", "2026-12-15T18:29:59Z").isEmpty())   // 10 minutes left
        assertTrue(schedule("2026-12-16T09:00:00Z", "2026-11-20T10:00:00Z", "2026-12-15T18:29:59Z").isEmpty())   // over
    }

    @Test
    fun `the evening follows the phone's own time zone`() {
        val india = TimeZone.getTimeZone("Asia/Kolkata")      // UTC+5:30: 18:00 there is 12:30 UTC
        val list = OfferNotificationLogic.schedule(at("2026-11-20T04:35:00Z"), at("2026-11-20T04:30:00Z"), null, india)
        assertEquals(at("2026-11-21T12:30:00Z"), list.first().fireAtMillis)
    }
}
