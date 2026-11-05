package com.jigar.me.ui.view.home.screens.math_game_zone

import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.jigar.me.R

/**
 * Programmatically drawn icons for the Math Game Zone tiles.
 * No image assets — every icon is built from shapes + text.
 */

// ── Per-game card colors ────────────────────────────────────────────────────

data class GameCardStyle(
    val light: Color, // gradient top
    val dark: Color,  // gradient bottom + shadow
    val tint: Color   // accent used inside white shapes
)

fun gameCardStyle(type: GameCategoryType): GameCardStyle = when (type) {
    GameCategoryType.NUMBER_SEQUENCE_PUZZLE ->
        GameCardStyle(Color(0xFF42A5F5), Color(0xFF1976D2), Color(0xFF1565C0))
    GameCategoryType.SUDOKU ->
        GameCardStyle(Color(0xFFAB47BC), Color(0xFF7B1FA2), Color(0xFF6A1B9A))
    GameCategoryType.MATH_PYRAMID ->
        GameCardStyle(Color(0xFFFFA726), Color(0xFFF57C00), Color(0xFFE65100))
    GameCategoryType.TARGET_NUMBER ->
        GameCardStyle(Color(0xFFEF5350), Color(0xFFD32F2F), Color(0xFFC62828))
    GameCategoryType.BALLOON_POP ->
        GameCardStyle(Color(0xFFEC407A), Color(0xFFC2185B), Color(0xFFAD1457))
    GameCategoryType.SPEED_COMPARE ->
        GameCardStyle(Color(0xFF26C6DA), Color(0xFF00838F), Color(0xFF006064))
    GameCategoryType.MISSING_OPERATOR ->
        GameCardStyle(Color(0xFF66BB6A), Color(0xFF2E7D32), Color(0xFF1B5E20))
    GameCategoryType.MAGIC_SQUARE ->
        GameCardStyle(Color(0xFF5C6BC0), Color(0xFF303F9F), Color(0xFF283593))
    // Cocoa brown — amber looked too close to Math Pyramid's orange
    GameCategoryType.CALCUDOKU ->
        GameCardStyle(Color(0xFF8D6E63), Color(0xFF5D4037), Color(0xFF4E342E))
    GameCategoryType.MERGE_2048 ->
        GameCardStyle(Color(0xFF9CCC65), Color(0xFF689F38), Color(0xFF33691E))
    GameCategoryType.EQUATION_MATCH ->
        GameCardStyle(Color(0xFF7E57C2), Color(0xFF512DA8), Color(0xFF4527A0))
    GameCategoryType.CROSS_MATH ->
        GameCardStyle(Color(0xFF26A69A), Color(0xFF00796B), Color(0xFF00695C))
    GameCategoryType.NUMBER_PATH ->
        GameCardStyle(Color(0xFFFF7043), Color(0xFFE64A19), Color(0xFFBF360C))
    GameCategoryType.TRUE_FALSE ->
        GameCardStyle(Color(0xFF29B6F6), Color(0xFF0277BD), Color(0xFF01579B))
    GameCategoryType.PLACE_VALUE ->
        GameCardStyle(Color(0xFFEC407A), Color(0xFFAD1457), Color(0xFF880E4F))
    GameCategoryType.CLOCK_MASTER ->
        GameCardStyle(Color(0xFF66BB6A), Color(0xFF388E3C), Color(0xFF1B5E20))
    GameCategoryType.MATH_BINGO ->
        GameCardStyle(Color(0xFFFFCA28), Color(0xFFF57F17), Color(0xFFE65100))
    GameCategoryType.KAKURO ->
        GameCardStyle(Color(0xFF78909C), Color(0xFF455A64), Color(0xFF263238))
    GameCategoryType.NUMBER_DETECTIVE ->
        GameCardStyle(Color(0xFF5C6BC0), Color(0xFF283593), Color(0xFF1A237E))
    GameCategoryType.NUMBER_SNAKE ->
        GameCardStyle(Color(0xFFFFD54F), Color(0xFFFFA000), Color(0xFFFF6F00))
}

// ── Icon dispatcher ─────────────────────────────────────────────────────────

