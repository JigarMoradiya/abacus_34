package com.jigar.me.ui.view.home.screens.math_game_zone.cross_math

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.appScale
import com.jigar.me.ui.view.ab.AbColors
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abBlended
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.screens.math_game_zone.GameCategoryType
import com.jigar.me.ui.view.home.screens.math_game_zone.common.AbDebugFlags
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameFitBox
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameResultCard
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameStatChip
import com.jigar.me.ui.view.home.screens.math_game_zone.common.PauseTimerInBackground
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBounce
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBreathe
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abPopIn
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTray
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.common.toyColor
import com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.components.CrossCellType
import com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.components.CrossMathCell
import com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.components.CrossMathUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.components.TrayTile
import com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.components.opDisplay
import com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.viewmodel.CrossMathPlayViewModel
import com.jigar.me.ui.view.home.screens.math_game_zone.gameCardStyle
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)
private val BLUE = Color(0xFF0074D5)
private val CELL_INK = Color(0xFF3E2723)

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

    val accent: Color = gameCardStyle(GameCategoryType.CROSS_MATH).light
    val accentDark: Color = gameCardStyle(GameCategoryType.CROSS_MATH).dark
    val colors = LocalAbColors.current

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
                    color = ORANGE, style = AbTypography.fixed(20f * appScale(), FontWeight.ExtraBold),
                    modifier = Modifier.align(Alignment.Center)
                        .abGamePill(outline = ORANGE_BORDER)
                        .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens8)
                )
                // Timer lives in the header, top-right, vertically centered.
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6),
                    modifier = Modifier.align(Alignment.CenterEnd)
                        .padding(end = AppDimens.Dimens16)
                        .abGamePill()
                        .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
                ) {
                    Text("⏱️", fontSize = (24f * s).sp)
                    Text(
                        timeString(state.elapsed), color = colors.textPrimary,
                        style = AbTypography.fixed(26f * appScale(), FontWeight.ExtraBold)
                    )
                }
            }

            // No mascot: the grid and the right column (stats card + tray +
            // tools) sit together as one centered group, so the panel never
            // feels stranded at the far edge of the screen.
            // The play area is natural size and centred; only if it is taller than the screen leaves room for (a 7-row Very Hard
            // board on a short phone) it is shrunk to fit, so neither the board nor the toolbar is ever cut off at the bottom.
            GameFitBox(modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = AppDimens.Dimens8)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CrossGrid(state, cell, s, accent, accentDark) { viewModel.tapCell(it) }

                    Spacer(Modifier.width((32f * s).dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val hasSigns = state.cells.any { it.type == CrossCellType.BLANK_OP }
                        CrossMathSidePanel(state, s)
                        Spacer(Modifier.height(AppDimens.Dimens12))
                        TrayGrid(state, cell, s, accent, hasSigns = hasSigns) { viewModel.tapTray(it) }
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

        // Debug builds only (`--ez ab_show_result true`): shows the real result overlay without playing a level.
        if (AbDebugFlags.showResultEnabled && !state.isGameOver) {
            ResultOverlay(
                stars = 3,
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

// Lets a decoration reach past its content: the content keeps the layout size it would have had, but the node is
// measured and placed bigger (the reverse of padding), so a tray can be drawn around a board without shrinking the board.
private fun Modifier.crossMathOutset(horizontal: Dp, top: Dp, bottom: Dp): Modifier = layout { measurable, constraints ->
    val h = horizontal.roundToPx()
    val t = top.roundToPx()
    val b = bottom.roundToPx()
    val placeable = measurable.measure(constraints.offset(horizontal = 2 * h, vertical = t + b))
    layout(placeable.width - 2 * h, placeable.height - t - b) { placeable.place(-h, -t) }
}

// Springy squash while a key is pressed, then the tap. Silent (the crossword has no click sound).
private fun Modifier.crossMathPressClick(enabled: Boolean = true, onClick: () -> Unit): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val squash by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.9f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 600f),
        label = "crossMathPress"
    )
    this
        .clickable(interactionSource = interaction, indication = null, enabled = enabled, onClick = onClick)
        .graphicsLayer { scaleX = squash; scaleY = squash }
}

// ── grid ────────────────────────────────────────────────────────────────────

// The crossword sits in a chunky game-coloured tray. The tray is drawn OUTSIDE the grid's layout box (outset), so every
// cell keeps its size and the board keeps the room it had.
@Composable
private fun CrossGrid(state: CrossMathUiState, cell: Dp, s: Float, accent: Color, accentDark: Color, onTap: (Int) -> Unit) {
    Column(
        Modifier
            .crossMathOutset(horizontal = 6.dp, top = 6.dp, bottom = 12.dp)
            .abToyTray(accent, 6.dp)
    ) {
        for (r in 0 until state.rows) {
            Row {
                for (c in 0 until state.cols) {
                    val i = r * state.cols + c
                    CrossCell(state, i, cell, s, accent, accentDark, onTap)
                }
            }
        }
    }
}

// What one cell looks like: the face colour, the ink, and whether it is an empty dashed slot.
private data class CrossMathCellLook(val face: Color, val ink: Color, val isSlot: Boolean, val tint: Color)

private fun crossMathCellLook(
    colors: AbColors, accent: Color, accentDark: Color, data: CrossMathCell,
    filled: Boolean, value: Int?, wrong: Boolean, solvedRun: Boolean, hint: Boolean, justSolved: Boolean
): CrossMathCellLook {
    val greenSoft: Color = colors.accentGreen.abBlended(Color.White, 0.5f)
    val redSoft: Color = colors.danger.abBlended(Color.White, 0.5f)
    return when (data.type) {
        CrossCellType.BLANK, CrossCellType.BLANK_OP -> {
            val isOp = data.type == CrossCellType.BLANK_OP
            if (!filled) {
                val tint: Color = if (isOp) colors.accentPurple else accent
                val slotFace: Color = if (wrong) redSoft else tint.copy(alpha = 0.16f)
                CrossMathCellLook(slotFace, CELL_INK, true, tint)
            } else {
                var face: Color = if (isOp) colors.accentPurple.abReadable(3f) else colors.toyColor(value ?: 0)
                if (justSolved) face = colors.accentGreen.abReadable(3f)
                else if (wrong) face = colors.danger.abReadable(3f)
                else if (solvedRun) face = colors.accentGreen.abReadable(3f)
                else if (hint) face = colors.accentPink.abReadable(3f)
                CrossMathCellLook(face, Color.White, false, accent)
            }
        }
        CrossCellType.GIVEN_NUM, CrossCellType.OP, CrossCellType.EQUALS -> when {
            justSolved -> CrossMathCellLook(greenSoft, CELL_INK, false, accent)
            wrong -> CrossMathCellLook(redSoft, CELL_INK, false, accent)
            solvedRun -> CrossMathCellLook(greenSoft, CELL_INK, false, accent)
            data.type == CrossCellType.GIVEN_NUM -> CrossMathCellLook(colors.accentYellow, CELL_INK, false, accent)
            data.type == CrossCellType.OP -> CrossMathCellLook(colors.accentOrange.abReadable(3f), Color.White, false, accent)
            else -> CrossMathCellLook(accentDark, Color.White, false, accent)
        }
        else -> CrossMathCellLook(accent.copy(alpha = 0.16f), CELL_INK, true, accent)
    }
}

@Composable
private fun CrossCell(state: CrossMathUiState, i: Int, cell: Dp, s: Float, accent: Color, accentDark: Color, onTap: (Int) -> Unit) {
    val data = state.cells.getOrNull(i) ?: return
    if (data.type == CrossCellType.ABSENT) {
        Spacer(Modifier.size(cell))
        return
    }
    val colors = LocalAbColors.current

    val trayId = state.placed[i]
    val placedValue = trayId?.let { id -> state.tray.firstOrNull { it.id == id }?.value }
    val placedOp = state.placedOps[i]
    val isWrong = i in state.wrongCells && state.wrongCells.isNotEmpty()
    val isSolvedRun = i in state.solvedCells
    val isSelected = state.selectedCell == i
    val isHintCell = i in state.hintLocked
    val isFilled = placedValue != null || placedOp != null
    val isSign = data.type == CrossCellType.OP || data.type == CrossCellType.EQUALS || data.type == CrossCellType.BLANK_OP

    // Gentle shake when this cell is part of a complete-but-wrong equation.
    val shake = remember { Animatable(0f) }
    LaunchedEffect(isWrong) {
        if (isWrong) {
            repeat(3) {
                shake.animateTo(5f, tween(45)); shake.animateTo(-5f, tween(45))
            }
            shake.animateTo(0f, tween(40))
        }
    }

    val look = crossMathCellLook(
        colors, accent, accentDark, data, isFilled, placedValue, isWrong, isSolvedRun, isHintCell, state.justSolved
    )
    val label: String = when (data.type) {
        CrossCellType.GIVEN_NUM -> "${data.given ?: 0}"
        CrossCellType.OP, CrossCellType.EQUALS -> data.displaySymbol
        CrossCellType.BLANK -> placedValue?.let { "$it" } ?: ""
        CrossCellType.BLANK_OP -> opDisplay(placedOp)
        else -> ""
    }
    val numValue: Int = when (data.type) {
        CrossCellType.GIVEN_NUM -> data.given ?: 0
        CrossCellType.BLANK -> placedValue ?: 0
        else -> 0
    }
    val fontSize: Float = if (isSign) 22f * s else numFont(numValue, 20f) * s
    val bounceKey = "${placedValue ?: -1}|${placedOp ?: ""}|$isSolvedRun"

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(cell)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = data.type == CrossCellType.BLANK || data.type == CrossCellType.BLANK_OP
            ) { onTap(i) }
            .abPopIn(minOf(i * 12, 400))
            .graphicsLayer { translationX = shake.value * density }
            .abBounce(bounceKey, 1.18f)
    ) {
        CrossMathCellTile(
            label = label, face = look.face, ink = look.ink, isSlot = look.isSlot, tint = look.tint,
            isSelected = isSelected, cellSize = cell, fontSize = fontSize
        )
    }
}

