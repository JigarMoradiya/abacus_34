package com.jigar.me.ui.view.home.screens.math_game_zone.merge2048

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
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
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.CommonDifficultySelectorCompose
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayMerge2048View
import com.jigar.me.ui.view.home.screens.math_game_zone.GameCategoryType
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTray
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.gameCardStyle
import com.jigar.me.ui.view.home.screens.math_game_zone.merge2048.components.Merge2048Config
import com.jigar.me.ui.view.home.screens.math_game_zone.merge2048.components.Merge2048HomeViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlin.math.max
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)
private val GREEN = Color(0xFF2E7D32)
private val PREVIEW = listOf(2, 4, 8, 16, 32, 64, 128, 256)

@Composable
fun Merge2048HomeScreen(
    viewModel: Merge2048HomeViewModel,
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
                title = stringResource(R.string.merge2048_game),
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
                    onSelect = { viewModel.selectDifficulty(it) },
                    label = { val n = Merge2048Config.forDifficulty(it).size; "${n}×${n}" }
                )
                GameButton(
                    text = stringResource(R.string.lets_start),
                    icon = Icons.Rounded.PlayArrow, type = ButtonType.ORANGE, isIconStart = false, onClick = onStartGame
                )
            }
        },
        ) {

            // Candy tiles in the game's tray: each one hops and squashes in a travelling wave.
            Merge2048PreviewGrid(s)

            Spacer(Modifier.height(AppDimens.Dimens24))

            Text(
                text = stringResource(R.string.merge2048_tagline),
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
        HowToPlayMerge2048View { showHelp = false }
    }
}

// ── Preview art ───────────────────────────────────────────────────────────────────────────────────────────────────────

@Composable
private fun Merge2048PreviewGrid(s: Float) {
    val accent = gameCardStyle(GameCategoryType.MERGE_2048).light
    var t by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) { withFrameNanos { now -> t = (now - start) / 1_000_000_000f } }
    }
    Column(
        verticalArrangement = Arrangement.spacedBy((5f * s).dp),
        modifier = Modifier.abToyTray(accent, (9f * s).dp)
    ) {
        for (r in 0 until 2) {
            Row(horizontalArrangement = Arrangement.spacedBy((5f * s).dp)) {
                for (c in 0 until 4) {
                    Merge2048PreviewTile(r * 4 + c, t, s)
                }
            }
        }
    }
}

@Composable
private fun Merge2048PreviewTile(i: Int, t: Float, s: Float) {
    val colors = LocalAbColors.current
    val v = PREVIEW[i]
    val face = merge2048Candy(v, colors)
    val wave = sin(t * 2.4f + i * 0.55f)
    val hop = max(0f, wave) * 3.5f * s
    val squash = 1f + max(0f, wave) * 0.08f
    val edge = 3f * s
    val side = 48f * s
    Box(
        modifier = Modifier
            .graphicsLayer { scaleX = squash; scaleY = squash; translationY = -hop * density }
            .size(side.dp)
            .abToyTile(face, (side * 0.22f).dp, edge.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "$v",
            style = AbTypography.fixed(19f * s, FontWeight.Black).copy(
                shadow = Shadow(face.abDarkened(0.5f).copy(alpha = 0.6f), Offset(0f, 3f), 0f)
            ),
            color = Color.White,
            maxLines = 1
        )
    }
}
