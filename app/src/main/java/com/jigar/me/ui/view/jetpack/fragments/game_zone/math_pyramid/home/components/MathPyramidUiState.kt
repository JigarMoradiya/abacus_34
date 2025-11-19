package com.jigar.me.ui.view.jetpack.fragments.game_zone.math_pyramid.home.components

import android.os.Parcelable
import com.jigar.me.ui.view.jetpack.fragments.common.enums.CommonDifficulty4
import kotlinx.parcelize.Parcelize


@Parcelize
data class MathPyramidUiState(
    val selectedLevel: Int = 2,
    val selectedDifficulty: CommonDifficulty4 = CommonDifficulty4.easy
) : Parcelable
