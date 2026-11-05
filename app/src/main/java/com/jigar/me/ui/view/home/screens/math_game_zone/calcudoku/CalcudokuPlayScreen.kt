package com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.ab.AbColors
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abBlended
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.screens.math_game_zone.GameCategoryType
import com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku.components.Cage
import com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku.components.CageOp
import com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku.components.CalcudokuUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku.viewmodel.CalcudokuPlayViewModel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.AbDebugFlags
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameMascotPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameResultCard
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameStatChip
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBounce
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBreathe
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abPopIn
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTray
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abWobble
import com.jigar.me.ui.view.home.screens.math_game_zone.common.randomGameCheer
import com.jigar.me.ui.view.home.screens.math_game_zone.common.toyColor
import com.jigar.me.ui.view.home.screens.math_game_zone.gameCardStyle
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)
private val CAGE_LINE = Color(0xFF455A64)
// Every digit is one dark ink, on every cage colour. Colour is only for the cages and for a wrong number (white on solid red).
private val DIGIT_INK = Color(0xFF211A3C)
private val GIVEN_FACE = Color(0xFFF3F1EC)
private val GIVEN_RIM = Color(0xFF8D8A99)
private val HINT_FILL = Color(0xFFFFF3C4)
private val HINT_RIM = Color(0xFFFFCA28)

private val cdScale: Float
    get() = if (DeviceInfo.isLargeTablet) 1.7f else if (DeviceInfo.isTablet) 1.45f else 1.0f

// Measurements of the candy cages (in design points, scaled by the game's scale).
private object CalcudokuArt {
    const val GAP = 3f      // half of the space between two neighbouring cages
    const val RADIUS = 9f   // candy corner of a cage
    const val RIM = 2.5f
    const val EDGE = 3f     // the 3D lip under a cage
}

