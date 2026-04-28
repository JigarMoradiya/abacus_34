package com.jigar.me.ui.view.home.screens.my_account

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText
import com.jigar.me.ui.view.jetpack.fragments.common.Loader
import com.jigar.me.ui.view.jetpack.fragments.reports.components.NoReportAvailableView
import com.jigar.me.ui.view.jetpack.fragments.reports.components.ReportDateFilterCard
import com.jigar.me.ui.view.jetpack.fragments.reports.components.ReportFilterCard
import com.jigar.me.ui.view.jetpack.fragments.reports.components.ReportsScreen
import com.jigar.me.ui.view.jetpack.fragments.reports.dialogs.ExerciseExamCompleteResultDialog
import com.jigar.me.ui.view.jetpack.fragments.reports.viewmodels.ReportHistoryViewModel

@Composable
fun ReportHistoryRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: ReportHistoryViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BackButtonWithText(
                title = stringResource(R.string.report_history_cards),
                onBackClick = onBackClick
            )
            Spacer(Modifier.width(dimensionResource(R.dimen.activity_padding16)))
            ReportFilterCard(uiState, viewModel)
            Spacer(Modifier.weight(1f))
            ReportDateFilterCard(uiState, viewModel)
        }
        if (uiState.list.isEmpty() && !uiState.isLoading) {
            NoReportAvailableView()
        } else {
            ReportsScreen(uiState, viewModel) {
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
}
