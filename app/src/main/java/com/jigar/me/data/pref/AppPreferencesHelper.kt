package com.jigar.me.data.pref

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
import javax.inject.Inject


class AppPreferencesHelper @Inject constructor(
    context: Context,
    @PreferenceInfo private val prefFileName: String
) : PreferencesHelper {
    companion object {
        private const val PREF_KEY_ACCESS_TOKEN = "PREF_KEY_ACCESS_TOKEN"
        private const val PREF_KEY_IS_USER_LOGGED_IN = "PREF_KEY_IS_USER_LOGGED_IN"
        private const val PREF_KEY_IS_USER_IN_FREE_TRIAL = "PREF_KEY_IS_USER_IN_FREE_TRIAL"
        private const val PREF_KEY_LOGIN_DATA = "PREF_KEY_LoginData"

        const val KEY_DEFAULT_TTS_VOICE = "KEY_DEFAULT_TTS_VOICE"
        const val DEFAULT_TTS_VOICE_VALUE = "en-in-x-end-network"
//        const val DEFAULT_TTS_VOICE_VALUE = "hi-in-x-hie-local"

        const val KEY_DEFAULT_TTS_LANGUAGE = "KEY_DEFAULT_TTS_LANGUAGE"
        const val DEFAULT_TTS_LANGUAGE_VALUE = "en_IN" // hi_IN, en_US

        const val KEY_DEFAULT_TTS_PITCH = "KEY_DEFAULT_TTS_PITCH"
        const val KEY_DEFAULT_TTS_SPEECH = "KEY_DEFAULT_TTS_SPEECH"
    }

    private val mPrefs: SharedPreferences =
        context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)


    override fun getCustomParam(paramName: String, defaultValue: String): String =
        mPrefs.getString(paramName, defaultValue).toString()

    override fun setCustomParam(paramName: String, paramValue: String) = mPrefs.edit {
        putString(paramName, paramValue)
    }

    override fun getCustomParamInt(paramName: String, defaultValue: Int): Int =
        mPrefs.getInt(paramName, defaultValue)

    override fun setCustomParamInt(paramName: String, paramValue: Int) = mPrefs.edit {
        putInt(paramName, paramValue)
    }

    override fun getCustomParamBoolean(paramName: String, defaultValue: Boolean): Boolean =
        mPrefs.getBoolean(paramName, defaultValue)

    override fun setCustomParamBoolean(paramName: String, paramValue: Boolean) = mPrefs.edit {
        putBoolean(paramName, paramValue)
    }

    override fun getCustomParamFloat(paramName: String, defaultValue: Float): Float =
        mPrefs.getFloat(paramName, defaultValue)

    override fun setCustomParamFloat(paramName: String, paramValue: Float) = mPrefs.edit {
        putFloat(paramName, paramValue)
    }

    override fun isUserLoggedIn(): Boolean = mPrefs.getBoolean(PREF_KEY_IS_USER_LOGGED_IN, false)

    override fun setUserLoggedIn(value: Boolean) = mPrefs.edit {
        putBoolean(PREF_KEY_IS_USER_LOGGED_IN, value)
    }
    override fun isUserInFreeTrial(): Boolean = mPrefs.getBoolean(PREF_KEY_IS_USER_IN_FREE_TRIAL, false)

    override fun setUserInFreeTrial(value: Boolean) = mPrefs.edit {
        putBoolean(PREF_KEY_IS_USER_IN_FREE_TRIAL, value)
    }

    override fun getAccessToken(): String? = mPrefs.getString(PREF_KEY_ACCESS_TOKEN, null)

    override fun setAccessToken(accessToken: String?) = mPrefs.edit {
        putString(PREF_KEY_ACCESS_TOKEN, accessToken)
    }
    override fun getLoginData(): String? = mPrefs.getString(PREF_KEY_LOGIN_DATA, null)

    override fun setLoginData(data: String?) = mPrefs.edit {
        putString(PREF_KEY_LOGIN_DATA, data)
    }

    fun getDefaultTTSPitch(): Float {
        return getCustomParamFloat(KEY_DEFAULT_TTS_PITCH, 10f)
    }

    fun getDefaultTTSSpeed(): Float {
        return getCustomParamFloat(KEY_DEFAULT_TTS_SPEECH, 9f)
    }

    fun clearPref() {
        val lastSyncTime = getCustomParam(Constants.last_sync_time, Constants.last_sync_default_time)
        val bgMusicVolume = getCustomParamInt(AppConstants.Settings.Setting_bg_music_volume, AppConstants.Settings.Setting_bg_music_volume_default)
        mPrefs.edit { clear() }
        setCustomParam(Constants.last_sync_time, lastSyncTime)
        setCustomParamInt(AppConstants.Settings.Setting_bg_music_volume, bgMusicVolume)
    }
}
