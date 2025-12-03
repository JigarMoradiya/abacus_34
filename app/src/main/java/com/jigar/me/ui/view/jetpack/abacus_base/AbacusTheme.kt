package com.jigar.me.ui.view.jetpack.abacus_base

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.jigar.me.utils.AppConstants

// AppThemeAbacus.kt
object AbacusTheme {

    private fun hex(h: String) = Color("#$h".toColorInt())

    fun colorPreset(name: String): ColorPresetModel {
        return when (name) {
            "poligon_rainbow" -> ColorPresetModel(
                abacusTopGradient = hex("607D8B"),
                abacusCenterGradient = hex("90A4AE"),
                abacusBottomGradient = hex("ECEFF1"),
                buttonColor = hex("37474F"),
                columnColors = hex("BCAAA4"),
                displayBGColor = hex("4E342E"),
                displayBorderColor = hex("8D6E63"),
                beadsImageSetName = name,
                beadsAspectRatio = 1.8f
            )

            "poligon_blue" -> ColorPresetModel(
                abacusTopGradient = hex("3F51B5"),
                abacusCenterGradient = hex("7986CB"),
                abacusBottomGradient = hex("E8EAF6"),
                buttonColor = hex("283593"),
                columnColors = hex("9FA8DA"),
                displayBGColor = hex("7f2ccb"),
                displayBorderColor = hex("ffa9e7"),
                beadsImageSetName = name
            )

            "poligon_red" -> ColorPresetModel(
                abacusTopGradient = hex("FF0000"),
                abacusCenterGradient = hex("E57373"),
                abacusBottomGradient = hex("FFEBEE"),
                buttonColor = hex("B71C1C"),
                columnColors = hex("EF9A9A"),
                displayBGColor = hex("0D1764"),
                displayBorderColor = hex("9199e2"),
                beadsImageSetName = name
            )

            "face_red" -> ColorPresetModel(
                abacusTopGradient = hex("E53935"),
                abacusCenterGradient = hex("EF5350"),
                abacusBottomGradient = hex("FFCDD2"),
                buttonColor = hex("B71C1C"),
                columnColors = hex("E57373"),
                displayBGColor = hex("344a70"),
                displayBorderColor = hex("57babb"),
                beadsImageSetName = name
            )

            else -> ColorPresetModel(
                abacusTopGradient = hex("9C27B0"),
                abacusCenterGradient = hex("BA68C8"),
                abacusBottomGradient = hex("F3E5F5"),
                buttonColor = hex("6A1B9A"),
                columnColors = hex("CE93D8"),
                beadsImageSetName = name
            )
        }
    }

    fun dimensionPreset(
        screenType: String = AppConstants.AbacusScreen.screenTypeFreeMode,
        isFreeModeOn : Boolean = false
    ): AbacusDimensionModel {
        // This is a simplified mapping of your AbacusDimension logic.
        // You can tweak multipliers same as Swift.
        val base = AbacusDimensionModel()

        val multiplier = when (screenType) {
            "exam", "settings" -> 0.5f
            "abacus_practice" -> 0.9f
            AppConstants.AbacusScreen.screenTypeFreeMode -> if (isFreeModeOn){
                1f
            }else{
                1f
            }
            else -> 0.9f
        }

        return base.copy(
            beadWidth = base.beadWidth * multiplier,
            beadHeight = base.beadHeight * multiplier,
            columnSpaces = if (screenType == "free_mode") base.columnSpaces * 2 else base.columnSpaces,
            beamHeight = if (screenType == "exam" || screenType == "settings") base.beamHeight
            else base.beamHeight * 2,
            rectLineWidth = if (screenType == "exam" || screenType == "settings")
                base.rectLineWidth / 2 else base.rectLineWidth,
            rectLineCorner = if (screenType == "exam" || screenType == "settings")
                base.rectLineCorner / 2 else base.rectLineCorner
        )
    }
}