// One crossword cell: a candy tile (or an empty dashed slot) inset inside its fixed square, so neighbours are separated
// by a small gap and the selected cell breathes inside its ring.
@Composable
private fun CrossMathCellTile(
    label: String, face: Color, ink: Color, isSlot: Boolean, tint: Color,
    isSelected: Boolean, cellSize: Dp, fontSize: Float
) {
    val colors = LocalAbColors.current
    val edge: Dp = if (cellSize >= 44.dp) 3.dp else 2.dp
    val inner: Dp = cellSize - 4.dp
    val corner: Dp = cellSize * 0.24f
    Box(Modifier.size(cellSize), contentAlignment = Alignment.Center) {
        if (isSlot) {
            Box(
                Modifier
                    .size(inner)
                    .drawBehind {
                        val r = corner.toPx()
                        val sw = 2.dp.toPx()
                        drawRoundRect(face, cornerRadius = CornerRadius(r, r))
                        drawRoundRect(
                            color = tint.copy(alpha = 0.6f),
                            topLeft = Offset(sw / 2f, sw / 2f),
                            size = Size(this.size.width - sw, this.size.height - sw),
                            cornerRadius = CornerRadius((r - sw / 2f).coerceAtLeast(0f)),
                            style = Stroke(
                                width = sw,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5.dp.toPx(), 4.dp.toPx()))
                            )
                        )
                    }
            )
        } else {
            Box(Modifier.size(inner).abToyTile(face, corner, edge), contentAlignment = Alignment.Center) {
                Text(
                    text = label,
                    style = AbTypography.fixed(fontSize, FontWeight.Black),
                    color = ink, maxLines = 1, softWrap = false
                )
            }
        }
        if (isSelected) {
            // The selection ring: a bright rim with a soft glow outside it. It draws only, so it never takes a tap.
            Box(
                Modifier
                    .size(cellSize - 1.dp)
                    .abBreathe(0.05f, 1200)
                    .drawBehind {
                        val g = 3.dp.toPx()
                        drawRoundRect(
                            color = colors.brandPrimary.copy(alpha = 0.28f),
                            topLeft = Offset(-g, -g),
                            size = Size(this.size.width + 2f * g, this.size.height + 2f * g),
                            cornerRadius = CornerRadius(corner.toPx() + g),
                            style = Stroke(width = 2f * g)
                        )
                    }
                    .border(3.dp, colors.brandPrimary, RoundedCornerShape(corner))
            )
        }
    }
}

