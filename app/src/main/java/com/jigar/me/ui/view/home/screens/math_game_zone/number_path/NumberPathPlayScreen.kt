package com.jigar.me.ui.view.home.screens.math_game_zone.number_path

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
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
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameResultCard
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameStatChip
import com.jigar.me.ui.view.home.screens.math_game_zone.common.PauseTimerInBackground
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBounce
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abPopIn
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTray
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.gameCardStyle
import com.jigar.me.ui.view.home.screens.math_game_zone.number_path.components.NumberPathCell
import com.jigar.me.ui.view.home.screens.math_game_zone.number_path.components.NumberPathUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.number_path.components.PathCellType
import com.jigar.me.ui.view.home.screens.math_game_zone.number_path.viewmodel.NumberPathPlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)
private val BLUE = Color(0xFF0074D5)
private val TILE_INK = Color(0xFF3E2723)

@Composable
fun NumberPathPlayScreen(
    viewModel: NumberPathPlayViewModel,
    onBackClick: () -> Unit,
    onNextLevel: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val s = gameScale()
    val colors = LocalAbColors.current
    val accent: Color = gameCardStyle(GameCategoryType.NUMBER_PATH).light
    LaunchedEffect(Unit) { viewModel.start() }
    // A phone call or the home button freezes the clock - fair play.
    PauseTimerInBackground { viewModel.setTimerPaused(it) }

    // Roomy tiles: start/goal stack an emoji over text, which clipped on
    // smaller devices with the old sizes — and landscape has the space.
    val cellBase = when {
        state.cols >= 9 -> 46f
        state.cols >= 8 -> 50f
        state.cols >= 7 -> 54f
        else -> 62f
    }
    val cell = (cellBase * s).dp

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BackButtonWithText(title = stringResource(R.string.number_path_game), onBackClick = onBackClick)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6),
                    modifier = Modifier.align(Alignment.CenterEnd)
                        .padding(end = AppDimens.Dimens16)
                        .abGamePill()
                        .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
                ) {
                    Text("⏱", fontSize = (24f * s).sp)
                    Text(
                        timeString(state.elapsed), color = colors.textPrimary,
                        style = AbTypography.fixed(26f * appScale(), FontWeight.ExtraBold)
                    )
                }
            }

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
                    PathGrid(state, cell, s, accent, viewModel.buddy) { viewModel.tapCell(it) }
                }

                Spacer(Modifier.width((32f * s).dp))

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    TotalsPanel(state, s, viewModel.buddy)
                    Spacer(Modifier.height(AppDimens.Dimens12))
                    ToolBar(state, cell, s,
                        onUndo = { viewModel.undo() },
                        onRestart = { viewModel.restart() },
                        onHint = { viewModel.useHint() })
                }
            }
        }

        if (state.isGameOver) {
            ResultOverlay(
                buddy = viewModel.buddy,
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
                buddy = viewModel.buddy,
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

// Lets a decoration reach past its content: the content keeps the layout size it would have had, but the node is
// measured and placed bigger (the reverse of padding), so a tray can be drawn around the maze without shrinking it.
private fun Modifier.numberPathOutset(horizontal: Dp, top: Dp, bottom: Dp): Modifier = layout { measurable, constraints ->
    val h = horizontal.roundToPx()
    val t = top.roundToPx()
    val b = bottom.roundToPx()
    val placeable = measurable.measure(constraints.offset(horizontal = 2 * h, vertical = t + b))
    layout(placeable.width - 2 * h, placeable.height - t - b) { placeable.place(-h, -t) }
}

// Springy squash while a key is pressed, then the tap. Silent (the maze has no click sound).
private fun Modifier.numberPathPressClick(enabled: Boolean = true, onClick: () -> Unit): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val squash by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.9f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 600f),
        label = "numberPathPress"
    )
    this
        .clickable(interactionSource = interaction, indication = null, enabled = enabled, onClick = onClick)
        .graphicsLayer { scaleX = squash; scaleY = squash }
}

// ── maze grid + hopping buddy ───────────────────────────────────────────────

