package com.jigar.me.ui.view.ab

import android.provider.Settings
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * The tile for one lesson/chapter. Not a flat gradient: the face carries an abacus (rods and beads) as its backdrop, a
 * floating emoji badge in a soft glow, a numbered medal, a glossy highlight, twinkling sparkles and a title plate holding
 * the bead stars. Sizes itself from [width] x [height], so it holds on phone and tablet landscape.
 *
 * Mirrors `AbLevelCard.swift`. `label` ("Lesson 3") is read aloud, and shown as a pill when no [number] is given.
 * Text parameters are already-resolved strings.
 *
 * @param showStars Chapters that are not scored (a reference card, a mode picker) hide the star beads.
 * @param tag A small pill in the top-right corner ("2 cols"); the lock takes its place when locked.
 * @param subtitle A second line under the title, inside the plate. When given (and there is no number) it replaces the
 * top-left pill.
 * @param cta A small white call-to-action pill at the foot of the plate ("Start").
 */
@Composable
fun AbLevelCard(
    emoji: String,
    title: String,
    label: String,
    accent: Color,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
    number: Int? = null,
    stars: Int = 0,
    isLocked: Boolean = false,
    showStars: Boolean = true,
    tag: String? = null,
    subtitle: String? = null,
    cta: String? = null,
    onClick: () -> Unit,
) {
    val w = width.value
    val h = height.value
    val edgeH = max(5f, h * 0.035f)
    val faceH = h - edgeH
    val clock = rememberAbCardClock(running = !rememberAbReduceMotion())
    Box(
        modifier
            .size(width, height)
            .abSquash(
                face = Color.Transparent, edge = accent.abDarkened(0.32f), stroke = null,
                cornerRadius = AbShapes.lg, edgeHeight = edgeH.dp, onClick = onClick,
            )
            .clearAndSetSemantics {
                contentDescription = "$label, $title"
                role = Role.Button
            },
    ) {
        AbLevelCardFace(
            emoji = emoji, title = title, label = label, number = number, accent = accent, w = w, faceH = faceH,
            stars = stars, isLocked = isLocked, showStars = showStars, tag = tag, subtitle = subtitle, cta = cta,
            clock = clock,
        )
    }
}

// MARK: - Face

