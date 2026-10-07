package com.jigar.me.utils.fcm

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jigar.me.R
import com.jigar.me.ui.view.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var fcmTokenManager: FcmTokenManager

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        fcmTokenManager.onToken(token)
    }

    // Only invoked while the app is in foreground (or for data-only messages); background
    // notification messages are shown by the system and delivered to HomeActivity as intent extras.
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title ?: message.data["title"]
        val body = message.notification?.body ?: message.data["body"]
        if (title.isNullOrEmpty() && body.isNullOrEmpty()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return

        createChannel(this)
        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(NotificationRouter.EXTRA_FROM_NOTIFICATION, true)
            message.data.forEach { (k, v) -> putExtra(k, v) }
        }
        val id = (message.messageId ?: System.nanoTime().toString()).hashCode()
        val pendingIntent = PendingIntent.getActivity(
            this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
            .setSmallIcon(R.drawable.logo_small)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        try {
            NotificationManagerCompat.from(this).notify(id, notification)
        } catch (_: SecurityException) {
        }
    }

    companion object {
        fun createChannel(context: Context) {
            val channel = NotificationChannelCompat.Builder(
                context.getString(R.string.default_notification_channel_id),
                NotificationManagerCompat.IMPORTANCE_HIGH
            ).setName(context.getString(R.string.app_name)).build()
            NotificationManagerCompat.from(context).createNotificationChannel(channel)
        }
    }
}
