package com.jigar.me.ui.view.home.common_ui.sheets

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.utils.AppReviewManager
import kotlinx.coroutines.launch

// Drives an auto-trigger review gate from any screen: call `attempt(prefs) { ... }`
// at a "strong result" moment instead of running the continuation directly.
// If the gate isn't eligible to show, the continuation runs immediately.
class ReviewGateController internal constructor() {
    var visible by mutableStateOf(false)
        internal set
    internal var onResolved: (() -> Unit)? = null

    fun attempt(prefs: AppPreferencesHelper, onDone: () -> Unit) {
        Log.d("ReviewGate", "attempt() called from a trigger site")
        if (AppReviewManager.shouldShowReviewGate(prefs)) {
            AppReviewManager.onGateShown(prefs)
            onResolved = onDone
            visible = true
        } else {
            onDone()
        }
    }
}

@Composable
fun rememberReviewGateController(): ReviewGateController = remember { ReviewGateController() }

// Renders the sheet driven by [controller]. Place once per screen alongside the
// trigger call sites that invoke `controller.attempt(prefs) { ... }`.
@Composable
fun ReviewGateHost(controller: ReviewGateController, prefs: AppPreferencesHelper) {
    val activity = LocalContext.current as? Activity
    val scope = rememberCoroutineScope()

    fun resolve(positive: Boolean) {
        val continuation = controller.onResolved
        controller.onResolved = null
        controller.visible = false
        if (positive && activity != null) {
            scope.launch { AppReviewManager.onGatePositive(prefs, activity) }
        } else {
            AppReviewManager.onGateNegative(prefs)
        }
        continuation?.invoke()
    }

    ReviewGateBottomSheet(
        visible = controller.visible,
        onDismiss = { resolve(false) },
        onNegative = { resolve(false) },
        onPositive = { resolve(true) }
    )
}
