package com.jigar.me

import com.jigar.me.utils.WinBackLogic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class WinBackLogicTest {

    @Test fun daysBetweenCountsCalendarDays() {
        assertEquals(0, WinBackLogic.daysBetween("2026-11-27", "2026-11-27"))
        assertEquals(7, WinBackLogic.daysBetween("2026-11-27", "2026-12-04"))
        assertEquals(14, WinBackLogic.daysBetween("2026-12-25", "2027-01-08"))   // over the new year
        assertEquals(31, WinBackLogic.daysBetween("2026-03-01", "2026-04-01"))   // over a daylight-saving change
        assertEquals(-1, WinBackLogic.daysBetween("2026-11-27", "2026-11-26"))   // clock turned back
        assertNull(WinBackLogic.daysBetween("", "2026-11-27"))
        assertNull(WinBackLogic.daysBetween("2026-13-40", "2026-11-27"))
    }

    @Test fun streakReminderOnlyOnTheOpenDayAndTwoDaysAfter() {
        assertTrue(WinBackLogic.streakReminderAllowed("2026-11-23", "2026-11-23"))   // Monday open: Monday
        assertTrue(WinBackLogic.streakReminderAllowed("2026-11-23", "2026-11-24"))   // Tuesday
        assertTrue(WinBackLogic.streakReminderAllowed("2026-11-23", "2026-11-25"))   // Wednesday
        assertFalse(WinBackLogic.streakReminderAllowed("2026-11-23", "2026-11-26"))  // Thursday: stops
        assertFalse(WinBackLogic.streakReminderAllowed("2026-11-23", "2026-12-23"))
        assertTrue(WinBackLogic.streakReminderAllowed("", "2026-11-26"))             // never recorded: as before
    }

    @Test fun winBackOnlyAfterEnoughDaysAway() {
        assertFalse(WinBackLogic.winBackDue("2026-11-27", "2026-12-03", 7))   // 6 days
        assertTrue(WinBackLogic.winBackDue("2026-11-27", "2026-12-04", 7))    // 7 days
        assertTrue(WinBackLogic.winBackDue("2026-11-27", "2026-12-05", 7))    // postponed a day
        assertFalse(WinBackLogic.winBackDue("2026-12-02", "2026-12-04", 7))   // opened again meanwhile
        assertFalse(WinBackLogic.winBackDue("2026-11-27", "2026-12-10", 14))
        assertTrue(WinBackLogic.winBackDue("2026-11-27", "2026-12-11", 14))
        assertFalse(WinBackLogic.winBackDue("", "2026-12-11", 7))            // unknown last open: never
    }

    @Test fun fireAtIsHalfPastSixOnTheRightDay() {
        val zone = TimeZone.getTimeZone("Asia/Kolkata")
        val open = Calendar.getInstance(zone).apply { set(2026, Calendar.NOVEMBER, 27, 23, 50, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis
        val at = Calendar.getInstance(zone).apply { timeInMillis = WinBackLogic.fireAt(open, 7, zone) }
        assertEquals(2026, at.get(Calendar.YEAR)); assertEquals(Calendar.DECEMBER, at.get(Calendar.MONTH)); assertEquals(4, at.get(Calendar.DAY_OF_MONTH))
        assertEquals(18, at.get(Calendar.HOUR_OF_DAY)); assertEquals(30, at.get(Calendar.MINUTE))
        val day14 = Calendar.getInstance(zone).apply { timeInMillis = WinBackLogic.fireAtFromDay("2026-11-27", 14, zone)!! }
        assertEquals(11, day14.get(Calendar.DAY_OF_MONTH)); assertEquals(Calendar.DECEMBER, day14.get(Calendar.MONTH)); assertEquals(18, day14.get(Calendar.HOUR_OF_DAY))
        assertNull(WinBackLogic.fireAtFromDay("", 14, zone))
    }

    @Test fun fireAtAcrossDaylightSavingKeepsTheLocalTime() {
        val zone = TimeZone.getTimeZone("America/New_York")   // clocks go back on 2026-11-01
        val open = Calendar.getInstance(zone).apply { set(2026, Calendar.OCTOBER, 28, 9, 0, 0) }.timeInMillis
        val at = Calendar.getInstance(zone).apply { timeInMillis = WinBackLogic.fireAt(open, 7, zone) }
        assertEquals(4, at.get(Calendar.DAY_OF_MONTH)); assertEquals(18, at.get(Calendar.HOUR_OF_DAY)); assertEquals(30, at.get(Calendar.MINUTE))
    }
}
