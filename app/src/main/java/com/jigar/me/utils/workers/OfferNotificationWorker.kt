package com.jigar.me.utils.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jigar.me.R
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.home.HomeActivity
import com.jigar.me.ui.view.home.navigation.RouteNavigation
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.HomeOfferManager
import com.jigar.me.utils.LanguageManager
import com.jigar.me.utils.OfferNotificationLogic.Kind
import com.jigar.me.utils.OfferNotifications
import com.jigar.me.utils.inAppLanguage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/** A local notification of a running Home offer (see [OfferNotifications]). Tapping it opens the Purchase screen. */
@HiltWorker
class OfferNotificationWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted params: WorkerParameters,
    private val prefs: AppPreferencesHelper,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        // Check again at the moment it fires: bought a plan or got one from the admin, or the offer is over = nothing to say
        if (CommonUtils.checkPurchaseForExerciseExamCCM(prefs) || !HomeOfferManager.isTimedOfferActive(prefs)) return Result.success()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return Result.success()

        val kind = runCatching { Kind.valueOf(inputData.getString(OfferNotifications.KEY_KIND).orEmpty()) }.getOrDefault(Kind.NUDGE_1)
        val percent = inputData.getInt(OfferNotifications.KEY_PERCENT, 0)
        val text = appContext.inAppLanguage()
        val cfg = HomeOfferManager.config(prefs)

        // the title is the Home card's: the Remote Config name in English, a translated static title in every other language
        val title = cfg.name?.takeIf { it.isNotBlank() && LanguageManager.currentTag() == "en" }
            ?: text.getString(if (cfg.isBiggestCampaign) R.string.home_offer_title_biggest else R.string.home_offer_title)
        val save = if (percent > 0) text.getString(R.string.home_offer_save_pct, percent) else text.getString(R.string.home_offer_special_price)
        val body = if (kind.isEndsSoon) text.getString(R.string.notif_offer_ends_soon, save) else save
        show(kind, title, body)
        return Result.success()
    }

    private fun show(kind: Kind, title: String, body: String) {
        val manager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, appContext.inAppLanguage().getString(R.string.notif_channel_offer_name), NotificationManager.IMPORTANCE_DEFAULT)
                .apply { description = appContext.inAppLanguage().getString(R.string.notif_channel_offer_desc) }
            manager.createNotificationChannel(channel)
        }
        val intent = Intent(appContext, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(HomeActivity.EXTRA_DEEP_LINK_ROUTE, RouteNavigation.Purchase.route)
        }
        // request codes of their own: the streak (0), weekly (0) and free-week (9100 + day) intents would share and overwrite these extras
        val pending = PendingIntent.getActivity(
            appContext, BASE_ID + kind.ordinal, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_small)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        manager.notify(BASE_ID + kind.ordinal, notification)
    }

    companion object {
        private const val CHANNEL_ID = "offer_reminder"
        private const val BASE_ID = 9200
    }
}
