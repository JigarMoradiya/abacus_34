package com.jigar.me.ui.view.home.screens.math_game_zone.place_value

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameCharacter

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.screens.math_game_zone.common.PauseTimerInBackground
import com.jigar.me.ui.view.home.screens.math_game_zone.common.NextDifficultyBanner
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
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
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.common.randomGameCheer
import com.jigar.me.ui.view.home.screens.math_game_zone.common.toyColor
import com.jigar.me.ui.view.home.screens.math_game_zone.place_value.components.PlaceValueUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.place_value.viewmodel.PlaceValuePlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)

// Place labels under the slots, right to left: Ones, Tens, Hundreds...
private val PLACE_LABELS = listOf("O", "T", "H", "Th", "TTh")

/**
 * A dashed toy socket in its place colour: a pale well, a tint of [face] and a dashed rim. Draws inside the box it is put on.
 * Also used by the Home preview.
 */
internal fun Modifier.placeValueSocket(face: Color, corner: Dp, tintAlpha: Float = 0.14f): Modifier = drawBehind {
    val r = corner.toPx()
    drawRoundRect(Color.White.copy(alpha = 0.7f), cornerRadius = CornerRadius(r))
    drawRoundRect(face.copy(alpha = tintAlpha), cornerRadius = CornerRadius(r))
    val sw = 3.dp.toPx()
    drawRoundRect(
        color = face,
        topLeft = Offset(sw / 2f, sw / 2f),
        size = Size(this.size.width - sw, this.size.height - sw),
        cornerRadius = CornerRadius(maxOf(0f, r - sw / 2f)),
        style = Stroke(
            width = sw,
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(7.dp.toPx(), 6.dp.toPx())),
        ),
    )
}

@Composable
fun PlaceValuePlayScreen(
    viewModel: PlaceValuePlayViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = LocalAbColors.current
    val baseCheer = remember { randomGameCheer() }
    val s = gameScale()
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
                    BackButtonWithText(title = stringResource(R.string.place_value_game), onBackClick = onBackClick)
                    Spacer(Modifier.weight(1f))
                }
                Text(
                    text = stringResource(R.string.place_value_prompt),
                    color = ORANGE, style = AbTypography.fixed(22f * s, FontWeight.ExtraBold),
                    modifier = Modifier.align(Alignment.Center)
                        .abGamePill(outline = ORANGE_BORDER)
                        .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens8)
                )
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
                        color = if (state.timeLeft <= 10) colors.danger.abReadable(4.5f) else colors.textPrimary,
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
                        state.lastAnswerRight && state.streak >= 3 -> "On fire! 🔥"
                        state.lastAnswerWrong -> "Check the places! 🧐"
                        else -> baseCheer
                    },
                    celebrate = state.lastAnswerRight, s = s,
                    modifier = Modifier.weight(1f).fillMaxHeight(), character = GameCharacter.Giraffe)

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ExpandedChips(state, s)
                    Spacer(Modifier.height((18f * s).dp))
                    DigitSlots(state, s) { viewModel.tapSlot(it) }
                    Spacer(Modifier.height((18f * s).dp))
                    Keypad(s, onDigit = { viewModel.tapDigit(it) }, onBackspace = { viewModel.backspace() })
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

        // Debug builds only: lets the lead render the real result overlay without playing a round.
        if (AbDebugFlags.showResultEnabled) {
            ResultOverlay(state = state, best = viewModel.bestScore,
                onPlayAgain = { viewModel.start() }, onBack = onBackClick)
        }
    }
}

// The expanded-form chips: candy blocks in their place-value colour that bounce in one by one for every new number.
@Composable
private fun ExpandedChips(state: PlaceValueUiState, s: Float) {
    val colors = LocalAbColors.current
    val q = state.question ?: return
    Row(horizontalArrangement = Arrangement.spacedBy((10f * s).dp), verticalAlignment = Alignment.CenterVertically) {
        q.expandedParts.forEachIndexed { i, part ->
            val enter = remember(state.questionNumber, i) { Animatable(0.01f) }
            LaunchedEffect(state.questionNumber) {
                enter.snapTo(0.01f)
                delay(90L * i)
                enter.animateTo(1f, spring(dampingRatio = 0.5f))
            }
            // The chip's colour is the colour of its place: ones orange, tens lime, hundreds green ...
            val face = colors.toyColor(maxOf(0, part.toString().length - 1))
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.graphicsLayer { scaleX = enter.value; scaleY = enter.value }) {
                if (i > 0) {
                    Text("+", color = colors.accentOrange.abDarkened(0.2f),
                        style = AbTypography.fixed(30f * s, FontWeight.Black),
                        modifier = Modifier.padding(end = (10f * s).dp))
                }
                Box(
                    modifier = Modifier.abToyTile(face, (14f * s).dp, 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "$part", color = Color.White,
                        style = AbTypography.fixed(28f * s, FontWeight.Black).copy(
                            shadow = Shadow(face.abDarkened(0.5f).copy(alpha = 0.55f), Offset(0f, 3f), 0f)
                        ),
                        maxLines = 1, softWrap = false,
                        modifier = Modifier.padding(horizontal = (14f * s).dp, vertical = (8f * s).dp)
                    )
                }
            }
        }
    }
}

