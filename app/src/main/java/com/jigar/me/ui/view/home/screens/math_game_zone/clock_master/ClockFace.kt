package com.jigar.me.ui.view.home.screens.math_game_zone.clock_master

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.screens.math_game_zone.GameCategoryType
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBounce
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBreathe
import com.jigar.me.ui.view.home.screens.math_game_zone.common.toyColor
import com.jigar.me.ui.view.home.screens.math_game_zone.gameCardStyle
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin

// A friendly toy alarm clock (Android twin of iOS ClockFaceView): a chunky rim in the game's colour with two bells that
// ring now and then, rainbow numerals and fat coloured hands. The hands SWING to each new time with a spring, the clock
// pops when a new time arrives and breathes gently while it waits: the star animation of Clock Master.
@Composable
fun ClockFace(hour: Int, minute: Int, questionKey: Int, diameter: Dp, modifier: Modifier = Modifier) {
    val hourAngleTarget = ((hour % 12) + minute / 60f) * 30f
    val minuteAngleTarget = minute * 6f

    val hourAngle = remember { Animatable(hourAngleTarget) }
    val minuteAngle = remember { Animatable(minuteAngleTarget) }
    LaunchedEffect(questionKey) {
        // Swing forward (never backwards) so the hands always sweep nicely.
        val hFrom = hourAngle.value % 360f
        val mFrom = minuteAngle.value % 360f
        hourAngle.snapTo(hFrom)
        minuteAngle.snapTo(mFrom)
        val hTo = if (hourAngleTarget >= hFrom) hourAngleTarget else hourAngleTarget + 360f
        val mTo = if (minuteAngleTarget >= mFrom) minuteAngleTarget else minuteAngleTarget + 360f
        coroutineScope {
            launch { hourAngle.animateTo(hTo, spring(dampingRatio = 0.65f, stiffness = 120f)) }
            launch { minuteAngle.animateTo(mTo, spring(dampingRatio = 0.65f, stiffness = 120f)) }
        }
    }

    // Seconds since the clock showed: drives the bells' ring (read only while drawing, so just that layer redraws).
    val time = remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) { withFrameNanos { now -> time.value = (now - start) / 1_000_000_000f } }
    }

    val colors = LocalAbColors.current
    val accent = gameCardStyle(GameCategoryType.CLOCK_MASTER)
    val gold = colors.accentYellow
    val hourColor = colors.accentOrange.abReadable(3f)
    val minuteColor = colors.brandPrimary
    val minuteTick = colors.textSecondary.copy(alpha = 0.45f)
    val hourTicks = List(12) { colors.toyColor(it) }
    val fontScale = LocalDensity.current.fontScale
    val d = diameter.value

    Box(
        modifier = modifier.size(diameter).abBounce(questionKey, 1.06f).abBreathe(0.012f, 2600),
        contentAlignment = Alignment.Center
    ) {
        // Two little bells on top. Every few seconds they shake like an alarm; in between they sit still.
        Canvas(Modifier.size(diameter)) {
            val dd = size.minDimension
            val c = Offset(size.width / 2f, size.height / 2f)
            val envelope = max(0f, sin(time.value * 0.8f)).pow(6)
            val shake = sin(time.value * 24f) * 7f * envelope
            rotate(shake, pivot = c) {
                clockMasterBell(c.x - dd * 0.30f, c.y - dd * 0.30f, dd * 0.1f, dd, gold)
                clockMasterBell(c.x + dd * 0.30f, c.y - dd * 0.30f, dd * 0.1f, dd, gold)
            }
        }

        // Rim, white face and the 60 ticks (an hour tick takes the rainbow colour of its number).
        Canvas(Modifier.size(diameter)) {
            val dd = size.minDimension
            val c = Offset(size.width / 2f, size.height / 2f)
            val rimR = dd * 0.47f
            drawCircle(accent.dark.abDarkened(0.2f), rimR, Offset(c.x, c.y + dd * 0.03f))
            drawCircle(accent.light, rimR, c)
            drawCircle(
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.34f), Color.Transparent), startY = c.y - rimR, endY = c.y
                ),
                radius = rimR, center = c
            )
            val rimRing = max(1f, dd * 0.01f)
            drawCircle(Color.White.copy(alpha = 0.45f), rimR - rimRing / 2f, c, style = Stroke(rimRing))

            val faceR = dd * 0.4f
            drawCircle(Color.White, faceR, c)
            val faceRing = max(1f, dd * 0.012f)
            drawCircle(accent.dark.copy(alpha = 0.28f), faceR - faceRing / 2f, c, style = Stroke(faceRing))

            for (i in 0 until 60) {
                val isHour = i % 5 == 0
                val w = if (isHour) dd * 0.02f else dd * 0.009f
                val h = if (isHour) dd * 0.045f else dd * 0.02f
                rotate(i * 6f, pivot = c) {
                    drawRoundRect(
                        color = if (isHour) hourTicks[i / 5] else minuteTick,
                        topLeft = Offset(c.x - w / 2f, c.y - dd * 0.365f - h / 2f),
                        size = Size(w, h),
                        cornerRadius = CornerRadius(w / 2f, w / 2f)
                    )
                }
            }
        }

        // The twelve rainbow numerals.
        for (i in 1..12) {
            val rad = (i * 30.0 - 90.0) * PI / 180.0
            val big = i % 3 == 0
            val fs = d * (if (big) 0.15f else 0.12f)
            Text(
                "$i",
                style = AbTypography.fixed(fs / fontScale, FontWeight.Black),
                color = colors.toyColor(i - 1),
                modifier = Modifier.offset(x = (cos(rad) * d * 0.28).dp, y = (sin(rad) * d * 0.28).dp)
            )
        }

        // Short fat hour hand (orange), long minute hand (blue), and the golden pin: both hands swing with a spring.
        Canvas(Modifier.size(diameter)) {
            val dd = size.minDimension
            val c = Offset(size.width / 2f, size.height / 2f)
            clockMasterHand(c, hourColor, dd * 0.22f, dd * 0.075f, dd, hourAngle.value)
            clockMasterHand(c, minuteColor, dd * 0.33f, dd * 0.055f, dd, minuteAngle.value)

            val pinR = dd * 0.085f / 2f
            drawCircle(accent.dark, pinR, c)
            drawCircle(gold, pinR - dd * 0.018f, c)
            drawCircle(Color.White.copy(alpha = 0.55f), dd * 0.009f, Offset(c.x - dd * 0.008f, c.y - dd * 0.01f))
        }
    }
}

