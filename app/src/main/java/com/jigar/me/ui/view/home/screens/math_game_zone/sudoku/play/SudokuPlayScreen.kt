package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton

import com.jigar.me.ui.view.home.navigation.safePopBackStack
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.common_ui.dialogs.CommonLoadingView
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.screens.math_game_zone.common.AbDebugFlags
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameResultCard
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameStatChip
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.components.SudokuNumberPad
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.components.SudokuBoard
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SudokuPlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.getButtonColors
import kotlinx.coroutines.delay

@Composable
fun SudokuPlayScreen(
    navController: NavController,
    vm: SudokuPlayViewModel,
    modifier: Modifier = Modifier
) {
    if (vm.isLoading) {
        CommonLoadingView(
            title = stringResource(R.string.please_wait),
            text = stringResource(R.string.making_your_sudoku_ready)
        )
        return
    }

    val size = vm.size
    val difficulty = vm.difficulty
    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.wrapContentWidth()) {
                BackButtonWithText(
                    title = "${size.displayName} • ${difficulty.displayName}",
                    onBackClick = { navController.safePopBackStack() },
                    modifier = Modifier.wrapContentWidth()
                )

                Box(
                    modifier = Modifier
                        // the room UNDER the header (fillMaxHeight() was the whole column, so the board ran past the bottom edge)
                        .weight(1f)
                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
                        .padding(bottom = Dimens12),
                    contentAlignment = Alignment.Center
                ) {
                    SudokuBoard(vm = vm, modifier = Modifier.fillMaxSize())
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                SudokuNumberPad(vm = vm)

                Spacer(Modifier.height(Dimens12))

                // the line under the keypad keeps its height even when empty, so nothing jumps
                val colors = LocalAbColors.current
                val s = LocalAbScale.current.factor
                val messageColor: Color = if (colors.isDark) colors.danger else colors.danger.abReadable(4.5f)
                if (vm.message.isNullOrEmpty()) {
                    Text(
                        "",
                        color = messageColor,
                        style = AbTypography.fixed(15f * s, FontWeight.ExtraBold)
                    )
                } else {
                    Text(
                        vm.message ?: "",
                        color = messageColor,
                        style = AbTypography.fixed(15f * s, FontWeight.ExtraBold)
                    )
                }

                Spacer(Modifier.height(Dimens4))

                Row {
                    GameButton(
                        text = if (vm.showCandidates) stringResource(R.string.hide_options) else stringResource(R.string.show_options),
                        icon = if (vm.showCandidates) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        type = ButtonType.TEAL,
                        isSmall = true,
                        onClick = { vm.toggleCandidates() }
                    )

                    Spacer(Modifier.width(Dimens8))

                    GameButton(
                        text = "Hint ${vm.hintUsed}/${vm.hintLimit}",
                        icon = Icons.Filled.Lightbulb,
                        type = ButtonType.ORANGE,
                        isSmall = true,
                        onClick = { vm.revealOneNumber() }
                    )

                    Spacer(Modifier.width(Dimens8))

                    GameButton(
                        text = stringResource(R.string.restart),
                        icon = Icons.Filled.Refresh,
                        type = ButtonType.PINK,
                        isSmall = true,
                        onClick = { vm.resetPuzzle() }
                    )

                }
            }
        }
    }

    if (vm.isSolved) {
        SudokuResultOverlay(
            hintUsed = vm.hintUsed,
            hintLimit = vm.hintLimit,
            onPlayAgain = {
                vm.isSolved = false
                vm.loadNewPuzzle(size, difficulty)
            },
            onBack = {
                vm.isSolved = false
                navController.safePopBackStack()
            }
        )
    }

    // Debug builds only: lets the lead render the real result overlay without playing (`--ez ab_show_result true`).
    if (AbDebugFlags.showResultEnabled && !vm.isSolved) {
        SudokuResultOverlay(
            hintUsed = vm.hintUsed,
            hintLimit = vm.hintLimit,
            onPlayAgain = {
                vm.isSolved = false
                vm.loadNewPuzzle(size, difficulty)
            },
            onBack = {
                vm.isSolved = false
                navController.safePopBackStack()
            }
        )
    }
}

// "Candy Pop" celebration: the shared Game Zone result card (glossy orange panel, bouncing brain badge, three stars, confetti).
// The scrim, the clap sound and the 1 second button delay are the game's own and stay exactly as they were.
@Composable
private fun SudokuResultOverlay(
    hintUsed: Int,
    hintLimit: Int,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit
) {
    val s = LocalAbScale.current.factor
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { AudioPlayerManager.playSoundClap() }
        GameResultCard(
            title = stringResource(R.string.you_are_a_genius),
            emoji = "🧠",
            accent = ButtonType.ORANGE,
            stars = 3,
            stats = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)
                ) {
                    Text(
                        stringResource(R.string.you_completed_this_sudoku),
                        color = Color.White,
                        style = AbTypography.fixed(18f * s, FontWeight.ExtraBold).copy(
                            shadow = Shadow(Color.Black.copy(alpha = 0.22f), Offset(0f, 3f), 0f)
                        ),
                        textAlign = TextAlign.Center
                    )
                    GameStatChip(emoji = "💡", value = "Hints used: $hintUsed/$hintLimit", color = Color(0xFFB45309))
                }
            },
            buttons = {
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    GameButton(
                        text = stringResource(R.string.play_again),
                        icon = Icons.Filled.Refresh,
                        type = ButtonType.POSITIVE,
                        onClick = { if (enabled) onPlayAgain() }
                    )
                    GameHomeButton(onClick = { if (enabled) onBack() })
                }
            }
        )
    }
}
