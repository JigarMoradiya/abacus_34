package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abChunkyFace
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.generator.SudokuBoxRules
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SudokuSize
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

// A drawn, animated Sudoku size icon for the Sudoku home (Android twin of iOS Main/Games/GameHomeIcons.swift `SudokuSizeIcon`).
// A toy puzzle board with a REAL Sudoku grid for the size (4x4 = 2x2 boxes, 6x6 = boxes of 2 rows x 3 columns, 9x9 = 3x3
// boxes): thin cell lines, thick box lines, alternate boxes tinted, sample digits that pop in a wave, a pulsing "?" cell, a
// size sticker and a wiggling pencil. The chosen size floats higher, glows and sparkles. View-level only, no game logic.

private fun sudokuSizeAccent(size: SudokuSize): Color = when (size) {
    SudokuSize.FOUR -> Color(0xFFF59E0B)
    SudokuSize.SIX -> Color(0xFF14B8A6)
    SudokuSize.NINE -> Color(0xFF8B5CF6)
}

/** Sample board digit for the icon (decoration only, not a real puzzle): every row, column and box holds each digit once. */
private fun sudokuSampleDigit(n: Int, r: Int, c: Int): Int = when (n) {
    4 -> (2 * (r % 2) + r / 2 + c) % 4 + 1
    6 -> (3 * (r % 2) + r / 2 + c) % 6 + 1
    else -> (3 * (r % 3) + r / 3 + c) % 9 + 1
}

private fun sudokuSampleIsBlank(n: Int, r: Int, c: Int): Boolean =
    if (n == 9) (r * 5 + c * 2) % 3 == 0 else (r * 7 + c * 3 + n) % 4 == 0

/** The animation clock in seconds: one frame loop per icon, read only in draw / graphicsLayer lambdas so nothing recomposes. */
@Composable
private fun rememberSudokuClock(): State<Float> {
    val time = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val start: Long = withFrameNanos { it }
        while (true) {
            withFrameNanos { now -> time.floatValue = (now - start) / 1_000_000_000f }
        }
    }
    return time
}

@Composable
fun SudokuSizeIcon(size: SudokuSize, selected: Boolean, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    val n: Int = size.grid
    val accent: Color = sudokuSizeAccent(size)
    val clock: State<Float> = rememberSudokuClock()
    val sel: Float by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(300),
        label = "sudokuSizeSel",
    )
    val density = LocalDensity.current

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val g: Dp = minOf(maxWidth, maxHeight)
        val side: Dp = g * 0.7f
        val badgeFace: Color = accent.abReadable(3f)
        val badgeSp = with(density) { (g * 0.12f).toSp() }
        val pencilSp = with(density) { (g * 0.2f).toSp() }

        Box(
            modifier = Modifier
                .requiredSize(g)
                .graphicsLayer {
                    // the whole toy bobs and tilts; the chosen size floats higher
                    val phase: Float = clock.value * 1.5f + n * 0.9f
                    translationY = sin(phase) * g.toPx() * (0.018f + 0.012f * sel)
                    rotationZ = sin(phase * 0.7f) * (1.2f + 1.3f * sel)
                },
            contentAlignment = Alignment.Center,
        ) {
            // glow behind the chosen size
            if (selected || sel > 0.01f) {
                Box(
                    modifier = Modifier
                        .requiredSize(g * 1.2f)
                        .graphicsLayer {
                            alpha = sel.coerceIn(0f, 1f)
                            val pulse: Float = 1f + 0.06f * sin(clock.value * 3f)
                            scaleX = pulse
                            scaleY = pulse
                        }
                        .drawBehind {
                            val radius: Float = this.size.minDimension / 2f
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(colors.accentYellow.copy(alpha = 0.7f), colors.accentYellow.copy(alpha = 0f)),
                                    center = center,
                                    radius = radius,
                                ),
                                radius = radius,
                                center = center,
                            )
                        },
                )
            }

            // the toy board with the real Sudoku grid
            SudokuSizeBoard(n = n, sudokuSize = size, accent = accent, side = side, clock = clock)

            // size sticker straddling the top edge of the board
            Text(
                text = size.displayName,
                style = AbTypography.fixed(1f, FontWeight.Black).copy(
                    fontSize = badgeSp,
                    lineHeight = badgeSp * 1.12f,
                    shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f),
                ),
                color = Color.White,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .offset(y = -(side * 0.585f))
                    .graphicsLayer { rotationZ = -4f }
                    .abChunkyFace(badgeFace, badgeFace.abDarkened(0.3f), null, AbShapes.pill, maxOf(3.dp, g * 0.02f))
                    .padding(horizontal = g * 0.06f, vertical = g * 0.03f),
            )

            // a wiggling pencil at the bottom-right corner
            Text(
                text = "✏️",
                fontSize = pencilSp,
                modifier = Modifier
                    .offset(x = side * 0.5f, y = side * 0.4f)
                    .graphicsLayer {
                        val w: Float = sin(clock.value * 5f)
                        rotationZ = -24f + 14f * w
                        translationX = w * g.toPx() * 0.012f
                        translationY = cos(clock.value * 5f) * g.toPx() * 0.012f
                    },
            )

            // sparkles around the chosen size
            if (selected || sel > 0.01f) {
                for (i in 0 until 3) {
                    val a: Float = i * 2.1f + 0.4f
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = colors.accentYellow,
                        modifier = Modifier
                            .size(g * 0.12f)
                            .graphicsLayer {
                                val twinkle: Float = (0.55f + 0.45f * abs(sin(clock.value * 3.2f + a))) * sel.coerceIn(0f, 1f)
                                scaleX = twinkle
                                scaleY = twinkle
                                translationX = g.toPx() * 0.47f * cos(a * 1.7f)
                                translationY = g.toPx() * 0.45f * sin(a * 1.9f)
                            },
                    )
                }
            }
        }
    }
}