@Composable
fun GameTileIcon(type: GameCategoryType, size: Dp, tint: Color) {
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        when (type) {
            GameCategoryType.NUMBER_SEQUENCE_PUZZLE -> SlidePuzzleIcon(size, tint)
            GameCategoryType.SUDOKU -> SudokuIcon(size)
            GameCategoryType.MATH_PYRAMID -> PyramidIcon(size, tint)
            GameCategoryType.TARGET_NUMBER -> TargetIcon(size, tint)
            GameCategoryType.BALLOON_POP -> BalloonIcon(size, tint)
            GameCategoryType.SPEED_COMPARE -> SpeedCompareIcon(size)
            GameCategoryType.MISSING_OPERATOR -> MissingSignIcon(size, tint)
            GameCategoryType.MAGIC_SQUARE -> MagicSquareIcon(size)
            GameCategoryType.CALCUDOKU -> CalcudokuIcon(size)
            GameCategoryType.MERGE_2048 -> MergeIcon(size, tint)
            GameCategoryType.EQUATION_MATCH -> EquationMatchIcon(size, tint)
            GameCategoryType.CROSS_MATH -> CrossMathIcon(size, tint)
            GameCategoryType.NUMBER_PATH -> NumberPathIcon(size, tint)
            GameCategoryType.TRUE_FALSE -> TrueFalseIcon(size, tint)
            GameCategoryType.PLACE_VALUE -> PlaceValueIcon(size, tint)
            GameCategoryType.CLOCK_MASTER -> ClockMasterIcon(size, tint)
            GameCategoryType.MATH_BINGO -> MathBingoIcon(size, tint)
            GameCategoryType.KAKURO -> KakuroIcon(size, tint)
            GameCategoryType.NUMBER_DETECTIVE -> NumberDetectiveIcon(size, tint)
            GameCategoryType.NUMBER_SNAKE -> NumberSnakeIcon(size, tint)
        }
    }
}

// ── Shared bits ─────────────────────────────────────────────────────────────

private val heavyFont = FontFamily(Font(R.font.font_extra_bold))

@Composable
private fun Dp.asSp(): TextUnit = with(LocalDensity.current) { this@asSp.toSp() }

@Composable
private fun IconNumber(text: String, fontSize: Dp, color: Color, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(
        text = text,
        color = color,
        fontSize = fontSize.asSp(),
        fontFamily = heavyFont,
        maxLines = 1,
        softWrap = false,
        // Android's default font padding pushes digits below center inside
        // small tiles — trim it and center the glyph in its line box.
        style = androidx.compose.ui.text.TextStyle(
            lineHeight = fontSize.asSp(),
            platformStyle = androidx.compose.ui.text.PlatformTextStyle(includeFontPadding = false),
            lineHeightStyle = androidx.compose.ui.text.style.LineHeightStyle(
                alignment = androidx.compose.ui.text.style.LineHeightStyle.Alignment.Center,
                trim = androidx.compose.ui.text.style.LineHeightStyle.Trim.Both
            )
        ),
        modifier = modifier
    )
}

@Composable
private fun MiniTile(text: String, side: Dp, tint: Color, filled: Boolean = true) {
    val shape = RoundedCornerShape(side * 0.24f)
    Box(
        modifier = Modifier
            .size(side)
            .then(if (filled) Modifier.background(Color.White, shape) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (!filled) {
            Canvas(modifier = Modifier.size(side)) {
                val stroke = (side.toPx() * 0.07f).coerceAtLeast(2f)
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.85f),
                    cornerRadius = CornerRadius(side.toPx() * 0.24f),
                    style = Stroke(
                        width = stroke,
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(side.toPx() * 0.18f, side.toPx() * 0.14f)
                        )
                    )
                )
            }
        }
        IconNumber(text, side * 0.5f, if (filled) tint else Color.White.copy(alpha = 0.9f))
    }
}

@Composable
private fun NumberGrid(size: Dp, divisions: Int, texts: List<String>, fontScale: Float = 0.5f) {
    val cell = size / divisions
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        Column {
            repeat(divisions) { r ->
                Row {
                    repeat(divisions) { c ->
                        Box(modifier = Modifier.size(cell), contentAlignment = Alignment.Center) {
                            IconNumber(texts[r * divisions + c], cell * fontScale, Color.White)
                        }
                    }
                }
            }
        }
        Canvas(modifier = Modifier.size(size)) {
            val lineWidth = (size.toPx() * 0.035f).coerceAtLeast(2f)
            val step = this.size.width / divisions
            for (i in 1 until divisions) {
                drawLine(
                    Color.White.copy(alpha = 0.75f),
                    Offset(step * i, 0f), Offset(step * i, this.size.height),
                    strokeWidth = lineWidth
                )
                drawLine(
                    Color.White.copy(alpha = 0.75f),
                    Offset(0f, step * i), Offset(this.size.width, step * i),
                    strokeWidth = lineWidth
                )
            }
            drawRoundRect(
                color = Color.White,
                cornerRadius = CornerRadius(this.size.width * 0.12f),
                style = Stroke(width = lineWidth * 1.4f)
            )
        }
    }
}

