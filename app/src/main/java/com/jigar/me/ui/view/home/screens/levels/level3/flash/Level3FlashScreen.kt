package com.jigar.me.ui.view.home.screens.levels.level3.flash

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbLevelCard
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.sheets.ReviewGateHost
import com.jigar.me.ui.view.home.common_ui.sheets.rememberReviewGateController
import com.jigar.me.ui.view.home.screens.levels.level3.*
import com.jigar.me.ui.view.home.screens.levels.level3.components.*
import com.jigar.me.ui.view.home.theme.AppDimens

// ─────────────────────────────── Picker Screen ──────────────────────────────

@Composable
fun Level3FlashPickerScreen(
    onBackClick:        () -> Unit,
    onSelectDifficulty: (Int) -> Unit,
) {
    AbTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Lagoon)

            Column(
                modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).screenLeftEdge()
            ) {
                ScreenTopBar(title = "Flash Challenge", onBack = onBackClick)

                BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    val spacing = AppDimens.Dimens12
                    val count   = l3FlashDifficulties.size
                    val cardW   = (maxWidth - spacing * (count + 1)) / count
                    val cardH   = minOf(maxHeight * 0.86f, abResponsive(400f).dp)

                    Row(
                        modifier              = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        l3FlashDifficulties.forEachIndexed { idx, diff ->
                            AbLevelCard(
                                emoji     = diff.emoji,
                                title     = diff.label,
                                label     = diff.label,
                                accent    = diff.endColor,
                                width     = cardW,
                                height    = cardH,
                                showStars = false,
                                subtitle  = diff.desc,
                                cta       = "Tap to Play!",
                                onClick   = { onSelectDifficulty(idx) },
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────── Flash Play Screen ───────────────────────────

private sealed class L3FlashPhase {
    data class Countdown(val n: Int)                            : L3FlashPhase()
    data class Flash(val idx: Int)                              : L3FlashPhase()
    object Answering                                            : L3FlashPhase()
    data class Result(val correct: Boolean, val expected: Int, val userAnswer: Int)  : L3FlashPhase()
}

@Composable
fun Level3FlashPlayScreen(
    diffIndex:   Int,
    onBackClick: () -> Unit,
    onFinished:  () -> Unit,
) {
    val diff    = l3FlashDifficulties[diffIndex.coerceIn(0, l3FlashDifficulties.lastIndex)]
    var session by remember { mutableStateOf(generateL3Session(diff.terms, diff.digits)) }

    var phase         by remember { mutableStateOf<L3FlashPhase>(L3FlashPhase.Countdown(3)) }
    var typedValue    by remember { mutableStateOf("") }
    var numberVisible by remember { mutableStateOf(true) }
    var restartKey    by remember { mutableIntStateOf(0) }

    LaunchedEffect(restartKey) {
        for (n in 3 downTo 1) { phase = L3FlashPhase.Countdown(n); delay(700) }
        AudioPlayerManager.playSoundCountGo()
        for ((idx, _) in session.terms.withIndex()) {
            numberVisible = true
            phase = L3FlashPhase.Flash(idx)
            delay(diff.flashMs.toLong())
            if (idx < session.terms.size - 1) {
                numberVisible = false
                delay(250)
            }
        }
        phase = L3FlashPhase.Answering
    }

    val prefs = LocalPreferencesHelper.current
    val reviewGate = rememberReviewGateController()

    AbTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Lagoon)

            // ── Single panel ──────────────────────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).screenLeftEdge()
            ) {
                ScreenTopBar(title = "${diff.label} Flash", onBack = onBackClick)
                Spacer(Modifier.height(AppDimens.Dimens8))

                // Content card — fills the room below the header
                L3ModeCard(
                    accent   = diff.endColor,
                    emoji    = diff.emoji,
                    title    = diff.label,
                    subtitle = diff.desc,
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = AppDimens.Dimens12)
                ) {
                    AnimatedContent(
                        targetState    = phase,
                        contentKey     = { p ->
                            when (p) {
                                is L3FlashPhase.Countdown -> "countdown"
                                is L3FlashPhase.Flash     -> "flash"
                                is L3FlashPhase.Answering -> "answering"
                                is L3FlashPhase.Result    -> "result"
                            }
                        },
                        transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(120)) },
                        contentAlignment = Alignment.Center,
                        label          = "flash-phase",
                        modifier       = Modifier.fillMaxSize()
                    ) { p ->
                        when (p) {
                            is L3FlashPhase.Countdown -> L3Countdown(p.n, digitSize = 90f)
                            is L3FlashPhase.Flash -> {
                                val term = session.terms[p.idx]
                                L3FlashDisplay(
                                    display       = if (term.sign.isEmpty()) "${term.value}" else "${term.sign} ${term.value}",
                                    index         = p.idx,
                                    total         = session.terms.size,
                                    numberVisible = numberVisible,
                                )
                            }
                            is L3FlashPhase.Answering -> L3AnswerPhase(
                                title      = "What's the total? 🔥",
                                typedValue = typedValue,
                                onDigit    = { typedValue = (typedValue + it.toString()).take(3) },
                                onDelete   = { if (typedValue.isNotEmpty()) typedValue = typedValue.dropLast(1) },
                                onConfirm  = {
                                    val typed = typedValue.toIntOrNull() ?: 0
                                    phase = L3FlashPhase.Result(typed == session.answer, session.answer, typed)
                                },
                            )
                            is L3FlashPhase.Result -> Unit
                        }
                    }
                }
                Spacer(Modifier.height(AppDimens.Dimens12))
            }

            // ── Result popup ──────────────────────────────────────────────────────
            (phase as? L3FlashPhase.Result)?.let { rp ->
                L3ResultPopup(
                    accent     = diff.endColor,
                    correct    = rp.correct,
                    emoji      = if (rp.correct) "🌟🌟🌟" else "💪",
                    message    = if (rp.correct) "Incredible! Flash mastery unlocked! 🚀"
                                 else "So close! Keep flashing — you're getting faster! ⚡",
                    retryLabel = "Try Again 🔄",
                    onRetry    = {
                        typedValue    = ""
                        numberVisible = true
                        session       = generateL3Session(diff.terms, diff.digits)
                        phase         = L3FlashPhase.Countdown(3)
                        restartKey++
                    },
                    onDone     = {
                        if (rp.correct) reviewGate.attempt(prefs) { onFinished() } else onFinished()
                    },
                ) {
                    L3AnswerTitle(rp.correct, rp.expected, rp.userAnswer, correctText = "Correct! ${rp.expected} 🔥")
                }
            }

            ReviewGateHost(reviewGate, prefs)
        }
    }
}
