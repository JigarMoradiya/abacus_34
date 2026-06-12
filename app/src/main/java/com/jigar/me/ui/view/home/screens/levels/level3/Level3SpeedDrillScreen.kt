package com.jigar.me.ui.view.home.screens.levels.level3

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.screens.levels.level3.components.L3Numpad
import com.jigar.me.ui.view.home.theme.AppDimens

private sealed class L3DrillPhase {
    data class Countdown(val n: Int)                                 : L3DrillPhase()
    data class Playing(val attempt: Int, val wrongFlash: Boolean)    : L3DrillPhase()
    data class Result(val score: Int)                                : L3DrillPhase()
}

@Composable
fun Level3SpeedDrillScreen(
    config:      L3Config,
    onBackClick: () -> Unit,
    onFinished:  () -> Unit,
) {
    val mode = config.mode

    var phase          by remember { mutableStateOf<L3DrillPhase>(L3DrillPhase.Countdown(3)) }
    var score          by remember { mutableIntStateOf(0) }
    var timeLeft       by remember { mutableIntStateOf(config.timeLimitSecs) }
    var typedValue     by remember { mutableStateOf("") }
    var currentSession by remember { mutableStateOf(generateL3Session(2, config.digits)) }

    LaunchedEffect(Unit) {
        for (n in 3 downTo 1) { phase = L3DrillPhase.Countdown(n); delay(800) }
        phase = L3DrillPhase.Playing(0, false)
    }

    LaunchedEffect(phase) {
        if (phase is L3DrillPhase.Playing) {
            while (timeLeft > 0 && phase is L3DrillPhase.Playing) {
                delay(1000)
                timeLeft--
            }
            if (timeLeft <= 0 && phase is L3DrillPhase.Playing) phase = L3DrillPhase.Result(score)
        }
        val p = phase as? L3DrillPhase.Playing ?: return@LaunchedEffect
        if (p.wrongFlash) { delay(500); phase = L3DrillPhase.Playing(p.attempt, false) }
    }

    val progress    = timeLeft.toFloat() / config.timeLimitSecs.toFloat()
    val timerColor  = when {
        progress > 0.5f  -> Color(0xFF4CAF50)
        progress > 0.25f -> Color(0xFFFFB300)
        else             -> Color(0xFFE53935)
    }
    val cardShape = RoundedCornerShape(AppDimens.Dimens20)

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()

        Row(modifier = Modifier.fillMaxSize()) {

            // ── LEFT: back button + mode info + timer + score ──────────────────
            Column(
                modifier = Modifier
                    .weight(0.38f)
                    .fillMaxHeight()
                    .padding(start = AppDimens.Dimens12, top = AppDimens.Dimens8, bottom = AppDimens.Dimens12, end = AppDimens.Dimens6)
            ) {
                BackButtonWithText(title = mode.title, onBackClick = onBackClick)
                Spacer(Modifier.height(AppDimens.Dimens8))

                // Mode info strip
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppDimens.Dimens4)
                        .shadow(AppDimens.Dimens4, cardShape,
                            spotColor    = mode.endColor.copy(0.40f),
                            ambientColor = mode.endColor.copy(0.20f))
                        .background(Brush.linearGradient(listOf(mode.startColor, mode.endColor)), cardShape)
                        .padding(horizontal = AppDimens.Dimens14, vertical = AppDimens.Dimens10)
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10)
                    ) {
                        Text(mode.emoji, style = MaterialTheme.typography.titleLarge.scaled())
                        Column {
                            Text(mode.title,
                                style      = MaterialTheme.typography.labelLarge.scaled(),
                                color      = Color.White,
                                fontWeight = FontWeight.Black)
                            Text(mode.subtitle,
                                style = MaterialTheme.typography.labelSmall.scaled(),
                                color = Color.White.copy(0.80f))
                        }
                    }
                }

                Spacer(Modifier.height(AppDimens.Dimens8))

                // Timer + score card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(AppDimens.Dimens4)
                        .shadow(AppDimens.Dimens8, cardShape,
                            spotColor    = mode.endColor.copy(0.40f),
                            ambientColor = mode.endColor.copy(0.20f))
                        .background(Brush.linearGradient(listOf(mode.startColor.copy(0.85f), mode.endColor.copy(0.85f))), cardShape)
                        .padding(AppDimens.Dimens20),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens16)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Canvas(modifier = Modifier.size(AppDimens.Dimens100)) {
                                val strokeW = 12.dp.toPx()
                                val inset   = strokeW / 2
                                drawArc(
                                    color      = Color.White.copy(0.20f),
                                    startAngle = -90f, sweepAngle = 360f, useCenter = false,
                                    topLeft    = Offset(inset, inset),
                                    size       = Size(size.width - strokeW, size.height - strokeW),
                                    style      = Stroke(strokeW)
                                )
                                drawArc(
                                    color      = timerColor,
                                    startAngle = -90f, sweepAngle = 360f * progress, useCenter = false,
                                    topLeft    = Offset(inset, inset),
                                    size       = Size(size.width - strokeW, size.height - strokeW),
                                    style      = Stroke(strokeW, cap = StrokeCap.Round)
                                )
                            }
                            Text("$timeLeft",
                                style      = MaterialTheme.typography.headlineMedium.scaled(),
                                color      = Color.White,
                                fontWeight = FontWeight.Black)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Score",
                                style      = MaterialTheme.typography.labelLarge.scaled(),
                                color      = Color.White.copy(0.80f),
                                fontWeight = FontWeight.Bold)
                            Text("$score",
                                style      = MaterialTheme.typography.displaySmall.scaled(),
                                color      = Color.White,
                                fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            // ── RIGHT: question + numpad — top-aligned ─────────────────────────
            Column(
                modifier = Modifier
                    .weight(0.62f)
                    .fillMaxHeight()
                    .padding(start = AppDimens.Dimens6, end = AppDimens.Dimens12, top = AppDimens.Dimens8, bottom = AppDimens.Dimens12)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(AppDimens.Dimens4)
                        .shadow(AppDimens.Dimens8, cardShape,
                            spotColor    = Color.Black.copy(0.15f),
                            ambientColor = Color.Black.copy(0.08f))
                        .background(Color.White.copy(0.18f), cardShape)
                ) {
                    AnimatedContent(
                        targetState    = phase,
                        transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(150)) },
                        label          = "drill-phase",
                        modifier       = Modifier.fillMaxSize()
                    ) { p ->
                        when (p) {
                            is L3DrillPhase.Countdown -> Box(
                                Modifier.fillMaxSize(), Alignment.Center
                            ) {
                                Text("${p.n}",
                                    style      = MaterialTheme.typography.displayLarge.scaled(),
                                    color      = mode.startColor,
                                    fontWeight = FontWeight.Black)
                            }
                            is L3DrillPhase.Playing -> {
                                val terms = currentSession.terms
                                val expr  = buildString {
                                    terms.forEachIndexed { i, t ->
                                        if (i == 0) append("${t.value}") else append(" ${t.sign} ${t.value}")
                                    }
                                    append(" = ?")
                                }
                                Column(
                                    modifier            = Modifier.fillMaxSize().padding(AppDimens.Dimens16),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                if (p.wrongFlash) Color(0xFFE53935).copy(0.25f)
                                                else mode.startColor.copy(0.12f),
                                                RoundedCornerShape(AppDimens.Dimens12)
                                            )
                                            .padding(vertical = AppDimens.Dimens14, horizontal = AppDimens.Dimens16),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(expr,
                                            style      = MaterialTheme.typography.headlineMedium.scaled(),
                                            color      = if (p.wrongFlash) Color(0xFFB71C1C) else mode.startColor,
                                            fontWeight = FontWeight.Black,
                                            textAlign  = TextAlign.Center)
                                    }
                                    Spacer(Modifier.height(AppDimens.Dimens12))
                                    L3Numpad(
                                        typedValue = typedValue,
                                        onDigit    = { typedValue = (typedValue + it.toString()).take(3) },
                                        onDelete   = { if (typedValue.isNotEmpty()) typedValue = typedValue.dropLast(1) },
                                        onConfirm  = {
                                            val typed = typedValue.toIntOrNull() ?: -1
                                            typedValue = ""
                                            if (typed == currentSession.answer) {
                                                score++
                                                currentSession = generateL3Session(2, config.digits)
                                                phase = L3DrillPhase.Playing(p.attempt + 1, false)
                                            } else {
                                                phase = L3DrillPhase.Playing(p.attempt + 1, true)
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth().weight(1f)
                                    )
                                }
                            }
                            is L3DrillPhase.Result -> Unit
                        }
                    }
                }
            }
        }

        // ── Result popup ────────────────────────────────────────────────────────
        val rp = phase as? L3DrillPhase.Result
        AnimatedVisibility(
            visible  = rp != null,
            enter    = fadeIn(tween(300)) + scaleIn(initialScale = 0.85f, animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f)),
            exit     = fadeOut(tween(200)) + scaleOut(targetScale = 0.85f),
            modifier = Modifier.fillMaxSize()
        ) {
            if (rp != null) {
                val popupShape = RoundedCornerShape(AppDimens.Dimens24)
                Box(
                    modifier         = Modifier.fillMaxSize().background(Color.Black.copy(0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.52f)
                            .padding(AppDimens.Dimens6)
                            .shadow(AppDimens.Dimens16, popupShape,
                                spotColor    = mode.endColor.copy(0.50f),
                                ambientColor = mode.endColor.copy(0.30f))
                            .background(Brush.linearGradient(listOf(mode.startColor, mode.endColor)), popupShape)
                            .padding(AppDimens.Dimens28),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens14)
                        ) {
                            val emoji = when {
                                rp.score >= 10 -> "🌟🌟🌟"
                                rp.score >= 5  -> "🌟🌟"
                                rp.score >= 1  -> "🌟"
                                else           -> "💪"
                            }
                            Text(emoji, style = MaterialTheme.typography.displaySmall.scaled())
                            Text("${rp.score} Correct!",
                                style      = MaterialTheme.typography.headlineMedium.scaled(),
                                color      = Color.White,
                                fontWeight = FontWeight.Black,
                                textAlign  = TextAlign.Center)
                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(0.18f), RoundedCornerShape(AppDimens.Dimens16))
                                    .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens10)
                            ) {
                                Text(
                                    "In ${config.timeLimitSecs}s you solved ${rp.score} problems! ⚡",
                                    style      = MaterialTheme.typography.bodyLarge.scaled(),
                                    color      = Color.White.copy(0.92f),
                                    textAlign  = TextAlign.Center,
                                    fontWeight = FontWeight.Medium)
                            }
                            Spacer(Modifier.height(AppDimens.Dimens4))
                            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
                                L3ResultBtn("Play Again 🔄", filled = false) {
                                    score          = 0
                                    timeLeft       = config.timeLimitSecs
                                    typedValue     = ""
                                    currentSession = generateL3Session(2, config.digits)
                                    phase          = L3DrillPhase.Countdown(3)
                                }
                                L3ResultBtn("Done ✓", filled = true, onClick = onFinished)
                            }
                        }
                    }
                }
            }
        }
    }
}