// ── 1. Number Sequence Puzzle — sliding tiles ───────────────────────────────

@Composable
private fun SlidePuzzleIcon(size: Dp, tint: Color) {
    val t = size * 0.44f
    val gap = size * 0.08f
    Column(verticalArrangement = Arrangement.spacedBy(gap)) {
        Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
            MiniTile("1", t, tint)
            MiniTile("2", t, tint)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
            MiniTile("3", t, tint)
            MiniTile("", t, tint, filled = false)
        }
    }
}

// ── 2. Sudoku — 4×4 grid, valid 1–4 solution, bold 2×2 box lines ────────────

@Composable
private fun SudokuIcon(size: Dp) {
    val cell = size / 4
    // Valid 4×4 sudoku: every row, column and 2×2 box has 1–4
    val nums = listOf(
        "1", "2", "3", "4",
        "3", "4", "1", "2",
        "2", "1", "4", "3",
        "4", "3", "2", "1"
    )
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        Column {
            repeat(4) { r ->
                Row {
                    repeat(4) { c ->
                        Box(modifier = Modifier.size(cell), contentAlignment = Alignment.Center) {
                            IconNumber(nums[r * 4 + c], cell * 0.60f, Color.White)
                        }
                    }
                }
            }
        }
        // Thin lines between cells, thick lines on the 2×2 box boundary + frame
        Canvas(modifier = Modifier.size(size)) {
            val lineWidth = (size.toPx() * 0.04f).coerceAtLeast(2f)
            val w = this.size.width
            val h = this.size.height
            listOf(0.25f, 0.75f).forEach { f ->
                drawLine(
                    Color.White.copy(alpha = 0.45f),
                    Offset(w * f, 0f), Offset(w * f, h),
                    strokeWidth = lineWidth * 0.5f
                )
                drawLine(
                    Color.White.copy(alpha = 0.45f),
                    Offset(0f, h * f), Offset(w, h * f),
                    strokeWidth = lineWidth * 0.5f
                )
            }
            drawLine(Color.White, Offset(w / 2, 0f), Offset(w / 2, h), strokeWidth = lineWidth)
            drawLine(Color.White, Offset(0f, h / 2), Offset(w, h / 2), strokeWidth = lineWidth)
            drawRoundRect(
                color = Color.White,
                cornerRadius = CornerRadius(w * 0.12f),
                style = Stroke(width = lineWidth)
            )
        }
    }
}

// ── 3. Math Pyramid — stacked blocks ────────────────────────────────────────

@Composable
private fun PyramidIcon(size: Dp, tint: Color) {
    val bw = size * 0.30f
    val bh = size * 0.26f
    val gap = size * 0.05f
    Column(
        verticalArrangement = Arrangement.spacedBy(gap),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        listOf(listOf("9"), listOf("4", "5"), listOf("1", "3", "2")).forEach { rowTexts ->
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                rowTexts.forEach { text ->
                    Box(
                        modifier = Modifier
                            .size(width = bw, height = bh)
                            .background(Color.White, RoundedCornerShape(bh * 0.28f)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconNumber(text, bh * 0.55f, tint)
                    }
                }
            }
        }
    }
}

// ── 4. Target the Number — bullseye ─────────────────────────────────────────

@Composable
private fun TargetIcon(size: Dp, tint: Color) {
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        // Bullseye with the big target number in the center
        Canvas(modifier = Modifier.size(size)) {
            val s = this.size.width
            drawCircle(
                Color.White,
                radius = s * 0.46f,
                style = Stroke(width = s * 0.06f)
            )
            drawCircle(
                Color.White.copy(alpha = 0.6f),
                radius = s * 0.33f,
                style = Stroke(width = s * 0.055f)
            )
            drawCircle(Color.White, radius = s * 0.21f)
        }
        IconNumber("12", size * 0.22f, tint)

        // Numbers + sign that make the target, scattered over the rings
        RingToken("3", size, tint, x = -0.32f, y = -0.27f, rotation = -14f)
        RingToken("×", size, tint, x = 0.34f, y = -0.20f, rotation = 12f)
        RingToken("4", size, tint, x = 0.27f, y = 0.32f, rotation = 10f)
    }
}

@Composable
private fun RingToken(text: String, size: Dp, tint: Color, x: Float, y: Float, rotation: Float) {
    Box(
        modifier = Modifier
            .offset(x = size * x, y = size * y)
            .rotate(rotation)
    ) {
        IconNumber(text, size * 0.18f, Color.White)
    }
}

