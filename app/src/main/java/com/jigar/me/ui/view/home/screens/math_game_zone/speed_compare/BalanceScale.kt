package com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBounce
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.components.CompareOperand
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sqrt

// Device scale — matches iOS (1.0 phone, 1.45 tablet, 1.7 large tablet)
private val deviceScale: Float
    get() = if (DeviceInfo.isLargeTablet) 1.7f else if (DeviceInfo.isTablet) 1.45f else 1.0f

// Damped spring 0→1 with a little overshoot (the satisfying bounce).
private fun settle(t: Double): Double {
    if (t <= 0) return 0.0
    val zeta = 0.32; val omega = 11.0
    val wd = omega * sqrt(1 - zeta * zeta)
    return 1 - exp(-zeta * omega * t) * (cos(wd * t) + (zeta * omega / wd) * sin(wd * t))
}

// Toy see-saw look (twin of iOS BalanceScaleView): a chunky candy plank on a bright star fulcrum, with the two numbers hanging
// from it as candy tiles that swing and bounce. Same clock-driven motion as before: idle rock, spring toward the heavier side.
@Composable
fun BalanceScale(left: CompareOperand, right: CompareOperand, revealed: Boolean) {
    val s = deviceScale
    val beamLength = 240f * s
    val pivotHeight = 104f * s
    val stringLength = 30f * s
    val plankHeight = 18f * s

    val truthTilt = when {
        left.value > right.value -> 13f
        left.value < right.value -> -13f
        else -> 0f
    }

    var nowNanos by remember { mutableLongStateOf(0L) }
    var startNanos by remember { mutableLongStateOf(0L) }
    var revealStart by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        startNanos = withFrameNanos { it }
        while (true) { withFrameNanos { nowNanos = it } }
    }
    LaunchedEffect(revealed) { revealStart = if (revealed) withFrameNanos { it } else 0L }

    val nowSec: Double = (nowNanos - startNanos) / 1_000_000_000.0
    val since: Double = if (revealStart == 0L) 0.0 else (nowNanos - revealStart) / 1_000_000_000.0

    val angle: Float = if (revealed) {
        (truthTilt * settle(since)).toFloat()
    } else {
        (sin(nowSec * 1.8) * 4.5).toFloat()
    }

    // Pan sway: the hanging tile swings a little on its string. Idle = a slow sway, revealed = a decaying swing.
    fun sway(side: Double): Float =
        if (revealed) (side * 6.0 * exp(-2.4 * since) * sin(9.0 * since)).toFloat()
        else (sin(nowSec * 2.1 + side * 1.4) * 2.5).toFloat()

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier.height((pivotHeight + 44f * s).dp)
    ) {
        BalanceFulcrum(s, pivotHeight)

        // Beam + hangers, rotate together around the top-center (the fulcrum apex)
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.graphicsLayer {
                rotationZ = angle
                transformOrigin = TransformOrigin(0.5f, 0f)
            }
        ) {
            BalancePlank(beamLength, plankHeight, s)
            Hanger(
                operand = left, tint = LocalAbColors.current.brandPrimary, revealed = revealed,
                heavier = revealed && left.value > right.value, lighter = revealed && left.value < right.value,
                angle = angle, swing = sway(1.0), s = s, stringLength = stringLength,
                modifier = Modifier.align(Alignment.TopCenter).offset(x = (-beamLength / 2f + 1f).dp, y = (plankHeight / 2f).dp)
            )
            Hanger(
                operand = right, tint = LocalAbColors.current.accentPink, revealed = revealed,
                heavier = revealed && right.value > left.value, lighter = revealed && right.value < left.value,
                angle = angle, swing = sway(-1.0), s = s, stringLength = stringLength,
                modifier = Modifier.align(Alignment.TopCenter).offset(x = (beamLength / 2f - 1f).dp, y = (plankHeight / 2f).dp)
            )
        }
    }
}

/** The chunky candy plank: orange face, glossy top, candy stripes near both ends, a soft rim, a darker 3D edge and a bolt. */
@Composable
private fun BalancePlank(beamLength: Float, plankHeight: Float, s: Float) {
    val colors = LocalAbColors.current
    val face = colors.accentOrange
    val boltColor = colors.accentYellow
    Canvas(modifier = Modifier.size(beamLength.dp, plankHeight.dp)) {
        val d = density
        val w = size.width
        val h = size.height
        val corner = CornerRadius(h / 2f, h / 2f)
        drawRoundRect(face.abDarkened(0.3f), topLeft = Offset(0f, 4f * s * d), size = size, cornerRadius = corner)
        drawRoundRect(face, size = size, cornerRadius = corner)
        drawRoundRect(
            Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.4f), Color.Transparent), startY = 0f, endY = h / 2f),
            size = size, cornerRadius = corner
        )
        // candy stripes near both ends
        val stripeW = 4f * s * d
        val stripeH = h * 0.45f
        val stripeGap = 5f * s * d
        val edgePad = 16f * s * d
        val groupW = 3f * stripeW + 2f * stripeGap
        for (side in 0 until 2) {
            val x0 = if (side == 0) edgePad else w - edgePad - groupW
            for (i in 0 until 3) {
                drawRoundRect(
                    Color.White.copy(alpha = 0.55f),
                    topLeft = Offset(x0 + i * (stripeW + stripeGap), (h - stripeH) / 2f),
                    size = Size(stripeW, stripeH),
                    cornerRadius = CornerRadius(stripeW / 2f, stripeW / 2f)
                )
            }
        }
        drawRoundRect(Color.White.copy(alpha = 0.45f), size = size, cornerRadius = corner, style = Stroke(width = 1.5f * d))
        // bolt in the middle of the plank, on the fulcrum's tip
        val b = 15f * s * d
        val c = Offset(w / 2f, h / 2f)
        drawCircle(boltColor.abDarkened(0.3f), radius = b / 2f, center = Offset(c.x, c.y + 1.5f * d))
        drawCircle(boltColor, radius = b / 2f, center = c)
        drawCircle(Color.White.copy(alpha = 0.6f), radius = b / 2f, center = c, style = Stroke(width = 1.5f * d))
        drawCircle(Color.White.copy(alpha = 0.7f), radius = b * 0.15f, center = Offset(c.x - b * 0.12f, c.y - b * 0.14f))
    }
}

