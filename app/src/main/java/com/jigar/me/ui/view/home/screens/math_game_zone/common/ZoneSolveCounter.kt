package com.jigar.me.ui.view.home.screens.math_game_zone.common

import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4

// Per-tier solved-puzzle counters for games that have no score/star system
// (Sudoku, Number Sequence, Math Pyramid, Target Number). Keyed by tier —
// same idea as "best score per difficulty" on the other game homes — so a
// kid (or parent) can tell exactly which difficulty was actually beaten,
// not just a combined total. The zone card badge sums across all tiers.
object ZoneSolveCounter {
    const val TARGET_NUMBER = "targetNumberSolved"
    const val SUDOKU = "sudokuSolved"
    const val NUMBER_SEQUENCE = "numberSequenceSolved"
    const val MATH_PYRAMID = "mathPyramidSolved"

    fun increment(prefManager: AppPreferencesHelper, gameKey: String, tier: String) {
        val key = "${gameKey}_$tier"
        prefManager.setCustomParamInt(key, prefManager.getCustomParamInt(key, 0) + 1)
    }

    fun increment(prefManager: AppPreferencesHelper, gameKey: String, difficulty: CommonDifficulty4) =
        increment(prefManager, gameKey, difficulty.name)

    fun solved(prefManager: AppPreferencesHelper, gameKey: String, tier: String): Int =
        prefManager.getCustomParamInt("${gameKey}_$tier", 0)

    fun solved(prefManager: AppPreferencesHelper, gameKey: String, difficulty: CommonDifficulty4): Int =
        solved(prefManager, gameKey, difficulty.name)

    // Total across every tier this game has ever used — feeds the zone badge.
    fun total(prefManager: AppPreferencesHelper, gameKey: String, tiers: List<String>): Int =
        tiers.sumOf { solved(prefManager, gameKey, it) }
}
