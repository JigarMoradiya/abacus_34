package com.jigar.me.utils

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.jigar.me.data.pref.PreferencesHelper
import com.jigar.me.utils.FreemiumWindowLogic.Phase
import com.jigar.me.utils.workers.FreemiumNotificationWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * The freemium time limit for NEW installs (see [FreemiumWindowLogic] for the rules). Mirrors iOS `FreemiumWindow`.
 *
 * - Remote Config `freemium_days` (<= 0 or missing = off, the default) is the length of the free window.
 * - Remote Config `free_core_after_window`: 2 (default) keeps Free Mode + Level 1 free once the window is over, 0 locks everything.
 * - EVERY user who is not subscribed (and has no admin plan) gets the window, existing users too: it starts the first time the
 *   app runs with the limit ON (`freemium_days` > 0), so an update that brings the limit is a fresh free week for everyone.
 *   (The old rule kept existing installs and installs from while the limit was off out of it: see the commented code below.)
 *
 * The gates are plain functions with no preferences at hand, so the state they need ([hasEnded], [coreAfterWindow]) is cached
 * here by [refresh]; call it at launch, after Remote Config is stored, and whenever Home opens.
 */
object FreemiumWindow {
    @Volatile private var phaseCache = Phase.OFF
    @Volatile private var coreCache = 2

    // Debug builds only (`--ei ab_freemium_days 7 --ei ab_freemium_core 0 --ei ab_freemium_day 8`)
    @Volatile var debugDays: Int? = null
    @Volatile var debugCore: Int? = null
    @Volatile var debugDay: Int? = null

    // ---- cached state, read by the gates ----

    val phase: Phase get() = phaseCache
    val hasEnded: Boolean get() = phaseCache == Phase.ENDED
    val coreAfterWindow: Int get() = coreCache

    /** The free tier has run out: a new install whose free window is over and who is not subscribed. */
    fun freeTierExpired(isSubscribed: Boolean): Boolean = hasEnded && !isSubscribed

    /** "Nothing free" mode (`free_core_after_window` = 0): even Free Mode and Level 1 are locked. */
    fun coreLocked(isSubscribed: Boolean): Boolean = freeTierExpired(isSubscribed) && coreCache < 2

    // ---- stored state ----

    fun windowDays(prefs: PreferencesHelper): Int = debugDays ?: prefs.getCustomParamInt(AppConstants.RemoteConfig.freemiumDays, 0)
    fun coreAfterWindow(prefs: PreferencesHelper): Int = debugCore ?: prefs.getCustomParamInt(AppConstants.RemoteConfig.freeCoreAfterWindow, 2)
    // Nobody is grandfathered any more (the owner wants existing users treated as new). Old rule:
    // fun isGrandfathered(prefs: PreferencesHelper): Boolean =
    //     if (debugDay != null) false else prefs.getCustomParamBoolean(AppConstants.Freemium.grandfathered, false)
    fun isGrandfathered(prefs: PreferencesHelper): Boolean = false

    fun startMillis(prefs: PreferencesHelper, now: Long = System.currentTimeMillis()): Long? {
        debugDay?.let { return now - it * FreemiumWindowLogic.DAY_MS - 60_000L }
        return prefs.getCustomParam(AppConstants.Freemium.startAt, "").toLongOrNull()
    }

    fun phase(prefs: PreferencesHelper, now: Long = System.currentTimeMillis()): Phase =
        FreemiumWindowLogic.phase(now, startMillis(prefs, now), windowDays(prefs), isGrandfathered(prefs))

    /** Whole free days left (0 once over); null when this install has no window. */
    fun daysLeft(prefs: PreferencesHelper, now: Long = System.currentTimeMillis()): Int? {
        if (phase(prefs, now) == Phase.OFF) return null
        val start = startMillis(prefs, now) ?: return null
        return FreemiumWindowLogic.daysLeft(now, start, windowDays(prefs))
    }

    /** Re-reads the stored state into the cache the gates use. Cheap; safe to call as often as needed. */
    fun refresh(prefs: PreferencesHelper, now: Long = System.currentTimeMillis()) {
        phaseCache = phase(prefs, now)
        coreCache = coreAfterWindow(prefs)
    }

    // ---- first sight of an install (called right after Remote Config has been stored) ----

