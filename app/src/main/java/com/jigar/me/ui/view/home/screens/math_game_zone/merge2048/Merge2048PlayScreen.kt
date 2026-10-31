package com.jigar.me.ui.view.home.screens.math_game_zone.merge2048

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameCharacter

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton

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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.PlayArrow
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.ab.AbColors
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abBlended
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.screens.math_game_zone.GameCategoryType
import com.jigar.me.ui.view.home.screens.math_game_zone.common.AbDebugFlags
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameMascotPanel
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameResultCard
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameStatChip
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBounce
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBreathe
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abPopIn
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.randomGameCheer
import com.jigar.me.ui.view.home.screens.math_game_zone.common.toyColor
import com.jigar.me.ui.view.home.screens.math_game_zone.gameCardStyle
import com.jigar.me.ui.view.home.screens.math_game_zone.merge2048.components.Merge2048Direction
import com.jigar.me.ui.view.home.screens.math_game_zone.merge2048.components.Merge2048Palette
import com.jigar.me.ui.view.home.screens.math_game_zone.merge2048.components.Merge2048Tile
import com.jigar.me.ui.view.home.screens.math_game_zone.merge2048.viewmodel.Merge2048PlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay
import kotlin.math.abs

private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)
private val GREEN = Color(0xFF2E7D32)

private val mScale: Float
    get() = if (DeviceInfo.isLargeTablet) 1.7f else if (DeviceInfo.isTablet) 1.45f else 1.0f

/**
 * The candy colour of a tile: one step of the toy rainbow per doubling (2, 4, 8 ... walk through the colours and repeat).
 * Also used by the Home preview.
 */
internal fun merge2048Candy(value: Int, colors: AbColors): Color {
    val step = maxOf(1, value.countTrailingZeroBits()) - 1
    return colors.toyColor(step)
}

@Composable
fun Merge2048PlayScreen(viewModel: Merge2048PlayViewModel, onBackClick: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val baseCheer = remember { randomGameCheer() }
    val s = mScale
    // on a short phone screen (384dp tall) a fixed 300dp board ran past the bottom edge: never taller than the room left
    val boardSide = minOf((300f * s).dp, (LocalConfiguration.current.screenHeightDp - 100).dp)
    val gap = (6f * s).dp
    val cell = (boardSide - gap * (state.size + 1)) / state.size
    // The tray takes the game's own colour from the zone list.
    val trayAccent = gameCardStyle(GameCategoryType.MERGE_2048).light

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
                    color = ORANGE, style = AbTypography.fixed(22f * s, FontWeight.ExtraBold),
                    modifier = Modifier.align(Alignment.Center)
                        .abGamePill(outline = ORANGE_BORDER)
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
                    modifier = Modifier.weight(1f).fillMaxHeight(), character = GameCharacter.Panda)

                BoardView(state.tiles, state.size, boardSide, cell, gap, trayAccent) { dir -> viewModel.move(dir) }

                Scoreboard(state.score, state.target, viewModel.bestScore, s, Modifier.weight(1f).fillMaxHeight())
            }
        }

        state.milestoneValue?.let { m -> MilestonePopup(m, state.target, s, trayAccent) { viewModel.dismissMilestone() } }

        if (showResume) {
            ResumePopup(s, trayAccent,
                onContinue = { showResume = false; viewModel.resumeSavedGame() },
                onNew = { showResume = false; viewModel.clearSave(); viewModel.start() })
        }

        if (state.isGameOver) {
            ResultOverlay(state.score, state.didWin, viewModel.highestTile, viewModel.target, viewModel.bestScore,
                onPlayAgain = { viewModel.start() }, onBack = onBackClick)
        }

        // Debug builds only: lets the lead render the real result overlay without playing a round.
        if (AbDebugFlags.showResultEnabled) {
            ResultOverlay(state.score, state.didWin, viewModel.highestTile, viewModel.target, viewModel.bestScore,
                onPlayAgain = { viewModel.start() }, onBack = onBackClick)
        }
    }
}

/** The frame of this game's popups: a white card with a rim in the game's colour and a 3D edge, like the board's tray. */
@Composable
private fun Merge2048PopupCard(accent: Color, width: Dp, s: Float, content: @Composable ColumnScope.() -> Unit) {
    val colors = LocalAbColors.current
    val corner = (24f * s).dp
    val shape = RoundedCornerShape(corner)
    val drop = (6f * s).dp
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(width)
            .padding(bottom = drop)
            .drawBehind {
                drawRoundRect(accent.abDarkened(0.3f), Offset(0f, drop.toPx()), size, CornerRadius(corner.toPx()))
            }
            .shadow((14f * s).dp, shape, clip = false)
            .background(colors.bgSecondary, shape)
            .border(5.dp, accent, shape)
            .padding(horizontal = (26f * s).dp, vertical = (20f * s).dp),
        content = content
    )
}

