package com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayBalloonPopView
import com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop.components.BalloonPopHomeViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
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
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButtonWithText(
                    title = androidx.compose.ui.res.stringResource(R.string.balloon_pop_game),
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

            // Bobbing balloons showing the bond clearly: 3 + 7 = 10
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DecorBalloon(text = "3", color = Color(0xFFFF6B6B), sizeDp = 68, bobDelay = 0.0f)
                EquationSymbol("+")
                DecorBalloon(text = "7", color = Color(0xFF4ECDC4), sizeDp = 68, bobDelay = 0.5f)
                EquationSymbol("=")
                DecorBalloon(text = "10", color = Color(0xFFFFC107), sizeDp = 88, bobDelay = 1.0f)
            }

            Spacer(Modifier.height(AppDimens.Dimens8))

            Text(
                text = androidx.compose.ui.res.stringResource(R.string.balloon_pop_tagline),
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
                    color = GREEN,
                    fontFamily = FontFamily(Font(R.font.font_bold)),
                    fontSize = 16.sp.scaled(),
                    modifier = Modifier
                        .clip(RoundedCornerShape(100f))
                        .background(Color.White.copy(alpha = 0.75f))
                        .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens4)
                )
            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth().padding(AppDimens.Dimens16),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CommonDifficultySelectorCompose(
                    selected = selectedDifficulty,
                    onSelect = { viewModel.selectDifficulty(it) }
                )
                Spacer(Modifier.weight(1f))
                KidsActionButton(
                    text = androidx.compose.ui.res.stringResource(R.string.lets_play),
                    icon = Icons.Rounded.PlayArrow,
                    type = ButtonType.ORANGE,
                    isIconStart = false,
                    onClick = onStartGame
                )
            }
        }
    }

    AnimatedVisibility(visible = showHelp, enter = fadeIn(), exit = fadeOut()) {
        HowToPlayBalloonPopView { showHelp = false }
    }
}

@Composable
private fun EquationSymbol(symbol: String) {
    Text(
        text = symbol,
        color = ORANGE,
        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
        fontSize = 34.sp.scaled(),
        modifier = Modifier.padding(bottom = AppDimens.Dimens16)
    )
}

// Decorative bobbing balloon for the start page — clock-driven bob so it never
// freezes when other UI animates (e.g. best-score chip appearing).
@Composable
private fun DecorBalloon(text: String, color: Color, sizeDp: Int, bobDelay: Float) {
    var time by remember { mutableFloatStateOf(0f) }
    LaunchedEffectTime { time = it }

    val bob = (sin((time + bobDelay) * 2f * Math.PI.toFloat() / 1.8f) * 7f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.offset(y = bob.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(sizeDp.dp, (sizeDp * 1.12f).dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawOval(brush = Brush.verticalGradient(listOf(Color.White, color)), topLeft = Offset.Zero, size = size)
            }
            Text(
                text = text,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                fontSize = (sizeDp * 0.36f).sp
            )
        }
        Canvas(modifier = Modifier.size((sizeDp * 0.25f).dp, (sizeDp * 0.5f).dp)) {
            val path = Path()
            val amplitude = size.width * 0.5f
            val seg = size.height / 4f
            path.moveTo(size.width / 2f, 0f)
            for (i in 0 until 4) {
                val y1 = i * seg
                val y2 = y1 + seg
                val dir = if (i % 2 == 0) 1f else -1f
                path.quadraticBezierTo(size.width / 2f + amplitude * dir, (y1 + y2) / 2f, size.width / 2f, y2)
            }
            drawPath(path, color = Color.Black.copy(alpha = 0.6f), style = Stroke(width = 1.5.dp.toPx()))
        }
    }
}

// Small clock hook — updates `onTime` with elapsed seconds every frame.
@Composable
private fun LaunchedEffectTime(onTime: (Float) -> Unit) {
    androidx.compose.runtime.LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) {
            withFrameNanos { now -> onTime((now - start) / 1_000_000_000f) }
        }
    }
}
