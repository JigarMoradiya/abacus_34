package com.jigar.me.ui.view.jetpack.abacus_base

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
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
    val arrowColor: Color = Color.Black,
    val beadsImageSetName: String = "poligon_purple",
    val beadsAspectRatio: Float = 1.8f,
    val abacusPreviewNumber: Int = 20
)

data class AbacusDimensionModel(
    val beadWidth: Dp = 45.dp,
    val beadHeight: Dp = 25.dp,
    val beamHeight: Dp = 4.dp,
    val extraSpace: Dp = 4.dp,
    val rectLineWidth: Dp = 20.dp,
    val rectLineCorner: Dp = 20.dp,
    val textSizeSp: Int = 16,
    val columnSpaces: Dp = 1.dp
)

// AbacusPreferences.kt
data class AbacusPreferences(
    val selectedAbacusTheme: String = "poligon_rainbow",
    val isFreeModeOn: Boolean = true,
    val resetEverytimeToggle: Boolean = true,
    val randomToggle: Boolean = true,
    val randomRangeLow: Int = 1,
    val randomRangeHigh: Int = 9,
    val isDisplayBeadDirection: Boolean = true,
    val isDisplayAbacusNumber: Boolean = true,
    val isAbacusSumSound: Boolean = false  // sound not implemented here
)