package com.jigar.me.utils

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.utils.workers.WeeklySummaryWorker
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.max
import com.jigar.me.R

// Tracks the child's activity for the current week and schedules a weekly
// parent-facing summary notification (Sunday 6 PM). Mirrors StreakManager.
object WeeklySummaryManager {

    internal const val TEST_NOTIFICATION = false

    private fun sdf() = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    // The Sunday that ends the current week — the day the summary fires. Groups
    // Mon–Sun into one bucket so the Sunday report matches that week's stats.
    private fun weekKey(): String {
        val c = Calendar.getInstance()
        val daysUntilSunday = (8 - c.get(Calendar.DAY_OF_WEEK)) % 7   // 0 if Sunday
        c.add(Calendar.DAY_OF_YEAR, daysUntilSunday)
        return sdf().format(c.time)
    }

    // Call whenever the child completes problems (from the result-submission point).
    fun record(prefs: AppPreferencesHelper, problems: Int) {
        val thisWeek = weekKey()
        if (prefs.getCustomParam(AppConstants.WeeklySummary.weekStartDate, "") != thisWeek) {
            prefs.setCustomParam(AppConstants.WeeklySummary.weekStartDate, thisWeek)
            prefs.setCustomParamInt(AppConstants.WeeklySummary.weeklyProblems, 0)
            prefs.setCustomParam(AppConstants.WeeklySummary.weeklyActiveDates, "")
        }
        val total = prefs.getCustomParamInt(AppConstants.WeeklySummary.weeklyProblems, 0) + max(0, problems)
        prefs.setCustomParamInt(AppConstants.WeeklySummary.weeklyProblems, total)

        val today = sdf().format(Calendar.getInstance().time)
        val dates = prefs.getCustomParam(AppConstants.WeeklySummary.weeklyActiveDates, "")
            .split(",").filter { it.isNotEmpty() }.toMutableList()
        if (!dates.contains(today)) {
            dates.add(today)
            prefs.setCustomParam(AppConstants.WeeklySummary.weeklyActiveDates, dates.joinToString(","))
        }
    }

    private fun activeDays(prefs: AppPreferencesHelper): Int =
        prefs.getCustomParam(AppConstants.WeeklySummary.weeklyActiveDates, "")
            .split(",").filter { it.isNotEmpty() }.size

    private fun hasActivityThisWeek(prefs: AppPreferencesHelper): Boolean =
        prefs.getCustomParam(AppConstants.WeeklySummary.weekStartDate, "") == weekKey() &&
            prefs.getCustomParamInt(AppConstants.WeeklySummary.weeklyProblems, 0) > 0

    // Read-only weekly stats for the in-app report.
    data class WeeklyStats(val problems: Int, val days: Int, val streak: Int)
    fun readStats(prefs: AppPreferencesHelper): WeeklyStats {
        val hasThis = prefs.getCustomParam(AppConstants.WeeklySummary.weekStartDate, "") == weekKey()
        return WeeklyStats(
            problems = if (hasThis) prefs.getCustomParamInt(AppConstants.WeeklySummary.weeklyProblems, 0) else 0,
            days = if (hasThis) activeDays(prefs) else 0,
            streak = prefs.getCustomParamInt(AppConstants.Streak.currentStreak, 0)
        )
    }

    private fun rotationIndex(): Int = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)

    private val weeklyTitles = listOf(
        R.string.notif_weekly_title_1,
        R.string.notif_weekly_title_2,
        R.string.notif_weekly_title_3,
        R.string.notif_weekly_title_4,
        R.string.notif_weekly_title_5
    )
    // %1$s = "5 problems", %2$s = "3 days" (each a plural of its own)
    private val weeklyBodies = listOf(
        R.string.notif_weekly_body_1,
        R.string.notif_weekly_body_2,
        R.string.notif_weekly_body_3,
        R.string.notif_weekly_body_4,
        R.string.notif_weekly_body_5
    )
    private val weeklyClosers = listOf(
        R.string.notif_weekly_closer_1,
        R.string.notif_weekly_closer_2,
        R.string.notif_weekly_closer_3,
        R.string.notif_weekly_closer_4
    )
    private val genericMessages = listOf(
        R.string.notif_weekly_generic_title_1 to R.string.notif_weekly_generic_body_1,
        R.string.notif_weekly_generic_title_2 to R.string.notif_weekly_generic_body_2,
        R.string.notif_weekly_generic_title_3 to R.string.notif_weekly_generic_body_3
    )

    // Schedule (or reschedule) the Sunday 6 PM weekly summary.
    fun scheduleNotification(context: Context) {
        val delayMs: Long = if (TEST_NOTIFICATION) {
            10_000L
        } else {
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 18)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                if (before(now)) add(Calendar.WEEK_OF_YEAR, 1)
            }
            target.timeInMillis - now.timeInMillis
        }

        val request = OneTimeWorkRequestBuilder<WeeklySummaryWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .addTag("weekly_summary")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "weekly_summary_notification",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    // TEMP (testing): record one realistic session (+15 problems, today active)
    // through the REAL counter, then fire the weekly summary in 5s so it shows a
    // live, accumulating count (tap again to add more).
    fun fireTestNotification(context: Context, prefs: AppPreferencesHelper) {
        record(prefs, 15)
        val request = OneTimeWorkRequestBuilder<WeeklySummaryWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS)
            .addTag("weekly_summary_test")
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "weekly_summary_test", ExistingWorkPolicy.REPLACE, request
        )
    }

    /** The Sunday summary in the app language (the worker has no screen, so the language is looked up here). */
    fun notificationContent(context: Context, prefs: AppPreferencesHelper): Pair<String, String> {
        val text = context.inAppLanguage()
        if (hasActivityThisWeek(prefs)) {
            val problems = prefs.getCustomParamInt(AppConstants.WeeklySummary.weeklyProblems, 0)
            val days = activeDays(prefs).coerceAtLeast(1)
            val streak = prefs.getCustomParamInt(AppConstants.Streak.currentStreak, 0)
            val i = rotationIndex()
            val title = text.getString(weeklyTitles[i % weeklyTitles.size])
            var body = text.getString(
                weeklyBodies[i % weeklyBodies.size],
                text.resources.getQuantityString(R.plurals.notif_weekly_problems, problems, problems),
                text.resources.getQuantityString(R.plurals.notif_weekly_days, days, days),
            )
            if (streak > 0) body += " " + text.resources.getQuantityString(R.plurals.notif_weekly_streak, streak, streak)
            body += " " + text.getString(weeklyClosers[i % weeklyClosers.size])
            return title to body
        }
        val (titleRes, bodyRes) = genericMessages[rotationIndex() % genericMessages.size]
        return text.getString(titleRes) to text.getString(bodyRes)
    }
}
