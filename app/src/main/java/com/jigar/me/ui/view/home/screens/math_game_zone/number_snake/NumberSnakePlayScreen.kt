package com.jigar.me.ui.view.home.screens.math_game_zone.number_snake

import androidx.compose.ui.platform.LocalConfiguration

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.StrokeCap
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
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
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
import com.jigar.me.ui.view.home.screens.math_game_zone.number_snake.components.NumberSnakeConfig
import com.jigar.me.ui.view.home.screens.math_game_zone.number_snake.components.NumberSnakeUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.number_snake.viewmodel.NumberSnakePlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay

private val GOLD = Color(0xFFFF6F00)
private val GOLD_BORDER = Color(0xFFFFA000)

@Composable
fun NumberSnakePlayScreen(
    viewModel: NumberSnakePlayViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val s = gameScale()
    val colors = LocalAbColors.current
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
                    NumberSnakeToolPill(
                        face = colors.accentYellow.abReadable(3f), corner = (16f * s).dp, s = s,
                        modifier = Modifier.graphicsLayer { alpha = if (state.hintsLeft > 0) 1f else 0.5f },
                        onClick = { viewModel.useHint() }
                    ) {
                        Text(
                            "💡 ${state.hintsLeft}", color = Color.White,
                            style = AbTypography.fixed(18f * s, FontWeight.Black).copy(
                                shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 2f), 0f)
                            ),
                            modifier = Modifier.abBounce(state.hintsLeft)
                        )
                    }
                    // Clears the number in the currently selected cell so a
                    // kid can undo a placement without waiting for a wrong-flash.
                    NumberSnakeToolPill(
                        face = colors.accentPink.abReadable(3f), corner = (16f * s).dp, s = s,
                        onClick = { state.selectedIndex?.let { viewModel.clearCell(it) } }
                    ) {
                        Text("🧹", color = Color.White, style = AbTypography.fixed(18f * s, FontWeight.Black))
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6),
                    modifier = Modifier.align(Alignment.CenterEnd)
                        .padding(end = AppDimens.Dimens16)
                        .abGamePill()
                        .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
                ) {
                    Text("⏱", fontSize = (22f * s).sp)
                    Text(
                        "%d:%02d".format(state.elapsed / 60, state.elapsed % 60),
                        color = colors.textPrimary,
                        style = AbTypography.fixed(22f * appScale(), FontWeight.Black)
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

        // Debug builds only (`--ez ab_show_result true`): shows the real result overlay without playing a puzzle.
        if (AbDebugFlags.showResultEnabled && !state.isGameOver) {
            ResultOverlay(state = state, config = viewModel.config, bestTime = viewModel.bestTime,
                onPlayAgain = { viewModel.start() }, onBack = onBackClick)
        }
    }
}

// Lets a decoration reach past its content: the content keeps the layout size it would have had, but the node is
// measured and placed bigger (the reverse of padding), so a tray can be drawn around the board without shrinking it.
private fun Modifier.numberSnakeOutset(horizontal: Dp, top: Dp, bottom: Dp): Modifier = layout { measurable, constraints ->
    val h = horizontal.roundToPx()
    val t = top.roundToPx()
    val b = bottom.roundToPx()
    val placeable = measurable.measure(constraints.offset(horizontal = 2 * h, vertical = t + b))
    layout(placeable.width - 2 * h, placeable.height - t - b) { placeable.place(-h, -t) }
}

// Springy squash while pressed, then the tap. Silent (the game plays its own sounds).
private fun Modifier.numberSnakePressClick(pressedScale: Float, onClick: () -> Unit): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val squash by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 600f),
        label = "numberSnakePress"
    )
    this
        .clickable(interactionSource = interaction, indication = null, onClick = onClick)
        .graphicsLayer { scaleX = squash; scaleY = squash }
}