    /**
     * Looks at this install: the first call stamps its first-seen time (day 1 of the returning-user prompts), and the window
     * starts at the first call that finds the limit ON, whatever the age of the install. Safe to call on every launch.
     */
    fun registerIfNeeded(prefs: PreferencesHelper, now: Long = System.currentTimeMillis()) {
        if (!prefs.getCustomParamBoolean(AppConstants.Freemium.registered, false)) {
            prefs.setCustomParamBoolean(AppConstants.Freemium.registered, true)
            prefs.setCustomParam(AppConstants.Freemium.firstSeenAt, now.toString())   // day 1 of the returning-user prompts
        }
        val days = windowDays(prefs)
        if (days > 0 && prefs.getCustomParam(AppConstants.Freemium.startAt, "").isEmpty()) {
            prefs.setCustomParam(AppConstants.Freemium.startAt, now.toString())
            val existing = prefs.getCustomParamInt(AppConstants.Streak.totalActiveDays, 0) > 0 || prefs.isUserLoggedIn()
            AppAnalytics.log(AppAnalytics.FREEMIUM_WINDOW_STARTED, "days" to days, "core" to prefs.getCustomParamInt(AppConstants.RemoteConfig.freeCoreAfterWindow, 2), "existing" to existing)
        }
        refresh(prefs, now)
    }

    // The old rule, kept for reference: an existing user, or an install from while the limit was off, was grandfathered.
    // fun registerIfNeeded(prefs: PreferencesHelper, now: Long = System.currentTimeMillis()) {
    //     if (!prefs.getCustomParamBoolean(AppConstants.Freemium.registered, false)) {
    //         prefs.setCustomParamBoolean(AppConstants.Freemium.registered, true)
    //         prefs.setCustomParam(AppConstants.Freemium.firstSeenAt, now.toString())
    //         val days = prefs.getCustomParamInt(AppConstants.RemoteConfig.freemiumDays, 0)
    //         val existing = prefs.getCustomParamInt(AppConstants.Streak.totalActiveDays, 0) > 0 || prefs.isUserLoggedIn()
    //         when {
    //             existing -> { prefs.setCustomParamBoolean(AppConstants.Freemium.grandfathered, true); AppAnalytics.log(AppAnalytics.FREEMIUM_GRANDFATHERED, "reason" to "existing") }
    //             days <= 0 -> { prefs.setCustomParamBoolean(AppConstants.Freemium.grandfathered, true); AppAnalytics.log(AppAnalytics.FREEMIUM_GRANDFATHERED, "reason" to "limit_off") }
    //             else -> { prefs.setCustomParam(AppConstants.Freemium.startAt, now.toString()); AppAnalytics.log(AppAnalytics.FREEMIUM_WINDOW_STARTED, "days" to days, "core" to prefs.getCustomParamInt(AppConstants.RemoteConfig.freeCoreAfterWindow, 2)) }
    //         }
    //     }
    //     refresh(prefs, now)
    // }

    /** Logs the end of the window once, and tags the user for the A/B comparison. Call when Home opens. */
    fun noteStateForAnalytics(prefs: PreferencesHelper) {
        val p = phase(prefs)
        AppAnalytics.setUserProperty("freemium_phase", p.name.lowercase())
        val days = windowDays(prefs)
        if (days > 0) AppAnalytics.setUserProperty("freemium_group", "d${days}_c${coreAfterWindow(prefs)}")
        if (p == Phase.ENDED && !prefs.getCustomParamBoolean(AppConstants.Freemium.endedLogged, false)) {
            prefs.setCustomParamBoolean(AppConstants.Freemium.endedLogged, true)
            AppAnalytics.log(AppAnalytics.FREEMIUM_WINDOW_ENDED, "days" to days, "core" to coreAfterWindow(prefs))
        }
    }

    // ---- parent reminder at app open (day 3 and day 7) ----

