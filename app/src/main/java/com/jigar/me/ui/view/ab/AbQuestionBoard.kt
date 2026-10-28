package com.jigar.me.ui.view.ab

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import com.jigar.me.data.local.data.ExamProvider.AbacusFormulaType
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

// The question side of the Practice play screen: a chalkboard in a wooden rim that holds the sum, and a sticky-note hint
// card for the formula / table that goes with the current step. Both only draw; the screen keeps all the logic.
// Mirrors `Abacus/AbQuestionBoard.swift`.

// MARK: - Device-dependent sizes (iOS `DimensionsUtils`)

/**
 * The four device-dependent sizes the question board is built from. On iOS they come from `DimensionsUtils`; map them to
 * the Android dimension helpers by providing [LocalAbQuestionMetrics] (all values in dp). Without a provider the defaults
 * below (the iOS phone / iPad values, picked from [LocalAbScale]) are used.
 */
data class AbQuestionMetrics(
    /** `DimensionsUtils.abacusQueTextSize`: the size of one line of the question. */
    val queTextSize: Float,
    /** `DimensionsUtils.abacusAnsTextSize`: the size of the answer. */
    val ansTextSize: Float,
    /** `DimensionsUtils.abacusQueBoxWidth`: the width of the old question box (the board is based on it). */
    val queBoxWidth: Float,
    /** `DimensionsUtils.formulaTitleTextSize`: the size of the formula name on a hint tag. */
    val formulaTitleTextSize: Float,
) {
    companion object {
        val Phone = AbQuestionMetrics(queTextSize = 16f, ansTextSize = 18f, queBoxWidth = 70f, formulaTitleTextSize = 10f)
        val Tablet = AbQuestionMetrics(queTextSize = 28f, ansTextSize = 30f, queBoxWidth = 120f, formulaTitleTextSize = 16f)
        val LargeTablet = AbQuestionMetrics(queTextSize = 32f, ansTextSize = 36f, queBoxWidth = 140f, formulaTitleTextSize = 20f)

        fun forScale(scale: AbScale): AbQuestionMetrics = when {
            scale.isTablet && scale.factor >= 1.5f -> LargeTablet
            scale.isTablet -> Tablet
            else -> Phone
        }
    }
}

val LocalAbQuestionMetrics = staticCompositionLocalOf<AbQuestionMetrics?> { null }

/** The metrics in force: the provided ones, else the defaults for this device class. */
@Composable
fun abQuestionMetrics(): AbQuestionMetrics = LocalAbQuestionMetrics.current ?: AbQuestionMetrics.forScale(LocalAbScale.current)

/** The unit every board / hint measurement is a multiple of (`max(0.5, abacusQueTextSize / 32)` on iOS). */
@Composable
private fun abQuestionUnit(): Float = max(0.5f, abQuestionMetrics().queTextSize / 32f)

/**
 * How much the writing on a question board is scaled (1 = normal). A screen that shows a formula card beside the question
 * sets this below 1 (`CompositionLocalProvider(LocalAbQuestionTextScale provides 0.8f)` around the board), and the board,
 * which follows the text, gets smaller with it. Mirrors the `abQuestionTextScale` environment value.
 */
val LocalAbQuestionTextScale = compositionLocalOf { 1f }

// MARK: - Chalk

enum class AbChalkTone { Normal, Active, Dim }

object AbChalk {
    val White = Color(0xFFFAF7EC)
    val Yellow = Color(0xFFFFE45C)

    /** The writing is bigger than the old question text so it reads from across the table. */
    const val SCALE = 1.4f

    fun color(tone: AbChalkTone): Color = when (tone) {
        AbChalkTone.Normal -> White
        AbChalkTone.Active -> Yellow
        AbChalkTone.Dim -> White.copy(alpha = 0.58f)
    }
}