// The maze sits in a chunky coral tray. The tray is drawn OUTSIDE the grid's layout box (outset), so the tiles keep
// their size and the board keeps the room it had.
@Composable
private fun PathGrid(state: NumberPathUiState, cell: Dp, s: Float, accent: Color, buddy: String, onTap: (Int) -> Unit) {
    if (state.cells.isEmpty()) return
    val colors = LocalAbColors.current
    val density = LocalDensity.current
    // Tiles lay out at rounded px sizes — the buddy must use the same
    // rounded step or it drifts off-center on wide grids.
    val cellPx = with(density) { cell.roundToPx() }.toFloat()
    val ribbon: Color = colors.accentGreen.copy(alpha = 0.75f)

    Box(Modifier.numberPathOutset(horizontal = 6.dp, top = 6.dp, bottom = 12.dp).abToyTray(accent, 6.dp)) {
        // A green ribbon through the tiles the buddy has walked, peeking out in the gaps between them.
        Canvas(Modifier.matchParentSize()) {
            val cols = maxOf(state.cols, 1)
            val line = Path()
            state.trail.forEachIndexed { n, idx ->
                val x = (idx % cols) * cellPx + cellPx / 2f
                val y = (idx / cols) * cellPx + cellPx / 2f
                if (n == 0) line.moveTo(x, y) else line.lineTo(x, y)
            }
            drawPath(line, ribbon, style = Stroke(width = cellPx * 0.24f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }

        Column {
            for (r in 0 until state.rows) {
                Row {
                    for (c in 0 until state.cols) {
                        PathTile(state, r * state.cols + c, cell, s, onTap)
                    }
                }
            }
        }

        // The buddy hops from tile to tile with a springy scale bounce, and
        // shakes when the goal bounces a wrong total back.
        val current = state.currentCell
        val targetOffset = Offset(
            (current % state.cols) * cellPx + cellPx / 2f,
            (current / state.cols) * cellPx + cellPx / 2f
        )
        val pos = remember { Animatable(targetOffset, Offset.VectorConverter) }
        val hop = remember { Animatable(1f) }
        LaunchedEffect(current) {
            hop.snapTo(1.25f)
            pos.animateTo(targetOffset, spring(dampingRatio = 0.6f, stiffness = 300f))
            hop.animateTo(1f, spring(dampingRatio = 0.4f))
        }
        val shake = remember { Animatable(0f) }
        LaunchedEffect(state.goalBounceCount, state.blockedBounceCount) {
            if (state.goalBounceCount > 0 || state.blockedBounceCount > 0) {
                repeat(3) { shake.animateTo(4f, tween(45)); shake.animateTo(-4f, tween(45)) }
                shake.animateTo(0f, tween(40))
            }
        }

        val buddySize = cell * 0.8f
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .offset {
                    IntOffset(
                        (pos.value.x - with(density) { buddySize.toPx() } / 2f + shake.value * density.density).roundToInt(),
                        (pos.value.y - with(density) { buddySize.toPx() } / 2f).roundToInt()
                    )
                }
                .size(buddySize)
                .graphicsLayer { scaleX = hop.value; scaleY = hop.value }
        ) {
            NumberPathBuddy(emoji = buddy, total = state.currentTotal, s = s)
        }
    }
}

// The kid's companion on the maze: it bobs and tilts while it waits, and wears a little chip with the running total.
@Composable
private fun NumberPathBuddy(emoji: String, total: Int, s: Float) {
    val colors = LocalAbColors.current
    val transition = rememberInfiniteTransition(label = "numberPathBuddy")
    val tilt by transition.animateFloat(
        initialValue = -5f, targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(1047, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "tilt"
    )
    val bob by transition.animateFloat(
        initialValue = -2f, targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(628, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "bob"
    )
    Box(contentAlignment = Alignment.TopCenter) {
        Text(
            emoji, fontSize = (26f * s).sp,
            modifier = Modifier.graphicsLayer { rotationZ = tilt; translationY = bob * s * density }
        )
        Text(
            "$total",
            style = AbTypography.fixed(12f * s, FontWeight.Black),
            color = Color.White,
            modifier = Modifier
                .offset(y = (-10f * s).dp)
                .abBounce(total, 1.35f)
                .background(colors.brandPrimary, CircleShape)
                .border(1.5.dp, Color.White, CircleShape)
                .padding(horizontal = (6f * s).dp, vertical = 1.dp)
        )
    }
}

// The colour of an operation: plus is green, minus orange, times purple, divide pink.
private fun opColor(colors: AbColors, op: Char?): Color = when (op) {
    '+' -> colors.accentGreen
    '-' -> colors.accentOrange
    '*' -> colors.accentPurple
    else -> colors.accentPink
}

// Face and ink of a tile. An op tile is a pastel until the buddy walks over it, then it fills with its colour.
private fun tileLook(colors: AbColors, data: NumberPathCell, visited: Boolean, justSolved: Boolean): Pair<Color, Color> =
    when (data.type) {
        PathCellType.START -> Pair(colors.brandSecondary.abReadable(3f), Color.White)
        PathCellType.GOAL ->
            if (justSolved) Pair(colors.accentGreen.abReadable(3f), Color.White) else Pair(colors.accentYellow, TILE_INK)
        else -> {
            val base: Color = opColor(colors, data.op)
            if (visited) Pair(base.abReadable(3f), Color.White)
            else Pair(base.abBlended(Color.White, 0.7f), base.abReadable(6f))
        }
    }

@Composable
private fun PathTile(state: NumberPathUiState, i: Int, cell: Dp, s: Float, onTap: (Int) -> Unit) {
    val data = state.cells.getOrNull(i) ?: return
    if (data.type == PathCellType.WALL) {
        Spacer(Modifier.size(cell))
        return
    }
    val colors = LocalAbColors.current
    val visited = i in state.trail
    val isHint = state.hintIndex == i

    // Hinted tile pulses until the kid moves. The infinite transition only
    // exists while this tile is the hint — other tiles stay animation-free.
    val pulse = if (isHint) {
        rememberInfiniteTransition(label = "hint").animateFloat(
            initialValue = 1f, targetValue = 1.12f,
            animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse), label = "hint"
        ).value
    } else 1f

    val look = tileLook(colors, data, visited, state.justSolved)
    val face = look.first
    val ink = look.second
    val edge: Dp = if (cell >= 54.dp) 4.dp else 3.dp
    val corner: Dp = (10f * s).dp
    val label: String = when (data.type) {
        PathCellType.START -> "${state.startValue}"
        PathCellType.GOAL -> "${state.target}"
        else -> data.displayText
    }

    // The cell is a fixed square; the candy tile is inset 2dp inside it, so neighbours are separated by a small gap.
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(cell)
            .padding(2.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onTap(i) }
            .abPopIn(minOf(i * 15, 400))
            .graphicsLayer { if (isHint) { scaleX = pulse; scaleY = pulse } }
            .abBounce(visited, 1.18f)
    ) {
        Box(Modifier.fillMaxSize().abToyTile(face, corner, edge), contentAlignment = Alignment.Center) {
            when (data.type) {
                PathCellType.START -> Text(
                    label, color = ink, style = AbTypography.fixed(18f * s, FontWeight.Black),
                    maxLines = 1, softWrap = false
                )
                PathCellType.GOAL -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🎯", style = AbTypography.fixed(12f * s, FontWeight.Black))
                    Text(
                        label, color = ink, maxLines = 1, softWrap = false,
                        style = AbTypography.fixed((if (state.target >= 100) 12f else 15f) * s, FontWeight.Black)
                    )
                }
                PathCellType.OP -> Text(
                    label, color = ink, maxLines = 1, softWrap = false,
                    style = AbTypography.fixed(15f * s, FontWeight.Black)
                )
                else -> {}
            }
        }
        if (isHint) {
            // The gold hint ring with a soft glow. It draws only, so it never takes a tap.
            Box(
                Modifier
                    .fillMaxSize()
                    .drawBehind {
                        val g = 3.dp.toPx()
                        drawRoundRect(
                            color = colors.accentYellow.copy(alpha = 0.5f),
                            topLeft = Offset(-g, -g),
                            size = androidx.compose.ui.geometry.Size(this.size.width + 2f * g, this.size.height + 2f * g),
                            cornerRadius = CornerRadius(corner.toPx() + g),
                            style = Stroke(width = 2f * g)
                        )
                    }
                    .border(3.5.dp, colors.accentYellow, RoundedCornerShape(corner))
            )
        }
    }
}

