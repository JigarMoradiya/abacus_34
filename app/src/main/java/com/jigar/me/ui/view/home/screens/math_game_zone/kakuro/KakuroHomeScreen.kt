package com.jigar.me.ui.view.home.screens.math_game_zone.kakuro

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.DifficultyPillBox
import com.jigar.me.ui.view.home.common_ui.DifficultyPillItem
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayKakuroView
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abPopIn
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.kakuro.components.KakuroConfig
import com.jigar.me.ui.view.home.screens.math_game_zone.kakuro.components.KakuroHomeViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)
private val NODE_LOCKED = Color(0xFFB0BEC5)
private val PATH_COLOR = Color(0xFFB0BEC5)

@Composable
fun KakuroHomeScreen(
    viewModel: KakuroHomeViewModel,
    isSubscribed: Boolean,
    onLevelClick: (CommonDifficulty4, Int) -> Unit,
    onBackClick: () -> Unit
) {
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsStateWithLifecycle()
    val progressVersion by viewModel.progressVersion.collectAsStateWithLifecycle()
    var showHelp by remember { mutableStateOf(false) }
    val s = gameScale()
    val kitScale = LocalAbScale.current.factor

    val stars = remember(selectedDifficulty, progressVersion) {
        viewModel.progress.stars(selectedDifficulty)
    }
    val nextLevel = remember(selectedDifficulty, progressVersion) {
        viewModel.progress.nextLevel(selectedDifficulty)
    }

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height((52f * s).dp))

            Text(
                text = stringResource(R.string.kakuro_tagline),
                color = ORANGE,
                style = AbTypography.fixed(22f * kitScale, FontWeight.ExtraBold),
                textAlign = TextAlign.Center,
                modifier = Modifier.abGamePill()
                    .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
            )

            Spacer(Modifier.height(AppDimens.Dimens8))

            RoadmapPath(
                stars = stars,
                nextLevel = nextLevel,
                s = s,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                onLevelClick = { level -> onLevelClick(selectedDifficulty, level) }
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(AppDimens.Dimens16),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DifficultyPillBox {
                    CommonDifficulty4.entries.forEach { d ->
                        val locked = d != CommonDifficulty4.easy && !isSubscribed
                        DifficultyPillItem(
                            text = (if (locked) "🔒 " else "") + d.displayName,
                            isSelected = d == selectedDifficulty
                        ) { viewModel.selectDifficulty(d) }
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().align(Alignment.TopStart), verticalAlignment = Alignment.CenterVertically) {
            BackButtonWithText(
                title = stringResource(R.string.kakuro),
                modifier = Modifier.weight(1f), onBackClick = onBackClick
            )
            GameButton(
                modifier = Modifier.padding(end = AppDimens.Dimens16),
                text = stringResource(R.string.how_to_play),
                icon = Icons.AutoMirrored.Filled.HelpOutline, type = ButtonType.PINK, isSmall = true,
                onClick = { showHelp = true }
            )
        }
    }

    AnimatedVisibility(visible = showHelp, enter = fadeIn(), exit = fadeOut()) {
        HowToPlayKakuroView { showHelp = false }
    }
}

