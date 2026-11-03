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
import com.jigar.me.utils.FreemiumWindow
import com.jigar.me.utils.FreemiumWindowLogic
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import com.jigar.me.utils.inAppLanguage

/** The day-5 / day-7 notification of a new install's free week. Tapping it opens the Purchase screen. */
@HiltWorker
class FreemiumNotificationWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted params: WorkerParameters,
    private val prefs: AppPreferencesHelper,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        // Check again at the moment it fires: subscribed, or no free window any more = nothing to say
        val subscribed = CommonUtils.checkPurchaseForExerciseExamCCM(prefs)
        if (subscribed || FreemiumWindow.phase(prefs) != FreemiumWindowLogic.Phase.ACTIVE) return Result.success()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return Result.success()

        val day = inputData.getInt(KEY_DAY, 5)
        val left = inputData.getInt(KEY_LEFT, 0)
        val text = appContext.inAppLanguage()
        val title = if (left >= 1) text.resources.getQuantityString(R.plurals.notif_free_days_left, left, left)
                    else text.getString(R.string.notif_free_last_day)
        show(day, title, text.getString(R.string.notif_free_body))
        return Result.success()
    }

    private fun show(day: Int, title: String, text: String) {
        val manager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, appContext.inAppLanguage().getString(R.string.notif_channel_free_name), NotificationManager.IMPORTANCE_DEFAULT)
                .apply { description = appContext.inAppLanguage().getString(R.string.notif_channel_free_desc) }
            manager.createNotificationChannel(channel)
        }
        val intent = Intent(appContext, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(HomeActivity.EXTRA_DEEP_LINK_ROUTE, RouteNavigation.Purchase.route)
        }
        // a request code of its own: the streak and weekly intents use 0 and would share (and overwrite) this one's extras
        val pending = PendingIntent.getActivity(
            appContext, REQUEST_CODE_BASE + day, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_small)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        manager.notify(NOTIFICATION_ID_BASE + day, notification)
    }

    companion object {
        const val KEY_DAY = "day"
        const val KEY_LEFT = "left"
        private const val CHANNEL_ID = "freemium_reminder"
        private const val NOTIFICATION_ID_BASE = 9100
        private const val REQUEST_CODE_BASE = 9100
    }
}
