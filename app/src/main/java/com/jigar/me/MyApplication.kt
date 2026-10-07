package com.jigar.me

//import com.facebook.drawee.backends.pipeline.Fresco
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
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
        // Initialize RevenueCat
        Purchases.logLevel = com.revenuecat.purchases.LogLevel.DEBUG
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
        // Register/refresh the FCM token for a logged-in student (guests are skipped inside)
        fcmTokenManager.syncCurrentToken()

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

}
