package com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku

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
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayCalcudokuView
import com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku.components.CalcudokuHomeViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)
private val GREEN = Color(0xFF2E7D32)

// A real 3×3 solution grouped into cages — same-cage cells share a colour and
// pulse together so cage pairs stand out.
private val PREVIEW = listOf(1, 2, 3, 2, 3, 1, 3, 1, 2)
private val PREVIEW_CAGE = listOf(0, 0, 1, 2, 3, 3, 2, 4, 4)
private val CLUES = mapOf(0 to "3+", 2 to "3", 3 to "6×", 4 to "2−", 7 to "2×")
private val CAGE_COLORS = listOf(
    Color(0xFFFF6B6B), Color(0xFFFFC107), Color(0xFF0074D5),
    Color(0xFF9B59B6), Color(0xFF2ECC71),
)

@Composable
fun CalcudokuHomeScreen(
    viewModel: CalcudokuHomeViewModel,
    onStartGame: () -> Unit,
    onBackClick: () -> Unit
) {
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsStateWithLifecycle()
    var showHelp by remember { mutableStateOf(false) }
    val bestTime = viewModel.bestTime(selectedDifficulty)
    val s = if (DeviceInfo.isLargeTablet) 1.7f else if (DeviceInfo.isTablet) 1.45f else 1.0f

    var time by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) { withFrameNanos { now -> time = (now - start) / 1_000_000_000f } }
    }

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {

        // Content centered over the full screen
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.weight(1f))

            Column(verticalArrangement = Arrangement.spacedBy((4f * s).dp)) {
                for (r in 0 until 3) {
                    Row(horizontalArrangement = Arrangement.spacedBy((4f * s).dp)) {
                        for (c in 0 until 3) {
                            val i = r * 3 + c
                            val cage = PREVIEW_CAGE[i]
                            val scale = 1f + sin(time * 2.0f + cage * 1.2f) * 0.08f   // same cage pulses together
                            Box(
                                modifier = Modifier
                                    .graphicsLayer { scaleX = scale; scaleY = scale }
                                    .size((50f * s).dp)
                                    .clip(RoundedCornerShape(AppDimens.Dimens8))
                                    .background(CAGE_COLORS[cage]),
                                contentAlignment = Alignment.Center
                            ) {
                                CLUES[i]?.let { clue ->
                                    Text(clue, color = Color.White.copy(alpha = 0.95f),
                                        fontFamily = FontFamily(Font(R.font.font_bold)), fontSize = (11f * s).sp,
                                        modifier = Modifier.align(Alignment.TopStart).padding(start = (4f * s).dp, top = (2f * s).dp))
                                }
                                Text("${PREVIEW[i]}", color = Color.White,
                                    fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (24f * s).sp)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(AppDimens.Dimens24))

            Text(
                text = stringResource(R.string.calcudoku_tagline),
                color = ORANGE, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 22.sp.scaled(),
                modifier = Modifier.clip(RoundedCornerShape(100f)).background(Color.White.copy(alpha = 0.85f))
                    .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
            )

            if (bestTime > 0) {
                Spacer(Modifier.height(AppDimens.Dimens6))
                Text("🏆 " + stringResource(R.string.best_time) + ": " + "%d:%02d".format(bestTime / 60, bestTime % 60),
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

        // Header floats on top (z-index above the centered content)
        Row(modifier = Modifier.fillMaxWidth().align(Alignment.TopStart), verticalAlignment = Alignment.CenterVertically) {
            BackButtonWithText(
                title = stringResource(R.string.calcudoku_game),
                modifier = Modifier.weight(1f), onBackClick = onBackClick
            )
            KidsActionButton(
                modifier = Modifier.padding(end = AppDimens.Dimens16),
                text = stringResource(R.string.how_to_play),
                icon = Icons.AutoMirrored.Filled.HelpOutline, type = ButtonType.PINK, isSmall = true,
                onClick = { showHelp = true }
            )
        }
    }

    AnimatedVisibility(visible = showHelp, enter = fadeIn(), exit = fadeOut()) {
        HowToPlayCalcudokuView { showHelp = false }
    }
}
