package com.jigar.me.ui.view.jetpack.fragments.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.fragments.common.enums.CommonDifficulty4


@Composable
fun CommonDifficultySelectorCompose(
    selected: CommonDifficulty4,
    onSelect: (CommonDifficulty4) -> Unit
) {
    Row(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.75f), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CommonDifficulty4.entries.forEach { d ->
            val isSelected = d == selected
            val shape = RoundedCornerShape(20.dp)
            // Surface renders the elevation (shadow). Do NOT clip the Surface itself.
            Surface(
                modifier = Modifier
                    .padding(horizontal = 4.dp),
                shape = shape,
                color = if (isSelected) colorResource(R.color.colorEditTextBlack_33) else Color.White,
                shadowElevation = if (isSelected) 8.dp else 0.dp, // elevation visible because Surface is not clipped
                tonalElevation = if (isSelected) 4.dp else 0.dp,
                border = if (!isSelected) BorderStroke(1.dp, Color.LightGray) else null
            ) {
                // Clip and clickable are applied INSIDE Surface so ripple is rounded,
                // but Surface remains unclipped so shadow renders.
                Box(
                    modifier = Modifier
                        .clip(shape) // <-- clipped here so ripple gets rounded bounds
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current
                        ) {
                            onSelect(d)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = d.displayName,
                        fontSize = if (isSelected)
                            dimensionResource(id = R.dimen.textSizeSuperExtraLarge).value.sp
                        else
                            dimensionResource(id = R.dimen.textSizeRegular).value.sp,
                        color = if (isSelected) Color.White else colorResource(R.color.black_text),
                        fontFamily = FontFamily(Font(if (isSelected) R.font.font_bold else R.font.font_regular))
                    )
                }
            }
        }
    }
}