package com.jigar.me.data.api.connections

import android.util.Log
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.utils.CommonUtils
import okhttp3.Interceptor
import okhttp3.Response

class AuthHeaderInterceptor  private constructor(
    private val prefManager : AppPreferencesHelper
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        requestBuilder.header("organizer-id",CommonUtils.getOrganizerId())
        Log.e("AuthHeaderInterceptor","organizer-id = "+CommonUtils.getOrganizerId())
        prefManager.getAccessToken()?.let {
            val token = it
            Log.e("AuthHeaderInterceptor","access-token = "+token)
            requestBuilder.header("access-token",token)
        }

        return chain.proceed(requestBuilder.build())
    }

    companion object {
        operator fun invoke(prefManager : AppPreferencesHelper) = AuthHeaderInterceptor(prefManager)
    }
}