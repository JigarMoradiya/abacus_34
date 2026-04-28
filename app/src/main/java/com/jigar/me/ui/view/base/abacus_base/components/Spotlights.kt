package com.jigar.me.ui.view.base.abacus_base.components

import android.annotation.SuppressLint
import android.graphics.RectF
import android.widget.TextView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import com.jigar.me.R
import com.jigar.me.ui.view.base.abacus_base.SpotlightTooltipCalculation
import com.jigar.me.ui.view.home.theme.AppDimens
import kotlin.math.max
import kotlin.math.min

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun SpotlightOverlay(currentSpot: Int?, onNext: () -> Unit) {
    if (currentSpot == null) return

    val item = SpotlightRegistry.get(currentSpot) ?: return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)  // 🔥 REQUIRED for clear mode
    ) {

        // Background dim layer
        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures { onNext() } }
        ) {
            // Dim layer
            drawRect(Color.Black.copy(alpha = 0.70f))

            // 🔥 The clear highlight
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(item.rect.left, item.rect.top),
                size = Size(item.rect.width, item.rect.height),
                blendMode = BlendMode.Clear,      // THIS NOW WORKS!
                cornerRadius = CornerRadius(16f)
            )
        }

        // Tooltip ABOVE
        // Read screen size (exact like iOS geo.size)
        val configuration = LocalConfiguration.current
        val density = LocalDensity.current

        val screenWidthPx = with(density) {
            configuration.screenWidthDp.dp.toPx()
        }

        val screenHeightPx = with(density) {
            configuration.screenHeightDp.dp.toPx()
        }
        // Read safe area insets
        val safeInsets = WindowInsets.systemBars

        TooltipBox(
            item = item,
            screenWidth = screenWidthPx,
            screenHeight = screenHeightPx,
            safeInsets = safeInsets
        )
    }
}

@Composable
fun TooltipBox(
    item: SpotlightItem,
    screenWidth: Float,
    screenHeight: Float,
    safeInsets: WindowInsets,
) {
    var tooltipSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    // TARGET center position
    val targetCenter = remember(item.rect, tooltipSize) {
        calculateTooltipPosition(
            SpotlightTooltipCalculation(
                rect = RectF(item.rect.left, item.rect.top, item.rect.right, item.rect.bottom),
                screenWidth = screenWidth,
                screenHeight = screenHeight,
                safeTop = safeInsets.getTop(density).toFloat(),
                safeBottom = safeInsets.getBottom(density).toFloat(),
                safeLeft = safeInsets.getLeft(density, layoutDirection).toFloat(),
                safeRight = safeInsets.getRight(density, layoutDirection).toFloat(),
                tooltipWidth = tooltipSize.width.toFloat(),
                tooltipHeight = tooltipSize.height.toFloat()
            )
        )
    }

    // 💥 iOS-style ANIMATED VALUES
    val animatedX by animateFloatAsState(
        targetValue = targetCenter.x - tooltipSize.width / 2f,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
    )

    val animatedY by animateFloatAsState(
        targetValue = targetCenter.y - tooltipSize.height / 2f,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(250, easing = LinearOutSlowInEasing)
    )

    val animatedScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(250, easing = FastOutSlowInEasing)
    )

    // start with fade + scale on first appearance
    LaunchedEffect(item.rect) {
        // reset animation instantly
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                alpha = animatedAlpha
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .offset { IntOffset(animatedX.toInt(), animatedY.toInt()) }
            .onGloballyPositioned { tooltipSize = it.size }
            .background(Color.White, RoundedCornerShape(AppDimens.Dimens16))
            .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens12)
    ) {
        HtmlText(html = item.text)
    }
}