// Same journey-map pattern as Cross Math, in Kakuro's slate family.
@Composable
private fun RoadmapPath(
    stars: IntArray,
    nextLevel: Int,
    s: Float,
    modifier: Modifier = Modifier,
    onLevelClick: (Int) -> Unit
) {
    val colors = LocalAbColors.current
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val nodeSize = (46f * s).dp
    val spacing = (76f * s).dp
    val startPad = (40f * s).dp
    val amplitude = (34f * s).dp
    val totalWidth = startPad * 2 + spacing * (KakuroConfig.LEVEL_COUNT - 1) + nodeSize

    LaunchedEffect(nextLevel, stars) {
        val target = with(density) { (startPad + spacing * (nextLevel - 1) - spacing * 2).toPx() }
        scrollState.animateScrollTo(target.toInt().coerceAtLeast(0))
    }

    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f, targetValue = 1.14f,
        animationSpec = infiniteRepeatable(tween(650), RepeatMode.Reverse), label = "pulse"
    )

    Box(modifier = modifier.horizontalScroll(scrollState)) {
        Box(Modifier.width(totalWidth).fillMaxHeight()) {

            fun nodeYFraction(i: Int): Float = sin(i * 0.9f)

            Canvas(modifier = Modifier.fillMaxSize()) {
                val cy = size.height / 2f
                val ampPx = amplitude.toPx()
                val nodeR = nodeSize.toPx() / 2f
                val step = spacing.toPx()
                val x0 = startPad.toPx() + nodeR
                val path = Path()
                for (i in 0 until KakuroConfig.LEVEL_COUNT) {
                    val x = x0 + step * i
                    val y = cy + nodeYFraction(i) * ampPx
                    if (i == 0) path.moveTo(x, y) else {
                        val px = x0 + step * (i - 1)
                        val py = cy + nodeYFraction(i - 1) * ampPx
                        path.quadraticBezierTo((px + x) / 2f, (py + y) / 2f + 14f, x, y)
                    }
                }
                drawPath(
                    path, PATH_COLOR,
                    style = Stroke(
                        width = 6.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 16f))
                    )
                )
            }

            for (i in 0 until KakuroConfig.LEVEL_COUNT) {
                val level = i + 1
                val done = stars[i] > 0
                val unlocked = level == 1 || stars[i - 1] > 0
                val isNext = unlocked && !done && level == nextLevel

                // candy level buttons: green when finished, orange for the one to play next, indigo when open, grey when locked
                val face = when {
                    !unlocked -> NODE_LOCKED
                    done -> colors.accentGreen.abReadable(3f)
                    isNext -> colors.accentOrange.abReadable(3f)
                    else -> colors.brandPrimary.abReadable(3f)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = startPad + spacing * i, y = amplitude * nodeYFraction(i))
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .abPopIn(minOf(400, level * 30))
                            .graphicsLayer {
                                if (isNext) { scaleX = pulse; scaleY = pulse }
                            }
                            .size(nodeSize)
                            .drawBehind {
                                drawCircle(
                                    color = face.abDarkened(0.3f),
                                    radius = size.minDimension / 2f,
                                    center = Offset(size.width / 2f, size.height / 2f + 3f * s * this.density)
                                )
                            }
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                enabled = unlocked
                            ) { onLevelClick(level) }
                            .background(face, CircleShape)
                            .drawBehind {
                                drawCircle(
                                    brush = Brush.verticalGradient(
                                        listOf(Color.White.copy(alpha = 0.38f), Color.Transparent),
                                        startY = 0f, endY = size.height / 2f
                                    )
                                )
                            }
                            .border(3.dp, Color.White, CircleShape)
                    ) {
                        Text(
                            text = if (unlocked) "$level" else "🔒",
                            color = Color.White,
                            style = AbTypography.fixed(20f * s, FontWeight.Black).copy(
                                shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f)
                            )
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    NodeStars(count = stars[i], s = s)
                }
            }
        }
    }
}

@Composable
private fun NodeStars(count: Int, s: Float) {
    Row {
        for (k in 1..3) {
            val scale = remember { Animatable(if (count >= k) 0.01f else 1f) }
            LaunchedEffect(count) {
                if (count >= k) {
                    kotlinx.coroutines.delay(120L * k)
                    scale.animateTo(1f, spring(dampingRatio = 0.45f))
                }
            }
            Text(
                "⭐", fontSize = (11f * s).sp,
                modifier = Modifier.graphicsLayer {
                    scaleX = scale.value; scaleY = scale.value
                    alpha = if (count >= k) 1f else 0.25f
                }
            )
        }
    }
}
