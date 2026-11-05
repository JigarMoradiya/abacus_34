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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import com.jigar.me.ui.view.ab.softShadow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.composed
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.LocalAbColors
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.appScale
import com.jigar.me.ui.view.ab.LocalAbFitScale
import com.jigar.me.ui.view.base.abacus_base.SpotlightTooltipCalculation
import com.jigar.me.ui.view.home.theme.AppDimens
import kotlin.math.max
import kotlin.math.min

/**
 * Where a screen wants the tour layer drawn. The layer fills the screen and works in root coordinates, so it cannot live
 * inside a box that scales or clips the abacus (the fit-to-space box of the restyled screens). A screen that scales its
 * abacus provides one of these and shows [content] last in its full-screen root; without one the layer is drawn in place.
 */
class SpotlightPortal {
    var content: (@Composable () -> Unit)? by mutableStateOf(null)
}

val LocalSpotlightPortal = staticCompositionLocalOf<SpotlightPortal?> { null }

@Composable
fun SpotlightOverlay(currentSpot: Int?, total: Int, onNext: () -> Unit, onSkip: () -> Unit) {
    if (currentSpot == null) return

    val portal = LocalSpotlightPortal.current
    if (portal == null) {
        SpotlightLayer(currentSpot, total, onNext, onSkip)
        return
    }
    // re-published after every recomposition, so the layer always holds this pass's step and click handler
    SideEffect { portal.content = { SpotlightLayer(currentSpot, total, onNext, onSkip) } }
    DisposableEffect(portal) { onDispose { portal.content = null } }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
private fun SpotlightLayer(currentSpot: Int, total: Int, onNext: () -> Unit, onSkip: () -> Unit) {
    val item = SpotlightRegistry.get(currentSpot) ?: return
    val latestOnNext by rememberUpdatedState(onNext)
    val latestOnSkip by rememberUpdatedState(onSkip)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)  // 🔥 REQUIRED for clear mode
    ) {

        // Background dim layer
        Canvas(modifier = Modifier
            .fillMaxSize()
            // a tap outside does nothing (the Next / Skip buttons move the tour); the layer still takes the touch so the
            // abacus underneath cannot be moved during the tour
            .pointerInput(Unit) { detectTapGestures { } }
        ) {
            // Dim layer
            drawRect(Color.Black.copy(alpha = 0.70f))

            // 🔥 The clear highlight
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(item.rect.left, item.rect.top),
                size = Size(item.rect.width, item.rect.height),
                blendMode = BlendMode.Clear,      // THIS NOW WORKS!
                cornerRadius = CornerRadius(item.cornerRadius)
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

        // The Skip / Next bar sits at the same spot on every step (bottom right) so Next can be tapped again and again,
        // and the bubble keeps clear of it as well as of the lit window.
        var barHeightPx by remember { mutableIntStateOf(0) }
        val barClearancePx = with(density) { (AppDimens.Dimens16 * 2).toPx() }

        TooltipBox(
            item = item,
            screenWidth = screenWidthPx,
            screenHeight = screenHeightPx,
            safeInsets = safeInsets,
            reservedBottom = if (barHeightPx > 0) barHeightPx + barClearancePx else 0f,
        )

        TourControls(
            step = currentSpot,
            total = total,
            onNext = latestOnNext,
            onSkip = latestOnSkip,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(AppDimens.Dimens16)
                .onGloballyPositioned { barHeightPx = it.size.height },
        )
    }
}

/** Skip, the step counter and Next in one pill, fixed in the bottom right corner. The last step shows Done instead. */
@Composable
private fun TourControls(step: Int, total: Int, onNext: () -> Unit, onSkip: () -> Unit, modifier: Modifier = Modifier) {
    val isLast = step >= total - 1
    val pill = RoundedCornerShape(50)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
        modifier = modifier
            .shadow(AppDimens.Dimens8, pill)
            .background(Color.White, pill)
            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens8),
    ) {
        if (!isLast) {
            AbButton(text = "Skip", tone = AbTone.Ghost, size = AbSize.Compact, onClick = onSkip)
        }
        Text(
            text = "${step + 1} / $total",
            color = LocalAbColors.current.textSecondary,
            fontSize = (13f * appScale()).sp,
            fontWeight = FontWeight.Bold,
        )
        AbButton(text = if (isLast) "Done" else "Next →", tone = AbTone.Primary, size = AbSize.Small, onClick = onNext)
    }
}

