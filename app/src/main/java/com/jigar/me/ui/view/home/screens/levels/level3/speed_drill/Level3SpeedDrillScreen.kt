package com.jigar.me.ui.view.home.screens.levels.level3.speed_drill

import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbFitToSpace
import com.jigar.me.ui.view.ab.AbHeroPanel
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.abFixedStyle
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
        AudioPlayerManager.playSoundCountGo()
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
    val prefs = LocalPreferencesHelper.current
    val reviewGate = rememberReviewGateController()

    AbTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Lagoon)

            // ── Two-panel ─────────────────────────────────────────────────────────
            Row(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).screenLeftEdge()) {

                // LEFT: back button + status card (mode info, timer, score)
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.44f)
                        .padding(bottom = AppDimens.Dimens12, end = AppDimens.Dimens6)
                ) {
                    ScreenTopBar(title = mode.title, onBack = onBackClick)
                    Spacer(Modifier.height(AppDimens.Dimens8))

                    Box(
                        modifier = Modifier
                            .weight(1f).fillMaxWidth()
                            .padding(start = AppDimens.Dimens12)
                    ) {
                        AbHeroPanel(mode.endColor, Modifier.matchParentSize(), padding = 0.dp)
                        AbFitToSpace(Modifier.matchParentSize().padding(AppDimens.Dimens16)) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens14)
                            ) {
                                Text(mode.emoji, style = abEmojiStyle(abResponsive(38f)))
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy((-6).dp)
                                ) {
                                    Text(mode.title,
                                        style     = abFont(13f, FontWeight.Black),
                                        color     = Color.White,
                                        textAlign = TextAlign.Center)
                                    Text(mode.subtitle,
                                        style     = abFont(11f),
                                        color     = Color.White.copy(alpha = 0.95f),
                                        textAlign = TextAlign.Center)
                                }
                                // Large timer circle
                                val sweep by animateFloatAsState(progress, tween(1000, easing = LinearEasing), label = "drill-timer")
                                Box(Modifier.size(abResponsive(76f).dp), contentAlignment = Alignment.Center) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val strokeW = 7.dp.toPx()
                                        val inset   = strokeW / 2
                                        drawArc(
                                            color      = Color.White.copy(alpha = 0.20f),
                                            startAngle = -90f, sweepAngle = 360f, useCenter = false,
                                            topLeft    = Offset(inset, inset),
                                            size       = Size(size.width - strokeW, size.height - strokeW),
                                            style      = Stroke(strokeW)
                                        )
                                        drawArc(
                                            color      = timerColor,
                                            startAngle = -90f, sweepAngle = 360f * sweep, useCenter = false,
                                            topLeft    = Offset(inset, inset),
                                            size       = Size(size.width - strokeW, size.height - strokeW),
                                            style      = Stroke(strokeW, cap = StrokeCap.Round)
                                        )
                                    }
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens1)
                                    ) {
                                        Text("$timeLeft",
                                            style = abFont(24f, FontWeight.Black),
                                            color = Color.White)
                                        Text("sec",
                                            style = abFont(10f),
                                            color = Color.White.copy(alpha = 0.90f))
                                    }
                                }
                                // Score
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens2)
                                ) {
                                    Text("$score",
                                        style = abFont(30f, FontWeight.Black),
                                        color = Color.White)
                                    Text("pts",
                                        style = abFont(11f),
                                        color = Color.White.copy(alpha = 0.90f))
                                }
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
                ) {
                    AbHeroPanel(mode.endColor, Modifier.matchParentSize(), padding = 0.dp)
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
                        contentAlignment = Alignment.Center,
                        label          = "drill-phase",
                        modifier       = Modifier
                            .matchParentSize()
                            .padding(horizontal = AppDimens.Dimens16)
                            .padding(top = AppDimens.Dimens16, bottom = AppDimens.Dimens12)
                    ) { p ->
                        FitOnPhone(Modifier.fillMaxSize()) { flexible ->
                            when (p) {
                                is L3DrillPhase.Countdown -> Box(
                                    modifier         = Modifier.fillMaxWidth().padding(vertical = AppDimens.Dimens16),
                                    contentAlignment = Alignment.Center
                                ) {
                                    L3Countdown(p.n, digitSize = 80f)
                                }
                                is L3DrillPhase.Playing -> {
                                    val expr = buildString {
                                        currentSession.terms.forEachIndexed { i, t ->
                                            if (i == 0) append("${t.value}") else append(" ${t.sign} ${t.value}")
                                        }
                                        append(" = ?")
                                    }
                                    DrillPlaying(
                                        expr           = expr,
                                        wrongFlash     = wrongFlash,
                                        shakeOffset    = shakeOffset.value,
                                        typedValue     = typedValue,
                                        confirmEnabled = typedValue.isNotEmpty() && timeLeft > 0,
                                        flexible       = flexible,
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
                                    )
                                }
                                is L3DrillPhase.Result -> Unit
                            }
                        }
                    }
                }
            }

            // ── Result popup ──────────────────────────────────────────────────────
            (phase as? L3DrillPhase.Result)?.let { rp ->
                L3ResultPopup(
                    accent     = mode.endColor,
                    correct    = rp.score >= 1,
                    emoji      = when {
                        rp.score >= 10 -> "🌟🌟🌟"
                        rp.score >= 5  -> "🌟🌟"
                        rp.score >= 1  -> "🌟"
                        else           -> "💪"
                    },
                    message    = "In ${config.timeLimitSecs}s you solved ${rp.score} problems! ⚡",
                    retryLabel = "Play Again 🔄",
                    onRetry    = {
                        score          = 0
                        timeLeft       = config.timeLimitSecs
                        typedValue     = ""
                        wrongFlash     = false
                        shakeCount     = 0
                        currentSession = generateL3Session(2, config.digits)
                        resetKey++
                    },
                    onDone     = {
                        if (rp.score >= 10) reviewGate.attempt(prefs) { onFinished() } else onFinished()
                    },
                ) {
                    Text("${rp.score} Correct!",
                        style     = abFixedStyle(abMainTitleSize(), FontWeight.Black),
                        color     = Color.White,
                        textAlign = TextAlign.Center)
                }
            }

            ReviewGateHost(reviewGate, prefs)
        }
    }
}

