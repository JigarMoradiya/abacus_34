package com.jigar.me.ui.view.home.screens.math_game_zone.cross_math

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.drawscope.Stroke
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.rounded.Undo
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
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
import com.jigar.me.ui.view.home.theme.getButtonColors
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.components.CrossCellType
import com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.components.CrossMathUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.components.opDisplay
import com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.viewmodel.CrossMathPlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)
private val BLUE = Color(0xFF0074D5)
private val GIVEN_BG = Color(0xFFF2CE5A)       // solid yellow, like the reference
private val BLANK_BG = Color(0xFFFAF3D3)       // pale yellow
private val TRAY_BG = Color(0xFF66BB6A)        // green tray tiles
private val SELECTED_BORDER = Color(0xFF0074D5)
// Run highlighting: the player's filled cells get the strong shade, the
// prefilled (given/operator/equals) cells of the same run a lighter one.
private val WRONG_BG = Color(0xFFEF9A9A)
private val WRONG_BG_GIVEN = Color(0xFFFFCDD2)
private val SOLVED_BG = Color(0xFF81C784)
private val SOLVED_BG_GIVEN = Color(0xFFC8E6C9)
private val CELL_BORDER = Color(0xFF6D6D6D)

@Composable
fun CrossMathPlayScreen(
    viewModel: CrossMathPlayViewModel,
    onBackClick: () -> Unit,
    onNextLevel: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val s = gameScale()
    LaunchedEffect(Unit) { viewModel.start() }
    // A phone call or the home button freezes the clock - fair play.
    PauseTimerInBackground { viewModel.setTimerPaused(it) }

    val cellBase = when {
        state.cols >= 11 -> 34f
        state.cols >= 9 -> 38f
        state.cols >= 7 -> 44f
        state.rows >= 7 -> 40f
        else -> 52f
    }
    val cell = (cellBase * s).dp

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BackButtonWithText(title = stringResource(R.string.cross_math_game), onBackClick = onBackClick)
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
                // Timer lives in the header, top-right, vertically centered.
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

            // No mascot: the grid and the right column (stats card + tray +
            // tools) sit together as one centered group, so the panel never
            // feels stranded at the far edge of the screen.
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CrossGrid(state, cell, s) { viewModel.tapCell(it) }
                }

                Spacer(Modifier.width((32f * s).dp))

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val hasSigns = state.cells.any { it.type == CrossCellType.BLANK_OP }
                    CrossMathSidePanel(state, s)
                    Spacer(Modifier.height(AppDimens.Dimens12))
                    TrayGrid(state, cell, s, hasSigns = hasSigns) { viewModel.tapTray(it) }
                    if (hasSigns) {
                        Spacer(Modifier.height(AppDimens.Dimens6))
                        SignRow(cell, s) { viewModel.tapSign(it) }
                    }
                    Spacer(Modifier.height(AppDimens.Dimens10))
                    ToolBar(state, cell, s,
                        onUndo = { viewModel.undo() },
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

// 3-digit values need a smaller font so they never clip at the tile edges.
private fun numFont(value: Int, base: Float): Float = when {
    value >= 100 -> base * 0.68f
    value >= 10 -> base * 0.88f
    else -> base
}

// ── grid ────────────────────────────────────────────────────────────────────

@Composable
private fun CrossGrid(state: CrossMathUiState, cell: Dp, s: Float, onTap: (Int) -> Unit) {
    Column {
        for (r in 0 until state.rows) {
            Row {
                for (c in 0 until state.cols) {
                    val i = r * state.cols + c
                    CrossCell(state, i, cell, s, onTap)
                }
            }
        }
    }
}

private fun cellPresent(state: CrossMathUiState, r: Int, c: Int): Boolean {
    if (r !in 0 until state.rows || c !in 0 until state.cols) return false
    return state.cells[r * state.cols + c].type != CrossCellType.ABSENT
}

@Composable
private fun CrossCell(state: CrossMathUiState, i: Int, cell: Dp, s: Float, onTap: (Int) -> Unit) {
    val data = state.cells.getOrNull(i) ?: return
    if (data.type == CrossCellType.ABSENT) {
        Spacer(Modifier.size(cell))
        return
    }

    val trayId = state.placed[i]
    val placedValue = trayId?.let { id -> state.tray.firstOrNull { it.id == id }?.value }
    val placedOp = state.placedOps[i]
    val isWrong = i in state.wrongCells && state.wrongCells.isNotEmpty()
    val isSolvedRun = i in state.solvedCells
    val isSelected = state.selectedCell == i
    val isHintCell = i in state.hintLocked
    val isFilled = placedValue != null || placedOp != null

    // Pop-in animation each time a number or sign lands in a blank cell.
    val pop = remember { Animatable(1f) }
    LaunchedEffect(placedValue, placedOp) {
        if (isFilled) {
            pop.snapTo(0.55f)
            pop.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = 700f))
        }
    }
    // Gentle shake when this cell is part of a complete-but-wrong equation.
    val shake = remember { Animatable(0f) }
    LaunchedEffect(isWrong) {
        if (isWrong) {
            repeat(3) {
                shake.animateTo(6f, tween(45)); shake.animateTo(-6f, tween(45))
            }
            shake.animateTo(0f, tween(40))
        }
    }

    // Whole-run highlight: every cell of a completed equation turns green or
    // red — the prefilled cells in a lighter shade than the kid's own tiles.
    val bg = when (data.type) {
        CrossCellType.OP, CrossCellType.EQUALS, CrossCellType.GIVEN_NUM -> when {
            state.justSolved -> SOLVED_BG_GIVEN
            isWrong -> WRONG_BG_GIVEN
            isSolvedRun -> SOLVED_BG_GIVEN
            else -> GIVEN_BG
        }
        CrossCellType.BLANK, CrossCellType.BLANK_OP -> when {
            state.justSolved -> SOLVED_BG
            isWrong && isFilled -> WRONG_BG
            isWrong -> WRONG_BG_GIVEN
            isSolvedRun && isFilled -> SOLVED_BG
            else -> BLANK_BG
        }
        else -> BLANK_BG
    }

    // Flush grid like the reference: no gaps, square corners. Each cell draws
    // its own top/left line (bottom/right only at the shape boundary) inside
    // its real layout bounds, so every line sits exactly on the cell edge with
    // a single, even thickness — no drift, no doubling.
    val row = i / state.cols
    val col = i % state.cols
    val topPresent = cellPresent(state, row - 1, col)
    val bottomPresent = cellPresent(state, row + 1, col)
    val leftPresent = cellPresent(state, row, col - 1)
    val rightPresent = cellPresent(state, row, col + 1)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .graphicsLayer { translationX = shake.value }
            .size(cell)
            .drawBehind {
                val w = 1.dp.toPx()
                // The background bleeds half a line-width toward PRESENT
                // neighbors only — that overlap hides under the grid line, so
                // fractional-pixel seams (white hairlines) can't show, and
                // color never leaks past the shape's outer boundary.
                val o = w / 2f
                val lp = if (leftPresent) o else 0f
                val tp = if (topPresent) o else 0f
                val rp = if (rightPresent) o else 0f
                val bp = if (bottomPresent) o else 0f
                drawRect(
                    bg,
                    topLeft = Offset(-lp, -tp),
                    size = androidx.compose.ui.geometry.Size(size.width + lp + rp, size.height + tp + bp)
                )
                drawLine(CELL_BORDER, Offset(0f, 0f), Offset(size.width, 0f), w)
                drawLine(CELL_BORDER, Offset(0f, 0f), Offset(0f, size.height), w)
                if (!bottomPresent) drawLine(CELL_BORDER, Offset(0f, size.height), Offset(size.width, size.height), w)
                if (!rightPresent) drawLine(CELL_BORDER, Offset(size.width, 0f), Offset(size.width, size.height), w)
            }
            // Selection ring drawn inset past the shared grid line: neighbor
            // cells paint half a line-width into our bounds after us, so a
            // plain border() gets overpainted on the right/bottom and looks
            // thinner there. Insetting keeps it an even width on all 4 sides.
            .then(if (isSelected) Modifier.drawBehind {
                val bw = 2.5.dp.toPx()
                val inset = 1.dp.toPx() / 2f + bw / 2f
                drawRect(
                    SELECTED_BORDER,
                    topLeft = Offset(inset, inset),
                    size = androidx.compose.ui.geometry.Size(size.width - 2f * inset, size.height - 2f * inset),
                    style = Stroke(bw)
                )
            } else Modifier)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = data.type == CrossCellType.BLANK || data.type == CrossCellType.BLANK_OP
            ) { onTap(i) }
    ) {
        when (data.type) {
            CrossCellType.GIVEN_NUM -> Text(
                "${data.given}", color = Color(0xFF3E2723), maxLines = 1, softWrap = false,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                fontSize = (numFont(data.given ?: 0, 20f) * s).sp
            )
            CrossCellType.OP, CrossCellType.EQUALS -> Text(
                data.displaySymbol, color = Color(0xFF3E2723),
                fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (22f * s).sp
            )
            CrossCellType.BLANK -> if (placedValue != null) {
                Text(
                    "$placedValue",
                    color = if (isHintCell) Color(0xFF7B1FA2) else BLUE, maxLines = 1, softWrap = false,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                    fontSize = (numFont(placedValue, 20f) * s).sp,
                    modifier = Modifier.graphicsLayer { scaleX = pop.value; scaleY = pop.value }
                )
            }
            CrossCellType.BLANK_OP -> if (placedOp != null) {
                Text(
                    opDisplay(placedOp),
                    color = if (isHintCell) Color(0xFF7B1FA2) else BLUE,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (22f * s).sp,
                    modifier = Modifier.graphicsLayer { scaleX = pop.value; scaleY = pop.value }
                )
            }
            else -> {}
        }
    }
}

