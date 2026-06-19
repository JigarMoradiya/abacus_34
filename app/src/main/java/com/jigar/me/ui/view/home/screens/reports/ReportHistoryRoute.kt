package com.jigar.me.ui.view.home.screens.reports

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.Loader
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumPaywallBottomSheet
import com.jigar.me.ui.view.home.screens.reports.components.NoReportAvailableView
import com.jigar.me.ui.view.home.screens.reports.components.ReportDateFilterCard
import com.jigar.me.ui.view.home.screens.reports.components.ReportFilterCard
import com.jigar.me.ui.view.home.screens.reports.components.ReportsScreen
import com.jigar.me.ui.view.home.screens.reports.dialogs.ExerciseExamCompleteResultDialog
import com.jigar.me.ui.view.home.screens.reports.viewmodels.ReportHistoryViewModel
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel

@Composable
fun ReportHistoryRoute(
    homeActivityViewModel: HomeActivityViewModel,
    onBackClick: () -> Unit,
) {
    val viewModel: ReportHistoryViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSubscribed by homeActivityViewModel.isPurchasedFlow.collectAsStateWithLifecycle()
    var showPaywall by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BackButtonWithText(
                title = stringResource(R.string.report_history_cards),
                onBackClick = onBackClick,
                modifier = Modifier.weight(1f)
            )

            ReportFilterCard(uiState, viewModel)
            Spacer(Modifier.width(AppDimens.Dimens16))
            ReportDateFilterCard(uiState, viewModel)
        }
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

    AnimatedVisibility(
        visible = uiState.isShowExerciseExamPopup,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
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

    if (showPaywall) {
        FreemiumPaywallBottomSheet(
            onDismiss = { showPaywall = false }
        )
    }
}
