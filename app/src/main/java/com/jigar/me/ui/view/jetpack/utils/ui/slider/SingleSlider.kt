package com.jigar.me.ui.view.jetpack.utils.ui.slider

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.jigar.me.R

@Composable
fun SingleSlider(
    value: Int,
    isShowText: Boolean = true,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .height(36.dp) // enough for thumb drag
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val density = LocalDensity.current

        val sliderHeight = with(density) { 4.dp.toPx() }
        val thumbSize = with(density) { 24.dp.toPx() }

        // Convert value → X
        fun xOffsetFromValue(v: Float): Float {
            val ratio = (v - range.start) / (range.endInclusive - range.start)
            return ratio * widthPx
        }

        // Convert X → value
        fun valueFromX(x: Float): Float {
            val ratio = (x / widthPx).coerceIn(0f, 1f)
            return range.start + ratio * (range.endInclusive - range.start)
        }

        val thumbX = xOffsetFromValue(value.toFloat())

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        val newValue = valueFromX(change.position.x)
                        onValueChange(newValue.coerceIn(range.start, range.endInclusive).toInt())
                    }
                }
        ) {
            val centerY = size.height / 2

            /* ───────── Track Background ───────── */
            drawRoundRect(
                color = Color.Gray.copy(alpha = 0.3f),
                topLeft = Offset(0f, centerY - sliderHeight / 2),
                size = Size(size.width, sliderHeight),
                cornerRadius = CornerRadius(sliderHeight)
            )

            /* ───────── Track Fill ───────── */
            drawRoundRect(
                color = Color(0xFF9C27B0), // colorAccentLight
                topLeft = Offset(0f, centerY - sliderHeight / 2),
                size = Size(thumbX, sliderHeight),
                cornerRadius = CornerRadius(sliderHeight)
            )

            /* ───────── Thumb ───────── */
            drawCircle(
                color = Color(0xFF9C27B0),
                radius = thumbSize / 2,
                center = Offset(thumbX, centerY)
            )
        }

        if (isShowText){
            /* ───────── Value Text on Thumb ───────── */
            Text(
                text = value.toString(),
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (thumbX - thumbSize / 2).toInt(),
                            (constraints.maxHeight / 2 - thumbSize / 2).toInt()
                        )
                    }
                    .size(24.dp),
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White, fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.font_bold))),
                textAlign = TextAlign.Center
            )
        }
    }
}
