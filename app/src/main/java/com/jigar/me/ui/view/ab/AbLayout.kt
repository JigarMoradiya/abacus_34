package com.jigar.me.ui.view.ab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Material vector icons carry about 15% internal padding inside their box, while an SF Symbol drawn at a point size N
 * fills about N. Kit sizes written for SF Symbols are multiplied by this to get the same visible glyph from a vector.
 */
internal const val AB_SF_TO_VECTOR = 1.2f

/**
 * How much [AbFitToSpace] has shrunk its content (1 = not at all). Overlays that must stay at true screen size read it.
 * It reaches the content one frame after a size change (the scale is settled during layout, then published).
 */
val LocalAbFitScale = compositionLocalOf { 1f }

/**
 * Lays its content out at natural height, then shrinks it (never grows it) to fit the space it is given, centred.
 * A plain column whose content is taller than the screen overflows and pushes the header off the top; this cannot,
 * because it simply fills the space it is offered and scales the content inside it. (Give it a bounded size: inside a
 * scroller there is nothing to fit into, so the content is shown at its natural size.)
 *
 * The content is measured with unbounded height, so it must have a natural height of its own: no `weight` / `fillMaxHeight`
 * children in a column that is meant to flex vertically (the iOS stack gives a Spacer there its minimum length as well).
 *
 * @param alignment where the (possibly shrunk) content sits in the space, and the point it shrinks toward.
 * @param stretchWidth for a row with flexible gaps (`Spacer(Modifier.weight(1f))`, `fillMaxWidth()`): lay it out wider
 * (width / scale) and scale it about its start edge, so it still spans the full width and keeps its start edge instead of
 * shrinking toward the middle.
 */
@Composable
fun AbFitToSpace(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    stretchWidth: Boolean = false,
    content: @Composable () -> Unit,
) {
    var fitScale by remember { mutableFloatStateOf(1f) }
    var laidScale by remember { mutableFloatStateOf(1f) }
    val published = remember { floatArrayOf(1f) }

    CompositionLocalProvider(LocalAbFitScale provides fitScale) {
        Layout(
            content = { Box(contentAlignment = Alignment.TopCenter) { content() } },
            modifier = modifier,
        ) { measurables, constraints ->
            val child = measurables.first()

            if (!constraints.hasBoundedWidth || !constraints.hasBoundedHeight) {
                // Nothing to fit into (e.g. inside a scroller): natural size, no scaling.
                val natural = child.measure(constraints.copy(minWidth = 0, minHeight = 0))
                return@Layout layout(natural.width, natural.height) { natural.place(0, 0) }
            }

            val availW = constraints.maxWidth
            val availH = constraints.maxHeight
            // Start-aligned content hugs its own width (its start edge is the space's start edge); centred content is
            // offered the full width.
            val hugsWidth = alignment != Alignment.Center && !stretchWidth

            fun measureAt(width: Int?): Placeable = child.measure(
                Constraints(
                    minWidth = width ?: 0,
                    maxWidth = width ?: (if (hugsWidth) Constraints.Infinity else availW),
                    minHeight = 0,
                    maxHeight = Constraints.Infinity,
                ),
            )

            val scale: Float
            val placed: Placeable
            if (stretchWidth) {
                // The content is laid out at width / laidScale and must still fit the height once scaled. Widening makes
                // it shorter, so the width settles over a few frames (as on iOS): one measure per pass (a Measurable
                // may be measured only once), the scale it needs is stored, and the change lays it out again.
                val laidAt = laidScale
                val p = measureAt((availW / laidAt).roundToInt())
                val needed = if (p.height > 0) min(1f, availH.toFloat() / p.height) else 1f
                if (abs(needed - laidAt) > 0.002f) laidScale = needed
                // never taller than the space, whatever width this pass was laid out at
                scale = if (p.height > 0) min(laidAt, availH.toFloat() / p.height) else laidAt
                placed = p
            } else {
                val p = measureAt(null)
                scale = if (p.width > 0 && p.height > 0) {
                    min(1f, min(availW.toFloat() / p.width, availH.toFloat() / p.height))
                } else 1f
                placed = p
            }

            val finalScale = scale
            if (abs(published[0] - finalScale) > 0.001f) {
                published[0] = finalScale
                fitScale = finalScale
            }

            layout(availW, availH) {
                val anchor = if (stretchWidth) Alignment.CenterStart else alignment
                val pos = anchor.align(IntSize(placed.width, placed.height), IntSize(availW, availH), layoutDirection)
                // the shrink anchor is the alignment point of the content box (centre, start edge, top edge ...)
                val o = anchor.align(IntSize.Zero, IntSize(1000, 1000), layoutDirection)
                val origin = TransformOrigin(o.x / 1000f, o.y / 1000f)
                placed.placeWithLayer(pos.x, pos.y) {
                    scaleX = finalScale
                    scaleY = finalScale
                    transformOrigin = origin
                }
            }
        }
    }
}

