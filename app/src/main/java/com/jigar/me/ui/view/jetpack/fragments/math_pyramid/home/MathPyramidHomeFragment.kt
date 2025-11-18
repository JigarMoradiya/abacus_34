package com.jigar.me.ui.view.jetpack.fragments.math_pyramid.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.fragments.math_pyramid.home.components.MathPyramidViewModel
import kotlin.getValue

class MathPyramidHomeFragment : Fragment() {
    private val viewModel : MathPyramidViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    MathPyramidHomeJetpackScreen(
                        viewModel = viewModel,
                        onStartGame = { levels, difficulty ->
                            val args = bundleOf("levels" to levels, "difficulty" to difficulty.name)
                            findNavController().navigate(R.id.toMathPyramidPlayFragment, args)
                        },
                        onBackClick = { findNavController().popBackStack() }
                    )
                }
            }
        }
    }
}

