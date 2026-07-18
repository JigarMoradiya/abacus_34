package com.jigar.me.ui.view.home.screens.math_game_zone

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun MathGameZoneScreen(
    gameType: (GameCategoryType) -> Unit,
    onBackClick: () -> Unit
) {
    val categories: List<GameCategoryData> = listOf(
        GameCategoryData(
            type = GameCategoryType.NUMBER_SEQUENCE_PUZZLE,
            title = stringResource(R.string.number_sequence_puzzle),
            desc = stringResource(R.string.number_sequence_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.SUDOKU,
            title = stringResource(R.string.sudoku),
            desc = stringResource(R.string.sudoku_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.MATH_PYRAMID,
            title = stringResource(R.string.math_pyramid),
            desc = stringResource(R.string.math_pyramid_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.TARGET_NUMBER,
            title = stringResource(R.string.target_the_number),
            desc = stringResource(R.string.target_number_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.BALLOON_POP,
            title = stringResource(R.string.balloon_pop),
            desc = stringResource(R.string.balloon_pop_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.SPEED_COMPARE,
            title = stringResource(R.string.speed_compare),
            desc = stringResource(R.string.speed_compare_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.MISSING_OPERATOR,
            title = stringResource(R.string.missing_operator),
            desc = stringResource(R.string.missing_operator_desc)
        ),
        GameCategoryData(
            type = GameCategoryType.MAGIC_SQUARE,
            title = stringResource(R.string.magic_square),
            desc = stringResource(R.string.magic_square_desc)
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {

            BackButtonWithText(title = stringResource(R.string.math_game_zone), onBackClick = onBackClick)

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                categories.forEach { category ->

                    val shape = RoundedCornerShape(AppDimens.Dimens12)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(shape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = LocalIndication.current
                            ) {
                                gameType.invoke(category.type)
                            }
                            .padding(horizontal = AppDimens.Dimens16)
                    ) {
                        Image(
                            painter = painterResource(id = category.type.toDrawable()),
                            contentDescription = category.type.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxHeight(0.7f)
                        )
                        Spacer(modifier = Modifier.height(AppDimens.Dimens8))
                        Text(
                            text = category.title,
                            color = colorResource(R.color.black),
                            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                            style = if (DeviceInfo.isTablet) MaterialTheme.typography.titleLarge.scaled() else MaterialTheme.typography.titleSmall.scaled(),
                        )
                        Text(
                            text = category.desc,
                            color = colorResource(R.color.black).copy(alpha = 0.8f),
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            style = if (DeviceInfo.isTablet) MaterialTheme.typography.bodyMedium.scaled() else MaterialTheme.typography.labelMedium.scaled(),
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

        }
    }
}

fun GameCategoryType.toDrawable(): Int {
    return when (this) {
        GameCategoryType.NUMBER_SEQUENCE_PUZZLE -> R.drawable.number_sequence_puzzle
        GameCategoryType.SUDOKU -> R.drawable.sudoku
        GameCategoryType.MATH_PYRAMID -> R.drawable.math_pyramid
        GameCategoryType.TARGET_NUMBER -> R.drawable.target_number
        GameCategoryType.BALLOON_POP -> R.drawable.balloon_pop
        GameCategoryType.SPEED_COMPARE -> R.drawable.speed_compare
        GameCategoryType.MISSING_OPERATOR -> R.drawable.missing_operator
        GameCategoryType.MAGIC_SQUARE -> R.drawable.magic_square
    }
}
