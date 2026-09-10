package com.jigar.me.ui.view.home.screens.math_game_zone.number_path

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.home.screens.math_game_zone.common.PauseTimerInBackground
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.number_path.components.NumberPathUiState
import com.jigar.me.ui.view.home.screens.math_game_zone.number_path.components.PathCellType
import com.jigar.me.ui.view.home.screens.math_game_zone.number_path.viewmodel.NumberPathPlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.getButtonColors
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)
private val BLUE = Color(0xFF0074D5)
private val TILE_BG = Color.White
private val TILE_VISITED = Color(0xFFA5D6A7)
private val TILE_START = Color(0xFF90CAF9)
private val TILE_GOAL = Color(0xFFFFD54F)
private val TILE_TEXT = Color(0xFF3E2723)
private val HINT_BORDER = Color(0xFFFFB300)

@Composable
fun NumberPathPlayScreen(
    viewModel: NumberPathPlayViewModel,
    onBackClick: () -> Unit,
    onNextLevel: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val s = gameScale()
    LaunchedEffect(Unit) { viewModel.start() }
    // A phone call or the home button freezes the clock - fair play.
    PauseTimerInBackground { viewModel.setTimerPaused(it) }

    // Roomy tiles: start/goal stack an emoji over text, which clipped on
    // smaller devices with the old sizes — and landscape has the space.
    val cellBase = when {
        state.cols >= 9 -> 46f
        state.cols >= 8 -> 50f
        state.cols >= 7 -> 54f
        else -> 62f
    }
    val cell = (cellBase * s).dp

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BackButtonWithText(title = stringResource(R.string.number_path_game), onBackClick = onBackClick)
                    Spacer(Modifier.weight(1f))
                }
                Text(
                    text = viewModel.difficulty.displayName + " · " +
                        stringResource(R.string.cross_math_level, viewModel.level),
                    color = ORANGE, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 20.sp.scaled(),
                    modifier = Modifier.align(Alignment.Center)
                        .clip(RoundedCornerShape(100f)).background(Color.White.copy(alpha = 0.9f))
                        .border(2.dp, ORANGE_BORDER, RoundedCornerShape(100f))
                        .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens8)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6),
                    modifier = Modifier.align(Alignment.CenterEnd)
                        .padding(end = AppDimens.Dimens16)
                        .clip(RoundedCornerShape(100f)).background(Color.White.copy(alpha = 0.9f))
                        .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
                ) {
                    Text("⏱", fontSize = (24f * s).sp)
                    Text(
                        timeString(state.elapsed), color = Color(0xFF5D4037),
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 26.sp.scaled()
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    PathGrid(state, cell, s, viewModel.buddy) { viewModel.tapCell(it) }
                }

                Spacer(Modifier.width((32f * s).dp))

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    TotalsPanel(state, s, viewModel.buddy)
                    Spacer(Modifier.height(AppDimens.Dimens12))
                    ToolBar(state, cell, s,
                        onUndo = { viewModel.undo() },
                        onRestart = { viewModel.restart() },
                        onHint = { viewModel.useHint() })
                }
            }
        }

        if (state.isGameOver) {
            ResultOverlay(
                buddy = viewModel.buddy,
                stars = state.starsEarned,
                elapsed = state.elapsed,
                hintsUsed = state.hintsUsed,
                hasNextLevel = viewModel.hasNextLevel,
                onNextLevel = onNextLevel,
                onPlayAgain = { viewModel.start() },
                onBack = onBackClick
            )
        }
    }
}

// ── maze grid + hopping buddy ───────────────────────────────────────────────

@Composable
private fun PathGrid(state: NumberPathUiState, cell: Dp, s: Float, buddy: String, onTap: (Int) -> Unit) {
    if (state.cells.isEmpty()) return
    val density = LocalDensity.current
    // Tiles lay out at rounded px sizes — the buddy must use the same
    // rounded step or it drifts off-center on wide grids.
    val cellPx = with(density) { cell.roundToPx() }.toFloat()

    Box {
        Column {
            for (r in 0 until state.rows) {
                Row {
                    for (c in 0 until state.cols) {
                        PathTile(state, r * state.cols + c, cell, s, onTap)
                    }
                }
            }
        }

        // The buddy hops from tile to tile with a springy scale bounce, and
        // shakes when the goal bounces a wrong total back.
        val current = state.currentCell
        val targetOffset = Offset(
            (current % state.cols) * cellPx + cellPx / 2f,
            (current / state.cols) * cellPx + cellPx / 2f
        )
        val pos = remember { Animatable(targetOffset, Offset.VectorConverter) }
        val hop = remember { Animatable(1f) }
        LaunchedEffect(current) {
            hop.snapTo(1.25f)
            pos.animateTo(targetOffset, spring(dampingRatio = 0.6f, stiffness = 300f))
            hop.animateTo(1f, spring(dampingRatio = 0.4f))
        }
        val shake = remember { Animatable(0f) }
        LaunchedEffect(state.goalBounceCount, state.blockedBounceCount) {
            if (state.goalBounceCount > 0 || state.blockedBounceCount > 0) {
                repeat(3) { shake.animateTo(7f, tween(45)); shake.animateTo(-7f, tween(45)) }
                shake.animateTo(0f, tween(40))
            }
        }

        val buddySize = cell * 0.8f
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .offset {
                    IntOffset(
                        (pos.value.x - with(density) { buddySize.toPx() } / 2f + shake.value).roundToInt(),
                        (pos.value.y - with(density) { buddySize.toPx() } / 2f).roundToInt()
                    )
                }
                .size(buddySize)
                .graphicsLayer { scaleX = hop.value; scaleY = hop.value }
        ) {
            Text(buddy, fontSize = (26f * s).sp)
            // Little total bubble riding on the buddy's head.
            Text(
                "${state.currentTotal}",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (11f * s).sp,
                modifier = Modifier.align(Alignment.TopCenter)
                    .offset(y = (-6f * s).dp)
                    .clip(RoundedCornerShape(100f)).background(BLUE)
                    .padding(horizontal = (5f * s).dp, vertical = 1.dp)
            )
        }
    }
}

