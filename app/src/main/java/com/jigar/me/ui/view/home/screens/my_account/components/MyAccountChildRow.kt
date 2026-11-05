package com.jigar.me.ui.view.home.screens.my_account.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.ab.AB_SF_TO_VECTOR
import com.jigar.me.ui.view.ab.AbChevron
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbShrinkText
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abSquash
import com.jigar.me.ui.view.home.screens.my_account.viewmodels.MyAccountMenu

/** One row of an account card: an orange icon, the title and a chevron on a soft plate that squashes when pressed. */
@Composable
fun MyAccountChildRow(
    item: MyAccountMenu,
    onClick: (String) -> Unit
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val shape = RoundedCornerShape(AbShapes.md * s)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .abSquash(cornerRadius = AbShapes.md * s, edgeHeight = 0.dp) { onClick(item.tag) }
            .background(colors.surfaceTint.copy(alpha = if (colors.isDark) 0.35f else 0.55f), shape)
            .heightIn(min = 44.dp * s)
            .padding(horizontal = 14.dp * s),
        horizontalArrangement = Arrangement.spacedBy(10.dp * s),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(26.dp * s), contentAlignment = Alignment.Center) {
            item.menuIcon?.let {
                Icon(it, null, Modifier.size((15f * AB_SF_TO_VECTOR).dp * s), tint = colors.accentOrange)
            }
        }

        AbShrinkText(
            text = item.menuTitle,
            style = AbTypography.body(s).copy(fontWeight = FontWeight.SemiBold),
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
            minScale = 0.7f,
            maxLines = 1
        )

        AbChevron(colors.textSecondary, Modifier.size(18.dp * s), pointingLeft = false)
    }
}
