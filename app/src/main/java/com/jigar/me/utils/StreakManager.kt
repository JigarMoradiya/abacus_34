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

    fun notificationContent(currentStreak: Int): Pair<String, String> {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val (title, body) = streakMessages[dayOfYear % streakMessages.size]
        return title.replace("{n}", currentStreak.toString()) to body
    }

    private val streakMessages = listOf(
        "🔥 Day {n} — keep the fire burning!" to
            "Your child's focus is growing stronger every day. A little practice today protects everything they've built.",
        "{n} days straight — what a learner! ⭐" to
            "Consistency like this is rare at any age. You're raising someone who truly finishes what they start.",
        "Don't break the {n}-day chain! 🔗" to
            "Every link in that chain represents real growth. Your child has come so far — today keeps it unbroken.",
        "🧠 {n} days of brain training!" to
            "The mental strength your child builds through daily practice will carry them through school and beyond.",
        "{n} days — a milestone is near! 🏆" to
            "Your child is on a remarkable run. A few minutes of practice today keeps that journey alive.",
        "Day {n} is waiting ✨" to
            "Small daily habits shape a child's future in ways we can't always see. Today's session matters more than you know.",
        "🔥 {n} days in — raising a champion!" to
            "Very few children build this kind of discipline. Be proud — and keep that momentum alive today.",
        "{n}-day habit in the making! 💡" to
            "Research shows consistent daily practice reshapes the brain for life. Your child is well on their way.",
        "Day {n} — almost automatic! 🎯" to
            "The best learners make practice a part of their daily life. Your child is already becoming one of them.",
        "🌟 {n} days of pure dedication!" to
            "Dedication at this age is extraordinary. A short session today adds to something truly special.",
        "Day {n} — the streak lives on! 🔥" to
            "Every day your child practices, their confidence and mental speed grow. Today is too important to miss.",
        "{n} days — your child is unstoppable! 💪" to
            "The best investment you can make is in daily learning. A few minutes today goes a long way.",
        "🎯 {n} days — precision in progress!" to
            "Abacus builds speed, focus, and confidence all at once. Your child is growing all three — keep it going.",
        "Day {n} — steady wins the race! 🐢" to
            "It's not about one big effort — it's about showing up every day. Your child already knows that.",
        "🌙 End the day right — {n} days strong!" to
            "A quick practice before bed is a powerful way to close the day. Your child will feel proud they showed up.",
        "{n} days — this habit is real! 🏅" to
            "Practice is becoming a part of who your child is. That's something worth protecting today.",
        "🔥 Day {n} check-in!" to
            "Champions show up every single day — even when life gets busy. Your child can do that today.",
        "Day {n} — future mathematician in training! 🧮" to
            "Every session sharpens their mind a little more. What feels small today adds up to something extraordinary.",
        "{n} days and still counting! ✨" to
            "Your child is among a rare group of consistent learners. That's something worth protecting — especially today.",
        "🌟 Day {n} — don't stop now!" to
            "You've invested so much in your child's learning. Today's practice is the simplest way to honor that."
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