@Composable
fun CalcudokuPlayScreen(
    viewModel: CalcudokuPlayViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val baseCheer = remember { randomGameCheer() }
    val s = cdScale
    val kitScale = LocalAbScale.current.factor
    val colors = LocalAbColors.current
    val cellBase = if (state.size <= 3) 72f else if (state.size == 4) 64f else 54f
    // the header + board + hint line must fit a short phone screen (the keys now live on the right, so they take no height)
    val cell = minOf((cellBase * s).dp, ((LocalConfiguration.current.screenHeightDp - 130) / state.size).dp)
    // everything drawn inside the board (cage candy, badges, digits) scales with the cell, so a small cell never gets fat art
    val u = cell.value / 56f
    val trayAccent = remember { gameCardStyle(GameCategoryType.CALCUDOKU).light }
    val paint = calcudokuPaint(state, colors)
    LaunchedEffect(Unit) { viewModel.start() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButtonWithText(title = str(R.string.calcudoku_game), onBackClick = onBackClick)
                Spacer(Modifier.weight(1f))
                Text(
                    text = str(R.string.calcudoku_prompt),
                    color = ORANGE,
                    style = AbTypography.fixed(22f * kitScale, FontWeight.ExtraBold),
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier
                        .abGamePill(ORANGE_BORDER)
                        .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens8)
                )
                Spacer(Modifier.weight(1f))
                CalcudokuStatChips(
                    state.score, state.elapsed, viewModel.bestTime, kitScale,
                    Modifier.padding(end = AppDimens.Dimens16)
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
                    modifier = Modifier.weight(1f).fillMaxHeight(), character = GameCharacter.Elephant)

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CalcudokuBoard(state, cell, u, trayAccent, paint) { viewModel.tapCell(it) }

                    Spacer(Modifier.height(AppDimens.Dimens8 + 6.dp))

                    HintBar(state, s)
                }

                CalcudokuKeypad(
                    count = state.size,
                    keySide = (cell * 0.95f).coerceIn(46.dp, (66f * s).dp),
                    s = s,
                    onNumber = { viewModel.placeNumber(it) },
                    onErase = { viewModel.erase() },
                    onReset = { viewModel.reset() },
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
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

        // Debug builds only: lets the lead render the real result overlay without playing.
        if (AbDebugFlags.showResultEnabled) {
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

// ── toy-box board ────────────────────────────────────────────────────────────

/** Per cell: the pastel face, the rim and the strong colour (for the sum badge) of the cage it belongs to. */
private class CalcudokuPaint(val faces: List<Color>, val rims: List<Color>, val bases: List<Color>)

/** Greedy colouring: a cage never gets the same colour as a cage it touches (while the palette lasts). */
private fun calcudokuCageColorIndices(n: Int, cageId: List<Int>, palette: Int): Map<Int, Int> {
    val assigned = HashMap<Int, Int>()
    for (i in cageId.indices) {
        val id = cageId[i]
        if (assigned.containsKey(id)) continue
        val used = HashSet<Int>()
        for (j in cageId.indices) {
            if (cageId[j] != id) continue
            val r = j / n
            val c = j % n
            val neighbours = ArrayList<Int>(4)
            if (r > 0) neighbours.add(j - n)
            if (r < n - 1) neighbours.add(j + n)
            if (c > 0) neighbours.add(j - 1)
            if (c < n - 1) neighbours.add(j + 1)
            for (nb in neighbours) {
                val k = assigned[cageId[nb]]
                if (k != null) used.add(k)
            }
        }
        var pick = 0
        while (used.contains(pick) && pick < palette - 1) pick++
        assigned[id] = pick
    }
    return assigned
}

/** Given cells are grey stone, finished cages go green, the rest wear their cage's candy colour. Never green by chance. */
private fun calcudokuPaint(state: CalcudokuUiState, colors: AbColors): CalcudokuPaint {
    val count = state.size * state.size
    if (state.cageId.size != count || state.fixed.size != count || state.grid.size != count) {
        return CalcudokuPaint(emptyList(), emptyList(), emptyList())
    }
    val palette = listOf(
        colors.brandSecondary, colors.accentPink, colors.accentOrange,
        colors.accentPurple, colors.accentYellow, colors.brandPrimary
    )
    val pick = calcudokuCageColorIndices(state.size, state.cageId, palette.size)
    val faces = ArrayList<Color>(count)
    val rims = ArrayList<Color>(count)
    val bases = ArrayList<Color>(count)
    for (i in 0 until count) {
        val base = palette[pick[state.cageId[i]] ?: 0]
        val isFixed = state.fixed[i]
        val done = !isFixed && (state.justSolved || cageSatisfiedForCell(state.cages, state.grid, i))
        if (isFixed) {
            faces.add(GIVEN_FACE)
            rims.add(GIVEN_RIM)
            bases.add(GIVEN_RIM)
        } else if (done) {
            faces.add(colors.accentGreen.abBlended(Color.White, 0.5f))
            rims.add(colors.accentGreen.abDarkened(0.08f))
            bases.add(colors.accentGreen)
        } else {
            faces.add(base.abBlended(Color.White, 0.6f))
            rims.add(base)
            bases.add(base)
        }
    }
    return CalcudokuPaint(faces, rims, bases)
}

/** Reports the size of the board itself while the tray around it is drawn outward, so the column keeps its old height. */
private fun Modifier.calcudokuOutward(side: Dp, inset: Dp): Modifier = this.layout { measurable, _ ->
    val placeable = measurable.measure(Constraints())
    val sidePx = side.roundToPx()
    val shift = inset.roundToPx()
    layout(sidePx, sidePx) { placeable.place(-shift, -shift) }
}

@Composable
private fun CalcudokuBoard(
    state: CalcudokuUiState,
    cell: Dp,
    u: Float,
    accent: Color,
    paint: CalcudokuPaint,
    onTap: (Int) -> Unit
) {
    val g = cell * state.size
    val inset = 6.dp
    Box(
        modifier = Modifier
            .calcudokuOutward(g, inset)
            .abPopIn(50)
            .abToyTray(accent, inset)
    ) {
        Box(modifier = Modifier.size(g)) {
            Canvas(modifier = Modifier.size(g)) {
                drawCalcudokuCages(state.size, cell.toPx(), u, state.cageId, paint)
            }
            Column {
                for (r in 0 until state.size) {
                    Row {
                        for (c in 0 until state.size) {
                            CalcudokuCell(state, r * state.size + c, cell, u, paint, onTap)
                        }
                    }
                }
            }
            if (state.justSolved) {
                CalcudokuSolvedBurst(side = g, unit = u)
            }
        }
    }
}

/**
 * Draws each cage as ONE jelly region: a pastel face with a rim, a glossy top strip and a 3D lip. Cells of the same cage are
 * joined (no seams), cages are separated by a clear gap, so the cage borders read without any dark lines.
 */
private fun DrawScope.drawCalcudokuCages(n: Int, cell: Float, unit: Float, cageId: List<Int>, paint: CalcudokuPaint) {
    if (n <= 0 || cageId.size != n * n || paint.faces.size != n * n || paint.rims.size != n * n) return
    val px = density
    val gap = CalcudokuArt.GAP * unit * px
    val radius = CalcudokuArt.RADIUS * unit * px
    val rim = CalcudokuArt.RIM * unit * px
    val edge = CalcudokuArt.EDGE * unit * px
    val c0 = gap + radius          // the cage's skeleton sits this far inside the cell
    val side = cell - 2f * c0
    if (side <= 0f) return

    val order = ArrayList<Int>()
    val members = HashMap<Int, MutableList<Int>>()
    for (i in 0 until n * n) {
        val id = cageId[i]
        val list = members[id]
        if (list == null) {
            order.add(id)
            members[id] = mutableListOf(i)
        } else {
            list.add(i)
        }
    }

    for (id in order) {
        val cells = members[id] ?: continue
        val first = cells.first()
        // The skeleton: a square per cell plus a bridge to each same-cage neighbour. Stroking it with a round join
        // `radius` wide grows it into the candy shape with rounded outer corners.
        val skeleton = Path()
        for (i in cells) {
            val row = i / n
            val col = i % n
            val x0 = col * cell
            val y0 = row * cell
            skeleton.addRect(Rect(x0 + c0, y0 + c0, x0 + c0 + side, y0 + c0 + side))
            val joinRight = col < n - 1 && cageId[i + 1] == id
            val joinDown = row < n - 1 && cageId[i + n] == id
            if (joinRight) {
                skeleton.addRect(Rect(x0 + cell - c0, y0 + c0, x0 + cell + c0, y0 + c0 + side))
            }
            if (joinDown) {
                skeleton.addRect(Rect(x0 + c0, y0 + cell - c0, x0 + c0 + side, y0 + cell + c0))
            }
            if (joinRight && joinDown && cageId[i + n + 1] == id) {
                skeleton.addRect(Rect(x0 + cell - c0, y0 + cell - c0, x0 + cell + c0, y0 + cell + c0))
            }
        }

        val face = paint.faces[first]
        val rimColor = paint.rims[first]
        val lip = rimColor.abDarkened(0.3f)
        val grow = Stroke(width = 2f * radius, join = StrokeJoin.Round)
        val growInner = Stroke(width = 2f * (radius - rim), join = StrokeJoin.Round)

        translate(top = edge) { drawPath(skeleton, lip, style = grow) }
        drawPath(skeleton, rimColor, style = grow)
        drawPath(skeleton, face)
        drawPath(skeleton, face, style = growInner)

        // Solid dividers between the cells of one cage (the cage's own colour, as strong as its rim), so every row and column
        // shows its boxes clearly even inside a big cage.
        val divider = rimColor.copy(alpha = 0.9f)
        val dividerW = 2.2f * unit * px
        val pad = gap + rim * 0.5f
        fun same(j: Int) = j in 0 until n * n && cageId[j] == id
        for (i in cells) {
            val row = i / n
            val col = i % n
            val x0 = col * cell
            val y0 = row * cell
            if (col < n - 1 && same(i + 1)) {
                val up = row > 0 && same(i - n) && same(i - n + 1)
                val down = row < n - 1 && same(i + n) && same(i + n + 1)
                drawLine(
                    divider, Offset(x0 + cell, if (up) y0 else y0 + pad), Offset(x0 + cell, if (down) y0 + cell else y0 + cell - pad),
                    dividerW, StrokeCap.Round
                )
            }
            if (row < n - 1 && same(i + n)) {
                val left = col > 0 && same(i - 1) && same(i + n - 1)
                val right = col < n - 1 && same(i + 1) && same(i + n + 1)
                drawLine(
                    divider, Offset(if (left) x0 else x0 + pad, y0 + cell), Offset(if (right) x0 + cell else x0 + cell - pad, y0 + cell),
                    dividerW, StrokeCap.Round
                )
            }
        }

        // Glossy strip along every top edge of the cage.
        for (i in cells) {
            val row = i / n
            val col = i % n
            val topOpen = row == 0 || cageId[i - n] != id
            if (!topOpen) continue
            val joinLeft = col > 0 && cageId[i - 1] == id
            val joinRight = col < n - 1 && cageId[i + 1] == id
            val x0 = col * cell
            val y0 = row * cell
            val left = x0 + (if (joinLeft) 0f else c0)
            val right = x0 + cell - (if (joinRight) 0f else c0)
            drawRoundRect(
                Color.White.copy(alpha = 0.5f),
                Offset(left, y0 + gap + rim + 1.5f * unit * px),
                Size(right - left, 3f * unit * px),
                CornerRadius(1.5f * unit * px)
            )
        }
    }
}

@Composable
private fun CalcudokuCell(
    state: CalcudokuUiState,
    i: Int,
    cell: Dp,
    u: Float,
    paint: CalcudokuPaint,
    onTap: (Int) -> Unit
) {
    val colors = LocalAbColors.current
    val v = state.grid[i]
    val isFixed = state.fixed[i]
    val isSelected = state.selectedIndex == i
    val conflict = hasConflict(state.grid, state.size, i)
    val cageDone = !isFixed && (state.justSolved || cageSatisfiedForCell(state.cages, state.grid, i))
    val clue: String? = if (isFixed) null else clueTextFor(state.cages, i)
    val inset = ((CalcudokuArt.GAP + CalcudokuArt.RIM + 1.5f) * u).dp
    val digitColor = if (conflict) Color.White else DIGIT_INK
    val base = paint.bases.getOrNull(i) ?: colors.brandSecondary
    val plateShape = RoundedCornerShape((8f * u).dp)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(cell)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onTap(i) }
    ) {
        // The tile under a digit. Given: white. Selected: white with a dark outline. Wrong: solid red with a white edge (the digit
        // turns white), so a wrong number never blends into a cage colour.
        if (isFixed || conflict || isSelected) {
            val face = if (conflict) colors.danger.abReadable(3f) else if (isSelected) Color.White else Color.White.copy(alpha = 0.85f)
            val pad = if (isFixed && !conflict && !isSelected) inset + (1.5f * u).dp else inset
            Box(
                Modifier.fillMaxSize().padding(pad)
                    .then(if (isSelected) Modifier.abBreathe(0.05f, 1300) else Modifier)
                    .background(face, plateShape)
                    .then(
                        if (isSelected) Modifier.border((3f * u).dp, DIGIT_INK, plateShape)
                        else if (conflict) Modifier.border((2f * u).dp, Color.White, plateShape)
                        else Modifier
                    )
            )
        }
        if (clue != null) {
            // The cage's sum, as a little coloured badge in its top-left cell. It lights up green and pops once the cage adds up.
            val badgeFace = if (cageDone) colors.accentGreen.abReadable(3f) else base.abReadable(3f)
            Text(
                clue,
                style = AbTypography.fixed(11f * u, FontWeight.Black),
                color = Color.White,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = inset, top = inset)
                    .abBounce(cageDone, 1.25f)
                    .background(badgeFace, CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                    .padding(horizontal = (4f * u).dp, vertical = (0.5f * u).dp)
            )
        }
        // The digit is sized from the cell and sits at the same height in every cell (a touch below centre, clear of the
        // sum badge in the top-left corner), so a row of numbers always lines up.
        Text(
            if (v == 0) "" else "$v",
            style = AbTypography.fixed(26f * u, FontWeight.Black),
            color = digitColor,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier
                .offset(y = (4f * u).dp)
                .abBounce(v)
                .abWobble(conflict)
        )
    }
}