@Composable
fun TooltipBox(
    item: SpotlightItem,
    screenWidth: Float,
    screenHeight: Float,
    safeInsets: WindowInsets,
    /** Room at the bottom the bubble must keep free (the control bar), on top of the system bars. */
    reservedBottom: Float = 0f,
) {
    var tooltipSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val corner = with(density) { AppDimens.Dimens20.toPx() }
    val outlineWidth = with(density) { 2.dp.toPx() }
    val gap = with(density) { AppDimens.Dimens16.toPx() }

    // TARGET center position
    val targetCenter = remember(item.rect, tooltipSize, reservedBottom) {
        calculateTooltipPosition(
            SpotlightTooltipCalculation(
                rect = RectF(item.rect.left, item.rect.top, item.rect.right, item.rect.bottom),
                screenWidth = screenWidth,
                screenHeight = screenHeight,
                safeTop = safeInsets.getTop(density).toFloat(),
                safeBottom = safeInsets.getBottom(density).toFloat() + reservedBottom,
                safeLeft = safeInsets.getLeft(density, layoutDirection).toFloat(),
                safeRight = safeInsets.getRight(density, layoutDirection).toFloat(),
                tooltipWidth = tooltipSize.width.toFloat(),
                tooltipHeight = tooltipSize.height.toFloat(),
                margin = gap,
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
            .drawBehind {
                // soft shadow, then the white card with a thin pale outline
                softShadow(Rect(0f, 0f, size.width, size.height), radius = corner, blur = corner * 0.5f, dy = corner * 0.15f, alpha = 0.35f)
                drawRoundRect(Color(0xFFD5D9F5), cornerRadius = CornerRadius(corner))
                drawRoundRect(
                    Color.White,
                    topLeft = Offset(outlineWidth, outlineWidth),
                    size = Size(size.width - outlineWidth * 2, size.height - outlineWidth * 2),
                    cornerRadius = CornerRadius(corner - outlineWidth),
                )
            }
            .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens14)
    ) {
        HtmlText(html = item.text)
    }
}


fun calculateTooltipPosition(calc: SpotlightTooltipCalculation): Offset {

    val margin = calc.margin

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

            // both fit: the roomier side, so the bubble does not jump between sides from one step to the next
            val placeAbove = if (spaceTop >= calc.tooltipHeight && spaceBottom >= calc.tooltipHeight) {
                spaceTop >= spaceBottom
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

    // If left & right are both available → the roomier side
    if (candidates.isNotEmpty()) {
        val chosen = (if (candidates.size == 2 && spaceRight > spaceLeft) candidates[1] else candidates[0]).second

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

    // 4️⃣ Final fallback: no side has room for the whole card (a tall card on a short landscape phone), so it hugs the
    // roomier top or bottom edge of the screen, inside the safe area, instead of sitting in the middle of the lit window.
    val fallbackX = clamp(
        rect.centerX(),
        calc.tooltipWidth / 2f + calc.safeLeft + margin,
        calc.screenWidth - calc.tooltipWidth / 2f - calc.safeRight - margin
    )
    val fallbackY = if (spaceBottom >= spaceTop) {
        calc.screenHeight - calc.safeBottom - calc.tooltipHeight / 2f - margin / 2f
    } else {
        calc.safeTop + calc.tooltipHeight / 2f + margin / 2f
    }

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
                textSize = 14f * appScale()
                typeface = ResourcesCompat.getFont(context, R.font.font_regular)
            }
        },
        update = { textView ->
            textView.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    )
}



object SpotlightRegistry {
    // observable, so the tour layer follows a rectangle that is measured again (the abacus is rescaled to fit)
    private val items = mutableStateMapOf<Int, SpotlightItem>()

    fun register(id: Int, item: SpotlightItem) {
        items[id] = item
    }

    fun get(id: Int) = items[id]
}

data class SpotlightItem(
    val rect: Rect,
    val text: String,
    /** Corner radius of the lit hole in px (the toy frame's own corner radii; 16 for the classic frame). */
    val cornerRadius: Float = 16f
)


fun Modifier.spotlightTag(id: Int, text: String, cornerRadius: Dp? = null): Modifier = composed {
    val density = LocalDensity.current
    val radiusPx = cornerRadius?.let { with(density) { it.toPx() } } ?: 16f
    // A screen that scales its abacus to fit changes the scale without laying the tag out again, so the rectangle taken at
    // layout time goes stale: measure it again once the new scale is in place.
    val fitScale = LocalAbFitScale.current
    var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    fun register(layout: LayoutCoordinates) {
        SpotlightRegistry.register(id, SpotlightItem(rect = layout.boundsInRoot(), text = text, cornerRadius = radiusPx))
    }
    LaunchedEffect(fitScale, coordinates) {
        withFrameNanos { }
        coordinates?.takeIf { it.isAttached }?.let { register(it) }
    }
    this.then(
        Modifier.layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) {
                placeable.place(0, 0)
            }
        }.onGloballyPositioned { layout ->
            coordinates = layout
            register(layout)
        }
    )
}


private fun clamp(v: Float, min: Float, max: Float): Float {
    return max(min, min(v, max))
}


