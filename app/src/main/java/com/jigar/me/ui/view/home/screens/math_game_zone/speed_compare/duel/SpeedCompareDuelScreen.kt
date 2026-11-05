package com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.duel

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.screens.math_game_zone.common.AbDebugFlags
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameResultCard
import com.jigar.me.ui.view.home.screens.math_game_zone.common.PauseTimerInBackground
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBounce
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abPopIn
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abWobble
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.components.Comparator
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
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

        // Debug builds only: render the duel's result overlay without playing a round (`--ez ab_show_result true`).
        if (AbDebugFlags.showResultEnabled && !state.isGameOver) {
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
    val tint: Color = if (won) CORRECT_GREEN else accent
    val panelTint by animateColorAsState(tint.copy(alpha = if (won) 0.18f else 0.08f), tween(250), label = "panelTint")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(panelTint)
            .graphicsLayer { alpha = if (locked) 0.55f else 1f }
            .padding(horizontal = AppDimens.Dimens16)
    ) {
        // The question: left ? right
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16)
        ) {
            DuelQuestionCard(round?.left?.display ?: "", accent, s)
            Text(
                if (state.revealed) (round?.truth?.symbol ?: "?") else "?",
                color = if (state.revealed) CORRECT_GREEN else Color(0xFFE65100),
                style = AbTypography.fixed(28f, FontWeight.Black).scaled(),
                modifier = Modifier.abBounce(state.revealed, 1.4f)
            )
            DuelQuestionCard(round?.right?.display ?: "", accent, s)
            if (won) {
                Text(
                    "+10 🎉",
                    color = CORRECT_GREEN,
                    style = AbTypography.fixed(18f, FontWeight.ExtraBold).scaled(),
                    modifier = Modifier.abPopIn()
                )
            }
        }
        Spacer(Modifier.height(AppDimens.Dimens12))
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16), modifier = Modifier.abWobble(locked)) {
            Comparator.entries.forEach { comp ->
                val highlight = state.revealed && round?.truth == comp
                AnswerBubble(
                    symbol = comp.symbol,
                    color = if (highlight) CORRECT_GREEN else accent,
                    highlight = highlight,
                    enabled = !locked && !state.revealed,
                    s = s
                ) { onAnswer(comp) }
            }
        }
    }
}

/** A candy number tile in the player's colour; it bounces when the number changes. */
@Composable
private fun DuelQuestionCard(text: String, accent: Color, s: Float) {
    Text(
        text,
        color = Color.White,
        maxLines = 1, softWrap = false,
        style = AbTypography.fixed(24f, FontWeight.Black).scaled().copy(shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f)),
        modifier = Modifier
            .abBounce(text, 1.12f)
            .abToyTile(accent.abReadable(3f), AppDimens.Dimens12, 4.dp)
            .padding(horizontal = (18f * s).dp, vertical = (8f * s).dp)
    )
}

/** A candy answer key: squashes when pressed, goes green and bounces when it is the right sign. */
@Composable
private fun AnswerBubble(symbol: String, color: Color, highlight: Boolean, enabled: Boolean, s: Float, onClick: () -> Unit) {
    val keyW = 52f * s
    val face by animateColorAsState(color.abReadable(3f), tween(180), label = "bubbleFace")
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val sx by animateFloatAsState(
        targetValue = if (pressed) 1.07f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium), label = "bubbleSx"
    )
    val sy by animateFloatAsState(
        targetValue = if (pressed) 0.88f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium), label = "bubbleSy"
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .abBounce(highlight, 1.18f)
            .graphicsLayer {
                scaleX = sx
                scaleY = sy
                transformOrigin = TransformOrigin(0.5f, 1f)
            }
            .clickable(interactionSource = interaction, indication = null, enabled = enabled) { onClick() }
            .abToyTile(face, (keyW * 0.32f).dp, 4.dp)
            .size(keyW.dp, (keyW - 4f).dp)
    ) {
        Text(
            symbol,
            color = Color.White,
            style = AbTypography.fixed(24f * s, FontWeight.Black).copy(shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f))
        )
    }
}

@Composable
private fun DuelResultOverlay(p1: Int, p2: Int, onPlayAgain: () -> Unit, onBack: () -> Unit) {
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        // The duel has no star rating, so the shared card shows its three ghost stars.
        GameResultCard(
            title = when {
                p1 > p2 -> stringResource(R.string.duel_wins, stringResource(R.string.duel_blue))
                p2 > p1 -> stringResource(R.string.duel_wins, stringResource(R.string.duel_pink))
                else -> stringResource(R.string.duel_tie)
            },
            emoji = "🏆",
            accent = ButtonType.ORANGE,
            stars = 0,
            stats = {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    ScoreChip(stringResource(R.string.duel_blue), p1, P1_COLOR)
                    ScoreChip(stringResource(R.string.duel_pink), p2, P2_COLOR)
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
                    GameHomeButton(
                        modifier = Modifier.graphicsLayer { alpha = if (enabled) 1f else 0.5f },
                        onClick = { if (enabled) onBack() }
                    )
                }
            }
        )
    }
}

/** One player's score on a white pill, in that player's colour. */
@Composable
private fun ScoreChip(label: String, score: Int, color: Color) {
    val s = gameScale()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy((6f * s).dp),
        modifier = Modifier
            .shadow(3.dp, CircleShape)
            .background(Color.White, CircleShape)
            .border(1.5.dp, Color.Black.copy(alpha = 0.06f), CircleShape)
            .padding(horizontal = (14f * s).dp, vertical = (4f * s).dp)
    ) {
        Text(label, color = color, style = AbTypography.fixed(16f * s, FontWeight.ExtraBold))
        Text("$score", color = color, style = AbTypography.fixed(22f * s, FontWeight.Black))
    }
}
