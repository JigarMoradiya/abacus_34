package com.jigar.me.ui.view.home.screens.abacus_practice.set_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.BuildConfig
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.screens.abacus_practice.set_list.components.PageItem
import com.jigar.me.ui.view.home.screens.abacus_practice.set_list.components.TopRightChips
import com.jigar.me.ui.view.home.screens.abacus_practice.set_list.viewmodels.SetViewModel
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun SetScreen(
    homeActivityViewModel: HomeActivityViewModel,
    onBackClick: () -> Unit,
    onNavigateToDoPractice: (setId: String) -> Unit,
    onNavigateToList: (setId: String) -> Unit,
    onNavigateToPurchase: () -> Unit,
) {
    val viewModel: SetViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allSets by homeActivityViewModel.allSets.collectAsStateWithLifecycle()
    val purchasedSKU by homeActivityViewModel.purchasedSku.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButtonWithText(
                title = uiState.name,
                onBackClick = onBackClick,
                modifier = Modifier.weight(1f)
            )
            TopRightChips()
        }
        Spacer(Modifier.weight(1f))

        if (uiState.showNoData) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_coming_soon),
                    contentDescription = null
                )
            }
        } else {
            val pageGridState = rememberLazyGridState()

            LazyVerticalGrid(
                state = pageGridState,
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(AppDimens.Dimens8)
            ) {
                items(uiState.pages) { page ->
                    PageItem(
                        page = page,
                        allSets = allSets,
                        onSetClick = { set ->
                            val isPurchase = homeActivityViewModel.isPurchasedSelectedLevel(purchasedSKU, uiState.name)
                            AudioPlayerManager.playSoundBtnBack()
                            if (isPurchase) {
                                onNavigateToDoPractice(set.id)
                            } else {
                                onNavigateToPurchase()
                            }
                        },
                        onSetLongClick = { set ->
                            if (BuildConfig.DEBUG) {
                                onNavigateToList(set.id)
                            }
                        }
                    )
                }
            }
        }
    }
}
