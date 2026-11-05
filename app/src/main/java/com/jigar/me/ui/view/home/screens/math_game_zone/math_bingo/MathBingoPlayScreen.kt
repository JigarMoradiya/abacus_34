package com.jigar.me.ui.view.home.screens.math_game_zone.math_bingo

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameCharacter

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abBlended
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.screens.math_game_zone.common.PauseTimerInBackground
import com.jigar.me.ui.view.home.screens.math_game_zone.common.NextDifficultyBanner
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.screens.math_game_zone.GameCategoryType
import com.jigar.me.ui.view.home.screens.math_game_zone.common.AbDebugFlags
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameMascotPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameResultCard
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameScoreboardPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameStatChip
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBounce
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBreathe
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTray
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.common.randomGameCheer
import com.jigar.me.ui.view.home.screens.math_game_zone.common.toyColor
import com.jigar.me.ui.view.home.screens.math_game_zone.gameCardStyle
import com.jigar.me.ui.view.home.screens.math_game_zone.math_bingo.components.BINGO_SIZE
import com.jigar.me.ui.view.home.screens.math_game_zone.math_bingo.components.MathBingoUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.math_bingo.viewmodel.MathBingoPlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)
private val WRONG_RED = Color(0xFFE53935)

@Composable
fun MathBingoPlayScreen(
    viewModel: MathBingoPlayViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = LocalAbColors.current
    val baseCheer = remember { randomGameCheer() }
    val s = gameScale()
    LaunchedEffect(Unit) { viewModel.start() }
    // A phone call or the home button freezes the clock - fair play.
    PauseTimerInBackground { viewModel.setTimerPaused(it) }
    val cell = (52f * s).dp

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BackButtonWithText(title = stringResource(R.string.math_bingo_game), onBackClick = onBackClick)
                    Spacer(Modifier.weight(1f))
                }
                // The called equation IS the header prompt — it pops per call.
                CallBanner(state, s, Modifier.align(Alignment.Center))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6),
                    modifier = Modifier.align(Alignment.CenterEnd)
                        .padding(end = AppDimens.Dimens16)
                        .abGamePill()
                        .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
                ) {
                    Text("⏱️", fontSize = (24f * s).sp)
                    Text(
                        "${state.timeLeft}",
                        color = if (state.timeLeft <= 10) WRONG_RED else colors.textPrimary,
                        style = AbTypography.fixed(26f * s, FontWeight.Black)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GameMascotPanel(
                    cheer = when {
                        state.justBingo -> "BINGO! 🎉"
                        state.wrongTapIndex != null -> "Find ${state.call?.text}! 🔍"
                        else -> baseCheer
                    },
                    celebrate = state.justBingo, s = s,
                    modifier = Modifier.weight(1f).fillMaxHeight(), character = GameCharacter.Parrot)

                BingoCard(state, cell, s) { viewModel.tapCell(it) }

                GameScoreboardPanel(
                    score = state.score, multiplier = 1 + state.bingoCount, best = viewModel.bestScore, s = s,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }
        }

        // BINGO banner pops when a line completes.
        if (state.justBingo) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                MathBingoBanner(s)
            }
        }

        if (state.isGameOver) {
            ResultOverlay(state = state, best = viewModel.bestScore,
                onPlayAgain = { viewModel.start() }, onBack = onBackClick)
            // Two strong runs in a row? Invite the kid up a tier.
            state.suggestedDifficulty?.let { NextDifficultyBanner(it) }
        }

        // Debug builds only: lets the lead render the real result overlay without playing a round.
        if (AbDebugFlags.showResultEnabled) {
            ResultOverlay(state = state, best = viewModel.bestScore,
                onPlayAgain = { viewModel.start() }, onBack = onBackClick)
        }
    }
}

@Composable
private fun CallBanner(state: MathBingoUiState, s: Float, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    val pop = remember { Animatable(1f) }
    LaunchedEffect(state.callNumber) {
        pop.snapTo(1.25f)
        pop.animateTo(1f, spring(dampingRatio = 0.5f))
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
        modifier = modifier
            .graphicsLayer { scaleX = pop.value; scaleY = pop.value }
            .abGamePill(outline = ORANGE_BORDER)
            .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens8)
    ) {
        Text("📢", fontSize = (20f * s).sp, modifier = Modifier.abBreathe(0.14f, 1100))
        Text(
            state.call?.text ?: "",
            color = ORANGE, style = AbTypography.fixed(24f * s, FontWeight.ExtraBold),
            maxLines = 1, softWrap = false
        )
        Text(
            "= ?", color = Color.White,
            style = AbTypography.fixed(24f * s, FontWeight.ExtraBold),
            modifier = Modifier
                .abBreathe(0.05f, 1300)
                .background(colors.brandPrimary.abReadable(3f), RoundedCornerShape(percent = 50))
                .padding(horizontal = AppDimens.Dimens10)
        )
    }
}

