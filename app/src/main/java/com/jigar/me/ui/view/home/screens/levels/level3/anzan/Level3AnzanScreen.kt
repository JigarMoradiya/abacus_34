package com.jigar.me.ui.view.home.screens.levels.level3.anzan

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.base.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.screens.levels.level3.*
import com.jigar.me.ui.view.home.screens.levels.level3.components.*
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants

private sealed class L3AnzanPhase {
    data class Countdown(val n: Int) : L3AnzanPhase()
    data class Flash(val idx: Int)   : L3AnzanPhase()
    object Answering                 : L3AnzanPhase()
    data class Result(val correct: Boolean, val expected: Int) : L3AnzanPhase()
}

@Composable
fun Level3AnzanScreen(
    config:      L3Config,
    onBackClick: () -> Unit,
    onFinished:  () -> Unit,
) {
    var session    by remember { mutableStateOf(generateL3Session(config.terms, config.digits)) }
    val mode    = config.mode
    val isSemi  = mode == L3Mode.SEMI_ANZAN

    var phase         by remember { mutableStateOf<L3AnzanPhase>(L3AnzanPhase.Countdown(3)) }
    var typedValue    by remember { mutableStateOf("") }
    var numberVisible by remember { mutableStateOf(true) }
    var restartKey    by remember { mutableIntStateOf(0) }

    val abCalc = remember { AbacusCalculations(2) }
    val prefs  = LocalPreferencesHelper.current
    val theme  = prefs.getCustomParam(AppConstants.Settings.Theam, AppConstants.Settings.theam_Default)

    LaunchedEffect(restartKey) {
        for (n in 3 downTo 1) {
            phase = L3AnzanPhase.Countdown(n)
            delay(800)
        }
        var running = 0
        for ((idx, term) in session.terms.withIndex()) {
            numberVisible = true
            phase = L3AnzanPhase.Flash(idx)
            running += if (term.sign == "−") -term.value else term.value
            if (isSemi) abCalc.setAbacusValueFromString(running.toString())
            delay(config.flashMs.toLong())
            if (idx < session.terms.size - 1) {
                numberVisible = false
                delay(250)
            }
        }
        phase = L3AnzanPhase.Answering
    }

    val cardShape = remember { RoundedCornerShape(AppDimens.Dimens20) }
    val cardBrush = remember(mode.startColor, mode.endColor) {
        Brush.linearGradient(listOf(mode.startColor.copy(0.82f), mode.endColor.copy(0.82f)))
    }

    val onDigit    : (Int) -> Unit = { typedValue = (typedValue + it.toString()).take(3) }
    val onDelete   : () -> Unit    = { if (typedValue.isNotEmpty()) typedValue = typedValue.dropLast(1) }
    val onConfirm  : () -> Unit    = {
        val typed = typedValue.toIntOrNull() ?: 0
        phase = L3AnzanPhase.Result(typed == session.answer, session.answer)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()

        if (isSemi) {
            // ── Two-panel: left = abacus, right = content ─────────────────────
            Row(
                modifier          = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEFT: back button + abacus (guided style)
                Column(
                    modifier = Modifier
                        .weight(0.44f).fillMaxHeight()
                        .padding(bottom = AppDimens.Dimens12, end = AppDimens.Dimens6)
                ) {
                    BackButtonWithText(title = mode.title, onBackClick = onBackClick)
                    Spacer(Modifier.height(AppDimens.Dimens8))
                    val abacusShape = RoundedCornerShape(AppDimens.Dimens24)
                    Box(
                        modifier = Modifier
                            .weight(1f).fillMaxWidth()
                            .padding(start = AppDimens.Dimens12).padding(AppDimens.Dimens4)
                            .background(Brush.linearGradient(listOf(Color.White.copy(0.18f), Color.White.copy(0.08f))), abacusShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AbacusWithDecimalCanvas(
                            selectedTheme               = theme,
                            screenType                  = AppConstants.AbacusScreen.screenTypeLevel2Practice,
                            abacusData                  = abCalc,
                            numberOfColumns             = 2,
                            rodMovement                 = emptyList(),
                            showDirectionHint           = false,
                            isBeadSoundOn               = false,
                            isDisplayCurrentAbacusInput = false,
                            onRodMovementChange         = {},
                            onShowDirectionHintsChange  = {},
                            onShowHighlighterChange     = {},
                            onReset = {}, onNext = {}
                        )
                    }
                }

                // RIGHT: content card
                Column(
                    modifier = Modifier
                        .weight(0.56f)
                        .padding(start = AppDimens.Dimens6, end = AppDimens.Dimens12, top = AppDimens.Dimens8, bottom = AppDimens.Dimens12)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth().padding(AppDimens.Dimens4)
                            .shadow(AppDimens.Dimens8, cardShape,
                                spotColor    = mode.endColor.copy(0.38f),
                                ambientColor = mode.endColor.copy(0.20f))
                            .background(cardBrush, cardShape)
                    ) {
                        AnzanContentPanel(phase, session, mode, typedValue, numberVisible, onDigit, onDelete, onConfirm)
                    }
                }
            }
        } else {
            // ── Single panel: full anzan ──────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxSize().padding(bottom = AppDimens.Dimens12)
            ) {
                BackButtonWithText(title = mode.title, onBackClick = onBackClick)
                Spacer(Modifier.height(AppDimens.Dimens8))
                Box(
                    modifier = Modifier
                        .weight(1f).fillMaxWidth()
                        .padding(horizontal = AppDimens.Dimens12).padding(AppDimens.Dimens4)
                        .shadow(AppDimens.Dimens8, cardShape,
                            spotColor    = mode.endColor.copy(0.38f),
                            ambientColor = mode.endColor.copy(0.20f))
                        .background(cardBrush, cardShape)
                ) {
                    AnzanContentPanel(phase, session, mode, typedValue, numberVisible, onDigit, onDelete, onConfirm)
                }
            }
        }

        // ── Result popup ──────────────────────────────────────────────────────
        val rp = phase as? L3AnzanPhase.Result
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
                            Text(if (rp.correct) "🌟🌟🌟" else "💪",
                                style = MaterialTheme.typography.displaySmall.scaled())
                            Text(
                                if (rp.correct) "Correct! ${rp.expected}" else "Answer: ${rp.expected}",
                                style      = MaterialTheme.typography.headlineMedium.scaled(),
                                color      = Color.White,
                                fontWeight = FontWeight.Black,
                                textAlign  = TextAlign.Center
                            )
                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(0.18f), RoundedCornerShape(AppDimens.Dimens16))
                                    .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens10)
                            ) {
                                Text(
                                    if (rp.correct) "Brilliant mental math! Keep it up! 🧠✨"
                                    else "Great try! Mental math takes practice — you'll get it! 💪",
                                    style      = MaterialTheme.typography.bodyLarge.scaled(),
                                    color      = Color.White.copy(0.92f),
                                    textAlign  = TextAlign.Center,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(Modifier.height(AppDimens.Dimens4))
                            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
                                L3ResultBtn("Try Again 🔄", filled = false) {
                                    abCalc.resetAbacusData()
                                    typedValue    = ""
                                    numberVisible = true
                                    session       = generateL3Session(config.terms, config.digits)
                                    phase         = L3AnzanPhase.Countdown(3)
                                    restartKey++
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

@Composable
private fun AnzanContentPanel(
    phase:         L3AnzanPhase,
    session:       L3Session,
    mode:          L3Mode,
    typedValue:    String,
    numberVisible: Boolean,
    onDigit:       (Int) -> Unit,
    onDelete:      () -> Unit,
    onConfirm:     () -> Unit,
) {
    val isAnswering = phase is L3AnzanPhase.Answering
    Column(
        modifier            = Modifier.fillMaxWidth().padding(AppDimens.Dimens16),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
    ) {
        // Mode info strip: hidden when numpad is showing
        if (!isAnswering) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
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
            Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(0.25f)))
        }

        // Phase-specific content — gap phase removed, numberVisible controls number alpha
        AnimatedContent(
            targetState    = phase,
            contentKey     = { p ->
                when (p) {
                    is L3AnzanPhase.Countdown -> "countdown"
                    is L3AnzanPhase.Flash     -> "flash"
                    is L3AnzanPhase.Answering -> "answering"
                    is L3AnzanPhase.Result    -> "result"
                }
            },
            transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(150)) },
            label          = "anzan-phase",
            modifier       = Modifier.fillMaxWidth()
        ) { p ->
            when (p) {
                is L3AnzanPhase.Countdown -> AnzanCountdownDisplay(p.n)
                is L3AnzanPhase.Flash     -> AnzanFlashDisplay(session, p.idx, numberVisible)
                is L3AnzanPhase.Answering -> AnzanAnswerPanel(typedValue, mode, onDigit, onDelete, onConfirm)
                is L3AnzanPhase.Result    -> Unit
            }
        }
    }
}