/** On a phone, shrinks the content to the space available instead of overflowing it. A tablet shows it as it is. */
@Composable
fun AbFitOnPhone(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    stretchWidth: Boolean = false,
    content: @Composable () -> Unit,
) {
    if (LocalAbScale.current.isTablet) {
        Box(modifier) { content() }
    } else {
        AbFitToSpace(modifier, alignment, stretchWidth, content)
    }
}

/**
 * The restyled screens pad their content a little more from the left edge than the app's standard edge. Put this on a
 * screen's root to pull the whole foreground (back button and content together) onto the standard edge. The right edge
 * and the background do not change. The content is laid out [pull] wider and shifted left by [pull], so nothing is cut.
 *
 * [pull] is `DimensionsUtils.padding16 - deviceSpacingHorizzontal` on iOS: 16 on a phone, 8 / 12 on the two tablet
 * classes. Pass your own value if the Android screens use a different standard edge.
 */
fun Modifier.abScreenLeftEdge(pull: Dp? = null): Modifier = composed {
    val scale = LocalAbScale.current
    val amount = pull ?: when {
        !scale.isTablet -> 16.dp
        scale.factor >= 1.5f -> 12.dp
        else -> 8.dp
    }
    layout { measurable, constraints ->
        val pullPx = amount.roundToPx()
        val inner = Constraints(
            minWidth = if (constraints.minWidth > 0) constraints.minWidth + pullPx else 0,
            maxWidth = if (constraints.hasBoundedWidth) constraints.maxWidth + pullPx else Constraints.Infinity,
            minHeight = constraints.minHeight,
            maxHeight = constraints.maxHeight,
        )
        val p = measurable.measure(inner)
        layout(maxOf(0, p.width - pullPx), p.height) { p.place(-pullPx, 0) }
    }
}

/**
 * The kit's main text colour (indigo in light, near-white in dark) for text that does not set its own colour, inside a
 * screen that cannot read the theme itself. (SwiftUI's `.abInk()` view modifier; a composition local cannot be set from
 * a Modifier, so this wraps the content.)
 */
@Composable
fun AbInk(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalContentColor provides LocalAbColors.current.textPrimary, content = content)
}

/**
 * SwiftUI's `minimumScaleFactor`: the text keeps [style]'s size and only shrinks, down to [minScale] of it, when it does
 * not fit the width (and [maxLines]) it is given. Below that it truncates with an ellipsis. Never grows.
 * Uses the platform auto-size text, so the style's explicit line height is kept as authored.
 */
@Composable
fun AbShrinkText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    minScale: Float = 0.7f,
    maxLines: Int = 1,
    textAlign: TextAlign? = null,
) {
    val full = style.fontSize
    if (minScale >= 1f || !full.isSpecified) {
        Text(
            text = text, modifier = modifier, color = color, textAlign = textAlign,
            overflow = TextOverflow.Ellipsis, maxLines = maxLines, style = style,
        )
    } else {
        Text(
            text = text, modifier = modifier, color = color,
            autoSize = TextAutoSize.StepBased(minFontSize = full * minScale, maxFontSize = full, stepSize = 0.25.sp),
            textAlign = textAlign, overflow = TextOverflow.Ellipsis, maxLines = maxLines, style = style,
        )
    }
}
