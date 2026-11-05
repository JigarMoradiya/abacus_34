package com.jigar.me.ui.view.home.screens.math_game_zone.missing_operator

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameCharacter

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton

import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.appScale
import com.jigar.me.ui.view.ab.AbColors
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abBlended
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.screens.math_game_zone.GameCategoryType
import com.jigar.me.ui.view.home.screens.math_game_zone.common.AbDebugFlags
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameMascotPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameResultCard
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameScoreboardPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameStatChip
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBounce
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abPopIn
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTray
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abWobble
import com.jigar.me.ui.view.home.screens.math_game_zone.common.randomGameCheer
import com.jigar.me.ui.view.home.screens.math_game_zone.gameCardStyle
import com.jigar.me.ui.view.home.screens.math_game_zone.missing_operator.components.MathOperator
import com.jigar.me.ui.view.home.screens.math_game_zone.missing_operator.components.MissingOperatorUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.missing_operator.viewmodel.MissingOperatorPlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)
private val GREEN = Color(0xFF2E7D32)
private val RED = Color(0xFFD32F2F)

private val moScale: Float
    get() = if (DeviceInfo.isLargeTablet) 1.7f else if (DeviceInfo.isTablet) 1.45f else 1.0f

@Composable
fun MissingOperatorPlayScreen(
    viewModel: MissingOperatorPlayViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val baseCheer = remember { randomGameCheer() }
    val s = moScale
    LaunchedEffect(Unit) { viewModel.start() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BackButtonWithText(title = str(R.string.missing_operator_game), onBackClick = onBackClick)
                    Spacer(Modifier.weight(1f))
                    Hud(viewModel.config.timerSeconds, viewModel.config.roundsGoal, state.timeLeft, state.roundsPlayed)
                }
                // The question, on a pill with an orange rim.
                Text(
                    text = str(R.string.missing_op_which_sign),
                    style = AbTypography.fixed(20f * appScale(), FontWeight.ExtraBold),
                    color = ORANGE,
                    modifier = Modifier.align(Alignment.Center)
                        .abGamePill(outline = ORANGE_BORDER)
                        .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens8)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GameMascotPanel(
                    cheer = when {
                        state.revealed && state.lastCorrect -> "Awesome! 🎉"
                        state.revealed -> "Oops! Try again 💪"
                        viewModel.multiplier > 1 -> "On fire! 🔥"
                        else -> baseCheer
                    },
                    celebrate = state.revealed && state.lastCorrect, s = s,
                    modifier = Modifier.weight(1f).fillMaxHeight(), character = GameCharacter.Monkey)

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    EquationCard(state, s)

                    Spacer(Modifier.height(AppDimens.Dimens16))

                    // A tick or a cross pops in once the sign is chosen.
                    val verdictScale by animateFloatAsState(
                        targetValue = if (state.revealed) 1f else 0.5f,
                        animationSpec = spring(dampingRatio = 0.5f), label = "missingOperatorVerdict"
                    )
                    Box(Modifier.height((48f * s).dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (state.revealed) (if (state.lastCorrect) "✅" else "❌") else " ",
                            fontSize = (40f * s).sp,
                            modifier = Modifier.graphicsLayer { scaleX = verdictScale; scaleY = verdictScale }
                        )
                    }

                    Spacer(Modifier.height(AppDimens.Dimens16))

                    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                        state.round.options.forEach { op ->
                            OperatorButton(op.symbol, opButtonState(op, state.revealed, state.round.answer, state.chosen), s) {
                                viewModel.answer(op)
                            }
                        }
                    }
                }

                GameScoreboardPanel(state.score, viewModel.multiplier, viewModel.bestScore, s, Modifier.weight(1f).fillMaxHeight())
            }
        }

        if (state.isGameOver) {
            ResultOverlay(state.score, state.correctCount, state.wrongCount, viewModel.bestScore,
                onPlayAgain = { viewModel.start() }, onBack = onBackClick)
        }
        // Debug builds only: lets the lead render the real result overlay without playing a round.
        if (AbDebugFlags.showResultEnabled && !state.isGameOver) {
            ResultOverlay(state.score, state.correctCount, state.wrongCount, viewModel.bestScore,
                onPlayAgain = { viewModel.start() }, onBack = onBackClick)
        }
    }
}

private enum class OpBtnState { IDLE, CORRECT, WRONG, DIMMED }
private fun opButtonState(op: MathOperator, revealed: Boolean, answer: MathOperator, chosen: MathOperator?): OpBtnState {
    if (!revealed) return OpBtnState.IDLE
    if (op == answer) return OpBtnState.CORRECT
    if (op == chosen) return OpBtnState.WRONG
    return OpBtnState.DIMMED
}

@Composable private fun str(id: Int): String = androidx.compose.ui.res.stringResource(id)

private val MissingOperatorTextShadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f)

/** Seconds since the screen showed, ticking every frame. Read it inside `graphicsLayer {}` / draw blocks so only the layer redraws. */
@Composable
internal fun rememberMissingOperatorTime(): State<Float> {
    val time = remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) { withFrameNanos { now -> time.value = (now - start) / 1_000_000_000f } }
    }
    return time
}

