package com.jigar.me.ui.view.home.screens.levels.level3.flash

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.screens.levels.level3.*
import com.jigar.me.ui.view.home.screens.levels.level3.components.*
import com.jigar.me.ui.view.home.theme.AppDimens

// ─────────────────────────────── Picker Screen ──────────────────────────────

@Composable
fun Level3FlashPickerScreen(
    onBackClick:        () -> Unit,
    onSelectDifficulty: (Int) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()

        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = AppDimens.Dimens12)
        ) {
            Spacer(Modifier.height(AppDimens.Dimens8))
            BackButtonWithText(title = "Flash Challenge", onBackClick = onBackClick)
            Spacer(Modifier.height(AppDimens.Dimens8))

            BoxWithConstraints(
                modifier         = Modifier.weight(1f).fillMaxWidth()
                    .padding(horizontal = AppDimens.Dimens12),
                contentAlignment = Alignment.Center
            ) {
                val spacing = AppDimens.Dimens12
                val cardW   = (maxWidth - spacing * 3) / 4
                val cardH   = maxHeight - AppDimens.Dimens8

                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    l3FlashDifficulties.forEachIndexed { idx, diff ->
                        FlashDiffCard(diff, cardW, cardH) { onSelectDifficulty(idx) }
                    }
                }
            }
            Spacer(Modifier.height(AppDimens.Dimens12))
        }
    }
}

@Composable
private fun FlashDiffCard(diff: L3FlashDifficulty, w: Dp, h: Dp, onClick: () -> Unit) {
    val shape = RoundedCornerShape(AppDimens.Dimens20)
    Box(
        modifier = Modifier
            .size(width = w, height = h)
            .padding(AppDimens.Dimens4)
            .shadow(AppDimens.Dimens8, shape,
                ambientColor = diff.endColor.copy(0.45f),
                spotColor    = diff.endColor.copy(0.45f))
            .background(Brush.linearGradient(listOf(diff.startColor, diff.endColor)), shape)
            .border(AppDimens.Dimens1, Color.White.copy(0.25f), shape)
            .clickable(remember { MutableInteractionSource() }, null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier            = Modifier.fillMaxSize().padding(AppDimens.Dimens12)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)
            ) {
                Text(diff.emoji, style = MaterialTheme.typography.displaySmall.scaled())
                Text(diff.label,
                    style      = MaterialTheme.typography.titleLarge.scaled(),
                    color      = Color.White,
                    fontWeight = FontWeight.Black,
                    textAlign  = TextAlign.Center)
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(0.22f), RoundedCornerShape(AppDimens.Dimens12))
                        .padding(horizontal = AppDimens.Dimens10, vertical = AppDimens.Dimens6)
                ) {
                    Text(diff.desc,
                        style      = MaterialTheme.typography.labelMedium.scaled(),
                        color      = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        textAlign  = TextAlign.Center)
                }
            }
            val tapShape = RoundedCornerShape(AppDimens.Dimens100)
            Box(
                modifier = Modifier
                    .padding(AppDimens.Dimens4)
                    .shadow(AppDimens.Dimens4, tapShape)
                    .background(Color.White, tapShape)
                    .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
            ) {
                Text("Tap to Play!",
                    style      = MaterialTheme.typography.labelLarge.scaled(),
                    color      = diff.startColor,
                    fontWeight = FontWeight.Black)
            }
        }
    }
}

// ─────────────────────────────── Flash Play Screen ───────────────────────────

private sealed class L3FlashPhase {
    data class Countdown(val n: Int)                            : L3FlashPhase()
    data class Flash(val idx: Int)                              : L3FlashPhase()
    object Gap                                                  : L3FlashPhase()
    object Answering                                            : L3FlashPhase()
    data class Result(val correct: Boolean, val expected: Int)  : L3FlashPhase()
}

