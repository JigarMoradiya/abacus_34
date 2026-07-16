package com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.components.CompareOperand
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

@Composable
fun BalanceScale(left: CompareOperand, right: CompareOperand, revealed: Boolean) {
    val s = deviceScale
    val beamLength = 240f * s
    val pivotHeight = 104f * s
    val stringLength = 30f * s

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
    LaunchedEffect(revealed) { if (revealed) revealStart = withFrameNanos { it } }

    val angle: Float = if (revealed) {
        val t = (nowNanos - revealStart) / 1_000_000_000.0
        (truthTilt * settle(t)).toFloat()
    } else {
        val t = (nowNanos - startNanos) / 1_000_000_000.0
        (sin(t * 1.8) * 4.5).toFloat()
    }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier.height((pivotHeight + 44f * s).dp)
    ) {
        // Fulcrum: post (apex at top) + base
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Canvas(modifier = Modifier.size((70f * s).dp, pivotHeight.dp)) {
                val path = Path().apply {
                    moveTo(size.width / 2f, 0f)
                    lineTo(0f, size.height)
                    lineTo(size.width, size.height)
                    close()
                }
                drawPath(path, brush = Brush.verticalGradient(listOf(Color(0xFFFFB74D), Color(0xFFFF8400))))
            }
            Box(
                modifier = Modifier
                    .size((96f * s).dp, (14f * s).dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF8D6E63))
            )
        }

        // Beam + hangers, rotate together around the top-center (the fulcrum apex)
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.graphicsLayer {
                rotationZ = angle
                transformOrigin = TransformOrigin(0.5f, 0f)
            }
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(beamLength.dp, (14f * s).dp)
                    .clip(RoundedCornerShape(50))
                    .background(Brush.horizontalGradient(listOf(Color(0xFFFFA726), Color(0xFFF57C00))))
            )
            Hanger(left, Color(0xFF0074D5), angle, s, stringLength,
                Modifier.align(Alignment.TopCenter).offset(x = (-beamLength / 2f + 1f).dp, y = (6f * s).dp))
            Hanger(right, Color(0xFFE91E63), angle, s, stringLength,
                Modifier.align(Alignment.TopCenter).offset(x = (beamLength / 2f - 1f).dp, y = (6f * s).dp))
        }
    }
}

@Composable
private fun Hanger(
    operand: CompareOperand,
    tint: Color,
    angle: Float,
    s: Float,
    stringLength: Float,
    modifier: Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.graphicsLayer {
            rotationZ = -angle                        // keep the card hanging straight down
            transformOrigin = TransformOrigin(0.5f, 0f)
        }
    ) {
        Box(
            modifier = Modifier
                .width((2f * s).dp)
                .height(stringLength.dp)
                .background(Color.Black.copy(alpha = 0.4f))
        )
        Text(
            text = operand.display,
            color = Color.Black,
            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
            fontSize = (26f * s).sp,
            modifier = Modifier
                .clip(RoundedCornerShape((16f * s).dp))
                .background(Color.White)
                .border((3f * s).dp, tint, RoundedCornerShape((16f * s).dp))
                .padding(horizontal = (16f * s).dp, vertical = (10f * s).dp)
        )
    }
}
