package com.jigar.me.ui.view.jetpack.fragments.game_zone.number_sequence_puzzle.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.findNavController
import com.jigar.me.ui.view.base.BaseFragment

class NumberSequencePuzzleHomeFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    val navController = findNavController()
                    NumberSequencePuzzleHomeJetpackScreen(
                        navController = navController,
                        onBackClick = { navController.popBackStack() })
                }
            }
        }
    }
}

/*@Preview(
    name = "Number Puzzle Home - Light",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 800,
    heightDp = 400
)
@Composable
fun NumberPuzzleHomeScreenPreview() {
    // Use a fake NavController for preview
    val fakeNavController = rememberNavController()

    MaterialTheme {
        NumberSequencePuzzleHomeJetpackScreen(
            navController = fakeNavController,
            onBackClick = {}
        )
    }
}*/
