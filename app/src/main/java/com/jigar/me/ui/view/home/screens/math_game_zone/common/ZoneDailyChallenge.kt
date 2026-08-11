package com.jigar.me.ui.view.home.screens.math_game_zone.common

import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.home.screens.math_game_zone.GameCategoryType

// The daily challenge only counts when the kid actually STARTS a round of
// today's featured game (a play screen opens) — just peeking at a game's
// home page never moves the streak.
object ZoneDailyChallenge {
    const val KEY_DAY = "dailyChallengeLastDay"
    const val KEY_STREAK = "dailyChallengeStreak"
    const val DAY_MS = 86_400_000L

    fun today(): Long = System.currentTimeMillis() / DAY_MS

    fun featuredToday(): GameCategoryType =
        GameCategoryType.entries[(today() % GameCategoryType.entries.size).toInt()]

    // Play-route prefix (before the first "/") -> game.
    private val playRouteToType = mapOf(
        "SpeedComparePlay" to GameCategoryType.SPEED_COMPARE,
        "SpeedCompareDuel" to GameCategoryType.SPEED_COMPARE,
        "BalloonPopPlay" to GameCategoryType.BALLOON_POP,
        "TrueFalsePlay" to GameCategoryType.TRUE_FALSE,
        "MathBingoPlay" to GameCategoryType.MATH_BINGO,
        "MissingOperatorPlay" to GameCategoryType.MISSING_OPERATOR,
        "EquationMatchPlay" to GameCategoryType.EQUATION_MATCH,
        "CrossMathPlay" to GameCategoryType.CROSS_MATH,
        "TargetNumberPlay" to GameCategoryType.TARGET_NUMBER,
        "NumberPathPlay" to GameCategoryType.NUMBER_PATH,
        "MathPyramidPlay" to GameCategoryType.MATH_PYRAMID,
        "MagicSquarePlay" to GameCategoryType.MAGIC_SQUARE,
        "Merge2048Play" to GameCategoryType.MERGE_2048,
        "NumberSequencePuzzlePlay" to GameCategoryType.NUMBER_SEQUENCE_PUZZLE,
        "PlaceValuePlay" to GameCategoryType.PLACE_VALUE,
        "ClockMasterPlay" to GameCategoryType.CLOCK_MASTER,
        "SudokuPlay" to GameCategoryType.SUDOKU,
        "CalcudokuPlay" to GameCategoryType.CALCUDOKU,
        "KakuroPlay" to GameCategoryType.KAKURO
    )

    // Called for every navigation destination; no-op unless it's a play
    // screen of today's featured game and today isn't already counted.
    fun onRouteVisited(route: String, prefManager: AppPreferencesHelper) {
        val type = playRouteToType[route.substringBefore("/")] ?: return
        if (type != featuredToday()) return
        val today = today()
        val lastDay = prefManager.getCustomParam(KEY_DAY, "").toLongOrNull() ?: -1L
        if (lastDay == today) return
        val newStreak = if (lastDay == today - 1) prefManager.getCustomParamInt(KEY_STREAK, 0) + 1 else 1
        prefManager.setCustomParam(KEY_DAY, today.toString())
        prefManager.setCustomParamInt(KEY_STREAK, newStreak)
    }
}