// A chunky candy key (tool button): a bright face with the toy 3D edge, `height` in total.
@Composable
private fun NumberPathKey(
    face: Color, width: Dp, height: Dp, modifier: Modifier = Modifier, content: @Composable () -> Unit
) {
    Box(
        modifier
            .size(width, height)
            .abToyTile(face, minOf(width, height) * 0.3f, 4.dp),
        contentAlignment = Alignment.Center
    ) { content() }
}

// ── right panel: target + animated running total ───────────────────────────

@Composable
private fun TotalsPanel(state: NumberPathUiState, s: Float, buddy: String) {
    AbGlassPanel {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens10),
            modifier = Modifier.padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens16)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6), verticalAlignment = Alignment.CenterVertically) {
                Text("🎯", fontSize = (20f * s).sp)
                Text("${state.target}", color = ORANGE, style = AbTypography.fixed(26f * s, FontWeight.Black))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6), verticalAlignment = Alignment.CenterVertically) {
                Text(buddy, fontSize = (20f * s).sp)
                Text(
                    "${state.currentTotal}",
                    color = if (state.currentTotal == state.target) Color(0xFF2E7D32) else BLUE,
                    style = AbTypography.fixed(26f * s, FontWeight.Black),
                    modifier = Modifier.abBounce(state.currentTotal, 1.25f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6), verticalAlignment = Alignment.CenterVertically) {
                Text("💡", fontSize = (18f * s).sp)
                Text(
                    "${state.hintsLeft}", color = Color(0xFFF57F17),
                    style = AbTypography.fixed(20f * s, FontWeight.Black),
                    modifier = Modifier.abBounce(state.hintsLeft)
                )
            }
        }
    }
}

