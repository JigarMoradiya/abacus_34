package com.jigar.me.ui.view.home.navigation

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

/**
 * Double-tap–safe navigation. Kids tap fast: two quick taps on a back button
 * fire popBackStack() twice — the second pop removes Home too, leaving the
 * NavHost with an EMPTY back stack (blank page showing only HomePageBackground).
 * After the first pop/navigate starts, the current entry leaves RESUMED, so
 * ignoring calls while not RESUMED swallows exactly the duplicate tap.
 *
 * Do NOT use these for programmatic navigation (deep links, splash handoff) —
 * those must never be dropped.
 */
fun NavController.safePopBackStack() {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        popBackStack()
    }
}

fun NavController.safeNavigate(route: String, builder: NavOptionsBuilder.() -> Unit = {}) {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        navigate(route, builder)
    }
}
