package com.jigar.me.ui.view.home.screens.abacus_practice.set_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.jigar.me.R
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorCoffee
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.theme.AppDimens


@Composable
fun TopRightChips(isShowTimeChip : Boolean = true) {
    Row(
        modifier = Modifier
            .padding(top = AppDimens.Dimens12)
            .padding(end = AppDimens.Dimens16),
        verticalAlignment = Alignment.CenterVertically
    ) {

        ChipLabel(
            text = stringResource(R.string.step_by_step),
            backgroundColor = colorResource(R.color.step_by_step_answer)
        )

        Spacer(modifier = Modifier.width(AppDimens.Dimens8))

        ChipLabel(
            text = stringResource(R.string.final_answer),
            backgroundColor = colorResource(R.color.final_answer)
        )

        Spacer(modifier = Modifier.width(AppDimens.Dimens8))

        ChipLabel(
            text = stringResource(R.string.formal_exam),
            backgroundColor = colorResource(R.color.formal_exam)
        )

        if (isShowTimeChip){
            Spacer(modifier = Modifier.width(AppDimens.Dimens8))
            TimerChip()
        }
    }
}

@Composable
fun ChipLabel(
    text: String,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .shadow(
                elevation = AppDimens.Dimens2,
                shape = RoundedCornerShape(AppDimens.Dimens12),
                clip = false
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(AppDimens.Dimens12)
            )
            .padding(
                horizontal = AppDimens.Dimens8,
                vertical = AppDimens.Dimens4
            )
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall.scaled().copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.font_bold)),
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            )
        )
    }
}

@Composable
fun TimerChip() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .shadow(
                elevation = AppDimens.Dimens2,
                shape = RoundedCornerShape(AppDimens.Dimens12),
                clip = false
            )
            .background(
                color = ColorCoffee,
                shape = RoundedCornerShape(AppDimens.Dimens12)
            )
            .padding(
                horizontal = AppDimens.Dimens8,
                vertical = AppDimens.Dimens4
            )
    ) {

        Box(
            modifier = Modifier
                .size(AppDimens.Dimens18)
                .background(
                    Color.White.copy(alpha = 0.2f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Timer,
                contentDescription = null,
                modifier = Modifier.size(AppDimens.Dimens12),
                tint = Color.White
            )
        }

        Text(
            text = stringResource(R.string.timer_on),
            color = Color.White,
            modifier = Modifier.padding(start = AppDimens.Dimens4),
            style = MaterialTheme.typography.bodySmall.scaled().copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.font_bold)),
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            )
        )
    }
}