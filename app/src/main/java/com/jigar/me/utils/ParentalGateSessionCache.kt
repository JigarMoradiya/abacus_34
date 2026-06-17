package com.jigar.me.utils

/**
 * Tracks whether the parental gate was passed for URL opens this foreground session.
 * Resets in HomeActivity.onPause() so it clears when the app goes to background.
 * Purchases and login always show the gate regardless of this cache.
 */
object ParentalGateSessionCache {
    var urlGatePassedThisSession: Boolean = false
        private set

    fun markPassed() { urlGatePassedThisSession = true }
    fun reset() { urlGatePassedThisSession = false }
}
