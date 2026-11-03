package com.jigar.me.ui.view.ab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/**
 * Keeps its content left-to-right even when the app is shown in a right-to-left language (Arabic).
 *
 * Use it around anything where the order must never flip: sums and equations ("3 + 4 = 7"), the abacus with its rods and place values,
 * the answer bar and the number pads. Words, menus and buttons around it still mirror as usual.
 */
@Composable
fun LtrIsland(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr, content = content)
}
