package com.jigar.me.ui.view.home.screens.math_game_zone.true_false

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameCharacter

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abBlended
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.screens.math_game_zone.GameCategoryType
import com.jigar.me.ui.view.home.screens.math_game_zone.common.AbDebugFlags
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameMascotPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameResultCard
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameScoreboardPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameStatChip
import com.jigar.me.ui.view.home.screens.math_game_zone.common.NextDifficultyBanner
import com.jigar.me.ui.view.home.screens.math_game_zone.common.PauseTimerInBackground
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBounce
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abPopIn
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abWobble
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.common.randomGameCheer
import com.jigar.me.ui.view.home.screens.math_game_zone.gameCardStyle
import com.jigar.me.ui.view.home.screens.math_game_zone.true_false.components.TrueFalseUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.true_false.viewmodel.TrueFalsePlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)
private val TRUE_GREEN_DARK = Color(0xFF2E7D32)
private val FALSE_RED = Color(0xFFE53935)
private val FALSE_RED_DARK = Color(0xFFB71C1C)

@Composable
fun TrueFalsePlayScreen(
    viewModel: TrueFalsePlayViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val baseCheer = remember { randomGameCheer() }
    val s = gameScale()
    val colors = LocalAbColors.current
    LaunchedEffect(Unit) { viewModel.start() }
    // A phone call or the home button freezes the clock - fair play.
    PauseTimerInBackground { viewModel.setTimerPaused(it) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BackButtonWithText(title = stringResource(R.string.true_false_game), onBackClick = onBackClick)
                    Spacer(Modifier.weight(1f))
                }
                Text(
                    text = stringResource(R.string.true_false_prompt),
                    color = ORANGE,
                    style = AbTypography.fixed(20f, FontWeight.ExtraBold).scaled(),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .abGamePill(outline = ORANGE_BORDER)
                        .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens8)
                )
                // Countdown — turns red for the final 10 seconds.
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = AppDimens.Dimens16)
                        .abGamePill()
                        .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
                ) {
                    Text("⏱", fontSize = (24f * s).sp)
                    Text(
                        "${state.timeLeft}",
                        color = if (state.timeLeft <= 10) FALSE_RED else colors.textPrimary,
                        style = AbTypography.fixed(26f, FontWeight.Black).scaled()
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GameMascotPanel(
                    cheer = when {
                        state.lastAnswerRight && state.streak >= 3 -> "On fire! 🔥"
                        state.lastAnswerWrong -> "Oops! Look close 👀"
                        else -> baseCheer
                    },
                    celebrate = state.lastAnswerRight, s = s,
                    modifier = Modifier.weight(1f).fillMaxHeight(), character = GameCharacter.Owl)

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    TrueFalseQuestionCard(state, s)

                    Spacer(Modifier.height((24f * s).dp))

                    Row(horizontalArrangement = Arrangement.spacedBy((24f * s).dp)) {
                        TrueFalseAnswerKey(true, stringResource(R.string.true_false_btn_true), state, s) {
                            viewModel.answer(true)
                        }
                        TrueFalseAnswerKey(false, stringResource(R.string.true_false_btn_false), state, s) {
                            viewModel.answer(false)
                        }
                    }
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
            // Two strong runs in a row? Invite the kid up a tier.
            state.suggestedDifficulty?.let { NextDifficultyBanner(it) }
        }

        // Debug builds only: render the real result overlay without playing (`--ez ab_show_result true`).
        if (AbDebugFlags.showResultEnabled && !state.isGameOver) {
            ResultOverlay(state = state, best = viewModel.bestScore,
                onPlayAgain = { viewModel.start() }, onBack = onBackClick)
        }
    }
}

