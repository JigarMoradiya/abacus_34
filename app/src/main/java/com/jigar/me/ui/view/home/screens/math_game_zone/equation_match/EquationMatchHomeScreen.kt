package com.jigar.me.ui.view.home.screens.math_game_zone.equation_match

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayEquationMatchView
import com.jigar.me.ui.view.home.screens.math_game_zone.equation_match.components.EqMatchPalette
import com.jigar.me.ui.view.home.screens.math_game_zone.equation_match.components.EquationMatchHomeViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
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
    val s = if (DeviceInfo.isLargeTablet) 1.7f else if (DeviceInfo.isTablet) 1.45f else 1.0f

    var time by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) { withFrameNanos { now -> time = (now - start) / 1_000_000_000f } }
    }

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {

        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.weight(1f))

            Column(verticalArrangement = Arrangement.spacedBy((8f * s).dp), horizontalAlignment = Alignment.CenterHorizontally) {
                for (r in 0 until 2) {
                    Row(horizontalArrangement = Arrangement.spacedBy((8f * s).dp)) {
                        for (c in 0 until 4) {
                            val (txt, pid) = PREVIEW[r * 4 + c]
                            val face = EqMatchPalette.face(pid)
                            val scale = 1f + sin(time * 2.2f + pid * 0.7f) * 0.06f   // pair pulses together
                            Box(
                                modifier = Modifier
                                    .graphicsLayer { scaleX = scale; scaleY = scale }
                                    .size((58f * s).dp)
                                    .clip(RoundedCornerShape(AppDimens.Dimens12))
                                    .background(face),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(txt, color = Color.White, maxLines = 1, softWrap = false,
                                    fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (18f * s).sp)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(AppDimens.Dimens24))

            Text(
                text = stringResource(R.string.equation_match_tagline),
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
                    onSelect = { viewModel.selectDifficulty(it) }
                )
                KidsActionButton(
                    text = stringResource(R.string.lets_start),
                    icon = Icons.Rounded.PlayArrow, type = ButtonType.ORANGE, isIconStart = false, onClick = onStartGame
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth().align(Alignment.TopStart), verticalAlignment = Alignment.CenterVertically) {
            BackButtonWithText(
                title = stringResource(R.string.equation_match),
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
        HowToPlayEquationMatchView { showHelp = false }
    }
}