private fun missingOperatorStarPath(cx: Float, cy: Float, outer: Float): Path {
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

// ── Toy equation board (shared with the Home preview) ────────────────────────────────────────────────────────────────

/** One candy colour per sign: plus sky blue, minus orange, times purple, divide pink. */
internal fun missingOperatorFace(symbol: String, colors: AbColors): Color {
    val base = when (symbol) {
        "+" -> colors.brandSecondary
        "−" -> colors.accentOrange
        "×" -> colors.accentPurple
        else -> colors.accentPink
    }
    return base.abReadable(3f)
}

/** A number on the board: a candy tile that pops when its value changes. */
@Composable
internal fun MissingOperatorTile(text: String, face: Color, s: Float) {
    Box(
        Modifier
            .abToyTile(face, (16f * s).dp, 4.dp)
            .defaultMinSize(minWidth = (50f * s).dp, minHeight = (62f * s).dp)
            .padding(horizontal = (8f * s).dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            style = AbTypography.fixed(40f * s, FontWeight.Black).copy(shadow = MissingOperatorTextShadow),
            color = Color.White,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.abBounce(text, 1.2f)
        )
    }
}

/** The "=" as two chunky bars, in the game's dark green so it reads on the white board. */
@Composable
internal fun MissingOperatorEquals(s: Float) {
    val ink = gameCardStyle(GameCategoryType.MISSING_OPERATOR).dark
    Column(
        Modifier.padding(bottom = 4.dp).height((62f * s).dp),
        verticalArrangement = Arrangement.spacedBy((7f * s).dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.size((26f * s).dp, (7f * s).dp).background(ink, CircleShape))
        Box(Modifier.size((26f * s).dp, (7f * s).dp).background(ink, CircleShape))
    }
}

/**
 * The empty slot is a dashed socket with a breathing "?" whose dashes march around it. A chosen sign drops in as a
 * candy tile that pops (green when right, red when wrong).
 */
@Composable
internal fun MissingOperatorSlot(symbol: String?, revealed: Boolean, correct: Boolean, s: Float) {
    val colors = LocalAbColors.current
    val tint = when {
        !revealed -> colors.accentOrange.abReadable(3f)
        correct -> colors.accentGreen.abReadable(3f)
        else -> colors.danger.abReadable(3f)
    }
    Box(Modifier.size((62f * s).dp, (66f * s).dp), contentAlignment = Alignment.TopCenter) {
        if (symbol != null) {
            Box(
                Modifier
                    .abPopIn()
                    .abToyTile(tint, (16f * s).dp, 4.dp)
                    .size((62f * s).dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    symbol,
                    style = AbTypography.fixed(44f * s, FontWeight.Black).copy(shadow = MissingOperatorTextShadow),
                    color = Color.White
                )
            }
        } else {
            MissingOperatorSocket(tint, s)
        }
    }
}

@Composable
private fun MissingOperatorSocket(tint: Color, s: Float) {
    val time = rememberMissingOperatorTime()
    val corner = (16f * s).dp
    Box(
        Modifier
            .size((62f * s).dp)
            .graphicsLayer {
                val pulse = 1f + 0.08f * sin(time.value * 3f)
                scaleX = pulse
                scaleY = pulse
            }
            .drawBehind {
                val lineW = 3.dp.toPx()
                val r = corner.toPx()
                drawRoundRect(tint.copy(alpha = 0.14f), cornerRadius = CornerRadius(r))
                val inset = lineW / 2f
                drawRoundRect(
                    tint,
                    Offset(inset, inset),
                    Size(size.width - lineW, size.height - lineW),
                    CornerRadius(r - inset),
                    style = Stroke(
                        lineW,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(7.dp.toPx(), 5.dp.toPx()), time.value * 14f * density)
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text("?", style = AbTypography.fixed(40f * s, FontWeight.Black), color = tint)
    }
}

/**
 * The equation on a chunky toy tray. It bounces up when the sign is right (with a star burst), and wobbles with a
 * red rim when it is wrong.
 */
@Composable
private fun EquationCard(state: MissingOperatorUiState, s: Float) {
    val colors = LocalAbColors.current
    val correct = state.lastCorrect
    val accent = if (state.revealed && !correct) colors.danger else gameCardStyle(GameCategoryType.MISSING_OPERATOR).light
    val cardScale by animateFloatAsState(
        targetValue = if (state.revealed && correct) 1.06f else 1f,
        animationSpec = spring(dampingRatio = 0.5f), label = ""
    )
    val burst = remember { Animatable(0f) }
    LaunchedEffect(state.revealed) {
        if (state.revealed && correct) burst.animateTo(1f, tween(900, easing = LinearOutSlowInEasing))
        else if (!state.revealed) burst.snapTo(0f)
    }
    val burstA = colors.accentYellow
    val burstB = colors.accentPink

    Box(
        Modifier
            .abWobble(state.revealed && !correct)
            .graphicsLayer { scaleX = cardScale; scaleY = cardScale }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .abToyTray(accent, AppDimens.Dimens12)
                .padding(horizontal = AppDimens.Dimens4)
        ) {
            MissingOperatorTile("${state.round.left}", colors.brandPrimary.abReadable(3f), s)
            MissingOperatorSlot(state.chosen?.symbol, state.revealed, correct, s)
            MissingOperatorTile("${state.round.right}", colors.brandPrimary.abReadable(3f), s)
            MissingOperatorEquals(s)
            MissingOperatorTile("${state.round.result}", colors.accentPurple.abReadable(3f), s)
        }
        // A burst of stars when the sign is right.
        Canvas(Modifier.matchParentSize()) {
            val p = burst.value
            if (p > 0f) {
                val fade = min(1f, p * 6f) * (1f - p)
                val cx = size.width / 2f
                val cy = size.height / 2f
                val rx = 150f * s * density
                val ry = 70f * s * density
                for (i in 0 until 10) {
                    val angle = i / 10.0 * 2.0 * PI
                    val x = cx + (cos(angle) * rx * p).toFloat()
                    val y = cy + (sin(angle) * ry * p).toFloat()
                    val tint = if (i % 2 == 0) burstA else burstB
                    drawPath(missingOperatorStarPath(x, y, 7f * (0.5f + p) * density), tint.copy(alpha = fade))
                }
            }
        }
    }
}

/** A chunky candy sign key. A right sign turns green and grows, a wrong tap turns red and wobbles, the rest fade to grey. */
@Composable
private fun OperatorButton(symbol: String, state: OpBtnState, s: Float, onClick: () -> Unit) {
    val colors = LocalAbColors.current
    val idleFace = missingOperatorFace(symbol, colors)
    val fill = when (state) {
        OpBtnState.IDLE -> idleFace
        OpBtnState.CORRECT -> colors.accentGreen.abReadable(3f)
        OpBtnState.WRONG -> colors.danger.abReadable(3f)
        OpBtnState.DIMMED -> idleFace.abBlended(Color.Gray, 0.7f)
    }
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (pressed) 0.92f else if (state == OpBtnState.CORRECT) 1.12f else 1f, spring(dampingRatio = 0.5f), label = ""
    )
    Box(
        modifier = Modifier
            .abWobble(state == OpBtnState.WRONG)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(interactionSource = interaction, indication = null) { onClick() }
    ) {
        Box(
            Modifier
                .abToyTile(fill, (22f * s).dp, 5.dp)
                .size((84f * s).dp, (80f * s).dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                symbol,
                style = AbTypography.fixed(44f * s, FontWeight.Black).copy(shadow = MissingOperatorTextShadow),
                color = Color.White
            )
        }
        // A tick on the right sign, a cross on a wrong tap.
        if (state == OpBtnState.CORRECT || state == OpBtnState.WRONG) {
            val tint = if (state == OpBtnState.CORRECT) colors.accentGreen.abReadable(3f) else colors.danger.abReadable(3f)
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (8f * s).dp, y = (-8f * s).dp)
                    .abPopIn()
                    .size((26f * s).dp)
                    .background(tint, CircleShape)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (state == OpBtnState.CORRECT) Icons.Filled.Check else Icons.Filled.Close,
                    contentDescription = null, tint = Color.White,
                    modifier = Modifier.size((15f * s).dp)
                )
            }
        }
    }
}

