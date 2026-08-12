package com.jigar.me.ui.view.home.screens.math_game_zone.trophy_room

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.screens.math_game_zone.common.ZoneBuddy
import com.jigar.me.ui.view.home.theme.AppDimens
import kotlinx.coroutines.delay
import kotlin.math.PI
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

    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BackButtonWithText(title = stringResource(R.string.trophy_room), onBackClick = onBackClick)
                Spacer(Modifier.weight(1f))
            }
            // Playful stat chips riding on the header.
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
                    onSelect = { viewModel.selectBuddy(it) }
                )
            }
            itemsIndexed(trophies, key = { _, t -> t.title }) { i, trophy ->
                TrophyCard(trophy, i)
            }
        }
    }
}

@Composable
private fun BuddyShelf(totalStars: Int, selected: String, onSelect: (String) -> Unit) {
    val t by rememberInfiniteTransition(label = "buddyBob").animateFloat(
        initialValue = 0f, targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label = "buddyBob"
    )
    // Title on the left, buddy list docked to the right in the same row —
    // each buddy's own icon sits above its label. The row carries its own
    // padding so a bobbing, rotating buddy at either end never gets clipped.
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(AppDimens.Dimens16))
            .border(2.dp, Color(0xFFFFB300).copy(alpha = 0.5f), RoundedCornerShape(AppDimens.Dimens16))
            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens8)
    ) {
        Text(
            stringResource(R.string.trophy_buddies),
            color = Color(0xFFE65100),
            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
            style = MaterialTheme.typography.labelLarge.scaled()
        )
        Spacer(Modifier.weight(1f))
        // Scrollable so all buddies never clip on narrow tablets or big fonts.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(vertical = AppDimens.Dimens8)
        ) {
        ZoneBuddy.all.forEachIndexed { i, def ->
            val unlocked = totalStars >= def.starsNeeded
            val isSelected = unlocked && selected == def.emoji
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = unlocked
                ) {
                    AudioPlayerManager.playSoundTilePlace()
                    onSelect(def.emoji)
                }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .graphicsLayer {
                            if (isSelected) {
                                translationY = sin(t + i) * 2.dp.toPx()
                                rotationZ = sin(t * 0.5f + i) * 6f
                            }
                        }
                        .background(
                            if (isSelected) Brush.verticalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFFA000)))
                            else Brush.verticalGradient(listOf(Color(0xFFF5F5F5), Color(0xFFE0E0E0))),
                            CircleShape
                        )
                        .border(
                            2.dp,
                            if (isSelected) Color(0xFFE65100) else Color(0xFFBDBDBD),
                            CircleShape
                        )
                        .padding(AppDimens.Dimens12)
                ) {
                    Text(
                        def.emoji,
                        style = MaterialTheme.typography.headlineMedium.scaled(),
                        modifier = Modifier.graphicsLayer { if (!unlocked) alpha = 0.35f }
                    )
                }
                Text(
                    if (unlocked) (if (isSelected) "✓" else " ") else "⭐${def.starsNeeded}",
                    color = if (isSelected) Color(0xFF2E7D32) else Color(0xFF8D6E63),
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                    style = MaterialTheme.typography.labelSmall.scaled()
                )
            }
        }
        }
    }
}

// Single-line text that shrinks itself until it fits — so trophy labels
// never clip, even at big system font sizes.
@Composable
private fun ShrinkText(
    text: String,
    color: Color,
    fontResId: Int,
    style: androidx.compose.ui.text.TextStyle
) {
    var scale by remember(text) { mutableStateOf(1f) }
    Text(
        text = text,
        color = color,
        textAlign = TextAlign.Center,
        maxLines = 1,
        softWrap = false,
        fontFamily = FontFamily(Font(fontResId)),
        style = style,
        fontSize = style.fontSize * scale,
        onTextLayout = { if (it.hasVisualOverflow && scale > 0.55f) scale *= 0.93f }
    )
}

