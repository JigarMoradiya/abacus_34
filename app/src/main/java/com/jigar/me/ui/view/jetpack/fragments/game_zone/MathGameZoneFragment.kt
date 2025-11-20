package com.jigar.me.ui.view.jetpack.fragments.game_zone

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
import com.jigar.me.ui.view.jetpack.fragments.game_zone.math_pyramid.home.MathPyramidHomeJetpackScreen
import com.jigar.me.ui.view.jetpack.fragments.game_zone.math_pyramid.home.components.MathPyramidViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
@AndroidEntryPoint
class MathGameZoneFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    MathGameZoneScreen(
                        gameType = { type ->
                            when (type) {
                                GameCategoryType.NUMBER_SEQUENCE_PUZZLE -> {
                                    findNavController().navigate(R.id.toNumberSequencePuzzleHomeFragment)
                                }
                                GameCategoryType.SUDOKU -> {
                                    findNavController().navigate(R.id.toSudokuHomeFragment)
                                }
                                GameCategoryType.MATH_PYRAMID -> {
                                    findNavController().navigate(R.id.toMathPyramidHomeFragment)
                                }
                                GameCategoryType.TARGET_NUMBER -> {
                                    findNavController().navigate(R.id.toTargetNumberHomeFragment)
                                }
                            }
                        },
                        onBackClick = { findNavController().popBackStack() }
                    )
                }
            }
        }
    }
}