@Composable
private fun AbLevelCardFace(
    emoji: String,
    title: String,
    label: String,
    number: Int?,
    accent: Color,
    w: Float,
    faceH: Float,
    stars: Int,
    isLocked: Boolean,
    showStars: Boolean,
    tag: String?,
    subtitle: String?,
    cta: String?,
    clock: FloatState,
) {
    val colors = LocalAbColors.current
    val dens = LocalDensity.current.density
    val shape = RoundedCornerShape(AbShapes.lg)
    val badge = min(faceH * 0.34f, w * 0.28f)
    val titleSize = min(faceH * 0.12f, w * 0.078f)
    val medal = max(24f, min(faceH * 0.16f, w * 0.12f))
    val plateH = faceH * 0.36f
    val top = accent.abBlended(Color.White, 0.26f)
    val deep = accent.abDarkened(0.12f)
    // Read only inside draw/graphicsLayer lambdas, so the 30 fps clock never recomposes the card.
    fun bob(): Float = sin(clock.floatValue * 2f + (number ?: 0)) * badge * 0.035f

    Box(Modifier.fillMaxSize().clip(shape)) {
        Canvas(Modifier.fillMaxSize()) { drawRect(Brush.verticalGradient(listOf(top, accent, deep))) }

        // Abacus backdrop: faint rods with beads, so every card says "abacus" even before you read it.
        AbRodBackdrop(Modifier.fillMaxSize().alpha(0.9f))

        // Soft glossy highlight sweeping from the top-left corner.
        Canvas(Modifier.fillMaxSize()) {
            val ew = size.width * 1.1f
            val eh = size.height * 0.62f
            val cx = size.width * 0.5f - size.width * 0.25f
            val cy = size.height * 0.5f - size.height * 0.42f
            drawOval(
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.38f), Color.White.copy(alpha = 0f)),
                    startY = cy - eh / 2f, endY = cy + eh / 2f,
                ),
                topLeft = Offset(cx - ew / 2f, cy - eh / 2f), size = Size(ew, eh),
            )
        }

        // Glow + floating badge, centred in the space above the title plate. (The iOS card also lays a fan of light rays
        // under the glow, but its own mask removes them entirely, so nothing is drawn for them here either.)
        val centerY = (faceH - plateH) * 0.5f + faceH * 0.02f
        val group = badge * 2.6f
        Box(
            Modifier.offset(x = (w / 2f - group / 2f).dp, y = (centerY - group / 2f).dp).requiredSize(group.dp),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(Modifier.requiredSize((badge * 1.9f).dp)) {
                val r = (badge * 0.95f).dp.toPx()
                drawCircle(
                    Brush.radialGradient(
                        listOf(Color.White.copy(alpha = 0.65f), Color.White.copy(alpha = 0f)),
                        center = center, radius = r,
                    ),
                    radius = r,
                )
            }
            // contact shadow on the "ground" under the badge
            Box(
                Modifier
                    .graphicsLayer { translationY = (badge * 0.62f - bob() * 0.4f) * dens }
                    .requiredSize((badge * 0.8f).dp, (badge * 0.14f).dp)
                    .blur(2.dp, BlurredEdgeTreatment.Unbounded)
                    .background(Color.Black.copy(alpha = 0.20f), AbCardEllipse),
            )
            Box(
                Modifier
                    .graphicsLayer { translationY = bob() * dens }
                    .requiredSize(badge.dp)
                    .shadow(4.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.22f), spotColor = Color.Black.copy(alpha = 0.22f))
                    .background(Color.White, CircleShape)
                    .border(max(2f, badge * 0.045f).dp, accent.abBlended(Color.White, 0.4f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(emoji, style = abEmojiStyle(abPt(badge * 0.56f)), maxLines = 1, softWrap = false)
            }
        }

        // Twinkling sparkles.
        AbSparkle(badge * 0.30f, 0f, clock, Modifier.offset(x = (w * 0.80f - badge * 0.15f).dp, y = (faceH * 0.20f - badge * 0.15f).dp))
        AbSparkle(badge * 0.20f, 2f, clock, Modifier.offset(x = (w * 0.16f - badge * 0.10f).dp, y = ((faceH - plateH) * 0.72f - badge * 0.10f).dp))
        AbSparkle(badge * 0.16f, 4f, clock, Modifier.offset(x = (w * 0.86f - badge * 0.08f).dp, y = ((faceH - plateH) * 0.80f - badge * 0.08f).dp))

        // Numbered medal (top-left) and lock / tag (top-right).
        Row(
            Modifier.align(Alignment.TopStart).fillMaxWidth().padding(max(faceH * 0.06f, 13f).dp),
            verticalAlignment = Alignment.Top,
        ) {
            AbMedal(number, label, subtitle, accent, medal)
            Spacer(Modifier.weight(1f))
            if (isLocked) {
                Box(Modifier.size(medal.dp).background(Color.Black.copy(alpha = 0.4f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Lock, null, Modifier.size((medal * 0.5f * 1.15f).dp), tint = Color.White)
                }
            } else if (tag != null) {
                Text(
                    tag, style = abFixedStyle(max(10f, medal * 0.42f), FontWeight.Bold), color = Color.White, maxLines = 1,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.25f), CircleShape)
                        .padding(horizontal = (medal * 0.3f).dp, vertical = (medal * 0.08f).dp),
                )
            }
        }

        // Title plate with stars, flush with the bottom of the face.
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(plateH.dp)
                .background(accent.abDarkened(0.45f).copy(alpha = 0.62f)),
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.align(Alignment.TopCenter).fillMaxWidth().height(1.5.dp).background(Color.White.copy(alpha = 0.22f)))
            Column(
                Modifier.fillMaxWidth().wrapContentHeight(unbounded = true).padding(horizontal = (w * 0.04f).dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy((faceH * 0.015f).dp),
            ) {
                // the title and its subtitle sit close: Baloo's line box leaves a wide empty band between two lines
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(if (subtitle != null) (-titleSize * 0.5f).dp else 0.dp),
                ) {
                    AbMinScaleText(
                        title, abFixedStyle(titleSize, FontWeight.ExtraBold).copy(shadow = abShadow(dens, Color.Black.copy(alpha = 0.3f), dy = 1.5f)),
                        Color.White, minScale = 0.65f, maxLines = 2,
                    )
                    if (subtitle != null) {
                        AbMinScaleText(
                            subtitle, abFixedStyle(max(10f, titleSize * 0.62f), FontWeight.SemiBold),
                            Color.White.copy(alpha = 0.88f), minScale = 0.7f, maxLines = 2,
                        )
                    }
                }
                if (cta != null) {
                    val ink = accent.abDarkened(0.25f)
                    Row(
                        Modifier
                            .drawBehind {
                                drawRoundRect(
                                    Color.Black.copy(alpha = 0.2f), topLeft = Offset(0f, 2.dp.toPx()), size = size,
                                    cornerRadius = CornerRadius(size.height / 2f),
                                )
                            }
                            .background(Color.White, CircleShape)
                            .padding(horizontal = (titleSize * 0.9f).dp, vertical = (titleSize * 0.22f).dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy((titleSize * 0.2f).dp),
                    ) {
                        Text(cta, style = abFixedStyle(max(11f, titleSize * 0.8f), FontWeight.ExtraBold), color = ink, maxLines = 1)
                        Canvas(Modifier.size((titleSize * 0.34f).dp, (titleSize * 0.56f).dp)) {
                            val lw = (titleSize * 0.15f).dp.toPx()
                            val chevron = Path().apply {
                                moveTo(lw / 2f, lw / 2f)
                                lineTo(size.width - lw / 2f, size.height / 2f)
                                lineTo(lw / 2f, size.height - lw / 2f)
                            }
                            drawPath(chevron, ink, style = Stroke(width = lw, cap = StrokeCap.Round, join = StrokeJoin.Round))
                        }
                    }
                }
                if (showStars) {
                    AbBeadStars(earned = stars, sizeFactor = max(0.5f, min(faceH / 300f, 1f)))
                }
            }
        }

        // The white rim of the face, on top of everything.
        Box(Modifier.fillMaxSize().border(2.dp, Color.White.copy(alpha = if (colors.isDark) 0.2f else 0.45f), shape))
    }
}