/** One line of chalk: a text in a box of 1.08 x its size, with an optional glow. */
@Composable
private fun AbChalkLine(text: String, sizePt: Float, color: Color, glow: Shadow?, modifier: Modifier = Modifier) {
    val style = abFixedStyle(sizePt, FontWeight.ExtraBold)
    Box(modifier.height((sizePt * 1.08f).dp), contentAlignment = Alignment.Center) {
        Text(
            text, style = if (glow != null) style.copy(shadow = glow) else style, color = color,
            maxLines = 1, softWrap = false, overflow = TextOverflow.Visible,
            // the line box is a little taller than the 1.08 x size box: let it keep its height (centred) instead of being
            // squeezed to the box, which pushed the digits down out of the highlight ring
            modifier = Modifier.wrapContentHeight(unbounded = true),
        )
    }
}

/**
 * One piece of chalk writing. The active one glows yellow; [pill] also rings it like a highlighter stroke.
 * [size] is in dp (iOS points); null = the question text size x [AbChalk.SCALE]. Both follow [LocalAbQuestionTextScale].
 */
@Composable
fun AbChalkText(
    text: String,
    modifier: Modifier = Modifier,
    tone: AbChalkTone = AbChalkTone.Normal,
    size: Float? = null,
    pill: Boolean = false,
) {
    val sizePt = (size ?: (abQuestionMetrics().queTextSize * AbChalk.SCALE)) * LocalAbQuestionTextScale.current
    val dens = LocalDensity.current.density
    val easeOut = tween<Float>(durationMillis = 200, easing = EaseOut)
    val colorSpec = tween<Color>(durationMillis = 200, easing = EaseOut)
    val color by animateColorAsState(AbChalk.color(tone), colorSpec, label = "abChalkColor")
    val glowColor by animateColorAsState(
        when (tone) {
            AbChalkTone.Active -> AbChalk.Yellow.copy(alpha = 0.6f)
            AbChalkTone.Normal -> AbChalk.White.copy(alpha = 0.22f)
            AbChalkTone.Dim -> AbChalk.White.copy(alpha = 0f)
        },
        colorSpec, label = "abChalkGlowColor",
    )
    val glowRadius by animateFloatAsState(sizePt * (if (tone == AbChalkTone.Active) 0.22f else 0.12f), easeOut, label = "abChalkGlow")
    val ring by animateFloatAsState(if (pill && tone == AbChalkTone.Active) 1f else 0f, easeOut, label = "abChalkRing")
    val ringModifier = if (pill) {
        Modifier
            .background(AbChalk.Yellow.copy(alpha = 0.16f * ring), CircleShape)
            .border(max(1.5f, sizePt * 0.05f).dp, AbChalk.Yellow.copy(alpha = 0.75f * ring), CircleShape)
            .padding(horizontal = (sizePt * 0.35f).dp)
    } else {
        Modifier
    }
    AbChalkLine(
        text, sizePt, color, Shadow(glowColor, Offset.Zero, glowRadius * dens),
        modifier.then(ringModifier),
    )
}

/** A dashed chalk line, the "equals" line above the answer. */
@Composable
fun AbChalkDash(modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxWidth().height(4.dp)) {
        drawLine(
            color = AbChalk.White.copy(alpha = 0.65f),
            start = Offset(0f, size.height / 2f),
            end = Offset(size.width, size.height / 2f),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(7.dp.toPx(), 6.dp.toPx())),
        )
    }
}

/** The answer under the line: a faint "?" until it is revealed, then big glowing chalk. */
@Composable
fun AbChalkAnswer(text: String, revealed: Boolean, modifier: Modifier = Modifier) {
    val sizePt = abQuestionMetrics().ansTextSize * AbChalk.SCALE * LocalAbQuestionTextScale.current
    val dens = LocalDensity.current.density
    Box(modifier.padding(vertical = 3.dp), contentAlignment = Alignment.Center) {
        AbChalkLine("?", sizePt, AbChalk.White.copy(alpha = 0.28f), null, Modifier.alpha(if (revealed) 0f else 1f))
        AbChalkLine(
            text, sizePt, AbChalk.Yellow, Shadow(AbChalk.Yellow.copy(alpha = 0.6f), Offset.Zero, sizePt * 0.22f * dens),
            Modifier.alpha(if (revealed) 1f else 0f),
        )
    }
}

