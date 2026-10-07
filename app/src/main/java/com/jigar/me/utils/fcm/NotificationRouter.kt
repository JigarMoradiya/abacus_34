package com.jigar.me.utils.fcm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.gson.Gson
import com.jigar.me.MyApplication
import com.jigar.me.data.model.NotificationData
import com.jigar.me.ui.view.home.HomeActivity
import com.jigar.me.ui.view.home.navigation.RouteNavigation
import com.jigar.me.utils.Constants
import com.jigar.me.utils.extensions.openURL
import com.jigar.me.utils.extensions.openYoutube
import com.jigar.me.utils.extensions.shareIntent

/** Maps a push notification's data (type + custom key/values) to an in-app destination or action. */
object NotificationRouter {
    // Set on the PendingIntent of notifications we show ourselves (foreground)
    const val EXTRA_FROM_NOTIFICATION = "abacus_notification_tap"

    /** True when the intent was delivered by tapping a push notification (system-shown or ours). */
    fun isNotificationIntent(intent: Intent?): Boolean {
        val extras = intent?.extras ?: return false
        return extras.containsKey(EXTRA_FROM_NOTIFICATION) || extras.containsKey("google.message_id")
    }

    /** Extracts the custom data keys, dropping FCM/system extras. */
    fun dataFromExtras(extras: Bundle?): Map<String, String> {
        val map = HashMap<String, String>()
        extras?.keySet()?.forEach { key ->
            if (key.startsWith("google.") || key.startsWith("gcm.") || key == "from" ||
                key == "collapse_key" || key == EXTRA_FROM_NOTIFICATION
            ) return@forEach
            extras.get(key)?.let { map[key] = it.toString() }
        }
        return map
    }

    fun clearNotificationExtras(intent: Intent) {
        intent.extras?.keySet()?.toList()?.forEach { key ->
            if (key.startsWith("google.") || key.startsWith("gcm.") || key == EXTRA_FROM_NOTIFICATION) {
                intent.removeExtra(key)
            }
        }
    }

    /**
     * [openRoute] receives in-app destinations; by default they start HomeActivity with the route.
     */
    fun handle(
        data: Map<String, String>,
        context: Context = MyApplication.getInstance(),
        openRoute: (String) -> Unit = { HomeActivity.getInstance(context, it) },
    ) {
        val notification = if (data.isEmpty()) null else
            Gson().fromJson(Gson().toJsonTree(data), NotificationData::class.java)
        if (notification == null) {
            openRoute(RouteNavigation.Home.route)
            return
        }
        when (notification.type) {
            Constants.notificationTypeStarter -> openRoute(RouteNavigation.AbacusFreeMode.route)
            Constants.notificationTypeExercise -> openRoute(RouteNavigation.Exercise.route)
            Constants.notificationTypeCCM -> openRoute(RouteNavigation.CCMHome.route)
            Constants.notificationTypeExam -> openRoute(RouteNavigation.ExamHome.route)
            Constants.notificationTypeNumberSequence -> openRoute(RouteNavigation.NumberSequencePuzzleHome.route)
            Constants.notificationTypeSetting -> openRoute(RouteNavigation.Settings.route)
            Constants.notificationTypePurchase -> openRoute(RouteNavigation.Purchase.route)
            Constants.notificationTypeMathGame -> openRoute(RouteNavigation.MathGameZone.route)
            Constants.notificationTypeCrossMath -> openRoute(RouteNavigation.CrossMathHome.route)
            Constants.notificationTypeNumberPath -> openRoute(RouteNavigation.NumberPathHome.route)
            Constants.notificationTypeTrueFalse -> openRoute(RouteNavigation.TrueFalseHome.route)
            Constants.notificationTypePlaceValue -> openRoute(RouteNavigation.PlaceValueHome.route)
            Constants.notificationTypeClockMaster -> openRoute(RouteNavigation.ClockMasterHome.route)
            Constants.notificationTypeMathBingo -> openRoute(RouteNavigation.MathBingoHome.route)
            Constants.notificationTypeKakuro -> openRoute(RouteNavigation.KakuroHome.route)
            Constants.notificationTypeSudoku -> openRoute(RouteNavigation.SudokuHome.route)
            Constants.notificationTypeMathPyramid -> openRoute(RouteNavigation.MathPyramidHome.route)
            Constants.notificationTypeTargetNumber -> openRoute(RouteNavigation.TargetNumberHome.route)
            Constants.notificationTypeBalloonPop -> openRoute(RouteNavigation.BalloonPopHome.route)
            Constants.notificationTypeSpeedCompare -> openRoute(RouteNavigation.SpeedCompareHome.route)
            Constants.notificationTypeMissingOperator -> openRoute(RouteNavigation.MissingOperatorHome.route)
            Constants.notificationTypeMagicSquare -> openRoute(RouteNavigation.MagicSquareHome.route)
            Constants.notificationTypeCalcudoku -> openRoute(RouteNavigation.CalcudokuHome.route)
            Constants.notificationTypeMerge2048 -> openRoute(RouteNavigation.Merge2048Home.route)
            Constants.notificationTypeEquationMatch -> openRoute(RouteNavigation.EquationMatchHome.route)
            Constants.notificationTypePractice -> openRoute(RouteNavigation.LevelCategory.levelCategory("1"))
            Constants.notificationTypeLevel1 -> openRoute(RouteNavigation.Level1Home.route)
            Constants.notificationTypeLevel2 -> openRoute(RouteNavigation.Level2Home.route)
            Constants.notificationTypeLevel3 -> openRoute(RouteNavigation.Level3Home.route)
            Constants.notificationTypeLevel4 -> openRoute(RouteNavigation.Level4TablePicker.route)
            Constants.notificationTypeYoutubeHome -> context.openYoutube()
            Constants.notificationTypeYoutube -> context.openYoutube(notification.youtube_url)
            Constants.notificationTypeRate ->
                context.openURL("https://play.google.com/store/apps/details?id=${context.packageName}")
            Constants.notificationTypeShare -> context.shareIntent()
            else -> openRoute(RouteNavigation.Home.route)
        }
    }
}
