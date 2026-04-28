package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HowToPlayButton
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlaySudokuView
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.components.SudokuDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.components.SudokuHomeViewModel
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.components.SudokuSize
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.components.SudokuStorage

@Composable
fun SudokuHomeScreen(
    viewModel: SudokuHomeViewModel,
    navController: NavController,
    onStart: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val sizes = listOf(SudokuSize.FOUR, SudokuSize.SIX, SudokuSize.NINE)
    var showHelp by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Column(modifier = Modifier.fillMaxSize()) {

            Row {
                BackButtonWithText(title = stringResource(R.string.sudoku), modifier = Modifier.weight(1f),onBackClick = { navController.popBackStack() })
                HowToPlayButton {
                    showHelp = true
                }
            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.activity_padding16)),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                sizes.forEach { s ->
                    val isSelected = state.selectedSize == s
                    val shape = RoundedCornerShape(200.dp)

                    val animatedPadding by animateDpAsState(
                        targetValue = if (isSelected)
                            dimensionResource(R.dimen.activity_padding4)
                        else
                            dimensionResource(R.dimen.activity_padding24),
                        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
                        label = ""
                    )

                    val animatedShadow by animateDpAsState(
                        targetValue = if (isSelected) 16.dp else 4.dp,
                        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
                        label = ""
                    )

                    val animatedScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.05f else 1f,
                        animationSpec = spring(dampingRatio = 0.6f, stiffness = 250f),
                        label = ""
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(animatedPadding)
                            .shadow(
                                elevation = animatedShadow,
                                shape = shape,
                                clip = false
                            )
                            .clip(shape)
                            .graphicsLayer {
                                scaleX = animatedScale
                                scaleY = animatedScale
                            }
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = LocalIndication.current
                            ) {
                                viewModel.selectSize(s)
                            },
                        contentAlignment = Alignment.Center
                    ) {

                        Image(
                            painter = painterResource(
                                id = when (s) {
                                    SudokuSize.FOUR -> R.drawable.sudoku_4
                                    SudokuSize.SIX -> R.drawable.sudoku_6
                                    SudokuSize.NINE -> R.drawable.sudoku_9
                                }
                            ),
                            contentScale = ContentScale.Fit,
                            contentDescription = null
                        )
                    }

                }
            }


            Spacer(Modifier.weight(1f))

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
                Box(modifier = Modifier.shadow(elevation = 8.dp, shape = shape, clip = false)) {
                    Button(
                        onClick = {
                            if (SudokuStorage.hasSavedGame(context)) {
                                viewModel.openResumePopup()
                            } else {
                                viewModel.setDataGameStart(state.selectedSize, state.selectedDifficulty, true)
                                onStart()
                            }
                        },
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
                            Text(
                                text = stringResource(R.string.lets_play),
                                fontSize = dimensionResource(R.dimen.textSizeSuperExtraLarge).value.sp,
                                fontFamily = FontFamily(Font(R.font.font_bold))
                            )
                            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.activity_padding6)))
                            Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null)
                        }
                    }
                }

            }
        }

        AnimatedVisibility(
            visible = showHelp,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            HowToPlaySudokuView {
                showHelp = false
            }
        }

        AnimatedVisibility(
            visible = state.showResumePopup,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CustomPopupView(
                title = stringResource(R.string.continue_previous_game),
                description = stringResource(R.string.you_have_an_unfinished_sudoku),
                positiveButtonText = stringResource(R.string.yes_please_continue),
                negativeButtonText = stringResource(R.string.start_this_new_sudoku),
                widthMultiplier = 0.5f,
                onPositiveTapped = {
                    viewModel.closeResumePopup()
                    val saved = SudokuStorage.load(context)
                    if (saved != null) {
                        viewModel.setDataGameStart(saved.puzzle.size, saved.puzzle.difficulty, false)
                        onStart()
                    } else {
                        viewModel.setDataGameStart(state.selectedSize, state.selectedDifficulty, true)
                        onStart()
                    }
                },
                onNegativeTapped = {
                    viewModel.closeResumePopup()
                    viewModel.setDataGameStart(state.selectedSize, state.selectedDifficulty, true)
                    onStart()
                }
            )
        }
    }
}

@Composable
fun DifficultySelectorCompose(
    selected: SudokuDifficulty4,
    onSelect: (SudokuDifficulty4) -> Unit
) {
    Row(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.75f), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SudokuDifficulty4.entries.forEach { d ->
            val isSelected = d == selected
            val shape = RoundedCornerShape(20.dp)
            Surface(
                modifier = Modifier
                    .padding(horizontal = 4.dp),
                shape = shape,
                color = if (isSelected) colorResource(R.color.colorEditTextBlack_33) else Color.White,
                shadowElevation = if (isSelected) 8.dp else 0.dp,
                tonalElevation = if (isSelected) 4.dp else 0.dp,
                border = if (!isSelected) BorderStroke(1.dp, Color.LightGray) else null
            ) {
                Box(
                    modifier = Modifier
                        .clip(shape)
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
