package com.jigar.me.ui.view.home.screens.math_game_zone.math_pyramid.home

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeLayout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abBlended
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens32
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.CommonDifficultySelectorCompose
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayMathPyramidView
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBounce
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abPopIn
import com.jigar.me.ui.view.home.screens.math_game_zone.math_pyramid.home.components.MathPyramidViewModel
import com.jigar.me.ui.view.home.theme.ButtonType

@Composable
fun MathPyramidHomeJetpackScreen(
    viewModel: MathPyramidViewModel,
    onStartGame: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showHelp by remember { mutableStateOf(false) }
    val levelRange = 2..6
    val kitScale = LocalAbScale.current.factor

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        GameHomeLayout(
            header = {Row(verticalAlignment = Alignment.CenterVertically) {
                BackButtonWithText(title = stringResource(R.string.math_pyramid), modifier = Modifier.weight(1f),onBackClick = onBackClick)
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
            bottom = {Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens16),
                horizontalArrangement = Arrangement.spacedBy(Dimens16, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CommonDifficultySelectorCompose(
                        selected = state.selectedDifficulty,
                        onSelect = { viewModel.selectDifficulty(it) }
                    )
                    // Solved count for the difficulty currently selected —
                    // so it's obvious which tier has actually been beaten.
                    val solved = viewModel.solvedCount(state.selectedDifficulty)
                    if (solved > 0) {
                        Text(
                            "🧩 " + stringResource(R.string.zone_solved_count, solved),
                            color = colorResource(R.color.colorBlueDark),
                            style = AbTypography.fixed(13f * kitScale, FontWeight.Bold),
                            modifier = Modifier.padding(top = AppDimens.Dimens4)
                        )
                    }
                }

                GameButton(
                    text = stringResource(R.string.lets_start),
                    icon = Icons.Rounded.PlayArrow,
                    type = ButtonType.ORANGE,
                    isIconStart = false,
                    onClick = {
                        onStartGame()
                    }
                )
            }},
        ) {Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens16),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                levelRange.forEach { level ->
                    val selected = state.selectedLevel == level
                    // a soft candy card behind the picked pyramid, so the choice reads at a glance
                    val card by animateFloatAsState(
                        targetValue = if (selected) 1f else 0f,
                        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium),
                        label = "levelCard"
                    )

                    // No clip here — the selected pyramid scales up, sways and
                    // glows beyond its own bounds; clipping cut it off.
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .abPopIn((level - levelRange.first) * 80)
                            .drawBehind {
                                val a = card.coerceIn(0f, 1f)
                                if (a > 0.01f) {
                                    val left = -8.dp.toPx()
                                    val top = -18.dp.toPx()
                                    val w = size.width + 16.dp.toPx()
                                    val h = size.height + 22.dp.toPx()
                                    val r = CornerRadius(20.dp.toPx())
                                    drawRoundRect(Color(0xFFE65100).copy(alpha = 0.35f * a), Offset(left, top + 4.dp.toPx()), Size(w, h), r)
                                    drawRoundRect(Color.White.copy(alpha = 0.9f * a), Offset(left, top), Size(w, h), r)
                                    val half = 1.5.dp.toPx()
                                    drawRoundRect(
                                        Color(0xFFFFA726).copy(alpha = a),
                                        Offset(left + half, top + half),
                                        Size(w - 2f * half, h - 2f * half),
                                        CornerRadius(18.5.dp.toPx()),
                                        style = Stroke(3.dp.toPx())
                                    )
                                }
                            }
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                viewModel.selectLevel(level)
                            }
                            .padding(Dimens8)
                    ) {
                        PyramidLevelIcon(
                            rows = level,
                            selected = selected,
                            modifier = Modifier
                                .fillMaxWidth(0.82f)
                                .aspectRatio(1.5f)
                        )

                        Box(
                            modifier = Modifier.height(Dimens32),
                            contentAlignment = Alignment.Center
                        ) {
                            // Kids style: chunky rounded label, orange when picked.
                            Text(
                                text = "Level $level",
                                color = if (selected) Color(0xFFE65100) else Color(0xFF5D4037),
                                style = AbTypography.fixed((if (selected) 26f else 18f) * kitScale, FontWeight.Black),
                                modifier = Modifier.abBounce(selected, 1.12f)
                            )
                        }
                    }
                }
            }}

    }

    AnimatedVisibility(
        visible = showHelp,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        HowToPlayMathPyramidView {
            showHelp = false
        }
    }
}

