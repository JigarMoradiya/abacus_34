package com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku

import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.animations.ConfettiRainEffect
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku.components.Cage
import com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku.components.CageOp
import com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku.components.CalcudokuUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku.viewmodel.CalcudokuPlayViewModel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameMascotPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.randomGameCheer
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)
private val BLUE = Color(0xFF0074D5)
private val GREEN = Color(0xFF2E7D32)
private val CAGE_LINE = Color(0xFF455A64)
private val FIXED_BG = Color(0xFFEDE7F6)
private val SOLVED_BG = Color(0xFFC8E6C9)
private val CAGE_DONE_BG = Color(0xFFE8F5E9)
private val SELECTED_BG = Color(0xFFFFE0B2)
private val CONFLICT_BG = Color(0xFFFFCDD2)

private val cdScale: Float
    get() = if (DeviceInfo.isLargeTablet) 1.7f else if (DeviceInfo.isTablet) 1.45f else 1.0f

@Composable
fun CalcudokuPlayScreen(
    viewModel: CalcudokuPlayViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val baseCheer = remember { randomGameCheer() }
    val s = cdScale
    val cellBase = if (state.size <= 3) 64f else if (state.size == 4) 56f else 46f
    val cell = (cellBase * s).dp
    LaunchedEffect(Unit) { viewModel.start() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BackButtonWithText(title = str(R.string.calcudoku_game), onBackClick = onBackClick)
                    Spacer(Modifier.weight(1f))
                }
                Text(
                    text = str(R.string.calcudoku_prompt),
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
                    GridView(state, cell, s) { viewModel.tapCell(it) }

                    Spacer(Modifier.height(AppDimens.Dimens8))

                    HintBar(state, s)

                    Spacer(Modifier.height(AppDimens.Dimens8))

                    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8), verticalAlignment = Alignment.CenterVertically) {
                        for (n in 1..state.size) {
                            val interaction = remember { MutableInteractionSource() }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(cell * 0.82f)
                                    .clip(RoundedCornerShape(AppDimens.Dimens10)).background(BLUE)
                                    .clickable(interactionSource = interaction, indication = null) { viewModel.placeNumber(n) }
                            ) {
                                Text("$n", color = Color.White, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (26f * s).sp)
                            }
                        }
                        val eraseInteraction = remember { MutableInteractionSource() }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(cell * 0.82f)
                                .clip(RoundedCornerShape(AppDimens.Dimens10)).background(Color(0xFF90A4AE))
                                .clickable(interactionSource = eraseInteraction, indication = null) { viewModel.erase() }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = null,
                                tint = Color.White, modifier = Modifier.size((22f * s).dp))
                        }

                        val resetInteraction = remember { MutableInteractionSource() }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(cell * 0.82f)
                                .clip(RoundedCornerShape(AppDimens.Dimens10)).background(Color(0xFFFFB74D))
                                .clickable(interactionSource = resetInteraction, indication = null) { viewModel.reset() }
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = null,
                                tint = Color.White, modifier = Modifier.size((22f * s).dp))
                        }
                    }
                }

                CalcudokuScoreboard(state.score, state.elapsed, viewModel.bestTime, s, Modifier.weight(1f).fillMaxHeight())
            }
        }

        if (state.isGameOver) {
            ResultOverlay(
                score = state.score,
                elapsed = state.elapsed,
                bestTime = viewModel.bestTime,
                speedBonus = state.lastSpeedBonus,
                par = viewModel.config.parSeconds,
                onPlayAgain = { viewModel.start() }, onBack = onBackClick)
        }
    }
}

@Composable
private fun GridView(state: CalcudokuUiState, cell: androidx.compose.ui.unit.Dp, s: Float, onTap: (Int) -> Unit) {
    val g = cell * state.size
    Box(modifier = Modifier.size(g).clip(RoundedCornerShape(AppDimens.Dimens10))) {
        Column {
            for (r in 0 until state.size) {
                Row {
                    for (c in 0 until state.size) {
                        CellView(state, r * state.size + c, cell, s, onTap)
                    }
                }
            }
        }
        CalcudokuGridLines(state.size, cell, state.cageId)
    }
}

