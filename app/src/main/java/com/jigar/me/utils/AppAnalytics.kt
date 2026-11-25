package com.jigar.me.utils

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

/**
 * Funnel measurement. Parent-level and anonymous: no child names, no ad identifiers (ad-ID collection and the ad consent defaults
 * are switched off in the manifest). Every call does nothing useful until Analytics is enabled for the Firebase project and the app
 * is built with the matching google-services.json. Never throws.
 */
object AppAnalytics {
    const val PAYWALL_VIEWED = "paywall_viewed"
    const val PAYWALL_DISMISSED = "paywall_dismissed"
    const val TUTORIAL_STEP = "tutorial_step"
    const val TUTORIAL_SKIPPED = "tutorial_skipped"
    const val TUTORIAL_FINISHED = "tutorial_finished"
    const val GATE_SHOWN = "paywall_gate_shown"
    const val GATE_PASSED = "paywall_gate_passed"
    const val PLAN_SELECTED = "plan_selected"
    const val LOGIN_PROMPTED = "login_prompted"
    const val LOGIN_COMPLETED = "login_completed"
    const val PURCHASE_STARTED = "purchase_started"
    const val PURCHASE_COMPLETED = "purchase_completed"
    const val PURCHASE_CANCELLED = "purchase_cancelled"
    const val PURCHASE_NOT_ACTIVE = "purchase_not_active"
    const val PURCHASE_FAILED = "purchase_failed"
    const val FREEMIUM_WINDOW_STARTED = "freemium_window_started"
    const val FREEMIUM_GRANDFATHERED = "freemium_grandfathered"
    const val FREEMIUM_WINDOW_ENDED = "freemium_window_ended"
    const val FREEMIUM_REMINDER_SHOWN = "freemium_reminder_shown"
    const val FREEMIUM_REMINDER_GATE_PASSED = "freemium_reminder_gate_passed"
    const val FREEMIUM_LOCKED_TAP = "freemium_locked_tap"
    const val RETURNING_PAYWALL_SHOWN = "returning_paywall_shown"
    const val PAYWALL_PLANS_LOADED = "paywall_plans_loaded"
    const val BILLING_ISSUE_SHOWN = "billing_issue_shown"
    const val BILLING_ISSUE_FIX_TAPPED = "billing_issue_fix_tapped"
    const val SHARE_APP = "share_app"
    const val DAY2_REVIEW_LIKE = "day2_review_like"
    const val DAY2_REVIEW_DISLIKE = "day2_review_dislike"
    const val FEEDBACK_SENT = "feedback_sent"
    const val LEVEL1_COMPLETE_SHOWN = "level1_complete_shown"
    const val LEVEL1_COMPLETE_SEE_LEVEL2 = "level1_complete_see_level2"
    const val LEVEL1_COMPLETE_GATE_PASSED = "level1_complete_gate_passed"
    const val LEVEL1_COMPLETE_GO_LEVEL2 = "level1_complete_go_level2"
    const val LEVEL1_COMPLETE_NOT_NOW = "level1_complete_not_now"
    const val LEVEL1_COMPLETE_CHIP_TAPPED = "level1_complete_chip_tapped"
    /** the restore at launch (once per RevenueCat customer, and once a week on a phone without Premium): result "ok" or "failed", weekly 1 = the weekly look (a failed first restore is tried again at the next launch) */
    const val RESTORE_LAUNCH = "restore_launch"
    // Level 2: the card for a free user who finished the free chapters, and the celebration for a subscriber who finished every chapter
    const val LEVEL2_FREE_CARD_SHOWN = "level2_free_card_shown"
    const val LEVEL2_FREE_CARD_SEE_FORMULAS = "level2_free_card_see_formulas"
    const val LEVEL2_FREE_CARD_GATE_PASSED = "level2_free_card_gate_passed"
    const val LEVEL2_FREE_CARD_SUBSCRIBED = "level2_free_card_subscribed"
    const val LEVEL2_FREE_CARD_NOT_NOW = "level2_free_card_not_now"
    const val LEVEL2_COMPLETE_SHOWN = "level2_complete_shown"
    const val LEVEL2_COMPLETE_DONE = "level2_complete_done"
    const val LEVEL2_COMPLETE_CHIP_TAPPED = "level2_complete_chip_tapped"

    /** A user property (for example the freemium group), so results can be compared per group in Firebase. */
    fun setUserProperty(name: String, value: String) {
        if (com.jigar.me.BuildConfig.DEBUG) android.util.Log.d("AppAnalytics", "user_property $name=$value")
        runCatching { Firebase.analytics.setUserProperty(name, value.take(36)) }
    }

    /** Values are put as Long, Double or String (Firebase limits: 40-char names, 100-char values). */
    fun log(event: String, vararg params: Pair<String, Any>) {
        if (com.jigar.me.BuildConfig.DEBUG) {
            android.util.Log.d("AppAnalytics", event + " " + params.sortedBy { it.first }.joinToString(" ") { it.first + "=" + it.second })
        }
        runCatching {
            val bundle = Bundle()
            for ((key, value) in params) {
                when (value) {
                    is Boolean -> bundle.putLong(key, if (value) 1L else 0L)
                    is Int -> bundle.putLong(key, value.toLong())
                    is Long -> bundle.putLong(key, value)
                    is Float -> bundle.putDouble(key, value.toDouble())
                    is Double -> bundle.putDouble(key, value)
                    else -> bundle.putString(key, value.toString().take(100))
                }
            }
            Firebase.analytics.logEvent(event, bundle)
        }
    }
}
