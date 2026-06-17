package com.jigar.me.ui.view.home.screens.levels.level3.speed_drill

import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
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
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.sheets.ReviewGateHost
import com.jigar.me.ui.view.home.common_ui.sheets.rememberReviewGateController
import com.jigar.me.ui.view.home.screens.levels.level3.*
import com.jigar.me.ui.view.home.screens.levels.level3.components.*
import com.jigar.me.ui.view.home.theme.AppDimens

private sealed class L3DrillPhase {
    data class Countdown(val n: Int)     : L3DrillPhase()
    data class Playing(val attempt: Int) : L3DrillPhase()
    data class Result(val score: Int)    : L3DrillPhase()
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
    var wrongFlash     by remember { mutableStateOf(false) }
    var shakeCount     by remember { mutableIntStateOf(0) }
    var resetKey       by remember { mutableIntStateOf(0) }

    // Shake animation
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(shakeCount) {
        if (shakeCount > 0) {
            shakeOffset.snapTo(0f)
            shakeOffset.animateTo(8f, tween(50))
            shakeOffset.animateTo(-8f, tween(50))
            shakeOffset.animateTo(6f, tween(50))
            shakeOffset.animateTo(-6f, tween(50))
            shakeOffset.animateTo(0f, tween(50))
        }
    }

    // Wrong flash reset — independent of timer
    LaunchedEffect(wrongFlash) {
        if (wrongFlash) { delay(500); wrongFlash = false }
    }

    // Countdown + timer — runs once; resetKey restart on Play Again
    LaunchedEffect(resetKey) {
        for (n in 3 downTo 1) { phase = L3DrillPhase.Countdown(n); delay(800) }
        phase = L3DrillPhase.Playing(0)
        while (timeLeft > 0) { delay(1000); timeLeft-- }
        phase = L3DrillPhase.Result(score)
    }

    val progress   = timeLeft.toFloat() / config.timeLimitSecs.toFloat()
    val timerColor = when {
        progress > 0.5f  -> Color(0xFF4CAF50)
        progress > 0.25f -> Color(0xFFFFB300)
        else             -> Color(0xFFE53935)
    }
    val cardShape = remember { RoundedCornerShape(AppDimens.Dimens20) }
    val cardBrush = remember(mode.startColor, mode.endColor) {
        Brush.linearGradient(listOf(mode.startColor.copy(0.82f), mode.endColor.copy(0.82f)))
    }
    val prefs = LocalPreferencesHelper.current
    val reviewGate = rememberReviewGateController()
    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()

