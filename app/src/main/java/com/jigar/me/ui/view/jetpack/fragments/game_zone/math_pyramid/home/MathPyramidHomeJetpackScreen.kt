package com.jigar.me.ui.view.jetpack.fragments.game_zone.math_pyramid.home


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText
import com.jigar.me.ui.view.jetpack.fragments.common.enums.CommonDifficulty4
import com.jigar.me.ui.view.jetpack.fragments.game_zone.math_pyramid.home.components.MathPyramidViewModel

@Composable
fun MathPyramidHomeJetpackScreen(
    viewModel: MathPyramidViewModel,
    onStartGame: (Int, CommonDifficulty4) -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    val levelRange = 2..6

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            BackButtonWithText(
                title = stringResource(R.string.math_pyramid),
                onBackClick = onBackClick
            )

            Spacer(Modifier.weight(1f))

            // LEVEL SELECTOR ------------------------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                levelRange.forEach { level ->

                    val shape = RoundedCornerShape(12.dp)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(shape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = LocalIndication.current
                            ) {
                                viewModel.selectLevel(level)
                            }
                            .padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = getDrawableForPyramid(level)),
                            contentDescription = "pyramid_$level",
                            contentScale = ContentScale.Fit
                        )

                        Box(
                            modifier = Modifier.height(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Level $level",
                                color = if (state.selectedLevel == level) colorResource(R.color.black) else colorResource(R.color.black_text),
                                fontFamily = FontFamily(Font(if (state.selectedLevel == level) R.font.font_bold else R.font.font_regular)),
                                fontSize = dimensionResource(
                                    id = if (state.selectedLevel == level) R.dimen.textSize24 else R.dimen.questionSize).value.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // DIFFICULTY + START BUTTON --------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DifficultySelectorCompose(
                    selected = state.selectedDifficulty,
                    onSelect = { viewModel.selectDifficulty(it) }
                )

                Spacer(Modifier.weight(1f))

                val shape = RoundedCornerShape(50)
                Box(modifier = Modifier.shadow(elevation = 8.dp,shape = shape, clip = false)) {
                    Button(
                        onClick = { onStartGame(state.selectedLevel, state.selectedDifficulty) },
                        shape = shape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.colorPrimary),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(
                            horizontal = dimensionResource(R.dimen.activity_padding16)
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = stringResource(R.string.start_game), fontSize = dimensionResource(R.dimen.textSizeSuperExtraLarge).value.sp,
                                fontFamily = FontFamily(Font(R.font.font_bold)))
                            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.activity_padding6)))
                            Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null)
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun getDrawableForPyramid(level: Int): Int {
    return when (level) {
        2 -> R.drawable.pyramid_2
        3 -> R.drawable.pyramid_3
        4 -> R.drawable.pyramid_4
        5 -> R.drawable.pyramid_5
        6 -> R.drawable.pyramid_6
        else -> R.drawable.pyramid_4
    }
}


@Composable
fun DifficultySelectorCompose(
    selected: CommonDifficulty4,
    onSelect: (CommonDifficulty4) -> Unit
) {
    Row(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.75f), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CommonDifficulty4.entries.forEach { d ->
            val isSelected = d == selected
            val shape = RoundedCornerShape(20.dp)
            // Surface renders the elevation (shadow). Do NOT clip the Surface itself.
            Surface(
                modifier = Modifier
                    .padding(horizontal = 4.dp),
                shape = shape,
                color = if (isSelected) colorResource(R.color.colorPrimaryDark) else Color.White,
                shadowElevation = if (isSelected) 8.dp else 0.dp, // elevation visible because Surface is not clipped
                tonalElevation = if (isSelected) 4.dp else 0.dp,
                border = if (!isSelected) BorderStroke(1.dp, Color.LightGray) else null
            ) {
                // Clip and clickable are applied INSIDE Surface so ripple is rounded,
                // but Surface remains unclipped so shadow renders.
                Box(
                    modifier = Modifier
                        .clip(shape) // <-- clipped here so ripple gets rounded bounds
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current
                        ) {
                            onSelect(d)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = d.displayName,
                        fontSize = if (isSelected)
                            dimensionResource(id = R.dimen.textSizeSuperExtraLarge).value.sp
                        else
                            dimensionResource(id = R.dimen.textSizeRegular).value.sp,
                        color = if (isSelected) Color.White else colorResource(R.color.black_text),
                        fontFamily = FontFamily(Font(if (isSelected) R.font.font_bold else R.font.font_regular))
                    )
                }
            }
        }
    }
}

//@Preview(
//    name = "Math Pyramid Home - Light",
//    showBackground = true,
//    backgroundColor = 0xFFFFFFFF,
//    widthDp = 800,
//    heightDp = 400
//)
//@Composable
//fun NumberPuzzleHomeScreenPreview() {
//    MaterialTheme {
//        MathPyramidHomeJetpackScreen(
//            onStartGame = { levels, difficulty -> },
//            onBackClick = {}
//        )
//    }
//}