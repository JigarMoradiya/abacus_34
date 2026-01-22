package com.jigar.me.ui.view.jetpack.fragments.abacus_free_mode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.jetpack.core.presentation.theme.AbacusTheme
import com.jigar.me.ui.view.jetpack.fragments.abacus_free_mode.components.AbacusFreeModeScreen
import com.jigar.me.ui.view.jetpack.fragments.abacus_free_mode.viewmodel.AbacusFreeModeViewModel
import com.jigar.me.ui.view.jetpack.fragments.common.LocalPreferencesHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AbacusFreeModeFragment : Fragment() {
    @Inject
    lateinit var preferences: AppPreferencesHelper
    private val viewModel: AbacusFreeModeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AbacusTheme {
                    CompositionLocalProvider(
                        LocalPreferencesHelper provides preferences
                    ) {
                        AbacusFreeModeScreen(
                            viewModel,
                            onBackClick = { findNavController().popBackStack() })
                    }

                }
            }
        }
    }
}