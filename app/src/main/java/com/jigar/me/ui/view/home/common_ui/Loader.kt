package com.jigar.me.ui.view.home.common_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jigar.me.ui.view.ab.AbBeadsLoader
import com.jigar.me.ui.view.ab.LocalAbColors

/** The app's loader: the three bobbing beads of the splash screen, over a soft veil that also blocks taps. */
@Composable
fun Loader() {
    val colors = LocalAbColors.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgPrimary.copy(alpha = 0.55f))
            .clickable(enabled = false) {},
    ) {
        AbBeadsLoader(Modifier.align(Alignment.Center))
    }
}
