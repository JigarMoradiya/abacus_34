package com.jigar.me.ui.view.home.screens.math_game_zone.math_pyramid.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.sin
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens32
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.CommonDifficultySelectorCompose
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayMathPyramidView
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

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Column(modifier = Modifier.fillMaxSize()) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                BackButtonWithText(title = stringResource(R.string.math_pyramid), modifier = Modifier.weight(1f),onBackClick = onBackClick)
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

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                levelRange.forEach { level ->

                    // No clip here — the selected pyramid scales up, sways and
                    // glows beyond its own bounds; clipping cut it off.
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
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
                            selected = state.selectedLevel == level,
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
                                color = if (state.selectedLevel == level) Color(0xFFE65100) else Color(0xFF5D4037),
                                fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                                fontSize = dimensionResource(
                                    id = if (state.selectedLevel == level) R.dimen.textSize24 else R.dimen.textSize17
                                ).value.sp.scaled()
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens16),
                horizontalArrangement = Arrangement.spacedBy(Dimens16, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CommonDifficultySelectorCompose(
                    selected = state.selectedDifficulty,
                    onSelect = { viewModel.selectDifficulty(it) }
                )

                KidsActionButton(
                    text = stringResource(R.string.lets_start),
                    icon = Icons.Rounded.PlayArrow,
                    type = ButtonType.ORANGE,
                    isIconStart = false,
                    onClick = {
                        onStartGame()
                    }
                )
            }
        }

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

// A real-looking sandstone pyramid drawn in code. Bricks build up from the
// bottom row with a stagger, the whole pyramid idles with a gentle sway
// (phase-shifted per size so they don't move in sync), and the selected one
// pops bigger with a slow bounce over a golden glow.
@Composable
private fun PyramidLevelIcon(rows: Int, selected: Boolean, modifier: Modifier = Modifier) {
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
        val gapPx = 2.dp.toPx()
        val brickH = (size.height - gapPx * (rows - 1)) / rows
        val brickW = (size.width - gapPx * (rows - 1)) / rows
        val corner = CornerRadius(brickH * 0.22f, brickH * 0.22f)

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
            val x0 = (size.width - rowW) / 2f
            val y = r * (brickH + gapPx)
            for (b in 0 until bricksInRow) {
                val p = (build * totalBricks - index).coerceIn(0f, 1f)
                index++
                if (p <= 0f) continue
                val cx = x0 + b * (brickW + gapPx) + brickW / 2f
                val cy = y + brickH / 2f
                val w = brickW * p
                val h = brickH * p
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFFFFCC80).copy(alpha = if (selected) 1f else 0.9f), Color(0xFFF57C00)),
                        startY = cy - h / 2f, endY = cy + h / 2f
                    ),
                    topLeft = Offset(cx - w / 2f, cy - h / 2f),
                    size = Size(w, h),
                    cornerRadius = corner
                )
                drawRoundRect(
                    color = Color(0xFFBF6000).copy(alpha = 0.85f),
                    topLeft = Offset(cx - w / 2f, cy - h / 2f),
                    size = Size(w, h),
                    cornerRadius = corner,
                    style = Stroke(1.5.dp.toPx())
                )
            }
        }
    }
}
