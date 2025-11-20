package com.jigar.me.ui.view.jetpack.fragments.game_zone.target_number

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText
import com.jigar.me.ui.view.jetpack.fragments.common.enums.CommonDifficulty4
import com.jigar.me.ui.view.jetpack.fragments.game_zone.target_number.components.TargetUiState
import com.jigar.me.ui.view.jetpack.fragments.game_zone.target_number.viewmodel.TargetNumberPlayViewModel
import com.jigar.me.ui.view.jetpack.fragments.game_zone.target_number.viewmodel.TargetNumberPlayViewModelFake
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TargetNumberPlayFragment : Fragment() {

    private val vm: TargetNumberPlayViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    TargetNumberPlayScreen(
                        viewModel = vm,
                        onBackClick = { findNavController().popBackStack() }
                    )
                }
            }
        }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, widthDp = 900, heightDp = 500)
@Composable
fun PreviewTargetNumberPlayScreen() {
    MaterialTheme {
//        TargetNumberPlayScreen(
//            viewModel = TargetNumberPlayViewModelFake(),
//            onBackClick = {}
//        )
    }
}

@Composable
fun TargetNumberPlayScreen(
    viewModel: TargetNumberPlayViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        BackButtonWithText(title = stringResource(R.string.complete_the_target), onBackClick = onBackClick)

        Row {
            Box(modifier = Modifier.weight(0.6f),
                contentAlignment = Alignment.Center) {
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp,
                    Alignment.CenterVertically), horizontalAlignment = Alignment.CenterHorizontally) {
// Target display
                    Text("🎯 Target: ${state.target}",
                        fontSize = dimensionResource(R.dimen.textSize48).value.sp,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                        color = Color.Red)

                    // Current expression
                    Text(state.currentExpression.ifEmpty { " " },
                        fontSize = dimensionResource(R.dimen.textSizeExtraLarge).value.sp,
                        fontFamily = FontFamily(Font(R.font.font_bold)),
                        modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)

                    // Numbers row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        state.numbers.forEachIndexed { idx, num ->

                            val isSelected = state.selectedNumberIndex == idx  // 👈 ADD selected index in UI state

                            Box(
                                modifier = Modifier
                                    .defaultMinSize(minWidth = 56.dp, minHeight = 56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected)
                                            Color(0xFF43A047) // green
                                        else
                                            colorResource(R.color.colorPrimary) // blue
                                    )
                                    .clickable { viewModel.tapNumber(idx) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = num.toString(),
                                    fontSize = dimensionResource(R.dimen.textSize20).value.sp,
                                    fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                                    color = Color.White
                                )
                            }
                        }
                    }


                    // Operations
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.allowedOps.forEach { op ->
                            Box(contentAlignment = Alignment.Center,modifier = Modifier.size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(colorResource(R.color.colorAccent))
                                .clickable{
                                viewModel.tapOperation(op)
                            }){
                                Text(op.symbol,
                                    fontSize = dimensionResource(R.dimen.textSize30).value.sp,
                                    fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                                    color = Color.White)
                            }
                        }
                    }
                }
            }

            Box(modifier = Modifier.weight(0.4f),
                contentAlignment = Alignment.Center) {

                Column(modifier = Modifier.padding(16.dp),horizontalAlignment = Alignment.CenterHorizontally) {
                    // Steps / log
                    Text("Steps", style = MaterialTheme.typography.headlineLarge)
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(state.steps.size) { i ->
                            Text(state.steps[i], modifier = Modifier.padding(6.dp))
                        }
                    }

                    // message
                    state.message?.let {
                        Text(it, color = if (state.isSolvedCorrect == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
                    }

                    // Controls
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (state.isSolvedCorrect == true){
                            Button(onClick = { viewModel.generateNewPuzzle(1, CommonDifficulty4.medium) }) {
                                Text("New Target")
                            }
                        }else{
                            Button(onClick = { viewModel.showHint() }, enabled = state.hintUsed < state.hintLimit) {
                                Text("Hint (${state.hintUsed}/${state.hintLimit})")
                            }
                            Button(onClick = { viewModel.resetPuzzle() }) {
                                Text("Reset")
                            }
                        }
                    }
                }
            }
        }
    }
}