@Composable
private fun PathTile(state: NumberPathUiState, i: Int, cell: Dp, s: Float, onTap: (Int) -> Unit) {
    val data = state.cells.getOrNull(i) ?: return
    if (data.type == PathCellType.WALL) {
        Spacer(Modifier.size(cell))
        return
    }
    val visited = i in state.trail
    val isHint = state.hintIndex == i

    // Hinted tile pulses until the kid moves. The infinite transition only
    // exists while this tile is the hint — other tiles stay animation-free.
    val pulse = if (isHint) {
        rememberInfiniteTransition(label = "hint").animateFloat(
            initialValue = 1f, targetValue = 1.12f,
            animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse), label = "hint"
        ).value
    } else 1f

    val bg = when {
        state.justSolved && data.type == PathCellType.GOAL -> Color(0xFF81C784)
        data.type == PathCellType.START -> TILE_START
        data.type == PathCellType.GOAL -> TILE_GOAL
        visited -> TILE_VISITED
        else -> TILE_BG
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(cell)
            .padding(2.dp)
            .graphicsLayer { if (isHint) { scaleX = pulse; scaleY = pulse } }
            .clip(RoundedCornerShape((10f * s).dp))
            .background(bg)
            .border(
                width = if (isHint) 3.dp else 1.5.dp,
                color = if (isHint) HINT_BORDER else Color(0xFFBCAAA4),
                shape = RoundedCornerShape((10f * s).dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onTap(i) }
    ) {
        when (data.type) {
            PathCellType.START -> Text(
                "${state.startValue}", color = TILE_TEXT,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (18f * s).sp
            )
            PathCellType.GOAL -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎯", fontSize = (13f * s).sp)
                Text(
                    "${state.target}", color = TILE_TEXT, maxLines = 1, softWrap = false,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                    fontSize = ((if (state.target >= 100) 13f else 16f) * s).sp
                )
            }
            PathCellType.OP -> Text(
                data.displayText, color = if (visited) Color(0xFF1B5E20) else TILE_TEXT,
                maxLines = 1, softWrap = false,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (15f * s).sp
            )
            else -> {}
        }
    }
}

// ── right panel: target + animated running total ───────────────────────────

@Composable
private fun TotalsPanel(state: NumberPathUiState, s: Float, buddy: String) {
    // The running total pops with a bounce every time it changes.
    val popScale = remember { Animatable(1f) }
    LaunchedEffect(state.totals.size) {
        popScale.snapTo(1.35f)
        popScale.animateTo(1f, spring(dampingRatio = 0.45f))
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens10),
        modifier = Modifier.clip(RoundedCornerShape(AppDimens.Dimens16)).background(Color.White.copy(alpha = 0.8f))
            .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens16)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6), verticalAlignment = Alignment.CenterVertically) {
            Text("🎯", fontSize = (20f * s).sp)
            Text(
                "${state.target}", color = ORANGE,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (26f * s).sp
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6), verticalAlignment = Alignment.CenterVertically) {
            Text(buddy, fontSize = (20f * s).sp)
            Text(
                "${state.currentTotal}",
                color = if (state.currentTotal == state.target) Color(0xFF2E7D32) else BLUE,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (26f * s).sp,
                modifier = Modifier.graphicsLayer { scaleX = popScale.value; scaleY = popScale.value }
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6), verticalAlignment = Alignment.CenterVertically) {
            Text("💡", fontSize = (18f * s).sp)
            Text(
                "${state.hintsLeft}", color = Color(0xFFF57F17),
                fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (20f * s).sp
            )
        }
    }
}

// ── toolbar ─────────────────────────────────────────────────────────────────

