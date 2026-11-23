package com.jigar.me.utils

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.jigar.me.data.pref.PreferencesHelper
import com.jigar.me.utils.OfferNotificationLogic.Kind
import com.jigar.me.utils.workers.OfferNotificationWorker
import java.util.concurrent.TimeUnit

/**
 * The local notifications of a running Home offer (Remote Config `home_offer`): for a phone that owns no plan and has none assigned
 * by the admin. WorkManager fires them (see [OfferNotificationWorker]); when they fire is [OfferNotificationLogic]. Mirrors iOS
 * OfferNotifications.
 */
object OfferNotifications {
    private const val TAG = "offer_notification"
    const val KEY_KIND = "kind"
    const val KEY_PERCENT = "percent"

    private fun workName(kind: Kind) = "offer_${kind.name.lowercase()}"

    /**
     * Re-syncs the pending notifications with the current state: they are (re)scheduled while the offer runs and [eligible] (the user
     * owns nothing and has no assigned plan), and cancelled otherwise. Safe to call as often as needed: the same offer always gives the
     * same fire times. [discountPercent] is the % off worked out from the store prices (0 = say "special price" instead).
     */
    fun sync(context: Context, prefs: PreferencesHelper, eligible: Boolean, discountPercent: Int, now: Long = System.currentTimeMillis()) {
        val wm = WorkManager.getInstance(context)
        val reminders = if (eligible && HomeOfferManager.isTimedOfferActive(prefs, now)) {
            OfferNotificationLogic.schedule(now, HomeOfferManager.startedAt(prefs) ?: now, HomeOfferManager.endMillis(prefs, now))
        } else emptyList()
        for (kind in Kind.values()) {
            val item = reminders.firstOrNull { it.kind == kind }
            if (item == null) {
                wm.cancelUniqueWork(workName(kind))
            } else {
                val request = OneTimeWorkRequestBuilder<OfferNotificationWorker>()
                    .setInitialDelay(item.fireAtMillis - now, TimeUnit.MILLISECONDS)
                    .setInputData(workDataOf(KEY_KIND to kind.name, KEY_PERCENT to discountPercent))
                    .addTag(TAG)
                    .build()
                wm.enqueueUniqueWork(workName(kind), ExistingWorkPolicy.REPLACE, request)
            }
        }
        if (com.jigar.me.BuildConfig.DEBUG) {
            android.util.Log.d("OfferNotif", "now: " + reminders.joinToString { "${it.kind}@${java.util.Date(it.fireAtMillis)}" })
        }
    }

    /** Debug builds only (`--es ab_offer_notify_now ENDS_3H`): fires that notification right away (25% off), to check the whole path. */
    fun debugNotifyNow(context: Context, kind: String) {
        val request = OneTimeWorkRequestBuilder<OfferNotificationWorker>()
            .setInputData(workDataOf(KEY_KIND to kind, KEY_PERCENT to 25))
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork("offer_debug_now", ExistingWorkPolicy.REPLACE, request)
    }

    /** Nothing to say any more (the offer is over or off, or the user got a plan). */
    fun cancelAll(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(TAG)
    }
}