/**
 * The question over the number pad. On a phone ([flexible] = false) everything is laid out at its natural size and shrunk to
 * fit by [FitOnPhone]; on a tablet the question plate takes the room the pad leaves.
 */
@Composable
private fun DrillPlaying(
    expr:           String,
    wrongFlash:     Boolean,
    shakeOffset:    Float,
    typedValue:     String,
    confirmEnabled: Boolean,
    flexible:       Boolean,
    onDigit:        (Int) -> Unit,
    onDelete:       () -> Unit,
    onConfirm:      () -> Unit,
) {
    Column(
        modifier            = if (flexible) Modifier.fillMaxSize() else Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (flexible) Modifier.weight(1f) else Modifier)
                .offset(x = shakeOffset.dp)
                .background(
                    if (wrongFlash) Color(0xFFE53935).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.20f),
                    RoundedCornerShape(AppDimens.Dimens16)
                ),
            contentAlignment = Alignment.Center
        ) {
            Crossfade(targetState = expr, animationSpec = tween(180), label = "drill-expr") { e ->
                Text(e,
                    style     = abFont(48f, FontWeight.Black),
                    color     = if (wrongFlash) Color(0xFFFF8A80) else Color.White,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.fillMaxWidth().padding(vertical = if (flexible) 0.dp else AppDimens.Dimens12))
            }
        }
        Spacer(Modifier.height(AppDimens.Dimens8))
        L3Numpad(
            typedValue     = typedValue,
            confirmEnabled = confirmEnabled,
            onDigit        = onDigit,
            onDelete       = onDelete,
            onConfirm      = onConfirm,
            modifier       = Modifier.fillMaxWidth().height(abResponsive(250f).dp)
        )
    }
}