// MARK: - Board

/**
 * The chalkboard in its wooden rim, with the chalk ledge under it. [content] is the writing (usually a centred Column with
 * [AbChalkText] lines, an [AbChalkDash] and an [AbChalkAnswer]). [contentWidth] is the width of the writing area (a little
 * wider than the old question box); it follows [LocalAbQuestionTextScale]. Unspecified = `queBoxWidth * 1.2 * AbChalk.SCALE`.
 */
@Composable
fun AbQuestionBoard(
    modifier: Modifier = Modifier,
    contentWidth: Dp = Dp.Unspecified,
    content: @Composable () -> Unit,
) {
    val colors = LocalAbColors.current
    val metrics = abQuestionMetrics()
    val textScale = LocalAbQuestionTextScale.current
    val q = abQuestionUnit()
    val rim = max(5f, 10f * q)
    val corner = max(12f, 22f * q)
    val writingWidth = (if (contentWidth.isSpecified) contentWidth else (metrics.queBoxWidth * 1.2f * AbChalk.SCALE).dp) * textScale
    val dark = colors.isDark
    val woodTop = if (dark) Color(0xFF9A6A3C) else Color(0xFFD9A15E)
    val woodBottom = if (dark) Color(0xFF6E4726) else Color(0xFFB57A3C)
    val slateTop = if (dark) Color(0xFF1F5249) else Color(0xFF2E8F73)
    val slateBottom = if (dark) Color(0xFF163A33) else Color(0xFF1F6D58)

    Column(modifier.padding(bottom = (5f * q).dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .drawBehind { drawBoardWood(q, corner, woodTop, woodBottom) }
                .padding(rim.dp)
                .drawBehind { drawBoardSlate(q, corner - rim * 0.45f, slateTop, slateBottom) }
                .padding(start = (12f * q).dp, end = (12f * q).dp, top = (10f * q).dp, bottom = (12f * q).dp),
        ) {
            Box(Modifier.width(writingWidth), contentAlignment = Alignment.Center) { content() }
        }
        ChalkTray(q, woodBottom)
    }
}

/** The wooden rim: a darker edge underneath, the wood face, its outline and a little grain. */
private fun DrawScope.drawBoardWood(q: Float, corner: Float, woodTop: Color, woodBottom: Color) {
    val r = corner.dp.toPx()
    drawRoundRect(woodBottom.abDarkened(0.4f), topLeft = Offset(0f, (5f * q).dp.toPx()), size = size, cornerRadius = CornerRadius(r))
    drawRoundRect(Brush.verticalGradient(listOf(woodTop, woodBottom)), size = size, cornerRadius = CornerRadius(r))
    val lw = 2.dp.toPx()
    drawRoundRect(
        woodBottom.abDarkened(0.45f), topLeft = Offset(lw / 2f, lw / 2f), size = Size(size.width - lw, size.height - lw),
        cornerRadius = CornerRadius(r - lw / 2f), style = Stroke(lw),
    )
    val clip = Path().apply { addRoundRect(RoundRect(0f, 0f, size.width, size.height, CornerRadius(r))) }
    clipPath(clip) {
        for (k in 1..3) {
            val y = size.height * k / 4f
            val line = Path().apply {
                moveTo(size.width * 0.05f, y)
                quadraticTo(size.width * 0.5f, y + (if (k % 2 == 0) 3.dp.toPx() else (-3).dp.toPx()), size.width * 0.95f, y)
            }
            drawPath(line, Color.Black.copy(alpha = 0.06f), style = Stroke(1.5.dp.toPx()))
        }
    }
}

