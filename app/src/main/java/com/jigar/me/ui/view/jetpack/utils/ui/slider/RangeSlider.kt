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
import com.jigar.me.ui.view.jetpack.core.presentation.theme.ColorPrimary
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun RangeSlider(
    startValue: Int, endValue: Int, range: ClosedFloatingPointRange<Float>, step: Int = 1, onValueChange: (Int, Int) -> Unit, modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.height(36.dp)) {

        val density = LocalDensity.current
        val widthPx = constraints.maxWidth.toFloat()

        val sliderHeightPx = with(density) { 4.dp.toPx() }
        val thumbRadiusPx = with(density) { 12.dp.toPx() }
        val textSizePx = with(density) { 12.sp.toPx() }

        var rawStart by remember { mutableFloatStateOf(startValue.toFloat()) }
        var rawEnd by remember { mutableFloatStateOf(endValue.toFloat()) }

        LaunchedEffect(startValue, endValue) {
            rawStart = startValue.toFloat()
            rawEnd = endValue.toFloat()
        }

        fun valueToX(v: Float): Float {
            val ratio = (v - range.start) / (range.endInclusive - range.start)
            return ratio.coerceIn(0f, 1f) * widthPx
        }

        fun xToValue(x: Float): Float {
            val ratio = (x / widthPx).coerceIn(0f, 1f)
            return range.start + ratio * (range.endInclusive - range.start)
        }

        fun snap(v: Float): Int {
            val stepped = ((v - range.start) / step).roundToInt() * step + range.start
            return stepped.coerceIn(range.start, range.endInclusive).toInt()
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, _ ->
                            val touchX = change.position.x
                            val startX = valueToX(rawStart)
                            val endX = valueToX(rawEnd)

                            val isStartThumb = abs(touchX - startX) < abs(touchX - endX)
                            if (isStartThumb) {
                                rawStart = xToValue(touchX).coerceAtMost(rawEnd)
                            } else {
                                rawEnd = xToValue(touchX).coerceAtLeast(rawStart)
                            }
                        },

                        onDragEnd = {
                            val snappedStart = snap(rawStart)
                            val snappedEnd = snap(rawEnd)

                            rawStart = snappedStart.toFloat()
                            rawEnd = snappedEnd.toFloat()

                            onValueChange(snappedStart, snappedEnd)
                        })
                }

        ) {
            val centerY = size.height / 2

            // ───────── Track background ─────────
            drawRoundRect(
                color = Color.Gray.copy(alpha = 0.3f), topLeft = Offset(0f, centerY - sliderHeightPx / 2), size = Size(size.width, sliderHeightPx), cornerRadius = CornerRadius(sliderHeightPx)
            )

            // ───────── Active range ─────────
            val startX = valueToX(rawStart)
            val endX = valueToX(rawEnd)

            drawRoundRect(
                color = ColorPrimary, topLeft = Offset(startX, centerY - sliderHeightPx / 2), size = Size(endX - startX, sliderHeightPx), cornerRadius = CornerRadius(sliderHeightPx)
            )

            // ───────── Left Thumb ─────────
            drawCircle(
                color = ColorPrimary, radius = thumbRadiusPx, center = Offset(startX, centerY)
            )

            // ───────── Right Thumb ─────────
            drawCircle(
                color = ColorPrimary, radius = thumbRadiusPx, center = Offset(endX, centerY)
            )
            val paint = Paint().apply {
                color = Color.White.toArgb()
                textSize = textSizePx
                textAlign = Paint.Align.CENTER
                isFakeBoldText = true
                isAntiAlias = true
            }

            drawContext.canvas.nativeCanvas.drawText(
                snap(rawStart).toString(), startX, centerY + textSizePx / 3, paint
            )

            drawContext.canvas.nativeCanvas.drawText(
                snap(rawEnd).toString(), endX, centerY + textSizePx / 3, paint
            )
        }
    }
}
