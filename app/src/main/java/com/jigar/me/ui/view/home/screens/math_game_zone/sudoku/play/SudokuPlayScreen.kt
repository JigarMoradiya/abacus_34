package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play

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
                        .fillMaxHeight()
                        .aspectRatio(1f),
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

                if (vm.message.isNullOrEmpty()) {
                    Text(
                        "",
                        color = Color.Red,
                        style = MaterialTheme.typography.labelSmall.scaled(),
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        vm.message ?: "",
                        color = Color.Red,
                        style = MaterialTheme.typography.labelSmall.scaled(),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(Dimens4))

                Row {
                    KidsActionButton(
                        text = if (vm.showCandidates) stringResource(R.string.hide_options) else stringResource(R.string.show_options),
                        icon = if (vm.showCandidates) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        type = ButtonType.TEAL,
                        isSmall = true,
                        onClick = { vm.toggleCandidates() }
                    )

                    Spacer(Modifier.width(Dimens8))

                    KidsActionButton(
                        text = "Hint ${vm.hintUsed}/${vm.hintLimit}",
                        icon = Icons.Filled.Lightbulb,
                        type = ButtonType.ORANGE,
                        isSmall = true,
                        onClick = { vm.revealOneNumber() }
                    )

                    Spacer(Modifier.width(Dimens8))

                    KidsActionButton(
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
}

// "Candy Pop" celebration card — matches Number Snake's result popup style
// (gradient card, white badge overlapping the top edge, two symmetric filled
// buttons) instead of the generic CustomPopupView celebration theme.
@Composable
private fun SudokuResultOverlay(
    hintUsed: Int,
    hintLimit: Int,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit
) {
    val accentColors = getButtonColors(ButtonType.ORANGE)
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { AudioPlayerManager.playSoundClap() }
        Box(modifier = Modifier.fillMaxWidth(0.6f), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp, RoundedCornerShape(AppDimens.Dimens20 * 1.2f), ambientColor = accentColors.base, spotColor = accentColors.base)
                    .background(accentColors.gradient, RoundedCornerShape(AppDimens.Dimens20 * 1.2f))
                    .padding(horizontal = AppDimens.Dimens30, vertical = AppDimens.Dimens20),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
            ) {
                Spacer(Modifier.height(AppDimens.Dimens20))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🧠", fontSize = (28f * gameScale()).sp)
                    Text(
                        stringResource(R.string.you_are_a_genius),
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                        fontSize = 26.sp.scaled(),
                        style = TextStyle(shadow = Shadow(color = accentColors.base.copy(alpha = 0.9f), offset = Offset(1.5f, 1.5f), blurRadius = 0f)),
                        modifier = Modifier.padding(horizontal = AppDimens.Dimens8)
                    )
                    Text("🧠", fontSize = (28f * gameScale()).sp)
                }
                Text(
                    stringResource(R.string.you_completed_this_sudoku),
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.font_semibold)),
                    fontSize = 18.sp.scaled(),
                    textAlign = TextAlign.Center
                )
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4), verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clip(RoundedCornerShape(100f)).background(Color.White)
                        .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6)) {
                    Text("💡", fontSize = (16f * gameScale()).sp)
                    Text(
                        "Hints used: $hintUsed/$hintLimit",
                        color = accentColors.base,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                        fontSize = 16.sp.scaled(),
                        maxLines = 1
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    KidsActionButton(text = stringResource(R.string.play_again), type = ButtonType.POSITIVE,
                        onClick = { if (enabled) onPlayAgain() })
                    KidsActionButton(text = stringResource(R.string.no_i_want_to_close), type = ButtonType.NEGATIVE,
                        onClick = { if (enabled) onBack() })
                }
            }

            Box(
                modifier = Modifier
                    .offset(y = (-28).dp)
                    .size(64.dp)
                    .shadow(8.dp, CircleShape)
                    .background(Color.White, CircleShape)
                    .border(4.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🧠", fontSize = (32f * gameScale()).sp)
            }
        }
    }
}
