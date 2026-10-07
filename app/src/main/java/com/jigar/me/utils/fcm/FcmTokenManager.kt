package com.jigar.me.utils.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.jigar.me.data.model.data.FcmTokenRequest
import com.jigar.me.data.model.data.LoginData
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.data.repositories.StudentApiRepository
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Registers this device's FCM token against the logged-in student on our server.
 * Guests (not logged in) are skipped silently. Every call is fire-and-forget and never throws.
 */
@Singleton
class FcmTokenManager @Inject constructor(
    private val apiRepository: StudentApiRepository,
    private val prefs: AppPreferencesHelper,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()

    /** Resolves the current FCM token and sends it if the student/token pair changed. */
    fun syncCurrentToken() {
        if (!isLoggedIn()) return
        try {
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token -> if (!token.isNullOrEmpty()) onToken(token) }
                .addOnFailureListener { Log.e(TAG, "token fetch failed", it) }
        } catch (e: Exception) {
            Log.e(TAG, "token fetch failed", e)
        }
    }

    /** Called with a known token (onNewToken or a fresh fetch). */
    fun onToken(token: String) {
        if (!isLoggedIn()) return
        val key = "${studentId()}|$token"
        scope.launch {
            mutex.withLock {
                if (prefs.getCustomParam(AppConstants.Notifications.fcmLastSent, "") == key) return@withLock
                if (send(token)) prefs.setCustomParam(AppConstants.Notifications.fcmLastSent, key)
            }
        }
    }

    /**
     * Clears the token on the server. Call BEFORE wiping prefs on logout: the access token is
     * captured here so the request still authenticates after prefs are cleared.
     */
    fun clearOnLogout() {
        val accessToken = prefs.getAccessToken()
        if (!isLoggedIn() || accessToken.isNullOrEmpty()) return
        scope.launch {
            mutex.withLock { send("", accessToken) }
        }
    }

    private suspend fun send(token: String, accessToken: String? = null): Boolean = try {
        val response = apiRepository.updateFcmToken(FcmTokenRequest(fcm_token = token), accessToken)
        response is Resource.Success && response.value.status == AppConstants.APIStatus.SUCCESS
    } catch (e: Exception) {
        Log.e(TAG, "token update failed", e)
        false
    }

    private fun isLoggedIn() = prefs.isUserLoggedIn() && !prefs.getAccessToken().isNullOrEmpty()

    private fun studentId(): String = try {
        Gson().fromJson(prefs.getLoginData(), LoginData::class.java)?.id.orEmpty()
    } catch (e: Exception) {
        ""
    }

    private companion object {
        const val TAG = "FcmTokenManager"
    }
}