// ── toolbar ─────────────────────────────────────────────────────────────────

@Composable
private fun ToolBar(
    state: NumberPathUiState, cell: Dp, s: Float,
    onUndo: () -> Unit, onRestart: () -> Unit, onHint: () -> Unit
) {
    val colors = LocalAbColors.current
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12), verticalAlignment = Alignment.CenterVertically) {
        ToolButton(face = colors.brandSecondary.abReadable(3f), enabled = state.trail.size > 1, size = cell, onClick = onUndo) {
            Icon(Icons.AutoMirrored.Rounded.Undo, contentDescription = null, tint = Color.White,
                modifier = Modifier.size((22f * s).dp))
        }
        ToolButton(face = colors.accentOrange.abReadable(3f), enabled = true, size = cell, onClick = onRestart) {
            Icon(Icons.Rounded.Refresh, contentDescription = null, tint = Color.White,
                modifier = Modifier.size((22f * s).dp))
        }
        ToolButton(face = colors.accentYellow, enabled = state.hintsLeft > 0, size = cell, onClick = onHint) {
            Text("💡${state.hintsLeft}", color = TILE_INK, style = AbTypography.fixed(16f * s, FontWeight.Black))
        }
    }
}

@Composable
private fun ToolButton(face: Color, enabled: Boolean, size: Dp, onClick: () -> Unit, content: @Composable () -> Unit) {
    NumberPathKey(
        face = face, width = size * 0.9f, height = size * 0.9f,
        modifier = Modifier
            .graphicsLayer { alpha = if (enabled) 1f else 0.4f }
            .numberPathPressClick(enabled = enabled, onClick = onClick)
    ) { content() }
}

// ── result overlay ──────────────────────────────────────────────────────────

private fun timeString(seconds: Int): String = "%d:%02d".format(seconds / 60, seconds % 60)

@Composable
private fun ResultOverlay(
    buddy: String,
    stars: Int, elapsed: Int, hintsUsed: Int, hasNextLevel: Boolean,
    onNextLevel: () -> Unit, onPlayAgain: () -> Unit, onBack: () -> Unit
) {
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        // Finishing a Number Path level is always a win — always celebrate.
        LaunchedEffect(Unit) { if (stars >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        // "Candy Pop" celebration card: the shared glossy accent panel with the bouncing badge, stars and confetti.
        GameResultCard(
            title = stringResource(R.string.balloon_great_job),
            emoji = buddy,
            accent = ButtonType.BLUE,
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
