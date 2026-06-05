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
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumLoginBottomSheet
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumPaywallBottomSheet
import com.jigar.me.ui.view.home.screens.abacus_practice.set_list.components.PageItem
import com.jigar.me.ui.view.home.screens.abacus_practice.set_list.components.TopRightChips
import com.jigar.me.ui.view.home.screens.abacus_practice.set_list.viewmodels.SetViewModel
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.FreemiumManager

@Composable
fun SetScreen(
    homeActivityViewModel: HomeActivityViewModel,
    onBackClick: () -> Unit,
    onNavigateToDoPractice: (setId: String, saveResults: Boolean) -> Unit,
    onNavigateToList: (setId: String) -> Unit,
) {
    val viewModel: SetViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allSets by homeActivityViewModel.allSets.collectAsStateWithLifecycle()
    var isSubscribed by remember(uiState.levelName) { mutableStateOf(homeActivityViewModel.isPurchasedSelectedLevel(uiState.levelName)) }
    val isLoggedIn = homeActivityViewModel.isUserLoggedIn()
    var showPaywall by remember { mutableStateOf(false) }
    var showLogin by remember { mutableStateOf(false) }
    var pendingSetId by remember { mutableStateOf<String?>(null) }
    var pendingSetAnswerSetting by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButtonWithText(
                title = uiState.pageTitle,
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
                itemsIndexed(uiState.pages) { pageIndex, page ->
                    val isLocked = FreemiumManager.levelPageGate(
                        isSubscribed, viewModel.name ?: "", pageIndex
                    ) == FreemiumManager.GateResult.REQUIRE_PAYWALL

                    PageItem(
                        page = page,
                        allSets = allSets,
                        isLocked = isLocked,
                        onSetClick = { set ->
                            AudioPlayerManager.playSoundBtnBack()
                            when {
                                isLocked -> showPaywall = true
                                !isLoggedIn -> {
                                    pendingSetId = set.id
                                    pendingSetAnswerSetting = set.answer_setting
                                    showLogin = true
                                }
                                else -> onNavigateToDoPractice(set.id, true)
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

    // Sheets outside Column — get full screen constraints from NavHost
    if (showLogin) {
        val loginSubtitle = if (pendingSetAnswerSetting == AppConstants.apiParams.answerFormalExam)
            stringResource(R.string.login_to_save_results)
        else
            stringResource(R.string.login_to_track_progress)

        FreemiumLoginBottomSheet(
            showContinueWithoutSaving = true,
            subtitle = loginSubtitle,
            onLoginSuccess = {
                showLogin = false
                isSubscribed = homeActivityViewModel.isPurchasedSelectedLevel(uiState.levelName)
                pendingSetId?.let { onNavigateToDoPractice(it, true) }
                pendingSetId = null
            },
            onContinueWithoutSaving = {
                showLogin = false
                pendingSetId?.let { onNavigateToDoPractice(it, false) }
                pendingSetId = null
            },
            onDismiss = { showLogin = false; pendingSetId = null; pendingSetAnswerSetting = null }
        )
    }

    if (showPaywall) {
        FreemiumPaywallBottomSheet(
            onSubscriptionActivated = { isSubscribed = true; showPaywall = false },
            onDismiss = { showPaywall = false }
        )
    }
}
