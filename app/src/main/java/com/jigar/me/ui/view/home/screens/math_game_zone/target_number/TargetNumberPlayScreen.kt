package com.jigar.me.ui.view.home.screens.math_game_zone.target_number

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.ui.extensions.appScale
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.screens.math_game_zone.GameCategoryType
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBounce
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abPopIn
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abWobble
import com.jigar.me.ui.view.home.screens.math_game_zone.common.toyColor
import com.jigar.me.ui.view.home.screens.math_game_zone.gameCardStyle
import com.jigar.me.ui.view.home.screens.math_game_zone.target_number.components.TargetOperation
import com.jigar.me.ui.view.home.screens.math_game_zone.target_number.viewmodel.TargetNumberPlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.AppDimens.ToolbarIconSize
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.drawConfetti

// Target Number has no result screen by design: a solved puzzle shows its green banner, a burst of stars, confetti over the board and "New target" + "Home" buttons.
@Composable
fun TargetNumberPlayScreen(
    viewModel: TargetNumberPlayViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val colors = LocalAbColors.current
    val s = appScale()
    val screenW = LocalConfiguration.current.screenWidthDp.dp
    val screenH = LocalConfiguration.current.screenHeightDp.dp
    // confetti rains over the whole board while the puzzle is solved (it stays on the board: no extra screen)
    var confettiT by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(state.isSolvedCorrect) {
        if (state.isSolvedCorrect == true) {
            val start = withFrameNanos { it }
            while (true) { confettiT = (withFrameNanos { it } - start) / 1_000_000_000f }
        }
    }
    Box(Modifier.fillMaxSize()) {
    Row(modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)) {
        Box(
            modifier = Modifier.weight(0.6f),
            contentAlignment = Alignment.Center
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                BackButtonWithText(title = stringResource(R.string.complete_the_target), onBackClick = onBackClick)

                Spacer(modifier = Modifier.weight(1f))

                Column(
                    modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(
                        Dimens8,
                        Alignment.CenterVertically
                    ), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TargetNumberBanner(
                        target = state.target,
                        solved = state.isSolvedCorrect == true,
                        failed = state.isSolvedCorrect == false
                    )

                    if (state.isSolvedCorrect == true) {
                        state.message?.let {
                            Spacer(Modifier.height(Dimens8))
                            Text(
                                it,
                                style = AbTypography.fixed(22f * s, FontWeight.ExtraBold),
                                color = colors.accentGreenInk
                            )
                        }
                        Spacer(Modifier.height(ToolbarIconSize))
                    } else {
                        Text(
                            state.currentExpression.ifEmpty { " " },
                            style = AbTypography.fixed(20f * s, FontWeight.Black),
                            color = colors.brandPrimary,
                            modifier = Modifier.fillMaxWidth().abBounce(state.currentExpression, 1.12f),
                            textAlign = TextAlign.Center
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            state.numbers.forEachIndexed { idx, num ->

                                val isSelected = state.selectedNumberIndex == idx

                                TargetNumberChip(
                                    text = num.toString(),
                                    face = colors.toyColor(num),
                                    minWidth = AppDimens.Dimens64,
                                    height = AppDimens.Dimens56,
                                    selected = isSelected,
                                    disabled = state.isSolved,
                                    onClick = { viewModel.tapNumber(idx) },
                                    modifier = Modifier
                                        .abPopIn(min(idx * 50, 400))
                                        .abBounce(num, 1.2f)
                                )
                            }
                        }

                        Spacer(Modifier.height(if (DeviceInfo.isTablet)AppDimens.Dimens10 else AppDimens.Dimens6))

                        Row(horizontalArrangement = Arrangement.spacedBy(if (DeviceInfo.isTablet)AppDimens.Dimens10 else AppDimens.Dimens6)) {
                            state.allowedOps.forEachIndexed { idx, op ->
                                TargetNumberOpKey(
                                    symbol = op.symbol,
                                    face = targetNumberOpFace(op, colors.accentGreen, colors.accentOrange, colors.accentPurple, colors.brandSecondary),
                                    size = AppDimens.Dimens48,
                                    // The ViewModel keeps the chosen sign to itself; the live expression ends with it ("8 +").
                                    selected = state.currentExpression.endsWith(" " + op.symbol),
                                    disabled = state.isSolved,
                                    onClick = {
                                        if (state.isSolvedCorrect != true) {
                                            viewModel.tapOperation(op)
                                        }
                                    },
                                    modifier = Modifier.abPopIn(min(idx * 60, 400))
                                )
                            }
                        }
                    }
                }
            }

        }

        Box(
            modifier = Modifier.weight(0.4f),
            contentAlignment = Alignment.Center
        ) {

            Column(modifier = Modifier.padding(AppDimens.Dimens16), horizontalAlignment = Alignment.CenterHorizontally) {
                if (state.steps.isNotNullOrEmpty()) {
                    StepsLogSection(
                        steps = state.steps,
                        originalNumbers = state.originalNumbers.joinToString(separator = ", ")
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (state.isSolvedCorrect != true){
                    state.message?.let {
                        Text(
                            text = it,
                            style = AbTypography.fixed(16f * s, FontWeight.Bold),
                            color = if (colors.isDark) colors.danger else colors.danger.abReadable(4.5f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimens8)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(Dimens8)) {
                    if (state.isSolvedCorrect == true) {
                        GameButton(
                            text = stringResource(R.string.new_target),
                            type = ButtonType.ORANGE,
                            isSmall = true,
                            onClick = {
                                viewModel.generateNewPuzzle()
                            }
                        )
                        GameHomeButton(isSmall = true, onClick = onBackClick)
                    } else {
                        GameButton(
                            text = stringResource(R.string.hint_count, state.hintUsed, state.hintLimit),
                            icon = Icons.Filled.Lightbulb,
                            type = ButtonType.ORANGE,
                            isSmall = true,
                            onClick = {
                                if (state.hintUsed < state.hintLimit){
                                    viewModel.showHint()
                                }
                            }
                        )

                        GameButton(
                            text = stringResource(R.string.reset),
                            icon = Icons.Filled.Refresh,
                            type = ButtonType.PINK,
                            isSmall = true,
                            onClick = { viewModel.resetPuzzle() }
                        )

                    }
                }
            }
        }
    }
    if (state.isSolvedCorrect == true) {
        Canvas(Modifier.fillMaxSize()) { drawConfetti(confettiT, size.width, size.height) }
    }
    }
}

// One candy colour per operation: add green, subtract orange, multiply purple, divide sky blue.
private fun targetNumberOpFace(op: TargetOperation, green: Color, orange: Color, purple: Color, blue: Color): Color {
    val base = when (op) {
        TargetOperation.ADD -> green
        TargetOperation.SUBTRACT -> orange
        TargetOperation.MULTIPLY -> purple
        TargetOperation.DIVIDE -> blue
    }
    return base.abReadable(3f)
}

@Composable
fun StepsLogSection(
    steps: List<String>,
    originalNumbers: String
) {
    val colors = LocalAbColors.current
    val s = appScale()
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(R.string.all_steps),
            style = AbTypography.fixed(18f * s, FontWeight.Black),
            color = if (colors.isDark) colors.danger else colors.danger.abReadable(4.5f)
        )

        Spacer(Modifier.height(Dimens8))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = AppDimens.Dimens1)
        ) {

            // The numbers the puzzle started with, in a blue pill.
            Text(
                text = originalNumbers,
                style = AbTypography.fixed(18f * s, FontWeight.ExtraBold),
                color = colors.textPrimary,
                modifier = Modifier
                    .background(colors.brandSecondary.copy(alpha = 0.18f), CircleShape)
                    .border(2.dp, colors.brandSecondary.copy(alpha = 0.7f), CircleShape)
                    .padding(horizontal = AppDimens.Dimens10, vertical = 2.dp)
            )

            if (steps.isNotEmpty()) {
                TargetNumberStepArrow()
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(steps) { index, step ->

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = AppDimens.Dimens1)
                ) {

                    // Every finished step is a green pill that pops in.
                    Text(
                        text = step,
                        style = AbTypography.fixed(18f * s, FontWeight.Bold),
                        color = colors.textPrimary,
                        modifier = Modifier
                            .abPopIn()
                            .background(colors.accentGreen.copy(alpha = 0.16f), CircleShape)
                            .border(2.dp, colors.accentGreen.copy(alpha = 0.7f), CircleShape)
                            .padding(horizontal = AppDimens.Dimens10, vertical = 2.dp)
                    )

                    if (index < steps.size - 1) {
                        TargetNumberStepArrow()
                    }
                }
            }
        }
    }
}

