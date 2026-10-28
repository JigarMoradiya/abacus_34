package com.jigar.me.ui.view.home.screens.reports

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbTopBar
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.home.common_ui.Loader
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumPaywallBottomSheet
import com.jigar.me.ui.view.home.common_ui.dialogs.PaywallContext
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.home.screens.reports.components.NoReportAvailableView
import com.jigar.me.ui.view.home.screens.reports.components.ReportDateFilterCard
import com.jigar.me.ui.view.home.screens.reports.components.ReportFilterCard
import com.jigar.me.ui.view.home.screens.reports.components.ReportsScreen
import com.jigar.me.ui.view.home.screens.reports.dialogs.ExerciseExamCompleteResultDialog
import com.jigar.me.ui.view.home.screens.reports.viewmodels.ReportHistoryViewModel
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun ReportHistoryRoute(
    homeActivityViewModel: HomeActivityViewModel,
    onBackClick: () -> Unit,
) {
    val viewModel: ReportHistoryViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSubscribed by homeActivityViewModel.isPurchasedFlow.collectAsStateWithLifecycle()
    var showPaywall by remember { mutableStateOf(false) }

    AbTheme {
        Box(Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Lagoon, Modifier.fillMaxSize())

            Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
                AbTopBar(
                    title = stringResource(R.string.report_history_cards),
                    onBack = onBackClick,
                    modifier = Modifier.padding(
                        start = DeviceInfo.screenHorizontalPadding(),
                        end = AppDimens.Dimens16,
                        top = DeviceInfo.screenTopPadding(),
                        bottom = AppDimens.Dimens6
                    ),
                    trailing = {
                        ReportFilterCard(uiState, viewModel)
                        ReportDateFilterCard(uiState, viewModel)
                    }
                )
                if (uiState.list.isEmpty() && !uiState.isLoading) {
                    NoReportAvailableView()
                } else {
                    ReportsScreen(
                        uiState = uiState,
                        viewModel = viewModel,
                        isSubscribed = isSubscribed,
                        onShowPaywall = { showPaywall = true }
                    ) {
                        viewModel.showExerciseExamDialog(it)
                    }
                }
            }

            if (uiState.isLoading) {
                Loader()
            }

            if (uiState.isShowExerciseExamPopup) {
                uiState.exerciseExamRequest?.let {
                    ExerciseExamCompleteResultDialog(
                        selectedTheme = uiState.selectedTheme,
                        it,
                        isFromHistory = true,
                        onClose = { viewModel.closeExercise() },
                        onGiveAgain = {},
                    )
                }
            }
        }

        if (showPaywall) {
            FreemiumPaywallBottomSheet(
                paywallContext = PaywallContext.REPORTS,
                onDismiss = { showPaywall = false }
            )
        }
    }
}
