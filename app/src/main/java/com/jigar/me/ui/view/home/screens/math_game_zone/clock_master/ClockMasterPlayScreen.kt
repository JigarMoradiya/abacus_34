package com.jigar.me.ui.view.home.screens.math_game_zone.clock_master

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
import androidx.compose.foundation.layout.width
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
import com.jigar.me.ui.view.home.screens.math_game_zone.clock_master.components.ClockMasterUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.clock_master.viewmodel.ClockMasterPlayViewModel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameMascotPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameScoreboardPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.common.randomGameCheer
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.getButtonColors
import kotlinx.coroutines.delay

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)
private val BLUE = Color(0xFF0074D5)
private val RIGHT_GREEN = Color(0xFF43A047)
private val WRONG_RED = Color(0xFFE53935)

@Composable
fun ClockMasterPlayScreen(
    viewModel: ClockMasterPlayViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
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
                    BackButtonWithText(title = stringResource(R.string.clock_master_game), onBackClick = onBackClick)
                    Spacer(Modifier.weight(1f))
                }
                Text(
                    text = stringResource(R.string.clock_master_prompt),
                    color = ORANGE, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 20.sp.scaled(),
                    modifier = Modifier.align(Alignment.Center)
                        .clip(RoundedCornerShape(100f)).background(Color.White.copy(alpha = 0.9f))
                        .border(2.dp, ORANGE_BORDER, RoundedCornerShape(100f))
                        .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens8)
                )
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
                        state.lastAnswerRight && state.streak >= 3 -> "On fire! 🔥"
                        state.lastAnswerWrong -> "Look at both hands! 🕐"
                        else -> baseCheer
                    },
                    celebrate = state.lastAnswerRight, s = s,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )

                Row(
                    modifier = Modifier.fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    ClockFace(
                        hour = state.question?.hour ?: 3,
                        minute = state.question?.minute ?: 0,
                        questionKey = state.questionNumber,
                        diameter = (170f * s).dp
                    )

                    Spacer(Modifier.width((28f * s).dp))

                    OptionsColumn(state, s) { viewModel.answer(it) }
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
    }
}

// 2x2 digital-time options; right answer flashes green, wrong tap shakes red
// while the correct one glows.
@Composable
private fun OptionsColumn(state: ClockMasterUiState, s: Float, onTap: (Int) -> Unit) {
    val q = state.question ?: return
    val shake = remember { Animatable(0f) }
    LaunchedEffect(state.lastAnswerWrong) {
        if (state.lastAnswerWrong) {
            repeat(3) { shake.animateTo(7f, tween(45)); shake.animateTo(-7f, tween(45)) }
            shake.animateTo(0f, tween(40))
        }
    }
    Column(
        verticalArrangement = Arrangement.spacedBy((12f * s).dp),
        modifier = Modifier.graphicsLayer { translationX = shake.value }
    ) {
        q.options.chunked(2).forEach { rowOptions ->
            Row(horizontalArrangement = Arrangement.spacedBy((12f * s).dp)) {
                rowOptions.forEach { option ->
                    val index = q.options.indexOf(option)
                    val revealCorrect = (state.lastAnswerRight || state.lastAnswerWrong) && index == q.correctIndex
                    val tappedWrong = state.lastAnswerWrong && index == state.selectedIndex
                    val enter = remember(state.questionNumber, index) { Animatable(0.01f) }
                    LaunchedEffect(state.questionNumber) {
                        enter.snapTo(0.01f)
                        delay(70L * index)
                        enter.animateTo(1f, spring(dampingRatio = 0.5f))
                    }
                    val bg = when {
                        revealCorrect -> RIGHT_GREEN
                        tappedWrong -> WRONG_RED
                        else -> Color.White
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .graphicsLayer { scaleX = enter.value; scaleY = enter.value }
                            .clip(RoundedCornerShape(AppDimens.Dimens12))
                            .background(bg)
                            .border(2.5.dp, if (revealCorrect) Color(0xFF2E7D32) else ORANGE_BORDER,
                                RoundedCornerShape(AppDimens.Dimens12))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onTap(index) }
                            .padding(horizontal = (24f * s).dp, vertical = (14f * s).dp)
                    ) {
                        Text(
                            option,
                            color = if (revealCorrect || tappedWrong) Color.White else Color(0xFF3E2723),
                            fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (26f * s).sp,
                            maxLines = 1, softWrap = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultOverlay(state: ClockMasterUiState, best: Int, onPlayAgain: () -> Unit, onBack: () -> Unit) {
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
                    Text("🕐", fontSize = (28f * gameScale()).sp)
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
                    StatChipStr("✓", "${state.correctCount}", Color(0xFF2E7D32))
                    StatChipStr("✗", "${state.wrongCount}", Color(0xFFB71C1C))
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
                Text("🕐", fontSize = 32.sp)
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
