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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.theme.AppDimens
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

private data class Trophy(
    val emoji: String,
    val title: String,
    val desc: String,
    val earned: Boolean
)

@Composable
fun TrophyRoomScreen(
    onBackClick: () -> Unit,
    viewModel: TrophyRoomViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.refresh() }

    // The first 11 trophies come straight from stats; Champion needs their
    // earned count, so it is appended after.
    val base = listOf(
        Trophy("🎮", stringResource(R.string.trophy_first_game), stringResource(R.string.trophy_first_game_desc), stats.gamesPlayed >= 1),
        Trophy("🧭", stringResource(R.string.trophy_explorer), stringResource(R.string.trophy_explorer_desc), stats.gamesPlayed >= 6),
        Trophy("🧠", stringResource(R.string.trophy_all_rounder), stringResource(R.string.trophy_all_rounder_desc), stats.gamesPlayed >= 18),
        Trophy("🌟", stringResource(R.string.trophy_star_10), stringResource(R.string.trophy_star_10_desc), stats.totalStars >= 10),
        Trophy("💫", stringResource(R.string.trophy_star_50), stringResource(R.string.trophy_star_50_desc), stats.totalStars >= 50),
        Trophy("👑", stringResource(R.string.trophy_star_150), stringResource(R.string.trophy_star_150_desc), stats.totalStars >= 150),
        Trophy("🗺️", stringResource(R.string.trophy_levels_10), stringResource(R.string.trophy_levels_10_desc), stats.levelsDone >= 10),
        Trophy("🚀", stringResource(R.string.trophy_levels_50), stringResource(R.string.trophy_levels_50_desc), stats.levelsDone >= 50),
        Trophy("✨", stringResource(R.string.trophy_perfect_5), stringResource(R.string.trophy_perfect_5_desc), stats.threeStars >= 5),
        Trophy("🔥", stringResource(R.string.trophy_hard_hero), stringResource(R.string.trophy_hard_hero_desc), stats.hardThreeStar),
        Trophy("💨", stringResource(R.string.trophy_speed_200), stringResource(R.string.trophy_speed_200_desc), stats.maxBest >= 200)
    )
    val trophies = base + Trophy(
        "🏆", stringResource(R.string.trophy_champion), stringResource(R.string.trophy_champion_desc),
        base.count { it.earned } >= 8
    )
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
                StatChip("🏅", "$earnedCount/12", Color(0xFFE91E63))
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
            contentPadding = PaddingValues(
                start = AppDimens.Dimens16, end = AppDimens.Dimens16,
                top = AppDimens.Dimens10, bottom = AppDimens.Dimens16
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(trophies, key = { _, t -> t.title }) { i, trophy ->
                TrophyCard(trophy, i)
            }
        }
    }
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

    val gradient = if (trophy.earned) {
        Brush.verticalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFFA000)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFFCFD8DC), Color(0xFF90A4AE)))
    }

    Box(
        modifier = Modifier
            .aspectRatio(1.45f)
            .graphicsLayer {
                scaleX = appearScale; scaleY = appearScale
                alpha = if (appeared) 1f else 0f
                rotationZ = ((index % 3) - 1) * 1.2f
            }
            .shadow(AppDimens.Dimens4, shape,
                ambientColor = if (trophy.earned) Color(0xFFFFA000) else Color(0xFF90A4AE),
                spotColor = if (trophy.earned) Color(0xFFFFA000) else Color(0xFF90A4AE))
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
                style = MaterialTheme.typography.headlineMedium.scaled(),
                modifier = Modifier.graphicsLayer {
                    if (trophy.earned) {
                        rotationZ = sin(t + phase) * 8f
                        translationY = sin(t * 2f + phase) * 2.dp.toPx()
                    } else {
                        alpha = 0.45f
                    }
                }
            )
            Text(
                trophy.title,
                color = Color.White, textAlign = TextAlign.Center, maxLines = 1, softWrap = false,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                style = MaterialTheme.typography.labelMedium.scaled()
            )
            Text(
                trophy.desc,
                color = Color.White.copy(alpha = 0.9f), textAlign = TextAlign.Center, maxLines = 1, softWrap = false,
                fontFamily = FontFamily(Font(R.font.font_bold)),
                style = MaterialTheme.typography.labelSmall.scaled()
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