/** One golden bell: a darker 3D edge, the face, a glossy top and a pale rim. */
private fun DrawScope.clockMasterBell(cx: Float, cy: Float, r: Float, dd: Float, gold: Color) {
    val center = Offset(cx, cy)
    drawCircle(gold.abDarkened(0.3f), r, Offset(cx, cy + dd * 0.012f))
    drawCircle(gold, r, center)
    drawCircle(
        brush = Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.5f), Color.Transparent), startY = cy - r, endY = cy),
        radius = r, center = center
    )
    drawCircle(Color.White.copy(alpha = 0.5f), r - 0.5.dp.toPx(), center, style = Stroke(1.dp.toPx()))
}

/** A fat capsule hand that pivots on the clock's centre: [length] to the tip, a short tail behind, a soft shadow and a shine. */
private fun DrawScope.clockMasterHand(c: Offset, color: Color, length: Float, width: Float, dd: Float, angle: Float) {
    val tail = dd * 0.05f
    val corner = CornerRadius(width / 2f, width / 2f)
    val topLeft = Offset(c.x - width / 2f, c.y - length)
    val handSize = Size(width, length + tail)
    rotate(angle, pivot = c) {
        drawRoundRect(Color.Black.copy(alpha = 0.22f), Offset(topLeft.x, topLeft.y + dd * 0.012f), handSize, corner)
        drawRoundRect(color, topLeft, handSize, corner)
        val shineW = width * 0.28f
        val inset = width * 0.45f
        drawRoundRect(
            Color.White.copy(alpha = 0.3f),
            Offset(c.x - width * 0.16f - shineW / 2f, topLeft.y + inset),
            Size(shineW, handSize.height - inset * 2f),
            CornerRadius(shineW / 2f, shineW / 2f)
        )
        val ring = 1.dp.toPx()
        drawRoundRect(
            Color.White.copy(alpha = 0.4f),
            Offset(topLeft.x + ring / 2f, topLeft.y + ring / 2f),
            Size(handSize.width - ring, handSize.height - ring),
            CornerRadius(width / 2f - ring / 2f, width / 2f - ring / 2f),
            style = Stroke(ring)
        )
    }
}
