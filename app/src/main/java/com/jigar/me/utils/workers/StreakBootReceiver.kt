package com.jigar.me.utils.workers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jigar.me.utils.StreakManager

class StreakBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            StreakManager.scheduleNotification(context)
        }
    }
}
