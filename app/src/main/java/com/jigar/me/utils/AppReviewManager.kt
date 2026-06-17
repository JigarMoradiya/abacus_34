package com.jigar.me.utils

import android.app.Activity
import android.util.Log
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManagerFactory
import com.jigar.me.data.pref.AppPreferencesHelper

object AppReviewManager {

    private const val TAG = "ReviewGate"

    // True when the gate is allowed to show: a new milestone has been reached
    // (by total active days) and it hasn't already been shown today.
    fun shouldShowReviewGate(prefs: AppPreferencesHelper): Boolean {
        val lastAskDate = prefs.getCustomParam(AppConstants.Review.lastAskDate, "")
        val today = StreakManager.today()
        if (lastAskDate == today) {
            Log.d(TAG, "shouldShowReviewGate: NO — already asked today ($lastAskDate)")
            return false
        }

        val nextMilestone = prefs.getCustomParamInt(
            AppConstants.Review.nextMilestoneDay,
            AppConstants.Review.milestones.first()
        )
        if (nextMilestone == AppConstants.Review.exhausted) {
            Log.d(TAG, "shouldShowReviewGate: NO — milestones exhausted")
            return false
        }

        val totalActiveDays = prefs.getCustomParamInt(AppConstants.Streak.totalActiveDays, 0)
        val eligible = totalActiveDays >= nextMilestone
        Log.d(
            TAG,
            "shouldShowReviewGate: $eligible — totalActiveDays=$totalActiveDays, nextMilestone=$nextMilestone, lastAskDate='$lastAskDate', today=$today"
        )
        return eligible
    }

    fun onGateShown(prefs: AppPreferencesHelper) {
        val today = StreakManager.today()
        Log.d(TAG, "onGateShown: stamping lastAskDate=$today")
        prefs.setCustomParam(AppConstants.Review.lastAskDate, today)
    }

    // Milestone advances regardless of the answer so the gate doesn't
    // re-qualify every day once totalActiveDays passes it.
    fun onGateNegative(prefs: AppPreferencesHelper) {
        Log.d(TAG, "onGateNegative: 'Not too much' tapped")
        advanceMilestone(prefs)
    }

    suspend fun onGatePositive(prefs: AppPreferencesHelper, activity: Activity) {
        Log.d(TAG, "onGatePositive: 'Yes, Enjoying!' tapped")
        advanceMilestone(prefs)
        triggerNativeReview(activity)
    }

    private fun advanceMilestone(prefs: AppPreferencesHelper) {
        val totalActiveDays = prefs.getCustomParamInt(AppConstants.Streak.totalActiveDays, 0)
        val next = AppConstants.Review.milestones.firstOrNull { it > totalActiveDays }
        val resolved = next ?: AppConstants.Review.exhausted
        Log.d(TAG, "advanceMilestone: totalActiveDays=$totalActiveDays -> nextMilestoneDay=$resolved")
        prefs.setCustomParamInt(AppConstants.Review.nextMilestoneDay, resolved)
    }

    // Google's native in-app review flow. May silently no-op per Play's own quota rules.
    // NOTE for debug builds: Play's In-App Review API generally only renders the real
    // system dialog for apps installed via Play (production/internal/closed testing
    // tracks) with an account that hasn't hit the quota. A sideloaded/debug build will
    // usually complete this call without ever showing the dialog — that's expected,
    // not a bug. Watch Logcat with this tag to confirm the call actually ran.
    suspend fun triggerNativeReview(activity: Activity) {
        Log.d(TAG, "triggerNativeReview: requesting ReviewInfo...")
        try {
            val manager = ReviewManagerFactory.create(activity)
            val reviewInfo = manager.requestReview()
            Log.d(TAG, "triggerNativeReview: ReviewInfo obtained, launching review flow")
            manager.launchReview(activity, reviewInfo)
            Log.d(TAG, "triggerNativeReview: launchReview call completed (may be a no-op in debug builds)")
        } catch (e: Exception) {
            Log.d(TAG, "triggerNativeReview: failed/no-op — ${e.message}")
            // Review flow is best-effort; never let it crash the app.
        }
    }
}