// A chunky candy key (tray number, sign, tool): a bright face with the toy 3D edge, `height` in total.
@Composable
private fun CrossMathKey(
    face: Color, width: Dp, height: Dp, modifier: Modifier = Modifier, content: @Composable () -> Unit
) {
    Box(
        modifier
            .size(width, height)
            .abToyTile(face, minOf(width, height) * 0.3f, 4.dp),
        contentAlignment = Alignment.Center
    ) { content() }
}

private fun keyTextStyle(size: Float): TextStyle =
    AbTypography.fixed(size, FontWeight.Black).copy(shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 2f), 0f))

// ── tray ────────────────────────────────────────────────────────────────────

// Tray tiles in the right column, below the stats card. Row shape depends on
// the tile count: 2-3 → one row, 4 → 2x2, 5 → 3+2, 6+ → rows of 3; a shorter
// last row is centered horizontally. The whole options area (numbers + sign
// palette) never exceeds 3 rows, so with signs the numbers get max 2 rows.
@Composable
private fun TrayGrid(state: CrossMathUiState, cell: Dp, s: Float, accent: Color, hasSigns: Boolean = false, onTap: (Int) -> Unit) {
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
        horizontalAlignment = Alignment.CenterHorizontally,
        // A soft game-coloured pad behind the keys, drawn past the layout box so the column keeps its size.
        modifier = Modifier.drawBehind {
            if (state.tray.isNotEmpty()) {
                val o = 7.dp.toPx()
                drawRoundRect(
                    color = accent.copy(alpha = 0.14f),
                    topLeft = Offset(-o, -o),
                    size = Size(this.size.width + 2f * o, this.size.height + 2f * o),
                    cornerRadius = CornerRadius(18.dp.toPx())
                )
            }
        }
    ) {
        state.tray.chunked(perRow).forEach { rowTiles ->
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6)) {
                rowTiles.forEach { tile ->
                    CrossMathTrayKey(tile, cell, s, onTap)
                }
            }
        }
    }
}

