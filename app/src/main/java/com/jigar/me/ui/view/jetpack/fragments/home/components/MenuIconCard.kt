package com.jigar.me.ui.view.jetpack.fragments.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.core.presentation.theme.ColorPrimary
import com.jigar.me.ui.view.jetpack.core.presentation.theme.ColorPrimaryLight

@Composable
fun MenuIconCard(
    type: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ColorPrimaryLight,
    iconTint: Color = ColorPrimary,
    onClick: (String) -> Unit
) {
    Card(
        onClick = { onClick(type) },
        shape = RoundedCornerShape(dimensionResource(R.dimen.menu_icons_corner)),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.size(dimensionResource(R.dimen.menu_icons_bg))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = type,
                tint = iconTint
            )
        }
    }
}
