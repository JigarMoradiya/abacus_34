package com.jigar.me.ui.view.home.screens.math_game_zone

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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.PrimaryBlue
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun MathGameZoneScreen(
    gameType: (GameCategoryType) -> Unit,
    onBackClick: () -> Unit
) {
    // Ordered easy → hard with similar games grouped: quick number sense
    // first (a kid's very first game), then equation games, sum grids,
    // number puzzles, and pure logic last.
    val categories: List<GameCategoryData> = listOf(
        GameCategoryData(
            type = GameCategoryType.SPEED_COMPARE,
            title = stringResource(R.string.speed_compare),
            desc = stringResource(R.string.speed_compare_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.BALLOON_POP,
            title = stringResource(R.string.balloon_pop),
            desc = stringResource(R.string.balloon_pop_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.TRUE_FALSE,
            title = stringResource(R.string.true_false),
            desc = stringResource(R.string.true_false_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.MATH_BINGO,
            title = stringResource(R.string.math_bingo),
            desc = stringResource(R.string.math_bingo_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.MISSING_OPERATOR,
            title = stringResource(R.string.missing_operator),
            desc = stringResource(R.string.missing_operator_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.EQUATION_MATCH,
            title = stringResource(R.string.equation_match),
            desc = stringResource(R.string.equation_match_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.CROSS_MATH,
            title = stringResource(R.string.cross_math),
            desc = stringResource(R.string.cross_math_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.TARGET_NUMBER,
            title = stringResource(R.string.target_the_number),
            desc = stringResource(R.string.target_number_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.NUMBER_PATH,
            title = stringResource(R.string.number_path),
            desc = stringResource(R.string.number_path_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.MATH_PYRAMID,
            title = stringResource(R.string.math_pyramid),
            desc = stringResource(R.string.math_pyramid_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.MAGIC_SQUARE,
            title = stringResource(R.string.magic_square),
            desc = stringResource(R.string.magic_square_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.MERGE_2048,
            title = stringResource(R.string.merge2048_game),
            desc = stringResource(R.string.merge2048_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.NUMBER_SEQUENCE_PUZZLE,
            title = stringResource(R.string.number_sequence_puzzle),
            desc = stringResource(R.string.number_sequence_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.PLACE_VALUE,
            title = stringResource(R.string.place_value),
            desc = stringResource(R.string.place_value_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.CLOCK_MASTER,
            title = stringResource(R.string.clock_master),
            desc = stringResource(R.string.clock_master_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.SUDOKU,
            title = stringResource(R.string.sudoku),
            desc = stringResource(R.string.sudoku_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.CALCUDOKU,
            title = stringResource(R.string.calcudoku),
            desc = stringResource(R.string.calcudoku_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.KAKURO,
            title = stringResource(R.string.kakuro),
            desc = stringResource(R.string.kakuro_desc)
        )
    )

    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            BackButtonWithText(title = stringResource(R.string.math_game_zone), onBackClick = onBackClick)
            Spacer(Modifier.weight(1f))
            // Playful header hint
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6),
                modifier = Modifier
                    .padding(end = AppDimens.Dimens16)
                    .background(Color.White.copy(alpha = 0.85f), CircleShape)
                    .border(1.5.dp, PrimaryBlue.copy(alpha = 0.3f), CircleShape)
                    .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6)
            ) {
                Text(text = "🎮", style = MaterialTheme.typography.labelMedium.scaled())
                Text(
                    text = stringResource(R.string.pick_a_game_have_fun),
                    color = PrimaryBlue.copy(alpha = 0.9f),
                    fontFamily = FontFamily(Font(R.font.font_bold)),
                    style = MaterialTheme.typography.labelMedium.scaled()
                )
            }
        }

        // Category rows: 5 themed sections, each a horizontal row of big
        // tiles — vertical scroll between categories, the next section
        // peeking in from the bottom so kids know to scroll.
        val sections = listOf(
            GameSection("⚡", stringResource(R.string.game_cat_speedy), Color(0xFFFF8400), categories.subList(0, 4)),
            GameSection("➕", stringResource(R.string.game_cat_equations), Color(0xFF0074D5), categories.subList(4, 8)),
            GameSection("🗺️", stringResource(R.string.game_cat_adventures), Color(0xFF43A047), categories.subList(8, 12)),
            GameSection("🔢", stringResource(R.string.game_cat_builders), Color(0xFF8E24AA), categories.subList(12, 15)),
            GameSection("🧩", stringResource(R.string.game_cat_puzzles), Color(0xFF546E7A), categories.subList(15, 18))
        )

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val hPad = AppDimens.Dimens16
            val vPad = AppDimens.Dimens10
            val gap = AppDimens.Dimens12
            // Room inside the clipping scroll row so card shadows (and the
            // sticker tilt) aren't cut off at the top/bottom. The spot
            // shadow throws ~1.5x elevation downward, so this must exceed it.
            val shadowPad = AppDimens.Dimens12
            // Width-driven sizing: exactly 4 wide cards cover the screen
            // width — the 4-game categories fill their row edge to edge.
            val tileW = (maxWidth - hPad * 2 - gap * 3) / 4
            val tileH = tileW / 1.35f

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(gap),
                contentPadding = PaddingValues(vertical = vPad),
                modifier = Modifier.fillMaxSize()
            ) {
                items(sections, key = { it.title }) { section ->
                    Column {
                        SectionHeader(section, hPad)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(gap),
                            contentPadding = PaddingValues(horizontal = hPad, vertical = shadowPad)
                        ) {
                            itemsIndexed(section.games, key = { _, c -> c.type.name }) { i, category ->
                                GameZoneCard(
                                    category = category,
                                    width = tileW,
                                    height = tileH,
                                    appearIndex = i,
                                    onClick = { gameType.invoke(category.type) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class GameSection(
    val emoji: String,
    val title: String,
    val accent: Color,
    val games: List<GameCategoryData>
)

@Composable
private fun SectionHeader(section: GameSection, hPad: Dp) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = hPad)
            .padding(bottom = AppDimens.Dimens6)
    ) {
        // Playful sticker-style badge: gradient capsule, white outline,
        // tilted a touch like it was slapped on by hand.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6),
            modifier = Modifier
                .graphicsLayer { rotationZ = -1.5f }
                .shadow(AppDimens.Dimens4, CircleShape, ambientColor = section.accent, spotColor = section.accent)
                .background(
                    Brush.horizontalGradient(listOf(section.accent.copy(alpha = 0.8f), section.accent)),
                    CircleShape
                )
                .border(2.dp, Color.White.copy(alpha = 0.65f), CircleShape)
                .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens4)
        ) {
            Text(text = section.emoji, style = MaterialTheme.typography.labelLarge.scaled())
            Text(
                text = section.title,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                style = MaterialTheme.typography.labelLarge.scaled()
            )
        }
        // Soft rule line carrying the category color across the row.
        Box(
            modifier = Modifier
                .weight(1f)
                .height(3.dp)
                .background(section.accent.copy(alpha = 0.2f), CircleShape)
        )
    }
}

@Composable
private fun GameZoneCard(
    category: GameCategoryData,
    width: Dp,
    height: Dp,
    appearIndex: Int,
    onClick: () -> Unit
) {
    val style = gameCardStyle(category.type)
    val corner = min(width, height) * 0.16f
    val shape = RoundedCornerShape(corner)
    // Disc = iconArea * 1.28 must stay well inside the flexible icon zone
    // (0.69 x height) even while bobbing — 0.38 leaves clear air above.
    val iconArea = min(width * 0.62f, height * 0.38f)

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        label = "pressScale"
    )

    // Staggered pop-in on first show. Capped so cards scrolled into view
    // later don't sit invisible waiting out a long delay.
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(minOf(appearIndex, 10) * 45L)
        appeared = true
    }
    val appearScale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.5f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "appearScale"
    )
    val appearAlpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        label = "appearAlpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .size(width = width, height = height)
            .graphicsLayer {
                val scale = pressScale * appearScale
                scaleX = scale
                scaleY = scale
                alpha = appearAlpha
                // Sticker-book feel: neighbors lean opposite ways a touch.
                rotationZ = ((appearIndex % 3) - 1) * 1.2f
            }
            .shadow(
                elevation = AppDimens.Dimens6,
                shape = shape,
                ambientColor = style.dark,
                spotColor = style.dark
            )
            .background(
                brush = Brush.verticalGradient(listOf(style.light, style.dark)),
                shape = shape
            )
            .border(2.dp, Color.White.copy(alpha = 0.7f), shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
    ) {
        // Icon zone takes the flexible space so every card's icon sits at
        // the same height, no matter how the title below wraps. Centered:
        // the bob animation keeps an even margin to the title below.
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            // Programmatic icon on a soft white disc — always in gentle
            // motion: a slow float + sway, phase-shifted per card so
            // neighbors don't bob in sync.
            val bob by rememberInfiniteTransition(label = "bob").animateFloat(
                initialValue = 0f, targetValue = (2.0 * PI).toFloat(),
                animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing)),
                label = "bob"
            )
            val phase = appearIndex * 0.9f
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .graphicsLayer {
                        translationY = sin(bob + phase) * 2.dp.toPx()
                        rotationZ = sin(bob * 0.5f + phase) * 3f
                    }
                    .size(iconArea * 1.28f)
                    .background(Color.White.copy(alpha = 0.18f), CircleShape)
            ) {
                GameTileIcon(type = category.type, size = iconArea, tint = style.tint)
            }
        }

        // Fixed-height text zone: title starts right under the icon. Both
        // lines are single-line and auto-shrink to fit the card width, so
        // nothing can wrap past the card edge or get clipped by the shadow
        // shape (dp-fixed zone vs sp-sized text was cutting 2-line titles).
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .height(height * 0.30f)
                .padding(horizontal = width * 0.06f)
        ) {
            AutoShrinkText(
                text = category.title,
                color = Color.White,
                fontResId = R.font.font_extra_bold,
                style = if (DeviceInfo.isTablet) MaterialTheme.typography.titleSmall.scaled()
                else MaterialTheme.typography.labelMedium.scaled()
            )

            AutoShrinkText(
                text = category.desc,
                color = Color.White.copy(alpha = 0.85f),
                fontResId = R.font.font_bold,
                style = MaterialTheme.typography.labelSmall.scaled()
            )
        }
        Spacer(Modifier.height(height * 0.03f))
    }
}

// Single-line text that shrinks itself (down to 55%) until it fits the
// available width — long titles like "Number Sequence Puzzle" stay on one
// line instead of wrapping past the card's clipped shadow shape.
@Composable
private fun AutoShrinkText(
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
