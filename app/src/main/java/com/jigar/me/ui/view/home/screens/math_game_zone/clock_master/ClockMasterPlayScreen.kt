package com.jigar.me.ui.view.home.screens.math_game_zone.clock_master

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameCharacter

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.appScale
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.screens.math_game_zone.clock_master.components.ClockMasterUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.clock_master.viewmodel.ClockMasterPlayViewModel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.AbDebugFlags
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameMascotPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameResultCard
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameScoreboardPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameStatChip
import com.jigar.me.ui.view.home.screens.math_game_zone.common.NextDifficultyBanner
import com.jigar.me.ui.view.home.screens.math_game_zone.common.PauseTimerInBackground
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBounce
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abPopIn
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abWobble
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.common.randomGameCheer
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)

@Composable
fun ClockMasterPlayScreen(
    viewModel: ClockMasterPlayViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val baseCheer = remember { randomGameCheer() }
    val s = gameScale()
    val colors = LocalAbColors.current
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
                // The question, on a pill with an orange rim.
                Text(
                    text = stringResource(R.string.clock_master_prompt),
                    style = AbTypography.fixed(20f * appScale(), FontWeight.ExtraBold),
                    color = ORANGE,
                    modifier = Modifier.align(Alignment.Center)
                        .abGamePill(outline = ORANGE_BORDER)
                        .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens8)
                )
                // The clock, turning red for the last ten seconds.
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
                        style = AbTypography.fixed(26f * s, FontWeight.Black),
                        color = if (state.timeLeft <= 10) colors.danger.abReadable(4.5f) else colors.textPrimary
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
                    modifier = Modifier.weight(1f).fillMaxHeight(), character = GameCharacter.Rabbit)

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
        // Debug builds only: lets the lead render the real result overlay without playing a round.
        if (AbDebugFlags.showResultEnabled && !state.isGameOver) {
            ResultOverlay(state = state, best = viewModel.bestScore,
                onPlayAgain = { viewModel.start() }, onBack = onBackClick)
        }
    }
}

// 2x2 digital-time options as candy tiles; the right answer turns green and bounces, a wrong tap turns red and
// wobbles while the correct one glows.
@Composable
private fun OptionsColumn(state: ClockMasterUiState, s: Float, onTap: (Int) -> Unit) {
    val q = state.question ?: return
    // A new question builds the tiles afresh, so they pop in one after another again.
    key(state.questionNumber) {
        Column(verticalArrangement = Arrangement.spacedBy((12f * s).dp)) {
            q.options.chunked(2).forEach { rowOptions ->
                Row(horizontalArrangement = Arrangement.spacedBy((12f * s).dp)) {
                    rowOptions.forEach { option ->
                        val index = q.options.indexOf(option)
                        val revealed = state.lastAnswerRight || state.lastAnswerWrong
                        val revealCorrect = revealed && index == q.correctIndex
                        val tappedWrong = state.lastAnswerWrong && index == state.selectedIndex
                        val faded = revealed && !revealCorrect && !tappedWrong
                        ClockMasterOptionTile(
                            option = option, index = index, s = s,
                            revealCorrect = revealCorrect, tappedWrong = tappedWrong, faded = faded,
                            onTap = { onTap(index) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClockMasterOptionTile(
    option: String,
    index: Int,
    s: Float,
    revealCorrect: Boolean,
    tappedWrong: Boolean,
    faded: Boolean,
    onTap: () -> Unit
) {
    val colors = LocalAbColors.current
    val face = when {
        revealCorrect -> colors.accentGreen.abReadable(3f)
        tappedWrong -> colors.danger.abReadable(3f)
        else -> listOf(colors.accentOrange, colors.brandSecondary, colors.accentPurple, colors.accentPink)[index % 4].abReadable(3f)
    }
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val squash by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "clockMasterTile"
    )
    val fade by animateFloatAsState(targetValue = if (faded) 0.55f else 1f, animationSpec = tween(200), label = "clockMasterFade")

    Box(
        modifier = Modifier
            .abPopIn(index * 60)
            .abBounce(revealCorrect, 1.12f)
            .abWobble(tappedWrong)
            .graphicsLayer { scaleX = squash; scaleY = squash; alpha = fade }
            .clickable(interactionSource = interaction, indication = null, onClick = onTap)
    ) {
        Box(
            modifier = Modifier
                .abToyTile(face, (18f * s).dp, 4.dp)
                .padding(horizontal = (24f * s).dp, vertical = (8f * s).dp)
                .widthIn(min = (64f * s).dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                option,
                style = AbTypography.fixed(26f * s, FontWeight.Black).copy(shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f)),
                color = Color.White,
                maxLines = 1, softWrap = false
            )
        }
        // A tick on the right answer, a cross on a wrong tap.
        if (revealCorrect || tappedWrong) {
            val tint = if (revealCorrect) colors.accentGreen.abReadable(3f) else colors.danger.abReadable(3f)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (8f * s).dp, y = (-8f * s).dp)
                    .abPopIn()
                    .size((24f * s).dp)
                    .background(tint, CircleShape)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (revealCorrect) Icons.Filled.Check else Icons.Filled.Close,
                    contentDescription = null, tint = Color.White,
                    modifier = Modifier.size((14f * s).dp)
                )
            }
        }
    }
}

@Composable
private fun ResultOverlay(state: ClockMasterUiState, best: Int, onPlayAgain: () -> Unit, onBack: () -> Unit) {
    val stars = state.starCount
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { if (stars >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        GameResultCard(
            title = stringResource(if (stars >= 2) R.string.balloon_great_job else R.string.good_try),
            emoji = "🕐",
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
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    GameButton(
                        text = stringResource(R.string.balloon_play_again),
                        icon = Icons.Filled.Refresh,
                        type = if (enabled) ButtonType.POSITIVE else ButtonType.DISABLE,
                        onClick = { if (enabled) onPlayAgain() }
                    )
                    GameHomeButton(onClick = { if (enabled) onBack() })
                }
            }
        )
    }
}
