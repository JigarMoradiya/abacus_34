package com.jigar.me.ui.view.home.screens.math_game_zone.equation_match

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeLayout

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.CommonDifficultySelectorCompose
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayEquationMatchView
import com.jigar.me.ui.view.home.screens.math_game_zone.GameCategoryType
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTray
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.equation_match.components.EqMatchPalette
import com.jigar.me.ui.view.home.screens.math_game_zone.equation_match.components.EquationMatchHomeViewModel
import com.jigar.me.ui.view.home.screens.math_game_zone.gameCardStyle
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)
private val GREEN = Color(0xFF2E7D32)
private val PREVIEW = listOf(
    "3×4" to 0, "8÷2" to 1, "13" to 2, "5" to 3,
    "4" to 1, "12" to 0, "8−3" to 3, "9+4" to 2
)

@Composable
fun EquationMatchHomeScreen(
    viewModel: EquationMatchHomeViewModel,
    onStartGame: () -> Unit,
    onBackClick: () -> Unit
) {
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsStateWithLifecycle()
    var showHelp by remember { mutableStateOf(false) }
    val bestScore = viewModel.bestScore(selectedDifficulty)
    val s = gameScale()

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {

        GameHomeLayout(
            header = {Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            BackButtonWithText(
                title = stringResource(R.string.equation_match),
                modifier = Modifier.weight(1f), onBackClick = onBackClick
            )
            GameButton(
                modifier = Modifier.padding(end = AppDimens.Dimens16),
                text = stringResource(R.string.how_to_play),
                icon = Icons.AutoMirrored.Filled.HelpOutline, type = ButtonType.PINK, isSmall = true,
                onClick = { showHelp = true }
            )
        }},
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
                    icon = Icons.Rounded.PlayArrow, type = ButtonType.ORANGE, isIconStart = false, onClick = onStartGame
                )
            }
        },
        ) {

            // Candy cards in the game's tray: the four pairs take turns hopping together.
            EquationMatchPreviewGrid(s)

            Spacer(Modifier.height(AppDimens.Dimens24))

            Text(
                text = stringResource(R.string.equation_match_tagline),
                color = ORANGE, style = AbTypography.fixed(22f * s, FontWeight.ExtraBold),
                textAlign = TextAlign.Center,
                modifier = Modifier.abGamePill()
                    .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
            )

            if (bestScore > 0) {
                Spacer(Modifier.height(AppDimens.Dimens6))
                Text("🏆 " + stringResource(R.string.balloon_best_score) + ": $bestScore",
                    color = GREEN, style = AbTypography.fixed(17f * s, FontWeight.Bold),
                    modifier = Modifier.abGamePill()
                        .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens4))
            }

            }
    }

    AnimatedVisibility(visible = showHelp, enter = fadeIn(), exit = fadeOut()) {
        HowToPlayEquationMatchView { showHelp = false }
    }
}

// ── Preview art ───────────────────────────────────────────────────────────────────────────────────────────────────────

/** Candy cards in the game's tray. The four pairs take turns: both cards of the active pair hop and squash together, with a star badge. */
@Composable
private fun EquationMatchPreviewGrid(s: Float) {
    var t by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) { withFrameNanos { now -> t = (now - start) / 1_000_000_000f } }
    }
    Column(
        verticalArrangement = Arrangement.spacedBy((10f * s).dp),
        // no bordered tray: the cards float on the sky, with room above and around so a hopping, growing pair is never cut
        modifier = Modifier.padding(horizontal = (14f * s).dp, vertical = (16f * s).dp)
    ) {
        for (r in 0 until 2) {
            Row(horizontalArrangement = Arrangement.spacedBy((10f * s).dp)) {
                for (c in 0 until 4) {
                    EquationMatchPreviewCard(r * 4 + c, t, s)
                }
            }
        }
    }
}

@Composable
private fun EquationMatchPreviewCard(i: Int, t: Float, s: Float) {
    val (txt, pid) = PREVIEW[i]
    val face = EqMatchPalette.face(pid)
    val beat = t * 1.1f
    val activePair = beat.toInt() % 4
    val frac = beat - floor(beat)
    val k = if (activePair == pid && frac < 0.7f) sin(frac / 0.7f * PI.toFloat()) else 0f
    val edge = 3f * s
    val side = 58f * s
    Box(
        modifier = Modifier
            .graphicsLayer {
                val grow = 1f + 0.14f * k
                scaleX = grow
                scaleY = grow
                translationY = -6f * s * k * density
            }
            .size(side.dp)
            .abToyTile(face, (side * 0.2f).dp, edge.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            txt,
            style = AbTypography.fixed(20f * s, FontWeight.Black).copy(
                shadow = Shadow(face.abDarkened(0.5f).copy(alpha = 0.55f), Offset(0f, 3f), 0f)
            ),
            color = Color.White,
            maxLines = 1, softWrap = false,
            modifier = Modifier.padding(horizontal = (4f * s).dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (4f * s).dp, y = (-4f * s).dp)
                .size((14f * s).dp)
                .graphicsLayer { scaleX = k; scaleY = k }
        ) {
            Icon(Icons.Filled.Star, contentDescription = null, tint = Color.Black.copy(alpha = 0.25f),
                modifier = Modifier.size((14f * s).dp).offset(y = 1.5.dp))
            Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFC53D),
                modifier = Modifier.size((14f * s).dp))
        }
    }
}
