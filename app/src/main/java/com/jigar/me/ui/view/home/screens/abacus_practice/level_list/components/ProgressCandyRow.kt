package com.jigar.me.ui.view.home.screens.abacus_practice.level_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens2
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens6
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8

@Composable
fun ProgressCandyRow(
    completed: Int,
    total: Int,
    colors: List<Color>
) {
    val progress =
        if (total > 0) completed.toFloat() / total.toFloat()
        else 0f

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .weight(1f)
                .height(Dimens6)
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.2f))
        ) {

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(colors)
                    )
            )
        }

        Spacer(modifier = Modifier.width(Dimens8))

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    Brush.horizontalGradient(colors)
                )
                .padding(
                    horizontal = Dimens8,
                    vertical = Dimens2
                )
        ) {
            Text(
                text = "$completed/$total",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall.scaled(),
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}