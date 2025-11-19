package com.jigar.me.ui.view.jetpack.fragments.game_zone.number_sequence_puzzle.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.jetpack.fragments.game_zone.number_sequence_puzzle.viewmodels.NumberSequencePuzzleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NumberSequencePuzzleJetpackFragment : BaseFragment() {

    private val viewModel: NumberSequencePuzzleViewModel by viewModels()

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
                        navController = navController,
                        gridSize = gridSize,viewModel = viewModel
                    )
                }
            }
        }
    }
}