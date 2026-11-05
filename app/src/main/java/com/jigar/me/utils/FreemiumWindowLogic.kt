package com.jigar.me.utils

import java.util.Calendar
import java.util.TimeZone

/**
 * The rules of the freemium time limit, with no storage, no UI and no clock of its own (`now` is always passed in), so
 * they can be checked on their own. [FreemiumWindow] feeds them with the stored values. Mirrors iOS `FreemiumWindowLogic`.
 *
 * A NEW install gets the free tier for `windowDays` days from `start`. The limit is off when `windowDays <= 0`
 * (Remote Config `freemium_days` missing or 0) and never applies to a grandfathered install.
 */
object FreemiumWindowLogic {
    enum class Phase {
        OFF,      // no limit for this install (or no limit configured)
        ACTIVE,   // inside the free window
        ENDED,    // the window is over: the free tier is locked
    }

    const val DAY_MS = 86_400_000L

    /** Parent reminders at app open: on day 3 and day 7 of use (day 1 = the first 24 hours). */
    val reminderDays = listOf(3, 7)

    /** Local notifications: day 5 and day 7, at 18:00 local time. */
    val notificationDays = listOf(5, 7)
    const val NOTIFICATION_HOUR = 18

    fun phase(now: Long, start: Long?, windowDays: Int, grandfathered: Boolean): Phase {
        if (windowDays <= 0 || grandfathered || start == null) return Phase.OFF
        return if (now < endMillis(start, windowDays)) Phase.ACTIVE else Phase.ENDED
    }

    fun endMillis(start: Long, windowDays: Int): Long = start + windowDays * DAY_MS

    /** Whole days still free, rounded up (a window with 20 hours left says 1), never below 0. */
    fun daysLeft(now: Long, start: Long, windowDays: Int): Int {
        val left = endMillis(start, windowDays) - now
        return if (left <= 0) 0 else ((left + DAY_MS - 1) / DAY_MS).toInt()
    }

    /** 1-based day of use: the first 24 hours are day 1. A clock moved back counts as day 1. */
    fun dayNumber(now: Long, start: Long): Int {
        val elapsed = now - start
        return if (elapsed <= 0) 1 else (elapsed / DAY_MS).toInt() + 1
    }

    /**
     * The reminder to show at app open, if one is due: the latest of day 3 / day 7 that has been reached, is inside the
     * window and was not shown yet. [shownMask] has bit 1 for day 3 and bit 2 for day 7 (see [mask]).
     */
    fun dueReminderDay(now: Long, start: Long, windowDays: Int, shownMask: Int): Int? {
        if (phase(now, start, windowDays, false) != Phase.ACTIVE) return null
        val reached = dayNumber(now, start)
        return reminderDays.lastOrNull { it <= reached && it <= windowDays && shownMask and mask(it) == 0 }
    }

    /** Every reminder up to [day] counts as shown once the latest due one is shown. */
    fun maskShown(upToDay: Int): Int = reminderDays.filter { it <= upToDay }.fold(0) { acc, d -> acc or mask(d) }

    fun mask(day: Int): Int {
        val i = reminderDays.indexOf(day)
        return if (i < 0) 0 else 1 shl i
    }

    /**
     * When the local notifications fire: 18:00 on the calendar day that is day 5 / day 7 of use, as (day, epoch millis).
     * Times already in the past are left out; only days inside the window are scheduled.
     */
    fun notificationSchedule(now: Long, start: Long, windowDays: Int, zone: TimeZone = TimeZone.getDefault()): List<Pair<Int, Long>> =
        notificationDays.mapNotNull { d ->
            if (d > windowDays) return@mapNotNull null
            val cal = Calendar.getInstance(zone).apply {
                timeInMillis = start
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                add(Calendar.DAY_OF_YEAR, d - 1)
                set(Calendar.HOUR_OF_DAY, NOTIFICATION_HOUR)
            }
            val fire = cal.timeInMillis
            if (fire > now) d to fire else null
        }
}
