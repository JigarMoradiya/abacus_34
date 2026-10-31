package com.jigar.me.ui.view.home.screens.math_game_zone.number_detective

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameCharacter

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.screens.math_game_zone.GameCategoryType
import com.jigar.me.ui.view.home.screens.math_game_zone.common.AbDebugFlags
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameMascotPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameResultCard
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameScoreboardPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameStatChip
import com.jigar.me.ui.view.home.screens.math_game_zone.common.NextDifficultyBanner
import com.jigar.me.ui.view.home.screens.math_game_zone.common.PauseTimerInBackground
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBounce
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBreathe
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abPopIn
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.common.randomGameCheer
import com.jigar.me.ui.view.home.screens.math_game_zone.common.toyColor
import com.jigar.me.ui.view.home.screens.math_game_zone.gameCardStyle
import com.jigar.me.ui.view.home.screens.math_game_zone.number_detective.components.NumberDetectiveUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.number_detective.viewmodel.NumberDetectivePlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun NumberDetectivePlayScreen(
    viewModel: NumberDetectivePlayViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val baseCheer = remember { randomGameCheer() }
    val s = gameScale()
    val colors = LocalAbColors.current
    val time = rememberNumberDetectiveTime()
    LaunchedEffect(Unit) { viewModel.start() }
    PauseTimerInBackground { viewModel.setTimerPaused(it) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BackButtonWithText(title = stringResource(R.string.number_detective_game), onBackClick = onBackClick)
                    Spacer(Modifier.weight(1f))
                }
                // The range of the secret number, on a pill with a blue rim.
                Text(
                    text = stringResource(R.string.number_detective_prompt, state.question?.minRange ?: 1, state.question?.maxRange ?: 20),
                    style = AbTypography.fixed(22f * s, FontWeight.Black),
                    color = if (colors.isDark) colors.brandPrimary else colors.brandPrimary.abReadable(4.5f),
                    modifier = Modifier.align(Alignment.Center)
                        .abGamePill(outline = colors.brandPrimary)
                        .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens8)
                )
                // The clock: the pill turns red and the number pops while the last ten seconds run.
                val low = state.timeLeft <= 10
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6),
                    modifier = Modifier.align(Alignment.CenterEnd)
                        .padding(end = AppDimens.Dimens16)
                        .abGamePill(outline = if (low) colors.danger else null)
                        .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
                ) {
                    Text("⏱️", fontSize = (24f * s).sp)
                    Text(
                        "${state.timeLeft}",
                        style = AbTypography.fixed(26f * s, FontWeight.Black),
                        color = if (low) numberDetectiveInk(colors.danger, colors.isDark) else colors.textPrimary,
                        modifier = Modifier.abBounce(low, 1.2f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GameMascotPanel(
                    cheer = when {
                        state.lastAnswerRight -> "You cracked it! 🕵️"
                        state.lastAnswerWrong -> "Missed it — new number! 🔍"
                        state.feedbackDirection > 0 -> "Higher! ⬆️"
                        state.feedbackDirection < 0 -> "Lower! ⬇️"
                        else -> baseCheer
                    },
                    celebrate = state.lastAnswerRight, s = s,
                    modifier = Modifier.weight(1f).fillMaxHeight(), character = GameCharacter.Dog)

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    NumberDetectiveTriesTrail(state.maxTries, state.triesUsed, s, time)
                    Spacer(Modifier.height((14f * s).dp))
                    GuessDisplay(state, s, time)
                    Spacer(Modifier.height((18f * s).dp))
                    Keypad(s, state.enteredDigits.isNotEmpty(),
                        onDigit = { viewModel.tapDigit(it) },
                        onBackspace = { viewModel.backspace() },
                        onSubmit = { viewModel.submitGuess() })
                }

                GameScoreboardPanel(
                    score = state.score, multiplier = state.multiplier, best = viewModel.bestScore, s = s,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }
        }

        if (state.isGameOver) {
            ResultOverlay(state = state, best = viewModel.bestScore,
                onPlayAgain = { viewModel.start() }, onBack = onBackClick)
            state.suggestedDifficulty?.let { NextDifficultyBanner(it) }
        }
        // Debug builds only: lets the lead render the real result overlay without playing a round.
        if (AbDebugFlags.showResultEnabled && !state.isGameOver) {
            ResultOverlay(state = state, best = viewModel.bestScore,
                onPlayAgain = { viewModel.start() }, onBack = onBackClick)
        }
    }
}

