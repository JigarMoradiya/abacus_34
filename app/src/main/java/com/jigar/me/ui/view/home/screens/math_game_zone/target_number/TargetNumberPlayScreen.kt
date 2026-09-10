package com.jigar.me.ui.view.home.screens.math_game_zone.target_number

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorGreen
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorRed
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.common_ui.buttons.KidsKeyPad
import com.jigar.me.ui.view.home.common_ui.buttons.KidsNumberButton
import com.jigar.me.ui.view.home.screens.math_game_zone.target_number.viewmodel.TargetNumberPlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.AppDimens.ToolbarIconSize
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.utils.extensions.isNotNullOrEmpty

@Composable
fun TargetNumberPlayScreen(
    viewModel: TargetNumberPlayViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    Row(modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)) {
        Box(
            modifier = Modifier.weight(0.6f),
            contentAlignment = Alignment.Center
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                BackButtonWithText(title = stringResource(R.string.complete_the_target), onBackClick = onBackClick)

                Spacer(modifier = Modifier.weight(1f))

                Column(
                    modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(
                        Dimens8,
                        Alignment.CenterVertically
                    ), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "🎯 Target: ${state.target}",
                        fontSize = dimensionResource(R.dimen.textSize40).value.sp.scaled(),
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                        color = Color.Red
                    )

                    if (state.isSolvedCorrect == true) {
                        state.message?.let {
                            Spacer(Modifier.height(Dimens8))
                            Text(
                                it,
                                style = MaterialTheme.typography.titleLarge.scaled(),
                                fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                                color = ColorGreen
                            )
                        }
                        Spacer(Modifier.height(ToolbarIconSize))
                    } else {
                        Text(
                            state.currentExpression.ifEmpty { " " },
                            style = MaterialTheme.typography.titleMedium.scaled(),
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            state.numbers.forEachIndexed { idx, num ->

                                val isSelected = state.selectedNumberIndex == idx

                                KidsNumberButton(
                                    num = num,
                                    isSelected = isSelected,
                                    minWidth = AppDimens.Dimens64,
                                    onClick = { viewModel.tapNumber(idx) }
                                )
                            }
                        }

                        Spacer(Modifier.height(if (DeviceInfo.isTablet)AppDimens.Dimens10 else AppDimens.Dimens6))

                        Row(horizontalArrangement = Arrangement.spacedBy(if (DeviceInfo.isTablet)AppDimens.Dimens10 else AppDimens.Dimens6)) {
                            state.allowedOps.forEach { op ->
                                KidsKeyPad(
                                    text = op.symbol,
                                    type = ButtonType.GREEN,
                                    width = AppDimens.Dimens48,
                                    height = AppDimens.Dimens48,
                                    onClick = {
                                        if (state.isSolvedCorrect != true) {
                                            viewModel.tapOperation(op)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

        }

        Box(
            modifier = Modifier.weight(0.4f),
            contentAlignment = Alignment.Center
        ) {

            Column(modifier = Modifier.padding(AppDimens.Dimens16), horizontalAlignment = Alignment.CenterHorizontally) {
                if (state.steps.isNotNullOrEmpty()) {
                    StepsLogSection(
                        steps = state.steps,
                        originalNumbers = state.originalNumbers.joinToString(separator = ", ")
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (state.isSolvedCorrect != true){
                    state.message?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall.scaled(),
                            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                            color = ColorRed,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimens8)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(Dimens8)) {
                    if (state.isSolvedCorrect == true) {
                        KidsActionButton(
                            text = stringResource(R.string.new_target),
                            type = ButtonType.ORANGE,
                            isSmall = true,
                            onClick = {
                                viewModel.generateNewPuzzle()
                            }
                        )
                    } else {
                        KidsActionButton(
                            text = "Hint ${state.hintUsed}/${state.hintLimit}",
                            icon = Icons.Filled.Lightbulb,
                            type = ButtonType.ORANGE,
                            isSmall = true,
                            onClick = {
                                if (state.hintUsed < state.hintLimit){
                                    viewModel.showHint()
                                }
                            }
                        )

                        KidsActionButton(
                            text = stringResource(R.string.reset),
                            icon = Icons.Filled.Refresh,
                            type = ButtonType.PINK,
                            isSmall = true,
                            onClick = { viewModel.resetPuzzle() }
                        )

                    }
                }
            }
        }
    }

    if (state.isSolvedCorrect == true) {
    }
}


@Composable
fun StepsLogSection(
    steps: List<String>,
    originalNumbers: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(R.string.all_steps),
            style = MaterialTheme.typography.headlineSmall.scaled(),
            fontFamily = FontFamily(Font(R.font.font_bold)),
            color = Color.Red
        )

        Spacer(Modifier.height(Dimens8))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = AppDimens.Dimens1)
        ) {

            Text(
                text = originalNumbers,
                style = MaterialTheme.typography.headlineMedium.scaled(),
                fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                color = Color.Black
            )

            if (steps.isNotEmpty()) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_down),
                    contentDescription = null,
                    tint = Color.Red.copy(alpha = 0.8f),
                    modifier = Modifier.size(AppDimens.Dimens16)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(steps) { index, step ->

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = AppDimens.Dimens1)
                ) {

                    Text(
                        text = step,
                        style = MaterialTheme.typography.titleSmall.scaled(),
                        fontFamily = FontFamily(Font(R.font.font_bold)),
                        color = Color.Black
                    )

                    if (index < steps.size - 1) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_down),
                            contentDescription = null,
                            tint = Color.Red.copy(alpha = 0.8f),
                            modifier = Modifier.size(AppDimens.Dimens16)
                        )
                    }
                }
            }
        }
    }
}