// ── tray ────────────────────────────────────────────────────────────────────

// Tray tiles in the right column, below the stats card. Row shape depends on
// the tile count: 2-3 → one row, 4 → 2x2, 5 → 3+2, 6+ → rows of 3; a shorter
// last row is centered horizontally. The whole options area (numbers + sign
// palette) never exceeds 3 rows, so with signs the numbers get max 2 rows.
@Composable
private fun TrayGrid(state: CrossMathUiState, cell: Dp, s: Float, hasSigns: Boolean = false, onTap: (Int) -> Unit) {
    val count = state.tray.size
    val maxNumRows = if (hasSigns) 2 else 3
    val perRow = when {
        count <= 3 -> maxOf(count, 1)
        count == 4 -> 2
        count <= 6 && !hasSigns -> 3
        else -> (count + maxNumRows - 1) / maxNumRows
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens6),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        state.tray.chunked(perRow).forEach { rowTiles ->
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6)) {
                rowTiles.forEach { tile ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .graphicsLayer { alpha = if (tile.used) 0.3f else 1f }
                            .size(cell * 0.95f)
                            .clip(RoundedCornerShape(AppDimens.Dimens10))
                            .background(TRAY_BG)
                            .border(2.dp, Color(0xFF388E3C), RoundedCornerShape(AppDimens.Dimens10))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                enabled = !tile.used
                            ) { onTap(tile.id) }
                    ) {
                        Text(
                            "${tile.value}", color = Color.White, maxLines = 1, softWrap = false,
                            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                            fontSize = (numFont(tile.value, 20f) * s).sp
                        )
                    }
                }
            }
        }
    }
}

