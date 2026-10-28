package com.jigar.me.ui.view.home.screens.levels.level3.anzan

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import com.jigar.me.ui.view.base.abacus_base.AbacusTheme
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.AbacusFrameStyle
import com.jigar.me.ui.view.base.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.sheets.ReviewGateHost
import com.jigar.me.ui.view.home.common_ui.sheets.rememberReviewGateController
import com.jigar.me.ui.view.home.screens.levels.level3.*
import com.jigar.me.ui.view.home.screens.levels.level3.components.*
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.utils.AppConstants

private sealed class L3AnzanPhase {
    data class Countdown(val n: Int) : L3AnzanPhase()
    data class Flash(val idx: Int)   : L3AnzanPhase()
    object Answering                 : L3AnzanPhase()
    data class Result(val correct: Boolean, val expected: Int, val userAnswer: Int) : L3AnzanPhase()
}

@Composable
fun Level3AnzanScreen(
    config:      L3Config,
    onBackClick: () -> Unit,
    onFinished:  () -> Unit,
) {
    var session by remember { mutableStateOf(generateL3Session(config.terms, config.digits)) }
    val mode   = config.mode
    val isSemi = mode == L3Mode.SEMI_ANZAN

    var phase         by remember { mutableStateOf<L3AnzanPhase>(L3AnzanPhase.Countdown(3)) }
    var typedValue    by remember { mutableStateOf("") }
    var numberVisible by remember { mutableStateOf(true) }
    var restartKey    by remember { mutableIntStateOf(0) }

    val abCalc = remember { AbacusCalculations(2) }
    val prefs  = LocalPreferencesHelper.current
    val reviewGate = rememberReviewGateController()
    val theme  = AbacusTheme.active(prefs)

    LaunchedEffect(restartKey) {
        for (n in 3 downTo 1) { phase = L3AnzanPhase.Countdown(n); delay(800) }
        AudioPlayerManager.playSoundCountGo()
        var running = 0
        for ((idx, term) in session.terms.withIndex()) {
            numberVisible = true
            phase = L3AnzanPhase.Flash(idx)
            running += if (term.sign == "−") -term.value else term.value
            if (isSemi) abCalc.setAbacusValueFromString(running.toString())
            delay(config.flashMs.toLong())
            if (idx < session.terms.size - 1) { numberVisible = false; delay(250) }
        }
        phase = L3AnzanPhase.Answering
    }

    val onDigit   : (Int) -> Unit = { typedValue = (typedValue + it.toString()).take(3) }
    val onDelete  : () -> Unit    = { if (typedValue.isNotEmpty()) typedValue = typedValue.dropLast(1) }
    val onConfirm : () -> Unit    = {
        val typed = typedValue.toIntOrNull() ?: 0
        phase = L3AnzanPhase.Result(typed == session.answer, session.answer, typed)
    }

    AbTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Lagoon)

            if (isSemi) {
                // ── Two-panel: left = abacus, right = content card ────────────────
                Row(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).screenLeftEdge()) {
                    Column(
                        modifier = Modifier
                            .weight(0.44f).fillMaxHeight()
                            .padding(bottom = AppDimens.Dimens12, end = AppDimens.Dimens6)
                    ) {
                        ScreenTopBar(title = mode.title, onBack = onBackClick)
                        Spacer(Modifier.height(AppDimens.Dimens8))
                        AbGlassPanel(
                            modifier = Modifier.weight(1f).fillMaxWidth().padding(start = AppDimens.Dimens12)
                        ) {
                            Box(Modifier.fillMaxSize().padding(AppDimens.Dimens8), contentAlignment = Alignment.Center) {
                                AbacusWithDecimalCanvas(
                                    selectedTheme               = theme,
                                    screenType                  = AppConstants.AbacusScreen.screenTypeLevel2Practice,
                                    abacusData                  = abCalc,
                                    numberOfColumns             = 2,
                                    rodMovement                 = emptyList(),
                                    showDirectionHint           = false,
                                    isBeadSoundOn               = false,
                                    isDisplayCurrentAbacusInput = false,
                                    frameStyle                  = AbacusFrameStyle.Toy,
                                    onRodMovementChange         = {},
                                    onShowDirectionHintsChange  = {},
                                    onShowHighlighterChange     = {},
                                    onReset = {}, onNext = {}
                                )
                            }
                        }
                    }

                    // Right: sticky header + content
                    AnzanCard(
                        mode      = mode,
                        phase     = phase,
                        session   = session,
                        typedValue = typedValue,
                        numberVisible = numberVisible,
                        onDigit   = onDigit, onDelete = onDelete, onConfirm = onConfirm,
                        modifier  = Modifier
                            .weight(0.56f).fillMaxHeight()
                            .padding(start = AppDimens.Dimens6, end = AppDimens.Dimens12, top = DeviceInfo.screenTopPadding(), bottom = AppDimens.Dimens12)
                    )
                }
            } else {
                // ── Single panel ──────────────────────────────────────────────────
                Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).screenLeftEdge()) {
                    ScreenTopBar(title = mode.title, onBack = onBackClick)
                    Spacer(Modifier.height(AppDimens.Dimens8))
                    AnzanCard(
                        mode      = mode,
                        phase     = phase,
                        session   = session,
                        typedValue = typedValue,
                        numberVisible = numberVisible,
                        onDigit   = onDigit, onDelete = onDelete, onConfirm = onConfirm,
                        modifier  = Modifier.weight(1f).fillMaxWidth().padding(horizontal = AppDimens.Dimens12)
                    )
                    Spacer(Modifier.height(AppDimens.Dimens12))
                }
            }

            // ── Result popup ──────────────────────────────────────────────────────
            (phase as? L3AnzanPhase.Result)?.let { rp ->
                L3ResultPopup(
                    accent     = mode.endColor,
                    correct    = rp.correct,
                    emoji      = if (rp.correct) "🌟🌟🌟" else "💪",
                    message    = if (rp.correct) "Brilliant mental math! Keep it up! 🧠✨"
                                 else "Great try! Mental math takes practice — you'll get it! 💪",
                    retryLabel = "Try Again 🔄",
                    onRetry    = {
                        abCalc.resetAbacusData()
                        typedValue    = ""
                        numberVisible = true
                        session       = generateL3Session(config.terms, config.digits)
                        phase         = L3AnzanPhase.Countdown(3)
                        restartKey++
                    },
                    onDone     = {
                        if (rp.correct) reviewGate.attempt(prefs) { onFinished() } else onFinished()
                    },
                ) {
                    L3AnswerTitle(rp.correct, rp.expected, rp.userAnswer, correctText = "Correct! ${rp.expected}")
                }
            }

            ReviewGateHost(reviewGate, prefs)
        }
    }
}

