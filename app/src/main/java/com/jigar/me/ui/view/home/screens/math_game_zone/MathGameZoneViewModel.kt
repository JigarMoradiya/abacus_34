package com.jigar.me.ui.view.home.screens.math_game_zone

import androidx.lifecycle.ViewModel
import com.jigar.me.data.pref.AppPreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

// Surfaces each game's saved progress on its zone card (total stars for
// roadmap games, best score for session games) and remembers the last games
// played for the "Jump back in" row.
@HiltViewModel
class MathGameZoneViewModel @Inject constructor(
    private val prefManager: AppPreferencesHelper
) : ViewModel() {

    private val difficulties = listOf("easy", "medium", "hard", "veryHard")

    private val _recentGames = MutableStateFlow<List<GameCategoryType>>(emptyList())
    val recentGames: StateFlow<List<GameCategoryType>> = _recentGames

    private val _badges = MutableStateFlow<Map<GameCategoryType, String>>(emptyMap())
    val badges: StateFlow<Map<GameCategoryType, String>> = _badges

    private val _daily = MutableStateFlow<DailyChallenge?>(null)
    val daily: StateFlow<DailyChallenge?> = _daily

    init { refresh() }

    // Re-read prefs — runs again when the zone comes back into composition,
    // so fresh wins show up right after a game.
    fun refresh() {
        _recentGames.value = prefManager.getCustomParam(KEY_RECENT, "")
            .split(",")
            .mapNotNull { name -> GameCategoryType.entries.firstOrNull { it.name == name } }
            .take(3)

        // Daily challenge: the featured game rotates through all 18 by UTC
        // day, identical on both platforms. Missing a day breaks the streak.
        val today = System.currentTimeMillis() / DAY_MS
        val featured = GameCategoryType.entries[(today % GameCategoryType.entries.size).toInt()]
        val lastDay = prefManager.getCustomParam(KEY_DAILY_DAY, "").toLongOrNull() ?: -1L
        var streak = prefManager.getCustomParamInt(KEY_DAILY_STREAK, 0)
        if (lastDay < today - 1) streak = 0
        _daily.value = DailyChallenge(featured, playedToday = lastDay == today, streak = streak)

        _badges.value = buildMap {
            starBadge(GameCategoryType.CROSS_MATH, "crossMathStars")
            starBadge(GameCategoryType.NUMBER_PATH, "numberPathStars")
            starBadge(GameCategoryType.KAKURO, "kakuroStars")
            bestBadge(GameCategoryType.SPEED_COMPARE, "speedCompareBest")
            bestBadge(GameCategoryType.BALLOON_POP, "balloonMakeTenBest")
            bestBadge(GameCategoryType.TRUE_FALSE, "trueFalseBest")
            bestBadge(GameCategoryType.MATH_BINGO, "mathBingoBest")
            bestBadge(GameCategoryType.MISSING_OPERATOR, "missingOperatorBest")
            bestBadge(GameCategoryType.EQUATION_MATCH, "equationMatchBest")
            bestBadge(GameCategoryType.MAGIC_SQUARE, "magicSquareBest")
            bestBadge(GameCategoryType.MERGE_2048, "merge2048Best")
            bestBadge(GameCategoryType.PLACE_VALUE, "placeValueBest")
            bestBadge(GameCategoryType.CLOCK_MASTER, "clockMasterBest")
            bestBadge(GameCategoryType.CALCUDOKU, "calcudokuBest")
            // Target Number, Math Pyramid, Sudoku and Number Sequence don't
            // persist scores today — no badge for them.
        }
    }

    fun recordPlay(type: GameCategoryType) {
        val updated = (listOf(type) + _recentGames.value).distinct().take(3)
        _recentGames.value = updated
        prefManager.setCustomParam(KEY_RECENT, updated.joinToString(",") { it.name })

        // Ever-played set feeds the Trophy Room (Explorer / All-Rounder).
        val played = prefManager.getCustomParam(KEY_PLAYED, "")
            .split(",").filter { it.isNotBlank() }.toMutableSet()
        if (played.add(type.name)) {
            prefManager.setCustomParam(KEY_PLAYED, played.joinToString(","))
        }

        // The daily streak is NOT updated here — opening a game's home page
        // isn't playing. ZoneDailyChallenge.onRouteVisited marks it when a
        // play screen actually opens; refresh() shows it on return.
    }

    // Sum of stars across all four tiers (CSV of 50 levels per tier).
    private fun MutableMap<GameCategoryType, String>.starBadge(type: GameCategoryType, keyPrefix: String) {
        val total = difficulties.sumOf { tier ->
            prefManager.getCustomParam("${keyPrefix}_$tier", "")
                .split(",").sumOf { it.trim().toIntOrNull() ?: 0 }
        }
        if (total > 0) put(type, "⭐ $total")
    }

    // Highest best score across the four difficulties.
    private fun MutableMap<GameCategoryType, String>.bestBadge(type: GameCategoryType, keyPrefix: String) {
        val top = difficulties.maxOf { prefManager.getCustomParamInt("${keyPrefix}_$it", 0) }
        if (top > 0) put(type, "🏆 $top")
    }

    companion object {
        private const val KEY_RECENT = "gameZoneRecentGames"
        private const val KEY_PLAYED = "gameZonePlayedGames"
        private const val KEY_DAILY_DAY = "dailyChallengeLastDay"
        private const val KEY_DAILY_STREAK = "dailyChallengeStreak"
        private const val DAY_MS = 86_400_000L
    }
}

// Today's featured game + whether it's already been played + streak length.
data class DailyChallenge(
    val type: GameCategoryType,
    val playedToday: Boolean,
    val streak: Int
)
