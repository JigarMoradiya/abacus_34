package com.jigar.me.ui.view.jetpack.utils.ui.slider

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun SingleSlider(
    value: Int,
    isShowText: Boolean = true,
    range: ClosedFloatingPointRange<Float>,
    step: Int = 1,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.height(32.dp)) {
        val density = LocalDensity.current
        val widthPx = constraints.maxWidth.toFloat()

        val sliderHeightPx = with(density) { 4.dp.toPx() }
        val thumbRadiusPx = with(density) { 12.dp.toPx() }
        val textSizePx = with(density) { 12.sp.toPx() }

        var rawValue by remember { mutableFloatStateOf(value.toFloat()) }
        var lastEmittedValue by remember { mutableIntStateOf(value) }

        LaunchedEffect(value) {
            rawValue = value.toFloat()
        }

        fun valueToX(v: Float): Float {
            val ratio = (v - range.start) / (range.endInclusive - range.start)
            return ratio.coerceIn(0f, 1f) * widthPx
        }

        fun xToRawValue(x: Float): Float {
            val ratio = (x / widthPx).coerceIn(0f, 1f)
            return range.start + ratio * (range.endInclusive - range.start)
        }

        fun snapToStep(v: Float): Int {
            val stepped =
                ((v - range.start) / step).roundToInt() * step + range.start
            return stepped
                .coerceIn(range.start, range.endInclusive)
                .toInt()
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, _ ->
                            rawValue = xToRawValue(change.position.x)
                            if (!isShowText){
                                val snapped = snapToStep(rawValue)
                                if (snapped != lastEmittedValue) {
                                    lastEmittedValue = snapped
                                    onValueChange(snapped)
                                }
                            }
                        },
                        onDragEnd = {
                            if (isShowText){
                                val snapped = snapToStep(rawValue)
                                onValueChange(snapped)
                            }
                        }
                    )
                }
        ) {
            val centerY = size.height / 2

            // Track background
            drawRoundRect(
                color = Color.Gray.copy(alpha = 0.3f),
                topLeft = Offset(0f, centerY - sliderHeightPx / 2),
                size = Size(size.width, sliderHeightPx),
                cornerRadius = CornerRadius(sliderHeightPx)
            )

            // Track fill
            drawRoundRect(
                color = Color(0xFF9C27B0),
                topLeft = Offset(0f, centerY - sliderHeightPx / 2),
                size = Size(valueToX(rawValue), sliderHeightPx),
                cornerRadius = CornerRadius(sliderHeightPx)
            )

            // Thumb
            drawCircle(
                color = Color(0xFF9C27B0),
                radius = thumbRadiusPx,
                center = Offset(valueToX(rawValue), centerY)
            )

            // Value text (centered)
            if (isShowText) {
                drawContext.canvas.nativeCanvas.apply {
                    val paint = Paint().apply {
                        color = Color.White.toArgb()
                        textSize = textSizePx
                        textAlign = Paint.Align.CENTER
                        isFakeBoldText = true
                        isAntiAlias = true
                    }

                    drawText(snapToStep(rawValue).toString(), valueToX(rawValue), centerY + textSizePx / 3, paint)
                }
            }
        }
    }
}