/** The slate: its gradient, two smudges of chalk dust and a soft inner shadow along the top where the rim overhangs. */
private fun DrawScope.drawBoardSlate(q: Float, cornerPt: Float, slateTop: Color, slateBottom: Color) {
    val r = cornerPt.dp.toPx()
    drawRoundRect(Brush.verticalGradient(listOf(slateTop, slateBottom)), size = size, cornerRadius = CornerRadius(r))
    val clip = Path().apply { addRoundRect(RoundRect(0f, 0f, size.width, size.height, CornerRadius(r))) }
    clipPath(clip) {
        // x, y, width, height as fractions of the slate
        val dust = arrayOf(floatArrayOf(0.2f, 0.16f, 0.5f, 0.14f), floatArrayOf(0.78f, 0.9f, 0.5f, 0.12f))
        for (d in dust) {
            drawOval(
                Color.White.copy(alpha = 0.03f),
                topLeft = Offset(size.width * (d[0] - d[2] / 2f), size.height * (d[1] - d[3] / 2f)),
                size = Size(size.width * d[2], size.height * d[3]),
            )
        }
    }
    val lw = max(2f, 3f * q).dp.toPx()
    drawRoundRect(
        Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.32f), Color.Black.copy(alpha = 0f)), startY = 0f, endY = size.height / 2f),
        topLeft = Offset(lw / 2f, lw / 2f), size = Size(size.width - lw, size.height - lw),
        cornerRadius = CornerRadius(r - lw / 2f), style = Stroke(lw),
    )
}

/** The chalk ledge under the board, with a stick of chalk and a felt eraser. */
@Composable
private fun ChalkTray(q: Float, woodBottom: Color) {
    Box(
        Modifier.width((130f * q).dp).negativeTopPadding((2f * q).dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height((9f * q).dp)
                .background(woodBottom.abDarkened(0.2f), CircleShape)
                .border(1.5.dp, woodBottom.abDarkened(0.5f), CircleShape),
        )
        Row(
            Modifier.offset(y = (-6f * q).dp),
            horizontalArrangement = Arrangement.spacedBy((8f * q).dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // felt eraser
            Box(Modifier.size((18f * q).dp, (8f * q).dp).background(Color(0xFFE85C8A), RoundedCornerShape((2f * q).dp))) {
                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height((2.5f * q).dp)
                        .background(Color(0xFF4B4B6B), RoundedCornerShape((1.5f * q).dp)),
                )
            }
            Box(Modifier.size((24f * q).dp, (5.5f * q).dp).rotate(-5f).background(AbChalk.White, CircleShape))
            Box(Modifier.size((14f * q).dp, (5.5f * q).dp).rotate(4f).background(AbChalk.Yellow, CircleShape))
        }
    }
}

/** SwiftUI's negative `.padding(.top, -x)`: the element is placed [amount] higher and takes [amount] less height. */
private fun Modifier.negativeTopPadding(amount: Dp): Modifier = layout { measurable, constraints ->
    val p = measurable.measure(constraints)
    val up = amount.roundToPx()
    layout(p.width, max(0, p.height - up)) { p.place(0, -up) }
}

// MARK: - Hint card

/** What kind of hint a card carries; only used to pick the tag colour. */
enum class AbHintKind(val color: Color) {
    SmallFriend(Color(0xFF00A99D)),
    BigFriend(Color(0xFF7E5CF0)),
    Family(Color(0xFFF57C00)),
    Table(Color(0xFF1976D2));

    companion object {
        /** Same mapping as the iOS `init(formulaType:)`: anything that is not a friend / family formula is a table. */
        fun fromFormulaType(formulaType: String): AbHintKind = when (formulaType) {
            AbacusFormulaType.SMALL_FRIEND.description, AbacusFormulaType.SMALL_FRIEND_SUB.description -> SmallFriend
            AbacusFormulaType.BIG_FRIEND.description, AbacusFormulaType.BIG_FRIEND_SUB.description -> BigFriend
            AbacusFormulaType.COMBINATION.description, AbacusFormulaType.COMBINATION_SUB.description -> Family
            else -> Table
        }
    }
}

