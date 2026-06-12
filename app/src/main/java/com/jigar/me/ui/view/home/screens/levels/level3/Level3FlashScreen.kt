package com.jigar.me.ui.view.home.screens.levels.level3

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
import kotlinx.coroutines.delay
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.screens.levels.level3.components.L3Numpad
import com.jigar.me.ui.view.home.theme.AppDimens

// ─────────────────────────────── Picker Screen ──────────────────────────────

@Composable
fun Level3FlashPickerScreen(
    onBackClick:        () -> Unit,
    onSelectDifficulty: (Int) -> Unit,
) {
    val cardShape = RoundedCornerShape(AppDimens.Dimens20)

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()

        Row(modifier = Modifier.fillMaxSize()) {
            // Left: back button + title info
            Column(
                modifier = Modifier
                    .weight(0.28f)
                    .fillMaxHeight()
                    .padding(start = AppDimens.Dimens12, top = AppDimens.Dimens8, bottom = AppDimens.Dimens12, end = AppDimens.Dimens6)
            ) {
                BackButtonWithText(title = "Flash Challenge", onBackClick = onBackClick)
                Spacer(Modifier.height(AppDimens.Dimens8))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppDimens.Dimens4)
                        .shadow(AppDimens.Dimens4, cardShape,
                            spotColor    = Color(0xFF1565C0).copy(0.40f),
                            ambientColor = Color(0xFF1565C0).copy(0.20f))
                        .background(Brush.linearGradient(listOf(Color(0xFF1565C0), Color(0xFF1976D2))), cardShape)
                        .padding(horizontal = AppDimens.Dimens14, vertical = AppDimens.Dimens12)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens4)) {
                        Text("🔥", style = MaterialTheme.typography.headlineMedium.scaled())
                        Text("Flash Challenge",
                            style      = MaterialTheme.typography.labelLarge.scaled(),
                            color      = Color.White,
                            fontWeight = FontWeight.Black)
                        Text("Pick your difficulty\nand test your speed!",
                            style = MaterialTheme.typography.labelSmall.scaled(),
                            color = Color.White.copy(0.80f))
                    }
                }
            }

            // Right: 4 difficulty cards
            BoxWithConstraints(
                modifier         = Modifier.weight(0.72f).fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                val spacing = AppDimens.Dimens12
                val hPad    = AppDimens.Dimens12
                val vPad    = AppDimens.Dimens10
                val cardW   = (maxWidth - hPad * 2 - spacing * 3) / 4
                val cardH   = maxHeight - vPad * 2

                Row(
                    modifier              = Modifier.fillMaxWidth().padding(horizontal = hPad, vertical = vPad),
                    horizontalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    l3FlashDifficulties.forEachIndexed { idx, diff ->
                        FlashDiffCard(diff, cardW, cardH) { onSelectDifficulty(idx) }
                    }
                }
            }
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
    data class Countdown(val n: Int)                                  : L3FlashPhase()
    data class Flash(val idx: Int)                                    : L3FlashPhase()
    object Gap                                                        : L3FlashPhase()
    object Answering                                                  : L3FlashPhase()
    data class Result(val correct: Boolean, val expected: Int)        : L3FlashPhase()
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

    val cardShape = RoundedCornerShape(AppDimens.Dimens20)

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()

        Row(
            modifier          = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ── LEFT: back button + difficulty info ─────────────────────────────
            Column(
                modifier = Modifier
                    .weight(0.30f)
                    .fillMaxHeight()
                    .padding(start = AppDimens.Dimens12, top = AppDimens.Dimens8, bottom = AppDimens.Dimens12, end = AppDimens.Dimens6)
            ) {
                BackButtonWithText(title = "${diff.label} Flash", onBackClick = onBackClick)
                Spacer(Modifier.height(AppDimens.Dimens8))

                // Difficulty info card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppDimens.Dimens4)
                        .shadow(AppDimens.Dimens4, cardShape,
                            spotColor    = diff.endColor.copy(0.40f),
                            ambientColor = diff.endColor.copy(0.20f))
                        .background(Brush.linearGradient(listOf(diff.startColor, diff.endColor)), cardShape)
                        .padding(horizontal = AppDimens.Dimens14, vertical = AppDimens.Dimens14)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens6)) {
                        Text(diff.emoji, style = MaterialTheme.typography.headlineMedium.scaled())
                        Text(diff.label,
                            style      = MaterialTheme.typography.titleMedium.scaled(),
                            color      = Color.White,
                            fontWeight = FontWeight.Black)
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(0.20f), RoundedCornerShape(AppDimens.Dimens8))
                                .padding(horizontal = AppDimens.Dimens8, vertical = AppDimens.Dimens4)
                        ) {
                            Text(diff.desc,
                                style      = MaterialTheme.typography.labelSmall.scaled(),
                                color      = Color.White,
                                fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // ── RIGHT: flash content — wraps height, centered ──────────────────
            Column(
                modifier = Modifier
                    .weight(0.70f)
                    .padding(start = AppDimens.Dimens6, end = AppDimens.Dimens12, top = AppDimens.Dimens8, bottom = AppDimens.Dimens12)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppDimens.Dimens4)
                        .shadow(AppDimens.Dimens8, cardShape,
                            spotColor    = diff.endColor.copy(0.38f),
                            ambientColor = diff.endColor.copy(0.20f))
                        .background(Brush.linearGradient(listOf(diff.startColor.copy(0.85f), diff.endColor.copy(0.85f))), cardShape)
                ) {
                    AnimatedContent(
                        targetState    = phase,
                        transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(120)) },
                        label          = "flash-phase",
                        modifier       = Modifier.fillMaxWidth()
                    ) { p ->
                        when (p) {
                            is L3FlashPhase.Countdown -> Box(
                                modifier         = Modifier.fillMaxWidth().padding(vertical = AppDimens.Dimens40),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${p.n}",
                                    style      = MaterialTheme.typography.displayLarge.scaled(),
                                    color      = Color.White,
                                    fontWeight = FontWeight.Black)
                            }
                            is L3FlashPhase.Flash -> {
                                val term    = session.terms[p.idx]
                                val display = if (term.sign.isEmpty()) "${term.value}" else "${term.sign} ${term.value}"
                                Column(
                                    modifier            = Modifier.fillMaxWidth().padding(AppDimens.Dimens24),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)
                                ) {
                                    Text("${p.idx + 1} / ${session.terms.size}",
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
                                    style  = MaterialTheme.typography.headlineLarge.scaled(),
                                    color  = Color.White.copy(0.50f))
                            }
                            is L3FlashPhase.Answering -> Column(
                                modifier            = Modifier.fillMaxWidth().padding(AppDimens.Dimens20),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Top
                            ) {
                                Text("What's the total? 🔥",
                                    style      = MaterialTheme.typography.titleLarge.scaled(),
                                    color      = Color.White,
                                    fontWeight = FontWeight.Black,
                                    textAlign  = TextAlign.Center)
                                Spacer(Modifier.height(AppDimens.Dimens12))
                                L3Numpad(
                                    typedValue = typedValue,
                                    onDigit    = { typedValue = (typedValue + it.toString()).take(3) },
                                    onDelete   = { if (typedValue.isNotEmpty()) typedValue = typedValue.dropLast(1) },
                                    onConfirm  = {
                                        val typed = typedValue.toIntOrNull() ?: 0
                                        phase = L3FlashPhase.Result(typed == session.answer, session.answer)
                                    },
                                    modifier = Modifier.fillMaxWidth().height(AppDimens.Dimens260)
                                )
                            }
                            is L3FlashPhase.Result -> Unit
                        }
                    }
                }
            }
        }

        // ── Result popup ────────────────────────────────────────────────────────
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
                            .fillMaxWidth(0.52f)
                            .padding(AppDimens.Dimens6)
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