// The statement card pops in for every new question, washes green on a right answer (with a star burst and a tick stamp) and
// shakes red on a wrong one (with a cross stamp and the true answer).
@Composable
private fun TrueFalseQuestionCard(state: TrueFalseUiState, s: Float) {
    val colors = LocalAbColors.current
    val gameAccent: Color = gameCardStyle(GameCategoryType.TRUE_FALSE).light
    val answered = state.lastAnswerRight || state.lastAnswerWrong
    val rimTarget: Color = when {
        state.lastAnswerRight -> colors.accentGreen
        state.lastAnswerWrong -> colors.danger
        else -> gameAccent
    }
    // paper-white (or night-purple) face that washes green on a right answer and red on a wrong one
    val faceTarget: Color = when {
        state.lastAnswerRight -> if (colors.isDark) colors.accentGreen.abDarkened(0.6f) else colors.accentGreen.abBlended(Color.White, 0.78f)
        state.lastAnswerWrong -> if (colors.isDark) colors.danger.abDarkened(0.6f) else colors.danger.abBlended(Color.White, 0.78f)
        else -> colors.bgSecondary
    }
    val rim by animateColorAsState(rimTarget, tween(180), label = "rim")
    val face by animateColorAsState(faceTarget, tween(180), label = "face")
    val cornerDp = (26f * s).dp
    val shape = RoundedCornerShape(cornerDp)
    val drop = (7f * s).dp
    val glossAlpha = if (colors.isDark) 0.08f else 0.4f

    Box(modifier = Modifier.abWobble(state.lastAnswerWrong)) {
        key(state.questionNumber) {
            Box(modifier = Modifier.abPopIn()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy((4f * s).dp),
                    modifier = Modifier
                        .padding(bottom = drop)
                        .drawBehind {
                            drawRoundRect(
                                rim.abDarkened(0.3f),
                                topLeft = Offset(0f, drop.toPx()),
                                size = size,
                                cornerRadius = CornerRadius(cornerDp.toPx())
                            )
                        }
                        .shadow(12.dp, shape, clip = false)
                        .background(face, shape)
                        .drawBehind {
                            drawRoundRect(
                                Brush.verticalGradient(listOf(Color.White.copy(alpha = glossAlpha), Color.Transparent), endY = size.height * 0.5f),
                                cornerRadius = CornerRadius(cornerDp.toPx())
                            )
                        }
                        .border(5.dp, rim, shape)
                        .padding(horizontal = (36f * s).dp, vertical = (24f * s).dp)
                ) {
                    Text(
                        state.question?.text ?: "",
                        color = colors.textPrimary,
                        style = AbTypography.fixed(40f * s, FontWeight.Black),
                        maxLines = 1, softWrap = false
                    )
                    if (state.lastAnswerWrong) {
                        Text(
                            stringResource(R.string.true_false_answer_was, state.question?.correctAnswer ?: 0),
                            color = colors.danger.abReadable(4.5f),
                            style = AbTypography.fixed(20f * s, FontWeight.ExtraBold)
                        )
                    }
                }
                if (answered) {
                    TrueFalseStamp(
                        right = state.lastAnswerRight, s = s,
                        modifier = Modifier.align(Alignment.TopEnd).offset(x = (16f * s).dp, y = (-16f * s).dp)
                    )
                }
            }
        }
        TrueFalseSparkBurst(
            trigger = state.correctCount, tint = colors.accentYellow, radius = 90f * s,
            modifier = Modifier.align(Alignment.TopEnd).offset(x = (16f * s).dp, y = (-16f * s).dp).size(1.dp)
        )
    }
}

/**
 * A chunky candy key. The pressed key squashes; after an answer the key the kid chose bounces (right) or shakes (wrong), and on a
 * wrong answer the correct key lights up with a white ring so the truth is clear.
 */
@Composable
private fun TrueFalseAnswerKey(forTrue: Boolean, label: String, state: TrueFalseUiState, s: Float, onClick: () -> Unit) {
    val colors = LocalAbColors.current
    val isCorrectKey: Boolean = (state.question?.isTrue ?: true) == forTrue
    val answered = state.lastAnswerRight || state.lastAnswerWrong
    val chosen: Boolean = answered && (isCorrectKey == state.lastAnswerRight)
    val reveal: Boolean = state.lastAnswerWrong && isCorrectKey
    val face: Color = (if (forTrue) colors.accentGreen else colors.danger).abReadable(3f)
    val cornerDp = (20f * s).dp

    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val sx by animateFloatAsState(
        targetValue = if (pressed) 1.06f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium), label = "keySx"
    )
    val sy by animateFloatAsState(
        targetValue = if (pressed) 0.86f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium), label = "keySy"
    )
    val revealScale by animateFloatAsState(
        targetValue = if (reveal) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium), label = "keyReveal"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((2f * s).dp),
        modifier = Modifier
            .abBounce(chosen && state.lastAnswerRight, 1.15f)
            .abWobble(chosen && state.lastAnswerWrong)
            .graphicsLayer {
                scaleX = sx * revealScale
                scaleY = sy * revealScale
                transformOrigin = TransformOrigin(0.5f, 1f)
            }
            .clickable(interactionSource = interaction, indication = null) { onClick() }
            .abToyTile(face, cornerDp, (6f * s).dp)
            .border(4.dp, Color.White.copy(alpha = if (reveal) 1f else 0f), RoundedCornerShape(cornerDp))
            .padding(horizontal = (34f * s).dp, vertical = (11f * s).dp)
    ) {
        Canvas(modifier = Modifier.size((30f * s).dp)) {
            trueFalseMark(check = forTrue, c = center, half = this.size.minDimension * 0.5f, strokeWidth = 5f * s * density)
        }
        Text(
            label,
            color = Color.White,
            style = AbTypography.fixed(18f * s, FontWeight.Black).copy(shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f))
        )
    }
}

