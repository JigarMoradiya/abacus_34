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

            // -----------------------
            //  poligon_rainbow
            // -----------------------
            "poligon_rainbow" -> ColorPresetModel(
                abacusTopGradient = hex("607D8B"),
                abacusCenterGradient = hex("90A4AE"),
                abacusBottomGradient = hex("ECEFF1"),
                buttonColor = hex("37474F"),
                columnColors = hex("BCAAA4"),
                displayBGColor = hex("4E342E"),
                displayBorderColor = hex("8D6E63")
            )

            // -----------------------
            //  poligon_black
            // -----------------------
            "poligon_black", "Poligon" -> ColorPresetModel(
                abacusTopGradient = hex("616161"),
                abacusCenterGradient = hex("BDBDBD"),
                abacusBottomGradient = hex("EEEEEE"),
                buttonColor = hex("212121"),
                columnColors = hex("E0E0E0"),
                displayBGColor = hex("bb4430"),
                displayBorderColor = hex("f6ae29"),
                arrowColor = hex("000000")
            )

            // -----------------------
            //  poligon_blue
            // -----------------------
            "poligon_blue" -> ColorPresetModel(
                abacusTopGradient = hex("3F51B5"),
                abacusCenterGradient = hex("7986CB"),
                abacusBottomGradient = hex("E8EAF6"),
                buttonColor = hex("283593"),
                columnColors = hex("9FA8DA"),
                displayBGColor = hex("7f2ccb"),
                displayBorderColor = hex("ffa9e7")
            )

            // -----------------------
            //  poligon_purple
            // -----------------------
            "poligon_purple" -> ColorPresetModel(
                abacusTopGradient = hex("9C27B0"),
                abacusCenterGradient = hex("BA68C8"),
                abacusBottomGradient = hex("F3E5F5"),
                buttonColor = hex("6A1B9A"),
                columnColors = hex("CE93D8"),
                displayBGColor = hex("a23a06"),
                displayBorderColor = hex("f6ae29")
            )

            // -----------------------
            //  poligon_skyblue
            // -----------------------
            "poligon_skyblue" -> ColorPresetModel(
                abacusTopGradient = hex("2196F3"),
                abacusCenterGradient = hex("64B5F6"),
                abacusBottomGradient = hex("E3F2FD"),
                buttonColor = hex("1565C0"),
                columnColors = hex("90CAF9"),
                displayBGColor = hex("401e5b"),
                displayBorderColor = hex("ff8552")
            )

            // -----------------------
            //  poligon_red
            // -----------------------
            "poligon_red" -> ColorPresetModel(
                abacusTopGradient = hex("FF0000"),
                abacusCenterGradient = hex("E57373"),
                abacusBottomGradient = hex("FFEBEE"),
                buttonColor = hex("B71C1C"),
                columnColors = hex("EF9A9A"),
                displayBGColor = hex("0D1764"),
                displayBorderColor = hex("9199e2")
            )

            // -----------------------
            //  poligon_green
            // -----------------------
            "poligon_green" -> ColorPresetModel(
                abacusTopGradient = hex("4CAF50"),
                abacusCenterGradient = hex("81C784"),
                abacusBottomGradient = hex("E8F5E9"),
                buttonColor = hex("2E7D32"),
                columnColors = hex("A5D6A7"),
                displayBGColor = hex("932826"),
                displayBorderColor = hex("fb9648")
            )

            // -----------------------
            //  poligon_orange
            // -----------------------
            "poligon_orange" -> ColorPresetModel(
                abacusTopGradient = hex("FF9800"),
                abacusCenterGradient = hex("FFB74D"),
                abacusBottomGradient = hex("FFF3E0"),
                buttonColor = hex("EF6C00"),
                columnColors = hex("FFCC80"),
                displayBGColor = hex("731500"),
                displayBorderColor = hex("fcdc4d")
            )

            // -----------------------
            //  poligon_cyan
            // -----------------------
            "poligon_cyan" -> ColorPresetModel(
                abacusTopGradient = hex("00BCD4"),
                abacusCenterGradient = hex("4DD0E1"),
                abacusBottomGradient = hex("E0F7FA"),
                buttonColor = hex("00838F"),
                columnColors = hex("80DEEA"),
                displayBGColor = hex("5f0f40"),
                displayBorderColor = hex("da7422")
            )

            // -----------------------
            //  poligon_pink
            // -----------------------
            "poligon_pink" -> ColorPresetModel(
                abacusTopGradient = hex("E91E63"),
                abacusCenterGradient = hex("F06292"),
                abacusBottomGradient = hex("FCE4EC"),
                buttonColor = hex("AD1457"),
                columnColors = hex("F48FB1"),
                displayBGColor = hex("216869"),
                displayBorderColor = hex("49a078")
            )

            // -----------------------
            //  poligon_yellow
            // -----------------------
            "poligon_yellow" -> ColorPresetModel(
                abacusTopGradient = hex("FFC107"),
                abacusCenterGradient = hex("FFD54F"),
                abacusBottomGradient = hex("FFF8E1"),
                buttonColor = hex("FF8F00"),
                columnColors = hex("FFE082"),
                displayBGColor = hex("388659"),
                displayBorderColor = hex("33ca7f")
            )

            // -----------------------
            //  poligon_silver
            // -----------------------
            "poligon_silver" -> ColorPresetModel(
                abacusTopGradient = hex("607D8B"),
                abacusCenterGradient = hex("90A4AE"),
                abacusBottomGradient = hex("ECEFF1"),
                buttonColor = hex("37474F"),
                columnColors = hex("B0BEC5"),
                displayBGColor = hex("235789"),
                displayBorderColor = hex("e1bc29")
            )

            // -----------------------
            //  poligon_brown
            // -----------------------
            "poligon_brown" -> ColorPresetModel(
                abacusTopGradient = hex("795548"),
                abacusCenterGradient = hex("A1887F"),
                abacusBottomGradient = hex("EFEBE9"),
                buttonColor = hex("4E342E"),
                columnColors = hex("BCAAA4"),
                displayBGColor = hex("932826"),
                displayBorderColor = hex("ff8841")
            )

            // -----------------------
            //  face_red
            // -----------------------
            "face_red" -> ColorPresetModel(
                abacusTopGradient = hex("E53935"),
                abacusCenterGradient = hex("EF5350"),
                abacusBottomGradient = hex("FFCDD2"),
                buttonColor = hex("B71C1C"),
                columnColors = hex("E57373"),
                displayBGColor = hex("344a70"),
                displayBorderColor = hex("57babb")
            )

            // -----------------------
            //  DEFAULT (poligon_purple)
            // -----------------------
            else -> ColorPresetModel(
                abacusTopGradient = hex("9C27B0"),
                abacusCenterGradient = hex("BA68C8"),
                abacusBottomGradient = hex("F3E5F5"),
                buttonColor = hex("6A1B9A"),
                columnColors = hex("CE93D8")
            )
        }
    }

    fun dimensionPreset(
        screenType: String = AppConstants.AbacusScreen.screenTypeFreeMode,
        abacusType: String? = null,
        isFreeModeOn : Boolean = false
    ): AbacusDimensionModel {
        // This is a simplified mapping of your AbacusDimension logic.
        // You can tweak multipliers same as Swift.
        val base = AbacusDimensionModel()

        val multiplier = when (screenType) {
            "exam", "settings" -> 0.5f
            AppConstants.AbacusScreen.screenTypeAbacusPractice -> if (abacusType == AppConstants.apiParams.answerStepByStep) 0.9f else {1f}
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
            columnSpaces = if (screenType == AppConstants.AbacusScreen.screenTypeFreeMode) base.columnSpaces * 2 else base.columnSpaces,
            beamHeight = if (screenType == "exam" || screenType == "settings") base.beamHeight
            else base.beamHeight * 2,
            rectLineWidth = if (screenType == "exam" || screenType == "settings")
                base.rectLineWidth / 2 else base.rectLineWidth,
            rectLineCorner = if (screenType == "exam" || screenType == "settings")
                base.rectLineCorner / 2 else base.rectLineCorner
        )
    }
}