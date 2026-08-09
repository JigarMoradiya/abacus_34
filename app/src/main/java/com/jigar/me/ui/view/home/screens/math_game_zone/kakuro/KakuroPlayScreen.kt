package com.jigar.me.ui.view.home.screens.math_game_zone.kakuro

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.animations.ConfettiRainEffect
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.kakuro.components.KakuroCellType
import com.jigar.me.ui.view.home.screens.math_game_zone.kakuro.components.KakuroUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.kakuro.viewmodel.KakuroPlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)
private val BLUE = Color(0xFF0074D5)
private val WALL_BG = Color(0xFF455A64)
private val CLUE_BG = Color(0xFF37474F)
private val BLANK_BG = Color.White
private val WRONG_BG = Color(0xFFEF9A9A)
private val SOLVED_BG = Color(0xFF81C784)
private val SELECTED_BORDER = Color(0xFF0074D5)
private val CELL_BORDER = Color(0xFF263238)

@Composable
fun KakuroPlayScreen(
    viewModel: KakuroPlayViewModel,
    onBackClick: () -> Unit,
    onNextLevel: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val s = gameScale()
    LaunchedEffect(Unit) { viewModel.start() }

    val cellBase = when {
        state.cols >= 7 -> 44f
        state.rows >= 7 -> 42f
        state.cols >= 6 -> 48f
        else -> 54f
    }
    val cell = (cellBase * s).dp

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BackButtonWithText(title = stringResource(R.string.kakuro_game), onBackClick = onBackClick)
                    Spacer(Modifier.weight(1f))
                }
                Text(
                    text = viewModel.difficulty.displayName + " · " +
                        stringResource(R.string.cross_math_level, viewModel.level),
                    color = ORANGE, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 20.sp.scaled(),
                    modifier = Modifier.align(Alignment.Center)
                        .clip(RoundedCornerShape(100f)).background(Color.White.copy(alpha = 0.9f))
                        .border(2.dp, ORANGE_BORDER, RoundedCornerShape(100f))
                        .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens8)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6),
                    modifier = Modifier.align(Alignment.CenterEnd)
                        .padding(end = AppDimens.Dimens16)
                        .clip(RoundedCornerShape(100f)).background(Color.White.copy(alpha = 0.9f))
                        .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
                ) {
                    Text("⏱", fontSize = (24f * s).sp)
                    Text(
                        timeString(state.elapsed), color = Color(0xFF5D4037),
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 26.sp.scaled()
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                KakuroGrid(state, cell, s) { viewModel.tapCell(it) }

                Spacer(Modifier.width((32f * s).dp))

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    StatsPanel(state, s)
                    Spacer(Modifier.height(AppDimens.Dimens12))
                    DigitPad(cell, s) { viewModel.tapDigit(it) }
                    Spacer(Modifier.height(AppDimens.Dimens10))
                    ToolBar(state, viewModel.canErase, cell, s,
                        onErase = { viewModel.erase() },
                        onRestart = { viewModel.restart() },
                        onHint = { viewModel.useHint() })
                }
            }
        }

        if (state.isGameOver) {
            ResultOverlay(
                stars = state.starsEarned,
                elapsed = state.elapsed,
                hintsUsed = state.hintsUsed,
                hasNextLevel = viewModel.hasNextLevel,
                onNextLevel = onNextLevel,
                onPlayAgain = { viewModel.start() },
                onBack = onBackClick
            )
        }
    }
}

// ── grid ────────────────────────────────────────────────────────────────────

@Composable
private fun KakuroGrid(state: KakuroUiState, cell: Dp, s: Float, onTap: (Int) -> Unit) {
    Column {
        for (r in 0 until state.rows) {
            Row {
                for (c in 0 until state.cols) {
                    KakuroCellView(state, r * state.cols + c, cell, s, onTap)
                }
            }
        }
    }
}

