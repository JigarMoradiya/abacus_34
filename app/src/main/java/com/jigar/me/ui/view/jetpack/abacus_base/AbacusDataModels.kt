package com.jigar.me.ui.view.jetpack.abacus_base

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

// ColorPresetModel.kt
data class ColorPresetModel(
    val abacusTopGradient: Color = Color.Cyan,
    val abacusCenterGradient: Color = Color.Magenta,
    val abacusBottomGradient: Color = Color.Yellow,
    val buttonColor: Color = Color.Blue,
    val displayBGColor: Color = Color(229, 74, 57),
    val displayBorderColor: Color = Color.Yellow,
    val columnColors: Color = Color.Cyan,
    val arrowColor: Color = Color.Black
)

data class AbacusDimensionModel(
    val beadWidth: Dp = 45.dp,
    val beadHeight: Dp = 25.dp,
    val beamHeight: Dp = 4.dp,
    val extraSpace: Dp = 4.dp,
    val rectLineWidth: Dp = 16.dp,
    val rectLineCorner: Dp = 20.dp,
    val textSizeSp: Int = 13,
    val stripHeight: Dp = 16.dp,
    val columnSpaces: Dp = 1.dp
)

data class AbacusBottomLabel(
    val textKey: String,
    val colorHex: String,
    val fontSize: TextUnit,
    val heightMultiplier: Int
)


data class SpotlightTooltipCalculation(
    val rect: android.graphics.RectF,
    val screenWidth: Float,
    val screenHeight: Float,
    val safeTop: Float,
    val safeBottom: Float,
    val safeLeft: Float,
    val safeRight: Float,
    val tooltipWidth: Float,
    val tooltipHeight: Float,
)


data class HighlightStep(val index: Int, val message: String)

val freeModeHighlightSteps = listOf(
    HighlightStep(0, "This is <font color='#FF5722'><b>Frame</b></font> of Abacus"),
    HighlightStep(1, "This all are <font color='#0000EE'><b>Rods</b></font> or <font color='#0000EE'><b>Column</b></font> of Abacus"),
    HighlightStep(2, "This is <font color='#6200EA'><b>Bar</b></font> or <font color='#6200EA'><b>Beam</b></font> of Abacus"),
    HighlightStep(3, "This all are <font color='#E91E63'><b>Upper Beads</b></font> of Abacus"),
    HighlightStep(4, "This all are <font color='#E91E63'><b>Lower Beads</b></font> of Abacus"),
    HighlightStep(5, "<font color='#0000EE'><b>Unit's Place</b></font> <b>(Ones column)</b><br/>of Abacus"),
    HighlightStep(6, "<b>On 1st ROD</b> <font color='#0000EE'><b>(Ones column)</b></font><br/>each lower bead <font color='#D81B60'><b>value is 1</b></font><br/>and the upper bead <font color='#388E3C'><b>value is 5</b></font>"),
    HighlightStep(7, "<b>On 2nd ROD</b> <font color='#0000EE'><b>(Tens column)</b></font><br/>each lower bead <font color='#D81B60'><b>value is 10</b></font><br/>and the upper bead <font color='#388E3C'><b>value is 50</b></font>"),
    HighlightStep(8, "<b>On 3rd ROD</b> <font color='#0000EE'><b>(Hundreds column)</b></font><br/>each lower bead <font color='#D81B60'><b>value is 100</b></font><br/>and the upper bead <font color='#388E3C'><b>value is 500,</b></font><br/>so on.."),
    HighlightStep(9, "<b>One Column</b><br/>can show any number from <font color='#EE0000'><b>0 to 9</b></font>"),
    HighlightStep(10, "<b>Two Column</b><br/>can show any numbers from <font color='#EE0000'><b>0 to 99</b></font>"),
    HighlightStep(11, "<b>Three Column</b><br/>can show any numbers from <font color='#EE0000'><b>0 to 999</b></font>"),
    HighlightStep(12, "<b>For Addition</b><br/>always use your <font color='#D81B60'><b>thumb</b></font><br/>to <font color='#0000EE'><b>move lower beads to upward</b></font>"),
    HighlightStep(13, "<b>For Addition</b><br/>always use your <font color='#D81B60'><b>index finger</b></font><br/>to <font color='#0000EE'><b>move top bead to downward</b></font>"),
    HighlightStep(14, "<b>For Subtraction</b><br/>always use your <font color='#D81B60'><b>index finger</b></font><br/>to <font color='#0000EE'><b>move lower beads to downward</b></font>"),
    HighlightStep(15, "<b>For Subtraction</b><br/>always use your <font color='#D81B60'><b>thumb</b></font><br/>to <font color='#0000EE'><b>move upper bead to upward</b></font>")
)