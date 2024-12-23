package com.jigar.me.internal.workmanagers

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.jigar.me.MyApplication
import com.jigar.me.utils.AppConstants
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.io.IOException

@HiltWorker
class FirstAppStartWorkManager @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    private val context = appContext
    override suspend fun doWork(): Result = coroutineScope {
        val jobs = async {
            recordReferral()
        }
        jobs.await()
        Result.success()
    }

    private fun recordReferral() = runBlocking{
        Log.e("recordReferral","welcome")
        try {
            val referrerClient = InstallReferrerClient.newBuilder(context).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {

                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    Log.e("recordReferral","responseCode = "+responseCode)
                    when (responseCode) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            // Connection established.
                            val response: ReferrerDetails = referrerClient.installReferrer
                            val referrerUrl = response.installReferrer // utm_source=google-play&utm_medium=organic //it's default value only

//                            val referrerClickTime: Long = response.referrerClickTimestampSeconds*1000
//                            val appInstallTime: Long = response.installBeginTimestampSeconds*1000

                            if (referrerUrl.isNotEmpty()) {
                                val referrerParts = referrerUrl.split("&")//split with &
                                Log.e("recordReferral", "referrerParts - $referrerParts")//divided in 2 parts utm_source,utm_content

                                val utmSource = referrerParts.find {
                                    it.contains("utm_source")
                                }?.split("=")?.get(1)//get the value of utm_source
                                Log.e("recordReferral", "utmSource - $utmSource")

                                if (utmSource != null && utmSource.contains("campaign")) {
                                    val utmContent = referrerParts.find {
                                        it.contains("utm_content")
                                    }?.split("=")?.get(1)//get the value of utm_content
                                    Log.e("recordReferral", "utmContent - $utmContent")

                                    if (utmContent != null) {
                                        val refCode = utmContent//save apply the code anywhere
                                        MyApplication.logEvent(AppConstants.FirebaseEvents.appInstallFrom, Bundle().apply {
                                            putString("utm_source", utmSource)
                                            putString("utm_content", refCode)
                                        })
                                    }
                                }
                            }

                            // Save App Install Time
//                            PreferenceUtils.putLong(Constants.PREF_KEY_APP_INSTALL_TIME, appInstallTime, ctx)

//                            Log.e("recordReferral","referrerUrl = "+referrerUrl)
//                            Log.e("recordReferral","referrerClickTime = "+referrerClickTime)
//                            Log.e("recordReferral","appInstallTime = "+appInstallTime)

//                            var isUserReferred = false
//                            referrerUrl.split("&").forEach { term ->
//                                if (term.contains("=") && term.split("=").size>1){
//                                    val param = term.split("=")[0]
//                                    val value = term.split("=")[1]
//                                    Log.e("recordReferral","Found utm_medium in Installation and value is $value")
////                                    when(param){
////                                        "utm_medium" -> {
////                                            Log.e("recordReferral","Found utm_medium in Installation and value is $value")
////                                            if (isUserReferred){
//////                                                PreferenceUtils.putString(Constants.PREF_KEY_USER_REFERRED_FROM, value, ctx)
////                                            }
////                                        }
////                                        "utm_source" -> {
////                                            Log.e("recordReferral","Found utm_source in Installation and value is $value")
////                                            if (value=="referral"){
//////                                                PreferenceUtils.putBoolean(Constants.PREF_KEY_IS_USER_REFERRED, true, ctx)
////                                                isUserReferred = true
////                                            }
////                                        }
////                                    }
//                                }
//                            }

                            referrerClient.endConnection()

                        }
                        InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                            // API not available on the current Play Store app.
                            Log.e("recordReferral","Feature Not Supported")
                        }
                        InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                            // Connection couldn't be established.
                            Log.e("recordReferral","Service is Unavailable.")
                        }
                    }
                }

                override fun onInstallReferrerServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                    Log.e("recordReferral","Service is disconnected.")
                }
            })
        }catch (e : IOException){
            e.printStackTrace()
        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    companion object{
        fun startWorkManager(context : Context){
            val uploadWorkRequest = OneTimeWorkRequestBuilder<FirstAppStartWorkManager>().build()
            val workManager = WorkManager.getInstance(context)
            workManager.enqueue(uploadWorkRequest)
        }
    }
}