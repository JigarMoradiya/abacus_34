package com.jigar.me.utils

import java.util.Calendar
import java.util.TimeZone

/**
 * When a parent is asked about the plans again after the first days: the "returning-user paywall". No storage, no UI and
 * no clock of its own, so it can be checked on its own. Mirrors iOS `ReturningPaywallLogic`.
 *
 * Education parents often decide late, so a free user who keeps coming back gets a parent prompt on a few days of use
 * (day 1 = the first 24 hours since this install was first seen): the grown-up question first, then the plans.
 * Rules: at most one prompt a day, each scheduled day once, never on a day a paywall was already shown.
 */
object ReturningPaywallLogic {
    /** Days of use that get a prompt when Remote Config `returning_paywall_days` is missing or empty. */
    val defaultDays = listOf(3, 10, 21, 45)

    /**
     * Parses the Remote Config value. Missing / empty = the default days. "off", "none" or "0" = no prompts.
     * "3,7,14" = those days (positive whole numbers, sorted, at most 10).
     */
    fun schedule(raw: String?): List<Int> {
        val text = (raw ?: "").trim().lowercase()
        if (text.isEmpty()) return defaultDays
        if (text == "off" || text == "none" || text == "0") return emptyList()
        return text.split(",").mapNotNull { it.trim().toIntOrNull() }.filter { it > 0 }.distinct().sorted().take(10)
    }

    /** The day to prompt for now, if any: the latest scheduled day that has been reached and was not shown yet. */
    fun dueDay(
        now: Long, firstSeen: Long, schedule: List<Int>, shown: Set<Int>,
        lastPromptDay: String?, today: String, paywallShownToday: Boolean,
    ): Int? {
        if (lastPromptDay == today || paywallShownToday) return null
        val reached = FreemiumWindowLogic.dayNumber(now, firstSeen)
        return schedule.lastOrNull { it <= reached && it !in shown }
    }

    /** A prompt for [day] also uses up the earlier scheduled days, so a user who comes back late gets one prompt, not a queue. */
    fun markShown(day: Int, schedule: List<Int>, shown: Set<Int>): Set<Int> = shown + schedule.filter { it <= day }

    fun encode(set: Set<Int>): String = set.sorted().joinToString(",")
    fun decode(text: String): Set<Int> = text.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()

    /** "2026-11-02" in the given zone: the key of "once a day". */
    fun dayString(millis: Long, zone: TimeZone = TimeZone.getDefault()): String {
        val c = Calendar.getInstance(zone).apply { timeInMillis = millis }
        return "%04d-%02d-%02d".format(java.util.Locale.US, c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
    }
}
