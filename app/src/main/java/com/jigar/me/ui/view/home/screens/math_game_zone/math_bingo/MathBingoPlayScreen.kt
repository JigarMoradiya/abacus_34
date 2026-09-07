package com.jigar.me.ui.view.home.screens.math_game_zone.math_bingo

import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.home.screens.math_game_zone.common.PauseTimerInBackground
import com.jigar.me.ui.view.home.screens.math_game_zone.common.NextDifficultyBanner
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.animations.ConfettiRainEffect
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameMascotPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameScoreboardPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.common.randomGameCheer
import com.jigar.me.ui.view.home.screens.math_game_zone.math_bingo.components.BINGO_SIZE
import com.jigar.me.ui.view.home.screens.math_game_zone.math_bingo.components.MathBingoUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.math_bingo.viewmodel.MathBingoPlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.getButtonColors
import kotlinx.coroutines.delay

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)
private val BLUE = Color(0xFF0074D5)
private val MARKED_GREEN = Color(0xFF66BB6A)
private val LINE_GOLD = Color(0xFFFFD54F)
private val WRONG_RED = Color(0xFFE53935)

@Composable
fun MathBingoPlayScreen(
    viewModel: MathBingoPlayViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
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
                        .clip(RoundedCornerShape(100f)).background(Color.White.copy(alpha = 0.9f))
                        .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
                ) {
                    Text("⏱", fontSize = (24f * s).sp)
                    Text(
                        "${state.timeLeft}",
                        color = if (state.timeLeft <= 10) WRONG_RED else Color(0xFF5D4037),
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 26.sp.scaled()
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
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )

                BingoCard(state, cell, s) { viewModel.tapCell(it) }

                GameScoreboardPanel(
                    score = state.score, multiplier = 1 + state.bingoCount, best = viewModel.bestScore, s = s,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }
        }

        // BINGO banner sails across when a line completes.
        if (state.justBingo) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                val pop = remember { Animatable(0.3f) }
                LaunchedEffect(Unit) { pop.animateTo(1f, spring(dampingRatio = 0.45f)) }
                Text(
                    "🎉 BINGO! 🎉",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (44f * s).sp,
                    modifier = Modifier
                        .graphicsLayer { scaleX = pop.value; scaleY = pop.value }
                        .clip(RoundedCornerShape(AppDimens.Dimens20))
                        .background(Brush.horizontalGradient(listOf(Color(0xFFFF8400), Color(0xFFFFC107))))
                        .padding(horizontal = AppDimens.Dimens30, vertical = AppDimens.Dimens16)
                )
            }
        }

        if (state.isGameOver) {
            ResultOverlay(state = state, best = viewModel.bestScore,
                onPlayAgain = { viewModel.start() }, onBack = onBackClick)
            // Two strong runs in a row? Invite the kid up a tier.
            state.suggestedDifficulty?.let { NextDifficultyBanner(it) }
        }
    }
}

@Composable
private fun CallBanner(state: MathBingoUiState, s: Float, modifier: Modifier = Modifier) {
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
            .clip(RoundedCornerShape(100f)).background(Color.White.copy(alpha = 0.95f))
            .border(2.dp, ORANGE_BORDER, RoundedCornerShape(100f))
            .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens8)
    ) {
        Text("📢", fontSize = (20f * s).sp)
        Text(
            state.call?.text ?: "",
            color = ORANGE, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 24.sp.scaled(),
            maxLines = 1, softWrap = false
        )
        Text("= ?", color = BLUE,
            fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 24.sp.scaled())
    }
}

@Composable
private fun BingoCard(state: MathBingoUiState, cell: androidx.compose.ui.unit.Dp, s: Float, onTap: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy((6f * s).dp)) {
        for (r in 0 until BINGO_SIZE) {
            Row(horizontalArrangement = Arrangement.spacedBy((6f * s).dp)) {
                for (c in 0 until BINGO_SIZE) {
                    val i = r * BINGO_SIZE + c
                    BingoCell(state, i, cell, s, onTap)
                }
            }
        }
    }
}

