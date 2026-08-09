package com.jigar.me.ui.view.home.screens.math_game_zone.number_path.components

import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4

// Per-tier level progress: one prefs string per difficulty holding a CSV of
// 50 star counts ("3,2,1,0,..."), 0 = not completed. Same keys on iOS.
class NumberPathProgress(private val prefManager: AppPreferencesHelper) {

    private fun key(difficulty: CommonDifficulty4) = "numberPathStars_${difficulty.name}"

    fun stars(difficulty: CommonDifficulty4): IntArray {
        val raw = prefManager.getCustomParam(key(difficulty), "")
        val out = IntArray(NumberPathConfig.LEVEL_COUNT)
        if (raw.isNotEmpty()) {
            raw.split(",").forEachIndexed { i, v ->
                if (i < out.size) out[i] = v.toIntOrNull()?.coerceIn(0, 3) ?: 0
            }
        }
        return out
    }

    fun setStars(difficulty: CommonDifficulty4, level: Int, stars: Int) {
        val all = stars(difficulty)
        val i = level - 1
        if (i !in all.indices) return
        all[i] = maxOf(all[i], stars.coerceIn(0, 3))
        prefManager.setCustomParam(key(difficulty), all.joinToString(","))
    }

    fun isUnlocked(difficulty: CommonDifficulty4, level: Int): Boolean {
        if (level <= 1) return true
        val all = stars(difficulty)
        val prev = level - 2
        return prev in all.indices && all[prev] > 0
    }

    fun nextLevel(difficulty: CommonDifficulty4): Int {
        val all = stars(difficulty)
        val i = all.indexOfFirst { it == 0 }
        return if (i == -1) NumberPathConfig.LEVEL_COUNT else i + 1
    }
}
