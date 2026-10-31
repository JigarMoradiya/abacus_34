package com.jigar.me.ui.view.home.screens.math_game_zone.target_number

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeLayout

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abBlended
import com.jigar.me.ui.view.ab.abChunkyFace
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.CommonDifficultySelectorCompose
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayTargetNumberView
import com.jigar.me.ui.view.home.screens.math_game_zone.common.toyColor
import com.jigar.me.ui.view.home.screens.math_game_zone.target_number.components.TargetNumberViewModel
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@Composable
fun TargetNumberHomeScreen(
    viewModel: TargetNumberViewModel,
    onStartGame: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showHelp by remember { mutableStateOf(false) }
    val levelRange = 1..3
    val time = rememberTargetNumberTime()

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        GameHomeLayout(
            fillHeight = true,
            header = {Row(verticalAlignment = Alignment.CenterVertically) {
                BackButtonWithText(title = stringResource(R.string.target_number_game),modifier = Modifier.weight(1f), onBackClick = onBackClick)
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
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = Dimens4)
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
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                levelRange.forEach { level ->
                    val isSelected = state.selectedLevel == level

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(Dimens4)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                viewModel.selectLevel(level)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        TargetNumberMedal(level = level, selected = isSelected, time = time, modifier = Modifier.fillMaxHeight(0.84f).aspectRatio(1f))
                    }

                }
            }}

    }
    AnimatedVisibility(
        visible = showHelp,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        HowToPlayTargetNumberView {
            showHelp = false
        }
    }
}

// ── Level medals ──────────────────────────────────────────────────────────────────────────────────────────────────────

/**
 * A level medal that floats and tilts gently, with a shine that sweeps across it now and then. The chosen medal floats
 * higher, glows and twinkles. (Android twin of `TargetNumberMedal` in iOS TargetNumberHomeView.swift.)
 */
@Composable
private fun TargetNumberMedal(level: Int, selected: Boolean, time: State<Float>, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.12f else 0.9f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f),
        label = "medalScale"
    )
    val glowAlpha by animateFloatAsState(targetValue = if (selected) 1f else 0f, label = "medalGlow")
    val glowColor = colors.accentYellow

    Box(
        modifier = modifier.graphicsLayer {
            val phase = time.value * 1.6f + level * 1.3f
            val lift = if (selected) 6f else 3.5f
            translationY = sin(phase) * lift * density
            rotationZ = sin(phase * 0.7f) * (if (selected) 3f else 1.5f)
            scaleX = scale
            scaleY = scale
        }
    ) {
        // A warm pulsing glow behind the chosen medal.
        Canvas(Modifier.matchParentSize()) {
            if (glowAlpha > 0.01f) {
                val pulse = 1f + 0.06f * sin(time.value * 3f)
                val c = Offset(size.width / 2f, size.height / 2f)
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(glowColor.copy(alpha = 0.7f * glowAlpha), glowColor.copy(alpha = 0f)),
                        center = c,
                        radius = size.width * 0.62f * pulse
                    ),
                    radius = size.width / 2f * pulse,
                    center = c
                )
            }
        }

        Box(Modifier.matchParentSize().padding(8.dp)) {
            TargetNumberMedalArt(level = level, time = time, modifier = Modifier.fillMaxSize())
        }

        // Little stars that twinkle on the chosen medal.
        if (selected) {
            TargetNumberTwinkle(time = time, phase = 0f, modifier = Modifier.align(Alignment.TopEnd).padding(6.dp))
            TargetNumberTwinkle(time = time, phase = 2.1f, modifier = Modifier.align(Alignment.BottomStart).padding(6.dp))
        }
    }
}

