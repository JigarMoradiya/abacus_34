package com.jigar.me.ui.view.home.screens.math_game_zone.equation_match

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.animations.ConfettiRainEffect
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameMascotPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.randomGameCheer
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameScoreboardPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.equation_match.components.EqMatchCard
import com.jigar.me.ui.view.home.screens.math_game_zone.equation_match.components.EqMatchCardState
import com.jigar.me.ui.view.home.screens.math_game_zone.equation_match.components.EqMatchPalette
import com.jigar.me.ui.view.home.screens.math_game_zone.equation_match.viewmodel.EquationMatchPlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)
private val BLUE = Color(0xFF0074D5)
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
                    color = ORANGE, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 20.sp.scaled(),
                    modifier = Modifier.align(Alignment.Center)
                        .clip(RoundedCornerShape(100f)).background(Color.White.copy(alpha = 0.9f))
                        .border(2.dp, ORANGE_BORDER, RoundedCornerShape(100f))
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
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )

                BoardView(state.cards, config.cols, config.rows, cell, gap) { viewModel.tap(it) }

                GameScoreboardPanel(state.score, state.multiplier, viewModel.bestScore, s, Modifier.weight(1f).fillMaxHeight())
            }
        }

        if (state.isGameOver) {
            ResultOverlay(state.score, viewModel.starCount(), state.matchesFound, viewModel.bestScore,
                onPlayAgain = { viewModel.start() }, onBack = onBackClick)
        }
    }
}

@Composable
private fun BoardView(cards: List<EqMatchCard>, cols: Int, rows: Int, cell: Dp, gap: Dp, onTap: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(gap)) {
        for (r in 0 until rows) {
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                for (c in 0 until cols) {
                    val i = r * cols + c
                    if (i < cards.size) FlipCard(cards[i], cell) { onTap(i) }
                    else Spacer(Modifier.size(cell))
                }
            }
        }
    }
}

@Composable
private fun FlipCard(card: EqMatchCard, size: Dp, onTap: () -> Unit) {
    val isUp = card.state != EqMatchCardState.DOWN
    val matched = card.state == EqMatchCardState.MATCHED
    val rotation by animateFloatAsState(if (isUp) 180f else 0f, tween(350), label = "flip")
    val pop by animateFloatAsState(if (matched) 1.06f else 1f, spring(dampingRatio = 0.5f), label = "pop")

    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
                scaleX = pop; scaleY = pop
            }
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onTap() },
        contentAlignment = Alignment.Center
    ) {
        if (rotation < 90f) {
            CardBack(size)
        } else {
            Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) { CardFront(card, size, matched) }
        }
    }
}

@Composable
private fun CardBack(size: Dp) {
    Box(
        modifier = Modifier.size(size).clip(RoundedCornerShape(size * 0.18f))
            .background(Brush.linearGradient(listOf(EqMatchPalette.backTop, EqMatchPalette.backBottom)))
            .border(2.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(size * 0.18f)),
        contentAlignment = Alignment.Center
    ) {
        Text("?", color = Color.White.copy(alpha = 0.85f),
            fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (size.value * 0.44f).sp)
    }
}

@Composable
private fun CardFront(card: EqMatchCard, size: Dp, matched: Boolean) {
    // Neutral while just flipped; once matched, the pair takes its own color + a ✓ badge.
    val face = EqMatchPalette.face(card.pairId)
    val ink = Color(0xFF2B2F36)
    val len = card.text.length
    val frac = when {
        len <= 2 -> 0.38f
        len == 3 -> 0.34f
        len == 4 -> 0.30f
        len == 5 -> 0.26f
        else -> 0.22f
    }
    Box(
        modifier = Modifier.size(size).clip(RoundedCornerShape(size * 0.18f))
            .background(if (matched) face else Color.White)
            .border(if (matched) 3.dp else 2.dp, if (matched) face else Color.Black.copy(alpha = 0.12f),
                RoundedCornerShape(size * 0.18f)),
        contentAlignment = Alignment.Center
    ) {
        Text(card.text, color = if (matched) Color.White else ink,
            fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (size.value * frac).sp,
            maxLines = 1, softWrap = false)
        if (matched) {
            Icon(
                imageVector = Icons.Filled.CheckCircle, contentDescription = null, tint = Color.White,
                modifier = Modifier.align(Alignment.TopEnd).padding(size * 0.05f).size(size * 0.20f)
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
        if (starCount >= 2) ConfettiRainEffect()
        Column(
            modifier = Modifier.fillMaxWidth(0.6f).clip(RoundedCornerShape(AppDimens.Dimens20))
                .border(3.dp, ORANGE_BORDER, RoundedCornerShape(AppDimens.Dimens20)).background(Color.White)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().height((64f * gameScale()).dp)
                    .background(Brush.horizontalGradient(listOf(Color(0xFFFF8400), Color(0xFFFFC107)))),
                contentAlignment = Alignment.Center
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = AppDimens.Dimens20),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("🧮", fontSize = (34f * gameScale()).sp)
                    Text(str(if (starCount >= 2) R.string.balloon_great_job else R.string.good_try), color = Color.White,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 32.sp.scaled())
                    Text("✨", fontSize = (34f * gameScale()).sp)
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = AppDimens.Dimens30, vertical = AppDimens.Dimens16),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16), verticalAlignment = Alignment.Bottom) {
                    BigStar(starCount >= 1, 44, 150); BigStar(starCount >= 2, 58, 400); BigStar(starCount >= 3, 44, 650)
                }
                Text(str(R.string.balloon_final_score) + ": $score", color = BLUE,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 28.sp.scaled())
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16)) {
                    StatChip("✅", matches, GREEN); StatChip("🏆", best, ORANGE_BORDER)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    KidsActionButton(text = str(R.string.balloon_play_again), type = ButtonType.ORANGE, onClick = { if (enabled) onPlayAgain() })
                    KidsActionButton(text = str(R.string.balloon_back), type = ButtonType.BLUE, onClick = { if (enabled) onBack() })
                }
            }
        }
    }
}

@Composable
private fun StatChip(emoji: String, value: Int, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4), verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clip(RoundedCornerShape(100f)).background(color.copy(alpha = 0.12f))
            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6)) {
        Text(emoji, fontSize = (16f * gameScale()).sp)
        Text("$value", color = color, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 20.sp.scaled())
    }
}

@Composable
private fun BigStar(earned: Boolean, sizeSp: Int, delayMs: Long) {
    val scale = remember { Animatable(0.01f) }
    LaunchedEffect(Unit) { delay(delayMs); scale.animateTo(1f, spring(dampingRatio = 0.5f)) }
    Text("⭐", fontSize = (sizeSp * gameScale()).sp,
        modifier = Modifier.graphicsLayer { scaleX = scale.value; scaleY = scale.value; alpha = if (earned) 1f else 0.35f })
}
