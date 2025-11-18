package com.jigar.me.ui.view.jetpack.fragments.math_pyramid.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jigar.me.ui.view.jetpack.fragments.common.enums.CommonDifficulty4

class MathPyramidPlayFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val args = arguments
        val levels = args?.getInt("levels") ?: 4
        val difficulty = CommonDifficulty4.fromName(args?.getString("difficulty"))

        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    val navController = findNavController()
                    MathPyramidPlayJetpackScreen(
                        levels = levels,
                        difficulty = difficulty,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}