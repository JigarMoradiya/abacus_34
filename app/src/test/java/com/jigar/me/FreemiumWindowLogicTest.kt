package com.jigar.me

import com.jigar.me.utils.FreemiumWindowLogic
import com.jigar.me.utils.ReturningPaywallLogic
import com.jigar.me.utils.FreemiumWindowLogic.DAY_MS
import com.jigar.me.utils.FreemiumWindowLogic.Phase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

/** The freemium window rules, on fixed timestamps (no Android runtime, no clock). Same cases as the iOS self-check. */
class FreemiumWindowLogicTest {
    private val t0 = 1_800_000_000_000L
    private fun at(days: Double) = t0 + (days * DAY_MS).toLong()
    private fun phase(now: Long, start: Long? = t0, days: Int = 7, grand: Boolean = false) =
        FreemiumWindowLogic.phase(now, start, days, grand)

    @Test fun `no limit when days is 0 or negative, no start, or grandfathered`() {
        assertEquals(Phase.OFF, phase(at(1.0), days = 0))
        assertEquals(Phase.OFF, phase(at(1.0), days = -1))
        assertEquals(Phase.OFF, phase(at(1.0), start = null))
        assertEquals(Phase.OFF, phase(at(30.0), grand = true))
    }

    @Test fun `active inside the window and ended from exactly 7 days`() {
        assertEquals(Phase.ACTIVE, phase(at(0.0)))
        assertEquals(Phase.ACTIVE, phase(at(6.99)))
        assertEquals(Phase.ENDED, phase(at(7.0)))
        assertEquals(Phase.ENDED, phase(at(40.0)))
        assertEquals(Phase.ACTIVE, phase(at(-2.0))) // a clock moved back never ends the window early
    }

    @Test fun `days left rounds up and never goes below 0`() {
        assertEquals(7, FreemiumWindowLogic.daysLeft(at(0.0), t0, 7))
        assertEquals(6, FreemiumWindowLogic.daysLeft(at(1.0), t0, 7))
        assertEquals(1, FreemiumWindowLogic.daysLeft(at(6.5), t0, 7))
        assertEquals(0, FreemiumWindowLogic.daysLeft(at(7.0), t0, 7))
        assertEquals(0, FreemiumWindowLogic.daysLeft(at(9.0), t0, 7))
    }

    @Test fun `day number is 1-based and a clock moved back is day 1`() {
        assertEquals(1, FreemiumWindowLogic.dayNumber(at(0.0), t0))
        assertEquals(1, FreemiumWindowLogic.dayNumber(at(0.99), t0))
        assertEquals(2, FreemiumWindowLogic.dayNumber(at(1.0), t0))
        assertEquals(3, FreemiumWindowLogic.dayNumber(at(2.0), t0))
        assertEquals(7, FreemiumWindowLogic.dayNumber(at(6.0), t0))
        assertEquals(1, FreemiumWindowLogic.dayNumber(at(-3.0), t0))
    }

    @Test fun `reminders come on day 3 and day 7, once each, and only inside the window`() {
        fun due(days: Double, mask: Int = 0, window: Int = 7) = FreemiumWindowLogic.dueReminderDay(at(days), t0, window, mask)
        assertNull(due(1.5))
        assertEquals(3, due(2.0))
        assertNull(due(2.0, mask = 1))
        assertNull(due(5.0, mask = 1))
        assertEquals(7, due(6.0, mask = 1))
        assertEquals(7, due(6.0)) // a late first open shows day 7 only
        assertNull(due(6.0, mask = FreemiumWindowLogic.maskShown(7)))
        assertNull(due(7.5)) // the window is over
        assertNull(due(4.0, mask = 1, window = 5)) // day 7 is outside a 5-day window
        assertNull(due(2.0, window = 2)) // day 3 is outside a 2-day window
    }

    @Test fun `shown masks cover the earlier reminders`() {
        assertEquals(3, FreemiumWindowLogic.maskShown(7))
        assertEquals(1, FreemiumWindowLogic.maskShown(3))
    }

    @Test fun `notifications fire at 18-00 local on day 5 and day 7 and skip past times`() {
        val utc = TimeZone.getTimeZone("UTC")
        fun millis(day: Int, hour: Int) = Calendar.getInstance(utc).apply { clear(); set(2026, Calendar.JANUARY, day, hour, 0, 0) }.timeInMillis
        val start = millis(1, 10)
        val all = FreemiumWindowLogic.notificationSchedule(start, start, 7, utc)
        assertEquals(listOf(5, 7), all.map { it.first })
        assertEquals(millis(5, 18), all[0].second)
        assertEquals(millis(7, 18), all[1].second)
        assertEquals(listOf(7), FreemiumWindowLogic.notificationSchedule(millis(5, 19), start, 7, utc).map { it.first })
        assertEquals(listOf(5), FreemiumWindowLogic.notificationSchedule(start, start, 6, utc).map { it.first })
        assertEquals(emptyList<Int>(), FreemiumWindowLogic.notificationSchedule(start, start, 4, utc).map { it.first })
    }

    // ---- returning-user paywall ----

    private val sched = listOf(3, 10, 21, 45)
    private fun due(days: Double, shown: Set<Int> = emptySet(), last: String? = null, today: String = "d", paywall: Boolean = false) =
        ReturningPaywallLogic.dueDay(at(days), t0, sched, shown, last, today, paywall)

    @Test fun `schedule parsing`() {
        assertEquals(listOf(3, 10, 21, 45), ReturningPaywallLogic.schedule(null))
        assertEquals(listOf(3, 10, 21, 45), ReturningPaywallLogic.schedule("  "))
        assertEquals(emptyList<Int>(), ReturningPaywallLogic.schedule("off"))
        assertEquals(emptyList<Int>(), ReturningPaywallLogic.schedule("NONE"))
        assertEquals(emptyList<Int>(), ReturningPaywallLogic.schedule("0"))
        assertEquals(listOf(3, 7, 14), ReturningPaywallLogic.schedule("14, 3,7,7,-2,x"))
        assertEquals(10, ReturningPaywallLogic.schedule("1,2,3,4,5,6,7,8,9,10,11,12").size)
    }

    @Test fun `returning prompt days`() {
        assertNull(due(1.0))
        assertEquals(3, due(2.0))
        assertNull(due(2.0, shown = setOf(3)))
        assertEquals(10, due(9.0, shown = setOf(3)))
        assertEquals(21, due(30.0)) // a late return gets the latest due day only
        assertEquals(setOf(3, 10, 21), ReturningPaywallLogic.markShown(21, sched, emptySet()))
        assertNull(due(30.0, shown = setOf(3, 10, 21)))
        assertEquals(45, due(44.0, shown = setOf(3, 10, 21)))
        assertNull(due(60.0, shown = setOf(3, 10, 21, 45)))
    }

    @Test fun `one prompt a day and none after a paywall`() {
        assertNull(due(2.0, last = "d", today = "d"))
        assertEquals(3, due(2.0, last = "yesterday", today = "d"))
        assertNull(due(2.0, paywall = true))
    }

    @Test fun `encode decode and day string`() {
        assertEquals(setOf(3, 10), ReturningPaywallLogic.decode(ReturningPaywallLogic.encode(setOf(10, 3))))
        val utc = TimeZone.getTimeZone("UTC")
        val noon = Calendar.getInstance(utc).apply { clear(); set(2026, Calendar.MARCH, 9, 12, 0, 0) }.timeInMillis
        assertEquals("2026-03-09", ReturningPaywallLogic.dayString(noon, utc))
    }
}
