package com.jigar.me.ui.view.jetpack.fragments.other.faqs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jigar.me.ui.view.home.screens.my_account.FAQsRoute
import com.jigar.me.ui.view.home.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Lightweight wrapper used only by the legacy login navigation graph
 * (login_navigation_graph.xml) so the login flow can show the FAQs screen.
 */
@AndroidEntryPoint
class FAQsFragmentNew : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MyApplicationTheme {
                    FAQsRoute(onBackClick = { findNavController().popBackStack() })
                }
            }
        }
    }
}
