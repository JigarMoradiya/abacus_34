package com.jigar.me.utils

import java.util.Calendar
import java.util.TimeZone

/**
 * When the local notifications of a running Home offer (Remote Config `home_offer`) fire. Pure, so it can be unit tested. Mirrors iOS
 * OfferNotificationLogic.
 *
 * It is a gentle schedule for a kids' app: at most about four notifications per offer, never at night, never two close together.
 *  - NUDGE_1: the evening after this phone first saw the offer ("it is still on").
 *  - NUDGE_7: a week after, only for an offer with no end (an offer with an end has its reminders below).
 *  - ENDS_24H / ENDS_3H / ENDS_30M: "ends soon", 24 hours, 3 hours and 30 minutes before the end. The last one is what a short
 *    per-device window (say 2 hours) gets, because it is too short for the others.
 */
object OfferNotificationLogic {

    enum class Kind { ENDS_24H, ENDS_3H, ENDS_30M, NUDGE_1, NUDGE_7;
        /** The "Ends soon!" wording: the three reminders before the end. The nudges only say the offer is on. */
        val isEndsSoon: Boolean get() = this == ENDS_24H || this == ENDS_3H || this == ENDS_30M
    }

    data class Reminder(val kind: Kind, val fireAtMillis: Long)

    const val MINUTE = 60_000L
    const val HOUR = 60 * MINUTE
    const val DAY = 24 * HOUR

    /** Nothing is shown before 08:00 or from 21:00 local time. */
    const val EARLIEST_HOUR = 8
    const val LATEST_HOUR = 21
    /** A nudge goes out at 18:00 local time. */
    const val NUDGE_HOUR = 18
    /** Two notifications of one offer are never closer than this. */
    const val MIN_GAP = 4 * HOUR
    /** A notification never fires later than this before the end, and never sooner than this after now. */
    const val BEFORE_END_MARGIN = 15 * MINUTE
    const val AFTER_NOW_MARGIN = 2 * MINUTE

    /**
     * @param now the moment of scheduling
     * @param firstSeen when this phone first saw the offer (the per-device start; `now` when unknown)
     * @param endMillis the offer's end, null for an offer with no end
     * @return the notifications to schedule, in time order (empty when there is nothing worth saying)
     */
    fun schedule(now: Long, firstSeen: Long, endMillis: Long?, zone: TimeZone = TimeZone.getDefault()): List<Reminder> {
        // in priority order: when two are too close the first one stays
        val candidates = buildList {
            if (endMillis != null) {
                add(Reminder(Kind.ENDS_24H, endMillis - DAY))
                add(Reminder(Kind.ENDS_3H, endMillis - 3 * HOUR))
                add(Reminder(Kind.ENDS_30M, endMillis - 30 * MINUTE))
            }
            add(Reminder(Kind.NUDGE_1, nextEvening(firstSeen + 20 * HOUR, zone)))
            if (endMillis == null) add(Reminder(Kind.NUDGE_7, nextEvening(firstSeen + 7 * DAY - 4 * HOUR, zone)))
        }
        val kept = mutableListOf<Reminder>()
        for (candidate in candidates) {
            val fire = withinDaytime(candidate.fireAtMillis, zone)
            if (fire <= now + AFTER_NOW_MARGIN) continue
            if (endMillis != null && fire > endMillis - BEFORE_END_MARGIN) continue
            if (kept.any { kotlin.math.abs(it.fireAtMillis - fire) < MIN_GAP }) continue
            kept += Reminder(candidate.kind, fire)
        }
        return kept.sortedBy { it.fireAtMillis }
    }

    /** The first 18:00 (local) at or after [from]. */
    private fun nextEvening(from: Long, zone: TimeZone): Long {
        val cal = Calendar.getInstance(zone).apply { timeInMillis = from; set(Calendar.HOUR_OF_DAY, NUDGE_HOUR); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
        if (cal.timeInMillis < from) cal.add(Calendar.DAY_OF_MONTH, 1)
        return cal.timeInMillis
    }

    /** [millis] when it falls between 08:00 and 21:00 local, else 08:00 the same morning (too early) or 20:30 the same evening (too late). */
    private fun withinDaytime(millis: Long, zone: TimeZone): Long {
        val cal = Calendar.getInstance(zone).apply { timeInMillis = millis }
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        return when {
            hour < EARLIEST_HOUR -> cal.apply { set(Calendar.HOUR_OF_DAY, EARLIEST_HOUR); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis
            hour >= LATEST_HOUR -> cal.apply { set(Calendar.HOUR_OF_DAY, LATEST_HOUR - 1); set(Calendar.MINUTE, 30); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis
            else -> millis
        }
    }
}
