package com.jigar.me.utils

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.utils.workers.StreakNotificationWorker
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import com.jigar.me.R

data class StreakData(
    val currentStreak: Int,
    val longestStreak: Int,
    val lastActivityDate: String,
    val totalActiveDays: Int,
    val shields: Int,
    val claimedRewards: List<Int>
)

data class StreakResult(
    val data: StreakData,
    val milestoneAwarded: Int?
)

object StreakManager {

    // Create a new instance per call — SimpleDateFormat is not thread-safe
    private fun sdf() = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun today(): String = sdf().format(Calendar.getInstance().time)

    fun checkAndUpdate(prefs: AppPreferencesHelper): StreakResult {
        val fmt      = sdf()
        val todayStr = fmt.format(Calendar.getInstance().time)
        val lastDate  = prefs.getCustomParam(AppConstants.Streak.lastActivityDate, "")
        var current   = prefs.getCustomParamInt(AppConstants.Streak.currentStreak, 0)
        var longest   = prefs.getCustomParamInt(AppConstants.Streak.longestStreak, 0)
        var total     = prefs.getCustomParamInt(AppConstants.Streak.totalActiveDays, 0)
        var shields   = prefs.getCustomParamInt(AppConstants.Streak.streakShields, 0)
        val claimedStr = prefs.getCustomParam(AppConstants.Streak.claimedRewards, "")
        val claimed   = if (claimedStr.isEmpty()) mutableListOf()
                        else claimedStr.split(",").mapNotNull { it.trim().toIntOrNull() }.toMutableList()

        // Already recorded today — return current state unchanged
        if (lastDate == todayStr) {
            return StreakResult(
                data = StreakData(current, longest, lastDate, total, shields, claimed),
                milestoneAwarded = null
            )
        }

        val yesterdayStr = dayOffset(fmt, todayStr, -1)
        val twoDaysAgo   = dayOffset(fmt, todayStr, -2)

        current = when {
            lastDate == yesterdayStr -> current + 1
            lastDate == twoDaysAgo && shields > 0 -> {
                shields = (shields - 1).coerceAtLeast(0)
                current + 1
            }
            else -> 1
        }

        total += 1
        if (current > longest) longest = current

        // Grant a shield when hitting 7 or 21 consecutive days (max 2 total)
        AppConstants.Streak.shieldMilestones.forEach { m ->
            if (current == m && shields < 2) shields++
        }

        // Award the lowest unclaimed milestone that has been reached
        var milestoneAwarded: Int? = null
        for (m in AppConstants.Streak.milestones) {
            if (current >= m && !claimed.contains(m)) {
                claimed.add(m)
                milestoneAwarded = m
                break
            }
        }

        prefs.setCustomParam(AppConstants.Streak.lastActivityDate, todayStr)
        prefs.setCustomParamInt(AppConstants.Streak.currentStreak, current)
        prefs.setCustomParamInt(AppConstants.Streak.longestStreak, longest)
        prefs.setCustomParamInt(AppConstants.Streak.totalActiveDays, total)
        prefs.setCustomParamInt(AppConstants.Streak.streakShields, shields)
        prefs.setCustomParam(AppConstants.Streak.claimedRewards, claimed.joinToString(","))

        return StreakResult(
            data = StreakData(current, longest, todayStr, total, shields, claimed),
            milestoneAwarded = milestoneAwarded
        )
    }

    fun read(prefs: AppPreferencesHelper): StreakData {
        val claimedStr = prefs.getCustomParam(AppConstants.Streak.claimedRewards, "")
        return StreakData(
            currentStreak    = prefs.getCustomParamInt(AppConstants.Streak.currentStreak, 0),
            longestStreak    = prefs.getCustomParamInt(AppConstants.Streak.longestStreak, 0),
            lastActivityDate = prefs.getCustomParam(AppConstants.Streak.lastActivityDate, ""),
            totalActiveDays  = prefs.getCustomParamInt(AppConstants.Streak.totalActiveDays, 0),
            shields          = prefs.getCustomParamInt(AppConstants.Streak.streakShields, 0),
            claimedRewards   = if (claimedStr.isEmpty()) emptyList()
                               else claimedStr.split(",").mapNotNull { it.trim().toIntOrNull() }
        )
    }

    // Schedule (or reschedule) the 7 PM daily streak reminder
    // TEST_MODE: set to true to fire after 10 seconds instead of waiting for 7 PM
    internal const val TEST_NOTIFICATION = false

    /** Debug builds only (`--ez ab_notify_now true`): the reminder is shown even if the child already practised today. */
    @Volatile internal var debugShowNow = false

    fun scheduleNotification(context: Context) {
        val delayMs: Long = if (TEST_NOTIFICATION) {
            10_000L
        } else {
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 19)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                // If 7 PM already passed today, fire tomorrow
                if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
            }
            target.timeInMillis - now.timeInMillis
        }

        val request = OneTimeWorkRequestBuilder<StreakNotificationWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .addTag("streak_notification")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "streak_daily_notification",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    /** The streak reminder in the app language (the worker has no screen, so the language is looked up here). */
    fun notificationContent(context: Context, currentStreak: Int): Pair<String, String> {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val (titleRes, bodyRes) = streakMessages[dayOfYear % streakMessages.size]
        val text = context.inAppLanguage()
        return text.getString(titleRes, currentStreak) to text.getString(bodyRes)
    }

    private val streakMessages = listOf(
        R.string.notif_streak_title_1 to R.string.notif_streak_body_1,
        R.string.notif_streak_title_2 to R.string.notif_streak_body_2,
        R.string.notif_streak_title_3 to R.string.notif_streak_body_3,
        R.string.notif_streak_title_4 to R.string.notif_streak_body_4,
        R.string.notif_streak_title_5 to R.string.notif_streak_body_5,
        R.string.notif_streak_title_6 to R.string.notif_streak_body_6,
        R.string.notif_streak_title_7 to R.string.notif_streak_body_7,
        R.string.notif_streak_title_8 to R.string.notif_streak_body_8,
        R.string.notif_streak_title_9 to R.string.notif_streak_body_9,
        R.string.notif_streak_title_10 to R.string.notif_streak_body_10,
        R.string.notif_streak_title_11 to R.string.notif_streak_body_11,
        R.string.notif_streak_title_12 to R.string.notif_streak_body_12,
        R.string.notif_streak_title_13 to R.string.notif_streak_body_13,
        R.string.notif_streak_title_14 to R.string.notif_streak_body_14,
        R.string.notif_streak_title_15 to R.string.notif_streak_body_15,
        R.string.notif_streak_title_16 to R.string.notif_streak_body_16,
        R.string.notif_streak_title_17 to R.string.notif_streak_body_17,
        R.string.notif_streak_title_18 to R.string.notif_streak_body_18,
        R.string.notif_streak_title_19 to R.string.notif_streak_body_19,
        R.string.notif_streak_title_20 to R.string.notif_streak_body_20
    )

    private fun dayOffset(fmt: SimpleDateFormat, dateStr: String, days: Int): String {
        return try {
            val cal = Calendar.getInstance()
            cal.time = fmt.parse(dateStr) ?: return ""
            cal.add(Calendar.DAY_OF_YEAR, days)
            fmt.format(cal.time)
        } catch (_: Exception) { "" }
    }
}
