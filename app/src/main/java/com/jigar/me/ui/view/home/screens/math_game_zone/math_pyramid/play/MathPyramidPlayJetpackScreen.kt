package com.jigar.me.ui.view.home.screens.math_game_zone.math_pyramid.play

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.ab.AbColors
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abBlended
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.GameCategoryType
import com.jigar.me.ui.view.home.screens.math_game_zone.common.AbDebugFlags
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameResultCard
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameStatChip
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBounce
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBreathe
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abPopIn
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTray
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abWobble
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.common.toyColor
import com.jigar.me.ui.view.home.screens.math_game_zone.gameCardStyle
import com.jigar.me.ui.view.home.screens.math_game_zone.math_pyramid.play.components.NumberPyramidGenerator
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.theme.AppDimens.keyPadHeight
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min

@Composable
fun MathPyramidPlayJetpackScreen(
    levels: Int = 4,
    difficulty: CommonDifficulty4 = CommonDifficulty4.easy,
    onBackClick: () -> Unit
) {
    val solveMarker: com.jigar.me.ui.view.home.screens.math_game_zone.ZoneDailyMarkerViewModel =
        androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
    var pyramid by remember { mutableStateOf<List<List<Int?>>>(emptyList()) }
    var editableMask by remember { mutableStateOf<List<List<Boolean>>>(emptyList()) }
    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var isSolved by remember { mutableStateOf(false) }

    fun generateNewPuzzle() {
        val range = when (difficulty) {
            CommonDifficulty4.easy -> 1..20
            CommonDifficulty4.medium -> 10..50
            CommonDifficulty4.hard -> 30..70
            CommonDifficulty4.veryHard -> 50..99
        }
        val res = NumberPyramidGenerator.generate(levels = levels, difficulty = difficulty, numberRange = range)
        pyramid = res.puzzle
        editableMask = pyramid.map { row -> row.map { it == null } }
        selectedCell = null
        isSolved = false
    }

    LaunchedEffect(Unit) { generateNewPuzzle() }

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 🔹 Header Bar
            BackButtonWithText(title = "Level $levels • ${difficulty.displayName}", onBackClick = onBackClick)

            Spacer(Modifier.weight(1f))

            Row {
                // Pyramid grid
                Column(modifier = Modifier.weight(0.7f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    // a new puzzle builds a new grid, so its bricks pop in again, top down
                    key(System.identityHashCode(editableMask)) {
                        PyramidGrid(levels,pyramid, editableMask, selectedCell) { r, c ->
                            if (editableMask.getOrNull(r)?.getOrNull(c) == true) {
                                selectedCell = Pair(r, c)
                                AudioPlayerManager.playSoundBtnClick()
                            }
                        }
                    }
                }

                // Right column: keypad + controls
                Column(modifier = Modifier.weight(0.3f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    AnimatedVisibility(visible = selectedCell != null, enter = fadeIn(), exit = fadeOut()) {
                        KeypadCompose(onKey = { label ->
                            selectedCell?.let { (r, c) ->
                                val current = pyramid[r][c] ?: 0
                                val newPyramid = pyramid.map { it.toMutableList() }.toMutableList()
                                when (label) {
                                    "Clear" -> {
                                        AudioPlayerManager.playSoundBtnBack()
                                        newPyramid[r][c] = null
                                    }
                                    "Erase" -> {
                                        AudioPlayerManager.playSoundBtnBack()
                                        val next = current / 10
                                        newPyramid[r][c] = if (next == 0) null else next
                                    }
                                    else -> {
                                        AudioPlayerManager.playSoundTilePlace()
                                        val digit = label.toIntOrNull()
                                        if (digit != null) {
                                            val next = current * 10 + digit
                                            if (next <= 9999) newPyramid[r][c] = next
                                        }
                                    }
                                }
                                pyramid = newPyramid
                                if (checkIfSolved(pyramid)) {
                                    AudioPlayerManager.playSoundWin()
                                    solveMarker.markSolved(
                                        com.jigar.me.ui.view.home.screens.math_game_zone.common.ZoneSolveCounter.MATH_PYRAMID,
                                        difficulty.name
                                    )
                                    solveMarker.recordWeekly(1)
                                    isSolved = true
                                    selectedCell = null
                                }
                            }
                        })
                    }

                    Spacer(modifier = Modifier.height(if (DeviceInfo.isTablet) AppDimens.Dimens24 else AppDimens.Dimens16))

                    GameButton(
                        text = stringResource(R.string.start_new),
                        icon = Icons.Filled.Shuffle,
                        type = ButtonType.ORANGE,
                        isSmall = true,
                        onClick = {
                            generateNewPuzzle()
                        }
                    )
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }

    if (isSolved) {
        PyramidResultOverlay(
            levels = levels,
            difficultyName = difficulty.displayName,
            onPlayAgain = {
                isSolved = false
                generateNewPuzzle()
            },
            onBack = {
                isSolved = false
                onBackClick()
            }
        )
    }

    // Debug builds only: lets the lead render the real result overlay without playing.
    if (AbDebugFlags.showResultEnabled) {
        PyramidResultOverlay(
            levels = levels,
            difficultyName = difficulty.displayName,
            onPlayAgain = {
                isSolved = false
                generateNewPuzzle()
            },
            onBack = {
                isSolved = false
                onBackClick()
            }
        )
    }
}

// "Candy Pop" celebration card — the shared result card, always with all three stars (a solved pyramid is always a win).
@Composable
private fun PyramidResultOverlay(
    levels: Int,
    difficultyName: String,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit
) {
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { AudioPlayerManager.playSoundClap() }
        GameResultCard(
            title = stringResource(R.string.you_did_it),
            emoji = "🔺",
            accent = ButtonType.ORANGE,
            stars = 3,
            stats = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)) {
                    Text(
                        stringResource(R.string.you_built_the_pyramid),
                        color = Color.White,
                        style = AbTypography.fixed(17f * gameScale(), FontWeight.ExtraBold).copy(
                            shadow = Shadow(Color.Black.copy(alpha = 0.22f), Offset(0f, 4f), 0f)
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    GameStatChip(emoji = "🔺", value = "Level $levels • $difficultyName", color = Color(0xFFE65100))
                }
            },
            buttons = {
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    GameButton(
                        text = stringResource(R.string.continue_to_play),
                        icon = Icons.Rounded.PlayArrow, type = ButtonType.POSITIVE,
                        onClick = { if (enabled) onPlayAgain() }
                    )
                    GameHomeButton(onClick = { if (enabled) onBack() })
                }
            }
        )
    }
}

