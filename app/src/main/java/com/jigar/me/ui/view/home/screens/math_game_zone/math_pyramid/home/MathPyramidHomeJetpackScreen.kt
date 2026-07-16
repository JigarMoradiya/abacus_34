package com.jigar.me.ui.view.home.screens.math_game_zone.math_pyramid.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.rounded.PlayArrow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens32
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.CommonDifficultySelectorCompose
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayMathPyramidView
import com.jigar.me.ui.view.home.screens.math_game_zone.math_pyramid.home.components.MathPyramidViewModel
import com.jigar.me.ui.view.home.theme.ButtonType

@Composable
fun MathPyramidHomeJetpackScreen(
    viewModel: MathPyramidViewModel,
    onStartGame: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showHelp by remember { mutableStateOf(false) }
    val levelRange = 2..6

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Column(modifier = Modifier.fillMaxSize()) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                BackButtonWithText(title = stringResource(R.string.math_pyramid), modifier = Modifier.weight(1f),onBackClick = onBackClick)
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

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                levelRange.forEach { level ->

                    val shape = RoundedCornerShape(Dimens12)

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
                            .padding(Dimens8)
                    ) {
                        Image(
                            painter = painterResource(id = getDrawableForPyramid(level)),
                            contentDescription = "pyramid_$level",
                            contentScale = ContentScale.Fit
                        )

                        Box(
                            modifier = Modifier.height(Dimens32),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Level $level",
                                color = if (state.selectedLevel == level) colorResource(R.color.black) else colorResource(R.color.black_text),
                                fontFamily = FontFamily(Font(if (state.selectedLevel == level) R.font.font_bold else R.font.font_regular)),
                                fontSize = dimensionResource(
                                    id = if (state.selectedLevel == level) R.dimen.textSize24 else R.dimen.textSize17
                                ).value.sp.scaled()
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens16),
                horizontalArrangement = Arrangement.spacedBy(Dimens16, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CommonDifficultySelectorCompose(
                    selected = state.selectedDifficulty,
                    onSelect = { viewModel.selectDifficulty(it) }
                )

                KidsActionButton(
                    text = stringResource(R.string.lets_start),
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
        HowToPlayMathPyramidView {
            showHelp = false
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