@Composable
private fun MilestonePopup(value: Int, nextTarget: Int, s: Float, accent: Color, onContinue: () -> Unit) {
    val colors = LocalAbColors.current
    val screenW = LocalConfiguration.current.screenWidthDp.dp
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.abPopIn()) {
            Merge2048PopupCard(accent = accent, width = minOf(screenW * 0.6f, (580f * s).dp), s = s) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy((8f * s).dp)
                ) {
                    Text("🎉", fontSize = (54f * s).sp, modifier = Modifier.abBreathe(0.08f, 1200))
                    Text(str(R.string.merge2048_target_title), color = colors.brandPrimary,
                        style = AbTypography.fixed(30f * s, FontWeight.Black), textAlign = TextAlign.Center)
                    Text(str(R.string.merge2048_reached, value), color = colors.accentGreenInk,
                        style = AbTypography.fixed(22f * s, FontWeight.ExtraBold), textAlign = TextAlign.Center)
                    Text(str(R.string.merge2048_next, nextTarget), color = colors.accentOrange.abReadable(3f),
                        style = AbTypography.fixed(19f * s, FontWeight.ExtraBold), textAlign = TextAlign.Center)
                    Text(str(R.string.merge2048_target_hint), color = colors.textSecondary,
                        style = AbTypography.fixed(16f * s, FontWeight.SemiBold), textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = AppDimens.Dimens8))
                    GameButton(text = str(R.string.keep_going), icon = Icons.Rounded.PlayArrow, type = ButtonType.ORANGE,
                        isIconStart = false, onClick = onContinue, modifier = Modifier.padding(top = AppDimens.Dimens6))
                }
            }
        }
    }
}

@Composable
private fun ResumePopup(s: Float, accent: Color, onContinue: () -> Unit, onNew: () -> Unit) {
    val colors = LocalAbColors.current
    val screenW = LocalConfiguration.current.screenWidthDp.dp
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.abPopIn()) {
            Merge2048PopupCard(accent = accent, width = minOf(screenW * 0.55f, (520f * s).dp), s = s) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy((12f * s).dp)
                ) {
                    Text("👋", fontSize = (44f * s).sp, modifier = Modifier.abBreathe(0.08f, 1200))
                    Text(str(R.string.resume_title), color = colors.brandPrimary,
                        style = AbTypography.fixed(28f * s, FontWeight.Black), textAlign = TextAlign.Center)
                    Text(str(R.string.resume_message), color = colors.textSecondary,
                        style = AbTypography.fixed(17f * s, FontWeight.SemiBold), textAlign = TextAlign.Center)
                    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                        GameButton(text = str(R.string.continue_game), icon = Icons.Rounded.PlayArrow, type = ButtonType.ORANGE, onClick = onContinue)
                        GameButton(text = str(R.string.new_game), icon = Icons.Filled.Refresh, type = ButtonType.BLUE, onClick = onNew)
                    }
                }
            }
        }
    }
}

@Composable
private fun BoardView(
    tiles: List<Merge2048Tile>, size: Int, boardSide: Dp, cell: Dp, gap: Dp, accent: Color,
    onSwipe: (Merge2048Direction) -> Unit
) {
    val tileCorner = maxOf(7f, cell.value * 0.22f).dp
    val outerCorner = 18.dp
    val outer = RoundedCornerShape(outerCorner)
    // the pale well of the tray: a dark surface with a hint of the game colour in dark mode
    val wellColor = com.jigar.me.ui.view.ab.LocalAbColors.current.let { c -> if (c.isDark) accent.abBlended(c.bgSecondary, 0.82f) else accent.abBlended(Color.White, 0.72f) }
    Box(
        modifier = Modifier
            .size(boardSide)
            // The board's toy tray, drawn inside the board's own frame: a rim in the game's colour, a pale well and a 3D edge.
            .drawBehind {
                drawRoundRect(accent.abDarkened(0.3f), Offset(0f, 6.dp.toPx()), this.size, CornerRadius(outerCorner.toPx()))
            }
            .shadow(12.dp, outer, clip = false)
            .drawBehind {
                val r = CornerRadius(outerCorner.toPx())
                drawRoundRect(accent, cornerRadius = r)
                drawRoundRect(
                    Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.3f), Color.Transparent), endY = this.size.height * 0.5f),
                    cornerRadius = r,
                )
                val rim = 3.dp.toPx()
                drawRoundRect(
                    wellColor,
                    topLeft = Offset(rim, rim),
                    size = Size(this.size.width - rim * 2f, this.size.height - rim * 2f),
                    cornerRadius = CornerRadius(15.dp.toPx()),
                )
                drawRoundRect(Color.White.copy(alpha = 0.45f), cornerRadius = r, style = Stroke(width = 1.5.dp.toPx()))
            }
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
        // empty sockets the tiles sit in
        for (i in 0 until size * size) {
            Box(
                modifier = Modifier
                    .offset(gap + (cell + gap) * (i % size), gap + (cell + gap) * (i / size))
                    .size(cell)
                    .background(accent.copy(alpha = 0.3f), RoundedCornerShape(tileCorner))
            )
        }
        tiles.forEach { tile -> key(tile.id) { MergeTile(tile, cell, gap, tileCorner) } }
    }
}

