package com.jigar.me.ui.view.home.screens.math_game_zone.trophy_room

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.screens.math_game_zone.common.BuddyDef
import com.jigar.me.ui.view.home.screens.math_game_zone.common.ZoneBuddy
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBounce
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abPopIn
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTray
import com.jigar.me.ui.view.home.theme.AppDimens
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


private data class Trophy(
    val emoji: String,
    val title: String,
    val desc: String,
    val earned: Boolean,
    val accent: Color,
    val progress: String
)

@Composable
fun TrophyRoomScreen(
    onBackClick: () -> Unit,
    viewModel: TrophyRoomViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.refresh() }

    // Every progressive trophy now has a 2nd, harder tier so kids always
    // have a next goal even after clearing the first one. Champion +
    // Legend are appended last since they depend on the others' count.
    // Each trophy shows live progress toward its goal, so kids always know
    // what to do next — and every card has its own color.
    fun prog(current: Int, goal: Int) = "${minOf(current, goal)}/$goal"
    val base = listOf(
        Trophy("🎮", stringResource(R.string.trophy_first_game), stringResource(R.string.trophy_first_game_desc), stats.gamesPlayed >= 1, Color(0xFF42A5F5), prog(stats.gamesPlayed, 1)),
        Trophy("🧭", stringResource(R.string.trophy_explorer), stringResource(R.string.trophy_explorer_desc), stats.gamesPlayed >= 6, Color(0xFF26A69A), prog(stats.gamesPlayed, 6)),
        Trophy("🧠", stringResource(R.string.trophy_all_rounder), stringResource(R.string.trophy_all_rounder_desc), stats.gamesPlayed >= 18, Color(0xFF7E57C2), prog(stats.gamesPlayed, 18)),
        Trophy("🌟", stringResource(R.string.trophy_star_10), stringResource(R.string.trophy_star_10_desc), stats.totalStars >= 10, Color(0xFFFFB300), prog(stats.totalStars, 10)),
        Trophy("💫", stringResource(R.string.trophy_star_50), stringResource(R.string.trophy_star_50_desc), stats.totalStars >= 50, Color(0xFFFF7043), prog(stats.totalStars, 50)),
        Trophy("👑", stringResource(R.string.trophy_star_150), stringResource(R.string.trophy_star_150_desc), stats.totalStars >= 150, Color(0xFFAB47BC), prog(stats.totalStars, 150)),
        Trophy("🌌", stringResource(R.string.trophy_star_300), stringResource(R.string.trophy_star_300_desc), stats.totalStars >= 300, Color(0xFF8E24AA), prog(stats.totalStars, 300)),
        Trophy("🗺️", stringResource(R.string.trophy_levels_10), stringResource(R.string.trophy_levels_10_desc), stats.levelsDone >= 10, Color(0xFF66BB6A), prog(stats.levelsDone, 10)),
        Trophy("🚀", stringResource(R.string.trophy_levels_50), stringResource(R.string.trophy_levels_50_desc), stats.levelsDone >= 50, Color(0xFF29B6F6), prog(stats.levelsDone, 50)),
        Trophy("🌠", stringResource(R.string.trophy_levels_150), stringResource(R.string.trophy_levels_150_desc), stats.levelsDone >= 150, Color(0xFF00897B), prog(stats.levelsDone, 150)),
        Trophy("✨", stringResource(R.string.trophy_perfect_5), stringResource(R.string.trophy_perfect_5_desc), stats.threeStars >= 5, Color(0xFFEC407A), prog(stats.threeStars, 5)),
        Trophy("🎆", stringResource(R.string.trophy_perfect_20), stringResource(R.string.trophy_perfect_20_desc), stats.threeStars >= 20, Color(0xFFD81B60), prog(stats.threeStars, 20)),
        // Tier-specific — totals above can be filled on Easy alone, these
        // can't, so they actually push a kid toward the harder difficulties.
        Trophy("🧗", stringResource(R.string.trophy_medium_climber), stringResource(R.string.trophy_medium_climber_desc), stats.mediumLevelsDone >= 5, Color(0xFF7CB342), prog(stats.mediumLevelsDone, 5)),
        Trophy("🏔️", stringResource(R.string.trophy_medium_mountain), stringResource(R.string.trophy_medium_mountain_desc), stats.mediumLevelsDone >= 20, Color(0xFF558B2F), prog(stats.mediumLevelsDone, 20)),
        Trophy("💪", stringResource(R.string.trophy_hard_tough), stringResource(R.string.trophy_hard_tough_desc), stats.hardLevelsDone >= 5, Color(0xFFE64A19), prog(stats.hardLevelsDone, 5)),
        Trophy("🛡️", stringResource(R.string.trophy_hard_iron), stringResource(R.string.trophy_hard_iron_desc), stats.hardLevelsDone >= 15, Color(0xFFBF360C), prog(stats.hardLevelsDone, 15)),
        Trophy("🦸", stringResource(R.string.trophy_veryhard_fearless), stringResource(R.string.trophy_veryhard_fearless_desc), stats.veryHardLevelsDone >= 3, Color(0xFF6A1B9A), prog(stats.veryHardLevelsDone, 3)),
        Trophy("🦾", stringResource(R.string.trophy_veryhard_unstoppable), stringResource(R.string.trophy_veryhard_unstoppable_desc), stats.veryHardLevelsDone >= 8, Color(0xFF4A148C), prog(stats.veryHardLevelsDone, 8)),
        Trophy("🔥", stringResource(R.string.trophy_hard_hero), stringResource(R.string.trophy_hard_hero_desc), stats.hardThreeStarCount >= 1, Color(0xFFEF5350), prog(stats.hardThreeStarCount, 1)),
        Trophy("🌋", stringResource(R.string.trophy_hard_master), stringResource(R.string.trophy_hard_master_desc), stats.hardThreeStarCount >= 10, Color(0xFFD84315), prog(stats.hardThreeStarCount, 10)),
        Trophy("💨", stringResource(R.string.trophy_speed_200), stringResource(R.string.trophy_speed_200_desc), stats.maxBest >= 200, Color(0xFF5C6BC0), prog(stats.maxBest, 200)),
        Trophy("⚡", stringResource(R.string.trophy_speed_500), stringResource(R.string.trophy_speed_500_desc), stats.maxBest >= 500, Color(0xFF3949AB), prog(stats.maxBest, 500))
    )
    val champion = Trophy(
        "🏆", stringResource(R.string.trophy_champion), stringResource(R.string.trophy_champion_desc),
        base.count { it.earned } >= 12, Color(0xFFFFA000), prog(base.count { it.earned }, 12)
    )
    val legend = Trophy(
        "💎", stringResource(R.string.trophy_legend), stringResource(R.string.trophy_legend_desc),
        base.all { it.earned } && champion.earned, Color(0xFF00BCD4),
        prog(base.count { it.earned } + (if (champion.earned) 1 else 0), base.size + 1)
    )
    val trophies = base + champion + legend
    val earnedCount = trophies.count { it.earned }


    // One clock for every wiggle, bob and glint on the screen. It is only read inside draw / layer blocks, so a tick never
    // recomposes the grid.
    val clock = rememberTrophyClock()

    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BackButtonWithText(title = stringResource(R.string.trophy_room), onBackClick = onBackClick)
                Spacer(Modifier.weight(1f))
            }
            // Playful candy stat chips riding on the header.
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = AppDimens.Dimens16)
            ) {
                StatChip("⭐", "${stats.totalStars}", Color(0xFFF57F17))
                StatChip("🗺️", "${stats.levelsDone}", Color(0xFF43A047))
                StatChip("🎮", "${stats.gamesPlayed}/18", Color(0xFF0074D5))
                StatChip("🏅", "$earnedCount/${trophies.size}", Color(0xFFE91E63))
            }
        }

        val buddy by viewModel.buddy.collectAsStateWithLifecycle()

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens10),
            contentPadding = PaddingValues(
                start = AppDimens.Dimens16, end = AppDimens.Dimens16,
                top = AppDimens.Dimens10, bottom = AppDimens.Dimens16
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            // Buddy shelf: earn stars to free new companions; tap to pick
            // who walks the Number Path maze.
            item(span = { GridItemSpan(maxLineSpan) }, key = "buddies") {
                BuddyShelf(
                    totalStars = stats.totalStars,
                    selected = buddy,
                    onSelect = { viewModel.selectBuddy(it) },
                    clock = clock
                )
            }
            itemsIndexed(trophies, key = { _, t -> t.title }) { i, trophy ->
                TrophyCard(trophy, i, clock)
            }
        }
    }
}

