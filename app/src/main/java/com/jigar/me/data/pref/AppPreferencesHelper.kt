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
        val lastSyncTime  = getCustomParam(Constants.last_sync_time, Constants.last_sync_default_time)
        val bgMusicVolume = getCustomParamInt(AppConstants.Settings.Setting_bg_music_volume, AppConstants.Settings.Setting_bg_music_volume_default)
        // Streak is device-level habit data — preserve across logout
        val streakCurrent  = getCustomParamInt(AppConstants.Streak.currentStreak, 0)
        val streakLongest  = getCustomParamInt(AppConstants.Streak.longestStreak, 0)
        val streakLastDate = getCustomParam(AppConstants.Streak.lastActivityDate, "")
        val streakTotal    = getCustomParamInt(AppConstants.Streak.totalActiveDays, 0)
        val streakShields  = getCustomParamInt(AppConstants.Streak.streakShields, 0)
        val streakClaimed  = getCustomParam(AppConstants.Streak.claimedRewards, "")
        // Notification UX state — device-level, preserve across logout
        val notifPermAsked     = getCustomParamBoolean(AppConstants.Notifications.permissionAsked, false)
        val notifSheetLastDate = getCustomParam(AppConstants.Notifications.sheetLastShownDate, "")
        // Review gate — device-level, preserve across logout (mirrors Streak.*
        // above; otherwise a logout+login cycle wipes "already asked"/"already
        // answered day 2" while totalActiveDays survives, re-firing both gates)
        val reviewNextMilestone = getCustomParamInt(AppConstants.Review.nextMilestoneDay, AppConstants.Review.milestones.first())
        val reviewLastAskDate   = getCustomParam(AppConstants.Review.lastAskDate, "")
        val reviewDay2Shown     = getCustomParamBoolean(AppConstants.Review.day2ReviewShown, false)
        // Timed Home offer window — device-level; otherwise logout+login would restart it
        val homeOfferStartedAt   = getCustomParam(AppConstants.HomeOffer.startedAt, "")
        val homeOfferStartedName = getCustomParam(AppConstants.HomeOffer.startedName, "")
        // Freemium time limit -- device-level; otherwise logout+login would restart or lift the free window
        val fmStartAt    = getCustomParam(AppConstants.Freemium.startAt, "")
        val fmGrand      = getCustomParamBoolean(AppConstants.Freemium.grandfathered, false)
        val fmRegistered = getCustomParamBoolean(AppConstants.Freemium.registered, false)
        val fmReminders  = getCustomParamInt(AppConstants.Freemium.remindersShown, 0)
        val fmEnded      = getCustomParamBoolean(AppConstants.Freemium.endedLogged, false)
        val fmDays       = getCustomParamInt(AppConstants.RemoteConfig.freemiumDays, 0)
        val fmCore       = getCustomParamInt(AppConstants.RemoteConfig.freeCoreAfterWindow, 2)
        val rpFirstSeen  = getCustomParam(AppConstants.Freemium.firstSeenAt, "")
        val rpShown      = getCustomParam(AppConstants.Freemium.returningShown, "")
        val rpLastPrompt = getCustomParam(AppConstants.Freemium.lastPromptDay, "")
        val rpLastPaywall = getCustomParam(AppConstants.Freemium.lastPaywallDay, "")
        val rpDays       = getCustomParam(AppConstants.RemoteConfig.returningPaywallDays, "")
        val pwOpenCount  = getCustomParamInt(AppConstants.Freemium.paywallOpenCount, 0)
        val billingDay   = getCustomParam(AppConstants.Freemium.billingIssueDay, "")
        val lifetimeMin  = getCustomParamInt(AppConstants.RemoteConfig.lifetimeMinPaywalls, 0)
        val onboardingSeen = getCustomParamBoolean("ab_onboarding_seen", false)   // the first-launch tutorial is shown once per device
        mPrefs.edit { clear() }
        setCustomParam(Constants.last_sync_time, lastSyncTime)
        setCustomParamInt(AppConstants.Settings.Setting_bg_music_volume, bgMusicVolume)
        setCustomParamInt(AppConstants.Streak.currentStreak, streakCurrent)
        setCustomParamInt(AppConstants.Streak.longestStreak, streakLongest)
        setCustomParam(AppConstants.Streak.lastActivityDate, streakLastDate)
        setCustomParamInt(AppConstants.Streak.totalActiveDays, streakTotal)
        setCustomParamInt(AppConstants.Streak.streakShields, streakShields)
        setCustomParam(AppConstants.Streak.claimedRewards, streakClaimed)
        setCustomParamBoolean(AppConstants.Notifications.permissionAsked, notifPermAsked)
        setCustomParam(AppConstants.Notifications.sheetLastShownDate, notifSheetLastDate)
        setCustomParamInt(AppConstants.Review.nextMilestoneDay, reviewNextMilestone)
        setCustomParam(AppConstants.Review.lastAskDate, reviewLastAskDate)
        setCustomParamBoolean(AppConstants.Review.day2ReviewShown, reviewDay2Shown)
        setCustomParam(AppConstants.HomeOffer.startedAt, homeOfferStartedAt)
        setCustomParam(AppConstants.HomeOffer.startedName, homeOfferStartedName)
        setCustomParam(AppConstants.Freemium.startAt, fmStartAt)
        setCustomParamBoolean(AppConstants.Freemium.grandfathered, fmGrand)
        setCustomParamBoolean(AppConstants.Freemium.registered, fmRegistered)
        setCustomParamInt(AppConstants.Freemium.remindersShown, fmReminders)
        setCustomParamBoolean(AppConstants.Freemium.endedLogged, fmEnded)
        setCustomParamInt(AppConstants.RemoteConfig.freemiumDays, fmDays)
        setCustomParamInt(AppConstants.RemoteConfig.freeCoreAfterWindow, fmCore)
        setCustomParam(AppConstants.Freemium.firstSeenAt, rpFirstSeen)
        setCustomParam(AppConstants.Freemium.returningShown, rpShown)
        setCustomParam(AppConstants.Freemium.lastPromptDay, rpLastPrompt)
        setCustomParam(AppConstants.Freemium.lastPaywallDay, rpLastPaywall)
        setCustomParam(AppConstants.RemoteConfig.returningPaywallDays, rpDays)
        setCustomParamInt(AppConstants.Freemium.paywallOpenCount, pwOpenCount)
        setCustomParam(AppConstants.Freemium.billingIssueDay, billingDay)
        setCustomParamInt(AppConstants.RemoteConfig.lifetimeMinPaywalls, lifetimeMin)
        setCustomParamBoolean("ab_onboarding_seen", onboardingSeen)
    }
}