@Composable
private fun BingoCard(state: MathBingoUiState, cell: Dp, s: Float, onTap: (Int) -> Unit) {
    // The card's tray takes the game's own colour from the zone list.
    val trayAccent = gameCardStyle(GameCategoryType.MATH_BINGO).light
    Column(
        verticalArrangement = Arrangement.spacedBy((6f * s).dp),
        modifier = Modifier.abToyTray(trayAccent, (9f * s).dp)
    ) {
        for (r in 0 until BINGO_SIZE) {
            // A row holding a freshly stamped square draws above its neighbours, so the sparkles are not cut off.
            val rowHasMarked = (0 until BINGO_SIZE).any { c -> state.marked.getOrNull(r * BINGO_SIZE + c) == true }
            Row(
                horizontalArrangement = Arrangement.spacedBy((6f * s).dp),
                modifier = Modifier.zIndex(if (rowHasMarked) 1f else 0f)
            ) {
                for (c in 0 until BINGO_SIZE) {
                    val i = r * BINGO_SIZE + c
                    BingoCell(state, i, cell, s, onTap)
                }
            }
        }
    }
}

// One bingo square. Open: a pastel candy tile with a bold coloured number. Marked: a bright candy tile with a bouncy star
// stamp. During a BINGO the marked tiles turn gold; a wrong tap flashes the tile red.
@Composable
private fun BingoCell(state: MathBingoUiState, i: Int, cell: Dp, s: Float, onTap: (Int) -> Unit) {
    val value = state.card.getOrNull(i) ?: return
    val colors = LocalAbColors.current
    val marked = state.marked.getOrNull(i) == true
    val isWrongTap = state.wrongTapIndex == i
    // Marked cells glow gold during the BINGO celebration.
    val gold = state.justBingo && marked
    val candy = colors.toyColor(i)

    // A wrong tap flashes the tile red for a moment.
    var flash by remember { mutableStateOf(false) }
    LaunchedEffect(state.wrongTapCount) {
        if (isWrongTap && state.wrongTapCount > 0) {
            flash = true
            delay(450)
            flash = false
        }
    }
    val faceTarget = when {
        flash -> colors.danger.abReadable(3f)
        gold -> colors.accentYellow
        marked -> colors.accentGreen.abReadable(3f)
        else -> candy.abBlended(Color.White, 0.74f)
    }
    val face by animateColorAsState(faceTarget, tween(200), label = "bingoFace")

    // The tile presses in when the cell gets marked.
    val stamp = remember { Animatable(1f) }
    LaunchedEffect(marked) {
        if (marked) {
            stamp.snapTo(1.18f)
            stamp.animateTo(1f, spring(dampingRatio = 0.45f))
        }
    }
    // Deal-in animation for every fresh card.
    val deal = remember(state.cardNumber, i) { Animatable(0.01f) }
    LaunchedEffect(state.cardNumber) {
        deal.snapTo(0.01f)
        delay(40L * i)
        deal.animateTo(1f, spring(dampingRatio = 0.55f))
    }
    val shake = remember { Animatable(0f) }
    LaunchedEffect(state.wrongTapCount) {
        if (isWrongTap && state.wrongTapCount > 0) {
            repeat(3) { shake.animateTo(5f, tween(40)); shake.animateTo(-5f, tween(40)) }
            shake.animateTo(0f, tween(35))
        }
    }

    val numberSize = (if (value >= 100) 18f else 24f) * s
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .zIndex(if (marked) 1f else 0f)
            .graphicsLayer {
                scaleX = deal.value * stamp.value
                scaleY = deal.value * stamp.value
                translationX = shake.value * density
            }
            .size(cell)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = !marked
            ) { onTap(i) }
            .abToyTile(face, (cell.value * 0.24f).dp, (3f * s).dp)
    ) {
        if (marked) {
            MathBingoStamp(size = (cell.value * 0.56f).dp, gold = gold)
        } else {
            Text(
                "$value",
                style = AbTypography.fixed(numberSize, FontWeight.Black),
                color = if (flash) Color.White else candy.abDarkened(0.25f),
                maxLines = 1, softWrap = false,
                modifier = Modifier.padding(horizontal = (3f * s).dp)
            )
        }
    }
}

/** A solid star with a hard darker copy under it, like the kit's lettering. Also used by the Home preview. */
@Composable
internal fun MathBingoStar(size: Dp, tint: Color, edgeColor: Color, modifier: Modifier = Modifier) {
    Box(modifier.size(size)) {
        Icon(Icons.Filled.Star, contentDescription = null, tint = edgeColor, modifier = Modifier.size(size).offset(y = 2.dp))
        Icon(Icons.Filled.Star, contentDescription = null, tint = tint, modifier = Modifier.size(size))
    }
}

