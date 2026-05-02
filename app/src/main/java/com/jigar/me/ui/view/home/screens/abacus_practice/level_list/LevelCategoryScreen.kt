package com.jigar.me.ui.view.home.screens.abacus_practice.level_list

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.screens.abacus_practice.level_list.components.CategoryItem
import com.jigar.me.ui.view.home.screens.abacus_practice.level_list.viewmodels.LevelCategoryViewModel
import com.jigar.me.ui.view.home.screens.abacus_practice.level_list.viewmodels.LevelProgress
import com.jigar.me.ui.view.home.screens.abacus_practice.set_list.components.TopRightChips
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.home.theme.AppDimens

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
    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButtonWithText(
                title = stringResource(R.string.practice_of_abacus),
                onBackClick = onBackClick,
                modifier = Modifier.weight(1f)
            )
            TopRightChips(false)
        }
        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    AppDimens.Dimens16,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically,
                contentPadding = PaddingValues(horizontal = AppDimens.Dimens16)
            ) {
                itemsIndexed(uiState.categories) { index, category ->
                    CategoryItem(
                        category = category,
                        progress = uiState.progressMap[category.id] ?: LevelProgress()
                    ) {
                        AudioPlayerManager.playSoundBtnClick()
                        onNavigateToSet(category)
                    }
                }
            }
        }
    }
}
