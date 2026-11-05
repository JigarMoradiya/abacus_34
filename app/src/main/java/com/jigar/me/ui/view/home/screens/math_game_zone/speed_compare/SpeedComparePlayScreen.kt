package com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare

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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.screens.math_game_zone.common.AbDebugFlags
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameMascotPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameResultCard
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameScoreboardPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameStatChip
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBounce
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abWobble
import com.jigar.me.ui.view.home.screens.math_game_zone.common.randomGameCheer
import com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.components.Comparator
import com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.viewmodel.SpeedComparePlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)
private val GREEN = Color(0xFF2E7D32)
private val RED = Color(0xFFD32F2F)

@Composable
fun SpeedComparePlayScreen(
    viewModel: SpeedComparePlayViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val baseCheer = remember { randomGameCheer() }
    val s = if (DeviceScaleHolder.isLargeTablet) 1.7f else if (DeviceScaleHolder.isTablet) 1.45f else 1.0f
    LaunchedEffect(Unit) { viewModel.start() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Header: back left, prompt dead-center, HUD right
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BackButtonWithText(title = stringRes(R.string.speed_compare_game), onBackClick = onBackClick)
                    Spacer(Modifier.weight(1f))
                    Hud(
                        timerSeconds = viewModel.config.timerSeconds,
                        roundsGoal = viewModel.config.roundsGoal,
                        timeLeft = state.timeLeft,
                        roundsPlayed = state.roundsPlayed
                    )
                }
                Text(
                    text = stringRes(R.string.speed_which_bigger),
                    color = ORANGE,
                    style = AbTypography.fixed(20f, FontWeight.ExtraBold).scaled(),
                    modifier = Modifier
                        .align(Alignment.Center)
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
                    modifier = Modifier.weight(1f).fillMaxHeight(), character = GameCharacter.Fox)

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    BalanceScale(left = state.round.left, right = state.round.right, revealed = state.revealed)

                    // Feedback tick / cross when revealed
                    Box(modifier = Modifier.height((40f * s).dp), contentAlignment = Alignment.Center) {
                        SpeedCompareFeedbackBadge(revealed = state.revealed, correct = state.lastCorrect, s = s)
                    }

                    Spacer(Modifier.height(AppDimens.Dimens12))

                    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens24)) {
                        Comparator.entries.forEach { comparator ->
                            CompareButton(
                                symbol = comparator.symbol,
                                state = buttonState(comparator, state.revealed, state.round.truth, state.chosen),
                                onClick = { viewModel.answer(comparator) }
                            )
                        }
                    }
                }

                GameScoreboardPanel(state.score, viewModel.multiplier, viewModel.bestScore, s, Modifier.weight(1f).fillMaxHeight())
            }
        }

        if (state.isGameOver) {
            ResultOverlay(
                score = state.score,
                correctCount = state.correctCount,
                wrongCount = state.wrongCount,
                bestScore = viewModel.bestScore,
                onPlayAgain = { viewModel.start() },
                onBack = onBackClick
            )
        }

        // Debug builds only: render the real result overlay without playing (`--ez ab_show_result true`).
        if (AbDebugFlags.showResultEnabled && !state.isGameOver) {
            ResultOverlay(
                score = state.score,
                correctCount = state.correctCount,
                wrongCount = state.wrongCount,
                bestScore = viewModel.bestScore,
                onPlayAgain = { viewModel.start() },
                onBack = onBackClick
            )
        }
    }
}

private enum class CompareBtnState { IDLE, CORRECT, WRONG, DIMMED }

private fun buttonState(c: Comparator, revealed: Boolean, truth: Comparator, chosen: Comparator?): CompareBtnState {
    if (!revealed) return CompareBtnState.IDLE
    if (c == truth) return CompareBtnState.CORRECT
    if (c == chosen) return CompareBtnState.WRONG
    return CompareBtnState.DIMMED
}

@Composable
private fun stringRes(id: Int): String = androidx.compose.ui.res.stringResource(id)

@Composable
private fun Hud(
    timerSeconds: Int?, roundsGoal: Int?, timeLeft: Int, roundsPlayed: Int
) {
    val colors = LocalAbColors.current
    val style = AbTypography.fixed(18f, FontWeight.ExtraBold).scaled()
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(end = AppDimens.Dimens16)
            .abGamePill()
            .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens6)
    ) {
        if (timerSeconds != null) {
            Text("⏱ $timeLeft", color = colors.textPrimary, style = style)
        } else if (roundsGoal != null) {
            Text("🎯 $roundsPlayed/$roundsGoal", color = colors.textPrimary, style = style)
        }
    }
}

/**
 * A candy key for < = >: each has its own colour, squashes when pressed, flashes green when it is the right answer (with a little
 * star burst) and red with a shake when it was the wrong pick.
 */
