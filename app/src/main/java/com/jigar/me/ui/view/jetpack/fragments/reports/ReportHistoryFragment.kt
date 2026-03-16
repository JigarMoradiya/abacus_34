package com.jigar.me.ui.view.jetpack.fragments.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.core.presentation.theme.AbacusTheme
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText
import com.jigar.me.ui.view.jetpack.fragments.common.Loader
import com.jigar.me.ui.view.jetpack.fragments.reports.components.NoReportAvailableView
import com.jigar.me.ui.view.jetpack.fragments.reports.components.ReportDateFilterCard
import com.jigar.me.ui.view.jetpack.fragments.reports.components.ReportFilterCard
import com.jigar.me.ui.view.jetpack.fragments.reports.components.ReportsScreen
import com.jigar.me.ui.view.jetpack.fragments.reports.dialogs.ExerciseExamCompleteResultDialog
import com.jigar.me.ui.view.jetpack.fragments.reports.viewmodels.ReportHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportHistoryFragment : Fragment() {
    private val viewModel: ReportHistoryViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                AbacusTheme {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BackButtonWithText(title = stringResource(R.string.report_history_cards), onBackClick = {findNavController().popBackStack()})
                            Spacer(Modifier.width(dimensionResource(R.dimen.activity_padding16)))
                            ReportFilterCard(uiState,viewModel)
                            Spacer(Modifier.weight(1f))
                            ReportDateFilterCard(uiState, viewModel)
                        }
                        if (uiState.list.isEmpty() && !uiState.isLoading) {
                            NoReportAvailableView()
                        } else {
                            ReportsScreen(uiState,viewModel){
                                viewModel.showExerciseExamDialog(it)
                            }
                        }


                    }
                    // loader
                    if (uiState.isLoading) {
                        Loader()
                    }

                    // exercise popup
                    AnimatedVisibility(
                        visible = uiState.isShowExerciseExamPopup, enter = fadeIn(), exit = fadeOut()
                    ) {
                        uiState.exerciseExamRequest?.let {
                            ExerciseExamCompleteResultDialog(selectedTheme = uiState.selectedTheme,
                                it, isFromHistory = true,
                                onClose = {
                                    viewModel.closeExercise()
                                },
                                onGiveAgain = {},
                            )
                        }
                    }
                }

            }
        }
    }

}
