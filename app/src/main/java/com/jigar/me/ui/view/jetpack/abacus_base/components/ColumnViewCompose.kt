package com.jigar.me.ui.view.jetpack.abacus_base.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.local.data.Movement
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusTheme
import com.jigar.me.ui.view.jetpack.abacus_base.ColorPresets
import com.jigar.me.utils.extensions.mixWith

@Composable
fun ColumnViewCompose(
    imageName: String,
    columnNumber: Int,
    isRedDotPresent: Boolean,
    isCentralColumn: Boolean,
    abacusData: AbacusCalculations,
    movement: Movement?,
    showDirection: Boolean,
    beadWidth: Dp,
    beadHeight: Dp,
    totalHeight: Dp,
    beamHeight: Dp,
    extraSpace: Dp,
    columnSpaces: Dp,
    showHighlighter: Boolean,
    currentSpot: Int?
) {
    val columnState = abacusData.abacusState[columnNumber]

    val columnColor = if (imageName.contains("poligon_rainbow")) {
        ColorPresets.getMixColorListOfPoligon()[columnNumber].copy(alpha = 0.6f)
    } else {
        AbacusTheme.colorPreset(imageName).abacusCenterGradient
    }

    Box(
        modifier = Modifier.wrapContentSize()
    ) {
        // Stick
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = columnSpaces)
                .width(beamHeight / 2)
                .height(totalHeight)
                .background(columnColor.copy(alpha = 0.2f))
                .alpha(
                    when {
                        showHighlighter && currentSpot == 1 -> 1f
                        isCentralColumn -> 1f
                        else -> 0.3f
                    }
                )
        )

        Column(
            modifier = Modifier
                .padding(vertical = extraSpace)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Top
        ) {
            repeat(columnState.size) { index ->
                BeadWithArrow(
                    index = index,
                    imageName = imageName,
                    columnNumber = columnNumber,
                    abacusData = abacusData,
                    isRedDotPresent = isRedDotPresent && index == 2,
                    isCentralColumn = isCentralColumn,
                    beadWidth = beadWidth,
                    beadHeight = beadHeight,
                    beamHeight = beamHeight,
                    columnSpaces = columnSpaces,
                    movement = movement,
                    showDirection = showDirection
                )
            }
        }
    }
}

