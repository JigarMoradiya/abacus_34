package com.jigar.me.utils

import com.jigar.me.data.pref.AppPreferencesHelper
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.awaitRestore

/**
 * The restore at app launch: once per RevenueCat customer (user id) on this install, not at every launch.
 *
 * It exists to find purchases of this Google account that RevenueCat does not know for this customer yet (a reinstall gives a new anonymous
 * id, and an old lifetime purchase from before RevenueCat). It is marked done ONLY after a restore that finished: if it fails (offline, Play
 * not ready) nothing is saved and the next launch tries again, so a paying customer is never left locked out by one bad launch. A new user id
 * (a login, a logout) is a new customer and is restored once too. The Restore buttons and the restore after a login are not touched.
 */
object PurchaseRestore {
    const val KEY = "purchase_restored_for_user"

    /** Restore unless this user id has already been restored successfully. */
    fun needsRestore(restoredFor: String, userId: String): Boolean = restoredFor != userId

    /** Debug builds only (`--ez ab_restore_fail true`): the restore fails, to see that nothing is marked done and the next launch retries. */
    @Volatile internal var debugFail = false

    /** Call at launch (the customer is read fresh afterwards by the caller). Never throws. */
    suspend fun runAtLaunch(prefs: AppPreferencesHelper) {
        val userId = try { Purchases.sharedInstance.appUserID } catch (_: Exception) { return }
        val restoredFor = prefs.getCustomParam(KEY, "")
        if (!needsRestore(restoredFor, userId)) return
        try {
            if (debugFail) throw IllegalStateException("debug: restore failed")
            val info = Purchases.sharedInstance.awaitRestore()
            prefs.setCustomParam(KEY, userId)   // only now: the restore finished
            RevenueCatHelper.update(info)
            AppAnalytics.log(AppAnalytics.RESTORE_LAUNCH, "result" to "ok", "first" to if (restoredFor.isEmpty()) 1 else 0, "premium" to if (CommonUtils.checkPurchaseForExerciseExamCCM(prefs)) 1 else 0)
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e   // the screen went away: not a failed restore (nothing is saved, the next launch tries again)
        } catch (e: Exception) {
            AppAnalytics.log(AppAnalytics.RESTORE_LAUNCH, "result" to "failed", "error" to (e.message ?: e.javaClass.simpleName).take(80))
        }
    }
}