// A toy-block pyramid drawn in code. Bricks build up from the bottom row with a stagger, each row in its own candy colour
// (the same rainbow the play bricks use, top to bottom). The whole pyramid idles with a gentle sway (phase-shifted per size so
// they don't move in sync), and the selected one pops bigger with a slow bounce over a golden glow.
@Composable
private fun PyramidLevelIcon(rows: Int, selected: Boolean, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    // The same rainbow the play bricks use, top to bottom. Worked out once here, not every frame.
    val tones: List<Color> = remember(colors) {
        listOf(colors.accentPink, colors.accentOrange, colors.accentGreen, colors.brandSecondary, colors.accentPurple, colors.brandPrimary)
    }
    val lights: List<Color> = remember(tones) { tones.map { it.abBlended(Color.White, 0.3f) } }
    val lips: List<Color> = remember(tones) { tones.map { it.abDarkened(0.3f) } }

    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appeared = true }
    val build by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(durationMillis = 450 + rows * 130, easing = FastOutSlowInEasing),
        label = "build"
    )
    val t by rememberInfiniteTransition(label = "sway").animateFloat(
        initialValue = 0f, targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing)),
        label = "sway"
    )
    val selScale by animateFloatAsState(
        targetValue = if (selected) 1.14f else 1f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "selScale"
    )
    val phase = rows * 0.9f

    Canvas(
        modifier = modifier.graphicsLayer {
            val bounce = if (selected) 1f + 0.04f * sin(t * 2f + phase) else 1f
            scaleX = selScale * bounce
            scaleY = selScale * bounce
            rotationZ = sin(t + phase) * 1.5f
            transformOrigin = TransformOrigin(0.5f, 1f)
        }
    ) {
        val gapPx = 3.dp.toPx()
        val lipPx = 2.5.dp.toPx()
        // keep the strokes and the 3D lip at the edges inside the canvas
        val inset = 1.5.dp.toPx()
        val drawW = size.width - inset * 2f
        val drawH = size.height - inset * 2f - lipPx
        val brickH = (drawH - gapPx * (rows - 1)) / rows
        val brickW = (drawW - gapPx * (rows - 1)) / rows

        if (selected) {
            val glowCenter = Offset(size.width / 2f, size.height * 0.6f)
            val glowRadius = size.width * 0.55f
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFFFFD54F).copy(alpha = 0.5f), Color.Transparent),
                    center = glowCenter, radius = glowRadius
                ),
                radius = glowRadius,
                center = glowCenter
            )
        }

        val totalBricks = rows * (rows + 1) / 2
        var index = 0
        // Bottom row first — pyramids are built from the ground up!
        for (r in rows - 1 downTo 0) {
            val bricksInRow = r + 1
            val rowW = bricksInRow * brickW + (bricksInRow - 1) * gapPx
            val x0 = inset + (drawW - rowW) / 2f
            val y = inset + r * (brickH + gapPx)
            val ti = min(r, tones.size - 1)
            for (b in 0 until bricksInRow) {
                val p = (build * totalBricks - index).coerceIn(0f, 1f)
                index++
                if (p <= 0f) continue
                val cx = x0 + b * (brickW + gapPx) + brickW / 2f
                val cy = y + brickH / 2f
                val w = brickW * p
                val h = brickH * p
                val left = cx - w / 2f
                val top = cy - h / 2f
                val corner = CornerRadius(brickH * 0.3f, brickH * 0.3f)
                // toy block: 3D lip, candy face, glossy strip, white rim
                drawRoundRect(lips[ti], Offset(left, top + lipPx), Size(w, h), corner)
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(lights[ti].copy(alpha = if (selected) 1f else 0.92f), tones[ti]),
                        startY = top, endY = top + h
                    ),
                    topLeft = Offset(left, top),
                    size = Size(w, h),
                    cornerRadius = corner
                )
                val glossH = max(1f, h * 0.2f)
                drawRoundRect(
                    Color.White.copy(alpha = 0.42f),
                    Offset(left + w * 0.14f, top + h * 0.12f),
                    Size(w * 0.72f, glossH),
                    CornerRadius(glossH / 2f, glossH / 2f)
                )
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.6f),
                    topLeft = Offset(left, top),
                    size = Size(w, h),
                    cornerRadius = corner,
                    style = Stroke(1.2.dp.toPx())
                )
            }
        }
    }
}
