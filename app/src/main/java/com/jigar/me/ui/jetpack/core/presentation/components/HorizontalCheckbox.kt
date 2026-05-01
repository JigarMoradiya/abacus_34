package com.jigar.me.ui.jetpack.core.presentation.components

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled


@Composable
fun HorizontalCheckbox(
    text: String,
    checked: Boolean,
    type: String,
    onCheckedChange: (String,Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable { onCheckedChange(type, !checked) }
            .padding(AppDimens.Dimens4),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4)
    ) {
        Checkbox(checked = checked, onCheckedChange = null)

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.scaled().copy(
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily(Font(R.font.font_semibold))
            ),
            textAlign = TextAlign.Start
        )
    }

}
