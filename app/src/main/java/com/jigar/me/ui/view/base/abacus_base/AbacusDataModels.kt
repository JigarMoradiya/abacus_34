package com.jigar.me.ui.view.base.abacus_base

import android.graphics.RectF
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.home.theme.AppDimens
import androidx.annotation.StringRes
import com.jigar.me.R

// ColorPresetModel.kt
data class ColorPresetModel(
    val abacusTopGradient: Color = Color.Cyan,
    val abacusCenterGradient: Color = Color.Magenta,
    val abacusBottomGradient: Color = Color.Yellow,
    val buttonColor: Color = Color.Blue,
    val displayBGColor: Color = Color(229, 74, 57),
    val displayBorderColor: Color = Color.Yellow,
    val columnColors: Color = Color.Cyan,
    val arrowColor: Color = Color.Black,
    // Stroke around the Reset/Current-Input/Next capsule (AbacusAnswerBarCompose).
    // Null = keep the old behavior of reusing abacusCenterGradient, for every theme
    // that hasn't been given its own dedicated value.
    val answerBarStrokeColor: Color? = null,
    // Top-to-bottom gradient of a bead that is not counted (resting away from the beam) and of one that is counted
    // (pushed up to the beam). Null = the usual tint of the bead colour; Dark mode sets its own so resting beads recede
    // on the dark panel and counted beads glow.
    val inactiveBeadColors: List<Color>? = null,
    val activeBeadColors: List<Color>? = null,
)

/*data class AbacusDimensionModel(
    val beadWidth: Dp = AppDimens.Dimens45,
    val beadHeight: Dp = AppDimens.Dimens25,
    val beamHeight: Dp = AppDimens.Dimens4,
    val extraSpace: Dp = AppDimens.Dimens4,
    val rectLineWidth: Dp = AppDimens.Dimens16,
    val rectLineCorner: Dp = AppDimens.Dimens20,
    val textSizeSp: Int = 13,
    val stripHeight: Dp = AppDimens.Dimens16,
    val columnSpaces: Dp = AppDimens.Dimens1
)*/

// 9:5 45x25
data class AbacusDimensionModel(
    val beadWidth: Dp = 45.dp,
    val beadHeight: Dp = 25.dp,
    val beamHeight: Dp = if (DeviceInfo.isLargeTablet) 8.dp else if (DeviceInfo.isTablet) 6.dp else 4.dp,
    val extraSpace: Dp = if (DeviceInfo.isLargeTablet) 8.dp else if (DeviceInfo.isTablet) 6.dp else 4.dp,
    val rectLineWidth: Dp = if (DeviceInfo.isLargeTablet) 32.dp else if (DeviceInfo.isTablet) 24.dp else 16.dp,
    val rectLineCorner: Dp = if (DeviceInfo.isLargeTablet) 36.dp else if (DeviceInfo.isTablet) 28.dp else 20.dp,
    val textSizeSp: Int = 13,
    val stripHeight: Dp = if (DeviceInfo.isLargeTablet) 36.dp else if (DeviceInfo.isTablet) 28.dp else 20.dp,
    val columnSpaces: Dp = if (DeviceInfo.isLargeTablet) 3.dp else if (DeviceInfo.isTablet) 2.dp else 1.dp,
    // extra horizontal gap inserted between column 6 (last integer rod) and
    // column 7 (first decimal/remainder rod), so kids can see the two groups
    // are separate. Only applied when numberOfColumns > 7.
    val decimalSeparatorGap: Dp = if (DeviceInfo.isLargeTablet) 26.dp else if (DeviceInfo.isTablet) 20.dp else 14.dp,
)

data class AbacusBottomLabel(
    @StringRes val textRes: Int,
    val colorHex: String,
    val fontSize: TextUnit,
    val heightMultiplier: Int
)

data class SpotlightTooltipCalculation(
    val rect: RectF,
    val screenWidth: Float,
    val screenHeight: Float,
    val safeTop: Float,
    val safeBottom: Float,
    val safeLeft: Float,
    val safeRight: Float,
    val tooltipWidth: Float,
    val tooltipHeight: Float,
    /** Free space kept between the card and the lit window (room for the card's pointer). */
    val margin: Float = 16f,
)


/** One step of the Free Mode tour: the string id of its text (HTML kept as text, shown by htmlToAnnotatedString). */
data class HighlightStep(val index: Int, @StringRes val messageRes: Int)

val freeModeHighlightSteps = listOf(
    HighlightStep(0, R.string.tour_step_01),
    HighlightStep(1, R.string.tour_step_02),
    HighlightStep(2, R.string.tour_step_03),
    HighlightStep(3, R.string.tour_step_04),
    HighlightStep(4, R.string.tour_step_05),
    HighlightStep(5, R.string.tour_step_06),
    HighlightStep(6, R.string.tour_step_07),
    HighlightStep(7, R.string.tour_step_08),
    HighlightStep(8, R.string.tour_step_09),
    HighlightStep(9, R.string.tour_step_10),
    HighlightStep(10, R.string.tour_step_11),
    HighlightStep(11, R.string.tour_step_12),
    HighlightStep(12, R.string.tour_step_13),
    HighlightStep(13, R.string.tour_step_14),
    HighlightStep(14, R.string.tour_step_15),
    HighlightStep(15, R.string.tour_step_16),
)