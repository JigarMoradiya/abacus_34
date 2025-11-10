package com.jigar.me.ui.view.jetpack.fragments.number_sequence_puzzle.components


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.fragments.number_sequence_puzzle.NumberSequencePuzzleHomeJetpackFragmentDirections


@Composable
fun NumberSequencePuzzleHomeJetpackScreen(
    navController: NavController, onBackClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🔹 Header Bar
            HeaderBar(onBackClick = onBackClick)

            Spacer(Modifier.weight(1f))

            // 🔹 Logo + Title + Subtitle + Puzzle Options
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.ic_number_sequences_puzzle_logo),
                    contentDescription = "Number Puzzle Logo",
                    modifier = Modifier
                        .height(dimensionResource(R.dimen._72dp))
                        .fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )

                // Title
                Text(
                    text = stringResource(R.string.select_the_puzzle_dashboard),
                    fontFamily = FontFamily(Font(R.font.font_bold)),
                    fontSize = dimensionResource(id = R.dimen.textSize20).value.sp,
                    color = colorResource(id = R.color.colorBlueDark),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.activity_padding16))
                )

                // Subtitle
                Text(
                    text = stringResource(R.string.arrange_all_the_numbers_of_puzzle_in_ascending_order),
                    fontFamily = FontFamily(Font(R.font.font_bold)),
                    fontSize = dimensionResource(id = R.dimen.textSizeLarge).value.sp,
                    color = Color(0xFFC06A6A),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                // Puzzle Options
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.activity_padding16))
                ) {
                    PuzzleOptionView(
                        gridSize = 3,
                        color = Color(0xFF9C27B0),
                        onClick = {
                            val action = NumberSequencePuzzleHomeJetpackFragmentDirections.toNumberSequencePuzzleJetpackFragment(3)
                            navController.navigate(action)
                        })
                    PuzzleOptionView(
                        gridSize = 4,
                        color = Color(0xFFFF9800),
                        onClick = {
                            val action = NumberSequencePuzzleHomeJetpackFragmentDirections.toNumberSequencePuzzleJetpackFragment(4)
                            navController.navigate(action)
                        })
                    PuzzleOptionView(
                        gridSize = 5,
                        color = Color(0xFF2196F3),
                        onClick = {
                            val action = NumberSequencePuzzleHomeJetpackFragmentDirections.toNumberSequencePuzzleJetpackFragment(5)
                            navController.navigate(action)
                        })
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
fun HeaderBar(
    title: String = "Number Sequences Puzzle", onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
        ) {
            Card(
                onClick = { onBackClick() },
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.light_back)
                ),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.menu_icons_corner)),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = dimensionResource(id = R.dimen.card_elevation)
                ),
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.icons_margin2))
                    .size(dimensionResource(id = R.dimen.menu_icons))
            ) {
                Box(
                    contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.menu_icons))
                            .padding(dimensionResource(id = R.dimen.menu_icons_padding))
                    )
                }
            }

            // 🔹 Center Title (same as MaterialTextView in XML)
            Text(
                text = title,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                fontSize = dimensionResource(id = R.dimen.textSize24).value.sp,
                color = colorResource(id = R.color.colorPrimaryDark),
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.CenterHorizontally),
            )

        }
    }
}

@Composable
fun PuzzleOptionView(
    gridSize: Int,
    color: Color,
    onClick: () -> Unit
) {
    val boxSize = dimensionResource(R.dimen._72dp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.size(boxSize)
        ) {
            // Outer border rectangle
            Canvas(modifier = Modifier.matchParentSize()) {
                drawRoundRect(
                    color = color,
                    size = size,
                    style = Stroke(width = 4f)
                )
            }

            // Calculate cell size in dp (minus border lines)
            val cellSize = with(LocalDensity.current) {
                (boxSize.toPx() / gridSize).toDp()
            }

            // Fixed-size grid
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                repeat(gridSize) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(gridSize) {
                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .border(
                                        width = 0.5.dp,
                                        color = color.copy(alpha = 1f)
                                    )
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = "$gridSize x $gridSize Puzzle",
            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
            fontSize = dimensionResource(id = R.dimen.textSizeRegular).value.sp,
            color = color,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.activity_padding8))
        )
    }
}