@Composable
private fun KakuroCellView(state: KakuroUiState, i: Int, cell: Dp, s: Float, onTap: (Int) -> Unit) {
    val data = state.cells.getOrNull(i) ?: return
    val isSelected = state.selectedCell == i
    val isWrong = i in state.wrongCells
    val isSolvedRun = i in state.solvedCells
    val isHintCell = i in state.hintLocked
    val placed = state.placed[i]

    // Pop-in animation each time a digit lands.
    val pop = remember { Animatable(1f) }
    LaunchedEffect(placed) {
        if (placed != null && data.type == KakuroCellType.BLANK) {
            pop.snapTo(0.55f)
            pop.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = 700f))
        }
    }
    val shake = remember { Animatable(0f) }
    LaunchedEffect(isWrong) {
        if (isWrong) {
            repeat(3) { shake.animateTo(6f, tween(45)); shake.animateTo(-6f, tween(45)) }
            shake.animateTo(0f, tween(40))
        }
    }

    val bg = when (data.type) {
        KakuroCellType.WALL -> WALL_BG
        KakuroCellType.CLUE -> CLUE_BG
        KakuroCellType.BLANK -> when {
            state.justSolved -> SOLVED_BG
            isWrong -> WRONG_BG
            isSolvedRun && placed != null -> SOLVED_BG
            else -> BLANK_BG
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .graphicsLayer { translationX = shake.value }
            .size(cell)
            .background(bg)
            .drawBehind {
                val w = 1.dp.toPx()
                drawLine(CELL_BORDER, Offset(0f, 0f), Offset(size.width, 0f), w)
                drawLine(CELL_BORDER, Offset(0f, 0f), Offset(0f, size.height), w)
                drawLine(CELL_BORDER, Offset(0f, size.height), Offset(size.width, size.height), w)
                drawLine(CELL_BORDER, Offset(size.width, 0f), Offset(size.width, size.height), w)
            }
            .then(if (isSelected) Modifier.border(2.5.dp, SELECTED_BORDER) else Modifier)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = data.type == KakuroCellType.BLANK
            ) { onTap(i) }
    ) {
        when (data.type) {
            KakuroCellType.CLUE -> {
                // Classic kakuro clue: diagonal split, across sum top-right,
                // down sum bottom-left.
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawLine(Color.White.copy(alpha = 0.5f), Offset(0f, 0f), Offset(size.width, size.height), 1.5.dp.toPx())
                }
                data.acrossSum?.let {
                    Text(
                        "$it", color = Color.White,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (13f * s).sp,
                        modifier = Modifier.align(Alignment.TopEnd).padding(end = (4f * s).dp, top = (2f * s).dp)
                    )
                }
                data.downSum?.let {
                    Text(
                        "$it", color = Color.White,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (13f * s).sp,
                        modifier = Modifier.align(Alignment.BottomStart).padding(start = (4f * s).dp, bottom = (2f * s).dp)
                    )
                }
            }
            KakuroCellType.BLANK -> if (placed != null) {
                Text(
                    "$placed",
                    color = if (isHintCell) Color(0xFF7B1FA2) else BLUE,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (22f * s).sp,
                    modifier = Modifier.graphicsLayer { scaleX = pop.value; scaleY = pop.value }
                )
            }
            else -> {}
        }
    }
}

// ── right panel ─────────────────────────────────────────────────────────────

@Composable
private fun StatsPanel(state: KakuroUiState, s: Float) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clip(RoundedCornerShape(AppDimens.Dimens16)).background(Color.White.copy(alpha = 0.8f))
            .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens10)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6), verticalAlignment = Alignment.CenterVertically) {
            Text("💡", fontSize = (18f * s).sp)
            Text("${state.hintsLeft}", color = Color(0xFFF57F17),
                fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (20f * s).sp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6), verticalAlignment = Alignment.CenterVertically) {
            Text("🧩", fontSize = (18f * s).sp)
            Text(
                "${state.placed.size}/${state.cells.count { it.type == KakuroCellType.BLANK }}",
                color = BLUE, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (20f * s).sp
            )
        }
    }
}

