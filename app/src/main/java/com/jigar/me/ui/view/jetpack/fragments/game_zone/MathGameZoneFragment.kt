package com.jigar.me.ui.view.jetpack.fragments.game_zone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jigar.me.R
import dagger.hilt.android.AndroidEntryPoint

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText

@AndroidEntryPoint
class MathGameZoneFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    MathGameZoneScreen(
                        gameType = { type ->
                            when (type) {
                                GameCategoryType.NUMBER_SEQUENCE_PUZZLE -> {
                                    findNavController().navigate(R.id.toNumberSequencePuzzleHomeFragment)
                                }
                                GameCategoryType.SUDOKU -> {
                                    findNavController().navigate(R.id.toSudokuHomeFragment)
                                }
                                GameCategoryType.MATH_PYRAMID -> {
                                    findNavController().navigate(R.id.toMathPyramidHomeFragment)
                                }
                                GameCategoryType.TARGET_NUMBER -> {
                                    findNavController().navigate(R.id.toTargetNumberHomeFragment)
                                }
                            }
                        },
                        onBackClick = { findNavController().popBackStack() }
                    )
                }
            }
        }
    }
}


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
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            BackButtonWithText(title = stringResource(R.string.math_game_zone), onBackClick = onBackClick)

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                categories.forEach { category ->

                    val shape = RoundedCornerShape(12.dp)
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
                            .padding(horizontal = 16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = category.type.toDrawable()),
                            contentDescription = category.type.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxHeight(0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = category.title,
                            color = colorResource(R.color.black),
                            fontFamily = FontFamily(Font( R.font.font_extra_bold)),
                            fontSize = dimensionResource(id = R.dimen.textSize17).value.sp,
                            lineHeight = 18.sp
                        )
                        Text(
                            text = category.desc,
                            color = colorResource(R.color.black).copy(alpha = 0.8f),
                            fontFamily = FontFamily(Font( R.font.font_bold)),
                            fontSize = dimensionResource(id = R.dimen.textSizeMedium).value.sp,
                            lineHeight = 18.sp
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
    }
}


@Preview(
    name = "Math Game Zone - Landscape",
    showBackground = true,
    widthDp = 800,
    heightDp = 400
)
@Composable
fun PreviewMathGameZoneScreenLandscape() {
    MaterialTheme {
        MathGameZoneScreen(
            gameType = { },
            onBackClick = { }
        )
    }
}

