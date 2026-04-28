package com.jigar.me.ui.jetpack.core.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimary

@Composable
fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = ColorPrimary,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(50.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = dimensionResource(R.dimen.activity_padding8),
            pressedElevation = dimensionResource(R.dimen.activity_padding4)
        ),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        contentPadding = PaddingValues(
            horizontal = dimensionResource(R.dimen.activity_padding16),
            vertical = dimensionResource(R.dimen.activity_padding8)
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.font_bold))
            )
        )
    }
}

@Composable
fun SecondaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = ColorPrimary,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50.dp),
        color = if (enabled) color else color.copy(alpha = 0.4f),
        shadowElevation = dimensionResource(R.dimen.activity_padding8),
        onClick = onClick,
        enabled = enabled
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.activity_padding12),
                vertical = dimensionResource(R.dimen.activity_padding6)
            ),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.font_bold))
            ),
            color = Color.White
        )
    }
}