// One candy number tile, coloured by its number so the same colour follows it into the crossword.
@Composable
private fun CrossMathTrayKey(tile: TrayTile, cell: Dp, s: Float, onTap: (Int) -> Unit) {
    val colors = LocalAbColors.current
    val usedScale by animateFloatAsState(
        targetValue = if (tile.used) 0.88f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
        label = "crossMathTrayUsed"
    )
    CrossMathKey(
        face = colors.toyColor(tile.value), width = cell * 0.95f, height = cell * 0.95f,
        modifier = Modifier
            .graphicsLayer { alpha = if (tile.used) 0.3f else 1f; scaleX = usedScale; scaleY = usedScale }
            .crossMathPressClick(enabled = !tile.used) { onTap(tile.id) }
            .abPopIn(minOf(tile.id * 40, 400))
    ) {
        Text(
            "${tile.value}", color = Color.White, maxLines = 1, softWrap = false,
            style = keyTextStyle(numFont(tile.value, 20f) * s)
        )
    }
}

// ── sign palette ────────────────────────────────────────────────────────────

// All four operators, always available (signs are reusable — the equation
// check decides right or wrong).
@Composable
private fun SignRow(cell: Dp, s: Float, onTap: (Char) -> Unit) {
    val colors = LocalAbColors.current
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6), verticalAlignment = Alignment.CenterVertically) {
        listOf('+', '-', '*', '/').forEachIndexed { index, op ->
            CrossMathKey(
                face = colors.accentPurple.abReadable(3f), width = cell * 0.95f, height = cell * 0.95f,
                modifier = Modifier
                    .crossMathPressClick { onTap(op) }
                    .abPopIn(index * 50)
            ) {
                Text(opDisplay(op), color = Color.White, style = keyTextStyle(22f * s))
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
    val colors = LocalAbColors.current
    val canErase = state.selectedCell?.let {
        (state.placed[it] != null || state.placedOps[it] != null) && it !in state.hintLocked
    } ?: false
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12), verticalAlignment = Alignment.CenterVertically) {
        ToolButton(face = colors.brandSecondary.abReadable(3f), enabled = state.undoStack.isNotEmpty(), size = cell, onClick = onUndo) {
            Icon(Icons.AutoMirrored.Rounded.Undo, contentDescription = null, tint = Color.White,
                modifier = Modifier.size((22f * s).dp))
        }
        ToolButton(face = colors.danger.abReadable(3f), enabled = canErase, size = cell, onClick = onErase) {
            Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = null, tint = Color.White,
                modifier = Modifier.size((20f * s).dp))
        }
        ToolButton(face = colors.accentOrange.abReadable(3f), enabled = true, size = cell, onClick = onRestart) {
            Icon(Icons.Rounded.Refresh, contentDescription = null, tint = Color.White,
                modifier = Modifier.size((22f * s).dp))
        }
        ToolButton(face = colors.accentYellow, enabled = state.hintsLeft > 0, size = cell, onClick = onHint) {
            Text("💡${state.hintsLeft}", color = CELL_INK, style = AbTypography.fixed(16f * s, FontWeight.Black))
        }
    }
}