@Composable
private fun TargetNumberStepArrow() {
    val colors = LocalAbColors.current
    Icon(
        painter = painterResource(R.drawable.ic_arrow_down),
        contentDescription = null,
        tint = colors.accentOrange,
        modifier = Modifier.size(AppDimens.Dimens16)
    )
}

// ── Toy pieces ────────────────────────────────────────────────────────────────────────────────────────────────────────

/** Seconds since the screen showed, ticking every frame. Read it inside `graphicsLayer {}` / draw blocks so only the layer redraws. */
@Composable
internal fun rememberTargetNumberTime(): State<Float> {
    val time = remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) { withFrameNanos { now -> time.value = (now - start) / 1_000_000_000f } }
    }
    return time
}

/** A five-pointed star as a path, centred on ([cx], [cy]) with tips [outer] away. */
internal fun targetNumberStarPath(cx: Float, cy: Float, outer: Float): Path {
    val path = Path()
    val inner = outer * 0.5f
    for (i in 0 until 10) {
        val r = if (i % 2 == 0) outer else inner
        val a = -PI / 2.0 + i * PI / 5.0
        val x = cx + (cos(a) * r).toFloat()
        val y = cy + (sin(a) * r).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

/**
 * The target banner: a chunky candy tile that bobs gently. It pops when a new target arrives, turns green and bursts
 * into stars when the puzzle is solved, and wobbles when the final number misses.
 */
@Composable
private fun TargetNumberBanner(target: Int, solved: Boolean, failed: Boolean) {
    val colors = LocalAbColors.current
    val time = rememberTargetNumberTime()
    val face = if (solved) colors.accentGreen.abReadable(3f) else gameCardStyle(GameCategoryType.TARGET_NUMBER).light.abReadable(3f)
    val burst = remember { Animatable(0f) }
    LaunchedEffect(solved) {
        if (solved) burst.animateTo(1f, tween(900, easing = LinearOutSlowInEasing)) else burst.snapTo(0f)
    }
    val burstA = colors.accentYellow
    val burstB = colors.accentPink

    Box(
        Modifier
            .abWobble(failed)
            .graphicsLayer { translationY = sin(time.value * 2f) * 2f * density }
    ) {
        Box(
            Modifier.abToyTile(face, 22.dp, 4.dp).padding(horizontal = AppDimens.Dimens20),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(R.string.target_banner, target.toString()),
                style = AbTypography.fixed(40f * appScale(), FontWeight.Black).copy(shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f)),
                color = Color.White,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.abBounce(target, 1.1f)
            )
        }
        // A ring of stars flying outward when the puzzle is solved.
        Canvas(Modifier.matchParentSize()) {
            val p = burst.value
            if (p > 0f) {
                val fade = min(1f, p * 6f) * (1f - p)
                val cx = size.width / 2f
                val cy = size.height / 2f
                val radius = 90f * density
                for (i in 0 until 8) {
                    val angle = i / 8.0 * 2.0 * PI
                    val x = cx + (cos(angle) * radius * p).toFloat()
                    val y = cy + (sin(angle) * radius * p).toFloat()
                    val tint = if (i % 2 == 0) burstA else burstB
                    drawPath(targetNumberStarPath(x, y, 7f * (0.5f + p) * density), tint.copy(alpha = fade))
                }
            }
        }
    }
}