// Digit slots with place labels; the active slot glows, wrong answers shake
// and flash the real number. Each slot wears the colour of the chip it belongs to.
@Composable
private fun DigitSlots(state: PlaceValueUiState, s: Float, onTap: (Int) -> Unit) {
    val colors = LocalAbColors.current
    val q = state.question ?: return
    val shake = remember { Animatable(0f) }
    LaunchedEffect(state.lastAnswerWrong) {
        if (state.lastAnswerWrong) {
            repeat(3) { shake.animateTo(5f, tween(45)); shake.animateTo(-5f, tween(45)) }
            shake.animateTo(0f, tween(40))
        }
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            horizontalArrangement = Arrangement.spacedBy((10f * s).dp),
            modifier = Modifier.graphicsLayer { translationX = shake.value * density }
        ) {
            for (i in 0 until q.digits) {
                val active = state.activeSlot == i && !state.lastAnswerRight && !state.lastAnswerWrong
                val place = q.digits - 1 - i
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    PlaceValueSlot(
                        digit = state.entered.getOrNull(i),
                        placeIndex = place,
                        active = active,
                        right = state.lastAnswerRight,
                        wrong = state.lastAnswerWrong,
                        s = s,
                        onTap = { onTap(i) }
                    )
                    Text(
                        PLACE_LABELS.getOrElse(place) { "" },
                        color = colors.toyColor(place).abDarkened(0.15f),
                        style = AbTypography.fixed(14f * s, FontWeight.Black)
                    )
                }
            }
        }
        if (state.lastAnswerWrong) {
            Text(
                stringResource(R.string.place_value_answer_was, q.target),
                color = colors.danger.abReadable(4.5f),
                style = AbTypography.fixed(18f * s, FontWeight.Black),
                modifier = Modifier.padding(top = (6f * s).dp).abPopIn()
            )
        }
    }
}

// One digit slot. Empty: a dashed toy socket in its place colour (a pulsing ring while it is the active one).
// Filled: a candy block that bounces as the digit lands; green with a sparkle burst when right, red when wrong.
@Composable
private fun PlaceValueSlot(digit: Int?, placeIndex: Int, active: Boolean, right: Boolean, wrong: Boolean, s: Float, onTap: () -> Unit) {
    val colors = LocalAbColors.current
    val side = (52f * s).dp
    val edge = 4.dp
    val corner = (14f * s).dp
    val placeColor = colors.toyColor(placeIndex)
    val filledFace = when {
        right -> colors.accentGreen.abReadable(3f)
        wrong -> colors.danger.abReadable(3f)
        else -> placeColor
    }
    Box(
        modifier = Modifier
            .size(side)
            .abBounce(digit, 1.25f)
            .abBounce(right, 1.12f)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onTap() }
    ) {
        if (digit != null) {
            Box(
                modifier = Modifier.size(side).abToyTile(filledFace, corner, edge),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$digit", color = Color.White,
                    style = AbTypography.fixed(28f * s, FontWeight.Black).copy(
                        shadow = Shadow(filledFace.abDarkened(0.5f).copy(alpha = 0.5f), Offset(0f, 3f), 0f)
                    )
                )
            }
        } else {
            Box(Modifier.size(side, side - edge).placeValueSocket(placeColor, corner, if (active) 0.3f else 0.14f))
        }
        if (active) PlaceValueGlow(colors.accentYellow, corner)
        if (right) PlaceValueBurst(side)
    }
}

