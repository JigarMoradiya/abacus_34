package com.jigar.me.ui.view.home.screens.activities.exam.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumLoginBottomSheet
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumPaywallBottomSheet
import com.jigar.me.ui.view.home.screens.activities.exam.home.components.ExamHomeScreen
import com.jigar.me.ui.view.home.screens.activities.exam.home.viewmodels.ExamHomeViewModel
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.utils.FreemiumManager
import com.jigar.me.utils.extensions.toastS

@Composable
fun ExamHomeRoute(
    homeActivityViewModel: HomeActivityViewModel,
    onBackClick: () -> Unit,
    onStartPlay: () -> Unit,
    onStartPlayWithoutSaving: () -> Unit = onStartPlay,
) {
    val viewModel: ExamHomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val purchasedSKU by homeActivityViewModel.purchasedSku.collectAsStateWithLifecycle()
    val isSubscribed = homeActivityViewModel.isPurchasedForModule(purchasedSKU)
    val isLoggedIn = homeActivityViewModel.isUserLoggedIn()
    val context = LocalContext.current
    val pleaseSelectMsg = stringResource(R.string.please_select_at_least_one_checkbox)
    var showPaywall by remember { mutableStateOf(false) }
    var showLogin by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        BackButtonWithText(
            title = stringResource(R.string.math_exam),
            onBackClick = onBackClick
        )
        ExamHomeScreen(uiState, viewModel, isSubscribed = isSubscribed) {
            if (!uiState.isAdditionSelected && !uiState.isSubtractionSelected &&
                !uiState.isMultiplicationSelected && !uiState.isDivisionSelected
            ) {
                context.toastS(pleaseSelectMsg)
            } else {
                when (FreemiumManager.examGate(
                    isLoggedIn = isLoggedIn,
                    isSubscribed = isSubscribed,
                    isAddition = uiState.isAdditionSelected,
                    isSubtraction = uiState.isSubtractionSelected,
                    isMultiplication = uiState.isMultiplicationSelected,
                    isDivision = uiState.isDivisionSelected,
                    difficulty = uiState.selectedDifficulty
                )) {
                    FreemiumManager.GateResult.ALLOW -> onStartPlay()
                    FreemiumManager.GateResult.REQUIRE_LOGIN -> showLogin = true
                    FreemiumManager.GateResult.REQUIRE_LOGIN_THEN_PAYWALL -> showLogin = true
                    FreemiumManager.GateResult.REQUIRE_PAYWALL -> showPaywall = true
                }
            }
        }
    }

    if (showLogin) {
        FreemiumLoginBottomSheet(
            showContinueWithoutSaving = true,
            subtitle = stringResource(R.string.login_to_save_results),
            onLoginSuccess = { showLogin = false; onStartPlay() },
            onContinueWithoutSaving = { showLogin = false; onStartPlayWithoutSaving() },
            onDismiss = { showLogin = false }
        )
    }

    if (showPaywall) {
        FreemiumPaywallBottomSheet(
            purchasedSKU = purchasedSKU,
            onDismiss = { showPaywall = false }
        )
    }
}