/** A faint white halo behind a chosen key (the iOS glow), drawn as a few soft rounded outlines. */
private fun Modifier.targetNumberGlow(strength: State<Float>, corner: Dp): Modifier = this.drawBehind {
    val a = strength.value
    if (a > 0.01f) {
        for (k in 1..4) {
            val grow = k * 2.dp.toPx()
            drawRoundRect(
                Color.White.copy(alpha = 0.24f * a * (5 - k) / 4f),
                Offset(-grow, -grow),
                Size(size.width + grow * 2f, size.height + grow * 2f),
                CornerRadius(corner.toPx() + grow)
            )
        }
    }
}

/** A number as a candy block. A picked number lifts, grows and gets a white ring; a dimmed one is out of play. */
@Composable
private fun TargetNumberChip(
    text: String,
    face: Color,
    minWidth: Dp,
    height: Dp,
    selected: Boolean,
    disabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else if (selected) 1.12f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "targetChipScale"
    )
    val lift by animateFloatAsState(
        targetValue = if (selected) -3f else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "targetChipLift"
    )
    val glow = animateFloatAsState(targetValue = if (selected) 1f else 0f, label = "targetChipGlow")
    val corner = height * 0.38f
    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale; translationY = lift * density; alpha = if (disabled) 0.5f else 1f }
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .targetNumberGlow(glow, corner)
            .abToyTile(face, corner, 4.dp)
            .defaultMinSize(minWidth = minWidth, minHeight = height)
            .then(if (selected) Modifier.border(3.dp, Color.White, RoundedCornerShape(corner)) else Modifier)
            .padding(horizontal = height * 0.3f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            style = AbTypography.fixed(height.value * 0.58f, FontWeight.Black).copy(shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f)),
            color = Color.White,
            maxLines = 1,
            softWrap = false
        )
    }
}

/** An operator as a chunky round key. The picked operator lifts, grows and gets a white ring. */
@Composable
private fun TargetNumberOpKey(
    symbol: String,
    face: Color,
    size: Dp,
    selected: Boolean,
    disabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else if (selected) 1.12f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "targetOpScale"
    )
    val lift by animateFloatAsState(
        targetValue = if (selected) -3f else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "targetOpLift"
    )
    val glow = animateFloatAsState(targetValue = if (selected) 1f else 0f, label = "targetOpGlow")
    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale; translationY = lift * density; alpha = if (disabled) 0.5f else 1f }
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .targetNumberGlow(glow, size / 2f)
            .abToyTile(face, size / 2f, 4.dp)
            .size(size)
            .then(if (selected) Modifier.border(3.dp, Color.White, CircleShape) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            symbol,
            style = AbTypography.fixed(size.value * 0.66f, FontWeight.Black).copy(shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f)),
            color = Color.White,
            maxLines = 1,
            softWrap = false
        )
    }
}
