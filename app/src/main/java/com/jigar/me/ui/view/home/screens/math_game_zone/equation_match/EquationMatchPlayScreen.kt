package com.jigar.me.ui.view.home.screens.math_game_zone.equation_match

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameCharacter

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abDarkened
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
import com.jigar.me.ui.view.home.screens.math_game_zone.common.randomGameCheer
import com.jigar.me.ui.view.home.screens.math_game_zone.common.toyColor
import com.jigar.me.ui.view.home.screens.math_game_zone.equation_match.components.EqMatchCard
import com.jigar.me.ui.view.home.screens.math_game_zone.equation_match.components.EqMatchCardState
import com.jigar.me.ui.view.home.screens.math_game_zone.equation_match.components.EqMatchPalette
import com.jigar.me.ui.view.home.screens.math_game_zone.equation_match.viewmodel.EquationMatchPlayViewModel
import com.jigar.me.ui.view.home.screens.math_game_zone.gameCardStyle
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)
private val GREEN = Color(0xFF2E7D32)

private val mScale: Float
    get() = if (DeviceInfo.isLargeTablet) 1.7f else if (DeviceInfo.isTablet) 1.45f else 1.0f

@Composable private fun str(id: Int): String = androidx.compose.ui.res.stringResource(id)
@Composable private fun str(id: Int, vararg args: Any): String = androidx.compose.ui.res.stringResource(id, *args)

@Composable
fun EquationMatchPlayScreen(viewModel: EquationMatchPlayViewModel, onBackClick: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val baseCheer = remember { randomGameCheer() }
    val s = mScale
    val config = viewModel.config

    val gap = (10f * s).dp
    val cardW = ((360f * s).dp - gap * (config.cols + 1)) / config.cols
    val cardH = ((240f * s).dp - gap * (config.rows + 1)) / config.rows
    val cell = if (cardW < cardH) cardW else cardH

    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { if (!started) { started = true; viewModel.start() } }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BackButtonWithText(title = str(R.string.equation_match), onBackClick = onBackClick)
                    Spacer(Modifier.weight(1f))
                }
                Text(
                    text = str(R.string.equation_match_prompt),
                    color = ORANGE, style = AbTypography.fixed(22f * s, FontWeight.ExtraBold),
                    modifier = Modifier.align(Alignment.Center)
                        .abGamePill(outline = ORANGE_BORDER)
                        .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens8)
                )
            }

            Row(modifier = Modifier.fillMaxWidth().weight(1f), verticalAlignment = Alignment.CenterVertically) {
                GameMascotPanel(
                    cheer = when {
                        state.multiplier >= 3 -> "On fire! 🔥"
                        state.multiplier == 2 -> "Nice match! 🎉"
                        else -> baseCheer
                    },
                    celebrate = state.pulse, s = s,
                    modifier = Modifier.weight(1f).fillMaxHeight(), character = GameCharacter.Frog)

                BoardView(state.cards, config.cols, config.rows, cell, gap) { viewModel.tap(it) }

                GameScoreboardPanel(state.score, state.multiplier, viewModel.bestScore, s, Modifier.weight(1f).fillMaxHeight())
            }
        }

        if (state.isGameOver) {
            ResultOverlay(state.score, viewModel.starCount(), state.matchesFound, viewModel.bestScore,
                onPlayAgain = { viewModel.start() }, onBack = onBackClick)
        }

        // Debug builds only: lets the lead render the real result overlay without playing a round.
        if (AbDebugFlags.showResultEnabled) {
            ResultOverlay(state.score, viewModel.starCount(), state.matchesFound, viewModel.bestScore,
                onPlayAgain = { viewModel.start() }, onBack = onBackClick)
        }
    }
}

