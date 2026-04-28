package com.jigar.me.ui.view.home.screens.math_game_zone.target_number

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.CommonDifficultySelectorCompose
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayTargetNumberView
import com.jigar.me.ui.view.home.screens.math_game_zone.target_number.components.TargetNumberViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens24
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.ButtonType

@Composable
fun TargetNumberHomeScreen(
    viewModel: TargetNumberViewModel,
    onStartGame: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showHelp by remember { mutableStateOf(false) }
    val levelRange = 1..3

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BackButtonWithText(title = stringResource(R.string.target_number_game),modifier = Modifier.weight(1f), onBackClick = onBackClick)
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
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                levelRange.forEach { level ->
                    val isSelected = state.selectedLevel == level
                    val shape = RoundedCornerShape(AppDimens.Dimens200)

                    val animatedPadding by animateDpAsState(
                        targetValue = if (isSelected) Dimens4 else Dimens24,
                        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
                        label = ""
                    )

                    val animatedScale by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 1f,
                        animationSpec = spring(dampingRatio = 0.6f, stiffness = 250f),
                        label = ""
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(animatedPadding)
                            .graphicsLayer {
                                scaleX = animatedScale
                                scaleY = animatedScale
                            }
                            .clip(shape)
                            .background(Color.Transparent)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = LocalIndication.current
                            ) {
                                viewModel.selectLevel(level)
                            },
                        contentAlignment = Alignment.Center
                    ) {

                        Image(
                            painter = painterResource(id = getDrawableForTargetNumber(level)),
                            contentScale = ContentScale.Fit,
                            contentDescription = null
                        )
                    }

                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppDimens.Dimens16),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CommonDifficultySelectorCompose(
                    selected = state.selectedDifficulty,
                    onSelect = { viewModel.selectDifficulty(it) }
                )

                Spacer(Modifier.weight(1f))

                KidsActionButton(
                    text = stringResource(R.string.lets_play),
                    icon = Icons.Rounded.PlayArrow,
                    type = ButtonType.ORANGE,
                    isIconStart = false,
                    onClick = {
                        onStartGame()
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
        HowToPlayTargetNumberView {
            showHelp = false
        }
    }

}

@Composable
fun getDrawableForTargetNumber(level: Int): Int {
    return when (level) {
        1 -> R.drawable.target_number_level1
        2 -> R.drawable.target_number_level2
        3 -> R.drawable.target_number_level3
        else -> R.drawable.target_number_level1
    }
}