// ── Shared bits ───────────────────────────────────────────────────────────────────────────────────────────────────────

/** Seconds since the screen showed, ticking every frame. Read it inside `graphicsLayer {}` / draw blocks so only the layer redraws. */
@Composable
internal fun rememberNumberDetectiveTime(): State<Float> {
    val time = remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) { withFrameNanos { now -> time.value = (now - start) / 1_000_000_000f } }
    }
    return time
}

/** Ink for text on a light pill: darkened until readable, but left bright in dark mode. */
private fun numberDetectiveInk(base: Color, isDark: Boolean): Color = if (isDark) base else base.abReadable(4.5f)

private fun numberDetectiveStarPath(cx: Float, cy: Float, outer: Float): Path {
    val path = Path()
    val inner = outer * 0.45f
    for (i in 0 until 10) {
        val r = if (i % 2 == 0) outer else inner
        val a = -PI / 2.0 + i * PI / 5.0
        val x = cx + (cos(a) * r).toFloat()
        val y = cy + (sin(a) * r).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

/** A chunky white arrow drawn with round caps: up for "too low, go higher", down for the other way. */
@Composable
internal fun NumberDetectiveArrow(up: Boolean, size: Dp, color: Color = Color.White) {
    Canvas(Modifier.size(size)) {
        val w = this.size.minDimension
        val stroke = w * 0.2f
        val cx = w / 2f
        val dir = if (up) 1f else -1f
        val tip = if (up) w * 0.1f else w * 0.9f
        val tail = if (up) w * 0.9f else w * 0.1f
        val head = w * 0.34f
        drawLine(color, Offset(cx, tail), Offset(cx, tip), stroke, StrokeCap.Round)
        drawLine(color, Offset(cx, tip), Offset(cx - head, tip + dir * head), stroke, StrokeCap.Round)
        drawLine(color, Offset(cx, tip), Offset(cx + head, tip + dir * head), stroke, StrokeCap.Round)
    }
}

/** The detective's tries as shiny coins: the ones left bob in a wave, a spent one goes dull and pops. */
@Composable
internal fun NumberDetectiveTriesTrail(maxTries: Int, triesUsed: Int, s: Float, time: State<Float>) {
    Row(
        modifier = Modifier.height((20f * s).dp),
        horizontalArrangement = Arrangement.spacedBy((6f * s).dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until maxTries) {
            NumberDetectiveCoin(index = i, used = i < triesUsed, s = s, time = time)
        }
    }
}

@Composable
private fun NumberDetectiveCoin(index: Int, used: Boolean, s: Float, time: State<Float>) {
    val colors = LocalAbColors.current
    val face = if (used) colors.outline else colors.accentYellow
    Canvas(
        Modifier
            .size((17f * s).dp)
            .graphicsLayer { translationY = if (used) 0f else sin(time.value * 3f + index * 0.7f) * 2f * s * density }
            .abBounce(used, 1.4f)
            .graphicsLayer { scaleX = if (used) 0.78f else 1f; scaleY = if (used) 0.78f else 1f }
    ) {
        val r = size.minDimension / 2f
        val center = Offset(size.width / 2f, size.height / 2f)
        drawCircle(face.abDarkened(0.28f), r, Offset(center.x, center.y + 1.5f * s * density))
        drawCircle(face, r, center)
        val ring = 1.2f * density
        drawCircle(Color.White.copy(alpha = 0.6f), r - ring / 2f, center, style = Stroke(ring))
        if (!used) {
            drawPath(numberDetectiveStarPath(center.x, center.y, 5.2f * s * density), Color.White.copy(alpha = 0.95f))
        }
    }
}

// ── The secret number ─────────────────────────────────────────────────────────────────────────────────────────────────

// The secret-number panel: a candy tile with a breathing "?" that bobs under a wiggling magnifier. A guess shows a
// bobbing hint arrow, a wrong guess wobbles it, and a cracked case bursts into stars. Under it, the hint pill.
@Composable
private fun GuessDisplay(state: NumberDetectiveUiState, s: Float, time: State<Float>) {
    val colors = LocalAbColors.current
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy((8f * s).dp)) {
        NumberDetectiveSecretBox(
            digits = state.enteredDigits,
            direction = state.feedbackDirection,
            isRight = state.lastAnswerRight,
            isWrong = state.lastAnswerWrong,
            triesUsed = state.triesUsed,
            s = s,
            time = time
        )

        val hint = when {
            state.lastAnswerRight -> "🎉 " + stringResource(R.string.number_detective_correct)
            state.lastAnswerWrong -> stringResource(R.string.number_detective_answer_was, state.question?.secret ?: 0)
            state.feedbackDirection > 0 -> "⬆️ " + stringResource(R.string.number_detective_too_low)
            state.feedbackDirection < 0 -> "⬇️ " + stringResource(R.string.number_detective_too_high)
            else -> stringResource(R.string.number_detective_tries_left, state.triesLeft)
        }
        val hintColor = when {
            state.lastAnswerRight -> colors.accentGreenInk
            state.lastAnswerWrong -> numberDetectiveInk(colors.danger, colors.isDark)
            state.feedbackDirection > 0 -> numberDetectiveInk(colors.accentOrange, colors.isDark)
            state.feedbackDirection < 0 -> numberDetectiveInk(colors.brandPrimary, colors.isDark)
            else -> colors.textSecondary
        }
        Text(
            hint,
            style = AbTypography.fixed(15f * s, FontWeight.ExtraBold),
            color = hintColor,
            modifier = Modifier
                .abBounce(hint, 1.12f)
                .abGamePill(outline = hintColor)
                .padding(horizontal = (10f * s).dp, vertical = (1f * s).dp)
        )
    }
}

@Composable
private fun NumberDetectiveSecretBox(
    digits: String,
    direction: Int,
    isRight: Boolean,
    isWrong: Boolean,
    triesUsed: Int,
    s: Float,
    time: State<Float>
) {
    val colors = LocalAbColors.current
    val face = when {
        isRight -> colors.accentGreen.abReadable(3f)
        isWrong -> colors.danger.abReadable(3f)
        else -> gameCardStyle(GameCategoryType.NUMBER_DETECTIVE).light.abReadable(3f)
    }

    // Every wrong guess shakes the box (one step 0 -> 1 is two quick wobbles); a cracked case bursts into stars.
    val shakes = remember { Animatable(0f) }
    LaunchedEffect(triesUsed) {
        if (direction != 0 || isWrong) {
            shakes.snapTo(0f)
            shakes.animateTo(1f, tween(500, easing = LinearEasing))
        }
    }
    val burst = remember { Animatable(0f) }
    LaunchedEffect(isRight) {
        if (isRight) burst.animateTo(1f, tween(900, easing = LinearOutSlowInEasing)) else burst.snapTo(0f)
    }
    val burstA = colors.accentYellow
    val burstB = colors.accentPink

    Box(
        Modifier.graphicsLayer {
            translationX = 8f * s * density * sin(shakes.value * PI.toFloat() * 4f)
            translationY = sin(time.value * 2f) * 2.5f * s * density
        }
    ) {
        Box(
            Modifier.abToyTile(face, (16f * s).dp, 4.dp).size((160f * s).dp, (56f * s).dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                digits.ifEmpty { "?" },
                style = AbTypography.fixed(32f * s, FontWeight.Black).copy(shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f)),
                color = Color.White,
                modifier = Modifier
                    .graphicsLayer {
                        val breathe = if (digits.isEmpty()) 1f + 0.07f * sin(time.value * 3f) else 1f
                        scaleX = breathe
                        scaleY = breathe
                    }
                    .abBounce(digits, 1.25f)
            )
        }

        // The wiggling magnifier hovering at the right edge.
        Text(
            "🔍",
            fontSize = (34f * s).sp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .graphicsLayer {
                    val t = time.value
                    rotationZ = sin(t * 1.7f) * 12f
                    translationX = (30f * s + sin(t * 1.3f) * 4f) * density
                    translationY = sin(t * 2.2f) * 4f * density
                }
        )

        // The hint arrow at the left edge: up = "go higher", down = "go lower".
        if (direction != 0) {
            val up = direction > 0
            val tint = (if (up) colors.accentOrange else colors.brandPrimary).abReadable(3f)
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .graphicsLayer {
                        translationX = -30f * s * density
                        translationY = sin(time.value * 5f) * 3f * s * (if (up) -1f else 1f) * density
                    }
                    .abPopIn()
                    .abBounce(direction, 1.3f)
                    .abToyTile(tint, (15f * s).dp, 2.dp)
                    .size((30f * s).dp),
                contentAlignment = Alignment.Center
            ) {
                NumberDetectiveArrow(up = up, size = (16f * s).dp)
            }
        }

        // A ring of stars flying outward when the case is cracked.
        Canvas(Modifier.matchParentSize()) {
            val p = burst.value
            if (p > 0f) {
                val fade = min(1f, p * 6f) * (1f - p)
                val cx = size.width / 2f
                val cy = size.height / 2f
                val radius = 84f * s * density
                for (i in 0 until 8) {
                    val angle = i / 8.0 * 2.0 * PI
                    val x = cx + (cos(angle) * radius * p).toFloat()
                    val y = cy + (sin(angle) * radius * p).toFloat()
                    val tint = if (i % 2 == 0) burstA else burstB
                    drawPath(numberDetectiveStarPath(x, y, 7f * (0.5f + p) * density), tint.copy(alpha = fade))
                }
            }
        }
    }
}

