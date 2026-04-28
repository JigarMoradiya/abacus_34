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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.jigar.me.R

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
            AppDimens.Dimens4
        )
    ) {
        RadioButton(selected = selected, onClick = null)

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily(Font(R.font.font_semibold))
            ),
            textAlign = TextAlign.Start
        )
    }
}