/** Seconds since the screen opened, ticking every frame. Read it only inside draw / graphicsLayer blocks. */
@Composable
private fun rememberTrophyClock(): State<Float> = produceState(initialValue = 0f) {
    val start = withFrameNanos { it }
    while (true) {
        withFrameNanos { now -> value = (now - start) / 1_000_000_000f }
    }
}

/** A short wiggle every few seconds: the padlock rattles, then rests. Degrees. */
private fun trophyLockWiggle(t: Float, phase: Float): Float {
    val cycle = 3.6f
    val c = (t + phase * 1.3f) % cycle
    return if (c < 0.8f) sin(c * 24f) * 16f * (1f - c / 0.8f) else 0f
}

/** Drains the colour out of what it wraps (a locked trophy or buddy). 1 = unchanged, 0 = grey. */
private fun Modifier.trophyDesaturate(amount: Float = 0.15f): Modifier = this.drawWithCache {
    val paint = Paint().apply { colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(amount) }) }
    onDrawWithContent {
        drawIntoCanvas { canvas ->
            canvas.saveLayer(Rect(0f, 0f, size.width, size.height), paint)
            drawContent()
            canvas.restore()
        }
    }
}

/** A small padlock on a lavender disc that hangs from its top and rattles with [angle] (degrees). */
@Composable
private fun TrophyLockBadge(sizeDp: Dp, angle: () -> Float, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .size(sizeDp)
            .graphicsLayer {
                rotationZ = angle()
                transformOrigin = TransformOrigin(0.5f, 0f)
            }
    ) {
        val d = density
        val dia = size.minDimension
        val r = dia / 2f
        val c = Offset(size.width / 2f, size.height / 2f)
        drawCircle(Color(0xFF5F64A0), radius = r, center = Offset(c.x, c.y + 1.5f * d))
        drawCircle(Color(0xFF7C82B8), radius = r, center = c)
        drawCircle(Color.White.copy(alpha = 0.55f), radius = r, center = c, style = Stroke(width = 1.2f * d))
        // padlock: a rounded body with a shackle arc on top
        val bodyW = dia * 0.36f
        val bodyH = dia * 0.26f
        val bodyTop = c.y - dia * 0.02f
        drawRoundRect(
            Color.White,
            topLeft = Offset(c.x - bodyW / 2f, bodyTop),
            size = Size(bodyW, bodyH),
            cornerRadius = CornerRadius(dia * 0.05f, dia * 0.05f)
        )
        val shackleR = dia * 0.11f
        drawArc(
            Color.White,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(c.x - shackleR, bodyTop - shackleR),
            size = Size(shackleR * 2f, shackleR * 2f),
            style = Stroke(width = dia * 0.07f, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun BuddyShelf(totalStars: Int, selected: String, onSelect: (String) -> Unit, clock: State<Float>) {
    // Title on the left, buddy list docked to the right in the same row —
    // each buddy's own icon sits above its label. The row carries its own
    // padding so a bobbing, rotating buddy at either end never gets clipped.
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .abToyTray(Color(0xFFFFB300), 4.dp)
            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens8)
    ) {
        // Paw + label stacked vertically instead of inline.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens4)
        ) {
            Text(
                "🐾",
                fontSize = 30.sp.scaled(),
                modifier = Modifier.graphicsLayer { translationY = sin(clock.value * 3f) * 2.dp.toPx() }
            )
            Text(
                stringResource(R.string.trophy_buddies),
                color = Color(0xFFE65100),
                style = AbTypography.fixed(13f, FontWeight.Black).scaled()
            )
        }
        Spacer(Modifier.weight(1f))
        // Scrollable so all buddies never clip on narrow tablets or big fonts.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens8)
        ) {
            ZoneBuddy.all.forEachIndexed { i, def ->
                val unlocked = totalStars >= def.starsNeeded
                TrophyBuddyButton(
                    index = i,
                    def = def,
                    unlocked = unlocked,
                    isSelected = unlocked && selected == def.emoji,
                    clock = clock,
                    onSelect = { onSelect(def.emoji) }
                )
            }
        }
    }
}

