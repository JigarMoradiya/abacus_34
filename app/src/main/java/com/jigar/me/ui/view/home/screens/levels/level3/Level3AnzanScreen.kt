package com.jigar.me.ui.view.home.screens.levels.level3

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.base.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.screens.levels.level3.components.L3Numpad
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants

private sealed class L3AnzanPhase {
    data class Countdown(val n: Int) : L3AnzanPhase()
    data class Flash(val idx: Int)   : L3AnzanPhase()
    object Gap                       : L3AnzanPhase()
    object Answering                 : L3AnzanPhase()
    data class Result(val correct: Boolean, val expected: Int) : L3AnzanPhase()
}

@Composable
fun Level3AnzanScreen(
    config:      L3Config,
    onBackClick: () -> Unit,
    onFinished:  () -> Unit,
) {
    val session = remember { generateL3Session(config.terms, config.digits) }
    val mode    = config.mode
    val isSemi  = mode == L3Mode.SEMI_ANZAN

    var phase      by remember { mutableStateOf<L3AnzanPhase>(L3AnzanPhase.Countdown(3)) }
    var typedValue by remember { mutableStateOf("") }

    val abCalc = remember { AbacusCalculations(2) }
    val prefs  = LocalPreferencesHelper.current
    val theme  = prefs.getCustomParam(AppConstants.Settings.Theam, AppConstants.Settings.theam_Default)

    LaunchedEffect(Unit) {
        for (n in 3 downTo 1) {
            phase = L3AnzanPhase.Countdown(n)
            delay(800)
        }
        var running = 0
        for ((idx, term) in session.terms.withIndex()) {
            phase = L3AnzanPhase.Flash(idx)
            running += if (term.sign == "−") -term.value else term.value
            if (isSemi) abCalc.setAbacusValueFromString(running.toString())
            delay(config.flashMs.toLong())
            if (idx < session.terms.size - 1) {
                phase = L3AnzanPhase.Gap
                delay(200)
            }
        }
        phase = L3AnzanPhase.Answering
    }

    val cardShape = RoundedCornerShape(AppDimens.Dimens20)

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()

        Row(
            modifier          = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ── LEFT: back button + mode info + abacus (semi only) ─────────────
            Column(
                modifier = Modifier
                    .weight(if (isSemi) 0.44f else 0.32f)
                    .fillMaxHeight()
                    .padding(start = AppDimens.Dimens12, top = AppDimens.Dimens8, bottom = AppDimens.Dimens12, end = AppDimens.Dimens6)
            ) {
                BackButtonWithText(title = mode.title, onBackClick = onBackClick)
                Spacer(Modifier.height(AppDimens.Dimens8))

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

                if (isSemi) {
                    Spacer(Modifier.height(AppDimens.Dimens8))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(AppDimens.Dimens4)
                            .shadow(AppDimens.Dimens6, cardShape,
                                spotColor    = Color.Black.copy(0.18f),
                                ambientColor = Color.Black.copy(0.08f))
                            .background(Color.White.copy(0.90f), cardShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.fillMaxSize().scale(0.8f)) {
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
                                onReset = {}, onNext = {},
                            )
                        }
                    }
                }
            }

            // ── RIGHT: flash / answer panel — wraps height, centered ───────────
            Column(
                modifier = Modifier
                    .weight(if (isSemi) 0.56f else 0.68f)
                    .padding(start = AppDimens.Dimens6, end = AppDimens.Dimens12, top = AppDimens.Dimens8, bottom = AppDimens.Dimens12)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppDimens.Dimens4)
                        .shadow(AppDimens.Dimens8, cardShape,
                            spotColor    = mode.endColor.copy(0.38f),
                            ambientColor = mode.endColor.copy(0.20f))
                        .background(Brush.linearGradient(listOf(mode.startColor.copy(0.82f), mode.endColor.copy(0.82f))), cardShape)
                ) {
                    AnimatedContent(
                        targetState    = phase,
                        transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(150)) },
                        label          = "anzan-phase",
                        modifier       = Modifier.fillMaxWidth()
                    ) { p ->
                        when (p) {
                            is L3AnzanPhase.Countdown -> AnzanCountdownDisplay(p.n)
                            is L3AnzanPhase.Flash     -> AnzanFlashDisplay(session, p.idx)
                            is L3AnzanPhase.Gap       -> AnzanGapDisplay()
                            is L3AnzanPhase.Answering -> AnzanAnswerPanel(
                                typedValue = typedValue,
                                mode       = mode,
                                onDigit    = { typedValue = (typedValue + it.toString()).take(3) },
                                onDelete   = { if (typedValue.isNotEmpty()) typedValue = typedValue.dropLast(1) },
                                onConfirm  = {
                                    val typed = typedValue.toIntOrNull() ?: 0
                                    phase = L3AnzanPhase.Result(typed == session.answer, session.answer)
                                }
                            )
                            is L3AnzanPhase.Result -> Unit
                        }
                    }
                }
            }
        }

        // ── Result popup ────────────────────────────────────────────────────────
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
                                    typedValue = ""
                                    phase      = L3AnzanPhase.Countdown(3)
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
private fun AnzanCountdownDisplay(n: Int) {
    Column(
        modifier            = Modifier.fillMaxWidth().padding(vertical = AppDimens.Dimens40),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)
    ) {
        Text("$n",
            style      = MaterialTheme.typography.displayLarge.scaled(),
            color      = Color.White,
            fontWeight = FontWeight.Black,
            textAlign  = TextAlign.Center)
        Text("Get ready!",
            style      = MaterialTheme.typography.titleLarge.scaled(),
            color      = Color.White.copy(0.80f),
            fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun AnzanFlashDisplay(session: L3Session, idx: Int) {
    val term    = session.terms[idx]
    val display = if (term.sign.isEmpty()) "${term.value}" else "${term.sign} ${term.value}"
    Column(
        modifier            = Modifier.fillMaxWidth().padding(AppDimens.Dimens20),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)
    ) {
        Text("${idx + 1} / ${session.terms.size}",
            style      = MaterialTheme.typography.labelLarge.scaled(),
            color      = Color.White.copy(0.70f),
            fontWeight = FontWeight.Bold)
        Text(display,
            style      = MaterialTheme.typography.displayMedium.scaled(),
            color      = Color.White,
            fontWeight = FontWeight.Black,
            textAlign  = TextAlign.Center)
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)) {
            session.terms.forEachIndexed { i, _ ->
                Box(
                    modifier = Modifier
                        .size(AppDimens.Dimens10)
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
private fun AnzanGapDisplay() {
    Box(
        modifier         = Modifier.fillMaxWidth().padding(vertical = AppDimens.Dimens40),
        contentAlignment = Alignment.Center
    ) {
        Text("•••",
            style  = MaterialTheme.typography.headlineLarge.scaled(),
            color  = Color.White.copy(0.50f))
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
        modifier            = Modifier.fillMaxWidth().padding(AppDimens.Dimens16),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("What's the total? 🤔",
            style      = MaterialTheme.typography.titleLarge.scaled(),
            color      = Color.White,
            fontWeight = FontWeight.Black,
            textAlign  = TextAlign.Center)
        Spacer(Modifier.height(AppDimens.Dimens12))
        L3Numpad(
            typedValue = typedValue,
            onDigit    = onDigit,
            onDelete   = onDelete,
            onConfirm  = onConfirm,
            modifier   = Modifier.fillMaxWidth().height(AppDimens.Dimens260)
        )
    }
}
