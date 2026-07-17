package com.jigar.me.ui.view.home.screens.math_game_zone.missing_operator

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.animations.ConfettiRainEffect
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.math_game_zone.missing_operator.components.MathOperator
import com.jigar.me.ui.view.home.screens.math_game_zone.missing_operator.viewmodel.MissingOperatorPlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)
private val BLUE = Color(0xFF0074D5)
private val GREEN = Color(0xFF2E7D32)
private val RED = Color(0xFFD32F2F)

private val moScale: Float
    get() = if (DeviceInfo.isLargeTablet) 1.7f else if (DeviceInfo.isTablet) 1.45f else 1.0f

@Composable
fun MissingOperatorPlayScreen(
    viewModel: MissingOperatorPlayViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val s = moScale
    LaunchedEffect(Unit) { viewModel.start() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BackButtonWithText(title = str(R.string.missing_operator_game), onBackClick = onBackClick)
                    Spacer(Modifier.weight(1f))
                    Hud(viewModel.config.timerSeconds, viewModel.config.roundsGoal, state.timeLeft, state.roundsPlayed, state.score, viewModel.multiplier)
                }
                Text(
                    text = str(R.string.missing_op_which_sign),
                    color = ORANGE, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 20.sp.scaled(),
                    modifier = Modifier.align(Alignment.Center)
                        .clip(RoundedCornerShape(100f)).background(Color.White.copy(alpha = 0.9f))
                        .border(2.dp, ORANGE_BORDER, RoundedCornerShape(100f))
                        .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens8)
                )
            }

            Spacer(Modifier.weight(1f))

            EquationCard(state, s)

            Spacer(Modifier.height(AppDimens.Dimens16))

            Text(
                text = if (state.revealed) (if (state.lastCorrect) "✅" else "❌") else " ",
                fontSize = (40f * s).sp
            )

            Spacer(Modifier.height(AppDimens.Dimens16))

            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                state.round.options.forEach { op ->
                    OperatorButton(op.symbol, opButtonState(op, state.revealed, state.round.answer, state.chosen), s) {
                        viewModel.answer(op)
                    }
                }
            }

            Spacer(Modifier.weight(1f))
        }

        if (state.isGameOver) {
            ResultOverlay(state.score, state.correctCount, state.wrongCount, viewModel.bestScore,
                onPlayAgain = { viewModel.start() }, onBack = onBackClick)
        }
    }
}

private enum class OpBtnState { IDLE, CORRECT, WRONG, DIMMED }
private fun opButtonState(op: MathOperator, revealed: Boolean, answer: MathOperator, chosen: MathOperator?): OpBtnState {
    if (!revealed) return OpBtnState.IDLE
    if (op == answer) return OpBtnState.CORRECT
    if (op == chosen) return OpBtnState.WRONG
    return OpBtnState.DIMMED
}

@Composable private fun str(id: Int): String = androidx.compose.ui.res.stringResource(id)

@Composable
private fun EquationCard(state: com.jigar.me.ui.view.home.screens.math_game_zone.missing_operator.components.MissingOperatorUiState, s: Float) {
    val borderColor = if (state.revealed && state.lastCorrect) GREEN else ORANGE_BORDER
    val cardScale by animateFloatAsState(
        targetValue = if (state.revealed && state.lastCorrect) 1.06f else 1f,
        animationSpec = spring(dampingRatio = 0.5f), label = ""
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .graphicsLayer { scaleX = cardScale; scaleY = cardScale }
            .clip(RoundedCornerShape(AppDimens.Dimens20))
            .background(Color.White.copy(alpha = 0.9f))
            .border(3.dp, borderColor, RoundedCornerShape(AppDimens.Dimens20))
            .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens12)
    ) {
        Token("${state.round.left}", s)
        Slot(state, s)
        Token("${state.round.right}", s)
        Token("=", s)
        Token("${state.round.result}", s)
    }
}

@Composable
private fun Token(text: String, s: Float) {
    Text(text, color = Color.Black, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (44f * s).sp)
}

