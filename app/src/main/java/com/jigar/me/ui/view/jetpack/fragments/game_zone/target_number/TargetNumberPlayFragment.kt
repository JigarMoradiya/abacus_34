package com.jigar.me.ui.view.jetpack.fragments.game_zone.target_number

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.core.presentation.theme.AbacusTheme
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText
import com.jigar.me.ui.view.jetpack.fragments.game_zone.target_number.viewmodel.TargetNumberPlayViewModel
import com.jigar.me.utils.PlaySound
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TargetNumberPlayFragment : Fragment() {

    private val vm: TargetNumberPlayViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        ComposeView(requireContext()).apply {
            setContent {
                AbacusTheme {
                    TargetNumberPlayScreen(
                        viewModel = vm,
                        onBackClick = { findNavController().popBackStack() }
                    )
                }
            }
        }
}

@Composable
fun TargetNumberPlayScreen(
    viewModel: TargetNumberPlayViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    Row {
        Box(modifier = Modifier.weight(0.6f),
            contentAlignment = Alignment.Center) {
            Column(modifier = Modifier.fillMaxSize(),){
                BackButtonWithText(title = stringResource(R.string.complete_the_target), onBackClick = onBackClick)

                Spacer(modifier = Modifier.weight(1f))

                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp,
                    Alignment.CenterVertically), horizontalAlignment = Alignment.CenterHorizontally) {
// Target display
                    Text("🎯 Target: ${state.target}",
                        fontSize = dimensionResource(R.dimen.textSize40).value.sp,
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
                            Box(contentAlignment = Alignment.Center,modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(colorResource(R.color.colorAccent))
                                .clickable {
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

        }

        Box(modifier = Modifier.weight(0.4f),
            contentAlignment = Alignment.Center) {

            Column(modifier = Modifier.padding(16.dp),horizontalAlignment = Alignment.CenterHorizontally) {
                // Steps / log
                if (state.steps.isNotNullOrEmpty()){
                    StepsLogSection(
                        steps = state.steps,
                        originalNumbers = state.originalNumbers.joinToString(separator = ", ")
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                // message
                state.message?.let {
                    Text(it,
                        fontSize = dimensionResource(R.dimen.textSizeExtraLarge).value.sp,
                        fontFamily = FontFamily(Font(R.font.font_bold)),
                        color = if (state.isSolvedCorrect == true) colorResource(R.color.green_600) else colorResource(R.color.red_600), modifier = Modifier.padding(8.dp))
                }

                // Controls
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (state.isSolvedCorrect == true){
                        Button(
                            onClick = {
                                PlaySound.playHint(context)
                                viewModel.generateNewPuzzle()
                                      },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.colorPrimary),   // Red
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(
                                horizontal = 16.dp, vertical = 8.dp
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoMode,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )

                                Text(
                                    text = stringResource(R.string.new_target),
                                    fontSize = dimensionResource(R.dimen.textSizeLarge).value.sp,
                                    fontFamily = FontFamily(Font(R.font.font_semibold)),
                                )
                            }
                        }
                    }else{
                        Button(
                            enabled = state.hintUsed < state.hintLimit,
                            onClick = { viewModel.showHint() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50),   // Green
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(
                                horizontal = 16.dp, vertical = 8.dp
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                // 🔢 Top: used/limit
                                Text(
                                    text = "${state.hintUsed}/${state.hintLimit}",
                                    fontSize = dimensionResource(R.dimen.textSize18).value.sp,
                                    fontFamily = FontFamily(Font(R.font.font_bold)),
                                    modifier = Modifier.height(24.dp)
                                )

                                // 🧩 Bottom: word "Hints"
                                Text(
                                    text = stringResource(R.string.hints),
                                    fontSize = dimensionResource(R.dimen.textSizeLarge).value.sp,
                                    fontFamily = FontFamily(Font(R.font.font_semibold)),
                                )
                            }
                        }


                        Button(
                            onClick = { viewModel.resetPuzzle() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE53935),   // Red
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(
                                horizontal = 16.dp, vertical = 8.dp
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )

                                Text(
                                    text = stringResource(R.string.reset),
                                    fontSize = dimensionResource(R.dimen.textSizeLarge).value.sp,
                                    fontFamily = FontFamily(Font(R.font.font_semibold)),
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}


@Composable
fun StepsLogSection(
    steps: List<String>,
    originalNumbers: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Title
        Text(
            text = stringResource(R.string.all_steps),
            fontSize = dimensionResource(R.dimen.textSize18).value.sp,
            fontFamily = FontFamily(Font(R.font.font_bold)),
            color = Color.Red
        )

        Spacer(Modifier.height(8.dp))

        // 🔹 ORIGINAL NUMBERS SECTION
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 1.dp)
        ) {

            // original numbers text
            Text(
                text = originalNumbers,
                fontSize = dimensionResource(R.dimen.textSize20).value.sp,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                color = Color.Black
            )

            // arrow below original numbers if steps exist
            if (steps.isNotEmpty()) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_down),
                    contentDescription = null,
                    tint = Color.Red.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Scrollable Steps List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(steps) { index, step ->

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 1.dp)
                ) {

                    // Step text
                    Text(
                        text = step,
                        fontSize = dimensionResource(R.dimen.textSizeExtraLarge).value.sp,
                        fontFamily = FontFamily(Font(R.font.font_bold)),
                        color = Color.Black
                    )

                    // Arrow below except last
                    if (index < steps.size - 1) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_down),
                            contentDescription = null,
                            tint = Color.Red.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
