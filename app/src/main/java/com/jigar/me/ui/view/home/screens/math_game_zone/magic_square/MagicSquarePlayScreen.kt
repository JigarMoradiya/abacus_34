package com.jigar.me.ui.view.home.screens.math_game_zone.magic_square

import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.animations.ConfettiRainEffect
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameMascotPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.randomGameCheer
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameScoreboardPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.magic_square.components.MagicSquareUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.magic_square.viewmodel.MagicSquarePlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.getButtonColors
import kotlinx.coroutines.delay

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)
private val BLUE = Color(0xFF0074D5)
private val GREEN = Color(0xFF2E7D32)
private val FIXED_BG = Color(0xFFEDE7F6)
private val SOLVED_BG = Color(0xFFC8E6C9)
private val SELECTED_BG = Color(0xFFFFE0B2)
private val CHIP_GREY = Color(0xFF888888)

private val msScale: Float
    get() = if (DeviceInfo.isLargeTablet) 1.7f else if (DeviceInfo.isTablet) 1.45f else 1.0f

@Composable
fun MagicSquarePlayScreen(
    viewModel: MagicSquarePlayViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val baseCheer = remember { randomGameCheer() }
    val s = msScale
    val cell = (64f * s).dp
    LaunchedEffect(Unit) { viewModel.start() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BackButtonWithText(title = str(R.string.magic_square_game), onBackClick = onBackClick)
                    Spacer(Modifier.weight(1f))
                    Hud(viewModel.config.timerSeconds, viewModel.config.puzzlesGoal, state.timeLeft, state.puzzlesSolved)
                }
                Text(
                    text = str(R.string.magic_square_make_target, viewModel.target),
                    color = ORANGE, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 20.sp.scaled(),
                    modifier = Modifier.align(Alignment.Center)
                        .clip(RoundedCornerShape(100f)).background(Color.White.copy(alpha = 0.9f))
                        .border(2.dp, ORANGE_BORDER, RoundedCornerShape(100f))
                        .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens8)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GameMascotPanel(
                    cheer = when {
                        state.justSolved -> "Awesome! 🎉"
                        state.streak >= 3 -> "On fire! 🔥"
                        else -> baseCheer
                    },
                    celebrate = state.justSolved, s = s,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    GridView(state, viewModel, cell, s)

                    Spacer(Modifier.height(AppDimens.Dimens16))

                    // Number palette
                    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10), verticalAlignment = Alignment.CenterVertically) {
                        state.palette.forEach { n ->
                            val interaction = remember { MutableInteractionSource() }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(cell * 0.8f)
                                    .clip(RoundedCornerShape(AppDimens.Dimens12))
                                    .background(BLUE)
                                    .clickable(interactionSource = interaction, indication = null) { viewModel.placeNumber(n) }
                            ) {
                                Text("$n", color = Color.White,
                                    fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (30f * s).sp)
                            }
                        }
                    }
                }

                GameScoreboardPanel(state.score, viewModel.multiplier, viewModel.bestScore, s, Modifier.weight(1f).fillMaxHeight())
            }
        }

        if (state.isGameOver) {
            ResultOverlay(state.score, state.puzzlesSolved, viewModel.bestScore,
                onPlayAgain = { viewModel.start() }, onBack = onBackClick)
        }
    }
}

@Composable
private fun GridView(state: MagicSquareUiState, viewModel: MagicSquarePlayViewModel, cell: androidx.compose.ui.unit.Dp, s: Float) {
    Column(verticalArrangement = Arrangement.spacedBy((6f * s).dp)) {
        for (r in 0 until 3) {
            Row(horizontalArrangement = Arrangement.spacedBy((6f * s).dp), verticalAlignment = Alignment.CenterVertically) {
                for (c in 0 until 3) {
                    CellView(state, viewModel, r * 3 + c, cell, s)
                }
                SumChip(viewModel.rowSum(state.grid, r), viewModel.rowComplete(state.grid, r), cell, s)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy((6f * s).dp), verticalAlignment = Alignment.CenterVertically) {
            for (c in 0 until 3) {
                Box(modifier = Modifier.width(cell), contentAlignment = Alignment.Center) { // center the chip under its column
                    SumChip(viewModel.colSum(state.grid, c), viewModel.colComplete(state.grid, c), cell, s)
                }
            }
            Spacer(Modifier.size(cell * 0.72f, cell * 0.5f)) // filler under the row-chip column
        }
    }
}

@Composable
private fun CellView(state: MagicSquareUiState, viewModel: MagicSquarePlayViewModel, i: Int, cell: androidx.compose.ui.unit.Dp, s: Float) {
    val value = state.grid[i]
    val isFixed = state.fixed[i]
    val isSelected = state.selectedIndex == i
    val bg = when {
        state.justSolved -> SOLVED_BG
        isFixed -> FIXED_BG
        value != 0 -> BLUE
        isSelected -> SELECTED_BG
        else -> Color.White
    }
    val fg = if (value != 0 && !isFixed && !state.justSolved) Color.White else Color.Black
    val borderColor = if (isSelected) ORANGE_BORDER else Color.Black.copy(alpha = 0.12f)
    val scale by animateFloatAsState(if (state.justSolved) 1.05f else 1f, spring(dampingRatio = 0.5f), label = "")
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .size(cell)
            .clip(RoundedCornerShape(AppDimens.Dimens12))
            .background(bg)
            .border(if (isSelected) 3.dp else 1.dp, borderColor, RoundedCornerShape(AppDimens.Dimens12))
            .clickable { viewModel.tapCell(i) }
    ) {
        Text(if (value == 0) "" else "$value", color = fg,
            fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (30f * s).sp)
    }
}

