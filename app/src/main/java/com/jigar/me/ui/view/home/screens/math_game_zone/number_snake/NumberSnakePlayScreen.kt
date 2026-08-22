package com.jigar.me.ui.view.home.screens.math_game_zone.number_snake

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.home.screens.math_game_zone.common.PauseTimerInBackground
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.animations.ConfettiRainEffect
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.number_snake.components.NumberSnakeConfig
import com.jigar.me.ui.view.home.screens.math_game_zone.number_snake.components.NumberSnakeUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.number_snake.viewmodel.NumberSnakePlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay

private val GOLD = Color(0xFFFF6F00)
private val GOLD_BORDER = Color(0xFFFFA000)
private val BLUE = Color(0xFF0074D5)
private val GIVEN_BG = Color(0xFFFFECB3)
private val EMPTY_BG = Color(0xFFFFF8E1)
private val SELECTED_BORDER = Color(0xFFE91E63)
private val WRONG_RED = Color(0xFFE53935)
private val TRAY_BG = Color(0xFFFF6F00)

@Composable
fun NumberSnakePlayScreen(
    viewModel: NumberSnakePlayViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val s = gameScale()
    LaunchedEffect(Unit) { viewModel.start() }
    PauseTimerInBackground { viewModel.setTimerPaused(it) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BackButtonWithText(title = stringResource(R.string.number_snake_game), onBackClick = onBackClick)
                    Spacer(Modifier.weight(1f))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text("💡 ${state.hintsLeft}", color = GOLD,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 18.sp.scaled(),
                        modifier = Modifier.clip(RoundedCornerShape(100f)).background(Color.White.copy(alpha = 0.9f))
                            .border(2.dp, GOLD_BORDER, RoundedCornerShape(100f))
                            .padding(horizontal = AppDimens.Dimens14, vertical = AppDimens.Dimens6)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() }, indication = null
                            ) { viewModel.useHint() }
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6),
                    modifier = Modifier.align(Alignment.CenterEnd)
                        .padding(end = AppDimens.Dimens16)
                        .clip(RoundedCornerShape(100f)).background(Color.White.copy(alpha = 0.9f))
                        .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
                ) {
                    Text("⏱", fontSize = (22f * s).sp)
                    Text(
                        "%d:%02d".format(state.elapsed / 60, state.elapsed % 60),
                        color = Color(0xFF5D4037),
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 22.sp.scaled()
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            NumberGrid(state, s, onTapCell = { viewModel.tapCell(it) })

            Spacer(Modifier.height((16f * s).dp))

            Tray(state, s, onTapNumber = { viewModel.tapTrayNumber(it) })

            Spacer(Modifier.weight(1f))
        }

        if (state.isGameOver) {
            ResultOverlay(state = state, config = viewModel.config, best = viewModel.bestScore,
                onPlayAgain = { viewModel.start() }, onBack = onBackClick)
        }
    }
}