@Composable
private fun MergeTile(tile: Merge2048Tile, cell: Dp, gap: Dp, tileCorner: Dp) {
    val colors = LocalAbColors.current
    val x by animateDpAsState(gap + (cell + gap) * tile.col, tween(130), label = "")
    val y by animateDpAsState(gap + (cell + gap) * tile.row, tween(130), label = "")
    val pop = remember { Animatable(if (tile.isNew) 0.4f else 1f) }
    LaunchedEffect(tile.isNew) { if (tile.isNew) pop.animateTo(1f, spring(dampingRatio = 0.5f)) }
    // ≤4 digits show the full number; bigger values show as K/M (max 4 chars).
    // Font shrinks by label length so it always fits one line on a 7×7 board.
    val text = Merge2048Palette.label(tile.value)
    val fontFrac = when (text.length) {
        1 -> 0.46f
        2 -> 0.42f
        3 -> 0.34f
        else -> 0.29f   // 4 chars ("8192", "512K")
    }
    val face = merge2048Candy(tile.value, colors)
    val edge = minOf(3f, maxOf(2f, cell.value * 0.06f)).dp
    val shape = RoundedCornerShape(tileCorner)
    // The tile is `cell` tall in total: a face (cell - edge) plus the 3D bottom edge the toy tile adds.
    Box(
        modifier = Modifier
            .offset(x, y)
            .size(cell)
            .graphicsLayer { scaleX = pop.value; scaleY = pop.value }
            .abBounce(tile.value, 1.28f)
            .then(
                if (tile.justMerged) Modifier.shadow(10.dp, shape, clip = false, ambientColor = face, spotColor = face)
                else Modifier
            )
            .abToyTile(face, tileCorner, edge),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            style = AbTypography.fixed(cell.value * fontFrac, FontWeight.Black).copy(
                shadow = Shadow(face.abDarkened(0.5f).copy(alpha = 0.6f), Offset(0f, 3f), 0f)
            ),
            color = Color.White,
            maxLines = 1, softWrap = false
        )
        if (tile.justMerged) {
            Box(Modifier.fillMaxSize().border(3.dp, Color.White, shape))
        }
    }
}

// ⭐ Score / 🎯 Target / 🏆 Best
@Composable
private fun Scoreboard(score: Int, target: Int, best: Int, s: Float, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    Column(modifier = modifier.padding(horizontal = AppDimens.Dimens8), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        AbGlassPanel {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
                modifier = Modifier.padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens16)
            ) {
                ScoreRow("⭐", "$score", colors.brandPrimary, s)
                ScoreRow("🎯", "$target", colors.accentOrange.abReadable(3f), s)
                if (best > 0) ScoreRow("🏆", "$best", colors.accentOrange.abReadable(3f), s)
            }
        }
    }
}

@Composable
private fun ScoreRow(emoji: String, value: String, color: Color, s: Float) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6), verticalAlignment = Alignment.CenterVertically) {
        Text(emoji, fontSize = (22f * s).sp)
        Text(value, color = color, style = AbTypography.fixed(22f * s, FontWeight.ExtraBold), maxLines = 1, softWrap = false,
            modifier = Modifier.abBounce(value))
    }
}

@Composable private fun str(id: Int): String = androidx.compose.ui.res.stringResource(id)
@Composable private fun str(id: Int, vararg args: Any): String = androidx.compose.ui.res.stringResource(id, *args)

@Composable
private fun ResultOverlay(score: Int, didWin: Boolean, highest: Int, goal: Int, best: Int, onPlayAgain: () -> Unit, onBack: () -> Unit) {
    val starCount = when { didWin -> 3; highest >= goal / 2 -> 2; else -> 1 }
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { if (starCount >= 2) AudioPlayerManager.playSoundClap() else AudioPlayerManager.playSoundWin() }
        // "Candy Pop" celebration card from the shared kit.
        GameResultCard(
            title = str(if (didWin) R.string.balloon_great_job else R.string.good_try),
            emoji = "🔢",
            accent = ButtonType.PINK,
            stars = starCount,
            score = str(R.string.balloon_final_score) + ": $score",
            stats = {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    GameStatChip("🔝", "$highest", GREEN)
                    GameStatChip("🏆", "$best", ORANGE)
                }
            },
            buttons = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20),
                    modifier = Modifier.graphicsLayer { alpha = if (enabled) 1f else 0.55f }
                ) {
                    GameButton(text = str(R.string.balloon_play_again), icon = Icons.Filled.Refresh,
                        type = ButtonType.POSITIVE, onClick = { if (enabled) onPlayAgain() })
                    GameHomeButton(onClick = { if (enabled) onBack() })
                }
            },
        )
    }
}