@Composable
private fun DigitPad(cell: Dp, s: Float, onDigit: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy((8f * s).dp)) {
        listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9)).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy((8f * s).dp)) {
                row.forEach { n ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(cell * 0.82f)
                            .clip(RoundedCornerShape(AppDimens.Dimens10))
                            .background(BLUE)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onDigit(n) }
                    ) {
                        Text("$n", color = Color.White,
                            fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (22f * s).sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolBar(
    state: KakuroUiState, canErase: Boolean, cell: Dp, s: Float,
    onErase: () -> Unit, onRestart: () -> Unit, onHint: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12), verticalAlignment = Alignment.CenterVertically) {
        ToolButton(bg = Color(0xFFEF5350), enabled = canErase, size = cell, onClick = onErase) {
            Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = null, tint = Color.White,
                modifier = Modifier.size((20f * s).dp))
        }
        ToolButton(bg = Color(0xFFFFB74D), enabled = true, size = cell, onClick = onRestart) {
            Icon(Icons.Rounded.Refresh, contentDescription = null, tint = Color.White,
                modifier = Modifier.size((22f * s).dp))
        }
        ToolButton(bg = Color(0xFFFFCA28), enabled = state.hintsLeft > 0, size = cell, onClick = onHint) {
            Text("💡${state.hintsLeft}", color = Color.White,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (16f * s).sp)
        }
    }
}

@Composable
private fun ToolButton(bg: Color, enabled: Boolean, size: Dp, onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .graphicsLayer { alpha = if (enabled) 1f else 0.4f }
            .size(size * 0.82f)
            .clip(RoundedCornerShape(AppDimens.Dimens10))
            .background(bg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, enabled = enabled
            ) { onClick() }
    ) { content() }
}

// ── result overlay ──────────────────────────────────────────────────────────

private fun timeString(seconds: Int): String = "%d:%02d".format(seconds / 60, seconds % 60)

@Composable
private fun ResultOverlay(
    stars: Int, elapsed: Int, hintsUsed: Int, hasNextLevel: Boolean,
    onNextLevel: () -> Unit, onPlayAgain: () -> Unit, onBack: () -> Unit
) {
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        // Finishing a Kakuro level is always a win — always celebrate.
        LaunchedEffect(Unit) { if (stars >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        ConfettiRainEffect()
        Column(
            modifier = Modifier.fillMaxWidth(0.6f).clip(RoundedCornerShape(AppDimens.Dimens20))
                .border(3.dp, ORANGE_BORDER, RoundedCornerShape(AppDimens.Dimens20)).background(Color.White)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().height((64f * gameScale()).dp)
                    .background(Brush.horizontalGradient(listOf(Color(0xFFFF8400), Color(0xFFFFC107)))),
                contentAlignment = Alignment.Center
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = AppDimens.Dimens20),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("🧮", fontSize = (34f * gameScale()).sp)
                    Text(stringResource(R.string.balloon_great_job),
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 32.sp.scaled())
                    Text("✨", fontSize = (34f * gameScale()).sp)
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = AppDimens.Dimens30, vertical = AppDimens.Dimens16),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16), verticalAlignment = Alignment.Bottom) {
                    BigStar(stars >= 1, 44, 150); BigStar(stars >= 2, 58, 400); BigStar(stars >= 3, 44, 650)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16)) {
                    StatChipStr("⏱", timeString(elapsed), Color(0xFF5D4037))
                    StatChipStr("💡", "$hintsUsed", Color(0xFFF57F17))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    if (hasNextLevel) {
                        KidsActionButton(text = stringResource(R.string.cross_math_next_level), type = ButtonType.ORANGE,
                            onClick = { if (enabled) onNextLevel() })
                    }
                    KidsActionButton(text = stringResource(R.string.balloon_play_again), type = ButtonType.BLUE,
                        onClick = { if (enabled) onPlayAgain() })
                    KidsActionButton(text = stringResource(R.string.balloon_back), type = ButtonType.PINK,
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
        Text(emoji, fontSize = (16f * gameScale()).sp)
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
