package com.jigar.me.utils.fcm

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.jigar.me.data.model.data.FcmTokenRequest
import com.jigar.me.data.model.data.LoginData
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.data.repositories.StudentApiRepository
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.DeviceCountry
import com.jigar.me.utils.LanguageManager
import com.jigar.me.utils.Resource
import com.jigar.me.utils.RevenueCatHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Keeps the logged-in student's push details on our server: the FCM token plus the country, app language and paid status
 * the admin filters pushes by. Everything goes through [requestSync]: asks made within [SYNC_DELAY_MS] join into one check,
 * and the check calls the server only when token, country, language or paid status differ from what was last sent
 * (so a normal app start makes no call). Guests are skipped. Every call is fire-and-forget and never throws.
 */
@Singleton
class FcmTokenManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiRepository: StudentApiRepository,
    private val prefs: AppPreferencesHelper,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()
    private var pending: Job? = null

    /** Once from MyApplication (after RevenueCat is configured): checks now, and again when paid status or language change. */
    fun start() {
        scope.launch {
            // the first value is the check at app start; later ones only when free / paid really flips
            RevenueCatHelper.customerInfoFlow
                .map { CommonUtils.checkPurchaseForExerciseExamCCM(prefs) }
                .distinctUntilChanged()
                .collect { requestSync() }
        }
        LanguageManager.onLanguageChanged = { requestSync() }
    }

    /** Asks for a check in [SYNC_DELAY_MS]; asks made meanwhile join it (app start, login, a new token, a purchase). */
    fun requestSync() {
        synchronized(this) {
            pending?.cancel()
            pending = scope.launch {
                delay(SYNC_DELAY_MS)
                syncNow()
            }
        }
    }

    private suspend fun syncNow() {
        if (!isLoggedIn()) return
        val token = currentToken() ?: return
        val request = FcmTokenRequest(
            fcm_token = token,
            country = DeviceCountry.code(context),
            language = LanguageManager.currentTag(),
            is_premium = CommonUtils.checkPurchaseForExerciseExamCCM(prefs),
        )
        // the date is part of the key: the server hears from this phone at least once a day it is used (its "last seen", for the admin's
        // "not opened for N days" push filter), and at once when something changes
        val key = "${com.jigar.me.utils.StreakManager.today()}|${studentId()}|$token|${request.country}|${request.language}|${request.is_premium}"
        mutex.withLock {
            if (prefs.getCustomParam(AppConstants.Notifications.fcmLastSent, "") == key) return@withLock
            if (send(request)) prefs.setCustomParam(AppConstants.Notifications.fcmLastSent, key)
        }
    }

    /**
     * Clears the token on the server. Call BEFORE wiping prefs on logout: the access token is
     * captured here so the request still authenticates after prefs are cleared.
     */
    fun clearOnLogout() {
        val accessToken = prefs.getAccessToken()
        if (!isLoggedIn() || accessToken.isNullOrEmpty()) return
        synchronized(this) { pending?.cancel() }
        scope.launch {
            mutex.withLock { send(FcmTokenRequest(fcm_token = ""), accessToken) }
        }
    }

    private suspend fun currentToken(): String? = try {
        suspendCancellableCoroutine { continuation ->
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { continuation.resume(it?.takeIf { token -> token.isNotEmpty() }) }
                .addOnFailureListener {
                    Log.e(TAG, "token fetch failed", it)
                    continuation.resume(null)
                }
        }
    } catch (e: Exception) {
        Log.e(TAG, "token fetch failed", e)
        null
    }

    private suspend fun send(request: FcmTokenRequest, accessToken: String? = null): Boolean = try {
        val response = apiRepository.updateFcmToken(request, accessToken)
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
        const val SYNC_DELAY_MS = 3_000L
    }
}