/** A burst of stars from the board's centre when the puzzle is solved. It starts the moment it appears and fades out. */
@Composable
private fun CalcudokuSolvedBurst(side: Dp, unit: Float) {
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
    Canvas(modifier = Modifier.size(side)) {
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
            drawCalcudokuStar(star, outer, tt * 220f, colors.toyColor(k).copy(alpha = fade))
        }
    }
}

private fun DrawScope.drawCalcudokuStar(centre: Offset, outer: Float, degrees: Float, color: Color) {
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

/** A chunky candy key that squishes while it is pressed. */
@Composable
private fun CalcudokuKey(face: Color, width: Dp, height: Dp, onClick: () -> Unit, content: @Composable BoxScope.() -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val squash by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "calcudokuKey"
    )
    Box(
        modifier = Modifier
            .size(width, height)
            .graphicsLayer { scaleX = squash; scaleY = squash }
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .abToyTile(face, minOf(width, height) * 0.3f, 4.dp),
        contentAlignment = Alignment.Center,
        content = content
    )
}

/**
 * The keys, on the right of the board: the numbers in a grid (three across, the last row centred) with erase and start-over
 * as two wide keys under them, so the hand never covers the board.
 */
@Composable
private fun CalcudokuKeypad(
    count: Int,
    keySide: Dp,
    s: Float,
    onNumber: (Int) -> Unit,
    onErase: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAbColors.current
    val gap = AppDimens.Dimens8
    val columns = if (count == 4) 2 else 3
    val gridWidth = keySide * columns + gap * (columns - 1)
    val wideKey = (gridWidth - gap) / 2
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(gap), horizontalAlignment = Alignment.CenterHorizontally) {
            for (rowNumbers in (1..count).chunked(columns)) {
                Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                    for (n in rowNumbers) {
                        CalcudokuKey(face = colors.toyColor(n - 1), width = keySide, height = keySide, onClick = { onNumber(n) }) {
                            Text(
                                "$n",
                                style = AbTypography.fixed(minOf(28f * s, keySide.value * 0.6f), FontWeight.Black).copy(
                                    shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f)
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                CalcudokuKey(face = colors.danger.abReadable(3f), width = wideKey, height = keySide * 0.82f, onClick = onErase) {
                    Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = null,
                        tint = Color.White, modifier = Modifier.size(minOf(24f * s, keySide.value * 0.5f).dp))
                }
                CalcudokuKey(face = colors.accentOrange.abReadable(3f), width = wideKey, height = keySide * 0.82f, onClick = onReset) {
                    Icon(Icons.Rounded.Refresh, contentDescription = null,
                        tint = Color.White, modifier = Modifier.size(minOf(24f * s, keySide.value * 0.5f).dp))
                }
            }
        }
    }
}

