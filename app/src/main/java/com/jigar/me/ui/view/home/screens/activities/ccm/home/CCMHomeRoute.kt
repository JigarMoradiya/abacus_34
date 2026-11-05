package com.jigar.me.ui.view.home.screens.activities.ccm.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumPaywallBottomSheet
import com.jigar.me.ui.view.home.common_ui.dialogs.PaywallContext
import com.jigar.me.ui.view.home.screens.activities.ActivityTopBar
import com.jigar.me.ui.view.home.screens.activities.ccm.home.components.CCMHomeScreen
import com.jigar.me.ui.view.home.screens.activities.ccm.home.viewmodels.CCMHomeViewModel
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.FreemiumManager
import com.jigar.me.utils.extensions.toastS

@Composable
fun CCMHomeRoute(
    homeActivityViewModel: HomeActivityViewModel,
    onBackClick: () -> Unit,
    onStartPlay: () -> Unit,
) {
    val viewModel: CCMHomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isSubscribed by remember { mutableStateOf(homeActivityViewModel.isPurchasedForModule()) }
    LaunchedEffect(Unit) { homeActivityViewModel.isPurchasedFlow.collect { if (it) isSubscribed = true } }
    val context = LocalContext.current
    val pleaseSelectMsg = stringResource(R.string.please_select_at_least_one_checkbox)
    var showPaywall by remember { mutableStateOf(false) }

    AbTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Meadow, Modifier.fillMaxSize())
            // The header is laid over the top: the left panel makes room under it, the right panel uses the whole height.
            Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
                val density = LocalDensity.current
                var headerHeight by remember { mutableStateOf(0.dp) }
                CCMHomeScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    headerHeight = headerHeight,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = DeviceInfo.screenHorizontalPadding(), end = AppDimens.Dimens16),
                ) {
                    if (FreemiumManager.gameGate(isSubscribed) == FreemiumManager.GateResult.ALLOW) {
                        if (!uiState.isQuestionSpeak && !uiState.isQuestionShowWord && !uiState.isQuestionShowNumber) {
                            context.toastS(pleaseSelectMsg)
                        } else {
                            onStartPlay()
                        }
                    } else {
                        showPaywall = true
                    }
                }
                ActivityTopBar(
                    title = stringResource(R.string.custom_challenge_mode),
                    onBack = onBackClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .onSizeChanged { headerHeight = with(density) { it.height.toDp() } },
                )
            }
        }

        if (showPaywall) {
            FreemiumPaywallBottomSheet(
                paywallContext = PaywallContext.CCM,
                onSubscriptionActivated = { isSubscribed = true; showPaywall = false },
                onDismiss = { showPaywall = false }
            )
        }
    }
}
