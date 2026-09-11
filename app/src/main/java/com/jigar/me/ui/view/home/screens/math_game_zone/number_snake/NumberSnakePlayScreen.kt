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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.number_snake.components.NumberSnakeConfig
import com.jigar.me.ui.view.home.screens.math_game_zone.number_snake.components.NumberSnakeUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.number_snake.viewmodel.NumberSnakePlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.getButtonColors
import kotlinx.coroutines.delay

private val GOLD = Color(0xFFFF6F00)
private val GOLD_BORDER = Color(0xFFFFA000)
private val BLUE = Color(0xFF0074D5)
private val GIVEN_BG = Color(0xFFFFCC80)
private val EMPTY_BG = Color(0xFFCFD8DC)
private val FILLED_BG = Color(0xFFC8E6C9)
private val HINT_BG = Color(0xFFB3E5FC)
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
                    // Clears the number in the currently selected cell so a
                    // kid can undo a placement without waiting for a wrong-flash.
                    Text("🧹", color = SELECTED_BORDER,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 18.sp.scaled(),
                        modifier = Modifier.clip(RoundedCornerShape(100f)).background(Color.White.copy(alpha = 0.9f))
                            .border(2.dp, SELECTED_BORDER, RoundedCornerShape(100f))
                            .padding(horizontal = AppDimens.Dimens14, vertical = AppDimens.Dimens6)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() }, indication = null
                            ) { state.selectedIndex?.let { viewModel.clearCell(it) } }
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
            ResultOverlay(state = state, config = viewModel.config, bestTime = viewModel.bestTime,
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
            // Purely local rule: any two GRID-ADJACENT cells whose visible
            // values are consecutive get connected — regardless of whether
            // that matches the puzzle's one true solution. Checking against
            // the solution key instead would only draw a line for placements
            // that happen to be "officially correct", which quietly tells the
            // kid their guess is right before they've solved anything.
            val n = state.size
            for (r in 0 until n) {
                for (c in 0 until n) {
                    val i = r * n + c
                    val v = state.grid[i]
                    if (v == 0) continue
                    if (c + 1 < n) {
                        val i2 = i + 1
                        val v2 = state.grid[i2]
                        if (v2 != 0 && kotlin.math.abs(v - v2) == 1) {
                            drawLine(Color(0xFFFF6F00), centerOf(i), centerOf(i2), gapPx * 1.1f, cap = StrokeCap.Round)
                        }
                    }
                    if (r + 1 < n) {
                        val i2 = i + n
                        val v2 = state.grid[i2]
                        if (v2 != 0 && kotlin.math.abs(v - v2) == 1) {
                            drawLine(Color(0xFFFF6F00), centerOf(i), centerOf(i2), gapPx * 1.1f, cap = StrokeCap.Round)
                        }
                    }
                }
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
                            isHint -> HINT_BG
                            value != 0 -> FILLED_BG
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
    // horizontalScroll makes the Row report itself as filling the available
    // width regardless of content, so a plain Box+contentAlignment wrapper
    // around it can't center anything — spacedBy's own alignment param is
    // what actually centers the tiles when they fit, while still allowing
    // the Row to scroll once they overflow.
    Row(
        horizontalArrangement = Arrangement.spacedBy((8f * s).dp, Alignment.CenterHorizontally),
        modifier = Modifier
            .fillMaxWidth()
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

@Composable
private fun ResultOverlay(
    state: NumberSnakeUiState, config: NumberSnakeConfig, bestTime: Int,
    onPlayAgain: () -> Unit, onBack: () -> Unit
) {
    val stars = NumberSnakeConfig.stars(state.hintsUsed, state.elapsed, config.parSeconds)
    val accentColors = getButtonColors(ButtonType.ORANGE)
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { if (stars >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        // "Candy Pop" celebration card — saturated gradient block with a white badge overlapping the top edge.
        Box(modifier = Modifier.fillMaxWidth(0.6f), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp, RoundedCornerShape(AppDimens.Dimens20 * 1.2f), ambientColor = accentColors.base, spotColor = accentColors.base)
                    .background(accentColors.gradient, RoundedCornerShape(AppDimens.Dimens20 * 1.2f))
                    .padding(horizontal = AppDimens.Dimens30, vertical = AppDimens.Dimens20),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
            ) {
                Spacer(Modifier.height(AppDimens.Dimens20))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🐍", fontSize = (28f * gameScale()).sp)
                    Text(
                        stringResource(if (stars >= 2) R.string.balloon_great_job else R.string.good_try),
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                        fontSize = 30.sp.scaled(),
                        style = TextStyle(shadow = Shadow(color = accentColors.base.copy(alpha = 0.9f), offset = Offset(1.5f, 1.5f), blurRadius = 0f)),
                        modifier = Modifier.padding(horizontal = AppDimens.Dimens8)
                    )
                    Text("🔢", fontSize = (28f * gameScale()).sp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8), verticalAlignment = Alignment.Bottom) {
                    BigStar(stars >= 1, 32, 150); BigStar(stars >= 2, 40, 400); BigStar(stars >= 3, 32, 650)
                }
                Text(stringResource(R.string.balloon_final_score) + ": ${state.score}", color = Color.White,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 26.sp.scaled())
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16)) {
                    StatChipStr("⏱", "%d:%02d".format(state.elapsed / 60, state.elapsed % 60), GOLD)
                    StatChipStr("🏆", if (bestTime > 0) "%d:%02d".format(bestTime / 60, bestTime % 60) else "—", GOLD_BORDER)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    KidsActionButton(text = stringResource(R.string.balloon_play_again), type = ButtonType.POSITIVE,
                        onClick = { if (enabled) onPlayAgain() })
                    KidsActionButton(text = stringResource(R.string.balloon_back), type = ButtonType.NEGATIVE,
                        onClick = { if (enabled) onBack() })
                }
            }

            Box(
                modifier = Modifier
                    .offset(y = (-28).dp)
                    .size(64.dp)
                    .shadow(8.dp, CircleShape)
                    .background(Color.White, CircleShape)
                    .border(4.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🐍", fontSize = 32.sp)
            }
        }
    }
}

@Composable
private fun StatChipStr(emoji: String, value: String, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4), verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clip(RoundedCornerShape(100f)).background(Color.White)
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