// ── toy-box pyramid ──────────────────────────────────────────────────────────

/** The size of one brick for a pyramid of [levels] rows. */
private fun pyramidCellSize(levels: Int): Pair<Dp, Dp> {
    val base = AppDimens.PyramidWidth.value
    var w = base
    var h = base
    when (levels) {
        2 -> { w = base * 1.9f; h = base * 1.2f }
        3 -> { w = base * 1.3f; h = base * 0.9f }
        4 -> { w = base * 1.1f; h = base * 0.7f }
        5 -> { h = base * 0.6f }
        6 -> { w = base * 0.95f; h = base * 0.56f }
    }
    return Pair(w.dp, h.dp)
}

/** A rainbow down the pyramid: pink at the top, then orange, green, sky, purple, indigo. */
private fun pyramidBrickTone(colors: AbColors, row: Int): Color {
    val tones = listOf(colors.accentPink, colors.accentOrange, colors.accentGreen, colors.brandSecondary, colors.accentPurple, colors.brandPrimary)
    return tones[row % tones.size]
}

/**
 * Bricks of a fully filled "top = left + right" triple that does not add up. A triple that contains the brick being typed
 * into is left alone, so a number that is still being typed never flashes red.
 */
private fun pyramidClashMask(pyramid: List<List<Int?>>, selected: Pair<Int, Int>?): List<List<Boolean>> {
    val mask: List<MutableList<Boolean>> = pyramid.map { row -> MutableList(row.size) { false } }
    val n = pyramid.size
    if (n < 2) return mask
    for (r in 0 until n - 1) {
        for (c in pyramid[r].indices) {
            if (pyramid[r + 1].size <= c + 1) continue
            val top = pyramid[r][c] ?: continue
            val left = pyramid[r + 1][c] ?: continue
            val right = pyramid[r + 1][c + 1] ?: continue
            if (top == left + right) continue
            if (selected != null) {
                val inTriple = (selected.first == r && selected.second == c) ||
                    (selected.first == r + 1 && (selected.second == c || selected.second == c + 1))
                if (inTriple) continue
            }
            mask[r][c] = true
            mask[r + 1][c] = true
            mask[r + 1][c + 1] = true
        }
    }
    return mask
}