@Composable
private fun CellView(state: CalcudokuUiState, i: Int, cell: androidx.compose.ui.unit.Dp, s: Float, onTap: (Int) -> Unit) {
    val v = state.grid[i]
    val isFixed = state.fixed[i]
    val isSelected = state.selectedIndex == i
    val conflict = hasConflict(state.grid, state.size, i)
    val cageDone = cageSatisfiedForCell(state.cages, state.grid, i)
    val bg = when {
        state.justSolved -> SOLVED_BG
        conflict -> CONFLICT_BG
        isSelected -> SELECTED_BG
        isFixed -> FIXED_BG
        cageDone -> CAGE_DONE_BG
        else -> Color.White
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(cell).background(bg).clickable { onTap(i) }
    ) {
        clueTextFor(state.cages, i)?.let { clue ->
            Text(clue, color = CAGE_LINE, fontFamily = FontFamily(Font(R.font.font_bold)), fontSize = (12f * s).sp,
                modifier = Modifier.align(Alignment.TopStart).padding(start = (4f * s).dp, top = (3f * s).dp))
        }
        if (v != 0) {
            Text("$v", color = if (isFixed) Color.Black else BLUE,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (26f * s).sp)
        }
    }
}

// Draws all grid lines in one pass so corners/junctions join cleanly.
@Composable
fun CalcudokuGridLines(n: Int, cell: androidx.compose.ui.unit.Dp, cageId: List<Int>, modifier: Modifier = Modifier) {
    val thin = Color.Black.copy(alpha = 0.12f)
    Canvas(modifier = modifier.size(cell * n)) {
        val c = cell.toPx()
        val g = c * n
        for (k in 1 until n) {
            val p = k * c
            drawLine(thin, Offset(p, 0f), Offset(p, g), 1.dp.toPx())
            drawLine(thin, Offset(0f, p), Offset(g, p), 1.dp.toPx())
        }
        val thickW = 3.dp.toPx()
        for (i in 0 until n * n) {
            val r = i / n; val col = i % n
            val x0 = col * c; val y0 = r * c
            val top = r == 0 || cageId[i] != cageId[i - n]
            val left = col == 0 || cageId[i] != cageId[i - 1]
            if (r > 0 && top) drawLine(CAGE_LINE, Offset(x0, y0), Offset(x0 + c, y0), thickW, cap = StrokeCap.Round)
            if (col > 0 && left) drawLine(CAGE_LINE, Offset(x0, y0), Offset(x0, y0 + c), thickW, cap = StrokeCap.Round)
        }
        val inset = 1.5.dp.toPx()
        drawRoundRect(CAGE_LINE, topLeft = Offset(inset, inset), size = Size(g - 2 * inset, g - 2 * inset),
            cornerRadius = CornerRadius(10.dp.toPx()), style = Stroke(width = thickW))
    }
}

// ── pure render helpers ──────────────────────────────────────────────
private fun clueTextFor(cages: List<Cage>, i: Int): String? = cages.firstOrNull { it.clueIndex == i }?.clueText

// Separate amber "lightbulb" hint pill below the grid (distinct from the cheer bubble).
@Composable
private fun HintBar(state: CalcudokuUiState, s: Float) {
    val hint = cageHint(state)
    val bg = if (hint == null) Color.White.copy(alpha = 0.7f) else Color(0xFFFFF3C4)
    val border = if (hint == null) Color.Transparent else Color(0xFFFFCA28)
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clip(RoundedCornerShape(100f)).background(bg)
            .border(1.5.dp, border, RoundedCornerShape(100f))
            .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens6)
    ) {
        Text("💡", fontSize = (15f * s).sp)
        Text(hint ?: "Tap a box for a hint",
            color = if (hint == null) Color(0xFF9E9E9E) else Color(0xFF5D4037),
            fontFamily = FontFamily(Font(R.font.font_bold)), fontSize = (15f * s).sp, maxLines = 1)
    }
}

// Plain-English explanation of the selected cell's cage (for the hint pill).
private fun cageHint(state: CalcudokuUiState): String? {
    val i = state.selectedIndex ?: return null
    val cage = state.cages.firstOrNull { it.cells.contains(i) } ?: return null
    val count = cage.cells.size
    return when (cage.op) {
        CageOp.GIVEN -> "This box is ${cage.target}."
        CageOp.PLUS -> "These $count boxes add up to ${cage.target}."
        CageOp.MINUS -> "These 2 boxes differ by ${cage.target}."
        CageOp.TIMES -> "These $count boxes multiply to make ${cage.target}."
        CageOp.DIVIDE -> "One box ÷ the other makes ${cage.target}."
    }
}

