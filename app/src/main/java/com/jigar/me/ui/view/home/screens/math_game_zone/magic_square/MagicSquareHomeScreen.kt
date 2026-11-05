package com.jigar.me.ui.view.home.screens.math_game_zone.magic_square

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
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.CommonDifficultySelectorCompose
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayMagicSquareView
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abPopIn
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.magic_square.components.MagicSquareHomeViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)
private val GREEN = Color(0xFF2E7D32)

// A real 3×3 magic square for the preview
private val PREVIEW = listOf(2, 7, 6, 9, 5, 1, 4, 3, 8)

@Composable
fun MagicSquareHomeScreen(
    viewModel: MagicSquareHomeViewModel,
    onStartGame: () -> Unit,
    onBackClick: () -> Unit
) {
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsStateWithLifecycle()
    var showHelp by remember { mutableStateOf(false) }
    val bestScore = viewModel.bestScore(selectedDifficulty)
    val s = if (DeviceInfo.isLargeTablet) 1.7f else if (DeviceInfo.isTablet) 1.45f else 1.0f
    val kitScale = LocalAbScale.current.factor
    val colors = LocalAbColors.current

    // nine candy colours, one per cell, deepened until white digits read on them
    val faces: List<Color> = remember(colors) {
        listOf(
            colors.accentPink, colors.brandSecondary, colors.accentOrange,
            colors.accentPurple, colors.brandPrimary, colors.accentGreen,
            colors.accentYellow, colors.danger, colors.placeValue[4]
        ).map { it.abReadable(3f) }
    }

    // read in the draw phase only, so the preview animates without recomposing the screen
    var time by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) { withFrameNanos { now -> time = (now - start) / 1_000_000_000f } }
    }

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        GameHomeLayout(
            header = {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                BackButtonWithText(
                    title = stringResource(R.string.magic_square_game),
                    modifier = Modifier.weight(1f), onBackClick = onBackClick
                )
                GameButton(
                    modifier = Modifier.padding(end = AppDimens.Dimens16),
                    text = stringResource(R.string.how_to_play),
                    icon = Icons.AutoMirrored.Filled.HelpOutline, type = ButtonType.PINK, isSmall = true,
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
                CommonDifficultySelectorCompose(selected = selectedDifficulty, onSelect = { viewModel.selectDifficulty(it) })
                GameButton(
                    text = stringResource(R.string.lets_start),
                    icon = Icons.Rounded.PlayArrow, type = ButtonType.ORANGE, isIconStart = false, onClick = onStartGame
                )
            }
        },
        ) {

            // Animated candy mini grid: the cells pop in, then swell in a wave while the whole square bobs
            Column(
                verticalArrangement = Arrangement.spacedBy((5f * s).dp),
                modifier = Modifier.graphicsLayer { translationY = sin(time * 1.6f) * 3f * s * density }
            ) {
                for (r in 0 until 3) {
                    Row(horizontalArrangement = Arrangement.spacedBy((5f * s).dp)) {
                        for (c in 0 until 3) {
                            val i = r * 3 + c
                            MagicSquarePreviewCell(index = i, face = faces[i], digit = PREVIEW[i], s = s, time = { time })
                        }
                    }
                }
            }

            Spacer(Modifier.height(AppDimens.Dimens24))

            Text(
                text = stringResource(R.string.magic_square_tagline),
                color = ORANGE,
                style = AbTypography.fixed(22f * kitScale, FontWeight.ExtraBold),
                textAlign = TextAlign.Center,
                modifier = Modifier.abGamePill()
                    .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
            )

            if (bestScore > 0) {
                Spacer(Modifier.height(AppDimens.Dimens6))
                Text(
                    "🏆 " + stringResource(R.string.balloon_best_score) + ": $bestScore",
                    color = GREEN,
                    style = AbTypography.fixed(17f * kitScale, FontWeight.Bold),
                    modifier = Modifier.abGamePill()
                        .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens4)
                )
            }

            }
    }

    AnimatedVisibility(visible = showHelp, enter = fadeIn(), exit = fadeOut()) {
        HowToPlayMagicSquareView { showHelp = false }
    }
}

/** One candy cell of the preview square: pops in, then swells in a wave with its neighbours. */
@Composable
private fun MagicSquarePreviewCell(index: Int, face: Color, digit: Int, s: Float, time: () -> Float) {
    Box(
        modifier = Modifier
            .abPopIn(minOf(400, index * 40))
            .graphicsLayer {
                val wave = 1f + sin(time() * 2.2f + index * 0.4f) * 0.06f
                scaleX = wave
                scaleY = wave
            }
            .size((48f * s).dp)
            .abToyTile(face, (13f * s).dp, (4f * s).dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "$digit",
            style = AbTypography.fixed(28f * s, FontWeight.Black).copy(
                shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f)
            ),
            color = Color.White
        )
    }
}
