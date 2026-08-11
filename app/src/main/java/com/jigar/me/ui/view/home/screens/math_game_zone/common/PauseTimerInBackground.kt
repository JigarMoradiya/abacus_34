package com.jigar.me.ui.view.home.screens.math_game_zone.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.LifecycleEventObserver

// Freezes a game's clock while the app is in the background (home button,
// phone call, task switcher) so an interruption never costs stars or time.
@Composable
fun PauseTimerInBackground(onPausedChange: (Boolean) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> onPausedChange(true)
                Lifecycle.Event.ON_RESUME -> onPausedChange(false)
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}