/** A sticky note with a little lightbulb, holding one or more hints (stacked, centred). */
@Composable
fun AbHintCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val colors = LocalAbColors.current
    val q = abQuestionUnit()
    val paperTop = if (colors.isDark) Color(0xFF4C3F14) else Color(0xFFFFF6C2)
    val paperBottom = if (colors.isDark) Color(0xFF3B3010) else Color(0xFFFFEA8F)
    val edge = if (colors.isDark) Color(0xFFA88510) else Color(0xFFE0B300)
    Box(modifier.padding(top = (8f * q).dp)) {
        Column(
            Modifier
                .drawBehind {
                    val r = CornerRadius((16f * q).dp.toPx())
                    drawRoundRect(edge.abDarkened(0.25f), topLeft = Offset(0f, (4f * q).dp.toPx()), size = size, cornerRadius = r)
                    drawRoundRect(Brush.verticalGradient(listOf(paperTop, paperBottom)), size = size, cornerRadius = r)
                    val lw = max(2f, 2.5f * q).dp.toPx()
                    drawRoundRect(
                        edge, topLeft = Offset(lw / 2f, lw / 2f), size = Size(size.width - lw, size.height - lw),
                        cornerRadius = CornerRadius(r.x - lw / 2f), style = Stroke(lw),
                    )
                }
                .padding(start = (12f * q).dp, end = (12f * q).dp, top = (16f * q).dp, bottom = (10f * q).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy((6f * q).dp),
            content = content,
        )
        // lightbulb badge
        Box(
            Modifier.align(Alignment.TopStart).offset(x = (-8f * q).dp, y = (-10f * q).dp).requiredSize((28f * q).dp),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(Modifier.requiredSize((28f * q).dp)) {
                val r = size.minDimension / 2f
                val gold = Color(0xFFFFB800)
                drawCircle(gold.abDarkened(0.3f), radius = r, center = Offset(center.x, center.y + (2f * q).dp.toPx()))
                drawCircle(Brush.verticalGradient(listOf(Color(0xFFFFE680), gold)), radius = r)
                val lw = max(1.5f, 2f * q).dp.toPx()
                drawCircle(Color.White, radius = r - lw / 2f, style = Stroke(lw))
            }
            Icon(Icons.Rounded.Lightbulb, null, Modifier.size((15f * q).dp), tint = Color(0xFF6B4A00))
        }
    }
}

/** The small coloured tag naming a hint ("Small Friend (+5)", "Multiplication"). */
@Composable
fun AbHintTag(title: String, kind: AbHintKind, modifier: Modifier = Modifier) {
    val dens = LocalDensity.current.density
    val t = abQuestionMetrics().formulaTitleTextSize
    val color = kind.color
    AbMinScaleText(
        text = title,
        style = abFixedStyle(t * 1.2f, FontWeight.ExtraBold).copy(shadow = abShadow(dens, Color.Black.copy(alpha = 0.25f), dy = 1f)),
        color = Color.White,
        modifier = modifier
            .drawBehind {
                drawRoundRect(color.abDarkened(0.3f), topLeft = Offset(0f, 2.dp.toPx()), size = size, cornerRadius = CornerRadius(size.height / 2f))
            }
            .background(Brush.verticalGradient(listOf(color.abBlended(Color.White, 0.18f), color)), CircleShape)
            .padding(horizontal = (t * 0.8f).dp, vertical = (t * 0.12f).dp),
        minScale = 0.6f,
        maxLines = 1,
    )
}

/** Ink for text written on a hint note. */
val AbColors.hintInk: Color get() = if (isDark) Color(0xFFFFF0B8) else Color(0xFF5A4300)

// MARK: - Panel (sizes the board and hint to the room that is left beside the abacus)

