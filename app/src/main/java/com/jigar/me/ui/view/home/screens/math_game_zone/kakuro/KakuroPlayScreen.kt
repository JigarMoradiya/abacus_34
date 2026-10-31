package com.jigar.me.ui.view.home.screens.math_game_zone.kakuro

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abBlended
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.screens.math_game_zone.GameCategoryType
import com.jigar.me.ui.view.home.screens.math_game_zone.common.AbDebugFlags
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton
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
import com.jigar.me.ui.view.home.screens.math_game_zone.gameCardStyle
import com.jigar.me.ui.view.home.screens.math_game_zone.kakuro.components.KakuroCell
import com.jigar.me.ui.view.home.screens.math_game_zone.kakuro.components.KakuroCellType
import com.jigar.me.ui.view.home.screens.math_game_zone.kakuro.components.KakuroUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.kakuro.viewmodel.KakuroPlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)
private val NO_SUM = Color(0xFF90A4AE)
private val WALL_FACE = Color(0xFFCFD8DC)
private val SOLVED_INK = Color(0xFF0B5D43)

// Measurements of the toy tiles (in design points, scaled by the game's scale).
private const val TILE_GAP = 2.5f   // half the space between two tiles
private const val TILE_EDGE = 3f    // the 3D lip under a tile
private const val TILE_CORNER = 9f