private fun hasConflict(grid: List<Int>, size: Int, i: Int): Boolean {
    val v = grid[i]
    if (v == 0) return false
    val r = i / size; val c = i % size
    for (cc in 0 until size) if (cc != c && grid[r * size + cc] == v) return true
    for (rr in 0 until size) if (rr != r && grid[rr * size + c] == v) return true
    return false
}

private fun cageSatisfiedForCell(cages: List<Cage>, grid: List<Int>, i: Int): Boolean {
    val cage = cages.firstOrNull { it.cells.contains(i) } ?: return false
    val vals = cage.cells.map { grid[it] }
    if (vals.contains(0)) return false
    return when (cage.op) {
        CageOp.GIVEN -> vals[0] == cage.target
        CageOp.PLUS -> vals.sum() == cage.target
        CageOp.TIMES -> vals.reduce { a, b -> a * b } == cage.target
        CageOp.MINUS -> abs(vals[0] - vals[1]) == cage.target
        CageOp.DIVIDE -> {
            val hi = max(vals[0], vals[1]); val lo = min(vals[0], vals[1])
            lo != 0 && hi % lo == 0 && hi / lo == cage.target
        }
    }
}

@Composable private fun str(id: Int): String = androidx.compose.ui.res.stringResource(id)

private fun timeString(seconds: Int): String = "%d:%02d".format(seconds / 60, seconds % 60)

@Composable
private fun CalcudokuScoreboard(score: Int, elapsed: Int, bestTime: Int, s: Float, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens16),
            modifier = Modifier.clip(RoundedCornerShape(AppDimens.Dimens16)).background(Color.White.copy(alpha = 0.8f))
                .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens16)
        ) {
            ScoreRow("⭐", "$score", BLUE, s)
            ScoreRow("⏱", timeString(elapsed), Color(0xFF5D4037), s)
            ScoreRow("🏆", if (bestTime > 0) timeString(bestTime) else "—", ORANGE_BORDER, s)
        }
    }
}

@Composable
private fun ScoreRow(emoji: String, value: String, color: Color, s: Float) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6), verticalAlignment = Alignment.CenterVertically) {
        Text(emoji, fontSize = (22f * s).sp)
        Text(value, color = color, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (22f * s).sp)
    }
}

@Composable
private fun ResultOverlay(score: Int, elapsed: Int, bestTime: Int, speedBonus: Int, par: Int, onPlayAgain: () -> Unit, onBack: () -> Unit) {
    val starCount = when {
        elapsed <= par * 6 / 10 -> 3
        elapsed <= par * 9 / 10 -> 2
        else -> 1
    }
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { if (starCount >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        if (starCount >= 2) ConfettiRainEffect()
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
                    Text(str(R.string.balloon_great_job), color = Color.White,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 32.sp.scaled())
                    Text("✨", fontSize = (34f * gameScale()).sp)
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = AppDimens.Dimens30, vertical = AppDimens.Dimens16),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16), verticalAlignment = Alignment.Bottom) {
                    BigStar(starCount >= 1, 44, 150); BigStar(starCount >= 2, 58, 400); BigStar(starCount >= 3, 44, 650)
                }
                Text(str(R.string.balloon_final_score) + ": $score", color = BLUE,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 28.sp.scaled())
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16)) {
                    StatChipStr("⏱", timeString(elapsed), Color(0xFF5D4037))
                    StatChip("⚡", speedBonus, BLUE)
                    StatChipStr("🏆", if (bestTime > 0) timeString(bestTime) else "—", ORANGE_BORDER)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    KidsActionButton(text = str(R.string.balloon_play_again), type = ButtonType.ORANGE, onClick = { if (enabled) onPlayAgain() })
                    KidsActionButton(text = str(R.string.balloon_back), type = ButtonType.BLUE, onClick = { if (enabled) onBack() })
                }
            }
        }
    }
}

@Composable
private fun StatChip(emoji: String, value: Int, color: Color) = StatChipStr(emoji, "$value", color)

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
    Text("⭐", fontSize = (sizeSp * gameScale()).sp, modifier = Modifier.graphicsLayer { scaleX = scale.value; scaleY = scale.value; alpha = if (earned) 1f else 0.35f })
}