/** The mode card of the Anzan screen: sticky header, then the countdown, the flashed numbers and the answer pad. */
@Composable
private fun AnzanCard(
    mode:          L3Mode,
    phase:         L3AnzanPhase,
    session:       L3Session,
    typedValue:    String,
    numberVisible: Boolean,
    onDigit:       (Int) -> Unit,
    onDelete:      () -> Unit,
    onConfirm:     () -> Unit,
    modifier:      Modifier = Modifier,
) {
    L3ModeCard(
        accent   = mode.endColor,
        emoji    = mode.emoji,
        title    = mode.title,
        subtitle = mode.subtitle,
        modifier = modifier,
    ) {
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
            contentAlignment = Alignment.Center,
            label          = "anzan-phase",
            modifier       = Modifier.fillMaxSize()
        ) { p ->
            when (p) {
                is L3AnzanPhase.Countdown -> L3Countdown(p.n, digitSize = 80f)
                is L3AnzanPhase.Flash -> {
                    val term = session.terms[p.idx]
                    L3FlashDisplay(
                        display       = if (term.sign.isEmpty()) "${term.value}" else "${term.sign} ${term.value}",
                        index         = p.idx,
                        total         = session.terms.size,
                        numberVisible = numberVisible,
                    )
                }
                is L3AnzanPhase.Answering -> L3AnswerPhase(
                    title      = "What's the total? 🤔",
                    typedValue = typedValue,
                    onDigit    = onDigit,
                    onDelete   = onDelete,
                    onConfirm  = onConfirm,
                )
                is L3AnzanPhase.Result -> Unit
            }
        }
    }
}