@Composable
fun KakuroPlayScreen(
    viewModel: KakuroPlayViewModel,
    onBackClick: () -> Unit,
    onNextLevel: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val s = gameScale()
    val kitScale = LocalAbScale.current.factor
    val colors = LocalAbColors.current
    val trayAccent = remember { gameCardStyle(GameCategoryType.KAKURO).light }
    LaunchedEffect(Unit) { viewModel.start() }
    // A phone call or the home button freezes the clock - fair play.
    PauseTimerInBackground { viewModel.setTimerPaused(it) }

    val cellBase = when {
        state.cols >= 7 -> 44f
        state.rows >= 7 -> 42f
        state.cols >= 6 -> 48f
        else -> 54f
    }
    val cell = (cellBase * s).dp
    val keySide = cell * 0.82f

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
                    color = ORANGE,
                    style = AbTypography.fixed(22f * kitScale, FontWeight.ExtraBold),
                    modifier = Modifier.align(Alignment.Center)
                        .abGamePill(ORANGE_BORDER)
                        .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens8)
                )
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
                        style = AbTypography.fixed(26f * kitScale, FontWeight.Black)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                KakuroGrid(state, cell, s, trayAccent) { viewModel.tapCell(it) }

                Spacer(Modifier.width((32f * s).dp))

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    StatsPanel(state, s)
                    Spacer(Modifier.height(AppDimens.Dimens12))
                    DigitPad(keySide, s) { viewModel.tapDigit(it) }
                    Spacer(Modifier.height(AppDimens.Dimens10))
                    ToolBar(state, viewModel.canErase, keySide, s,
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

        // Debug builds only: lets the lead render the real result overlay without playing.
        if (AbDebugFlags.showResultEnabled) {
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

/** Reports the size of the board itself while the tray around it is drawn outward, so the column keeps its old size. */
private fun Modifier.kakuroOutward(width: Dp, height: Dp, inset: Dp): Modifier = this.layout { measurable, _ ->
    val placeable = measurable.measure(Constraints())
    val shift = inset.roundToPx()
    layout(width.roundToPx(), height.roundToPx()) { placeable.place(-shift, -shift) }
}

// The board sits in a toy tray; the tray grows outward so the column keeps its old size.
@Composable
private fun KakuroGrid(state: KakuroUiState, cell: Dp, s: Float, accent: Color, onTap: (Int) -> Unit) {
    val w = cell * state.cols
    val h = cell * state.rows
    val inset = 6.dp
    Box(
        modifier = Modifier
            .kakuroOutward(w, h, inset)
            .graphicsLayer { alpha = if (state.cells.isEmpty()) 0f else 1f }
            .abToyTray(accent, inset)
    ) {
        Box(modifier = Modifier.size(w, h), contentAlignment = Alignment.Center) {
            Column {
                for (r in 0 until state.rows) {
                    Row {
                        for (c in 0 until state.cols) {
                            KakuroCellView(state, r * state.cols + c, cell, s, onTap)
                        }
                    }
                }
            }
            if (state.justSolved && state.cols > 0) {
                KakuroSolvedBurst(side = w, unit = s)
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
    val isGreen = state.justSolved || (isSolvedRun && placed != null)
    val row = if (state.cols > 0) i / state.cols else 0
    val col = if (state.cols > 0) i % state.cols else 0

    val shake = remember { Animatable(0f) }
    LaunchedEffect(isWrong) {
        if (isWrong) {
            repeat(3) { shake.animateTo(6f, tween(45)); shake.animateTo(-6f, tween(45)) }
            shake.animateTo(0f, tween(40))
        }
    }

    Box(
        modifier = Modifier
            .abPopIn(minOf(400, (row + col) * 30))
            .graphicsLayer { translationX = shake.value }
            .size(cell)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = data.type == KakuroCellType.BLANK
            ) { onTap(i) }
    ) {
        when (data.type) {
            KakuroCellType.WALL -> KakuroWallTile(s)
            KakuroCellType.CLUE -> KakuroClueTile(data, s)
            KakuroCellType.BLANK -> KakuroEntryTile(placed, isSelected, isWrong, isGreen, isHintCell, s)
        }
    }
}

/** Walls: a quiet pale block so the puzzle's shape reads, but it never competes with the clues. */
@Composable
private fun KakuroWallTile(s: Float) {
    Box(
        Modifier.fillMaxSize()
            .padding((TILE_GAP * s).dp)
            .background(WALL_FACE.copy(alpha = 0.55f), RoundedCornerShape((TILE_CORNER * s).dp))
    )
}

/**
 * Clues: a rounded candy block split by a diagonal. The "across" sum rides the orange half (top right), the "down" sum
 * rides the blue half (bottom left).
 */
@Composable
private fun KakuroClueTile(data: KakuroCell, s: Float) {
    val colors = LocalAbColors.current
    val corner = (TILE_CORNER * s).dp
    val acrossFace = if (data.acrossSum != null) colors.accentOrange.abReadable(3f) else NO_SUM
    val downFace = if (data.downSum != null) colors.brandSecondary.abReadable(3f) else NO_SUM
    val numberStyle = AbTypography.fixed(14f * s, FontWeight.Black).copy(
        shadow = Shadow(Color.Black.copy(alpha = 0.2f), Offset(0f, 2f), 0f)
    )
    Box(
        Modifier.fillMaxSize()
            .padding((TILE_GAP * s).dp)
            .abToyTile(colors.brandPrimaryDark, corner, (TILE_EDGE * s).dp)
    ) {
        Box(Modifier.fillMaxSize().clip(RoundedCornerShape(corner))) {
            Canvas(Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val across = Path().apply { moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h); close() }
                val down = Path().apply { moveTo(0f, 0f); lineTo(w, h); lineTo(0f, h); close() }
                drawPath(across, acrossFace)
                drawPath(down, downFace)
                drawLine(Color.White.copy(alpha = 0.85f), Offset(0f, 0f), Offset(w, h), 1.5f * s * density)
            }
            data.acrossSum?.let {
                Text(
                    "$it", color = Color.White, style = numberStyle,
                    modifier = Modifier.align(Alignment.TopEnd).padding(end = (5f * s).dp, top = (2f * s).dp)
                )
            }
            data.downSum?.let {
                Text(
                    "$it", color = Color.White, style = numberStyle,
                    modifier = Modifier.align(Alignment.BottomStart).padding(start = (5f * s).dp, bottom = (2f * s).dp)
                )
            }
        }
    }
}

/**
 * Entry boxes: white candy tiles with a soft coloured rim. They turn yellow when picked (and breathe), pink when a line
 * is wrong, green when a line adds up, lilac when a hint filled them.
 */
@Composable
private fun KakuroEntryTile(placed: Int?, selected: Boolean, wrong: Boolean, green: Boolean, hint: Boolean, s: Float) {
    val colors = LocalAbColors.current
    val gap = (TILE_GAP * s).dp
    val edge = (TILE_EDGE * s).dp
    val corner = (TILE_CORNER * s).dp

    var face = Color.White
    var rim = colors.brandPrimary.abBlended(Color.White, 0.6f)
    var ink = colors.brandPrimaryDark
    if (hint) {
        face = colors.accentPurple.abBlended(Color.White, 0.82f)
        rim = colors.accentPurple.abBlended(Color.White, 0.35f)
        ink = colors.accentPurple.abReadable(4.5f)
    }
    if (selected) {
        face = colors.accentYellow.abBlended(Color.White, 0.6f)
        rim = colors.brandPrimary
    }
    if (wrong) {
        face = colors.danger.abBlended(Color.White, 0.6f)
        rim = colors.danger
        ink = colors.danger.abReadable(4.5f)
    }
    if (green) {
        face = colors.accentGreen.abBlended(Color.White, 0.45f)
        rim = colors.accentGreen.abDarkened(0.08f)
        ink = SOLVED_INK
    }
    val rimWidth = (if (selected) 3f else 2f) * s

    val faceNow by animateColorAsState(face, tween(180), label = "kakuroFace")
    val rimNow by animateColorAsState(rim, tween(180), label = "kakuroRim")
    val inkNow by animateColorAsState(ink, tween(180), label = "kakuroInk")

    Box(Modifier.fillMaxSize().padding(gap)) {
        Box(
            Modifier.fillMaxSize()
                .then(if (selected) Modifier.abBreathe(0.04f, 1300) else Modifier)
                .kakuroEntryFace(faceNow, rimNow, rimWidth.dp, corner, edge)
        )
        Box(Modifier.fillMaxSize().padding(bottom = edge), contentAlignment = Alignment.Center) {
            Text(
                placed?.toString() ?: "",
                style = AbTypography.fixed(24f * s, FontWeight.Black),
                color = inkNow,
                modifier = Modifier.abBounce(placed)
            )
        }
    }
}

/** One toy block: a face with a glossy top, a rim, and a 3D lip under it. */
private fun Modifier.kakuroEntryFace(face: Color, rim: Color, rimWidth: Dp, corner: Dp, edge: Dp): Modifier {
    val shape = RoundedCornerShape(corner)
    return this
        .padding(bottom = edge)
        .drawBehind {
            drawRoundRect(
                rim.abDarkened(0.2f),
                Offset(0f, edge.toPx()),
                size,
                CornerRadius(corner.toPx())
            )
        }
        .background(face, shape)
        .drawBehind {
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.55f), Color.Transparent),
                    startY = 0f, endY = size.height / 2f
                ),
                cornerRadius = CornerRadius(corner.toPx())
            )
        }
        .border(rimWidth, rim, shape)
}