// ── Number Detective — magnifying glass over a mystery number ──────────────

@Composable
private fun NumberDetectiveIcon(size: Dp, tint: Color) {
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val s = this.size.width
            // Lens
            drawCircle(
                Color.White,
                radius = s * 0.28f,
                center = androidx.compose.ui.geometry.Offset(s * 0.42f, s * 0.42f),
                style = Stroke(width = s * 0.07f)
            )
            // Handle
            drawLine(
                Color.White,
                start = androidx.compose.ui.geometry.Offset(s * 0.62f, s * 0.62f),
                end = androidx.compose.ui.geometry.Offset(s * 0.82f, s * 0.82f),
                strokeWidth = s * 0.09f,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
        Box(
            modifier = Modifier.offset(x = -size * 0.08f, y = -size * 0.08f),
            contentAlignment = Alignment.Center
        ) {
            IconNumber("?", size * 0.22f, tint)
        }
    }
}

// ── Number Snake — a connected 1→2→3 trail ──────────────────────────────────

@Composable
private fun NumberSnakeIcon(size: Dp, tint: Color) {
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        val tile = size * 0.32f
        Box(modifier = Modifier.offset(x = -size * 0.24f, y = -size * 0.22f)) { MiniTile("1", tile, tint) }
        Box(modifier = Modifier.offset(x = size * 0.02f, y = size * 0.02f)) { MiniTile("2", tile, tint) }
        Box(modifier = Modifier.offset(x = size * 0.28f, y = size * 0.26f)) { MiniTile("3", tile, tint) }
    }
}

// ── 5. Balloon Pop — balloon with a 10 ──────────────────────────────────────

@Composable
private fun BalloonIcon(size: Dp, tint: Color) {
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        // Exact same balloon as the game: oval 94×106 + 4-segment rope 20×50,
        // scaled to the icon (no knot, no highlight — the game has neither)
        Canvas(modifier = Modifier.size(size)) {
            val s = this.size.width
            // Balloon body
            drawOval(
                Color.White,
                topLeft = Offset(s * 0.20f, 0f),
                size = Size(s * 0.60f, s * 0.68f)
            )
            // Wavy rope — same 4-segment curve the Balloon Pop game uses
            val rope = Path().apply {
                val amplitude = s * 0.065f
                val seg = s * 0.08f
                val topY = s * 0.68f
                moveTo(s * 0.5f, topY)
                for (i in 0 until 4) {
                    val y1 = topY + i * seg
                    val y2 = y1 + seg
                    val dir = if (i % 2 == 0) 1f else -1f
                    quadraticBezierTo(s * 0.5f + amplitude * dir, (y1 + y2) / 2f, s * 0.5f, y2)
                }
            }
            drawPath(
                rope, Color.White.copy(alpha = 0.95f),
                style = Stroke(
                    width = (s * 0.03f).coerceAtLeast(2f),
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            )
            // Pop dots sprinkled around the balloon
            val dots = listOf(
                Triple(-0.44f, -0.32f, 0.035f),
                Triple(-0.36f, -0.02f, 0.025f),
                Triple(-0.44f, 0.24f, 0.028f),
                Triple(0.44f, -0.26f, 0.033f),
                Triple(0.40f, 0.04f, 0.023f),
                Triple(0.44f, 0.30f, 0.030f),
                Triple(0.00f, -0.46f, 0.023f)
            )
            dots.forEach { (dx, dy, r) ->
                drawCircle(
                    Color.White.copy(alpha = 0.85f),
                    radius = s * r,
                    center = Offset(s * 0.5f + s * dx, s * 0.5f + s * dy)
                )
            }
        }
        IconNumber("10", size * 0.24f, tint, modifier = Modifier.offset(y = -size * 0.16f))
    }
}

// ── 6. Speed Compare — 9 > 5 with a bolt ────────────────────────────────────

@Composable
private fun SpeedCompareIcon(size: Dp) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(size * 0.02f)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(size * 0.07f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconNumber("9", size * 0.30f, Color.White)
            IconNumber(">", size * 0.36f, Color.White.copy(alpha = 0.95f))
            IconNumber("5", size * 0.30f, Color.White.copy(alpha = 0.75f))
        }
        Canvas(modifier = Modifier.size(width = size * 0.22f, height = size * 0.32f)) {
            val w = this.size.width
            val h = this.size.height
            val bolt = Path().apply {
                moveTo(w * 0.62f, 0f)
                lineTo(w * 0.12f, h * 0.58f)
                lineTo(w * 0.45f, h * 0.58f)
                lineTo(w * 0.38f, h)
                lineTo(w * 0.88f, h * 0.40f)
                lineTo(w * 0.55f, h * 0.40f)
                close()
            }
            drawPath(bolt, Color.White)
        }
    }
}

