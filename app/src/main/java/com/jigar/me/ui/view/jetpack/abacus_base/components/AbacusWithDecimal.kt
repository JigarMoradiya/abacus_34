package com.jigar.me.ui.view.jetpack.abacus_base.components

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.data.local.data.RodMovement
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusTheme

@Composable
fun AbacusWithDecimal(
    numberOfColumns: Int,
    abacusData: AbacusCalculations,
    rodMovements: List<RodMovement>,
    showDirectionHints: Boolean,
    showHighlighter: Boolean,
    selectedTheme: String,
    modifier: Modifier = Modifier,
    screenType: String,
    isFreeModeOn: Boolean = false, // default
) {
    val dim = AbacusTheme.dimensionPreset(screenType = screenType,isFreeModeOn)
    val totalWidth = (dim.beadWidth * numberOfColumns) + (dim.rectLineWidth * 2) + (dim.columnSpaces * (numberOfColumns) * 2)
    val totalHeight = (dim.beadHeight * 7) + (dim.rectLineWidth * 2) + (dim.extraSpace * 2) + dim.beamHeight

    val strokeBrush = if (selectedTheme == "poligon_rainbow") {
        Brush.verticalGradient(
            listOf(
                Color(0xFFD7CCC8),
                Color(0xFFE0E0E0),
                Color(0xFFCFD8DC)
            )
        )
    } else {
        val preset = AbacusTheme.colorPreset(selectedTheme)
        Brush.verticalGradient(
            listOf(
                preset.abacusTopGradient,
                preset.abacusCenterGradient,
                preset.abacusBottomGradient
            )
        )
    }

    val textColor = if (selectedTheme == "poligon_rainbow") {
        Color(0xFF5D4037)
    } else {
        Color.White
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center   // whole abacus centered
    ) {


        // 2️⃣ RODS INSIDE FRAME (CENTERED)
        Row(
            modifier = Modifier
                .height(totalHeight)
                .width(totalWidth) // inner width (same as Swift)
                .padding(vertical = dim.rectLineWidth)     // equal top/bottom padding
                .padding(horizontal = dim.rectLineWidth)     // equal top/bottom padding
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            for (col in 0 until numberOfColumns) {
                val movement = rodMovements.firstOrNull { it.rodIndex == col }?.movement
                ColumnViewCompose(
                    imageName = selectedTheme,
                    columnNumber = col,
                    isRedDotPresent = col == numberOfColumns - 1 ||
                            col == numberOfColumns - 4 ||
                            col == numberOfColumns - 7 ||
                            col == numberOfColumns - 10 ||
                            col == numberOfColumns - 13,
                    isCentralColumn = (col == 6),
                    abacusData = abacusData,
                    movement = movement,
                    showDirection = showDirectionHints,
                    beadWidth = dim.beadWidth,
                    beadHeight = dim.beadHeight,
                    totalHeight = totalHeight,
                    beamHeight = dim.beamHeight,
                    extraSpace = dim.extraSpace,
                    columnSpaces = dim.columnSpaces,
                    showHighlighter = showHighlighter,
                    currentSpot = null
                )
            }
        }

        // 1️⃣ Frame
        Box(
            modifier = Modifier
                .width(totalWidth - (dim.columnSpaces * 2))
                .height(totalHeight)
                .border(
                    width = dim.rectLineWidth,
                    brush = strokeBrush,
                    shape = RoundedCornerShape(dim.rectLineCorner)
                )
        )

        // 2️⃣ Text perfectly centered INSIDE frame width
        Box(
            modifier = Modifier.height(totalHeight)
                .align(Alignment.TopCenter)
        ) {
            Text(modifier = Modifier.height(dim.rectLineWidth),
                text = abacusData.displayValue,
                color = textColor,
                fontSize = dim.textSizeSp.sp,
                lineHeight = dim.textSizeSp.sp,
                fontFamily = FontFamily(Font(R.font.font_extra_bold))
            )
        }
    }

    // 🔢 NUMBER STRIP BAR (outside box, below)
    Log.e("jigarLogs","isFreeModeOn = "+isFreeModeOn)
    if (isFreeModeOn){
        NumberStripBar(
            dim = dim,
            totalWidth = totalWidth,
            totalHeight = totalHeight
        )
    }
}