@Composable
private fun AnzanCountdownDisplay(n: Int) {
    Column(
        modifier            = Modifier.fillMaxWidth().padding(vertical = AppDimens.Dimens16),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)
    ) {
        Text("$n",
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

@Composable
private fun AnzanFlashDisplay(session: L3Session, idx: Int, numberVisible: Boolean) {
    val term     = session.terms[idx]
    val display  = if (term.sign.isEmpty()) "${term.value}" else "${term.sign} ${term.value}"
    val numAlpha by animateFloatAsState(
        targetValue   = if (numberVisible) 1f else 0f,
        animationSpec = tween(150),
        label         = "num-alpha"
    )
    Column(
        modifier            = Modifier.fillMaxWidth().padding(vertical = AppDimens.Dimens12),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
    ) {
        Text("${idx + 1}  /  ${session.terms.size}",
            style      = MaterialTheme.typography.labelLarge.scaled(),
            color      = Color.White.copy(0.70f),
            fontWeight = FontWeight.Bold,
            textAlign  = TextAlign.Center)
        // Fixed height box — number fades via alpha, layout never shifts
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppDimens.Dimens100)
                .alpha(numAlpha)
                .background(Color.White.copy(0.20f), RoundedCornerShape(AppDimens.Dimens16)),
            contentAlignment = Alignment.Center
        ) {
            Text(display,
                fontSize   = 72.sp.scaled(),
                color      = Color.White,
                fontWeight = FontWeight.Black,
                textAlign  = TextAlign.Center,
                modifier   = Modifier.fillMaxWidth())
        }
        // Line progress bar (compact)
        Row(
            modifier              = Modifier.fillMaxWidth(0.60f).height(AppDimens.Dimens8),
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4)
        ) {
            repeat(session.terms.size) { i ->
                Box(
                    modifier = Modifier
                        .weight(1f).fillMaxHeight()
                        .background(
                            if (i <= idx) Color.White else Color.White.copy(0.30f),
                            RoundedCornerShape(AppDimens.Dimens100)
                        )
                )
            }
        }
    }
}


@Composable
private fun AnzanAnswerPanel(
    typedValue: String,
    mode:       L3Mode,
    onDigit:    (Int) -> Unit,
    onDelete:   () -> Unit,
    onConfirm:  () -> Unit,
) {
    Column(
        modifier            = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("What's the total? 🤔",
            style      = MaterialTheme.typography.titleLarge.scaled(),
            color      = Color.White,
            fontWeight = FontWeight.Black,
            textAlign  = TextAlign.Center)
        Spacer(Modifier.height(AppDimens.Dimens8))
        L3Numpad(
            typedValue = typedValue,
            onDigit    = onDigit,
            onDelete   = onDelete,
            onConfirm  = onConfirm,
            modifier   = Modifier.fillMaxWidth().height(AppDimens.Dimens200)
        )
    }
}
