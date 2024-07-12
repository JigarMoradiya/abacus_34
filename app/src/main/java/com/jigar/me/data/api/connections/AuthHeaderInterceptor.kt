package com.jigar.me.data.api.connections

import android.util.Log
import com.jigar.me.BuildConfig
import com.jigar.me.data.pref.AppPreferencesHelper
import okhttp3.Interceptor
import okhttp3.Response

class AuthHeaderInterceptor  private constructor(
    private val prefManager : AppPreferencesHelper
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
//        requestBuilder.header("application-package-id",BuildConfig.APPLICATION_ID)
        requestBuilder.header("organizer-id",BuildConfig.ORGANIZER_ID)
        prefManager.getAccessToken()?.let {
            val token = it
            if (BuildConfig.DEBUG){
                Log.e("jigarLogs", "AuthHeaderInterceptor accessToken = $token")
            }
            requestBuilder.header("access-token",token)
        }

        return chain.proceed(requestBuilder.build())
    }

    companion object {
        operator fun invoke(prefManager : AppPreferencesHelper) = AuthHeaderInterceptor(prefManager)
    }
}