// ── Keypad ────────────────────────────────────────────────────────────────────────────────────────────────────────────

@Composable
private fun Keypad(s: Float, hasDigits: Boolean, onDigit: (Int) -> Unit, onBackspace: () -> Unit, onSubmit: () -> Unit) {
    val colors = LocalAbColors.current
    val keyShadow = TextStyle(shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f))
    Column(verticalArrangement = Arrangement.spacedBy((8f * s).dp), horizontalAlignment = Alignment.CenterHorizontally) {
        listOf(listOf(1, 2, 3, 4, 5), listOf(6, 7, 8, 9, 0)).forEachIndexed { rowIndex, row ->
            Row(horizontalArrangement = Arrangement.spacedBy((8f * s).dp)) {
                row.forEach { n ->
                    // A candy digit key, one rainbow colour per digit (1..9 then 0).
                    val colorIndex = if (n == 0) 9 else n - 1
                    NumberDetectiveKey(face = colors.toyColor(colorIndex), s = s, popDelayMs = colorIndex * 30, onClick = { onDigit(n) }) {
                        Text("$n", style = AbTypography.fixed(22f * s, FontWeight.Black).merge(keyShadow), color = Color.White)
                    }
                }
                if (rowIndex == 1) {
                    NumberDetectiveKey(face = colors.danger.abReadable(3f), s = s, popDelayMs = 300, onClick = onBackspace) {
                        Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = null,
                            tint = Color.White, modifier = Modifier.size((20f * s).dp))
                    }
                }
            }
        }
        // The Guess button breathes once a number is typed, to invite the next tap.
        GameButton(
            text = stringResource(R.string.number_detective_guess),
            type = ButtonType.ORANGE, isSmall = true, onClick = onSubmit,
            modifier = if (hasDigits) Modifier.abBreathe(0.045f, 1200) else Modifier
        )
    }
}

