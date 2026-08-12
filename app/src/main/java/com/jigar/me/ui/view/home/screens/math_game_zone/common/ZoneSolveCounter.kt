package com.jigar.me.ui.view.home.screens.math_game_zone.common

import com.jigar.me.data.pref.AppPreferencesHelper

// Lifetime solved-puzzle counters for games that have no score/star system
// (Sudoku, Number Sequence, Math Pyramid, Target Number) — feeds the 🧩
// badge on their zone cards.
object ZoneSolveCounter {
    const val TARGET_NUMBER = "targetNumberSolved"
    const val SUDOKU = "sudokuSolved"
    const val NUMBER_SEQUENCE = "numberSequenceSolved"
    const val MATH_PYRAMID = "mathPyramidSolved"

    fun increment(prefManager: AppPreferencesHelper, key: String) {
        prefManager.setCustomParamInt(key, prefManager.getCustomParamInt(key, 0) + 1)
    }
}
