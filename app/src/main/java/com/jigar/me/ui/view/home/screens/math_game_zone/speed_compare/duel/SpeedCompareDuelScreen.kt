package com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.duel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.animations.ConfettiRainEffect
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.PauseTimerInBackground
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.components.Comparator
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.getButtonColors
import kotlinx.coroutines.delay

private val P1_COLOR = Color(0xFF0074D5)   // blue, bottom player
private val P2_COLOR = Color(0xFFE91E63)   // pink, top player
private val CORRECT_GREEN = Color(0xFF43A047)

@Composable
fun SpeedCompareDuelScreen(
    viewModel: SpeedCompareDuelViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.start() }
    // A phone call or the home button freezes the clock - fair play.
    PauseTimerInBackground { viewModel.setTimerPaused(it) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top half faces the kid sitting across the table.
            Box(modifier = Modifier.weight(1f).graphicsLayer { rotationZ = 180f }) {
                PlayerPanel(state = state, player = 2, accent = P2_COLOR) { c ->
                    viewModel.answer(2, c)
                }
            }

            // Center bar: timer + a back door for grown-ups.
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16, Alignment.CenterHorizontally),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(Color(0xFFFF8400), Color(0xFFFFC107))))
                    .padding(vertical = AppDimens.Dimens4)
            ) {
                Text(
                    "✖",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                    style = MaterialTheme.typography.titleMedium.scaled(),
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            AudioPlayerManager.playSoundBtnClick()
                            onBackClick()
                        }
                        .padding(horizontal = AppDimens.Dimens12)
                )
                Text(
                    "⏱ ${state.timeLeft}",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                    style = MaterialTheme.typography.titleMedium.scaled()
                )
                Text(
                    "${state.p2Score} · ${state.p1Score}",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                    style = MaterialTheme.typography.titleMedium.scaled()
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                PlayerPanel(state = state, player = 1, accent = P1_COLOR) { c ->
                    viewModel.answer(1, c)
                }
            }
        }

        if (state.isGameOver) {
            DuelResultOverlay(
                p1 = state.p1Score, p2 = state.p2Score,
                onPlayAgain = { viewModel.start() },
                onBack = onBackClick
            )
        }
    }
}

@Composable
private fun PlayerPanel(
    state: DuelUiState,
    player: Int,
    accent: Color,
    onAnswer: (Comparator) -> Unit
) {
    val s = gameScale()
    val round = state.round
    val locked = player in state.lockedPlayers && !state.revealed
    val won = state.roundWinner == player

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(accent.copy(alpha = 0.08f))
            .graphicsLayer { alpha = if (locked) 0.55f else 1f }
            .padding(horizontal = AppDimens.Dimens16)
    ) {
        // The question: left ? right
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16)
        ) {
            QuestionCard(round?.left?.display ?: "", accent, s)
            Text(
                if (state.revealed) (round?.truth?.symbol ?: "?") else "?",
                color = if (state.revealed) CORRECT_GREEN else Color(0xFFE65100),
                fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                style = MaterialTheme.typography.headlineMedium.scaled()
            )
            QuestionCard(round?.right?.display ?: "", accent, s)
            if (won) {
                Text(
                    "+10 🎉",
                    color = CORRECT_GREEN,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                    style = MaterialTheme.typography.titleMedium.scaled()
                )
            }
        }
        Spacer(Modifier.height(AppDimens.Dimens12))
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16)) {
            Comparator.entries.forEach { comp ->
                val highlight = state.revealed && round?.truth == comp
                AnswerBubble(
                    symbol = comp.symbol,
                    color = if (highlight) CORRECT_GREEN else accent,
                    enabled = !locked && !state.revealed,
                    s = s
                ) { onAnswer(comp) }
            }
        }
    }
}

@Composable
private fun QuestionCard(text: String, accent: Color, s: Float) {
    Text(
        text,
        color = Color(0xFF3E2723),
        maxLines = 1, softWrap = false,
        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
        style = MaterialTheme.typography.headlineSmall.scaled(),
        modifier = Modifier
            .clip(RoundedCornerShape(AppDimens.Dimens12))
            .background(Color.White)
            .border(2.dp, accent, RoundedCornerShape(AppDimens.Dimens12))
            .padding(horizontal = (18f * s).dp, vertical = (10f * s).dp)
    )
}

@Composable
private fun AnswerBubble(symbol: String, color: Color, enabled: Boolean, s: Float, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size((52f * s).dp)
            .clip(CircleShape)
            .background(Brush.verticalGradient(listOf(color.copy(alpha = 0.85f), color)))
            .border(2.dp, Color.White.copy(alpha = 0.7f), CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, enabled = enabled
            ) { onClick() }
    ) {
        Text(
            symbol,
            color = Color.White,
            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
            style = MaterialTheme.typography.titleLarge.scaled()
        )
    }
}

@Composable
private fun DuelResultOverlay(p1: Int, p2: Int, onPlayAgain: () -> Unit, onBack: () -> Unit) {
    val accentColors = getButtonColors(ButtonType.ORANGE)
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.55f)),
        contentAlignment = Alignment.Center
    ) {
        ConfettiRainEffect()
        // "Candy Pop" celebration card — saturated gradient block with a white badge overlapping the top edge.
        Box(contentAlignment = Alignment.TopCenter) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
                modifier = Modifier
                    .shadow(20.dp, RoundedCornerShape(AppDimens.Dimens20 * 1.2f), ambientColor = accentColors.base, spotColor = accentColors.base)
                    .background(accentColors.gradient, RoundedCornerShape(AppDimens.Dimens20 * 1.2f))
                    .padding(horizontal = AppDimens.Dimens30, vertical = AppDimens.Dimens20)
            ) {
                Spacer(Modifier.height(AppDimens.Dimens20))
                Text(
                    when {
                        p1 > p2 -> stringResource(R.string.duel_wins, stringResource(R.string.duel_blue))
                        p2 > p1 -> stringResource(R.string.duel_wins, stringResource(R.string.duel_pink))
                        else -> stringResource(R.string.duel_tie)
                    },
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                    style = MaterialTheme.typography.headlineSmall.scaled().copy(
                        shadow = Shadow(color = accentColors.base.copy(alpha = 0.9f), offset = Offset(1.5f, 1.5f), blurRadius = 0f)
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16)) {
                    ScoreChip(stringResource(R.string.duel_blue), p1, P1_COLOR)
                    ScoreChip(stringResource(R.string.duel_pink), p2, P2_COLOR)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16)) {
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
                Text("🏆", style = MaterialTheme.typography.displaySmall.scaled())
            }
        }
    }
}

@Composable
private fun ScoreChip(label: String, score: Int, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6),
        modifier = Modifier
            .background(Color.White, CircleShape)
            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6)
    ) {
        Text(
            label, color = color,
            fontFamily = FontFamily(Font(R.font.font_bold)),
            style = MaterialTheme.typography.labelLarge.scaled()
        )
        Text(
            "$score", color = color,
            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
            style = MaterialTheme.typography.titleMedium.scaled()
        )
    }
}
