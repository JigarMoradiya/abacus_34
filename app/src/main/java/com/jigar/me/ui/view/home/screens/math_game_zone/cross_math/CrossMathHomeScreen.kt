package com.jigar.me.ui.view.home.screens.math_game_zone.cross_math

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton

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
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
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
import androidx.compose.ui.unit.Dp
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
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.jigar.me.ui.jetpack.utils.ui.extensions.appScale
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abBlended
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.screens.math_game_zone.GameCategoryType
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBounce
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abPopIn
import com.jigar.me.ui.view.home.screens.math_game_zone.gameCardStyle
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)

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
                color = ORANGE, style = AbTypography.fixed(22f * appScale(), FontWeight.ExtraBold),
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
                title = stringResource(R.string.cross_math),
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
        HowToPlayCrossMathView { showHelp = false }
    }
}

// Candy-path journey map (twin of iOS CrossMathHomeView.roadmap): a chunky winding road with one candy node per level,
// scrolling horizontally (the app is landscape-locked). The finished stretch of road is coloured in, the next level hops.
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
    val accent: Color = gameCardStyle(GameCategoryType.CROSS_MATH).light
    val accentDark: Color = gameCardStyle(GameCategoryType.CROSS_MATH).dark
    val reached = maxOf(nextLevel - 1, 0)

    // Scroll so the next-to-play node is comfortably in view.
    LaunchedEffect(nextLevel, stars) {
        val target = with(density) { (startPad + spacing * (nextLevel - 1) - spacing * 2).toPx() }
        scrollState.animateScrollTo(target.toInt().coerceAtLeast(0))
    }

    Box(modifier = modifier.horizontalScroll(scrollState)) {
        Box(Modifier.width(totalWidth).fillMaxHeight()) {

            fun nodeYFraction(i: Int): Float = sin(i * 0.9f)

            Canvas(modifier = Modifier.fillMaxSize()) {
                val cy = size.height / 2f
                val ampPx = amplitude.toPx()
                val nodeR = nodeSize.toPx() / 2f
                val step = spacing.toPx()
                val x0 = startPad.toPx() + nodeR
                // The road through the first `count` nodes (count = all of them for the whole road).
                fun road(count: Int): Path {
                    val path = Path()
                    for (i in 0 until count) {
                        val x = x0 + step * i
                        val y = cy + nodeYFraction(i) * ampPx
                        if (i == 0) path.moveTo(x, y) else {
                            val px = x0 + step * (i - 1)
                            val py = cy + nodeYFraction(i - 1) * ampPx
                            path.quadraticBezierTo((px + x) / 2f, (py + y) / 2f + 14.dp.toPx(), x, y)
                        }
                    }
                    return path
                }
                val whole = road(CrossMathConfig.LEVEL_COUNT)
                val wideStroke = (22f * s).dp.toPx()
                val roadStroke = (16f * s).dp.toPx()
                drawPath(whole, accentDark.copy(alpha = 0.32f), style = Stroke(width = wideStroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawPath(whole, Color.White.copy(alpha = 0.92f), style = Stroke(width = roadStroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
                // The part already walked is filled in with the game's colour.
                if (reached > 0) {
                    drawPath(
                        road(minOf(reached + 1, CrossMathConfig.LEVEL_COUNT)), accent,
                        style = Stroke(width = roadStroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
                drawPath(
                    whole, Color.White.copy(alpha = 0.85f),
                    style = Stroke(
                        width = (3.5f * s).dp.toPx(), cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.dp.toPx(), (11f * s).dp.toPx()))
                    )
                )
            }

            for (i in 0 until CrossMathConfig.LEVEL_COUNT) {
                val level = i + 1
                val done = stars[i] > 0
                val unlocked = level == 1 || stars[i - 1] > 0
                val isNext = unlocked && !done && level == nextLevel

                CrossMathRoadNode(
                    level = level, stars = stars[i], unlocked = unlocked, done = done, isNext = isNext,
                    size = nodeSize, s = s, doneFace = accent,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = startPad + spacing * i, y = amplitude * nodeYFraction(i)),
                    onTap = { onLevelClick(level) }
                )
            }
        }
    }
}

// One level on the candy road: a round candy node with a number (gold and hopping when it is the next level to play,
// game-coloured when done), or a muted node with a lock that wiggles when tapped. Three star badges sit below.
@Composable
private fun CrossMathRoadNode(
    level: Int,
    stars: Int,
    unlocked: Boolean,
    done: Boolean,
    isNext: Boolean,
    size: Dp,
    s: Float,
    doneFace: Color,
    modifier: Modifier = Modifier,
    onTap: () -> Unit
) {
    val colors = LocalAbColors.current
    val face: Color = when {
        !unlocked -> colors.textSecondary.abBlended(Color.White, 0.45f)
        done -> doneFace.abReadable(3f)
        isNext -> colors.accentYellow
        else -> colors.accentOrange.abReadable(3f)
    }
    val ink: Color = if (unlocked && isNext && !done) Color(0xFF3E2723) else Color.White

    val scope = rememberCoroutineScope()
    val wiggle = remember { Animatable(0f) }
    val interaction = remember { MutableInteractionSource() }
    val pressedRaw by interaction.collectIsPressedAsState()
    val press by animateFloatAsState(
        targetValue = if (pressedRaw && unlocked) 0.9f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 600f),
        label = "crossMathRoadPress"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier.abPopIn(minOf(level * 30, 400))
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .clickable(interactionSource = interaction, indication = null) {
                    if (unlocked) {
                        onTap()
                    } else {
                        scope.launch {
                            wiggle.snapTo(0f)
                            wiggle.animateTo(1f, tween(500, easing = LinearEasing))
                        }
                    }
                }
                .crossMathRoadHop(active = unlocked && isNext && !done, height = (7f * s).dp)
                .graphicsLayer { scaleX = press; scaleY = press }
                .drawBehind {
                    val w = this.size.width
                    val h = this.size.height
                    val d = w - (4f * s).dp.toPx()
                    val r = d / 2f
                    val lift = (2f * s).dp.toPx()
                    // soft ground shadow under the node
                    drawOval(
                        Color.Black.copy(alpha = 0.12f),
                        topLeft = Offset(w * 0.1f, h - h * 0.08f),
                        size = Size(w * 0.8f, h * 0.16f)
                    )
                    // the candy's 3D edge, then the face with its gloss and white rim
                    drawCircle(face.abDarkened(0.3f), radius = r, center = Offset(w / 2f, h / 2f + lift))
                    val c = Offset(w / 2f, h / 2f - lift)
                    drawCircle(face, radius = r, center = c)
                    drawCircle(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.4f), Color.Transparent),
                            startY = c.y - r, endY = c.y
                        ),
                        radius = r, center = c
                    )
                    drawCircle(Color.White, radius = r - 1.5.dp.toPx(), center = c, style = Stroke(width = 3.dp.toPx()))
                },
        ) {
            if (unlocked) {
                Text(
                    text = "$level",
                    style = AbTypography.fixed(18f * s, FontWeight.Black),
                    color = ink,
                    modifier = Modifier.offset(y = (-2f * s).dp)
                )
            } else {
                Icon(
                    Icons.Filled.Lock, contentDescription = null, tint = Color.White,
                    modifier = Modifier
                        .offset(y = (-2f * s).dp)
                        .size((19f * s).dp)
                        .graphicsLayer { rotationZ = (kotlin.math.sin(wiggle.value * PI * 4) * 23.0).toFloat() }
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(1.dp), verticalAlignment = Alignment.CenterVertically) {
            for (k in 1..3) {
                CrossMathRoadStar(
                    earned = stars >= k,
                    size = ((if (k == 2) 17f else 13f) * s).dp,
                    lift = if (k == 2) (2f * s).dp else 0.dp
                )
            }
        }
    }
}

