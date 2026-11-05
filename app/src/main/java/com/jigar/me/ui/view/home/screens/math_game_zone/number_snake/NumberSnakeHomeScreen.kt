package com.jigar.me.ui.view.home.screens.math_game_zone.number_snake

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeLayout

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.appScale
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.CommonDifficultySelectorCompose
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayNumberSnakeView
import com.jigar.me.ui.view.home.screens.math_game_zone.GameCategoryType
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTray
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.common.toyColor
import com.jigar.me.ui.view.home.screens.math_game_zone.gameCardStyle
import com.jigar.me.ui.view.home.screens.math_game_zone.number_snake.components.NumberSnakeHomeViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlin.math.abs
import kotlin.math.sin

private val GOLD = Color(0xFFFF6F00)
private val BEST_GOLD = Color(0xFFE65100)

// A 3x3 snapshot showing the number trail winding through the grid.
private val PREVIEW = listOf(1, 2, 3, 8, 9, 4, 7, 6, 5)

@Composable
fun NumberSnakeHomeScreen(
    viewModel: NumberSnakeHomeViewModel,
    onStartGame: () -> Unit,
    onBackClick: () -> Unit
) {
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsStateWithLifecycle()
    var showHelp by remember { mutableStateOf(false) }
    val bestTime = viewModel.bestTime(selectedDifficulty)
    val s = gameScale()

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {

        GameHomeLayout(
            header = {Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            BackButtonWithText(
                title = stringResource(R.string.number_snake),
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
                CommonDifficultySelectorCompose(selected = selectedDifficulty, onSelect = { viewModel.selectDifficulty(it) })
                GameButton(
                    text = stringResource(R.string.lets_start),
                    icon = Icons.Rounded.PlayArrow, type = ButtonType.ORANGE, isIconStart = false, onClick = onStartGame
                )
            }
        },
        ) {

            NumberSnakePreview(values = PREVIEW, s = s)

            Spacer(Modifier.height(AppDimens.Dimens24))

            Text(
                text = stringResource(R.string.number_snake_tagline),
                color = GOLD, style = AbTypography.fixed(22f * appScale(), FontWeight.ExtraBold),
                textAlign = TextAlign.Center,
                modifier = Modifier.abGamePill()
                    .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
            )

            if (bestTime > 0) {
                Spacer(Modifier.height(AppDimens.Dimens6))
                Text("🏆 " + stringResource(R.string.best_time) + ": " + "%d:%02d".format(bestTime / 60, bestTime % 60),
                    color = BEST_GOLD, style = AbTypography.fixed(16f * appScale(), FontWeight.Bold),
                    modifier = Modifier.abGamePill()
                        .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens4))
            }

            }
    }

    AnimatedVisibility(visible = showHelp, enter = fadeIn(), exit = fadeOut()) {
        HowToPlayNumberSnakeView { showHelp = false }
    }
}

// Lets a decoration reach past its content: the content keeps the layout size it would have had, but the node is
// measured and placed bigger (the reverse of padding), so a tray can be drawn around the board without growing it.
private fun Modifier.numberSnakeOutset(horizontal: Dp, top: Dp, bottom: Dp): Modifier = layout { measurable, constraints ->
    val h = horizontal.roundToPx()
    val t = top.roundToPx()
    val b = bottom.roundToPx()
    val placeable = measurable.measure(constraints.offset(horizontal = 2 * h, vertical = t + b))
    layout(placeable.width - 2 * h, placeable.height - t - b) { placeable.place(-h, -t) }
}

// The hook art: a 3x3 snapshot of the number trail in a toy tray. Each number is a rainbow candy joined to the next by a
// coloured band, and a wave of bounces slithers along the trail from 1 to 9 and then rests (twin of iOS NumberSnakePreview).
@Composable
private fun NumberSnakePreview(values: List<Int>, s: Float) {
    val colors = LocalAbColors.current
    val accent: Color = gameCardStyle(GameCategoryType.NUMBER_SNAKE).light

    var time by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) { withFrameNanos { now -> time = (now - start) / 1_000_000_000f } }
    }
    val t: Double = time.toDouble()
    val wave: Double = (t * 3.4) % 14.0

    val cellSize = 48f * s
    val gap = 4f * s
    val side = cellSize * 3 + gap * 2

    Box(
        Modifier
            .numberSnakeOutset(horizontal = 6.dp, top = 6.dp, bottom = 12.dp)
            .abToyTray(accent, 6.dp)
    ) {
        Box(Modifier.size(side.dp)) {
            // the coloured bands joining each number to the next
            Canvas(Modifier.size(side.dp)) {
                val cellPx = cellSize.dp.toPx()
                val gapPx = gap.dp.toPx()
                fun center(index: Int): Offset {
                    val r = index / 3
                    val c = index % 3
                    return Offset(c * (cellPx + gapPx) + cellPx / 2f, r * (cellPx + gapPx) + cellPx / 2f)
                }
                for (v in 1 until values.size) {
                    val a = values.indexOf(v)
                    val b = values.indexOf(v + 1)
                    if (a < 0 || b < 0) continue
                    drawLine(colors.toyColor(v), center(a), center(b), gapPx * 2.2f, cap = StrokeCap.Round)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(gap.dp)) {
                for (r in 0 until 3) {
                    Row(horizontalArrangement = Arrangement.spacedBy(gap.dp)) {
                        for (c in 0 until 3) {
                            val i = r * 3 + c
                            val v = values[i]
                            val lift: Float = maxOf(0.0, 1.0 - abs(wave - v.toDouble()) / 1.3).toFloat()
                            val idle: Float = (sin(t * 2.4 + i * 0.5) * 0.025).toFloat()
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .graphicsLayer {
                                        val k = 1f + idle + 0.16f * lift
                                        scaleX = k
                                        scaleY = k
                                        translationY = -6f * lift * s * density
                                    }
                                    .size(cellSize.dp)
                                    .abToyTile(colors.toyColor(v), (10f * s).dp, 3.dp)
                            ) {
                                Text(
                                    "$v", color = Color.White,
                                    style = AbTypography.fixed(22f * s, FontWeight.Black).copy(
                                        shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 2f), 0f)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
