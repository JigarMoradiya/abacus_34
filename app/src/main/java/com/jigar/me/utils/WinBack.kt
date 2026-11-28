package com.jigar.me.utils

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.utils.workers.WinBackWorker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

/**
 * The rules of the "come back" notifications, kept free of Android so they can be unit tested. Dates are the app's own "yyyy-MM-dd" day keys
 * (the phone's calendar day, see [StreakManager.today]).
 *
 * - A child who has not opened the app for 7 days gets one notification, and one more at 14 days; every app open moves both forward again.
 * - The daily 7 PM streak reminder runs only on the day of the last open and the 2 days after it, then the come-back ones take over (no more
 *   daily reminders for a child who stopped).
 * - At most one of the app's own notifications a day: a come-back notification that finds another one already shown that day waits for the
 *   next day (at most [MAX_POSTPONES] times).
 */
object WinBackLogic {
    /** Days after the last app open. */
    val DAYS = listOf(7, 14)
    const val HOUR = 18
    const val MINUTE = 30
    const val MAX_POSTPONES = 2
    /** The streak reminder runs on the day of the last open and this many days after it. */
    const val STREAK_REMINDER_DAYS = 2

    private const val DAY_MS = 86_400_000L

    /** Whole calendar days from [from] to [to] (both "yyyy-MM-dd"), null when one of them can not be read. */
    fun daysBetween(from: String, to: String): Int? {
        // read in UTC: the difference of two midnights is then always a whole number of days (no daylight-saving hour)
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC"); isLenient = false }
        val a = runCatching { fmt.parse(from)?.time }.getOrNull() ?: return null
        val b = runCatching { fmt.parse(to)?.time }.getOrNull() ?: return null
        return ((b - a) / DAY_MS).toInt()
    }

    /** True while the 7 PM streak reminder may still be shown: the last open was today or at most [STREAK_REMINDER_DAYS] days ago. An unknown
     *  last open (never recorded) keeps the old behaviour. */
    fun streakReminderAllowed(lastOpen: String, today: String): Boolean {
        if (lastOpen.isEmpty()) return true
        val d = daysBetween(lastOpen, today) ?: return true
        return d <= STREAK_REMINDER_DAYS
    }

    /** True when the come-back notification of [day] is still right: the app has not been opened for at least [day] days. */
    fun winBackDue(lastOpen: String, today: String, day: Int): Boolean {
        if (lastOpen.isEmpty()) return false
        val d = daysBetween(lastOpen, today) ?: return false
        return d >= day
    }

    /** The moment, in [zone], of [daysLater] days after the calendar day of [fromMillis] at [HOUR]:[MINUTE]. */
    fun fireAt(fromMillis: Long, daysLater: Int, zone: TimeZone = TimeZone.getDefault()): Long =
        Calendar.getInstance(zone).apply {
            timeInMillis = fromMillis
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_YEAR, daysLater)
            set(Calendar.HOUR_OF_DAY, HOUR); set(Calendar.MINUTE, MINUTE)
        }.timeInMillis

    /** The moment of [daysLater] days after the day key [date] ("yyyy-MM-dd", a local calendar day), null when it can not be read. */
    fun fireAtFromDay(date: String, daysLater: Int, zone: TimeZone = TimeZone.getDefault()): Long? {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = zone; isLenient = false }
        val start = runCatching { fmt.parse(date)?.time }.getOrNull() ?: return null
        return fireAt(start, daysLater, zone)
    }
}

/** Schedules the come-back notifications ([WinBackLogic]) and remembers on which day the app last showed one of its own notifications. */
object WinBackNotifications {
    private const val SHOWN_DATE = "local_notification_shown_date"
    private fun workName(day: Int) = "winback_day$day"

    /** Debug builds only (`--ei ab_winback N`, N = 7 or 14): the app acts as if it was last opened N days ago and shows that notification in
     *  about 10 seconds (leave the app right away to see it). */
    @Volatile internal var debugDay: Int? = null

    /** The last app open: the day key [StreakManager.noteAppOpen] keeps (an update from an older version falls back to the last streak day). */
    fun lastOpenDate(prefs: AppPreferencesHelper): String =
        prefs.getCustomParam(AppConstants.Streak.openDate, "").ifEmpty { prefs.getCustomParam(AppConstants.Streak.lastActivityDate, "") }

    /** Every app open: both come-back notifications start again from today (day 7, which then queues day 14). */
    fun scheduleFromOpen(context: Context, prefs: AppPreferencesHelper, now: Long = System.currentTimeMillis()) {
        val wm = WorkManager.getInstance(context)
        wm.cancelUniqueWork(workName(14))
        val debug = if (com.jigar.me.BuildConfig.DEBUG) debugDay?.also { debugDay = null } else null
        if (debug != null && debug in WinBackLogic.DAYS) {
            // pretend the last open was `debug` days ago, then fire in 10 seconds
            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -debug) }
            prefs.setCustomParam(AppConstants.Streak.openDate, SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time))
            // (the "one notification a day" rule still applies: another notification shown today makes it wait for tomorrow)
            wm.cancelUniqueWork(workName(7))
            enqueue(context, debug, 10_000L, 0, ExistingWorkPolicy.REPLACE)
            return
        }
        enqueue(context, 7, WinBackLogic.fireAt(now, 7) - now, 0, ExistingWorkPolicy.REPLACE)
    }

    /** Day 7 was shown: queue day 14, counted from the last open. */
    fun scheduleDay14(context: Context, prefs: AppPreferencesHelper, now: Long = System.currentTimeMillis()) {
        val at = WinBackLogic.fireAtFromDay(lastOpenDate(prefs), 14) ?: return
        enqueue(context, 14, maxOf(0L, at - now), 0, ExistingWorkPolicy.REPLACE)
    }

    /** Another notification of the app was already shown today: the same one tries again tomorrow at the same time. Runs inside that worker,
     *  so the new request is appended to it (REPLACE would cancel the running worker). */
    fun postpone(context: Context, day: Int, postpones: Int, now: Long = System.currentTimeMillis()) {
        enqueue(context, day, WinBackLogic.fireAt(now, 1) - now, postpones + 1, ExistingWorkPolicy.APPEND_OR_REPLACE)
    }

    private fun enqueue(context: Context, day: Int, delayMs: Long, postpones: Int, policy: ExistingWorkPolicy) {
        val request = OneTimeWorkRequestBuilder<WinBackWorker>()
            .setInitialDelay(maxOf(0L, delayMs), TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(WinBackWorker.KEY_DAY to day, WinBackWorker.KEY_POSTPONES to postpones))
            .addTag("winback_notification")
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(workName(day), policy, request)
    }

    /** Every notification the app shows itself calls this (streak, weekly summary, free week, offers, come-back). */
    fun markShown(prefs: AppPreferencesHelper) = prefs.setCustomParam(SHOWN_DATE, StreakManager.today())

    fun shownToday(prefs: AppPreferencesHelper): Boolean = prefs.getCustomParam(SHOWN_DATE, "") == StreakManager.today()
}
