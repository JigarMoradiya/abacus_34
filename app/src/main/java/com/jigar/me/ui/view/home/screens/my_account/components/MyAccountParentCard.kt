package com.jigar.me.ui.view.home.screens.my_account.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.home.screens.my_account.viewmodels.MyAccountMenu

/** A titled card that holds a column of account rows. */
@Composable
fun MyAccountParentCard(
    menu: MyAccountMenu,
    onItemClick: (String) -> Unit,
    modifier: Modifier
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    AbGlassPanel(modifier, padding = 12.dp) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp * s)) {
            Text(
                text = menu.menuTitle,
                modifier = Modifier.padding(horizontal = 4.dp * s),
                style = AbTypography.headline(s),
                color = colors.textPrimary
            )

            menu.subMenu.forEach { child ->
                MyAccountChildRow(
                    item = child,
                    onClick = onItemClick
                )
            }
        }
    }
}