@Composable
private fun StatChip(emoji: String, value: String, color: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.9f), CircleShape)
            .border(1.5.dp, color.copy(alpha = 0.4f), CircleShape)
            .padding(horizontal = AppDimens.Dimens10, vertical = AppDimens.Dimens4)
    ) {
        Text(emoji, style = MaterialTheme.typography.labelMedium.scaled())
        Text(
            value, color = color,
            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
            style = MaterialTheme.typography.labelMedium.scaled()
        )
    }
}

@Composable
private fun TrophyCard(trophy: Trophy, index: Int) {
    val shape = RoundedCornerShape(AppDimens.Dimens16)

    // Staggered pop-in, and earned trophies wiggle proudly forever.
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(minOf(index, 10) * 50L); appeared = true }
    val appearScale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.4f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "appear"
    )
    val t by rememberInfiniteTransition(label = "wiggle").animateFloat(
        initialValue = 0f, targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing)),
        label = "wiggle"
    )
    val phase = index * 0.8f

    // Every trophy wears its own color; locked ones stay muted gray so the
    // earned ones pop like a real collection shelf.
    val gradient = if (trophy.earned) {
        Brush.verticalGradient(
            listOf(androidx.compose.ui.graphics.lerp(trophy.accent, Color.White, 0.3f), trophy.accent)
        )
    } else {
        Brush.verticalGradient(listOf(Color(0xFFCFD8DC), Color(0xFF90A4AE)))
    }
    val glowColor = if (trophy.earned) trophy.accent else Color(0xFF90A4AE)

    Box(
        modifier = Modifier
            // .clip() below is the safety net that keeps content from
            // bleeding into the row beneath, so this can stay close to the
            // original card shape instead of a tall square.
            .aspectRatio(1.15f)
            .graphicsLayer {
                scaleX = appearScale; scaleY = appearScale
                alpha = if (appeared) 1f else 0f
                rotationZ = ((index % 3) - 1) * 1.2f
            }
            .shadow(AppDimens.Dimens4, shape, ambientColor = glowColor, spotColor = glowColor)
            .clip(shape)
            .background(gradient, shape)
            .border(2.dp, Color.White.copy(alpha = 0.7f), shape)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(horizontal = AppDimens.Dimens8)
        ) {
            Text(
                trophy.emoji,
                fontSize = 40.sp.scaled(),
                modifier = Modifier.graphicsLayer {
                    if (trophy.earned) {
                        rotationZ = sin(t + phase) * 8f
                        translationY = sin(t * 2f + phase) * 2.dp.toPx()
                    } else {
                        alpha = 0.45f
                    }
                }
            )
            ShrinkText(
                trophy.title,
                color = Color.White,
                fontResId = R.font.font_extra_bold,
                style = MaterialTheme.typography.labelMedium.scaled()
            )
            ShrinkText(
                trophy.desc,
                color = Color.White.copy(alpha = 0.9f),
                fontResId = R.font.font_bold,
                style = MaterialTheme.typography.labelSmall.scaled()
            )
            // Live progress toward the goal — kids see exactly what's left.
            Text(
                if (trophy.earned) "✓ ${trophy.progress}" else trophy.progress,
                color = Color.White,
                maxLines = 1, softWrap = false,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                style = MaterialTheme.typography.labelSmall.scaled(),
                modifier = Modifier
                    .padding(top = AppDimens.Dimens2)
                    .background(Color.Black.copy(alpha = 0.22f), CircleShape)
                    .padding(horizontal = AppDimens.Dimens8, vertical = AppDimens.Dimens2)
            )
        }
        if (!trophy.earned) {
            Text(
                "🔒",
                style = MaterialTheme.typography.labelLarge.scaled(),
                modifier = Modifier.align(Alignment.TopEnd).padding(AppDimens.Dimens6)
            )
        }
    }
}