/** The number medal (white disc, accent ring), or the "Lesson 3" pill when the card has no number and no subtitle. */
@Composable
private fun AbMedal(number: Int?, label: String, subtitle: String?, accent: Color, size: Float) {
    if (number != null) {
        Box(
            Modifier
                .size(size.dp)
                .shadow(2.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.2f), spotColor = Color.Black.copy(alpha = 0.2f))
                .background(Color.White, CircleShape)
                .border(max(2f, size * 0.09f).dp, accent.abDarkened(0.2f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "$number", style = abFixedStyle(size * 0.62f, FontWeight.ExtraBold), color = accent.abDarkened(0.3f),
                maxLines = 1, softWrap = false, modifier = Modifier.wrapContentSize(unbounded = true),
            )
        }
    } else if (subtitle == null) {
        Text(
            label, style = abFixedStyle(max(9f, size * 0.4f), FontWeight.Bold), color = Color.White, maxLines = 1,
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.25f), CircleShape)
                .padding(horizontal = (size * 0.3f).dp, vertical = (size * 0.08f).dp),
        )
    }
}

// MARK: - Decoration

/**
 * Five rods with a few hexagonal beads each, drawn in translucent white behind the card content. Also the backdrop of
 * [AbHeroPanel]. Give it a size (fillMaxSize / matchParentSize).
 */
@Composable
fun AbRodBackdrop(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val rods = 5
        val beadW = size.width * 0.15f
        val beadH = beadW * 0.6f
        // Bead count and starting height per rod, so the pattern does not look like a grid.
        val counts = intArrayOf(3, 2, 4, 2, 3)
        val starts = floatArrayOf(0.50f, 0.58f, 0.44f, 0.60f, 0.52f)
        val rodW = 3.dp.toPx()
        val beadGap = 1.5.dp.toPx()
        val line = 1.dp.toPx()
        for (r in 0 until rods) {
            val x = size.width * (r + 0.5f) / rods
            drawRect(Color.White.copy(alpha = 0.12f), Offset(x - rodW / 2f, 0f), Size(rodW, size.height))
            for (b in 0 until counts[r]) {
                val y = size.height * starts[r] + b * (beadH + beadGap)
                val path = abBeadPath(Rect(x - beadW / 2f, y, x + beadW / 2f, y + beadH))
                drawPath(path, Color.White.copy(alpha = 0.13f))
                drawPath(path, Color.White.copy(alpha = 0.10f), style = Stroke(line))
            }
        }
    }
}

