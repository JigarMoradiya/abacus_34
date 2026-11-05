package com.jigar.me.ui.view.home.screens.math_game_zone.trophy_room

import androidx.lifecycle.ViewModel
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.home.screens.math_game_zone.common.ZoneBuddy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

// Everything the Trophy Room needs, computed live from the same prefs the
// games already write.
data class ZoneStats(
    val totalStars: Int = 0,      // all stars across the 3 roadmap games
    val levelsDone: Int = 0,      // levels with at least 1 star
    val threeStars: Int = 0,      // levels with a perfect 3
    val hardThreeStarCount: Int = 0, // 3-star levels on hard/veryHard
    val maxBest: Int = 0,         // highest session best score anywhere
    val gamesPlayed: Int = 0,     // distinct games ever opened
    // Levels beaten on exactly this tier — easy alone can't fill these, so
    // they push kids toward Medium/Hard/Very Hard specifically.
    val mediumLevelsDone: Int = 0,
    val hardLevelsDone: Int = 0,
    val veryHardLevelsDone: Int = 0
) {
    val hardThreeStar: Boolean get() = hardThreeStarCount > 0
}

@HiltViewModel
class TrophyRoomViewModel @Inject constructor(
    private val prefManager: AppPreferencesHelper
) : ViewModel() {

    private val _stats = MutableStateFlow(ZoneStats())
    val stats: StateFlow<ZoneStats> = _stats

    private val _buddy = MutableStateFlow(ZoneBuddy.current(prefManager))
    val buddy: StateFlow<String> = _buddy

    init { refresh() }

    fun selectBuddy(emoji: String) {
        prefManager.setCustomParam(ZoneBuddy.KEY, emoji)
        _buddy.value = emoji
    }

    fun refresh() {
        val starPrefixes = listOf("crossMathStars", "numberPathStars", "kakuroStars")
        val tiers = listOf("easy", "medium", "hard", "veryHard")
        val hardTiers = setOf("hard", "veryHard")

        var total = 0; var done = 0; var perfect = 0; var hardPerfect = 0
        var mediumDone = 0; var hardDone = 0; var veryHardDone = 0
        for (prefix in starPrefixes) for (tier in tiers) {
            val stars = prefManager.getCustomParam("${prefix}_$tier", "")
                .split(",").mapNotNull { it.trim().toIntOrNull() }
            total += stars.sum()
            done += stars.count { it > 0 }
            perfect += stars.count { it >= 3 }
            if (tier in hardTiers) hardPerfect += stars.count { it >= 3 }
            when (tier) {
                "medium" -> mediumDone += stars.count { it > 0 }
                "hard" -> hardDone += stars.count { it > 0 }
                "veryHard" -> veryHardDone += stars.count { it > 0 }
            }
        }

        val bestPrefixes = listOf(
            "speedCompareBest", "balloonMakeTenBest", "trueFalseBest", "mathBingoBest",
            "missingOperatorBest", "equationMatchBest", "magicSquareBest", "merge2048Best",
            "placeValueBest", "clockMasterBest", "calcudokuBest"
        )
        val maxBest = bestPrefixes.maxOf { prefix ->
            tiers.maxOf { prefManager.getCustomParamInt("${prefix}_$it", 0) }
        }

        val played = prefManager.getCustomParam("gameZonePlayedGames", "")
            .split(",").filter { it.isNotBlank() }.distinct().size

        _stats.value = ZoneStats(
            totalStars = total,
            levelsDone = done,
            threeStars = perfect,
            hardThreeStarCount = hardPerfect,
            maxBest = maxBest,
            gamesPlayed = played,
            mediumLevelsDone = mediumDone,
            hardLevelsDone = hardDone,
            veryHardLevelsDone = veryHardDone
        )
    }
}
