package com.jigar.me.ui.view.jetpack.fragments.other.setting.components.voice

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.jigar.me.R

@Composable
fun <T> SimpleDropdownField(
    modifier: Modifier = Modifier,
    text: String,
    items: List<T>,
    itemLabel: (T) -> String,
    onSelect: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var fieldWidth by remember { mutableIntStateOf(0) }

    Box(modifier = modifier) {

        OutlinedTextField(
            value = text,
            onValueChange = {},
            readOnly = true,
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    fieldWidth = it.size.width
                },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.ArrowDropDown,
                    contentDescription = null
                )
            }
        )

        // Invisible click layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    expanded = true
                }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            properties = PopupProperties(focusable = true),
            modifier = Modifier
                .width(with(LocalDensity.current) { fieldWidth.toDp() }) // ✅ SAME WIDTH
                .heightIn(max = 300.dp)
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = itemLabel(item),
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black,
                                fontWeight = FontWeight.Normal, fontFamily = FontFamily(Font(R.font.font_regular))),
                        )
                    },
                    onClick = {
                        onSelect(item)
                        expanded = false
                    },
                    modifier = Modifier.heightIn(max = 32.dp),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                    )
                )
            }
        }
    }
}
