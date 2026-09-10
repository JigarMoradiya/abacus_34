package com.jigar.me.ui.view.home.screens.math_game_zone.merge2048

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameMascotPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.randomGameCheer
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.merge2048.components.Merge2048Direction
import com.jigar.me.ui.view.home.screens.math_game_zone.merge2048.components.Merge2048Palette
import com.jigar.me.ui.view.home.screens.math_game_zone.merge2048.components.Merge2048Tile
import com.jigar.me.ui.view.home.screens.math_game_zone.merge2048.viewmodel.Merge2048PlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.getButtonColors
import kotlinx.coroutines.delay
import kotlin.math.abs

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)
private val BLUE = Color(0xFF0074D5)
private val GREEN = Color(0xFF2E7D32)
private val BOARD_BG = Color(0xFFBBADA0)

private val mScale: Float
    get() = if (DeviceInfo.isLargeTablet) 1.7f else if (DeviceInfo.isTablet) 1.45f else 1.0f

@Composable
fun Merge2048PlayScreen(viewModel: Merge2048PlayViewModel, onBackClick: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val baseCheer = remember { randomGameCheer() }
    val s = mScale
    val boardSide = (300f * s).dp
    val gap = (6f * s).dp
    val cell = (boardSide - gap * (state.size + 1)) / state.size

    var showResume by remember { mutableStateOf(false) }
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!started) {
            started = true
            if (viewModel.hasSavedGame()) showResume = true else viewModel.start()
        }
    }
    DisposableEffect(Unit) { onDispose { viewModel.saveGame() } }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BackButtonWithText(title = str(R.string.merge2048_game), onBackClick = onBackClick)
                    Spacer(Modifier.weight(1f))
                }
                Text(
                    text = str(R.string.merge2048_prompt, state.target),
                    color = ORANGE, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 20.sp.scaled(),
                    modifier = Modifier.align(Alignment.Center)
                        .clip(RoundedCornerShape(100f)).background(Color.White.copy(alpha = 0.9f))
                        .border(2.dp, ORANGE_BORDER, RoundedCornerShape(100f))
                        .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens8)
                )
            }

            Row(modifier = Modifier.fillMaxWidth().weight(1f), verticalAlignment = Alignment.CenterVertically) {
                GameMascotPanel(
                    cheer = when {
                        viewModel.highestTile >= viewModel.target -> "On fire! 🔥"
                        else -> baseCheer
                    },
                    celebrate = state.milestoneValue != null, s = s,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )

                BoardView(state.tiles, state.size, boardSide, cell, gap) { dir -> viewModel.move(dir) }

                Scoreboard(state.score, state.target, viewModel.bestScore, s, Modifier.weight(1f).fillMaxHeight())
            }
        }

        state.milestoneValue?.let { m -> MilestonePopup(m, state.target, s) { viewModel.dismissMilestone() } }

        if (showResume) {
            ResumePopup(s,
                onContinue = { showResume = false; viewModel.resumeSavedGame() },
                onNew = { showResume = false; viewModel.clearSave(); viewModel.start() })
        }

        if (state.isGameOver) {
            ResultOverlay(state.score, state.didWin, viewModel.highestTile, viewModel.target, viewModel.bestScore,
                onPlayAgain = { viewModel.start() }, onBack = onBackClick)
        }
    }
}

@Composable
private fun MilestonePopup(value: Int, nextTarget: Int, s: Float, onContinue: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
            modifier = Modifier.fillMaxWidth(0.6f).clip(RoundedCornerShape(AppDimens.Dimens20)).background(Color.White)
                .border(3.dp, ORANGE_BORDER, RoundedCornerShape(AppDimens.Dimens20))
                .padding(horizontal = AppDimens.Dimens30, vertical = AppDimens.Dimens24)
        ) {
            Text("🎉", fontSize = (54f * s).sp)
            Text(str(R.string.merge2048_target_title), color = BLUE,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 28.sp.scaled())
            Text(str(R.string.merge2048_reached, value), color = GREEN,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 22.sp.scaled())
            Text(str(R.string.merge2048_next, nextTarget), color = ORANGE,
                fontFamily = FontFamily(Font(R.font.font_bold)), fontSize = 16.sp.scaled())
            Text(str(R.string.merge2048_target_hint), color = Color.Black.copy(alpha = 0.65f),
                fontFamily = FontFamily(Font(R.font.font_medium)), fontSize = 14.sp.scaled())
            KidsActionButton(text = str(R.string.keep_going), type = ButtonType.ORANGE, onClick = onContinue)
        }
    }
}