@Composable
private fun SumChip(sum: Int, complete: Boolean, cell: androidx.compose.ui.unit.Dp, s: Float) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(cell * 0.72f, cell * 0.5f)
            .clip(RoundedCornerShape(AppDimens.Dimens8))
            .background(if (complete) GREEN else Color.White.copy(alpha = 0.7f))
    ) {
        Text("$sum", color = if (complete) Color.White else CHIP_GREY,
            fontFamily = FontFamily(Font(R.font.font_bold)), fontSize = (18f * s).sp)
    }
}

@Composable private fun str(id: Int): String = androidx.compose.ui.res.stringResource(id)
@Composable private fun str(id: Int, vararg args: Any): String = androidx.compose.ui.res.stringResource(id, *args)

@Composable
private fun Hud(timerSeconds: Int?, puzzlesGoal: Int?, timeLeft: Int, puzzlesSolved: Int) {
    val font = FontFamily(Font(R.font.font_bold))
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16), verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = AppDimens.Dimens12).clip(RoundedCornerShape(AppDimens.Dimens12))
            .background(Color.White.copy(alpha = 0.75f)).padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6)
    ) {
        if (timerSeconds != null) Text("⏱ $timeLeft", color = Color.Black, fontFamily = font, fontSize = 16.sp.scaled())
        else if (puzzlesGoal != null) Text("🧩 $puzzlesSolved/$puzzlesGoal", color = Color.Black, fontFamily = font, fontSize = 16.sp.scaled())
    }
}

@Composable
private fun ResultOverlay(score: Int, puzzlesSolved: Int, bestScore: Int, onPlayAgain: () -> Unit, onBack: () -> Unit) {
    val starCount = when { puzzlesSolved >= 5 -> 3; puzzlesSolved >= 3 -> 2; else -> 1 }
    val accentColors = getButtonColors(ButtonType.ORANGE)
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { if (starCount >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        if (starCount >= 2) ConfettiRainEffect()
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
                    Text("🧩", fontSize = (28f * gameScale()).sp)
                    Text(
                        str(R.string.balloon_great_job),
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                        fontSize = 30.sp.scaled(),
                        style = TextStyle(shadow = Shadow(color = accentColors.base.copy(alpha = 0.9f), offset = Offset(1.5f, 1.5f), blurRadius = 0f)),
                        modifier = Modifier.padding(horizontal = AppDimens.Dimens8)
                    )
                    Text("✨", fontSize = (28f * gameScale()).sp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8), verticalAlignment = Alignment.Bottom) {
                    BigStar(starCount >= 1, 32, 150); BigStar(starCount >= 2, 40, 400); BigStar(starCount >= 3, 32, 650)
                }
                Text(str(R.string.balloon_final_score) + ": $score", color = Color.White,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 26.sp.scaled())
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16)) {
                    StatChip("🧩", puzzlesSolved, GREEN); StatChip("🏆", bestScore, ORANGE_BORDER)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    KidsActionButton(text = str(R.string.balloon_play_again), type = ButtonType.POSITIVE, onClick = { if (enabled) onPlayAgain() })
                    KidsActionButton(text = str(R.string.balloon_back), type = ButtonType.NEGATIVE, onClick = { if (enabled) onBack() })
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
                Text("🧩", fontSize = 32.sp)
            }
        }
    }
}

@Composable
private fun StatChip(emoji: String, value: Int, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4), verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clip(RoundedCornerShape(100f)).background(Color.White)
            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6)) {
        Text(emoji, fontSize = (16f * gameScale()).sp)
        Text("$value", color = color, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 20.sp.scaled())
    }
}

@Composable
private fun BigStar(earned: Boolean, sizeSp: Int, delayMs: Long) {
    val scale = remember { Animatable(0.01f) }
    LaunchedEffect(Unit) { delay(delayMs); scale.animateTo(1f, spring(dampingRatio = 0.5f)) }
    Text("⭐", fontSize = (sizeSp * gameScale()).sp, modifier = Modifier.graphicsLayer { scaleX = scale.value; scaleY = scale.value; alpha = if (earned) 1f else 0.35f })
}