@Composable
private fun NumberGrid(state: NumberSnakeUiState, s: Float, onTapCell: (Int) -> Unit) {
    val shake = remember { Animatable(0f) }
    LaunchedEffect(state.wrongFlash) {
        if (state.wrongFlash) {
            repeat(3) { shake.animateTo(6f, tween(40)); shake.animateTo(-6f, tween(40)) }
            shake.animateTo(0f, tween(35))
        }
    }
    val cellSize = when (state.size) {
        4 -> 56f; 5 -> 48f; else -> 42f
    } * s
    val gap = 9f * s

    // Where each number 1..total currently sits, so we can draw a trail
    // segment the instant both its neighbours are correctly in place —
    // the "snake" visibly grows as the kid builds the sequence.
    val positionOfNumber = remember(state.solution) {
        IntArray(state.solution.size + 1).also { arr ->
            state.solution.forEachIndexed { index, number -> if (number in arr.indices) arr[number] = index }
        }
    }

    Box(modifier = Modifier.graphicsLayer { translationX = shake.value }) {
        Canvas(
            modifier = Modifier.size(
                (cellSize * state.size + gap * (state.size - 1)).dp
            )
        ) {
            val cellPx = cellSize.dp.toPx()
            val gapPx = gap.dp.toPx()
            fun centerOf(index: Int): Offset {
                val r = index / state.size; val c = index % state.size
                return Offset(
                    c * (cellPx + gapPx) + cellPx / 2f,
                    r * (cellPx + gapPx) + cellPx / 2f
                )
            }
            val total = state.size * state.size
            for (num in 1 until total) {
                val i1 = positionOfNumber[num]
                val i2 = positionOfNumber[num + 1]
                if (state.grid[i1] != num || state.grid[i2] != num + 1) continue
                drawLine(
                    color = Color(0xFFFF6F00),
                    start = centerOf(i1),
                    end = centerOf(i2),
                    strokeWidth = gapPx * 1.1f,
                    cap = StrokeCap.Round
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(gap.dp)) {
            for (r in 0 until state.size) {
                Row(horizontalArrangement = Arrangement.spacedBy(gap.dp)) {
                    for (c in 0 until state.size) {
                        val i = r * state.size + c
                        val value = state.grid[i]
                        val isGiven = state.given[i] != 0
                        val isHint = i in state.hintLocked
                        val isSelected = state.selectedIndex == i
                        val bg = when {
                            state.wrongFlash && value != 0 -> WRONG_RED.copy(alpha = 0.35f)
                            isGiven -> GIVEN_BG
                            isHint -> Color(0xFFFFE082)
                            value != 0 -> Color.White
                            else -> EMPTY_BG
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(cellSize.dp)
                                .clip(RoundedCornerShape(AppDimens.Dimens6))
                                .background(bg)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) SELECTED_BORDER else GOLD_BORDER.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(AppDimens.Dimens6)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    enabled = !isGiven && !isHint
                                ) { onTapCell(i) }
                        ) {
                            if (value != 0) {
                                Text(
                                    "$value",
                                    color = if (isGiven) Color(0xFFE65100) else GOLD,
                                    fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                                    fontSize = (cellSize * 0.42f).sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Tray(state: NumberSnakeUiState, s: Float, onTapNumber: (Int) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Row(
            horizontalArrangement = Arrangement.spacedBy((8f * s).dp),
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = AppDimens.Dimens16)
        ) {
            state.tray.forEach { n ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size((40f * s).dp)
                        .clip(RoundedCornerShape(AppDimens.Dimens8))
                        .background(TRAY_BG)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTapNumber(n) }
                ) {
                    Text("$n", color = Color.White,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (18f * s).sp)
                }
            }
        }
    }
}

@Composable
private fun ResultOverlay(
    state: NumberSnakeUiState, config: NumberSnakeConfig, best: Int,
    onPlayAgain: () -> Unit, onBack: () -> Unit
) {
    val stars = NumberSnakeConfig.stars(state.hintsUsed, state.elapsed, config.parSeconds)
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { if (stars >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        ConfettiRainEffect()
        Column(
            modifier = Modifier.fillMaxWidth(0.6f).clip(RoundedCornerShape(AppDimens.Dimens20))
                .border(3.dp, GOLD_BORDER, RoundedCornerShape(AppDimens.Dimens20)).background(Color.White)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().height((64f * gameScale()).dp)
                    .background(Brush.horizontalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF6F00)))),
                contentAlignment = Alignment.Center
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = AppDimens.Dimens20),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("🐍", fontSize = (34f * gameScale()).sp)
                    Text(stringResource(if (stars >= 2) R.string.balloon_great_job else R.string.good_try),
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 32.sp.scaled())
                    Text("🔢", fontSize = (34f * gameScale()).sp)
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = AppDimens.Dimens30, vertical = AppDimens.Dimens16),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16), verticalAlignment = Alignment.Bottom) {
                    BigStar(stars >= 1, 44, 150); BigStar(stars >= 2, 58, 400); BigStar(stars >= 3, 44, 650)
                }
                Text(stringResource(R.string.balloon_final_score) + ": ${state.score}", color = BLUE,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 28.sp.scaled())
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16)) {
                    StatChipStr("⏱", "%d:%02d".format(state.elapsed / 60, state.elapsed % 60), GOLD)
                    StatChipStr("🏆", "$best", GOLD_BORDER)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    KidsActionButton(text = stringResource(R.string.balloon_play_again), type = ButtonType.ORANGE,
                        onClick = { if (enabled) onPlayAgain() })
                    KidsActionButton(text = stringResource(R.string.balloon_back), type = ButtonType.BLUE,
                        onClick = { if (enabled) onBack() })
                }
            }
        }
    }
}

@Composable
private fun StatChipStr(emoji: String, value: String, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4), verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clip(RoundedCornerShape(100f)).background(color.copy(alpha = 0.12f))
            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6)) {
        Text(emoji, color = color, fontSize = (16f * gameScale()).sp,
            fontFamily = FontFamily(Font(R.font.font_extra_bold)))
        Text(value, color = color, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 20.sp.scaled(),
            maxLines = 1, softWrap = false)
    }
}

@Composable
private fun BigStar(earned: Boolean, sizeSp: Int, delayMs: Long) {
    val scale = remember { Animatable(0.01f) }
    LaunchedEffect(Unit) { delay(delayMs); scale.animateTo(1f, spring(dampingRatio = 0.5f)) }
    Text("⭐", fontSize = (sizeSp * gameScale()).sp,
        modifier = Modifier.graphicsLayer { scaleX = scale.value; scaleY = scale.value; alpha = if (earned) 1f else 0.35f })
}