// ── 7. Missing Sign — 3 ? 4 ─────────────────────────────────────────────────

@Composable
private fun MissingSignIcon(size: Dp, tint: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(size * 0.08f)
    ) {
        // Full equation with the missing sign — answer included: 3 ? 4 = 7
        Row(
            horizontalArrangement = Arrangement.spacedBy(size * 0.04f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconNumber("3", size * 0.18f, Color.White)
            Box(
                modifier = Modifier
                    .size(size * 0.26f)
                    .background(Color.White, RoundedCornerShape(size * 0.06f)),
                contentAlignment = Alignment.Center
            ) {
                IconNumber("?", size * 0.16f, tint)
            }
            IconNumber("4", size * 0.18f, Color.White)
            IconNumber("=", size * 0.18f, Color.White.copy(alpha = 0.9f))
            IconNumber("7", size * 0.18f, Color.White)
        }

        // Answer options below — "+" shown as the picked one
        Row(horizontalArrangement = Arrangement.spacedBy(size * 0.06f)) {
            SignOptionChip("+", size, tint, filled = true)
            SignOptionChip("−", size, tint, filled = false)
            SignOptionChip("×", size, tint, filled = false)
        }
    }
}

@Composable
private fun SignOptionChip(symbol: String, size: Dp, tint: Color, filled: Boolean) {
    val shape = RoundedCornerShape(size * 0.055f)
    Box(
        modifier = Modifier
            .size(size * 0.22f)
            .then(
                if (filled) Modifier.background(Color.White, shape)
                else Modifier.border(1.5.dp, Color.White.copy(alpha = 0.8f), shape)
            ),
        contentAlignment = Alignment.Center
    ) {
        IconNumber(symbol, size * 0.13f, if (filled) tint else Color.White.copy(alpha = 0.9f))
    }
}

// ── 8. Magic Square — grid with sparkle ─────────────────────────────────────

@Composable
private fun MagicSquareIcon(size: Dp) {
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        NumberGrid(size, 3, listOf("2", "7", "6", "9", "", "1", "4", "3", "8"), fontScale = 0.48f)
        IconNumber("✨", size * 0.22f, Color.White)
    }
}

// ── 9. Calcudoku — real cage look: corner clues + thick cage borders ────────

@Composable
private fun CalcudokuIcon(size: Dp) {
    val cell = size / 2
    // 2×2 Latin square: 1 2 / 2 1. Top row is one "3+" cage (no divider inside),
    // bottom cells are single cages with their own clues.
    val numbers = listOf("1", "2", "2", "1")
    val clues = listOf("3+", "", "2", "1")
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        Column {
            repeat(2) { r ->
                Row {
                    repeat(2) { c ->
                        val i = r * 2 + c
                        Box(modifier = Modifier.size(cell)) {
                            IconNumber(
                                numbers[i], cell * 0.42f, Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )
                            if (clues[i].isNotEmpty()) {
                                IconNumber(
                                    clues[i], cell * 0.22f, Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .offset(x = cell * 0.10f, y = cell * 0.04f)
                                )
                            }
                        }
                    }
                }
            }
        }
        // Cage borders: outer frame + full middle horizontal line (top cage vs
        // bottom) + vertical divider only in the bottom half (two single cages).
        // The top row has no inner divider — that's what reads as a "cage".
        Canvas(modifier = Modifier.size(size)) {
            val lineWidth = (size.toPx() * 0.05f).coerceAtLeast(2.5f)
            val w = this.size.width
            val h = this.size.height
            drawLine(Color.White, Offset(0f, h / 2), Offset(w, h / 2), strokeWidth = lineWidth)
            drawLine(Color.White, Offset(w / 2, h / 2), Offset(w / 2, h), strokeWidth = lineWidth)
            // Light cell divider inside the "3+" cage (between the 1 and the 2)
            drawLine(
                Color.White.copy(alpha = 0.4f),
                Offset(w / 2, 0f), Offset(w / 2, h / 2),
                strokeWidth = lineWidth * 0.5f
            )
            drawRoundRect(
                color = Color.White,
                cornerRadius = CornerRadius(w * 0.12f),
                style = Stroke(width = lineWidth)
            )
        }
    }
}

