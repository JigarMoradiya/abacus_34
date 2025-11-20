package com.jigar.me.ui.view.jetpack.fragments.game_zone.sudoku


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.findNavController
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText
import com.jigar.me.ui.view.jetpack.fragments.game_zone.sudoku.components.SudokuDifficulty4
import com.jigar.me.ui.view.jetpack.fragments.game_zone.sudoku.components.SudokuSize
import com.jigar.me.ui.view.jetpack.fragments.game_zone.sudoku.components.SudokuStorage

class SudokuHomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val navController = findNavController()

        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    SudokuHomeScreen(navController,
                        onStart = { size, difficulty, isNew ->

                            // Navigate using directions
                            val action = SudokuHomeFragmentDirections.toSudokuPlayFragment(size.name, difficulty.name,isNew)
                            navController.navigate(action)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 400)
@Composable
fun PreviewSudokuHomeScreen() {
    MaterialTheme {
        val navController = rememberNavController()

        SudokuHomeScreen(
            navController = navController,
            onStart = { size, difficulty, isNew -> }
        )
    }
}


// ---------- Compose UI ----------
@Composable
fun SudokuHomeScreen(
    navController: NavController,
    onStart: (SudokuSize, SudokuDifficulty4, Boolean) -> Unit
) {
    val sizes = listOf(SudokuSize.FOUR, SudokuSize.SIX, SudokuSize.NINE)
    var selectedSize by remember { mutableStateOf(SudokuSize.FOUR) }
    var selectedDiff by remember { mutableStateOf(SudokuDifficulty4.EASY) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {

        BackButtonWithText(
            title = stringResource(R.string.sudoku),
            onBackClick = { navController.popBackStack() }
        )

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.activity_padding16)),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        )  {
            sizes.forEach { s ->
                val isSelected = selectedSize == s
                val shape = RoundedCornerShape(200.dp)

                // 🎯 Animated padding
                val animatedPadding by animateDpAsState(
                    targetValue = if (isSelected)
                        dimensionResource(R.dimen.activity_padding4)
                    else
                        dimensionResource(R.dimen.activity_padding24),
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
                    label = ""
                )

                // 🎯 Animated shadow
                val animatedShadow by animateDpAsState(
                    targetValue = if (isSelected) 16.dp else 4.dp,
                    animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
                    label = ""
                )

                // 🎯 Optional: animated zoom (like SwiftUI scaleEffect)
                val animatedScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.05f else 1f,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 250f),
                    label = ""
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(animatedPadding)
                        .shadow(
                            elevation = animatedShadow,
                            shape = shape,
                            clip = false
                        )
                        .clip(shape)
                        .graphicsLayer {
                            scaleX = animatedScale
                            scaleY = animatedScale
                        }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current
                        ) {
                            selectedSize = s
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Image(
                        painter = painterResource(
                            id = when (s) {
                                SudokuSize.FOUR -> R.drawable.sudoku_4
                                SudokuSize.SIX -> R.drawable.sudoku_6
                                SudokuSize.NINE -> R.drawable.sudoku_9
                            }
                        ),
                        contentScale = ContentScale.Fit,
                        contentDescription = null
                    )
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
            DifficultySelectorCompose(
                selected = selectedDiff,
                onSelect = { selectedDiff = it }
            )

            Spacer(Modifier.weight(1f))

            val shape = RoundedCornerShape(50)
            Box(modifier = Modifier.shadow(elevation = 8.dp,shape = shape, clip = false)) {
                Button(
                    onClick = {
//                        if (SudokuStorage.hasSavedGame(context)) {
//                            onStart(selectedSize, selectedDiff, false)
//                        } else {
                            onStart(selectedSize, selectedDiff, true)
//                        }
                    },
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
                        Text(text = stringResource(R.string.lets_play), fontSize = dimensionResource(R.dimen.textSizeSuperExtraLarge).value.sp,
                            fontFamily = FontFamily(Font(R.font.font_bold)))
                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.activity_padding6)))
                        Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null)
                    }
                }
            }

        }
    }
}

@Composable
fun DifficultySelectorCompose(
    selected: SudokuDifficulty4,
    onSelect: (SudokuDifficulty4) -> Unit
) {
    Row(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.75f), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SudokuDifficulty4.entries.forEach { d ->
            val isSelected = d == selected
            val shape = RoundedCornerShape(20.dp)
            // Surface renders the elevation (shadow). Do NOT clip the Surface itself.
            Surface(
                modifier = Modifier
                    .padding(horizontal = 4.dp),
                shape = shape,
                color = if (isSelected) colorResource(R.color.colorPrimaryDark) else Color.White,
                shadowElevation = if (isSelected) 8.dp else 0.dp, // elevation visible because Surface is not clipped
                tonalElevation = if (isSelected) 4.dp else 0.dp,
                border = if (!isSelected) BorderStroke(1.dp, Color.LightGray) else null
            ) {
                // Clip and clickable are applied INSIDE Surface so ripple is rounded,
                // but Surface remains unclipped so shadow renders.
                Box(
                    modifier = Modifier
                        .clip(shape) // <-- clipped here so ripple gets rounded bounds
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current
                        ) {
                            onSelect(d)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = d.displayName,
                        fontSize = if (isSelected)
                            dimensionResource(id = R.dimen.textSizeSuperExtraLarge).value.sp
                        else
                            dimensionResource(id = R.dimen.textSizeRegular).value.sp,
                        color = if (isSelected) Color.White else colorResource(R.color.black_text),
                        fontFamily = FontFamily(Font(if (isSelected) R.font.font_bold else R.font.font_regular))
                    )
                }
            }
        }
    }
}