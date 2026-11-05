package com.jigar.me.ui.view.home.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbSettingRow
import com.jigar.me.ui.view.ab.AbSwitch
import com.jigar.me.ui.view.home.screens.settings.viewmodels.ToggleItem

/** A card that holds a column of setting rows. */
@Composable
fun ToggleSection(
    modifier: Modifier = Modifier,
    items: List<ToggleItem>
) {
    AbGlassPanel(modifier, padding = 10.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
    AbSettingRow(label = label, icon = icon) { AbSwitch(isOn = isOn, onChange = onToggle) }
}