@Composable
private fun BingoCell(state: MathBingoUiState, i: Int, cell: androidx.compose.ui.unit.Dp, s: Float, onTap: (Int) -> Unit) {
    val value = state.card.getOrNull(i) ?: return
    val marked = state.marked.getOrNull(i) == true
    val isWrongTap = state.wrongTapIndex == i
    // Marked cells glow gold during the BINGO celebration.
    val cellBg = when {
        state.justBingo && marked -> LINE_GOLD
        marked -> MARKED_GREEN
        else -> Color.White
    }

    // Stamp animation when the cell gets marked.
    val stamp = remember { Animatable(1f) }
    LaunchedEffect(marked) {
        if (marked) {
            stamp.snapTo(1.35f)
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
            repeat(3) { shake.animateTo(6f, tween(40)); shake.animateTo(-6f, tween(40)) }
            shake.animateTo(0f, tween(35))
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .graphicsLayer {
                scaleX = deal.value * stamp.value
                scaleY = deal.value * stamp.value
                translationX = shake.value
            }
            .size(cell)
            .clip(RoundedCornerShape(AppDimens.Dimens10))
            .background(cellBg)
            .border(
                2.dp,
                if (marked) Color(0xFF2E7D32) else ORANGE_BORDER,
                RoundedCornerShape(AppDimens.Dimens10)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = !marked
            ) { onTap(i) }
    ) {
        if (marked) {
            Text("⭐", fontSize = (22f * s).sp)
        } else {
            Text(
                "$value", color = Color(0xFF3E2723), maxLines = 1, softWrap = false,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                fontSize = ((if (value >= 100) 15f else 20f) * s).sp
            )
        }
    }
}

@Composable
private fun ResultOverlay(state: MathBingoUiState, best: Int, onPlayAgain: () -> Unit, onBack: () -> Unit) {
    val stars = state.starCount
    val accentColors = getButtonColors(ButtonType.ORANGE)
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { if (stars >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        ConfettiRainEffect()
        // "Candy Pop" celebration card — saturated gradient block with a white badge overlapping the top edge.
        Box(modifier = Modifier.fillMaxWidth(0.6f), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp, RoundedCornerShape(AppDimens.Dimens20 * 1.2f), ambientColor = accentColors.base, spotColor = accentColors.base)
                    .background(accentColors.gradient, RoundedCornerShape(AppDimens.Dimens20 * 1.2f))
                    .padding(horizontal = AppDimens.Dimens30, vertical = AppDimens.Dimens20),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
            ) {
                Spacer(Modifier.height(AppDimens.Dimens20))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎱", fontSize = (28f * gameScale()).sp)
                    Text(
                        stringResource(if (stars >= 2) R.string.balloon_great_job else R.string.good_try),
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                        fontSize = 30.sp.scaled(),
                        style = TextStyle(shadow = Shadow(color = accentColors.base.copy(alpha = 0.9f), offset = Offset(1.5f, 1.5f), blurRadius = 0f)),
                        modifier = Modifier.padding(horizontal = AppDimens.Dimens8)
                    )
                    Text("✨", fontSize = (28f * gameScale()).sp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8), verticalAlignment = Alignment.Bottom) {
                    BigStar(stars >= 1, 32, 150); BigStar(stars >= 2, 40, 400); BigStar(stars >= 3, 32, 650)
                }
                Text(stringResource(R.string.balloon_final_score) + ": ${state.score}", color = Color.White,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 26.sp.scaled())
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16)) {
                    StatChipStr("🎱", "${state.bingoCount}", Color(0xFF7B1FA2))
                    StatChipStr("✓", "${state.correctCount}", Color(0xFF2E7D32))
                    StatChipStr("🏆", "$best", ORANGE_BORDER)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    KidsActionButton(text = stringResource(R.string.balloon_play_again), type = ButtonType.POSITIVE,
                        onClick = { if (enabled) onPlayAgain() })
                    KidsActionButton(text = stringResource(R.string.balloon_back), type = ButtonType.NEGATIVE,
                        onClick = { if (enabled) onBack() })
                }
            }

            Box(
                modifier = Modifier
                    .offset(y = (-28).dp)
                    .size(64.dp)
                    .shadow(8.dp, CircleShape)
                    .background(Color.White, CircleShape)
                    .border(4.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🎱", fontSize = 32.sp)
            }
        }
    }
}

@Composable
private fun StatChipStr(emoji: String, value: String, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4), verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clip(RoundedCornerShape(100f)).background(Color.White)
            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6)) {
        Text(emoji, color = color, fontSize = (16f * gameScale()).sp,
            fontFamily = FontFamily(Font(R.font.font_extra_bold)))
        Text(value, color = color, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 20.sp.scaled(),
            maxLines = 1, softWrap = false)
    }
}

@Composable
private fun BigStar(earned: Boolean, sizeSp: Int, delayMs: Long) {
    val scale = remember { Animatable(0.01f) }
    LaunchedEffect(Unit) { delay(delayMs); scale.animateTo(1f, spring(dampingRatio = 0.5f)) }
    Text("⭐", fontSize = (sizeSp * gameScale()).sp,
        modifier = Modifier.graphicsLayer { scaleX = scale.value; scaleY = scale.value; alpha = if (earned) 1f else 0.35f })
}