@Composable
private fun BoardView(cards: List<EqMatchCard>, cols: Int, rows: Int, cell: Dp, gap: Dp, onTap: (Int) -> Unit) {
    // The tray takes the game's own colour from the zone list. The grid already leaves one gap of margin all round
    // (see `cell`), so the tray's inset fits inside that budget.
    val trayAccent = gameCardStyle(GameCategoryType.EQUATION_MATCH).light
    Column(
        verticalArrangement = Arrangement.spacedBy(gap),
        modifier = Modifier.abToyTray(trayAccent, gap)
    ) {
        for (r in 0 until rows) {
            // A row holding a matched pair draws above its neighbours, so the sparkles are not cut off.
            val rowHasMatched = (0 until cols).any { c -> cards.getOrNull(r * cols + c)?.state == EqMatchCardState.MATCHED }
            Row(
                horizontalArrangement = Arrangement.spacedBy(gap),
                modifier = Modifier.zIndex(if (rowHasMatched) 1f else 0f)
            ) {
                for (c in 0 until cols) {
                    val i = r * cols + c
                    if (i < cards.size) FlipCard(cards[i], cell, i) { onTap(i) }
                    else Spacer(Modifier.size(cell))
                }
            }
        }
    }
}

@Composable
private fun FlipCard(card: EqMatchCard, size: Dp, index: Int, onTap: () -> Unit) {
    val isUp = card.state != EqMatchCardState.DOWN
    val matched = card.state == EqMatchCardState.MATCHED
    val pairColor = EqMatchPalette.face(card.pairId)
    val rotation by animateFloatAsState(if (isUp) 180f else 0f, tween(350), label = "flip")
    val pop by animateFloatAsState(if (matched) 1.06f else 1f, spring(dampingRatio = 0.5f), label = "pop")
    val shape = RoundedCornerShape(size * 0.2f)

    Box(
        modifier = Modifier
            .zIndex(if (matched) 1f else 0f)
            .size(size)
            .abPopIn(minOf(400, index * 30))
            .graphicsLayer { scaleX = pop; scaleY = pop }
            .abBounce(matched, 1.18f)
            // A matched pair glows in its own colour.
            .then(
                if (matched) Modifier.shadow(9.dp, shape, clip = false, ambientColor = pairColor, spotColor = pairColor)
                else Modifier
            )
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onTap() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                },
            contentAlignment = Alignment.Center
        ) {
            if (rotation < 90f) {
                EquationMatchCardBack(size, index)
            } else {
                Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                    EquationMatchCardFront(card.text, size, matched, pairColor)
                }
            }
        }
        if (matched) EquationMatchBurst(size)
    }
}

// Face down: a candy tile in its own colour with a big "?" that wiggles and a twinkling star in the corner.
@Composable
private fun EquationMatchCardBack(size: Dp, index: Int) {
    val colors = LocalAbColors.current
    val face = colors.toyColor(index)
    val edge = minOf(4f, maxOf(2f, size.value * 0.07f)).dp
    val phase = index * 0.7f
    val transition = rememberInfiniteTransition(label = "eqBack")
    val wiggleAngle by transition.animateFloat(
        0f, (2.0 * PI).toFloat(), infiniteRepeatable(tween(2417, easing = LinearEasing), RepeatMode.Restart), label = "wiggle"
    )
    val twinkleAngle by transition.animateFloat(
        0f, (2.0 * PI).toFloat(), infiniteRepeatable(tween(1571, easing = LinearEasing), RepeatMode.Restart), label = "twinkle"
    )
    Box(
        modifier = Modifier.size(size).abToyTile(face, (size.value * 0.2f).dp, edge),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "?",
            style = AbTypography.fixed(size.value * 0.5f, FontWeight.Black).copy(
                shadow = Shadow(face.abDarkened(0.5f).copy(alpha = 0.5f), Offset(0f, 4f), 0f)
            ),
            color = Color.White,
            modifier = Modifier.graphicsLayer { rotationZ = sin(wiggleAngle + phase) * 9f }
        )
        Icon(
            Icons.Filled.Star, contentDescription = null, tint = Color.White.copy(alpha = 0.75f),
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = -(size * 0.27f), y = -(size * 0.27f))
                .size(size * 0.16f)
                .graphicsLayer {
                    val twinkle = 0.85f + 0.2f * sin(twinkleAngle + phase)
                    scaleX = twinkle
                    scaleY = twinkle
                }
        )
    }
}

