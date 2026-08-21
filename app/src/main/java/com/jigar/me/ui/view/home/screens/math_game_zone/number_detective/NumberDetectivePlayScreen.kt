package com.jigar.me.ui.view.home.screens.math_game_zone.number_detective

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
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
import androidx.compose.ui.res.stringResource
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
import com.jigar.me.ui.view.home.screens.math_game_zone.number_detective.components.NumberDetectiveUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.number_detective.viewmodel.NumberDetectivePlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay

private val INDIGO = Color(0xFF283593)
private val INDIGO_BORDER = Color(0xFF3F51B5)
private val BLUE = Color(0xFF0074D5)
private val CHIP_TEAL = Color(0xFF00897B)
private val CHIP_TEAL_DARK = Color(0xFF004D40)
private val KEY_BG = Color(0xFF283593)
private val FALSE_RED = Color(0xFFE53935)

@Composable
fun NumberDetectivePlayScreen(
    viewModel: NumberDetectivePlayViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val baseCheer = remember { randomGameCheer() }
    val s = gameScale()
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
                Text(
                    text = stringResource(R.string.number_detective_prompt, state.question?.minRange ?: 1, state.question?.maxRange ?: 20),
                    color = INDIGO, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 18.sp.scaled(),
                    modifier = Modifier.align(Alignment.Center)
                        .clip(RoundedCornerShape(100f)).background(Color.White.copy(alpha = 0.9f))
                        .border(2.dp, INDIGO_BORDER, RoundedCornerShape(100f))
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
                        color = if (state.timeLeft <= 10) FALSE_RED else Color(0xFF5D4037),
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
                        state.lastAnswerRight -> "You cracked it! 🕵️"
                        state.lastAnswerWrong -> "Missed it — new number! 🔍"
                        state.feedbackDirection > 0 -> "Higher! ⬆️"
                        state.feedbackDirection < 0 -> "Lower! ⬇️"
                        else -> baseCheer
                    },
                    celebrate = state.lastAnswerRight, s = s,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    TriesTrail(state, s)
                    Spacer(Modifier.height((14f * s).dp))
                    GuessDisplay(state, s)
                    Spacer(Modifier.height((18f * s).dp))
                    Keypad(s,
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
    }
}

// Little magnifying-glass dots — one per try, filled in as they're used.
@Composable
private fun TriesTrail(state: NumberDetectiveUiState, s: Float) {
    Row(horizontalArrangement = Arrangement.spacedBy((6f * s).dp), verticalAlignment = Alignment.CenterVertically) {
        for (i in 0 until state.maxTries) {
            val used = i < state.triesUsed
            Box(
                modifier = Modifier
                    .size((14f * s).dp)
                    .clip(CircleShape)
                    .background(if (used) CHIP_TEAL_DARK else Color.White.copy(alpha = 0.6f))
                    .border(1.dp, CHIP_TEAL_DARK, CircleShape)
            )
        }
    }
}

// The number being typed, plus the last Too High / Too Low nudge.
@Composable
private fun GuessDisplay(state: NumberDetectiveUiState, s: Float) {
    val shake = remember { Animatable(0f) }
    LaunchedEffect(state.feedbackDirection) {
        if (state.feedbackDirection != 0) {
            repeat(3) { shake.animateTo(8f, tween(45)); shake.animateTo(-8f, tween(45)) }
            shake.animateTo(0f, tween(40))
        }
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .graphicsLayer { translationX = shake.value }
                .width((160f * s).dp).height((56f * s).dp)
                .clip(RoundedCornerShape(AppDimens.Dimens10))
                .background(if (state.lastAnswerRight) Color(0xFF81C784) else CHIP_TEAL)
                .border(3.dp, if (state.lastAnswerRight) Color(0xFF2E7D32) else CHIP_TEAL_DARK, RoundedCornerShape(AppDimens.Dimens10))
        ) {
            Text(
                state.enteredDigits.ifEmpty { "?" },
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (30f * s).sp
            )
        }
        Spacer(Modifier.height((8f * s).dp))
        val hint = when {
            state.lastAnswerRight -> "🎉 " + stringResource(R.string.number_detective_correct)
            state.lastAnswerWrong -> stringResource(R.string.number_detective_answer_was, state.question?.secret ?: 0)
            state.feedbackDirection > 0 -> "⬆️ " + stringResource(R.string.number_detective_too_low)
            state.feedbackDirection < 0 -> "⬇️ " + stringResource(R.string.number_detective_too_high)
            else -> stringResource(R.string.number_detective_tries_left, state.triesLeft)
        }
        val hintColor = when {
            state.lastAnswerRight -> Color(0xFF2E7D32)
            state.lastAnswerWrong -> Color(0xFFB71C1C)
            else -> Color(0xFF5D4037)
        }
        Text(hint, color = hintColor, fontFamily = FontFamily(Font(R.font.font_bold)), fontSize = (15f * s).sp)
    }
}

