package com.jigar.me.ui.view.home.screens.math_game_zone.true_false

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
import androidx.compose.foundation.layout.windowInsetsPadding
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
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.animations.ConfettiRainEffect
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameMascotPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameScoreboardPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.common.randomGameCheer
import com.jigar.me.ui.view.home.screens.math_game_zone.true_false.components.TrueFalseUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.true_false.viewmodel.TrueFalsePlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)
private val BLUE = Color(0xFF0074D5)
private val TRUE_GREEN = Color(0xFF43A047)
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
                    color = ORANGE, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 20.sp.scaled(),
                    modifier = Modifier.align(Alignment.Center)
                        .clip(RoundedCornerShape(100f)).background(Color.White.copy(alpha = 0.9f))
                        .border(2.dp, ORANGE_BORDER, RoundedCornerShape(100f))
                        .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens8)
                )
                // Countdown — turns red for the final 10 seconds.
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
                        state.lastAnswerRight && state.streak >= 3 -> "On fire! 🔥"
                        state.lastAnswerWrong -> "Oops! Look close 👀"
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
                    QuestionCard(state, s)

                    Spacer(Modifier.height((24f * s).dp))

                    Row(horizontalArrangement = Arrangement.spacedBy((24f * s).dp)) {
                        AnswerButton("✓", stringResource(R.string.true_false_btn_true), TRUE_GREEN, TRUE_GREEN_DARK, s) {
                            viewModel.answer(true)
                        }
                        AnswerButton("✗", stringResource(R.string.true_false_btn_false), FALSE_RED, FALSE_RED_DARK, s) {
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
        }
    }
}

// The statement card slides in with a spring for every new question, flashes
// green on a right answer and shakes red (revealing the truth) on a wrong one.
@Composable
private fun QuestionCard(state: TrueFalseUiState, s: Float) {
    val enter = remember { Animatable(0.6f) }
    LaunchedEffect(state.questionNumber) {
        enter.snapTo(0.6f)
        enter.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = 500f))
    }
    val shake = remember { Animatable(0f) }
    LaunchedEffect(state.lastAnswerWrong) {
        if (state.lastAnswerWrong) {
            repeat(3) { shake.animateTo(8f, tween(45)); shake.animateTo(-8f, tween(45)) }
            shake.animateTo(0f, tween(40))
        }
    }
    val bg = when {
        state.lastAnswerRight -> Color(0xFFC8E6C9)
        state.lastAnswerWrong -> Color(0xFFFFCDD2)
        else -> Color.White
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .graphicsLayer { scaleX = enter.value; scaleY = enter.value; translationX = shake.value }
            .clip(RoundedCornerShape(AppDimens.Dimens16))
            .background(bg)
            .border(3.dp, ORANGE_BORDER, RoundedCornerShape(AppDimens.Dimens16))
            .padding(horizontal = (36f * s).dp, vertical = (24f * s).dp)
    ) {
        Text(
            state.question?.text ?: "",
            color = Color(0xFF3E2723),
            fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (40f * s).sp,
            maxLines = 1, softWrap = false
        )
        if (state.lastAnswerWrong) {
            Text(
                stringResource(R.string.true_false_answer_was, state.question?.correctAnswer ?: 0),
                color = FALSE_RED_DARK,
                fontFamily = FontFamily(Font(R.font.font_bold)), fontSize = (18f * s).sp
            )
        }
    }
}

// Big smashable TRUE/FALSE button with press-bounce.
@Composable
private fun AnswerButton(mark: String, label: String, bg: Color, border: Color, s: Float, onClick: () -> Unit) {
    val press = remember { Animatable(1f) }
    var pressTrigger by remember { mutableStateOf(0) }
    LaunchedEffect(pressTrigger) {
        if (pressTrigger > 0) {
            press.snapTo(0.85f)
            press.animateTo(1f, spring(dampingRatio = 0.4f))
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
            .graphicsLayer { scaleX = press.value; scaleY = press.value }
            .clip(RoundedCornerShape(AppDimens.Dimens16))
            .background(bg)
            .border(3.dp, border, RoundedCornerShape(AppDimens.Dimens16))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { pressTrigger++; onClick() }
            .padding(horizontal = (34f * s).dp, vertical = (14f * s).dp)
    ) {
        Text(mark, color = Color.White,
            fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (34f * s).sp)
        Text(label, color = Color.White,
            fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (16f * s).sp)
    }
}

@Composable
private fun ResultOverlay(state: TrueFalseUiState, best: Int, onPlayAgain: () -> Unit, onBack: () -> Unit) {
    val stars = state.starCount
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { if (stars >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        ConfettiRainEffect()
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
                    Text("⚖️", fontSize = (34f * gameScale()).sp)
                    Text(stringResource(if (stars >= 2) R.string.balloon_great_job else R.string.good_try),
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 32.sp.scaled())
                    Text("✨", fontSize = (34f * gameScale()).sp)
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
                    StatChipStr("✓", "${state.correctCount}", TRUE_GREEN_DARK)
                    StatChipStr("✗", "${state.wrongCount}", FALSE_RED_DARK)
                    StatChipStr("🏆", "$best", ORANGE_BORDER)
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
