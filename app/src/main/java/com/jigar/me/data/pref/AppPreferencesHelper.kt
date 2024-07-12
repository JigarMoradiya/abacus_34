package com.jigar.me.data.pref

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.jigar.me.utils.AppConstants
import javax.inject.Inject


class AppPreferencesHelper @Inject constructor(
    context: Context,
    @PreferenceInfo private val prefFileName: String
) : PreferencesHelper {
    companion object {
        private const val PREF_KEY_FCMID = "PREF_KEY_FCMID"
        private const val PREF_KEY_DEVICE_ID = "PREF_KEY_DEVICE_ID"
        private const val PREF_KEY_ACCESS_TOKEN = "PREF_KEY_ACCESS_TOKEN"
        private const val PREF_KEY_COUNTRY_CODE = "PREF_KEY_COUNTRY_CODE"
        private const val PREF_KEY_IS_USER_LOGGED_IN = "PREF_KEY_IS_USER_LOGGED_IN"
        private const val PREF_KEY_IS_CURRENCY_INR = "PREF_KEY_IS_CURRENCY_INR"
        private const val PREF_KEY_LOGIN_DATA = "PREF_KEY_LoginData"
        private const val PREF_KEY_BASE_URL = "PREF_KEY_BASEURL"

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

    override fun getBaseUrl(): String =
        mPrefs.getString(PREF_KEY_BASE_URL, AppConstants.TEMP_BASE_URL).toString()

    override fun setBaseUrl(baseUrl: String) = mPrefs.edit {
        putString(PREF_KEY_BASE_URL, baseUrl)
    }

    override fun getFCMID(): String = mPrefs.getString(PREF_KEY_FCMID, "").toString()

    override fun setFCMID(value: String) = mPrefs.edit {
        putString(PREF_KEY_FCMID, value)
    }

    override fun isUserLoggedIn(): Boolean = mPrefs.getBoolean(PREF_KEY_IS_USER_LOGGED_IN, false)

    override fun setUserLoggedIn(value: Boolean) = mPrefs.edit {
        putBoolean(PREF_KEY_IS_USER_LOGGED_IN, value)
    }

    override fun isCurrencyINR(): Boolean = mPrefs.getBoolean(PREF_KEY_IS_CURRENCY_INR, false)

    override fun setIsCurrencyINR(value: Boolean) = mPrefs.edit {
        putBoolean(PREF_KEY_IS_CURRENCY_INR, value)
    }

    override fun getAccessToken(): String? = mPrefs.getString(PREF_KEY_ACCESS_TOKEN, null)

    override fun setAccessToken(accessToken: String?) = mPrefs.edit {
        putString(PREF_KEY_ACCESS_TOKEN, accessToken)
    }
    override fun getCountryCode(): String = mPrefs.getString(PREF_KEY_COUNTRY_CODE, "US")?:"US"

    override fun setCountryCode(value: String) = mPrefs.edit {
        putString(PREF_KEY_COUNTRY_CODE, value)
    }
    override fun getLoginData(): String? = mPrefs.getString(PREF_KEY_LOGIN_DATA, null)

    override fun setLoginData(data: String?) = mPrefs.edit {
        putString(PREF_KEY_LOGIN_DATA, data)
    }

    override fun getDeviceId(): String = mPrefs.getString(PREF_KEY_DEVICE_ID, "").toString()

    override fun setDeviceId(id: String) = mPrefs.edit {
        putString(PREF_KEY_DEVICE_ID, id)
    }

    fun getDefaultTTSPitch(): Float {
        return getCustomParamFloat(KEY_DEFAULT_TTS_PITCH, 1.0f)
    }

    fun getDefaultTTSSpeed(): Float {
        return getCustomParamFloat(KEY_DEFAULT_TTS_SPEECH, 0.9f)
    }

}
