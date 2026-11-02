package com.jigar.me.utils

import com.jigar.me.data.pref.AppPreferencesHelper

/**
 * A child did real practice (an answered question, a finished problem, a Free Mode session): the weekly parent report
 * ("This Week": problems and days) counts it. Mirrors iOS `WeeklyActivity`.
 */
object WeeklyActivity {
    fun record(prefs: AppPreferencesHelper, problems: Int = 1) {
        if (problems > 0) WeeklySummaryManager.record(prefs, problems)
    }
}
