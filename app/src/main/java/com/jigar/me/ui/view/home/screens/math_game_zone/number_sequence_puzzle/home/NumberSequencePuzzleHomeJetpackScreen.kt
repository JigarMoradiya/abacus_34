package com.jigar.me.ui.view.home.screens.math_game_zone.number_sequence_puzzle.home

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeLayout

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import androidx.compose.ui.graphics.drawscope.Stroke
import com.jigar.me.ui.jetpack.utils.ui.extensions.appScale
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abPopIn
import androidx.navigation.NavController
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorYellowOrange
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayNumberSequenceView
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.SudokuHomeIconsBox
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

@Composable
fun NumberSequencePuzzleHomeJetpackScreen(
    navController: NavController,
    isSubscribed: Boolean = true,
    onPuzzleSelect: (Int) -> Unit,
    onBackClick: () -> Unit = {}
) {
    var showHelp by remember { mutableStateOf(false) }
    // Solved counts per grid size — so it's obvious which size was beaten.
    val solveMarker: com.jigar.me.ui.view.home.screens.math_game_zone.ZoneDailyMarkerViewModel =
        androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()

    Box(
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        GameHomeLayout(
            header = {Row(verticalAlignment = Alignment.CenterVertically) {
            BackButtonWithText(title = stringResource(R.string.number_sequence_puzzle), modifier = Modifier.weight(1f),onBackClick = onBackClick)
            GameButton(
                modifier = Modifier.padding(end = Dimens16),
                text = stringResource(R.string.how_to_play),
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                type = ButtonType.PINK,
                isSmall = true,
                onClick = {
                    showHelp = true
                }
            )
        }},
            bottom = {},
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Hero: a big multicolor sliding puzzle, alive — one tile
                // keeps sliding into the empty corner and back.
                SlidingPuzzleIcon(
                    gridSize = 4,
                    color = Color(0xFF2196F3),
                    multicolor = true,
                    modifier = Modifier
                        .size((androidx.compose.ui.platform.LocalConfiguration.current.screenHeightDp * (if (DeviceInfo.isTablet) 0.4f else 0.34f)).dp)
                )

                Text(
                    text = stringResource(R.string.select_the_puzzle_dashboard),
                    color = LocalAbColors.current.textPrimary,
                    textAlign = TextAlign.Center,
                    style = AbTypography.fixed(22f * appScale(), FontWeight.Bold),
                    modifier = Modifier.padding(top = Dimens16)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens24),
                    modifier = Modifier.padding(top = Dimens16)
                ) {
                    PuzzleOptionView(
                        gridSize = 3,
                        color = Color(0xFFF33173),
                        isLocked = false,
                        solved = solveMarker.solvedCount(
                            com.jigar.me.ui.view.home.screens.math_game_zone.common.ZoneSolveCounter.NUMBER_SEQUENCE, "grid3"
                        ),
                        onClick = { onPuzzleSelect(3) }
                    )
                    PuzzleOptionView(
                        gridSize = 4,
                        color = ColorYellowOrange,
                        isLocked = !isSubscribed,
                        solved = solveMarker.solvedCount(
                            com.jigar.me.ui.view.home.screens.math_game_zone.common.ZoneSolveCounter.NUMBER_SEQUENCE, "grid4"
                        ),
                        onClick = { onPuzzleSelect(4) }
                    )
                    PuzzleOptionView(
                        gridSize = 5,
                        color = Color(0xFF2196F3),
                        isLocked = !isSubscribed,
                        solved = solveMarker.solvedCount(
                            com.jigar.me.ui.view.home.screens.math_game_zone.common.ZoneSolveCounter.NUMBER_SEQUENCE, "grid5"
                        ),
                        onClick = { onPuzzleSelect(5) }
                    )
                }
            }

            }

    }
    AnimatedVisibility(
        visible = showHelp,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        HowToPlayNumberSequenceView {
            showHelp = false
        }
    }
}