    /**
     * The reminder to show now, if one is due and the player is not subscribed. Stamps it as shown straight away (with the
     * earlier ones), so it can only appear once.
     */
    fun takeDueReminderDay(prefs: PreferencesHelper, isSubscribed: Boolean, now: Long = System.currentTimeMillis()): Int? {
        if (isSubscribed || phase(prefs, now) != Phase.ACTIVE) return null
        val start = startMillis(prefs, now) ?: return null
        val debug = debugDay != null   // a debug day override shows the reminder again on every launch
        val shown = if (debug) 0 else prefs.getCustomParamInt(AppConstants.Freemium.remindersShown, 0)
        val day = FreemiumWindowLogic.dueReminderDay(now, start, windowDays(prefs), shown) ?: return null
        if (!debug) {
            prefs.setCustomParamInt(AppConstants.Freemium.remindersShown, shown or FreemiumWindowLogic.maskShown(day))
            // one prompt a day, and the returning-user schedule must not repeat what this reminder just said
            prefs.setCustomParam(AppConstants.Freemium.lastPromptDay, ReturningPaywallLogic.dayString(now))
            val schedule = ReturningPaywallLogic.schedule(prefs.getCustomParam(AppConstants.RemoteConfig.returningPaywallDays, ""))
            val seen = ReturningPaywallLogic.decode(prefs.getCustomParam(AppConstants.Freemium.returningShown, ""))
            prefs.setCustomParam(AppConstants.Freemium.returningShown, ReturningPaywallLogic.encode(ReturningPaywallLogic.markShown(day, schedule, seen)))
        }
        return day
    }

    // ---- returning-user paywall (a parent prompt at app open, on the Remote Config schedule) ----

    // Debug: `--ei ab_returning_day N` behaves like an install first seen N days ago, and shows on every launch
    @Volatile var debugReturningDay: Int? = null
    // Debug: `--ez ab_billing_issue true` shows the payment-problem prompt on every launch
    @Volatile var debugBillingIssue: Boolean = false

    private fun firstSeenMillis(prefs: PreferencesHelper, now: Long): Long {
        debugReturningDay?.let { return now - it * FreemiumWindowLogic.DAY_MS - 60_000L }
        val stored = prefs.getCustomParam(AppConstants.Freemium.firstSeenAt, "").toLongOrNull()
        if (stored != null) return stored
        prefs.setCustomParam(AppConstants.Freemium.firstSeenAt, now.toString())   // registered before this key existed: counts from now
        return now
    }

    /**
     * The returning-user prompt to show now, if one is due: never for a subscriber, at most one prompt a day (the day 3 / day 7
     * reminders of a free week count too) and never on a day a paywall sheet was already shown. Stamps it as shown straight away.
     */
    fun takeReturningPrompt(prefs: PreferencesHelper, isSubscribed: Boolean, now: Long = System.currentTimeMillis()): Int? {
        if (isSubscribed) return null
        val first = firstSeenMillis(prefs, now)
        val schedule = ReturningPaywallLogic.schedule(prefs.getCustomParam(AppConstants.RemoteConfig.returningPaywallDays, ""))
        val today = ReturningPaywallLogic.dayString(now)
        val debugging = debugReturningDay != null
        val shown = if (debugging) emptySet() else ReturningPaywallLogic.decode(prefs.getCustomParam(AppConstants.Freemium.returningShown, ""))
        val day = ReturningPaywallLogic.dueDay(
            now, first, schedule, shown,
            lastPromptDay = if (debugging) null else prefs.getCustomParam(AppConstants.Freemium.lastPromptDay, ""),
            today = today,
            paywallShownToday = !debugging && prefs.getCustomParam(AppConstants.Freemium.lastPaywallDay, "") == today,
        ) ?: return null
        if (!debugging) {
            prefs.setCustomParam(AppConstants.Freemium.returningShown, ReturningPaywallLogic.encode(ReturningPaywallLogic.markShown(day, schedule, shown)))
            prefs.setCustomParam(AppConstants.Freemium.lastPromptDay, today)
        }
        return day
    }

    /** A paywall sheet of any kind was just opened: today is no day for another prompt. */
    fun notePaywallShown(prefs: PreferencesHelper) {
        prefs.setCustomParam(AppConstants.Freemium.lastPaywallDay, ReturningPaywallLogic.dayString(System.currentTimeMillis()))
    }

    // ---- OneSignal tags (for win-back and trial campaigns sent from the OneSignal dashboard) ----

