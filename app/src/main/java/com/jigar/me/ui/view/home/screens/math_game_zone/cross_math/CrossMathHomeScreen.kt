package com.jigar.me.ui.view.home.screens.math_game_zone.cross_math

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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.DifficultyPillBox
import com.jigar.me.ui.view.home.common_ui.DifficultyPillItem
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayCrossMathView
import com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.components.CrossMathConfig
import com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.components.CrossMathHomeViewModel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)
private val TEAL = Color(0xFF26A69A)
private val TEAL_DARK = Color(0xFF00796B)
private val NODE_LOCKED = Color(0xFFB0BEC5)
private val PATH_COLOR = Color(0xFF80CBC4)

@Composable
fun CrossMathHomeScreen(
    viewModel: CrossMathHomeViewModel,
    isSubscribed: Boolean,
    onLevelClick: (CommonDifficulty4, Int) -> Unit,
    onBackClick: () -> Unit
) {
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsStateWithLifecycle()
    val progressVersion by viewModel.progressVersion.collectAsStateWithLifecycle()
    var showHelp by remember { mutableStateOf(false) }
    val s = gameScale()

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
                text = stringResource(R.string.cross_math_tagline),
                color = ORANGE, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 22.sp.scaled(),
                modifier = Modifier.clip(RoundedCornerShape(100f)).background(Color.White.copy(alpha = 0.85f))
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
                title = stringResource(R.string.cross_math),
                modifier = Modifier.weight(1f), onBackClick = onBackClick
            )
            KidsActionButton(
                modifier = Modifier.padding(end = AppDimens.Dimens16),
                text = stringResource(R.string.how_to_play),
                icon = Icons.AutoMirrored.Filled.HelpOutline, type = ButtonType.PINK, isSmall = true,
                onClick = { showHelp = true }
            )
        }
    }

    AnimatedVisibility(visible = showHelp, enter = fadeIn(), exit = fadeOut()) {
        HowToPlayCrossMathView { showHelp = false }
    }
}

// Candy-Crush-style journey map: a dashed winding path with one node per
// level, scrolling horizontally (the app is landscape-locked).
@Composable
private fun RoadmapPath(
    stars: IntArray,
    nextLevel: Int,
    s: Float,
    modifier: Modifier = Modifier,
    onLevelClick: (Int) -> Unit
) {
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val nodeSize = (46f * s).dp
    val spacing = (76f * s).dp
    val startPad = (40f * s).dp
    val amplitude = (34f * s).dp
    val totalWidth = startPad * 2 + spacing * (CrossMathConfig.LEVEL_COUNT - 1) + nodeSize

    // Scroll so the next-to-play node is comfortably in view.
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
                for (i in 0 until CrossMathConfig.LEVEL_COUNT) {
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

            for (i in 0 until CrossMathConfig.LEVEL_COUNT) {
                val level = i + 1
                val done = stars[i] > 0
                val unlocked = level == 1 || stars[i - 1] > 0
                val isNext = unlocked && !done && level == nextLevel

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = startPad + spacing * i, y = amplitude * nodeYFraction(i))
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .graphicsLayer {
                                if (isNext) { scaleX = pulse; scaleY = pulse }
                            }
                            .size(nodeSize)
                            .clip(CircleShape)
                            .background(if (unlocked) (if (done) TEAL else ORANGE) else NODE_LOCKED)
                            .border(3.dp, Color.White, CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                enabled = unlocked
                            ) { onLevelClick(level) }
                    ) {
                        Text(
                            text = if (unlocked) "$level" else "🔒",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                            fontSize = (18f * s).sp
                        )
                    }
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
