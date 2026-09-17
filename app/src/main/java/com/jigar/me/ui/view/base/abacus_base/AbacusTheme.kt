package com.jigar.me.ui.view.base.abacus_base

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.ToolbarIconSize
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
                displayBGColor = hex("607D8B"),
                displayBorderColor = hex("90A4AE")
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
                displayBGColor = hex("616161"),
                displayBorderColor = hex("BDBDBD"),
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
                displayBGColor = hex("3F51B5"),
                displayBorderColor = hex("7986CB")
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
                displayBGColor = hex("9C27B0"),
                displayBorderColor = hex("BA68C8")
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
                displayBGColor = hex("2196F3"),
                displayBorderColor = hex("64B5F6")
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
                displayBGColor = hex("FF0000"),
                displayBorderColor = hex("E57373")
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
                displayBGColor = hex("4CAF50"),
                displayBorderColor = hex("81C784")
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
                // Bead orange is too light for white text -- use the darker button
                // shade instead, same hue family either way.
                displayBGColor = hex("EF6C00"),
                displayBorderColor = hex("FFB74D")
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
                // Bead cyan is too light for white text -- use the darker button
                // shade instead, same hue family either way.
                displayBGColor = hex("00838F"),
                displayBorderColor = hex("4DD0E1")
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
                displayBGColor = hex("E91E63"),
                displayBorderColor = hex("F06292")
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
                // Bead yellow is far too light for white text -- use the darker
                // button shade instead, same hue family either way.
                displayBGColor = hex("FF8F00"),
                displayBorderColor = hex("FFD54F")
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
                displayBGColor = hex("607D8B"),
                displayBorderColor = hex("90A4AE")
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
                displayBGColor = hex("795548"),
                displayBorderColor = hex("A1887F")
            )

            // -----------------------
            //  poligon_duotone -- rod alternates between 2 colors (see ColorPresets.getDuoToneColorList)
            // -----------------------
            "poligon_duotone" -> ColorPresetModel(
                abacusTopGradient = hex("2E86AB"),
                abacusCenterGradient = hex("6BA9C6"),
                abacusBottomGradient = hex("E3EEF3"),
                buttonColor = hex("1B5E76"),
                columnColors = hex("A9CBDA"),
                // Monochromatic blue, matching the frame -- the coral duo-tone accent
                // looked good on its own bead but clashed as a border/chrome color.
                displayBGColor = hex("1B3A4B"),
                displayBorderColor = hex("6BA9C6")
            )

            // -----------------------
            //  poligon_candy -- 5 colors, one per bead position (see ColorPresets.getCandyColorList)
            // -----------------------
            "poligon_candy" -> ColorPresetModel(
                // Same neutral slate frame Rainbow uses, so the 5-color candy beads are
                // the visual focus instead of competing with a single frame hue. (A near-
                // white value here would also wash out the rod tint, which mixes this
                // color 80% with white -- this shade is proven visible at that mix.)
                abacusTopGradient = hex("607D8B"),
                abacusCenterGradient = hex("90A4AE"),
                abacusBottomGradient = hex("ECEFF1"),
                // Dark neutral tint (same as Rainbow's) -- drives the Reset/Next
                // background, the dot markers, and the beam end-caps, so none of them
                // compete with the candy beads either.
                buttonColor = hex("37474F"),
                columnColors = hex("F8BBD9"),
                // Neutral, matching the frame -- the answer bar stays out of the way so
                // the 5-color candy beads are the only colorful thing on screen.
                displayBGColor = hex("607D8B"),
                displayBorderColor = hex("90A4AE"),
                answerBarStrokeColor = hex("90A4AE")
            )

            // -----------------------
            //  poligon_heaven_earth -- heaven bead (upper) vs earth beads (lower) two-tone
            // -----------------------
            "poligon_heaven_earth" -> ColorPresetModel(
                abacusTopGradient = hex("0B4F6C"),
                abacusCenterGradient = hex("4A82A0"),
                abacusBottomGradient = hex("DCEAF1"),
                buttonColor = hex("083A50"),
                columnColors = hex("9DC3D6"),
                // Monochromatic teal, matching the frame -- the gold heaven-bead accent
                // looked good on the bead itself but clashed as a border/chrome color.
                displayBGColor = hex("0B4F6C"),
                displayBorderColor = hex("4A82A0")
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
                displayBGColor = hex("E53935"),
                displayBorderColor = hex("EF5350")
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

//    @OptIn(UnstableApi::class)
//    fun dimensionPreset(
//        context: Context,
//        screenType: String = AppConstants.AbacusScreen.screenTypeFreeMode,
//        abacusType: String? = null,
//        questionType: String? = null,
//        isFreeModeOn: Boolean = false
//    ): AbacusDimensionModel {
//        // This is a simplified mapping of your AbacusDimension logic.
//        // You can tweak multipliers same as Swift.
//        val base = AbacusDimensionModel()
//        val freeModeBase = 0.9f
//        val multiplierTemp = when (screenType) {
//            AppConstants.AbacusScreen.screenTypeExam, AppConstants.AbacusScreen.screenTypeSettingPreview -> 0.45f
//            AppConstants.AbacusScreen.screenTypeExamResult -> 0.25f
//            AppConstants.AbacusScreen.screenTypeCCM -> 0.85f
//            AppConstants.AbacusScreen.screenTypeExercise -> 0.90f
//            AppConstants.AbacusScreen.screenTypeAbacusPractice -> if (questionType == AppConstants.extras_Comman.AbacusTypeNumber) { 0.9f } else if (abacusType == AppConstants.apiParams.answerStepByStep) 0.85f else {0.9f}
//            AppConstants.AbacusScreen.screenTypeFreeMode -> if (isFreeModeOn){
//                1f
//            }else{
//                1f
//            }
//            else -> 0.9f
//        }
//        val multiplier = (multiplierTemp * freeModeBase)
//        val pref = AppPreferencesHelper(context, AppConstants.PREF_NAME)
//        val displayMetrics = context.resources.displayMetrics
//        val density = displayMetrics.density.takeIf { it > 0f } ?: 1f
//        val densityWidthDp = displayMetrics.widthPixels / density
//        val configurationWidthDp = context.resources.configuration.screenWidthDp.toFloat()
//        val prefWidthDp = pref.getCustomParamInt(AppConstants.screenWidthDp,0).toFloat()
//        val screenWidthDp = listOf(prefWidthDp, configurationWidthDp, densityWidthDp)
//            .filter { it > 0f }
//            .minOrNull() ?: configurationWidthDp
//        val rectWidth = (base.rectLineWidth * 2).value
//        val colSpace = (base.columnSpaces * 14).value
//        val extraPadding = (base.extraSpace * 2).value
//
//        // OPTION 4 (option 2 and option 3 combo)
//        val maxAbacusWidth = when {
//            DeviceInfo.isLargeTablet -> 1240
//            DeviceInfo.isTablet -> 1100
//            else -> 820
//        }
//        val horizontalMargin = when {
//            DeviceInfo.isLargeTablet -> 48
//            DeviceInfo.isTablet -> 40
//            else -> 24
//        }
//        val usableWidth = min(screenWidthDp, maxAbacusWidth.toFloat())
//        val remainSpace = usableWidth - (horizontalMargin * 2) - colSpace - rectWidth - extraPadding
//        val beadWidth = (remainSpace / 13).coerceAtLeast(1f)
//        val beadHeight : Double = ((5 * beadWidth) / 9).toDouble()  // 9 : 5
//
//        return base.copy(
//            beadWidth = beadWidth.dp * multiplier,
//            beadHeight = beadHeight.dp * multiplier,
//            columnSpaces = if (screenType == AppConstants.AbacusScreen.screenTypeCCM || screenType == AppConstants.AbacusScreen.screenTypeExercise) base.columnSpaces * 2 else if (screenType == AppConstants.AbacusScreen.screenTypeFreeMode) base.columnSpaces * 2 else base.columnSpaces,
//            beamHeight = if (screenType == AppConstants.AbacusScreen.screenTypeExam || screenType == AppConstants.AbacusScreen.screenTypeExamResult || screenType == AppConstants.AbacusScreen.screenTypeSettingPreview) base.beamHeight else base.beamHeight * 2,
//            rectLineWidth = when (screenType) {
//                AppConstants.AbacusScreen.screenTypeExam, AppConstants.AbacusScreen.screenTypeExamResult -> { 0.dp }
//                AppConstants.AbacusScreen.screenTypeSettingPreview -> base.rectLineWidth / 2
//                else -> base.rectLineWidth
//            },
//            rectLineCorner = when (screenType) {
//                AppConstants.AbacusScreen.screenTypeExam, AppConstants.AbacusScreen.screenTypeExamResult -> { 0.dp }
//                AppConstants.AbacusScreen.screenTypeSettingPreview -> base.rectLineCorner / 2
//                else -> base.rectLineCorner
//            },
//            textSizeSp =  if (screenType == AppConstants.AbacusScreen.screenTypeExam || screenType == AppConstants.AbacusScreen.screenTypeExamResult) {0} else if (screenType == AppConstants.AbacusScreen.screenTypeSettingPreview) 7 else 13
//        )
//    }

    fun dimensionPreset(
        context: Context,
        screenType: String = AppConstants.AbacusScreen.screenTypeFreeMode,
        abacusType: String? = null,
        questionType: String? = null,
        isFreeModeOn: Boolean = false
    ): AbacusDimensionModel {

        val base = AbacusDimensionModel()

        // Multiplier per screen type
        val freeModeBase = 1f

        val multiplierTemp = when (screenType) {
            AppConstants.AbacusScreen.screenTypeSettingPreview -> 0.40f
            AppConstants.AbacusScreen.screenTypeExam -> 0.45f
            AppConstants.AbacusScreen.screenTypeExamResult -> 0.25f
            AppConstants.AbacusScreen.screenTypeCCM -> 1f
            AppConstants.AbacusScreen.screenTypeExercise -> 1f
            AppConstants.AbacusScreen.screenTypeLevel1Learn,
            AppConstants.AbacusScreen.screenTypeLevel1Practice -> 1f
            AppConstants.AbacusScreen.screenTypeLevel2Learn,
            AppConstants.AbacusScreen.screenTypeLevel2Practice -> 0.9f
            AppConstants.AbacusScreen.screenTypeLevel2LearnMeetFormula -> 0.65f
            AppConstants.AbacusScreen.screenTypeAbacusPractice ->
                if (abacusType == AppConstants.apiParams.answerFinalAnswer || abacusType == AppConstants.apiParams.answerFormalExam) { 0.95f }
                else if (questionType == AppConstants.extras_Comman.AbacusTypeNumber) { 0.9f }
                else { 0.88f }
            AppConstants.AbacusScreen.screenTypeFreeMode -> 1f
            else -> 0.9f
        }

        val multiplier = multiplierTemp * freeModeBase

        // Screen dimensions
        val displayMetrics = context.resources.displayMetrics
        val density = displayMetrics.density.takeIf { it > 0f } ?: 1f
        val screenWidthDp = displayMetrics.widthPixels / density
        val screenHeightDp = displayMetrics.heightPixels / density

        // Static dimensions
        val frameWidth = (base.rectLineWidth * 2).value
        val columnSpacing = (base.columnSpaces * 14).value
        val extraHorizontalPadding = (base.extraSpace * 2).value

        val usableWidth = screenWidthDp
        val availableWidthForBeads = usableWidth - frameWidth - columnSpacing - extraHorizontalPadding
        val beadWidthFromWidth = (availableWidthForBeads / 13f).coerceAtLeast(1f)

        // HEIGHT CALCULATION
        val verticalMargin = when {
            DeviceInfo.isLargeTablet -> 40f
            DeviceInfo.isTablet -> 32f
            else -> 20f
        }

        val answerBarHeight = when {
            (screenType == AppConstants.AbacusScreen.screenTypeFreeMode)
            || screenType == AppConstants.AbacusScreen.screenTypeExam
            || screenType == AppConstants.AbacusScreen.screenTypeExamResult
            || screenType == AppConstants.AbacusScreen.screenTypeLevel1Learn
            || screenType == AppConstants.AbacusScreen.screenTypeLevel1Practice
            || screenType == AppConstants.AbacusScreen.screenTypeLevel2Learn
            || screenType == AppConstants.AbacusScreen.screenTypeLevel2LearnMeetFormula
            || screenType == AppConstants.AbacusScreen.screenTypeLevel2Practice -> 0f
            else -> 70f
        }

        val numberStripHeight = when (screenType) {
            AppConstants.AbacusScreen.screenTypeFreeMode -> {
                (base.stripHeight.value * 3f) + 40f
            }
            AppConstants.AbacusScreen.screenTypeAbacusPractice, AppConstants.AbacusScreen.screenTypeCCM, AppConstants.AbacusScreen.screenTypeExercise -> {
                base.stripHeight.value + 20f
            }
            else -> 0f
        }

        val headerHeight = ToolbarIconSize.value
        val availableHeight = if (DeviceInfo.isTablet || DeviceInfo.isLargeTablet) {
            val phoneHeightRatio = 384f / 781f
            val targetHeight = screenWidthDp * phoneHeightRatio

            val additionPadding = if (DeviceInfo.isTablet || DeviceInfo.isLargeTablet) AppDimens.Dimens20.value * 2 else 0f
            minOf(targetHeight, screenHeightDp) - verticalMargin - answerBarHeight - numberStripHeight - headerHeight - additionPadding

        } else {
            screenHeightDp - verticalMargin - answerBarHeight - numberStripHeight - headerHeight
        }


        val fixedHeightParts = (base.rectLineWidth.value * 2) + base.beamHeight.value + (base.extraSpace.value * 4)
        val availableHeightForBeads = availableHeight - fixedHeightParts
        val beadHeightFromHeight = (availableHeightForBeads / 7f).coerceAtLeast(1f)

        // Convert width -> height ratio
        val beadHeightFromWidth = (5f * beadWidthFromWidth) / 9f
        val beadWidthFromHeight = (beadHeightFromHeight * 9f) / 5f

        val finalBeadWidth = minOf(beadWidthFromWidth, beadWidthFromHeight)
        val finalBeadHeight = minOf(beadHeightFromWidth, beadHeightFromHeight)

        return base.copy(
            beadWidth = (finalBeadWidth * multiplier).dp,
            beadHeight = (finalBeadHeight * multiplier).dp,

            columnSpaces = when (screenType) {
                AppConstants.AbacusScreen.screenTypeCCM,
                AppConstants.AbacusScreen.screenTypeExercise,
                AppConstants.AbacusScreen.screenTypeFreeMode -> {
                    base.columnSpaces * 2
                }

                else -> base.columnSpaces
            },

            beamHeight = when (screenType) {
                AppConstants.AbacusScreen.screenTypeExam,
                AppConstants.AbacusScreen.screenTypeExamResult,
                AppConstants.AbacusScreen.screenTypeSettingPreview -> {
                    base.beamHeight
                }

                else -> base.beamHeight * 2
            },

            rectLineWidth = when (screenType) {
                AppConstants.AbacusScreen.screenTypeExam,
                AppConstants.AbacusScreen.screenTypeExamResult -> 0.dp

                AppConstants.AbacusScreen.screenTypeSettingPreview,
                AppConstants.AbacusScreen.screenTypeLevel1Learn,
                AppConstants.AbacusScreen.screenTypeLevel1Practice,
                AppConstants.AbacusScreen.screenTypeLevel2Learn,
                AppConstants.AbacusScreen.screenTypeLevel2LearnMeetFormula,
                AppConstants.AbacusScreen.screenTypeLevel2Practice -> {
                    base.rectLineWidth / 2
                }

                else -> base.rectLineWidth
            },

            rectLineCorner = when (screenType) {
                AppConstants.AbacusScreen.screenTypeExam,
                AppConstants.AbacusScreen.screenTypeExamResult -> 0.dp

                AppConstants.AbacusScreen.screenTypeSettingPreview,
                AppConstants.AbacusScreen.screenTypeLevel1Learn,
                AppConstants.AbacusScreen.screenTypeLevel1Practice,
                AppConstants.AbacusScreen.screenTypeLevel2Learn,
                AppConstants.AbacusScreen.screenTypeLevel2LearnMeetFormula,
                AppConstants.AbacusScreen.screenTypeLevel2Practice -> {
                    base.rectLineCorner / 2
                }

                else -> base.rectLineCorner
            },

            textSizeSp = when (screenType) {
                AppConstants.AbacusScreen.screenTypeExam,
                AppConstants.AbacusScreen.screenTypeExamResult -> 0

                AppConstants.AbacusScreen.screenTypeSettingPreview -> 7

                else -> 13
            }
        )
    }
}
