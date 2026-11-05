package com.jigar.me.ui.view.home.screens.math_game_zone.magic_square

import androidx.compose.ui.platform.LocalConfiguration

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameCharacter

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.material.icons.rounded.Refresh
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abBlended
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.screens.math_game_zone.GameCategoryType
import com.jigar.me.ui.view.home.screens.math_game_zone.common.AbDebugFlags
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameMascotPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameResultCard
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameScoreboardPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameStatChip
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBounce
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBreathe
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTray
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abWobble
import com.jigar.me.ui.view.home.screens.math_game_zone.common.randomGameCheer
import com.jigar.me.ui.view.home.screens.math_game_zone.common.toyColor
import com.jigar.me.ui.view.home.screens.math_game_zone.gameCardStyle
import com.jigar.me.ui.view.home.screens.math_game_zone.magic_square.components.MagicSquareUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.magic_square.viewmodel.MagicSquarePlayViewModel
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
private val GREEN = Color(0xFF2E7D32)

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
    val kitScale = LocalAbScale.current.factor
    val colors = LocalAbColors.current
    // 3 rows + the sums row + the number chips must fit a short phone screen
    val cell = minOf((64f * s).dp, ((LocalConfiguration.current.screenHeightDp - 150) / 4f).dp)
    val trayAccent = remember { gameCardStyle(GameCategoryType.MAGIC_SQUARE).light }
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
                    Hud(viewModel.config.timerSeconds, viewModel.config.puzzlesGoal, state.timeLeft, state.puzzlesSolved, s)
                }
                Text(
                    text = str(R.string.magic_square_make_target, viewModel.target),
                    color = ORANGE,
                    style = AbTypography.fixed(22f * kitScale, FontWeight.ExtraBold),
                    modifier = Modifier.align(Alignment.Center)
                        .abGamePill(ORANGE_BORDER)
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
                    modifier = Modifier.weight(1f).fillMaxHeight(), character = GameCharacter.Unicorn)

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    GridView(state, viewModel, cell, s, trayAccent)

                    Spacer(Modifier.height(AppDimens.Dimens16))

                    // Number palette
                    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10), verticalAlignment = Alignment.CenterVertically) {
                        state.palette.forEach { n ->
                            MagicSquareKey(face = colors.toyColor(n - 1), side = cell * 0.8f, onClick = { viewModel.placeNumber(n) }) {
                                Text(
                                    "$n",
                                    style = AbTypography.fixed(30f * s, FontWeight.Black).copy(
                                        shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f)
                                    ),
                                    color = Color.White,
                                    modifier = Modifier.abBounce(n)
                                )
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

        // Debug builds only: lets the lead render the real result overlay without playing.
        if (AbDebugFlags.showResultEnabled) {
            ResultOverlay(state.score, state.puzzlesSolved, viewModel.bestScore,
                onPlayAgain = { viewModel.start() }, onBack = onBackClick)
        }
    }
}

// ── toy-box board ────────────────────────────────────────────────────────────

/** Reports the size of the board itself while the tray around it is drawn outward, so the layout keeps its old size. */
private fun Modifier.magicOutward(width: Dp, height: Dp, inset: Dp): Modifier = this.layout { measurable, _ ->
    val placeable = measurable.measure(Constraints())
    val shift = inset.roundToPx()
    layout(width.roundToPx(), height.roundToPx()) { placeable.place(-shift, -shift) }
}

private fun rowWrong(state: MagicSquareUiState, viewModel: MagicSquarePlayViewModel, r: Int): Boolean =
    (0..2).all { state.grid[r * 3 + it] != 0 } && viewModel.rowSum(state.grid, r) != viewModel.target

private fun colWrong(state: MagicSquareUiState, viewModel: MagicSquarePlayViewModel, c: Int): Boolean =
    (0..2).all { state.grid[it * 3 + c] != 0 } && viewModel.colSum(state.grid, c) != viewModel.target

