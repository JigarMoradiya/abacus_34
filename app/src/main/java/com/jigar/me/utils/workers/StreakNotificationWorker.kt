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
import com.jigar.me.utils.StreakManager
import com.jigar.me.utils.WinBackLogic
import com.jigar.me.utils.WinBackNotifications
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import com.jigar.me.utils.inAppLanguage

@HiltWorker
class StreakNotificationWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted params: WorkerParameters,
    private val prefs: AppPreferencesHelper,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val data = StreakManager.read(prefs)

        // only on the day of the last app open and the 2 days after it: then the come-back notifications take over (no daily reminder for a child
        // who stopped). The next app open schedules this reminder again.
        if (!StreakManager.TEST_NOTIFICATION && !StreakManager.debugShowNow &&
            !WinBackLogic.streakReminderAllowed(WinBackNotifications.lastOpenDate(prefs), StreakManager.today())) return Result.success()

        // In test mode always show; in production only if today has not counted for the streak yet (the child has not solved 5 problems today)
        // never while the app is on screen (a phone that holds background work back runs this only when the app is opened)
        val onScreen = android.app.ActivityManager.RunningAppProcessInfo().also { android.app.ActivityManager.getMyMemoryState(it) }.importance <=
            android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
        if (StreakManager.TEST_NOTIFICATION || StreakManager.debugShowNow || (data.lastActivityDate != StreakManager.today() && !onScreen)) {
            showNotification(StreakManager.displayStreak(prefs).coerceAtLeast(1))
            WinBackNotifications.markShown(prefs)
        }

        // Always reschedule for the next day
        StreakManager.scheduleNotification(appContext)
        return Result.success()
    }

    private fun showNotification(currentStreak: Int) {
        val manager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                appContext.inAppLanguage().getString(R.string.notif_channel_streak_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = appContext.inAppLanguage().getString(R.string.notif_channel_streak_desc) }
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(appContext, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            appContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val (title, message) = StreakManager.notificationContent(appContext, currentStreak)

        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_small)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID       = "streak_reminder"
        private const val NOTIFICATION_ID  = 9001
    }
}
