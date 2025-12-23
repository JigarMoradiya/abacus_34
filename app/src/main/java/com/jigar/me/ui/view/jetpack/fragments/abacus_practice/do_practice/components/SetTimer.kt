package com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.components

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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.viewmodels.AbacusDoPracticeUiState
import com.jigar.me.utils.DateTimeUtils

@Composable
fun SetTimer(uiState: AbacusDoPracticeUiState) {
    uiState.currentSetTime?.let { seconds ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(end = dimensionResource(R.dimen.activity_padding16))
                .background(colorResource(R.color.back_icon_bg), RoundedCornerShape(6.dp))
                .padding(horizontal = dimensionResource(R.dimen.activity_padding8), vertical = dimensionResource(R.dimen.activity_padding4))
        ) {
            Icon(
                imageVector = Icons.Outlined.Timer, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp)
            )

            Spacer(Modifier.width(dimensionResource(R.dimen.activity_padding6)))

            Text(
                text = DateTimeUtils.displayDurationHourMinSec(seconds), color = Color.White,
                fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily(Font(R.font.font_bold))
            )
        }
    }
}