// The board sits in a toy tray. The tray grows outward so the layout keeps its old size.
@Composable
private fun GridView(
    state: MagicSquareUiState,
    viewModel: MagicSquarePlayViewModel,
    cell: Dp,
    s: Float,
    accent: Color
) {
    val gap = (6f * s).dp
    val boardW = cell * 3 + cell * 0.72f + gap * 3
    val boardH = cell * 3 + cell * 0.5f + gap * 3
    val inset = 8.dp
    Box(
        modifier = Modifier
            .magicOutward(boardW, boardH, inset)
            .abToyTray(accent, inset)
    ) {
        Box(modifier = Modifier.size(boardW, boardH), contentAlignment = Alignment.Center) {
            Column(verticalArrangement = Arrangement.spacedBy(gap)) {
                for (r in 0 until 3) {
                    Row(horizontalArrangement = Arrangement.spacedBy(gap), verticalAlignment = Alignment.CenterVertically) {
                        for (c in 0 until 3) {
                            val i = r * 3 + c
                            val lineWrong = state.grid[i] != 0 && !state.fixed[i] &&
                                (rowWrong(state, viewModel, r) || colWrong(state, viewModel, c))
                            CellView(state, viewModel, i, cell, s, lineWrong)
                        }
                        SumChip(
                            viewModel.rowSum(state.grid, r), viewModel.rowComplete(state.grid, r),
                            rowWrong(state, viewModel, r), cell, s
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(gap), verticalAlignment = Alignment.CenterVertically) {
                    for (c in 0 until 3) {
                        Box(modifier = Modifier.width(cell), contentAlignment = Alignment.Center) { // center the chip under its column
                            SumChip(
                                viewModel.colSum(state.grid, c), viewModel.colComplete(state.grid, c),
                                colWrong(state, viewModel, c), cell, s
                            )
                        }
                    }
                    Spacer(Modifier.size(cell * 0.72f, cell * 0.5f)) // filler under the row-chip column
                }
            }
            if (state.justSolved) {
                MagicSquareSolvedBurst(side = cell * 3, unit = s)
            }
        }
    }
}

@Composable
private fun CellView(
    state: MagicSquareUiState,
    viewModel: MagicSquarePlayViewModel,
    i: Int,
    cell: Dp,
    s: Float,
    lineWrong: Boolean
) {
    val colors = LocalAbColors.current
    val value = state.grid[i]
    val isFixed = state.fixed[i]
    val isSelected = state.selectedIndex == i

    // given numbers are purple, yours are blue, empty slots are pale, the picked slot is sunny
    val face = when {
        state.justSolved -> colors.accentGreen.abReadable(3f)
        isFixed -> colors.accentPurple.abReadable(3f)
        value != 0 -> colors.brandPrimary.abReadable(3f)
        isSelected -> colors.accentYellow.abBlended(Color.White, 0.45f)
        else -> colors.brandSecondary.abBlended(Color.White, 0.72f)
    }
    val corner = (14f * s).dp
    val scale by animateFloatAsState(if (state.justSolved) 1.05f else 1f, spring(dampingRatio = 0.5f), label = "")

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .then(if (isSelected) Modifier.abBreathe(0.045f, 1300) else Modifier)
            .size(cell)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { viewModel.tapCell(i) }
            .abToyTile(face, corner, (3f * s).dp)
            .then(
                if (isSelected) Modifier.border((3f * s).dp, colors.accentOrange, RoundedCornerShape(corner))
                else Modifier
            )
    ) {
        Text(
            if (value == 0) "" else "$value",
            style = AbTypography.fixed(30f * s, FontWeight.Black).copy(
                shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f)
            ),
            color = Color.White,
            modifier = Modifier.abBounce(value).abWobble(lineWrong)
        )
    }
}

/**
 * A row/column total chip. Neutral while it fills up, green and popping once it hits the target, red and shaking if the
 * line is full but does not add up.
 */