/** One companion on a candy disc: it squashes when pressed, the chosen one bobs and sways, a locked one wears a rattling padlock. */
@Composable
private fun TrophyBuddyButton(
    index: Int,
    def: BuddyDef,
    unlocked: Boolean,
    isSelected: Boolean,
    clock: State<Float>,
    onSelect: () -> Unit
) {
    val face: Color = if (isSelected) Color(0xFFFFC53D) else if (unlocked) Color(0xFFEEF0FB) else Color(0xFFC9CDE4)
    val rim: Color = if (isSelected) Color(0xFFE65100) else Color.White.copy(alpha = 0.6f)
    val phase = index.toFloat()
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val sx by animateFloatAsState(
        targetValue = if (pressed) 1.06f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium), label = "buddySx"
    )
    val sy by animateFloatAsState(
        targetValue = if (pressed) 0.88f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium), label = "buddySy"
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .graphicsLayer {
                scaleX = sx
                scaleY = sy
                transformOrigin = TransformOrigin(0.5f, 1f)
            }
            .clickable(interactionSource = interaction, indication = null, enabled = unlocked) {
                AudioPlayerManager.playSoundTilePlace()
                onSelect()
            }
    ) {
        Box {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(bottom = 3.dp)
                    .graphicsLayer {
                        if (isSelected) {
                            val t = clock.value
                            translationY = sin(t * 3.1f + phase) * 3.dp.toPx()
                            rotationZ = sin(t * 1.55f + phase) * 6f
                        }
                    }
                    .drawBehind {
                        val r = size.minDimension / 2f
                        val c = Offset(size.width / 2f, size.height / 2f)
                        drawCircle(face.abDarkened(0.3f), radius = r, center = Offset(c.x, c.y + 3.dp.toPx()))
                        drawCircle(face, radius = r, center = c)
                        drawCircle(
                            Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.4f), Color.Transparent), startY = 0f, endY = size.height / 2f),
                            radius = r, center = c
                        )
                        drawCircle(rim, radius = r - 1.dp.toPx(), center = c, style = Stroke(width = 2.dp.toPx()))
                    }
                    .padding(AppDimens.Dimens14)
            ) {
                Text(
                    def.emoji,
                    fontSize = 32.sp.scaled(),
                    modifier = Modifier
                        .graphicsLayer { if (!unlocked) alpha = 0.4f }
                        .then(if (!unlocked) Modifier.trophyDesaturate() else Modifier)
                )
            }
            if (!unlocked) {
                TrophyLockBadge(
                    sizeDp = 16.dp,
                    angle = { trophyLockWiggle(clock.value, phase * 0.7f) },
                    modifier = Modifier.align(Alignment.BottomEnd).offset(x = 3.dp, y = 3.dp)
                )
            }
        }
        Text(
            if (unlocked) (if (isSelected) "✓" else " ") else "⭐${def.starsNeeded}",
            color = if (isSelected) Color(0xFF2E7D32) else Color(0xFF8D6E63),
            style = AbTypography.fixed(11f, FontWeight.ExtraBold).scaled()
        )
    }
}

