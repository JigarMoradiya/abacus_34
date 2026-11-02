package com.jigar.me.ui.view.home.screens.purchase

import com.jigar.me.ui.view.home.common_ui.dialogs.ParentalGateDialog
import com.jigar.me.utils.AppAnalytics
import android.app.Activity
import androidx.compose.foundation.layout.Box
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
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbWorld
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
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    var showLoginSheet by remember { mutableStateOf(false) }
    // Grown-up gate: asked once per visit, when a parent taps Subscribe (this screen had no gate at all)
    var gatePassed by remember { mutableStateOf(false) }
    var showGate by remember { mutableStateOf(false) }
    var pendingGateAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    fun requireGate(action: () -> Unit) {
        if (gatePassed) { action(); return }
        pendingGateAction = action
        showGate = true
        AppAnalytics.log(AppAnalytics.GATE_SHOWN, "surface" to "purchase_screen", "context" to "full")
    }

    LaunchedEffect(Unit) {
        viewModel.loadData()
        AppAnalytics.log(AppAnalytics.PAYWALL_VIEWED, "surface" to "purchase_screen", "context" to "full",
            "logged_in" to homeActivityViewModel.isUserLoggedIn())
    }

    AbTheme {
        // the scene runs under the system bars; only the content is inset
        Box(Modifier.fillMaxSize()) { AbBackground(AbWorld.Sunset, Modifier.fillMaxSize()) }
        Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            PurchaseScreen(
                uiState = uiState,
                onPlanSelected = { viewModel.onPlanSelected(it) },
                onShowOldSubClick = { viewModel.onShowOldSubClick() },
                onSubscribe = {
                    val sku = uiState.sortedPlanList.getOrNull(uiState.selectedIndex)?.sku ?: ""
                    AppAnalytics.log(AppAnalytics.PLAN_SELECTED, "surface" to "purchase_screen", "context" to "full", "product_id" to sku)
                    requireGate {
                        if (homeActivityViewModel.isUserLoggedIn()) viewModel.makePurchase(activity)
                        else {
                            showLoginSheet = true
                            AppAnalytics.log(AppAnalytics.LOGIN_PROMPTED, "surface" to "purchase_screen", "context" to "full", "product_id" to sku)
                        }
                    }
                },
                onClose = onClose,
                oldSubPopupCloseClick = { viewModel.oldSubPopupClose() },
                onRetry = { viewModel.loadData() },
                privacyUrl = viewModel.privacyPolicyUrl(),
                onOpenLink = { url -> requireGate { uriHandler.openUri(url) } },
            )
        }

        if (showGate) {
            ParentalGateDialog(
                onPassed = {
                    gatePassed = true
                    showGate = false
                    AppAnalytics.log(AppAnalytics.GATE_PASSED, "surface" to "purchase_screen", "context" to "full")
                    val action = pendingGateAction
                    pendingGateAction = null
                    action?.invoke()
                },
                onCancelled = { showGate = false; pendingGateAction = null }
            )
        }

        if (showLoginSheet) {
            FreemiumLoginBottomSheet(
                showContinueWithoutSaving = false,
                skipParentalGate = true,   // the grown-up gate was already passed at the Subscribe tap
                onLoginSuccess = {
                    AppAnalytics.log(AppAnalytics.LOGIN_COMPLETED, "surface" to "purchase_screen", "context" to "full")
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
}