// A soft ring that pulses around the slot waiting for a digit.
@Composable
private fun BoxScope.PlaceValueGlow(color: Color, corner: Dp) {
    val transition = rememberInfiniteTransition(label = "pvGlow")
    val k by transition.animateFloat(
        0f, 1f, infiniteRepeatable(tween(785, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "k"
    )
    Box(
        Modifier.matchParentSize().drawBehind {
            val sw = 3.dp.toPx()
            val out = 3.dp.toPx() + 2.dp.toPx() * k
            drawRoundRect(
                color = color.copy(alpha = 0.95f - 0.45f * k),
                topLeft = Offset(-out + sw / 2f, -out + sw / 2f),
                size = Size(this.size.width + (out - sw / 2f) * 2f, this.size.height + (out - sw / 2f) * 2f),
                cornerRadius = CornerRadius(corner.toPx() + 3.dp.toPx()),
                style = Stroke(width = sw),
            )
        }
    )
}

// A ring of little stars thrown out of a slot when the number is right.
@Composable
private fun BoxScope.PlaceValueBurst(size: Dp) {
    val colors = LocalAbColors.current
    val burst = remember { Animatable(0f) }
    LaunchedEffect(Unit) { burst.animateTo(1f, tween(600, easing = LinearOutSlowInEasing)) }
    for (k in 0 until 6) {
        val angle = k * (PI / 3.0) + 0.5
        val dx = (cos(angle) * size.value * 0.8).toFloat()
        val dy = (sin(angle) * size.value * 0.8).toFloat()
        val tint = if (k % 2 == 0) colors.accentYellow else colors.accentPink
        Icon(
            Icons.Filled.Star, contentDescription = null, tint = tint,
            modifier = Modifier
                .align(Alignment.Center)
                .size(size * 0.22f)
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

// Chunky candy keys in the rainbow, like the Sudoku pad. Same taps and sounds as before.
@Composable
private fun Keypad(s: Float, onDigit: (Int) -> Unit, onBackspace: () -> Unit) {
    val colors = LocalAbColors.current
    Column(verticalArrangement = Arrangement.spacedBy((8f * s).dp), horizontalAlignment = Alignment.CenterHorizontally) {
        listOf(listOf(1, 2, 3, 4, 5), listOf(6, 7, 8, 9, 0)).forEachIndexed { rowIndex, row ->
            Row(horizontalArrangement = Arrangement.spacedBy((8f * s).dp)) {
                row.forEach { n ->
                    PlaceValueKey(face = colors.toyColor(if (n == 0) 9 else n - 1), s = s, onClick = { onDigit(n) }) {
                        Text("$n", color = Color.White,
                            style = AbTypography.fixed(44f * s * 0.55f, FontWeight.Black).copy(
                                shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f)
                            ))
                    }
                }
                if (rowIndex == 1) {
                    PlaceValueKey(face = colors.danger.abReadable(3f), s = s, onClick = { onBackspace() }) {
                        Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = null,
                            tint = Color.White, modifier = Modifier.size((44f * s * 0.45f).dp))
                    }
                }
            }
        }
    }
}

// A keypad key: a candy tile with a bold white glyph that squashes a little while it is held down.
@Composable
private fun PlaceValueKey(face: Color, s: Float, onClick: () -> Unit, content: @Composable () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val squash by animateFloatAsState(
        if (pressed) 0.9f else 1f, spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium), label = "pvKey"
    )
    val side = 44f * s
    Box(
        modifier = Modifier
            .size(side.dp)
            .graphicsLayer { scaleX = squash; scaleY = squash }
            .clickable(interactionSource = interaction, indication = null) { onClick() }
            .abToyTile(face, (side * 0.3f).dp, 4.dp),
        contentAlignment = Alignment.Center
    ) { content() }
}

@Composable
private fun ResultOverlay(state: PlaceValueUiState, best: Int, onPlayAgain: () -> Unit, onBack: () -> Unit) {
    val stars = state.starCount
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { if (stars >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        // "Candy Pop" celebration card from the shared kit.
        GameResultCard(
            title = stringResource(if (stars >= 2) R.string.balloon_great_job else R.string.good_try),
            emoji = "🏗️",
            accent = ButtonType.GREEN,
            stars = stars,
            score = stringResource(R.string.balloon_final_score) + ": ${state.score}",
            stats = {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    GameStatChip("✅", "${state.correctCount}", Color(0xFF2E7D32))
                    GameStatChip("❌", "${state.wrongCount}", Color(0xFFB71C1C))
                    GameStatChip("🏆", "$best", ORANGE)
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