// Face up: a cream candy card with the equation / answer. Once matched the pair takes its colour and gets a check badge.
@Composable
private fun EquationMatchCardFront(text: String, size: Dp, matched: Boolean, pairColor: Color) {
    val edge = minOf(4f, maxOf(2f, size.value * 0.07f)).dp
    val face = if (matched) pairColor else Color(0xFFFFF6DC)
    val ink = Color(0xFF2B2F36)
    val len = text.length
    val frac = when {
        len <= 2 -> 0.38f
        len == 3 -> 0.34f
        len == 4 -> 0.30f
        len == 5 -> 0.26f
        else -> 0.22f
    }
    Box(
        modifier = Modifier.size(size).abToyTile(face, (size.value * 0.2f).dp, edge),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            style = AbTypography.fixed(size.value * frac, FontWeight.Black),
            color = if (matched) Color.White else ink,
            maxLines = 1, softWrap = false,
            modifier = Modifier.padding(horizontal = size * 0.08f)
        )
        if (matched) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(size * 0.04f)
                    .size(size * 0.22f)
                    .abPopIn(100)
            ) {
                Box(Modifier.align(Alignment.Center).fillMaxSize(0.7f).background(Color.White, CircleShape))
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = pairColor, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

// A ring of little stars thrown out when a pair is found.
@Composable
private fun EquationMatchBurst(size: Dp) {
    val colors = LocalAbColors.current
    val burst = remember { Animatable(0f) }
    LaunchedEffect(Unit) { burst.animateTo(1f, tween(700, easing = LinearOutSlowInEasing)) }
    Box(Modifier.size(size)) {
        for (k in 0 until 6) {
            val angle = k * (PI / 3.0) + 0.4
            val dx = (cos(angle) * size.value * 0.78).toFloat()
            val dy = (sin(angle) * size.value * 0.78).toFloat()
            val tint = if (k % 2 == 0) colors.accentYellow else colors.accentPink
            Icon(
                Icons.Filled.Star, contentDescription = null, tint = tint,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(size * 0.2f)
                    .graphicsLayer {
                        val b = burst.value
                        translationX = dx * b * density
                        translationY = dy * b * density
                        scaleX = 1f - 0.7f * b
                        scaleY = 1f - 0.7f * b
                        alpha = (1f - b).coerceIn(0f, 1f)
                    }
            )
        }
    }
}

@Composable
private fun ResultOverlay(score: Int, starCount: Int, matches: Int, best: Int, onPlayAgain: () -> Unit, onBack: () -> Unit) {
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { if (starCount >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        // "Candy Pop" celebration card from the shared kit.
        GameResultCard(
            title = str(if (starCount >= 2) R.string.balloon_great_job else R.string.good_try),
            emoji = "🟰",
            accent = ButtonType.RED,
            stars = starCount,
            score = str(R.string.balloon_final_score) + ": $score",
            stats = {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    GameStatChip("✅", "$matches", GREEN)
                    GameStatChip("🏆", "$best", ORANGE_BORDER)
                }
            },
            buttons = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20),
                    modifier = Modifier.graphicsLayer { alpha = if (enabled) 1f else 0.55f }
                ) {
                    GameButton(text = str(R.string.balloon_play_again), icon = Icons.Filled.Refresh,
                        type = ButtonType.POSITIVE, onClick = { if (enabled) onPlayAgain() })
                    GameHomeButton(onClick = { if (enabled) onBack() })
                }
            },
        )
    }
}
