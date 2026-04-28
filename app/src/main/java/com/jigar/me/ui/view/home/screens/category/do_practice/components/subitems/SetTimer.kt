package com.jigar.me.ui.view.home.screens.category.do_practice.components.subitems

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.home.screens.category.do_practice.viewmodels.AbacusDoPracticeUiState
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.DateTimeUtils

@Composable
fun SetTimer(uiState: AbacusDoPracticeUiState) {
    uiState.currentSetTime?.let { seconds ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(end = AppDimens.Dimens16)
                .background(colorResource(R.color.back_icon_bg), RoundedCornerShape(AppDimens.Dimens6))
                .padding(horizontal = AppDimens.Dimens8, vertical = AppDimens.Dimens4)
        ) {
            Icon(
                imageVector = Icons.Outlined.Timer, contentDescription = null, tint = Color.White, modifier = Modifier.size(AppDimens.Dimens16)
            )

            Spacer(Modifier.width(AppDimens.Dimens6))

            Text(
                text = DateTimeUtils.displayDurationHourMinSec(seconds), color = Color.White,
                fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily(Font(R.font.font_bold))
            )
        }
    }
}
