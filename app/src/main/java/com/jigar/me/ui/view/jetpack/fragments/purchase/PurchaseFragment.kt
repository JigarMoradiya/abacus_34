package com.jigar.me.ui.view.jetpack.fragments.purchase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.jigar.me.ui.view.jetpack.core.presentation.theme.AbacusTheme
import com.jigar.me.ui.view.jetpack.fragments.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.jetpack.fragments.purchase.components.PurchaseScreen
import com.jigar.me.ui.view.jetpack.fragments.purchase.viewmodels.PurchaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class PurchaseFragment : Fragment() {
    private val viewModel: PurchaseViewModel by viewModels()
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
                viewModel.loadInitialData(purchasedSKU)
                AbacusTheme {
                    Column(modifier = Modifier.fillMaxSize()) {
                        PurchaseScreen(uiState,onPlanSelected ={
                            viewModel.onPlanSelected(it)
                        },onShowOldSubClick = {
                            viewModel.onShowOldSubClick()
                        },onSubscribe = {
                            viewModel.makePurchase(requireActivity())
                        },onClose = {
                            findNavController().popBackStack()
                        },oldSubPopupCloseClick = {
                            viewModel.oldSubPopupClose()
                        })
                    }
                }

            }
        }
    }

}
