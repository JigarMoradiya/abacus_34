package com.jigar.me

//import com.facebook.drawee.backends.pipeline.Fresco
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.jigar.me.utils.RevenueCatHelper
import com.revenuecat.purchases.CacheFetchPolicy
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.awaitCustomerInfo
import com.revenuecat.purchases.awaitOfferings
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import kotlinx.coroutines.launch
import com.jigar.me.ui.view.home.HomeActivity
import com.jigar.me.utils.VersionUpdation
import com.jigar.me.utils.fcm.AppFirebaseMessagingService
import com.jigar.me.utils.fcm.FcmTokenManager
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
    lateinit var fcmTokenManager: FcmTokenManager

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

        AppFirebaseMessagingService.createChannel(this)
        // Keeps the logged-in student's push details on our server (guests are skipped inside); calls when something changed, and once a
        // day the app is used (the server's "last seen")
        fcmTokenManager.start()

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity is HomeActivity){
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
                }else{
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
                }
            }
            // the app came to the front: a new day is reported once (a process can stay alive across days)
            override fun onActivityStarted(activity: Activity) { if (activity is HomeActivity) fcmTokenManager.requestSync() }
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

}