// The bricks stack straight into a pyramid on the background: no box around them, the stepped shape itself is the pyramid.
@Composable
private fun PyramidGrid(
    levels: Int,
    pyramid: List<List<Int?>>,
    editableMask: List<List<Boolean>>,
    selectedCell: Pair<Int, Int>?,
    onCellTap: (Int, Int) -> Unit
) {
    val clash = pyramidClashMask(pyramid, selectedCell)
    val accent = remember { gameCardStyle(GameCategoryType.MATH_PYRAMID).light }
    val cellSize = pyramidCellSize(levels)
    val gap = AppDimens.Dimens8
    val rows = pyramid.size

    Box(
        modifier = Modifier.graphicsLayer { alpha = if (pyramid.isEmpty()) 0f else 1f }
    ) {
        // the six-step pyramid has the most rows, so its rows sit closer together
        val rowGap = if (levels >= 6) 4.dp else gap
        Column(verticalArrangement = Arrangement.spacedBy(rowGap), horizontalAlignment = Alignment.CenterHorizontally) {
            for (r in 0 until rows) {
                Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                    for (c in 0 until pyramid[r].size) {
                        val value = pyramid[r][c]
                        val isEditable = editableMask.getOrNull(r)?.getOrNull(c) ?: false
                        val isSelected = selectedCell?.first == r && selectedCell.second == c
                        val isClash = clash.getOrNull(r)?.getOrNull(c) ?: false
                        PyramidCell(levels, r, c, value = value, isEditable = isEditable, isSelected = isSelected, clash = isClash) {
                            onCellTap(r, c)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PyramidCell(
    levels: Int,
    row: Int,
    col: Int,
    value: Int?,
    isEditable: Boolean,
    isSelected: Boolean,
    clash: Boolean,
    onTap: () -> Unit
) {
    val colors = LocalAbColors.current
    val gs = gameScale()
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        label = ""
    )

    val cellSize = pyramidCellSize(levels)
    val width = cellSize.first
    val height = cellSize.second
    val tone = pyramidBrickTone(colors, row)
    val conflict = clash && isEditable && value != null
    val edge = (3f * gs).dp
    val corner = min(height.value * 0.3f, 14f * gs).dp
    val ink = Color(0xFF1E2470)

    // Given bricks are white with a coloured rim; empty slots are pale; typed bricks are solid candy.
    var face = Color.White
    var rim = tone
    var lip = tone.abDarkened(0.2f)
    var rimWidth = 3f
    var digit = ink
    if (isEditable) {
        if (value == null) {
            face = tone.abBlended(Color.White, 0.7f)
            digit = tone.abReadable(4.5f)
        } else {
            face = tone.abReadable(3f)
            rim = Color.White.copy(alpha = 0.5f)
            lip = face.abDarkened(0.3f)
            rimWidth = 2f
            digit = Color.White
        }
    }
    if (conflict) {
        face = colors.danger.abBlended(Color.White, 0.6f)
        rim = colors.danger
        lip = colors.danger.abDarkened(0.25f)
        rimWidth = 3f
        digit = colors.danger.abReadable(4.5f)
    }
    if (isSelected) {
        face = colors.accentYellow.abBlended(Color.White, 0.55f)
        rim = colors.brandPrimary
        lip = colors.brandPrimary.abDarkened(0.2f)
        rimWidth = 3.5f
        digit = ink
    }

    val label = value?.toString() ?: (if (isEditable && !isSelected) "?" else "")

    Box(
        modifier = Modifier
            .abPopIn(minOf(400, row * 70 + col * 20))
            .abWobble(conflict)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .size(width, height)
            .clickable(
                interactionSource = interaction,
                indication = null
            ) {
                if (isEditable) onTap()
            }
    ) {
        // One toy block: a face with a glossy top, a rim, and a 3D lip under it.
        Box(
            Modifier.fillMaxSize()
                .then(if (isSelected) Modifier.abBreathe(0.04f, 1300) else Modifier)
                .pyramidBrickFace(face, rim, lip, rimWidth.dp, corner, edge)
        )
        // Value or placeholder
        Box(Modifier.fillMaxSize().padding(bottom = edge), contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = digit,
                style = AbTypography.fixed(height.value * 0.6f, FontWeight.Black),
                modifier = Modifier.abBounce(value)
            )
        }
    }
}

private fun Modifier.pyramidBrickFace(face: Color, rim: Color, lip: Color, rimWidth: Dp, corner: Dp, edge: Dp): Modifier {
    val shape = RoundedCornerShape(corner)
    return this
        .padding(bottom = edge)
        .drawBehind {
            drawRoundRect(lip, Offset(0f, edge.toPx()), size, CornerRadius(corner.toPx()))
        }
        .background(face, shape)
        .drawBehind {
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.4f), Color.Transparent),
                    startY = 0f, endY = size.height / 2f
                ),
                cornerRadius = CornerRadius(corner.toPx())
            )
        }
        .border(rimWidth, rim, shape)
}

