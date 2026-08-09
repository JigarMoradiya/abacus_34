package com.jigar.me.ui.view.home.screens.math_game_zone.clock_master

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// A friendly analog clock. The hands SWING to each new time with a spring —
// the star animation of Clock Master.
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

    // One Paint reused across all 12 numbers and every frame of the swing.
    val numberPaint = remember {
        android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(0x3E, 0x27, 0x23)
            textAlign = android.graphics.Paint.Align.CENTER
            isFakeBoldText = true
            isAntiAlias = true
        }
    }

    Canvas(modifier = modifier.size(diameter)) {
        val r = min(size.width, size.height) / 2f
        val c = Offset(size.width / 2f, size.height / 2f)
        numberPaint.textSize = r * 0.24f

        // Face
        drawCircle(Color.White, radius = r, center = c)
        drawCircle(Color(0xFFFF8400), radius = r, center = c, style = Stroke(width = r * 0.06f))

        // Hour ticks + numbers
        for (i in 1..12) {
            val a = Math.toRadians(i * 30.0 - 90).toFloat()
            val tickOuter = Offset(c.x + cos(a) * r * 0.88f, c.y + sin(a) * r * 0.88f)
            val tickInner = Offset(c.x + cos(a) * r * 0.8f, c.y + sin(a) * r * 0.8f)
            drawLine(Color(0xFF5D4037), tickInner, tickOuter, strokeWidth = r * 0.03f, cap = StrokeCap.Round)
            val numPos = Offset(c.x + cos(a) * r * 0.66f, c.y + sin(a) * r * 0.66f)
            drawContext.canvas.nativeCanvas.drawText(
                "$i", numPos.x, numPos.y + r * 0.09f, numberPaint
            )
        }

        // Minute hand (long, blue)
        val ma = Math.toRadians(minuteAngle.value.toDouble() - 90).toFloat()
        drawLine(
            Color(0xFF0074D5), c,
            Offset(c.x + cos(ma) * r * 0.72f, c.y + sin(ma) * r * 0.72f),
            strokeWidth = r * 0.055f, cap = StrokeCap.Round
        )
        // Hour hand (short, dark)
        val ha = Math.toRadians(hourAngle.value.toDouble() - 90).toFloat()
        drawLine(
            Color(0xFF3E2723), c,
            Offset(c.x + cos(ha) * r * 0.48f, c.y + sin(ha) * r * 0.48f),
            strokeWidth = r * 0.075f, cap = StrokeCap.Round
        )
        // Center pin
        drawCircle(Color(0xFFFF8400), radius = r * 0.06f, center = c)
    }
}