// ── 10. Number Merge — 2 + 2 → 4 tiles ──────────────────────────────────────

@Composable
private fun MergeIcon(size: Dp, tint: Color) {
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier
            .offset(x = -size * 0.24f, y = -size * 0.22f)
            .alpha(0.55f)) {
            MiniTile("2", size * 0.46f, tint)
        }
        Box(modifier = Modifier
            .offset(x = size * 0.24f, y = -size * 0.22f)
            .alpha(0.75f)) {
            MiniTile("2", size * 0.46f, tint)
        }
        Box(modifier = Modifier.offset(y = size * 0.20f)) {
            MiniTile("4", size * 0.56f, tint)
        }
    }
}

// ── 11. Equation Match — flipped pair of cards ──────────────────────────────

// Animated: a mini kakuro corner — clue cell with diagonal, digits fading in
// one after another like the puzzle being solved.
@Composable
private fun KakuroIcon(size: Dp, tint: Color) {
    val t by androidx.compose.animation.core.rememberInfiniteTransition(label = "kkIcon").animateFloat(
        initialValue = 0f, targetValue = 3f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            androidx.compose.animation.core.tween(2700, easing = androidx.compose.animation.core.LinearEasing)
        ), label = "kkIcon"
    )
    // 3 tiles + 2 gaps must fit inside the 0.90 padded width — 0.3 overflowed
    // and squeezed the last tile.
    val tile = size * 0.28f
    // Content is ~2/3 of the box — center it or the icon hugs the top-left.
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.size(size).padding(size * 0.05f)
    ) {
        Row {
            // Clue cell with the classic diagonal split.
            Box(modifier = Modifier.size(tile)) {
                Canvas(modifier = Modifier.size(tile)) {
                    drawRoundRect(Color.White.copy(alpha = 0.35f), cornerRadius = CornerRadius(tile.toPx() * 0.15f))
                    drawLine(Color.White, Offset(0f, 0f), Offset(this.size.width, this.size.height), 1.5.dp.toPx())
                }
                IconNumber("7", tile * 0.34f, Color.White, Modifier.align(Alignment.TopEnd).padding(end = tile * 0.08f))
                IconNumber("6", tile * 0.34f, Color.White, Modifier.align(Alignment.BottomStart).padding(start = tile * 0.08f))
            }
            listOf("3", "4").forEachIndexed { i, d ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(start = size * 0.02f).size(tile)
                        .alpha(if (t > i) 1f else 0.25f)
                        .background(Color.White, RoundedCornerShape(tile * 0.15f))
                ) { IconNumber(d, tile * 0.5f, tint) }
            }
        }
        Row(modifier = Modifier.padding(top = size * 0.02f)) {
            Spacer(Modifier.size(tile))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(start = size * 0.02f).size(tile)
                    .alpha(if (t > 2f) 1f else 0.25f)
                    .background(Color.White, RoundedCornerShape(tile * 0.15f))
            ) { IconNumber("2", tile * 0.5f, tint) }
        }
    }
}

// Animated: a mini bingo card whose diagonal stars pulse one after another.
@Composable
private fun MathBingoIcon(size: Dp, tint: Color) {
    val t by androidx.compose.animation.core.rememberInfiniteTransition(label = "mbIcon").animateFloat(
        initialValue = 0f, targetValue = (2f * Math.PI).toFloat(),
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            androidx.compose.animation.core.tween(2000, easing = androidx.compose.animation.core.LinearEasing)
        ), label = "mbIcon"
    )
    val tile = size * 0.24f
    Column(
        verticalArrangement = Arrangement.spacedBy(size * 0.03f, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.size(size).padding(size * 0.1f)
    ) {
        for (r in 0 until 3) {
            Row(horizontalArrangement = Arrangement.spacedBy(size * 0.03f)) {
                for (c in 0 until 3) {
                    val onDiagonal = r == c
                    val pulse = if (onDiagonal) 1f + kotlin.math.sin(t + r * 1.6f) * 0.12f else 1f
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .graphicsLayer { scaleX = pulse; scaleY = pulse }
                            .size(tile)
                            .background(
                                if (onDiagonal) Color.White else Color.White.copy(alpha = 0.6f),
                                RoundedCornerShape(tile * 0.2f)
                            )
                    ) {
                        if (onDiagonal) IconNumber("★", tile * 0.55f, tint)
                    }
                }
            }
        }
    }
}