@Composable
fun PuzzleOptionView(
    gridSize: Int,
    color: Color,
    isLocked: Boolean = false,
    solved: Int = 0,
    onClick: () -> Unit
) {
    val boxSize = if (DeviceInfo.isLargeTablet) 190.dp else if (DeviceInfo.isTablet) 140.dp else 100.dp
    val displayColor = if (isLocked) Color.Gray.copy(alpha = 0.4f) else color

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .abPopIn((gridSize - 3) * 120 + 100)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            SlidingPuzzleIcon(
                gridSize = gridSize,
                color = color,
                locked = isLocked,
                modifier = Modifier
                    .size(boxSize)
                    .graphicsLayer { alpha = if (isLocked) 0.5f else 1f }
            )

            if (isLocked) {
                Box(
                    modifier = Modifier
                        .offset(x = AppDimens.Dimens4, y = (-AppDimens.Dimens4))
                        .size(AppDimens.Dimens18)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = ColorYellowOrange,
                        modifier = Modifier.size(AppDimens.Dimens10)
                    )
                }
            }
        }

        Text(
            text = "$gridSize x $gridSize Puzzle",
            style = AbTypography.fixed(16f * appScale(), FontWeight.ExtraBold),
            color = displayColor,
            modifier = Modifier.padding(top = AppDimens.Dimens8)
        )
        // Solved count for this size — so it's obvious which size was beaten.
        if (!isLocked && solved > 0) {
            Text(
                text = "🧩 " + stringResource(R.string.zone_solved_count, solved),
                style = AbTypography.fixed(11f * appScale(), FontWeight.Bold),
                color = displayColor.copy(alpha = 0.85f)
            )
        }
    }
}

private fun puzzleEaseInOut(x: Float): Float =
    if (x < 0.5f) 2f * x * x else 1f - (-2f * x + 2f).let { it * it } / 2f

// 0 -> 1 with a small springy overshoot that settles (the tile bounces into the gap).
private fun puzzleSpringOut(x: Float): Float {
    val clamped = x.coerceIn(0f, 1f)
    val value = 1f - exp(-6f * clamped) * cos(9f * clamped)
    return minOf(value, 1.08f)
}