@Composable
private fun Slot(state: com.jigar.me.ui.view.home.screens.math_game_zone.missing_operator.components.MissingOperatorUiState, s: Float) {
    val color = when {
        !state.revealed -> BLUE
        state.lastCorrect -> GREEN
        else -> RED
    }
    val bump by animateFloatAsState(if (state.chosen != null) 1.15f else 1f, spring(dampingRatio = 0.45f), label = "")
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .graphicsLayer { scaleX = bump; scaleY = bump }
            .size((62f * s).dp)
            .clip(RoundedCornerShape(AppDimens.Dimens12))
            .background(color.copy(alpha = 0.12f))
            .border(2.5.dp, color, RoundedCornerShape(AppDimens.Dimens12))
    ) {
        Text(state.chosen?.symbol ?: "?", color = color,
            fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (44f * s).sp)
    }
}

@Composable
private fun OperatorButton(symbol: String, state: OpBtnState, s: Float, onClick: () -> Unit) {
    val fill = when (state) {
        OpBtnState.IDLE -> BLUE
        OpBtnState.CORRECT -> GREEN
        OpBtnState.WRONG -> RED
        OpBtnState.DIMMED -> Color(0xFFB0BEC5)
    }
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (pressed) 0.92f else if (state == OpBtnState.CORRECT) 1.08f else 1f, spring(dampingRatio = 0.5f), label = ""
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
            .size((84f * s).dp, (80f * s).dp)
            .clip(RoundedCornerShape(AppDimens.Dimens20)).background(fill)
            .clickable(interactionSource = interaction, indication = null) { onClick() }
    ) {
        Text(symbol, color = Color.White, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (44f * s).sp)
    }
}

@Composable
private fun Hud(timerSeconds: Int?, roundsGoal: Int?, timeLeft: Int, roundsPlayed: Int, score: Int, multiplier: Int) {
    val font = FontFamily(Font(R.font.font_bold))
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16), verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = AppDimens.Dimens12).clip(RoundedCornerShape(AppDimens.Dimens12))
            .background(Color.White.copy(alpha = 0.75f)).padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6)
    ) {
        if (timerSeconds != null) Text("⏱ $timeLeft", color = Color.Black, fontFamily = font, fontSize = 16.sp.scaled())
        else if (roundsGoal != null) Text("🎯 $roundsPlayed/$roundsGoal", color = Color.Black, fontFamily = font, fontSize = 16.sp.scaled())
        Text("⭐ $score", color = Color.Black, fontFamily = font, fontSize = 16.sp.scaled())
        if (multiplier > 1) Text("🔥 x$multiplier", color = ORANGE, fontFamily = font, fontSize = 16.sp.scaled())
    }
}

@Composable
private fun ResultOverlay(score: Int, correctCount: Int, wrongCount: Int, bestScore: Int, onPlayAgain: () -> Unit, onBack: () -> Unit) {
    val attempts = correctCount + wrongCount
    val accuracy = if (attempts > 0) correctCount.toFloat() / attempts else 0f
    val starCount = when { accuracy >= 0.9f -> 3; accuracy >= 0.7f -> 2; else -> 1 }
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        ConfettiRainEffect()
        Column(
            modifier = Modifier.fillMaxWidth(0.6f).clip(RoundedCornerShape(AppDimens.Dimens20))
                .border(3.dp, ORANGE_BORDER, RoundedCornerShape(AppDimens.Dimens20)).background(Color.White)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().height(64.dp)
                    .background(Brush.horizontalGradient(listOf(Color(0xFFFF8400), Color(0xFFFFC107)))),
                contentAlignment = Alignment.Center
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = AppDimens.Dimens20),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("➗", fontSize = 34.sp)
                    Text(str(R.string.balloon_great_job), color = Color.White,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 32.sp.scaled())
                    Text("✖️", fontSize = 34.sp)
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
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
                    StatChip("🎯", correctCount, GREEN); StatChip("❌", wrongCount, RED); StatChip("🏆", bestScore, ORANGE_BORDER)
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
        Text(emoji, fontSize = 16.sp)
        Text("$value", color = color, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 20.sp.scaled())
    }
}

@Composable
private fun BigStar(earned: Boolean, sizeSp: Int, delayMs: Long) {
    val scale = remember { Animatable(0.01f) }
    LaunchedEffect(Unit) { delay(delayMs); scale.animateTo(1f, spring(dampingRatio = 0.5f)) }
    Text("⭐", fontSize = sizeSp.sp, modifier = Modifier.graphicsLayer { scaleX = scale.value; scaleY = scale.value; alpha = if (earned) 1f else 0.35f })
}