@Composable
private fun SumChip(sum: Int, complete: Boolean, wrong: Boolean, cell: Dp, s: Float) {
    val colors = LocalAbColors.current
    val face = when {
        complete -> colors.accentGreen.abReadable(3f)
        wrong -> colors.danger.abReadable(3f)
        else -> colors.brandSecondary.abBlended(Color.White, 0.78f)
    }
    val ink = if (complete || wrong) Color.White else colors.brandPrimaryDark
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .abBounce(complete, 1.22f)
            .abWobble(wrong)
            .size(cell * 0.72f, cell * 0.5f)
            .abToyTile(face, (10f * s).dp, (3f * s).dp)
    ) {
        Text(
            "$sum", color = ink,
            style = AbTypography.fixed(18f * s, FontWeight.Black),
            modifier = Modifier.abBounce(sum)
        )
    }
}

/** A chunky candy key that squishes while it is pressed. */
@Composable
private fun MagicSquareKey(face: Color, side: Dp, onClick: () -> Unit, content: @Composable BoxScope.() -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val squash by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "magicSquareKey"
    )
    Box(
        modifier = Modifier
            .size(side)
            .graphicsLayer { scaleX = squash; scaleY = squash }
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .abToyTile(face, side * 0.3f, 4.dp),
        contentAlignment = Alignment.Center,
        content = content
    )
}

/** A burst of stars from the board's centre when the square is solved. It starts the moment it appears and fades out. */
@Composable
private fun MagicSquareSolvedBurst(side: Dp, unit: Float) {
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
            drawMagicSquareStar(star, outer, tt * 220f, colors.toyColor(k).copy(alpha = fade))
        }
    }
}

private fun DrawScope.drawMagicSquareStar(centre: Offset, outer: Float, degrees: Float, color: Color) {
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

@Composable private fun str(id: Int): String = androidx.compose.ui.res.stringResource(id)
@Composable private fun str(id: Int, vararg args: Any): String = androidx.compose.ui.res.stringResource(id, *args)

@Composable
private fun Hud(timerSeconds: Int?, puzzlesGoal: Int?, timeLeft: Int, puzzlesSolved: Int, s: Float) {
    val colors = LocalAbColors.current
    val kitScale = LocalAbScale.current.factor
    val style = AbTypography.fixed(20f * kitScale, FontWeight.ExtraBold)
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16), verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = AppDimens.Dimens12).abGamePill()
            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6)
    ) {
        if (timerSeconds != null) Text("⏱ $timeLeft", color = colors.brandPrimaryDark, style = style)
        else if (puzzlesGoal != null) Text("🧩 $puzzlesSolved/$puzzlesGoal", color = colors.brandPrimaryDark, style = style)
    }
}

// ── result ───────────────────────────────────────────────────────────

@Composable
private fun ResultOverlay(score: Int, puzzlesSolved: Int, bestScore: Int, onPlayAgain: () -> Unit, onBack: () -> Unit) {
    val starCount = when { puzzlesSolved >= 5 -> 3; puzzlesSolved >= 3 -> 2; else -> 1 }
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { if (starCount >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        GameResultCard(
            title = str(R.string.balloon_great_job),
            emoji = "🧩",
            accent = ButtonType.PURPLE,
            stars = starCount,
            score = str(R.string.balloon_final_score) + ": $score",
            stats = {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    GameStatChip(emoji = "🧩", value = "$puzzlesSolved", color = GREEN)
                    GameStatChip(emoji = "🏆", value = "$bestScore", color = ORANGE)
                }
            },
            buttons = {
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    GameButton(
                        text = str(R.string.balloon_play_again),
                        icon = Icons.Rounded.Refresh, type = ButtonType.POSITIVE,
                        onClick = { if (enabled) onPlayAgain() }
                    )
                    GameHomeButton(onClick = { if (enabled) onBack() })
                }
            }
        )
    }
}
