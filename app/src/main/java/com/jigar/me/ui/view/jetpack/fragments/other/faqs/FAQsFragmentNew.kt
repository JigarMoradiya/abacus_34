package com.jigar.me.ui.view.jetpack.fragments.other.faqs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
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
import com.jigar.me.ui.view.jetpack.fragments.other.faqs.components.FAQsListItem
import com.jigar.me.ui.view.jetpack.fragments.other.faqs.viewmodels.FAQsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FAQsFragmentNew : Fragment() {
    private val viewModel: FAQsViewModel by viewModels()
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
                        Row {
                            if (!uiState.isUserLoggedIn){
                                Spacer(Modifier.width(dimensionResource(R.dimen.activity_padding12)))
                            }
                            BackButtonWithText(title = stringResource(R.string.faqs), onBackClick = {findNavController().popBackStack()})
                        }

                        FAQsListItem(uiState.faqsList)
                    }
                }

            }
        }
    }

}
