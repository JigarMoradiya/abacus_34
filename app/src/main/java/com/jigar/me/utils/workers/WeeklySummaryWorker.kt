package com.jigar.me.utils.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jigar.me.R
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.home.HomeActivity
import com.jigar.me.utils.WeeklySummaryManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import com.jigar.me.utils.inAppLanguage

@HiltWorker
class WeeklySummaryWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted params: WorkerParameters,
    private val prefs: AppPreferencesHelper,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        showNotification()
        // Always reschedule for the following week
        WeeklySummaryManager.scheduleNotification(appContext)
        return Result.success()
    }

    private fun showNotification() {
        val manager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                appContext.inAppLanguage().getString(R.string.notif_channel_weekly_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = appContext.inAppLanguage().getString(R.string.notif_channel_weekly_desc) }
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(appContext, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            appContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val (title, message) = WeeklySummaryManager.notificationContent(appContext, prefs)

        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_small)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID      = "weekly_summary"
        private const val NOTIFICATION_ID = 9002
    }
}
