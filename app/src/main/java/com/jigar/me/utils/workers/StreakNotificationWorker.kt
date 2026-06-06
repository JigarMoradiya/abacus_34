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
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class StreakNotificationWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted params: WorkerParameters,
    private val prefs: AppPreferencesHelper,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val data = StreakManager.read(prefs)

        // In test mode always show; in production only if user hasn't opened the app today
        if (StreakManager.TEST_NOTIFICATION || data.lastActivityDate != StreakManager.today()) {
            showNotification(data.currentStreak)
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
                "Daily Streak Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Reminds you to keep your learning streak going" }
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(appContext, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            appContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val (title, message) = if (currentStreak > 0) {
            "Keep your $currentStreak-day streak going! 🔥" to
            "Open the app and practice today to maintain your streak."
        } else {
            "Start your learning streak today! 🔥" to
            "Open Vedaavi Abacus and begin your daily practice."
        }

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