// ── sign palette ────────────────────────────────────────────────────────────

// All four operators, always available (signs are reusable — the equation
// check decides right or wrong).
@Composable
private fun SignRow(cell: Dp, s: Float, onTap: (Char) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6), verticalAlignment = Alignment.CenterVertically) {
        listOf('+', '-', '*', '/').forEach { op ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(cell * 0.95f)
                    .clip(RoundedCornerShape(AppDimens.Dimens10))
                    .background(Color(0xFF7E57C2))
                    .border(2.dp, Color(0xFF512DA8), RoundedCornerShape(AppDimens.Dimens10))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTap(op) }
            ) {
                Text(
                    opDisplay(op), color = Color.White,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (22f * s).sp
                )
            }
        }
    }
}

// ── toolbar ─────────────────────────────────────────────────────────────────

@Composable
private fun ToolBar(
    state: CrossMathUiState, cell: Dp, s: Float,
    onUndo: () -> Unit, onErase: () -> Unit, onRestart: () -> Unit, onHint: () -> Unit
) {
    val canErase = state.selectedCell?.let {
        (state.placed[it] != null || state.placedOps[it] != null) && it !in state.hintLocked
    } ?: false
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12), verticalAlignment = Alignment.CenterVertically) {
        ToolButton(bg = Color(0xFF90A4AE), enabled = state.undoStack.isNotEmpty(), size = cell, onClick = onUndo) {
            Icon(Icons.AutoMirrored.Rounded.Undo, contentDescription = null, tint = Color.White,
                modifier = Modifier.size((22f * s).dp))
        }
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
            .size(size * 0.9f)
            .clip(RoundedCornerShape(AppDimens.Dimens10))
            .background(bg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, enabled = enabled
            ) { onClick() }
    ) { content() }
}