/**
 * The question board with its hint card. It measures both at their natural size, then scales the pair to the room it is
 * given: a 13-rod abacus leaves a narrow column (hint above the board), a 7-rod abacus leaves much more (bigger, and the
 * hint can sit beside the board). The hint keeps its place while no hint shows, so the board does not jump between steps of
 * one question. A new question ([resetKey] changes) starts the hint's place over, so the pair is laid out again for that
 * question's own hint instead of keeping the room the previous question's biggest hint needed. (The hint content is
 * recreated when [resetKey] changes.)
 *
 * The modifier must give it a bounded width and height: it fills what it gets (iOS `GeometryReader`). Both slots are
 * measured at their natural (unbounded) size, so the content must not depend on the incoming constraints.
 *
 * @param hintLeading In the side-by-side layout, whether the hint goes before (leading) or after the board.
 * @param boardMax The most the board may be scaled up (1 = its natural size).
 * @param hintMax The most the hint may be scaled up.
 * @param sideBySide Always put the hint beside the board and push the pair to one edge (a 7-rod abacus, which leaves a wide area).
 * @param packTrailing With [sideBySide]: pack against the trailing edge (true) or the leading one.
 * @param topInset Room to leave free at the top of the area (the pair is centred in what is left between the insets).
 * @param bottomInset Room to leave free at the bottom of the area.
 * @param resetKey Changes when a new question starts; the hint's remembered size starts over then.
 */
