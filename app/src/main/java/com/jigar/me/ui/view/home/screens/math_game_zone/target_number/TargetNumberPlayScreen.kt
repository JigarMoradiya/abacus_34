package com.jigar.me.ui.view.home.screens.math_game_zone.target_number

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimaryDark
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.screens.math_game_zone.target_number.viewmodel.TargetNumberPlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.PlaySound
import com.jigar.me.utils.extensions.isNotNullOrEmpty

@Composable
fun TargetNumberPlayScreen(
    viewModel: TargetNumberPlayViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
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
                        AppDimens.Dimens8,
                        Alignment.CenterVertically
                    ), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "🎯 Target: ${state.target}",
                        fontSize = dimensionResource(R.dimen.textSize40).value.sp,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                        color = Color.Red
                    )

                    Text(
                        state.currentExpression.ifEmpty { " " },
                        fontSize = dimensionResource(R.dimen.textSizeExtraLarge).value.sp,
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

                            Box(
                                modifier = Modifier
                                    .defaultMinSize(minWidth = 56.dp, minHeight = 56.dp)
                                    .clip(RoundedCornerShape(AppDimens.Dimens12))
                                    .background(
                                        if (isSelected)
                                            Color(0xFF43A047)
                                        else
                                            colorResource(R.color.colorPrimary)
                                    )
                                    .clickable { viewModel.tapNumber(idx) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = num.toString(),
                                    fontSize = dimensionResource(R.dimen.textSize20).value.sp,
                                    fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                                    color = Color.White
                                )
                            }
                        }
                    }


                    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)) {
                        state.allowedOps.forEach { op ->
                            Box(
                                contentAlignment = Alignment.Center, modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(AppDimens.Dimens12))
                                    .background(ColorPrimaryDark)
                                    .clickable {
                                        viewModel.tapOperation(op)
                                    }) {
                                Text(
                                    op.symbol,
                                    fontSize = dimensionResource(R.dimen.textSize30).value.sp,
                                    fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                                    color = Color.White
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
                state.message?.let {
                    Text(
                        it,
                        fontSize = dimensionResource(R.dimen.textSizeExtraLarge).value.sp,
                        fontFamily = FontFamily(Font(R.font.font_bold)),
                        color = if (state.isSolvedCorrect == true) colorResource(R.color.green_600) else colorResource(R.color.red_600),
                        modifier = Modifier.padding(AppDimens.Dimens8)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)) {
                    if (state.isSolvedCorrect == true) {
                        Button(
                            onClick = {
                                PlaySound.playHint(context)
                                viewModel.generateNewPuzzle()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.colorPrimary),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(AppDimens.Dimens8),
                            contentPadding = PaddingValues(
                                horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoMode,
                                    contentDescription = null,
                                    modifier = Modifier.size(AppDimens.Dimens24)
                                )

                                Text(
                                    text = stringResource(R.string.new_target),
                                    fontSize = dimensionResource(R.dimen.textSizeLarge).value.sp,
                                    fontFamily = FontFamily(Font(R.font.font_semibold)),
                                )
                            }
                        }
                    } else {
                        Button(
                            enabled = state.hintUsed < state.hintLimit,
                            onClick = { viewModel.showHint() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(AppDimens.Dimens8),
                            contentPadding = PaddingValues(
                                horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${state.hintUsed}/${state.hintLimit}",
                                    fontSize = dimensionResource(R.dimen.textSize18).value.sp,
                                    fontFamily = FontFamily(Font(R.font.font_bold)),
                                    modifier = Modifier.height(AppDimens.Dimens24)
                                )

                                Text(
                                    text = stringResource(R.string.hints),
                                    fontSize = dimensionResource(R.dimen.textSizeLarge).value.sp,
                                    fontFamily = FontFamily(Font(R.font.font_semibold)),
                                )
                            }
                        }


                        Button(
                            onClick = { viewModel.resetPuzzle() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE53935),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(AppDimens.Dimens8),
                            contentPadding = PaddingValues(
                                horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(AppDimens.Dimens24)
                                )

                                Text(
                                    text = stringResource(R.string.reset),
                                    fontSize = dimensionResource(R.dimen.textSizeLarge).value.sp,
                                    fontFamily = FontFamily(Font(R.font.font_semibold)),
                                )
                            }
                        }

                    }
                }
            }
        }
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
            fontSize = dimensionResource(R.dimen.textSize18).value.sp,
            fontFamily = FontFamily(Font(R.font.font_bold)),
            color = Color.Red
        )

        Spacer(Modifier.height(AppDimens.Dimens8))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 1.dp)
        ) {

            Text(
                text = originalNumbers,
                fontSize = dimensionResource(R.dimen.textSize20).value.sp,
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
                    modifier = Modifier.padding(vertical = 1.dp)
                ) {

                    Text(
                        text = step,
                        fontSize = dimensionResource(R.dimen.textSizeExtraLarge).value.sp,
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
