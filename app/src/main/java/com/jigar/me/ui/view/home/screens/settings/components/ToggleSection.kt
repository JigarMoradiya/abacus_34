package com.jigar.me.ui.view.home.screens.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimary
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimaryLight
import com.jigar.me.ui.view.home.screens.settings.viewmodels.ToggleItem
import kotlin.collections.forEachIndexed

@Composable
fun ToggleSection(
    modifier: Modifier = Modifier,
    title: String,
    items: List<ToggleItem>
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ColorPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column {
            items.forEachIndexed { index, item ->
                SettingsSwitchRow(
                    icon = item.icon,
                    label = item.label,
                    isOn = item.isOn,
                    onToggle = item.onToggle
                )
                if (index != items.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun SettingsSwitchRow(
    icon: ImageVector,
    label: String,
    isOn: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.activity_padding16),
                vertical = 0.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Black,
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black, fontWeight = FontWeight.Medium,fontFamily = FontFamily(Font(R.font.font_medium))),
        )

        Switch(
            modifier = Modifier.scale(0.9f),
            checked = isOn,
            onCheckedChange = onToggle,
        )
    }
}


