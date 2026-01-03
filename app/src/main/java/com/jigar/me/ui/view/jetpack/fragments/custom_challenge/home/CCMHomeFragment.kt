package com.jigar.me.ui.view.jetpack.fragments.custom_challenge.home

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
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText
import com.jigar.me.ui.view.jetpack.fragments.custom_challenge.home.components.CCMHomeScreen
import com.jigar.me.ui.view.jetpack.fragments.custom_challenge.home.viewmodels.CCMHomeViewModel
import com.jigar.me.ui.view.jetpack.fragments.home.viewmodels.HomeActivityViewModel
import com.jigar.me.utils.extensions.toastS
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class CCMHomeFragment : Fragment() {
    private val viewModel: CCMHomeViewModel by viewModels()
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
                MaterialTheme {
                    Column(modifier = Modifier.fillMaxSize()) {
                        BackButtonWithText(title = stringResource(R.string.custom_challenge_mode), onBackClick = {findNavController().popBackStack()})
                        CCMHomeScreen(uiState,viewModel){
                            val isPurchase = homeActivityViewModel.isPurchasedForModule(purchasedSKU)
                            if (isPurchase){
                                if (!uiState.isQuestionSpeak && !uiState.isQuestionShowWord && !uiState.isQuestionShowNumber){
                                    requireContext().toastS(getString(R.string.please_select_at_least_one_checkbox))
                                }else{
                                    findNavController().navigate(CCMHomeFragmentDirections.toCCMPlayFragment())
                                }
                            }else{
                                // redirect to purchase fragment
                                findNavController().navigate(CCMHomeFragmentDirections.toPurchaseFragment())
                            }
                        }
                    }
                }

            }
        }
    }

}
