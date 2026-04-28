package com.jigar.me.ui.view.home.common_ui.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens2
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens6
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.getButtonColors
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled


@Composable
fun KidsLabel(txt: String,type: ButtonType = ButtonType.PURPLE) {
    val colors = getButtonColors(type)
    Box(
        modifier = Modifier
            .padding(end = Dimens16)
    ) {

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(colors.gradient)
                .padding(horizontal = Dimens12, vertical = Dimens6)
        ) {

            Box(
                modifier = Modifier
                    .matchParentSize()
            ) {

                // top right
                Box(
                    modifier = Modifier
                        .size(Dimens12)
                        .align(Alignment.TopEnd)
                        .offset(x = Dimens6, y = -Dimens2) // small offset only
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            CircleShape
                        )
                )


                // small dot
                Box(
                    modifier = Modifier
                        .size(Dimens8)
                        .align(Alignment.CenterEnd)
                        .offset(x = Dimens8)
                        .background(
                            Color.White.copy(alpha = 0.25f),
                            CircleShape
                        )
                )
            }

            Text(
                text = txt,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium.scaled(),
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}