@Composable
private fun CompareButton(symbol: String, state: CompareBtnState, onClick: () -> Unit) {
    val colors = LocalAbColors.current
    val s = if (DeviceScaleHolder.isLargeTablet) 1.7f else if (DeviceScaleHolder.isTablet) 1.45f else 1.0f
    val edge = 5f * s
    val idleFace: Color = when (symbol) {
        "<" -> colors.brandPrimary
        "=" -> colors.accentPurple
        else -> colors.accentPink
    }
    val targetFill: Color = when (state) {
        CompareBtnState.IDLE -> idleFace.abReadable(3f)
        CompareBtnState.CORRECT -> colors.accentGreen.abReadable(3f)
        CompareBtnState.WRONG -> colors.danger.abReadable(3f)
        CompareBtnState.DIMMED -> if (colors.isDark) Color(0xFF4A4690) else Color(0xFFB4B2D4)
    }
    val fill by animateColorAsState(targetFill, tween(180), label = "fill")
    val isCorrect = state == CompareBtnState.CORRECT

    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val sx by animateFloatAsState(
        targetValue = if (pressed) 1.07f else if (isCorrect) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium), label = "sx"
    )
    val sy by animateFloatAsState(
        targetValue = if (pressed) 0.88f else if (isCorrect) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium), label = "sy"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .abWobble(state == CompareBtnState.WRONG)
            .abBounce(state, 1.1f)
            .graphicsLayer {
                scaleX = sx
                scaleY = sy
                transformOrigin = TransformOrigin(0.5f, 1f)
            }
            .clickable(interactionSource = interaction, indication = null) { onClick() }
            .abToyTile(fill, (24f * s).dp, edge.dp)
            .size((92f * s).dp, (84f * s - edge).dp)
    ) {
        Text(
            symbol,
            color = Color.White.copy(alpha = if (state == CompareBtnState.DIMMED) 0.85f else 1f),
            style = AbTypography.fixed(52f * s, FontWeight.Black).copy(shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f))
        )
        SpeedCompareSparkBurst(active = isCorrect, tint = colors.accentYellow, reach = 84f * s, modifier = Modifier.matchParentSize())
    }
}

/** The round tick / cross badge under the scale: pops in when the answer is revealed. */
@Composable
private fun SpeedCompareFeedbackBadge(revealed: Boolean, correct: Boolean, s: Float) {
    val colors = LocalAbColors.current
    val face: Color = (if (correct) colors.accentGreen else colors.danger).abReadable(3f)
    val pop by animateFloatAsState(
        targetValue = if (revealed) 1f else 0.3f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium), label = "badge"
    )
    Canvas(
        modifier = Modifier
            .abWobble(revealed && !correct)
            .graphicsLayer {
                scaleX = pop
                scaleY = pop
                alpha = ((pop - 0.3f) / 0.7f).coerceIn(0f, 1f)
            }
            .size((34f * s).dp)
    ) {
        val d = density
        val r = size.minDimension / 2f
        val c = Offset(size.width / 2f, size.height / 2f)
        drawCircle(face.abDarkened(0.3f), radius = r, center = Offset(c.x, c.y + 3f * d))
        drawCircle(face, radius = r, center = c)
        drawCircle(
            Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.35f), Color.Transparent), startY = 0f, endY = size.height / 2f),
            radius = r, center = c
        )
        drawCircle(Color.White.copy(alpha = 0.45f), radius = r, center = c, style = Stroke(width = 1.5f * d))
        speedCompareMark(check = correct, c = c, half = r * 0.5f, strokeWidth = 3.5f * s * d)
    }
}

/** Eight little stars fly out of the key and fade. Plays each time [active] turns true. */
@Composable
private fun SpeedCompareSparkBurst(active: Boolean, tint: Color, reach: Float, modifier: Modifier) {
    val progress = remember { Animatable(1f) }
    LaunchedEffect(active) {
        if (active) {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(700, easing = LinearEasing))
        }
    }
    Canvas(modifier) {
        val p = progress.value
        if (active && p > 0f && p < 1f) {
            val eased = 1f - (1f - p) * (1f - p)
            val dist = reach * density * (0.45f + 0.6f * eased)
            val starR = reach * density * 0.11f * (1f - 0.5f * p)
            for (i in 0 until 8) {
                val angle = i / 8f * 2f * PI.toFloat()
                val color = if (i % 2 == 0) tint else Color.White
                speedCompareStar(
                    Offset(center.x + cos(angle) * dist, center.y + sin(angle) * dist),
                    starR,
                    color.copy(alpha = 1f - p)
                )
            }
        }
    }
}

@Composable
private fun ResultOverlay(
    score: Int, correctCount: Int, wrongCount: Int, bestScore: Int,
    onPlayAgain: () -> Unit, onBack: () -> Unit
) {
    val attempts = correctCount + wrongCount
    val accuracy = if (attempts > 0) correctCount.toFloat() / attempts else 0f
    val starCount = when { accuracy >= 0.9f -> 3; accuracy >= 0.7f -> 2; else -> 1 }

    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { if (starCount >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        // "Candy Pop" celebration card (the shared kit card in this game's orange).
        GameResultCard(
            title = stringRes(R.string.balloon_great_job),
            emoji = "⚖️",
            accent = ButtonType.ORANGE,
            stars = starCount,
            score = stringRes(R.string.balloon_final_score) + ": $score",
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
                        text = stringRes(R.string.balloon_play_again),
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

private object DeviceScaleHolder {
    val isTablet: Boolean get() = com.jigar.me.data.local.data.DeviceInfo.isTablet
    val isLargeTablet: Boolean get() = com.jigar.me.data.local.data.DeviceInfo.isLargeTablet
}
