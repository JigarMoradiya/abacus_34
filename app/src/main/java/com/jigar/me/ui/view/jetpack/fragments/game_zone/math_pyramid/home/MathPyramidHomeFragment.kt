package com.jigar.me.ui.view.jetpack.fragments.game_zone.math_pyramid.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.jigar.me.R
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText
import com.jigar.me.ui.view.jetpack.fragments.common.CommonDifficultySelectorCompose
import com.jigar.me.ui.view.jetpack.fragments.common.HowToPlayButton
import com.jigar.me.ui.view.jetpack.fragments.common.how_to_play.HowToPlayMathPyramidView
import com.jigar.me.ui.view.jetpack.fragments.game_zone.math_pyramid.home.components.MathPyramidViewModel
import com.jigar.me.ui.view.jetpack.fragments.home.viewmodels.HomeActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MathPyramidHomeFragment : BaseFragment() {
    private val viewModel : MathPyramidViewModel by viewModels()
    private val homeActivityViewModel: HomeActivityViewModel by activityViewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    val purchasedSKU by homeActivityViewModel.purchasedSku.collectAsStateWithLifecycle()
                    MathPyramidHomeJetpackScreen(
                        viewModel = viewModel,
                        onStartGame = {
                            val isPurchase = homeActivityViewModel.isPurchasedForModule(purchasedSKU)
                            if (isPurchase){
                                val args = bundleOf("levels" to viewModel.uiState.value.selectedLevel, "difficulty" to viewModel.uiState.value.selectedDifficulty.name)
                                findNavController().navigate(R.id.toMathPyramidPlayFragment, args)
                            }else{
                                // redirect to purchase fragment
                                findNavController().navigate(MathPyramidHomeFragmentDirections.toPurchaseFragment())
                            }
                        },
                        onBackClick = { findNavController().popBackStack() }
                    )
                }
            }
        }
    }
}


@Composable
fun MathPyramidHomeJetpackScreen(
    viewModel: MathPyramidViewModel,
    onStartGame: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showHelp by remember { mutableStateOf(false) }
    val levelRange = 2..6

    Box(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier.fillMaxSize()) {

            // 🔹 Header Bar
            Row {
                BackButtonWithText(title = stringResource(R.string.math_pyramid), onBackClick = onBackClick)
                Spacer(Modifier.weight(1f))
                HowToPlayButton{
                    showHelp = true
                }
            }

            Spacer(Modifier.weight(1f))

            // LEVEL SELECTOR ------------------------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                levelRange.forEach { level ->

                    val shape = RoundedCornerShape(12.dp)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(shape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = LocalIndication.current
                            ) {
                                viewModel.selectLevel(level)
                            }
                            .padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = getDrawableForPyramid(level)),
                            contentDescription = "pyramid_$level",
                            contentScale = ContentScale.Fit
                        )

                        Box(
                            modifier = Modifier.height(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Level $level",
                                color = if (state.selectedLevel == level) colorResource(R.color.black) else colorResource(R.color.black_text),
                                fontFamily = FontFamily(Font(if (state.selectedLevel == level) R.font.font_bold else R.font.font_regular)),
                                fontSize = dimensionResource(
                                    id = if (state.selectedLevel == level) R.dimen.textSize24 else R.dimen.textSize17).value.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // DIFFICULTY + START BUTTON --------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CommonDifficultySelectorCompose(
                    selected = state.selectedDifficulty,
                    onSelect = { viewModel.selectDifficulty(it) }
                )

                Spacer(Modifier.weight(1f))

                val shape = RoundedCornerShape(50)
                Box(modifier = Modifier.shadow(elevation = 8.dp,shape = shape, clip = false)) {
                    Button(
                        onClick = { onStartGame() },
                        shape = shape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.colorPrimary),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(
                            horizontal = dimensionResource(R.dimen.activity_padding16)
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = stringResource(R.string.lets_start), fontSize = dimensionResource(R.dimen.textSizeSuperExtraLarge).value.sp,
                                fontFamily = FontFamily(Font(R.font.font_bold)))
                            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.activity_padding6)))
                            Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null)
                        }
                    }
                }

            }
        }
        AnimatedVisibility(
            visible = showHelp,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            HowToPlayMathPyramidView {
                showHelp = false
            }
        }
    }
}

@Composable
fun getDrawableForPyramid(level: Int): Int {
    return when (level) {
        2 -> R.drawable.pyramid_2
        3 -> R.drawable.pyramid_3
        4 -> R.drawable.pyramid_4
        5 -> R.drawable.pyramid_5
        6 -> R.drawable.pyramid_6
        else -> R.drawable.pyramid_4
    }
}

