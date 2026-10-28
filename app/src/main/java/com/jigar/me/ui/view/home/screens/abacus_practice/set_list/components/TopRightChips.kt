package com.jigar.me.ui.view.home.screens.abacus_practice.set_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.jigar.me.R
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorCoffee
import com.jigar.me.ui.view.ab.AbChip
import com.jigar.me.ui.view.home.theme.AppDimens

/** The legend of the three answer modes (and, on the page list, the timed-set clock): chunky kit chips. */
@Composable
fun TopRightChips(isShowTimeChip: Boolean = true) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AbChip(text = stringResource(R.string.step_by_step), face = colorResource(R.color.step_by_step_answer))
        AbChip(text = stringResource(R.string.final_answer), face = colorResource(R.color.final_answer))
        AbChip(text = stringResource(R.string.formal_exam), face = colorResource(R.color.formal_exam))
        if (isShowTimeChip) {
            AbChip(text = stringResource(R.string.timer_on), face = ColorCoffee, icon = Icons.Outlined.Timer)
        }
    }
}
