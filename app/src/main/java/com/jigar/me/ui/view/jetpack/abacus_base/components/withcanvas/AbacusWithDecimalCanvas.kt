package com.jigar.me.ui.view.jetpack.abacus_base.components.withcanvas

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.data.local.data.RodMovement
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusTheme
import com.jigar.me.ui.view.jetpack.abacus_base.components.AbacusAnswerBarCompose
import com.jigar.me.ui.view.jetpack.abacus_base.components.NumberStripBar
import com.jigar.me.ui.view.jetpack.abacus_base.components.SpotlightOverlay
import com.jigar.me.ui.view.jetpack.abacus_base.components.spotlightTag
import com.jigar.me.ui.view.jetpack.abacus_base.freeModeHighlightSteps
import com.jigar.me.ui.view.jetpack.abacus_base.utils.MathUtils
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.PlaySound
import kotlin.math.abs

// ─────────────────────────────────────────────────────────────
// Canvas-based AbacusWithDecimal
//  - Beads + sticks + beam drawn on Canvas
//  - Uses AbacusCalculations for state
//  - Drag up/down on beads to move them
//  - Keeps answer bar, frame, number strip, and spotlight logic
// ─────────────────────────────────────────────────────────────

@SuppressLint("LocalContextResourcesRead")
@Composable
fun AbacusWithDecimalCanvas(
    selectedTheme: String,
    screenType: String,
    isFreeModeOn: Boolean,
    isBeadSoundOn: Boolean,
    isDisplayCurrentAbacusInput: Boolean,
    abacusData: AbacusCalculations,
    numberOfColumns: Int,
    rodMovement: List<RodMovement>,      // kept for future arrow-on-canvas if needed
    showDirectionHint: Boolean,
    showHighlighter: Boolean,
    onRodMovementChange: (List<RodMovement>) -> Unit,
    onShowDirectionHintsChange: (Boolean) -> Unit,
    onShowHighlighterChange: (Boolean) -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {

    val context = LocalContext.current
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


    val dim = AbacusTheme.dimensionPreset(screenType = screenType, isFreeModeOn = isFreeModeOn)
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
        Color(0xFF5D4037)
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
        if (!isFreeModeOn) {
            val offsetY = (-48).dp

            AbacusAnswerBarCompose(
                answer = abacusData.displayValue,
                theme = selectedTheme,
                screenType = screenType,
                abacusType = null,
                isDisplayAbacusNumber = isDisplayCurrentAbacusInput,
                onReset = {
                    abacusData.resetAbacusData()
                    // reset abacus sound
                    if (isBeadSoundOn){
                        PlaySound.playBeadReset(context)
                    }
                },
                onNext = {
                    // Hook for "next" – screen can handle if needed
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

        // Preload face-open beads
        val faceOpen = remember {
            mapOf(
                1 to ImageBitmap.imageResource(context.resources, R.drawable.face_red_open),
                2 to ImageBitmap.imageResource(context.resources, R.drawable.face_pink_open),
                3 to ImageBitmap.imageResource(context.resources, R.drawable.face_orange_open),
                4 to ImageBitmap.imageResource(context.resources, R.drawable.face_blue_open),
                5 to ImageBitmap.imageResource(context.resources, R.drawable.face_green_open)
            )
        }

        // Preload face-close beads
        val faceClose = remember {
            mapOf(
                0 to ImageBitmap.imageResource(context.resources, R.drawable.face_red_close),
                3 to ImageBitmap.imageResource(context.resources, R.drawable.face_pink_close),
                4 to ImageBitmap.imageResource(context.resources, R.drawable.face_orange_close),
                5 to ImageBitmap.imageResource(context.resources, R.drawable.face_blue_close),
                6 to ImageBitmap.imageResource(context.resources, R.drawable.face_green_close)
            )
        }

        // --- Inner rods & beads on Canvas ---
        if (!showHighlighter || currentSpot != 0) { // hide all things when frame highlighter show
            Canvas(
                modifier = Modifier
                    .height(totalHeight)
                    .width(totalWidth)
                    .padding(vertical = dim.rectLineWidth, horizontal = dim.rectLineWidth)
                    .align(Alignment.Center)
                    .pointerInput(Unit) {
                        val thresholdPx = with(density) { 2.dp.toPx() }

                        awaitPointerEventScope {
                            while (true) {
                                // 1️⃣ Wait for first finger down
                                val down = awaitFirstDown(requireUnconsumed = false)
                                val pointerId = down.id

                                val startCol = geometry.findColumn(down.position.x)
                                val startIndex = geometry.findRow(down.position.y)

                                var totalDy = 0f

                                // 2️⃣ Smooth drag
                                drag(pointerId) { change ->
                                    val deltaY = change.position.y - change.previousPosition.y
                                    totalDy += deltaY
                                    change.consume()
                                }

                                // 3️⃣ Move bead if drag is enough
                                if (startCol != null && startIndex != null &&
                                    abs(totalDy) > thresholdPx
                                ) {
                                    if (totalDy < 0) {
                                        if (abacusData.canMoveUp(startIndex, startCol)) {
                                            abacusData.moveBeadUp(startIndex, startCol)
                                            if (isBeadSoundOn) PlaySound.playBeadClick(context)
                                        }
                                    } else {
                                        if (abacusData.canMoveDown(startIndex, startCol)) {
                                            abacusData.moveBeadDown(startIndex, startCol)
                                            if (isBeadSoundOn) PlaySound.playBeadClick(context)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    .spotlightTag(1, highlightSteps[1].message) // rods highlight
            ) {
                // Draw all columns inside Canvas
                drawAbacusColumns(
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
                    faceOpen = faceOpen,
                    faceClose = faceClose,
                    arrowUPBitmap = arrowUp,
                    arrowDownBitmap = arrowDown
                )
            }
        }

        // --- Outer frame ---
        Box(
            modifier = Modifier
                .width(totalWidth - (dim.columnSpaces * 2))
                .height(totalHeight)
                .border(
                    width = dim.rectLineWidth,
                    brush = strokeBrush,
                    shape = RoundedCornerShape(dim.rectLineCorner)
                )
                .spotlightTag(0, highlightSteps[0].message) // frame highlight
        )

        // Display number at top inside the frame (free-mode only)
        if (screenType == AppConstants.AbacusScreen.screenTypeFreeMode && isFreeModeOn) {
            Box(
                modifier = Modifier
                    .height(totalHeight)
                    .align(Alignment.TopCenter)
            ) {
                Text(
                    modifier = Modifier.height(dim.rectLineWidth),
                    text = abacusData.displayValue,
                    color = textColor,
                    fontSize = dim.textSizeSp.sp,
                    lineHeight = dim.textSizeSp.sp,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold))
                )
            }
        }
    }

    // Number strip below abacus (same as before)
    if (screenType == AppConstants.AbacusScreen.screenTypeFreeMode && isFreeModeOn) {
        NumberStripBar(
            dim = dim,
            totalWidth = totalWidth,
            totalHeight = totalHeight
        )
    }

    // If highlighter is off, skip step rectangles
    if (!showHighlighter) return

    val totalBeadsHeight = totalHeight - (dim.rectLineWidth * 2)

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

    // 5️⃣ Unit place
    Column(
        modifier = Modifier
            .height(totalHeight)
            .padding(dim.rectLineWidth)
    ) {
        Row(
            modifier = Modifier
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

    // 6️⃣ First rods
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

    // 7️⃣ Second rods
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

    // 8️⃣ Third rods
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

    // 9️⃣ 1 rod = 0–9
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

    // 🔟 2 rods = 0–99
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

    // 1️⃣1️⃣ 3 rods = 0–999
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

    // 1️⃣2️⃣ Addition bottom bead
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

    // 1️⃣3️⃣ Addition top bead
    Column(
        modifier = Modifier
            .height(totalHeight)
            .padding(dim.rectLineWidth)
    ) {
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

    // 1️⃣4️⃣ Subtraction bottom bead
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

    // 1️⃣5️⃣ Subtraction top bead
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

                when (nextSpot) {
                    6, 9 -> abacusData.setAbacusValueFromString("9000000")
                    7 -> abacusData.setAbacusValueFromString("90000000")
                    8 -> abacusData.setAbacusValueFromString("900000000")
                    10 -> abacusData.setAbacusValueFromString("99000000")
                    11 -> abacusData.setAbacusValueFromString("999000000")

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
                        abacusData.setAbacusValueFromString("4000000")
                        val list = MathUtils.calculateRodMovements(from = 4, to = 0, rods = 1, isForRightRods = false)
                        onRodMovementChange(list)
                        onShowDirectionHintsChange(true)
                    }

                    15 -> {
                        abacusData.setAbacusValueFromString("5000000")
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




