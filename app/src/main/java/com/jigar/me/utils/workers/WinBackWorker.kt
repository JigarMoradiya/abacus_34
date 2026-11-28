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
import com.jigar.me.utils.AppAnalytics
import com.jigar.me.utils.Constants
import com.jigar.me.utils.StreakManager
import com.jigar.me.utils.WinBackLogic
import com.jigar.me.utils.WinBackNotifications
import com.jigar.me.utils.fcm.NotificationRouter
import com.jigar.me.utils.inAppLanguage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/** The "come back" notification of day 7 or day 14 after the last app open (rules in [WinBackLogic]). */
@HiltWorker
class WinBackWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted params: WorkerParameters,
    private val prefs: AppPreferencesHelper,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val day = inputData.getInt(KEY_DAY, 7)
        val postpones = inputData.getInt(KEY_POSTPONES, 0)
        // opened since it was queued: that open queued a new one, nothing to do here
        if (!WinBackLogic.winBackDue(WinBackNotifications.lastOpenDate(prefs), StreakManager.today(), day)) return Result.success()
        // the app is on screen right now (a phone that holds background work back runs this only when the app opens): it was just opened, and
        // that open queues a new one
        if (isAppInForeground()) return Result.success()
        // one notification of the app a day: another one was already shown today, so try again tomorrow
        if (WinBackNotifications.shownToday(prefs) && postpones < WinBackLogic.MAX_POSTPONES) {
            WinBackNotifications.postpone(appContext, day, postpones)
            return Result.success()
        }
        show(day)
        WinBackNotifications.markShown(prefs)
        AppAnalytics.log(AppAnalytics.WINBACK_SHOWN, "day" to day)
        if (day == 7) WinBackNotifications.scheduleDay14(appContext, prefs)
        return Result.success()
    }

    private fun isAppInForeground(): Boolean {
        val info = android.app.ActivityManager.RunningAppProcessInfo()
        android.app.ActivityManager.getMyMemoryState(info)
        return info.importance <= android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
    }

    private fun show(day: Int) {
        val manager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val text = appContext.inAppLanguage()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // the streak reminder's channel: a parent who turned the daily reminder off does not get these either
            val channel = NotificationChannel(CHANNEL_ID, text.getString(R.string.notif_channel_streak_name), NotificationManager.IMPORTANCE_DEFAULT)
                .apply { description = text.getString(R.string.notif_channel_streak_desc) }
            manager.createNotificationChannel(channel)
        }
        // opens Home, through the same path as a push of type "home"; `winback` lets Home count the open
        val intent = Intent(appContext, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(NotificationRouter.EXTRA_FROM_NOTIFICATION, true)
            putExtra("type", Constants.notificationTypeHome)
            putExtra(EXTRA_WINBACK_DAY, day.toString())
        }
        val pendingIntent = PendingIntent.getActivity(appContext, REQUEST_CODE + day, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val (titleRes, bodyRes) = if (day >= 14) R.string.winback_14_title to R.string.winback_14_body else R.string.winback_7_title to R.string.winback_7_body
        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_small)
            .setContentTitle(text.getString(titleRes))
            .setContentText(text.getString(bodyRes))
            .setStyle(NotificationCompat.BigTextStyle().bigText(text.getString(bodyRes)))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        manager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val KEY_DAY = "day"
        const val KEY_POSTPONES = "postpones"
        const val EXTRA_WINBACK_DAY = "winback"
        private const val CHANNEL_ID = "streak_reminder"
        private const val NOTIFICATION_ID = 9003
        private const val REQUEST_CODE = 9300
    }
}
