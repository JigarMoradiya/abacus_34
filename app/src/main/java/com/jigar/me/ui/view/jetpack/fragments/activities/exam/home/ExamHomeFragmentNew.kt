package com.jigar.me.ui.view.jetpack.fragments.activities.exam.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.core.presentation.theme.AbacusTheme
import com.jigar.me.ui.view.jetpack.fragments.activities.exam.home.components.ExamHomeScreen
import com.jigar.me.ui.view.jetpack.fragments.activities.exam.home.viewmodels.ExamHomeViewModel
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText
import com.jigar.me.ui.view.jetpack.fragments.home.viewmodels.HomeActivityViewModel
import com.jigar.me.utils.extensions.toastS
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExamHomeFragmentNew : Fragment() {
    private val viewModel: ExamHomeViewModel by viewModels()
    private val homeActivityViewModel: HomeActivityViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val purchasedSKU by homeActivityViewModel.purchasedSku.collectAsStateWithLifecycle()
                AbacusTheme {
                    Column(modifier = Modifier.fillMaxSize()) {
                        BackButtonWithText(title = stringResource(R.string.math_exam), onBackClick = {findNavController().popBackStack()})
                        ExamHomeScreen(uiState,viewModel){
                            val isPurchase = homeActivityViewModel.isPurchasedForModule(purchasedSKU)
                            if (isPurchase){
                                if (!uiState.isAdditionSelected && !uiState.isSubtractionSelected && !uiState.isMultiplicationSelected && !uiState.isDivisionSelected){
                                    requireContext().toastS(getString(R.string.please_select_at_least_one_checkbox))
                                }else{
                                    findNavController().navigate(ExamHomeFragmentNewDirections.toExamPlayFragment())
                                }
                            }else{
                                // redirect to purchase fragment
                                findNavController().navigate(ExamHomeFragmentNewDirections.toPurchaseFragment())
                            }
                        }
                    }
                }

            }
        }
    }

}
