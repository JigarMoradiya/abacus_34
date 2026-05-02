package com.jigar.me.ui.view.base.abacus_base.components

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
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
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.base.abacus_base.AbacusBottomLabel
import com.jigar.me.ui.view.base.abacus_base.AbacusDimensionModel
import com.jigar.me.ui.view.base.abacus_base.ColorPresets.hex

@Composable
fun OnlyWordStripBar(
    dim: AbacusDimensionModel,
    totalWidth: Dp,
    totalHeight: Dp
) {
    val beadWidth = dim.beadWidth + (dim.columnSpaces * 2)

    Column(
        modifier = Modifier
            .width(totalWidth) // inner width (same as Swift)
            .padding(horizontal = dim.rectLineWidth),   // equal top/bottom padding
    ) {

        Spacer(modifier = Modifier.height(totalHeight + AppDimens.Dimens4))

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
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = hex(label.colorHex),
                        lineHeight = label.fontSize.scaled(),
                        fontSize = label.fontSize.scaled(),
                    ),
                    textAlign = TextAlign.Center,
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
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = hex("F57C00"),
                        fontSize = 10.sp.scaled(),
                        lineHeight = 10.sp.scaled(),
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    "Unit Rod",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 9.sp.scaled(),
                        lineHeight = 10.sp.scaled(),
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
