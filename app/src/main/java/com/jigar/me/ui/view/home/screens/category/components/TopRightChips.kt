package com.jigar.me.ui.view.home.screens.category.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.theme.AppDimens


@Composable
fun TopRightChips() {
    Row(Modifier
        .padding(top = AppDimens.Dimens12)
        .padding(end = AppDimens.Dimens16),
        verticalAlignment = Alignment.CenterVertically
    ) {

        ChipLabel(
            text = stringResource(R.string.step_by_step),
            backgroundColor = colorResource(R.color.step_by_step_answer)
        )

        Spacer(modifier = Modifier.width(AppDimens.Dimens10))

        ChipLabel(
            text = stringResource(R.string.final_answer),
            backgroundColor = colorResource(R.color.final_answer)
        )

        Spacer(modifier = Modifier.width(AppDimens.Dimens10))

        ChipLabel(
            text = stringResource(R.string.formal_exam),
            backgroundColor = colorResource(R.color.formal_exam)
        )

        Spacer(modifier = Modifier.width(AppDimens.Dimens12))

        TimerChip()
    }
}

@Composable
fun ChipLabel(
    text: String,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(AppDimens.Dimens6)
            )
            .padding(
                horizontal = AppDimens.Dimens6,
                vertical = AppDimens.Dimens2
            )
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall.scaled().copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.font_bold)),
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
        )
    }
}

@Composable
fun TimerChip() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = Color.Black,
                shape = RoundedCornerShape(AppDimens.Dimens6)
            )
            .padding(vertical = AppDimens.Dimens2)
            .padding(
                start = AppDimens.Dimens4,
                end = AppDimens.Dimens6,
            )
    ) {

        Icon(
            imageVector = Icons.Outlined.Timer,
            contentDescription = null,
            modifier = Modifier.size(AppDimens.Dimens16),
            tint = Color.White
        )

        Text(
            text = stringResource(R.string.timer_on),
            color = Color.White,
            modifier = Modifier.padding(start = AppDimens.Dimens4),
            style = MaterialTheme.typography.bodySmall.scaled().copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.font_bold)),
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
        )
    }
}