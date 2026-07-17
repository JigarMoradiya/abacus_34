package com.jigar.me.ui.view.home.screens.math_game_zone.missing_operator

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
import androidx.compose.foundation.layout.offset
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
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayMissingOperatorView
import com.jigar.me.ui.view.home.screens.math_game_zone.missing_operator.components.MissingOperatorHomeViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)
private val GREEN = Color(0xFF2E7D32)
private val BLUE = Color(0xFF0074D5)

@Composable
fun MissingOperatorHomeScreen(
    viewModel: MissingOperatorHomeViewModel,
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
                    title = androidx.compose.ui.res.stringResource(R.string.missing_operator_game),
                    modifier = Modifier.weight(1f), onBackClick = onBackClick
                )
                KidsActionButton(
                    modifier = Modifier.padding(end = AppDimens.Dimens16),
                    text = androidx.compose.ui.res.stringResource(R.string.how_to_play),
                    icon = Icons.AutoMirrored.Filled.HelpOutline, type = ButtonType.PINK, isSmall = true,
                    onClick = { showHelp = true }
                )
            }

            Spacer(Modifier.weight(1f))

            // Preview: 12 [?] 3 = 4 with pulsing slot — animated
            var time by remember { mutableFloatStateOf(0f) }
            LaunchedEffect(Unit) {
                val start = withFrameNanos { it }
                while (true) { withFrameNanos { now -> time = (now - start) / 1_000_000_000f } }
            }
            val pulse = 1f + sin(time * 3f) * 0.10f
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8), verticalAlignment = Alignment.CenterVertically) {
                PreviewToken("12", s)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.graphicsLayer { scaleX = pulse; scaleY = pulse }
                        .size((56f * s).dp)
                        .clip(RoundedCornerShape(AppDimens.Dimens12)).background(BLUE.copy(alpha = 0.12f))
                        .border(2.5.dp, BLUE, RoundedCornerShape(AppDimens.Dimens12))
                ) {
                    Text("?", color = BLUE, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (36f * s).sp)
                }
                PreviewToken("3", s); PreviewToken("=", s); PreviewToken("4", s)
            }

            Spacer(Modifier.height(AppDimens.Dimens20))

            // Animated operator signs bobbing in a wave — fills the space + hints the choices
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12), verticalAlignment = Alignment.CenterVertically) {
                listOf("+", "−", "×", "÷").forEachIndexed { index, sym ->
                    val bob = sin(time * 2f + index * 0.6f) * 6f
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .offset(y = bob.dp)
                            .size((54f * s).dp)
                            .clip(RoundedCornerShape(AppDimens.Dimens16))
                            .background(BLUE)
                    ) {
                        Text(sym, color = Color.White, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (30f * s).sp)
                    }
                }
            }

            Spacer(Modifier.height(AppDimens.Dimens20))

            Text(
                text = androidx.compose.ui.res.stringResource(R.string.missing_operator_tagline),
                color = ORANGE, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 22.sp.scaled(),
                modifier = Modifier.clip(RoundedCornerShape(100f)).background(Color.White.copy(alpha = 0.85f))
                    .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
            )

            if (bestScore > 0) {
                Spacer(Modifier.height(AppDimens.Dimens6))
                Text("🏆 " + androidx.compose.ui.res.stringResource(R.string.balloon_best_score) + ": $bestScore",
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
                    text = androidx.compose.ui.res.stringResource(R.string.lets_start),
                    icon = Icons.Rounded.PlayArrow, type = ButtonType.ORANGE, isIconStart = false, onClick = onStartGame
                )
            }
        }
    }

    AnimatedVisibility(visible = showHelp, enter = fadeIn(), exit = fadeOut()) {
        HowToPlayMissingOperatorView { showHelp = false }
    }
}

@Composable
private fun PreviewToken(text: String, s: Float) {
    Text(text, color = Color.Black, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (36f * s).sp)
}
