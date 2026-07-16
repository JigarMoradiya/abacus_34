package com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare

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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.CommonDifficultySelectorCompose
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlaySpeedCompareView
import com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.components.SpeedCompareHomeViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)
private val GREEN = Color(0xFF2E7D32)

@Composable
fun SpeedCompareHomeScreen(
    viewModel: SpeedCompareHomeViewModel,
    onStartGame: () -> Unit,
    onBackClick: () -> Unit
) {
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsStateWithLifecycle()
    var showHelp by remember { mutableStateOf(false) }
    val bestScore = viewModel.bestScore(selectedDifficulty)

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                BackButtonWithText(
                    title = androidx.compose.ui.res.stringResource(R.string.speed_compare_game),
                    modifier = Modifier.weight(1f),
                    onBackClick = onBackClick
                )
                KidsActionButton(
                    modifier = Modifier.padding(end = AppDimens.Dimens16),
                    text = androidx.compose.ui.res.stringResource(R.string.how_to_play),
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    type = ButtonType.PINK,
                    isSmall = true,
                    onClick = { showHelp = true }
                )
            }

            Spacer(Modifier.weight(1f))

            // Preview: 7 > 5 cards see-sawing up/down — animated, matches iOS
            var time by remember { mutableFloatStateOf(0f) }
            LaunchedEffect(Unit) {
                val start = withFrameNanos { it }
                while (true) { withFrameNanos { now -> time = (now - start) / 1_000_000_000f } }
            }
            val bob = (sin(time * 1.6f) * 10f)
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PreviewCard("7", Color(0xFF0074D5), Modifier.offset(y = (-bob).dp))
                Text(">", color = ORANGE, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 40.sp.scaled())
                PreviewCard("5", Color(0xFFE91E63), Modifier.offset(y = bob.dp))
            }

            Spacer(Modifier.height(AppDimens.Dimens30))   // space before the tagline card

            Text(
                text = androidx.compose.ui.res.stringResource(R.string.speed_compare_tagline),
                color = ORANGE,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                fontSize = 22.sp.scaled(),
                modifier = Modifier
                    .clip(RoundedCornerShape(100f))
                    .background(Color.White.copy(alpha = 0.85f))
                    .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
            )

            if (bestScore > 0) {
                Spacer(Modifier.height(AppDimens.Dimens6))
                Text(
                    text = "🏆 " + androidx.compose.ui.res.stringResource(R.string.balloon_best_score) + ": $bestScore",
                    color = GREEN, fontFamily = FontFamily(Font(R.font.font_bold)), fontSize = 16.sp.scaled(),
                    modifier = Modifier.clip(RoundedCornerShape(100f)).background(Color.White.copy(alpha = 0.75f))
                        .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens4)
                )
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
                    text = androidx.compose.ui.res.stringResource(R.string.lets_start),
                    icon = Icons.Rounded.PlayArrow,
                    type = ButtonType.ORANGE,
                    isIconStart = false,
                    onClick = onStartGame
                )
            }
        }
    }

    AnimatedVisibility(visible = showHelp, enter = fadeIn(), exit = fadeOut()) {
        HowToPlaySpeedCompareView { showHelp = false }
    }
}

@Composable
private fun PreviewCard(text: String, tint: Color, modifier: Modifier = Modifier) {
    val s = if (com.jigar.me.data.local.data.DeviceInfo.isLargeTablet) 1.7f
    else if (com.jigar.me.data.local.data.DeviceInfo.isTablet) 1.45f else 1.0f
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size((74f * s).dp)
            .clip(RoundedCornerShape((16f * s).dp))
            .background(Color.White)
            .border((3f * s).dp, tint, RoundedCornerShape((16f * s).dp))
    ) {
        Text(text, color = Color.Black, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (30f * s).sp)
    }
}
