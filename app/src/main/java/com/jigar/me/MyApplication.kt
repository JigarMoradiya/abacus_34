package com.jigar.me

//import com.facebook.drawee.backends.pipeline.Fresco
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.hilt.work.HiltWorkerFactory
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.Configuration
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.jigar.me.data.model.NotificationData
import com.jigar.me.ui.view.base.inapp.BillingRepository
import com.jigar.me.ui.view.dashboard.MainDashboardActivity
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
        alreadyCalledversionCheck = false
        System.loadLibrary("native-lib")
        System.loadLibrary("sqlcipher")
    }

    @Inject
    lateinit var billingRepository: BillingRepository

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

    override fun onTerminate() {
        super.onTerminate()
        billingRepository.endDataSourceConnections()
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize billing once at app start
        billingRepository.startDataSourceConnections()

        // app version update if any code logic change
        VersionUpdation.init(this)

//        Fresco.initialize(this)
        analytics = FirebaseAnalytics.getInstance(this@MyApplication)
        oneSignal()
    }

    private fun oneSignal() {
        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.Debug.logLevel = LogLevel.VERBOSE
        // OneSignal Initialization
        OneSignal.initWithContext(this, CommonUtils.getOneSignalKey())

        InAppMessages.addLifecycleListener(object : IInAppMessageLifecycleListener {
            override fun onWillDisplay(@NonNull event: IInAppMessageWillDisplayEvent) {
            }

            override fun onDidDisplay(@NonNull event: IInAppMessageDidDisplayEvent) {
            }

            override fun onWillDismiss(@NonNull event: IInAppMessageWillDismissEvent) {
            }

            override fun onDidDismiss(@NonNull event: IInAppMessageDidDismissEvent) {
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
                                moveToDestination(R.id.abacusFreeModeFragment)
                            }
                            Constants.notificationTypeExercise -> {
                                moveToDestination(R.id.exerciseFragment)
                            }
                            Constants.notificationTypeCCM -> {
                                moveToDestination(R.id.ccmHomeFragment)
                            }
                            Constants.notificationTypeExam -> {
                                moveToDestination(R.id.examHomeFragmentNew)
                            }
                            Constants.notificationTypeNumberSequence -> {
                                moveToDestination(R.id.numberSequencePuzzleHomeFragment)
                            }
                            Constants.notificationTypeSetting -> {
                                moveToDestination(R.id.settingFragmentNew)
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
                                moveToDestination(R.id.homeFragmentNew)
                            }
                        }
                    }else{
                        moveToDestination(R.id.homeFragmentNew)
                    }

                }else{
                    moveToDestination(R.id.homeFragmentNew)
                }
            }
        })

        Notifications.addForegroundLifecycleListener(object : INotificationLifecycleListener {
            override fun onWillDisplay(@NonNull event: INotificationWillDisplayEvent) {
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
            }
        })

        InAppMessages.paused = true
        Location.isShared = false

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
