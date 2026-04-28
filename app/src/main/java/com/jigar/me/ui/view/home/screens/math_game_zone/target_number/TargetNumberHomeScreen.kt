package com.jigar.me.ui.view.home.screens.math_game_zone.target_number

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
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
import com.jigar.me.ui.view.home.common.BackButtonWithText
import com.jigar.me.ui.view.home.common.CommonDifficultySelectorCompose
import com.jigar.me.ui.view.home.common.HowToPlayButton
import com.jigar.me.ui.view.home.common.how_to_play.HowToPlayTargetNumberView
import com.jigar.me.ui.view.home.screens.math_game_zone.target_number.components.TargetNumberViewModel

@Composable
fun TargetNumberHomeScreen(
    viewModel: TargetNumberViewModel,
    onStartGame: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showHelp by remember { mutableStateOf(false) }
    val levelRange = 1..3

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row {
                BackButtonWithText(title = stringResource(R.string.target_number_game), onBackClick = onBackClick)
                Spacer(Modifier.weight(1f))
                HowToPlayButton {
                    showHelp = true
                }
            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                levelRange.forEach { level ->
                    val isSelected = state.selectedLevel == level
                    val shape = RoundedCornerShape(200.dp)

                    val animatedPadding by animateDpAsState(
                        targetValue = if (isSelected)
                            dimensionResource(R.dimen.activity_padding4)
                        else
                            dimensionResource(R.dimen.activity_padding24),
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
                            .padding(animatedPadding)
                            .clip(shape)
                            .graphicsLayer {
                                scaleX = animatedScale
                                scaleY = animatedScale
                            }
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

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CommonDifficultySelectorCompose(
                    selected = state.selectedDifficulty,
                    onSelect = { viewModel.selectDifficulty(it) }
                )

                Spacer(Modifier.weight(1f))

                val shape = RoundedCornerShape(50)
                Box(modifier = Modifier.shadow(elevation = 8.dp, shape = shape, clip = false)) {
                    Button(
                        onClick = { onStartGame() },
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
                                text = stringResource(R.string.lets_start),
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
            HowToPlayTargetNumberView {
                showHelp = false
            }
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