/** A chunky candy key: bright face, gloss, 3D edge, and a springy squash while it is held down. */
@Composable
private fun NumberDetectiveKey(face: Color, s: Float, popDelayMs: Int, onClick: () -> Unit, content: @Composable () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val press by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "numberDetectiveKey"
    )
    val size = (44f * s).dp
    Box(
        modifier = Modifier
            .abPopIn(popDelayMs)
            .graphicsLayer { scaleX = press; scaleY = press }
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .abToyTile(face, size * 0.3f, 3.dp)
            .size(size),
        contentAlignment = Alignment.Center
    ) { content() }
}

// ── Result ────────────────────────────────────────────────────────────────────────────────────────────────────────────

@Composable
private fun ResultOverlay(state: NumberDetectiveUiState, best: Int, onPlayAgain: () -> Unit, onBack: () -> Unit) {
    val stars = state.starCount
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { if (stars >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        GameResultCard(
            title = stringResource(if (stars >= 2) R.string.balloon_great_job else R.string.good_try),
            emoji = "🕵️",
            accent = ButtonType.GREEN,
            stars = stars,
            score = stringResource(R.string.balloon_final_score) + ": ${state.score}",
            stats = {
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
                    GameStatChip("✅", "${state.correctCount}", Color(0xFF2E7D32))
                    GameStatChip("❌", "${state.wrongCount}", Color(0xFFB71C1C))
                    GameStatChip("🏆", "$best", Color(0xFF3F51B5))
                }
            },
            buttons = {
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    GameButton(
                        text = stringResource(R.string.balloon_play_again),
                        icon = Icons.Filled.Refresh,
                        type = if (enabled) ButtonType.POSITIVE else ButtonType.DISABLE,
                        onClick = { if (enabled) onPlayAgain() }
                    )
                    GameHomeButton(onClick = { if (enabled) onBack() })
                }
            }
        )
    }
}
