package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.home


import com.jigar.me.ui.view.home.navigation.safePopBackStack
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.rounded.PlayArrow
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
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlaySudokuView
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SudokuDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SudokuSize
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.generator.SudokuStorage
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.ButtonType

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

            Row(verticalAlignment = Alignment.CenterVertically) {
                BackButtonWithText(title = stringResource(R.string.sudoku), modifier = Modifier.weight(1f),onBackClick = { navController.safePopBackStack() })
                KidsActionButton(
                    modifier = Modifier.padding(end = Dimens16),
                    text = stringResource(R.string.how_to_play),
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    type = ButtonType.PINK,
                    isSmall = true,
                    onClick = {
                        showHelp = true
                    }
                )
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Dimens16),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                sizes.forEach { s ->
                    val isSelected = state.selectedSize == s
                    val animatedScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.0f else 0.82f,
                        animationSpec = spring(
                            dampingRatio = 0.6f,
                            stiffness = 300f
                        ),
                        label = ""
                    )

                    val animatedShadow by animateDpAsState(
                        targetValue = if (isSelected) Dimens16 else AppDimens.Dimens4,
                        animationSpec = spring(),
                        label = ""
                    )

                    val shape = RoundedCornerShape(AppDimens.Dimens200)
                    val interactionSource = remember { MutableInteractionSource() }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            // Size the circle from the row HEIGHT, not width/3 —
                            // on landscape phones width/3 is taller than the row
                            // and the circles blew past the screen
                            .aspectRatio(1f, matchHeightConstraintsFirst = true)
                            .padding(AppDimens.Dimens12)
                            .graphicsLayer {
                                scaleX = animatedScale
                                scaleY = animatedScale
                            }
                            .shadow(
                                elevation = animatedShadow,
                                shape = shape,
                                clip = false
                            )
                            .clip(shape)
                            .background(Color.Transparent)
                            .clickable(
                                interactionSource = interactionSource,
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens16),
                horizontalArrangement = Arrangement.spacedBy(Dimens16, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DifficultySelectorCompose(
                    selected = state.selectedDifficulty,
                    onSelect = { viewModel.selectDifficulty(it) }
                )

                KidsActionButton(
                    text = stringResource(R.string.lets_start),
                    icon = Icons.Rounded.PlayArrow,
                    type = ButtonType.ORANGE,
                    isIconStart = false,
                    onClick = {
                        if (SudokuStorage.hasSavedGame(context)) {
                            viewModel.openResumePopup()
                        } else {
                            viewModel.setDataGameStart(state.selectedSize, state.selectedDifficulty, true)
                            onStart()
                        }
                    }
                )

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

@Composable
fun DifficultySelectorCompose(
    selected: SudokuDifficulty4,
    onSelect: (SudokuDifficulty4) -> Unit
) {
    // Shared iOS-matching pill box (same as every other Math Game Zone game)
    com.jigar.me.ui.view.home.common_ui.DifficultyPillBox {
        SudokuDifficulty4.entries.forEach { d ->
            com.jigar.me.ui.view.home.common_ui.DifficultyPillItem(
                text = d.displayName,
                isSelected = d == selected
            ) { onSelect(d) }
        }
    }
}
