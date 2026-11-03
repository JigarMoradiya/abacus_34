package com.jigar.me.ui.view.home.screens.math_game_zone.common

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/** A line a view model chooses (a hint, a warning) but that is written in the app language only when the screen draws it. */
class GameText(@StringRes val id: Int, vararg val args: Any)

@Composable
fun GameText.asString(): String = stringResource(id, *args)
