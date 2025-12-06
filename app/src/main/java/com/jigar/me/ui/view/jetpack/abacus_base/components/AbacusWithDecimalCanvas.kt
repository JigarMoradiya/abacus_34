package com.jigar.me.ui.view.jetpack.abacus_base.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.jigar.me.R
import com.jigar.me.data.local.data.Movement
import com.jigar.me.data.local.data.RodMovement
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusDimensionModel
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusTheme
import com.jigar.me.ui.view.jetpack.abacus_base.ColorPresets
import com.jigar.me.ui.view.jetpack.abacus_base.freeModeHighlightSteps
import com.jigar.me.utils.AppConstants
import kotlin.math.abs
import androidx.core.graphics.createBitmap

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
    abacusData: AbacusCalculations,
    numberOfColumns: Int,
    rodMovement: List<RodMovement>,      // kept for future arrow-on-canvas if needed
    showDirectionHint: Boolean,
    showHighlighter: Boolean,
    onRodMovementChange: (List<RodMovement>) -> Unit,
    onShowDirectionHintsChange: (Boolean) -> Unit,
    onShowHighlighterChange: (Boolean) -> Unit,
    currentSpot: Int?,
    onSpotChange: (Int?) -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {

    val context = LocalContext.current

    val arrowUp = remember {
        drawableToImageBitmap(context, R.drawable.ic_abacus_arrow_up)
    }
    val arrowDown = remember {
        drawableToImageBitmap(context, R.drawable.ic_abacus_arrow_down)
    }


    val dim = AbacusTheme.dimensionPreset(
        screenType = screenType,
        isFreeModeOn = isFreeModeOn
    )

    val totalWidth =
        (dim.beadWidth * numberOfColumns) +
                (dim.rectLineWidth * 2) +
                (dim.columnSpaces * (numberOfColumns) * 2)

    val totalHeight =
        (dim.beadHeight * 7) +
                (dim.rectLineWidth * 2) +
                (dim.extraSpace * 2) +
                dim.beamHeight

    val highlightSteps = freeModeHighlightSteps

    val strokeBrush = if (selectedTheme == "poligon_rainbow") {
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
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {

        // Top Answer Bar (hidden in free mode, same as your old logic)
        if (!isFreeModeOn) {
            val offsetY = (-48).dp

            AbacusAnswerBarCompose(
                answer = abacusData.displayValue,
                theme = selectedTheme,
                screenType = screenType,
                abacusType = null,
                isDisplayAbacusNumber = true,
                onReset = {
                    abacusData.resetAbacusData()
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
            ImageBitmap.imageResource(context.resources, R.drawable.poligon_gray)
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
        Canvas(
            modifier = Modifier
                .height(totalHeight)
                .width(totalWidth)
                .alpha(if (currentSpot == 0) 0f else 1f) // step 0 = frame only
                .padding(vertical = dim.rectLineWidth, horizontal = dim.rectLineWidth)
                .align(Alignment.Center)
                .pointerInput(Unit) {
                    // DRAG HANDLING: map drag to column + bead index
                    val thresholdPx = with(density) { 8.dp.toPx() }
                    var startCol: Int? = null
                    var startIndex: Int? = null
                    var totalDy = 0f

                    detectDragGestures(
                        onDragStart = { offset ->
                            startCol = geometry.findColumn(offset.x)
                            startIndex = geometry.findRow(offset.y)
                            totalDy = 0f
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            totalDy += dragAmount.y
                        },
                        onDragCancel = {
                            startCol = null
                            startIndex = null
                            totalDy = 0f
                        },
                        onDragEnd = {
                            val col = startCol
                            val idx = startIndex
                            if (col != null && idx != null && abs(totalDy) > thresholdPx) {
                                if (totalDy < 0) {
                                    if (abacusData.canMoveUp(idx, col)) {
                                        abacusData.moveBeadUp(idx, col)
                                    }
                                } else {
                                    if (abacusData.canMoveDown(idx, col)) {
                                        abacusData.moveBeadDown(idx, col)
                                    }
                                }
                            }
                            startCol = null
                            startIndex = null
                            totalDy = 0f
                        }
                    )
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

    // ==========================
    //  Spotlight Overlay & Steps
    //  (kept same as your original)
    // ==========================
    if (currentSpot != null && currentSpot >= 0) {

        // Overlay with blur + clear hole + tooltip
        SpotlightOverlay(
            currentSpot = currentSpot,
            onNext = { onSpotChange(currentSpot + 1) }
        )

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
    }
}

// ─────────────────────────────────────────────────────────────
// Geometry helper for Canvas-based Abacus
// ─────────────────────────────────────────────────────────────

data class AbacusCanvasGeometry(
    val columnCentersX: List<Float>,
    val rowTop: List<Float>,
    val rowBottom: List<Float>,
    val beadWidthPx: Float,
    val beadHeightPx: Float,
    val beamHeightPx: Float,
    val columnSpacesPx: Float,
    val extraSpacePx: Float
) {
    fun findColumn(x: Float): Int? {
        val colWidth = beadWidthPx + columnSpacesPx * 2f
        if (colWidth <= 0f) return null
        val index = (x / colWidth).toInt()
        return if (index in columnCentersX.indices) index else null
    }

    fun findRow(y: Float): Int? {
        for (i in rowTop.indices) {
            if (y >= rowTop[i] && y <= rowBottom[i]) return i
        }
        return null
    }

    companion object {
        fun build(dim: AbacusDimensionModel, numberOfColumns: Int, density: Density): AbacusCanvasGeometry = with(density) {

            val beadW = dim.beadWidth.toPx()
            val beadH = dim.beadHeight.toPx()
            val beamH = dim.beamHeight.toPx()
            val spacing = dim.columnSpaces.toPx()
            val extra = dim.extraSpace.toPx()

            // Column centers
            val colWidth = beadW + spacing * 2
            val centers = List(numberOfColumns) { i ->
                spacing + beadW / 2 + i * colWidth
            }

            // -------------------------
            // VERTICAL ROW POSITIONS
            // -------------------------

            val rowTop = FloatArray(7)
            val rowBottom = FloatArray(7)

            var y = extra

            // Row 0 (upper bead 1)
            rowTop[0] = y
            rowBottom[0] = y + beadH
            y = rowBottom[0]

            // Row 1 (upper bead 2)
            rowTop[1] = y
            rowBottom[1] = y + beadH
            y = rowBottom[1]

            // Beam before row 2 — Center the beam
            val beamTop = y
            val bead2Top = beamTop + beamH

            // Row 2 (first lower bead)
            rowTop[2] = bead2Top
            rowBottom[2] = bead2Top + beadH

            y = rowBottom[2]

            // Row 3–6 (remaining lower beads)
            for (i in 3..6) {
                rowTop[i] = y
                rowBottom[i] = y + beadH
                y = rowBottom[i]
            }

            return AbacusCanvasGeometry(
                columnCentersX = centers,
                rowTop = rowTop.toList(),
                rowBottom = rowBottom.toList(),
                beadWidthPx = beadW,
                beadHeightPx = beadH,
                beamHeightPx = beamH,
                columnSpacesPx = spacing,
                extraSpacePx = extra
            )
        }
    }

}

// ─────────────────────────────────────────────────────────────
// Draw columns + beads inside Canvas
// ─────────────────────────────────────────────────────────────

private fun DrawScope.drawAbacusColumns(
    selectedTheme: String,
    abacusData: AbacusCalculations,
    numberOfColumns: Int,
    dim: AbacusDimensionModel,
    geometry: AbacusCanvasGeometry,
    showHighlighter: Boolean,
    currentSpot: Int?,
    rodMovementByRod: Map<Int, RodMovement>,
    showDirectionHint: Boolean,
    beadPolygonGray: ImageBitmap,
    faceOpen: Map<Int, ImageBitmap>,
    faceClose: Map<Int, ImageBitmap>,
    arrowUPBitmap : ImageBitmap,
    arrowDownBitmap : ImageBitmap,
) {
    val preset = AbacusTheme.colorPreset(selectedTheme)
    val isPolygonTheme = selectedTheme.contains("poligon", ignoreCase = true)
    val isRainbow = selectedTheme.equals("poligon_rainbow", ignoreCase = true)

    val beadH = geometry.beadHeightPx
    val beamH = geometry.beamHeightPx
    val extra = geometry.extraSpacePx

    // Helper: Y of bead *top* for a given index (0..6)
    fun beadTopForIndex(idx: Int): Float {
        return 0.5F + extra + beadH * idx + if (idx >= 2) beamH else 0f
    }

    // Beam band is between row 1 and row 2
    val beamBandTop = extra + beadH * 2        // bottom of row 1
    val beamBandBottom = beamBandTop + beamH

    for (col in 0 until numberOfColumns) {
        val columnState = abacusData.abacusState[col]
        val isCentralColumn = (col == 6)
        val isRedDotColumn = col == numberOfColumns - 1 ||
                col == numberOfColumns - 4 ||
                col == numberOfColumns - 7 ||
                col == numberOfColumns - 10 ||
                col == numberOfColumns - 13

        val columnColor = when {
            isRainbow -> ColorPresets.getMixColorListOfPoligon()[col].copy(alpha = 0.2f)
            isPolygonTheme -> preset.columnColors.copy(alpha = 0.3f)
            else -> preset.columnColors.copy(alpha = 0.3f)
        }

        val xCenter = geometry.columnCentersX[col]
        val stickWidth = beamH / 2f

        // ───────── Stick (rod) ─────────
        drawRoundRect(
            color = columnColor,
            topLeft = Offset(
                x = xCenter - stickWidth / 2f,
                y = 0f
            ),
            size = Size(
                width = stickWidth,
                height = size.height
            ),
            cornerRadius = CornerRadius(stickWidth / 2f, stickWidth / 2f),
            alpha = when {
                showHighlighter && currentSpot == 1 -> 1f
                isCentralColumn -> 1f
                else -> 0.3f
            }
        )

        // Compute "active" mask (same as ColumnViewCompose)
        val arr = MutableList(columnState.size) { false }.also { mask ->
            if (columnState[1]) mask[1] = true
            for (i in 2 until columnState.size) {
                if (columnState[i]) mask[i] = true else break
            }
        }

        // ───────── Beam band + red dot (shared for the column) ─────────
        run {
            val beamWidth = geometry.beadWidthPx + geometry.columnSpacesPx * 2f

            drawRoundRect(
                color = preset.abacusCenterGradient.copy(alpha = 0.3f),
                topLeft = Offset(
                    x = xCenter - beamWidth / 2f,
                    y = beamBandTop + (beamH / 4f)
                ),
                size = Size(
                    width = beamWidth,
                    height = beamH / 2f
                ),
                cornerRadius = CornerRadius(8f, 8f)
            )

            if (isRedDotColumn) {
                val dotRadius =
                    if (isCentralColumn) beamH / 2f else beamH * 0.25f

                drawCircle(
                    color = if (isCentralColumn) Color.White else preset.buttonColor,
                    radius = dotRadius,
                    center = Offset(
                        x = xCenter,
                        y = beamBandTop + (beamH / 2f)
                    )
                )
            }
        }

        // ───────── Per bead index (0..6) ─────────
        columnState.indices.forEach { idx ->
            val beadTop = beadTopForIndex(idx)

            // ---------- Bead image ----------
            if (columnState[idx]) {
                val beadIsActive = arr[idx]

                val imageToDraw: ImageBitmap =
                    if (isPolygonTheme) {
                        beadPolygonGray
                    } else if (beadIsActive) {
                        faceOpen[idx] ?: faceClose[0]!!
                    } else {
                        faceClose[idx] ?: faceClose[0]!!
                    }

                val baseColor =
                    if (isRainbow) ColorPresets.getMixColorListOfPoligon()[col]
                    else preset.abacusTopGradient

                val tintColor =
                    if (!isPolygonTheme) Color.White
                    else if (beadIsActive)
                        baseColor.copy(alpha = 0.9f)   // active, brighter
                    else
                        baseColor.copy(alpha = 0.6f)   // inactive, dimmer

                drawImage(
                    image = imageToDraw,
                    srcOffset = IntOffset.Zero,
                    srcSize = IntSize(imageToDraw.width, imageToDraw.height),
                    dstOffset = IntOffset(
                        (xCenter - geometry.beadWidthPx / 2f).toInt(),
                        beadTop.toInt()
                    ),
                    dstSize = IntSize(
                        geometry.beadWidthPx.toInt(),
                        geometry.beadHeightPx.toInt()
                    ),
                    colorFilter = if (isPolygonTheme)
                        ColorFilter.tint(tintColor, BlendMode.SrcIn)
                    else null
                )
            }

            // ───────── Arrows (direction hints) ─────────
            if (showDirectionHint) {
                val movement = rodMovementByRod[col]?.movement
                if (movement != null) {
                    val arrowColor = AbacusTheme.colorPreset(selectedTheme).arrowColor
                    drawArrowForBeadCanvas(
                        colCenterX = xCenter,
                        rowIndex = idx,
                        movement = movement,
                        geometry = geometry,
                        isPolygon = isPolygonTheme,
                        arrowColor = arrowColor,
                        arrowUp = arrowUPBitmap,
                        arrowDown = arrowDownBitmap,
                    )
                }
            }
        }
    }
}

fun drawableToImageBitmap(context: Context, resId: Int): ImageBitmap {
    val drawable = AppCompatResources.getDrawable(context, resId)!!

    val bmp = createBitmap(drawable.intrinsicWidth.takeIf { it > 0 } ?: 64, drawable.intrinsicHeight.takeIf { it > 0 } ?: 64)

    val canvas = android.graphics.Canvas(bmp)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bmp.asImageBitmap()
}


// ----------------------------------------------------------------------
// Draw arrows on Canvas (equivalent of ArrowForBeadFull, but for Canvas)
// ----------------------------------------------------------------------
fun DrawScope.drawArrowForBeadCanvas(
    colCenterX: Float,
    rowIndex: Int,
    movement: Movement,
    geometry: AbacusCanvasGeometry,
    isPolygon: Boolean,
    arrowColor: Color,
    arrowUp: ImageBitmap,
    arrowDown: ImageBitmap
) {
    val arrowSize = (geometry.beadHeightPx * 0.70f).toInt()

    fun drawArrow(isUp: Boolean) {
        val img = if (isUp) arrowUp else arrowDown
        val centerY = geometry.rowTop[rowIndex] + geometry.beadHeightPx / 2f

        drawImage(
            image = img,
            dstOffset = IntOffset(
                (colCenterX - arrowSize / 2f).toInt(),
                (centerY - arrowSize / 2f).toInt()
            ),
            dstSize = IntSize(arrowSize, arrowSize),
            colorFilter = ColorFilter.tint(arrowColor)
        )
    }

    // -------------------- UPPER BEADS --------------------
    if (rowIndex == 0 && movement.upperDown) {
        drawArrow(false); return
    }
    if (rowIndex == 1 && movement.upperUp) {
        drawArrow(true); return
    }

    // -------------------- LOWER BEADS DOWN --------------------
    if (movement.lowerDown > 0) {
        val d = movement.lowerDown
        val old = movement.lowerOldValue
        val idx = rowIndex

        if (old >= d) {
            when (d) {
                1 -> if (idx == old + 1) drawArrow(false)
                2 -> if (idx in listOf(old, old + 1)) drawArrow(false)
                3 -> if (idx in listOf(old - 1, old, old + 1)) drawArrow(false)
                4 -> if (idx in 2..5) drawArrow(false)
            }
        }
        return
    }

    // -------------------- LOWER BEADS UP --------------------
    if (movement.lowerUp > 0) {
        val up = movement.lowerUp
        val old = movement.lowerOldValue
        val idx = rowIndex

        when (old) {
            0 -> if (idx in 3 until 3 + up) drawArrow(true)
            1 -> if (idx in 4 until 4 + up) drawArrow(true)
            2 -> if (idx in 5 until 5 + up) drawArrow(true)
            3 -> if (idx == 6) drawArrow(true)
        }
    }
}