// The star that lands on a marked square: it drops in big and tilted, springs flat, and throws a ring of little sparkles.
@Composable
private fun MathBingoStamp(size: Dp, gold: Boolean) {
    val colors = LocalAbColors.current
    val landed = remember { Animatable(0f) }
    val burst = remember { Animatable(0f) }
    LaunchedEffect(Unit) { landed.animateTo(1f, spring(dampingRatio = 0.42f, stiffness = 260f)) }
    LaunchedEffect(Unit) { burst.animateTo(1f, tween(600, easing = LinearOutSlowInEasing)) }
    val edgeColor = if (gold) colors.accentOrange else colors.accentGreen.abDarkened(0.45f)

    Box(contentAlignment = Alignment.Center) {
        for (k in 0 until 6) {
            val angle = k * (PI / 3.0)
            val dx = (cos(angle) * size.value).toFloat()
            val dy = (sin(angle) * size.value).toFloat()
            val tint = if (k % 2 == 0) colors.accentYellow else colors.accentPink
            Icon(
                Icons.Filled.Star, contentDescription = null, tint = tint,
                modifier = Modifier
                    .size(size * 0.3f)
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
        MathBingoStar(
            size = size, tint = Color.White, edgeColor = edgeColor,
            modifier = Modifier
                .graphicsLayer {
                    val l = landed.value
                    val sc = 2.4f - 1.4f * l
                    scaleX = sc
                    scaleY = sc
                    rotationZ = -50f * (1f - l)
                    alpha = l.coerceIn(0f, 1f)
                }
                .abBounce(gold, 1.3f)
        )
    }
}

// The BINGO! banner: a chunky gold-orange candy sign that pops in and breathes, with twinkling stars on its corners.
@Composable
private fun MathBingoBanner(s: Float) {
    val colors = LocalAbColors.current
    val face = colors.accentOrange.abReadable(3f)
    val pop = remember { Animatable(0.3f) }
    LaunchedEffect(Unit) { pop.animateTo(1f, spring(dampingRatio = 0.45f)) }
    var t by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) { withFrameNanos { now -> t = (now - start) / 1_000_000_000f } }
    }

    Box(
        modifier = Modifier
            .graphicsLayer { scaleX = pop.value; scaleY = pop.value }
            .abBreathe(0.04f, 900)
    ) {
        Text(
            "🎉 BINGO! 🎉",
            style = AbTypography.fixed(44f * s, FontWeight.Black).copy(
                shadow = Shadow(face.abDarkened(0.45f), Offset(0f, 6f), 0f)
            ),
            color = Color.White,
            maxLines = 1, softWrap = false,
            modifier = Modifier
                .abToyTile(face, 28.dp, 7.dp)
                .padding(horizontal = AppDimens.Dimens30, vertical = AppDimens.Dimens16)
        )
        MathBingoTwinkle(t, 0.0f, s, Modifier.align(Alignment.TopStart).offset(x = (-14f * s).dp, y = (-14f * s).dp))
        MathBingoTwinkle(t, 1.7f, s, Modifier.align(Alignment.TopEnd).offset(x = (14f * s).dp, y = (-14f * s).dp))
        MathBingoTwinkle(t, 3.1f, s, Modifier.align(Alignment.BottomStart).offset(x = (-14f * s).dp, y = (10f * s).dp))
        MathBingoTwinkle(t, 4.4f, s, Modifier.align(Alignment.BottomEnd).offset(x = (14f * s).dp, y = (10f * s).dp))
    }
}

@Composable
private fun MathBingoTwinkle(t: Float, phase: Float, s: Float, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    val grow = 0.8f + 0.25f * sin(t * 6f + phase)
    val spin = sin(t * 3f + phase) * 20f
    MathBingoStar(
        size = (24f * s).dp, tint = colors.accentYellow, edgeColor = colors.accentOrange,
        modifier = modifier.graphicsLayer { scaleX = grow; scaleY = grow; rotationZ = spin }
    )
}

@Composable
private fun ResultOverlay(state: MathBingoUiState, best: Int, onPlayAgain: () -> Unit, onBack: () -> Unit) {
    val stars = state.starCount
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { if (stars >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        // "Candy Pop" celebration card from the shared kit.
        GameResultCard(
            title = stringResource(if (stars >= 2) R.string.balloon_great_job else R.string.good_try),
            emoji = "🎱",
            accent = ButtonType.PINK,
            stars = stars,
            score = stringResource(R.string.balloon_final_score) + ": ${state.score}",
            stats = {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    GameStatChip("🎱", "${state.bingoCount}", Color(0xFF7B1FA2))
                    GameStatChip("✅", "${state.correctCount}", Color(0xFF2E7D32))
                    GameStatChip("🏆", "$best", ORANGE_BORDER)
                }
            },
            buttons = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20),
                    modifier = Modifier.graphicsLayer { alpha = if (enabled) 1f else 0.55f }
                ) {
                    GameButton(text = stringResource(R.string.balloon_play_again), icon = Icons.Filled.Refresh,
                        type = ButtonType.POSITIVE, onClick = { if (enabled) onPlayAgain() })
                    GameHomeButton(onClick = { if (enabled) onBack() })
                }
            },
        )
    }
}