@Composable
private fun ResumePopup(s: Float, onContinue: () -> Unit, onNew: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens16),
            modifier = Modifier.fillMaxWidth(0.6f).clip(RoundedCornerShape(AppDimens.Dimens20)).background(Color.White)
                .border(3.dp, ORANGE_BORDER, RoundedCornerShape(AppDimens.Dimens20))
                .padding(horizontal = AppDimens.Dimens30, vertical = AppDimens.Dimens24)
        ) {
            Text("👋", fontSize = (44f * s).sp)
            Text(str(R.string.resume_title), color = BLUE,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 26.sp.scaled())
            Text(str(R.string.resume_message), color = Color.Black.copy(alpha = 0.7f),
                fontFamily = FontFamily(Font(R.font.font_medium)), fontSize = 15.sp.scaled())
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                KidsActionButton(text = str(R.string.continue_game), type = ButtonType.ORANGE, onClick = onContinue)
                KidsActionButton(text = str(R.string.new_game), type = ButtonType.BLUE, onClick = onNew)
            }
        }
    }
}

@Composable
private fun BoardView(
    tiles: List<Merge2048Tile>, size: Int, boardSide: Dp, cell: Dp, gap: Dp,
    onSwipe: (Merge2048Direction) -> Unit
) {
    Box(
        modifier = Modifier
            .size(boardSide)
            .clip(RoundedCornerShape(AppDimens.Dimens12))
            .background(BOARD_BG)
            .pointerInput(size) {
                var total = Offset.Zero
                detectDragGestures(
                    onDragStart = { total = Offset.Zero },
                    onDragEnd = {
                        if (abs(total.x) > abs(total.y))
                            onSwipe(if (total.x > 0) Merge2048Direction.RIGHT else Merge2048Direction.LEFT)
                        else
                            onSwipe(if (total.y > 0) Merge2048Direction.DOWN else Merge2048Direction.UP)
                    },
                    onDrag = { change, amt -> total += amt; change.consume() }
                )
            }
    ) {
        for (i in 0 until size * size) {
            Box(
                modifier = Modifier
                    .offset(gap + (cell + gap) * (i % size), gap + (cell + gap) * (i / size))
                    .size(cell)
                    .clip(RoundedCornerShape(AppDimens.Dimens8))
                    .background(Color.White.copy(alpha = 0.25f))
            )
        }
        tiles.forEach { tile -> key(tile.id) { MergeTile(tile, cell, gap) } }
    }
}

@Composable
private fun MergeTile(tile: Merge2048Tile, cell: Dp, gap: Dp) {
    val x by animateDpAsState(gap + (cell + gap) * tile.col, tween(130), label = "")
    val y by animateDpAsState(gap + (cell + gap) * tile.row, tween(130), label = "")
    val pop = remember { Animatable(if (tile.isNew) 0.4f else 1f) }
    LaunchedEffect(tile.isNew) { if (tile.isNew) pop.animateTo(1f, spring(dampingRatio = 0.5f)) }
    LaunchedEffect(tile.justMerged) {
        if (tile.justMerged) { pop.animateTo(1.14f, tween(90)); pop.animateTo(1f, spring(dampingRatio = 0.55f)) }
    }
    // ≤4 digits show the full number; bigger values show as K/M (max 4 chars).
    // Font shrinks by label length so it always fits one line on a 7×7 board.
    val text = Merge2048Palette.label(tile.value)
    val fontFrac = when (text.length) {
        1 -> 0.46f
        2 -> 0.42f
        3 -> 0.34f
        else -> 0.29f   // 4 chars ("8192", "512K")
    }
    Box(
        modifier = Modifier
            .offset(x, y)
            .size(cell)
            .graphicsLayer { scaleX = pop.value; scaleY = pop.value }
            .clip(RoundedCornerShape(AppDimens.Dimens8))
            .background(Merge2048Palette.bg(tile.value)),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Merge2048Palette.fg(tile.value),
            fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (cell.value * fontFrac).sp,
            maxLines = 1, softWrap = false)
    }
}