/** Bright yellow rounded fulcrum with a star, on a purple candy base. */
@Composable
private fun BalanceFulcrum(s: Float, pivotHeight: Float) {
    val colors = LocalAbColors.current
    val postFace = colors.accentYellow
    val postEdge = colors.accentOrange
    val baseFace = colors.accentPurple
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(modifier = Modifier.size((70f * s).dp, pivotHeight.dp)) {
            val d = density
            val inset = 6f * s * d
            val path = Path().apply {
                moveTo(size.width / 2f, inset)
                lineTo(inset, size.height - inset)
                lineTo(size.width - inset, size.height - inset)
                close()
            }
            val round = Stroke(width = 12f * s * d, join = StrokeJoin.Round)
            translate(top = 4f * s * d) { drawPath(path, postEdge, style = round) }
            drawPath(path, postFace)
            drawPath(path, postFace, style = round)
            drawPath(path, Color.White.copy(alpha = 0.4f), style = Stroke(width = 2f * d, join = StrokeJoin.Round))
            speedCompareStar(Offset(size.width / 2f, size.height / 2f + 24f * s * d), 12f * s * d, Color.White.copy(alpha = 0.85f))
        }
        Canvas(modifier = Modifier.size((112f * s).dp, (16f * s).dp)) {
            val d = density
            val corner = CornerRadius(size.height / 2f, size.height / 2f)
            drawRoundRect(baseFace.abDarkened(0.3f), topLeft = Offset(0f, 4f * s * d), size = size, cornerRadius = corner)
            drawRoundRect(baseFace, size = size, cornerRadius = corner)
            drawRoundRect(
                Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.35f), Color.Transparent), startY = 0f, endY = size.height / 2f),
                size = size, cornerRadius = corner
            )
            drawRoundRect(Color.White.copy(alpha = 0.4f), size = size, cornerRadius = corner, style = Stroke(width = 1.5f * d))
        }
    }
}

/** A string and a candy number tile that hang straight down from a beam end however the beam tilts. */
@Composable
private fun Hanger(
    operand: CompareOperand,
    tint: Color,
    revealed: Boolean,
    heavier: Boolean,
    lighter: Boolean,
    angle: Float,
    swing: Float,
    s: Float,
    stringLength: Float,
    modifier: Modifier
) {
    val colors = LocalAbColors.current
    val face = tint.abReadable(3f)
    val emphasis by animateFloatAsState(
        targetValue = if (heavier) 1.08f else if (lighter) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow),
        label = "emphasis"
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.graphicsLayer {
            rotationZ = -angle + swing                // keep the card hanging straight down
            transformOrigin = TransformOrigin(0.5f, 0f)
        }
    ) {
        // string with a little ring at its top
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .size((3f * s).dp, stringLength.dp)
                .background(colors.textSecondary.copy(alpha = 0.7f), CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-4f * s).dp)
                    .requiredSize((10f * s).dp)
                    .background(Color.White, CircleShape)
                    .border((2.5f * s).dp, colors.textSecondary.copy(alpha = 0.85f), CircleShape)
            )
        }
        Text(
            text = operand.display,
            style = AbTypography.fixed(28f * s, FontWeight.Black).copy(
                shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f)
            ),
            color = Color.White,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier
                .graphicsLayer {
                    scaleX = emphasis
                    scaleY = emphasis
                    transformOrigin = TransformOrigin(0.5f, 0f)
                }
                .abBounce("${operand.display}|$revealed", 1.12f)
                .abToyTile(face, (16f * s).dp, (4f * s).dp)
                .padding(horizontal = (16f * s).dp, vertical = (8f * s).dp)
        )
    }
}

/** A five-point star, drawn centred on [center] (the fulcrum's emblem and the spark bursts). */
internal fun DrawScope.speedCompareStar(center: Offset, outer: Float, color: Color) {
    val inner = outer * 0.45f
    val path = Path()
    for (k in 0 until 10) {
        val r = if (k % 2 == 0) outer else inner
        val a = -PI / 2.0 + k * PI / 5.0
        val x = center.x + (cos(a) * r).toFloat()
        val y = center.y + (sin(a) * r).toFloat()
        if (k == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color)
}

/** A tick or a cross in white, centred on [c]. [half] is half the glyph's width. */
internal fun DrawScope.speedCompareMark(check: Boolean, c: Offset, half: Float, strokeWidth: Float) {
    val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val path = Path()
    if (check) {
        path.moveTo(c.x - half * 0.9f, c.y + half * 0.05f)
        path.lineTo(c.x - half * 0.25f, c.y + half * 0.65f)
        path.lineTo(c.x + half * 0.95f, c.y - half * 0.6f)
    } else {
        path.moveTo(c.x - half * 0.7f, c.y - half * 0.7f)
        path.lineTo(c.x + half * 0.7f, c.y + half * 0.7f)
        path.moveTo(c.x + half * 0.7f, c.y - half * 0.7f)
        path.lineTo(c.x - half * 0.7f, c.y + half * 0.7f)
    }
    drawPath(path, Color.White, style = stroke)
}