@Composable
private fun TargetNumberTwinkle(time: State<Float>, phase: Float, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    val star = colors.accentYellow
    Canvas(
        modifier
            .size(20.dp)
            .graphicsLayer {
                val k = 0.7f + 0.35f * sin(time.value * 4f + phase)
                scaleX = k
                scaleY = k
            }
    ) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f * 0.95f
        drawPath(targetNumberStarPath(c.x, c.y + 1.dp.toPx(), r), Color.Black.copy(alpha = 0.25f))
        drawPath(targetNumberStarPath(c.x, c.y, r), star)
        drawPath(targetNumberStarPath(c.x, c.y, r), star, style = Stroke(r * 0.18f, join = androidx.compose.ui.graphics.StrokeJoin.Round))
    }
}

/**
 * The artwork of one Target Number level medal: a glossy candy medal with a pulsing star that holds a sample target,
 * number chips and the signs that level allows, under a "LEVEL n" ribbon, with a shine that sweeps across the disc now
 * and then. [time] is the animation clock (seconds) the caller already runs.
 */
@Composable
internal fun TargetNumberMedalArt(level: Int, time: State<Float>, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    val fontScale = LocalDensity.current.fontScale
    val accent = when (level) {
        1 -> Color(0xFF14B8A6)
        2 -> Color(0xFFF97316)
        else -> Color(0xFF8B5CF6)
    }
    val target = when (level) {
        1 -> 3
        2 -> 94
        else -> 21
    }
    val numbers = when (level) {
        1 -> listOf(9, 2, 12, 8)
        2 -> listOf(12, 4, 10, 3)
        else -> listOf(3, 10, 15, 6)
    }
    val signs = when (level) {
        1 -> listOf("+", "−")
        2 -> listOf("+", "−", "×")
        else -> listOf("+", "−", "×", "÷")
    }
    val starGold = Color(0xFFFFD54F)
    val deepGold = Color(0xFFB45309)

    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        val g = min(maxWidth.value, maxHeight.value)   // the medal's square, in dp
        val d = g * 0.76f                               // the disc
        val shift = g * 0.05f                           // everything sits a little low, under the ribbon

        Box(Modifier.size(g.dp)) {
            // The disc: a bronze edge, a gold ring, the level's colour and a pale inner ring.
            Canvas(Modifier.align(Alignment.Center).offset(y = shift.dp).size(d.dp)) {
                val r = size.minDimension / 2f
                val c = Offset(size.width / 2f, size.height / 2f)
                drawCircle(deepGold, r, Offset(c.x, c.y + size.width * 0.04f))
                drawCircle(
                    Brush.verticalGradient(listOf(Color(0xFFFFE082), Color(0xFFF59E0B)), startY = c.y - r, endY = c.y + r),
                    r, c
                )
                val inner = r - size.width * 0.07f
                drawCircle(
                    Brush.radialGradient(
                        listOf(accent.abBlended(Color.White, 0.25f), accent.abDarkened(0.15f)),
                        center = Offset(c.x - inner, c.y - inner),
                        radius = size.width * 0.8f
                    ),
                    inner, c
                )
                val ringW = max(1.5.dp.toPx(), size.width * 0.012f)
                drawCircle(Color.White.copy(alpha = 0.5f), r - size.width * 0.1f - ringW / 2f, c, style = Stroke(ringW))
            }

            // The pulsing star holding the sample target.
            Box(
                Modifier
                    .align(Alignment.Center)
                    .offset(y = (-d * 0.14f + shift).dp)
                    .size((d * 0.56f).dp)
                    .graphicsLayer {
                        val pulse = 1f + 0.06f * sin(time.value * 3f + level)
                        scaleX = pulse
                        scaleY = pulse
                    },
                contentAlignment = Alignment.Center
            ) {
                Canvas(Modifier.matchParentSize()) {
                    val c = Offset(size.width / 2f, size.height / 2f)
                    val r = size.minDimension / 2f * 0.95f
                    val join = androidx.compose.ui.graphics.StrokeJoin.Round
                    drawPath(targetNumberStarPath(c.x, c.y + d * 0.02f * density, r), deepGold.copy(alpha = 0.7f))
                    drawPath(targetNumberStarPath(c.x, c.y + d * 0.02f * density, r), deepGold.copy(alpha = 0.7f), style = Stroke(r * 0.16f, join = join))
                    drawPath(targetNumberStarPath(c.x, c.y, r), starGold)
                    drawPath(targetNumberStarPath(c.x, c.y, r), starGold, style = Stroke(r * 0.16f, join = join))
                }
                Text(
                    "$target",
                    style = AbTypography.fixed(d * (if (target > 9) 0.17f else 0.21f) / fontScale, FontWeight.Black),
                    color = Color(0xFF7C2D12),
                    modifier = Modifier.offset(y = (d * 0.02f).dp)
                )
            }

            // Four number chips that bob in a wave.
            val chipW = d * 0.14f
            Row(
                Modifier.align(Alignment.Center).offset(y = (d * 0.2f + shift).dp),
                horizontalArrangement = Arrangement.spacedBy((d * 0.02f).dp)
            ) {
                numbers.forEachIndexed { i, value ->
                    Box(
                        Modifier
                            .size(chipW.dp)
                            .graphicsLayer { translationY = sin(time.value * 2.4f + i * 0.8f) * d * 0.012f * density }
                            .background(colors.toyColor(i + level), RoundedCornerShape((chipW * 0.3f).dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "$value",
                            style = AbTypography.fixed(chipW * (if (value > 9) 0.5f else 0.62f) / fontScale, FontWeight.Black),
                            color = Color.White,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            // The signs this level allows, each pulsing in turn.
            val signW = d * 0.1f
            Row(
                Modifier.align(Alignment.Center).offset(y = (d * 0.34f + shift).dp),
                horizontalArrangement = Arrangement.spacedBy((d * 0.03f).dp)
            ) {
                signs.forEachIndexed { i, sign ->
                    Box(
                        Modifier
                            .size(signW.dp)
                            .graphicsLayer {
                                val spin = 1f + 0.12f * max(0f, sin(time.value * 2f - i * 0.7f))
                                scaleX = spin
                                scaleY = spin
                            }
                            .background(Color.White.copy(alpha = 0.28f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            sign,
                            style = AbTypography.fixed(signW * 0.9f / fontScale, FontWeight.Black),
                            color = Color.White,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            // The LEVEL ribbon above the disc.
            val ribbon = Color(0xFFE11D48)
            Text(
                "LEVEL $level",
                style = AbTypography.fixed(g * 0.1f / fontScale, FontWeight.Black).copy(shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f)),
                color = Color.White,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-g * 0.4f).dp)
                    .abChunkyFace(ribbon, ribbon.abDarkened(0.3f), null, AbShapes.pill, max(3f, g * 0.018f).dp)
                    .padding(horizontal = (g * 0.09f).dp, vertical = (g * 0.01f).dp)
            )

            // A soft band of light that sweeps across the disc now and then (clipped to the disc, not the ribbon).
            Canvas(Modifier.matchParentSize()) {
                val w = size.width
                val cycle = 3.6f
                val local = ((time.value + level * 1.1f) % cycle) / cycle
                if (local < 0.4f) {
                    val sweep = min(1f, local / 0.4f)
                    val disc = Path().apply {
                        addOval(Rect(Offset(w / 2f, size.height / 2f + w * 0.05f), w * 0.38f))
                    }
                    val x0 = -w * 0.5f + sweep * w * 1.3f
                    val y0 = -w * 0.3f
                    val bw = w * 0.35f
                    val bh = w * 1.6f
                    clipPath(disc) {
                        rotate(20f, pivot = Offset(x0 + bw / 2f, y0 + bh / 2f)) {
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    listOf(Color.Transparent, Color.White.copy(alpha = 0.75f), Color.Transparent),
                                    startX = x0, endX = x0 + bw
                                ),
                                topLeft = Offset(x0, y0),
                                size = Size(bw, bh)
                            )
                        }
                    }
                }
            }
        }
    }
}