@Composable
private fun ToolBar(
    state: NumberPathUiState, cell: Dp, s: Float,
    onUndo: () -> Unit, onRestart: () -> Unit, onHint: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12), verticalAlignment = Alignment.CenterVertically) {
        ToolButton(bg = Color(0xFF90A4AE), enabled = state.trail.size > 1, size = cell, onClick = onUndo) {
            Icon(Icons.AutoMirrored.Rounded.Undo, contentDescription = null, tint = Color.White,
                modifier = Modifier.size((22f * s).dp))
        }
        ToolButton(bg = Color(0xFFFFB74D), enabled = true, size = cell, onClick = onRestart) {
            Icon(Icons.Rounded.Refresh, contentDescription = null, tint = Color.White,
                modifier = Modifier.size((22f * s).dp))
        }
        ToolButton(bg = Color(0xFFFFCA28), enabled = state.hintsLeft > 0, size = cell, onClick = onHint) {
            Text("💡${state.hintsLeft}", color = Color.White,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (16f * s).sp)
        }
    }
}

@Composable
private fun ToolButton(bg: Color, enabled: Boolean, size: Dp, onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .graphicsLayer { alpha = if (enabled) 1f else 0.4f }
            .size(size * 0.9f)
            .clip(RoundedCornerShape(AppDimens.Dimens10))
            .background(bg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, enabled = enabled
            ) { onClick() }
    ) { content() }
}

// ── result overlay ──────────────────────────────────────────────────────────

private fun timeString(seconds: Int): String = "%d:%02d".format(seconds / 60, seconds % 60)

@Composable
private fun ResultOverlay(
    buddy: String,
    stars: Int, elapsed: Int, hintsUsed: Int, hasNextLevel: Boolean,
    onNextLevel: () -> Unit, onPlayAgain: () -> Unit, onBack: () -> Unit
) {
    val accentColors = getButtonColors(ButtonType.ORANGE)
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        // Finishing a Number Path level is always a win — always celebrate.
        LaunchedEffect(Unit) { if (stars >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        // "Candy Pop" celebration card — saturated gradient block with a white badge overlapping the top edge.
        Box(modifier = Modifier.fillMaxWidth(0.6f), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp, RoundedCornerShape(AppDimens.Dimens20 * 1.2f), ambientColor = accentColors.base, spotColor = accentColors.base)
                    .background(accentColors.gradient, RoundedCornerShape(AppDimens.Dimens20 * 1.2f))
                    .padding(horizontal = AppDimens.Dimens30, vertical = AppDimens.Dimens20),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
            ) {
                Spacer(Modifier.height(AppDimens.Dimens20))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(buddy, fontSize = (28f * gameScale()).sp)
                    Text(
                        stringResource(R.string.balloon_great_job),
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                        fontSize = 30.sp.scaled(),
                        style = TextStyle(shadow = Shadow(color = accentColors.base.copy(alpha = 0.9f), offset = Offset(1.5f, 1.5f), blurRadius = 0f)),
                        modifier = Modifier.padding(horizontal = AppDimens.Dimens8)
                    )
                    Text("✨", fontSize = (28f * gameScale()).sp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8), verticalAlignment = Alignment.Bottom) {
                    BigStar(stars >= 1, 32, 150); BigStar(stars >= 2, 40, 400); BigStar(stars >= 3, 32, 650)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16)) {
                    StatChipStr("⏱", timeString(elapsed), Color(0xFF5D4037))
                    StatChipStr("💡", "$hintsUsed", Color(0xFFF57F17))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    if (hasNextLevel) {
                        KidsActionButton(text = stringResource(R.string.cross_math_next_level), type = ButtonType.ORANGE,
                            onClick = { if (enabled) onNextLevel() })
                    }
                    KidsActionButton(text = stringResource(R.string.balloon_play_again), type = ButtonType.POSITIVE,
                        onClick = { if (enabled) onPlayAgain() })
                    KidsActionButton(text = stringResource(R.string.balloon_back), type = ButtonType.NEGATIVE,
                        onClick = { if (enabled) onBack() })
                }
            }

            Box(
                modifier = Modifier
                    .offset(y = (-28).dp)
                    .size(64.dp)
                    .shadow(8.dp, CircleShape)
                    .background(Color.White, CircleShape)
                    .border(4.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(buddy, fontSize = 32.sp)
            }
        }
    }
}

@Composable
private fun StatChipStr(emoji: String, value: String, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4), verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clip(RoundedCornerShape(100f)).background(Color.White)
            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6)) {
        Text(emoji, fontSize = (16f * gameScale()).sp)
        Text(value, color = color, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 20.sp.scaled(),
            maxLines = 1, softWrap = false)
    }
}

@Composable
private fun BigStar(earned: Boolean, sizeSp: Int, delayMs: Long) {
    val scale = remember { Animatable(0.01f) }
    LaunchedEffect(Unit) { delay(delayMs); scale.animateTo(1f, spring(dampingRatio = 0.5f)) }
    Text("⭐", fontSize = (sizeSp * gameScale()).sp,
        modifier = Modifier.graphicsLayer { scaleX = scale.value; scaleY = scale.value; alpha = if (earned) 1f else 0.35f })
}
