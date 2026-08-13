package com.jigar.me.ui.view.home.screens.math_game_zone

import androidx.lifecycle.ViewModel
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.home.screens.math_game_zone.common.ZoneDailyChallenge
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// Tiny bridge so the nav graph can mark the daily challenge as played the
// moment a play screen opens.
@HiltViewModel
class ZoneDailyMarkerViewModel @Inject constructor(
    private val prefManager: AppPreferencesHelper
) : ViewModel() {
    fun onRouteVisited(route: String) = ZoneDailyChallenge.onRouteVisited(route, prefManager)

    // Also lets screens without their own prefs bump a solved counter.
    fun markSolved(gameKey: String, tier: String) =
        com.jigar.me.ui.view.home.screens.math_game_zone.common.ZoneSolveCounter.increment(prefManager, gameKey, tier)

    // Lets screens without their own prefs bump the weekly parent-report counter.
    fun recordWeekly(problems: Int) =
        com.jigar.me.utils.WeeklySummaryManager.record(prefManager, problems)

    fun solvedCount(gameKey: String, tier: String): Int =
        com.jigar.me.ui.view.home.screens.math_game_zone.common.ZoneSolveCounter.solved(prefManager, gameKey, tier)
}
