package com.jigar.me.utils

import android.app.Activity
import android.util.Log
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManagerFactory
import com.jigar.me.MyApplication
import com.jigar.me.data.pref.AppPreferencesHelper

object AppReviewManager {

    private const val TAG = "ReviewGate"

    // Nothing asks for a review on the first day: the app must have been installed for at least this long. Counting calendar days alone is not
    // enough (an install at 23:50 and a second visit at 00:10 is "day 2"), and a restored backup can bring old counters back on a new install.
    private const val MIN_INSTALL_AGE_MS = 20L * 60 * 60 * 1000

    /** Pure, for tests. [installedMillis] 0 = unknown (an app that cannot tell is not blocked). */
    fun isPastFirstDay(installedMillis: Long, nowMillis: Long): Boolean =
        installedMillis <= 0L || nowMillis - installedMillis >= MIN_INSTALL_AGE_MS

    private fun installedLongEnough(): Boolean {
        val installed = runCatching {
            val app = MyApplication.getInstance()
            app.packageManager.getPackageInfo(app.packageName, 0).firstInstallTime
        }.getOrDefault(0L)
        val ok = isPastFirstDay(installed, System.currentTimeMillis())
        if (!ok) Log.d(TAG, "review ask: NO — installed less than a day ago (firstInstallTime=$installed)")
        return ok
    }

    // True when the gate is allowed to show: a new milestone has been reached
    // (by total active days) and it hasn't already been shown today.
    fun shouldShowReviewGate(prefs: AppPreferencesHelper): Boolean {
        if (!installedLongEnough()) return false
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
        // Advance immediately so a force-quit can't cause the gate to re-show
        // the next day. Button taps call advanceMilestone() again — harmless
        // since totalActiveDays hasn't changed within the same session.
        advanceMilestone(prefs)
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

    // MARK: - Day-2 review ask
    // A separate, one-time-only prompt shown the first time the child has used
    // the app on 2 distinct days -- independent of the milestone gate above so
    // it never re-fires and never interferes with the day 3/7/15/... gate.
    // Two thumbs: thumb up opens the Play Store listing directly (handled by the
    // caller, which owns the URL); thumb down opens the feedback page, whose
    // answer is written to support by mail (the caller opens it). Tapping outside
    // just closes it. This is a deliberate product decision, made with the
    // "review gating" policy risk (Google/Apple) called out and accepted.
    fun shouldShowDay2Review(prefs: AppPreferencesHelper): Boolean {
        if (!installedLongEnough()) return false
        if (prefs.getCustomParamBoolean(AppConstants.Review.day2ReviewShown, false)) {
            return false
        }
        val totalActiveDays = prefs.getCustomParamInt(AppConstants.Streak.totalActiveDays, 0)
        val eligible = totalActiveDays >= 2
        Log.d(TAG, "shouldShowDay2Review: $eligible — totalActiveDays=$totalActiveDays")
        return eligible
    }

    // Stamped the instant the sheet is decided to show -- like onGateShown() for the
    // milestone gate -- so a force-quit/kill before the user picks a thumb or dismisses
    // it can't leave the flag unset and cause the one-time sheet to re-show later.
    fun onDay2ReviewShown(prefs: AppPreferencesHelper) {
        Log.d(TAG, "onDay2ReviewShown: stamping day2ReviewShown immediately")
        prefs.setCustomParamBoolean(AppConstants.Review.day2ReviewShown, true)
    }

    fun onDay2ReviewLike(prefs: AppPreferencesHelper) {
        Log.d(TAG, "onDay2ReviewLike: stamping day2ReviewShown, opening Play Store")
        prefs.setCustomParamBoolean(AppConstants.Review.day2ReviewShown, true)
        AppAnalytics.log(AppAnalytics.DAY2_REVIEW_LIKE)
    }

    fun onDay2ReviewDislike(prefs: AppPreferencesHelper) {
        Log.d(TAG, "onDay2ReviewDislike: stamping day2ReviewShown, opening the feedback page")
        prefs.setCustomParamBoolean(AppConstants.Review.day2ReviewShown, true)
        AppAnalytics.log(AppAnalytics.DAY2_REVIEW_DISLIKE)
    }

    // The day-2 answer led somewhere (the store after thumb up, the feedback mail after thumb down): the recurring "Enjoying the App?" ask
    // of day 3 and 7 would only ask the same parent again, so it waits until day 15.
    fun deferGateToDay15(prefs: AppPreferencesHelper) {
        val current = prefs.getCustomParamInt(AppConstants.Review.nextMilestoneDay, AppConstants.Review.milestones.first())
        if (current == AppConstants.Review.exhausted) return
        val totalActiveDays = prefs.getCustomParamInt(AppConstants.Streak.totalActiveDays, 0)
        val next = AppConstants.Review.milestones.firstOrNull { it >= 15 && it > totalActiveDays } ?: AppConstants.Review.exhausted
        val resolved = if (next == AppConstants.Review.exhausted) next else maxOf(current, next)
        Log.d(TAG, "deferGateToDay15: nextMilestoneDay $current -> $resolved")
        prefs.setCustomParamInt(AppConstants.Review.nextMilestoneDay, resolved)
    }

    fun onDay2ReviewDismissed(prefs: AppPreferencesHelper) {
        Log.d(TAG, "onDay2ReviewDismissed: stamping day2ReviewShown (silent)")
        prefs.setCustomParamBoolean(AppConstants.Review.day2ReviewShown, true)
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
