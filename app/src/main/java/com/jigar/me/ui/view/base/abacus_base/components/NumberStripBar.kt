package com.jigar.me.ui.view.base.abacus_base.components

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.ui.view.base.abacus_base.AbacusBottomLabel
import com.jigar.me.ui.view.base.abacus_base.AbacusDimensionModel
import com.jigar.me.ui.view.base.abacus_base.ColorPresets.hex

@Composable
fun NumberStripBar(
    dim: AbacusDimensionModel,
    totalWidth: Dp,
    totalHeight: Dp
) {
    val beadWidth = dim.beadWidth + (dim.columnSpaces * 2)
    val stripHeight = dim.stripHeight     // same as iOS UIConstants.abacusNumberStripTextSize

    Column(
        modifier = Modifier
            .width(totalWidth) // inner width (same as Swift)
            .padding(horizontal = dim.rectLineWidth)     // equal top/bottom padding
    ) {

        // 🔵 TOP BAR (5000000..5)
        Row(
            modifier = Modifier
                .width(totalWidth),
            horizontalArrangement = Arrangement.Start
        ) {
            val items = listOf(
                Triple("5000000", "303F9F", 7.sp),
                Triple("500000", "1976D2", 8.sp),
                Triple("50000", "0097A7", 9.sp),
                Triple("5000", "00796B", 10.sp),
                Triple("500", "388E3C", 12.sp),
                Triple("50", "A4B42B", 12.sp),
                Triple("5", "F57C00", 12.sp)
            )

            items.forEachIndexed { index, item ->
                Box(
                    modifier = Modifier
                        .width(beadWidth)
                        .height(stripHeight)
                        .background(hex(item.second)),
                    contentAlignment = Alignment.Center    // ⭐ THIS CENTERS TEXT VERTICALLY + HORIZONTALLY
                ) {
                    Text(
                        text = item.first,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = item.third,
                        lineHeight = item.third,  // match line height to font
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(totalHeight + stripHeight + AppDimens.Dimens8))

    }

    //         🔵 MIDDLE BAR (1000000..1)
    Column(
        modifier = Modifier
            .width(totalWidth) // inner width (same as Swift)
            .padding(horizontal = dim.rectLineWidth),   // equal top/bottom padding
    ) {

        Spacer(modifier = Modifier.height(totalHeight + (stripHeight * 2) + AppDimens.Dimens20))

        Row(
            modifier = Modifier
                .width(totalWidth),
            horizontalArrangement = Arrangement.Start
        ) {
            val items = listOf(
                Triple("1000000", "303F9F", 7.sp),
                Triple("100000", "1976D2", 8.sp),
                Triple("10000", "0097A7", 9.sp),
                Triple("1000", "00796B", 10.sp),
                Triple("100", "388E3C", 12.sp),
                Triple("10", "A4B42B", 12.sp),
                Triple("1", "F57C00", 12.sp)
            )

            items.forEachIndexed { index, item ->
                Box(
                    modifier = Modifier
                        .width(beadWidth)
                        .height(stripHeight)
                        .background(hex(item.second)),
                    contentAlignment = Alignment.Center    // ⭐ THIS CENTERS TEXT VERTICALLY + HORIZONTALLY
                ) {
                    Text(
                        text = item.first,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = item.third,
                        lineHeight = item.third,  // match line height to font
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(AppDimens.Dimens4))
        // 🔵 BOTTOM LABEL BAR
        Row(
            modifier = Modifier.width(totalWidth),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val abacusBottomLabels = listOf(
                AbacusBottomLabel("Ten\nLakhs", "303F9F", 9.sp, 2),
                AbacusBottomLabel("Lakhs", "1976D2", 10.sp, 1),
                AbacusBottomLabel("Ten\nThounsands", "F57C00", 6.sp, 2),
                AbacusBottomLabel("Thounsands", "00796B", 6.sp, 1),
                AbacusBottomLabel("Hundreds", "388E3C", 8.sp, 1),
                AbacusBottomLabel("Tens", "A4B42B", 10.sp, 1)
            )
            abacusBottomLabels.forEachIndexed { index, label ->
                Text(
                    text = label.textKey,
                    modifier = Modifier
                        .width(beadWidth)
                    ,
                    fontWeight = FontWeight.Bold,
                    color = hex(label.colorHex),
                    textAlign = TextAlign.Center,
                    lineHeight = label.fontSize,
                    fontSize = label.fontSize,
                )
            }

            // "Ones • Unit Rod"
            Column(
                modifier = Modifier
                    .width(beadWidth),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Ones",
                    fontWeight = FontWeight.Bold,
                    color = hex("F57C00"),
                    fontSize = 10.sp,
                    lineHeight = 10.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Unit Rod",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 9.sp,
                    lineHeight = 10.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