/** A burst of stars from the board's centre when the level is solved. It starts the moment it appears and fades out. */
@Composable
private fun KakuroSolvedBurst(side: Dp, unit: Float) {
    val colors = LocalAbColors.current
    var t by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        var elapsed = 0f
        while (elapsed < 1.2f) {
            withFrameNanos { now ->
                elapsed = (now - start) / 1_000_000_000f
                t = elapsed
            }
        }
    }
    Canvas(modifier = Modifier.requiredSize(side)) {
        val tt = t
        val sidePx = size.width
        val centre = Offset(size.width / 2f, size.height / 2f)
        for (k in 0 until 12) {
            val angle = k / 12.0 * 2.0 * PI + 0.3
            val progress = min(tt / 1f, 1f)
            val eased = 1f - (1f - progress) * (1f - progress) * (1f - progress)
            val far = sidePx * (if (k % 2 == 0) 0.62f else 0.46f)
            val dist = far * eased
            val grow = min(tt / 0.25f, 1f)
            val fade = if (progress < 0.6f) 1f else max(0f, 1f - (progress - 0.6f) / 0.4f)
            val star = Offset(centre.x + (cos(angle) * dist).toFloat(), centre.y + (sin(angle) * dist).toFloat())
            val outer = (if (k % 2 == 0) 11f else 8f) * unit * density * grow
            drawKakuroStar(star, outer, tt * 220f, colors.toyColor(k).copy(alpha = fade))
        }
    }
}