        // ── Two-panel ─────────────────────────────────────────────────────────
        Row(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {

            // LEFT: back button + status card (mode info, timer, score)
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.44f)
                    .padding(bottom = AppDimens.Dimens12, end = AppDimens.Dimens6)
            ) {
                BackButtonWithText(title = mode.title, onBackClick = onBackClick)
                Spacer(Modifier.height(AppDimens.Dimens8))

                Box(
                    modifier = Modifier
                        .weight(1f).fillMaxWidth()
                        .padding(start = AppDimens.Dimens12)
                        .shadow(AppDimens.Dimens8, cardShape,
                            spotColor    = mode.endColor.copy(0.38f),
                            ambientColor = mode.endColor.copy(0.20f))
                        .background(cardBrush, cardShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(AppDimens.Dimens16),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens14)
                    ) {
                        Text(mode.emoji, style = MaterialTheme.typography.titleLarge.scaled())
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens2)
                        ) {
                            Text(mode.title,
                                style      = MaterialTheme.typography.labelLarge.scaled(),
                                color      = Color.White,
                                fontWeight = FontWeight.Black,
                                textAlign  = TextAlign.Center)
                            Text(mode.subtitle,
                                style     = MaterialTheme.typography.labelSmall.scaled(),
                                color     = Color.White.copy(0.80f),
                                textAlign = TextAlign.Center)
                        }
                        // Large timer circle
                        Box(contentAlignment = Alignment.Center) {
                            Canvas(modifier = Modifier.size(AppDimens.Dimens72)) {
                                val strokeW = 7.dp.toPx()
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
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$timeLeft",
                                    style      = MaterialTheme.typography.titleMedium.scaled(),
                                    color      = Color.White,
                                    fontWeight = FontWeight.Black)
                                Text("sec",
                                    style = MaterialTheme.typography.labelSmall.scaled(),
                                    color = Color.White.copy(0.70f))
                            }
                        }
                        // Score
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$score",
                                style      = MaterialTheme.typography.headlineSmall.scaled(),
                                color      = Color.White,
                                fontWeight = FontWeight.Black)
                            Text("pts",
                                style = MaterialTheme.typography.labelSmall.scaled(),
                                color = Color.White.copy(0.70f))
                        }
                    }
                }
            }

            // RIGHT: phase content card
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.56f)
                    .padding(start = AppDimens.Dimens6, end = AppDimens.Dimens12)
                    .padding(top = DeviceInfo.screenTopPadding(), bottom = AppDimens.Dimens12)
                    .shadow(AppDimens.Dimens8, cardShape,
                        spotColor    = mode.endColor.copy(0.38f),
                        ambientColor = mode.endColor.copy(0.20f))
                    .background(cardBrush, cardShape),
                contentAlignment = Alignment.Center
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    AnimatedContent(
                        targetState    = phase,
                        contentKey     = { p ->
                            when (p) {
                                is L3DrillPhase.Countdown -> "countdown"
                                is L3DrillPhase.Playing   -> "playing"
                                is L3DrillPhase.Result    -> "result"
                            }
                        },
                        transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(150)) },
                        label          = "drill-phase",
                        modifier       = Modifier.size(maxWidth, maxHeight)
                    ) { p ->
                        when (p) {
                            is L3DrillPhase.Countdown -> Box(
                                modifier         = Modifier.fillMaxSize().padding(vertical = AppDimens.Dimens16),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)
                                ) {
                                    Text("${p.n}",
                                        fontSize   = 80.sp.scaled(),
                                        color      = Color.White,
                                        fontWeight = FontWeight.Black,
                                        textAlign  = TextAlign.Center)
                                    Text("Get ready!",
                                        style      = MaterialTheme.typography.headlineLarge.scaled(),
                                        color      = Color.White.copy(0.80f),
                                        fontWeight = FontWeight.Medium)
                                }
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
                                            .weight(1f)
                                            .offset(x = shakeOffset.value.dp)
                                            .background(
                                                if (wrongFlash) Color(0xFFE53935).copy(0.25f)
                                                else Color.White.copy(0.20f),
                                                RoundedCornerShape(AppDimens.Dimens16)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Crossfade(targetState = expr, animationSpec = tween(180), label = "drill-expr") { e ->
                                            Text(e,
                                                fontSize   = 52.sp.scaled(),
                                                color      = if (wrongFlash) Color(0xFFFF8A80) else Color.White,
                                                fontWeight = FontWeight.Black,
                                                textAlign  = TextAlign.Center,
                                                modifier   = Modifier.fillMaxWidth())
                                        }
                                    }
                                    Spacer(Modifier.height(AppDimens.Dimens8))
                                    L3Numpad(
                                        typedValue     = typedValue,
                                        confirmEnabled = typedValue.isNotEmpty() && timeLeft > 0,
                                        onDigit        = { typedValue = (typedValue + it.toString()).take(3) },
                                        onDelete       = { if (typedValue.isNotEmpty()) typedValue = typedValue.dropLast(1) },
                                        onConfirm      = {
                                            if (timeLeft > 0) {
                                                val typed = typedValue.toIntOrNull() ?: -1
                                                typedValue = ""
                                                if (typed == currentSession.answer) {
                                                    score++
                                                    currentSession = generateL3Session(2, config.digits)
                                                    phase = L3DrillPhase.Playing(p.attempt + 1)
                                                } else {
                                                    shakeCount++
                                                    wrongFlash = true
                                                    phase = L3DrillPhase.Playing(p.attempt + 1)
                                                }
                                            }
                                        },
                                        modifier       = Modifier.fillMaxWidth().height(AppDimens.Dimens240)
                                    )
                                }
                            }
                            is L3DrillPhase.Result -> Unit
                        }
                    }
                }
            }
        }

        // ── Result popup ──────────────────────────────────────────────────────
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
                            .fillMaxWidth(0.52f).padding(AppDimens.Dimens6)
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
                                    wrongFlash     = false
                                    shakeCount     = 0
                                    currentSession = generateL3Session(2, config.digits)
                                    resetKey++
                                }
                                L3ResultBtn("Done ✓", filled = true, onClick = {
                                    if (rp.score >= 10) reviewGate.attempt(prefs) { onFinished() } else onFinished()
                                })
                            }
                        }
                    }
                }
            }
        }

        ReviewGateHost(reviewGate, prefs)
    }
}