@Composable
private fun Keypad(s: Float, onDigit: (Int) -> Unit, onBackspace: () -> Unit, onSubmit: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy((8f * s).dp), horizontalAlignment = Alignment.CenterHorizontally) {
        listOf(listOf(1, 2, 3, 4, 5), listOf(6, 7, 8, 9, 0)).forEachIndexed { rowIndex, row ->
            Row(horizontalArrangement = Arrangement.spacedBy((8f * s).dp)) {
                row.forEach { n ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size((44f * s).dp)
                            .clip(RoundedCornerShape(AppDimens.Dimens10))
                            .background(KEY_BG)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onDigit(n) }
                    ) {
                        Text("$n", color = Color.White,
                            fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (22f * s).sp)
                    }
                }
                if (rowIndex == 1) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size((44f * s).dp)
                            .clip(RoundedCornerShape(AppDimens.Dimens10))
                            .background(Color(0xFF90A4AE))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onBackspace() }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = null,
                            tint = Color.White, modifier = Modifier.size((20f * s).dp))
                    }
                }
            }
        }
        KidsActionButton(
            text = stringResource(R.string.number_detective_guess),
            type = ButtonType.ORANGE, isSmall = true, onClick = onSubmit
        )
    }
}

@Composable
private fun ResultOverlay(state: NumberDetectiveUiState, best: Int, onPlayAgain: () -> Unit, onBack: () -> Unit) {
    val stars = state.starCount
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { if (stars >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        ConfettiRainEffect()
        Column(
            modifier = Modifier.fillMaxWidth(0.6f).clip(RoundedCornerShape(AppDimens.Dimens20))
                .border(3.dp, INDIGO_BORDER, RoundedCornerShape(AppDimens.Dimens20)).background(Color.White)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().height((64f * gameScale()).dp)
                    .background(Brush.horizontalGradient(listOf(Color(0xFF3F51B5), Color(0xFF00897B)))),
                contentAlignment = Alignment.Center
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = AppDimens.Dimens20),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("🕵️", fontSize = (34f * gameScale()).sp)
                    Text(stringResource(if (stars >= 2) R.string.balloon_great_job else R.string.good_try),
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 32.sp.scaled())
                    Text("🔍", fontSize = (34f * gameScale()).sp)
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = AppDimens.Dimens30, vertical = AppDimens.Dimens16),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16), verticalAlignment = Alignment.Bottom) {
                    BigStar(stars >= 1, 44, 150); BigStar(stars >= 2, 58, 400); BigStar(stars >= 3, 44, 650)
                }
                Text(stringResource(R.string.balloon_final_score) + ": ${state.score}", color = BLUE,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 28.sp.scaled())
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16)) {
                    StatChipStr("✓", "${state.correctCount}", Color(0xFF2E7D32))
                    StatChipStr("✗", "${state.wrongCount}", Color(0xFFB71C1C))
                    StatChipStr("🏆", "$best", INDIGO_BORDER)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    KidsActionButton(text = stringResource(R.string.balloon_play_again), type = ButtonType.ORANGE,
                        onClick = { if (enabled) onPlayAgain() })
                    KidsActionButton(text = stringResource(R.string.balloon_back), type = ButtonType.BLUE,
                        onClick = { if (enabled) onBack() })
                }
            }
        }
    }
}

@Composable
private fun StatChipStr(emoji: String, value: String, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4), verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clip(RoundedCornerShape(100f)).background(color.copy(alpha = 0.12f))
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
