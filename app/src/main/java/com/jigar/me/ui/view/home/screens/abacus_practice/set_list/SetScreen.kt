package com.jigar.me.ui.view.home.screens.abacus_practice.set_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.BuildConfig
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.AbTopBar
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumLoginBottomSheet
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumPaywallBottomSheet
import com.jigar.me.ui.view.home.common_ui.dialogs.PaywallContext
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
    LaunchedEffect(Unit) { homeActivityViewModel.isPurchasedFlow.collect { if (it) isSubscribed = true } }
    val isLoggedIn = homeActivityViewModel.isUserLoggedIn()
    var showPaywall by remember { mutableStateOf(false) }
    var showLogin by remember { mutableStateOf(false) }
    var pendingSetId by remember { mutableStateOf<String?>(null) }
    var pendingSetAnswerSetting by remember { mutableStateOf<String?>(null) }

    AbTheme {
    Box(Modifier.fillMaxSize()) {
    AbBackground(AbWorld.Meadow, Modifier.fillMaxSize())
    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        AbTopBar(
            title = uiState.pageTitle,
            onBack = onBackClick,
            modifier = Modifier.padding(
                start = DeviceInfo.screenHorizontalPadding(),
                end = AppDimens.Dimens16,
                top = DeviceInfo.screenTopPadding(),
                bottom = AppDimens.Dimens6,
            ),
            trailing = { TopRightChips() },
        )

        if (uiState.showNoData) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = AppDimens.Dimens20),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_coming_soon),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        } else {
            val pageGridState = rememberLazyGridState()
            val level = uiState.levelName.filter { it.isDigit() }.toIntOrNull() ?: 1

            LazyVerticalGrid(
                state = pageGridState,
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f).fillMaxWidth(),
                // the cards carry a small inset of their own, so only the difference to the header edge is added
                contentPadding = PaddingValues(
                    start = maxOf(DeviceInfo.screenHorizontalPadding() - AppDimens.Dimens4, 0.dp),
                    end = AppDimens.Dimens16,
                    bottom = AppDimens.Dimens16
                ),
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
                // each card also carries a 3D edge (6 * scale) under it, so it is taken off the row gap: the gap between rows
                // then looks the same as the gap between the two columns
                verticalArrangement = Arrangement.spacedBy(maxOf(0.dp, AppDimens.Dimens8 - 6.dp * LocalAbScale.current.factor))
            ) {
                itemsIndexed(uiState.pages) { pageIndex, page ->
                    val isLocked = FreemiumManager.levelPageGate(
                        isSubscribed, viewModel.name ?: "", pageIndex
                    ) == FreemiumManager.GateResult.REQUIRE_PAYWALL

                    PageItem(
                        page = page,
                        allSets = allSets,
                        level = level,
                        isLocked = isLocked,
                        onSetClick = { set ->
                            when {
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
                        },
                        onLockedTapped = { showPaywall = true }
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
                homeActivityViewModel.refreshPurchaseState()
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
            paywallContext = PaywallContext.PRACTICE,
            onSubscriptionActivated = { isSubscribed = true; showPaywall = false },
            onDismiss = { showPaywall = false }
        )
    }
    }
    }
}
