package com.jigar.me.ui.view.home.screens.settings.components

import androidx.compose.animation.core.animateFloatAsState
import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimary
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimaryLight
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.screens.settings.viewmodels.ToggleItem
import kotlin.collections.forEachIndexed

@Composable
fun ToggleSection(
    modifier: Modifier = Modifier,
    items: List<ToggleItem>
) {
    Card(
        shape = RoundedCornerShape(AppDimens.Dimens16),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)), // ✅ same as before
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimens.Dimens3),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(vertical = AppDimens.Dimens6)) { // 🔥 reduced

            items.forEach { item ->
                SettingsSwitchRow(
                    icon = item.icon,
                    label = item.label,
                    isOn = item.isOn,
                    onToggle = item.onToggle
                )
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

    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        label = ""
    )

    val bgColor = if (isOn)
        Color(0xFFE8F5E9) // ✅ same green highlight
    else
        Color.White

    Row(
        modifier = Modifier
            .padding(horizontal = AppDimens.Dimens10, vertical = AppDimens.Dimens4) // 🔥 reduced
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(AppDimens.Dimens12))
            .background(bgColor)
            .clickable(
                interactionSource = interaction,
                indication = null
            ) {
                onToggle(!isOn)
            }
            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens4), // 🔥 reduced
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 🔥 ONLY change: bigger icon
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isOn) Color(0xFF43A047) else Color(0xFF5D4037),
            modifier = Modifier.size(AppDimens.Dimens24) // 👈 increased
        )

        Spacer(modifier = Modifier.width(AppDimens.Dimens8))

        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge.scaled().copy(
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily(Font(R.font.font_medium)),
                color = if (isOn) Color(0xFF2E7D32) else Color(0xFF5D4037)
            ),
            maxLines = 1
        )

        Switch(
            checked = isOn,
            onCheckedChange = onToggle,
            modifier = Modifier.scale(0.8f), // 👈 slightly smaller
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF2E7D32),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFBDBDBD)
            )
        )
    }
}


