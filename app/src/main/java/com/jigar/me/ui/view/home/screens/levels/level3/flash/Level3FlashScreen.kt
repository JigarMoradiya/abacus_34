package com.jigar.me.ui.view.home.screens.levels.level3.flash

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.alpha
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
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
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            BackButtonWithText(title = "Flash Challenge", onBackClick = onBackClick)

            Spacer(Modifier.weight(1f))

            Row(
                modifier              = Modifier.fillMaxWidth()
                    .padding(horizontal = AppDimens.Dimens12),
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                l3FlashDifficulties.forEachIndexed { idx, diff ->
                    FlashDiffCard(diff) { onSelectDifficulty(idx) }
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun RowScope.FlashDiffCard(diff: L3FlashDifficulty, onClick: () -> Unit) {
    val shape = RoundedCornerShape(AppDimens.Dimens20)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens10),
        modifier            = Modifier
            .weight(1f)
            .padding(AppDimens.Dimens4)
            .shadow(AppDimens.Dimens8, shape,
                ambientColor = diff.endColor.copy(0.45f),
                spotColor    = diff.endColor.copy(0.45f))
            .background(Brush.linearGradient(listOf(diff.startColor, diff.endColor)), shape)
            .border(AppDimens.Dimens1, Color.White.copy(0.25f), shape)
            .clickable(remember { MutableInteractionSource() }, null) {
                AudioPlayerManager.playSoundBtnClick()
                onClick()
            }
            .padding(AppDimens.Dimens12)
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
                modifier         = Modifier
                    .fillMaxWidth()
                    .height(AppDimens.Dimens45)
                    .background(Color.White.copy(0.22f), RoundedCornerShape(AppDimens.Dimens12))
                    .padding(horizontal = AppDimens.Dimens10, vertical = AppDimens.Dimens6),
                contentAlignment = Alignment.Center
            ) {
                Text(diff.desc,
                    style      = MaterialTheme.typography.labelMedium.scaled(),
                    color      = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    textAlign  = TextAlign.Center,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis)
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

    val cardShape = remember { RoundedCornerShape(AppDimens.Dimens20) }
    val cardBrush = remember(diff.startColor, diff.endColor) {
        Brush.linearGradient(listOf(diff.startColor.copy(0.85f), diff.endColor.copy(0.85f)))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()

        // ── Single panel ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            BackButtonWithText(title = "${diff.label} Flash", onBackClick = onBackClick)

            // Content card
            Column(
                modifier = Modifier
                    .weight(1f).fillMaxWidth()
                    .padding(horizontal = AppDimens.Dimens12).padding(vertical = AppDimens.Dimens4)
                    .shadow(AppDimens.Dimens8, cardShape,
                        spotColor    = diff.endColor.copy(0.38f),
                        ambientColor = diff.endColor.copy(0.20f))
                    .background(cardBrush, cardShape)
            ) {
                // Info header — always sticky at top
                Row(
                    modifier              = Modifier.fillMaxWidth()
                        .padding(horizontal = AppDimens.Dimens16)
                        .padding(top = AppDimens.Dimens12, bottom = AppDimens.Dimens8),
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

                // Divider
                Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(0.25f)))

                // BoxWithConstraints measures exact remaining space — no layout ambiguity
                BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    FlashContentPanel(
                        phase         = phase,
                        session       = session,
                        diff          = diff,
                        typedValue    = typedValue,
                        numberVisible = numberVisible,
                        onDigit       = { typedValue = (typedValue + it.toString()).take(3) },
                        onDelete      = { if (typedValue.isNotEmpty()) typedValue = typedValue.dropLast(1) },
                        onConfirm     = {
                            val typed = typedValue.toIntOrNull() ?: 0
                            phase = L3FlashPhase.Result(typed == session.answer, session.answer, typed)
                        },
                        modifier      = Modifier.width(maxWidth).height(maxHeight)
                    )
                }
            }
            Spacer(Modifier.height(AppDimens.Dimens8))
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
                            if (rp.correct) {
                                Text("Correct! ${rp.expected} 🔥",
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
                                        Text("${rp.userAnswer}",
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
                                        Text("${rp.expected}",
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
                                    typedValue    = ""
                                    numberVisible = true
                                    session       = generateL3Session(diff.terms, diff.digits)
                                    phase         = L3FlashPhase.Countdown(3)
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
private fun FlashContentPanel(
    phase:         L3FlashPhase,
    session:       L3Session,
    diff:          L3FlashDifficulty,
    typedValue:    String,
    numberVisible: Boolean,
    onDigit:       (Int) -> Unit,
    onDelete:      () -> Unit,
    onConfirm:     () -> Unit,
    modifier:      Modifier = Modifier,
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
        label          = "flash-phase",
        modifier       = modifier
    ) { p ->
        when (p) {
            is L3FlashPhase.Countdown -> Box(
                modifier         = Modifier.fillMaxSize(),
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
                val term     = session.terms[p.idx]
                val display  = if (term.sign.isEmpty()) "${term.value}" else "${term.sign} ${term.value}"
                val numAlpha by animateFloatAsState(
                    targetValue   = if (numberVisible) 1f else 0f,
                    animationSpec = tween(150),
                    label         = "flash-num-alpha"
                )
                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
                    ) {
                        Text("${p.idx + 1}  /  ${session.terms.size}",
                            style      = MaterialTheme.typography.labelLarge.scaled(),
                            color      = Color.White.copy(0.70f),
                            fontWeight = FontWeight.Bold,
                            textAlign  = TextAlign.Center)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
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
            }
            is L3FlashPhase.Answering -> Column(
                modifier            = Modifier.fillMaxSize()
                    .padding(horizontal = AppDimens.Dimens16)
                    .padding(vertical = AppDimens.Dimens12),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("What's the total? 🔥",
                    style      = MaterialTheme.typography.titleMedium.scaled(),
                    color      = Color.White,
                    fontWeight = FontWeight.Black,
                    textAlign  = TextAlign.Center)
                Spacer(Modifier.height(AppDimens.Dimens8))
                L3Numpad(
                    typedValue = typedValue,
                    onDigit    = onDigit,
                    onDelete   = onDelete,
                    onConfirm  = onConfirm,
                    modifier   = Modifier.fillMaxWidth().weight(1f)
                )
            }
            is L3FlashPhase.Result -> Unit
        }
    }
}
