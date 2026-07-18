package com.jigar.me.ui.view.home.screens.math_game_zone.merge2048

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
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayMerge2048View
import com.jigar.me.ui.view.home.screens.math_game_zone.merge2048.components.Merge2048Config
import com.jigar.me.ui.view.home.screens.math_game_zone.merge2048.components.Merge2048HomeViewModel
import com.jigar.me.ui.view.home.screens.math_game_zone.merge2048.components.Merge2048Palette
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
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
    val s = if (DeviceInfo.isLargeTablet) 1.7f else if (DeviceInfo.isTablet) 1.45f else 1.0f

    var time by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) { withFrameNanos { now -> time = (now - start) / 1_000_000_000f } }
    }

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {

        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.weight(1f))

            Column(verticalArrangement = Arrangement.spacedBy((5f * s).dp)) {
                for (r in 0 until 2) {
                    Row(horizontalArrangement = Arrangement.spacedBy((5f * s).dp)) {
                        for (c in 0 until 4) {
                            val i = r * 4 + c
                            val v = PREVIEW[i]
                            val scale = 1f + sin(time * 2.2f + i * 0.5f) * 0.07f
                            Box(
                                modifier = Modifier
                                    .graphicsLayer { scaleX = scale; scaleY = scale }
                                    .size((48f * s).dp)
                                    .clip(RoundedCornerShape(AppDimens.Dimens10))
                                    .background(Merge2048Palette.bg(v)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("$v", color = Merge2048Palette.fg(v),
                                    fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (19f * s).sp)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(AppDimens.Dimens24))

            Text(
                text = stringResource(R.string.merge2048_tagline),
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
                CommonDifficultySelectorCompose(
                    selected = selectedDifficulty,
                    onSelect = { viewModel.selectDifficulty(it) },
                    label = { val n = Merge2048Config.forDifficulty(it).size; "${n}×${n}" }
                )
                KidsActionButton(
                    text = stringResource(R.string.lets_start),
                    icon = Icons.Rounded.PlayArrow, type = ButtonType.ORANGE, isIconStart = false, onClick = onStartGame
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth().align(Alignment.TopStart), verticalAlignment = Alignment.CenterVertically) {
            BackButtonWithText(
                title = stringResource(R.string.merge2048_game),
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
        HowToPlayMerge2048View { showHelp = false }
    }
}