    /**
     * Tags the install with its state only (no personal data), so a campaign can target, for example, "free, first seen more
     * than 30 days ago". OneSignal ignores a tag that has not changed.
     */
    fun syncUserSegments(prefs: PreferencesHelper, isSubscribed: Boolean, now: Long = System.currentTimeMillis()) {
        runCatching {
            val firstSeen = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date(firstSeenMillis(prefs, now)))
            val tags = mapOf(
                "premium" to if (isSubscribed) "1" else "0",
                "freemium_phase" to phase(prefs, now).name.lowercase(),
                "first_seen" to firstSeen,
                "active_days" to prefs.getCustomParamInt(AppConstants.Streak.totalActiveDays, 0).toString(),
            )
            // OneSignal.User waits until OneSignal has finished starting up: off the main thread, so a slow start cannot freeze the screen
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default).launch {
                runCatching { com.onesignal.OneSignal.User.addTags(tags) }
            }
            // The same state as RevenueCat subscriber attributes, to split its charts and Experiments results by group
            val days = windowDays(prefs)
            com.revenuecat.purchases.Purchases.sharedInstance.setAttributes(
                mapOf(
                    "freemium_phase" to phase(prefs, now).name.lowercase(),
                    "freemium_group" to if (days > 0) "d${days}_c${coreAfterWindow(prefs)}" else "off",
                    "first_seen" to firstSeen,
                )
            )
        }
    }

    /** Debug builds only (`--ez ab_freemium_notify_now true`): fires the day-5 notification right away, to check the whole path. */
    fun debugNotifyNow(context: Context) {
        val request = OneTimeWorkRequestBuilder<FreemiumNotificationWorker>()
            .setInputData(workDataOf(FreemiumNotificationWorker.KEY_DAY to 5, FreemiumNotificationWorker.KEY_LEFT to 2))
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork("freemium_debug_now", ExistingWorkPolicy.REPLACE, request)
    }

    // ---- price variant (RevenueCat Experiments) ----

    /** Logs which offering and which yearly price this user is looking at, once per paywall opening. */
    fun notePriceVariant(offering: String, yearlySku: String?, yearlyPrice: Double?, currency: String?) {
        if (offering.isEmpty()) return
        AppAnalytics.setUserProperty("price_variant", offering.take(36))
        val params = mutableListOf<Pair<String, Any>>("offering" to offering)
        if (yearlySku != null) params += "yearly_product" to yearlySku
        if (yearlyPrice != null) params += "yearly_price" to yearlyPrice
        if (!currency.isNullOrEmpty()) params += "currency" to currency
        AppAnalytics.log(AppAnalytics.PAYWALL_PLANS_LOADED, *params.toTypedArray())
    }

    // ---- local notifications (day 5 and day 7, scheduled ahead of time) ----

    private fun workName(day: Int) = "freemium_day$day"

    /**
     * Re-syncs the two pending notifications with the current state: day 5 and day 7 are (re)scheduled while this install has
     * an active window and is not subscribed, and cancelled otherwise. The worker checks again when it fires.
     */
    fun syncNotifications(context: Context, prefs: PreferencesHelper, isSubscribed: Boolean, now: Long = System.currentTimeMillis()) {
        val wm = WorkManager.getInstance(context)
        val start = startMillis(prefs, now)
        val days = windowDays(prefs)
        val schedule = if (start != null && !isSubscribed && phase(prefs, now) == Phase.ACTIVE) {
            FreemiumWindowLogic.notificationSchedule(now, start, days)
        } else emptyList()
        for (day in FreemiumWindowLogic.notificationDays) {
            val item = schedule.firstOrNull { it.first == day }
            if (item == null) {
                wm.cancelUniqueWork(workName(day))
            } else {
                val leftFull = maxOf(0L, (FreemiumWindowLogic.endMillis(start!!, days) - item.second) / FreemiumWindowLogic.DAY_MS).toInt()
                val request = OneTimeWorkRequestBuilder<FreemiumNotificationWorker>()
                    .setInitialDelay(item.second - now, TimeUnit.MILLISECONDS)
                    .setInputData(workDataOf(FreemiumNotificationWorker.KEY_DAY to day, FreemiumNotificationWorker.KEY_LEFT to leftFull))
                    .addTag("freemium_notification")
                    .build()
                wm.enqueueUniqueWork(workName(day), ExistingWorkPolicy.REPLACE, request)
            }
        }
        if (com.jigar.me.BuildConfig.DEBUG) {
            android.util.Log.d("Freemium", "notifications now: " + schedule.joinToString { "day${it.first}@${java.util.Date(it.second)}" })
        }
    }
}
