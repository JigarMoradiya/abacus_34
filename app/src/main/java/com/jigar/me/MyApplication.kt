package com.jigar.me

import android.app.Activity
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import androidx.hilt.work.HiltWorkerFactory
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.Configuration
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.jigar.me.data.model.NotificationData
import com.jigar.me.ui.view.dashboard.MainDashboardActivity
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
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
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import java.io.IOException
import java.net.SocketException
import javax.inject.Inject


@HiltAndroidApp
class MyApplication : Application(), Configuration.Provider {
    init {
        instance = this
        alreadyCalledversionCheck = false
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    companion object {
        var instance: MyApplication? = null
        var alreadyCalledversionCheck: Boolean? = false

        var analytics: FirebaseAnalytics? = null

        fun logEvent(event:String,data: Bundle?){
            analytics?.logEvent(event,data)
        }

        fun getInstance(): Context {
            return instance!!.applicationContext
        }
    }


    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        Fresco.initialize(this)

        if (BuildConfig.DEBUG){
            val testDeviceIds = listOf("465FAD15876FE450FAC4DFB84C422B2E","382FFCBE27B5AE320FD2EECB403C0D46")
            val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
            MobileAds.setRequestConfiguration(configuration)
        }

        analytics = FirebaseAnalytics.getInstance(this@MyApplication)

        oneSignal()

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
//                if (activity is PaymentActivity){
//                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//                }else{
//                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
//                }
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })


        RxJavaPlugins.setErrorHandler { e: Throwable ->
            if (e is UndeliverableException) {
                // fine, irrelevant network problem or API that throws on cancellation
                return@setErrorHandler
            }
            if (e is IOException || e is SocketException) {
                // fine, irrelevant network problem or API that throws on cancellation
                return@setErrorHandler
            }
            if (e is InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                return@setErrorHandler
            }
            if (e is NullPointerException || e is IllegalArgumentException) {
                // that's likely a bug in the application
                Thread.currentThread().uncaughtExceptionHandler
                    ?.uncaughtException(Thread.currentThread(), e)
                return@setErrorHandler
            }
            if (e is IllegalStateException) {
                // that's a bug in RxJava or in a custom operator
                Thread.currentThread().uncaughtExceptionHandler
                    ?.uncaughtException(Thread.currentThread(), e)
                return@setErrorHandler
            }
            Log.e("Undeliverable exception", e.toString())
        }


    }

    private fun oneSignal() {
        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.Debug.logLevel = LogLevel.VERBOSE
        // OneSignal Initialization
        OneSignal.initWithContext(this, BuildConfig.ONE_SIGNAL)

        InAppMessages.addLifecycleListener(object : IInAppMessageLifecycleListener {
            override fun onWillDisplay(@NonNull event: IInAppMessageWillDisplayEvent) {
                Log.e("jigarOneSignal", "onWillDisplayInAppMessage")
            }

            override fun onDidDisplay(@NonNull event: IInAppMessageDidDisplayEvent) {
                Log.e("jigarOneSignal", "onDidDisplayInAppMessage")
            }

            override fun onWillDismiss(@NonNull event: IInAppMessageWillDismissEvent) {
                Log.e("jigarOneSignal", "onWillDismissInAppMessage")
            }

            override fun onDidDismiss(@NonNull event: IInAppMessageDidDismissEvent) {
                Log.e("jigarOneSignal", "onDidDismissInAppMessage")
            }
        })

        InAppMessages.addClickListener(object : IInAppMessageClickListener {
            override fun onClick(event: IInAppMessageClickEvent) {
                Log.e("jigarOneSignal", "INotificationClickListener.inAppMessageClicked")
            }
        })

        Notifications.addClickListener(object : INotificationClickListener {
            override fun onClick(event: INotificationClickEvent) {
                Log.e(
                    "jigarOneSignal", "INotificationClickListener.onClick fired" +
                            " with event: " + event.notification.additionalData
                )

                val additional_data = event.notification.additionalData.toString()
                if (additional_data.isNotEmpty()) {
                    val notification = Gson().fromJson(additional_data, NotificationData::class.java)

                    if (notification != null) {
                        when (notification.type) {
                            Constants.notificationTypeStarter -> {
                                moveToDestination(R.id.fullAbacusFragment)
                            }
                            Constants.notificationTypeNumber -> {
                                moveToPages(AppConstants.HomeClicks.Menu_Number,getString(R.string.page_title_Number))
                            }
                            Constants.notificationTypeAddition -> {
                                moveToPages(AppConstants.HomeClicks.Menu_Addition_Subtraction,getString(R.string.page_title_AdditionSubtraction))
                            }
                            Constants.notificationTypeSubtraction -> {
                                moveToPages(AppConstants.HomeClicks.Menu_Formulas,getString(R.string.page_title_Formulas))
                            }
                            Constants.notificationTypeMultiplication -> {
                                moveToPages(AppConstants.HomeClicks.Menu_Multiplication,getString(R.string.page_title_Multiplication))
                            }
                            Constants.notificationTypeDivision -> {
                                moveToPages(AppConstants.HomeClicks.Menu_Division,getString(R.string.page_title_Division))
                            }
                            Constants.notificationTypeMaterial -> {
                                moveToDestination(R.id.materialHomeFragment)
                            }
                            Constants.notificationTypeExercise -> {
                                moveToDestination(R.id.exerciseHomeFragment)
                            }
                            Constants.notificationTypeCCM -> {
                                moveToDestination(R.id.customChallengeHomeFragment)
                            }
                            Constants.notificationTypeMaterialMath -> {
                                moveToPractiseMaterialType(AppConstants.extras_Comman.DownloadType_Maths)
                            }
                            Constants.notificationTypeMaterialNursery -> {
                                moveToPractiseMaterialType(AppConstants.extras_Comman.DownloadType_Nursery)
                            }
                            Constants.notificationTypeExam -> {
                                moveToDestination(R.id.examHomeFragment)
                            }
                            Constants.notificationTypeNumberSequence -> {
                                moveToDestination(R.id.puzzleNumberHomeFragment)
                            }
                            Constants.notificationTypeSetting -> {
                                moveToDestination(R.id.settingsFragment)
                            }
                            Constants.notificationTypePurchase -> {
                                moveToDestination(R.id.purchaseFragment)
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
                                moveToDestination(R.id.homeFragment)
                            }
                        }
                    }else{
                        moveToDestination(R.id.homeFragment)
                    }

                }else{
                    moveToDestination(R.id.homeFragment)
                }
            }
        })

        Notifications.addForegroundLifecycleListener(object : INotificationLifecycleListener {
            override fun onWillDisplay(@NonNull event: INotificationWillDisplayEvent) {
                Log.e("jigarOneSignal", "INotificationLifecycleListener.onWillDisplay fired" +
                            " with event: " + event
                )
                val notification = event.notification
                val data = notification.additionalData

                //Prevent OneSignal from displaying the notification immediately on return. Spin
                //up a new thread to mimic some asynchronous behavior, when the async behavior (which
                //takes 2 seconds) completes, then the notification can be displayed.
                event.preventDefault()
                val r = Runnable {
                    try {
                        Thread.sleep(2000)
                    } catch (ignored: InterruptedException) {
                    }
                    notification.display()
                }
                val t = Thread(r)
                t.start()
            }
        })

        User.addObserver(object : IUserStateObserver {
            override fun onUserStateChange(@NonNull state: UserChangedState) {
                val currentUserState = state.current
                Log.e("jigarOneSignal", "onUserStateChange fired " + currentUserState.toJSONObject())
            }
        })

        InAppMessages.paused = true
        Location.isShared = false


//        OneSignal.setNotificationOpenedHandler { result ->
//            Log.d("notification_data", result.toString())
//            val additional_data = result.notification.additionalData.toString()
//            if (additional_data.isNotEmpty()) {
//                val notification = Gson().fromJson(additional_data, NotificationData::class.java)
//
//                if (notification != null) {
//                    when (notification.type) {
//                        Constants.notificationTypeStarter -> {
//                            moveToDestination(R.id.fullAbacusFragment)
//                        }
//                        Constants.notificationTypeNumber -> {
//                            moveToPages(AppConstants.HomeClicks.Menu_Number,getString(R.string.page_title_Number))
//                        }
//                        Constants.notificationTypeAddition -> {
//                            moveToPages(AppConstants.HomeClicks.Menu_Addition_Subtraction,getString(R.string.page_title_AdditionSubtraction))
//                        }
//                        Constants.notificationTypeSubtraction -> {
//                            moveToPages(AppConstants.HomeClicks.Menu_Formulas,getString(R.string.page_title_Formulas))
//                        }
//                        Constants.notificationTypeMultiplication -> {
//                            moveToPages(AppConstants.HomeClicks.Menu_Multiplication,getString(R.string.page_title_Multiplication))
//                        }
//                        Constants.notificationTypeDivision -> {
//                            moveToPages(AppConstants.HomeClicks.Menu_Division,getString(R.string.page_title_Division))
//                        }
//                        Constants.notificationTypeMaterial -> {
//                            moveToDestination(R.id.materialHomeFragment)
//                        }
//                        Constants.notificationTypeExercise -> {
//                            moveToDestination(R.id.exerciseHomeFragment)
//                        }
//                        Constants.notificationTypeCCM -> {
//                            moveToDestination(R.id.customChallengeHomeFragment)
//                        }
//                        Constants.notificationTypeMaterialMath -> {
//                            moveToPractiseMaterialType(AppConstants.extras_Comman.DownloadType_Maths)
//                        }
//                        Constants.notificationTypeMaterialNursery -> {
//                            moveToPractiseMaterialType(AppConstants.extras_Comman.DownloadType_Nursery)
//                        }
//                        Constants.notificationTypeExam -> {
//                            moveToDestination(R.id.examHomeFragment)
//                        }
//                        Constants.notificationTypeNumberSequence -> {
//                            moveToDestination(R.id.puzzleNumberHomeFragment)
//                        }
//                        Constants.notificationTypeSetting -> {
//                            moveToDestination(R.id.settingsFragment)
//                        }
//                        Constants.notificationTypePurchase -> {
//                            moveToDestination(R.id.purchaseFragment)
//                        }
//                        Constants.notificationTypeYoutubeHome -> {
//                            this.openYoutube()
//                        }
//                        Constants.notificationTypeYoutube -> {
//                            this.openYoutube(notification.youtube_url)
//                        }
//                        Constants.notificationTypeRate -> {
//                            this.openURL("https://play.google.com/store/apps/details?id=${this.packageName}")
//                        }
//                        Constants.notificationTypeShare -> {
//                            this.shareIntent()
//                        }
//                        else -> {
//                            moveToDestination(R.id.homeFragment)
//                        }
//                    }
//                }else{
//                    moveToDestination(R.id.homeFragment)
//                }
//
//            }else{
//                moveToDestination(R.id.homeFragment)
//            }
//        }
//        OneSignal.setNotificationWillShowInForegroundHandler {
//
//        }
    }

    private fun moveToPages(from : Int, title : String) {
        val args = Bundle()
        args.putInt("from", from)
        args.putString("title", title)

        NavDeepLinkBuilder(this)
            .setGraph(R.navigation.main_navigation_graph)
            .setDestination(R.id.pageFragment)
            .setArguments(args)
            .setComponentName(MainDashboardActivity::class.java)
            .createTaskStackBuilder().getPendingIntent(1,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)!!
            .send()
    }
    private fun moveToPractiseMaterialType(downloadType : String) {
        val args = Bundle()
        args.putString("downloadType", downloadType)

        NavDeepLinkBuilder(this)
            .setGraph(R.navigation.main_navigation_graph)
            .setDestination(R.id.materialDownloadFragment)
            .setArguments(args)
            .setComponentName(MainDashboardActivity::class.java)
            .createTaskStackBuilder().getPendingIntent(1,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)!!
            .send()
    }

    private fun moveToDestination(id : Int) {
        NavDeepLinkBuilder(this)
            .setGraph(R.navigation.main_navigation_graph)
            .setDestination(id)
            .setComponentName(MainDashboardActivity::class.java)
            .createTaskStackBuilder().getPendingIntent(1,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)!!
            .send()
    }

}
