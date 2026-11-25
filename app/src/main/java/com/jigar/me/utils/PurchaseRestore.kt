package com.jigar.me.utils

import com.jigar.me.data.pref.AppPreferencesHelper
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.awaitRestore

/**
 * The restore at app launch. Two cases, both quiet:
 *
 * 1. Once per RevenueCat customer (user id) on this install, not at every launch. It exists to find purchases of this Google account that
 *    RevenueCat does not know for this customer yet (a reinstall gives a new anonymous id, and an old lifetime purchase from before
 *    RevenueCat). It is marked done ONLY after a restore that finished: if it fails (offline, Play not ready) nothing is saved and the next
 *    launch tries again, so a paying customer is never left locked out by one bad launch. A new user id (a login, a logout) is a new customer
 *    and is restored once too.
 * 2. A phone that owns nothing looks again once a week, because a plan bought on ANOTHER device with the same Google account (a family
 *    tablet) would otherwise only show up after a tap on Restore. A phone that already has Premium never does this. The week is counted from
 *    the last TRY, not the last success: a phone that cannot restore is asked once a week, not at every launch.
 *
 * The Restore buttons and the restore after a login are not touched.
 */
object PurchaseRestore {
    const val KEY = "purchase_restored_for_user"
    /** Milliseconds of the last restore try (the first one or a weekly one), as text. */
    const val CHECKED_KEY = "purchase_restore_checked_at"
    const val WEEK_MILLIS = 7L * 24 * 60 * 60 * 1000

    /** Restore unless this user id has already been restored successfully. */
    fun needsRestore(restoredFor: String, userId: String): Boolean = restoredFor != userId

    /**
     * A phone without Premium looks again once a week. Due when it was never tried, when a week has passed, or when the clock went back
     * (a saved time in the future would otherwise block the check until the clock catches up).
     */
    fun needsWeeklyCheck(checkedAt: Long, now: Long, hasPremium: Boolean): Boolean =
        !hasPremium && (checkedAt <= 0 || checkedAt > now || now - checkedAt >= WEEK_MILLIS)

    /** Debug builds only (`--ez ab_restore_fail true`): the restore fails, to see that nothing is marked done and the next launch retries. */
    @Volatile internal var debugFail = false

    /** Call at launch (the customer is read fresh afterwards by the caller). Never throws, except to pass on a cancelled coroutine. */
    suspend fun runAtLaunch(prefs: AppPreferencesHelper) {
        val userId = try { Purchases.sharedInstance.appUserID } catch (_: Exception) { return }
        val restoredFor = prefs.getCustomParam(KEY, "")
        val now = System.currentTimeMillis()
        val first = needsRestore(restoredFor, userId)
        val weekly = !first && needsWeeklyCheck(prefs.getCustomParam(CHECKED_KEY, "").toLongOrNull() ?: 0L, now, CommonUtils.checkPurchaseForExerciseExamCCM(prefs))
        if (!first && !weekly) return
        // a weekly look is counted when it is tried, not when it works (see above); the first restore is counted only when it works
        if (weekly) prefs.setCustomParam(CHECKED_KEY, now.toString())
        try {
            if (debugFail) throw IllegalStateException("debug: restore failed")
            val info = Purchases.sharedInstance.awaitRestore()
            if (first) {
                prefs.setCustomParam(KEY, userId)   // only now: the restore finished
                prefs.setCustomParam(CHECKED_KEY, now.toString())   // the weekly look counts from here
            }
            RevenueCatHelper.update(info)
            AppAnalytics.log(AppAnalytics.RESTORE_LAUNCH, "result" to "ok", "first" to if (restoredFor.isEmpty()) 1 else 0, "weekly" to if (weekly) 1 else 0, "premium" to if (CommonUtils.checkPurchaseForExerciseExamCCM(prefs)) 1 else 0)
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e   // the screen went away: not a failed restore (the first restore is not saved, the next launch tries again)
        } catch (e: Exception) {
            AppAnalytics.log(AppAnalytics.RESTORE_LAUNCH, "result" to "failed", "weekly" to if (weekly) 1 else 0, "error" to (e.message ?: e.javaClass.simpleName).take(80))
        }
    }
}
