package com.jigar.me.ui.view.home.screens.activities.exercise.components.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorAccent
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorOrange
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimary
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.activities.exercise.exercise_generator.GridItemModel
import com.jigar.me.ui.view.home.screens.activities.exercise.viewmodels.ExerciseUiState
import com.jigar.me.ui.view.home.screens.activities.exercise.viewmodels.ExerciseViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.ButtonType

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExercisePagerScreen(
    uiState: ExerciseUiState,
    viewModel: ExerciseViewModel,
    onStart: (GridItemModel, Int) -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = uiState.currentPage,
        pageCount = { uiState.exercises.size }
    )

    LaunchedEffect(pagerState.currentPage) {
        viewModel.onPageChanged(pagerState.currentPage)
    }

    Column(
        modifier = Modifier
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->

            val exercise = uiState.exercises[page]
            val selectedItem = uiState.selectedItems[page]

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {

                // TITLE
                Text(
                    text = exercise.title,
                    style = MaterialTheme.typography.titleLarge.scaled().copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                        color = Color(0xFF4E342E),
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.2f),
                            offset = Offset(1f, 1f),
                            blurRadius = 0f
                        )
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(Dimens4))

                // SUBTITLE
                Text(
                    text = stringResource(R.string.select_your_exercise),
                    style = MaterialTheme.typography.bodyMedium.scaled().copy(
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily(Font(R.font.font_medium)),
                        color = Color(0xFF8D6E63)
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(Dimens16))

                // GRID
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    ExerciseWrapGrid(
                        items = exercise.gridItems,
                        selectedItem = selectedItem,
                        onItemSelected = {
                            AudioPlayerManager.playSoundBtnClick()
                            viewModel.onGridItemSelected(page, it)
                        }
                    )
                }

                Spacer(Modifier.height(Dimens16))

                // DESCRIPTION
                Text(
                    text = selectedItem?.selectedItemDescription(page) ?: "",
                    style = MaterialTheme.typography.bodyMedium.scaled().copy(
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily(Font(R.font.font_medium)),
                        color = Color(0xFF5D4037),
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.1f),
                            offset = Offset(0.5f, 0.5f),
                            blurRadius = 0f
                        )
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = Dimens16)
                )

                Spacer(Modifier.height(Dimens12))

                // BUTTON
                KidsActionButton(
                    modifier = Modifier
                        .padding(vertical = Dimens12, horizontal = Dimens16),
                    text = stringResource(R.string.start_exercise),
                    icon = Icons.Default.RocketLaunch,
                    type = ButtonType.ORANGE,
                    onClick = {
                        selectedItem?.let { onStart(it, page) }
                    }
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            repeat(uiState.exercises.size) { idx ->
                Box(
                    modifier = Modifier
                        .size(AppDimens.Dimens8)
                        .clip(CircleShape)
                        .background(
                            if (idx == pagerState.currentPage)
                                ColorOrange
                            else
                                Color.Gray.copy(alpha = 0.4f)
                        )
                )
            }
        }

        Spacer(Modifier.height(AppDimens.Dimens16))
    }
}