@Composable
fun AbQuestionPanel(
    showsHint: Boolean,
    modifier: Modifier = Modifier,
    hintLeading: Boolean = true,
    boardMax: Float = 1f,
    hintMax: Float = 1.3f,
    sideBySide: Boolean = false,
    packTrailing: Boolean = true,
    topInset: Dp = 0.dp,
    bottomInset: Dp = 0.dp,
    resetKey: Int = 0,
    hint: @Composable () -> Unit,
    board: @Composable () -> Unit,
) {
    val dens = LocalDensity.current.density
    val q = abQuestionUnit()
    // Natural sizes in dp. The hint's is the largest seen so far in this question (hints differ in size; within a
    // question the slot only grows); hintKey is the resetKey that it belongs to.
    var boardNat by remember { mutableStateOf(Size.Zero) }
    var hintNat by remember { mutableStateOf(Size.Zero) }
    var hintKey by remember { mutableStateOf<Int?>(null) }

    BoxWithConstraints(modifier) {
        val areaW = maxWidth.value
        val areaH = max(maxHeight.value - topInset.value - bottomInset.value, 1f)
        // keep a little air from the abacus and the screen edge
        val usableW = max(areaW - 16f * q, 1f)
        val usableH = max(areaH - 12f * q, 1f)
        val gap = 10f * q
        // Before the first measure a typical size stands in.
        val bw = if (boardNat.width > 0f) boardNat.width else 280f * q
        val bh = if (boardNat.width > 0f) boardNat.height else 360f * q
        val hw = if (!showsHint) 0f else if (hintNat.width > 0f) hintNat.width else 250f * q
        val hh = if (!showsHint) 0f else if (hintNat.width > 0f) hintNat.height else 110f * q

        // Hint above the board: each gets its own scale (the board keeps its size, the hint may grow), then both shrink
        // together if they are too tall for the area.
        val vb0 = min(boardMax, usableW / bw)
        val vh0 = if (showsHint) min(hintMax, usableW / hw) else 0f
        val fv = min(1f, usableH / (vb0 * bh + (if (showsHint) gap + vh0 * hh else 0f)))
        val vb = vb0 * fv
        val vh = vh0 * fv
        // Hint beside the board, for areas that are wide rather than tall (a 7-rod abacus).
        val sb0 = min(boardMax, usableH / bh)
        val sh0 = if (showsHint) min(hintMax, usableH / hh) else 0f
        val fs = if (showsHint) min(1f, usableW / (sb0 * bw + gap + sh0 * hw)) else 0f
        val side = showsHint && (sideBySide || sb0 * fs > vb * 1.08f)
        val boardScale = max(0.5f, if (side) sb0 * fs else vb)
        val hintScale = if (side) sh0 * fs else vh

        val boardW = bw * boardScale
        val boardH = bh * boardScale
        val hintW = hw * hintScale
        val hintH = hh * hintScale
        // The board is centred. The hint goes above it, and the board only moves down when the area is too short for the
        // hint to fit above a centred board.
        val boardY = if (side || !showsHint) areaH / 2f
        else min(max(areaH / 2f, hintH + gap + boardH / 2f + 4f * q), areaH - boardH / 2f)
        val sideWidth = boardW + gap + hintW
        val edge = max(14f * q, 10f) // air from the screen edge when packed to one side
        // Centred by default; with sideBySide the pair is packed against one edge instead.
        val left = if (!sideBySide) (areaW - sideWidth) / 2f
        else if (packTrailing) areaW - edge - sideWidth else edge
        val boardX = if (side) {
            if (hintLeading) left + hintW + gap + boardW / 2f else left + boardW / 2f
        } else if (sideBySide) {
            if (packTrailing) areaW - edge - boardW / 2f else edge + boardW / 2f
        } else {
            areaW / 2f
        }
        val hintX = if (side) {
            if (hintLeading) left + hintW / 2f else left + boardW + gap + hintW / 2f
        } else {
            areaW / 2f
        }
        val hintY = if (side) areaH / 2f else boardY - boardH / 2f - gap - hintH / 2f

        Box(Modifier.padding(top = topInset).size(areaW.dp, areaH.dp)) {
            // The board at its natural size, scaled about its centre and centred on (boardX, boardY).
            Box(Modifier.abCenteredScaled(boardX, boardY, boardScale)) {
                Box(
                    Modifier.onSizeChanged { px: IntSize ->
                        // Sub-point differences (text rounding at different scales) must not feed back into the scale.
                        val w = px.width / dens
                        val h = px.height / dens
                        if (w > 0f && (abs(w - boardNat.width) > 1f || abs(h - boardNat.height) > 1f)) boardNat = Size(w, h)
                    },
                ) { board() }
            }
            if (showsHint) {
                // The hint's place is kept (its slot is the largest hint seen), with the hint centred in it and scaled.
                // Keyed by resetKey: a new question's hint always reports its size, even when it equals the last one.
                key(resetKey) {
                    Box(Modifier.abCenteredScaled(hintX, hintY, hintScale)) {
                        Box(
                            Modifier.onSizeChanged { px: IntSize ->
                                val w = px.width / dens
                                val h = px.height / dens
                                if (resetKey != hintKey) {
                                    // A new question: the slot starts over at this question's first hint.
                                    hintKey = resetKey
                                    hintNat = Size(w, h)
                                } else if (w > hintNat.width + 1f || h > hintNat.height + 1f) {
                                    hintNat = Size(max(w, hintNat.width), max(h, hintNat.height))
                                }
                            },
                        ) { hint() }
                    }
                }
            }
        }
    }
}

/**
 * Puts the element, at its natural (unconstrained) size, with its centre at ([cx], [cy]) dp inside the parent and scales
 * it by [scale] about that centre. Layout size is the parent's, like SwiftUI `.position`.
 */
private fun Modifier.abCenteredScaled(cx: Float, cy: Float, scale: Float): Modifier = layout { measurable, constraints ->
    val p = measurable.measure(Constraints())
    val w = if (constraints.hasBoundedWidth) constraints.maxWidth else p.width
    val h = if (constraints.hasBoundedHeight) constraints.maxHeight else p.height
    val x = (cx.dp.toPx() - p.width / 2f).roundToInt()
    val y = (cy.dp.toPx() - p.height / 2f).roundToInt()
    layout(w, h) {
        p.placeWithLayer(x, y) {
            scaleX = scale
            scaleY = scale
        }
    }
}
