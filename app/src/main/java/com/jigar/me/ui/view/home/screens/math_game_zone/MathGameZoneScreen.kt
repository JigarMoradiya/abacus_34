package com.jigar.me.ui.view.home.screens.math_game_zone

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
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

    // Row layout: phone 9 + 9, tablet 6 + 6 + 6 — everything fits with no scrolling
    val rowCounts = if (DeviceInfo.isTablet) listOf(6, 6, 6) else listOf(9, 9)
    val rows = remember(categories, rowCounts) {
        var index = 0
        rowCounts.map { count ->
            val end = minOf(index + count, categories.size)
            categories.subList(index, end).also { index = end }
        }
    }

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

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val hPad = AppDimens.Dimens16
            val vPad = AppDimens.Dimens10
            val gap = AppDimens.Dimens10
            val maxCols = rows.maxOf { it.size }
            val tileW = (maxWidth - hPad * 2 - gap * (maxCols - 1)) / maxCols
            val tileH = (maxHeight - vPad * 2 - gap * (rows.size - 1)) / rows.size

            Column(
                verticalArrangement = Arrangement.spacedBy(gap),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = hPad, vertical = vPad)
            ) {
                var index = 0
                rows.forEach { rowItems ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(gap, Alignment.CenterHorizontally),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowItems.forEach { category ->
                            GameZoneCard(
                                category = category,
                                width = tileW,
                                height = tileH,
                                appearIndex = index++,
                                onClick = { gameType.invoke(category.type) }
                            )
                        }
                    }
                }
            }
        }
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
    val iconArea = min(width * 0.62f, height * 0.48f)

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        label = "pressScale"
    )

    // Staggered pop-in on first show
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(appearIndex * 45L)
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
        // Programmatic icon on a soft white disc
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(iconArea * 1.28f)
                .background(Color.White.copy(alpha = 0.18f), CircleShape)
        ) {
            GameTileIcon(type = category.type, size = iconArea, tint = style.tint)
        }

        Spacer(Modifier.size(height * 0.045f))

        Text(
            text = category.title,
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 2,
            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
            style = if (DeviceInfo.isTablet) MaterialTheme.typography.titleMedium.scaled()
            else MaterialTheme.typography.labelMedium.scaled(),
            modifier = Modifier.padding(horizontal = width * 0.06f)
        )

        if (DeviceInfo.isTablet) {
            Text(
                text = category.desc,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                maxLines = 1,
                fontFamily = FontFamily(Font(R.font.font_bold)),
                style = MaterialTheme.typography.labelSmall.scaled(),
                modifier = Modifier.padding(horizontal = width * 0.06f)
            )
        }
    }
}