@Composable
private fun ToolButton(face: Color, enabled: Boolean, size: Dp, onClick: () -> Unit, content: @Composable () -> Unit) {
    CrossMathKey(
        face = face, width = size * 0.9f, height = size * 0.9f,
        modifier = Modifier
            .graphicsLayer { alpha = if (enabled) 1f else 0.4f }
            .crossMathPressClick(enabled = enabled, onClick = onClick)
    ) { content() }
}

// ── side panel (timer / hints) ──────────────────────────────────────────────

private fun timeString(seconds: Int): String = "%d:%02d".format(seconds / 60, seconds % 60)

@Composable
private fun CrossMathSidePanel(state: CrossMathUiState, s: Float, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        AbGlassPanel(modifier = Modifier.padding(horizontal = AppDimens.Dimens8)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens16),
                modifier = Modifier.padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens16)
            ) {
                val filled = state.placed.size + state.placedOps.size
                val total = state.cells.count { it.type == CrossCellType.BLANK || it.type == CrossCellType.BLANK_OP }
                PanelRow("💡", "${state.hintsLeft}", Color(0xFFF57F17), s)
                PanelRow("🧩", "$filled/$total", BLUE, s)
            }
        }
    }
}

@Composable
private fun PanelRow(emoji: String, value: String, color: Color, s: Float) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6), verticalAlignment = Alignment.CenterVertically) {
        Text(emoji, fontSize = (22f * s).sp)
        Text(value, color = color, style = AbTypography.fixed(22f * s, FontWeight.Black), modifier = Modifier.abBounce(value))
    }
}

// ── result overlay ──────────────────────────────────────────────────────────

@Composable
private fun ResultOverlay(
    stars: Int, elapsed: Int, hintsUsed: Int, hasNextLevel: Boolean,
    onNextLevel: () -> Unit, onPlayAgain: () -> Unit, onBack: () -> Unit
) {
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        // Finishing a Cross Math level is always a win — always celebrate.
        LaunchedEffect(Unit) { if (stars >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        // "Candy Pop" celebration card: the shared glossy accent panel with the bouncing badge, stars and confetti.
        GameResultCard(
            title = stringResource(R.string.balloon_great_job),
            emoji = "🔢",
            accent = ButtonType.ORANGE,
            stars = stars,
            wide = true,
            stats = {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    GameStatChip("⏱️", timeString(elapsed), Color(0xFF5D4037))
                    GameStatChip("💡", "$hintsUsed", Color(0xFFF57F17))
                }
            },
            buttons = {
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    if (hasNextLevel) {
                        GameButton(text = stringResource(R.string.cross_math_next_level), type = ButtonType.ORANGE,
                            onClick = { if (enabled) onNextLevel() })
                    }
                    GameButton(text = stringResource(R.string.balloon_play_again), type = ButtonType.POSITIVE,
                        onClick = { if (enabled) onPlayAgain() })
                    GameHomeButton(onClick = { if (enabled) onBack() })
                }
            }
        )
    }
}