// A chunky candy pill for the header tools (hint, eraser): white ink on a bright face, squashes while pressed.
@Composable
private fun NumberSnakeToolPill(
    face: Color, corner: Dp, s: Float, modifier: Modifier = Modifier, onClick: () -> Unit, content: @Composable () -> Unit
) {
    Box(
        modifier
            .numberSnakePressClick(0.9f, onClick)
            .abToyTile(face, corner, 3.dp)
            .padding(horizontal = (14f * s).dp, vertical = (5f * s).dp),
        contentAlignment = Alignment.Center
    ) { content() }
}

@Composable
private fun NumberGrid(state: NumberSnakeUiState, s: Float, onTapCell: (Int) -> Unit) {
    val colors = LocalAbColors.current
    val accent: Color = gameCardStyle(GameCategoryType.NUMBER_SNAKE).dark
    val shake = remember { Animatable(0f) }
    LaunchedEffect(state.wrongFlash) {
        if (state.wrongFlash) {
            repeat(3) { shake.animateTo(5f, tween(40)); shake.animateTo(-5f, tween(40)) }
            shake.animateTo(0f, tween(35))
        }
    }
    val gap = 9f * s
    // header + chip row + tray take ~160dp: on a short phone screen the cells shrink so the chip row stays on screen
    val cellSize = minOf(
        when (state.size) { 4 -> 56f; 5 -> 48f; else -> 42f } * s,
        ((LocalConfiguration.current.screenHeightDp - 165f) - gap * (state.size - 1)) / state.size
    )

    // The grid sits in a chunky game-coloured tray. The tray is drawn OUTSIDE the grid's layout box (outset), so every
    // cell keeps its size and the board keeps the room it had.
    Box(
        modifier = Modifier
            .graphicsLayer { translationX = shake.value * density }
            .numberSnakeOutset(horizontal = 6.dp, top = 6.dp, bottom = 12.dp)
            .abToyTray(accent, 6.dp)
    ) {
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
            // The snake's body takes the colour of the lower number, so the trail reads as a rainbow.
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
                            drawLine(colors.toyColor(minOf(v, v2)), centerOf(i), centerOf(i2), gapPx * 1.5f, cap = StrokeCap.Round)
                        }
                    }
                    if (r + 1 < n) {
                        val i2 = i + n
                        val v2 = state.grid[i2]
                        if (v2 != 0 && kotlin.math.abs(v - v2) == 1) {
                            drawLine(colors.toyColor(minOf(v, v2)), centerOf(i), centerOf(i2), gapPx * 1.5f, cap = StrokeCap.Round)
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
                        // A placed or given number takes its own rainbow colour (a given one is drawn as a cream locked tile, see NumberSnakeTile); a
                        // wrong board flashes red; an empty cell is a dashed slot waiting for a number.
                        val face: Color = when {
                            state.wrongFlash && value != 0 -> colors.danger.abReadable(3f)
                            value != 0 -> colors.toyColor(value)
                            else -> accent
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(cellSize.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    enabled = !isGiven && !isHint
                                ) { onTapCell(i) }
                                .abPopIn(minOf(i * 30, 400))
                                .abBounce(value, 1.22f)
                                .abBounce(state.justSolved, 1.15f)
                        ) {
                            NumberSnakeTile(
                                value = value, face = face, isGiven = isGiven, isHint = isHint,
                                isSelected = isSelected, cellSize = cellSize.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

// One board cell: a candy number in its rainbow colour (a white ring marks a clue, a star marks a hint), or an empty
// dashed slot. It fills its whole square; a selected cell gets a breathing ring.
@Composable
private fun NumberSnakeTile(
    value: Int, face: Color, isGiven: Boolean, isHint: Boolean, isSelected: Boolean, cellSize: Dp
) {
    val colors = LocalAbColors.current
    val edge: Dp = if (cellSize >= 48.dp) 4.dp else 3.dp
    val corner: Dp = cellSize * 0.26f
    Box(Modifier.size(cellSize), contentAlignment = Alignment.Center) {
        if (value == 0) {
            Box(
                Modifier
                    .size(cellSize)
                    .drawBehind {
                        val r = corner.toPx()
                        val sw = 2.5.dp.toPx()
                        drawRoundRect(face.copy(alpha = 0.18f), cornerRadius = CornerRadius(r, r))
                        drawRoundRect(
                            color = face.copy(alpha = 0.75f),
                            topLeft = Offset(sw / 2f, sw / 2f),
                            size = Size(this.size.width - sw, this.size.height - sw),
                            cornerRadius = CornerRadius((r - sw / 2f).coerceAtLeast(0f)),
                            style = Stroke(
                                width = sw,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 5.dp.toPx()))
                            )
                        )
                    }
            )
        } else {
            // Two clearly different looks: a number the CHILD placed is a solid rainbow candy with a white digit; a number that was
            // GIVEN with the puzzle is a cream "printed" tile with a thick coloured rim, a dark digit and a little lock. So the
            // two never read alike, even though both take their colour from the number.
            val tileFace: Color = if (isGiven) Color(0xFFFFF7E6) else face
            val ink: Color = if (isGiven) face.abDarkened(0.45f) else Color.White
            Box(Modifier.size(cellSize).abToyTile(tileFace, corner, edge), contentAlignment = Alignment.Center) {
                Text(
                    "$value", color = ink, maxLines = 1, softWrap = false,
                    style = AbTypography.fixed(cellSize.value * 0.46f, FontWeight.Black).copy(
                        shadow = if (isGiven) null else Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 2f), 0f)
                    )
                )
                if (isGiven) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .border(3.5.dp, face, RoundedCornerShape(corner))
                    )
                    Icon(
                        Icons.Filled.Lock, contentDescription = null, tint = face,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 3.dp, end = 3.dp)
                            .size(cellSize * 0.24f)
                    )
                }
                if (isHint) {
                    Icon(
                        Icons.Filled.Star, contentDescription = null, tint = colors.accentYellow,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(3.dp)
                            .size(cellSize * 0.26f)
                    )
                }
            }
        }
        if (isSelected) {
            // The selection ring: a bright rim with a soft glow outside it. It draws only, so it never takes a tap.
            Box(
                Modifier
                    .size(cellSize + 2.dp)
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
                    .border(3.5.dp, colors.brandPrimary, RoundedCornerShape(corner))
            )
        }
    }
}

