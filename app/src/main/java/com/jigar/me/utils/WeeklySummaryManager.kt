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
        "📊 Your child's weekly report",
        "🌟 A week of learning!",
        "🎉 This week's progress is in!",
        "📈 Your child's week in review",
        "💪 Another great week of practice!"
    )
    private val weeklyBodies = listOf(
        "This week your child solved {p} {pw} across {d} {dw}.",
        "{p} {pw} tackled over {d} {dw} this week — wonderful effort!",
        "A busy week: {p} {pw} across {d} {dw} of practice.",
        "Your child worked through {p} {pw} on {d} {dw} this week.",
        "{d} {dw} of practice and {p} {pw} solved this week!"
    )
    private val weeklyClosers = listOf(
        " A little practice this week keeps the momentum going.",
        " Keep it up — every session counts!",
        " Cheer them on for another great week!",
        " Small steps each day add up to big progress."
    )
    private val genericMessages = listOf(
        "📊 Weekly progress report" to "See how your child is doing — open Abacus for their weekly report and keep the learning going! 🌟",
        "🌟 A fresh week awaits!" to "Open Abacus with your child and start building this week's progress together.",
        "📈 Time for this week's report" to "Tap to open Abacus and see how your child's learning is going this week."
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

    fun notificationContent(prefs: AppPreferencesHelper): Pair<String, String> {
        if (hasActivityThisWeek(prefs)) {
            val problems = prefs.getCustomParamInt(AppConstants.WeeklySummary.weeklyProblems, 0)
            val days = activeDays(prefs).coerceAtLeast(1)
            val streak = prefs.getCustomParamInt(AppConstants.Streak.currentStreak, 0)
            val i = rotationIndex()
            val title = weeklyTitles[i % weeklyTitles.size]
            var body = weeklyBodies[i % weeklyBodies.size]
                .replace("{p}", "$problems")
                .replace("{pw}", if (problems == 1) "problem" else "problems")
                .replace("{d}", "$days")
                .replace("{dw}", if (days == 1) "day" else "days")
            if (streak > 0) body += " On a $streak-day streak! 🔥"
            body += weeklyClosers[i % weeklyClosers.size]
            return title to body
        }
        return genericMessages[rotationIndex() % genericMessages.size]
    }
}
