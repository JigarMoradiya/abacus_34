package com.jigar.me.ui.view.ab

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/** The round thumb: a chunky disc with a white rim and its value on it. [size] is the full diameter. */
@Composable
private fun AbThumb(text: String, accent: Color, size: Dp, modifier: Modifier = Modifier) {
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        // 3D edge, sunk a little below the face
        Box(Modifier.fillMaxSize().offset(y = size * 0.08f).background(accent.abDarkened(0.3f), CircleShape))
        Box(
            Modifier
                .fillMaxSize()
                .background(accent, CircleShape)
                .border(maxOf(2.dp, size * 0.08f), Color.White.copy(alpha = 0.95f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            AbShrinkText(
                text = text,
                style = AbTypography.fixed(size.value * 0.46f, FontWeight.ExtraBold),
                color = Color.White,
                minScale = 0.6f,
                maxLines = 1,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** The track: a capsule with an outline, filled with the accent gradient from [from] to [to] (x positions in the track). */
@Composable
private fun AbTrack(accent: Color, from: Dp, to: Dp, width: Dp, height: Dp, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    Box(modifier.width(width).height(height)) {
        Box(
            Modifier
                .fillMaxSize()
                .background(colors.surfaceTint, CircleShape)
                .border(1.5.dp, colors.outline, CircleShape),
        )
        Box(
            Modifier
                .offset(x = from)
                .width(maxOf(height, to - from))
                .fillMaxHeight()
                .background(Brush.verticalGradient(listOf(accent.abBlended(Color.White, 0.25f), accent)), CircleShape),
        )
    }
}

/**
 * Chunky single-thumb slider: a rounded track filled with the accent colour, optional step dots (when [step] > 1) and a
 * round thumb that shows its value. Tap or drag anywhere on it. Values are doubles like the iOS kit; the value snaps to
 * [step] while dragging.
 *
 * @param valueText what the thumb shows (a count by default; a percentage for a volume).
 */
@Composable
fun AbSlider(
    value: Double,
    onValueChange: (Double) -> Unit,
    range: ClosedFloatingPointRange<Double>,
    modifier: Modifier = Modifier,
    step: Double = 1.0,
    accent: Color? = null,
    valueText: (Double) -> String = { it.toInt().toString() },
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val thumb = 32.dp * s
    val tone = accent ?: colors.brandPrimary
    val density = LocalDensity.current
    val currentOnChange by rememberUpdatedState(onValueChange)

    BoxWithConstraints(modifier.fillMaxWidth().height(thumb + 6.dp * s)) {
        // the thumb stays fully inside the track ends
        val thumbPx = with(density) { thumb.toPx() }
        val widthPx = constraints.maxWidth.toFloat()
        val inset = thumbPx / 2f
        val usable = max(1f, widthPx - thumbPx)
        val lo = range.start
        val hi = range.endInclusive
        val span = hi - lo
        fun xOf(v: Double): Float = inset + (if (span == 0.0) 0f else ((v - lo) / span).toFloat()) * usable
        fun pxToDp(px: Float): Dp = with(density) { px.toDp() }

        fun valueAt(px: Float) {
            val ratio = ((px - inset) / usable).coerceIn(0f, 1f)
            val raw = lo + ratio.toDouble() * span
            // round half away from zero like Swift's round(); raw - lo is never negative
            val snapped = if (step > 0) floor((raw - lo) / step + 0.5) * step + lo else raw
            currentOnChange(min(max(snapped, lo), hi))
        }

        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(widthPx, thumbPx, lo, hi, step) {
                    // pressing sets the value at once (minimum drag distance 0), dragging follows the finger
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        valueAt(down.position.x)
                        down.consume()
                        var pressed = true
                        while (pressed) {
                            val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id }
                            if (change == null || !change.pressed) {
                                pressed = false
                            } else {
                                valueAt(change.position.x)
                                change.consume()
                            }
                        }
                    }
                },
        ) {
            AbTrack(tone, from = 0.dp, to = pxToDp(xOf(value)), width = pxToDp(widthPx), height = 12.dp * s, modifier = Modifier.align(Alignment.Center))

            if (step > 1) {
                val total = ((hi - lo) / step).roundToInt()
                val dotPx = with(density) { (5.dp * s).toPx() }
                for (i in 0..total) {
                    val v = lo + i * step
                    Box(
                        Modifier
                            .align(Alignment.CenterStart)
                            .offset { IntOffset((xOf(v) - dotPx / 2f).roundToInt(), 0) }
                            .size(5.dp * s)
                            .background(if (v <= value) Color.White.copy(alpha = 0.9f) else colors.outline, CircleShape),
                    )
                }
            }

            AbThumb(
                text = valueText(value), accent = tone, size = thumb,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset { IntOffset((xOf(value) - thumbPx / 2f).roundToInt(), 0) },
            )
        }
    }
}

/**
 * Chunky two-thumb slider (a minimum and a maximum). Whole numbers only: the thumb nearest to where the touch starts is
 * the one that moves, and the two can never cross. [onMinChange] / [onMaxChange] stand in for the iOS bindings.
 */
@Composable
fun AbRangeSlider(
    minValue: Double,
    maxValue: Double,
    onMinChange: (Double) -> Unit,
    onMaxChange: (Double) -> Unit,
    range: ClosedFloatingPointRange<Double>,
    modifier: Modifier = Modifier,
    accent: Color? = null,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val thumb = 32.dp * s
    val tone = accent ?: colors.brandPrimary
    val density = LocalDensity.current
    val currentMin by rememberUpdatedState(minValue)
    val currentMax by rememberUpdatedState(maxValue)
    val currentOnMin by rememberUpdatedState(onMinChange)
    val currentOnMax by rememberUpdatedState(onMaxChange)

    BoxWithConstraints(modifier.fillMaxWidth().height(thumb + 6.dp * s)) {
        val thumbPx = with(density) { thumb.toPx() }
        val widthPx = constraints.maxWidth.toFloat()
        val inset = thumbPx / 2f
        val usable = max(1f, widthPx - thumbPx)
        val lo = range.start
        val hi = range.endInclusive
        val span = hi - lo
        fun xOf(v: Double): Float = inset + (if (span == 0.0) 0f else ((v - lo) / span).toFloat()) * usable
        fun pxToDp(px: Float): Dp = with(density) { px.toDp() }
        fun valueAt(px: Float): Double {
            val ratio = ((px - inset) / usable).coerceIn(0f, 1f)
            return floor(lo + ratio.toDouble() * span + 0.5)
        }

        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(widthPx, thumbPx, lo, hi) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        // the thumb nearest to where the touch started is the one that moves
                        val startX = down.position.x
                        val dragging = if (abs(startX - xOf(currentMin)) <= abs(startX - xOf(currentMax))) 0 else 1
                        fun moveTo(px: Float) {
                            val v = valueAt(px)
                            if (dragging == 0 && v < currentMax && v >= lo) currentOnMin(v)
                            if (dragging == 1 && v > currentMin && v <= hi) currentOnMax(v)
                        }
                        moveTo(startX)
                        down.consume()
                        var pressed = true
                        while (pressed) {
                            val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id }
                            if (change == null || !change.pressed) {
                                pressed = false
                            } else {
                                moveTo(change.position.x)
                                change.consume()
                            }
                        }
                    }
                },
        ) {
            AbTrack(
                tone, from = pxToDp(xOf(minValue)), to = pxToDp(xOf(maxValue)), width = pxToDp(widthPx), height = 12.dp * s,
                modifier = Modifier.align(Alignment.Center),
            )
            AbThumb(
                text = minValue.toInt().toString(), accent = tone, size = thumb,
                modifier = Modifier.align(Alignment.CenterStart).offset { IntOffset((xOf(minValue) - thumbPx / 2f).roundToInt(), 0) },
            )
            AbThumb(
                text = maxValue.toInt().toString(), accent = tone, size = thumb,
                modifier = Modifier.align(Alignment.CenterStart).offset { IntOffset((xOf(maxValue) - thumbPx / 2f).roundToInt(), 0) },
            )
        }
    }
}
