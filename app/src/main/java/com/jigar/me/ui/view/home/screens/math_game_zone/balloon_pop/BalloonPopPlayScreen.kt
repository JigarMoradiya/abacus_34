package com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop

import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.animations.ConfettiRainEffect
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop.viewmodel.BalloonPopPlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.getButtonColors
import kotlinx.coroutines.delay

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)
private val BLUE = Color(0xFF0074D5)
private val GREEN = Color(0xFF2E7D32)
private val RED = Color(0xFFD32F2F)
private val GRAY = Color(0xFF888888)

@Composable
fun BalloonPopPlayScreen(
    viewModel: BalloonPopPlayViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.start() }

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {

        Column(modifier = Modifier.fillMaxSize()) {

            // Header: back button left, target card dead-centre, HUD right.
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BackButtonWithText(title = stringResourceSafe(), onBackClick = onBackClick)
                    Spacer(Modifier.weight(1f))
                    Hud(
                        timerSeconds = viewModel.config.timerSeconds,
                        popsGoal = viewModel.config.popsGoal,
                        timeLeft = state.timeLeft,
                        correctCount = state.correctCount,
                        score = state.score,
                        multiplier = viewModel.multiplier
                    )
                }
                TargetCard(
                    targetSum = viewModel.config.targetSum,
                    partner = state.partner,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Play field
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(0.dp))
            ) {
                val density = LocalDensity.current
                val widthPx = with(density) { maxWidth.toPx() }
                val heightPx = with(density) { maxHeight.toPx() }
                state.balloons.forEach { balloon ->
                    key(balloon.id) {
                        BalloonPopCell(
                            balloon = balloon,
                            containerWidthPx = widthPx,
                            containerHeightPx = heightPx,
                            isShaking = state.shakeBalloonId == balloon.id,
                            onTap = { viewModel.tap(balloon.id) },
                            onExitTop = { viewModel.balloonExitedTop(balloon.id) }
                        )
                    }
                }
            }
        }

        if (state.showNewTargetBanner) {
            NewTargetBanner(
                targetSum = viewModel.config.targetSum,
                partner = state.partner,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (state.isGameOver) {
            ResultOverlay(
                score = state.score,
                correctCount = state.correctCount,
                wrongCount = state.wrongCount,
                missedCount = state.missedCount,
                bestScore = viewModel.bestScore,
                onPlayAgain = { viewModel.start() },
                onBack = onBackClick
            )
        }
    }
}

@Composable
private fun stringResourceSafe(): String =
    androidx.compose.ui.res.stringResource(R.string.balloon_pop_game)

@Composable
private fun TargetCard(targetSum: Int, partner: Int, modifier: Modifier = Modifier) {
    Text(
        text = androidx.compose.ui.res.stringResource(R.string.balloon_make_target, targetSum, partner),
        color = ORANGE,
        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
        fontSize = 20.sp.scaled(),
        modifier = modifier
            .clip(RoundedCornerShape(100f))
            .background(Color.White.copy(alpha = 0.9f))
            .border(2.dp, ORANGE_BORDER, RoundedCornerShape(100f))
            .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
    )
}

@Composable
private fun Hud(
    timerSeconds: Int?,
    popsGoal: Int?,
    timeLeft: Int,
    correctCount: Int,
    score: Int,
    multiplier: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(end = AppDimens.Dimens12)
            .clip(RoundedCornerShape(AppDimens.Dimens12))
            .background(Color.White.copy(alpha = 0.75f))
            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6)
    ) {
        val font = FontFamily(Font(R.font.font_bold))
        if (timerSeconds != null) {
            Text("⏱ $timeLeft", color = Color.Black, fontFamily = font, fontSize = 16.sp.scaled())
        } else if (popsGoal != null) {
            Text("🎈 $correctCount/$popsGoal", color = Color.Black, fontFamily = font, fontSize = 16.sp.scaled())
        }
        Text("⭐ $score", color = Color.Black, fontFamily = font, fontSize = 16.sp.scaled())
        if (multiplier > 1) {
            Text("🔥 x$multiplier", color = ORANGE, fontFamily = font, fontSize = 16.sp.scaled())
        }
    }
}

