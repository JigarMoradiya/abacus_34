package com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.Shader
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.createBitmap
import com.jigar.me.data.local.data.Movement
import com.jigar.me.data.local.data.RodMovement
import com.jigar.me.ui.view.base.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.base.abacus_base.AbacusDimensionModel
import com.jigar.me.ui.view.base.abacus_base.AbacusTheme
import com.jigar.me.ui.view.base.abacus_base.ColorPresets
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens1
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens2
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.mixWith


// ─────────────────────────────────────────────────────────────
// Draw columns + beads inside Canvas
// ─────────────────────────────────────────────────────────────

fun DrawScope.drawAbacusColumns(
    screenType: String,
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
    arrowUPBitmap: ImageBitmap,
    arrowDownBitmap: ImageBitmap
) {
    val preset = AbacusTheme.colorPreset(selectedTheme)
    val isPolygonTheme = selectedTheme.contains("poligon", ignoreCase = true)
    val isRainbow = selectedTheme.equals("poligon_rainbow", ignoreCase = true)

    val beadH = geometry.beadHeightPx
    val beamH = geometry.beamHeightPx
    val extra = geometry.extraSpacePx

    // Helper: Y of bead *top* for a given index (0..6)
    fun beadTopForIndex(idx: Int): Float {
        val tempSpace = if (idx == 7){ // patch
            0F
        }else{
            0.2F
        }
        return tempSpace + extra + beadH * idx + if (idx >= 2) beamH else 0f
    }

    // Beam band is between row 1 and row 2
    val beamBandTop = extra + beadH * 2        // bottom of row 1

    for (col in 0 until numberOfColumns) {
        val columnState = abacusData.abacusState[col]
        val isCentralColumn = (col == 6)
        val isRedDotColumn = if (screenType == AppConstants.AbacusScreen.screenTypeLevel1Learn || screenType == AppConstants.AbacusScreen.screenTypeLevel1Practice || screenType == AppConstants.AbacusScreen.screenTypeLevel2Learn || screenType == AppConstants.AbacusScreen.screenTypeLevel2Practice) false else
                    col == numberOfColumns - 1 ||
                    col == numberOfColumns - 4 ||
                    col == numberOfColumns - 7 ||
                    col == numberOfColumns - 10 ||
                    col == numberOfColumns - 13

        val baseColor = if (isRainbow) ColorPresets.getMixColorListOfPoligon()[col] else preset.abacusTopGradient
        val columnColor = baseColor.mixWith(Color.White, 0.8f)
        val beamColor = preset.abacusTopGradient.mixWith(Color.White, 0.8f)

        val xCenter = geometry.columnCentersX[col]
        val stickWidth = beamH / 2f

        // ───────── Stick (rod) ─────────
        val rectLeft = xCenter - stickWidth / 2f
        val rectTop = 0f
        val rectSize = Size(width = stickWidth, height = size.height)

        val alphaValue = when {
            showHighlighter && currentSpot == 1 -> Dimens1.value
            isCentralColumn -> Dimens1.value
            else -> 0.4f
        }

        // Shadow (draw first)
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.20f),
            topLeft = Offset(rectLeft + Dimens1.value, rectTop + Dimens1.value), // slight offset
            size = rectSize,
            cornerRadius = CornerRadius(0f, 0f)
        )

        // Main rect
        drawRoundRect(
            color = columnColor,
            topLeft = Offset(rectLeft, rectTop),
            size = rectSize,
            cornerRadius = CornerRadius(0f, 0f),
            alpha = alphaValue
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
            val beamWidth = geometry.beadWidthPx + (geometry.columnSpacesPx * 2f)
            val beamLeft = xCenter - beamWidth / 2f
            val beamTop = beamBandTop + (beamH / 4f)
            val beamSize = Size(width = beamWidth, height = beamH / 2f)

            // Shadow (draw first)
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.20f),
                topLeft = Offset(x = beamLeft + Dimens1.value, y = beamTop + Dimens1.value),
                size = beamSize, cornerRadius = CornerRadius(0f, 0f))

            // Main beam
            drawRoundRect(
                color = beamColor,
                topLeft = Offset(x = beamLeft, y = beamTop),
                size = beamSize, cornerRadius = CornerRadius(0f, 0f))


            if (!showHighlighter || currentSpot != 1) { // hide dots when rods highlighter show
                if (isRedDotColumn) {
                    val dotRadius = if (isCentralColumn) beamH * 0.40f else beamH * 0.25f

                    drawCircle(
                        color = if (isCentralColumn) preset.buttonColor else preset.buttonColor,
                        radius = dotRadius,
                        center = Offset(x = xCenter, y = beamBandTop + (beamH / 2f))
                    )
                }
            }
        }

        if (screenType != AppConstants.AbacusScreen.screenTypeExam && screenType != AppConstants.AbacusScreen.screenTypeExamResult && screenType != AppConstants.AbacusScreen.screenTypeLevel1Learn && screenType != AppConstants.AbacusScreen.screenTypeLevel1Practice && screenType != AppConstants.AbacusScreen.screenTypeLevel2Learn && screenType != AppConstants.AbacusScreen.screenTypeLevel2Practice){
            val isFirstColumn = col == 0
            val isLastColumn = col == numberOfColumns - 1

            val firstCenter = geometry.columnCentersX.first()
            val lastCenter = geometry.columnCentersX.last()

            val beamWidthPerColumn = geometry.beadWidthPx + (geometry.columnSpacesPx * 2f)

            // Full beam boundaries
            val fullBeamLeft = firstCenter - beamWidthPerColumn / 2f
            val fullBeamRight = lastCenter + beamWidthPerColumn / 2f

            val capWidth = beamH * 0.6f
            val capHeight = beamH * 1.1f
            val radius = capHeight / 2f
            if (isFirstColumn) {
                // LEFT DOT
                val leftCapRect = Rect(
                    left = fullBeamLeft,
                    top = beamBandTop,
                    right = fullBeamLeft + capWidth,
                    bottom = beamBandTop + capHeight
                )

                val leftPath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = leftCapRect,
                            topLeft = CornerRadius(0f, 0f),
                            bottomLeft = CornerRadius(0f, 0f),
                            topRight = CornerRadius(radius, radius),
                            bottomRight = CornerRadius(radius, radius)
                        )
                    )
                }

                drawPath(
                    path = leftPath,
                    color = preset.buttonColor
                )
            }

            // ✅ RIGHT MOST (only once)
            if (isLastColumn) {
                val rightCapRect = Rect(
                    left = fullBeamRight - capWidth,
                    top = beamBandTop,
                    right = fullBeamRight,
                    bottom = beamBandTop + capHeight
                )

                val rightPath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = rightCapRect,
                            topLeft = CornerRadius(radius, radius),
                            bottomLeft = CornerRadius(radius, radius),
                            topRight = CornerRadius(0f, 0f),
                            bottomRight = CornerRadius(0f, 0f)
                        )
                    )
                }

                drawPath(
                    path = rightPath,
                    color = preset.buttonColor
                )
            }
        }

        // ───────── Per bead index (0..6) ─────────
        if (!showHighlighter || currentSpot != 1) { // hide all beads when rods highlighter show
            columnState.indices.forEach { idx ->
                val beadTop = beadTopForIndex(idx)

                // ---------- Bead image ----------
                if (columnState[idx]) {
                    val beadIsActive = arr[idx]
                    val imageToDraw: ImageBitmap = beadPolygonGray

                    val tintColor =
                        if (!isPolygonTheme) Color.White
                        else if (beadIsActive)
                            baseColor.mixWith(Color.White, 0.4f)   // active, brighter
                        else
                            baseColor.mixWith(Color.White,  0.9f)   // inactive, dimmer

                    if (isPolygonTheme) {
                        drawIntoCanvas { canvas ->
                            val topColor = if (beadIsActive)
                                baseColor.mixWith(Color.White, 0.40f)
                            else
                                baseColor.mixWith(Color.White, 0.8f)

                            val bottomColor = if (beadIsActive)
                                baseColor.mixWith(Color.White,0.10f)
                            else
                                baseColor.mixWith(Color.White, 0.7f)

                            drawBeadWithGradientMask(
                                image = beadPolygonGray,      // or faceOpen / faceClose
                                xCenter = xCenter,
                                beadTop = beadTop,
                                geometry = geometry,
                                topColor = topColor,
                                bottomColor = bottomColor
                            )
                        }
                    } else {
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
}

private fun DrawScope.drawBeadWithGradientMask(
    image: ImageBitmap,
    xCenter: Float,
    beadTop: Float,
    geometry: AbacusCanvasGeometry,
    topColor: Color,
    bottomColor: Color
) {
    val beadWidth = geometry.beadWidthPx
    val beadHeight = geometry.beadHeightPx
    val shadowPaint = Paint().apply {
        isAntiAlias = true
        alpha = (0.25f * 255).toInt() // same as 20% opacity
        colorFilter = android.graphics.PorterDuffColorFilter(
            android.graphics.Color.BLACK,
            PorterDuff.Mode.SRC_IN
        )
    }
    drawIntoCanvas { canvas ->
        val native = canvas.nativeCanvas

        val dstRectF = RectF(
            xCenter - beadWidth / 2f,
            beadTop,
            xCenter + beadWidth / 2f,
            beadTop + beadHeight
        )

        // =========================
        // 1. FAKE SHADOW (FAST)
        // =========================
        native.drawBitmap(
            image.asAndroidBitmap(),
            null,
            RectF(
                dstRectF.left + Dimens2.value,   // X offset
                dstRectF.top + Dimens2.value,    // Y offset
                dstRectF.right + Dimens2.value,
                dstRectF.bottom + Dimens2.value
            ),
            shadowPaint
        )

        // =========================
        // 🎯 2. MAIN LAYER
        // =========================
        val layerId = native.saveLayer(dstRectF, null)

        // 2️⃣ Draw main bead image
        native.drawBitmap(
            image.asAndroidBitmap(),
            null,
            dstRectF,
            null
        )

        // =========================
        // 🎨 3. GRADIENT MASK
        // =========================
        val shader = LinearGradient(
            0f,
            dstRectF.top,
            0f,
            dstRectF.bottom,
            topColor.toArgb(),
            bottomColor.toArgb(),
            Shader.TileMode.CLAMP
        )

        val paint = Paint().apply {
            isAntiAlias = true
            this.shader = shader
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        }

        native.drawRect(dstRectF, paint)

        // =========================
        // ✅ 4. RESTORE
        // =========================
        native.restoreToCount(layerId)
    }
}


fun drawableToImageBitmap(context: Context, resId: Int): ImageBitmap {
    val drawable = AppCompatResources.getDrawable(context, resId)!!

    val bmp = createBitmap(drawable.intrinsicWidth.takeIf { it > 0 } ?: 64, drawable.intrinsicHeight.takeIf { it > 0 } ?: 64)

    val canvas = Canvas(bmp)
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