/** A chunky candy key that squishes while it is pressed. */
@Composable
private fun PyramidKey(face: Color, side: Dp, onClick: () -> Unit, content: @Composable BoxScope.() -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val squash by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "pyramidKey"
    )
    Box(
        modifier = Modifier
            .size(side)
            .graphicsLayer { scaleX = squash; scaleY = squash }
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .abToyTile(face, side * 0.3f, 4.dp),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
fun KeypadCompose(onKey: (String) -> Unit) {
    val colors = LocalAbColors.current
    val buttons = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("Clear", "0", "Erase")
    )
    val fontSize = keyPadHeight.value * 0.55f
    val keyText = AbTypography.fixed(fontSize, FontWeight.Black).copy(
        shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f)
    )

    Column(modifier = Modifier.wrapContentWidth(), verticalArrangement = Arrangement.spacedBy(if (DeviceInfo.isTablet)AppDimens.Dimens10 else AppDimens.Dimens6)) {
        buttons.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(if (DeviceInfo.isTablet)AppDimens.Dimens10 else AppDimens.Dimens6)) {
                row.forEach { label ->
                    when (label){
                        "Erase" ->{
                            PyramidKey(face = colors.danger.abReadable(3f), side = keyPadHeight, onClick = { onKey("Erase") }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_backspace),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(keyPadHeight * 0.55f)
                                )
                            }
                        }
                        "Clear" ->{
                            PyramidKey(face = colors.danger.abReadable(3f), side = keyPadHeight, onClick = { onKey("Clear") }) {
                                Text("C", style = keyText, color = Color.White)
                            }
                        }
                        else ->{
                            PyramidKey(
                                face = colors.toyColor((label.toIntOrNull() ?: 1) - 1),
                                side = keyPadHeight,
                                onClick = { onKey(label) }
                            ) {
                                Text(label, style = keyText, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
private fun checkIfSolved(pyramid: List<List<Int?>>): Boolean {
    // ensure all filled
    for (r in pyramid.indices) {
        for (c in pyramid[r].indices) {
            if (pyramid[r][c] == null) return false
        }
    }
    val n = pyramid.size
    if (n < 2) return true
    for (r in 0 until n - 1) {
        for (c in 0 until pyramid[r].size) {
            val top = pyramid[r][c]
            val left = pyramid[r + 1][c]
            val right = pyramid[r + 1][c + 1]
            if (top == null || left == null || right == null) return false
            if (top != left + right) return false
        }
    }
    return true
}
