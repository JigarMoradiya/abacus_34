package com.jigar.me.ui.view.home.screens.purchase

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumLoginBottomSheet
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.home.screens.purchase.components.PurchaseScreen
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.PurchaseViewModel

@Composable
fun PurchaseScreenRoute(
    homeActivityViewModel: HomeActivityViewModel,
    onClose: () -> Unit,
) {
    val viewModel: PurchaseViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current as Activity
    var showLoginSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadData() }

    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        PurchaseScreen(
            uiState = uiState,
            onPlanSelected = { viewModel.onPlanSelected(it) },
            onShowOldSubClick = { viewModel.onShowOldSubClick() },
            onSubscribe = {
                if (homeActivityViewModel.isUserLoggedIn()) viewModel.makePurchase(activity)
                else showLoginSheet = true
            },
            onClose = onClose,
            oldSubPopupCloseClick = { viewModel.oldSubPopupClose() }
        )
    }

    if (showLoginSheet) {
        FreemiumLoginBottomSheet(
            showContinueWithoutSaving = false,
            onLoginSuccess = {
                showLoginSheet = false
                if (viewModel.isUserSubscribed()) {
                    viewModel.loadData()
                } else {
                    viewModel.makePurchase(activity)
                }
            },
            onDismiss = { showLoginSheet = false }
        )
    }
}