/** A four-point sparkle whose brightness follows the clock (radians = clock * 2.6 + [phaseOffset]). */
@Composable
private fun AbSparkle(sizePt: Float, phaseOffset: Float, clock: FloatState, modifier: Modifier) {
    Canvas(modifier.requiredSize(sizePt.dp)) {
        val a = 0.35f + 0.65f * (0.5f + 0.5f * sin(clock.floatValue * 2.6f + phaseOffset))
        val r = this.size.minDimension / 2f
        val k = r * 0.22f
        val c = this.center
        val star = Path().apply {
            moveTo(c.x, c.y - r)
            quadraticTo(c.x + k, c.y - k, c.x + r, c.y)
            quadraticTo(c.x + k, c.y + k, c.x, c.y + r)
            quadraticTo(c.x - k, c.y + k, c.x - r, c.y)
            quadraticTo(c.x - k, c.y - k, c.x, c.y - r)
            close()
        }
        scale(0.8f + 0.2f * a, c) { drawPath(star, Color.White.copy(alpha = a)) }
    }
}

// MARK: - Shared helpers of the card family (level card, practice card, table tile, panels, question board)

/** A true ellipse (RoundedCornerShape with 50% corners turns a wide box into a capsule instead). */
internal val AbCardEllipse = GenericShape { size, _ -> addOval(Rect(Offset.Zero, size)) }

/** `.position(x:y:)`: the element's centre sits at ([x], [y]) inside the parent; the parent's full size is claimed. */
internal fun Modifier.abPosition(x: Dp, y: Dp): Modifier = layout { measurable, constraints ->
    val p = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
    val w = if (constraints.hasBoundedWidth) constraints.maxWidth else p.width
    val h = if (constraints.hasBoundedHeight) constraints.maxHeight else p.height
    val px = x.roundToPx()
    val py = y.roundToPx()
    layout(w, h) { p.place(px - p.width / 2, py - p.height / 2) }
}

/** Seconds on a clock that advances about 30 times a second (the iOS cards animate at `minimumInterval: 1/30`). */
@Composable
internal fun rememberAbCardClock(running: Boolean = true): FloatState {
    val t = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(running) {
        if (!running) {
            t.floatValue = 0f
            return@LaunchedEffect
        }
        var last = 0L
        while (true) {
            withFrameNanos { now ->
                if (now - last >= 30_000_000L) {
                    last = now
                    t.floatValue = ((now / 1_000_000L) % 10_000_000L) / 1000f
                }
            }
        }
    }
    return t
}

/** The system "remove animations" setting (iOS: Reduce Motion): the decorative loops stand still. */
@Composable
internal fun rememberAbReduceMotion(): Boolean {
    val context = LocalContext.current
    return remember {
        runCatching {
            Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
        }.getOrDefault(false)
    }
}

/** A size in iOS points (= dp) as a font size that ignores the system font scale, like iOS `Font.custom(fixedSize:)`. */
@Composable
internal fun abPt(points: Float): TextUnit = (points / LocalDensity.current.fontScale).sp

/** [AbTypography.fixed] in points (dp), independent of the system font scale: for text inside fixed-size tiles. */
@Composable
internal fun abFixedStyle(points: Float, weight: FontWeight = FontWeight.ExtraBold, tabular: Boolean = false): TextStyle =
    AbTypography.fixed(points / LocalDensity.current.fontScale, weight, tabular)

/** Style for an emoji: the system emoji font, centred in its line box. */
private fun abEmojiStyle(size: TextUnit) = TextStyle(
    fontSize = size,
    lineHeight = size * 1.15f,
    platformStyle = PlatformTextStyle(includeFontPadding = false),
    lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.Both),
)

/** A text shadow given in points (dp): [dy] offset down, [radius] blur. */
internal fun abShadow(density: Float, color: Color, dy: Float = 0f, radius: Float = 0f): Shadow =
    Shadow(color, Offset(0f, dy * density), radius * density)

/**
 * `minimumScaleFactor`: shows [text] at the style's size, and steps the size down (never below [minScale] times it) while
 * the text does not fit its box ([maxLines] lines). A single line does not wrap.
 */
@Composable
internal fun AbMinScaleText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    minScale: Float = 1f,
    maxLines: Int = 1,
    textAlign: TextAlign = TextAlign.Center,
) {
    var scale by remember(text, style.fontSize, maxLines) { mutableFloatStateOf(1f) }
    Text(
        text = text,
        modifier = modifier,
        color = color,
        style = style.copy(
            fontSize = style.fontSize * scale,
            lineHeight = if (style.lineHeight.isSpecified) style.lineHeight * scale else style.lineHeight,
        ),
        textAlign = textAlign,
        maxLines = maxLines,
        softWrap = maxLines > 1,
        overflow = TextOverflow.Clip,
        onTextLayout = { result ->
            if ((result.hasVisualOverflow || result.didOverflowWidth) && scale > minScale + 0.001f) {
                scale = max(minScale, scale - 0.05f)
            }
        },
    )
}
