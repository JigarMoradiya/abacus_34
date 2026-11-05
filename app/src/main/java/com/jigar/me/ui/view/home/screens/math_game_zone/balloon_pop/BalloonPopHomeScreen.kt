package com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeLayout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.CommonDifficultySelectorCompose
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayBalloonPopView
import com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop.components.BalloonPopHomeViewModel
import com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop.components.balloonPopPalette
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlin.math.PI
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)
private val GREEN = Color(0xFF2E7D32)

@Composable
fun BalloonPopHomeScreen(
    viewModel: BalloonPopHomeViewModel,
    onStartGame: () -> Unit,
    onBackClick: () -> Unit
) {
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsStateWithLifecycle()
    var showHelp by remember { mutableStateOf(false) }
    val bestScore = viewModel.bestScore(selectedDifficulty)

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        GameHomeLayout(
            header = {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButtonWithText(
                    title = stringResource(R.string.balloon_pop_game),
                    modifier = Modifier.weight(1f),
                    onBackClick = onBackClick
                )
                GameButton(
                    modifier = Modifier.padding(end = AppDimens.Dimens16),
                    text = stringResource(R.string.how_to_play),
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    type = ButtonType.PINK,
                    isSmall = true,
                    onClick = { showHelp = true }
                )
            }

            },
            bottom = {

            Row(
                modifier = Modifier.fillMaxWidth().padding(AppDimens.Dimens16),
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CommonDifficultySelectorCompose(
                    selected = selectedDifficulty,
                    onSelect = { viewModel.selectDifficulty(it) }
                )
                GameButton(
                    text = stringResource(R.string.lets_start),
                    icon = Icons.Rounded.PlayArrow,
                    type = ButtonType.ORANGE,
                    isIconStart = false,
                    onClick = onStartGame
                )
            }
        },
        ) {

            // Bobbing glossy balloons showing the bond clearly: 3 + 7 = 10
            BalloonPopHomePreview()

            Spacer(Modifier.height(AppDimens.Dimens8))

            Text(
                text = stringResource(R.string.balloon_pop_tagline),
                color = ORANGE,
                style = AbTypography.fixed(22f, FontWeight.ExtraBold).scaled(),
                modifier = Modifier
                    .abGamePill()
                    .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
            )

            if (bestScore > 0) {
                Spacer(Modifier.height(AppDimens.Dimens6))
                Text(
                    text = "🏆 " + stringResource(R.string.balloon_best_score) + ": $bestScore",
                    color = GREEN,
                    style = AbTypography.fixed(17f, FontWeight.Bold).scaled(),
                    modifier = Modifier
                        .abGamePill()
                        .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens4)
                )
            }

            }
    }

    AnimatedVisibility(visible = showHelp, enter = fadeIn(), exit = fadeOut()) {
        HowToPlayBalloonPopView { showHelp = false }
    }
}

/**
 * The preview: 3 + 7 = 10 on the same glossy balloons as the game, each bobbing and swaying. One clock for all three, in its own
 * composable so a clock tick never recomposes the whole home screen (and the balloons never freeze when other UI animates).
 */
@Composable
private fun BalloonPopHomePreview() {
    var time by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) { withFrameNanos { now -> time = (now - start) / 1_000_000_000f } }
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BalloonPopDecor(text = "3", color = balloonPopPalette[0], sizeDp = 68f, bobDelay = 0.0f, time = time)
        EquationSymbol("+")
        BalloonPopDecor(text = "7", color = balloonPopPalette[1], sizeDp = 68f, bobDelay = 0.5f, time = time)
        EquationSymbol("=")
        BalloonPopDecor(text = "10", color = balloonPopPalette[2], sizeDp = 88f, bobDelay = 1.0f, time = time)
    }
}

@Composable
private fun EquationSymbol(symbol: String) {
    Text(
        text = symbol,
        color = ORANGE,
        style = AbTypography.fixed(36f, FontWeight.Black).scaled(),
        modifier = Modifier.padding(bottom = AppDimens.Dimens16)
    )
}

// Decorative bobbing balloon for the start page: the same cute glossy balloon as the game, with a wiggling string. Clock-driven bob.
@Composable
private fun BalloonPopDecor(text: String, color: Color, sizeDp: Float, bobDelay: Float, time: Float) {
    val t = time + bobDelay
    val bob = sin(t * 2f * PI.toFloat() / 1.8f) * 7f
    val tilt = sin(t * 2f * PI.toFloat() / 3.4f) * 3f
    Box(
        modifier = Modifier
            .offset(y = bob.dp)
            .graphicsLayer {
                rotationZ = tilt
                transformOrigin = TransformOrigin(0.5f, 1f)
            }
    ) {
        BalloonPopCandyBalloon(color = color, text = text, widthDp = sizeDp, fontSp = sizeDp * 0.36f, elapsed = t)
    }
}
