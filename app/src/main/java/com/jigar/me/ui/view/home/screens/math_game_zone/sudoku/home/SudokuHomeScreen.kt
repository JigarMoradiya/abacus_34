package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.home

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeLayout

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton


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
import androidx.compose.material3.MaterialTheme
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
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import androidx.compose.ui.text.font.FontWeight
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.common_ui.dialogs.PopupTheme
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
        GameHomeLayout(
            fillHeight = true,
            header = {Row(verticalAlignment = Alignment.CenterVertically) {
                BackButtonWithText(title = stringResource(R.string.sudoku), modifier = Modifier.weight(1f),onBackClick = { navController.safePopBackStack() })
                GameButton(
                    modifier = Modifier.padding(end = Dimens16),
                    text = stringResource(R.string.how_to_play),
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    type = ButtonType.PINK,
                    isSmall = true,
                    onClick = {
                        showHelp = true
                    }
                )
            }},
            bottom = {Row(
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

                GameButton(
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

            }},
        ) {Row(
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

                    val interactionSource = remember { MutableInteractionSource() }
                    val solved = viewModel.solvedCount(s)

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                // Size the icon from the row HEIGHT, not width/3 —
                                // on landscape phones width/3 is taller than the row
                                // and the icons blew past the screen
                                .aspectRatio(1f, matchHeightConstraintsFirst = true)
                                .padding(AppDimens.Dimens12)
                                .graphicsLayer {
                                    scaleX = animatedScale
                                    scaleY = animatedScale
                                }
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) {
                                    viewModel.selectSize(s)
                                },

                            contentAlignment = Alignment.Center
                        ) {
                            // A drawn, animated toy board with the real Sudoku grid for this size
                            // (replaces the old sudoku_4/6/9 pictures)
                            SudokuSizeIcon(
                                size = s,
                                selected = isSelected,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        // Solved count for this size — so it's obvious which
                        // size was beaten.
                        if (solved > 0) {
                            Text(
                                text = "🧩 " + stringResource(R.string.zone_solved_count, solved),
                                style = AbTypography.fixed(11f * LocalAbScale.current.factor, FontWeight.Bold),
                                color = LocalAbColors.current.brandPrimary
                            )
                        }
                    }

                }
            }}

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
            },
            icon = R.drawable.ic_alert,
            theme = PopupTheme.CONFIRM,
            accent = ButtonType.BLUE
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
