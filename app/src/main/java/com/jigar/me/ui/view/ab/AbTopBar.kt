package com.jigar.me.ui.view.ab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/** Landscape header of restyled screens: chunky back circle + a chunky title pill, optional trailing slot. */
@Composable
fun AbTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable RowScope.() -> Unit = {},
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp * s), verticalAlignment = Alignment.CenterVertically) {
        AbBackButton(onClick = onBack)
        Box(
            Modifier
                .abChunkyFace(colors.brandPrimary, colors.brandPrimaryDark, null, AbShapes.pill, 5.dp * s)
                .heightIn(min = 44.dp * s)
                .padding(horizontal = 20.dp * s),
            contentAlignment = Alignment.Center,
        ) {
            Text(title, style = AbTypography.headline(s), color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.weight(1f))
        trailing()
    }
}