// Single-line text that shrinks itself until it fits — so trophy labels
// never clip, even at big system font sizes.
@Composable
private fun ShrinkText(
    text: String,
    color: Color,
    style: TextStyle
) {
    var scale by remember(text) { mutableStateOf(1f) }
    Text(
        text = text,
        color = color,
        textAlign = TextAlign.Center,
        maxLines = 1,
        softWrap = false,
        style = style,
        fontSize = style.fontSize * scale,
        onTextLayout = { if (it.hasVisualOverflow && scale > 0.55f) scale *= 0.93f }
    )
}

/** A candy chip on the header: an emoji and a number that bounces when it changes. */
@Composable
private fun StatChip(emoji: String, value: String, color: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .abBounce(value, 1.2f)
            .abToyTile(color.abReadable(3f), 14.dp, 3.dp)
            .padding(horizontal = AppDimens.Dimens10, vertical = AppDimens.Dimens4)
    ) {
        Text(emoji, style = AbTypography.fixed(12f, FontWeight.Black).scaled())
        Text(
            value,
            color = Color.White,
            style = AbTypography.fixed(13f, FontWeight.Black).scaled().copy(shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 2f), 0f))
        )
    }
}

/**
 * A trophy as a candy tile. Earned: its own colour, a proud wiggle, a glint that sweeps across and a twinkle. Locked: a soft
 * lavender with a padlock that rattles now and then.
 */
