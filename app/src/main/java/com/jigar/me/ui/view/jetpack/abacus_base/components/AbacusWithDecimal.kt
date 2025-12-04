package com.jigar.me.ui.view.jetpack.abacus_base.components

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.data.local.data.RodMovement
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusTheme
import com.jigar.me.ui.view.jetpack.fragments.abacus_free_mode.AbacusFreeModeScreen
import com.jigar.me.utils.AppConstants

@Composable
fun AbacusWithDecimal(
    numberOfColumns: Int,
    abacusData: AbacusCalculations,
    rodMovements: List<RodMovement>,
    showDirectionHints: Boolean,
    showHighlighter: Boolean,
    selectedTheme: String,
    screenType: String,
    isFreeModeOn: Boolean = false,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    onReset: () -> Unit,
    onNext: () -> Unit,
) {
    val dim = remember(screenType, isFreeModeOn) {
        AbacusTheme.dimensionPreset(screenType = screenType, isFreeModeOn)
    }
    val totalWidth = (dim.beadWidth * numberOfColumns) + (dim.rectLineWidth * 2) + (dim.columnSpaces * (numberOfColumns) * 2)
    val totalHeight = (dim.beadHeight * 7) + (dim.rectLineWidth * 2) + (dim.extraSpace * 2) + dim.beamHeight

    val strokeBrush = remember(selectedTheme) {
        if (selectedTheme == "poligon_rainbow") {
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
    }

    val textColor = if (selectedTheme == "poligon_rainbow") {
        Color(0xFF5D4037)
    } else {
        Color.White
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter   // whole abacus centered
    ) {

        // ⬆️ Answer Bar ONLY when free mode is OFF
        if (!isFreeModeOn) {
            val offsetY = (-48).dp

            AbacusAnswerBarCompose(
                answer = abacusData.displayValue,
                screenType = screenType,
                abacusType = null,
                isDisplayAbacusNumber = true,
                onReset = {
                    abacusData.resetAbacusData()
                },
                onNext = { /* next logic */ },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = offsetY)
            )

        }

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
                key(col) {
                    ColumnViewCompose(
                        columnNumber = col,
                        imageName = selectedTheme,
                        isRedDotPresent = col == numberOfColumns - 1 ||
                                col == numberOfColumns - 4 ||
                                col == numberOfColumns - 7 ||
                                col == numberOfColumns - 10 ||
                                col == numberOfColumns - 13,
                        isCentralColumn = (col == 6),
                        abacusData = abacusData,
                        movement = rodMovements.firstOrNull { it.rodIndex == col }?.movement,
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

        if (screenType == AppConstants.AbacusScreen.screenTypeFreeMode && isFreeModeOn){
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
    }

    // 🔢 NUMBER STRIP BAR (outside box, below)
    if (screenType == AppConstants.AbacusScreen.screenTypeFreeMode && isFreeModeOn){
        NumberStripBar(
            dim = dim,
            totalWidth = totalWidth,
            totalHeight = totalHeight
        )
    }
}


@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 780,
    heightDp = 400
)
@Composable
fun PreviewAbacusFreeModeScreen1() {
    MaterialTheme {
        AbacusFreeModeScreen()
    }
}