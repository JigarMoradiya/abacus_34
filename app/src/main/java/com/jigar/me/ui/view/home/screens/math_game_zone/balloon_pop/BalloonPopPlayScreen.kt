package com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.screens.math_game_zone.GameCategoryType
import com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop.viewmodel.BalloonPopPlayViewModel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.AbDebugFlags
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameResultCard
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameStatChip
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBounce
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBreathe
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abPopIn
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTray
import com.jigar.me.ui.view.home.screens.math_game_zone.gameCardStyle
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)
private val BLUE = Color(0xFF0074D5)
private val GREEN = Color(0xFF2E7D32)
private val RED = Color(0xFFD32F2F)
private val GRAY = Color(0xFF6B6F80)

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

        // Debug builds only: render the real result overlay without playing (`--ez ab_show_result true`).
        if (AbDebugFlags.showResultEnabled && !state.isGameOver) {
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
    stringResource(R.string.balloon_pop_game)

// Springy pill in the header — bounces every time the target changes.
@Composable
private fun TargetCard(targetSum: Int, partner: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.balloon_make_target, targetSum, partner),
        color = ORANGE,
        style = AbTypography.fixed(20f, FontWeight.ExtraBold).scaled(),
        modifier = modifier
            .abBounce(partner, 1.2f)
            .abGamePill(outline = ORANGE_BORDER)
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
    val colors = LocalAbColors.current
    val style = AbTypography.fixed(18f, FontWeight.ExtraBold).scaled()
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(end = AppDimens.Dimens16)
            .abGamePill()
            .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens6)
    ) {
        if (timerSeconds != null) {
            Text("⏱ $timeLeft", color = colors.textPrimary, style = style)
        } else if (popsGoal != null) {
            Text("🎈 $correctCount/$popsGoal", color = colors.textPrimary, style = style)
        }
        Text("⭐ $score", color = colors.textPrimary, style = style)
        if (multiplier > 1) {
            Text("🔥 x$multiplier", color = ORANGE, style = style)
        }
    }
}

// Full-screen announcement when the target changes — kids can't miss it. A toy card in the game's pink with a bouncing target.
@Composable
private fun NewTargetBanner(targetSum: Int, partner: Int, modifier: Modifier = Modifier) {
    val accent: Color = gameCardStyle(GameCategoryType.BALLOON_POP).light
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
        modifier = modifier
            .abPopIn()
            .abToyTray(accent, 0.dp)
            .padding(horizontal = AppDimens.Dimens30, vertical = AppDimens.Dimens20)
    ) {
        Text("🎯", fontSize = 54.sp, modifier = Modifier.abBreathe(0.1f, 800))
        Text(
            stringResource(R.string.balloon_new_target),
            color = BLUE,
            style = AbTypography.fixed(28f, FontWeight.ExtraBold).scaled()
        )
        Text(
            stringResource(R.string.balloon_make_target, targetSum, partner),
            color = ORANGE,
            style = AbTypography.fixed(30f, FontWeight.ExtraBold).scaled()
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

    var buttonsEnabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); buttonsEnabled = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        LaunchedEffect(Unit) { if (starCount >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }

        // "Candy Pop" celebration card (the shared kit card in this game's pink).
        GameResultCard(
            title = stringResource(R.string.balloon_great_job),
            emoji = "🎈",
            accent = ButtonType.PINK,
            stars = starCount,
            score = stringResource(R.string.balloon_final_score) + ": $score",
            stats = {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    GameStatChip("🎯", "$correctCount", GREEN)
                    GameStatChip("❌", "$wrongCount", RED)
                    GameStatChip("💨", "$missedCount", GRAY)
                    GameStatChip("🏆", "$bestScore", ORANGE_BORDER)
                }
            },
            buttons = {
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    GameButton(
                        text = stringResource(R.string.balloon_play_again),
                        icon = Icons.Filled.Refresh,
                        type = if (buttonsEnabled) ButtonType.POSITIVE else ButtonType.DISABLE,
                        onClick = { if (buttonsEnabled) onPlayAgain() }
                    )
                    GameHomeButton(
                        modifier = Modifier.graphicsLayer { alpha = if (buttonsEnabled) 1f else 0.5f },
                        onClick = { if (buttonsEnabled) onBack() }
                    )
                }
            }
        )
    }
}
