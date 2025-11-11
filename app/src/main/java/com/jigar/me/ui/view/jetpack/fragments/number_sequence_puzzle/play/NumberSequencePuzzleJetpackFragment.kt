package com.jigar.me.ui.view.jetpack.fragments.number_sequence_puzzle.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.utils.AppConstants

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
                        navController = navController, prefManager = prefManager,
                        gridSize = gridSize
                    )
                }
            }
        }
    }
}


@Preview(
    name = "Number Puzzle Home - Light",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 800,
    heightDp = 400
)
@Composable
fun NumberSequencePuzzleScreenPreview() {
    // Use a fake NavController for preview
    val fakeNavController = rememberNavController()

    MaterialTheme {
        NumberSequencePuzzleJetpackScreen(
            navController = fakeNavController,
            gridSize = 5,prefManager = AppPreferencesHelper(LocalContext.current, AppConstants.PREF_NAME)
        )
    }
}