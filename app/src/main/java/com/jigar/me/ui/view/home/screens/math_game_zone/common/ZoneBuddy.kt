package com.jigar.me.ui.view.home.screens.math_game_zone.common

import com.jigar.me.data.pref.AppPreferencesHelper

// The kid's companion character. New buddies unlock with total stars and
// the chosen one walks the Number Path maze (and can join more games later).
data class BuddyDef(val emoji: String, val starsNeeded: Int)

object ZoneBuddy {
    const val KEY = "zoneBuddy"

    val all = listOf(
        BuddyDef("🐻", 0),
        BuddyDef("🐼", 25),
        BuddyDef("🦊", 75),
        BuddyDef("🦖", 150),
        BuddyDef("🦄", 300)
    )

    fun current(prefManager: AppPreferencesHelper): String =
        prefManager.getCustomParam(KEY, "🐻").ifBlank { "🐻" }
}
