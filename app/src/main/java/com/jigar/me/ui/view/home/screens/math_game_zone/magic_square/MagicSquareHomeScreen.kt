package com.jigar.me.ui.view.home.screens.math_game_zone.magic_square

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.CommonDifficultySelectorCompose
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayMagicSquareView
import com.jigar.me.ui.view.home.screens.math_game_zone.magic_square.components.MagicSquareHomeViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)
private val GREEN = Color(0xFF2E7D32)

// A real 3×3 magic square for the preview
private val PREVIEW = listOf(2, 7, 6, 9, 5, 1, 4, 3, 8)
private val CELL_COLORS = listOf(
    Color(0xFFFF6B6B), Color(0xFF4ECDC4), Color(0xFFFFC107),
    Color(0xFF9B59B6), Color(0xFF0074D5), Color(0xFFFF8400),
    Color(0xFF2ECC71), Color(0xFFE91E63), Color(0xFF00BCD4),
)

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

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                BackButtonWithText(
                    title = stringResource(R.string.magic_square_game),
                    modifier = Modifier.weight(1f), onBackClick = onBackClick
                )
                KidsActionButton(
                    modifier = Modifier.padding(end = AppDimens.Dimens16),
                    text = stringResource(R.string.how_to_play),
                    icon = Icons.AutoMirrored.Filled.HelpOutline, type = ButtonType.PINK, isSmall = true,
                    onClick = { showHelp = true }
                )
            }

            Spacer(Modifier.weight(1f))

            // Animated colorful mini grid — cells gently pop in a wave
            var time by remember { mutableFloatStateOf(0f) }
            LaunchedEffect(Unit) {
                val start = withFrameNanos { it }
                while (true) { withFrameNanos { now -> time = (now - start) / 1_000_000_000f } }
            }
            Column(verticalArrangement = Arrangement.spacedBy((5f * s).dp)) {
                for (r in 0 until 3) {
                    Row(horizontalArrangement = Arrangement.spacedBy((5f * s).dp)) {
                        for (c in 0 until 3) {
                            val i = r * 3 + c
                            val scale = 1f + sin(time * 2.2f + i * 0.4f) * 0.06f
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .graphicsLayer { scaleX = scale; scaleY = scale }
                                    .size((48f * s).dp)
                                    .clip(RoundedCornerShape(AppDimens.Dimens10))
                                    .background(CELL_COLORS[i])
                            ) {
                                Text("${PREVIEW[i]}", color = Color.White,
                                    fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (26f * s).sp)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(AppDimens.Dimens24))

            Text(
                text = stringResource(R.string.magic_square_tagline),
                color = ORANGE, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 22.sp.scaled(),
                modifier = Modifier.clip(RoundedCornerShape(100f)).background(Color.White.copy(alpha = 0.85f))
                    .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
            )

            if (bestScore > 0) {
                Spacer(Modifier.height(AppDimens.Dimens6))
                Text("🏆 " + stringResource(R.string.balloon_best_score) + ": $bestScore",
                    color = GREEN, fontFamily = FontFamily(Font(R.font.font_bold)), fontSize = 16.sp.scaled(),
                    modifier = Modifier.clip(RoundedCornerShape(100f)).background(Color.White.copy(alpha = 0.75f))
                        .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens4))
            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth().padding(AppDimens.Dimens16),
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CommonDifficultySelectorCompose(selected = selectedDifficulty, onSelect = { viewModel.selectDifficulty(it) })
                KidsActionButton(
                    text = stringResource(R.string.lets_start),
                    icon = Icons.Rounded.PlayArrow, type = ButtonType.ORANGE, isIconStart = false, onClick = onStartGame
                )
            }
        }
    }

    AnimatedVisibility(visible = showHelp, enter = fadeIn(), exit = fadeOut()) {
        HowToPlayMagicSquareView { showHelp = false }
    }
}
