package com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.data.local.data.RodMovement
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorCoffee
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.base.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.base.abacus_base.AbacusTheme
import com.jigar.me.ui.view.base.abacus_base.components.AbacusAnswerBarCompose
import com.jigar.me.ui.view.base.abacus_base.components.NumberStripBar
import com.jigar.me.ui.view.base.abacus_base.components.OnlyWordStripBar
import com.jigar.me.ui.view.base.abacus_base.components.SpotlightOverlay
import com.jigar.me.ui.view.base.abacus_base.components.spotlightTag
import com.jigar.me.ui.view.base.abacus_base.freeModeHighlightSteps
import com.jigar.me.ui.view.base.abacus_base.utils.MathUtils
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens2
import com.jigar.me.utils.AppConstants
import kotlin.math.abs

@SuppressLint("LocalContextResourcesRead")
@Composable
fun AbacusWithDecimalCanvas(
    selectedTheme: String,
    screenType: String,
    isFreeModeOn: Boolean = false,
    isBeadSoundOn: Boolean,
    isDisplayCurrentAbacusInput: Boolean,
    abacusData: AbacusCalculations,
    numberOfColumns: Int,
    rodMovement: List<RodMovement>,      // kept for future arrow-on-canvas if needed
    showDirectionHint: Boolean,
    showHighlighter: Boolean = false,
    abacusType: String? = null,
    questionType: String? = null,
    isNextButtonEnable: Boolean = false,
    onRodMovementChange: (List<RodMovement>) -> Unit,
    onShowDirectionHintsChange: (Boolean) -> Unit,
    onShowHighlighterChange: (Boolean) -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    onReset: () -> Unit,
    onNext: () -> Unit,
) {

    val context = LocalContext.current

    LaunchedEffect(screenType,selectedTheme) {
        if (screenType == AppConstants.AbacusScreen.screenTypeSettingPreview) {
            val randomNumber = (101..999).random()
            abacusData.setAbacusValueFromString(randomNumber.toString())
        }
    }

    var currentSpot by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(showHighlighter) {
        currentSpot = if (showHighlighter) 0 else null
    }

    val arrowUp = remember {
        drawableToImageBitmap(context, R.drawable.ic_abacus_arrow_up)
    }
    val arrowDown = remember {
        drawableToImageBitmap(context, R.drawable.ic_abacus_arrow_down)
    }


    val dim = AbacusTheme.dimensionPreset(context,screenType = screenType,abacusType = abacusType,questionType = questionType, isFreeModeOn = isFreeModeOn)
    val totalWidth = (dim.beadWidth * numberOfColumns) + (dim.rectLineWidth * 2) + (dim.columnSpaces * (numberOfColumns) * 2)
    val totalHeight = (dim.beadHeight * 7) + (dim.rectLineWidth * 2) + (dim.extraSpace * 2) + dim.beamHeight

    val highlightSteps = freeModeHighlightSteps

    val strokeBrush = if (selectedTheme == "poligon_rainbow") {
        Brush.verticalGradient(listOf(Color(0xFFD7CCC8), Color(0xFFE0E0E0), Color(0xFFCFD8DC)))
    } else {
        val preset = AbacusTheme.colorPreset(selectedTheme)
        Brush.verticalGradient(listOf(preset.abacusTopGradient, preset.abacusCenterGradient, preset.abacusBottomGradient))
    }

    val textColor = if (selectedTheme == "poligon_rainbow") {
        ColorCoffee
    } else {
        Color.White
    }

    // ─────────────────────────────────────────────
    // Geometry for hit-testing and drawing
    // ─────────────────────────────────────────────
    val density = LocalDensity.current
    val geometry = remember(dim, numberOfColumns, density) {
        AbacusCanvasGeometry.build(dim, numberOfColumns, density)
    }

    // Pre-map rod movement by rod index (for future arrows on canvas)
    val rodMovementByRod = remember(rodMovement) {
        rodMovement.associateBy { it.rodIndex }
    }

    // ==========================
    //  Main Abacus Box
    // ==========================
    Box(modifier = modifier, contentAlignment = Alignment.TopCenter) {

        // Top Answer Bar (hidden in free mode, same as your old logic)
        if (!(screenType == AppConstants.AbacusScreen.screenTypeFreeMode && isFreeModeOn) && screenType != AppConstants.AbacusScreen.screenTypeSettingPreview && screenType != AppConstants.AbacusScreen.screenTypeExam && screenType != AppConstants.AbacusScreen.screenTypeExamResult
            && screenType != AppConstants.AbacusScreen.screenTypeLevel1Learn
            && screenType != AppConstants.AbacusScreen.screenTypeLevel1Practice
            && screenType != AppConstants.AbacusScreen.screenTypeLevel2Learn
            && screenType != AppConstants.AbacusScreen.screenTypeLevel2LearnMeetFormula
            && screenType != AppConstants.AbacusScreen.screenTypeLevel2Practice
            && screenType != AppConstants.AbacusScreen.screenTypeLevel3
            ) {
            val isSmallAnswerBar = screenType == AppConstants.AbacusScreen.screenTypeCCM || screenType == AppConstants.AbacusScreen.screenTypeExercise
            val answerBarScale = when {
                DeviceInfo.isLargeTablet -> if (isSmallAnswerBar) 1.35 else 1.5
                DeviceInfo.isTablet -> if (isSmallAnswerBar) 1.2 else 1.3
                else -> 1.0
            }
            val offsetY = ((if (isSmallAnswerBar) -36 else -48) * answerBarScale).dp

            AbacusAnswerBarCompose(
                answer = abacusData.displayValue,
                theme = selectedTheme,
                screenType = screenType,
                abacusType = abacusType,
                isNextButtonEnable = isNextButtonEnable,
                isDisplayAbacusNumber = isDisplayCurrentAbacusInput,
                onReset = {
                    abacusData.resetAbacusData()
                    onReset()
                    // reset abacus sound
                    if (isBeadSoundOn){
                        AudioPlayerManager.playAbacusReset()
                    }
                },
                onNext = {
                    onNext()
                    // abacus change sound same as reset
                    if (isBeadSoundOn && screenType != AppConstants.AbacusScreen.screenTypeCCM && screenType != AppConstants.AbacusScreen.screenTypeExercise){
                        AudioPlayerManager.playAbacusReset()
                    }else{
                        AudioPlayerManager.playSoundBtnClick()
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = offsetY)
            )
        }

        val context = LocalContext.current

        // Preload polygon bead image
        val beadPolygonGray = remember {
            ImageBitmap.imageResource(context.resources, R.drawable.poligon_gray_light)
        }

        // --- Inner rods & beads on Canvas ---

        if (!showHighlighter || currentSpot != 0) { // hide all things when frame highlighter show
            val isTouchEnabled = screenType != AppConstants.AbacusScreen.screenTypeLevel1Learn && screenType != AppConstants.AbacusScreen.screenTypeLevel1Practice && screenType != AppConstants.AbacusScreen.screenTypeLevel2Learn && screenType != AppConstants.AbacusScreen.screenTypeLevel2LearnMeetFormula && screenType != AppConstants.AbacusScreen.screenTypeLevel2Practice && screenType != AppConstants.AbacusScreen.screenTypeExam && screenType != AppConstants.AbacusScreen.screenTypeExamResult && screenType != AppConstants.AbacusScreen.screenTypeSettingPreview
            val gestureModifier = if (isTouchEnabled) {
                Modifier.pointerInput(Unit) {
                    val thresholdPx = with(density) { Dimens2.toPx() }

                    awaitPointerEventScope {
                        while (true) {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val pointerId = down.id

                            val startCol = geometry.findColumn(down.position.x)
                            val startIndex = geometry.findRow(down.position.y)

                            var totalDy = 0f

                            drag(pointerId) { change ->
                                val deltaY = change.position.y - change.previousPosition.y
                                totalDy += deltaY
                                change.consume()
                            }

                            if (startCol != null && startIndex != null &&
                                abs(totalDy) > thresholdPx
                            ) {
                                if (totalDy < 0) {
                                    if (abacusData.canMoveUp(startIndex, startCol)) {
                                        abacusData.moveBeadUp(startIndex, startCol)
                                        if (isBeadSoundOn) AudioPlayerManager.playAbacusMove()
                                    }
                                } else {
                                    if (abacusData.canMoveDown(startIndex, startCol)) {
                                        abacusData.moveBeadDown(startIndex, startCol)
                                        if (isBeadSoundOn) AudioPlayerManager.playAbacusMove()
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Modifier // 🚫 no touch
            }
            Canvas(
                modifier = Modifier
                    .height(totalHeight)
                    .width(totalWidth)
                    .padding(vertical = dim.rectLineWidth, horizontal = dim.rectLineWidth)
                    .align(Alignment.Center)
                    .then(gestureModifier)
                    .spotlightTag(1, highlightSteps[1].message) // rods highlight
            ) {
                // Draw all columns inside Canvas
                drawAbacusColumns(
                    screenType = screenType,
                    selectedTheme = selectedTheme,
                    abacusData = abacusData,
                    numberOfColumns = numberOfColumns,
                    dim = dim,
                    geometry = geometry,
                    showHighlighter = showHighlighter,
                    currentSpot = currentSpot,
                    rodMovementByRod = rodMovementByRod,
                    showDirectionHint = showDirectionHint,
                    beadPolygonGray = beadPolygonGray,
                    arrowUPBitmap = arrowUp,
                    arrowDownBitmap = arrowDown
                )
            }
        }

        // --- Outer frame ---
        if (screenType != AppConstants.AbacusScreen.screenTypeExam && screenType != AppConstants.AbacusScreen.screenTypeExamResult){
            val shape = RoundedCornerShape(dim.rectLineCorner)

            Box(
                modifier = Modifier
                    .width(totalWidth - (dim.columnSpaces * 2))
                    .height(totalHeight)

                    .drawBehind {
                        // 🔥 Shadow / glow (draw bigger + transparent)
                        drawRoundRect(
                            color = Color.Black.copy(alpha = 0.20f), // shadow color
                            size = size,
                            cornerRadius = CornerRadius(
                                dim.rectLineCorner.toPx(),
                                dim.rectLineCorner.toPx()
                            ),
                            style = Stroke(width = Dimens2.toPx()) // 👈 bigger than border
                        )
                    }

                    // ✅ Actual border on top
                    .border(
                        width = dim.rectLineWidth,
                        brush = strokeBrush,
                        shape = shape
                    )

                    .spotlightTag(0, highlightSteps[0].message)
            )
        }

        // Display number at top inside the frame (free-mode only)
        if ((screenType == AppConstants.AbacusScreen.screenTypeFreeMode && isFreeModeOn) || screenType == AppConstants.AbacusScreen.screenTypeSettingPreview) {
            Box(
                modifier = Modifier
                    .height(totalHeight)
                    .align(Alignment.TopCenter)
            ) {
                Text(
                    modifier = Modifier.height(dim.rectLineWidth),
                    text = abacusData.displayValue,
                    color = textColor,
                    fontSize = dim.textSizeSp.sp.scaled(),
                    lineHeight = dim.textSizeSp.sp.scaled(),
                    fontFamily = FontFamily(Font(R.font.font_extra_bold))
                )
            }
        }
    }

    // Number strip below abacus (same as before)
    if (screenType == AppConstants.AbacusScreen.screenTypeFreeMode) {
        if (isFreeModeOn){
            NumberStripBar(dim = dim, totalWidth = totalWidth, totalHeight = totalHeight)
        }
    }else if (screenType == AppConstants.AbacusScreen.screenTypeAbacusPractice || screenType == AppConstants.AbacusScreen.screenTypeCCM || screenType == AppConstants.AbacusScreen.screenTypeExercise) {
        OnlyWordStripBar(dim = dim, totalWidth = totalWidth, totalHeight = totalHeight)
    }

    // If highlighter is off, skip step rectangles
    if (!showHighlighter) return

    val totalBeadsHeight = totalHeight - (dim.rectLineWidth * 2)
    // width of one abacus column — used to position tour highlights by exact
    // rod index instead of hardcoded 13-column pixel offsets, so the tour
    // still points at the right rod when numberOfColumns is 7.
    val colUnit = dim.beadWidth + (dim.columnSpaces * 2)

    // 2️⃣ Beam (bar)
    Column(
        modifier = Modifier
            .height(totalHeight)
            .padding(dim.rectLineWidth)
    ) {
        Spacer(modifier = Modifier.height((dim.beadHeight * 2) + dim.extraSpace))
        Box(
            modifier = Modifier
                .width(totalWidth - (dim.rectLineWidth * 2))
                .height(dim.beamHeight)
                .spotlightTag(2, highlightSteps[2].message)
        )
        Spacer(modifier = Modifier.weight(1f))
    }

    // 3️⃣ Upper beads area
    Column(
        modifier = Modifier
            .height(totalHeight)
            .padding(dim.rectLineWidth)
    ) {
        Box(
            modifier = Modifier
                .width(totalWidth - (dim.rectLineWidth * 2))
                .height(dim.beadHeight + (dim.extraSpace * 2))
                .spotlightTag(3, highlightSteps[3].message)
        )
        Spacer(modifier = Modifier.weight(1f))
    }

    // 4️⃣ Lower beads area
    Column(
        modifier = Modifier
            .height(totalHeight)
            .padding(dim.rectLineWidth)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .width(totalWidth - (dim.rectLineWidth * 2))
                .height((dim.beadHeight * 4) + (dim.extraSpace * 2))
                .spotlightTag(4, highlightSteps[4].message)
        )
    }

    // 5️⃣ Unit place — ones place is always column index 6
    Column(
        modifier = Modifier
            .height(totalHeight)
            .padding(dim.rectLineWidth)
    ) {
        Row(
            modifier = Modifier
                .padding(top = (dim.beadHeight * 2) + dim.extraSpace)
        ) {
            Box(modifier = Modifier.width(colUnit * 6))
            Box(
                modifier = Modifier
                    .width(dim.beadWidth)
                    .height(dim.beamHeight)
                    .spotlightTag(5, highlightSteps[5].message)
            )
            Box(modifier = Modifier.width(colUnit * (numberOfColumns - 7)))
        }
        Spacer(modifier = Modifier.weight(1f))
    }

    // 6️⃣ First rods — ones place is always column index 6 (the integer part
    // is always the first 7 columns), so it's colUnit*6 from the left and
    // whatever's left (numberOfColumns - 7) from the right.
    Row(modifier = Modifier.padding(dim.rectLineWidth)) {
        Box(modifier = Modifier.width(colUnit * 6).height(totalBeadsHeight))
        Box(
            modifier = Modifier
                .width(dim.beadWidth)
                .height(totalBeadsHeight)
                .spotlightTag(6, highlightSteps[6].message)
        )
        Box(modifier = Modifier.width(colUnit * (numberOfColumns - 7)).height(totalBeadsHeight))
    }

    // 7️⃣ Second rods — tens place is always column index 5
    Row(modifier = Modifier.padding(dim.rectLineWidth)) {
        Box(
            modifier = Modifier
                .width(colUnit * 5)
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
                .width(colUnit * (numberOfColumns - 6))
                .height(totalBeadsHeight)
        )
    }

    // 8️⃣ Third rods — hundreds place is always column index 4
    Row(modifier = Modifier.padding(dim.rectLineWidth)) {
        Box(
            modifier = Modifier
                .width(colUnit * 4)
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
                .width(colUnit * (numberOfColumns - 5))
                .height(totalBeadsHeight)
        )
    }

    // 9️⃣ 1 rod = 0–9 — same position as step 6 (ones, column index 6)
    Row(modifier = Modifier.padding(dim.rectLineWidth)) {
        Box(modifier = Modifier.width(colUnit * 6).height(totalBeadsHeight))
        Box(
            modifier = Modifier
                .width(dim.beadWidth)
                .height(totalBeadsHeight)
                .spotlightTag(9, highlightSteps[9].message)
        )
        Box(modifier = Modifier.width(colUnit * (numberOfColumns - 7)).height(totalBeadsHeight))
    }

    // 🔟 2 rods = 0–99 — tens + ones, column indices 5-6
    Row(modifier = Modifier.padding(dim.rectLineWidth)) {
        Box(
            modifier = Modifier
                .width(colUnit * 5)
                .height(totalBeadsHeight)
        )
        Box(
            modifier = Modifier
                .width(colUnit * 2)
                .height(totalBeadsHeight)
                .spotlightTag(10, highlightSteps[10].message)
        )
        Box(
            modifier = Modifier
                .width(colUnit * (numberOfColumns - 7))
                .height(totalBeadsHeight)
        )
    }

    // 1️⃣1️⃣ 3 rods = 0–999 — hundreds + tens + ones, column indices 4-6
    Row(modifier = Modifier.padding(dim.rectLineWidth)) {
        Box(
            modifier = Modifier
                .width(colUnit * 4)
                .height(totalBeadsHeight)
        )
        Box(
            modifier = Modifier
                .width(colUnit * 3)
                .height(totalBeadsHeight)
                .spotlightTag(11, highlightSteps[11].message)
        )
        Box(
            modifier = Modifier
                .width(colUnit * (numberOfColumns - 7))
                .height(totalBeadsHeight)
        )
    }

    // 1️⃣2️⃣ Addition bottom bead — ones place, column index 6
    Row(modifier = Modifier.padding(dim.rectLineWidth)) {
        Box(modifier = Modifier.width(colUnit * 6))
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
        Box(modifier = Modifier.width(colUnit * (numberOfColumns - 7)))
    }

    // 1️⃣3️⃣ Addition top bead — ones place, column index 6
    Row(modifier = Modifier.padding(dim.rectLineWidth)) {
        Box(modifier = Modifier.width(colUnit * 6))
        Column(
            modifier = Modifier.height(totalHeight - (dim.rectLineWidth * 2))
        ) {
            Box(
                modifier = Modifier
                    .width(dim.beadWidth)
                    .height((dim.beadHeight * 2) + dim.extraSpace)
                    .spotlightTag(13, highlightSteps[13].message)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        Box(modifier = Modifier.width(colUnit * (numberOfColumns - 7)))
    }

    // 1️⃣4️⃣ Subtraction bottom bead — ones place, column index 6
    Row(modifier = Modifier.padding(dim.rectLineWidth)) {
        Box(modifier = Modifier.width(colUnit * 6))
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
        Box(modifier = Modifier.width(colUnit * (numberOfColumns - 7)))
    }

    // 1️⃣5️⃣ Subtraction top bead — ones place, column index 6
    Row(modifier = Modifier.padding(dim.rectLineWidth)) {
        Box(modifier = Modifier.width(colUnit * 6))
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
        Box(modifier = Modifier.width(colUnit * (numberOfColumns - 7)))
    }

    // ==========================
    //  Spotlight Overlay & Steps
    //  (kept same as your original)
    // ==========================
    if (currentSpot != null && currentSpot!! >= 0) {
        // Overlay with blur + clear hole + tooltip
        SpotlightOverlay(
            currentSpot = currentSpot,
            onNext = {
                val nextSpot = if (currentSpot == highlightSteps.lastIndex) {
                    null
                } else {
                    currentSpot!! + 1
                }
                currentSpot = nextSpot

                // reset abacus + remove arrows first
                abacusData.resetAbacusData()
                onRodMovementChange(emptyList())
                onShowDirectionHintsChange(false)

                if (nextSpot == null) {
                    // tour finished
                    abacusData.setAbacusValueFromString("0")
                    onShowHighlighterChange(false)
                    return@SpotlightOverlay
                }

                // demo values below are built as "<leading digits>" + trailing
                // zeros so they land on the correct rod (ones/tens/hundreds are
                // always columns 6/5/4) regardless of numberOfColumns, instead
                // of fixed 7-9 digit literals that only lined up at 13 columns.
                val trailingZerosFromOnes = "0".repeat(numberOfColumns - 7)
                when (nextSpot) {
                    6, 9 -> abacusData.setAbacusValueFromString("9$trailingZerosFromOnes")
                    7 -> abacusData.setAbacusValueFromString("9" + "0".repeat(numberOfColumns - 6))
                    8 -> abacusData.setAbacusValueFromString("9" + "0".repeat(numberOfColumns - 5))
                    10 -> abacusData.setAbacusValueFromString("99$trailingZerosFromOnes")
                    11 -> abacusData.setAbacusValueFromString("999$trailingZerosFromOnes")

                    12 -> {
                        val list = MathUtils.calculateRodMovements(from = 0, to = 4, rods = 1, isForRightRods = false)
                        onRodMovementChange(list)
                        onShowDirectionHintsChange(true)
                    }

                    13 -> {
                        val list = MathUtils.calculateRodMovements(from = 0, to = 5, rods = 1, isForRightRods = false)
                        onRodMovementChange(list)
                        onShowDirectionHintsChange(true)
                    }

                    14 -> {
                        abacusData.setAbacusValueFromString("4$trailingZerosFromOnes")
                        val list = MathUtils.calculateRodMovements(from = 4, to = 0, rods = 1, isForRightRods = false)
                        onRodMovementChange(list)
                        onShowDirectionHintsChange(true)
                    }

                    15 -> {
                        abacusData.setAbacusValueFromString("5$trailingZerosFromOnes")
                        val list = MathUtils.calculateRodMovements(from = 5, to = 0, rods = 1, isForRightRods = false)
                        onRodMovementChange(list)
                        onShowDirectionHintsChange(true)
                    }

                    else -> {
                        abacusData.setAbacusValueFromString("0")
                    }
                }
            }
        )
    }
}



