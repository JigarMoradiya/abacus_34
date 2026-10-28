package com.jigar.me.ui.view.home.screens.levels.level3.guided

import com.jigar.me.ui.view.base.abacus_base.AbacusTheme
import com.jigar.me.ui.view.home.screens.levels.level3.*
import com.jigar.me.ui.view.home.screens.levels.level3.components.*
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FormatListNumbered
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbCountPill
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbPanelButton
import com.jigar.me.ui.view.ab.AbProgressBar
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.AbacusFrameStyle
import com.jigar.me.ui.view.base.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.sheets.ReviewGateHost
import com.jigar.me.ui.view.home.common_ui.sheets.rememberReviewGateController
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants

private sealed class L3GuidedPhase {
    data class Step(val index: Int) : L3GuidedPhase()
    object Answering : L3GuidedPhase()
    data class Result(val correct: Boolean, val expected: Int, val userAnswer: Int) : L3GuidedPhase()
}

@Composable
fun Level3GuidedScreen(
    config:      L3Config,
    onBackClick: () -> Unit,
    onFinished:  () -> Unit,
) {
    val session = remember { generateL3Session(config.terms, config.digits) }
    val mode    = config.mode

    var phase      by remember { mutableStateOf<L3GuidedPhase>(L3GuidedPhase.Step(0)) }
    var typedValue by remember { mutableStateOf("") }

    val currentStep = (phase as? L3GuidedPhase.Step)?.index ?: 0
    val runningAt   = { idx: Int ->
        session.terms.take(idx + 1).fold(0) { acc, t ->
            if (t.sign == "−") acc - t.value else acc + t.value
        }
    }

    val abCalc = remember { AbacusCalculations(2) }
    val prefs  = LocalPreferencesHelper.current
    val reviewGate = rememberReviewGateController()
    val theme  = AbacusTheme.active(prefs)

    LaunchedEffect(currentStep, config.autoAbacus) {
        if (config.autoAbacus && phase is L3GuidedPhase.Step) {
            delay(400)
            abCalc.setAbacusValueFromString(runningAt(currentStep).toString())
        }
    }

    AbTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Lagoon)

            Row(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).screenLeftEdge()) {

                // ── LEFT: back button + abacus ────────────────────────────────────
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
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            when (phase) {
                                is L3GuidedPhase.Step -> AbacusWithDecimalCanvas(
                                    selectedTheme               = theme,
                                    screenType                  = if (!config.autoAbacus)
                                        AppConstants.AbacusScreen.screenTypeLevel3
                                    else
                                        AppConstants.AbacusScreen.screenTypeLevel2Practice,
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
                                    onReset = {}, onNext = {},
                                )
                                is L3GuidedPhase.Answering -> Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12, Alignment.CenterVertically),
                                    modifier = Modifier.fillMaxSize().padding(AppDimens.Dimens16)
                                ) {
                                    Text("🧮", style = abEmojiStyle(abResponsive(40f)))
                                    Text(
                                        "All ${session.terms.size} steps done!\nEnter your total →",
                                        style      = abFont(15f, FontWeight.Black),
                                        color      = mode.startColor,
                                        textAlign  = TextAlign.Center
                                    )
                                }
                                is L3GuidedPhase.Result -> AbacusWithDecimalCanvas(
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
                                    onReset = {}, onNext = {},
                                )
                            }
                        }
                    }
                }

                // ── RIGHT: card with sticky header + content ──────────────────────
                L3ModeCard(
                    accent   = mode.endColor,
                    emoji    = mode.emoji,
                    title    = mode.title,
                    subtitle = mode.subtitle,
                    modifier = Modifier
                        .weight(0.56f).fillMaxHeight()
                        .padding(start = AppDimens.Dimens6, end = AppDimens.Dimens12, top = AppDimens.Dimens8, bottom = AppDimens.Dimens12)
                ) {
                    AnimatedContent(
                        targetState    = phase,
                        contentKey     = { p ->
                            when (p) {
                                is L3GuidedPhase.Step      -> "step"
                                is L3GuidedPhase.Answering -> "answering"
                                is L3GuidedPhase.Result    -> "result"
                            }
                        },
                        transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(200)) },
                        contentAlignment = Alignment.Center,
                        label          = "guided-phase",
                        modifier       = Modifier.fillMaxSize()
                    ) { p ->
                        when (p) {
                            is L3GuidedPhase.Step -> GuidedStepPanel(
                                session    = session,
                                stepIndex  = p.index,
                                autoAbacus = config.autoAbacus,
                                mode       = mode,
                                onNext     = {
                                    val nextIdx = p.index + 1
                                    if (nextIdx < session.terms.size) {
                                        phase = L3GuidedPhase.Step(nextIdx)
                                    } else {
                                        if (config.autoAbacus) {
                                            phase = L3GuidedPhase.Result(true, session.answer, session.answer)
                                        } else {
                                            abCalc.resetAbacusData()
                                            phase = L3GuidedPhase.Answering
                                        }
                                    }
                                }
                            )
                            is L3GuidedPhase.Answering -> L3AnswerPhase(
                                title      = "What's your total? 🤔",
                                typedValue = typedValue,
                                onDigit    = { typedValue = (typedValue + it.toString()).take(3) },
                                onDelete   = { if (typedValue.isNotEmpty()) typedValue = typedValue.dropLast(1) },
                                onConfirm  = {
                                    val typed = typedValue.toIntOrNull() ?: 0
                                    phase = L3GuidedPhase.Result(typed == session.answer, session.answer, typed)
                                },
                            )
                            is L3GuidedPhase.Result -> GuidedStepPanel(
                                session    = session,
                                stepIndex  = session.terms.size - 1,
                                autoAbacus = config.autoAbacus,
                                mode       = mode,
                                onNext     = {}
                            )
                        }
                    }
                }
            }

            // ── Result popup ──────────────────────────────────────────────────────
            (phase as? L3GuidedPhase.Result)?.let { rp ->
                L3ResultPopup(
                    accent     = mode.endColor,
                    correct    = rp.correct,
                    emoji      = if (rp.correct) "🌟🌟🌟" else "💪",
                    message    = if (rp.correct) "Amazing work! Your abacus skills are growing! 🚀"
                                 else "Keep practicing! You're getting better every time! 💫",
                    retryLabel = "Try Again 🔄",
                    onRetry    = {
                        phase      = L3GuidedPhase.Step(0)
                        typedValue = ""
                        abCalc.resetAbacusData()
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

@Composable
private fun GuidedStepPanel(
    session:    L3Session,
    stepIndex:  Int,
    autoAbacus: Boolean,
    mode:       L3Mode,
    onNext:     () -> Unit,
) {
    val term      = session.terms[stepIndex]
    val opDisplay = if (term.sign.isEmpty()) "${term.value}" else "${term.sign} ${term.value}"
    val isLast    = stepIndex == session.terms.size - 1

    val instruction = when {
        autoAbacus && term.sign.isEmpty() -> "Watch the abacus start at ${term.value}!"
        autoAbacus                        -> "See how the abacus updates!"
        term.sign.isEmpty()               -> "Set your abacus to ${term.value}"
        term.sign == "+"                  -> "Add ${term.value} on your abacus"
        else                              -> "Subtract ${term.value} from your abacus"
    }

    val btnLabel = when {
        isLast && autoAbacus -> "See Result! 🎉"
        isLast               -> "Enter My Answer! →"
        else                 -> "Next Step →"
    }

    // on a phone the content is laid out at its natural height and shrunk to the card, so no row is squeezed away
    FitOnPhone(Modifier.fillMaxSize()) { flexible ->
    Column(
        modifier            = if (flexible) Modifier.fillMaxSize() else Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (flexible) Arrangement.spacedBy(AppDimens.Dimens12, Alignment.CenterVertically)
                              else Arrangement.spacedBy(AppDimens.Dimens12)
    ) {
        AbCountPill(
            text = "Step ${stepIndex + 1} of ${session.terms.size}",
            icon = Icons.Rounded.FormatListNumbered,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.20f), RoundedCornerShape(AppDimens.Dimens16))
                .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens14),
            contentAlignment = Alignment.Center
        ) {
            Text(
                opDisplay,
                style     = abFont(60f, FontWeight.Black),
                color     = Color.White,
                textAlign = TextAlign.Center
            )
        }
        Text(
            instruction,
            style     = abFont(15f, FontWeight.Medium),
            color     = Color.White.copy(alpha = 0.90f),
            textAlign = TextAlign.Center,
            modifier  = Modifier.fillMaxWidth()
        )
        AbProgressBar(
            filled   = stepIndex + 1,
            total    = session.terms.size,
            modifier = Modifier.widthIn(max = abResponsive(230f).dp).fillMaxWidth(),
            onColor  = true,
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            AbPanelButton(label = btnLabel, accent = mode.endColor, filled = true, onClick = onNext)
        }
    }
    }
}