// Draws all grid lines in one pass so corners/junctions join cleanly. (Still used by the How to play card.)
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
    val colors = LocalAbColors.current
    val hint = cageHint(state)
    val hasHint = hint != null
    val fill = if (hasHint) HINT_FILL else colors.bgSecondary.copy(alpha = if (colors.isDark) 0.92f else 0.9f)
    val rim = if (hasHint) HINT_RIM else colors.outline
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .abBounce(state.selectedIndex, 1.06f)
            .background(fill, CircleShape)
            .border(2.dp, rim, CircleShape)
            .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens6)
    ) {
        Text(
            "💡",
            fontSize = (15f * s).sp,
            modifier = Modifier.abBreathe(if (hasHint) 0.16f else 0.07f, if (hasHint) 900 else 1800)
        )
        Text(
            hint ?: "Tap a box for a hint",
            color = if (hasHint) Color(0xFF5D4037) else colors.textSecondary,
            style = AbTypography.fixed(15f * s, FontWeight.ExtraBold),
            maxLines = 1
        )
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

/** Star, time and best time as three small pills in the header, so the board and keys keep the whole height. */
@Composable
private fun CalcudokuStatChips(score: Int, elapsed: Int, bestTime: Int, k: Float, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8), verticalAlignment = Alignment.CenterVertically) {
        StatChip("⭐", "$score", colors.brandPrimary, k, bounce = score)
        StatChip("⏱️", timeString(elapsed), colors.textPrimary, k)
        StatChip("🏆", if (bestTime > 0) timeString(bestTime) else "—", colors.accentOrange.abReadable(3f), k)
    }
}

