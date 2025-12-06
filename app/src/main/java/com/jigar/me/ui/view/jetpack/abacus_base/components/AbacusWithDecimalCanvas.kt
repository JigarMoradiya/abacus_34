package com.jigar.me.ui.view.jetpack.abacus_base.components

import android.annotation.SuppressLint
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
import com.jigar.me.R
import com.jigar.me.data.local.data.Movement
import com.jigar.me.data.local.data.RodMovement
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusDimensionModel
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusTheme
import com.jigar.me.ui.view.jetpack.abacus_base.ColorPresets
import com.jigar.me.ui.view.jetpack.abacus_base.freeModeHighlightSteps
import com.jigar.me.utils.AppConstants

// ─────────────────────────────────────────────────────────────
// Canvas-based AbacusWithDecimal
//  - Beads + sticks + beam drawn on Canvas
//  - Uses AbacusCalculations for state
//  - Drag up/down on beads to move them
//  - Keeps answer bar, frame, number strip, and spotlight logic
// ─────────────────────────────────────────────────────────────

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
                            if (col != null && idx != null && kotlin.math.abs(totalDy) > thresholdPx) {
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
                showDirectionHint = showDirectionHint
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
        fun build(dim: AbacusDimensionModel, numberOfColumns: Int, density: androidx.compose.ui.unit.Density): AbacusCanvasGeometry {
            return with(density) {
                val beadWidthPx = dim.beadWidth.toPx()
                val beadHeightPx = dim.beadHeight.toPx()
                val beamHeightPx = dim.beamHeight.toPx()
                val columnSpacesPx = dim.columnSpaces.toPx()
                val extraSpacePx = dim.extraSpace.toPx()

                val colWidth = beadWidthPx + columnSpacesPx * 2f
                val centers = List(numberOfColumns) { index ->
                    columnSpacesPx + beadWidthPx / 2f + index * colWidth
                }

                val rowTop = FloatArray(7)
                val rowBottom = FloatArray(7)

                var y = extraSpacePx
                // index 0
                rowTop[0] = y
                rowBottom[0] = y + beadHeightPx
                y = rowBottom[0]

                // index 1
                rowTop[1] = y
                rowBottom[1] = y + beadHeightPx
                y = rowBottom[1]

                // index 2: beam + bead
                rowTop[2] = y + beamHeightPx       // bead starts after beam
                rowBottom[2] = rowTop[2] + beadHeightPx
                y = rowBottom[2]

                for (i in 3..6) {
                    rowTop[i] = y
                    rowBottom[i] = y + beadHeightPx
                    y = rowBottom[i]
                }

                AbacusCanvasGeometry(
                    columnCentersX = centers,
                    rowTop = rowTop.toList(),
                    rowBottom = rowBottom.toList(),
                    beadWidthPx = beadWidthPx,
                    beadHeightPx = beadHeightPx,
                    beamHeightPx = beamHeightPx,
                    columnSpacesPx = columnSpacesPx,
                    extraSpacePx = extraSpacePx
                )
            }
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
    showDirectionHint: Boolean
) {
    val preset = AbacusTheme.colorPreset(selectedTheme)

    for (col in 0 until numberOfColumns) {
        val columnState = abacusData.abacusState[col]
        val isCentralColumn = (col == 6)
        val isRedDotColumn = col == numberOfColumns - 1 ||
                col == numberOfColumns - 4 ||
                col == numberOfColumns - 7 ||
                col == numberOfColumns - 10 ||
                col == numberOfColumns - 13

        val columnColor = if (selectedTheme.contains("poligon_rainbow")) {
            ColorPresets.getMixColorListOfPoligon()[col].copy(alpha = 0.2f)
        } else {
            preset.columnColors.copy(alpha = 0.3f)
        }

        val xCenter = geometry.columnCentersX[col]
        val stickWidth = geometry.beamHeightPx / 2f

        // Stick
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

        // Compute "active" mask: same logic as ColumnViewCompose
        val arr = MutableList(columnState.size) { false }
        if (columnState[1]) arr[1] = true
        for (i in 2 until columnState.size) {
            if (columnState[i]) arr[i] = true else break
        }

        // Draw per row
        for (idx in columnState.indices) {
            val top = geometry.rowTop[idx]
            val bottom = geometry.rowBottom[idx]
            val centerY = (top + bottom) / 2f

            // Beam row (index 2) has separate bar & red dot
            if (idx == 2) {
                // Beam background
                val beamWidth = geometry.beadWidthPx + geometry.columnSpacesPx * 2f
                val beamHeightHalf = geometry.beamHeightPx / 2f
                val beamTop = top - geometry.beamHeightPx   // center beam between 1 and 2

                drawRoundRect(
                    color = preset.abacusCenterGradient.copy(alpha = 0.3f),
                    topLeft = Offset(
                        x = xCenter - beamWidth / 2f,
                        y = beamTop + beamHeightHalf / 2f
                    ),
                    size = Size(
                        width = beamWidth,
                        height = beamHeightHalf
                    ),
                    cornerRadius = CornerRadius(beamHeightHalf / 2f, beamHeightHalf / 2f)
                )

                if (isRedDotColumn) {
                    val dotRadius = if (isCentralColumn) geometry.beamHeightPx / 2f else geometry.beamHeightPx * 0.25f
                    drawCircle(
                        color = if (isCentralColumn) Color.White else preset.buttonColor,
                        radius = dotRadius,
                        center = Offset(
                            x = xCenter,
                            y = beamTop + geometry.beamHeightPx / 2f
                        )
                    )
                }
            }

            // If bead is "down" (visible slot) draw it
            if (columnState[idx]) {
                val beadColor =
                    if (selectedTheme.contains("poligon", ignoreCase = true)) {
                        // approximate polygon tint: lighter when active
                        val base = if (selectedTheme == "poligon_rainbow") {
                            ColorPresets.getMixColorListOfPoligon()[col]
                        } else {
                            preset.abacusTopGradient
                        }
                        if (arr[idx]) base else base.copy(alpha = 0.6f)
                    } else {
                        // non polygon themes: simple solid (faces would need images)
                        if (arr[idx]) preset.buttonColor else preset.buttonColor.copy(alpha = 0.5f)
                    }

                drawRoundRect(
                    color = beadColor,
                    topLeft = Offset(
                        x = xCenter - geometry.beadWidthPx / 2f,
                        y = centerY - geometry.beadHeightPx / 2f
                    ),
                    size = Size(geometry.beadWidthPx, geometry.beadHeightPx),
                    cornerRadius = CornerRadius(geometry.beadHeightPx / 2f, geometry.beadHeightPx / 2f)
                )
            }

            // ============== DRAW ARROWS (Canvas Version) ==============
            if (showDirectionHint) {
                val movement = rodMovementByRod[col]?.movement
                if (movement != null) {
                    drawArrowForBeadCanvas(
                        colCenterX = xCenter,
                        rowIndex = idx,
                        movement = movement,
                        geometry = geometry,
                        isPolygon = selectedTheme.contains("poligon", ignoreCase = true)
                    )
                }
            }
        }



    }
}

private fun DrawScope.drawArrowForBeadCanvas(
    colCenterX: Float,
    rowIndex: Int,
    movement: Movement,
    geometry: AbacusCanvasGeometry,
    isPolygon: Boolean
) {
//    val arrowColor = preset.arrowColor
    val arrowColor = Color.Black
    val beadW = geometry.beadWidthPx
    val beadH = geometry.beadHeightPx
    val beamH = geometry.beamHeightPx

    // ---- Compute base Y using row geometry ----
    val top = geometry.rowTop[rowIndex]
    val bottom = geometry.rowBottom[rowIndex]
    val centerY = (top + bottom) / 2f

    // ---- Arrow size ----
    val arrowLength = beadH * 0.55f
    val arrowWidth = beadW * 0.25f

    // ---- Draw helper ----
    fun drawArrowUp() {
        val start = Offset(colCenterX, centerY + arrowLength * 0.35f)
        val end = Offset(colCenterX, centerY - arrowLength * 0.35f)

        drawLine(
            color = arrowColor,
            start = start,
            end = end,
            strokeWidth = arrowWidth / 3f,
            cap = StrokeCap.Round
        )

        // arrow head
        drawLine(
            color = arrowColor,
            start = end,
            end = end + Offset(-arrowWidth / 2f, arrowWidth / 1.5f),
            strokeWidth = arrowWidth / 3f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = arrowColor,
            start = end,
            end = end + Offset(arrowWidth / 2f, arrowWidth / 1.5f),
            strokeWidth = arrowWidth / 3f,
            cap = StrokeCap.Round
        )
    }

    fun drawArrowDown() {
        val start = Offset(colCenterX, centerY - arrowLength * 0.35f)
        val end = Offset(colCenterX, centerY + arrowLength * 0.35f)

        drawLine(
            color = arrowColor,
            start = start,
            end = end,
            strokeWidth = arrowWidth / 3f,
            cap = StrokeCap.Round
        )

        drawLine(
            color = arrowColor,
            start = end,
            end = end + Offset(-arrowWidth / 2f, -arrowWidth / 1.5f),
            strokeWidth = arrowWidth / 3f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = arrowColor,
            start = end,
            end = end + Offset(arrowWidth / 2f, -arrowWidth / 1.5f),
            strokeWidth = arrowWidth / 3f,
            cap = StrokeCap.Round
        )
    }

    // ======================================================
    // 1️⃣ UPPER BEADS
    // ======================================================
    if (rowIndex == 0 && movement.upperDown) {
        drawArrowDown()
        return
    }
    if (rowIndex == 1 && movement.upperUp) {
        drawArrowUp()
        return
    }

    // ======================================================
    // 2️⃣ LOWER BEADS — DOWN
    // ======================================================
    if (movement.lowerDown > 0) {
        val down = movement.lowerDown
        val old = movement.lowerOldValue

        if (old >= down) {
            // ↓ your exact if-logic from BeadWithArrow ↓
            when (down) {
                1 -> when (old) {
                    1 -> if (rowIndex == 2) drawArrowDown()
                    2 -> if (rowIndex == 3) drawArrowDown()
                    3 -> if (rowIndex == 4) drawArrowDown()
                    4 -> if (rowIndex == 5) drawArrowDown()
                }
                2 -> when (old) {
                    2 -> if (rowIndex == 2 || rowIndex == 3) drawArrowDown()
                    3 -> if (rowIndex == 3 || rowIndex == 4) drawArrowDown()
                    4 -> if (rowIndex == 4 || rowIndex == 5) drawArrowDown()
                }
                3 -> when (old) {
                    3 -> if (rowIndex in listOf(2,3,4)) drawArrowDown()
                    4 -> if (rowIndex in listOf(3,4,5)) drawArrowDown()
                }
                4 -> when (old) {
                    4 -> if (rowIndex in listOf(2,3,4,5)) drawArrowDown()
                }
            }
        }
        return
    }

    // ======================================================
    // 3️⃣ LOWER BEADS — UP
    // ======================================================
    if (movement.lowerUp > 0) {
        val up = movement.lowerUp
        val old = movement.lowerOldValue

        when (old) {
            0 -> when (up) {
                1 -> if (rowIndex == 3) drawArrowUp()
                2 -> if (rowIndex == 3 || rowIndex == 4) drawArrowUp()
                3 -> if (rowIndex == 3 || rowIndex == 4 || rowIndex == 5) drawArrowUp()
                4 -> if (rowIndex == 3 || rowIndex == 4 || rowIndex == 5 || rowIndex == 6) drawArrowUp()
            }
            1 -> when (up) {
                1 -> if (rowIndex == 4) drawArrowUp()
                2 -> if (rowIndex == 4 || rowIndex == 5) drawArrowUp()
                3 -> if (rowIndex == 4 || rowIndex == 5 || rowIndex == 6) drawArrowUp()
            }
            2 -> when (up) {
                1 -> if (rowIndex == 5) drawArrowUp()
                2 -> if (rowIndex == 5 || rowIndex == 6) drawArrowUp()
            }
            3 -> when (up) {
                1 -> if (rowIndex == 6) drawArrowUp()
            }
        }
    }
}