@Composable
private fun Scoreboard(score: Int, target: Int, best: Int, s: Float, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens16),
            modifier = Modifier.clip(RoundedCornerShape(AppDimens.Dimens16)).background(Color.White.copy(alpha = 0.8f))
                .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens16)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6), verticalAlignment = Alignment.CenterVertically) {
                Text("⭐", fontSize = (22f * s).sp)
                Text("$score", color = BLUE, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (22f * s).sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6), verticalAlignment = Alignment.CenterVertically) {
                Text("🎯", fontSize = (22f * s).sp)
                Text("$target", color = ORANGE, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (22f * s).sp)
            }
            if (best > 0) {
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6), verticalAlignment = Alignment.CenterVertically) {
                    Text("🏆", fontSize = (22f * s).sp)
                    Text("$best", color = ORANGE_BORDER, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (22f * s).sp)
                }
            }
        }
    }
}

@Composable private fun str(id: Int): String = androidx.compose.ui.res.stringResource(id)
@Composable private fun str(id: Int, vararg args: Any): String = androidx.compose.ui.res.stringResource(id, *args)

@Composable
private fun ResultOverlay(score: Int, didWin: Boolean, highest: Int, goal: Int, best: Int, onPlayAgain: () -> Unit, onBack: () -> Unit) {
    val starCount = when { didWin -> 3; highest >= goal / 2 -> 2; else -> 1 }
    // A full win (reached the target tile) earns the stronger green celebration accent;
    // any other completion keeps the default orange.
    val accent = if (didWin) ButtonType.GREEN else ButtonType.ORANGE
    val accentColors = getButtonColors(accent)
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { if (starCount >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
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
                    Text("🔢", fontSize = (28f * gameScale()).sp)
                    Text(
                        str(if (didWin) R.string.balloon_great_job else R.string.good_try),
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                        fontSize = 30.sp.scaled(),
                        style = TextStyle(shadow = Shadow(color = accentColors.base.copy(alpha = 0.9f), offset = Offset(1.5f, 1.5f), blurRadius = 0f)),
                        modifier = Modifier.padding(horizontal = AppDimens.Dimens8)
                    )
                    Text("✨", fontSize = (28f * gameScale()).sp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8), verticalAlignment = Alignment.Bottom) {
                    BigStar(starCount >= 1, 32, 150); BigStar(starCount >= 2, 40, 400); BigStar(starCount >= 3, 32, 650)
                }
                Text(str(R.string.balloon_final_score) + ": $score", color = Color.White,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 26.sp.scaled())
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16)) {
                    StatChip("🔝", highest, GREEN); StatChip("🏆", best, ORANGE_BORDER)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    KidsActionButton(text = str(R.string.balloon_play_again), type = ButtonType.POSITIVE, onClick = { if (enabled) onPlayAgain() })
                    KidsActionButton(text = str(R.string.balloon_back), type = ButtonType.NEGATIVE, onClick = { if (enabled) onBack() })
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
                Text("🔢", fontSize = 32.sp)
            }
        }
    }
}

@Composable
private fun StatChip(emoji: String, value: Int, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4), verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clip(RoundedCornerShape(100f)).background(Color.White)
            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6)) {
        Text(emoji, fontSize = (16f * gameScale()).sp)
        Text("$value", color = color, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 20.sp.scaled())
    }
}

@Composable
private fun BigStar(earned: Boolean, sizeSp: Int, delayMs: Long) {
    val scale = remember { Animatable(0.01f) }
    LaunchedEffect(Unit) { delay(delayMs); scale.animateTo(1f, spring(dampingRatio = 0.5f)) }
    Text("⭐", fontSize = (sizeSp * gameScale()).sp,
        modifier = Modifier.graphicsLayer { scaleX = scale.value; scaleY = scale.value; alpha = if (earned) 1f else 0.35f })
}
