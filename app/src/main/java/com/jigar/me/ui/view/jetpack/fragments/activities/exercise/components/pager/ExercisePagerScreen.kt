package com.jigar.me.ui.view.jetpack.fragments.activities.exercise.components.pager

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.core.presentation.components.PrimaryButton
import com.jigar.me.ui.view.jetpack.fragments.activities.exercise.exercise_generator.GridItemModel
import com.jigar.me.ui.view.jetpack.fragments.activities.exercise.viewmodels.ExerciseUiState
import com.jigar.me.ui.view.jetpack.fragments.activities.exercise.viewmodels.ExerciseViewModel

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

                Text(
                    text = exercise.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold))
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(R.string.select_your_exercise),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily(Font(R.font.font_regular))
                    )
                )

                Spacer(Modifier.height(dimensionResource(R.dimen.activity_padding16)))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    ExerciseWrapGrid(
                        items = exercise.gridItems,
                        selectedItem = selectedItem,
                        onItemSelected = {
                            viewModel.onGridItemSelected(page, it)
                        }
                    )
                }

                Spacer(Modifier.height(dimensionResource(R.dimen.activity_padding16)))

                Text(
                    text = selectedItem?.selectedItemDescription(page) ?: "",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily(Font(R.font.font_medium))
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(dimensionResource(R.dimen.activity_padding12)))

                PrimaryButton(
                    text = stringResource(R.string.start_exercise), enabled = selectedItem != null,
                    onClick = {
                        selectedItem?.let { onStart(it, page) }
                    },
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            repeat(uiState.exercises.size) { idx ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (idx == pagerState.currentPage)
                                MaterialTheme.colorScheme.primary
                            else
                                Color.Gray.copy(alpha = 0.4f)
                        )
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