/** The round tick / cross stamp on the statement card's corner. Pops in when the answer is known. */
@Composable
private fun TrueFalseStamp(right: Boolean, s: Float, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    val face: Color = (if (right) colors.accentGreen else colors.danger).abReadable(3f)
    Canvas(modifier = modifier.abPopIn().size((38f * s).dp)) {
        val d = density
        val r = size.minDimension / 2f
        val c = Offset(size.width / 2f, size.height / 2f)
        drawCircle(face.abDarkened(0.3f), radius = r, center = Offset(c.x, c.y + 3f * d))
        drawCircle(face, radius = r, center = c)
        drawCircle(
            Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.35f), Color.Transparent), startY = 0f, endY = size.height / 2f),
            radius = r, center = c
        )
        drawCircle(Color.White, radius = r, center = c, style = Stroke(width = 2.5f * d))
        trueFalseMark(check = right, c = c, half = r * 0.5f, strokeWidth = 3.6f * s * d)
    }
}

/** A ring of stars that flies out from a point and fades. Plays each time the correct count goes up. */
@Composable
private fun TrueFalseSparkBurst(trigger: Int, tint: Color, radius: Float, modifier: Modifier) {
    val progress = remember { Animatable(1f) }
    val last = remember { intArrayOf(trigger) }
    LaunchedEffect(trigger) {
        val increased = trigger > last[0]
        last[0] = trigger
        if (increased) {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(750, easing = LinearEasing))
        }
    }
    Canvas(modifier) {
        val p = progress.value
        if (p > 0f && p < 1f) {
            val eased = 1f - (1f - p) * (1f - p)
            for (i in 0 until 10) {
                val angle = i / 10f * 2f * PI.toFloat() - 0.6f
                val reachScale = if (i % 2 == 0) 1f else 0.7f
                val dist = radius * density * (0.35f + 0.65f * eased) * reachScale
                val starR = (16f + (i % 3) * 4f) * density * 0.5f * (1f - 0.4f * p)
                val color = if (i % 2 == 0) tint else Color.White
                trueFalseStar(Offset(center.x + cos(angle) * dist, center.y + sin(angle) * dist), starR, color.copy(alpha = 1f - p))
            }
        }
    }
}

@Composable
private fun ResultOverlay(state: TrueFalseUiState, best: Int, onPlayAgain: () -> Unit, onBack: () -> Unit) {
    val stars = state.starCount
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { if (stars >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        // "Candy Pop" celebration card (the shared kit card in this game's green).
        GameResultCard(
            title = stringResource(if (stars >= 2) R.string.balloon_great_job else R.string.good_try),
            emoji = "⚖️",
            accent = ButtonType.GREEN,
            stars = stars,
            score = stringResource(R.string.balloon_final_score) + ": ${state.score}",
            stats = {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    GameStatChip("✅", "${state.correctCount}", TRUE_GREEN_DARK)
                    GameStatChip("❌", "${state.wrongCount}", FALSE_RED_DARK)
                    GameStatChip("🏆", "$best", ORANGE_BORDER)
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
                    GameHomeButton(
                        modifier = Modifier.graphicsLayer { alpha = if (enabled) 1f else 0.5f },
                        onClick = { if (enabled) onBack() }
                    )
                }
            }
        )
    }
}

/** A five-point star centred on [center]. */
private fun DrawScope.trueFalseStar(center: Offset, outer: Float, color: Color) {
    val inner = outer * 0.45f
    val path = Path()
    for (k in 0 until 10) {
        val r = if (k % 2 == 0) outer else inner
        val a = -PI / 2.0 + k * PI / 5.0
        val x = center.x + (cos(a) * r).toFloat()
        val y = center.y + (sin(a) * r).toFloat()
        if (k == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color)
}

/** A white tick or cross centred on [c]; [half] is half the glyph's width. */
private fun DrawScope.trueFalseMark(check: Boolean, c: Offset, half: Float, strokeWidth: Float) {
    val path = Path()
    if (check) {
        path.moveTo(c.x - half * 0.9f, c.y + half * 0.05f)
        path.lineTo(c.x - half * 0.25f, c.y + half * 0.65f)
        path.lineTo(c.x + half * 0.95f, c.y - half * 0.6f)
    } else {
        path.moveTo(c.x - half * 0.7f, c.y - half * 0.7f)
        path.lineTo(c.x + half * 0.7f, c.y + half * 0.7f)
        path.moveTo(c.x + half * 0.7f, c.y - half * 0.7f)
        path.lineTo(c.x - half * 0.7f, c.y + half * 0.7f)
    }
    drawPath(path, Color.White, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
}
