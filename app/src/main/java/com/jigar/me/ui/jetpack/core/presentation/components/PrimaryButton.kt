package com.jigar.me.ui.jetpack.core.presentation.components

import com.jigar.me.ui.view.home.theme.AppDimens

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
        shape = RoundedCornerShape(AppDimens.Dimens50),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = AppDimens.Dimens8,
            pressedElevation = AppDimens.Dimens4
        ),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        contentPadding = PaddingValues(
            horizontal = AppDimens.Dimens16,
            vertical = AppDimens.Dimens8
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
        shape = RoundedCornerShape(AppDimens.Dimens50),
        color = if (enabled) color else color.copy(alpha = 0.4f),
        shadowElevation = AppDimens.Dimens8,
        onClick = onClick,
        enabled = enabled
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = AppDimens.Dimens12,
                vertical = AppDimens.Dimens6
            ),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.font_bold))
            ),
            color = Color.White
        )
    }
}

