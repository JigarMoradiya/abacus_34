package com.jigar.me.ui.view.home.screens.purchase

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.ui.view.jetpack.fragments.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.jetpack.fragments.purchase.components.PurchaseScreen
import com.jigar.me.ui.view.jetpack.fragments.purchase.viewmodels.PurchaseViewModel

@Composable
fun PurchaseScreenRoute(
    homeActivityViewModel: HomeActivityViewModel,
    onClose: () -> Unit,
) {
    val viewModel: PurchaseViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val purchasedSKU by homeActivityViewModel.purchasedSku.collectAsStateWithLifecycle()
    val activity = LocalContext.current as Activity

    viewModel.loadInitialData(purchasedSKU)

    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        PurchaseScreen(
            uiState = uiState,
            onPlanSelected = { viewModel.onPlanSelected(it) },
            onShowOldSubClick = { viewModel.onShowOldSubClick() },
            onSubscribe = { viewModel.makePurchase(activity) },
            onClose = onClose,
            oldSubPopupCloseClick = { viewModel.oldSubPopupClose() }
        )
    }
}