// Animated: a mini clock whose hands sweep around continuously.
@Composable
private fun ClockMasterIcon(size: Dp, tint: Color) {
    val t by androidx.compose.animation.core.rememberInfiniteTransition(label = "cmIcon").animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            androidx.compose.animation.core.tween(6000, easing = androidx.compose.animation.core.LinearEasing)
        ), label = "cmIcon"
    )
    Canvas(modifier = Modifier.size(size)) {
        val r = this.size.width * 0.36f
        val c = Offset(this.size.width / 2f, this.size.height / 2f)
        drawCircle(Color.White, radius = r, center = c)
        for (i in 0 until 12) {
            val a = Math.toRadians(i * 30.0).toFloat()
            drawCircle(
                tint.copy(alpha = 0.5f), radius = this.size.width * 0.015f,
                center = Offset(
                    c.x + kotlin.math.cos(a) * r * 0.82f,
                    c.y + kotlin.math.sin(a) * r * 0.82f
                )
            )
        }
        // Minute hand sweeps fast, hour hand slow.
        val ma = Math.toRadians(t * 2.0 - 90).toFloat()
        drawLine(tint, c, Offset(c.x + kotlin.math.cos(ma) * r * 0.68f, c.y + kotlin.math.sin(ma) * r * 0.68f),
            strokeWidth = this.size.width * 0.035f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        val ha = Math.toRadians(t / 6.0 - 90).toFloat()
        drawLine(tint, c, Offset(c.x + kotlin.math.cos(ha) * r * 0.45f, c.y + kotlin.math.sin(ha) * r * 0.45f),
            strokeWidth = this.size.width * 0.05f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        drawCircle(tint, radius = this.size.width * 0.03f, center = c)
    }
}

// Animated: three stacked place-value blocks (H, T, O) bob one after another,
// like blocks being stacked into a number.
@Composable
private fun PlaceValueIcon(size: Dp, tint: Color) {
    val t by androidx.compose.animation.core.rememberInfiniteTransition(label = "pvIcon").animateFloat(
        initialValue = 0f, targetValue = (2f * Math.PI).toFloat(),
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            androidx.compose.animation.core.tween(2400, easing = androidx.compose.animation.core.LinearEasing)
        ), label = "pvIcon"
    )
    // Bars are 3x0.24 + 2x0.05 spacing = 0.82 wide — 0.08 padding fits them
    // centered (0.12 made the row overflow and sit off-center).
    Row(
        horizontalArrangement = Arrangement.spacedBy(size * 0.05f, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.size(size).padding(size * 0.08f)
    ) {
        listOf("4" to 0.9f, "7" to 0.65f, "2" to 0.4f).forEachIndexed { i, (digit, h) ->
            val bob = kotlin.math.sin(t + i * 1.2f) * size.value * 0.035f
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .offset(y = bob.dp)
                    .size(width = size * 0.24f, height = size * h * 0.8f)
                    .background(Color.White.copy(alpha = if (i == 0) 1f else 0.85f), RoundedCornerShape(size * 0.06f))
            ) {
                IconNumber(digit, size * 0.16f, tint)
            }
        }
    }
}

// Animated: two cards see-saw like a judge's scale — ✓ up, ✗ down, and back.
@Composable
private fun TrueFalseIcon(size: Dp, tint: Color) {
    val sway by androidx.compose.animation.core.rememberInfiniteTransition(label = "tfIcon").animateFloat(
        initialValue = -8f, targetValue = 8f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            androidx.compose.animation.core.tween(1400), androidx.compose.animation.core.RepeatMode.Reverse
        ), label = "tfIcon"
    )
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .offset(x = -size * 0.18f, y = (sway * (size.value / 100f)).dp)
                .rotate(-10f)
                .size(width = size * 0.42f, height = size * 0.52f)
                .background(Color.White, RoundedCornerShape(size * 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            IconNumber("✓", size * 0.26f, Color(0xFF2E7D32))
        }
        Box(
            modifier = Modifier
                .offset(x = size * 0.18f, y = (-sway * (size.value / 100f)).dp)
                .rotate(10f)
                .size(width = size * 0.42f, height = size * 0.52f)
                .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(size * 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            IconNumber("✗", size * 0.26f, Color(0xFFC62828))
        }
    }
}

