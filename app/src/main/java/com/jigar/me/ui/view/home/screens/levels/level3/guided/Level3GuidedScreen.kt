package com.jigar.me.ui.view.home.screens.levels.level3.guided

import com.jigar.me.ui.view.home.screens.levels.level3.*
import com.jigar.me.ui.view.home.screens.levels.level3.components.*
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.base.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
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
    val theme  = prefs.getCustomParam(AppConstants.Settings.Theam, AppConstants.Settings.theam_Default)

    LaunchedEffect(currentStep, config.autoAbacus) {
        if (config.autoAbacus && phase is L3GuidedPhase.Step) {
            delay(400)
            abCalc.setAbacusValueFromString(runningAt(currentStep).toString())
        }
    }

    val cardShape = remember { RoundedCornerShape(AppDimens.Dimens20) }
    val cardBrush = remember(mode.startColor, mode.endColor) {
        Brush.linearGradient(listOf(mode.startColor.copy(0.82f), mode.endColor.copy(0.82f)))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()

        Row(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {

            // ── LEFT: back button + abacus ────────────────────────────────────
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
                            Text("🧮", style = MaterialTheme.typography.displaySmall.scaled())
                            Text(
                                "All ${session.terms.size} steps done!\nEnter your total →",
                                style      = MaterialTheme.typography.titleMedium.scaled(),
                                color      = mode.startColor,
                                fontWeight = FontWeight.Black,
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
                            onRodMovementChange         = {},
                            onShowDirectionHintsChange  = {},
                            onShowHighlighterChange     = {},
                            onReset = {}, onNext = {},
                        )
                    }
                }
            }

            // ── RIGHT: sticky header + content (full height) ──────────────────
            Column(
                modifier = Modifier
                    .weight(0.56f).fillMaxHeight()
                    .padding(start = AppDimens.Dimens6, end = AppDimens.Dimens12, top = AppDimens.Dimens8, bottom = AppDimens.Dimens12)
                    .shadow(AppDimens.Dimens8, cardShape,
                        spotColor    = mode.endColor.copy(0.38f),
                        ambientColor = mode.endColor.copy(0.20f))
                    .background(cardBrush, cardShape)
            ) {
                // Sticky header
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppDimens.Dimens16)
                        .padding(top = AppDimens.Dimens12, bottom = AppDimens.Dimens8),
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

                BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
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
                        label          = "guided-phase",
                        modifier       = Modifier.width(maxWidth).height(maxHeight)
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
                            is L3GuidedPhase.Answering -> Column(
                                modifier            = Modifier.fillMaxSize()
                                    .padding(horizontal = AppDimens.Dimens16)
                                    .padding(vertical = AppDimens.Dimens12),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "What's your total? 🤔",
                                    style      = MaterialTheme.typography.titleLarge.scaled(),
                                    color      = Color.White,
                                    fontWeight = FontWeight.Black,
                                    textAlign  = TextAlign.Center
                                )
                                Spacer(Modifier.height(AppDimens.Dimens8))
                                L3Numpad(
                                    typedValue = typedValue,
                                    onDigit    = { typedValue = (typedValue + it.toString()).take(3) },
                                    onDelete   = { if (typedValue.isNotEmpty()) typedValue = typedValue.dropLast(1) },
                                    onConfirm  = {
                                        val typed = typedValue.toIntOrNull() ?: 0
                                        phase = L3GuidedPhase.Result(typed == session.answer, session.answer, typed)
                                    },
                                    modifier   = Modifier.fillMaxWidth().weight(1f)
                                )
                            }
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
        }

        // ── Result popup ────────────────────────────────────────────────────────
        val resultPhase = phase as? L3GuidedPhase.Result
        AnimatedVisibility(
            visible  = resultPhase != null,
            enter    = fadeIn(tween(300)) + scaleIn(initialScale = 0.85f, animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f)),
            exit     = fadeOut(tween(200)) + scaleOut(targetScale = 0.85f),
            modifier = Modifier.fillMaxSize()
        ) {
            if (resultPhase != null) {
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
                            Text(if (resultPhase.correct) "🌟🌟🌟" else "💪",
                                style = MaterialTheme.typography.displaySmall.scaled())
                            if (resultPhase.correct) {
                                Text("Correct! ${resultPhase.expected}",
                                    style      = MaterialTheme.typography.headlineMedium.scaled(),
                                    color      = Color.White,
                                    fontWeight = FontWeight.Black,
                                    textAlign  = TextAlign.Center)
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
                                    verticalAlignment     = Alignment.CenterVertically
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Your Answer",
                                            style = MaterialTheme.typography.labelSmall.scaled(),
                                            color = Color(0xFFFF8A80).copy(0.80f))
                                        Text("${resultPhase.userAnswer}",
                                            style      = MaterialTheme.typography.displaySmall.scaled(),
                                            color      = Color(0xFFFF8A80),
                                            fontWeight = FontWeight.Black)
                                    }
                                    Text("→",
                                        style = MaterialTheme.typography.titleLarge.scaled(),
                                        color = Color.White.copy(0.50f))
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Correct Answer",
                                            style = MaterialTheme.typography.labelSmall.scaled(),
                                            color = Color.White.copy(0.70f))
                                        Text("${resultPhase.expected}",
                                            style      = MaterialTheme.typography.displaySmall.scaled(),
                                            color      = Color.White,
                                            fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(0.18f), RoundedCornerShape(AppDimens.Dimens16))
                                    .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens10)
                            ) {
                                Text(
                                    if (resultPhase.correct) "Amazing work! Your abacus skills are growing! 🚀"
                                    else "Keep practicing! You're getting better every time! 💫",
                                    style      = MaterialTheme.typography.bodyLarge.scaled(),
                                    color      = Color.White.copy(0.92f),
                                    textAlign  = TextAlign.Center,
                                    fontWeight = FontWeight.Medium)
                            }
                            Spacer(Modifier.height(AppDimens.Dimens4))
                            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
                                L3ResultBtn("Try Again 🔄", filled = false) {
                                    phase      = L3GuidedPhase.Step(0)
                                    typedValue = ""
                                    abCalc.resetAbacusData()
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

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(AppDimens.Dimens16),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
        ) {
            Text(
                "Step ${stepIndex + 1}  /  ${session.terms.size}",
                style      = MaterialTheme.typography.labelLarge.scaled(),
                color      = Color.White.copy(0.80f),
                fontWeight = FontWeight.Bold,
                textAlign  = TextAlign.Center,
                modifier   = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(0.20f), RoundedCornerShape(AppDimens.Dimens16))
                    .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens16),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    opDisplay,
                    style      = MaterialTheme.typography.displayMedium.scaled(),
                    color      = Color.White,
                    fontWeight = FontWeight.Black,
                    textAlign  = TextAlign.Center
                )
            }
            Text(
                instruction,
                style      = MaterialTheme.typography.bodyLarge.scaled(),
                color      = Color.White.copy(0.90f),
                fontWeight = FontWeight.Medium,
                textAlign  = TextAlign.Center,
                modifier   = Modifier.fillMaxWidth()
            )
            Row(
                modifier              = Modifier.fillMaxWidth(0.60f).height(AppDimens.Dimens8),
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4)
            ) {
                repeat(session.terms.size) { i ->
                    Box(
                        modifier = Modifier
                            .weight(1f).fillMaxHeight()
                            .background(
                                if (i <= stepIndex) Color.White else Color.White.copy(0.30f),
                                RoundedCornerShape(AppDimens.Dimens100)
                            )
                    )
                }
            }
            val btnShape = RoundedCornerShape(AppDimens.Dimens100)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Box(
                    modifier = Modifier
                        .padding(AppDimens.Dimens3)
                        .shadow(AppDimens.Dimens4, btnShape)
                        .background(Color.White, btnShape)
                        .clickable(remember { MutableInteractionSource() }, null) { onNext() }
                        .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens10),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        btnLabel,
                        style      = MaterialTheme.typography.labelMedium.scaled(),
                        color      = mode.startColor,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