@Composable
private fun TrophyCard(trophy: Trophy, index: Int, clock: State<Float>) {
    val colors = LocalAbColors.current
    val cornerDp = AppDimens.Dimens16
    val edge = 4.dp
    val shape = RoundedCornerShape(cornerDp)
    // Earned = the trophy's own candy colour. Locked = a soft candy grey-lavender.
    val face: Color = if (trophy.earned) trophy.accent.abReadable(3f) else if (colors.isDark) Color(0xFF3A3680) else Color(0xFFC9CDE4)
    // Text on a locked tile: a deep lavender that reads on the pale face.
    val lockedInk: Color = if (colors.isDark) Color(0xFFD3CFFA) else Color(0xFF4A4F86)
    val titleInk: Color = if (trophy.earned) Color.White else lockedInk
    val pillInk: Color = if (trophy.earned) Color.Black else lockedInk
    val glow: Color = if (trophy.earned) trophy.accent else Color(0xFF9098C8)
    val phase = index * 0.8f

    Box(
        modifier = Modifier
            .abPopIn(minOf(index, 10) * 40)
            // .aspectRatio keeps the cell shape the grid always had
            .aspectRatio(1.3f)
            .graphicsLayer {
                rotationZ = ((index % 3) - 1) * 1.2f
                translationY = if (trophy.earned) sin(clock.value * 1.9f + phase) * 2.dp.toPx() else 0f
            }
            .shadow(if (trophy.earned) 6.dp else 3.dp, shape, clip = false, ambientColor = glow, spotColor = glow)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().abToyTile(face, cornerDp, edge)
        ) {
            if (trophy.earned) {
                TrophyShine(clock = clock, phase = phase, corner = cornerDp, modifier = Modifier.matchParentSize())
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize().padding(horizontal = AppDimens.Dimens8)
            ) {
                // Earned trophies bob and wiggle proudly forever.
                Text(
                    trophy.emoji,
                    fontSize = 46.sp.scaled(),
                    modifier = Modifier
                        .graphicsLayer {
                            if (trophy.earned) {
                                val t = clock.value
                                rotationZ = sin(t * 2.6f + phase) * 8f
                                translationY = sin(t * 4.4f + phase) * 3.dp.toPx()
                            } else {
                                alpha = 0.5f
                            }
                        }
                        .then(if (!trophy.earned) Modifier.trophyDesaturate() else Modifier)
                )
                ShrinkText(
                    trophy.title,
                    color = titleInk,
                    style = AbTypography.fixed(15f, FontWeight.Black).scaled().copy(
                        shadow = if (trophy.earned) Shadow(Color.Black.copy(alpha = 0.22f), Offset(0f, 2f), 0f) else null
                    )
                )
                ShrinkText(
                    trophy.desc,
                    color = titleInk.copy(alpha = 0.92f),
                    style = AbTypography.fixed(12f, FontWeight.Bold).scaled()
                )
                // Live progress toward the goal — kids see exactly what's left.
                Text(
                    if (trophy.earned) "✓ ${trophy.progress}" else trophy.progress,
                    color = if (trophy.earned) Color.White else lockedInk,
                    maxLines = 1, softWrap = false,
                    style = AbTypography.fixed(11f, FontWeight.ExtraBold).scaled(),
                    modifier = Modifier
                        .padding(top = AppDimens.Dimens2)
                        .background(pillInk.copy(alpha = if (trophy.earned) 0.22f else 0.12f), CircleShape)
                        .padding(horizontal = AppDimens.Dimens8, vertical = AppDimens.Dimens2)
                )
            }
            if (trophy.earned) {
                TrophySparkle(clock = clock, phase = phase, modifier = Modifier.align(Alignment.TopStart).padding(AppDimens.Dimens6))
            }
        }
        if (!trophy.earned) {
            TrophyLockBadge(
                sizeDp = 22.dp,
                angle = { trophyLockWiggle(clock.value, index * 0.6f) },
                modifier = Modifier.align(Alignment.TopEnd).padding(AppDimens.Dimens6)
            )
        }
    }
}

/** A soft white glint that sweeps across an earned tile every few seconds. */
@Composable
private fun TrophyShine(clock: State<Float>, phase: Float, corner: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.clip(RoundedCornerShape(corner))) {
        val cycle = 3.4f
        val c = ((clock.value + phase) % cycle) / cycle
        val sweep = if (c < 0.32f) c / 0.32f else 2f
        if (sweep <= 1f) {
            val w = size.width
            val h = size.height
            val bandW = w * 0.32f
            val x0 = w / 2f + (sweep - 0.5f) * 2f * w * 0.9f - bandW / 2f
            rotate(degrees = 18f, pivot = Offset(w / 2f, h / 2f)) {
                drawRect(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, Color.White.copy(alpha = 0.5f), Color.Transparent),
                        startX = x0,
                        endX = x0 + bandW
                    ),
                    topLeft = Offset(x0, -h * 0.4f),
                    size = Size(bandW, h * 1.8f)
                )
            }
        }
    }
}

/** A four-point twinkle in the corner of an earned tile. */
@Composable
private fun TrophySparkle(clock: State<Float>, phase: Float, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .size(12.dp)
            .graphicsLayer {
                val sc = 0.8f + 0.4f * sin(clock.value * 3.2f + phase)
                scaleX = sc
                scaleY = sc
                alpha = 0.9f
            }
    ) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f
        val path = Path()
        for (k in 0 until 8) {
            val rad = if (k % 2 == 0) r else r * 0.28f
            val a = -PI / 2.0 + k * PI / 4.0
            val x = c.x + (cos(a) * rad).toFloat()
            val y = c.y + (sin(a) * rad).toFloat()
            if (k == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, Color.White)
    }
}