// ── side panel (timer / hints) ──────────────────────────────────────────────

private fun timeString(seconds: Int): String = "%d:%02d".format(seconds / 60, seconds % 60)

@Composable
private fun CrossMathSidePanel(state: CrossMathUiState, s: Float, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens16),
            modifier = Modifier.clip(RoundedCornerShape(AppDimens.Dimens16)).background(Color.White.copy(alpha = 0.8f))
                .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens16)
        ) {
            val filled = state.placed.size + state.placedOps.size
            val total = state.cells.count { it.type == CrossCellType.BLANK || it.type == CrossCellType.BLANK_OP }
            PanelRow("💡", "${state.hintsLeft}", Color(0xFFF57F17), s)
            PanelRow("🧩", "$filled/$total", BLUE, s)
        }
    }
}

@Composable
private fun PanelRow(emoji: String, value: String, color: Color, s: Float) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6), verticalAlignment = Alignment.CenterVertically) {
        Text(emoji, fontSize = (22f * s).sp)
        Text(value, color = color, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (22f * s).sp)
    }
}

// ── result overlay ──────────────────────────────────────────────────────────

@Composable
private fun ResultOverlay(
    stars: Int, elapsed: Int, hintsUsed: Int, hasNextLevel: Boolean,
    onNextLevel: () -> Unit, onPlayAgain: () -> Unit, onBack: () -> Unit
) {
    val accentColors = getButtonColors(ButtonType.ORANGE)
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        // Finishing a Cross Math level is always a win — always celebrate.
        LaunchedEffect(Unit) { if (stars >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        ConfettiRainEffect()
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
                    Text("🔢", fontSize = (28f * gameScale()).sp)
                    Text(
                        stringResource(R.string.balloon_great_job),
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                        fontSize = 30.sp.scaled(),
                        style = TextStyle(shadow = Shadow(color = accentColors.base.copy(alpha = 0.9f), offset = Offset(1.5f, 1.5f), blurRadius = 0f)),
                        modifier = Modifier.padding(horizontal = AppDimens.Dimens8)
                    )
                    Text("✨", fontSize = (28f * gameScale()).sp)
                }
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

            Box(
                modifier = Modifier
                    .offset(y = (-28).dp)
                    .size(64.dp)
                    .shadow(8.dp, CircleShape)
                    .background(Color.White, CircleShape)
                    .border(4.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🔢", fontSize = 32.sp)
            }
        }
    }
}

@Composable
private fun StatChipStr(emoji: String, value: String, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4), verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clip(RoundedCornerShape(100f)).background(Color.White)
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