@Composable
fun Level3FlashPlayScreen(
    diffIndex:   Int,
    onBackClick: () -> Unit,
    onFinished:  () -> Unit,
) {
    val diff    = l3FlashDifficulties[diffIndex.coerceIn(0, l3FlashDifficulties.lastIndex)]
    val session = remember { generateL3Session(diff.terms, diff.digits) }

    var phase      by remember { mutableStateOf<L3FlashPhase>(L3FlashPhase.Countdown(3)) }
    var typedValue by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        for (n in 3 downTo 1) { phase = L3FlashPhase.Countdown(n); delay(700) }
        for ((idx, _) in session.terms.withIndex()) {
            phase = L3FlashPhase.Flash(idx)
            delay(diff.flashMs.toLong())
            if (idx < session.terms.size - 1) { phase = L3FlashPhase.Gap; delay(200) }
        }
        phase = L3FlashPhase.Answering
    }

    val cardShape = remember { RoundedCornerShape(AppDimens.Dimens20) }
    val cardBrush = remember(diff.startColor, diff.endColor) {
        Brush.linearGradient(listOf(diff.startColor.copy(0.85f), diff.endColor.copy(0.85f)))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()

        // ── Single panel ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = AppDimens.Dimens12)
        ) {
            BackButtonWithText(title = "${diff.label} Flash", onBackClick = onBackClick)
            Spacer(Modifier.height(AppDimens.Dimens8))

            Box(
                modifier = Modifier
                    .weight(1f).fillMaxWidth()
                    .padding(horizontal = AppDimens.Dimens12).padding(AppDimens.Dimens4)
                    .shadow(AppDimens.Dimens8, cardShape,
                        spotColor    = diff.endColor.copy(0.38f),
                        ambientColor = diff.endColor.copy(0.20f))
                    .background(cardBrush, cardShape)
            ) {
                FlashContentPanel(phase, session, diff, typedValue,
                    onDigit  = { typedValue = (typedValue + it.toString()).take(3) },
                    onDelete = { if (typedValue.isNotEmpty()) typedValue = typedValue.dropLast(1) },
                    onConfirm = {
                        val typed = typedValue.toIntOrNull() ?: 0
                        phase = L3FlashPhase.Result(typed == session.answer, session.answer)
                    }
                )
            }
            Spacer(Modifier.height(AppDimens.Dimens12))
        }

        // ── Result popup ──────────────────────────────────────────────────────
        val rp = phase as? L3FlashPhase.Result
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
                                spotColor    = diff.endColor.copy(0.50f),
                                ambientColor = diff.endColor.copy(0.30f))
                            .background(Brush.linearGradient(listOf(diff.startColor, diff.endColor)), popupShape)
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
                                if (rp.correct) "Correct! ${rp.expected} 🔥" else "Answer: ${rp.expected}",
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
                                    if (rp.correct) "Incredible! Flash mastery unlocked! 🚀"
                                    else "So close! Keep flashing — you're getting faster! ⚡",
                                    style      = MaterialTheme.typography.bodyLarge.scaled(),
                                    color      = Color.White.copy(0.92f),
                                    textAlign  = TextAlign.Center,
                                    fontWeight = FontWeight.Medium)
                            }
                            Spacer(Modifier.height(AppDimens.Dimens4))
                            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
                                L3ResultBtn("Try Again 🔄", filled = false) {
                                    typedValue = ""
                                    phase      = L3FlashPhase.Countdown(3)
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
private fun FlashContentPanel(
    phase:     L3FlashPhase,
    session:   L3Session,
    diff:      L3FlashDifficulty,
    typedValue: String,
    onDigit:   (Int) -> Unit,
    onDelete:  () -> Unit,
    onConfirm: () -> Unit,
) {
    val isAnswering = phase is L3FlashPhase.Answering
    Column(
        modifier            = Modifier.fillMaxWidth().padding(AppDimens.Dimens16),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
    ) {
        // Difficulty info strip: hidden when numpad is showing
        if (!isAnswering) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10)
            ) {
                Text(diff.emoji, style = MaterialTheme.typography.titleLarge.scaled())
                Column {
                    Text(diff.label,
                        style      = MaterialTheme.typography.labelLarge.scaled(),
                        color      = Color.White,
                        fontWeight = FontWeight.Black)
                    Text(diff.desc,
                        style = MaterialTheme.typography.labelSmall.scaled(),
                        color = Color.White.copy(0.80f))
                }
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(0.25f)))
        }

        // Phase-specific content
        AnimatedContent(
            targetState    = phase,
            contentKey     = { p ->
                when (p) {
                    is L3FlashPhase.Countdown -> "countdown"
                    is L3FlashPhase.Flash     -> "flash"
                    is L3FlashPhase.Gap       -> "gap"
                    is L3FlashPhase.Answering -> "answering"
                    is L3FlashPhase.Result    -> "result"
                }
            },
            transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(120)) },
            label          = "flash-phase",
            modifier       = Modifier.fillMaxWidth()
        ) { p ->
            when (p) {
                is L3FlashPhase.Countdown -> Box(
                    modifier         = Modifier.fillMaxWidth().padding(vertical = AppDimens.Dimens16),
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
                is L3FlashPhase.Flash -> {
                    val term    = session.terms[p.idx]
                    val display = if (term.sign.isEmpty()) "${term.value}" else "${term.sign} ${term.value}"
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(vertical = AppDimens.Dimens12),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
                    ) {
                        Text("${p.idx + 1}  /  ${session.terms.size}",
                            style      = MaterialTheme.typography.labelLarge.scaled(),
                            color      = Color.White.copy(0.70f),
                            fontWeight = FontWeight.Bold,
                            textAlign  = TextAlign.Center)
                        // Fixed height so layout never shifts when number changes
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(AppDimens.Dimens100)
                                .background(Color.White.copy(0.20f), RoundedCornerShape(AppDimens.Dimens16)),
                            contentAlignment = Alignment.Center
                        ) {
                            Crossfade(targetState = display, animationSpec = tween(200), label = "flash-num") { d ->
                                Text(d,
                                    fontSize   = 72.sp.scaled(),
                                    color      = Color.White,
                                    fontWeight = FontWeight.Black,
                                    textAlign  = TextAlign.Center,
                                    modifier   = Modifier.fillMaxWidth())
                            }
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
                                            if (i <= p.idx) Color.White else Color.White.copy(0.30f),
                                            RoundedCornerShape(AppDimens.Dimens100)
                                        )
                                )
                            }
                        }
                    }
                }
                is L3FlashPhase.Gap -> Box(
                    modifier         = Modifier.fillMaxWidth().padding(vertical = AppDimens.Dimens40),
                    contentAlignment = Alignment.Center
                ) {
                    Text("•••",
                        style = MaterialTheme.typography.headlineLarge.scaled(),
                        color = Color.White.copy(0.50f))
                }
                is L3FlashPhase.Answering -> Column(
                    modifier            = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text("What's the total? 🔥",
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
                        modifier   = Modifier.fillMaxWidth(0.72f).height(AppDimens.Dimens200)
                    )
                }
                is L3FlashPhase.Result -> Unit
            }
        }
    }
}