// A gold star badge (a ghost grey one while not earned) that pops when it is earned.
@Composable
private fun CrossMathRoadStar(earned: Boolean, size: Dp, lift: Dp) {
    val colors = LocalAbColors.current
    Box(Modifier.offset(y = -lift).abBounce(earned, 1.5f), contentAlignment = Alignment.Center) {
        if (earned) {
            Icon(
                Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFB8C00).copy(alpha = 0.6f),
                modifier = Modifier.size(size).offset(y = 1.dp)
            )
        }
        Icon(
            Icons.Filled.Star, contentDescription = null,
            tint = if (earned) colors.accentYellow else Color.Gray.copy(alpha = 0.35f),
            modifier = Modifier.size(size)
        )
    }
}

// A little hop-and-squash loop for the next level to play (pivot at the bottom). Changes only how it is drawn.
private fun Modifier.crossMathRoadHop(active: Boolean, height: Dp): Modifier = composed {
    if (!active) return@composed this
    val transition = rememberInfiniteTransition(label = "crossMathRoadHop")
    val phase by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(667, easing = LinearEasing), RepeatMode.Restart),
        label = "crossMathRoadHopPhase"
    )
    graphicsLayer {
        val lift = 4f * phase * (1f - phase)
        transformOrigin = TransformOrigin(0.5f, 1f)
        scaleX = 1f + 0.07f * (1f - lift)
        scaleY = 1f - 0.07f * (1f - lift)
        translationY = -lift * height.toPx()
    }
}