@Composable
private fun Hud(timerSeconds: Int?, roundsGoal: Int?, timeLeft: Int, roundsPlayed: Int) {
    val colors = LocalAbColors.current
    val style = AbTypography.fixed(18f * appScale(), FontWeight.ExtraBold)
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16), verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = AppDimens.Dimens16)
            .abGamePill()
            .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens6)
    ) {
        if (timerSeconds != null) Text("⏱ $timeLeft", style = style, color = colors.textPrimary)
        else if (roundsGoal != null) Text("🎯 $roundsPlayed/$roundsGoal", style = style, color = colors.textPrimary)
    }
}

@Composable
private fun ResultOverlay(score: Int, correctCount: Int, wrongCount: Int, bestScore: Int, onPlayAgain: () -> Unit, onBack: () -> Unit) {
    val attempts = correctCount + wrongCount
    val accuracy = if (attempts > 0) correctCount.toFloat() / attempts else 0f
    val starCount = when { accuracy >= 0.9f -> 3; accuracy >= 0.7f -> 2; else -> 1 }
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { if (starCount >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        GameResultCard(
            title = str(R.string.balloon_great_job),
            emoji = "➗",
            accent = ButtonType.ORANGE,
            stars = starCount,
            score = str(R.string.balloon_final_score) + ": $score",
            stats = {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    GameStatChip("🎯", "$correctCount", GREEN)
                    GameStatChip("❌", "$wrongCount", RED)
                    GameStatChip("🏆", "$bestScore", ORANGE_BORDER)
                }
            },
            buttons = {
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    GameButton(
                        text = str(R.string.balloon_play_again),
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
