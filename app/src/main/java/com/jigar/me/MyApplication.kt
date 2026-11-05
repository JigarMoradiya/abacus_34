package com.jigar.me

//import com.facebook.drawee.backends.pipeline.Fresco
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.annotation.NonNull
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.gson.Gson
import com.jigar.me.data.model.NotificationData
import com.jigar.me.utils.RevenueCatHelper
import com.revenuecat.purchases.CacheFetchPolicy
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.awaitCustomerInfo
import com.revenuecat.purchases.awaitOfferings
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import kotlinx.coroutines.launch
import com.jigar.me.ui.view.home.HomeActivity
import com.jigar.me.ui.view.home.navigation.RouteNavigation
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Constants
import com.jigar.me.utils.VersionUpdation
import com.jigar.me.utils.extensions.openURL
import com.jigar.me.utils.extensions.openYoutube
import com.jigar.me.utils.extensions.shareIntent
import com.onesignal.OneSignal
import com.onesignal.OneSignal.InAppMessages
import com.onesignal.OneSignal.Location
import com.onesignal.OneSignal.Notifications
import com.onesignal.OneSignal.User
import com.onesignal.debug.LogLevel
import com.onesignal.inAppMessages.IInAppMessageClickEvent
import com.onesignal.inAppMessages.IInAppMessageClickListener
import com.onesignal.inAppMessages.IInAppMessageDidDismissEvent
import com.onesignal.inAppMessages.IInAppMessageDidDisplayEvent
import com.onesignal.inAppMessages.IInAppMessageLifecycleListener
import com.onesignal.inAppMessages.IInAppMessageWillDismissEvent
import com.onesignal.inAppMessages.IInAppMessageWillDisplayEvent
import com.onesignal.notifications.INotificationClickEvent
import com.onesignal.notifications.INotificationClickListener
import com.onesignal.notifications.INotificationLifecycleListener
import com.onesignal.notifications.INotificationWillDisplayEvent
import com.onesignal.user.state.IUserStateObserver
import com.onesignal.user.state.UserChangedState
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class MyApplication : Application(), Configuration.Provider {
    init {
        instance = this
        System.loadLibrary("native-lib")
        System.loadLibrary("sqlcipher")
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    companion object {
        var instance: MyApplication? = null

        fun getInstance(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        installTextToSpeechCrashGuard()
        stopFinalizerWatchdogOnAndroid7()
        // Initialize RevenueCat
        Purchases.logLevel = if (BuildConfig.DEBUG) com.revenuecat.purchases.LogLevel.DEBUG else com.revenuecat.purchases.LogLevel.WARN
        Purchases.configure(
            PurchasesConfiguration.Builder(this, "goog_GZMHOoRwGnscKzjqLIghpwwUzYh").build()
        )
        Purchases.sharedInstance.updatedCustomerInfoListener = UpdatedCustomerInfoListener { RevenueCatHelper.update(it) }
        kotlinx.coroutines.MainScope().launch {
            // Seed RC cache from disk immediately — eliminates the cold-launch null window
            // where cachedInfo is null for ~1s before the listener fires.
            try { RevenueCatHelper.update(Purchases.sharedInstance.awaitCustomerInfo(CacheFetchPolicy.CACHE_ONLY)) } catch (_: Exception) {}
            try { Purchases.sharedInstance.awaitOfferings() } catch (_: Exception) {}
        }

        // app version update if any code logic change
        VersionUpdation.init(this)

        oneSignal()

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity is HomeActivity){
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
                }else{
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
                }
            }
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    /**
     * Some phones' own text-to-speech engine fails while Android is connecting to it. The framework does that in an AsyncTask
     * (`android.speech.tts.TextToSpeech$Connection$SetupConnectionAsyncTask`), so the error is thrown on a background thread where
     * nothing of ours can catch it, and the app closed at launch ("An error occurred while executing doInBackground()", caused by
     * "Task was cancelled"). The app works without speech, so that one failure is recorded in Crashlytics and the app goes on.
     * Every other uncaught exception still reaches the normal handler.
     */
    /**
     * On Android 7 (API 24 and 25) the system closes the whole app when any object's finalize() runs longer than 10 seconds
     * ("android.graphics.Path.finalize() timed out after 10 seconds"). That happens on slow, overloaded phones that draw a lot,
     * because every Path has a finalizer on those versions, and it is not a fault of one screen. Stopping that watchdog lets the
     * slow clean-up finish instead of closing the app. Newer Android versions have no finalizer on Path and block this access, so
     * it is limited to the two old versions, and any failure of the call is ignored.
     */
    private fun stopFinalizerWatchdogOnAndroid7() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) return
        try {
            val watchdog = Class.forName("java.lang.Daemons\$FinalizerWatchdogDaemon")
            val instance = watchdog.getDeclaredField("INSTANCE").apply { isAccessible = true }.get(null)
            watchdog.superclass.getDeclaredMethod("stop").apply { isAccessible = true }.invoke(instance)
        } catch (_: Throwable) {
            // not available on this phone: nothing to stop
        }
    }

    private fun installTextToSpeechCrashGuard() {
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            if (thread != Looper.getMainLooper().thread && throwable.isTextToSpeechSetupFailure()) {
                runCatching { com.google.firebase.crashlytics.FirebaseCrashlytics.getInstance().recordException(throwable) }
            } else {
                previous?.uncaughtException(thread, throwable)
            }
        }
    }

    private fun Throwable.isTextToSpeechSetupFailure(): Boolean =
        message?.contains("doInBackground") == true &&
            generateSequence(this) { it.cause }.any { t -> t.stackTrace.any { it.className.startsWith("android.speech.tts.TextToSpeech") } }

    private fun oneSignal() {
        // Verbose logging only for debug builds; releases stay quiet.
        OneSignal.Debug.logLevel = if (BuildConfig.DEBUG) LogLevel.VERBOSE else LogLevel.WARN
        // OneSignal Initialization
        OneSignal.initWithContext(this, CommonUtils.getOneSignalKey())

        // Every OneSignal object used below (InAppMessages, Notifications, User, Location) waits until OneSignal has finished
        // starting up. On a slow phone that wait ran out on the main thread and threw from onCreate, which closed the app at
        // launch ("initWithContext was not called or timed out"). The wait now happens on a background thread, and a failure
        // only costs the push and in-app message listeners.
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default).launch {
            runCatching { oneSignalListeners() }.onFailure {
                com.google.firebase.crashlytics.FirebaseCrashlytics.getInstance().recordException(it)
            }
        }
    }

    private fun oneSignalListeners() {
        InAppMessages.addLifecycleListener(object : IInAppMessageLifecycleListener {
            override fun onWillDisplay(event: IInAppMessageWillDisplayEvent) {
            }

            override fun onDidDisplay(event: IInAppMessageDidDisplayEvent) {
            }

            override fun onWillDismiss(event: IInAppMessageWillDismissEvent) {
            }

            override fun onDidDismiss(event: IInAppMessageDidDismissEvent) {
            }
        })

        InAppMessages.addClickListener(object : IInAppMessageClickListener {
            override fun onClick(event: IInAppMessageClickEvent) {
            }
        })

        Notifications.addClickListener(object : INotificationClickListener {
            override fun onClick(event: INotificationClickEvent) {
                val additional_data = event.notification.additionalData.toString()
                if (additional_data.isNotEmpty()) {
                    val notification = Gson().fromJson(additional_data, NotificationData::class.java)

                    if (notification != null) {
                        when (notification.type) {
                            Constants.notificationTypeStarter -> {
                                moveToDestination(RouteNavigation.AbacusFreeMode.route)
                            }
                            Constants.notificationTypeExercise -> {
                                moveToDestination(RouteNavigation.Exercise.route)
                            }
                            Constants.notificationTypeCCM -> {
                                moveToDestination(RouteNavigation.CCMHome.route)
                            }
                            Constants.notificationTypeExam -> {
                                moveToDestination(RouteNavigation.ExamHome.route)
                            }
                            Constants.notificationTypeNumberSequence -> {
                                moveToDestination(RouteNavigation.NumberSequencePuzzleHome.route)
                            }
                            Constants.notificationTypeSetting -> {
                                moveToDestination(RouteNavigation.Settings.route)
                            }
                            Constants.notificationTypePurchase -> {
                                moveToDestination(RouteNavigation.Purchase.route)
                            }
                            Constants.notificationTypeMathGame -> {
                                moveToDestination(RouteNavigation.MathGameZone.route)
                            }
                            Constants.notificationTypeCrossMath -> {
                                moveToDestination(RouteNavigation.CrossMathHome.route)
                            }
                            Constants.notificationTypeNumberPath -> {
                                moveToDestination(RouteNavigation.NumberPathHome.route)
                            }
                            Constants.notificationTypeTrueFalse -> {
                                moveToDestination(RouteNavigation.TrueFalseHome.route)
                            }
                            Constants.notificationTypePlaceValue -> {
                                moveToDestination(RouteNavigation.PlaceValueHome.route)
                            }
                            Constants.notificationTypeClockMaster -> {
                                moveToDestination(RouteNavigation.ClockMasterHome.route)
                            }
                            Constants.notificationTypeMathBingo -> {
                                moveToDestination(RouteNavigation.MathBingoHome.route)
                            }
                            Constants.notificationTypeKakuro -> {
                                moveToDestination(RouteNavigation.KakuroHome.route)
                            }
                            Constants.notificationTypeSudoku -> {
                                moveToDestination(RouteNavigation.SudokuHome.route)
                            }
                            Constants.notificationTypeMathPyramid -> {
                                moveToDestination(RouteNavigation.MathPyramidHome.route)
                            }
                            Constants.notificationTypeTargetNumber -> {
                                moveToDestination(RouteNavigation.TargetNumberHome.route)
                            }
                            Constants.notificationTypeBalloonPop -> {
                                moveToDestination(RouteNavigation.BalloonPopHome.route)
                            }
                            Constants.notificationTypeSpeedCompare -> {
                                moveToDestination(RouteNavigation.SpeedCompareHome.route)
                            }
                            Constants.notificationTypeMissingOperator -> {
                                moveToDestination(RouteNavigation.MissingOperatorHome.route)
                            }
                            Constants.notificationTypeMagicSquare -> {
                                moveToDestination(RouteNavigation.MagicSquareHome.route)
                            }
                            Constants.notificationTypeCalcudoku -> {
                                moveToDestination(RouteNavigation.CalcudokuHome.route)
                            }
                            Constants.notificationTypeMerge2048 -> {
                                moveToDestination(RouteNavigation.Merge2048Home.route)
                            }
                            Constants.notificationTypeEquationMatch -> {
                                moveToDestination(RouteNavigation.EquationMatchHome.route)
                            }
                            Constants.notificationTypePractice -> {
                                moveToDestination(RouteNavigation.LevelCategory.levelCategory("1"))
                            }
                            Constants.notificationTypeLevel1 -> {
                                moveToDestination(RouteNavigation.Level1Home.route)
                            }
                            Constants.notificationTypeLevel2 -> {
                                moveToDestination(RouteNavigation.Level2Home.route)
                            }
                            Constants.notificationTypeLevel3 -> {
                                moveToDestination(RouteNavigation.Level3Home.route)
                            }
                            Constants.notificationTypeLevel4 -> {
                                moveToDestination(RouteNavigation.Level4TablePicker.route)
                            }
                            Constants.notificationTypeYoutubeHome -> {
                                getInstance().openYoutube()
                            }
                            Constants.notificationTypeYoutube -> {
                                getInstance().openYoutube(notification.youtube_url)
                            }
                            Constants.notificationTypeRate -> {
                                getInstance().openURL("https://play.google.com/store/apps/details?id=${getInstance().packageName}")
                            }
                            Constants.notificationTypeShare -> {
                                getInstance().shareIntent()
                            }
                            else -> {
                                moveToDestination(RouteNavigation.Home.route)
                            }
                        }
                    }else{
                        moveToDestination(RouteNavigation.Home.route)
                    }

                }else{
                    moveToDestination(RouteNavigation.Home.route)
                }
            }
        })

        // Foreground pushes display immediately with OneSignal's default
        // behavior — the old sample-code listener added a 2s delay thread.

        User.addObserver(object : IUserStateObserver {
            override fun onUserStateChange(@NonNull state: UserChangedState) {
                val currentUserState = state.current
            }
        })

        InAppMessages.paused = true
        Location.isShared = false

    }

    private fun moveToDestination(route: String) {
        HomeActivity.getInstance(this, route)
    }

}