// A real sliding number puzzle drawn in code (twin of iOS SlidingPuzzleIcon): a toy tray (3D edge, soft white base, thick
// rim in [color]) with recessed slots and candy tiles 1..n²-1, the bottom-right corner open. One tile keeps sliding into
// the gap with a springy overshoot and gliding home — exactly the motion of the actual game — while the whole board
// sways gently (phase-shifted per size so the three previews don't move in sync).
@Composable
private fun SlidingPuzzleIcon(
    gridSize: Int,
    color: Color,
    locked: Boolean = false,
    multicolor: Boolean = false,
    modifier: Modifier = Modifier
) {
    val cycle = rememberInfiniteTransition(label = "slide").animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing)),
        label = "slide"
    )
    val phase = gridSize * 1.1f
    val palette = listOf(Color(0xFFF33173), Color(0xFFFF9800), Color(0xFF2196F3), Color(0xFF43A047))
    val base: Color = if (locked) Color.Gray else color
    val context = LocalContext.current
    val numberPaint = remember {
        android.graphics.Paint().apply {
            // `this.` needed — the composable's `color` param shadows Paint's.
            this.color = android.graphics.Color.WHITE
            textAlign = android.graphics.Paint.Align.CENTER
            isFakeBoldText = true
            isAntiAlias = true
            // the kit's heavy rounded font; the fake bold is only the fallback when it is missing
            val kitFont = ResourcesCompat.getFont(context, R.font.baloo2_extrabold)
            if (kitFont != null) { typeface = kitFont; isFakeBoldText = false }
        }
    }

    Canvas(
        modifier = modifier.graphicsLayer {
            rotationZ = sin(cycle.value * 2f * PI.toFloat() + phase) * 1.2f
        }
    ) {
        val t = cycle.value
        // Slide into the gap with a springy overshoot, rest, glide home, rest (2.2s cycle).
        val slide = when {
            t < 0.2f -> puzzleSpringOut(t / 0.2f)
            t < 0.55f -> 1f
            t < 0.75f -> 1f - puzzleEaseInOut((t - 0.55f) / 0.2f)
            else -> 0f
        }
        val w = this.size.width
        val h = this.size.height
        val minDim = minOf(w, h)
        val rim = maxOf(3.dp.toPx(), minDim * 0.035f)
        val trayEdge = maxOf(2.dp.toPx(), minDim * 0.035f)
        val gap = maxOf(2.dp.toPx(), minDim * 0.025f)
        val trayH = h - trayEdge
        val trayCorner = minDim * 0.14f

        // The toy tray: a 3D edge, a soft white base, a thick rim in the preview's colour.
        drawRoundRect(base.abDarkened(0.3f), topLeft = Offset(0f, trayEdge), size = Size(w, trayH), cornerRadius = CornerRadius(trayCorner))
        drawRoundRect(Color.White.copy(alpha = 0.94f), size = Size(w, trayH), cornerRadius = CornerRadius(trayCorner))
        drawRoundRect(
            base, topLeft = Offset(rim / 2f, rim / 2f), size = Size(w - rim, trayH - rim),
            cornerRadius = CornerRadius(trayCorner), style = Stroke(width = rim)
        )

        val innerLeft = rim + gap
        val innerTop = rim + gap
        val innerW = w - 2f * (rim + gap)
        val innerH = trayH - 2f * (rim + gap)
        val cell = (minOf(innerW, innerH) - gap * (gridSize - 1)) / gridSize
        val corner = cell * 0.24f
        val edge = maxOf(2.dp.toPx(), cell * 0.07f)
        val total = gridSize * gridSize

        // Recessed slots, one per position.
        for (i in 0 until total) {
            val r = i / gridSize
            val c = i % gridSize
            drawRoundRect(
                base.copy(alpha = 0.16f),
                topLeft = Offset(innerLeft + c * (cell + gap), innerTop + r * (cell + gap)),
                size = Size(cell, cell), cornerRadius = CornerRadius(corner)
            )
        }

        numberPaint.textSize = cell * 0.5f
        val emptyIndex = total - 1     // bottom-right corner stays open
        val sliderIndex = total - 2    // its left neighbor does the sliding
        for (i in 0 until total) {
            if (i == emptyIndex) continue
            val r = i / gridSize
            val c = i % gridSize
            var x = innerLeft + c * (cell + gap)
            val y = innerTop + r * (cell + gap)
            if (i == sliderIndex) x += slide * (cell + gap)
            val tint: Color = (if (locked) base else if (multicolor) palette[i % palette.size] else base).abReadable(3f)
            val faceH = cell - edge

            // 3D edge, bright face, glossy top, soft rim.
            drawRoundRect(tint.abDarkened(0.3f), topLeft = Offset(x, y + edge), size = Size(cell, faceH), cornerRadius = CornerRadius(corner))
            drawRoundRect(tint, topLeft = Offset(x, y), size = Size(cell, faceH), cornerRadius = CornerRadius(corner))
            drawRoundRect(
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.32f), Color.Transparent),
                    startY = y, endY = y + faceH / 2f
                ),
                topLeft = Offset(x, y), size = Size(cell, faceH), cornerRadius = CornerRadius(corner)
            )
            drawRoundRect(
                Color.White.copy(alpha = 0.45f),
                topLeft = Offset(x + 0.75f, y + 0.75f), size = Size(cell - 1.5f, faceH - 1.5f),
                cornerRadius = CornerRadius(corner), style = Stroke(width = 1.5.dp.toPx())
            )
            val cy = y + faceH / 2f
            drawContext.canvas.nativeCanvas.drawText(
                "${i + 1}", x + cell / 2f, cy - (numberPaint.descent() + numberPaint.ascent()) / 2f, numberPaint
            )
        }
    }
}