@Composable
private fun StatChip(emoji: String, value: String, color: Color, k: Float, bounce: Any? = null) {
    Row(
        horizontalArrangement = Arrangement.spacedBy((4f * k).dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.abGamePill().padding(horizontal = (10f * k).dp, vertical = (3f * k).dp)
    ) {
        Text(emoji, fontSize = (16f * k).sp)
        Text(
            value, color = color, maxLines = 1, softWrap = false,
            style = AbTypography.fixed(17f * k, FontWeight.ExtraBold, tabular = true),
            modifier = if (bounce != null) Modifier.abBounce(bounce, 1.25f) else Modifier
        )
    }
}

// ── result ───────────────────────────────────────────────────────────

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
        GameResultCard(
            title = str(R.string.balloon_great_job),
            emoji = "✖️",
            accent = ButtonType.BLUE,
            stars = starCount,
            score = str(R.string.balloon_final_score) + ": $score",
            stats = {
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
                    GameStatChip(emoji = "⏱️", value = timeString(elapsed), color = Color(0xFF5D4037))
                    GameStatChip(emoji = "⚡️", value = "$speedBonus", color = Color(0xFF0074D5))
                    GameStatChip(emoji = "🏆", value = if (bestTime > 0) timeString(bestTime) else "—", color = Color(0xFFC2570C))
                }
            },
            buttons = {
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    GameButton(
                        text = str(R.string.balloon_play_again), icon = Icons.Rounded.Refresh, type = ButtonType.POSITIVE,
                        onClick = { if (enabled) onPlayAgain() }
                    )
                    GameHomeButton(onClick = { if (enabled) onBack() })
                }
            }
        )
    }
}