@Composable
private fun NewTargetBanner(targetSum: Int, partner: Int, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
        modifier = modifier
            .clip(RoundedCornerShape(AppDimens.Dimens20))
            .background(Color.White)
            .border(3.dp, ORANGE_BORDER, RoundedCornerShape(AppDimens.Dimens20))
            .padding(horizontal = AppDimens.Dimens30, vertical = AppDimens.Dimens20)
    ) {
        Text("🎯", fontSize = 54.sp)
        Text(
            androidx.compose.ui.res.stringResource(R.string.balloon_new_target),
            color = BLUE, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 26.sp.scaled()
        )
        Text(
            androidx.compose.ui.res.stringResource(R.string.balloon_make_target, targetSum, partner),
            color = ORANGE, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 28.sp.scaled()
        )
    }
}

@Composable
private fun ResultOverlay(
    score: Int,
    correctCount: Int,
    wrongCount: Int,
    missedCount: Int,
    bestScore: Int,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit
) {
    val attempts = correctCount + wrongCount
    val accuracy = if (attempts > 0) correctCount.toFloat() / attempts else 0f
    val starCount = when {
        accuracy >= 0.9f && missedCount <= 2 -> 3
        accuracy >= 0.7f -> 2
        else -> 1
    }
    val accentColors = getButtonColors(ButtonType.ORANGE)

    var buttonsEnabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); buttonsEnabled = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        LaunchedEffect(Unit) { if (starCount >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        if (starCount >= 2) ConfettiRainEffect()

        // "Candy Pop" celebration card — saturated gradient block with a white badge overlapping the top edge.
        Box(
            modifier = Modifier.fillMaxWidth(0.6f),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp, RoundedCornerShape(AppDimens.Dimens20 * 1.2f), ambientColor = accentColors.base, spotColor = accentColors.base)
                    .background(accentColors.gradient, RoundedCornerShape(AppDimens.Dimens20 * 1.2f))
                    .padding(horizontal = AppDimens.Dimens30, vertical = AppDimens.Dimens20),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
            ) {
                Spacer(Modifier.height(AppDimens.Dimens20))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎈", fontSize = (28f * gameScale()).sp, modifier = Modifier.graphicsLayer { rotationZ = -14f })
                    Text(
                        androidx.compose.ui.res.stringResource(R.string.balloon_great_job),
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                        fontSize = 30.sp.scaled(),
                        style = TextStyle(shadow = Shadow(color = accentColors.base.copy(alpha = 0.9f), offset = Offset(1.5f, 1.5f), blurRadius = 0f)),
                        modifier = Modifier.padding(horizontal = AppDimens.Dimens8)
                    )
                    Text("🎈", fontSize = (28f * gameScale()).sp, modifier = Modifier.graphicsLayer { rotationZ = 14f })
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16),
                    verticalAlignment = Alignment.Bottom
                ) {
                    BigStar(earned = starCount >= 1, sizeDp = 44, delayMs = 150)
                    BigStar(earned = starCount >= 2, sizeDp = 58, delayMs = 400)
                    BigStar(earned = starCount >= 3, sizeDp = 44, delayMs = 650)
                }

                Text(
                    androidx.compose.ui.res.stringResource(R.string.balloon_final_score) + ": $score",
                    color = Color.White, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 26.sp.scaled()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
                    StatChip("🎯", correctCount, GREEN)
                    StatChip("❌", wrongCount, RED)
                    StatChip("💨", missedCount, GRAY)
                    StatChip("🏆", bestScore, ORANGE_BORDER)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    KidsActionButton(
                        text = androidx.compose.ui.res.stringResource(R.string.balloon_play_again),
                        type = ButtonType.ORANGE,
                        onClick = { if (buttonsEnabled) onPlayAgain() }
                    )
                    KidsActionButton(
                        text = androidx.compose.ui.res.stringResource(R.string.balloon_back),
                        type = ButtonType.BLUE,
                        onClick = { if (buttonsEnabled) onBack() }
                    )
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
                Text("🎈", fontSize = 32.sp)
            }
        }
    }
}

@Composable
private fun StatChip(emoji: String, value: Int, color: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(100f))
            .background(Color.White)
            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6)
    ) {
        Text(emoji, fontSize = (16f * gameScale()).sp)
        Text("$value", color = color, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 20.sp.scaled())
    }
}

@Composable
private fun BigStar(earned: Boolean, sizeDp: Int, delayMs: Long) {
    val scale = remember { Animatable(0.01f) }
    LaunchedEffect(Unit) {
        delay(delayMs)
        scale.animateTo(1f, androidx.compose.animation.core.spring(dampingRatio = 0.5f, stiffness = 300f))
    }
    Text(
        text = "⭐",
        fontSize = (sizeDp * gameScale()).sp,
        modifier = Modifier
            .graphicsLayer { scaleX = scale.value; scaleY = scale.value; alpha = if (earned) 1f else 0.35f }
    )
}
