package com.jigar.me.ui.view.home.screens.math_game_zone.number_sequence_puzzle.home

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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import kotlin.math.sin

@Composable
fun NumberSequencePuzzleHomeJetpackScreen(
    navController: NavController,
    isSubscribed: Boolean = true,
    onPuzzleSelect: (Int) -> Unit,
    onBackClick: () -> Unit = {}
) {
    var showHelp by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BackButtonWithText(title = stringResource(R.string.number_sequence_puzzle), modifier = Modifier.weight(1f),onBackClick = onBackClick)
            KidsActionButton(
                modifier = Modifier.padding(end = Dimens16),
                text = stringResource(R.string.how_to_play),
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                type = ButtonType.PINK,
                isSmall = true,
                onClick = {
                    showHelp = true
                }
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))
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
                        .fillMaxHeight(if (DeviceInfo.isTablet) 0.4f else 0.35f)
                        .aspectRatio(1f)
                )

                Text(
                    text = stringResource(R.string.select_the_puzzle_dashboard),
                    fontFamily = FontFamily(Font(R.font.font_bold)),
                    color = colorResource(id = R.color.colorBlueDark),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge.scaled(),
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
                        onClick = { onPuzzleSelect(3) }
                    )
                    PuzzleOptionView(
                        gridSize = 4,
                        color = ColorYellowOrange,
                        isLocked = !isSubscribed,
                        onClick = { onPuzzleSelect(4) }
                    )
                    PuzzleOptionView(
                        gridSize = 5,
                        color = Color(0xFF2196F3),
                        isLocked = !isSubscribed,
                        onClick = { onPuzzleSelect(5) }
                    )
                }
            }

            Spacer(Modifier.weight(1f))
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
    onClick: () -> Unit
) {
    val boxSize = SudokuHomeIconsBox
    val displayColor = if (isLocked) Color.Gray.copy(alpha = 0.4f) else color

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            SlidingPuzzleIcon(
                gridSize = gridSize,
                color = color,
                locked = isLocked,
                modifier = Modifier.size(boxSize)
            )

            if (isLocked) {
                Box(
                    modifier = Modifier
                        .offset(x = AppDimens.Dimens4, y = (-AppDimens.Dimens4))
                        .size(AppDimens.Dimens18)
                        .background(Color(0xFFFF9800), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(AppDimens.Dimens10)
                    )
                }
            }
        }

        Text(
            text = "$gridSize x $gridSize Puzzle",
            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
            style = MaterialTheme.typography.labelLarge.scaled(),
            color = displayColor,
            modifier = Modifier.padding(top = AppDimens.Dimens8)
        )
    }
}

// A real sliding number puzzle drawn in code: colored tiles 1..n²-1 with the
// bottom-right corner open. One tile keeps sliding into the gap and back —
// exactly the motion of the actual game — while the whole board sways
// gently (phase-shifted per size so the three previews don't move in sync).
@Composable
private fun SlidingPuzzleIcon(
    gridSize: Int,
    color: Color,
    locked: Boolean = false,
    multicolor: Boolean = false,
    modifier: Modifier = Modifier
) {
    val t by rememberInfiniteTransition(label = "slide").animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing)),
        label = "slide"
    )
    // Slide into the gap, rest, slide home, rest.
    val slide = when {
        t < 0.2f -> FastOutSlowInEasing.transform(t / 0.2f)
        t < 0.55f -> 1f
        t < 0.75f -> 1f - FastOutSlowInEasing.transform((t - 0.55f) / 0.2f)
        else -> 0f
    }
    val phase = gridSize * 1.1f
    val palette = listOf(Color(0xFFF33173), Color(0xFFFF9800), Color(0xFF2196F3), Color(0xFF43A047))
    val numberPaint = remember {
        android.graphics.Paint().apply {
            // `this.` needed — the composable's `color` param shadows Paint's.
            this.color = android.graphics.Color.WHITE
            textAlign = android.graphics.Paint.Align.CENTER
            isFakeBoldText = true
            isAntiAlias = true
        }
    }

    Canvas(
        modifier = modifier.graphicsLayer {
            rotationZ = sin(t * 2f * PI.toFloat() + phase) * 1.2f
        }
    ) {
        val gap = 2.dp.toPx()
        val cell = (size.minDimension - gap * (gridSize + 1)) / gridSize
        val corner = CornerRadius(cell * 0.2f, cell * 0.2f)
        val baseTint = if (locked) Color.Gray.copy(alpha = 0.55f) else color

        // Soft board behind the tiles.
        drawRoundRect(
            color = baseTint.copy(alpha = 0.14f),
            cornerRadius = CornerRadius(cell * 0.3f, cell * 0.3f)
        )
        numberPaint.textSize = cell * 0.5f

        val total = gridSize * gridSize
        val emptyIndex = total - 1     // bottom-right corner stays open
        val sliderIndex = total - 2    // its left neighbor does the sliding
        for (i in 0 until total) {
            if (i == emptyIndex) continue
            val r = i / gridSize
            val c = i % gridSize
            var x = gap + c * (cell + gap)
            val y = gap + r * (cell + gap)
            if (i == sliderIndex) x += slide * (cell + gap)
            val tint = when {
                locked -> baseTint
                multicolor -> palette[i % palette.size]
                else -> baseTint
            }
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(tint.copy(alpha = 0.8f), tint),
                    startY = y, endY = y + cell
                ),
                topLeft = Offset(x, y),
                size = Size(cell, cell),
                cornerRadius = corner
            )
            drawContext.canvas.nativeCanvas.drawText(
                "${i + 1}", x + cell / 2f, y + cell / 2f + cell * 0.18f, numberPaint
            )
        }
    }
}
