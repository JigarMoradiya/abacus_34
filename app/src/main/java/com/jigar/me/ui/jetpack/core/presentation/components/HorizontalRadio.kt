package com.jigar.me.ui.jetpack.core.presentation.components

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled

@Composable
fun HorizontalRadio(
    text: String,
    selected: Boolean,
    type: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable { onSelected(type) }
            .padding(AppDimens.Dimens4),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            if (DeviceInfo.isTablet) AppDimens.Dimens8 else AppDimens.Dimens4
        )
    ) {
        RadioButton(selected = selected, onClick = null,modifier = Modifier.scale(
            if (DeviceInfo.isTablet) 1.4f else 1f
        ))

        Text(
            text = text,
            style = (if (DeviceInfo.isTablet) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge).scaled().copy(
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily(Font(R.font.font_semibold))
            ),
            textAlign = TextAlign.Start
        )
    }
}