/**
 * The board: rim + 3D edge + white base, then an n x n grid of cells (alternate boxes tinted), sample digits that pop in a
 * diagonal wave, one pulsing "?" cell, thin lines between cells and thick lines around each box. One draw pass, with the
 * digits measured once per size so nothing is laid out while it animates.
 */
@Composable
private fun SudokuSizeBoard(n: Int, sudokuSize: SudokuSize, accent: Color, side: Dp, clock: State<Float>) {
    val measurer = rememberTextMeasurer(cacheSize = 24)
    val boxRC: Pair<Int, Int> = SudokuBoxRules.boxSize(sudokuSize)
    val boxRows: Int = boxRC.first
    val boxCols: Int = boxRC.second
    var qr = 0
    var qc = 0
    var found = false
    for (r in 0 until n) {
        for (c in 0 until n) {
            if (!found && sudokuSampleIsBlank(n, r, c)) {
                qr = r
                qc = c
                found = true
            }
        }
    }
    val questionRow: Int = qr
    val questionCol: Int = qc

    Box(
        modifier = Modifier
            .requiredSize(side)
            .drawWithCache {
                val s: Float = this.size.width
                val inset: Float = s * 0.06f
                val gs: Float = s - inset * 2f
                val cell: Float = gs / n
                val corner: Float = s * 0.14f
                val rim: Float = maxOf(3.dp.toPx(), s * 0.035f)
                val thin: Float = maxOf(0.8f, gs * 0.007f)
                val thick: Float = maxOf(2f, gs * 0.025f)

                val digitSp = (cell * 0.66f / density).dp.toSp()
                val digitStyle = AbTypography.fixed(1f, FontWeight.Black).copy(fontSize = digitSp, lineHeight = digitSp * 1.12f)
                val questionSp = (cell * 0.7f / density).dp.toSp()
                val questionStyle = AbTypography.fixed(1f, FontWeight.Black).copy(fontSize = questionSp, lineHeight = questionSp * 1.12f)
                val digitLayouts = (1..n).map { d -> measurer.measure(d.toString(), digitStyle) }
                val questionLayout = measurer.measure("?", questionStyle)

                val digitInk: Color = accent.abDarkened(0.35f)
                val questionInk: Color = accent.abDarkened(0.3f)
                val edgeColor: Color = accent.abDarkened(0.3f)
                val gridClip: Path = Path().apply {
                    addRoundRect(RoundRect(Rect(Offset.Zero, Size(gs, gs)), CornerRadius(s * 0.03f, s * 0.03f)))
                }

                onDrawBehind {
                    val t: Float = clock.value

                    // 3D edge, white base, rim
                    val r = CornerRadius(corner, corner)
                    drawRoundRect(edgeColor, topLeft = Offset(0f, s * 0.045f), size = Size(s, s), cornerRadius = r)
                    drawRoundRect(Color.White.copy(alpha = 0.96f), topLeft = Offset.Zero, size = Size(s, s), cornerRadius = r)
                    drawRoundRect(
                        color = accent,
                        topLeft = Offset(rim / 2f, rim / 2f),
                        size = Size(s - rim, s - rim),
                        cornerRadius = CornerRadius(corner - rim / 2f, corner - rim / 2f),
                        style = Stroke(width = rim),
                    )

                    translate(left = inset, top = inset) {
                        clipPath(gridClip) {
                            // cells: tint on every other box, the pulsing question cell, the popping digits
                            for (row in 0 until n) {
                                for (col in 0 until n) {
                                    val x: Float = col * cell
                                    val y: Float = row * cell
                                    val tinted: Boolean = ((row / boxRows) + (col / boxCols)) % 2 == 1
                                    drawRect(
                                        color = if (tinted) accent.copy(alpha = 0.17f) else Color.White,
                                        topLeft = Offset(x, y),
                                        size = Size(cell, cell),
                                    )
                                    val cx: Float = x + cell / 2f
                                    val cy: Float = y + cell / 2f
                                    if (row == questionRow && col == questionCol) {
                                        val pulse: Float = sin(t * 4f)
                                        drawRect(
                                            color = accent.copy(alpha = (0.3f + 0.18f * pulse).coerceIn(0f, 1f)),
                                            topLeft = Offset(x, y),
                                            size = Size(cell, cell),
                                        )
                                        scale(scale = 1f + 0.18f * pulse, pivot = Offset(cx, cy)) {
                                            drawText(
                                                textLayoutResult = questionLayout,
                                                color = questionInk,
                                                topLeft = Offset(
                                                    cx - questionLayout.size.width / 2f,
                                                    cy - questionLayout.size.height / 2f,
                                                ),
                                            )
                                        }
                                    } else if (!sudokuSampleIsBlank(n, row, col)) {
                                        val layout = digitLayouts[sudokuSampleDigit(n, row, col) - 1]
                                        val wave: Float = sin(t * 2.2f - (row + col) * 0.55f)
                                        val pop: Float = maxOf(0f, wave).pow(8)
                                        scale(scale = 1f + 0.3f * pop, pivot = Offset(cx, cy)) {
                                            drawText(
                                                textLayoutResult = layout,
                                                color = digitInk,
                                                topLeft = Offset(cx - layout.size.width / 2f, cy - layout.size.height / 2f),
                                            )
                                        }
                                    }
                                }
                            }

                            // thin lines between cells
                            val thinColor: Color = accent.copy(alpha = 0.4f)
                            for (i in 1 until n) {
                                val p: Float = i * cell
                                drawLine(thinColor, Offset(p, 0f), Offset(p, gs), strokeWidth = thin)
                                drawLine(thinColor, Offset(0f, p), Offset(gs, p), strokeWidth = thin)
                            }
                            // thick lines around each box
                            for (i in 1 until n) {
                                val p: Float = i * cell
                                if (i % boxCols == 0) drawLine(accent, Offset(p, 0f), Offset(p, gs), strokeWidth = thick)
                                if (i % boxRows == 0) drawLine(accent, Offset(0f, p), Offset(gs, p), strokeWidth = thick)
                            }
                        }
                    }
                }
            },
    )
}