fun calculateTooltipPosition(calc: SpotlightTooltipCalculation): Offset {

    val margin = 16f

    val rect = calc.rect

    val spaceTop = rect.top - calc.safeTop - margin
    val spaceBottom = (calc.screenHeight - calc.safeBottom) - rect.bottom - margin
    val spaceLeft = rect.left - calc.safeLeft - margin
    val spaceRight = (calc.screenWidth - calc.safeRight) - rect.right - margin

    val verticalMax = max(spaceTop, spaceBottom)
    val horizontalMax = max(spaceLeft, spaceRight)

    // If we don't know tooltip size yet (first frame)
    if (calc.tooltipWidth == 0f || calc.tooltipHeight == 0f) {
        return Offset(
            x = rect.centerX(),
            y = rect.bottom + 20f
        )
    }

    // 1️⃣ Prefer vertical side (top/bottom)
    if (verticalMax > horizontalMax) {

        if (spaceTop >= calc.tooltipHeight || spaceBottom >= calc.tooltipHeight) {

            val placeAbove = if (spaceTop >= calc.tooltipHeight && spaceBottom >= calc.tooltipHeight) {
                listOf(true, false).random()
            } else {
                spaceTop >= calc.tooltipHeight
            }

            val clampedX = clamp(
                rect.centerX(),
                calc.tooltipWidth / 2f + calc.safeLeft + margin,
                calc.screenWidth - calc.tooltipWidth / 2f - calc.safeRight - margin
            )

            val y = if (placeAbove) {
                rect.top - (calc.tooltipHeight / 2f) - margin
            } else {
                rect.bottom + (calc.tooltipHeight / 2f) + margin
            }

            return Offset(clampedX, y)
        }
    }

    // 2️⃣ Horizontal options (left/right)
    val candidates = mutableListOf<Pair<Boolean, Offset>>()  // Boolean means "left side"

    // left
    if (spaceLeft >= calc.tooltipWidth) {
        val clampedY = clamp(
            rect.centerY(),
            calc.tooltipHeight / 2f + calc.safeTop + margin,
            calc.screenHeight - calc.tooltipHeight / 2f - calc.safeBottom - margin
        )
        val x = rect.left - (calc.tooltipWidth / 2f) - margin
        candidates.add(true to Offset(x, clampedY))
    }

    // right
    if (spaceRight >= calc.tooltipWidth) {
        val clampedY = clamp(
            rect.centerY(),
            calc.tooltipHeight / 2f + calc.safeTop + margin,
            calc.screenHeight - calc.tooltipHeight / 2f - calc.safeBottom - margin
        )
        val x = rect.right + (calc.tooltipWidth / 2f) + margin
        candidates.add(false to Offset(x, clampedY))
    }

    // If left & right available → randomize like Swift
    if (candidates.isNotEmpty()) {
        val chosen = candidates.random().second

        val clampedX = clamp(
            chosen.x,
            calc.tooltipWidth / 2f + calc.safeLeft + margin,
            calc.screenWidth - calc.tooltipWidth / 2f - calc.safeRight - margin
        )
        val clampedY = clamp(
            chosen.y,
            calc.tooltipHeight / 2f + calc.safeTop + margin,
            calc.screenHeight - calc.tooltipHeight / 2f - calc.safeBottom + margin
        )

        return Offset(clampedX, clampedY)
    }

    // 3️⃣ fallback vertical
    if (spaceTop >= calc.tooltipHeight) {
        val clampedX = clamp(
            rect.centerX(),
            calc.tooltipWidth / 2 + calc.safeLeft + margin,
            calc.screenWidth - calc.tooltipWidth / 2 - calc.safeRight - margin
        )
        return Offset(
            clampedX,
            rect.top - calc.tooltipHeight / 2 - margin
        )
    }

    if (spaceBottom >= calc.tooltipHeight) {
        val clampedX = clamp(
            rect.centerX(),
            calc.tooltipWidth / 2 + calc.safeLeft + margin,
            calc.screenWidth - calc.tooltipWidth / 2 - calc.safeRight - margin
        )
        return Offset(
            clampedX,
            rect.bottom + calc.tooltipHeight / 2 + margin
        )
    }

    // 4️⃣ Final fallback (center near highlight)
    val fallbackX = clamp(
        rect.centerX(),
        calc.tooltipWidth / 2f + calc.safeLeft + margin,
        calc.screenWidth - calc.tooltipWidth / 2f - calc.safeRight - margin
    )
    val fallbackY = clamp(
        rect.centerY(),
        calc.tooltipHeight / 2f + calc.safeTop + margin,
        calc.screenHeight - calc.tooltipHeight / 2f - calc.safeBottom - margin
    )

    return Offset(fallbackX, fallbackY)
}

@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                setTextColor(android.graphics.Color.BLACK)
                textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textSize = 14f
                typeface = ResourcesCompat.getFont(context, R.font.font_regular)
            }
        },
        update = { textView ->
            textView.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    )
}



object SpotlightRegistry {
    private val items = mutableMapOf<Int, SpotlightItem>()

    fun register(id: Int, item: SpotlightItem) {
        items[id] = item
    }

    fun get(id: Int) = items[id]
}

data class SpotlightItem(
    val rect: Rect,
    val text: String
)


fun Modifier.spotlightTag(id: Int, text: String): Modifier =
    this.then(
        Modifier.layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) {
                placeable.place(0, 0)
            }
        }.onGloballyPositioned { layout ->
            SpotlightRegistry.register(
                id,
                SpotlightItem(
                    rect = layout.boundsInRoot(),
                    text = text
                )
            )
        }
    )


private fun clamp(v: Float, min: Float, max: Float): Float {
    return max(min, min(v, max))
}