// Animated: a little dot travels along a mini S-shaped path of tiles,
// echoing the game's hop-along-the-maze motion.
@Composable
private fun NumberPathIcon(size: Dp, tint: Color) {
    val stops = listOf(
        Offset(0.15f, 0.2f), Offset(0.5f, 0.2f), Offset(0.85f, 0.2f),
        Offset(0.85f, 0.55f), Offset(0.5f, 0.55f), Offset(0.15f, 0.55f),
        Offset(0.15f, 0.9f), Offset(0.5f, 0.9f), Offset(0.85f, 0.9f)
    )
    val progress by androidx.compose.animation.core.rememberInfiniteTransition(label = "npIcon").animateFloat(
        initialValue = 0f, targetValue = (stops.size - 1).toFloat(),
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            androidx.compose.animation.core.tween(4200, easing = androidx.compose.animation.core.LinearEasing)
        ), label = "npIcon"
    )
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        fun at(o: Offset) = Offset(o.x * w, o.y * w)
        // Path line through the tile stops.
        val path = Path()
        stops.forEachIndexed { i, o -> if (i == 0) path.moveTo(at(o).x, at(o).y) else path.lineTo(at(o).x, at(o).y) }
        drawPath(path, Color.White.copy(alpha = 0.5f), style = Stroke(width = w * 0.05f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(w * 0.06f, w * 0.05f))))
        // Tile dots (start, mid, goal).
        stops.forEachIndexed { i, o ->
            val r = if (i == 0 || i == stops.size - 1) w * 0.09f else w * 0.055f
            drawCircle(if (i == stops.size - 1) Color(0xFFFFD54F) else Color.White, radius = r, center = at(o))
        }
        // The traveling dot, interpolated between stops.
        val seg = progress.toInt().coerceIn(0, stops.size - 2)
        val f = progress - seg
        val a = at(stops[seg]); val b = at(stops[seg + 1])
        val dot = Offset(a.x + (b.x - a.x) * f, a.y + (b.y - a.y) * f)
        drawCircle(tint, radius = w * 0.075f, center = dot)
        drawCircle(Color.White, radius = w * 0.075f, center = dot, style = Stroke(width = w * 0.02f))
    }
}

@Composable
private fun CrossMathIcon(size: Dp, tint: Color) {
    // Real cross-math example: 3 + 2 = 5 across, 8 − 2 = 6 down, sharing the
    // "2" in the middle — a tiny version of an actual puzzle. The two answer
    // cells are pale, like the blanks the kid fills in.
    val h = listOf("3", "+", "2", "=", "5")   // row 2
    val v = listOf("8", "−", "2", "=", "6")   // col 2
    // 5 columns must fit inside the box — 0.205 x 5 overflowed and squeezed
    // the last "= 5" cell narrower than its siblings.
    val tile = size * 0.19f
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        Column {
            for (r in 0 until 5) {
                Row {
                    for (c in 0 until 5) {
                        val text = when {
                            r == 2 -> h[c]
                            c == 2 -> v[r]
                            else -> null
                        }
                        if (text == null) {
                            Spacer(Modifier.size(tile))
                        } else {
                            val isBlankCell = (r == 2 && c == 4) || (r == 4 && c == 2)
                            Box(
                                modifier = Modifier
                                    .size(tile)
                                    .background(if (isBlankCell) Color.White.copy(alpha = 0.62f) else Color.White)
                                    .border(Dp.Hairline, tint.copy(alpha = 0.35f)),
                                contentAlignment = Alignment.Center
                            ) {
                                // includeFontPadding=false + tight line height so the
                                // glyph sits dead-center of the tiny tile instead of
                                // being pushed down and clipped by font padding.
                                androidx.compose.material3.Text(
                                    text = text,
                                    color = tint,
                                    fontSize = (tile * 0.6f).asSp(),
                                    fontFamily = heavyFont,
                                    maxLines = 1,
                                    softWrap = false,
                                    textAlign = TextAlign.Center,
                                    style = androidx.compose.ui.text.TextStyle(
                                        platformStyle = androidx.compose.ui.text.PlatformTextStyle(includeFontPadding = false),
                                        lineHeight = (tile * 0.6f).asSp()
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EquationMatchIcon(size: Dp, tint: Color) {
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        // Back card (face down)
        Box(
            modifier = Modifier
                .offset(x = -size * 0.18f, y = -size * 0.03f)
                .rotate(-14f)
                .size(width = size * 0.48f, height = size * 0.62f)
                .background(Color.White.copy(alpha = 0.55f), RoundedCornerShape(size * 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            IconNumber("?", size * 0.24f, Color.White)
        }
        // Front card (face up)
        Box(
            modifier = Modifier
                .offset(x = size * 0.18f, y = size * 0.05f)
                .rotate(10f)
                .size(width = size * 0.48f, height = size * 0.62f)
                .background(Color.White, RoundedCornerShape(size * 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            IconNumber("3+2", size * 0.16f, tint)
        }
    }
}
