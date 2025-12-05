package com.jigar.me.ui.view.jetpack.abacus_base.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.jigar.me.R
import com.jigar.me.data.local.data.RodMovement
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusTheme
import com.jigar.me.ui.view.jetpack.abacus_base.freeModeHighlightSteps
import com.jigar.me.ui.view.jetpack.fragments.abacus_free_mode.AbacusFreeModeScreen
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.MathUtils

@Composable
fun AbacusWithDecimal(
    numberOfColumns: Int,
    abacusData: AbacusCalculations,
    rodMovements: List<RodMovement>,
    onRodMovementChange: (List<RodMovement>) -> Unit,
    showDirectionHints: Boolean,
    onShowDirectionHintsChange: (Boolean) -> Unit,
    showHighlighter: Boolean,
    onShowHighlighterChange: (Boolean) -> Unit,
    selectedTheme: String,
    screenType: String,
    isFreeModeOn: Boolean = false,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    val dim = remember(screenType, isFreeModeOn) {
        AbacusTheme.dimensionPreset(screenType = screenType, isFreeModeOn)
    }
    val totalWidth = (dim.beadWidth * numberOfColumns) + (dim.rectLineWidth * 2) + (dim.columnSpaces * (numberOfColumns) * 2)
    val totalHeight = (dim.beadHeight * 7) + (dim.rectLineWidth * 2) + (dim.extraSpace * 2) + dim.beamHeight
    var currentSpot by remember { mutableStateOf<Int?>(-1) }
    val highlightSteps = freeModeHighlightSteps   // your array of 16 messages
    LaunchedEffect(showHighlighter) {
        currentSpot = if (showHighlighter) {
            0       // start tutorial
        } else {
            null    // stop tutorial
        }
    }

    val strokeBrush = remember(selectedTheme) {
        if (selectedTheme == "poligon_rainbow") {
            Brush.verticalGradient(listOf(Color(0xFFD7CCC8), Color(0xFFE0E0E0), Color(0xFFCFD8DC)))
        } else {
            val preset = AbacusTheme.colorPreset(selectedTheme)
            Brush.verticalGradient(listOf(preset.abacusTopGradient, preset.abacusCenterGradient, preset.abacusBottomGradient))
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

        Row(
            modifier = Modifier
                .height(totalHeight)
                .width(totalWidth)
                .alpha(if (currentSpot == 0) 0f else 1f) // frame highlight, hide all internal
                .padding(vertical = dim.rectLineWidth)
                .padding(horizontal = dim.rectLineWidth)
                .align(Alignment.Center)
                .spotlightTag(1, highlightSteps[1].message), // rod highlight
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
                        currentSpot = currentSpot
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
                    width = dim.rectLineWidth, brush = strokeBrush, shape = RoundedCornerShape(dim.rectLineCorner)
                )
                .spotlightTag(0, highlightSteps[0].message)
        )

        if (screenType == AppConstants.AbacusScreen.screenTypeFreeMode && isFreeModeOn){
            // 2️⃣ Text perfectly centered INSIDE frame width
            Box(
                modifier = Modifier
                    .height(totalHeight)
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


    if (currentSpot != null && currentSpot!! >= 0) {
        SpotlightOverlay(
            currentSpot = currentSpot,
            totalCount = highlightSteps.size,
            onNext = {
                val next = if (currentSpot == highlightSteps.lastIndex) null else currentSpot!! + 1
                currentSpot = next

                abacusData.resetAbacusData()
                onRodMovementChange(emptyList())

                if (next == null) {
                    abacusData.setAbacusValueFromString("0")
                    onShowDirectionHintsChange(false)
                    onShowHighlighterChange(false)
                    return@SpotlightOverlay
                }

                when (next) {
                    6, 9 -> abacusData.setAbacusValueFromString("9000000")
                    7 -> abacusData.setAbacusValueFromString("90000000")
                    8 -> abacusData.setAbacusValueFromString("900000000")
                    10 -> abacusData.setAbacusValueFromString("99000000")
                    11 -> abacusData.setAbacusValueFromString("999000000")

                    12 -> {
                        val list = MathUtils().calculateRodMovements(from = 0, to = 4, rods = 1, isForRightRods = false)
                        onRodMovementChange(list)
                        onShowDirectionHintsChange(true)
                    }

                    13 -> {
                        val list = MathUtils().calculateRodMovements(from = 0, to = 5, rods = 1, isForRightRods = false)
                        onRodMovementChange(list)
                        onShowDirectionHintsChange(true)
                    }

                    14 -> {
                        abacusData.setAbacusValueFromString("4000000")
                        val list = MathUtils().calculateRodMovements(from = 4, to = 0, rods = 1, isForRightRods = false)
                        onRodMovementChange(list)
                        onShowDirectionHintsChange(true)
                    }

                    15 -> {
                        abacusData.setAbacusValueFromString("5000000")
                        val list = MathUtils().calculateRodMovements(from = 5, to = 0, rods = 1, isForRightRods = false)
                        onRodMovementChange(list)
                        onShowDirectionHintsChange(true)
                    }

                    else -> {
                        abacusData.setAbacusValueFromString("0")
                    }
                }
            }
        )

        if (!showHighlighter) return

        val totalBeadsHeight = totalHeight - (dim.rectLineWidth * 2)

        // -----------------------------
        // 2️⃣ Beam (bar)
        // -----------------------------
        Column(modifier = Modifier
            .height(totalHeight)
            .padding(dim.rectLineWidth)) {
            Spacer(modifier = Modifier.height((dim.beadHeight * 2) + dim.extraSpace))
            Box(
                modifier = Modifier
                    .width(totalWidth - (dim.rectLineWidth * 2))
                    .height(dim.beamHeight)
                    .spotlightTag(2, highlightSteps[2].message)
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        // -----------------------------
        // 3️⃣ Upper beads area
        // -----------------------------
        Column(modifier = Modifier
            .height(totalHeight)
            .padding(dim.rectLineWidth)) {
            Box(
                modifier = Modifier
                    .width(totalWidth - (dim.rectLineWidth * 2))
                    .height(dim.beadHeight + (dim.extraSpace * 2))
                    .spotlightTag(3, highlightSteps[3].message)
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        // -----------------------------
        // 4️⃣ Lower beads area
        // -----------------------------
        Column(modifier = Modifier
            .height(totalHeight)
            .padding(dim.rectLineWidth)) {
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .width(totalWidth - (dim.rectLineWidth * 2))
                    .height((dim.beadHeight * 4) + (dim.extraSpace * 2))
                    .spotlightTag(4, highlightSteps[4].message)
            )
        }

        // -----------------------------
        // 5️⃣ Unit place
        // -----------------------------
        Column(modifier = Modifier
            .height(totalHeight)
            .padding(dim.rectLineWidth)) {
            Row(modifier = Modifier
                .padding(top = (dim.beadHeight * 2) + dim.extraSpace)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .width(dim.beadWidth)
                        .height(dim.beamHeight)
                        .spotlightTag(5, highlightSteps[5].message)
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        // -----------------------------
        // 6️⃣ First rods
        // -----------------------------
        Row(modifier = Modifier.padding(dim.rectLineWidth)) {
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .width(dim.beadWidth)
                    .height(totalBeadsHeight)
                    .spotlightTag(6, highlightSteps[6].message)
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        // -----------------------------
        // 7️⃣ Second rods
        // -----------------------------
        Row(modifier = Modifier.padding(dim.rectLineWidth)) {
            Box(
                modifier = Modifier
                    .width((dim.beadWidth + (dim.columnSpaces * 2)) * 5)
                    .height(totalBeadsHeight)
            )
            Box(
                modifier = Modifier
                    .width(dim.beadWidth)
                    .height(totalHeight - dim.rectLineWidth - (dim.beamHeight * 2))
                    .spotlightTag(7, highlightSteps[7].message)
            )
            Box(
                modifier = Modifier
                    .width((dim.beadWidth + (dim.columnSpaces * 2)) * 7)
                    .height(totalBeadsHeight)
            )
        }

        // -----------------------------
        // 8️⃣ Third rods
        // -----------------------------
        Row(modifier = Modifier.padding(dim.rectLineWidth)) {
            Box(
                modifier = Modifier
                    .width((dim.beadWidth + (dim.columnSpaces * 2)) * 4)
                    .height(totalBeadsHeight)
            )
            Box(
                modifier = Modifier
                    .width(dim.beadWidth)
                    .height(totalBeadsHeight)
                    .spotlightTag(8, highlightSteps[8].message)
            )
            Box(
                modifier = Modifier
                    .width((dim.beadWidth + (dim.columnSpaces * 2)) * 8)
                    .height(totalBeadsHeight)
            )
        }

        // -----------------------------
        // 9️⃣ 1 rod = 0–9
        // -----------------------------
        Row(modifier = Modifier.padding(dim.rectLineWidth)) {
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .width(dim.beadWidth)
                    .height(totalBeadsHeight)
                    .spotlightTag(9, highlightSteps[9].message)
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        // -----------------------------
        // 1️⃣0️⃣ rods 0–99
        // -----------------------------
        Row(modifier = Modifier.padding(dim.rectLineWidth)) {
            Box(
                modifier = Modifier
                    .width((dim.beadWidth + (dim.columnSpaces * 2)) * 5)
                    .height(totalBeadsHeight)
            )
            Box(
                modifier = Modifier
                    .width((dim.beadWidth + (dim.columnSpaces * 2)) * 2)
                    .height(totalBeadsHeight)
                    .spotlightTag(10, highlightSteps[10].message)
            )
            Box(
                modifier = Modifier
                    .width((dim.beadWidth + (dim.columnSpaces * 2)) * 6)
                    .height(totalBeadsHeight)
            )
        }

        // -----------------------------
        // 1️⃣1️⃣ rods 0–999
        // -----------------------------
        Row(modifier = Modifier.padding(dim.rectLineWidth)) {
            Box(
                modifier = Modifier
                    .width((dim.beadWidth + (dim.columnSpaces * 2)) * 4)
                    .height(totalBeadsHeight)
            )
            Box(
                modifier = Modifier
                    .width((dim.beadWidth + (dim.columnSpaces * 2)) * 3)
                    .height(totalBeadsHeight)
                    .spotlightTag(11, highlightSteps[11].message)
            )
            Box(
                modifier = Modifier
                    .width((dim.beadWidth + (dim.columnSpaces * 2)) * 6)
                    .height(totalBeadsHeight)
            )
        }

        // -----------------------------
        // 1️⃣2️⃣ Addition bottom bead
        // -----------------------------
        Row(modifier = Modifier.padding(dim.rectLineWidth)) {
            Spacer(modifier = Modifier.weight(1f))
            Column {
                Box(
                    modifier = Modifier
                        .width(dim.beadWidth)
                        .height((dim.beadHeight * 2) + dim.beamHeight + dim.extraSpace)
                )
                Box(
                    modifier = Modifier
                        .width(dim.beadWidth)
                        .height((dim.beadHeight * 5) + dim.extraSpace)
                        .spotlightTag(12, highlightSteps[12].message)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        // -----------------------------
        // 1️⃣3️⃣ Addition top bead
        // -----------------------------
        Column(modifier = Modifier
            .height(totalHeight)
            .padding(dim.rectLineWidth)) {
            Spacer(modifier = Modifier.weight(1f))
            Column {
                Box(
                    modifier = Modifier
                        .width(dim.beadWidth)
                        .height((dim.beadHeight * 2) + dim.extraSpace)
                        .spotlightTag(13, highlightSteps[13].message)
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        // -----------------------------
        // 1️⃣4️⃣ Subtraction bottom bead
        // -----------------------------
        Row(modifier = Modifier.padding(dim.rectLineWidth)) {
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier.height(totalHeight - (dim.rectLineWidth * 2))
            ) {
                Box(
                    modifier = Modifier
                        .width(dim.beadWidth)
                        .height((dim.beadHeight * 2) + dim.beamHeight + dim.extraSpace)
                )
                Box(
                    modifier = Modifier
                        .width(dim.beadWidth)
                        .height((dim.beadHeight * 5) + dim.extraSpace)
                        .spotlightTag(14, highlightSteps[14].message)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        // -----------------------------
        // 1️⃣5️⃣ Subtraction top bead
        // -----------------------------
        Row(modifier = Modifier.padding(dim.rectLineWidth)) {
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier.height(totalHeight - (dim.rectLineWidth * 2))
            ) {
                Box(
                    modifier = Modifier
                        .width(dim.beadWidth)
                        .height((dim.beadHeight * 2) + dim.extraSpace)
                        .spotlightTag(15, highlightSteps[15].message)
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.weight(1f))
        }
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