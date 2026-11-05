package com.jigar.me.ui.view.home.screens.abacus_practice.level_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbPracticeLevelCard
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbTopBar
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.home.screens.abacus_practice.level_list.viewmodels.LevelCategoryViewModel
import com.jigar.me.ui.view.home.screens.abacus_practice.level_list.viewmodels.LevelProgress
import com.jigar.me.ui.view.home.screens.abacus_practice.set_list.components.TopRightChips
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens6

@Composable
fun LevelCategoryScreen(
    homeActivityViewModel: HomeActivityViewModel,
    onBackClick: () -> Unit,
    onNavigateToSet: (category: Category) -> Unit,
) {
    val viewModel: LevelCategoryViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allSets by homeActivityViewModel.allSets.collectAsStateWithLifecycle()
    viewModel.loadLoad(allSets)
    AbTheme {
        Box(Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Meadow, Modifier.fillMaxSize())
            BoxWithConstraints(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
                // a tablet shows the cards at half the screen height, a phone at 70% (as iOS)
                val screenHeight = maxHeight
                Column(Modifier.fillMaxSize()) {
                    AbTopBar(
                        title = stringResource(R.string.practice_of_abacus),
                        onBack = onBackClick,
                        modifier = Modifier.padding(
                            start = DeviceInfo.screenHorizontalPadding(),
                            end = Dimens16,
                            top = DeviceInfo.screenTopPadding(),
                            bottom = Dimens6,
                        ),
                        trailing = { TopRightChips(false) },
                    )

                    BoxWithConstraints(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        // iOS sizes the cards at about 70% of the screen height; the box centres the list in the room under the header
                        val listHeight = minOf(screenHeight * (if (DeviceInfo.isTablet) 0.5f else 0.7f), maxHeight)
                        // the cards carry a small inset of their own, so only the difference to the header edge is added
                        val leadingInset = maxOf(DeviceInfo.screenHorizontalPadding() - Dimens4, 0.dp)
                        LazyRow(
                            modifier = Modifier.fillMaxWidth().padding(end = Dimens16).height(listHeight),
                            horizontalArrangement = Arrangement.spacedBy(Dimens16),
                            contentPadding = PaddingValues(start = leadingInset + Dimens4, end = Dimens4),
                        ) {
                            items(uiState.categories) { category ->
                                AbPracticeLevelCard(
                                    // "level3" -> 3 (the server names levels level1 ... level10)
                                    level = category.name.filter { it.isDigit() }.toIntOrNull() ?: 1,
                                    progress = uiState.progressMap[category.id] ?: LevelProgress(),
                                    modifier = Modifier.padding(vertical = Dimens4).fillMaxHeight().aspectRatio(379f / 519f),
                                    // the card is silent by design: the screen plays the click
                                    onClick = {
                                        AudioPlayerManager.playSoundBtnClick()
                                        onNavigateToSet(category)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