@Composable
private fun Tray(state: NumberSnakeUiState, s: Float, onTapNumber: (Int) -> Unit) {
    val colors = LocalAbColors.current
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
            // A number chip in the tray row: the same rainbow colour the number takes on the board.
            val chip: Dp = (40f * s).dp
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .numberSnakePressClick(0.88f) { onTapNumber(n) }
                    .abPopIn((n % 8) * 20)
                    .size(chip)
                    .abToyTile(colors.toyColor(n), chip * 0.3f, 4.dp)
            ) {
                Text(
                    "$n", color = Color.White,
                    style = AbTypography.fixed(18f * s, FontWeight.Black).copy(
                        shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 2f), 0f)
                    )
                )
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
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { if (stars >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        // "Candy Pop" celebration card: the shared glossy accent panel with the bouncing badge, stars and confetti.
        GameResultCard(
            title = stringResource(if (stars >= 2) R.string.balloon_great_job else R.string.good_try),
            emoji = "🐍",
            accent = ButtonType.TEAL,
            stars = stars,
            score = stringResource(R.string.balloon_final_score) + ": ${state.score}",
            stats = {
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
                    GameStatChip("⏱️", "%d:%02d".format(state.elapsed / 60, state.elapsed % 60), Color(0xFFC2570C))
                    GameStatChip("🏆", if (bestTime > 0) "%d:%02d".format(bestTime / 60, bestTime % 60) else "—", Color(0xFFB45309))
                }
            },
            buttons = {
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    GameButton(text = stringResource(R.string.balloon_play_again), type = ButtonType.POSITIVE,
                        onClick = { if (enabled) onPlayAgain() })
                    GameHomeButton(onClick = { if (enabled) onBack() })
                }
            }
        )
    }
}