@Composable
private fun BeadWithArrow(
    index: Int,
    imageName: String,
    columnNumber: Int,
    abacusData: AbacusCalculations,
    isRedDotPresent: Boolean,
    isCentralColumn: Boolean,
    beadWidth: Dp,
    beadHeight: Dp,
    beamHeight: Dp,
    columnSpaces: Dp,
    movement: Movement?,
    showDirection: Boolean
) {
    val columnState = abacusData.abacusState[columnNumber]

    val columnSize = abacusData.abacusState[columnNumber]
    val arr = MutableList(columnSize.size) { false }
    if (columnSize[1]) arr[1] = true
    for (i in 2 until columnSize.size) {
        if (columnSize[i]) arr[i] = true else break
    }
    val beadIsActive = arr[index]

    val isPolygonTheme = imageName.contains("poligon")

    val imgRes: Int =
        if (isPolygonTheme) {
            R.drawable.poligon_gray   // polygon base
        } else {
            if (beadIsActive) faceOpenRes(index) else faceCloseRes(index)
        }

    val tintColors: List<Color> = if (isPolygonTheme) {
        if (imageName == "poligon_rainbow") {
            val topColor = ColorPresets.getMixColorListOfPoligon()[columnNumber]
            if (beadIsActive)  listOf(
                topColor.mixWith(Color.White, 0.30f),
                topColor.mixWith(Color.White, 0.1f)
            ) else listOf(
                topColor.mixWith(Color.White, 0.75f),
                topColor.mixWith(Color.White, 0.65f)
            )
        } else {
            val preset = AbacusTheme.colorPreset(imageName)
            val topColor = preset.abacusTopGradient
            if (beadIsActive) {
                listOf(
                    topColor.mixWith(Color.White, 0.30f),
                    topColor.mixWith(Color.White, 0.1f)
                )
            } else {
                listOf(
                    topColor.mixWith(Color.White, 0.75f),
                    topColor.mixWith(Color.White, 0.65f)
                )
            }
        }
    } else {
        emptyList()
    }

    val swipeThreshold = 8.dp
    var dy by remember { mutableFloatStateOf(0f) }

    val widthNew = beadWidth+(columnSpaces  * 2)
    Box(
        modifier = Modifier
            .width(widthNew)
            .height(beadHeight + if (index == 2) beamHeight else 0.dp)
    ) {
        Column {
            // Beam + red dot
            if (index == 2) {
                Box(
                    modifier = Modifier
                        .height(beamHeight)
                        .fillMaxWidth()
                ) {
                    // Background beam (like SwiftUI Rectangle)
                    Box(
                        modifier = Modifier
                            .height(beamHeight / 2)     // same as SwiftUI
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .background(
                                AbacusTheme.colorPreset(imageName).abacusCenterGradient.copy(alpha = 0.3f)
                            )
                    )

                    if (isRedDotPresent) {
                        if (isCentralColumn) {
                            Box(
                                modifier = Modifier
                                    .size(beamHeight)
                                    .align(Alignment.Center)
                                    .background(Color.White, CircleShape)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(beamHeight * 0.5f)
                                    .align(Alignment.Center)
                                    .background(
                                        AbacusTheme.colorPreset(imageName).buttonColor,
                                        CircleShape
                                    )
                            )
                        }
                    }
                }
            }

            // Bead
            if (columnState[index]) {
                Image(
                    painter = painterResource(id = imgRes),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .width(widthNew)
                        .height(beadHeight)
                        .padding(horizontal = columnSpaces)
                        .let { mod ->
                            if (isPolygonTheme) {
                                mod
                                    .graphicsLayer(alpha = 0.99f)
                                    .drawWithCache {
                                        val gradient = Brush.verticalGradient(tintColors)

                                        onDrawWithContent {
                                            drawContent()
                                            drawRect(
                                                brush = gradient,
                                                blendMode = BlendMode.SrcIn
                                            )
                                        }
                                    }
                            } else {
                                mod
                            }
                        }
                        .pointerInput(columnNumber, index) {
                            detectDragGestures(
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    dy += dragAmount.y
                                },
                                onDragEnd = {
                                    val thr = with(this) { swipeThreshold.toPx() }
                                    if (dy < -thr) {
                                        if (abacusData.canMoveUp(index, columnNumber)) {
                                            abacusData.moveBeadUp(index, columnNumber)
                                        }
                                    } else if (dy > thr) {
                                        if (abacusData.canMoveDown(index, columnNumber)) {
                                            abacusData.moveBeadDown(index, columnNumber)
                                        }
                                    }
                                    dy = 0f
                                }
                            )
                        }
                )
            } else {
                Spacer(
                    modifier = Modifier
                        .width(widthNew)
                        .height(beadHeight)
                )
            }
        }

        // Arrows (simplified – you can expand same as your Swift logic)
        if (showDirection && movement != null) {
            ArrowForBead(
                index = index,
                movement = movement,
                beadWidth = beadWidth,
                beadHeight = beadHeight,
                imageName = imageName
            )
        }
    }
}

@DrawableRes
private fun faceOpenRes(index: Int): Int = when (index) {
    0 -> R.drawable.face_red_open
    1 -> R.drawable.face_pink_open
    2 -> R.drawable.face_orange_open
    3 -> R.drawable.face_blue_open
    4 -> R.drawable.face_green_open
    else -> R.drawable.face_gray_close
}

@DrawableRes
private fun faceCloseRes(@Suppress("UNUSED_PARAMETER") index: Int): Int =
    R.drawable.face_gray_close

@Composable
private fun ArrowForBead(
    index: Int,
    movement: Movement,
    beadWidth: Dp,
    beadHeight: Dp,
    imageName: String
) {
    val arrowColor = AbacusTheme.colorPreset(imageName).arrowColor

    fun showUp(): Boolean {
        if (index == 1 && movement.upperUp) return true
        if (movement.lowerUp > 0) return true
        return false
    }

    fun showDown(): Boolean {
        if (index == 0 && movement.upperDown) return true
        if (movement.lowerDown > 0) return true
        return false
    }

    if (!showUp() && !showDown()) return

    Box(
        modifier = Modifier
            .width(beadWidth)
            .height(beadHeight),
        contentAlignment = Alignment.TopCenter
    ) {
        Icon(
            imageVector = if (showUp()) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = arrowColor,
            modifier = Modifier.size(beadHeight / 1.2f)
        )
    }
}