private fun DrawScope.drawKakuroStar(centre: Offset, outer: Float, degrees: Float, color: Color) {
    val inner = outer * 0.45f
    val path = Path()
    for (p in 0 until 10) {
        val radius = if (p % 2 == 0) outer else inner
        val a = (p * 36.0 - 90.0) * PI / 180.0
        val x = centre.x + (cos(a) * radius).toFloat()
        val y = centre.y + (sin(a) * radius).toFloat()
        if (p == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    rotate(degrees, pivot = centre) { drawPath(path, color) }
}

// ── right panel ─────────────────────────────────────────────────────────────

@Composable
private fun StatsPanel(state: KakuroUiState, s: Float) {
    val colors = LocalAbColors.current
    AbGlassPanel {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens10)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6), verticalAlignment = Alignment.CenterVertically) {
                Text("💡", fontSize = (18f * s).sp)
                Text(
                    "${state.hintsLeft}", color = colors.accentOrange.abReadable(3f),
                    style = AbTypography.fixed(20f * s, FontWeight.ExtraBold),
                    modifier = Modifier.abBounce(state.hintsLeft)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6), verticalAlignment = Alignment.CenterVertically) {
                Text("🧩", fontSize = (18f * s).sp)
                Text(
                    "${state.placed.size}/${state.cells.count { it.type == KakuroCellType.BLANK }}",
                    color = colors.brandPrimary, style = AbTypography.fixed(20f * s, FontWeight.ExtraBold)
                )
            }
        }
    }
}

@Composable
private fun DigitPad(keySide: Dp, s: Float, onDigit: (Int) -> Unit) {
    val colors = LocalAbColors.current
    Column(verticalArrangement = Arrangement.spacedBy((8f * s).dp)) {
        listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9)).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy((8f * s).dp)) {
                row.forEach { n ->
                    KakuroKey(face = colors.toyColor(n - 1), side = keySide, enabled = true, onClick = { onDigit(n) }) {
                        Text(
                            "$n",
                            style = AbTypography.fixed(min(24f * s, keySide.value * 0.6f), FontWeight.Black).copy(
                                shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f)
                            ),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolBar(
    state: KakuroUiState, canErase: Boolean, keySide: Dp, s: Float,
    onErase: () -> Unit, onRestart: () -> Unit, onHint: () -> Unit
) {
    val colors = LocalAbColors.current
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12), verticalAlignment = Alignment.CenterVertically) {
        KakuroKey(face = colors.danger.abReadable(3f), side = keySide, enabled = canErase, onClick = onErase) {
            Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = null, tint = Color.White,
                modifier = Modifier.size((20f * s).dp))
        }
        KakuroKey(face = colors.accentOrange.abReadable(3f), side = keySide, enabled = true, onClick = onRestart) {
            Icon(Icons.Rounded.Refresh, contentDescription = null, tint = Color.White,
                modifier = Modifier.size((22f * s).dp))
        }
        KakuroKey(face = colors.accentYellow.abReadable(3f), side = keySide, enabled = state.hintsLeft > 0, onClick = onHint) {
            Text(
                "💡${state.hintsLeft}", color = Color.White,
                style = AbTypography.fixed(16f * s, FontWeight.Black),
                modifier = Modifier.abBounce(state.hintsLeft)
            )
        }
    }
}

/** A chunky candy key that squishes while it is pressed. */
@Composable
private fun KakuroKey(face: Color, side: Dp, enabled: Boolean, onClick: () -> Unit, content: @Composable BoxScope.() -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val squash by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "kakuroKey"
    )
    Box(
        modifier = Modifier
            .size(side)
            .graphicsLayer {
                scaleX = squash
                scaleY = squash
                alpha = if (enabled) 1f else 0.4f
            }
            .clickable(interactionSource = interaction, indication = null, enabled = enabled, onClick = onClick)
            .abToyTile(face, side * 0.3f, 4.dp),
        contentAlignment = Alignment.Center,
        content = content
    )
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
        GameResultCard(
            title = stringResource(R.string.balloon_great_job),
            emoji = "➕",
            accent = ButtonType.TEAL,
            stars = stars,
            wide = true,
            stats = {
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
                    GameStatChip(emoji = "⏱️", value = timeString(elapsed), color = Color(0xFF5D4037))
                    GameStatChip(emoji = "💡", value = "$hintsUsed", color = Color(0xFFB45309))
                }
            },
            buttons = {
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    if (hasNextLevel) {
                        GameButton(
                            text = stringResource(R.string.cross_math_next_level),
                            icon = Icons.AutoMirrored.Filled.ArrowForward, isIconStart = false,
                            type = ButtonType.ORANGE,
                            onClick = { if (enabled) onNextLevel() }
                        )
                    }
                    GameButton(
                        text = stringResource(R.string.balloon_play_again),
                        icon = Icons.Rounded.Refresh, type = ButtonType.POSITIVE,
                        onClick = { if (enabled) onPlayAgain() }
                    )
                    GameHomeButton(onClick = { if (enabled) onBack() })
                }
            }
        )
    }
}
