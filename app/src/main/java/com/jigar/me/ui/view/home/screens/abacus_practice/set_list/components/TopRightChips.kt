package com.jigar.me.ui.view.home.screens.abacus_practice.set_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
        // each chip gets a share of the row in proportion to its own width (text + padding + clock icon): when everything fits, nothing
        // shrinks (English), and a long translation shrinks every chip a little instead of one chip a lot or cutting it
        val chips = buildList<Triple<String, Color, ImageVector?>> {
            add(Triple(stringResource(R.string.step_by_step), colorResource(R.color.step_by_step_answer), null))
            add(Triple(stringResource(R.string.final_answer), colorResource(R.color.final_answer), null))
            add(Triple(stringResource(R.string.formal_exam), colorResource(R.color.formal_exam), null))
            if (isShowTimeChip) add(Triple(stringResource(R.string.timer_on), ColorCoffee, Icons.Outlined.Timer))
        }
        for ((text, face, icon) in chips) {
            AbChip(text = text, face = face, icon = icon, modifier = Modifier.weight((text.length + if (icon != null) 7 else 4).toFloat(), fill = false))
        }
    }
}
