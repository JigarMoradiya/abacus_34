package com.jigar.me.ui.view.jetpack.fragments.number_sequence_puzzle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.findNavController
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.jetpack.fragments.number_sequence_puzzle.components.NumberSequencePuzzleJetpackScreen


class NumberSequencePuzzleJetpackFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val gridSize = NumberSequencePuzzleJetpackFragmentArgs.fromBundle(requireArguments()).type
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    val navController = findNavController()
                    NumberSequencePuzzleJetpackScreen(
                        navController = navController,prefManager = prefManager,
                        gridSize = gridSize
                    )
                }
            }
        }
    }
}

